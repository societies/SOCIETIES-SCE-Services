<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CollabTools - Notifications</title>

<link href="css/context.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="css/ctx-table-style.css" rel="stylesheet"
	type="text/css" media="screen" />

<script type="text/javascript">
    function getNotification() {
        $.ajax({
            url : 'getnotifications.html',
            success : function(data) {
                $('#result').html(data);
            }
        });
    }
</script>

<script type="text/javascript">
//     var intervalId = 0;
//     intervalId = window.setInterval(getNotification, 1000);
		setTimeout(function(){location.reload();}, 1000);
</script>

</head>
<body>

	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT HERE ................ -->
			
		<!-- Table markup-->
		<div class="query">
		<h1>Notifications</h1>
		<label>Current Sessions: </label>
		</div>
		<table id="newspaper-a">

			<!-- Table header -->

			<thead>
				<tr>
					<th scope="col" id="session-name">Session Name</th>
				    <th scope="col" id="members">Members</th>
					<th scope="col" id="language">Language</th>
					<th scope="col" id="floor-control">Floor Control</th>
				</tr>
			</thead>

			<!-- Table footer -->
		
			<tfoot>
				
			</tfoot>

			<!-- Table body -->
			<tbody>
				<xc:forEach var="element" items="${results}">
					<tr id="${element[0]}">
						<td  name="SessionName">${element[0]}</td>
					    <td  name="Members">${element[1]}</td>
						<td  name="Language">${element[2]}</td>
						<td  name="FloorControl">invite, kick</td>
					</tr>
				</xc:forEach>
			</tbody>

		</table>
		
		<div id="log"></div>
		
        ${app_name}
        ${resultsAsync}
        
        <xc:forEach var="element" items="${resultsAsync}">
        			<tr id="${element}">
						<td  name="SessionName">${element}</td>
					</tr>
        </xc:forEach>      


    <div align="center">
        <div id="result"></div>
        <br>
    </div>




<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>