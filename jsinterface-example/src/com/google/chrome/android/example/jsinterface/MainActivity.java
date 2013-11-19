/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.chrome.android.example.jsinterface;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.io.StringReader;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference of WebView from layout/activity_main.xml
        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Add Javascript Interface, this will expose "window.NotificationBind"
        // in Javascript
        mWebView.addJavascriptInterface(new NotificationBindObject(getApplicationContext()), "NotificationBind");

        setUpWebViewDefaults(mWebView);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore the previous URL and history stack
            mWebView.restoreState(savedInstanceState);
        }

        // Prepare the WebView and get the appropriate URL
        String url = prepareWebView(mWebView.getUrl());

        // Load the local index.html file
        if(mWebView.getUrl() == null) {
            mWebView.loadUrl(url);
        }
    }

    /**
     * Inflate a menu for help menu option
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * If the help menu option is selected, show a new
     * screen in the WebView
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_help:
            loadJavascript("showSecretMessage();");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Go back through the WebView back stack before
     * exiting the app
     */
    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * When the screen orientation changes, we want to be able to
     * keep the history stack etc.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the WebView state (including history stack)
        mWebView.saveState(savedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * This method is designed to hide how Javascript is injected into
     * the WebView.
     *
     * In KitKat the new evaluateJavascript method has the ability to
     * give you access to any return values via the ValueCallback object.
     *
     * The String passed into onReceiveValue() is a JSON string, so if you
     * execute a javascript method which return a javascript object, you can
     * parse it as valid JSON. If the method returns a primitive value, it
     * will be a valid JSON object, but you should use the setLenient method
     * to true and then you can use peek() to test what kind of object it is,
     *
     * @param javascript
     */
    private void loadJavascript(String javascript) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // In KitKat+ you should use the evaluateJavascript method
            mWebView.evaluateJavascript(javascript, new ValueCallback<String>() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveValue(String s) {
                    JsonReader reader = new JsonReader(new StringReader(s));

                    // Must set lenient to parse single values
                    reader.setLenient(true);

                    try {
                        if(reader.peek() != JsonToken.NULL) {
                            if(reader.peek() == JsonToken.STRING) {
                                String msg = reader.nextString();
                                if(msg != null) {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "MainActivity: IOException", e);
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            });
        } else {
            /**
             * For pre-KitKat+ you should use loadUrl("javascript:<JS Code Here>");
             * To then call back to Java you would need to use addJavascriptInterface()
             * and have your JS call the interface
             **/
            mWebView.loadUrl("javascript:"+javascript);
        }
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * This is method where specific logic for this application is going to live
     * @return url to load
     */
    private String prepareWebView(String currentUrl) {
        String hash = "";
        int bgColor;

        if(currentUrl != null) {
            String[] hashSplit = currentUrl.split("#");
            if(hashSplit.length == 2) {
                hash = hashSplit[1];
            }
        } else {
            Intent intent = getIntent();
            if(intent != null && intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false)) {
                hash = "notification-launch";
            }
        }

        if(hash.equals("notification-launch")) {
            bgColor = Color.parseColor("#1abc9c");
        } else if(hash.equals("notification-shown")) {
            bgColor = Color.parseColor("#3498db");
        } else if(hash.equals("secret")) {
            bgColor = Color.parseColor("#34495e");
        } else {
            bgColor = Color.parseColor("#f1c40f");
        }

        preventBGColorFlicker(bgColor);

        return "file:///android_asset/www/index.html#"+hash;
    }

    /**
     * This is a little bit of trickery to make the background color of the UI
     * the same as the anticipated UI background color of the web-app.
     *
     * @param bgColor
     */
    private void preventBGColorFlicker(int bgColor) {
        ((ViewGroup) findViewById(R.id.activity_main_container)).setBackgroundColor(bgColor);
        mWebView.setBackgroundColor(bgColor);

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        mWebView.setWebViewClient(new WebViewClient());
    }
}
