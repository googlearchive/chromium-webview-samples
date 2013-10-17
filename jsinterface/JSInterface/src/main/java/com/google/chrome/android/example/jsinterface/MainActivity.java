package com.google.chrome.android.example.jsinterface;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";

    private WebView mWebView;
    private NotificationBindObject mNotificationBindObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationBindObject = new NotificationBindObject(getApplicationContext());

        // Get reference of WebView from layout/activity_main.xml
        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        setUpWebViewDefaults(mWebView);

        // Add Javascript Interface
        mWebView.addJavascriptInterface(mNotificationBindObject, "Android_NotificationBind");


        // Prepare the WebView and get the appropriate URL
        String url = prepareWebView();

        // Load the local index.html file
        mWebView.loadUrl(url);
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
            settings.setDisplayZoomControls(false);
        }
    }

    /**
     * This is method where specific logic for this application is going to live
     * @return url to load
     */
    private String prepareWebView() {
        String hash = "#";
        int bgColor;

        Intent intent = getIntent();
        if(intent != null && intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false)) {
            hash = "#launchFromNotification";
            bgColor = Color.parseColor("#1abc9c");
        } else {
            bgColor = Color.parseColor("#f1c40f");
        }

        preventBGColorFlicker(bgColor);

        return "file:///android_asset/www/index.html"+hash;
    }

    private void preventBGColorFlicker(int bgColor) {
        ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0).setBackgroundColor(bgColor);
        mWebView.setBackgroundColor(bgColor);

        mWebView.setVisibility(View.INVISIBLE);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                view.setVisibility(View.VISIBLE);
            }

        });
    }
}
