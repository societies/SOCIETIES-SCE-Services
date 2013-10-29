<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<!DOCTYPE html>
<html>
	<head>
		<link href="css/context.css" rel="stylesheet" type="text/css"
		media="screen" />
	
	 	<script type="text/javascript" src="js/jquery.js"></script>
	 	
	 	<script src="js/collabtoolsmgmt.js"></script>
	
		<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no"/>
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<title>CollabTools Mobile</title>
		<link href="//ajax.googleapis.com/ajax/libs/dojo/1.9.1/dojox/mobile/themes/iphone/iphone.css" rel="stylesheet"></link>
	</head>
<body style="visibility:hidden;">
	<div id="group1" data-dojo-type="dojox.mobile.View" data-dojo-props='keepScrollPos:false'><!-- keepScrollPos=false is to improve performance -->
		<ul data-dojo-type="dojox.mobile.TabBar" data-dojo-props='barType:"segmentedControl", fill:"always"'>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#communities", selected:true'>Communities</li>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#rules"'>Rules</li>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#applications"'>Applications</li>
			<li data-dojo-type="dojox.mobile.TabBarButton" data-dojo-props='moveTo:"#notification"'>Sessions</li>
		</ul>
			
		<div id="communities" data-dojo-type="dojox.mobile.View" data-dojo-props="selected: true">
			<div data-dojo-type="dojox/mobile/Heading" data-dojo-props='label:"Communities"'>
			  <span data-dojo-type="dojox/mobile/ToolBarButton">Stop</span>
			  <span data-dojo-type="dojox/mobile/ToolBarButton"
			        data-dojo-props='label:"Done",defaultColor:"mblColorBlue"'
			        style="float:right;" onclick="console.log('+ was clicked')"></span>
			</div>
			<h2 data-dojo-type="dojox.mobile.RoundRectCategory">There are ${size} Communities </h2>
			<ul data-dojo-type="dojox.mobile.RoundRectList" data-dojo-props='select:"single"'>
				<xc:forEach var="element" items="${cisresults}">
						<li data-dojo-type="dojox.mobile.ListItem">
							${element [0]}
						</li>
				</xc:forEach>
			</ul>
			<button data-dojo-type="dojox.mobile.Button" data-dojo-props='label:"Start CollabTools", onClick:function(e){ alert("button clicked");return true; }'></button>


		</div>
		
		<div id="rules" data-dojo-type="dojox.mobile.View">
			<div data-dojo-type="dojox/mobile/Heading" data-dojo-props='label:"Rules"'>
			  <span data-dojo-type="dojox/mobile/ToolBarButton" data-dojo-props='icon:"mblDomButtonWhitePlus"' style="float:right;" onclick="dijit.registry.byId('customPicker').show()"></span>
			</div>
			<h2 data-dojo-type="dojox.mobile.RoundRectCategory">Current Rules</h2>
			<ul id="list1" data-dojo-type="dojox/mobile/RoundRectList" data-dojo-props='editable:true'>
				<xc:forEach var="element" items="${rulesresults}">
 						<li data-dojo-type="dojox.mobile.ListItem">
							${element[0]} | ${element[1]} | ${element[2]} | ${element[3]} | ${element[4]} | ${element[5]}
								    <span data-dojo-type="dojox/mobile/ToolBarButton" data-dojo-props="layout: 'right'" onclick="deleteRule('${element[0]}','${element[1]}','${element[2]}','${element[3]}','${element[4]}','${element[5]}')">Delete</span>
 						</li> 
				</xc:forEach>
			</ul>
			<button onclick="dijit.registry.byId('customPicker').show()">Add Rule</button>
			<div id="customPicker" data-dojo-type="dojox/mobile/Overlay">
			      <h1 data-dojo-type="dojox/mobile/Heading" label="Rules">
			              <div data-dojo-type="dojox/mobile/ToolBarButton" label="Discard" class="mblColorBlue" style="width:45px;float:left;" onClick="dijit.registry.byId('customPicker').hide()"></div>			              
			              <div data-dojo-type="dojox/mobile/ToolBarButton" label="Done" class="mblColorBlue" style="width:45px;float:right;" onClick="insertRule();dijit.registry.byId('customPicker').hide()"></div>
			      </h1>
			          <div id="spin1" data-dojo-type="dojox.mobile.SpinWheel">
					     <div data-dojo-type="dojox.mobile.SpinWheelSlot"
					     	 labels="r01,r02,r03,r04,r05,r06,r07,r08,r09"
					         style="width:70px;"></div>			          
					     <div data-dojo-type="dojox.mobile.SpinWheelSlot"
					     	labels="
					     	<xc:forEach var="type" items="${attributeTypes}">
								${type},
							</xc:forEach>	
					         "
					         style="text-align:center;width:140px;"></div>
					     <div data-dojo-type="dojox.mobile.SpinWheelSlot"
					     	 labels="Same,Diff,=,!=,>,>=,<,<=,Similar"
					         style="width:130px;"></div>
					     <div data-dojo-type="dojox.mobile.SpinWheelSlot"
					         labelFrom="0" labelTo="9"
					         style="width:30px;"></div>
					     <div data-dojo-type="dojox.mobile.SpinWheelSlot"
					         labels="ShortTerm Ctx,LongTerm Ctx"
					         style="width:150px;"></div>
					     <div data-dojo-type="dojox.mobile.SpinWheelSlot"
					         labelFrom="0" labelTo="9"
					         style="width:30px;"></div>					     
					  </div>
					</div>
			<button onclick="onClickEdit()">Edit</button>
			<button onclick="onClickDone()">Done</button>
<!-- 			<button data-dojo-type="dojox.mobile.Button" onClick="showSelectedValue()" data-dojo-props='label:"Value"'></button> -->
<!-- 			<div id="msg"></div> -->
		</div>
		
		
		

		<div id="applications" data-dojo-type="dojox.mobile.ScrollableView">
			<h1 data-dojo-type="dojox.mobile.Heading">Applications</h1>
			<h2 data-dojo-type="dojox.mobile.RoundRectCategory">Applications available</h2>
			<ul data-dojo-type="dojox.mobile.RoundRectList">
					<xc:forEach var="serverName" items="${appserver}" varStatus="loop">
<!-- 					<input type="radio" data-dojo-type="dojox/mobile/RadioButton" checked="false" name="radioGroup" value="no" /> -->
						<li data-dojo-type="dojox.mobile.ListItem"
      						data-dojo-props='rightText:"${appnames[loop.index]}"'>Server:${serverName}					
  						</li>
					</xc:forEach>
			</ul>
			<textarea data-dojo-type="dojox.mobile.TextArea" placeHolder="Enter new server"></textarea>
			
			<button data-dojo-type="dojox.mobile.Button" onClick="showSelectedValue()" data-dojo-props='label:"Value"'></button>
			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"bottom"'><a href="https://play.google.com/store/apps/details?id=com.xabber.android">Download collaborative application client</a></h1>
			<div id="msg"></div>			
		</div>
		
		<div id="notification" data-dojo-type="dojox.mobile.ScrollableView">
			<h1 data-dojo-type="dojox.mobile.Heading">Sessions</h1>
				<h2 data-dojo-type="dojox.mobile.RoundRectCategory">Current Session</h2>
				<ul data-dojo-type="dojox.mobile.RoundRectList" data-dojo-props='select:"single"'>
					<li id="cisItem1" data-dojo-type="dojox.mobile.ListItem" data-dojo-props='checked:true'>
						CIS 1
					</li>
				</ul>
				<button data-dojo-type="dojox.mobile.Button" data-dojo-props='label:"Click me!", onClick:function(e){ alert("button clicked");return true; }'></button>
			</div>

		<!-- configure and load dojo -->
		<script src="//ajax.googleapis.com/ajax/libs/dojo/1.9.1/dojo/dojo.js" data-dojo-config="isDebug:1, async:1"></script>
		<script>

			require(["dojo/_base/connect", "dojox/mobile/parser", "dojox/mobile", "dojox/mobile/deviceTheme", "dojox/mobile/compat", "dojo/domReady!", "dojox/mobile/TabBar", "dojox/mobile/ScrollableView", "dojox/mobile/TextArea", "dojo/ready",
						"dijit/registry", "dojo/dom-class", "dojox/mobile/SpinWheel", "dojox/mobile/Overlay", "dojox/mobile/ListItem", "dojox/mobile/Button"],
						
						

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
