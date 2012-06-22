<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>

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
// --- check if an item should be created or modified ---
$action_type = (isset($_GET['id']) ? 'modify' : 'create');

// --- MODIFY CALL -> get entry ---
if ($action_type == 'modify')
{
	$res = mysql_query("SELECT * FROM users WHERE id='" .  mysql_real_escape_string($_GET['id'])  . "'");

	if($row = mysql_fetch_object($res))
	{
		// do nothing
	}
	else
	{
		echo "Error: Can't fetch data!<br>";
	}

	// add tags to row
	$sql = "SELECT tags.name FROM tags LEFT JOIN nm_users_tags  ON tags.id = nm_users_tags.tag_id WHERE user_id='" .  mysql_real_escape_string($_GET['id'])  . "' ORDER BY  tags.name ASC" ;
	//echo $sql;
	$res_tags = mysql_query($sql);
	$t_str ='';
	while($row_tags = mysql_fetch_object($res_tags))
	{
		$t_str .= $row_tags->name . ', ';
	}
	$row->tags = $t_str;

}
else if (isset($_POST['error_db']))
{
	$row = new stdClass();
	$row->first_name = $_POST['first_name'];
	$row->last_name = $_POST['last_name'];
	$row->email = $_POST['email'];
	$row->language = $_POST['language'];
	$row->group = $_POST['group'];
	$row->institute = $_POST['institute'];
	$row->tags = $_POST['tags'];

}

?>

<div id="heading">
	<?php echo $LANG_BRICKS["USERS_HEADING_".$action_type] ?>
</div>

<form id="form1" action="<?php $PHP_SELF ?>" method="post">


	<div class="form_input_outer_container">

		<div class="form_input_inner_container">

			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["USERS_FIRST_NAME"]  ?> *
				</div>
				<input type="text" maxlength="255" name="first_name"
					class="form_textfield" 
					value="<?php echo (isset($row)? $row->first_name : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["USERS_LAST_NAME"] ?> *
				</div>
				<input type="text" maxlength="255" name="last_name"
					class="form_textfield" 
					value="<?php echo (isset($row) ? $row->last_name : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["USERS_EMAIL"] ?> *
				</div>
				<input type="text" maxlength="255" name="email"
					class="form_textfield" 
					value="<?php echo (isset($row) ? $row->email : '')?>" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["USERS_INSTITUTE"] ?>
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
					value="<?php echo (isset($row) ? $row->tags : '')?>"  />
			</div>

			<?php
			// --- group selection should be available for admins only ---
			if (isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_users_admin'] == 1)
			{
				?>
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["USERS_GROUP"] ?> *
				</div>
				<select class="form_textfield" type="text" name="group">
					<?php
					$res_group = mysql_query("SELECT * FROM groups ORDER BY name ASC");
					while($row_group = mysql_fetch_object($res_group))
					{
						if ($row_group->id != $ANONYMOUS_USER_ID)
						{
							echo "<option " . (isset($row) && $row->group == $row_group->id ? 'selected' : '') . " value='". $row_group->id ."'> " . $row_group->name;
						}
					}
					?>
				</select>
			</div>
			<?php
			}
			else
			{
				// --- select user group "user" ---
				echo "<input type='hidden' name='group' value='3' />";
			}
			?>

			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["USERS_LANGUAGE"] ?> *
				</div>
				<select class="form_textfield" type="text" name="language">
					<?php
					$res_lang = mysql_query("SELECT * FROM languages ORDER BY name ASC");
					while($row_lang = mysql_fetch_object($res_lang))
					{
						if (check_language_file($row_lang->name) == $LANG_BRICKS["LANGUAGES_YES"])
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
					<?php echo $LANG_BRICKS["USERS_PASSWORD"] ?> *
				</div>
				<input type="password" name="password" class="form_textfield" />
			</div>
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS["USERS_RE_PASSWORD"] ?> *
				</div>
				<input type="password" name="repassword" class="form_textfield" />
			</div>
			<div class="dataset">
				<div class="label">
					 * = <?php echo $LANG_BRICKS["USERS_REQUIRED"] ?>
				</div>
			</div>
			
			<div class="buttons">
				<input type="hidden" name="id"
					value="<?php echo ($action_type == 'modify' ? $row->id : '')?>" />
				<input type="submit" class="form_button"
					name="<?php echo $action_type . '_user'; ?>"
					value="<?php  echo $LANG_BRICKS["USERS_BUTTON_".$action_type] ?>" />
				<?php
				// --- back button should be available for admins only ---
				if (isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_users_admin'] == 1)
				{
					?>
				&nbsp; <input type="button" class="form_button"
					value="<?php echo $LANG_BRICKS["USERS_BUTTON_BACK"] ?>"
					onclick="window.location.href ='./index.php?site=users_list'" />
				<?php
				}
				?>
				</p>

			</div>

		</div>

</form>
