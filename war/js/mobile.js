var refreshFunction;

if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
}

var refreshOnShake = function() {
	console.log("refreshOnShake");
	if (refreshFunction !== undefined || refreshFunction != null) {
		if (typeof(android) !== "undefined") {
			window.android.toast('Refreshing...');
			console.log("refreshing...");
		}
		refreshFunction();
	}
	else {
		console.log("refreshFunction is undefined or null");
	}
};

var isSocietiesUser = function() {
    if (typeof(android) !== "undefined") {
        return (window.android.isSocietiesUser);
    }
    if (window.location.hostname === 'localhost') return true;

    return false;
};

var CrowdTaskingApp = function() {

    var tasks = [];
    var currentTaskIndex = -1;
    var mode = 'new';
    var currentUser = null;
    var communities = [];

    var getTaskById = function(id) {
    	$.ajax({
    		type: 'GET',
    		url: '/rest/task',
    		data: { 'id': id },
    		success: function(task) {
    			tasks = [];
            	tasks.push(task);
            	currentTaskIndex = 0;
    			$('#backButton').bind('tap', function(event, data) {
	    	        event.preventDefault();
	    	        window.location.replace('/menu');
    			});    			
            	fillForm();
    		}
    	});
    };

    var refreshTasks = function() {
    	$.ajax({
    		type: 'GET',
    		url: '/rest/tasks/interesting',
    		success: function(allTasks) {
    			tasks = allTasks;
    		}
    	});
    };

    var getUser = function () {
    	$.ajax({
    		type: 'GET',
    		url: '/rest/users/me',
    		success: function(user) {
    			currentUser = user;
    			$('#currentUser .ui-btn-text').text(user.firstName+' '+user.lastName);
    			$('#currentUser').show();
    		}
    	});
    };

    var loadTasks = function(apiUrl) {
    	$.ajax({
    		type: 'GET',
    		url: '/rest/tasks/'+apiUrl,
    		success: function(allTasks) {
    			tasks = allTasks;
    			if (apiUrl === 'my') {
        			displayTasks('My tasks');
    			}
    			else {
        			displayTasks();
    			}
    		   	var results = new RegExp('[\\?&]id=([^&#]*)').exec(window.location.href);
    		   	if (results != null) {
    		   		if (CrowdTaskingApp.setCurrentTaskById(results[1]) != -1) {
    		   			$.mobile.changePage('/task/view');
    		   		}
    		   	}
    		}
    	});
    };
    
    var postTask = function() {	  
        var form_data = $('#newTaskForm').serialize();
        $.ajax({
          type: "POST",
          url: "/rest/task",
          data: form_data,
          error: function(error) {
        	  toast(error.responseText);
              $('#saveButton').show();
          },
          success: function() {
              history.back();
        	  loadTasks('my');
          },
          complete: function() {
          }
        });
    };
    
    var startExecution = function() {	  
    	var form_data = $('#executeTaskForm').serialize();

        $.ajax({
            type: "POST",
            url: "/rest/task",
            data: form_data,
            error: function(error) {
            	toast(error.responseText);
            	$('#executeButton').show();
            },
            success: function() {
            	history.back();
            	history.back();
            	var url = window.location.href;
            	if (url.endsWith("tasks/interesting")) {
            		loadTasks('interesting');
            	}
            	if (url.endsWith("tasks/my")) {
            		loadTasks('my');
            	}
            	
            	/*tasks[currentTaskIndex].status = 'inprogress';	// je to ok???
                $.mobile.changePage('/menu', {
                    transition: 'slide',
                    reverse: true
                });
                //loadTasks('interesting');*/
            },
            complete: function() {
            }
          });
    };

    var taskFinalize = function() {
    	var form_data = $('#viewTaskForm').serialize();
        $.ajax({
            type: "POST",
            url: "/rest/task",
            data: form_data,
            error: function() {
            },
            success: function() {
            },
            complete: function() {
            	refreshTasks();
                $.mobile.changePage('/menu', {
                    transition: 'slide',
                    reverse: true
                });
                //loadTasks('interesting');
            }
          });
    };
    
    var showComments = function(taskId, showOnlyCommentsForExecution) {
    	if(typeof(showOnlyCommentsForExecution)==='undefined') showOnlyCommentsForExecution = false;

    	$("#comments").find("tr:gt(0)").remove();
    	$.ajax({
    		type: 'GET',
    		url: '/rest/comment',
    		data: { taskId: taskId,
    				execution: showOnlyCommentsForExecution },
    		success: function(comments) {
				displayComments(comments);
    		    if (comments.length == 0) {
    		    	//$('#btnExec').hide();
    		    }
    		}
    	});
    	 
    };

    var _postComment = function() {
        var form_data = $('#viewTaskForm').serialize();
        $.ajax({
          type: "POST",
          url: "/rest/comment",
          data: form_data,
          error: function() {
          },
          success: function() {
              showComments($('#vwTaskId').val(), $('#commentForExecution').val());
              $('#vwComment').val('');
          },
          complete: function() {
        	  $('#commentButton').show();
          }
        });
    };

    var postMeeting = function() {
        var form_data = $('#meetingForm').serialize();
        $.ajax({
          type: "POST",
          url: "/rest/meeting/create",
          data: form_data,
   		  error: function(error) {
   			  toast(error.responseText);
		  },
          success: function(response) {
        	  cancelNewMeeting();
        	  var updatedTask = jQuery.parseJSON(response);
        	  tasks[currentTaskIndex] = updatedTask;
        	  showMeetings(updatedTask);
          },
          complete: function(response) {
        	  $('#saveNewMeetingButton').show();
          }
        });
    };

    var displayTasks = function (heading) {
    	if(typeof(heading)==='undefined') heading = 'Tasks in my communities';

        var createTapHandler = function(currentIndex) {
            return function (event, data) {
                CrowdTaskingApp.setCurrentTask(currentIndex);
            };
        };

        var list = $('#taskList');
        list.empty();

        $("#taskList").append('<li data-role="list-divider" role="heading">'+heading+'</li>');
        for (var index = 0, length = tasks.length; index < length; ++index) {
            var task = tasks[index];
            // icons from http://www.iconfinder.com/search/?q=iconset%3Adortmund
            var editLink = $('<a>');
            editLink.attr('href', '/task/view');
            editLink.attr('data-transition', 'slide');
            editLink.bind('tap', createTapHandler(index));
            //editLink.append('<img src="/images/'+task.status+'.png" alt="'+task.status+'" class="ui-li-icon" style="vertical-align:middle">');
            editLink.append('<img src="/images/'+task.status+'.png" alt="'+task.status+'" class="ui-li-icon" style="left:12px; top:42px;">');
            editLink.append('<h3 style="white-space:normal;">'+task.title+'</h3>');
            editLink.append('<p style="white-space:normal;"><strong>'+task.description+'</strong></p>');
            editLink.append('<p>'+task.postedBy+'<img class="'+task.trustLevel+'" src="/images/img_trans.gif" width="1" height="1" /> ('+formatDate(task.created)+')</p>');
        	//var d1=new Date(task.dueDate);
            //editLink.append('<p>'+task.postedBy+', '+d1.getDate()+'.'+(d1.getMonth()+1)+'.'+d1.getFullYear()+'</p>');

            //editLink.append('<p class="ui-li-aside">'+task.postedBy+'</p>');
            
            var nrScore = $('<span>');
            nrScore.attr('class', 'ui-li-count');
            nrScore.text(task.score+","+task.interestScore);

            var newLi = $('<li style="white-space:normal;">');
            //newLi.append('<img src="/images/'+task.status+'.png" alt="'+task.status+'" class="ui-li-icon">');
            newLi.append(editLink);
            //newLi.append(nrScore);
            list.append(newLi);
        }
        list.listview('refresh', true);
    };

    var showMeetings = function(task) {
    	if (task.status != 'IN_PROGRESS') return;
    	
    	$('#meetingDiv').show();
    	$meetingList = $('#meetingList'); 
    	if (task.meetings == null || task.meetings.length == 0) {
    		$meetingList.hide();
    		return;
    	}
    	else {
    		$meetingList.show();
    	}
    	$meetingList.empty();
    	for (var i=0; i<task.meetings.length; i++) {
    		var miting = task.meetings[i];
    		var organizer = miting.organizer == '' ? '' : ' organized by ' + miting.organizer;
    		var meetingText = miting.subject+' in '+miting.cs.name+' at '+formatDate(miting.startTime)+organizer;
    		
			if (typeof(android) !== "undefined") {
				$meetingList.append($('<li><a style="white-space:normal;" href="/android/meeting/'+miting.id+'" data-ajax="false">'+meetingText+'</a></li>'));
			}
			else {
	    		$meetingList.append($('<li style="white-space:normal;">'+meetingText+'</li>'));
			}
    	}
    	$meetingList.listview('refresh', true);
    };

    var displayComments = function (comments) {
    	if (mode === 'execute') {
    		var commentsDiv = $('#commentsDiv');
	        for (var i = 0; i < comments.length; i++) {
	            var comment = comments[i];
	            var newDiv = $('<div>');
	            newDiv.append('<input type="checkbox" name="activeComments" value="'+comment.id+'" id="'+comment.id+'" class="custom">');
	            newDiv.append('<label style="width:100%;" for="'+comment.id+'" data-corners="true" data-shadow="false" data-iconshadow="true"'+
	            		'data-icon="checkbox-off" data-theme="c">'+
	            		comment.commentText+'<br><span style="font-weight:normal">by '+comment.postedBy+'</span></label>');
	            commentsDiv.append(newDiv);
	        }
	        $("#commentsFieldset").fieldcontain();
	        $("input[type='checkbox']").checkboxradio();
    	}
    	else {
	    	var list = $('#commentList');
	        list.empty();
	
	        if (comments.length > 0) {
	        	$("#commentList").append('<li data-role="list-divider" role="heading">Comments:</li>');
	        	var task = tasks[currentTaskIndex];
	        	if (task.status == 'inprogress' && task.myTask == true) {
	        		$("a.rightHeaderButton").hide().eq(1).show();	// show finalize button
	        	}
	        	if (task.status == 'open' && task.myTask == true) {
	        		$("a.rightHeaderButton").hide().eq(0).show();	// show execute button
	        	}
		        for (var i = 0; i < comments.length; i++) {
		            var comment = comments[i];
		            var newLi = $('<li data-role="fieldcontain">');
		            
		            
		            //URLs starting with http://, https://, or ftp://
    				replacePattern = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
    				var newCommentText = comment.commentText.replace(replacePattern, '<a href="$1" target="_blank">$1</a>');
		            
		            newLi.append('<h3 style="white-space:normal;">'+newCommentText+'</h3>');
		            newLi.append('<p style="white-space:normal;">'+comment.postedBy+'<img class="'+comment.trustLevel+'" src="/images/img_trans.gif" width="1" height="1" /> ('+formatDate(comment.posted)+')</p>');
		            var picUrl = comment.picUrl;
		            if (picUrl == undefined) {
		            	picUrl = '/images/pic'+(Math.floor(Math.random()*4)+1)+'.png';
		            }
		            newLi.append('<img src="'+picUrl+'" class="ui-li-icon" style="left:12px; top:34px;">');
		            //newLi.append('<img src="/images/pic'+(Math.floor(Math.random()*4)+1)+'.png" class="ui-li-icon" style="left:12px; top:34px;">');
		            if (!comment.myComment) {
		            	var selectedOff = !comment.liked ? 'selected' : '';
		            	var selectedOn = comment.liked ? 'selected' : '';
			            newLi.append(' \
		            		<select name="slider" id="slider'+i+'" data-role="slider"> \
								<option value="off-'+comment.id+'" '+ selectedOff + '>Like</option> \
								<option value="on-'+comment.id+'" '+ selectedOn + '>Liked</option> \
							</select> \
				        ');
		        	}
		            list.append(newLi);
			        $('#slider'+i).slider();
			        $('#slider'+i).on("change", function(event, ui) {
			        	event.preventDefault();
			        	postCommentsLikes();
		        	});
		        }
	        }
	        list.listview('refresh', true);
    	}
    };
    
    var fillForm = function() {
        var task = tasks[currentTaskIndex];
        if (task === undefined) {	// add task
            task = new CrowdTaskingApp.Task();
            $('#taskName').val(task.title);
            $('#taskDescription').val(task.description);
            //desc = $('#taskDescription').val();
            $('#taskTags').text(task.tags);
        }
        else {	// view, execute task
        	// ui-title
        	//var title = $('#formTitle').text();
        	var tazkId = $('#vwTaskId').val();
/*
            if (mode === 'execute') {
            	form_data= $('#executeTaskForm').serialize();
            }
*/
            $('#vwTaskId').val(task.id);	// post comment form
            $('#taskId').val(task.id);	    // new meeting form
/*
        	if (task.status === 'inprogress') {
        		$('#formTitle').text("Task's execution");
           		$("a.rightHeaderButton").hide();	// hide execute & finalize buttons
        	}
*/
        	if (task.status === 'finished') {
        		$("a.rightHeaderButton").hide();
        	}
        	if (task.status !== 'finished') {
        		$('#vwReply').show();
        	}
        	if (task.status === "open") {
        		$('#viewTaskDialog').dialog('option', 'title', 'View task and reply');
        	    if (!task.myTask) {
               	    getLike();
        	    }
        	}
        	$('#taskName').text(task.title);
        	$('#taskDescription').text(task.description);
            var d1=new Date(task.created);
            $('#taskCreated').text(d1.getDate()+"."+(d1.getMonth()+1)+"."+d1.getFullYear());
            d1=new Date(task.dueDate);
            $('#taskDate').text(d1.getDate()+"."+(d1.getMonth()+1)+"."+d1.getFullYear());

            //var communities = [];
            var communitiesText = "";
            if (task.communities !== undefined) {
                var communities = task.communities;
            }
            if (task.communityJids !== undefined) {
                // get Societies communities from android
                // TODO getCISes(JIDs[])
                // communities = android.getCommunites(String[] jids)
                communities = [{"description":"Open community. Join us.","id":"cis-2ea7bb44-31cc-466b-a0e8-3015a2ce852d.research.setcce.si", "name":"community 1","memberStatus":"You are the owner.","member":false,"owner":true,"pending":false}];
            }
            if (communities != null && communities.length > 0) {
                communitiesText = communities[0].name;
                for (var i=1; i<communities.length; i++) {
                    communitiesText += ", " + communities[i].name;
                }
            }
            $('#taskCommunities').text(communitiesText);
            $('#taskTags').text(task.tags);

            $('#commentForExecution').val(task.status == 'inprogress' || task.status == 'finished');
            
            $meetingCS = $('#meetingCS');
            $meetingCS.empty();
   			for (var i=0; i<task.spaces.length; i++) {
   				if (task.spaces[i] === undefined || task.spaces[i] === null) continue;
   				var spaceId = task.spaces[i].id;
   				var spaceName = task.spaces[i].name;
   				//$meetingCS.append('<option value='+task.spaces[i].id+'>'+task.spaces[i].name+'</option>');
   				$meetingCS.append('<option value='+spaceId+'>'+spaceName+'</option>');
			}
   			$meetingCS.selectmenu('refresh');
            
            showMeetings(task);
            showComments(task.id, false);
        }
    };

    var isNewTaskFormDirty = function() {
        if ($('#taskName').val() != "") return true;
        if ($('#taskDescription').val() != "") return true;
        if ($('#taskDate').val() != "") return true;
        var tc = $('#taskCommunity');
        if (tc.length == 1) {
        	if (tc.val() != null) {
        		return true;
        	}
        }
        if ($('#newTag').val() != "") return true;
        if ($('#taskTagsDiv a').length != 0) return true;
    	return false;
    }
    
    var deleteCurrentTask = function() {
        tasks.splice(currentTaskIndex, 1);
    };

    var currentTaskById = function(id) {
    	currentTaskIndex = -1;
        for (var i = 0; i < tasks.length; i++) {
        	if (tasks[i].id == id) {
        		currentTaskIndex = i;
        	}
        }
        return currentTaskIndex;
    };

    function fillCommunityComboBox() {
        var $taskCommunity;
        if (isSocietiesUser()) {
            $('#ctCommunities').hide();
            $taskCommunity = $('#taskCommunityJids');
        }
        else {
            $('#societiesCommunities').hide();
            $taskCommunity = $('#taskCommunity');
        }
        $taskCommunity.empty();
        for (var i = 0; i < communities.length; i++) {
            $taskCommunity.append('<option value=' + communities[i].id + '>' + communities[i].name + '</option>');
        }
        $taskCommunity.selectmenu('refresh');
    };

    var getAllCIS4User = function() {
        if (window.location.hostname === 'localhost') {
            communities =[{"description":"Open community. Join us.","id":"cis-2ea7bb44-31cc-466b-a0e8-3015a2ce852d.research.setcce.si", "name":"community 1","memberStatus":"You are the owner.","member":false,"owner":true,"pending":false}];
        }
        else {
            communities = JSON.parse(window.android.getSocietiesCommunities());
        }
        return communities;
    }
    var getCommunities4User = function(successFn) {
        if (isSocietiesUser()) {
            communities = getAllCIS4User();
            if (successFn !== undefined) {
                successFn();
            }
        }
        else {
            $.ajax({
                type: 'GET',
                url: '/rest/community/4user',
                //data: { 'id': id },
                success: function(result) {
                    communities = result;
                    if (successFn !== undefined) {
                        successFn();
                    }
//                    fillCommunityComboBox(communities);
                }
            });
        }
    };

    var setAddTaskButton = function() {
        if (communities === null || communities.length === 0) {
            $('#addTaskButton').hide();
        }
    };

    return {
        Task: function () {
            this.title = '';
            this.description = '';
            this.dueDate = new Date();
            this.spaceId = -1;
            this.tags = '';
        },
        
        addTask: function () {
        	console.log("addTask function");
            currentTaskIndex = -1;
            //clearNewTaskForm();
        	console.log("getCommunites4User");
            getCommunities4User(fillCommunityComboBox);
        },

        init: function() {
            loadTasks('interesting');
        },
        
        myTasks: function() {
            getCommunities4User(setAddTaskButton);
            loadTasks('my');
        },
        
        user: function() {
        	if (currentUser == null) {
        		getUser();
        	}
        	else {
        		$('#currentUser .ui-btn-text').text(currentUser.firstName+' '+currentUser.lastName);
        	}
        },

        displayTask: function(_mode) {
        	mode = _mode;
            var task = tasks[currentTaskIndex];

        	if (mode != 'new' && task === undefined) {	// view task directly not from tasks
            	// get task's id
    		   	var results = new RegExp('[\\?&]id=([^&#]*)').exec(window.location.href);
    		   	var id=-1;
    		   	if (results != null) {
    		   		id=results[1];
                	getTaskById(id);
    		   	}
    		   	else {
            		history.back();
            		return;
    		   	}
        	}
        	else {
                fillForm();
        	}
        },
        
        showTask: function(id) {
        	getTaskById(id);
        },

        saveTask: function() {
            var ctCommunities = $("#ctCommunities option:selected");
            var societiesCommunities = $("#societiesCommunities option:selected");
            if (ctCommunities.val() === undefined && societiesCommunities.val() === undefined) {
                toast("At least one community has to be selected.");
                $('#saveButton').show();
                return true;
            }
        	var newTag = $('#newTag').val().trim();
        	if (newTag != "") {
        		$("#tagWarning").text('Do you want to add: "'+newTag+'"?');
        		$("#popupDialog").popup("open");
                $('#saveButton').show();
        		return true;
        	}
        	// div to html
        	var tags = [];
        	$('#taskTagsDiv a').each(function(index) {
                var tag = $(this).text();
                tag = tag.replace(/^\s+|\s+$/g, '');
        		tags.push(tag);
        	});
        	$('#taskTags').val(JSON.stringify(tags));
        	postTask();
            return false;
        },

        cancelTask: function() {
        	if (isNewTaskFormDirty()) {
        		$("#cancelDialog").popup("open");
        		return false;
        	}
        	return true;
        },
        
        clearNewTaskForm: function(){
        	$('#taskName').val("");
        	$('#taskDescription').val("");
        	$('#taskDate').val("");
        	$('#newTag').val("");
        },
        
        executeTask: function() {
        	startExecution();
        },

        finalizeTask: function() {
        	taskFinalize();
        },

        postComment: function() {
        	_postComment();
        },

        setCurrentTask: function(index) {
            currentTaskIndex = index;
        },

        setCurrentTaskById: function(id) {
            return currentTaskById(id);
        },

        saveMeeting: function() {
        	postMeeting();
        },

        refreshViewTask: function() {
        	var task = tasks[currentTaskIndex];
        	getTaskById(task.id);
        },

        removeTask: function() {
            deleteCurrentTask();
            syncStorage();
            displayTasks();
            $.mobile.changePage('mobile.html', {
                transition: 'slide',
                reverse: true
            });
        }
    };
}();


function addNewTag() {
    var newTag = $('#newTag').val().trim();
    if (newTag == '')  {
    	return;
    }
    var tags = newTag.split(",");
    for (var i=0; i<tags.length; i++) {
        newTagButton(tags[i]);
    }

    $('#newTag').val("");
}

function newTagButton(newTag) {
	newTag = newTag.trim();
    var removeButton = function(button) {
        return function(event, data) {
        	event.preventDefault();
            button.remove();
        };
    };

    var taskDiv = $('#taskTagsDiv');
    var tagButton = $('<a>');
    //editLink.attr('href', '#');
    tagButton.attr('data-inline', 'true');
    tagButton.attr('data-icon', 'delete');
    tagButton.attr('data-iconpos', 'right');
    tagButton.attr('data-mini', 'true');
    tagButton.bind('tap', removeButton(tagButton));
    tagButton.append(newTag);
    taskDiv.append(tagButton);
    tagButton.button();
}

function formatDate(dateString) {
	var date = new Date(dateString);
	var min = date.getMinutes();
    return ''+date.getDate()+'.'+(date.getMonth()+1)+'.'+date.getFullYear()+' '+date.getHours()+':'+(min<=9?'0'+min:min);
} 

function postLike() {	  
    var form_data = $('#viewTaskForm').serialize();
    $.ajax({
      type: "POST",
      url: "/rest/like/task",
      data: form_data,
      error: function() {
      },
      success: function() {
    	  refreshTasks();
      },
      complete: function() {
      }
    });
}

function postCommentsLikes() {	  
    var form_data = $('#commentsForm').serialize();
    $.ajax({
      type: "POST",
      url: "/rest/like/comment",
      data: form_data,
      error: function() {
      },
      success: function() {
    	  //refreshTasks();
      },
      complete: function() {
      }
    });
}

function getLike() {
	var form_data = $('#viewTaskForm').serialize();
	$.ajax({
	  type: "GET",
	  url: "/rest/like/task",
	  data: form_data,
	  error: function() {
	  },
	  success: function(isLike) {
		  if (isLike) {
			  like();
		  }
		  else {
			  unlike();
		  }
	  },
	  complete: function() {
	  }
	});
}

function showNewsFeed() {
	$.ajax({
        type: "GET",
        url: '/rest/event',
        error: function() {
        },
        success: function(events) {
        var list = $('#eventList');
        list.empty();
        $("#eventList").append('<li data-role="list-divider" role="heading">News feed</li>');
        /*	      for (var i = 0; i<events.length; i++) {
            var newLi = $('<li style="white-space:normal;">');
            newLi.append(events[i].eventText);
            list.append(newLi);
        }*/

        for (var i = 0; i<events.length; i++) {
/*
        var editLink = $('<a>');
        editLink.attr('href', events[i].taskLink);
        editLink.attr('data-transition', 'slide');
        editLink.append('<h3 style="white-space:normal;">'+events[i].eventTextHTML+'</h3>');
*/

        var newLi = $('<li style="white-space:normal;">');
        newLi.append('<h3 style="white-space:normal;">'+events[i].eventTextHTML+'</h3>');
        list.append(newLi);
    }
	      
	      
	      list.listview('refresh', true);
	  },
	  complete: function() {
	  }
	});
}

function like() {
    $('#likeButton').hide();
    $('#likedButton').show();
}

function unlike() {
    $('#likedButton').hide();
    $('#likeButton').show();
}

function getSettings() {
	$.ajax({
		type: 'GET',
		url: '/rest/users/me',
		success: function(user) {
			$("#cbExecuteTask").attr("checked",user.notifications.executeTask).checkboxradio("refresh");
			$("#cbFinalizeTask").attr("checked",user.notifications.finalizeTask).checkboxradio("refresh");
			$("#cbLikeTask").attr("checked",user.notifications.likeTask).checkboxradio("refresh");
			$("#cbLikeComment").attr("checked",user.notifications.likeComment).checkboxradio("refresh");
			$("#cbNewTaskInCommunity").attr("checked",user.notifications.newTaskInCommunity).checkboxradio("refresh");
			$("#cbNewComment").attr("checked",user.notifications.newComment).checkboxradio("refresh");
			$("#cbJoinCommunityRequest").attr("checked",user.notifications.joinCommunityRequest).checkboxradio("refresh");
			if (user.admin === true) {
				$('#timeout').val(user.applicationSettings.chekInTimeOut/60000);
				$('#appSetiings').show();
			}
		}
	});
}

function submitSettings() {
    var form_data = $('#settingsForm').serialize();
    $.ajax({
      type: "POST",
      url: "/rest/users/settings",
      data: form_data,
      error: function() {
    	  alert('error');
      },
      success: function() {
          history.back();
      },
      complete: function() {
      }
    });	
}

function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function showNewMeetingForm() {
	$('#newMeetingList').show();
	$('#newMeetingButton').hide();
	$('#saveNewMeetingButton').show();
	$('#cancelNewMeetingButton').show();
}

function cancelNewMeeting() {
	// clear form
	$('#meetingSubject').val("");
	$('#meetingDescription').val("");
	$('#meetingCS').selectmenu('refresh', true);
	$('#taskStart').val("");
	$('#taskEnd').val("");

	$('#newMeetingList').hide();
	$('#newMeetingButton').show();
	$('#saveNewMeetingButton').hide();
	$('#cancelNewMeetingButton').hide();	
}

var toast = function(msg) {
	$("<div class='ui-loader ui-overlay-shadow ui-body-e ui-corner-all'><h3>" + msg + "</h3></div>")
	.css({
		display : "block",
		opacity : 0.90,
		position : "fixed",
		padding : "7px",
		"text-align" : "center",
		width : "270px",
		left : ($(window).width() - 284) / 2,
		top : $(window).height() / 2
	}).appendTo($.mobile.pageContainer).delay(1000).fadeOut(4000, function() {
		$(this).remove();
	});
};
/*
var trackPageView(page) {
  try {
	    _gaq.push( ['_trackPageview', event.target.id] );
	    console.log(page);
	  } catch(err) {
		  console.log(err);
	  }
};
*/
//------------

var selector = ':jqmData(role=page)';
$('body').on('pageinit', selector, function(event, data) {
    // initialize page
    var $page = $(this);
    //alert('init ' + $page.attr('id'));
}).on('pageshow', selector, function(e, data) {
    // showpage
    var $page = $(this);
    //alert('show ' + $page.attr('id'));
    //trackPageView($page.attr('id'));
});

$(document).on('pageshow', '#indexPage', function(event, data){       
	CrowdTaskingApp.user();
	if (typeof(android) !== "undefined") {
		$('#androidMenu').show();
		$('#logoutOption').hide();
	}
	//trackPageView(event);
/*	var android = getUrlVars()["android"];
	if (getUrlVars()["android"] == 'true') {
		$('#androidMenu').show();
	}*/
});


$(document).on('pageinit', '#mobilePage', function(event, data){       
//$('#mobilePage').live('pageinit', function() {
	refreshFunction = CrowdTaskingApp.init;
	CrowdTaskingApp.init();
});

$(document).on('pageshow', '#myTasksPage', function(event, data){
//$('#myTasksPage').live('pageinit', function() {
	refreshFunction = CrowdTaskingApp.myTasks;
	CrowdTaskingApp.myTasks();
	
    /*$('#addTaskButton').bind('tap', function(event, data) {
    	console.log("Add new task button pressed");
    	CrowdTaskingApp.addTask();
    });*/
});

$(document).on('pageinit', '#newsFeed', function(event, data){       
//$('#newsFeed').live('pageinit', function() {
	refreshFunction = showNewsFeed;
	showNewsFeed();
});

$(document).on('pageinit', '#formPage', function(event, data){       
//$('#formPage').live('pageinit', function() {
	refreshFunction = null;
    $('#cancelButton').bind('tap', function(event, data) {
        event.preventDefault();
        if (CrowdTaskingApp.cancelTask()) {
        	history.back();
        }
    });
    $('#cancelTaskSaveButton').bind('tap', function(event, data) {
        event.preventDefault();
    	$("#cancelDialog").popup("close");
        $('#saveButton').hide();
        CrowdTaskingApp.saveTask();
    });
    $('#cancelTaskNoButton').bind('tap', function(event, data) {
        event.preventDefault();
    	$("#cancelDialog").popup("close");
    	CrowdTaskingApp.clearNewTaskForm();
		if (typeof(android) !== "undefined") {
			window.android.goBack();
		}
		else {
	       	history.back();
		}
       	CrowdTaskingApp.myTasks();
    });
    $('#saveButton').bind('tap', function(event, data) {
        event.preventDefault();
        $('#saveButton').hide();
        CrowdTaskingApp.saveTask();
        //$('#saveButton').unbind('tap');
    });
    $('#commentButton').bind('tap', function(event, data) {
        event.preventDefault();
        CrowdTaskingApp.postComment();
    });
    $('#addTagButton').bind('tap', function(event, data) {
        event.preventDefault();
        addNewTag();
    });
    $('#addTagAndSaveButton').bind('tap', function(event, data) {
    	event.preventDefault();
    	$("#popupDialog").popup("close");
    	addNewTag();
    	CrowdTaskingApp.saveTask();
    });
    $('#dontAddTagAndSaveButton').bind('tap', function(event, data) {
    	event.preventDefault();
    	$("#popupDialog").popup("close");
    	$('#newTag').val('');
    	CrowdTaskingApp.saveTask();
    });
    $("#taskDate").scroller({preset:'date', dateFormat:'dd.mm.yyyy', timeFormat:'HH:ii', dateOrder: 'ddmmyy'});
});

$(document).on('pagebeforeshow', '#formPage', function(event, data){       
//$('#formPage').live('pagebeforeshow', function () {
	//console.log("formPage:pagebeforeshow");
	CrowdTaskingApp.addTask();
});

$(document).on('pageshow', '#viewTask', function(event, data){       
//$('#viewTask').live('pageshow', function () {
	refreshFunction = CrowdTaskingApp.refreshViewTask; 
    CrowdTaskingApp.displayTask('view');
});

$(document).on('pageinit', '#viewTask', function(event, data){       
//$('#viewTask').live('pageinit', function() {
    $('#likeButton').hide();
    $('#likedButton').hide();

    $('#finalizeTaskSaveButton').bind('tap', function(event, data) {
        event.preventDefault();
        $("#finalizeDialog").popup("close");
        $('#finalizeButton').hide();
        CrowdTaskingApp.finalizeTask();
    });
    $('#commentButton').bind('tap', function(event, data) {
        event.preventDefault();
        $('#commentButton').hide();
        CrowdTaskingApp.postComment();
    });
    $('#finalizeButton').bind('tap', function(event, data) {
        event.preventDefault();
        $("#finalizeDialog").popup("open");
    });
    $('#likeButton').bind('tap', function(event, data) {
        event.preventDefault();
        like();
        postLike();
    });
    $('#likedButton').bind('tap', function(event, data) {
        event.preventDefault();
        unlike();
        postLike();
    });
    $('#showOnPdButton').bind('tap', function(event, data) {
        event.preventDefault();
        showTaskOnPd(function() {
            $('#showOnPdButton').hide();
            $('#hideOnPdButton').show();
        });
    });
    $('#hideOnPdButton').bind('tap', function(event, data) {
        event.preventDefault();
        hideTaskOnPd(function() {
            $('#hideOnPdButton').hide();
            $('#showOnPdButton').show();
        });
    });
    $('#newMeetingButton').bind('tap', function(event, data) {
        event.preventDefault();
        showNewMeetingForm();
    });
    $('#saveNewMeetingButton').bind('tap', function(event, data) {
        event.preventDefault();
        $('#saveNewMeetingButton').hide();
        CrowdTaskingApp.saveMeeting();
    });
    $('#cancelNewMeetingButton').bind('tap', function(event, data) {
        event.preventDefault();
        cancelNewMeeting();
    });
    $("#taskStart").scroller({preset: 'datetime', dateFormat:'dd.mm.yyyy', timeFormat:'HH:ii', dateOrder: 'ddmmyy', timeWheels: 'HHii'});
    $("#taskEnd").scroller({preset: 'datetime', dateFormat:'dd.mm.yyyy', timeFormat:'HH:ii', dateOrder: 'ddmmyy', timeWheels: 'HHii'});
});

$(document).on('pageshow', '#executeTask', function(event, data){       
//$('#executeTask').live('pageshow', function () {
    CrowdTaskingApp.displayTask('execute');
});

$(document).on('pageinit', '#executeTask', function(event, data){       
//$('#executeTask').live('pageinit', function() {
	refreshFunction = null; 
    $('#executeButton').bind('tap', function(event, data) {
        event.preventDefault();
        $('#executeButton').hide();
        CrowdTaskingApp.executeTask();
    });
});

$(document).on('pageinit', '#settingsPage', function(event, data){       
//$('#settingsPage').live('pageinit', function () {
	refreshFunction = null;
	getSettings();
	
    $('#submitSettingsButton').bind('click', function(event, data) {
        event.preventDefault();
        $('#submitSettingsButton').unbind('tap');
        submitSettings();
    });
});

$(document).on('pageinit', '#communitiesPage', function(event, data){       
//$('#communitiesPage').live('pageinit', function() {
	refreshFunction = Community.loadCommunities; 
    /*$('#addCommunityButton').bind('tap', function(event, data) {
        event.preventDefault();
        $('#addCommunityButton').unbind('tap');
    	Community.setMode('new');
    });*/
	Community.loadCommunities();
});

$(document).on('pageinit', '#editCommunityPage', function(event, data){       
//$('#editCommunityPage').live('pageinit', function() {
	refreshFunction = null;
	$('#saveButton').bind('tap', function(event, data) {
	    event.preventDefault();
        $('#saveButton').unbind('tap');
	    Community.createCommunity();
	});
	//Community.view();
});

$(document).on('pagechange', '#editCommunityPage', function(event, data){       
//$('#editCommunityPage').live('pagechange', function() {
	Community.view();
});

$(document).on('pageshow', '#editCommunityPage', function(event, data){       
//$('#editCommunityPage').live('pageshow', function() {
	Community.edit();
});

$(document).on('pageinit', '#viewCommunity', function(event, data){       
//$('#viewCommunity').live('pageinit', function() {
	refreshFunction = Community.view;
	$('#editCommunityButton').bind('tap', function(event, data) {
	    event.preventDefault();
	    Community.setMode('edit');
		$.mobile.changePage('/community/edit');
	});
	$('#saveCSButton').bind('tap', function(event, data) {
	    event.preventDefault();
	    Community.saveCS();
	});
	$('#cancelCSButton').bind('tap', function(event, data) {
	    event.preventDefault();
	    $('#popupEditCS').popup("close");
	});
	$('#addCSButton').bind('tap', function(event, data) {
	    event.preventDefault();
	    Community.editSpace();
	});
	$('#joinCommunityButton').bind('tap', function(event, data) {
	    event.preventDefault();
	    Community.joinCommunity();
	});
	$('#leaveCommunityButton').bind('tap', function(event, data) {
	    event.preventDefault();
	    Community.leaveCommunity();
	});
    $('#confirmUserButton').bind('tap', function(event, data) {
    	event.preventDefault();
    	$("#confirmPopup").popup("close");
    	Community.confirmUser();
    });
    $('#rejectUserButton').bind('tap', function(event, data) {
    	event.preventDefault();
    	$("#confirmPopup").popup("close");
    	Community.rejectUser();
    });
});

$(document).on('pageshow', '#viewCommunity', function(event, data){       
//$('#viewCommunity').live('pageshow', function () {
	Community.view();
});