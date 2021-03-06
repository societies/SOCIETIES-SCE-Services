<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@page import="si.setcce.societies.crowdtasking.api.RESTful.impl.UsersAPI" %>
<%@page import="si.setcce.societies.crowdtasking.model.AuthenticatedUser" %>
<%@page import="si.setcce.societies.crowdtasking.model.AuthenticationPrvider" %>
<%@page import="si.setcce.societies.crowdtasking.model.CTUser" %>
<%@page import="java.util.Date" %>
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
        authenticatedUser.setScope(request.getParameter("scope"));

        session.setAttribute("loggedIn", "true");
        session.setAttribute("authenticatedUser", authenticatedUser);

        System.out.println("user logged in & authenticated");
        CTUser user = UsersAPI.getUserByFederatedId(authenticatedUser);
        if (user != null) {
            user.setLastLogin(new Date());
            if (user.getId() != null) {
                session.setAttribute("CTUserId", user.getId());
            }
            UsersAPI.saveUser(user);
            System.out.println("user got by federated ID");
        }
        response.sendRedirect(request.getParameter("continue"));
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no">
    <title>Welcome to Crowd tasking!</title>
    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.css" />
    <script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.js"></script>
    <script src="/js/mobile.js" type="text/javascript" charset="utf-8"></script>
</head>
<body>
<div data-role="page" id="loginPage" data-theme="b">
    <div class="header" data-role="header" data-position="fixed" data-theme="a">
        <h2>Crowd tasking</h2>
    </div>
    <div class="main" data-role="content">
        <%--<a href="" id="societiesButton" data-ajax="false" data-role="button" class="ui-disabled">Sign in with Societies!</a>--%>
        <a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "https://www.google.com/accounts/o8/id", null) %>'
           data-ajax="false" data-role="button">Sign in with Google</a>
        <a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "yahoo.com", null) %>'
           data-ajax="false" data-role="button">Sign in with Yahoo</a>
        <a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "myopenid.com", null) %>'
           data-ajax="false" data-role="button">Sign in with MyOpenId.com</a>
        <a href='<%=UserServiceFactory.getUserService().createLoginURL(suffix, null, "myspace.com", null) %>'
           data-ajax="false" data-role="button">Sign in with MySpace</a>
    </div>
    <form data-ajax="false" action="<%=request.getRequestURL() %>" id="loginForm" method="post">
        <input type="hidden" name="federatedIdentity" id="federatedIdentity" value="SOCIETIES"/>
        <input type="hidden" name="userId" id="userId" value="admin.societies.local"/>
        <input type="hidden" name="name" id="name"/>
        <input type="hidden" name="foreName" id="foreName"/>
        <input type="hidden" name="email" id="email"/>
        <input type="hidden" name="scope" id="scope"/>
        <input type="hidden" name="continue" value="<%= suffix %>"/>
    </form>
</div>

<script type='text/javascript'>
    function loginSocietiesUser() {
        var result = window.android.getSocietiesUser();
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
            if (user.scope != "undefined") {
                $("#email").val(user.scope);
            }
            $('#loginForm').submit();
        }
        else {
            toast("Error connecting to Societies Android Client. Are you sure that SOCIETIES Android client app is installed?");
        }
    }

    $(document).on('pageinit', '#loginPage', function (event, data) {
        if (typeof(android) !== "undefined") {
            if (window.android.isSocietiesUser()) {
                loginSocietiesUser();
            }
            /*
             $('#societiesButton').removeClass('ui-disabled');
             $('#societiesButton').bind('tap', function(event, data) {
             loginSocietiesUser();
             });
             */
        }
        else {
            console.log("android is undefined");
        }
    });
</script>
</body>
