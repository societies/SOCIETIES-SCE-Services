/**
 * 
 */
function doAjaxUpdatePassword() {  
	  // get the form values  
	  var userid = $('#element_societiesid').val();
	  var firstPassword = $('#element_password1').val();
	  var secondPassword = $('#element_password2').val();
	   
	  $.ajax({  
	    type: "POST",  
	    url: contexPath + "/updatepassword.html",  
	    data: "userid=" + userid + "&firstPassword=" + firstPassword + "&secondPassword=" + secondPassword,  
	    success: function(response){
	      // we have the response 
	      if(response.status == "SUCCESS"){
	    	  $('#password_info').html("User password has been changed " );
		      $('#password_error').hide('slow');
		      $('#password_info').show('slow');
	      }else{
	    	  errorInfo = "";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
	    	  }
	    	  $('#password_error').html("Please correct following errors: " + errorInfo);
	    	  $('#password_info').hide('slow');
	    	  $('#password_error').show('slow');
	      }	      
	    },  
	    error: function(e){  
	      alert('Error: ' + e);  
	    }  
	  });  
	}  

function doAjaxCheckMatchingPassword() {  
	  // get the form values  
	  var userid = $('#element_societiesid').val();
	  var firstPassword = $('#element_password1').val();
	  var secondPassword = $('#element_password2').val();
	   
	  $.ajax({  
	    type: "POST",  
	    url: contexPath + "/checkmatchingpassword.html",  
	    data: "userid=" + userid + "&firstPassword=" + firstPassword + "&secondPassword=" + secondPassword,  
	    success: function(response){
	      // we have the response 
	      if(response.status == "SUCCESS"){
	    	  $('#password_info').html("Passwords Match " );
		      $('#password_error').hide('slow');
		      $('#password_info').show('slow');
	      }else{
	    	  $('#password_error').html("Passwords do not match ");
	    	  $('#password_info').hide('slow');
	    	  $('#password_error').show('slow');
	      }	      
	    },  
	    error: function(e){  
	      alert('Error: ' + e);  
	    }  
	  });  
	} 