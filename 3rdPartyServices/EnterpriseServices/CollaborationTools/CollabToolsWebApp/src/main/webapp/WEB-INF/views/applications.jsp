<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>CollabTools - Applications</title>
  
<link href="css/context.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="css/ctx-table-style.css" rel="stylesheet"
	type="text/css" media="screen" />

 <script type="text/javascript" src="js/jquery.js"></script>

 <script type="text/javascript">
	 function doAjax(input1, input2) {
	     $.ajax({
	       url: 'setcollabapps.html',
	       data: ({app : input1, server : input2}),
	       success: function(data) {
	         $('#setcollabapps').html(data);
	       }
	     });
	   }
 
	    function setText(target) {
	    	var txt = document.getElementById(target);
	    	var temp = txt.value;
	    	var tf = document.getElementById('server');
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

<h1>Collaborative Applications Available</h1>

	<form name="checkboxform"  method="get">
					<xc:forEach var="serverName" items="${appserver}" varStatus="loop">
						<input type="checkbox" id="${serverName}" name="appnames" onClick="setText('${serverName}')" value="${serverName}">${appnames[loop.index]} ${serverName}<br>
					</xc:forEach>
	</form>

<br>
<br>
Change application server URL:
<br>

		<input type="text" id="server" size="55" name="msg">
		<input type="button" id="submitServer" onclick="doAjax('${appnames[0]}', document.getElementById('server').value)" value=" Submit "/>
	
<div id="setcollabapps">

</div>
<br>
<!-- 	<div id="application"> -->

<!-- 		<div class="query">	 -->
			
<!-- 			<select id="lookup" > -->
<!-- 				<option value="NONE">--- Select Application---</option> -->
<!-- 				<option value="chat">Chat</option> -->
<!-- 				<option value="voip">VoIP (Asterisk PBX)</option> -->
<!-- 			</select>  -->
			
<!-- 			<input type="button" value=" Include " id="executeQuery"/> -->
		
<!-- 		</div> -->


<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>
