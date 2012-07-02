<?php

// --- SAVE ACTION -> update db ---
if (isset($_POST['save_user_settings']))
{
	$error_string = check_user_data_for_db(true, $_POST['language'], 1, $_POST['last_name'], $_POST['first_name'], $_POST['password'], $_POST['repassword'], $_POST['email'] );

	// check if email already exists
	$sql = "Select email FROM users where id= '" . prepare_input_for_sql($_SESSION['user_id']) . "'";
	$row = mysql_fetch_object(mysql_query($sql));
	if(is_email_already_exists($_POST['email']) &&  $_POST['email'] != $row->email)
	{
		$error_string .= "<br>Email address already exists";
	}
	
	if ($error_string == NULL)
	{
		$updates = "language='" . $_POST['language'] . "', ";
		$updates .= "last_name='" . $_POST['last_name'] . "', ";
		$updates .= "first_name='" . $_POST['first_name'] . "', ";
		$updates .= "last_name='" . $_POST['last_name'] . "', ";
		$updates .= "institute='" . $_POST['institute'] . "', ";

		// notification - on new ticket
		if (isset($_POST['notify_on_new_ticket']))
		{
			$updates .= "notify_on_new_ticket='1', ";
		}
		else
		{
			$updates .= "notify_on_new_ticket='0', ";
		}
		
		// notification - on new answer
		if (isset($_POST['notify_on_new_answer']))
		{
			$updates .= "notify_on_new_answer='1', ";
		}
		else
		{
			$updates .= "notify_on_new_answer='0', ";
		}
		
		$updates .= "email='" . $_POST['email'] . "' ";
		
		$_POST['info'] = "user was updated ";

		mysql_query("UPDATE users Set " . $updates . " WHERE id ='" . prepare_input_for_sql($_SESSION['user_id']) . "'");
	
		// modify tags
		insertTagsForUser($_SESSION['user_id'], $_POST['tags']);
		
		// reconfigure language of user
		set_user_language();
		$LANG_BRICKS = get_user_lang_bricks();
	}
	else
	{
		$_POST['error'] = "Can not save data:" . $error_string;
		//$_POST['error_db'] = TRUE;

	}
}


?>