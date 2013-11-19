Chromium WebView Samples
===========================

This is a repository with useful examples for developing apps using the Chromium WebView.

If you spot any issues or questions please feel free to file an issue or reach out to [@gauntface](http://www.twitter.com/gauntface).

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