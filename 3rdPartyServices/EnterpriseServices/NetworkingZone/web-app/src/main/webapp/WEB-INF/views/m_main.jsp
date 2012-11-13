<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html>
<html lang="en">
<head>
<title>Societies</title>
<meta charset="utf-8">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dialog.css">
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
<a href="http://localhost:8080/domain-authority/"><img src="images/societies_logo.jpg" alt="Logo" /></a>
</h1>
<!--WebMenu -->
<nav id="webmenu_nav">
<ul id="navigation" class="grid_8">
<li><a href="m_myprofile.html"><br />My Account</a>
<ul class="sub-menu"> 
<li><a href="m_editprofile.html">My Profile</a></li>
<li><a href="m_myprofile.html">Privacy Settings</a></li>
</ul>
</li>
<li><a href="m_main.html" class="current"><br />Home</a></li>
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
  <h4 class="box_title">NETWORKING ZONES </h4></header>
<div class="hr grid_12 clearfix">&nbsp;</div>
<table style="float:center" border="1">
<xc:forEach var="zoneentry" items="${zonedets}" varStatus="status" begin="0" step="1">
<tr>
<td></td>
<td>
<article>
<a class="homeboxgrid box " href="${status.count}/m_gotozone.html" title="Click to Enter ${zoneentry.zonelocationdisplay}">
<strong>Location : ${zoneentry.zonelocationdisplay} - Members : ${zoneentry.zonemembercount}</strong>
<img src="images/zone.png" width="270" height="150" alt="${zoneentry.zonelocationdisplay}">
<strong>Topic : ${zoneentry.zonetopics} </strong>
</a>
</article>
</td>
</tr>
</xc:forEach>
</table>
</section>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>
<!-- Footer -->
<footer class="container_12 clearfix">
<section class="footer">
<p class="footer-links">
<span><a href="termsofuse.html">Terms of Use</a> | <a href="disclaimer.html">Disclaimer</a> | <a href="privacy.html">Privacy</a> | <a href="help.html">Help</a> | <a href="about.html">About</a></span>
<a class="float right toplink" href="#">top</a>
</p>
</section>
</footer>
</div>
</body>
</html>