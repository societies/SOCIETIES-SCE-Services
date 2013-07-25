var MAX_EVENTS = 10;

$(document).ready(function() {
    myModule.showMeetingsAndEvents();
    myModule.initChannel();
});

function newRow(meeting) {
	var newRow = $('<tr>');
	newRow.append($('<td>').text(meeting.subject));
	newRow.append($('<td>').text(meeting.description));
	newRow.append($('<td>').text(formatDate(meeting.startTime)));
	newRow.append($('<td>').text(formatDate(meeting.endTime)));
	newRow.append($('<td>').text(meeting.organizer));
	return newRow;
}

function showMeetings(csId) {
	$.ajax({
		type: 'GET',
		url: '/rest/meeting/cs',
		data: { 'csId': csId },
		success: function(meetings) {
			var list = $('#meeting_list');
			list.find("tr:gt(0)").remove();
			for (var i = 0; i < meetings.length; i++) {
				list.append(newRow(meetings[i]));
			}
			console.log(list);
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
			'limit' : MAX_EVENTS
			},
		success: function(events) {
			showEvents(events);
		}
	});
}

function showEvents(events) {
	$('#event_list tr').remove();
	var stage = $('#tabContent');
	stage.empty();
	for (var i = 0; i < events.length && i <MAX_EVENTS; i++) {
		var $event = $('<div style="display: none;">'+events[i].eventText+'</div>');
		stage.append($event);
		$event.show('slow');
	}
}

function addEvent(event) {
	$('#tabContent div:nth-child('+MAX_EVENTS+')').remove();
	var $newEvent = $('<div style="display: none;">'+event+'</div>');
	$('#tabContent').prepend($newEvent);
	$newEvent.show('slow');
}

function showTask(id) {
	//CrowdTaskingApp.showTask(id);
	//$("#taskPanel").panel("open");
	toast("Sorry, the functionality is not implemented yet.");
}

function hideTask() {
	//$("#taskPanel").panel("close");
	toast("Sorry, the functionality is not implemented yet.");
}