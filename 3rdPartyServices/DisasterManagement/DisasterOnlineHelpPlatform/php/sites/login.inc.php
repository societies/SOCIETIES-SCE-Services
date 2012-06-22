<?php  /* INC-Protection */ if ($_SERVER["PHP_SELF"] != $INC_SELF_PHP) { 
	echo "INC-PROTECTION!";die;
}  /* INC-Protection */ ?>

<div id="heading">
	<?php echo $LANG_BRICKS['LOGIN_HEADING']; ?>
</div>

<form name="loginform" id="loginform" action="./index.php?site=login"
	method="post">

	<div class="form_input_outer_container">

		<div class="form_input_inner_container">

			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS['LOGIN_EMAIL']; ?>
				</div>
				<input type="text" name="user_login" class="form_textfield"
					 />
			</div>
			
			<div class="dataset">
				<div class="label">
					<?php echo $LANG_BRICKS['LOGIN_PASSWORD']; ?>
				</div>
				<input type="password" name="user_passwd" class="form_textfield"
					/>
			</div>

			<div class="dataset">
				<a href="./index.php?site=users_create_modify"><?php echo $LANG_BRICKS['LOGIN_REGISTER']; ?>
				</a> <!-- <br> <a href="./index.php?site=users_forgot_passwd"><?php echo $LANG_BRICKS['LOGIN_FORGOT_PASSWD']; ?> -->
				</a>
			</div>

			<div class="buttons">
				<input type="submit" name="submit" class="form_button"
					value="<?php echo $LANG_BRICKS['LOGIN_BUTTON_LOGIN']; ?>"
					/>
			</div>

		</div>

	</div>

</form>
