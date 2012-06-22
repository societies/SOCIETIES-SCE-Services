<?php
if (!isset($_SESSION['user_id']) && isset($_POST['user_login']) && isset($_POST['user_passwd']))
{
	login_user($_POST['user_login'], $_POST['user_passwd']);
}
else if (isset($_SESSION['user_id']) && isset($_GET['logout']))
{
	unset($_SESSION['user_id']);
	unset($_SESSION['user_email']);
	unset($_SESSION['permissions']);
	$_POST['info'] = $LANG_BRICKS['LOGOUT_MSG'];
}
?>