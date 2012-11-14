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

<script type="text/javascript">
	var contexPath = "<%=request.getContextPath() %>";	
</script>
<script src="js/profile.js"></script>
</head>


<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
<div class="login-form"> </div>
<!-- Logo -->
<h1 id="logo" class="logo-pos">
<a href="index.html"><img src="images/societies_logo.jpg" alt="Logo" /></a>
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
</nav><!-- #end webmenu -->
<div class="clear"></div>
</header><!-- #header -->
<div class="hr grid_12 clearfix">&nbsp;</div>
<!-- Left Column -->
<section id="left_col" class="grid_12">
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
<!-- Form -->
<section id="form_style_main">
<form:form method="POST" action="m_editprofile.html" name="profileform" id="" commandName="profileForm">	
<h4 class="form_title">Edit Profile </h4>
<div class="hr dotted clearfix">&nbsp;</div>
<ul>						
<li class="clearfix">
<label for="">Name</label>
<form:input type="text" name="" id="smalltext"  path="name" readonly="true"/>
<div class="clear"></div>
<p class="error">Please, insert ...</p>
</li> 
<li class="clearfix">
<label for="">Email</label>
<form:input type="text" name="" id="smalltext"  path="email" readonly="true"/>
<div class="clear"></div>
<p class="error">Please, insert ...</p>
</li> 
<li class="clearfix">
<label for="">Lives In</label>
<form:input type="text" name="" id="smalltext"  path="homelocation" readonly="true"/>
<div class="clear"></div>
<p class="error">Please, insert ...</p>
</li> 
<li class="clearfix">
<label for="">Company</label>
<form:input type="text" name="" id="smalltext" path="company" />
<div class="clear"></div>
<p class="error">Please, insert...</p>
</li> 
<li class="clearfix">
<label for="">Department</label>
<form:input type="text" name="" id="smalltext" path="department" />
<div class="clear"></div>
<p class="error">Please, insert ...</p>
</li> 
<li class="clearfix"> 
<label for="">Position</label>
<form:input type="text" name="" id="smalltext" path="position"  />
<div class="clear"></div>
<p  class="error">Please, enter ...</p>
</li> 
<li class="clearfix"> 
<label for="">Write A Little Bit About Yourself</label>
<form:textarea name="" id="largetextarea" rows="30" cols="30" path="about"></form:textarea>
<div class="clear"></div>
<p class="error">Please, enter ...</p>
</li>

<li class="clearfix">
<p class="success">Thank you! Success Message here.</p>
<p class="error">Sorry, an error has occured. Please try again later.</p>	
<div id="button">
<input type="submit" id="send_message" class="sendButton" value="Save" />
</div>				
</li> 
</ul> 
</form:form>
</section>
</section>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>
<!-- #Main Content -->	
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