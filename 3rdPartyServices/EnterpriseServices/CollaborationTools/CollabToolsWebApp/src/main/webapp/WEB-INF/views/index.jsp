<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE HTML>
<html>
	<head>
		<script type="text/javascript">
			var userAgent = navigator.userAgent;
			if (userAgent.indexOf("Android") > -1) {
				window.location.href = "android.html";
			} 
			else if (userAgent.indexOf("iPad") > -1) {
				window.location.href = "iphone.html";
			} 
			else if (userAgent.indexOf("iPhone") > -1) {
				window.location.href = "iphone.html";
			} 
			else {
				window.location.href = "default.html";
			}
		</script>
	</head>
	<body>
		Redirect to properly browser.
	</body>
</html>