<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<%@page import="si.stecce.societies.crowdtasking.api.RESTful.UsersAPI"%>
<%@page import="si.stecce.societies.crowdtasking.model.CTUser"%>
<%@page import="si.stecce.societies.crowdtasking.model.AuthenticationPrvider"%>
<%@page import="si.stecce.societies.crowdtasking.model.AuthenticatedUser"%>
<%@page import="com.google.appengine.api.users.UserService" %>
<%@page import="com.google.appengine.api.users.UserServiceFactory" %>
<%
String suffix = request.getParameter("continue");
if (suffix == null) {
	suffix = "";
}
String federatedIdentity = request.getParameter("federatedIdentity");
if ("SOCIETIES".equalsIgnoreCase(federatedIdentity)) {
	AuthenticatedUser authenticatedUser = new AuthenticatedUser();
	authenticatedUser.setFederatedIdentity(federatedIdentity);
	authenticatedUser.setProvider(AuthenticationPrvider.getAuthenticationPrvider(federatedIdentity));
	authenticatedUser.setUserId(request.getParameter("userId"));
	authenticatedUser.setNickName(request.getParameter("nickName"));
	authenticatedUser.setEmail(request.getParameter("email"));
	authenticatedUser.setFirstName(request.getParameter("foreName"));
	authenticatedUser.setLastName(request.getParameter("name"));
	
	session.setAttribute("loggedIn", "true");
	session.setAttribute("authenticatedUser", authenticatedUser);

	CTUser user = UsersAPI.getUserByFederatedId(authenticatedUser);
	if (user != null) {
		user.setLastLogin(new Date());
		if (user.getId() != null) {
			session.setAttribute("CTUserId", user.getId());
		}
		UsersAPI.saveUser(user);
	}
	response.sendRedirect(request.getParameter("continue"));
}
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
	<meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no">
	<title>Welcome to Crowd tasking!</title>
	<link rel="stylesheet" href="/css/jquery.mobile-1.3.1.min.css" type="text/css" media="screen"/>
	<script src="/js/jquery-2.0.0.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="/js/jquery.mobile-1.3.1.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="/js/mobile.js" type="text/javascript" charset="utf-8"></script>
</head>
<body>
	<div data-role="page" id="loginPage" data-theme="b">
		<div class="header" data-role="header" data-position="fixed" data-theme="a">
			<h2>Crowd tasking</h2>
		</div>
		<div class="main" data-role="content">
			<a href="" id="societiesButton" data-ajax="false" data-role="button" class="ui-disabled">Sign in with Societies!</a>
			<a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "https://www.google.com/accounts/o8/id", null) %>' data-ajax="false" data-role="button">Sign in with Google</a>
			<a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "yahoo.com", null) %>' data-ajax="false" data-role="button">Sign in with Yahoo</a>
			<a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "myopenid.com", null) %>' data-ajax="false" data-role="button">Sign in with MyOpenId.com</a>
			<a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "myspace.com", null) %>' data-ajax="false" data-role="button">Sign in with MySpace</a>
		</div>
	<form data-ajax="false" action="<%=request.getRequestURL() %>" id="loginForm" method="post">
		<input type="hidden" name="federatedIdentity" id="federatedIdentity" value="SOCIETIES"/>
		<input type="hidden" name="userId" id="userId" value="admin.societies.local"/>
		<input type="hidden" name="name"  id="name"/>
		<input type="hidden" name="foreName"  id="foreName"/>
		<input type="hidden" name="email" id="email"/>
		<input type="hidden" name="continue" value="<%= suffix %>"/>
	</form>
	</div>

<script type='text/javascript'>
	$(document).on('pageinit', '#loginPage', function(event, data){       
		if (typeof(android) !== "undefined") {
			$('#societiesButton').removeClass('ui-disabled');
			$('#societiesButton').bind('tap', function(event, data) {
				var result = window.android.loginData()
				if (result) {
					var user = JSON.parse(result);
					$("#userId").val(user.userId);
					if (user.name != "undefined") {
						$("#name").val(user.name);
					}
					if (user.foreName != "undefined") {
						$("#foreName").val(user.foreName);
					}
					if (user.email != "undefined") {
						$("#email").val(user.email);
					}
					$('#loginForm').submit();
				}
				else {
					toast("Error connecting to Societies Android Client. Are you sure that SOCIETIES Android client app is installed?");
				}
			});
		}
		else {
			console.log("android is undefined");
		}
	});
</script>
</body>
