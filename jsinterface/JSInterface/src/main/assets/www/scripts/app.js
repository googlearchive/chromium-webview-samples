/*global define */
define([], function () {
    'use strict';

    // Add empty touchstart event listener to get :Active class to work
    document.body.addEventListener('touchstart', function(){}, false);

    // Add event listener to button
    var notifyButton = document.querySelector('#notify-button');
    
    notifyButton.addEventListener('click', function(e) {
    	if(!Android) {
    		console.error('Android JS Interface isn\'t loaded');
            return;
    	}

    	Android.showNotification('This is an Awesome notification');
    }, false);


});