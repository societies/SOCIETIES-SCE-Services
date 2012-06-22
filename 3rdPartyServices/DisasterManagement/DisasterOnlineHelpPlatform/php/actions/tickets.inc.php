<?php

// --- CREATE ACTION -> write to db ---
if (isset($_POST['create_ticket']) && isset($_SESSION['user_id']))
{
	$error_string = check_ticket_data_for_db($_POST['short_descr'], $_POST['descr']);

	if(!isset($_POST['error']) && $error_string == NULL)
	{


		$_POST['info'] = "Request was created";

		// --- ticket entry ---
		$sql ="INSERT INTO `societies`.`tickets` (`ticket_type_id` , `ticket_status_id` , `short_descr` , `descr` , `timestamp_created` , `reporter_id`, `deadline`) VALUES ( '". prepare_input_for_sql($_POST['ticket_type']) ."','1', '" . prepare_input_for_sql($_POST['short_descr']) . "', '" .  prepare_input_for_sql($_POST['descr']) . "' ," . "CURRENT_TIMESTAMP , '" . prepare_input_for_sql($_SESSION['user_id']) . "', '" . prepare_input_for_sql(convertToMysqlDate($_POST['deadline'])) . "')";
		//echo $sql;
		mysql_query($sql);
		$new_ticket_id = mysql_insert_id();

		// --- tags entry ---
		insertTagsForTicket($new_ticket_id, $_POST['tags']);


		// fetching config data
		$sql = "SELECT * FROM config WHERE id='1'";
		$res = mysql_query($sql);
		$row = mysql_fetch_object($res);

		// tweet it
		if($row->xmpp == 1){
			tweetout($new_ticket_id, $_POST['descr']);
		}

		// email it
		if($row->email_notification == 1){
			sendMailToUsers($new_ticket_id);
		}
		
		$_GET['site'] = 'tickets_list';

	}
	else {
		if (!isset($_POST['error']))
		{
			$_POST['error'] = "Can not create request:" . $error_string;
		}
		$_POST['error_db'] = TRUE;
	}


}
// --- MODIFY ACTION -> update to db ---
else if (isset($_POST['modify_ticket']) && isset($_SESSION['user_id']))
{

	$error_string = check_ticket_data_for_db($_POST['short_descr'], $_POST['descr']);
	 
	if ($error_string == NULL && !isset($_POST['error']))
	{
		// modify ticket
		$updates = '';
		$updates .= "ticket_type_id='" . prepare_input_for_sql($_POST['ticket_type']) . "' , ";
		$updates .= "short_descr='" . prepare_input_for_sql($_POST['short_descr']) . "' , ";
		$updates .= "descr='" . prepare_input_for_sql($_POST['descr']) . "' , ";
		$updates .= "deadline='" . prepare_input_for_sql(convertToMysqlDate($_POST['deadline'])) . "'";

		$_POST['info'] = "request was updated";
		mysql_query("UPDATE tickets Set " . $updates . " WHERE id ='" . prepare_input_for_sql($_GET['id']) . "'");

		// modify tags
		insertTagsForTicket($_GET['id'], $_POST['tags']);


		//$_GET['site'] = 'tickets_list';
	}
	else
	{
		if (!isset($_POST['error']))
		{
			$_POST['error'] = "Can not modify request:" . $error_string;
		}
		$_POST['error_db'] = TRUE;

	}
}
// --- DELETE ACTION -> delete dataset ---
else if (isset($_GET['delete_ticket']) && (isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_tickets_admin']==1))
{
	$_POST['info'] = "Request was deleted";

	// --- delete pic of ticket ---
	//delete_image_from_ticket($_GET['delete_ticket']);

	// --- delete all votes ---
	$sql = "Select id as a_id From nm_users_tickets a where ticket_id='" . prepare_input_for_sql($_GET['delete_ticket']) . "'";
	$res = mysql_query($sql);
	while($row = mysql_fetch_object($res))
	{
		mysql_query("DELETE FROM nm_users_answers WHERE answer_id ='" . prepare_input_for_sql($row->a_id) . "'");
	}
	
	// --- delete answers ---
	mysql_query("DELETE FROM nm_users_tickets WHERE ticket_id ='" . prepare_input_for_sql($_GET['delete_ticket']) . "'");

	// --- delete tags ---
	$sql = "DELETE FROM nm_tickets_tags WHERE ticket_id = '". prepare_input_for_sql($_GET['delete_ticket']) ."'";
	mysql_query($sql);

	// --- delete ticket ---
	mysql_query("DELETE FROM tickets WHERE id ='" . prepare_input_for_sql($_GET['delete_ticket']) . "'");

}
// --- ANSWER ACTION -> create dataset ---
else if(isset($_POST['answer_ticket']))
{
	$error_string = check_ticket_answer_data_for_db($_POST['answer_ticket']);

	if ($error_string == NULL)
	{
		$answering_user_id = isset($_SESSION['user_id']) ? escape_str($_SESSION['user_id']) : $ANONYMOUS_USER_ID;
		mysql_query("INSERT INTO `societies`.`nm_users_tickets` (`ticket_id` , `user_id` , `answer` , `time`) VALUES ( '". prepare_input_for_sql($_GET['id']) ."','". prepare_input_for_sql($answering_user_id) ."', '" . prepare_input_for_sql($_POST['answer_ticket']) . "', CURRENT_TIMESTAMP)");
		$_POST['info'] = "Request has been answered";
		
		// send mail
		sendMailToTicketCreator($_GET['id']);
	}
	else
	{
		$_POST['error'] = "Can not answer request:" . $error_string;
		$_POST['error_db'] = TRUE;
	}

}
else if(isset($_GET['close_ticket']) && isset($_SESSION['user_id']))
{
	// --- close request ---
	$sql = "UPDATE tickets SET ticket_status_id = '2' WHERE id='" . prepare_input_for_sql($_GET['id']). "'";
	$_POST['info'] = "Ticket #" . $_GET['id'] . " closed";
	mysql_query($sql);
	$_GET['site'] = "tickets_list";
}
else if(isset($_GET['reopen_ticket']) && isset($_SESSION['user_id']))
{
	// --- reopen request ---
	$sql = "UPDATE tickets SET ticket_status_id = '1' WHERE id='" . prepare_input_for_sql($_GET['id']). "'";
	$_POST['info'] = "Ticket #" . $_GET['id'] . " reopened";
	mysql_query($sql);
	$_GET['site'] = "tickets_list";
}


?>