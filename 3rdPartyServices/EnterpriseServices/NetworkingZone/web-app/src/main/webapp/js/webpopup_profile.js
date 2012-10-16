$(document).ready(function() {
	$('a.profileprompt-window').click(function() {
		
		// Getting the variable's value from a link 
		var profilepromptBox = $(this).attr('href');

		//Fade in the Popup and add close button
		$(loginBox).fadeIn(300);
		
		//Set the center alignment padding + border
		var popMargTop = ($(profilepromptBox).height() + 24) / 2; 
		var popMargLeft = ($(profilepromptBox).width() + 24) / 2; 
		
		$(loginBox).css({ 
			'margin-top' : -popMargTop,
			'margin-left' : -popMargLeft
		});
		
		// Add the mask to body
		$('body').append('<div id="mask"></div>');
		$('#mask').fadeIn(300);
		
		return false;
	});
	
	// When clicking on the button close or the mask layer the popup closed
	$('a.close, #mask').live('click', function() { 
	  $('#mask , .profile-popup').fadeOut(300 , function() {
		$('#mask').remove();  
	}); 
	return false;
	});
});