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
	<h2>Instant Messaging application client is available for Windows, Linux, Mac and Android </h2>
	<p>Instant Messaging application client is available for desktop and mobile platform. Just download and install the two SOCIETIES Android app.</p>
	
	
	<ul class="downloadBox">
	<div class='vspace'></div>
	<ul>
	<li><img src='https://jitsi.org/wiki/pub/logo-win.png' alt='' title='' /> <a class='urllink' href='https://download.jitsi.org/jitsi/windows/jitsi-1.0-latest-x86.exe' rel='nofollow'> Microsoft Windows</a> (also available as <a class='urllink' href='https://download.jitsi.org/jitsi/msi/jitsi-1.0-latest-x86.msi' rel='nofollow'>msi</a>)</li>
	<li><img src='https://jitsi.org/wiki/pub/logo-mac.png' alt='' title='' />  <a class='urllink' href='https://download.jitsi.org/jitsi/macosx/jitsi-1.0-latest.dmg' rel='nofollow'>Mac OS X</a> </li>
	<li><img src='https://jitsi.org/wiki/pub/logo-ubuntu.png' alt='' title='' /> <a class='urllink' href='https://download.jitsi.org/jitsi/debian/jitsi_1.0-latest_i386.deb' rel='nofollow'>_Ubuntu</a> </li>
	<li><img src='https://jitsi.org/wiki/pub/logo-android.png' alt='' title='' />  <a class='urllink' href='https://play.google.com/store/apps/details?id=com.xabber.android' rel='nofollow'>Android (Google Play)</a> </li>
	</ul>
	</ul>
	
	<div class="article">
		<h2>Login and Configuration Steps</h2>
		<p>
			
			In order to login, launch the application client and fill your account credentials.
			The credentials are the same user and password used to login in the SOCIETIES platform.
			
			<p>For Jitsi client follow:

			<br>1 - Select Tools > Options > Account > Add
			<br>2 - In network option choose XMMP.
			<br>3 - Fill your account credentials.  
			<p><em>* The XMPP Server name is the same required to authenticate in the SOCIETIES platform.</em></p>
		</p>
		<img src="/images/jitsi1.png" alt="Configuration step" />
		<img src="/images/jitsi2.png" alt="Configuration step" />
		<p>
			For Xabber (Android) follow:
			<br />
			<strong>After installing from Google Play</strong>
			<br />
			<br>1 - click in add account.
			<br>2 - In account type choose XMMP.
			<br>3 - Fill your account credentials. 
			<p><em>* The XMPP Server name is the same required to authenticate in SOCIETIES platform.</em></p>
		<img src="/images/xabber1.png" alt="Configuration step" />
		<img src="/images/xabber2.png" alt="Configuration step" />
		
	</div>	
</div>


<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>