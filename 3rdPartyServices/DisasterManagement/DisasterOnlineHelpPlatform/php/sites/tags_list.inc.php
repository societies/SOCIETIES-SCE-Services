<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>

<div id="heading">
	<?php echo $LANG_BRICKS['TAGS_HEADING']; ?>
</div>

<form action="index.php?site=tags_list" method="post">


	<div style="float: left; width: 750px;">


		<p>
			<label><?php echo $LANG_BRICKS['TAGS_SEARCH']; ?><br /> <input
				class="formstyle_textfield" class="textfield" type="text"
				name="search" id="search" class="input"
				value="<?php echo (isset($_POST['search']) ? $_POST['search'] : '') ?>"
				tabindex="20" /> </label>
		</p>
	</div>



	<div style="text-align: right;">
		<input type="submit" class="form_button" style="width: 180px;"
			value="<?php echo $LANG_BRICKS['TAGS_BUTTON_REFRESH_RESULTS']; ?>" />
	</div>

	<div style="clear: both;"></div>
	</p>


	<p>
	
	
	<table id="sortable_table" class="listing">
		<thead>
			<tr>
				<th><?php echo $LANG_BRICKS['TAGS_ID']; ?>
				</th>
				<th><?php echo $LANG_BRICKS['TAGS_NAME']; ?>
				</th>
				<th><?php echo $LANG_BRICKS['TAGS_COUNT_TICKETS']; ?>
				</th>
				<th><?php echo $LANG_BRICKS['TAGS_COUNT_USERS']; ?>
				</th>
				<th><?php echo $LANG_BRICKS['TAGS_OPTIONS']; ?>
				</th>
			</tr>
		</thead>
		<tbody>

			<?php

			// search string
			if (isset($_POST['search']))
			{
				$search =  "WHERE t.name LIKE '%". mysql_real_escape_string($_POST['search']) ."%' ";
			}
			else
			{
				$search = "";
			}

			// GETTING DATA
			$sql = "SELECT t.*,
			(SELECT COUNT(*) FROM nm_users_tags ut WHERE ut.tag_id=t.id) AS count_users,
			(SELECT COUNT(*) FROM nm_tickets_tags tt WHERE tt.tag_id=t.id) AS count_tickets
			FROM tags AS t
			". $search ."
			ORDER BY t.name ASC";


			$res = mysql_query($sql);

			//echo "sql:" . $sql;
			while($row = mysql_fetch_object($res))
			{
				echo "<tr>";
				echo "<td>" . $row->id . "</td>";
				echo "<td>" . $row->name . "</td>";
				echo "<td>" . $row->count_tickets . "</td>";
				echo "<td>" . $row->count_users . "</td>";

				if ($row->count_tickets==0 && $row->count_users==0)
				{
					echo "<td><a onclick=\"return confirm('". $LANG_BRICKS['CONFIRM_DELETE_TAG'] ."')\" href= 'index.php?site=tags_list&delete_tag=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_DELETE'] . "' src='./images/delete.gif'></a> </td>";
				}
				else
				{
					echo "<td></td>";
				}
				echo "</tr>";
			}


			?>

		</tbody>
	</table>
	</p>

</form>
