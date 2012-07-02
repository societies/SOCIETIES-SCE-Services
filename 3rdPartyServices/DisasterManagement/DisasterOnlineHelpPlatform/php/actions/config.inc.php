<?php

// --- SAVE ACTION -> update db ---
if (isset($_POST['save_config']))
{
	$_POST['info'] = "config was updated";

	if (isset($_POST['xmpp']))
	{
		$x = 1;
	}
	else
	{
		$x = 0;
	}

	if (isset($_POST['email_notification']))
	{
		$e = 1;
	}
	else
	{
		$e = 0;
	}
	
	mysql_query("UPDATE config Set xmpp='" . $x . "' WHERE id ='1'");
	mysql_query("UPDATE config Set email_notification='" . $e . "' WHERE id ='1'");
}

?>