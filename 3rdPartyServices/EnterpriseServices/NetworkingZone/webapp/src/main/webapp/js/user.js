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


function doAjaxUpdateAllInfo() {  
	
		var displayName = $('#element_displayname').val();
		var companyName = $('#element_companyname').val();
		var deptName = $('#element_department').val();
	
	  var showEmpHist = $('input:radio[name=element_employment_radio]:checked').val();
	  var emphistCompany = $('#element_employment_history_company').val();
	  var emphistDept = $('#element_employment_history_department').val();
	  
	  var showEduHist = $('input:radio[name=element_education_radio]:checked').val();
	
	  var eduhistCollege = $('#element_education_history_college').val();
	  var eduhistGradYear = $('#element_employment_history_when').val();
	  var eduhistCourse = $('#element_employment_history_course').val();
	  
	  
	  $.ajax({  
	    type: "POST",  
	    url: contexPath + "/updateallinfo.html",
	    contentType: "application/json",
	    data: "displayName=" + displayName + "&companyName=" + companyName + "&deptName=" + deptName + 
		  "&emphistCompany=" + emphistCompany + "&emphistDept=" + emphistDept + 
		  "&eduhistCollege=" + eduhistCollege + "&eduhistGradYear=" + eduhistGradYear + "&eduhistCourse=" + eduhistCourse + 
		  "&showEduHist=" + showEduHist + "&showEmpHist=" + showEmpHist ,
		  dataType: "json",
	  	success: function(response){
	      // we have the response 
	    	
	    	if(response.status == "SUCCESS"){
		    	  $('#publicinfo_info').html("Information saved " );
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


function doAjaxUpdateAllInfotemp() {  
	
	var displayName = $('#element_displayname').val();
	var companyName = $('#element_companyname').val();
	var deptName = $('#element_department').val();

  var showEmpHist = $('input:radio[name=element_employment_radio]:checked').val();
  var emphistCompany = $('#element_employment_history_company').val();
  var emphistDept = $('#element_employment_history_department').val();
  
  var showEduHist = $('input:radio[name=element_education_radio]:checked').val();

  var eduhistCollege = $('#element_education_history_college').val();
  var eduhistGradYear = $('#element_employment_history_when').val();
  var eduhistCourse = $('#element_employment_history_course').val();
  
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/updateallinfo.html",
    contentType: "application/json",
    data: "displayName=" + displayName ,
	dataType: "json",
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

function doAjaxUpdateDisplayName() {  
	
	var displayName = $('#element_displayname').val();
	
  
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/updatedisplayname.html",
    data: "displayName=" + displayName,
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
	    	  $('#publicinfo_info').html("Information saved " );
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

function doAjaxUpdatePersonalDetails() {  
	
	var displayName = $('#element_displayname').val();
	var companyName = $('#element_companyname').val();
	var deptName = $('#element_department').val();
  
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/updatepersonaldetails.html",
    data: "displayName=" + displayName + "&companyName=" + companyName + "&deptName=" + deptName,
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
	    	  $('#publicinfo_info_userdetails').html("User Details Information saved " );
		      $('#publicinfo_error_userdetails').hide('slow');
		      $('#publicinfo_info_userdetails').show('slow');
	      }else{
	    	  
	    	  errorInfo = "";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
	    	  }
	    	  
	    	  $('#publicinfo_error_userdetails').html("Problem saving details !! " + errorInfo);
	    	  $('#publicinfo_info_userdetails').hide('slow');
	    	  $('#publicinfo_error_userdetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxAddEmploymentHistory() {  
	
	var company = $('#element_employment_history_company').val();
	var department = $('#element_employment_history_department').val();
  
	$("#employdetailslist").show();
	$("#employdetailslist").fadeIn(400).html('<span class="loading">Adding new Employment Histroy Item </span>');


	
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/addemploymenthistoryitem.html",
    data: "company=" + company + "&department=" + department, 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
	    	  $('#publicinfo_info_employdetails').html("Employment History Item Added" );
		      $('#publicinfo_error_employdetails').hide('slow');
		      $('#publicinfo_info_employdetails').show('slow');
		      
		      $("ol#listdata").prepend("<tr><td>" + company + "</td><td>" + department + "</td><tr>");
		      $("ol#listdata li:first").slideDown("slow");
		      $("#employdetailslist").hide();
		      
	      }else{
	    	  
	    	  errorInfo = "";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
	    	  }
	    	  
	    	  $("#employdetailslist").hide();
	    	  $('#publicinfo_error_userdetails').html("Problem saving details !! " + errorInfo);
	    	  $('#publicinfo_info_userdetails').hide('slow');
	    	  $('#publicinfo_error_userdetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxAddEducationHistory() {  
	
	var college = $('#element_education_history_college').val();
	var course = $('#element_education_history_course').val();
  
	$("#edudetailslist").show();
	$("#edudetailslist").fadeIn(400).html('<span class="loading">Adding new Education Histroy Item </span>');


	
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/addeducationhistoryitem.html",
    data: "college=" + college + "&course=" + course, 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
	    	  $('#publicinfo_info_edudetails').html("Educational History Item Added" );
		      $('#publicinfo_error_edudetails').hide('slow');
		      $('#publicinfo_info_edudetails').show('slow');
		      
		      $("ol#edulistdata").prepend("<tr><td>" + college + "</td><td>" + course + "</td><tr>");
		      $("ol#edulistdata li:first").slideDown("slow");
		      $("#edudetailslist").hide();
		      
	      }else{
	    	  
	    	  errorInfo = "";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
	    	  }
	    	  
	    	  $("#edudetailslist").hide();
	    	  $('#publicinfo_error_edudetails').html("Problem saving details !! " + errorInfo);
	    	  $('#publicinfo_info_edudetails').hide('slow');
	    	  $('#publicinfo_error_edudetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxSetEducationHistoryVisible() {  
	
	var showeduhist = $('input:radio[name=element_education_radio]:checked').val();
  
		
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/educationhistoryvisible.html",
    data: "showeduhist=" + showeduhist , 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
	    	  $('#publicinfo_info_edudetails').html("Educational History - Visibility Changed" );
		      $('#publicinfo_error_edudetails').hide('slow');
		      $('#publicinfo_info_edudetails').show('slow');
		      
	      }else{
	    	  
	    	  $("#edudetailslist").hide();
	    	  $('#publicinfo_error_edudetails').html("Problem changing visibility !! ");
	    	  $('#publicinfo_info_edudetails').hide('slow');
	    	  $('#publicinfo_error_edudetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxSetEmploymentHistoryVisible() {  
	
	var showemphist = $('input:radio[name=element_employment_radio]:checked').val();
  
		
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/employmenthistoryvisible.html",
    data: "showemphist=" + showemphist , 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
	    	  $('#publicinfo_info_empdetails').html("Employment History - Visibility Changed" );
		      $('#publicinfo_error_empdetails').hide('slow');
		      $('#publicinfo_info_empdetails').show('slow');
		      
	      }else{
	    	  
	    	  $("#edudetailslist").hide();
	    	  $('#publicinfo_error_empdetails').html("Problem changing visibility !! ");
	    	  $('#publicinfo_info_empdetails').hide('slow');
	    	  $('#publicinfo_error_empdetails').show('slow');
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





