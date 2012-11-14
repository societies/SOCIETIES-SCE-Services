<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html>
<html lang="en">
<head>
<title>Societies</title>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/dialog.css">
<script>!window.jQuery && document.write('<script src="js/jquery-v1.8.1.js"><\/script>')</script>
<!-- Menu -->
<script src="js/webmenu_nav.js"></script>
<!--[if IE]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]--> 
<!--[if lt IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" />
<script src="http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE7.js"></script><![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" /><![endif]-->
<!--script for PopUp for Login/Register--> 
<script src="js/webpopup.js"></script>


</head>
<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
<!-- Logo -->
<h1 id="logo" class="logo-pos">
<!-- TODO: Hardcoded for now -->
<a href="http://localhost:8080/domain-authority/"><img src="${pageContext.request.contextPath}/images/societies_logo.jpg" alt="Logo" /></a>
</h1>
<!--WebMenu -->
<nav id="webmenu_nav">
<ul id="navigation" class="grid_8">
<li><a href="${pageContext.request.contextPath}/myprofile.html"><br />My Account</a>
<ul class="sub-menu"> 
<li><a href="${pageContext.request.contextPath}/editprofile.html">My Profile</a></li>
<li><a href="${pageContext.request.contextPath}/myprofile.html">Privacy Settings</a></li>
<!-- <li><a href="settings.html">Security Settings</a></li>
<li><a href="privacysettings.html">Privacy Settings</a></li> -->
</ul>
</li>
<!-- 
<li><a href="your_installed_apps.html"><br />Apps</a>
</li>
<li><a href="your_societies_friends_list.html"><br />Friends</a>
<ul class="sub-menu"> 
<li><a href="your_societies_friends_list.html">Your Friends</a></li>
<li><a href="suggested_societies_friends_list.html">Suggested Friends</a></li>
</ul>
</li>
<li><a href="your_communities_list.html"><br />Communities</a>
<ul class="sub-menu"> 
<li><a href="your_communities_list.html">Your Communities</a></li>
<li><a href="manage_communities.html">Manage your Communities</a></li>
<li><a href="create_community.html">Create a Community</a></li>
<li><a href="your_suggested_communities_list.html">Suggested Communities</a></li>
</ul>
</li>
-->
<li><a href="${pageContext.request.contextPath}/main.html" class="current"><br />Home</a></li>
</ul>
</nav><!-- #webmenu -->
<div class="clear"></div>
</header>
<!--Main Content -->
<section id="featured" class="clearfix grid_12">
<div>
<article> 
</article>			
</div>
</section>
<div class="hr grid_12 clearfix boxhr">&nbsp;</div>
<section class="homebox_entries grid_12">
<!-- FIRST ROW -->
<header>
  <h4 class="box_title">NETWORKING ZONE </h4>
  <h4 class="box_title">Location : <strong>${zonelocation}</strong>  Topic : <strong>${topics}</strong></h4>
</header>
<div class="hr grid_12 clearfix">&nbsp;</div>
<article>
<section id="left_col" class="grid_8">
<section>
<div class="keyinfo_content">
<div class="clearfix">
</div>
<br/>


<script language="javascript">
	function submitform(friendid) {    
		document.forms["zoneform"]["friendid"].value = friendid;
		document.forms["zoneform"].submit();
	} 
</script>

<form id="zoneform" name="zoneform" method="post" action="${pageContext.request.contextPath}/zone.html">

<table width="1000" border="1"> 
<tr>
<td width = 25%>
<article>
<p><strong>Suggested Connections in Current Location</strong></p>
<ul>
<xc:forEach var="memberlistentry" items="${suggestlist}">
<li>${memberlistentry.displayName},${memberlistentry.company} <br/> <input type="button" value="Connect" onclick="submitform('${memberlistentry.userid}')" > </li>
</xc:forEach>
</ul>
</article>
</td>
<td width = 25%>
<article>
<p><strong>All Members in Current Location</strong></p>
<ul>
<xc:forEach var="memberlistentry" items="${memberlist}">
<li>${memberlistentry.displayName},${memberlistentry.company} <br/> <input type="button" value="Connect" onclick="submitform('${memberlistentry.userid}')" > </li>
</xc:forEach>
</ul>
</article>
</td>
<td width = 25%>
<article>
<p><strong>Zone Activity Feed</strong></p>
<ul>
<xc:forEach var="zoneactivity" items="${zoneeventlist}">
<li>${zoneactivity.username} : ${zoneactivity.useraction} <br/> <input type="button" value="Connect" onclick="submitform('${zoneactivity.userid}')" > </li>
</xc:forEach>
</ul>
</article>
</td>
</tr>
</table>
<input type="hidden" name="friendid" value="${zoneform.friendid}"/>
</form>
<br/>
</div>
</section>
</section>
</article>

</section>
<!-- Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>		
<div class="hr dotted clearfix">&nbsp;</div>
<div class="hr dotted clearfix">&nbsp;</div>
<div class="sidebar_bottom_BG"></div>
</aside>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>
<!-- Footer -->
<footer class="container_12 clearfix">
<section class="footer">
<p class="footer-links">
<span><a href="termsofuse.html">Terms of Use</a> | <a href="${pageContext.request.contextPath}/disclaimer.html">Disclaimer</a> | <a href="${pageContext.request.contextPath}/privacy.html">Privacy</a> | <a href="${pageContext.request.contextPath}/help.html">Help</a> | <a href="${pageContext.request.contextPath}/about.html">About</a></span>
<a class="float right toplink" href="#">top</a>
</p>
</section>
</footer>
</div>
</body>
</html>