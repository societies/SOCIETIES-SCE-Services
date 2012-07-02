<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>

<?php

// fetching data
$ticket_id = mysql_escape_string($_GET['id']);


// fetching ticket data
$ticket_res = mysql_query("SELECT t.*, t_t.name AS ticket_type, u.first_name AS reporter_first_name, u.last_name AS reporter_last_name FROM users AS u, tickets AS t, tickets_type AS t_t WHERE t.id = ".$ticket_id." AND t_t.id = t.ticket_type_id AND u.id = t.reporter_id");

$row = mysql_fetch_object($ticket_res);

// update view counter
if (!isset($_POST['answer_ticket'])) {
	mysql_query("UPDATE tickets SET count_views=count_views+1 WHERE id='" . $ticket_id . "'");
}
?>

<div id="heading" style="float: left;">
	<?php echo $LANG_BRICKS['TICKETS_PROCESS_HEADER']; ?>
</div>
<div class="buttons" style="float: right; margin-top: 18px;"
	align="right">
	<input type="button" style="width: 250px;" class="form_button"
		value="<?php echo $LANG_BRICKS["TICKETS_BACK_LONG"] ?>"
		onclick="window.location.href ='./index.php?site=tickets_list'" />
</div>
<div style="clear: both;"></div>

<hr>
<br>


<?php
// GETTING DATA
$sql = "SELECT t.*, t_t.name AS ticket_type, t_s.name AS ticket_status, u.first_name AS reporter_first_name, u.last_name AS reporter_last_name, u.institute AS reporter_institute, count(a.user_id) as count_answers \n"
. "FROM tickets AS t\n"
. "LEFT JOIN users AS u ON t.reporter_id = u.id \n"
. "LEFT JOIN tickets_states AS t_s ON t.ticket_status_id = t_s.id\n"
. "LEFT JOIN tickets_type AS t_t ON t.ticket_type_id = t_t.id\n"
. "LEFT JOIN nm_users_tickets AS a ON a.ticket_id = t.id \n"
. "WHERE t.id='" . $ticket_id  . "'"
. "GROUP BY a.ticket_id  ORDER BY t.id ";

//echo $sql;
$ticket_res = mysql_query($sql);

if($row = mysql_fetch_object($ticket_res))
{
	?>

<div class="list_title">
	<div class="request_title">
		<?php  if ($row->ticket_status == 'closed') { 
			echo '[' .$LANG_BRICKS['DB_STATUS_'.$row->ticket_status]. ']';
		} ?>
		<?php echo $row->short_descr; ?>
	</div>
	<div class="request_content">
		<?php  echo html_entity_decode($row->descr); ?>
	</div>
		<div class="list_tag_capture list_grey_text">Tags:</div>
	<div class="tags">
		<?php
		$sql = "SELECT  ta.id, ta.name "
		. "FROM nm_tickets_tags AS nm "
		. "LEFT JOIN tickets AS t ON nm.ticket_id = t.id "
		. "LEFT JOIN tags AS ta ON nm.tag_id = ta.id "
		. "WHERE t.id='" .  $row->id . "'"
		. "ORDER BY ta.name ASC";

		//echo $sql;
		$tag_res = mysql_query($sql);

		while($tag_row = mysql_fetch_object($tag_res))
		{
			echo  "<span class='tag'><a href='index.php?site=tickets_list&tag_search=". $tag_row->id ."'> " . $tag_row->name  ." </a> </span>";
		}
		?>

	</div>
	<br>
</div>
<div class="list_meta" align="right">
	<table>
		<?php
		if (isset($_SESSION['user_id']))
		{
			// init variables
			$options_html_string = '';
			$options_available = false;

			// fill variables
			$options_html_string .= "<tr><td class='list_meta_title'>Options:</td><td class='list_meta_options'>" ;

			if ( ($_SESSION['permissions']['perm_tickets_admin']==1 || $_SESSION['user_id'] == $row->reporter_id ) && $row->ticket_status_id == $TICKET_OPENED_ID)
			{
				$options_available = true;
				$options_html_string .= "<a href= 'index.php?site=tickets_list&close_ticket=True&id=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_ARCHIVE'] . "' src='./images/check.gif'></a> &nbsp;";
			}
			else if ( ($_SESSION['permissions']['perm_tickets_admin']==1 || $_SESSION['user_id'] == $row->reporter_id) &&  $row->ticket_status_id == $TICKET_CLOSED_ID)
			{
				$options_available = true;
				$options_html_string .= "<a href= 'index.php?site=tickets_list&reopen_ticket=True&id=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_REOPEN'] . "' src='./images/reopen.gif'></a> &nbsp;";
			}

			if ($_SESSION['permissions']['perm_tickets_admin']==1 || $_SESSION['user_id'] == $row->reporter_id)
			{
				$options_available = true;
				$options_html_string .= "<a href= 'index.php?site=tickets_create_modify&id=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_EDIT'] . "' src='./images/edit.gif'></a> &nbsp;";
			}

			if ($_SESSION['permissions']['perm_tickets_admin']==1)
			{
				$options_available = true;
				$options_html_string .= "<a onclick=\"return confirm('" . $LANG_BRICKS['CONFIRM_DELETE_TICKET'] . "')\" href= 'index.php?site=tickets_list&delete_ticket=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_DELETE'] . "' src='./images/delete.gif'></a>";
			}

			echo "</td></tr>";

			// check if options are available
			if ($options_available == true)
			{
				echo $options_html_string;
			}
		}
		?>
		<tr>
			<td class="list_meta_title">Author:</td>
			<td><?php echo $row->reporter_last_name . ", " . $row->reporter_first_name ?>
			</td>
		</tr>
		<tr>
			<td class="list_meta_title">Institute:</td>
			<td><?php echo  $row->reporter_institute ?></td>
		</tr>
		<tr>
			<td class="list_meta_title">Created:</td>
			<td><?php echo $row->timestamp_created ?></td>
		</tr>
		<tr>
			<td class="list_meta_title">Deadline:</td>
			<td><?php echo ($row->deadline != "0000-00-00 00:00:00" ? $row->deadline : "-") ?>
			</td>
		</tr>

	</table>
</div>
<div style="clear: both;"></div>
<hr>
<?php } ?>
<div class="blog_answer_area">
	<form id="form1" action="index.php?site=tickets_process&id=<?php echo $_GET['id']?>" method="post">
		<br>
		<div class="dataset">
			<div class="request_title">
				<?php echo $LANG_BRICKS["TICKETS_ANSWER_2"] ?>
			</div>
			<textarea class="form_textarea_without_width" name="answer_ticket"></textarea>
			<script type="text/javascript">
				  CKEDITOR.replace( 'answer_ticket' ,
	         {
		          toolbar : 'SocietiesToolbar'
	         });
		  	</script>
		</div>

		<div class="dataset" style="text-align: right; margin-top: 20px;">
			<input type="submit" class="form_button"
				value="<?php echo $LANG_BRICKS['TICKETS_ANSWER']; ?>" />
		</div>
		<br>


		<?php
			
		//$user_id = (isset($_SESSION['user_id']) ? $_SESSION['user_id'] : 1);
		//$sql = "SELECT u.last_name, u.first_name, u.institute, ut.* FROM users u, nm_users_tickets ut, tickets t WHERE u.id=ut.user_id and ut.ticket_id='".  prepare_input_for_sql($_GET['id']) ."' GROUP BY time ORDER BY revision DESC, time DESC";

		$sql="SELECT u.last_name, u.first_name, u.institute, ut.*, sum(ua.crowd) AS sum_crowd FROM nm_users_tickets ut
		LEFT JOIN  users u ON u.id=ut.user_id
		LEFT JOIN  nm_users_answers ua ON ut.id=ua.answer_id
		WHERE ut.ticket_id='".  prepare_input_for_sql($_GET['id']) ."'
		GROUP BY time, user_id, ua.answer_id  ORDER BY revision DESC, sum_crowd DESC, time DESC";
		
		// GETTING DATA
		//echo $sql;
		$ticket_res = mysql_query($sql);
			
		if (mysql_num_rows($ticket_res))
		{
			echo "<hr style='' /> <br> <div class='request_title' ><a name='answers'>" . $LANG_BRICKS['TICKETS_ANSWERS'] . "</a></div>";
		}
			
		while($row = mysql_fetch_object($ticket_res))
		{
			$inst = $row->institute != "" ? " (".$row->institute.")" : "";
			echo "<div class='blog_single'>";
			echo "<div class='blog_single_head' >";
			echo $row->first_name . " " . $row->last_name .$inst. " " . $LANG_BRICKS['TICKETS_AT'] . " " . $row->time;
			echo "</div ><div class='blog_single_body'>";
			echo "<div class='blog_single_meta' >";

			// get sum crowd verfication
			//$sql = "SELECT sum(crowd) AS sum_crowd FROM nm_users_answers WHERE answer_id = '".  prepare_input_for_sql($row->id) ."' GROUP BY answer_id ";
			//echo $sql;
			//$count_res = mysql_query($sql);
			//if ($row_count = mysql_fetch_object($count_res)) {
			//}
			//else {$row_count->sum_crowd = 0;
			//}
			// crowd based verification
			echo "<a href='./index?site=tickets_process&id=" . $ticket_id . "&crowd_up=". $row->id ."'> <img class='blog_margin' src='./images/arrow_up.png'></a>";
			echo "<div class='list_large_text list_grey_text'>" . ($row->sum_crowd == "" ? 0 :$row->sum_crowd)  . "</div>";
			echo "<a href='./index?site=tickets_process&id=" . $ticket_id . "&crowd_down=". $row->id ."'> <img class='blog_margin' src='./images/arrow_down.png'></a>";

			// revision based verification
			if ($row->revision == 0)
			{
				echo "<a href='./index?site=tickets_process&id=" . $ticket_id . "&revision_set=" . $row->id ."'> <img class='blog_margin' src='./images/checkmark_gray.png'></a>";
			}
			else
			{
				echo "<a href='./index?site=tickets_process&id=" . $ticket_id . "&revision_unset=" . $row->id ."'><img class='blog_margin' src='./images/checkmark_green.png'></a>";
			}

			echo "</div><div class='blog_single_content' >";
			echo html_entity_decode($row->answer);
			echo "</div> <div style='clear:both;'> </div></div></div>";
		}
			
			
		?>

	</form>
</div>
