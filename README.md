Chromium WebView Samples
===========================

This is a repository with useful examples for developing apps using the Chromium WebView.

If you spot any issues or questions please feel free to file an issue or reach out to [@gauntface](http://www.twitter.com/gauntface)

##TEXT_AUTOSIZING

In the Android KitKat+ there will no longer be support for [SINGLE_COLUMN or NARROW_COLUMN layout algorithms](http://developer.android.com/reference/android/webkit/WebSettings.LayoutAlgorithm.html). But in KitKat a new layout algorithm [TEXT_AUTOSIZING](http://developer.android.com/reference/android/webkit/WebSettings.LayoutAlgorithm.html) was added and *textautosizing-example* contains an example to try out.

<p style="text-align: center">
<img src="http://i.imgur.com/03c0isb.png" alt="Image Show off TEXT_AUTOSIZING" />
</p>

##Touch Events in the WebView

In the older version of WebView developers didn't need to implement the *touchcancel* event, although it's good practice to do so. In the Chromium WebView it's important to implement the *touchcancel* event as certain scenarios will now trigger a *touchcancel* event instead of a *touchend* event (i.e. a user scrolls off of an element or e.preventDefault() isn't called in the *touchstart* event. The *web-touch-example* contains an example application using touch to move an element and reveal a little Android.

<p style="text-align: center">
<img src="http://i.imgur.com/ffz4gkV.png" alt="Image of WebView Touch Example" />
</p>
