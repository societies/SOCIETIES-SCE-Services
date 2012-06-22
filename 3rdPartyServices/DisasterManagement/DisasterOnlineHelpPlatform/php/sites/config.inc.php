<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>

<?php

// fetching config data
$sql = "SELECT * FROM config WHERE id='1'";

$res = mysql_query($sql);

$row = mysql_fetch_object($res);
?>

<div id="heading">
	<?php echo $LANG_BRICKS['CONF_HEADING']; ?>
</div>

<form id="form1" action="<?php $PHP_SELF ?>" method="post">

	<div class="form_input_outer_container">

		<div class="form_input_inner_container">

			<div class="dataset">
				<input class="formstyle_textfield"
				<?php echo (isset($row) && $row->xmpp==1 ? 'checked' :  '') ?>
					class="textfield" type="checkbox" name="xmpp" class="input" />
				<?php echo "&nbsp;" . $LANG_BRICKS['CONF_XMPP']; ?>
			</div>

			<div class="dataset">
				<input class="formstyle_textfield"
				<?php echo (isset($row) && $row->email_notification==1 ? 'checked' :  '') ?>
					class="textfield" type="checkbox" name="email_notification"
					class="input" />
				<?php echo "&nbsp;" . $LANG_BRICKS['CONF_EMAIL_NOTIFICATION']; ?>
			</div>

			<div class="buttons">
				<input type="submit" class="form_button" name="save_config"
					value="<?php echo  $LANG_BRICKS['CONF_BUTTON_SAVE']; ?> " />
				</p>

			</div>

		</div>

</form>
