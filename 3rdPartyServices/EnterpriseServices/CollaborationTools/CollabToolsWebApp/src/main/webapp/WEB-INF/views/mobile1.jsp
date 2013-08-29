<!DOCTYPE HTML>
<html>
	<head>
		<script type="text/javascript">
			var userAgent = navigator.userAgent;
			if (userAgent.indexOf("Android") > -1) {
				window.location.href = "android.html";
			} else if (userAgent.indexOf("iPad") > -1) {
				window.location.href = "iphone.html";
			} else if (userAgent.indexOf("iPhone") > -1) {
				window.location.href = "iphone.html";
			} } else {
				window.location.href = "default.html";
			}
		</script>
	</head>
	<body>
		Redirect to properly browser.
	</body>
</html>