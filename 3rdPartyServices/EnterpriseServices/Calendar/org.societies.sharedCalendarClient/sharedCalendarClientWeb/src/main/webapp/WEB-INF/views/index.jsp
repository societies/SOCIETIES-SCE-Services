<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<!-- CSSs -->
<link rel='stylesheet' type='text/css' href='/sharedCal/resources/css/jquery.weekcalendar.css' />
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.3/themes/ui-lightness/jquery-ui.css" media="all" />
<!-- Javascript -->
<script type="text/javascript" src="/sharedCal/resources/js/prototype.js" ></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.7.3/jquery-ui.min.js"></script>
<script type='text/javascript' src="/sharedCal/resources/js/jquery.weekcalendar.js"></script>
<script type='text/javascript' src="/sharedCal/resources/js/SharedCalendar.js"></script>

<title>Shared Calendar Web Client</title>
</head>
<body>	
<div id="divHeader">
	<h2>SOCIETIES Shared Calendar Web UI</h2>
	<h3>Move your mouse over a button to highlight the mandatory input fields</h3>
	<hr/>
	<span id="cisId_span">Cis ID: <input type="text" id="cisId" /><br/></span>
	<span id="calId_span">Calendar ID: <input type="text" id="calId" /><br/></span>
	<span id="cisSummary_span">Calendar Summary: <input type="text" id="cisSummary" /><br/></span>
	<span id="evtId_span">Event ID: <input type="text" id="evtId" /><br/></span>
	<button id="getAllRelevantCIS">Show all CISs I belong to/own</button><br/>
	<button id="getAllCisCalendarsAjax">Show all Cis Calendars</button><br/>
	<button id="createCisCalendarButton">Create Cis Calendar</button><button id="deleteCisCalendarButton">Delete Cis Calendar</button><button id="getCisCalendarEventsButton">Get Cis Calendar Events</button><br/>
	<button id="createCssCalendarButton">Create CSS Calendar</button><button id="deleteCssCalendarButton">Delete CSS Calendar</button><br/>	
	<button id="retrieveCssCalendarEvents">Get Private (CSS) Events</button><br/>
	
	<div>
		Use the yyyy-MM-dd'T'HH:mm:ssz format for start and end dates.<br/>
		Example:<pre>2012-08-31T10:00:00+0200</pre><br/>
		No validations are performed.<br/>
		Start Date: <input id="evt_start" type="text" value="" name="evt_start"><br/>
		End Date: <input id="evt_end" type="text" value="" name="evt_end"><br/>
		Event Description: <input id="evtDescr" type="text"><br/>
		Event Summary: <input id="evtSummary" type="text" value=""><br/>
		Event Location: <input id="evtLocation" type="text" value=""><br/>
		<button id="add_CIS_Evt">Add Event To CIS Calendar</button><button id="del_CIS_Evt">Delete Event from CIS Calendar</button><br/>
		<button id="add_CSS_Evt">Add Event To CSS Calendar</button><button id="del_CSS_Evt">Delete Event from CSS Calendar</button><br/>
	</div>
	
	
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
