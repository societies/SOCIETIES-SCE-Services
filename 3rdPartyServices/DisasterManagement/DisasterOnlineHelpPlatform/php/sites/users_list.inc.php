<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>


<div id="heading">
	<?php echo $LANG_BRICKS['USERS_HEADING']; ?>
</div>

<form id="form1" action="index.php?site=users_list" method="post">

	<p>
	
	
	<div style="float: left;">
		<p>
			<label><?php echo $LANG_BRICKS['USERS_SEARCH']; ?><br /> <input
				class="formstyle_textfield" class="textfield" type="text"
				name="search" id="search" class="input"
				value="<?php echo (isset($_POST['search']) ? $_POST['search'] : '') ?>"
				tabindex="20" /> </label>
		</p>
	</div>

	<div style="text-align: right;">
		<input type="button" class="form_button"
			style="width: 180px; margin-bottom: 10px;" name="create"
			onclick="window.location.href ='./index.php?site=users_create_modify'"
			value="<?php echo $LANG_BRICKS['USERS_BUTTON_CREATE_NEW_USER']; ?>">
		<br> <input type="submit" class="form_button" style="width: 180px;"
			value="<?php echo $LANG_BRICKS['USERS_BUTTON_REFRESH_RESULTS']; ?>" />
	</div>

	<div style="clear: both;"></div>
	</p>


	<p>
		<label><?php echo $LANG_BRICKS['USERS_RESULT']; ?><br />
			<table id="sortable_table" class="listing">
				<thead>
					<tr>
						<th><?php echo $LANG_BRICKS['USERS_ID']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_NAME']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_EMAIL']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_INSTITUTE']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_GROUP']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_LANGUAGE']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_NOTIFY_TICKET']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_NOTIFY_ANSWER']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['USERS_OPTIONS']; ?>
						</th>
					</tr>
				</thead>
				<tbody>

					<?php

					// search string
					if (isset($_POST['search']))
					{
						$search =  "AND ( u.last_name LIKE '%". $_POST['search'] ."%' OR u.first_name LIKE '%". mysql_real_escape_string($_POST['search']) ."%' OR u.email LIKE '%". mysql_real_escape_string($_POST['search']) ."%' )";
					}
					else
					{
						$search = "";
					}
					//echo $search;

					// GETTING DATA
					$ticket_res = mysql_query("SELECT u . * , g.name AS group_name, l.name AS language_name FROM users AS u, groups AS g, languages AS l WHERE u.group = g.id AND u.language = l.id ". $search ." ORDER BY last_name ASC ");

					while($row = mysql_fetch_object($ticket_res))
					{
						if ($row->id != 1) // anonymous user
						{
							echo "<tr>";
							echo "<td>" . $row->id . "</td>";
							echo "<td>" . $row->last_name . ", " . $row->first_name . "</td>";
							echo "<td>" . $row->email . "</td>";
							echo "<td>" . $row->institute . "</td>";
							echo "<td>" . $row->group_name . "</td>";
							echo "<td>" . $row->language_name . "</td>";
							echo "<td>" . $row->notify_on_new_ticket . "</td>";
							echo "<td>" . $row->notify_on_new_answer . "</td>";
								
							echo "<td> <a href= 'index.php?site=users_create_modify&id=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_EDIT'] . "' src='./images/edit.gif'></a> &nbsp; <a onclick=\"return confirm('". $LANG_BRICKS['CONFIRM_DELETE_USER'] ."')\" href= 'index.php?site=users_list&delete_user=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_DELETE'] . "' src='./images/delete.gif'></a> </td>";
							echo "</tr>";
						}
					}


					?>

				</tbody>
			</table>
	
	</p>

</form>
