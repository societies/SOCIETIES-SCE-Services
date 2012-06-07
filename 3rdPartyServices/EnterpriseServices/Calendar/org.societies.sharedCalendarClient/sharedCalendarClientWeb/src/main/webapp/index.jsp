<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="./js/prototype.js" type="text/javascript"></script>
<title>Shared Calendar Web Client</title>
</head>
<body>
	<script language="JavaScript" src="./js/myCalendar.js"></script>
	<h2>Hello World!</h2>
	<button id="retrieveCalendar">Retrieve All Calendars</button>
	<br/>Result:<br/>
	<div id="result"></div>
	<script type="text/javascript">
    <!--      window.defaultStatus = "Welcome to the large URL page."

	$("retrieveCalendar").on("click", function(event){
			new Ajax.Updater('result','CalendarServlet/',
					  {
					    method:'get',
					    onSuccess: function(transport){
					      var response = transport.responseText || "no response text";
					      alert("Success! \n\n" + response);
					    },
					    onFailure: function(){ alert('Something went wrong...') }
					  });			
		});
   	/*	
      function changeStatus() {
        window.status = "Click me to go to the Unleashed home page."
     }
   
     function changeDefaultStatus() {
        window.defaultStatus = window.document.statusForm.messageList. 
                               options[window.document.statusForm.messageList. 
                               selectedIndex].text
     }*/
    //-->
    </script>
</body>
</html>
