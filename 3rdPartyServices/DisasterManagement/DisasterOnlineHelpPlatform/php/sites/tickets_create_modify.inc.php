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


	$(function() {
		$( "#deadline" ).datetimepicker({
    	ampm: true
    });
  });

</script>


<?php
// --- check if an item should be created or modified ---
$action_type = (isset($_GET['id']) ? 'modify' : 'create');

// --- MODIFY CALL -> get entry ---
if ($action_type == 'modify')
{
	$res = mysql_query("SELECT * FROM tickets WHERE id='" .  mysql_real_escape_string($_GET['id'])  . "'");

	if($row = mysql_fetch_object($res))
	{
		// do nothing
	}
	else
	{
		echo "Error: Can't fetch data!<br>";
	}

	// add tags to row
	$sql = "SELECT tags.name FROM tags LEFT JOIN nm_tickets_tags  ON tags.id = nm_tickets_tags.tag_id WHERE ticket_id='" .  mysql_real_escape_string($_GET['id'])  . "' ORDER BY  tags.name ASC" ;
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
	$row->ticket_type_id = $_POST['ticket_type'];
	$row->short_descr = $_POST['short_descr'];
	$row->descr = $_POST['descr'];
	$row->tags = $_POST['tags'];
	$row->deadline = $_POST['deadline'];
}

?>


<div id="heading" style="float: left;">
	<?php echo $LANG_BRICKS["TICKETS_CREATE_HEADLINE_".$action_type] ?>
</div>
<div class="buttons" style="float: right; margin-top: 20px;"
	align="right">
	<input type="button" style="width: 250px;" class="form_button"
		value="<?php echo $LANG_BRICKS["TICKETS_BACK_LONG"] ?>"
		onclick="window.location.href ='./index.php?site=tickets_list'" />
</div>
<div style="clear: both;"></div>


<form id="form1" action="<?php $PHP_SELF ?>" method="post"
	enctype="multipart/form-data">

	<div class="dataset">
		<div class="label">
			<?php echo $LANG_BRICKS["TICKETS_SHORT_DESCRIPTION"] ?>
		</div>
		<input type="text" maxlength="255" name="short_descr"
			class="form_textfield_100"
			value="<?php echo (isset($row) ? $row->short_descr : '')?>"
			/>
	</div>

	<input type="hidden" name="ticket_type" value="1" /> 
	
	<!-- 
	JUST ONE TYPE AVAILABLE ATM...
	<div class="dataset">
		<div class="label">
			<?php echo $LANG_BRICKS["TICKETS_TYPE"] ?>
		</div>
		<select class="form_textfield_100" name="ticket_type" >
			<?php
			$res_tickets_type = mysql_query("SELECT * FROM tickets_type ORDER BY name ASC");
			while($row_tickets_type = mysql_fetch_object($res_tickets_type))
			{
				echo "<option " . (isset($row) && $row->ticket_type_id == $row_tickets_type->id ? 'selected' : '') . " value='". $row_tickets_type->id ."'> " . $LANG_BRICKS["DB_TICKETS_".$row_tickets_type->name];
			}
			?>
		</select>
	</div>
 -->
	<div class="dataset">
		<div class="label">
			<?php echo $LANG_BRICKS["TICKETS_DEADLINE"] ?>
		</div>
		<input type="text" maxlength="255" id="deadline" name="deadline"
			class="form_textfield_100"
			value="<?php echo (isset($row) ? convertToTimeDatePickerDate($row->deadline) : '')?>"
			/>
	</div>

	<div class="dataset">
		<div class="label">
			<?php echo $LANG_BRICKS["TICKETS_TAGS"] ?>
		</div>
		<input type="text" maxlength="255" id="tags" name="tags"
			class="form_textfield_100"
			value="<?php echo (isset($row) ? $row->tags : '')?>"  />
	</div>

	<div class="dataset">
		<div class="label">
			<?php echo $LANG_BRICKS["TICKETS_DESCRIPTION"] ?>
		</div>
		<textarea class="form_textarea_without_width" name="descr">
			<?php echo (isset($row) ? $row->descr : '')?>
		</textarea>
		<script type="text/javascript">
				  CKEDITOR.replace( 'descr' ,
	         {
		          toolbar : 'SocietiesToolbar'
	         });
		  	</script>
	</div>


	<div class="buttons">
		<input type="hidden" name="id"
			value="<?php echo ($action_type == 'modify' ? $row->id : '')?>" /> <input
			type="submit" class="form_button"
			name="<?php echo $action_type . '_ticket'; ?>"
			value="<?php echo $LANG_BRICKS["TICKETS_CREATE_BUTTON_".$action_type]; ?>" />
		&nbsp;
	</div>



</form>
