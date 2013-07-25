$(document).ready(function() {
    $('#task_list').dataTable({
        "bJQueryUI": true,
        "bPaginate": false,
        "bLengthChange": false,
        "bFilter": false,
        "bSort": false,
        "bInfo": false,
        "bAutoWidth": false
    });
    $('#eventsTable').dataTable();
    
    oTable = $('#top_users').dataTable({
        "bJQueryUI": true,
        "bPaginate": false,
        "bLengthChange": false,
        "bFilter": false,
        "bSort": false,
        "bInfo": false,
        "bAutoWidth": false
    });
    
    myModule.showEvents();
    myModule.initChannel();
});

function task_template(row, task) {
	row.find('.task_status').append('<img src="/images/'+task.status+'.png" alt="'+task.status+'">');
	row.find('.task_mytask').text(task.myTask);
	row.find('.task_id').text(task.id);
	row.find('.task_title').text(task.title);
	row.find('.task_postedby').text(task.postedBy);
	var d1=new Date(task.dueDate);
	row.find('.task_duedate').text(d1.getDate()+"."+(d1.getMonth()+1)+"."+d1.getFullYear());
    var desc = task.description;
    if (desc.length > 200) {
    	desc = desc.substring(0, 200)+"...";
    }
	row.find('.task_description').text(desc);
	row.find('.task_description').attr('title', task.description);

    var meetings = "";
	if (task.meetings != null && task.meetings.length > 0) {
		meetings = task.meetings[0].subject+" in "+task.meetings[0].cs.name+" at "+formatDate(task.meetings[0].startTime);
		for (var i=1; i<task.meetings.length; i++) {
			meetings += ", " + task.meetings[i].subject+" in "+task.meetings[i].cs.name+" at "+formatDate(task.meetings[i].startTime);
		}
	}
	
	row.find('.task_meetings').text(meetings);
	//row.find('.task_qrcode').append('<img src="http://chart.apis.google.com/chart?cht=qr&chs=100x100&chl=http://'+location.host+'/mobile.html?id='+task.id+'"/>');
	row.find('.task_qrcode').append('<a href="http://'+location.host+'/task/view?id='+task.id+'"><img src="http://chart.apis.google.com/chart?cht=qr&chs=100x100&chl=http://'+location.host+'/task/view?id='+task.id+'"/></a>');
	return row;
}

function user_template(row, user) {
	row.find('.user_name').text(user.firstName+' '+user.lastName);
	row.find('.user_karma').text(user.karma);
	return row;
}

function showTasks(communityId) {
	$.ajax({
		type: 'GET',
		url: '/rest/tasks/cs',
		data: { 'communityId': communityId },
		success: function(tasks) {
			$("#task_list").find("tr:gt(2)").remove();
			for (var i = 0; i < tasks.length; i++) {
				var newRow = $('#task_list .template').clone().removeClass('template');
				task_template(newRow, tasks[i])
				.appendTo('#task_list')
				.fadeIn();
			}
		}
	});
}

function showTasks2(communityId) {
	$.ajax({
		type: 'GET',
		url: '/rest/tasks/publicdisplay',
		data: { 'spaceId': communityId },
		success: function(tasks) {
			$("#task_list").find("tr:gt(2)").remove();
			for (var i = 0; i < tasks.length; i++) {
				var newRow = $('#task_list .template').clone().removeClass('template');
				task_template(newRow, tasks[i])
				.appendTo('#task_list')
				.fadeIn();
			}
			$("#task_list").trigger("create");
		}
	});
}

function showTopUsers() {
	$.ajax({
		type: 'GET',
		url: '/rest/users/top',
		data: { 'limit': 7 },
		success: function(users) {
			$("#top_users").find("tr:gt(2)").remove();
			for (var i = 0; i < users.length; i++) {
				var newRow = $('#top_users .topuser_template').clone().removeClass('topuser_template');
				user_template(newRow, users[i])
				.appendTo('#top_users')
				.fadeIn();
			}
		},
		error: function(text) {
			//alert("error:"+text.responseText);
		}
	});
}

function getEvents(communityId, spaceId) {
	$.ajax({
		type: 'GET',
		url: '/rest/event',
		data: { 
			'communityId': communityId,
			'spaceId': spaceId,
			'limit' : 8
			},
		success: function(events) {
			showEvents(events);
		    //setTimeout(getEvents, 3000);
		}
	});
}

function showEvents(events) {
	$('#event_list tr').remove();
	var eventsTable = $('#event_list');
	for (var i = 0; i < events.length; i++) {
		var newRow = $('<tr>');
		newRow.append(events[i].eventText);
		eventsTable.append(newRow);
	}
}

function addEvent(event) {
	document.getElementById("event_list").insertRow(0).innerHTML = '<td>'+event+'</td>';
	
/*	if ($("#event_list > tbody > tr").length > 7)
		$('#event_list tr:first').remove();
	var newRow = $('<tr>');
	newRow.append(event).hide();
	newRow.appendTo('#event_list').slideToggle();*/
}

function formatDate(dateString) {
	var date = new Date(dateString);
	var min = date.getMinutes();
    return ''+date.getDate()+'.'+(date.getMonth()+1)+'.'+date.getFullYear()+' '+date.getHours()+':'+(min<=9?'0'+min:min);
}