<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CollabTools IM Download</title>
<link rel="stylesheet" href="css/style.css" type="text/css"/>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->


<!-- .................PLACE YOUR CONTENT HERE ................ -->

<div id="content" class="download">
	<h2>Download IM Clients for Windows, Linux, Mac and Android </h2>
	<p>Instant Messaging application is available for Android smartphone. Just download and install the two SOCIETIES Android app.</p>
	<p><em>You need to allow your Android phone to install application outside of Google Play. Parameters > Security, and then check "Allow Unknown Sources".</em></p>
	
	
	<ul class="downloadBox">
	<div class='vspace'></div>
	<ul>
	<li><img src='https://jitsi.org/wiki/pub/logo-win.png' alt='' title='' /> <a class='urllink' href='https://download.jitsi.org/jitsi/windows/jitsi-1.0-latest-x86.exe' rel='nofollow'> Microsoft Windows</a> (also available as <a class='urllink' href='https://download.jitsi.org/jitsi/msi/jitsi-1.0-latest-x86.msi' rel='nofollow'>msi</a>)</li>
	<li><img src='https://jitsi.org/wiki/pub/logo-mac.png' alt='' title='' />  <a class='urllink' href='https://download.jitsi.org/jitsi/macosx/jitsi-1.0-latest.dmg' rel='nofollow'>MAC OS X</a> </li>
	<li><img src='https://jitsi.org/wiki/pub/logo-ubuntu.png' alt='' title='' /> <a class='urllink' href='https://download.jitsi.org/jitsi/debian/jitsi_1.0-latest_i386.deb' rel='nofollow'>_Ubuntu</a> </li>
	<li><img src='https://jitsi.org/wiki/pub/logo-android.png' alt='' title='' />  <a class='urllink' href='https://play.google.com/store/apps/details?id=com.xabber.android&hl=en' rel='nofollow'>Android</a> </li>
	</ul>
	</ul>
	
	<div class="article">
		<h2>Login and Configuration Steps</h2>
		<p>
			As soon as the SOCIETIES administrator has notified you that your SOCIETIES account is ready, you can login to your SOCIETIES Android client!<br />
			But before any login, your Android App shall be associated to your SOCIETIES account properly. However, the SOCIETIES administrator may have provide you some configuration parameters.
			In order to create this association, launch your App and, using the Android menu button, select Preferences then CSS Configuration.
			<br />
			<strong>Menu > Preference > CSS Configuration</strong>
			<br />
			Now fills your account credentials, and eventually the parameters provided by the SOCIETIES administrator. <small>The XMPP Server IP address is only required if this SOCIETIES platform is not accessible through the Internet.</small>
		</p>
		<img src="/societies-platform/images/societies-android-app_configuration.png" alt="Configuration step" />
		<p>
		You are now ready to login. To perform the login, open your App, check the box "update with app preferences", verify your credentials and then click on the LOGIN button.
		</p>
		<img src="/societies-platform/images/societies-android-app_login.png" alt="Login step" />
		
		<p><strong>That's it, your logged in, and ready to use SOCIETIES! Enjoy!</strong></p>
	</div>	
</div>


<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>