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


function doAjaxUpdateInfo() {  
	  // get the form values  
	 var userid = $('#element_societiesid').val();
	 
	 
	  var displayCompany = $('input:radio[name=element_company_radio]:checked').val();
	  var company = $('#element_company').val();
	  var department = $('#element_department').val();
	  
	  var displayInterests = $('input:radio[name=element_interest_radio]:checked').val();
	
	  var interestOne = $('#element_interests:checked').val();
	  var interestTwo = $('#element_interest_2:checked').val();
	  var interestThree = $('#element_interest_3:checked').val();
	  

	  
	  $.ajax({  
	    type: "POST",  
	    url: contexPath + "/updateinfo.html",  
	    data: "userid=" + userid + "&displayCompany=" + displayCompany + "&company=" + company + "&department=" + department + "&displayInterests=" + displayInterests + "&interestOne=" + interestOne + "&interestTwo=" + interestTwo  + "&interestThree=" + interestThree ,  
	    success: function(response){
	      // we have the response 
	    	
	    	if(response.status == "SUCCESS"){
		    	  $('#publicinfo_info').html("Inforamtion saved " );
			      $('#publicinfo_error').hide('slow');
			      $('#publicinfo_info').show('slow');
		      }else{
		    	  
		    	  errorInfo = "";
		    	  for(i =0 ; i < response.result.length ; i++){
		    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
		    	  }
		    	  
		    	  $('#publicinfo_error').html("Problem!! " + errorInfo);
		    	  $('#publicinfo_info').hide('slow');
		    	  $('#publicinfo_error').show('slow');
		      }	      
	    	
	     
	    },  
	    error: function(e){  
	      alert('Error: ' + e);  
	    }  
	  });  
	}  


function doAjaxGetUsers() {  
	 
	 
	   
	  $.ajax({  
	    type: "POST",  
	    url: contexPath + "/getusers.html",  
	   
	    success: function(response){
	      // we have the response 
	      if(response.status == "SUCCESS"){
	    	  userInfo = "<ol>";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  userInfo +=  "<a href=\"#\" onclick=\"doAjaxGetUserDetails('" + response.result[i].userid + "');return false\" > " ;
	    		  userInfo +=  "<b> Display Name</b> : " + response.result[i].username + "<br/>";
	    		  		
	    	  }
	    	  userInfo += "</ol>";
	    	  
	    	  $('#users_info').html("Her are the current Users. " + userInfo);
		      $('#users_info').show('slow');
		      $('#users_error').hide('slow');
	      }else{
	    	  errorInfo = "";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
	    	  }
	    	  $('#users_error').html("Please correct following errors: " + errorInfo);
	    	  $('#users_info').hide('slow');
	    	  $('#users_error').show('slow');
	      }	      
	    },  
	    error: function(e){  
	      alert('Error: ' + e);  
	    }  
	  });  
	}


function doAjaxGetUserDetails(friendid) {  
	 
	 
	   
	  $.ajax({  
	    type: "POST",  
	    url: contexPath + "/getusers.html",  
	   
	    success: function(response){
	      // we have the response 
	      if(response.status == "SUCCESS"){
	    	
	    	  
	    	  $('#users_info').html("doAjaxGetUserDetails called with parameter. " + friendid);
		      $('#users_info').show('slow');
		      $('#users_error').hide('slow');
	      }else{
	    	  errorInfo = "";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
	    	  }
	    	  $('#users_error').html("Please correct following errors: " + errorInfo);
	    	  $('#users_info').hide('slow');
	    	  $('#users_error').show('slow');
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