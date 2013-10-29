
/**
 *	JS for CollabTools framework
 *   @author: Chris Lima
 **/

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


function doAjax(input) {
	$.ajax({
		url: 'checkcis.html',
		data: ({name : input}),
		success: function(data) {
			$('#checkcis').html(data);
		}
	});
}

function(connect, domClass, ready, registry){
	var delItem, handler, btn1, list1;

	function showDeleteButton(item){
		hideDeleteButton();
		delItem = item;
		item.rightIconNode.style.display = "none";
		if(!item.rightIcon2Node){
			item.set("rightIcon2", "mblDomButtonMyRedButton_0");
			item.rightIcon2Node.firstChild.innerHTML = "Delete";
		}
		item.rightIcon2Node.style.display = "";
		handler = connect.connect(list1.domNode, "onclick", onDeleteItem);
	}
	function hideDeleteButton(){
		if(delItem){
			delItem.rightIconNode.style.display = "";
			delItem.rightIcon2Node.style.display = "none";
			delItem = null;
		}
		connect.disconnect(handler);
	}
	function onDeleteItem(e){
		var item = registry.getEnclosingWidget(e.target);
		if(domClass.contains(e.target, "mblDomButtonMyRedButton_0")){
			setTimeout(function(){
				item.destroy();
			}, 0);
		}
		hideDeleteButton();
	}

	connect.subscribe("/dojox/mobile/deleteListItem", function(item){
		showDeleteButton(item);
	});

	onClickEdit = function(){
		list1.startEdit();
	}
	onClickDone = function(){
		hideDeleteButton();
		list1.endEdit();
	}

	ready(function(){
		btn1 = registry.byId("btn1");
		list1 = registry.byId("list1");
		connect.connect(list1, "onStartEdit", null, function(){
			console.log("StartEdit");
		});
		connect.connect(list1, "onEndEdit", null, function(){
			console.log("EndEdit");
		});
		connect.connect(list1, "onDeleteItem", null, function(widget){
			console.log("DeleteIconItem: " + widget.label);
		});
		connect.connect(ic, "onMoveItem", null, function(widget, from, to){
			console.log("MoveIconItem: " + widget.label + " (" + from + " -> " + to + ")");
		});
	});