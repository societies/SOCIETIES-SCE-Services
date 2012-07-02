<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<script src="/sharedCal/resources/js/prototype.js" type="text/javascript"></script>
<link rel='stylesheet' type='text/css' href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/themes/smoothness/jquery-ui.css' />
<link rel='stylesheet' type='text/css' href='/sharedCal/resources/css/jquery.weekcalendar.css' />
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js'></script>
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js'></script>
<script type='text/javascript' src='/sharedCal/resources/js/jquery.weekcalendar.js'></script>
<title>Shared Calendar Web Client</title>
</head>
<body>
	<script language="JavaScript" src="/sharedCal/resources/js/SharedCalendar.js"></script>
	<h2>SOCIETIES Shared Calendar Web UI</h2>
	Cis ID: <input type="text" id="cisId" /><br/>
	Cis/Css Calendar Summary: <input type="text" id="cisSummary" /><br/>
	Calendar ID: <input type="text" id="calId" /><br/>
	<button id="getAllCisCalendarsAjax">Show all Cis Calendars</button><br/>
	<button id="createCisCalendarButton">Create Cis Calendar</button><button id="deleteCisCalendarButton">Delete Cis Calendar</button><br/>
	<button id="createCssCalendarButton">Create CSS Calendar</button><button id="deleteCssCalendarButton">Delete CSS Calendar</button><br/>	
	<button id="retrieveCssCalendarEvents">Get Private (CSS) Events</button><br/>
	<br/>Result:<br/>
	<div id="result"></div>
	<div id='calendar'></div>
	<script type='text/javascript' src='/sharedCal/resources/js/myCalendar.js'></script>
</body>
</html>
