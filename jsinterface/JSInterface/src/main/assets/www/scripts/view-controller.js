/*global define */
define([], function () {
    'use strict';

    var animEndEventNames = {
        'WebkitAnimation' : 'webkitAnimationEnd',
        'OAnimation' : 'oAnimationEnd',
        'msAnimation' : 'MSAnimationEnd',
        'animation' : 'animationend'
        };
    
    // animation end event name
    var animEndEventName = animEndEventNames[ Modernizr.prefixed( 'animation' ) ];

    var NOTIFICATION_SHOWN = 0;
    var LAUNCH_FROM_NOTIFICATION = 1;
    var DEFAULT = 2;

    var exports = {};

    var currentState;
    var animating = false;

    function changeState(newState) {
        if(animating) {
            return;
        }

        console.log('newState = '+newState);

        var newUI;
        switch(newState) {
            case NOTIFICATION_SHOWN:
                window.location.hash = '#notificationOpened';
                newUI = getNotifcationArrow();
            break;
            case LAUNCH_FROM_NOTIFICATION:
                window.location.hash = '#launchFromNotification';
                newUI = getLaunchFromNotificationUI();
            break;
            case DEFAULT:
                window.location.hash = '#';
                newUI = getDefaultUI();
            break;
        }

        newUI.classList.add('current-page');

        var currentPage = document.querySelector('.current-page');
        if(!currentPage) {
            // Nothing to animate, just display
            document.body.appendChild(newUI);
        } else {
            animating = true;

            document.body.appendChild(newUI);

            currentPage.classList.add('animate-to-left');
            newUI.classList.add('animate-from-right');

            currentPage.addEventListener(animEndEventName, function() {
                document.body.removeChild(currentPage);
            }, false);
        }

        currentState = newState;
    }

    function generatePageContainer(stateName, elements) {
        var pageContainer = document.createElement('div');
        pageContainer.classList.add('page-container');

        if(stateName != null) {
            pageContainer.classList.add(stateName);
        }

        if(elements != null) {
            for(var i = 0; i < elements.length; i++) {
                pageContainer.appendChild(elements[i]);
            }
        }

        return pageContainer;
    }

    function generateMainContent(elements) {
        var mainContent = document.createElement('div');
        mainContent.classList.add('main-content');

        if(elements != null) {
            for(var i = 0; i < elements.length; i++) {
                mainContent.appendChild(elements[i]);
            }
        }

        return mainContent;
    }

    function getPageHeader(title) {
        var header = document.createElement('header');
        var heading = document.createElement('h1');
        heading.appendChild(document.createTextNode(title));

        header.appendChild(heading);

        return header;
    }

    function getNotifcationArrow() {
        // Add the Arrow Image
        var arrowImg = document.createElement('img');
        arrowImg.src = 'images/arrow.png';
        arrowImg.classList.add('notification-arrow');
        
        var p = document.createElement('p');
        p.appendChild(document.createTextNode('There should be a notification waiting for you...'));

        var mainContent = generateMainContent([p]);
        var title = getPageHeader('Notification Time');

        return generatePageContainer('notificationOpened', [arrowImg, title, mainContent]);
    }

    function getLaunchFromNotificationUI() {
        var p = document.createElement('p');
        p.appendChild(document.createTextNode('There is only so much I can do with a notification you know...'));

        var mainContent = generateMainContent([p]);
        var title = getPageHeader('You Clicked the Notification');

        return generatePageContainer('launchFromNotification', [title, mainContent]);
    }

    function getDefaultUI() {
        var button = document.createElement('button');
        button.appendChild(document.createTextNode('Press Me!'));
        button.addEventListener('click', function(e) {
            console.log("On Click");
            if(typeof Android !== 'undefined') {
                Android.showNotification('This is an Awesome notification');
            } else {
                console.error('Android JS Interface isn\'t loaded');
            }

            changeState(NOTIFICATION_SHOWN);
        }, false);

        var mainContent = generateMainContent([button]);
        var title = getPageHeader('Notify Me of Awesome');
        
        return generatePageContainer(null, [title, mainContent]);
    }

    exports.init = function() {
        var windowHash = window.location.hash;
        var state;
        console.log('window.location.hash = '+windowHash);
        if(windowHash == '#launchFromNotification') {
            state = LAUNCH_FROM_NOTIFICATION;
        } else if(windowHash == '#notificationOpened') {
            state = NOTIFICATION_SHOWN;
        } else {
            state = DEFAULT;
        }

        changeState(state);
    };


    return exports;
});