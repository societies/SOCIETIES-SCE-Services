<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<title>SOCIETIES | Virtual Graffiti</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <link rel="stylesheet" type="text/css"
                  href="dojo/dijit/themes/dijit.css">
                  <link rel="stylesheet" type="text/css"
                  href="http://ajax.googleapis.com/ajax/libs/dojo/1.6/dijit/themes/claro/claro.css" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.7.2/dojo/dojo.js" djConfig="isDebug: true, parseOnLoad: true"></script>
<style type='text/css'>
p.msg{font-family: Comic Sans MS,Brush Script MT,cursive;
    font-size: large;
    font-weight: bolder;
    }
</style>
<script type="text/javascript">
 	
 	
 	dojo.require("dijit.TitlePane");
	var tp;
	dojo.ready(function(){
 	 tp = new dijit.TitlePane({title:"Write something", content: dojo.byId('iContent').innerHTML});
  
      dojo.byId("holder").appendChild(tp.domNode);
     
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
function submitMessage(){

 // The parameters to pass to xhrGet, the url, how to handle it, and the callbacks.
  var xhrArgs = {
    url: "/Admin/PersonalTags",
    handleAs: "text",
    preventCache: true,
    content: {
      key1: "value1",
      key2: "value2",
      key3: "value3"
    },
    load: function(data){
      // Replace newlines with nice HTML tags.
      data = data.replace(/\n/g, "<br>");

      // Replace tabs with spacess.
      data = data.replace(/\t/g, "&nbsp;&nbsp;&nbsp;");

      targetNode.innerHTML = data;
    },
    error: function(error){
      targetNode.innerHTML = "An unexpected error occurred: " + error;
    }
  }


    var deferred = dojo.xhrPost(xhrArgs);


}

function getMessages(){

 var xhrArgs = {

    url: "vg/message/"+dojo.byId('userId').value+"/0",
    handleAs: "text",
    preventCache: true,
    load: function(data){
       
    // alert(data)
     messages=dojo.fromJson(data);
     mc=dojo.byId('messagesContainer');
     for (msg in messages)
     	{
     	 var c = new dojo.Color("red");
   		try{
   		sp=thisMsg.style.split('-');
   		c.setColor(sp[0],sp[1],sp[2]);
   		}
   		catch(err){}
      
    ///  alert(c.toHex());
      
     	thisMsg = messages[msg];
     	var p = document.createElement('p');
     	p.setAttribute("style", 'color:'+c.toCss());
     	p.setAttribute("class", "msg");
     	p.innerHTML=thisMsg.userId+' says: '+thisMsg.msg;
     	mc.appendChild(p);
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
<body onload="getMacFromQS();getMessages();"style="height: 100%;background-color: #EBECEB;min-height: 100%;color: #656253; font-family: Arial,Tahoma,Verdana;  font-size: 12px;  line-height: 20px;  margin: 0 auto; padding: 0;">
<div id="wrapper"  style="background-color: white;margin: 0 auto;
    padding: 0 30px;
    width: 940px;">
<div>
<img height="100" alt="" src="http://www.ict-societies.eu/wp-content/themes/societies/images/logo.png">
</div>

<div id="title" style="color: #F28B0C;font-family: Arial,Verdana;
    font-weight: normal; font-size: 20px;">
<h2>3rd Party Services - Virtual Graffiti!</h2>
</div>
<div style="border-bottom: 1px solid #7C7A6B;
    clear: left;
    
    margin: 10px 0 0;
    position: relative;">
<p style="font-size: 12px;
    margin-bottom: 20px;">Virtual Graffiti is a social contextual location based service, taking your posts to real walls.</p>
</div>
<div style="border-bottom: 1px solid #7C7A6B;
    clear: left;
    height:200px;
    margin: 10px 0 0;
    position: relative; background-image: url('views/img/wall.jpg');overflow : auto; " id="messagesContainer">
    <p  class = 'msg' style="color: red;">
    Omri writes: Hey Everybody, this is a graffiti!!
    </p>
     <p  class = 'msg' style=";color: aqua;">
    Guy writes: Welcome to the meeting :)
    </p>
    </div>
<div>
<div id="response2"></div>
<div id="holder"></div>
<div id="iContent" style="display: none">
<form id='messageFrm' name ='messageFrm' >
<table>
<tbody>
<tr>
<td>Id:</td><td><input type="text" id='userId' name='userId'value='<%=request.getParameter("id") %>' /></td>
</tr>
<tr>
<td>Group(community?):</td><td><input type="text" value='1' name="targetGroup"></td>
</tr>
<tr>
<td>style:</td><td><input type="text" name ='style'></td>
</tr>
<tr>
<td>Text:</td><td><input type="text" name='msg'></td>
</tr>
<tr>
<td>Expiration?:</td><td><input type="text" name ='expiration'></td>
</tr>
</tbody>
</table>
<a onclick="submitMessage();" onmouseover="this.style.cursor='pointer'">Spray Graffiti!</a>
</form>
</div>
</div>

</div>




</body>
</html>