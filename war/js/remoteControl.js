$(document).on('pageinit', '#remoteControlPage', function(event, data){       
    $('#defaultSC').bind('tap', function(event, data) {
        event.preventDefault();
        page('1');
    });
    $('#oldSC').bind('tap', function(event, data) {
        event.preventDefault();
        page('2');
    });
    $('#meetingsSC').bind('tap', function(event, data) {
        event.preventDefault();
        page('4');
    });
});

var remoteControl = function(channel, params, callback) {
	$.ajax({
		type: 'GET',
		url: '/rest/remote/'+channel,
		data: params,
		error: function(error) {
			toast(error.responseText);
		},
		success: function(result) {
			//toast(result);
			if (callback !== undefined) {
				callback();
			}
		},
	});
};

var page = function(pageNumber, spaceUrl) {
	remoteControl('changeChannel', {'page':pageNumber});
};

var hideTaskOnPd = function(callback) {
	remoteControl('hideTask', {}, callback);
};

var showTaskOnPd = function(callback) {
	remoteControl('showTask', {'taskId':$('#taskId').val()}, callback);
};