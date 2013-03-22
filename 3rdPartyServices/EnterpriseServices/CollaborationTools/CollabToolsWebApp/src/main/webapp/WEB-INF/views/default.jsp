<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Societies Homepage</title>

 <script type="text/javascript" src="js/jquery.js"></script>

 <script type="text/javascript">
    function doAjax(input) {
      $.ajax({
        url: 'time.html',
        data: ({name : input}),
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
<h1>Welcome to the CollabTools Admin page</h1>
${message}
<br>
<br>
Insert cisID to start CollabTools
<br>

<input type="text" id='cisID' size="50" name='msg'>
<input type="button" id="demo" onclick="doAjax(document.getElementById('cisID').value)" value="Run CollabTools"/>

<div id="time">
</div>


<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>
