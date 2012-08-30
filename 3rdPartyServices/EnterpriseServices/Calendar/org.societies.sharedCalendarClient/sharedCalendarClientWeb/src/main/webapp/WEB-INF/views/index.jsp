<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<script src="/sharedCal/resources/js/prototype.js" type="text/javascript"></script>
<link rel='stylesheet' type='text/css' href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/themes/smoothness/jquery-ui.css' />
<link rel='stylesheet' type='text/css' href='/sharedCal/resources/css/jquery.weekcalendar.css' />
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js'></script>
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js'></script>
<script type='text/javascript' src='/sharedCal/resources/js/jquery.weekcalendar.js'></script>
<script type='text/javascript' src="/sharedCal/resources/js/SharedCalendar.js"></script>
<title>Shared Calendar Web Client</title>
</head>
<body>	
<div id="divHeader">
	<h2>SOCIETIES Shared Calendar Web UI</h2>
	<span id="cisId_span">Cis ID: <input type="text" id="cisId" /><br/></span>
	<span id="calId_span">Calendar ID: <input type="text" id="calId" /><br/></span>
	<span id="evtId_span">Event ID: <input type="text" id="evtId" /><br/></span>
	<button id="getAllRelevantCIS">Show all CISs I belong to/own</button><br/>
	<button id="getAllCisCalendarsAjax">Show all Cis Calendars</button><br/>
	<button id="createCisCalendarButton">Create Cis Calendar</button><button id="deleteCisCalendarButton">Delete Cis Calendar</button><button id="getCisCalendarEventsButton">Get Cis Calendar Events</button><br/>
	<button id="createCssCalendarButton">Create CSS Calendar</button><button id="deleteCssCalendarButton">Delete CSS Calendar</button><br/>	
	<button id="retrieveCssCalendarEvents">Get Private (CSS) Events</button><br/>
	
	
	
	<br/>Result:<br/>
	<div id="result"></div>
	</div>
	
	<div id="cis">
	<br/>Relevant Cis:<br/>	
		<table id='cisTable' border='1'>
	 		<tbody id='cisTableBody'>
	 		</tbody>
	 	</table>
	 </div>
	 
	<div id="calendars">
	<br/>Calendars:<br/>	
		<table id='calendarsTable' border='1'>
	 		<tbody id='calendarsTableBody'>
	 		</tbody>
	 	</table>
	 </div>
	
	<div id="calEvents">
		 <br/>Calendar Events:<br/>
		 <table id='eventsTable' border='1'>
		 	<tbody id='eventsTableBody'>
		 	</tbody>
		 </table>
	</div>	
	<div id='calendar'>		
	</div>
</body>
</html>
