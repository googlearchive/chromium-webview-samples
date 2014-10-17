package com.google.chrome.android.webrtcsample;

import android.annotation.TargetApi;
import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.PermissionRequest;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private WebView mWebRTCWebView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mWebRTCWebView = (WebView) rootView.findViewById(R.id.fragment_main_webview);

            setUpWebViewDefaults(mWebRTCWebView);

            mWebRTCWebView.loadUrl("https://apprtc-m.appspot.com/");

            mWebRTCWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onPermissionRequest(final PermissionRequest request) {
                    Log.d(TAG, "onPermissionRequest");
                    getActivity().runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.L)
                        @Override
                        public void run() {
                            if(request.getOrigin().toString().equals("https://apprtc-m.appspot.com/")) {
                                request.grant(request.getResources());
                            } else {
                                request.deny();
                            }
                        }
                    });
                }

            });

            return rootView;
        }

        @Override
        public void onStop() {
            super.onStop();

            /**
             * When the application falls into the background we want to stop the media stream
             * such that the camera is free to use by other apps.
             */
            mWebRTCWebView.evaluateJavascript("if(window.localStream){window.localStream.stop();}", null);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        /**
         * Convenience method to set some generic defaults for a
         * given WebView
         *
         * @param webView
         */
        @TargetApi(Build.VERSION_CODES.L)
        private void setUpWebViewDefaults(WebView webView) {
            WebSettings settings = webView.getSettings();

            // Enable Javascript
            settings.setJavaScriptEnabled(true);

            // Use WideViewport and Zoom out if there is no viewport defined
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);

            // Enable pinch to zoom without the zoom buttons
            settings.setBuiltInZoomControls(true);

            // Allow use of Local Storage
            settings.setDomStorageEnabled(true);

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                // Hide the zoom controls for HONEYCOMB+
                settings.setDisplayZoomControls(false);
            }

            // Enable remote debugging via chrome://inspect
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }

            webView.setWebViewClient(new WebViewClient());

            // AppRTC requires third party cookies to work
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(mWebRTCWebView, true);
        }
    }

}
