

var year = new Date().getFullYear();
var month = new Date().getMonth();
var day = new Date().getDate();

var eventData = {
		events : [
		{"id":1, "start": new Date(year, month, day, 12), "end": new Date(year, month, day, 13, 35),"title":"Lunch with Mike"},
		{"id":2, "start": new Date(year, month, day, 14), "end": new Date(year, month, day, 14, 45),"title":"Dev Meeting"}
		]
		};
//JQuery Stuff
jQuery.noConflict();	
jQuery(document).ready(function($) {

	$('#calendar').weekCalendar({
		timeslotsPerHour: 4,
		height: function($calendar){
			return $(window).height() - $("h1").outerHeight();
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
		data: eventData
	});

	function displayMessage(message) {
		$("#message").html(message).fadeIn();
	}

	$("<div id=\"message\" class=\"ui-corner-all\"></div>").prependTo($("body"));
	
});    	


//Prototype.js handlers, 
//defined AFTER the document has loaded

document.observe("dom:loaded", function() {
	  
	
$("createCisCalendarButton").on("click", function(event){
new Ajax.Updater('result','/sharedCal/createCisCalendarAjax.do',
		  {
			parameters: { cisId: $F('cisId'), cisSummary: $F('cisSummary') },
		    method:'get',
		    onSuccess: function(transport){
		      var response = transport.responseText || "no response text";
		      $('result').replace("<p>CIS Calendar created successfully.</p>");
		    },
		    onFailure: function(){ alert('Something went wrong...') }
		  });			
});

$("getCisCalendarEventsButton").on("click", function(event){
	new Ajax.Updater('result','/sharedCal/getCisCalendarEvents.do',
			  {
				parameters: { calendarId: $F('calId')},
			    method:'get',
			    onSuccess: function(transport){
			      var response = transport.responseText || "no response text";
			      var ajResp = eval("(" + response + ")"); 
			      //alert("Success! \n\n" + response);
			      eventData.events = ajResp;
			      jQuery("#calendar").weekCalendar('refresh'); 
			      jQuery("#calendar").show();
			    },
			    onFailure: function(){ alert('Something went wrong...') }
			  });			
	});

$("deleteCisCalendarButton").on("click", function(event){
	new Ajax.Updater('result','/sharedCal/deleteCisCalendar.do',
			  {
				parameters: { calendarId: $F('calId')},
			    method:'get',
			    onSuccess: function(transport){
			      var response = transport.responseText || "no response text";
			      $('result').replace("<p>CIS Calendar deleted successfully.</p>");
			    },
			    onFailure: function(){ alert('Something went wrong...') }
			  });			
	});

$("createCssCalendarButton").on("click", function(event){
	new Ajax.Updater('result','/sharedCal/createCssCalendarAjax.do',
			  {
				parameters: { cssSummary: $F('cisSummary') },
			    method:'get',
			    onSuccess: function(transport){
			      var response = transport.responseText || "no response text";
			      $('result').replace("<p>CSS Calendar created successfully.</p>");
			    },
			    onFailure: function(){ alert('Something went wrong...') }
			  });			
	});

$("deleteCssCalendarButton").on("click", function(event){
	new Ajax.Updater('result','/sharedCal/deletePrivateCalendar.do',
		  {
			method:'get',
		    onSuccess: function(transport){
		      var response = transport.responseText || "no response text";
		      $('result').replace("<p>CSS Calendar deleted successfully.</p>");
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
		      var ajResp = eval("(" + response + ")"); 
		      //alert("Success! \n\n" + response);
		      eventData.events = ajResp;
		      jQuery("#calendar").weekCalendar('refresh'); 
		      jQuery("#calendar").show();
		    },
		    onFailure: function(){ alert('Something went wrong...'); }
		  });			
});

$("getAllCisCalendarsAjax").on("click", function(event){
	new Ajax.Updater('result','/sharedCal/getAllCisCalendarsAjax.do',
		  {
			parameters: { cisId: $F('cisId')},
		    method:'get',
		    onSuccess: function(transport){
		      var response = transport.responseText || "no response text";
		      //alert("Success! \n\n" + response);
		      var ajResp = eval("(" + response + ")");
		      var calList = ajResp.calendarList; 
		      var result = "<table border='1'><tr><th>Calendar ID</th><th>Calendar Summary</th></tr>";
		      for (var calIndex=0; calIndex < calList.length; calIndex++){
		    	  result = result + "<tr><td>"+
		    	  	calList[calIndex].calendarId+"</td><td>"+
		    	  	calList[calIndex].summary+"</td></tr>";
			  }
			  result += "</table>";
			  $('result').replace(result);
		    },
		    onFailure: function(){ alert('Something went wrong...') }
		  }
	);			
});

//First time initializations
jQuery("#calendar").hide();

});