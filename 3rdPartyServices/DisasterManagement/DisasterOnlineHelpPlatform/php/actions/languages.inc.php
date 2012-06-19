<?php

// --- CREATE ACTION -> write to db ---
if (isset($_POST['create_lang']))
{
	$error_string = check_lang_data_for_db($_POST['language']);

	if ($error_string == NULL)
	{
		$_POST['info'] = "language was created";
		mysql_query("INSERT INTO languages (name) VALUES ('" . prepare_input_for_sql($_POST['language']) . "')");
		$_GET['site'] = 'languages_list';
	}
	else
	{
		$_POST['error'] = "Can not create language:" . $error_string;
		$_POST['error_db'] = TRUE;
	}
}
// --- MODIFY ACTION -> update to db ---
else if (isset($_POST['modify_lang']))
{

	$error_string = check_lang_data_for_db($_POST['language']);

	if ($error_string == NULL)
	{
		$updates = "name='" . $_POST['language'] . "'";

		$_POST['info'] = "language was updated";
		mysql_query("UPDATE languages Set " . $updates . " WHERE id ='" . prepare_input_for_sql($_GET['id']) . "'");

		$_GET['site'] = 'languages_list';
	}
	else
	{
		$_POST['error'] = "Can not modify language:" . $error_string;
		$_POST['error_db'] = TRUE;

	}
}
// --- DELETE ACTION -> delete dataset ---
else if (isset($_GET['delete_lang']))
{
	$_POST['info'] = "language was deleted";
	mysql_query("DELETE FROM languages WHERE id ='" . prepare_input_for_sql($_GET['delete_lang']) . "'");
}

?>