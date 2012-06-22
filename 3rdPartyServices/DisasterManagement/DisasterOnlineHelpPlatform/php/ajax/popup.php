
<?php   
//debugging
ini_set('display_errors', 1);

// --- get last ticket id ---
if (isset($_GET['user_id']))
{
	// check if a new ticket is available
	$sql = "SELECT t.*, t_t.name AS ticket_type, t_s.name AS ticket_status, u.first_name AS reporter_first_name, u.last_name AS reporter_last_name, u.institute AS reporter_institute, count(a.user_id) as count_answers
	FROM tickets AS t
	LEFT JOIN users AS u ON t.reporter_id = u.id
	LEFT JOIN tickets_states AS t_s ON t.ticket_status_id = t_s.id
	LEFT JOIN tickets_type AS t_t ON t.ticket_type_id = t_t.id
	LEFT JOIN nm_users_tickets AS a ON a.ticket_id = t.id
	GROUP BY t.short_descr  ORDER BY t.timestamp_created DESC limit 1";
	$res = mysql_query($sql);


	if($row = mysql_fetch_object($res))
	{
		// sets session var "last_tickets_id", if not available
		if (!isset($_SESSION['last_tickets_id']))
		{
			//echo "set last ticket id";
			// very first call of this script
			$_SESSION['last_tickets_id'] = $row->id;
			die();
		}
		// new ticket available
		else if ($_SESSION['last_tickets_id'] != $row->id)
		{
			//echo "new ticket";

			// checks if user and the new ticket has one or more similar tags
			$sql = "SELECT t.tag_id
			FROM nm_users_tags u
			LEFT JOIN nm_tickets_tags t ON u.tag_id=t.tag_id
			WHERE t.ticket_id='". mysql_real_escape_string($row->id) ."' AND u.user_id='". mysql_real_escape_string($_GET['user_id'])  ."'";
			$res = mysql_query($sql);


			if( mysql_fetch_object($res))
			{
				// show popup!!!!
			}
			else
			{
				//echo "no tags similar";
				// if no tags are similar -> stop script
				die();
			}


		}
		else
		{
			//echo "no new tickets";
			// no new tickets available
			die();
		}

	}
	else
	{
		// no ticket id available
		die();
	}

}
else if (isset($_GET['close_popup']))
{
	// --- popup is closed at index.php (and do not reappear) ---
	// check if a new ticket is available
	$sql = "SELECT t.*, t_t.name AS ticket_type, t_s.name AS ticket_status, u.first_name AS reporter_first_name, u.last_name AS reporter_last_name, u.institute AS reporter_institute, count(a.user_id) as count_answers
	FROM tickets AS t
	LEFT JOIN users AS u ON t.reporter_id = u.id
	LEFT JOIN tickets_states AS t_s ON t.ticket_status_id = t_s.id
	LEFT JOIN tickets_type AS t_t ON t.ticket_type_id = t_t.id
	LEFT JOIN nm_users_tickets AS a ON a.ticket_id = t.id
	GROUP BY t.short_descr  ORDER BY t.timestamp_created DESC limit 1";
	$res = mysql_query($sql);


	if($row = mysql_fetch_object($res))
	{
		$_SESSION['last_tickets_id'] = $row->id;
	}

	die();
}
else
{
	// no user id or close dialog action given (GET)
	die();
}

// --- if new ticket -> check tags for user ---

//$_SESSION['ajax_last_ticket_id'] = 5;

?>


<script language="JavaScript">
<!--
$(document).ready(function()     
{         
	$("#popup_close").click( function(i) {
     $("#ajaxcontent_popup").load("./php/ajax/popup.php?close_popup=1");
     $("#ajaxcontent_popup").html("");
  });
  
  	$("#popup_title").click( function(i) {
     
     $.ajax({
        url: "./php/ajax/popup.php?close_popup=1",
        context: document.body,
        success: function(){
        window.location.href = "index.php?site=tickets_process&id=<?php echo $row->id;?>";
        }
    }); 
    
  });
  
       
});

//-->
</script>

<div id="popup">

	<div align="right">
		<div id="popup_close">close</div>
	</div>

	<div>You seem to be an expert for a new request:</div>

	<div id="popup_title"
		style="font-size: 16px; text-decoration: underline; cursor: hand; cursor: pointer;">
		<?php echo $row->short_descr; ?>
	</div>

	<div class="popup_meta" align="center">
		<table class="popup_table">
			<tr>
				<td><b>Author:</b></td>
				<td><?php echo $row->reporter_last_name . ", " . $row->reporter_first_name ?>
				</td>
			</tr>
			<tr>
				<td><b>Institute:</b></td>
				<td><?php echo  $row->reporter_institute ?></td>
			</tr>
			<tr>
				<td><b>Tags:</b></td>
				<td><?php
				// get tags of ticket
				$sql = "SELECT  ta.id, ta.name "
				. "FROM nm_tickets_tags AS nm "
				. "LEFT JOIN tickets AS t ON nm.ticket_id = t.id "
				. "LEFT JOIN tags AS ta ON nm.tag_id = ta.id "
				. "WHERE t.id='" .  $row->id . "'"
				. "ORDER BY ta.name ASC";

				//echo $sql;
				$tag_res = mysql_query($sql);
				$tag_str = "";
				while($tag_row = mysql_fetch_object($tag_res))
				{
					$tag_str .= $tag_row->name . ", ";
				}
				echo substr($tag_str, 0, -2);
				?>
				</td>
			</tr>

		</table>
	</div>

</div>
