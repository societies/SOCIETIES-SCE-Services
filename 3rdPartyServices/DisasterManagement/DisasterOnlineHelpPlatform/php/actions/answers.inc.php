<?php

// --- check if you logged in as a user, revisor or admin ---
if (!isset($_SESSION['user_id']) && (isset($_GET['revision_set']) || isset($_GET['revision_unset']) || isset($_GET['crowd_up']) || isset($_GET['crowd_down'])) )
{
	$_POST['error'] = "You are not allowed to do this action";
}
// --- increase crowd verification counter ---
else if(isset($_GET['crowd_up']) && $_SESSION['permissions']['perm_crowd_verification'] == 1)
{
	// querys for verfication +/-
	$sql_plus="Select * from nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_up']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "' AND crowd='1'";
	$sql_minus="Select * from nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_up']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "' AND crowd='-1'";

	// If entry is available, table row must be 1 row
	if(mysql_num_rows(mysql_query($sql_plus))==1 )
	{
		$_POST['error'] = "You have already voted +1";
	}
	else if(mysql_num_rows(mysql_query($sql_minus))==1 )
	{
		// delete all crowd entrys for this user
		$sql="DELETE FROM nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_up']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "'";
		mysql_query($sql);
		$_POST['info'] = "Verification +/- 0 saved";
	}
	else
	{
		// delete all crowd entrys for this user
		$sql="DELETE FROM nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_up']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "'";
		mysql_query($sql);
		//echo $sql . "<br>";

		// insert crowd entrys for this user
		$sql="INSERT INTO `nm_users_answers` (`answer_id` , `user_id` , `crowd`) VALUES ('" . prepare_input_for_sql($_GET['crowd_up']) . "' , '" . prepare_input_for_sql($_SESSION['user_id']) . "' , '1')";
		mysql_query($sql);

		//echo $sql;
		$_POST['info'] = "Verification +1 saved";
	}
}
// --- decrease crowd verification counter ---
else if(isset($_GET['crowd_down']) && $_SESSION['permissions']['perm_crowd_verification'] == 1)
{
	// querys for verfication +/-
	$sql_plus="Select * from nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_down']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "' AND crowd='1'";
	$sql_minus="Select * from nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_down']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "' AND crowd='-1'";

	// If entry is available, table row must be 1 row
	if(mysql_num_rows(mysql_query($sql_minus))==1 )
	{
		$_POST['error'] = "You have already voted -1";
	}
	else if(mysql_num_rows(mysql_query($sql_plus))==1 )
	{
		// delete all crowd entrys for this user
		$sql="DELETE FROM nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_down']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "'";
		mysql_query($sql);
		$_POST['info'] = "Verification +/- 0 saved";
	}
	else
	{
		// delete all crowd entrys for this user
		$sql="DELETE FROM nm_users_answers WHERE answer_id = '" . prepare_input_for_sql($_GET['crowd_down']) . "' AND user_id = '" . prepare_input_for_sql($_SESSION['user_id']) . "'";
		mysql_query($sql);

		// insert crowd entrys for this user
		$sql="INSERT INTO `nm_users_answers` (`answer_id` , `user_id` , `crowd`) VALUES ('" . prepare_input_for_sql($_GET['crowd_down']) . "' , '" . prepare_input_for_sql($_SESSION['user_id']) . "' , '-1')";
		mysql_query($sql);
		$_POST['info'] = "Verification -1 saved";
	}
}
// --- decrease crowd verification counter ---
else if(isset($_GET['revision_set']) && $_SESSION['permissions']['perm_revision_verification'] == 1)
{
	// all to 0
	$sql="UPDATE nm_users_tickets SET revision = '0' WHERE ticket_id = '" . prepare_input_for_sql($_GET['id']) . "'";
	mysql_query($sql);

	// entry to 1
	$sql="UPDATE nm_users_tickets SET revision = '1' WHERE id = '" . prepare_input_for_sql($_GET['revision_set']) . "'";
	mysql_query($sql);
	$_POST['info'] = "Revision mark set";
}
else if(isset($_GET['revision_unset']) && $_SESSION['permissions']['perm_revision_verification'] == 1)
{
	// all to 0
	$sql="UPDATE nm_users_tickets SET revision = '0' WHERE ticket_id = '" . prepare_input_for_sql($_GET['id']) . "'";
	mysql_query($sql);
	$_POST['info'] = "Revision mark unset";
}
else if ( isset($_GET['revision_set']) || isset($_GET['revision_unset']) || isset($_GET['crowd_up']) || isset($_GET['crowd_down']) )
{
	$_POST['error'] = "You are not allowed to do this action";
}

?>