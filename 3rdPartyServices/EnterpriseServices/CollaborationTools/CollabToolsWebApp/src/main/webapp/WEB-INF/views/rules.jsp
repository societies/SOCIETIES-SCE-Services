<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CollabTools - Rules</title>


<link href="css/context.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="css/ctx-table-style.css" rel="stylesheet"
	type="text/css" media="screen" />

<script type="text/javascript" src="js/jquery.js"></script>
 <script type="text/javascript">
    function mangeRule(input) {
      $.ajax({
        url: 'rulesmanager.html',
        data:{ "value0":input[0], "value1":input[1], "value2":input[2], "value3":input[3], "value4":input[4], "value5":input[5], "value6":input[6]},
        success: function(data) {
          $('#rules').html(data);
        }
      });
    }
    
    function insertRule() {
    	var info = []; 
		info[0] = document.getElementById('ruleValue').value; 
		info[1] = document.getElementById('${attribute_label}').value; 
		info[2] = document.getElementById('operatorValue').value;
		info[3] = document.getElementById('typeValue').value;
		info[4] = document.getElementById('priorityValue').value;
		info[5] = document.getElementById('ctxValue').value;
		info[6] = 'insert';
		mangeRule(info);
		setTimeout(function(){location.reload();}, 1000);
      }
    
    function deleteRule(rulename, ctxAttribute, operator, type, priority, value) {
    	var info = []; 
		info[0] = rulename; 
		info[1] = ctxAttribute; 
		info[2] = operator; 
		info[3] = type; 
		info[4] = priority;
		info[5] = value;
		info[6] = 'delete';
		mangeRule(info);
		setTimeout(function(){location.reload();}, 1000);
    }
    

  </script>


</head>
<body>

	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT HERE ................ -->
			<div class="query">

			<h1>Rules</h1>
				<table id="ruleTable">
					<tbody>
					<tr id="ruleFistRow"><td>Name</td><td>Attribute</td><td>Operator</td><td>Type</td><td>Priority</td><td>Value</td></tr>
					
					<tr>
					<td><input size="10" id="ruleValue"/></td>
					<td><select id="${attribute_label}" class="textArea">
						<option value="NONE" label="--- Select Attribute Type --- " />
							<xc:forEach var="type" items="${attributeTypes}">
								<option value="${type}">${type}</option>
							</xc:forEach>	
						</select></td>
					<td><select id="operatorValue" name="operator" class="textArea">
							<option value="SAME">same</option>
							<option value="DIFFERENT">different</option>
							<option value="EQUAL">equals</option>
						    <option value="NOT_EQUAL">not equal</option>
							<option value="GREATER">greater</option>
							<option value="GREATER_OR_EQUAL">greater or equal</option>
							<option value="LESS">less</option>
							<option value="LESS_OR_EQUAL">less or equal</option>
							<option value="SIMILAR">similar</option>
						</select></td>
						<td><select id="typeValue" name="type" class="textArea">
							<option value="ShortTermCtxTypes">Short Term Ctx</option>
							<option value="LongTermCtxTypes">Long Term Ctx</option>
						</select></td>
 					<td><input size="8" id="priorityValue" name="priorityValue" type="text" value=""/></td>
<!-- 					<td><select id="weightValue" name="weight" class="textArea"> -->
<!-- 						<option value="10">10 %</option> -->
<!-- 						<option value="20">20 %</option> -->
<!-- 						<option value="30">30 %</option> -->
<!-- 						<option value="40">40 %</option> -->
<!-- 						<option value="50" selected="selected">50 %</option> -->
<!-- 						<option value="60">60 %</option> -->
<!-- 						<option value="70">70 %</option> -->
<!-- 						<option value="80">80 %</option> -->
<!-- 						<option value="90">90 %</option> -->
<!-- 						<option value="100">100 %</option> -->
<!-- 					</select></td> -->
					<td><input size="8" id="ctxValue"/></td>
					<td><button type="button" onclick="insertRule()" id="addRule">Add Rule</button></td>			
					
					</tr>

				  </tbody>
				</table>
			</div>

		<!--  RESULT SECTION -->


		<!-- Table markup-->
		<div class="query">
		<label>Rules available: </label>
		</div>
		<table id="newspaper-a">

			<!-- Table header -->

			<thead>
				<tr>
					<th scope="col" id="rule-name">Rule Name</th>
				    <th scope="col" id="ctx-type">Context Type</th>
					<th scope="col" id="operator">Operator</th>
					<th scope="col" id="ctx-type">Context Type</th>
					<th scope="col" id="priority">Priority</th>
					<th scope="col" id="value">Value</th>
					<th scope="col" id="action">Action</th>

				</tr>
			</thead>

			<!-- Table footer -->
		
			<tfoot>
				
			</tfoot>

			<!-- Table body -->
			<tbody>
				<xc:forEach var="element" items="${rulesresults}">
					<tr id="${element[0]}">
						<td  name="RuleName">${element[0]}</td>
					    <td  name="CtxAttributetype">${element[1]}</td>
						<td  name="Operatortype">${element[2]}</td>
						<td  name="Ctxtype">${element[3]}</td>
						<td  name="Prioritytype">${element[4]}</td>
						<td  name="Valuetype">${element[5]}</td>
						<td>
						    <button onclick="deleteRule('${element[0]}','${element[1]}','${element[2]}','${element[3]}','${element[4]}','${element[5]}')"> Delete </button>
						</td>

					</tr>
				</xc:forEach>
			</tbody>

		</table>

		<div class="navigator">
	 	 	<div id="rules">
		</div>

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>