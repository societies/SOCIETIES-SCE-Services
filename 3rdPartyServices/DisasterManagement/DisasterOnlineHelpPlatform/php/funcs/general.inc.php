<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */

/* ---------------------------------------------------------------------------- */
/* --- Prepare an input for sql - trimming and escaping ----------------------- */
/* ---------------------------------------------------------------------------- */
function prepare_input_for_sql($input)
{
	return mysql_real_escape_string(trim($input));
}

/* ---------------------------------------------------------------------------- */
/* --- Sets the language for the current user --------------------------------- */
/* ---------------------------------------------------------------------------- */
function set_user_language()
{

	global $CONFIG_LANG_DEFAULT, $_POST, $_SESSION;

	// --- no language is set ---
	if (!isset($_SESSION['user_language']))
	{
		$_SESSION['user_language'] =  $CONFIG_LANG_DEFAULT;
	}
	// --- login-user ---
	else if (isset($_SESSION['user_id']) )
	{
		// --- get user lang ---
		$request = "SELECT l.name as lang FROM languages AS l, users AS u WHERE u.id = '". prepare_input_for_sql($_SESSION['user_id']) ."' AND l.id = u.language";
		$result = mysql_query($request);
		if ($row = mysql_fetch_object($result))
		{
			$_SESSION['user_language'] = $row->lang;
		}
		else
		{
			$_POST['errors'] = "Can't fetch language of user " . $_SESSION['user_id'];
			return;
		}

	}
	// --- annonymous-user ---
	else if (!isset($_GET['user_id']) && isset($_GET['set_language']))
	{
		$_SESSION['user_language'] = $_GET['set_language'];
	}

}


/* ---------------------------------------------------------------------------- */
/* --- Gets the langs bricks of the current user ------------------------------ */
/* ---------------------------------------------------------------------------- */
function get_user_lang_bricks()
{
	global $CONFIG_LANG_DEFAULT, $_POST, $_SESSION;

	// --- get bricks ---
	$lines = file('./lang/' . $_SESSION['user_language'] . '.lang');
	foreach ($lines as $line_num => $line)
	{
		if  (strpos($line,"=")!==false)
		{
			$l_a = split("=", $line);
			$return_array[trim($l_a[0])] = trim($l_a[1]);
		}
	}

	return $return_array;
}


/* ---------------------------------------------------------------------------- */
/* --- Configure language for HTTP software - not for RPC SERVER -------------- */
/* ---------------------------------------------------------------------------- */
if ($INC_SELF_PHP == "/societies/index.php")
{
	set_user_language();
	$LANG_BRICKS = get_user_lang_bricks();
}

/* ---------------------------------------------------------------------------- */
/* --- Gets the langs bricks of the current user ------------------------------ */
/* ---------------------------------------------------------------------------- */
function login_user($myusername, $mypassword)
{
	global $_POST, $_SESSION;

	// To protect MySQL injection (more detail about MySQL injection)
	$myusername = stripslashes($myusername);
	$mypassword = stripslashes($mypassword);

	$result=mysql_query($sql);

	// Mysql_num_row is counting table row
	$count=mysql_num_rows($result);
	// If result matched $myusername and $mypassword, table row must be 1 row

	if( $count==1 && $row = mysql_fetch_object($result))
	{
		// --- register user session var ---
		$_SESSION['user_id'] = $row->id;
		//$_SESSION['user_email'] = $myusername;
		$_GET['site'] = "tickets_list";

		// --- get permissions and set permission session var ---
		$sql2="SELECT * FROM groups WHERE id='" . $row->group . "'";
		$result2=mysql_query($sql2);
		if($row2 = mysql_fetch_assoc($result2))
		{
			$_SESSION['permissions'] = $row2;
			unset($_SESSION['permissions']['id']);
			unset($_SESSION['permissions']['name']);
		}

	}
	else
	{
		$_POST['error'] = "Wrong Username or Password!";
	}


}


/* ---------------------------------------------------------------------------- */
/* --- Shows infos and errors ------------------------------------------------- */
/* ---------------------------------------------------------------------------- */
function info_and_error_display()
{
	global $_POST;

	if (isset($_POST['error']))
	{
		echo "<div id='display_outer_frame'><div id='display_errors'>" . $_POST['error'] . "</div></div>";
	}
	if (isset($_POST['info']))
	{
		echo "<div id='display_outer_frame'><div id='display_infos'>" . $_POST['info'] . "</div></div>";
	}
}

/* ---------------------------------------------------------------------------- */
/* --- Escapes a string ------------------------------------------------------- */
/* ---------------------------------------------------------------------------- */
function escape_str($str)
{
	return $str;
}


/* ---------------------------------------------------------------------------- */
/* --- Checks if an email adress is already in DB ----------------------------- */
/* ---------------------------------------------------------------------------- */
function is_email_already_exists($email)
{
	// fetching data
	$sql = "SELECT id FROM users WHERE  email='" . $email ."'";
	$res = mysql_query($sql);

	if ( mysql_num_rows($res))
	{
		return true;
	}
	else
	{
		return false;
	}

}


/* ---------------------------------------------------------------------------- */
/* --- Checks user data for db ------------------------------------------------ */
/* --- First boolean param: Password is allowed to be empty (no new passwd) --- */
/* ---------------------------------------------------------------------------- */
function check_user_data_for_db($passwd_allowed_empty, $language, $group, $last_name, $first_name, $password, $repassword, $email)
{
	$error_string = NULL;

	// --- check first name ---
	if ($first_name == "")
	{
		$error_string .= "<br>First name is empty!";
	}

	// --- check last name ---
	if ($last_name == "")
	{
		$error_string .= "<br>Last name is empty!";
	}

	// --- check email ---
	if ($email == "" || !filter_var($email, FILTER_VALIDATE_EMAIL))
	{
		$error_string .= "<br>Email is empty or invalid!";
	}

	return $error_string;
}


/* ---------------------------------------------------------------------------- */
/* --- Checks ticket data for db ---------------------------------------------- */
/* ---------------------------------------------------------------------------- */
function check_ticket_data_for_db($short_descr, $descr)
{
	$error_string = NULL;

	// --- check shortdescr ---
	if ($short_descr == "")
	{
		$error_string .= "<br>Title is empty!";
	}

	// --- check description ---
	if ($descr == "")
	{
		$error_string .= "<br>Description is empty!";
	}

	return $error_string;
}


/* ---------------------------------------------------------------------------- */
/* --- Checks ticket answer for db -------------------------------------------- */
/* ---------------------------------------------------------------------------- */
function check_ticket_answer_data_for_db($answer)
{
	global $LANG_BRICKS;

	$error_string = NULL;

	// --- check answer ---
	if ($answer == "")
	{
		$error_string .= "<br>Answer is empty!";
	} else if($answer == $LANG_BRICKS['TICKETS_SHORT_DESCRIPTION'])
	{
		$error_string .= "<br>Default answer given";
	}

	return $error_string;
}


/* ---------------------------------------------------------------------------- */
/* --- Deletes an image of a ticket ------------------------------------------- */
/* ---------------------------------------------------------------------------- */
/*
 function delete_image_from_ticket ($id)
 {

//TODO: Implement it for the new mechnism
// --- delete pic of ticket ---
$sql = "Select image from tickets WHERE id='" . prepare_input_for_sql($id) . "'";
$result = mysql_query($sql);
if ($row = mysql_fetch_object($result))
{
if ($row->image != '') {
unlink('./images/uploads/' . $row->image);
}
}

}*/


/* ---------------------------------------------------------------------------- */
/* --- Converts a string to timestamp ----------------------------------------- */
/* ---------------------------------------------------------------------------- */
function getTimestamp()
{

	$timestamp = mktime(0,0,0,11,22,2004);
}


/* ---------------------------------------------------------------------------- */
/* --- Converts timedatepicker to mysql date ---------------------------------- */
/* ---------------------------------------------------------------------------- */
function convertToMysqlDate($date)
{
	// '03/09/2012 9:00 PM' -> 2012-03-09 21:00:00
	return date("Y-m-d H:i:s" , strtotime($date));
}


/* ---------------------------------------------------------------------------- */
/* --- Converts mysql to timedatepicker date ---------------------------------- */
/* ---------------------------------------------------------------------------- */
function convertToTimeDatePickerDate($date)
{
	// 2012-03-09 21:00:00 ->'03/09/2012 9:00 PM'
	return date("m/d/Y h:i a" , strtotime($date));
}


/* ---------------------------------------------------------------------------- */
/* --- If tag is in DB -> return id . Else create new entry + return new id --- */
/* ---------------------------------------------------------------------------- */
function getTagId($t)
{
	// query to check if tag is already available
	$sql ="SELECT id FROM tags WHERE name='" . prepare_input_for_sql($t) . "'";
	$tag_res = mysql_query($sql);

	// tag available
	if($tag_row = mysql_fetch_object($tag_res))
	{
		//echo "tags:"  . $tag_row->id;
		return $tag_row->id;
	}
	// tag not available
	else
	{
		$sql ="INSERT INTO tags (name) VALUES ('".  prepare_input_for_sql($t) ."')";
		mysql_query($sql);
		return mysql_insert_id();
	}
}


/* ---------------------------------------------------------------------------- */
/* --- Insert tags into db - delete old tags of ticket before! ------------------- */
/* ---------------------------------------------------------------------------- */
function insertTagsForTicket($ticket_id, $tags)
{
	// delete all tags of ticket
	$sql = "DELETE FROM nm_tickets_tags WHERE ticket_id = '". prepare_input_for_sql($ticket_id) ."'";
	//echo $sql;
	mysql_query($sql);



	// prepare array
	$tags_array = explode(",", $tags);
	$tags_array = array_map('trim', $tags_array);
	$tags_array = array_unique($tags_array);
	//print_r($tags_array);

	// insert tags
	foreach ($tags_array as $t)
	{
		//$t = trim($t);

		if ($t == "") {
			continue;
		}

		$tag_id = getTagId($t, $ticket_id);

		// tag available
			
		// insert entry at tag-ticket table
		$sql ="INSERT INTO nm_tickets_tags (ticket_id, tag_id) VALUES ('".  prepare_input_for_sql($ticket_id) ."', '".  prepare_input_for_sql($tag_id) ."')";
		mysql_query($sql);


	}
}


/* ---------------------------------------------------------------------------- */
/* --- Insert tags into db - delete old tags of ticket before! ------------------- */
/* ---------------------------------------------------------------------------- */
function insertTagsForUser($user_id, $tags)
{
	// delete all tags of user
	$sql = "DELETE FROM nm_users_tags WHERE user_id = '". prepare_input_for_sql($user_id) ."'";
	//echo $sql;
	mysql_query($sql);

	// prepare array
	$tags_array = explode(",", $tags);
	$tags_array = array_map('trim', $tags_array);
	$tags_array = array_unique($tags_array);
	//print_r($tags_array);

	// insert tags
	foreach ($tags_array as $t)
	{
		//$t = trim($t);

		if ($t == "") {
			continue;
		}

		$tag_id = getTagId($t, $user_id);

		// tag available
			
		// insert entry at tag-user table
		$sql ="INSERT INTO nm_users_tags (user_id, tag_id) VALUES ('".  prepare_input_for_sql($user_id) ."', '".  prepare_input_for_sql($tag_id) ."')";
		mysql_query($sql);


	}
}


/* ---------------------------------------------------------------------------- */
/* --- Checks language data for db -------------------------------------------- */
/* ---------------------------------------------------------------------------- */
function check_lang_data_for_db($language)
{
	global $LANG_BRICKS;

	$error_string = NULL;

	// --- check shortdescr ---
	if ($language == "")
	{
		$error_string .= "<br>Language is empty!";
	}

	return $error_string;
}


/* ---------------------------------------------------------------------------- */
/* --- sees, if a substring is found in another string ------------------------ */
/* ---------------------------------------------------------------------------- */
function check_language_file($language)
{
	global $CONFIG_LANG_DIR, $LANG_BRICKS;

	if (file_exists($CONFIG_LANG_DIR . $language . ".lang"))
	{
		return $LANG_BRICKS["LANGUAGES_YES"];
	}
	else
	{
		return $LANG_BRICKS["LANGUAGES_NO"];
	}

}


/* ---------------------------------------------------------------------------- */
/* --- SANDBOX - Tweeting a new Request --------------------------------------- */
/* ---------------------------------------------------------------------------- */
function tweetout($requestNr, $summary)
{
	try {
		$conn->connect();
		$conn->processUntil('session_start');
		$conn->presence();
		$conn->message("societies_feed@twitter.tweet.im", "Request $requestNr: $summary");
		$conn->disconnect();
	} catch(XMPPHP_Exception $e) {
		die($e->getMessage());
	}
}

/* ---------------------------------------------------------------------------- */
/* --- Builds the HTML output for the page view ------------------------------- */
/* ---------------------------------------------------------------------------- */
function buildPageView($count_items)
{
	global $_GET,$_REQUEST, $ITEMS_PER_PAGE;

	$output_str='';
	$j=1;

	if (!isset($_GET['page'])) {
		$_GET['page']=1;
	}

	for ($i=1; $i <= $count_items; $i=$i+$ITEMS_PER_PAGE)
	{
		//
		$get_str = '';
		$get_str .= (isset($_REQUEST['search']) ? '&search='. $_REQUEST['search'] : '' );
		$get_str .= (isset($_REQUEST['list_only_suggestions']) ? '&list_only_suggestions=true' : '' );
		$get_str .= (isset($_REQUEST['list_closed_tickets']) ? '&list_closed_tickets=true' : '' );
		$get_str .= (isset($_REQUEST['tag_search']) ? '&tag_search=' . $_REQUEST['tag_search'] : '' );

		$output_str .= "<a href='./index.php?page=". $j . $get_str . "'>";
		if ($_GET['page'] == $j)
		{
			$output_str .= "<u>" . $j . "</u>&nbsp;";
		}
		else
		{
			$output_str .= $j . "&nbsp;";
		}
		$output_str .= "</a>";
		$j = $j +1;
	}

	return $output_str;
}

/* ---------------------------------------------------------------------------- */
/* --- Checks if a specified item is within a page range ---------------------- */
/* ---------------------------------------------------------------------------- */
function isItemInPageRange($counter_current_ticket, $page)
{
	global $ITEMS_PER_PAGE;

	if (!isset($page) || !is_numeric($page)) {
		$page=1;
	}

	$min_page = $ITEMS_PER_PAGE *($page- 1) + 1;
	$max_page = $ITEMS_PER_PAGE * $page;

	if ($min_page > $counter_current_ticket || $max_page < $counter_current_ticket)
	{
		return false;
	}
	else
	{
		return true;
	}
}

/* ---------------------------------------------------------------------------- */
/* --- Notifies all users that a new request was created ---------------------- */
/* ---------------------------------------------------------------------------- */
function sendMailToUsers($ticket_id)
{
	// blocker
	//return;

	// --- get ticket infos ---
	$sql = "SELECT t.*, t_t.name AS ticket_type, t_s.name AS ticket_status, u.first_name AS reporter_first_name, u.last_name AS reporter_last_name, u.institute AS reporter_institute, count(a.user_id) as count_answers
	FROM tickets AS t
	LEFT JOIN users AS u ON t.reporter_id = u.id
	LEFT JOIN tickets_states AS t_s ON t.ticket_status_id = t_s.id
	LEFT JOIN tickets_type AS t_t ON t.ticket_type_id = t_t.id
	LEFT JOIN nm_users_tickets AS a ON a.ticket_id = t.id
	WHERE t.id='" .  prepare_input_for_sql($ticket_id) . "'
	GROUP BY t.id ";

	$res = mysql_query($sql);
	if ($row = mysql_fetch_object($res))
	{
		// --- send mails --- TODO
		$sql1 = "SELECT id, email, notify_on_new_ticket FROM users";
		$res1 = mysql_query($sql1);

		$text = "<html>";
		$text .= "<b>A new request was created:</b><br><br>";
		$text .= "<b>Title: </b>" . $row->short_descr . "<br>";
		$text .= "<b>Reporter: </b>" . $row->reporter_first_name . " " . $row->reporter_last_name . "<br>";
		$text .= "<b>Institute: </b>" . ($row->reporter_institute != '' ? $row->reporter_institute : '-') . "<br>";

		// tags
		$sql_tags = "SELECT  ta.id, ta.name "
		. "FROM nm_tickets_tags AS nm "
		. "LEFT JOIN tickets AS t ON nm.ticket_id = t.id "
		. "LEFT JOIN tags AS ta ON nm.tag_id = ta.id "
		. "WHERE t.id='" .  $row->id . "'"
		. "ORDER BY ta.name ASC";
		$tag_res = mysql_query($sql_tags);
		$tag_str = '  ';
		while($tag_row = mysql_fetch_object($tag_res))
		{
			$tag_str .=  $tag_row->name . ", ";
		}

		$tag_str = substr($tag_str, 0, -2);
		if ($tag_str != '')
		{
			$text .= "<b>Tags: </b>" . $tag_str .  "<br>";
		}
		$text .= "<br><br> <b>Thank you very much for your help! <br> Your Societies Team </b></html>";

		while ($row1 = mysql_fetch_object($res1))
		{
			if ($row1->email != '' && $row1->notify_on_new_ticket == 1 && $row1->id != $_SESSION['user_id'])
			{
				//echo "t:" . $text;
				sendMail($row1->email, "[Societies DLR platform] new request '" .  $row->short_descr . "'", $text);
			}
		}
	}

}


/* ---------------------------------------------------------------------------- */
/* --- Notifies a creator of a ticket that an answer is available ------------- */
/* ---------------------------------------------------------------------------- */
function sendMailToTicketCreator($ticket_id)
{
	// blocker
	//return;

	// --- get ticket infos ---
	$sql = "SELECT t.*, u.email AS reporter_email, u.notify_on_new_answer AS reporter_notify
	FROM tickets AS t
	LEFT JOIN users AS u ON t.reporter_id = u.id
	LEFT JOIN tickets_states AS t_s ON t.ticket_status_id = t_s.id
	LEFT JOIN tickets_type AS t_t ON t.ticket_type_id = t_t.id
	LEFT JOIN nm_users_tickets AS a ON a.ticket_id = t.id
	WHERE t.id='" .  prepare_input_for_sql($ticket_id) . "'
	GROUP BY t.id ";

	$res = mysql_query($sql);
	if ($row = mysql_fetch_object($res))
	{
		// --- send mail ---
		$text = "<html>";

		$text .= "<br><br> <b>Thank you very much for your help! <br> Your Societies Team </b></html>";

		if ($row->reporter_notify == 1)
		{
			sendMail($row->reporter_email, "[Societies DLR platform] new answer for request '" .  $row->short_descr . "'", $text);
		}
		
	}

}
?>