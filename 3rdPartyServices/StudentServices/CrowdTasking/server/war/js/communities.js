var Community = function () {
    var communities = [];
    var societiesCommunities = [];
    var collaborativeSpaces = [];
    var societiesCommunity = false;
    var currentIndex = -1;
    var _mode = 'new';
    var TEST_HOST = "localhost1";

    function listCommunities(_communities, tapHandler, list) {
        console.log("listCommunities - _communities.length=" + _communities.length);
        for (var index = 0; index < _communities.length; index++) {
            var community = _communities[index];
            var editLink = $('<a>');
            editLink.attr('href', '/community/view');
            editLink.attr('data-transition', 'slide');
            editLink.bind('tap', tapHandler(index));
            editLink.append('<h3 style="white-space:normal;">' + community.name + '</h3>');
            editLink.append('<p>' + community.memberStatus + '</p>');

            var newLi = $('<li style="white-space:normal;">');
            newLi.append(editLink);
            list.append(newLi);
        }
    }

    var displayCommunities = function () {
        var createTapHandler = function (currentIndex) {
            return function (event, data) {
                Community.setCurrentIndex(currentIndex);
                societiesCommunity = false;
            };
        };

        var createSocietiesTapHandler = function (currentIndex) {
            return function (event, data) {
                Community.setCurrentIndex(currentIndex);
                societiesCommunity = true;
            };
        };

        var list = $('#communitiesList');
        list.empty();

        list.append('<li data-role="list-divider" role="heading">Communities</li>');
        if (isSocietiesUser()) {
            listCommunities(societiesCommunities, createSocietiesTapHandler, list);
        }
        else {
            listCommunities(communities, createTapHandler, list);
        }
        list.listview('refresh', true);
        if (isSocietiesUser()) {
            $('#addCommunityButton').hide();
        }
    };

    var getScope = function () {
        if (typeof(android) !== "undefined") {
            return window.android.getScope();
        }
        if (isTestHost()) {
            console.log("on " + TEST_HOST);
            return "HWU";
        }
        return null;
    }

    var isSocietiesUser = function () {
        if (typeof(android) !== "undefined") {
            return window.android.isSocietiesUser();
            //return (android.socUser);
        }
        if (isTestHost()) {
            console.log("on " + TEST_HOST);
            return true;
        }
        console.log("not on android");
        return false;
    };

    var getSocietiesUser = function () {
        if (isTestHost()) {
            return {"foreName": "Arthur", "userId": "arthur.societies.local.macs.hw.ac.uk"};
        }
        else {
            return JSON.parse(window.android.getSocietiesUser());
        }
    };

    var isTestHost = function () {
        return (window.location.hostname === TEST_HOST);
    };

    var getCommunities = function (successFn) {
        console.log("getCommunities");
        if (isSocietiesUser()) {
            console.log("societies user");
            if (isTestHost()) {
                societiesCommunities = [
                    {"description": "Open community. Join us.", "jid": "cis-2ea7bb44-31cc-466b-a0e8-3015a2ce852d.research.setcce.si", "name": "community 1", "memberStatus": "You are the owner.", "member": false, "owner": true, "pending": false, "spaces": [
                        {"id": 30, "name": "space2", "urlMapping": "space2", "symbolicLocation": "space2"}
                    ]}
                ];
            }
            else {
                societiesCommunities = JSON.parse(window.android.getSocietiesCommunities());
            }
            successFn();
        }
        else {
            console.log("NOT societies user");
            $.ajax({
                type: 'GET',
                url: '/rest/community/browse',
                success: function (result) {
                    communities = result;
                    successFn();
                }
            });
        }
    };

    var getCommunityById = function (communityId) {
        $.ajax({
            type: 'GET',
            url: '/rest/community/get',
            data: { 'communityId': communityId },
            success: function (community) {
                communities = [];
                communities.push(community);
                currentIndex = 0;

                $('#backButton').bind('tap', function (event, data) {
                    event.preventDefault();
                    window.location.replace('/menu');
                });
                viewCommunity();
            }
        });
    };

    var getUsers = function (successFn) {
        $.ajax({
            type: 'GET',
            url: '/rest/users/all',
            error: function (error) {
                toast(error.responseText);
            },
            success: function (result) {
                successFn(result);
            }
        });
    };

    var postCommunity = function (successFn) {
        console.log("post community");
        var form_data = $('#editCommunityForm').serialize();
        console.log("form data:" + form_data);
        $.ajax({
            type: "POST",
            url: "/rest/community/create",
            data: form_data,
            error: function (error) {
                toast(error.responseText);
            },
            success: function (response) {
                if (isSocietiesUser()) {
                    //var community = jQuery.parseJSON(response);
                    if (!isTestHost()) {
                        window.android.setCommunitySpaces(response);
                        societiesCommunities = JSON.parse(window.android.getSocietiesCommunities());
                    }
                }
                successFn();
            },
            complete: function () {
            }
        });
    };

    var postEditCommunity = function (action, successFn) {
        var form_data = $('#editSpaceForm').serialize();
        var urlString = "/rest/community/" + action + "/";
        $.ajax({
            type: "POST",
            url: urlString,
            data: form_data,
            error: function (error) {
                toast(error.responseText);
            },
            success: function () {
                successFn();
            },
            complete: function () {
            }
        });
    };

    var postSpace = function (completeFn) {
        var form_data = $('#editSpaceForm').serialize();
        $.ajax({
            type: "POST",
            url: "/rest/space",
            data: form_data,
            error: function (error) {
                toast(error.responseText);
            },
            success: function () {
                $("#popupEditCS").popup("close");
                completeFn();
            },
            complete: function () {
            }
        });
    };

    var getSpaces = function (completeFn, communitySpaces) {
        var scope = getScope();
        $.ajax({
            type: "GET",
            url: "/rest/space",
            data: { 'scope': scope },
            error: function (error) {
                toast(error.responseText);
            },
            success: function (result) {
                collaborativeSpaces = result;
                completeFn(result, communitySpaces);
            },
            complete: function () {
            }
        });
    };

    function getSelectedCommunity() {
        var community;
        if (societiesCommunity) {
            community = societiesCommunities[currentIndex];
        }
        else {
            community = communities[currentIndex];
        }
        return community;
    }

    var viewCommunity = function () {
        var list;
        var community = getSelectedCommunity();
        $('#name').text(community.name);
        $('#description').text(community.description);
        $('#memberStatus').text(community.memberStatus);

        if (community.canAddCS) {
            console.log("user can add CS: " + community.canAddCS);
            $('#addCSButton').show();
            $('#spaceDetails').show();
            $('#cancelCSButton').show();
            $('#saveCSButton').show();
        }
        if (!community.owner) {
            $('#editCommunityButton').hide();
        }
        else {
            var picUrl;
            list = $('#requestList');
            list.empty();
            list.append('<li data-role="list-divider" role="heading">Requests:</li>');
            if (community.requests !== undefined) {
                for (var i = 0; i < community.requests.length; i++) {
                    var request = community.requests[i];
                    var newLi = $('<li>');
                    picUrl = request.picUrl;
                    if (picUrl == undefined) {
                        picUrl = '/images/pic' + (Math.floor(Math.random() * 4) + 1) + '.png';
                    }
                    newLi.append('<a href="#"><img src="' + picUrl + '" style="left:30px; top:25px;"><h2>' + request.username + '</h2></a>');
                    newLi.append('<a href="javascript:confirm(' + request.id + ')" data-rel="popup" data-position-to="window" data-transition="pop">Approve user</a>');
                    list.append(newLi);
                }
                list.listview('refresh', true);
                list.show();
            }
            else {
                list.hide();
            }
        }
        if ((community.owner || community.member) && !isSocietiesUser()) {
            list = $('#memeberList');
            list.empty();
            list.append('<li data-role="list-divider" role="heading">Members:</li>');
            for (var i = 0; i < community.members.length; i++) {
                var member = community.members[i];
                var newLi = $('<li data-role="fieldcontain">');
                newLi.append('<h3 style="white-space:normal;">' + member.username + '</h3>');
                picUrl = member.picUrl;
                if (picUrl == undefined) {
                    picUrl = '/images/pic' + (Math.floor(Math.random() * 4) + 1) + '.png';
                }
                newLi.append('<img src="' + picUrl + '" class="ui-li-icon" style="left:12px; top:22px;">');
                list.append(newLi);
            }
            list.listview('refresh', true);
            list.show();
        }

        var spaces = "";
        if (community.spaces !== undefined && community.spaces != null && community.spaces.length > 0) {
//    		spaces = community.spaces[0].name;
            // SCT-32 Remove link from CS, leave for admin
            spaces = community.canAddCS ? getCSEditLink(community.spaces[0], 0) : community.spaces[0].name;
            for (var i = 1; i < community.spaces.length; i++) {
//    			spaces += ", " + community.spaces[i].name;
                // SCT-32 Remove link from CS
                spaces += ", ";
                spaces += community.canAddCS ? getCSEditLink(community.spaces[i], i) : community.spaces[i].name;
            }
        }
        $('#csNames').html(spaces);
        $('#communityId').val(community.id);
        if (!community.member && !community.pending && !isSocietiesUser()) {
            $('#joinCommunityButton').show();
        }
        else {
            if (!community.owner && !community.pending && !isSocietiesUser()) {
                $('#leaveCommunityButton').show();
            }
        }
    };

    var getCSEditLink = function (space, index) {
        return '<a href="javascript:Community.editSpace(' + index + ')">' + space.name + '</a>';
    };

    function fillSpacesCombo(spaces, communitySpaces) {
        var $spacesCombo = $('#spacesCombo');
        $spacesCombo.empty();
        for (var i = 0; i < spaces.length; i++) {
            var spaceId = spaces[i].id;
            var spaceName = spaces[i].name;
            var selected = "";
            if (communitySpaces !== undefined && communitySpaces.length !== undefined) {
                for (var j = 0; j < communitySpaces.length; j++) {
                    var comSpace = communitySpaces[j];
                    if (spaceId === comSpace.id) {
                        selected = "selected";
                        break;
                    }
                }
            }
            $spacesCombo.append('<option value=' + spaceId + ' ' + selected + '>' + spaceName + '</option>');
            console.log("space:" + spaceName + ", id:" + spaceId);
        }
        $spacesCombo.selectmenu('refresh');
    }

    var editCommunity = function () {
        if (_mode == 'new') {
            currentIndex = -1;
            $('#communityId').val('');
            $('#name').val('');
            $('#description').text('');
            //getUsers(showUsers);
            getSpaces(fillSpacesCombo);
            return;
        }
        if ($('#status').val() === 'editing') {
            return;
        }
        else {
            $('#status').val("editing");
        }
        var community = getSelectedCommunity();
//        var community = communities[currentIndex];
        $('#name').val(community.name);
        $('#description').text(community.description);
        if (community.id !== undefined) {
            $('#communityId').val(community.id);
//            $('#name').textinput('enable');
//            $('#description').textinput('enable');
        }
        if (community.jid !== undefined) {
//            $('#name').textinput('disable');
            $('#sctCommunity').hide();
            $('#cisCommunity').show();
            $('#txtName').text(community.name);
            $('#txtDescription').text(community.description);
            $('#communityJid').val(community.jid);
            var societiesUser = getSocietiesUser();
            console.log("societiesUser:" + societiesUser);
            var ownerJid = societiesUser.userId;
            console.log("societiesUser.userId:" + societiesUser.userId);
            console.log("ownerJid:" + ownerJid);
            $('#ownerJid').val(ownerJid);
        }
        getSpaces(fillSpacesCombo, community.spaces);
        //getUsers(showUsers);
    };

    var showUsers = function (users) {
        var members = [];
        if (currentIndex > -1) {
            members = communities[currentIndex].members;
        }
        var $usersDiv = $('#usersDiv');

        for (var i = 0; i < users.length; i++) {	// ne prikažemo 'owner-ja'
            var user = users[i];
            if (user.me) continue;
            var newDiv = $('<div>');
            var checked = isMember(user, members) ? 'checked="checked"' : '';
            newDiv.append('<input type="checkbox" name="members" value="' + user.id + '" id="' + user.id + '" class="custom" ' + checked + '>');
            //newDiv.append('<input type="checkbox" name="members" value="'+user.id+'" id="cb'+user.id+'" class="custom">');
            newDiv.append('<label for="' + user.id + '" data-corners="true" data-shadow="false" data-iconshadow="true"' +
                'data-wrapperels="span" data-icon="checkbox-off" data-theme="c"><span class="ui-btn-text">' +
                user.username + '</span></label>');
            $usersDiv.append(newDiv);
        }
        $("input[type='checkbox']").checkboxradio();
    };

    var isMember = function (user, members) {
        for (var i = 0; i < members.length; i++) {
            if (user.id == members[i].id)
                return true;
        }
        return false;
    };

    return {
        loadCommunities: function () {
            getCommunities(displayCommunities);
        },

        createCommunity: function () {
            var onCommunityCreated = function () {
                history.back();
                var onGotCommunitites = function () {
                    if (_mode === 'new') {
                        //_mode='view';
                    }
                    else {
                        history.back();
                    }
                    displayCommunities();
                };
                getCommunities(onGotCommunitites);
            };
            postCommunity(onCommunityCreated);
        },

        view: function () {
            var results = new RegExp('[\\?&]id=([^&#]*)').exec(window.location.href);
            var id = -1;
            if (results != null) {
                id = results[1];
                getCommunityById(id);
            }
            else {
                if (currentIndex == -1) {
                    history.back();
                    return;
                }
                viewCommunity();
            }
        },

        edit: function () {
            if (_mode !== 'new' && currentIndex === -1) {
                history.back();
                return;
            }
            editCommunity();
        },

        setCurrentIndex: function (index) {
            currentIndex = index;
        },

        setMode: function (mode) {
            _mode = mode;
        },

        saveCS: function () {
            var complete = function () {
                getCommunities(viewCommunity);
            };
            postSpace(complete);
        },

        editSpace: function (index) {
            if (index !== undefined) {
                var community = getSelectedCommunity(community);
                var space = community.spaces[index];
                $('#spaceId').val(space.id);
                $('#spaceName').val(space.name);
                $('#urlMapping').val(space.urlMapping);
                $('#symbolicLocation').val(space.symbolicLocation);
            }
            else {
                $('#spaceId').val('0');
                $('#spaceName').val('');
                $('#urlMapping').val('');
                $('#symbolicLocation').val('');
            }
            $("#popupEditCS").popup("open");
        },

        confirmUser: function () {
            postEditCommunity("confirm", function () {
                getCommunities(viewCommunity);
            });
        },

        rejectUser: function () {
            postEditCommunity("reject", function () {
                getCommunities(viewCommunity);
            });
        },

        joinCommunity: function () {
            var onCommunityEdited = function () {
                $('#joinCommunityButton').hide();
                getCommunities(viewCommunity);
            };
            postEditCommunity("request", onCommunityEdited);
        },

        leaveCommunity: function () {
            var onCommunityEdited = function () {
                $('#leaveCommunityButton').hide();
                $('#joinCommunityButton').show();
                getCommunities(viewCommunity);
            };
            postEditCommunity("leave", onCommunityEdited);
        }
    };
}();

function copyToClipboard(action) {
    if (typeof(android) !== 'undefined') {
        window.android.share($('#symbolicLocation').val(), action);
    }
    else {
        if (action === 'PD') {
            //var pdUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '')+'/cs/'+$('#urlMapping').val();
            window.prompt('Copy to clipboard: Ctrl+C, Enter', location.origin + '/cs/' + $('#symbolicLocation').val());
        }
    }
}

function confirm(memberId) {
    $('#memberId').val(memberId);
    $("#confirmPopup").popup("open");
}