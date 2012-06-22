<?php
// --- CREATE ACTION -> write to db ---
if (isset($_POST['create_user']))
{

	$error_string = check_user_data_for_db(false, $_POST['language'], $_POST['group'], $_POST['last_name'], $_POST['first_name'], $_POST['password'], $_POST['repassword'], $_POST['email'] );

	// check if email already exists
	if(is_email_already_exists($_POST['email']))
	{
		$error_string .= "<br>Email address already exists";
	}

	
	// notification - on new ticket
	if (isset($_POST['notify_on_new_ticket']))
	{
		$notify_on_new_ticket = 1;
	}
	else
	{
		$notify_on_new_ticket = 0;
	}
	
	// notification - on new answer
	if (isset($_POST['notify_on_new_answer']))
	{
		$notify_on_new_answer = 1;
	}
	else
	{
		$notify_on_new_answer = 0;
	}
	
	if ($error_string == NULL)
	{
		$_POST['info'] = "user was created ";
		$sql = "INSERT INTO users (language, `group`, last_name, first_name, passwd, email, institute, notify_on_new_ticket, notify_on_new_answer) VALUES ('" . prepare_input_for_sql($_POST['language']) . "', '" . prepare_input_for_sql($_POST['group']). "', '" . prepare_input_for_sql($_POST['last_name']) . "', '" . prepare_input_for_sql($_POST['first_name']) . "', '" . "', '" . prepare_input_for_sql($_POST['email']) . "', '" . prepare_input_for_sql($_POST['institute']) . "', '" . $notify_on_new_ticket . "', '" . $notify_on_new_answer . "' )";
		//echo $sql;
		mysql_query($sql);

		$new_user_id = mysql_insert_id();
		// insert tags
		insertTagsForUser($new_user_id, $_POST['tags']);

		// --- select return page ---
		if (!isset($_SESSION['user_id']))
		{
			$_GET['site'] = 'user_activation';
		}
		else
		{
			$_GET['site'] = 'users_list';
		}

	}
	else
	{
		$_POST['error'] = "Can not create user: " . $error_string;
		$_POST['error_db'] = TRUE;

	}
}
// --- MODIFY ACTION -> update to db ---
else if (isset($_POST['modify_user']))
{
	//MARK1
	$error_string = check_user_data_for_db(true, $_POST['language'], $_POST['group'], $_POST['last_name'], $_POST['first_name'], $_POST['password'], $_POST['repassword'], $_POST['email'] );

	// check if email already exists
	$sql = "Select email FROM users where id= '" . prepare_input_for_sql($_GET['id']) . "'";
	$row = mysql_fetch_object(mysql_query($sql));
	if(is_email_already_exists($_POST['email']) &&  $_POST['email'] != $row->email)
	{
		$error_string .= "<br>Email address already exists";
	}

	if ($error_string == NULL)
	{
		$updates = "language='" . prepare_input_for_sql($_POST['language']) . "', ";
		$updates .= "`group`='" . prepare_input_for_sql($_POST['group']) . "', ";
		$updates .= "institute='" . prepare_input_for_sql($_POST['institute']) . "', ";
		$updates .= "last_name='" . prepare_input_for_sql($_POST['last_name']) . "', ";
		$updates .= "first_name='" . prepare_input_for_sql($_POST['first_name']) . "', ";
		$updates .= "last_name='" . prepare_input_for_sql($_POST['last_name']) . "', ";

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
		
		$updates .= "email='" .  prepare_input_for_sql($_POST['email']) . "' ";


		$_POST['info'] = "user was updated";
		mysql_query("UPDATE users Set " . $updates . " WHERE id ='" . prepare_input_for_sql($_GET['id']) . "'");

		// modify tags
		insertTagsForUser($_GET['id'], $_POST['tags']);

		// reconfigure language of user
		set_user_language();
		$LANG_BRICKS = get_user_lang_bricks();

		$_GET['site'] = 'users_list';
	}
	else
	{
		$_POST['error'] = "Can not modify user:" . $error_string;
		$_POST['error_db'] = TRUE;

	}
}
// --- DELETE ACTION -> delete dataset ---
else if (isset($_GET['delete_user']))
{
	// --- delete tags ---
	$sql = "DELETE FROM nm_users_tags WHERE ticket_id = '". prepare_input_for_sql($_GET['delete_user']) ."'";
	mysql_query($sql);
	mysql_query("UPDATE tickets SET reporter_id ='". $ANONYMOUS_USER_ID ."' WHERE reporter_id ='" . prepare_input_for_sql($_GET['delete_user']) . "'");
	mysql_query("UPDATE nm_users_tickets SET user_id='". $ANONYMOUS_USER_ID ."' WHERE user_id ='" . prepare_input_for_sql($_GET['delete_user']) . "'");
	mysql_query("DELETE FROM nm_users_answers WHERE user_id ='" . prepare_input_for_sql($_GET['delete_user']) . "'");
	mysql_query("DELETE FROM nm_users_tags WHERE user_id ='" . prepare_input_for_sql($_GET['delete_user']) . "'");
	mysql_query("DELETE FROM users WHERE id ='" . prepare_input_for_sql($_GET['delete_user']) . "'");
	$_POST['info'] = "user was deleted";
}
?>