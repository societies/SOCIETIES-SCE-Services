<%@tag description="Main template Tag" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
	<title>Welcome to Crowd tasking!</title>
	<link rel="stylesheet" href="/css/main.css" />
	<link rel="stylesheet" href="/css/comments.css" />
	<link rel="stylesheet" href="/css/smoothness/jquery-ui-1.8.22.custom.css" type="text/css" media="screen" charset="utf-8"/>
	<script src="/js/jquery-1.7.2.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="/js/jquery-ui-1.8.22.custom.min.js" type="text/javascript" charset="utf-8"></script>
</head>
<body>
	<div class="wrapper">
		<div class="header">
			<h1><a href="/">Crowd tasking</a></h1>
			<h3>collaboration environment for students</h3>
		</div>
		<div class="main">
			<jsp:doBody/>
		</div>
		<!--div class="footer">
			Footer, Copyright &copy; bla bla bla
		</div-->
	</div>
</body>
