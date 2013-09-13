<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html>
<html lang="en">
<head>
<title>Networking Zone</title>
<meta charset="utf-8">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dialog.css">
<script>!window.jQuery && document.write('<script src="js/jquery-v1.8.1.js"><\/script>')</script>
<!-- Menu -->

<script type="text/javascript">
	var contexPath = "<%=request.getContextPath() %>";	
</script>
<script src="js/webmenu_nav.js"></script>
<script src="js/profile.js"></script>
</head>
<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
<div class="login-form">   
</div>
<!-- Logo -->
<h1 id="logo" class="logo-pos">
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
<li><a href="m_main.html"><br />Home</a></li>
</ul>
</nav><!-- #webmenu -->
<div class="clear"></div>
</header><!-- #header -->
<div class="hr grid_12 clearfix">&nbsp;</div>
<section id="left_col" class="grid_12">
<section>
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
</section>
<div class="websearchbar">
<div class="websearchtitle">
<h4 class="profile_title">${profileForm.name}</h4>
</div>
<div class="groupsearch">
</div>
</div>
</section>
<section>
<header>
  <h4 class="box_title">Default Privacy Settings </h4> </header>
</section>
<!-- Left Column -->
<section id="left_col" class="grid_8">
<section>
<table width=100%>
<tr>
<td width=25%>
<figure class="gravatar">
<img alt="" src="images/webprofile_pic_sample1.jpg" height="48" width="48" />
<a class="furtherinfo-link" href="m_editprofile.html">EDIT PROFILE</a>
</figure>
</td>
<td width=50%>
<div class="keyinfo_content">
<div class="clearfix">
<cite class="author_name">My Personal Details</cite>
</div>
<br/>
<br/>
<p><strong>Email:</strong> ${profileForm.email}</p>
<p><strong>Live In:</strong> ${profileForm.homelocation}</p>
<br/>
</div>
<input type="hidden" name="personalvisiblecheck" value="${profileForm.personalvisible}"/>
<input type="hidden" name="aboutlvisiblecheck" value="${profileForm.aboutvisible}"/>
<input type="hidden" name="employvisiblecheck" value="${profileForm.employvisible}"/>
</td>
<td>
<div class="option-group radio">
<input id="element_personal_show" name="element_personal_radio" type="radio" value="1" onclick="doAjaxSetPersonalVisible()" <xc:if test="${profileForm.personalvisible == 1}">CHECKED</xc:if> />
<label for="element_personal_show">Show</label>
<input id="element_personal_hide" name="element_personal_radio" type="radio" value="0" onclick="doAjaxSetPersonalVisible()" <xc:if test="${profileForm.personalvisible == 0}">CHECKED</xc:if> />
<label for="element_personal_hide">Hide</label>
</div>
</td>
</tr>
</table>
</section>
<section>
<article class="post">
&nbsp;
</article>

<div class="clearfix">
<table width=100%>
<tr>
<td width=75%>
<p><em>About Me:</em></p> 
<p>${profileForm.about}</p>
</td>
<td>
<div class="option-group radio">
<input id="element_about_show" name="element_about_radio" type="radio" value="1" onclick="doAjaxSetAboutVisible()" <xc:if test="${profileForm.aboutvisible == 1}">CHECKED</xc:if> />
<label for="element_about_show">Show</label>
<input id="element_about_hide" name="element_about_radio" type="radio" value="0" onclick="doAjaxSetAboutVisible()" <xc:if test="${profileForm.aboutvisible == 0}">CHECKED</xc:if> />
<label for="element_about_hide">Hide</label>
</div>
</td>
</tr>
</table>
</div>
</section>
<section>
<article class="post">
&nbsp;
</article>
<table width=100%>
<tr>
<td width=75%>
<div class="clearfix">
<cite class="author_name">My Current Employment</cite>
</div>
<br/>
<p><strong>Employer:</strong> ${profileForm.company}</p>
<p><strong>Position:</strong> ${profileForm.position}</p>
<p><strong>Department:</strong> ${profileForm.department}</p>
<br>
</td>
<td>
<div class="option-group radio">
<input id="element_employment_show" name="element_employment_radio" type="radio" value="1" onclick="doAjaxSetAboutEmployVisible()" <xc:if test="${profileForm.employvisible == 1}">CHECKED</xc:if> />
<label for="element_employment_show">Show</label>
<input id="element_employment_hide" name="element_employment_radio" type="radio" value="0" onclick="doAjaxSetAboutEmployVisible()" <xc:if test="${profileForm.employvisible == 0}">CHECKED</xc:if> />
<label for="element_employment_hide">Hide</label>
<p class="keyinfo_text"><small>Show or Hide your Employment Information </small></p>
</div>
</td>
</tr>
</table>
</section>
<section>				
<article class="post">
&nbsp;
</article>
<p><strong>Education History:</strong></p>
<!-- Unordered -->
<!-- <div id="publicinfo_error_employdetails" class="error"></div>
<div id="publicinfo_info_employdetails" class="success"></div>
			
<div id="employdetailslist"></div>
<table>
	<ol id="listdata" class="timeline">
	</ol>
</table>
-->				
<ul>
<xc:forEach var="eduDet" items="${profileForm.educationHistory}">
<li>${eduDet.what} , ${eduDet.where}, ${eduDet.level}</li>
</xc:forEach>
</ul>
<br>
</section>
<section>
<article class="post">
&nbsp;
</article>
<br>
<p><strong>Employment History:</strong></p>
<!-- Unordered -->
<ul>
<xc:forEach var="empDet" items="${profileForm.employmentHistory}">
<li>${empDet.what} , ${empDet.where}</li>
</xc:forEach>
</ul>
<article class="post">
&nbsp;
</article>
<br>

<div class="hr dotted clearfix">&nbsp;</div>
</section>
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