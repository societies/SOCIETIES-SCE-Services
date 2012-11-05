function doAjaxAddEducationHistory() {  
	
	var college =document.profileform.neweduwhere.value;
	var course = document.profileform.neweduwhat.value;
  
	/*
	$("#edudetailslist").show();
	$("#edudetailslist").fadeIn(400).html('<span class="loading">Adding new Education Histroy Item </span>');
	*/

	
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/addeducationhistoryitem.html",
    data: "where=" + college + "&what=" + course, 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
	    	  $('#publicinfo_info_edudetails').html("Educational History Item Added" );
		      $('#publicinfo_error_edudetails').hide('slow');
		      $('#publicinfo_info_edudetails').show('slow');
		      
		 //     $("ol#edulistdata").prepend("<tr><td>" + college + "</td><td>" + course + "</td><tr>");
		 //     $("ol#edulistdata li:first").slideDown("slow");
		 //     $("#edudetailslist").hide();
		      
	      }else{
	    	  
	    	  errorInfo = "";
	    	  for(i =0 ; i < response.result.length ; i++){
	    		  errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
	    	  }
	    	  
	    //	  $("#edudetailslist").hide();
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

function doAjaxSetPersonalVisible() {  
	
	var details = $('input:radio[name=element_personal_radio]:checked').val();
  
		
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/personalvisible.html",
    data: details , 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
//	    	  $('#publicinfo_info_personaldetails').html("Educational History - Visibility Changed" );
//		      $('#publicinfo_error_edudetails').hide('slow');
//		      $('#publicinfo_info_edudetails').show('slow');
		      
	      }else{
	    	  
//	    	  $("#edudetailslist").hide();
//	    	  $('#publicinfo_error_edudetails').html("Problem changing visibility !! ");
//	    	  $('#publicinfo_info_edudetails').hide('slow');
//	    	  $('#publicinfo_error_edudetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxSetAboutVisible() {  
	
	var details = $('input:radio[name=element_about_radio]:checked').val();
  
		
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/aboutvisible.html",
    data: details , 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
//	    	  $('#publicinfo_info_personaldetails').html("Educational History - Visibility Changed" );
//		      $('#publicinfo_error_edudetails').hide('slow');
//		      $('#publicinfo_info_edudetails').show('slow');
		      
	      }else{
	    	  
//	    	  $("#edudetailslist").hide();
//	    	  $('#publicinfo_error_edudetails').html("Problem changing visibility !! ");
//	    	  $('#publicinfo_info_edudetails').hide('slow');
//	    	  $('#publicinfo_error_edudetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxSetAboutEmplHistoryVisible() {  
	
	var details = $('input:radio[name=element_employment_radio]:checked').val();
  
		
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/employhistoryvisible.html",
    data: details , 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
//	    	  $('#publicinfo_info_personaldetails').html("Educational History - Visibility Changed" );
//		      $('#publicinfo_error_edudetails').hide('slow');
//		      $('#publicinfo_info_edudetails').show('slow');
		      
	      }else{
	    	  
//	    	  $("#edudetailslist").hide();
//	    	  $('#publicinfo_error_edudetails').html("Problem changing visibility !! ");
//	    	  $('#publicinfo_info_edudetails').hide('slow');
//	    	  $('#publicinfo_error_edudetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxSetAboutEmployVisible() {  
	
	var details = $('input:radio[name=element_employment_radio]:checked').val();
  
		
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/employmentvisible.html",
    data: details , 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
//	    	  $('#publicinfo_info_personaldetails').html("Educational History - Visibility Changed" );
//		      $('#publicinfo_error_edudetails').hide('slow');
//		      $('#publicinfo_info_edudetails').show('slow');
		      
	      }else{
	    	  
//	    	  $("#edudetailslist").hide();
//	    	  $('#publicinfo_error_edudetails').html("Problem changing visibility !! ");
//	    	  $('#publicinfo_info_edudetails').hide('slow');
//	    	  $('#publicinfo_error_edudetails').show('slow');
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

function doAjaxAddNote() {  
	
	var newnote = document.getElementById("newnote").value;
		
  
  $.ajax({  
    type: "POST",  
    url: contexPath + "/addnote.html",
    data: newnote , 
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
    		$("ul#notes").append("<li>" + newnote + "</li>");
	      }else{
	    	  
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
}  

