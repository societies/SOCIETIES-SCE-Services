<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Networking Zone - Built for Societies Platform</title>

<script type="text/javascript" src="js/tabView.js"></script>
<script type="text/javascript">
function doAjaxPostInside() {
    // get the form values
    var ajaxName = $('name').val();
    
    $.ajax({
        type: "POST",
        url: contexPath + "/AddUser.html", 
        data: "name=" + ajaxName ,
        success: function(response){
            // we have the response
            if(response.status == "SUCCESS"){
                userInfo = "<ol>";
                for(i =0 ; i < response.result.length ; i++){
                    userInfo += "<br><li><b>Name</b> : " + response.result[i];
                 }
                 userInfo += "</ol>";
                 $('#info').html("User has been added to the list successfully. " + userInfo);
                 $('#name').val('');
                 $('#error').hide('slow');
                 $('#info').show('slow');
             }else{
                 errorInfo = "";
                 for(i =0 ; i < response.result.length ; i++){
                     errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
                 }
                 $('#error').html("Please correct following errors: " + errorInfo);
                 $('#info').hide('slow');
                 $('#error').show('slow');
             }
         },
         error: function(e){
             alert('Error: ' + e);
         }
    });
}
</script>

<style type="text/css">
Tabview code from www.javascriptsource.com css tab-view.html -->div.TabView div.Tabs
	{
	height: 24px;
	overflow: hidden;
}

div.TabView div.Tabs a {
	float: left;
	display: block;
	width: 200px;
	text-align: left;
	height: 24px;
	line-height: 28px;
	vertical-align: middle;
	background: url("images/tabs.png") no-repeat -2px -1px;
	text-decoration: none;
	font-family: "Times New Roman", Serif;
	font-weight: 900;
	font-size: 13px;
	color: #000080;
}

div.TabView div.Tabs a:hover,div.TabView div.Tabs a.Active {
	background: url("images/tabs.png") no-repeat -2px -31px;
}

div.TabView div.Pages {
	clear: both;
	border: 1px solid #404040;
	overflow: hidden;
}

div.TabView div.Pages div.Page {
	height: 100%;
	padding: 0px;
	overflow: hidden;
}

div.TabView div.Pages div.Page div.Pad {
	padding: 3px 18px;
}
</style>


<script type="text/javascript" src="js/jquery-1.7.2.js"></script>


<script type="text/javascript">  
	var contexPath = "<%=request.getContextPath()%>";
</script>

<script type="text/javascript" src="js/userajax.js"></script>


</head>




</head>
<body>


	<table border="0" width="100%">
		<tr>
			<td colspan="2">

				<table border="0" align="center">
					<tr>
						<td>
							<center>
								<img src="images\NetworkingZone.png" />
							</center>
						</td>
					</tr>
				</table>


				<table border="0" width="100%">
					<tr>
						<td width="75%">
							<table border="0" width="75%">
								<!-- .................PLACE YOUR CONTENT HERE ................ -->
								<tr><td>
								<h4>${message}</h4>
								<br />
								<h4>${error}</h4>
								<br />
								</td></tr>
								<form:form method="POST" action="networking.html"
									commandName="networkingForm">

									<form:errors path="*" cssClass="errorblock" element="div" />


									<div class="TabView" id="TabView">

										<!-- *** Tabs ************************************************************** -->

										<div class="Tabs" style="width: 800px;">
											<a>&nbsp; &nbsp; My Info </a> <a>&nbsp; &nbsp;
												Connections </a>
										</div>

										<!-- *** Pages ************************************************************* -->

										<div class="Pages"
											style="width: 1000px; height: 300px; text-align: left;">

											<div class="Page">
												<div class="Pad">
												
												
												stuff goes here													
												
												</div>
											</div>

											<!-- *** Page2 Start *** -->

											<div class="Page">
												<div class="Pad">

													Zone info and options go here
													<!-- *** Page2 End ***** -->

												</div>
											</div>


										</div>
									</div>

									<input type="submit" value="Save" onclick="doAjaxFormPost()" />
								</form:form>
							</table>
						</td>
					<!-- 	<td width="25%">
							<table border="2" width="25%"><tr>TheEvents</tr> </table>
						</td> -->
					</tr>
				</table> 
				
	</td>
	</tr>
	</table>			
												<h1>Add Users using Ajax ........</h1>  

    <!--       <table>  

                 <tr><td colspan="2"><div id="error" class="error"></div></td></tr>  
                 <tr><td>Enter your name : </td><td> <input type="text" id="ajaxName"><br/></td></tr>  

                 <tr><td colspan="2"><input type="button" value="Add Users" onclick="doAjaxPostInside()"><br/></td></tr>  

                 <tr><td colspan="2"><div id="info" class="success"></div></td></tr>  

         </table> -->  
         
         Enter your name : </td><td> <input type="text" id="name"><br/>  
         <input type="button" value="Add Users" onclick="doAjaxPostInside()"><br/>
		<div id="info" class="success"></div>
				
				<script type="text/javascript">
					tabview_initialize('TabView');
				</script> <!-- .................END PLACE YOUR CONTENT HERE ................ -->
				<!-- FOOTER --> <jsp:include page="footer.jsp" /> <!-- END FOOTER -->
</body>


</html>


