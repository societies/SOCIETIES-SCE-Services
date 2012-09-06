<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="forms" uri="http://www.springframework.org/tags/form"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Schmoozer</title>
	<link href="css/common.css" rel="stylesheet" type="text/css" media="all" />
	<link href="css/login.css" rel="stylesheet" type="text/css" media="all" />
	
</head>

<body id="body" >
<!-- HEADER -->
<jsp:include page="header.jsp" />
	<!-- END HEADER -->
	<jsp:include page="left_empty.jsp" />

<script language="javascript">
function updateDetails(selected) {    
	document.forms["frontForm"]["selectedCis"].value = selected;
} 
</script>

	
	<img id="top" src="images/top.png" alt=""/>
	
	<forms:form method="POST" action="front.html" commandName="frontForm">
	<input type="hidden" name="selectedCis" id="selectedCis">
	<div id="form_container">
	
		<h1><a></a></h1>
		
	
		<form id="form_0" class="appnitro"  method="post" action="" >
		<div class="form_description">
			<h2>Zone Details</h2>
		</div>						
			<ul >
			
					<li id="li_1" >
		<table>
		 <xc:forEach var="zones" items="${frontForm.zoneList}">
        		<tr>
					<td>
						<input id="element_zone" name="element_zone" class="element radio" type="radio" value="${zones}" onclick="updateDetails('${zones}')" />
						<label class="choice" for="element_zone">${zones}</label>
					</td>
				</tr>			
        </xc:forEach>
        </table>
		</li> 
		
			
		<li class="section_break">	</li>
		
			
		<li class="buttons">
		<input type="hidden" name="form_id" value="0" />
				<input id="saveForm" class="button_text" type="submit" name="submit" value="Submit" />



				
		</li>
			</ul>
		</form>	


		<div id="footer">
			
		</div>
		
	</div>
	</forms:form>
	<img id="bottom" src="images/bottom.png" alt="">
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
	</body>
</html>