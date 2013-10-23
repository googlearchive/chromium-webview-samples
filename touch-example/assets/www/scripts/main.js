var TRANSITION_END = 'webkitTransitionEnd',
TRANSITION_CSS = '-webkit-transition',
TRANSFORM_CSS = '-webkit-transform',
TRANSFORM = 'webkitTransform',
TRANSITION = 'webkitTransition';

window.requestAnimFrame = (function(){
  return  window.requestAnimationFrame       ||
          window.webkitRequestAnimationFrame ||
          window.mozRequestAnimationFrame    ||
          function( callback ){
            window.setTimeout(callback, 1000 / 60);
          };
})();

var centerBlock = document.querySelector('.center-content');
var pullDown = document.querySelector('.pull-down');
var android = document.querySelector('.android');

var touchStartY;
var currentTouchYOffset;
var dropDownHeight = 0;

// Top margin to move android offscreen (234 = android image height)
var androidYOffsetDefault = -234;
var currentAndroidYOffset = androidYOffsetDefault;
var maxAndroidYOffset = -101;

// Handle the state of animation and RAF
var isAnimating = false;
var isEnding = false;

/**
 * Start the Request Animdation Frame
 */
function startRAF() {
	// If we are ending, allow the CSS transforms to handle the animations
	if(isEnding) {
		return;
	}

	isAnimating = true;

	// Position the central block to follow finger
	centerBlock.style[TRANSFORM] = 'translate3d(0,' + currentTouchYOffset + 'px' + ',0)';
	
	// Change the height of the pull down block
	pullDown.style['height'] = dropDownHeight+'px';

	// Calculate the Android position based on Dropdown height
	if(dropDownHeight > 0) {
		// Give a parallax scroll and stop at maxAndroidYOffset
		currentAndroidYOffset = Math.min(androidYOffsetDefault + (0.66 * dropDownHeight), maxAndroidYOffset);
	} else {
		// Default value is to keep offscreen
		currentAndroidYOffset = androidYOffsetDefault;
	}
	android.style[TRANSFORM] = 'translate3d(0,'+currentAndroidYOffset+'px,0) rotate(180deg)';

	// This method exists to keep the page doing something
	// and hence show the FPS counter
	window.requestAnimFrame(startRAF);
}

/**
 * 'touchend' or 'touchcancel' event has been thrown, we should
 * animate to our default state
 */
function resetUI() {
	// If we are already ending, we don't need to start again
	if(isEnding) {
		return;
	}

	isEnding = true;

	dropDownHeight = 0;

	centerBlock.style[TRANSITION] = '.4s ease-out';
	centerBlock.style[TRANSFORM] = 'translate3d(0,0,0)';
			
	pullDown.style[TRANSITION] = '.4s ease-out';
	pullDown.style['height'] = dropDownHeight+'px';
			
	android.style[TRANSITION] = '.4s ease-out';
	android.style[TRANSFORM] = 'translate3d(0,'+androidYOffsetDefault+'px,0) rotate(180deg)';
}

/**
 * On a 'touchstart' event we want to note the initial touches 
 * Y position and being the request animation frame loop if it
 * hasn't already started
 */
centerBlock.addEventListener('touchstart', function(e) {
	e.preventDefault();
	touchStartY = e.targetTouches[0].screenY;

	if(!isAnimating) {
		startRAF();
	}
}, false);

/**
 * On a 'touchmove' event we want to calculate how far the finger
 * has moved (currentTouchYOffset) and how tall the drop down div
 * should be
 */
centerBlock.addEventListener('touchmove', function(e) {
	e.preventDefault();

	currentTouchYOffset = e.targetTouches[0].screenY - touchStartY;
	dropDownHeight = Math.max(currentTouchYOffset, 0);
}, false);

/**
 * On a 'touchend' we want to reset the UI to what it was initially
 */
centerBlock.addEventListener('touchend', function(e) {
	e.preventDefault();

	resetUI();
}, false);

/**
 * On a 'touchcancel' we want to reset the UI to what it was initially
 */
centerBlock.addEventListener('touchcancel', function(e) {
	e.preventDefault();

	resetUI();
});

/**
 * When the animation on centerBlock is complete, we will
 * reset the values and allow touches to interact with the UI
 */
centerBlock.addEventListener(TRANSITION_END, function(e) {
	centerBlock.style[TRANSITION] = 'none';
	pullDown.style[TRANSITION] = 'none';
	android.style[TRANSITION] = 'none';

	currentTouchYOffset = 0;
	dropDownHeight = 0;

	isAnimating = false;
	isEnding = false;
});