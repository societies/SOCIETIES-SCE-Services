<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>CollabTools</title>

<link href="css/context.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="css/ctx-table-style.css" rel="stylesheet"
	type="text/css" media="screen" />
	
 <script type="text/javascript" src="js/jquery.js"></script>

 <script type="text/javascript">
    function doAjax(input, input2) {
      $.ajax({
        url: 'checkcis.html',
        data: ({name : input, check : input2}),
        success: function(data) {
          $('#checkcis').html(data);
        }
      });
    }
    
    function setText(target) {
    	var txt = document.getElementById(target);
    	var temp = txt.value;
    	var tf = document.getElementById("cisID");
    	tf.value = temp;
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
<h1>Welcome to CollabTools Admin</h1>
${message}
<br>
Choose a CIS to start CollabTools
<br>

	<input type="text" id='cisID' size="55" name='msg'>
	<input type="button" id="start" onclick="doAjax(document.getElementById('cisID').value, 'start')" value="Run CollabTools"/>
	<input type="button" id="stop" onclick="doAjax(document.getElementById('cisID').value, 'stop')" value="Stop CollabTools"/>

<br>
	<div id="checkcis">
	
	</div>



<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>
