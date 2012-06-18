<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<script src="/sharedCal/resources/js/prototype.js" type="text/javascript"></script>
<link rel='stylesheet' type='text/css' href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/themes/smoothness/jquery-ui.css' />
<link rel='stylesheet' type='text/css' href='/sharedCal/resources/css/jquery.weekcalendar.css' />
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js'></script>
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js'></script>
<script type='text/javascript' src='/sharedCal/resources/js/jquery.weekcalendar.js'></script>
<script type="text/javascript">
        var $j = jQuery.noConflict();
</script>
<script type='text/javascript'>


	var year = new Date().getFullYear();
	var month = new Date().getMonth();
	var day = new Date().getDate();

	   
	$j(document).ready(function() {

		$j('#calendar').weekCalendar({
			timeslotsPerHour: 4,
			height: function($calendar){
				return $j(window).height() - $j("h1").outerHeight();
			},
			eventRender : function(calEvent, $event) {
				if(calEvent.end.getTime() < new Date().getTime()) {
					$event.css("backgroundColor", "#aaa");
					$event.find(".time").css({"backgroundColor": "#999", "border":"1px solid #888"});
				}
			},
			eventNew : function(calEvent, $event) {
				displayMessage("<strong>Added event</strong><br/>Start: " + calEvent.start + "<br/>End: " + calEvent.end);
				alert("You've added a new event. You would capture this event, add the logic for creating a new event with your own fields, data and whatever backend persistence you require.");
			},
			eventDrop : function(calEvent, $event) {
				displayMessage("<strong>Moved Event</strong><br/>Start: " + calEvent.start + "<br/>End: " + calEvent.end);
			},
			eventResize : function(calEvent, $event) {
				displayMessage("<strong>Resized Event</strong><br/>Start: " + calEvent.start + "<br/>End: " + calEvent.end);
			},
			eventClick : function(calEvent, $event) {
				displayMessage("<strong>Clicked Event</strong><br/>Start: " + calEvent.start + "<br/>End: " + calEvent.end);
			},
			eventMouseover : function(calEvent, $event) {
				displayMessage("<strong>Mouseover Event</strong><br/>Start: " + calEvent.start + "<br/>End: " + calEvent.end);
			},
			eventMouseout : function(calEvent, $event) {
				displayMessage("<strong>Mouseout Event</strong><br/>Start: " + calEvent.start + "<br/>End: " + calEvent.end);
			},
			noEvents : function() {
				displayMessage("There are no events for this week");
			},
			data:"events.json"
		});

		function displayMessage(message) {
			$j("#message").html(message).fadeIn();
		}

		$j("<div id=\"message\" class=\"ui-corner-all\"></div>").prependTo($j("body"));
		
	});

</script>
<title>Shared Calendar Web Client</title>
</head>
<body>
	<script language="JavaScript" src="/sharedCal/resources/js/myCalendar.js"></script>
	<h2>Hello World!</h2>
	Cis ID: <input type="text" id="cisId" /><br/>
	Cis Summary: <input type="text" id="cisSummary" /><br/>
	<button id="createCisCalendarButton">Create Cis Calendar</button><br/>
	<button id="retrieveCalendar">Retrieve All Calendars</button><br/>
	<button id="retrieveCssCalendarEvents">Get Private Events</button><br/>
	<br/>Result:<br/>
	<div id="result"></div>
	<div id='calendar'></div>
	<script type="text/javascript">
    <!--      window.defaultStatus = "Welcome to the large URL page."

	
	$("retrieveCalendar").on("click", function(event){
			new Ajax.Updater('result','/sharedCal/retrieveCalendars.do',
					  {
					    method:'get',
					    onSuccess: function(transport){
					      var response = transport.responseText || "no response text";
					      alert("Success! \n\n" + response);
					    },
					    onFailure: function(){ alert('Something went wrong...') }
					  });			
		});

    $("createCisCalendarButton").on("click", function(event){
		new Ajax.Updater('result','/sharedCal/createCisCalendarAjax.do',
				  {
					parameters: { cisId: $F('cisId'), cisSummary: $F('cisSummary') },
				    method:'get',
				    onSuccess: function(transport){
				      var response = transport.responseText || "no response text";
				      alert("Success! \n\n" + response);
				    },
				    onFailure: function(){ alert('Something went wrong...') }
				  });			
	});

    $("retrieveCssCalendarEvents").on("click", function(event){
		new Ajax.Updater('result','/sharedCal/getPrivateEvents.do',
				  {
					method:'get',
				    onSuccess: function(transport){
				      var response = transport.responseText || "no response text";
				      alert("Success! \n\n" + response);
				      $("calendar").data = response;
				    },
				    onFailure: function(){ alert('Something went wrong...') }
				  });			
	});
    //-->
    </script>
</body>
</html>
