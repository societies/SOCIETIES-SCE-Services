<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="forms" uri="http://www.springframework.org/tags/form"%>

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

	
	<img id="top" src="images/top.png" alt=""/>
	<forms:form method="POST" action="newaccount.html" commandName="newAccountForm">
	<div id="form_container">
	
		<h1><a>Create your new Schmoozer account</a></h1>
		
	
		<form id="form_0" class="appnitro"  method="post" action="" >
		<div class="form_description">
			<h2>Login Details</h2>
		</div>						
			<ul >
			
					<li id="li_1" >
		<label class="description" for="element_1">Societies Userid</label>
		<div>
			<forms:input id="name" name="name" class="element text medium" type="text" maxlength="255" path="userid" value="maria.societies.local"/> 
		</div><p class="guidelines" id="guide_1"><small>Your societies idenity</small></p> 
		</li>		
		<li id="li_1" >
		<label class="description" for="element_displayname">Scmoozer Display Name</label>
		<div>
			<forms:input id="displayname" name="displayname" class="element text medium" type="text" maxlength="255" path="displayName" value="Maria Mannion"/> 
		</div><p class="guidelines" id="guide_1"><small>The name to be displayed to other users </small></p> 
		</li>		
		<li id="li_2" >
		<label class="description" for="element_2">Password</label>
		<div>
			<forms:input id="password_1" name="password_1" class="element text medium" type="password" path="password" maxlength="255" value=""/> 
		</div><p class="guidelines" id="guide_2"><small>Your Schmoozer Password</small></p> 
		</li>		
		<li id="li_2" >
		<label class="description" for="element_2">Confirm Password</label>
		<div>
			<input id="password_2" name="password_2" class="element text medium" type="password" maxlength="255" value=""/> 
		</div><p class="guidelines" id="guide_2"><small>Your Schmoozer Password</small></p> 
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