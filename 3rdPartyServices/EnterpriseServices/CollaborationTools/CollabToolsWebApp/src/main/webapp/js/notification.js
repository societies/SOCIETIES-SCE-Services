
/**
 *	JS for Monitor Notification
 *   @author: Chris Lima
 **/

var MESSAGE = "Test!";
var SECONDS = 5;
var TEMP = 1;

function notifications() {
//	return $.ajax({
//        url : 'getnotifications.html'
//    });
}

function showMessage() {
	document.getElementById("ufeedbackNotifications").innerHTML = MESSAGE;
	window.setInterval("blankMessage()", SECONDS * 1000);
}

function blankMessage() {
	document.getElementById("ufeedbackNotifications").innerHTML = notifications();
}