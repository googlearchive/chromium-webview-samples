/**
Copyright 2013 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**/

var TRANSITION_END = 'webkitTransitionEnd',
    TRANSITION_CSS = '-webkit-transition',
    TRANSFORM_CSS = '-webkit-transform',
    TRANSFORM = 'webkitTransform',
    TRANSITION = 'webkitTransition';

window.requestAnimFrame = (function(){
    return  window.requestAnimationFrame       ||
            window.webkitRequestAnimationFrame ||
            function( callback ){
                window.setTimeout(callback, 1000 / 60);
            };
})();

var STATE_DEFAULT = 0;
var STATE_TOUCHING = 1;
var STATE_ENDING_TOUCH = 2;

// Handle the state of interaction
var currentState = STATE_DEFAULT;

var centerBlock = document.querySelector('.center-content');
var pullDown = document.querySelector('.pull-down');
var android = document.querySelector('.android');

var touchStartY;
var currentTouchYOffset = 0;
var dropDownHeight = 0;
var initialBlockPosition = 0;

// Top margin to move android offscreen (234 = android image height)
var androidYOffsetDefault = -234;
var currentAndroidYOffset = androidYOffsetDefault;
var maxAndroidYOffset = -101;

var framePending = false;

// Handle of the state of the page
function changeState(newState) {
    if(currentState == newState) {
        // State is the same, nothing to do
        return;
    }

    switch(newState) {
        case STATE_DEFAULT:
            // NOOP
            break;
        case STATE_TOUCHING:
            if(currentState == STATE_ENDING_TOUCH) {
                centerBlock.style[TRANSITION] = 'none';
                pullDown.style[TRANSITION] = 'none';
                android.style[TRANSITION] = 'none';
            }
            break;
        case STATE_ENDING_TOUCH:
            centerBlock.style[TRANSITION] = '.4s ease-out';
            centerBlock.style[TRANSFORM] = 'translate3d(0,0,0)';

            pullDown.style[TRANSITION] = '.4s ease-out';
            pullDown.style['height'] = 0+'px';

            android.style[TRANSITION] = '.4s ease-out';
            android.style[TRANSFORM] = 'translate3d(0,'+androidYOffsetDefault+'px,0) rotate(180deg)';
            break;
    }

    currentState = newState;
}

/**
 * Start the Request Animdation Frame
 */
function updateUI() {
    framePending = false;

    // If we are ending, allow the CSS transforms to finish the animation
    if(currentState != STATE_TOUCHING) {
        return;
    }

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
}

function touchFinish(e) {
    e.preventDefault();

    // Only finish if there are no fingers on the screen
    if(e.touches.length > 0) {
        return;
    }

    changeState(STATE_ENDING_TOUCH);
}

// Position the center block based on the touch positions
function updateUIPositions(e) {
    // Ensure the dropdown element and center block start
    // with the correct initial value
    var scrollOffset = e.targetTouches[0].screenY - touchStartY;
    currentTouchYOffset = initialBlockPosition + scrollOffset;
    dropDownHeight = Math.max(currentTouchYOffset, 0);

    // If we aren't waiting for a RAF callback, add one
    if(!framePending) {
        framePending = true;
        window.requestAnimFrame(updateUI);
    }
}

/**
 * On a 'touchstart' event we want to note the initial touches
 * Y position and being the request animation frame loop if it
 * hasn't already started
 */
centerBlock.addEventListener('touchstart', function(e) {
    e.preventDefault();

    // Only set touchStartY for first finger
    if(e.changedTouches.length != e.touches.length) {
        return;
    }

    // If we are animating, we want to work out where the centerBlock
    // is when we start
    // NOTE: getComputedStyle will not give us an accurate position for
    // the animated element, but for this demo is good enough. It may
    // be better to finish the animation before allowing touch.
    var centerBlockStyle = window.getComputedStyle(centerBlock, null);
    var matrix = centerBlockStyle.getPropertyValue(TRANSFORM_CSS);
    if(matrix && matrix != 'none') {
        var values = matrix.split('(')[1];
        values = values.split(')')[0];
        values = values.split(',');
        initialBlockPosition = parseInt(values[5]);
    } else {
        initialBlockPosition = 0;
    }

    touchStartY = e.targetTouches[0].screenY;

    updateUIPositions(e);

    changeState(STATE_TOUCHING);
}, false);

/**
 * On a 'touchmove' event we want to calculate how far the finger
 * has moved (currentTouchYOffset) and how tall the drop down div
 * should be
 */
centerBlock.addEventListener('touchmove', function(e) {
    e.preventDefault();

    updateUIPositions(e);
}, false);

/**
 * On a 'touchend' we want to reset the UI to what it was initially
 */
centerBlock.addEventListener('touchend', touchFinish, false);

/**
 * On a 'touchcancel' we want to reset the UI to what it was initially
 */
centerBlock.addEventListener('touchcancel', touchFinish, false);

/**
 * When the animation on centerBlock is complete, we will
 * reset the values and allow touches to interact with the UI
 */
centerBlock.addEventListener(TRANSITION_END, function(e) {
    centerBlock.style[TRANSITION] = 'none';
    pullDown.style[TRANSITION] = 'none';
    android.style[TRANSITION] = 'none';

    currentTouchYOffset = 0;
    initialBlockPosition = 0;
    dropDownHeight = 0;

    changeState(STATE_DEFAULT);
});