<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<?php

// ----------------------------------------------------------------------------
// --- Configs, Consts and Includes -------------------------------------------
// ----------------------------------------------------------------------------
//phpinfo();
/* DEBUGGING */
ini_set('display_errors', 1);
ini_set('upload_max_filesize', '8M');

/* START SESSION SYSTEM */
session_start();

/* CONFIG */
$CONFIG_ACTIONS_DIR = "./php/actions/";
$CONFIG_SITES_DIR = "./php/sites/";
$CONFIG_FUNCS_DIR = "./php/funcs/";
$CONFIG_LIBS_DIR = "./php/libs/";
$CONFIG_AJAX_DIR = "./php/ajax/";
$CONFIG_LANG_DIR = "./lang/";
$CONFIG_LANG_DEFAULT = "english";

/* CONSTS */
$ANONYMOUS_USER_ID = 1;
$TICKET_OPENED_ID = 1;
$TICKET_CLOSED_ID = 2;
$INC_SELF_PHP = "/societies/index.php";
$ITEMS_PER_PAGE = 10; // tickets per page at front page

/* ESCAPE GET-, POST- AND SESSION-PARAMS - SECURITY FEATURE */
//$_POST = array_map('htmlspecialchars', $_POST);
//$_GET = array_map('htmlspecialchars', $_GET);
//$_POST = array_map('trim', $_POST);
//$_GET = array_map('trim', $_GET);

/* INCLUDE PHP GENERAL LIBS AND FUNCS */
require($CONFIG_LIBS_DIR . "xmpphp/XMPPHP/XMPP.php");
require($CONFIG_FUNCS_DIR . 'general.inc.php');

/* DUMP */
/*
 echo 'GET: ';
var_dump($_GET);
echo '<br>';

echo 'POST: ';
var_dump($_POST);
echo '<br>';

echo 'SESSION: ';
var_dump($_SESSION);
echo '<br>';
*/


// ----------------------------------------------------------------------------
// --- Select appropriated actions --------------------------------------------
// ----------------------------------------------------------------------------

// --- TICKETS (LIST AND ADMIN) ---
if (!isset($_GET['site']) || $_GET['site'] == "tickets_list" || $_GET['site'] == "tickets_create_modify" || $_GET['site'] == "tickets_process" )
{
	/* PROCESS TICKETS FORM */
	require($CONFIG_ACTIONS_DIR . 'tickets.inc.php');

	if (isset($_GET['site']) && $_GET['site'] == "tickets_process" )
	{
		/* PROCESS ANSWERS FORM */
		require($CONFIG_ACTIONS_DIR . 'answers.inc.php');
	}

	if (isset($_GET['logout']))
	{
		/* LOGOUT USER */
		require($CONFIG_ACTIONS_DIR . 'login.inc.php');
	}

}

// --- LOGIN ---
else if ($_GET['site'] == "login")
{
	/* LOGIN USER */
	require($CONFIG_ACTIONS_DIR . 'login.inc.php');
}

// --- USER SETTINGS ---
else if ($_GET['site'] == "user_settings" && isset($_SESSION['user_id']) &&  $_SESSION['permissions']['perm_user_settings']==1)
{
	/* PROCESS USER SETTINGS FORM */
	require($CONFIG_ACTIONS_DIR . 'user_settings.inc.php');
}

// --- USERS ---
else if ( $_GET['site'] == "users_list" || $_GET['site'] == "users_create_modify")
{
	/* PROCESS USER FORM */
	require($CONFIG_ACTIONS_DIR . 'users.inc.php');
}

// --- LANGUAGE ADMIN ---
else if ( ($_GET['site'] == "languages_list" || $_GET['site'] == "languages_create_modify") && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_languages_admin']==1)
{
	/* PROCESS LANGUAGES FORM */
	require($CONFIG_ACTIONS_DIR . 'languages.inc.php');
}

// --- CONFIG ADMIN ---
else if ( $_GET['site'] == "config" && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_config_admin']==1)
{
	/* PROCESS CONFIG FORM */
	require($CONFIG_ACTIONS_DIR . 'config.inc.php');
}

// --- TAGS ADMIN ---
else if ( $_GET['site'] == "tags_list" && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_tags_admin']==1)
{
	/* PROCESS TAG FORM */
	require($CONFIG_ACTIONS_DIR . 'tags.inc.php');
}


?>

<html>
<head>
<title>Societies - DLR</title>
<meta http-equiv="X-UA-Compatible" content="IE=IE8" />
<link rel="stylesheet" href="./css/style.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="./css/smoothness/jquery-ui-1.8.18.css"
	type="text/css" media="screen" />

<script type="text/javascript" src="./js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="./js/jquery-ui-1.8.18.min.js"></script>
<script type="text/javascript" src="./js/jquery.tablesorter.js"></script>
<script type="text/javascript" src="./js/jquery.timepicker.js"></script>
<script type="text/javascript" src="./js/jquery.timers.js"></script>
<script type="text/javascript" src="./js/ckeditor/ckeditor.js"></script>

<?php
/* INCLUDE JS CODE IF A USER IS LOGGED IN */
if (isset($_SESSION['user_id']))
{
	?>
<script language="JavaScript">
<!--
$(document).ready(function()     
{
	// TABLESORTER
	$("#sortable_table").tablesorter();       
	
  	// POPUP
 	$(document).everyTime(2000, function(i) {
      $("#ajaxcontent_popup").load("<?php echo $CONFIG_AJAX_DIR?>popup.php?user_id=<?php echo $_SESSION['user_id']?>");
  	});       
});

//-->
</script>
<?php
}
?>

</head>
<body>

	<div id="container">

		<div id="logos">
			<a href="http://www.ict-societies.eu" target="_blank"> <img id="logo_societies"
				src="./images/logo_societies.png" alt="" height="100" />
			</a> <a href="http://www.dlr.de/kn" target="_blank"> <img id="logo_dlr"
				src="./images/logo_dlr.png" alt="" height="100" />
			</a>
		</div>

		<!-- NAVI -->
		<div id="navi">
			<ul>
				<li><a
					id="<?php echo (!isset($_GET['site']) || $_GET['site']=='tickets_list' ? 'current_page_item' : '') ?>"
					href="<?php echo $CONFIG_SITE_INDEX ?>"> <?php echo $LANG_BRICKS['NAVI_TICKETS'] ?>
				</a>
				</li>
				<?php
				if (isset($_SESSION['user_id']))
				{
					// --- user settings ---
					if( $_SESSION['permissions']['perm_user_settings'] == 1)
					{
						echo "<li> <a id='" . (isset($_GET['site']) && $_GET['site']=='user_settings' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?site=user_settings'>" . $LANG_BRICKS['NAVI_USER_SETTINGS'] . " </a></li>";
					}

					// --- tickets ---
					/*
					if( $_SESSION['permissions']['perm_tickets_list'] == 1)
					{
					echo "<li> <a id='" . (isset($_GET['site']) && $_GET['site']=='tickets_list' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?site=tickets_list'>" . $LANG_BRICKS['NAVI_TICKETS'] . " </a></li>";
					}*/

					// --- users ---
					if( $_SESSION['permissions']['perm_users_admin'] == 1)
					{
						echo "<li> <a id='" . (isset($_GET['site']) && $_GET['site']=='users_list' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?site=users_list'>" . $LANG_BRICKS['NAVI_USERS'] . " </a></li>";
					}

					// --- languages ---
					if( $_SESSION['permissions']['perm_languages_admin'] == 1)
					{
						echo "<li> <a id='" . (isset($_GET['site']) && $_GET['site']=='languages_list' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?site=languages_list'>" . $LANG_BRICKS['NAVI_LANGUAGES'] . " </a></li>";
					}

					// --- tags ---
					if( $_SESSION['permissions']['perm_tags_admin'] == 1)
					{
						echo "<li> <a id='" . (isset($_GET['site']) && $_GET['site']=='tags_list' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?site=tags_list'>" . $LANG_BRICKS['NAVI_TAGS'] . " </a></li>";
					}

					// --- Config ---
					if( $_SESSION['permissions']['perm_config_admin'] == 1)
					{
						echo "<li> <a id='" . (isset($_GET['site']) && $_GET['site']=='config' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?site=config'>" . $LANG_BRICKS['NAVI_CONFIG'] . " </a></li>";
					}
				}
				?>

				<li><a
					id="<?php echo (isset($_GET['site']) && $_GET['site']=='about' ? 'current_page_item' : '') ?>"
					href="<?php echo $CONFIG_SITE_INDEX . '?site=about' ?>"> <?php echo $LANG_BRICKS['NAVI_ABOUT'] ?>
				</a>
				</li>
				<!--
				<li>
					<a id="<?php echo (isset($_GET['site']) && $_GET['site']=='help' ? 'current_page_item' : '') ?>" href="<?php echo $CONFIG_SITE_INDEX . '?site=help' ?>"> <?php echo $LANG_BRICKS['NAVI_HELP'] ?> </a>
				</li>
        -->
				<li><a
					id="<?php echo (isset($_GET['site']) && $_GET['site']=='contact' ? 'current_page_item' : '') ?>"
					href="<?php echo $CONFIG_SITE_INDEX . '?site=contact' ?>"> <?php echo $LANG_BRICKS['NAVI_CONTACT'] ?>
				</a>
				</li>

				<?php
				if (isset($_SESSION['user_id']))
				{
					// --- logout ---
					echo "<li><b> <a id='" . (isset($_GET['site']) && $_GET['site']=='logout' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?logout=true'> " . $LANG_BRICKS['NAVI_LOGOUT'] . " </a></b></li>";
				}
				else
				{
					// --- login ---
					echo "<li><b> <a id='" . (isset($_GET['site']) && $_GET['site']=='login' ? 'current_page_item' : '') . "' href='" . $CONFIG_SITE_INDEX . "?site=login'>" . $LANG_BRICKS['NAVI_LOGIN'] . " </a></b></li>";
				}
				?>

			</ul>


			<!-- LANGUAGE SELECTION -->

			<?php
			if (!isset($_SESSION['user_id']))
			{
				?>
			<span>
				<form action="./index.php" method="get">
					<select class="form_textfield_without_width" type="text"
						name="set_language" tabindex="50" onChange="this.form.submit()">
						<?php
						$res_lang = mysql_query("SELECT * FROM languages ORDER BY name ASC");
						while($row_lang = mysql_fetch_object($res_lang))
						{
							if (check_language_file($row_lang->name) == $LANG_BRICKS['LANGUAGES_YES'])
							{
								echo "<option " . ($_SESSION['user_language'] == $row_lang->name ? 'selected' : '') . " value='". $row_lang->name ."'> " . $row_lang->name;
							}
						}
						?>
					</select>
				</form>
			</span>
			<?php
			}
			?>


			<div style="clear: both;"></div>

		</div>

		<?php
		/* SHOW INFO AND ERRORS */
		info_and_error_display();
		?>


		<!-- MAIN CONTENT -->
		<div id="content">

			<?php

			// ----------------------------------------------------------------------------
			// --- Select content ---------------------------------------------------------
			// ----------------------------------------------------------------------------

			if ( !isset($_GET['site']) || $_GET['site'] == 'tickets_list' )
			{
				require($CONFIG_SITES_DIR . 'tickets_list.inc.php');
			}
			else if ($_GET['site'] == 'contact')
			{
				require($CONFIG_SITES_DIR . 'contact.inc.php');
			}
			else if ($_GET['site'] == 'about')
			{
				require($CONFIG_SITES_DIR . 'about.inc.php');
			}
			else if ($_GET['site'] == 'help')
			{
				require($CONFIG_SITES_DIR . 'help.inc.php');
			}
			else if ($_GET['site'] == 'tickets_process')
			{
				require($CONFIG_SITES_DIR . 'tickets_process.inc.php');
			}
			else if ($_GET['site'] == 'tickets_create_modify' && isset($_SESSION['user_id']))
			{
				require($CONFIG_SITES_DIR . 'tickets_create_modify.inc.php');
			}
			else if ($_GET['site'] == 'languages_list' && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_languages_admin'] == 1)
			{
				require($CONFIG_SITES_DIR .'languages_list.inc.php');
			}
			else if ($_GET['site'] == 'languages_create_modify' && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_languages_admin'] == 1)
			{
				require($CONFIG_SITES_DIR . 'languages_create_modify.inc.php');
			}
			else if ($_GET['site'] == 'tags_list' && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_tags_admin'] == 1)
			{
				require($CONFIG_SITES_DIR .'tags_list.inc.php');
			}
			else if ($_GET['site'] == 'users_list' && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_users_admin'] == 1)
			{
				require($CONFIG_SITES_DIR . 'users_list.inc.php');
			}
			else if ($_GET['site'] == 'users_create_modify')
			{
				require($CONFIG_SITES_DIR . 'users_create_modify.inc.php');
			}
			else if ($_GET['site'] == 'config' && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_config_admin'] == 1)
			{
				require($CONFIG_SITES_DIR . 'config.inc.php');
			}
			else if ($_GET['site'] == 'user_settings' && isset($_SESSION['user_id']) && $_SESSION['permissions']['perm_user_settings'] == 1)
			{
				require($CONFIG_SITES_DIR . 'user_settings.inc.php');
			}
			else if ($_GET['site'] == 'login' && !isset($_SESSION['user_id']))
			{
				require($CONFIG_SITES_DIR . 'login.inc.php');
			}
			else if ($_GET['site'] == 'logout' )
			{
				require($CONFIG_SITES_DIR . 'logout.inc.php');
			}
			else if ($_GET['site'] == 'user_activation' )
			{
				require($CONFIG_SITES_DIR . 'user_activation.inc.php');
			}
			else
			{
				// --- check if ticket_id is int ---
				require($CONFIG_SITES_DIR . 'bad_request.inc.php');
			}

			?>

		</div>

		<div id="bottom">Copyright © 2012 DLR OP KN</div>

	</div>
	<!-- end #container -->

	<!-- PLACEHOLDER AJAXCONTENT (POPUP) -->
	<div id="ajaxcontent_popup"></div>

</body>
</html>
