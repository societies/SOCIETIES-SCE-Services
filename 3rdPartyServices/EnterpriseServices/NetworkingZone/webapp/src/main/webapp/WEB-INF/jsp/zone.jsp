<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="forms" uri="http://www.springframework.org/tags/form"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Schmoozer</title>
<link href="css/common.css" rel="stylesheet" type="text/css" media="all" />
	<link href="css/login.css" rel="stylesheet" type="text/css" media="all" />
	<link href="css/event.css" rel="stylesheet" type="text/css" media="all" />

	
<script src="<%=request.getContextPath() %>/js/jquery.js"></script>
<script type="text/javascript">
	var contexPath = "<%=request.getContextPath() %>";
</script>
<script src="<%=request.getContextPath() %>/js/user.js"></script>	
</head>

<body id="main_body" >
<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="left.jsp" />
	<!-- END LEFTBAR -->
	
	
	<img id="top" src="images/top.png" alt=""/>
	<forms:form method="POST" action="zone.html" commandName="zoneForm">
	<div id="form_container">
	
		<h1><a>User Details</a></h1>
		<form id="form_430771" class="appnitro"  method="post" action="">
		<div class="form_description">
			<h2>User Details</h2>
			<p>Enter or update your user details</p>
		</div>						
		<ul >
			<li id="li_1" >
				<label class="description" for="element_displayname">Display Name </label>
				<div>
					<input id="element_displayname" name="element_displayname" class="element text medium" type="text" maxlength="255" value="${zoneForm.displayName}"/> 
				</div>
				<p class="guidelines" id="guide_displayname"><small>Name as displayed to other users</small></p>
			</li>
			
		
			<li class="section_break">
				<h3>Public Details</h3>
				<p>Hide or Show Your Schmoozer Details To other users</p>
			</li>
		</ul>
			<div id="company_details">
			<ul>
				<li id=li_show_details>
				<table>
				
						<tr>
							<td width=5%>
								<input id="element_company_show" name="element_company_radio" class="element radio" type="radio" value="1" />
								<label class="choice" for="element_company_show">Show</label>
							</td>
							<td width=5%>
								<input id="element_company_hide" name="element_company_radio" class="element radio" type="radio" value="0" />
								<label class="choice" for="element_company_hide">Hide</label>
							</td>
					
							<td> 
								<p class="guidelines" id="guide_company"><small>Select what to share</small></p> 
							</td>
						</tr>
				</table>		
				</li>
				<li id=li_company_details>
				<table>
				
						<tr>
							<td width=50%>
								<label class="description" for="element_company">Company </label>
								<input id="element_company" name="element_company" class="element text medium" type="text" maxlength="255" value="${companyname}"/>
							</td>
							<td> 
								<p class="guidelines" id="guide_company"><small>The company you work for</small></p> 
							</td>
						</tr>
				</table>		
				</li>
				<li id=li_company_department_details>
				<table>		
						<tr>
							<td width=50%>
								<label class="description" for="element_department">Department </label>
								<input id="element_department" name="element_department" class="element text medium" type="text" maxlength="255" value="${deptname}"/>
							</td>
							<td>  
								<p class="guidelines" id="guide_department"><small>The department you work in</small></p> 
							</td>
						</tr>
				</table>		
				</li>	
					
			</ul>		
				</div>
				
			<ul>	
				<li class="section_break">
				</li>
				<li id=li_show_interests>
				<table>
				
						<tr>
							<td width=5%>
								<input id="element_interest_show" name="element_interest_radio" class="element radio" type="radio" value="1" />
								<label class="choice" for="element_interest_show">Show</label>
							</td>
							<td width=5%>
								<input id="element_interest_hide" name="element_interest_radio" class="element radio" type="radio" value="0" />
								<label class="choice" for="element_interest_hide">Hide</label>
							</td>
					
							<td> 
								<p class="guidelines" id="guide_interest"><small>Select what to share</small></p> 
							</td>
						</tr>
				</table>		
				</li>
				<li id="li_6" >
					<label class="description" for="element_interests">Interests </label>
					<span>
						<input id="element_interests" name="element_interests" class="element checkbox" type="checkbox" value="1" />
						<label class="choice" for="element_interest_1">Interesting Topic One</label>
						<input id="element_interest_2" name="element_interest_2" class="element checkbox" type="checkbox" value="1" />
						<label class="choice" for="element_interest_2">Interesting Topic One</label>
						<input id="element_interest_3" name="element_interest_3" class="element checkbox" type="checkbox" value="1" />
						<label class="choice" for="element_interest_3">Interesting Topic Three</label>
					</span>
					<p class="guidelines" id="guide_6"><small>Here is the hint</small></p> 
				</li>
			
				<div>	
					<button type="button" onclick="doAjaxUpdateInfo()">Update Public Information <strong>&raquo;</strong></button>
				</div>
				<div id="publicinfo_error" class="error"></div>
				<div id="publicinfo_info" class="success"></div>
			</ul>
		</form>	
		<div id="footer">
			
		</div>
	</div>
	</forms:form>
	<div id="error" class="error"></div>
	<div id="info" class="success"></div>
	<img id="bottom" src="images/bottom.png" alt="">
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
	</body>
</html>