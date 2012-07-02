<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>

<div id="heading">
	<?php echo $LANG_BRICKS['LANGUAGES_HEADING']; ?>
</div>

<form action="index.php?site=languages_list" method="post">


	<div style="float: left; width: 750px;">
		<p>
			<label><?php echo $LANG_BRICKS['LANGUAGES_HINT_LABEL']; ?> </label><br />
			<?php echo $LANG_BRICKS['LANGUAGES_HINT'] ?>
		</p>

		<p>
			<label><?php echo $LANG_BRICKS['LANGUAGES_SEARCH']; ?><br /> <input
				class="formstyle_textfield" class="textfield" type="text"
				name="search" id="search" class="input"
				value="<?php echo (isset($_POST['search']) ? $_POST['search'] : '') ?>"
				tabindex="20" /> </label>
		</p>
	</div>



	<div style="text-align: right;">
		<input type="button" class="form_button"
			style="width: 180px; margin-bottom: 10px;" name="create"
			onclick="window.location.href ='./index.php?site=languages_create_modify'"
			value="<?php echo $LANG_BRICKS['LANGUAGES_BUTTON_CREATE_NEW_LANGUAGE']; ?>">
		<br> <input type="submit" class="form_button" style="width: 180px;"
			value="<?php echo $LANG_BRICKS['LANGUAGES_BUTTON_REFRESH_RESULTS']; ?>" />
	</div>

	<div style="clear: both;"></div>
	</p>


	<p>
		<label><?php echo $LANG_BRICKS['LANGUAGES_RESULT']; ?><br />
			<table id="sortable_table" class="listing">
				<thead>
					<tr>
						<th><?php echo $LANG_BRICKS['LANGUAGES_RESULT']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['LANGUAGES_LANGUAGE']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['LANGUAGES_TRANS_AV']; ?>
						</th>
						<th><?php echo $LANG_BRICKS['LANGUAGES_OPTIONS']; ?>
						</th>
					</tr>
				</thead>
				<tbody>

					<?php

					// search string
					if (isset($_POST['search']))
					{
						$search =  "WHERE name LIKE '%". mysql_real_escape_string($_POST['search']) ."%' ";
					}
					else
					{
						$search = "";
					}

					// GETTING DATA
					$ticket_res = mysql_query("SELECT * FROM languages " . $search . " ORDER BY name ASC ");

					while($row = mysql_fetch_object($ticket_res))
					{
						echo "<tr>";
						echo "<td>" . $row->id . "</td>";
						echo "<td>" . $row->name . "</td>";
						echo "<td>" . check_language_file($row->name) . "</td>";
						echo "<td> <a href= 'index.php?site=languages_create_modify&id=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_EDIT'] . "' src='./images/edit.gif'></a> &nbsp; <a onclick=\"return confirm('". $LANG_BRICKS['CONFIRM_DELETE_LANG'] ."')\" href= 'index.php?site=languages_list&delete_lang=" . $row->id  ."'><img title='" . $LANG_BRICKS['TOOLTIP_DELETE'] . "' src='./images/delete.gif'></a> </td>";
						echo "</tr>";
					}


					?>

				</tbody>
			</table>
	
	</p>

</form>
