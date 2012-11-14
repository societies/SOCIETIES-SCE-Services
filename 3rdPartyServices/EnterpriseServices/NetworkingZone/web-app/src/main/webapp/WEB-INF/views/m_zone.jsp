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
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/collap.css">
<script src="${pageContext.request.contextPath}/js/jquery-v1.8.1.js"></script>
<!-- Menu -->
<script src="${pageContext.request.contextPath}/js/webmenu_nav.js"></script>
<script src="${pageContext.request.contextPath}/js/webpopup.js"></script>

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
<li><a href="${pageContext.request.contextPath}/m_myprofile.html"><br />My Account</a>
<ul class="sub-menu"> 
<li><a href="${pageContext.request.contextPath}/m_editprofile.html">My Profile</a></li>
<li><a href="${pageContext.request.contextPath}/m_myprofile.html">Privacy Settings</a></li>

</ul>
</li>
<li><a href="${pageContext.request.contextPath}/m_main.html" class="current"><br />Home</a></li>
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
  <h7 class="box_title">NETWORKING ZONE </h7><br/>
  <h7 class="box_title">Location : <strong>${zonelocation}</strong>  
  <br/>
  Topic : <strong>${topics}</strong></h7>
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

<form id="zoneform" name="zoneform" method="post" action="${pageContext.request.contextPath}/m_zone.html">

<table> 
<tr>
<td> 
<article>
<p><strong>Suggested Connections in Current Location</strong></p>
<ul>
<xc:forEach var="memberlistentry" items="${suggestlist}">
<li>
<a href="#" onclick="submitform('${memberlistentry.userid}')">
${memberlistentry.displayName},${memberlistentry.company}
</a> </li>
</xc:forEach>
</ul>
</article>
</td>
</tr>
<tr>
<td> 
<article>
<p><strong>All Members in Current Location</strong></p>
<ul>
<xc:forEach var="memberlistentry" items="${memberlist}">
<li>
<a href="#" onclick="submitform('${memberlistentry.userid}')">
${memberlistentry.displayName},${memberlistentry.company}
</a> </li>
</xc:forEach>
</ul>
</article>
</td>
</tr>
<tr>
<td> 
<article>
<p><strong>Zone Activity Feed</strong></p>
<ul>
<xc:forEach var="zoneactivity" items="${zoneeventlist}">
<li>${zoneactivity.username} : ${zoneactivity.useraction} <br/></li>
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