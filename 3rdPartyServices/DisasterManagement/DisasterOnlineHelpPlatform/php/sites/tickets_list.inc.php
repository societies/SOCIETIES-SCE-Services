<?php /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>

<div id="heading">
	<?php echo $LANG_BRICKS['TICKETS_HEADLINE']?>
</div>

<form id="form1" action="./index.php" method="post">


	<p>
	
	
	<div style="float: left;">
		<p>
			<label><?php echo $LANG_BRICKS['TICKETS_SEARCH']?><br /> <input
				class="formstyle_textfield" class="textfield" type="text"
				name="search" id="search" class="input"
				value="<?php echo (isset($_REQUEST['search']) ? $_REQUEST['search'] : ''); ?>"
				tabindex="20" /> </label>

			<?php if (isset($_SESSION['user_id'])) { ?>
			&nbsp;&nbsp;&nbsp; <input class="formstyle_textfield"
			<?php echo (isset ($_REQUEST['list_only_suggestions']) ? 'checked' :  ''); ?>
				class="textfield" type="checkbox" name="list_only_suggestions"
				class="input" tabindex="30" />
			<?php echo "&nbsp;" . $LANG_BRICKS['TICKETS_LIST_SUGGESTIONS']; ?>
			&nbsp; <input class="formstyle_textfield"
			<?php echo (isset ($_REQUEST['list_closed_tickets']) ? 'checked' :  ''); ?>
				class="textfield" type="checkbox" name="list_closed_tickets"
				class="input" tabindex="30" />
			<?php echo "&nbsp;" . $LANG_BRICKS['TICKETS_LIST_CLOSED']; ?>


			<?php } ?>
		</p>
	</div>

	<div style="text-align: right;">

		<?php
		if (isset($_SESSION['user_id']))
		{
			echo "<input type='button' class='form_button' style='width:180px; margin-bottom:10px;' name='create' onclick='window.location.href =\"./index.php?site=tickets_create_modify\"'  value='" . $LANG_BRICKS['TICKETS_CREATE_NEW'] . "'>";

		}
		?>

		<br> <input type="submit" class="form_button" style="width: 180px;"
			value="<?php echo $LANG_BRICKS['TICKETS_REFRESH']?>" />
	</div>

	<div style="clear: both;"></div>
	</p>







	<?php

	// search string
	if (isset($_REQUEST['search']))
	{
		$search =  "WHERE ( t.short_descr LIKE '%". mysql_real_escape_string($_REQUEST['search']) ."%' OR u.first_name LIKE '%". mysql_real_escape_string($_REQUEST['search']) ."%' OR u.last_name LIKE '%". mysql_real_escape_string($_REQUEST['search']) ."%' )";
	}
	else
	{
		$search = "";
	}

	// include closed tickets???
	if (isset ($_REQUEST['list_closed_tickets']))
	{
		$list_closed_tickets = "";
	}
	else
	{
		$list_closed_tickets = ($search=="" ? "WHERE ": "AND ") . " t.ticket_status_id='" . $TICKET_OPENED_ID . "' ";
	}

	// GETTING DATA
	$sql = "SELECT t.*, t_t.name AS ticket_type, t_s.name AS ticket_status, u.first_name AS reporter_first_name, u.last_name AS reporter_last_name, u.institute AS reporter_institute, count(a.user_id) as count_answers \n"
	. "FROM tickets AS t\n"
	. "LEFT JOIN users AS u ON t.reporter_id = u.id \n"
	. "LEFT JOIN tickets_states AS t_s ON t.ticket_status_id = t_s.id\n"
	. "LEFT JOIN tickets_type AS t_t ON t.ticket_type_id = t_t.id\n"
	. "LEFT JOIN nm_users_tickets AS a ON a.ticket_id = t.id \n"
	. $search . $list_closed_tickets
	. "GROUP BY t.id  ORDER BY t.timestamp_created DESC";

	//echo $sql;
	$ticket_res = mysql_query($sql);

	// count row
	$num_rows_tickets = mysql_num_rows($ticket_res);

	// counter current ticket
	$counter_current_ticket = 0;

	while($row = mysql_fetch_object($ticket_res))
	{

		// only list suggested tickets? TODO: set above query
		if (isset($_SESSION['user_id']) && isset ($_REQUEST['list_only_suggestions']))
		{
			$sql1 = "SELECT t.id " .
					"FROM tags AS t " .
					"LEFT JOIN nm_tickets_tags AS tt ON t.id = tt.tag_id " .
					"LEFT JOIN nm_users_tags AS tu ON t.id = tu.tag_id " .
					"WHERE tt.ticket_id = '" . prepare_input_for_sql($row->id) . "' " .
					"AND tu.user_id = '" . prepare_input_for_sql($_SESSION['user_id'])  . "'";

			//echo $sql1;
			if(mysql_num_rows(mysql_query($sql1)) < 1)
			{
				//$counter_current_ticket -= 1;
				continue;
			}

		}

		// tag search
		if ( isset ($_GET['tag_search']))
		{
			$sql1 = "SELECT t.id " .
					"FROM tags AS t " .
					"LEFT JOIN nm_tickets_tags AS tt ON t.id = tt.tag_id " .
					"WHERE tt.ticket_id = '" . prepare_input_for_sql($row->id) . "' " .
					"AND t.id = '" . prepare_input_for_sql($_GET['tag_search'])  . "'";

			//echo $sql1;
			if(mysql_num_rows(mysql_query($sql1)) < 1)
			{
				
				continue;
			}
		}

		// is ticket within range of the selected page?
		$counter_current_ticket += 1;
		if (!isItemInPageRange($counter_current_ticket, (isset($_GET['page']) ? $_GET['page'] : 1)))
		{
			//$counter_current_ticket -= 1;
			continue;
		}
		
		?>
	<div class="list_container">

		<div
			class="list_count_answers list_count_<?php echo ($row->count_answers==0 ? 'unanswered' : 'answered') ?>">
			<div class="list_large_text">
				<?php echo  $row->count_answers ?>
			</div>
			answers
		</div>
		<div class="list_count_views list_grey_text">
			<div class="list_large_text">
				<?php echo  $row->count_views ?>
			</div>
			views
		</div>
		<div class="list_title">
			<div>
				<a href="index.php?site=tickets_process&id=<?php echo $row->id;?>">
					<?php  if ($row->ticket_status == 'closed') { 
						echo '[' .$LANG_BRICKS['DB_STATUS_'.$row->ticket_status]. ']';
					} ?> <?php echo $row->short_descr; ?>
				</a>
			</div>
			<div class="tags">
				
				<div class="list_tag_capture list_grey_text">Tags:</div>
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
	</div>
	<?php } 
	
	// --- page selection ---
	if ($counter_current_ticket == 0)
	{
		echo '<div class="list_no_results">' . $LANG_BRICKS["TICKETS_LIST_NO_RES"] . ' </div>';
	}
	else 
	{
		echo '<div class="page_selection">Page:' . buildPageView($counter_current_ticket) . "</div>";
	}
	
	?>
	
	
	
	
		
</form>
