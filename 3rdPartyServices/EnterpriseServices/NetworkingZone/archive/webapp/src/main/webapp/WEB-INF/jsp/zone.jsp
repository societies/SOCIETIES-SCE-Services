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
			<li id="li_1" >
				<label class="description" for="element_companyname">Company Name </label>
				<div>
					<input id="element_companyname" name="element_companyname" class="element text medium" type="text" maxlength="255" value="${zoneForm.companyName}"/> 
				</div>
				<p class="guidelines" id="guide_companyname"><small>Company Name as displayed to other users</small></p>
			</li>
			<li id="li_1" >
				<label class="description" for="element_department">Department </label>
				<div>
					<input id="element_department" name="element_department" class="element text medium" type="text" maxlength="255" value="${zoneForm.deptName}"/> 
				</div>
				<p class="guidelines" id="guide_deptname"><small>Department Name as displayed to other users</small></p>
			</li>
			<div>	
					<button type="button" onclick="doAjaxUpdatePersonalDetails()">Save <strong>&raquo;</strong></button>
				</div>
				<div id="publicinfo_error_userdetails" class="error"></div>
				<div id="publicinfo_info_userdetails" class="success"></div>
					
		
		</ul>

		<ul>
			
			<li class="section_break">
				<h3>Employment History</h3>
			</li>
				<li id=li_show_details>
				<table>
				
						<tr>
							<td width=5%>
								<input id="element_employment_show" name="element_employment_radio" class="element radio" type="radio" value="1" onclick="doAjaxSetEmploymentHistoryVisible()"/>
								<label class="choice" for="element_employment_show">Show</label>
							</td>
							<td width=5%>
								<input id="element_employment_hide" name="element_employment_radio" class="element radio" type="radio" value="0" onclick="doAjaxSetEmploymentHistoryVisible()"/>
								<label class="choice" for="element_employment_hide">Hide</label>
							</td>
					
							<td> 
								<p class="guidelines" id="guide_employment"><small>Show or Hide your Employment History </small></p> 
							</td>
						</tr>
				</table>		
				</li>
				<li id=li_employment_company_details>
				<table>
				
						<tr>
							<td width=40%>
								<label class="description" for="element_employment_history_company">Company </label>
							</td>
							<td width=40%>
								<label class="description" for="element_employment_history_department">Department </label>
							</td>
							<td> 
								<p class="guidelines" id="guide_employment_history_company"><small>Details of the company you worked for</small></p> 
							</td>
						</tr>
						<tr>
							<td width=40%>
								<input id="element_employment_history_company" name="element_employment_history_company" class="element text medium" type="text" maxlength="255" value="${zoneForm.emphistCompany}" width=100 />
							</td>
							<td width=40%>
								<input id="element_employment_history_department" name="element_employment_history_department" class="element text medium" type="text" maxlength="255" value="${zoneForm.emphistDept}" width=100 />
							</td>		
							<td> 
								<p class="guidelines" id="guide_employment_history_company"><small>Details of the company you worked for</small></p> 
							</td>
							<td>
							<a href="#" onclick="doAjaxAddEmploymentHistory();"><img src="images/add.png" width =20 height=20></a> 
							
							</td>
						</tr>
					
				</table>
				<div id="publicinfo_error_employdetails" class="error"></div>
				<div id="publicinfo_info_employdetails" class="success"></div>
				
				<div id="employdetailslist"></div>
				<table>
					<ol id="listdata" class="timeline">
					</ol>
				</table>
				</li>	
					
			</ul>		
				
			<ul>
			
			<li class="section_break">
				<h3>Education History</h3>
			</li>
				<li id=li_show_details>
				<table>
				
						<tr>
							<td width=5%>
								<input id="element_education_show" name="element_education_radio" class="element radio" type="radio" value="1" onclick="doAjaxSetEducationHistoryVisible()"/>
								<label class="choice" for="element_education_show">Show</label>
							</td>
							<td width=5%>
								<input id="element_education_hide" name="element_education_radio" class="element radio" type="radio" value="0" onclick="doAjaxSetEducationHistoryVisible()"/>
								<label class="choice" for="element_education_hide">Hide</label>
							</td>
					
							<td> 
								<p class="guidelines" id="guide_education"><small>Show or Hide your Educational History </small></p> 
							</td>
						</tr>
				</table>		
				</li>
				<li id=li_education_company_details>
				<table>
				
						<tr>
							<td width=40%>
								<label class="description" for="element_education_history_college">College </label>
							</td>
							<td width=40%>
								<label class="description" for="element_education_history_course">Course </label>
							</td>
							<td> 
								<p class="guidelines" id="guide_education_history_college"><small>Details of the course you completed</small></p> 
							</td>
						</tr>
						<tr>
							<td width=40%>
								<input id="element_education_history_college" name="element_education_history_college" class="element text medium" type="text" maxlength="255" value="${zoneForm.eduhistCollege}" width=100 />
							</td>
							<td width=40%>
								<input id="element_education_history_course" name="element_education_history_course" class="element text medium" type="text" maxlength="255" value="${zoneForm.eduhistCourse}" width=100 />
							</td>		
							<td> 
								<p class="guidelines" id="guide_education_history_college"><small>Details of the course you completed</small></p> 
							</td>
							<td>
							<a href="#" onclick="doAjaxAddEducationHistory();"><img src="images/add.png" width =20 height=20></a> 
							
							</td>
						</tr>
					
				</table>
				<div id="publicinfo_error_edudetails" class="error"></div>
				<div id="publicinfo_info_edudetails" class="success"></div>
				
				<div id="edudetailslist"></div>
				<table>
					<ol id="edulistdata" class="timeline">
					</ol>
				</table>
				</li>	
					
			</ul>		
				

				<div>	
					<button type="button" onclick="doAjaxUpdatePersonalDetails()">Update Public Information <strong>&raquo;</strong></button>
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