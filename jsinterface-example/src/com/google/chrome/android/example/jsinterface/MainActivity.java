package com.google.chrome.android.example.jsinterface;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";
    
    private WebView mWebView;
    private NotificationBindObject mNotificationBindObject;
    private boolean mPageIsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mNotificationBindObject = new NotificationBindObject(getApplicationContext());

        // Get reference of WebView from layout/activity_main.xml
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        
        // Add Javascript Interface
        mWebView.addJavascriptInterface(mNotificationBindObject, "NotificationBind");
        
        mWebView.clearCache(true);
        
        setUpWebViewDefaults(mWebView);
        
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore the previous URL
            mWebView.restoreState(savedInstanceState);
        }
        
        // Prepare the WebView and get the appropriate URL
        String url = prepareWebView();
        
        // Load the local index.html file
        if(!url.equals(mWebView.getUrl())) {
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
            if(mPageIsLoaded) {
                loadJavascript("showSecretMessage();");
                return true;
            }
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
        // Save the WebView state
        mWebView.saveState(savedInstanceState);
        
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void loadJavascript(String javascript) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // In KitKat+ you should use the evaluateJavascript method
            mWebView.evaluateJavascript(javascript, new ValueCallback<String>() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveValue(String s) {
                    try {
                        // To ensure all results in onReceiveValue can be passed as valid
                        // JSON we wrap the json value in a json object
                        // See http://www.json.org/ for details
                        JSONObject resultObj = new JSONObject("{ \"result\" : "+s+"}");
                        
                        // If the result obj is null, then s was "null"
                        if(resultObj.isNull("result")) {
                            return;
                        }
                        
                        // We are expecting the JS to return a javascript object
                        // so try and parse as a JSONObject
                        JSONObject returnObj = resultObj.optJSONObject("result");
                        if(returnObj == null) {
                            return;
                        }
                        
                        // Check the json obj has a msg variable and display a toast
                        // if it does
                        String msg = returnObj.optString("msg");
                        if(msg != null) {
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // NOOP
                        Log.e("TAG", "JSONException", e);
                    }
                }
            });
        } else {
            // For pre-KitKat+ you should use loadUrl("javascript:<JS Code Here>");
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
        settings.setSupportZoom(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }
    }

    /**
     * This is method where specific logic for this application is going to live
     * @return url to load
     */
    private String prepareWebView() {
        String hash = "";
        int bgColor;
        
        String currentUrl = mWebView.getUrl();
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

        mWebView.setVisibility(View.INVISIBLE);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                view.setVisibility(View.VISIBLE);
                mPageIsLoaded = true;
            }

        });
    }
}
