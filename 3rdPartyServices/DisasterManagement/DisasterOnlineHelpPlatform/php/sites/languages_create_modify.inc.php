<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */

// --- check if an item should be created or modified ---
$action_type = (isset($_GET['id']) ? 'modify' : 'create');

// --- MODIFY CALL -> get entry ---
if ($action_type == 'modify')
{
	$res = mysql_query("SELECT * FROM languages WHERE id='" .  mysql_real_escape_string($_GET['id'])  . "'");

	if($row = mysql_fetch_object($res))
	{
		// do nothing
	}
	else
	{
		echo "Error: Can't fetch data!<br>";
	}

}
else if (isset($_POST['error_db']))
{
	$row = new stdClass();
	$row->name = $_POST['language'];
}

?>

<div id="heading">
	Language
	<?php echo $action_type; ?>
</div>

<form id="form1" action="<?php $PHP_SELF ?>" method="post">


	<div class="form_input_outer_container">

		<div class="form_input_inner_container">

			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["LANGUAGES_LANGUAGE"] ?>
				</div>
				<input type="text" maxlength="255" name="language"
					class="form_textfield" tabindex="10"
					value="<?php echo (isset($row)? $row->name : '')?>" />
			</div>
			<div class="buttons">
				<input type="hidden" name="id"
					value="<?php echo ($action_type == 'modify' ? $row->id : '')?>" />
				<input type="submit" class="form_button"
					name="<?php echo $action_type . '_lang'; ?>"
					value="<?php  echo $LANG_BRICKS["USERS_BUTTON_".$action_type] ?>" />
				&nbsp; <input type="button" class="form_button"
					value="<?php  echo $LANG_BRICKS["USERS_BUTTON_BACK"] ?>"
					onclick="window.location.href ='./index.php?site=languages_list'" />
				</p>

			</div>

		</div>

</form>
