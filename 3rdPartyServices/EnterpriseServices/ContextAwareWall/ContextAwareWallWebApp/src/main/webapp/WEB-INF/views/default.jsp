<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Societies Homepage</title>

 <script type="text/javascript" src="js/jquery.js"></script>

 <script type="text/javascript">
    function doAjax() {
      $.ajax({
        url: 'time.html',
        data: ({name : "me"}),
        success: function(data) {
          $('#time').html(data);
        }
      });
    }
  </script>


</head>
<body >
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT HERE ................ -->
Welcome to the societies admin page
Guy Guy Guy
<h1>Guy ${message}</h1>


<button id="demo" onclick="doAjax()" title="Button">Get the time!</button>
<div id="time">
</div>


<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>
