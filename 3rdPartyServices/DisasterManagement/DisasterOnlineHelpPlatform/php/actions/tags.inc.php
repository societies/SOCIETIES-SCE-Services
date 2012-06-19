<?php

// --- DELETE ACTION -> delete dataset ---
if (isset($_GET['delete_tag']))
{
	$_POST['info'] = "tag was deleted";
	mysql_query("DELETE FROM nm_tickets_tags WHERE tag_id ='" . prepare_input_for_sql($_GET['delete_tag']) . "'");
	mysql_query("DELETE FROM nm_users_tags WHERE tag_id ='" . prepare_input_for_sql($_GET['delete_tag']) . "'");
	mysql_query("DELETE FROM tags WHERE id ='" . prepare_input_for_sql($_GET['delete_tag']) . "'");
}

?>