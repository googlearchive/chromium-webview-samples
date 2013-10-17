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

        var currentPage = document.querySelector('.current-page');
        newUI.classList.add('current-page');
        if(currentPage) {
        	// We need to animate
            animating = true;

            currentPage.classList.add('animate-to-left');
            newUI.classList.add('animate-from-right');

            currentPage.addEventListener(animEndEventName, function() {
            	animating = false;
                currentPage.classList.remove('current-page')
            }, false);
        }

        currentState = newState;
    }

    function getNotifcationArrow() {
        return document.querySelector('.notification-opened');
    }

    function getLaunchFromNotificationUI() {
        return document.querySelector('.launched-from-notification');
    }

    function getDefaultUI() {
        return document.querySelector('.init-screen');
    }

    function initViews() {
    	var showNotificationBtn = document.querySelector('#show-notification-btn');
    	showNotificationBtn.addEventListener('click', function() {
    		if(window.Android_NotificationBind) {
    			Android_NotificationBind.showNotification('This is an Awesome notification');
    		}
    		changeState(NOTIFICATION_SHOWN);
    	});
    }

    function init() {
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

        initViews();
        changeState(state);
    }

    init();
