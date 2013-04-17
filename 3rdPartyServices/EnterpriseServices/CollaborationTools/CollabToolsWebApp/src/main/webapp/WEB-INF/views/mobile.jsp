<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<html>
    <head>
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no"/>
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <title>Virtual Grafitti</title>
    <link href="http://ajax.googleapis.com/ajax/libs/dojo/1.8.3/dojox/mobile/themes/iphone/iphone.css" rel="stylesheet"></link>
 

  <style type='text/css'>
p.msg{font-family: Comic Sans MS,Brush Script MT,cursive;
    font-size: large;
    font-weight: bolder;
    }
</style>
 <script src="http://ajax.googleapis.com/ajax/libs/dojo/1.8.3/dojo/dojo.xd.js"
    djConfig="isDebug:true, parseOnLoad:true"></script>
    <script type="text/javascript">
    // Load the widget parser
	dojo.require("dojox.mobile.parser");
	// Load the base lib
	dojo.require("dojox.mobile");
	// If not a WebKit-based client, load compat
	dojo.requireIf(!dojo.isWebKit, "dojox.mobile.compat");
	dojo.require("dijit.TitlePane");
	var tp;
	dojo.ready(function(){
 	 tp = new dijit.TitlePane({title:"Write something", content: dojo.byId('iContent').innerHTML});
  
      dojo.byId("holder").appendChild(tp.domNode);
     
	});
 

	window.addEventListener("load",function() {
  // Set a timeout...
  setTimeout(function(){
    // Hide the address bar!
    window.scrollTo(0, 1);
  }, 0);
});
 
function getMacFromQS(){

   var queryString = window.top.location.search.substring(1);
   var parameterName = 'id' + "=";
   
   if (queryString.length > 0) {
   
   	begin = queryString.indexOf(parameterName);
   	if (begin != -1) {
   		begin += parameterName.length;
   		end = queryString.indexOf("&", begin);
   		if (end == -1) {
   				end = queryString.length
   			}
   		 val=unescape(queryString.substring(begin, end));
   		//alert(val);
   		document.getElementById("userId").value=val;//val ;
   	
   	//	alert (document.getElementById("userId").value);
   	}
  	}
                  
}

var myInterval; 

function initialUserDetails(){
 var xhrArgs = {
    url: "initialUserDetails.html",
    handleAs: "text",
    preventCache: true,
    load: function(data){
     
     jsonObj=dojo.fromJson(data);
     userId = jsonObj.userId
     dojo.byId('userId').value = userId;
     messages=jsonObj.messages;
     //messages=dojo.fromJson(data);
     mc=dojo.byId('cisBox');
     for (msg in messages){
     	thisMsg = messages[msg];
     	
     	var option = document.createElement('option');
     	option.setAttribute("id", thisMsg.id);
     	option.innerHTML=thisMsg.name;
     	option.setAttribute("value", thisMsg.id);
     	mc.appendChild(option);
     }
     	
     getMessages();
     myInterval = window.setInterval("getMessages()",60000);
     
    },
    
    error: function(error){
       alert( "An unexpected error occurred: " + error);
    }
  };

  // Call the asynchronous xhrGet

  var deferred = dojo.xhrGet(xhrArgs);
	
}


function submitMessage(){

 var xhrArgs = {
      //url: "mobile.html",
      //postData: dojo.formToJson("MessageForm"),
      form: dojo.byId("MessageForm"),
        handleAs: "text",
      load: function(data){
        //alert( "Message posted." + data);
        //addMsg(data)
         getMessages();
      },
      error: function(error){
        alert( "Message error. "+error);
      	}
    };
  // alert(1);
    // Call the asynchronous xhrPost
    //alert(xhrArgs.postData);
    var deferred = dojo.xhrPost(xhrArgs);
    //var deferred = dojo.xhrGet(xhrArgs);
}

var lastUpdateMsgId = 0;
function getMessages(){

 var xhrArgs = {

    //url: "vg/message/"+dojo.byId('userId').value+"/1,2,3/0",
    //url: "getMsg.html/guy-phone/1/0",
    url: "getMsg.html?userID="+dojo.byId('userId').value+"&cis=" + dojo.byId('cisBox').value + "&number="+lastUpdateMsgId,
    handleAs: "text",
    preventCache: true,
    load: function(data){
    
     messages=dojo.fromJson(data);
     mc=dojo.byId('messagesContainer');
     
     for (msg in messages)	{
     	 var c = new dojo.Color("red");
   		try{
   		sp=thisMsg.style.split('-');
   		c.setColor(sp[0],sp[1],sp[2]);
   		}
   		catch(err){}
      
    ///  alert(c.toHex());
      
     	thisMsg = messages[msg];
     	var p = document.createElement('p');
     //	p.setAttribute("style", 'color:'+c.toCss());
     	p.setAttribute("style", 'color:'+thisMsg.style);
     	p.setAttribute("class", "msg");
     	p.innerHTML=thisMsg.userId+' says: '+thisMsg.msg;
     	
     	mc.appendChild(p); ////----reverse
     	
     	if (lastUpdateMsgId < thisMsg.messageId){
     		lastUpdateMsgId = thisMsg.messageId;
     	}
     }
     	mc.scrollTop = mc.scrollHeight;
    },
    
    error: function(error){
       alert( "An unexpected error occurred: " + error);
    }
  };

  // Call the asynchronous xhrGet

  var deferred = dojo.xhrGet(xhrArgs);

}
</script>
    </head>
 
 <body onload="initialUserDetails();" onunload="window.clearInterval(myInterval)">
 
  <!-- the view or "page"; select it as the "home" screen -->
<div id="settings" dojoType="dojox.mobile.View" selected="true">
 
    <!-- a sample heading -->
    <h1 dojoType="dojox.mobile.Heading">Virtual Grafitti</h1>
 	<div>
		<IMG SRC="images/logo1.png" ALT=""> 
	</div>
   <div style="border-bottom: 1px solid #7C7A6B;
    clear: left;
    height:200px;
    margin: 10px 0 0;
    position: relative; background-image: url('images/wall.jpg');overflow : auto; " id="messagesContainer">
	    <p  class = 'msg' style="color: red;">
	    Omri writes: Hey Everybody, this is a graffiti!!
	    </p>
	     <p  class = 'msg' style=";color: aqua;">
	    Guy writes: Welcome to the meeting :)
	    </p>
    </div>
<div>
<div id="response2"></div>


<form id='MessageForm' name ='MessageForm' action="postMsg1.html" >
<table>
<tbody>
<tr>
<td>Id:</td><td><input type="text" id='userId' name='userId'value='<%=request.getParameter("id") %>' /></td>

</tr>
<!--
<tr>
<td>Group:</td><td><input type="text" value='1' id="cis" name="cis"></td>
</tr>
-->
<tr>
<td>color:</td><td><input type="text" name ='style'></td>
</tr>
<tr>
<td>Text:</td><td><input type="text" name='msg'></td>
</tr>
<tr>
<td>CIs:</td><td><select name="cisBox" id="cisBox" /></td>
</tr>

</tbody>
</table>
<a onclick="submitMessage();" style="cursor: hand">Spray Graffiti!</a>
</form>
</div>
</div>





 
    </body>
</html>