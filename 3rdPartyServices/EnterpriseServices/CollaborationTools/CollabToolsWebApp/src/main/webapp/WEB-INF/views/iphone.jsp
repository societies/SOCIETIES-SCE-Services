<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<!DOCTYPE html>
<html>
	<head>
	 	<script type="text/javascript" src="js/jquery.js"></script>
	 	
	 	 <script type="text/javascript">
		     function doAjax(input) {
		         $.ajax({
		           url: 'checkcis.html',
		           data: ({name : input}),
		           success: function(data) {
		             $('#checkcis').html(data);
		           }
		         });
		       }
		  </script>
	
		<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no"/>
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<title>CollabTools Mobile</title>
		<link href="//ajax.googleapis.com/ajax/libs/dojo/1.8.3/dojox/mobile/themes/iphone/iphone.css" rel="stylesheet"></link>
	</head>
<body style="visibility:hidden;">
	<div id="group1" data-dojo-type="dojox.mobile.View" data-dojo-props='keepScrollPos:false'><!-- keepScrollPos=false is to improve performance -->
		<ul data-dojo-type="dojox.mobile.TabBar" data-dojo-props='barType:"segmentedControl", fixed:"top", syncWithViews:true'>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#communities", selected:true'>Communities</li>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#rules"'>Rules</li>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#applications"'>Applications</li>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#notification"'>Sessions</li>
		</ul>
			
		<div id="communities" data-dojo-type="dojox.mobile.View" data-dojo-props="selected: true">
			<h1 data-dojo-type="dojox.mobile.Heading">Communities</h1>
			<h2 data-dojo-type="dojox.mobile.RoundRectCategory">There are ${size} CIS </h2>
			<ul id="list1" data-dojo-type="dojox.mobile.RoundRectList">
					<xc:forEach var="name" items="${cisname}">
						<li data-dojo-type="dojox.mobile.ListItem">
							${name}
						</li>
					</xc:forEach>
			</ul>
			<button data-dojo-type="dojox.mobile.Button" data-dojo-props='label:"Start CollabTools", onClick:function(e){ alert("button clicked");return true; }'></button>


		</div>
		
		<div id="rules" data-dojo-type="dojox.mobile.View">
			<h1 data-dojo-type="dojox.mobile.Heading" data-dojo-props="back:'Home', moveTo:'home'">Rules</h1>
			<h2 data-dojo-type="dojox.mobile.RoundRectCategory">Rules</h2>
			<ul data-dojo-type="dojox.mobile.RoundRectList">
				<li id="item1" data-dojo-type="dojox.mobile.ListItem" data-dojo-props="icon:'', rightText:'Cis List', moveTo: 'cislist'">
					Rule 1
				</li>
				<li data-dojo-type="dojox.mobile.ListItem" data-dojo-props="icon:''">
					Rule 2
					<div class="mblItemSwitch" data-dojo-type="dojox.mobile.Switch"></div>
				</li>
				<li data-dojo-type="dojox.mobile.ListItem" data-dojo-props="icon:'', moveTo: 'applications'">
					Rule 3
				</li>
			</ul>
			<button data-dojo-type="dojox.mobile.Button" onClick="showSelectedValue()" data-dojo-props='label:"Value"'></button>
			<div id="msg"></div>
		</div>
		
		
		

		<div id="applications" data-dojo-type="dojox.mobile.View">
			<h1 data-dojo-type="dojox.mobile.Heading" data-dojo-props="back:'Home', moveTo:'home'">Applications</h1>
			<h2 data-dojo-type="dojox.mobile.RoundRectCategory">Applications available</h2>
			<ul data-dojo-type="dojox.mobile.RoundRectList">
				<li data-dojo-type="dojox.mobile.ListItem" data-dojo-props="moveTo:'cislist'">
					App 1
				</li>
				<li data-dojo-type="dojox.mobile.ListItem" data-dojo-props="rightText: '2h 40m', moveTo: 'cislist'">
					App 2
				</li>
			</ul>
		</div>
		
		<div id="notification" data-dojo-type="dojox.mobile.ScrollableView">
			<h1 data-dojo-type="dojox.mobile.Heading" data-dojo-props="back:'Home', moveTo:'home'">CIS List</h1>
				<h2 data-dojo-type="dojox.mobile.RoundRectCategory">CIS List</h2>
				<ul data-dojo-type="dojox.mobile.RoundRectList" data-dojo-props='select:"single"'>
					<li id="cisItem1" data-dojo-type="dojox.mobile.ListItem" data-dojo-props='checked:true'>
						CIS 1
					</li>
					<li data-dojo-type="dojox.mobile.ListItem">
						CIS 2
					</li>
					<li data-dojo-type="dojox.mobile.ListItem">
						CIS 3
					</li>
				</ul>
				<button data-dojo-type="dojox.mobile.Button" data-dojo-props='label:"Click me!", onClick:function(e){ alert("button clicked");return true; }'></button>
			</div>

		<!-- configure and load dojo -->
		<script src="//ajax.googleapis.com/ajax/libs/dojo/1.8.3/dojo/dojo.js" data-dojo-config="isDebug:1, async:1"></script>
		<script>

			require(["dojo/_base/connect", "dojox/mobile/parser", "dojox/mobile", "dojox/mobile/deviceTheme", "dojox/mobile/compat", "dojo/domReady!", "dojox/mobile/TabBar", "dojox/mobile/ScrollableView", "dojo/ready",
						"dijit/registry", "dojox/mobile/ListItem", "dojox/mobile/Button"],

			function(connect, parser, registry, ListItem, ready) {
				parser.parse();
				showSelectedValue = function(){
					document.getElementById("msg").innerHTML =	registry.byId("cisItem1");
				}
				
// 				ready(function(){
// 					var btnWidget = registry.byId("btn1");
// 					connect.connect(btnWidget.domNode, "onclick", onBtnClicked);
// 					function onBtnClicked(e){
// 						alert("button clicked");
// 						return true;
// 					}
// 				});
			});
		</script>

	</body>
</html>
