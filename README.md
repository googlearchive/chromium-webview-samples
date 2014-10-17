Chromium WebView Samples
===========================

This is a repository with useful examples for developing apps using the Chromium WebView.

If you spot any issues or questions please feel free to file an issue or reach out to [@gauntface](http://www.twitter.com/gauntface).

## WebRTC

In the Developer Preview of L the WebView will support WebRTC.

The methods this example relies may change as this is only a preview. At the
moment the example is using the new permission request API in WebChromeClient:

    mWebRTCWebView.setWebChromeClient(new WebChromeClient() {
        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    request.grant(request.getResources());
                }
            });
        }
    });

In the final version of this example should change with the launch of L to use the
preauthorizePermission method (At the moment this method is not working).

<p align="center">
<img src="http://i.imgur.com/AUYL7dK.png" alt="WebRTC on the Chrome WebView Example" />
</p>

##TEXT_AUTOSIZING

From KitKat and above, there will no longer be support for [SINGLE_COLUMN or NARROW_COLUMN layout algorithms](http://developer.android.com/reference/android/webkit/WebSettings.LayoutAlgorithm.html).

However a new layout algorithm [TEXT_AUTOSIZING](http://developer.android.com/reference/android/webkit/WebSettings.LayoutAlgorithm.html) was added and *textautosizing-example* contains a basic example to see the affects of the algorithm.

<p align="center">
<img src="http://i.imgur.com/03c0isb.png" alt="Image Show off TEXT_AUTOSIZING" />
</p>

##Touch Events in the WebView

In the older version of the WebView developers didn't need to implement the *touchcancel* event, although it's good practice to do so.

In the Chromium WebView it's important to implement the *touchcancel* event as certain scenarios will  trigger a *touchcancel* event instead of a *touchend* event, where they wouldn't before (i.e. a user scrolls off of an element or e.preventDefault() isn't called in the *touchstart* event).

The *web-touch-example* contains a simple app which uses touch to move an element and reveal a little Android.

<p align="center">
<img src="http://i.imgur.com/ffz4gkV.png" alt="Image of WebView Touch Example" />
</p>

##JS Interface in the WebView

This example demonstrates the following:

  * Using evaluateJavascript()
  * Adding a javascript interface
  * Hiding the white flash of the WebView load
  * Saving state of the WebView

<p align="center">
<img src="http://i.imgur.com/iL4aB0r.png" alt="Image of WebView JS Interface" />
</p>

## Fullscreen Video

This demo illustrates how to set a custom poster image, how to show the fullscreen
button for a <video> element only when available and how to implement fullscreen
videos.

<p align="center">
<img src="http://i.imgur.com/J3x6ch8.png" alt="Image of Fullscreen Video Sample" />
</p>

## File Input

This demonstrates the use of the onShowFileChooser() method in WebChromeClient
including how to handle the activity result.

<p align="center">
<img src="http://i.imgur.com/0wynCKL.png" alt="Image of File Input Sample" />
</p>
