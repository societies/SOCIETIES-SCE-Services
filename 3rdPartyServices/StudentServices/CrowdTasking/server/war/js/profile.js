$(document).on('pageinit', '#profilePage', function(){       
	getUserProfile();
	$("#userProfileForm").validate({
		errorPlacement: function(error, element) {
			error.insertAfter($(element).parent());
		}
	});
	$('#submitButton').bind('tap', function(event, data) {
	    event.preventDefault();
	    //$('#userProfileForm').submit();
	    saveProfile();
	});

	var results = new RegExp('[\\?&]continue=([^&#]*)').exec(window.location.href);
	if (results != null) {
		$('#my-link').attr('href', results[1]);
	}

    $('#addInterestsButton').bind('tap', function(event, data) {
        event.preventDefault();
        addInterests();
    });

    $('#addTagAndSaveButton').bind('tap', function(event, data) {
    	event.preventDefault();
    	$("#popupDialog").popup("close");
    	addInterests();
    	saveProfile();
    });

    $('#dontAddTagAndSaveButton').bind('tap', function(event, data) {
    	event.preventDefault();
    	$("#popupDialog").popup("close");
    	$('#newTag').val('');
    	saveProfile();
    });
});

function saveProfile() {
    var newTag = $('#newTag').val().trim();
	if (newTag != "") {
		$("#tagWarning").text('Do you want to add: "'+newTag+'"?');
		$("#popupDialog").popup("open");
		return;
	}
    
    var tags = [];
	$('#taskTagsDiv a').each(function(index) {
        var tag = $(this).text();
        tag = tag.replace(/^\s+|\s+$/g, '');
		tags.push(tag);
	});
	$('#interests').val(JSON.stringify(tags));
    postUser();
}

function getUserProfile() {
	$.ajax({
		type: 'GET',
		url: '/rest/users/me',
		success: function(user) {
			if (user.id == null) {
				// register
				$('#formTitle').text('Register');
				$('#backButton').hide();
//				$("#newAccountNotification").popup("open");
                toast("This login is new to Crowd Tasking. Please create a new account.");
			}
			else {
				// update profile
				$('#submitButton').text('Update profile');
//				$('#submitButton').button('refresh');
			}
			fillForm(user);
		}
	});
};

function postUser() {	  
    var form_data = $('#userProfileForm').serialize();
    $.ajax({
      type: "POST",
      url: "/rest/users/profile",
      data: form_data,
      error: function(error) {
    	  toast(error.responseText);
      },
      success: function() {
    	  $("#my-link").trigger('click');
      },
      complete: function() {
      }
    });
}

function connectedAccountToString(connectedAccount) {
	if (connectedAccount.federatedIdentity === "SOCIETIES") {
		return connectedAccount.provider+":"+connectedAccount.userId;
	}
	else {
		return connectedAccount.provider+":"+connectedAccount.nickName;
	}
	
}

function fillForm(user) {
	$('#userId').val(user.id);
	$('#fname').val(user.firstName);
	$('#lname').val(user.lastName);
	if (user.interests != null) {
		for (var i = 0; i < user.interests.length; i++) {
			showInterestButton(user.interests[i]);
		}
	}
	$('#email').val(user.email);
	var connAccs = "";
	if (user.connectedAccounts.length > 0) {
		connAccs += connectedAccountToString(user.connectedAccounts[0]);
		for (var i = 1; i < user.connectedAccounts.length; i++) {
			connAccs += ', '+connectedAccountToString(user.connectedAccounts[i]);
		}
	}
	$('#federatedIdentities').val(connAccs);
	var picNumber = user.picUrl.substring(user.picUrl.length-5,user.picUrl.length-4);
	$("#radio-choice-"+picNumber).attr("checked",true).checkboxradio("refresh");
	//$('#karma').val(user.karma);
}

function addInterests() {
	var newTag = $('#newTag').val().trim();
    if (newTag == '') return;
    var tags = newTag.split(",");
    for (var i=0; i<tags.length; i++) {
    	showInterestButton(tags[i]);
    }
    $('#newTag').val("");
}

function showInterestButton(interest) {// \xA0
    var removeButton = function(button) {
        return function(event, data) {
        	event.preventDefault();
            button.remove();
        };
    };

    var taskDiv = $('#taskTagsDiv');
    var tagButton = $('<a>');
    //editLink.attr('href', '#');
    tagButton.attr('data-inline', 'true');
    tagButton.attr('data-icon', 'delete');
    tagButton.attr('data-iconpos', 'right');
    tagButton.attr('data-mini', 'true');
    tagButton.bind('tap', removeButton(tagButton));
    tagButton.append(interest);
    taskDiv.append(tagButton);
    tagButton.button();
}