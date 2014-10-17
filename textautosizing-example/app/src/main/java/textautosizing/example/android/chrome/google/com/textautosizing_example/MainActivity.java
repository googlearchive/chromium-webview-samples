package textautosizing.example.android.chrome.google.com.textautosizing_example;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity implements View.OnClickListener {

    private WebView mWebView;
    private Button mToggleButton;

    private boolean mUseTextAutoSize = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mToggleButton = (Button) findViewById(R.id.activity_main_toggle_btn);

        // Apply the click listener
        mToggleButton.setOnClickListener(this);

        // Apply defaults including useWideViewport which us required
        // to make the text auto size to work
        setUpWebViewDefaults(mWebView);

        // Ensure we are using the default TextAutoSize
        setUseTextAutoSize(mUseTextAutoSize);

        // Make the WebView handle all loaded URLs
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.loadUrl("file:///android_asset/www/index.html");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.activity_main_toggle_btn:
                // On toggle click, switch the auto size boolean
                // and set the layout algorithm
                mUseTextAutoSize = !mUseTextAutoSize;
                setUseTextAutoSize(mUseTextAutoSize);
                break;
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

    // Change the layout algorithm used in the WebView
    private void setUseTextAutoSize(boolean useAlgorithm) {
        WebSettings settings = mWebView.getSettings();

        WebSettings.LayoutAlgorithm layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL;
        if(useAlgorithm) {
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING;
        }

        settings.setLayoutAlgorithm(layoutAlgorithm);
    }
}
