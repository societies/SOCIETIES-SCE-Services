<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */
?>

<script>	

    $(function() {
    var availableTags = [
    <?php
      $tag_str = '';
      $tags = mysql_query("SELECT * FROM tags ORDER BY name ASC");
      while($tag = mysql_fetch_object($tags))
      {
           $tag_str .= "'". $tag->name ."',";
      }
      $tag_str = substr_replace($tag_str ,"",-1);
      echo $tag_str; 
    ?>
    ];
    
    function split( val ) {
			return val.split( /,\s*/ );
		}
		function extractLast( term ) {
			return split( term ).pop();
		}

		$( "#tags" )
			// don't navigate away from the field on tab when selecting an item
			.bind( "keydown", function( event ) {
				if ( event.keyCode === $.ui.keyCode.TAB &&
						$( this ).data( "autocomplete" ).menu.active ) {
					event.preventDefault();
				}
			})
			.autocomplete({
				minLength: 0,
				source: function( request, response ) {
					// delegate back to autocomplete, but extract the last term
					response( $.ui.autocomplete.filter(
						availableTags, extractLast( request.term ) ) );
				},
				focus: function() {
					// prevent value inserted on focus
					return false;
				},
				select: function( event, ui ) {
					var terms = split( this.value );
					// remove the current input
					terms.pop();
					// add the selected item
					terms.push( ui.item.value );
					// add placeholder to get the comma-and-space at the end
					terms.push( "" );
					this.value = terms.join( ", " );
					return false;
				}
			});
	});

</script>

<?php
// --- get user entry ---
if ($_SESSION['user_id'])
{
	$res = mysql_query("SELECT * FROM users WHERE id='" . escape_str($_SESSION['user_id'])  . "'");

	if($row = mysql_fetch_object($res))
	{
		// do nothing
	}
	else
	{
		echo "Error: Can't fetch data!<br>";
	}


	// add tags to row
	$sql = "SELECT tags.name FROM tags LEFT JOIN nm_users_tags  ON tags.id = nm_users_tags.tag_id WHERE user_id='" .  mysql_real_escape_string($_SESSION['user_id'])  . "' ORDER BY  tags.name ASC" ;
	//echo $sql;
	$res_tags = mysql_query($sql);
	$t_str ='';
	while($row_tags = mysql_fetch_object($res_tags))
	{
		$t_str .= $row_tags->name . ', ';
	}
	$row->tags = $t_str;

}
else
{
	die("No user is logged in!");
}

?>

<div id="heading">
	<?php echo  $LANG_BRICKS['USER_SETTINGS_HEADING']; ?>
</div>

<form id="form1" action="<?php $PHP_SELF ?>" method="post">


	<div class="form_input_outer_container">

		<div class="form_input_inner_container">

			<div class="dataset">
				<div class="label">
					<?php echo  $LANG_BRICKS['USER_SETTINGS_FIRST_NAME']; ?>
				</div>
				<input type="text" maxlength="255" name="first_name"
					class="form_textfield"
					value="<?php echo (isset($row)? $row->first_name : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo  $LANG_BRICKS['USER_SETTINGS_LAST_NAME']; ?>
				</div>
				<input type="text" maxlength="255" name="last_name"
					class="form_textfield"
					value="<?php echo (isset($row) ? $row->last_name : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo  $LANG_BRICKS['USER_SETTINGS_EMAIL']; ?>
				</div>
				<input type="text" maxlength="255" name="email"
					class="form_textfield"
					value="<?php echo (isset($row) ? $row->email : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo  $LANG_BRICKS['USERS_INSTITUTE']; ?>
				</div>
				<input type="text" maxlength="255" name="institute"
					class="form_textfield"
					value="<?php echo (isset($row) ? $row->institute : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["TICKETS_TAGS"] ?>
				</div>
				<input type="text" maxlength="255" id="tags" name="tags"
					class="form_textfield"
					value="<?php echo (isset($row) ? $row->tags : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo  $LANG_BRICKS['USER_SETTINGS_LANGUAGE']; ?>
				</div>
				<select class="form_textfield" type="text" name="language">
					<?php
					$res_lang = mysql_query("SELECT * FROM languages ORDER BY name ASC");
					while($row_lang = mysql_fetch_object($res_lang))
					{
						if (check_language_file($row_lang->name) == $LANG_BRICKS['LANGUAGES_YES'])
						{
							echo "<option " . (isset($row) && $row->language == $row_lang->id ? 'selected' : '') . " value='". $row_lang->id ."'> " . $row_lang->name;
						}
					}
					?>
				</select>
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["TICKETS_NOTIFICATIONS"] ?>
				</div>
				<div class="checkboxes">
					<input type="checkbox" maxlength="255" name="notify_on_new_ticket"
					<?php echo ( (isset($row) && $row->notify_on_new_ticket==1) ? 'checked' : '')?> />
					<?php echo $LANG_BRICKS["TICKETS_NOTIFY_TICKET"] ?>
					
				</div>
				<div class="checkboxes">
					<input type="checkbox" maxlength="255" name="notify_on_new_answer"
					<?php echo ( (isset($row) && $row->notify_on_new_answer==1) ? 'checked' : '')?> />
					<?php echo $LANG_BRICKS["TICKETS_NOTIFY_ANSWER"] ?>
					
				</div>
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo  $LANG_BRICKS['USER_SETTINGS_NEW_PASSWORD']; ?>
				</div>
				<input type="password" name="password" class="form_textfield" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo  $LANG_BRICKS['USER_SETTINGS_RE_PASSWORD']; ?>
				</div>
				<input type="password" name="repassword" class="form_textfield" />
			</div>

			<div class="buttons">
				<input type="submit" class="form_button" name="save_user_settings"
					value="<?php echo  $LANG_BRICKS['USER_SETTINGS_BUTTON_SAVE']; ?> " />
				</p>

			</div>

		</div>

</form>
