<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Third Party Services - Networking</title>
<style type="text/css">
.error {
	color: #ff0000;
}


html {height:100%;}
body {height:100%; margin:0; padding:0;}
#bg {position:fixed; top:0; left:0; width:100%; height:100%;}
#content {position:relative; z-index:1;}



.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}


body{
        text-align:center;
    }
    
 #container{
        width:150px;
        margin-left:auto;
        margin-right:auto;
        text-align:left;
    }
 
 
</style>
</head>
<body>
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" style="background-image:url(images/NetworkBackground.png);background-repeat:no-repeat;background-size: 100%;">
<tr height="25%" width="100%">
<td> </td>
</tr>
<tr height="75%" width="100%">
<td>		
<!-- .................PLACE YOUR CONTENT HERE ................ -->



<form:form method="POST" action="login.html" commandName="networkingLoginForm" style="text-align:center;">

	<div id="container">
	
	<h3>Networking Service</h3>
	<H4>${message}</h4> 
	
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Networking UserName</td>
			</tr>
			<tr>	
				<td><form:input path="userName" value="" size="50" /></td>
			</tr>	
			<tr>
				<td>Networking Password</td>
			</tr>
			<tr>		
				<td><form:password path="password" value=""  />
				</td>
			</tr>						
			<tr>
				<td><input type="submit" value="Logon to Networking" /></td>
			</tr>
		</table>
	</div>
		
	</form:form>
	
</td>
</tr>	
</table>
<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	
</body>
</html>

