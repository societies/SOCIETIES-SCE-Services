/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.model.Manager;
import com.asocom.tools.Tools;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class UserProfileComponent.
 */
public class UserProfileComponent extends LinearLayout {

	/** Numero maximo de caracteres del campo nombre. */
	private static final int NAME_MAX_LENGTH = 16;

	/** The name. */
	private EditText name;
	
	/** The category. */
	private Spinner category;
	
	/** The interest. */
	private Spinner interest;
	
	/** The preference. */
	private Spinner preference;
	
	/** The save. */
	private Button save;
	
	/** The ok. */
	private Button ok;
	
	/** The text. */
	private TextView text;
	
	/** The context. */
	private Context context;
	
	/** The pos category. */
	private int posCategory;
	
	/** The pos interest. */
	private int posInterest;
	
	/** The pos preference. */
	private int posPreference;
	
	/** The array category. */
	private String[] arrayCategory;
	
	/** The array interest. */
	private String[][] arrayInterest;
	
	/** The array preference. */
	private String[][] arrayPreference;
	
	/** The list interest. */
	private String[] listInterest;
	
	/** The list preference. */
	private String[] listPreference;
	
	/** The profile. */
	private String profile;
	
	/** The show profile. */
	private String showProfile;

	/**
	 * Instantiates a new user profile component.
	 *
	 * @param context the context
	 */
	public UserProfileComponent(Context context) {
		super(context);
		this.context = context;
		init(context);
	}

	/**
	 * Inits the.
	 *
	 * @param context the context
	 */
	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.user_profile, this, true);

		name = (EditText) findViewById(R.id.user_profile_name);
		name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				NAME_MAX_LENGTH) });
		category = (Spinner) findViewById(R.id.user_profile_category);
		interest = (Spinner) findViewById(R.id.user_profile_interest);
		preference = (Spinner) findViewById(R.id.user_profile_preference);
		text = (TextView) findViewById(R.id.user_profile_text);
		save = (Button) findViewById(R.id.user_profile_save);
		ok = (Button) findViewById(R.id.user_profile_ok);

		save.setOnClickListener((OnClickListener) context);
		ok.setOnClickListener((OnClickListener) context);
		profile = "";
		showProfile = "";
		setArrayCategory();
		setArrayInterest();
		setArrayPreference();

	}

	/**
	 * On click.
	 *
	 * @param v the v
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case (R.id.user_profile_save):

			if (!showProfile.equals("")) {

				String[] profiles = showProfile.split("\n");
				if (profiles.length == 8) {
					Tools.alertDialog(context, "Alert", "Allow just 8 items");
					break;
				}

				int exit = 0;
				for (int i = 0; i < profiles.length; i++) {
					if (profiles[i].equals(arrayCategory[posCategory] + " > "
							+ listInterest[posInterest] + " > "
							+ listPreference[posPreference])) {
						Tools.alertDialog(context, "Alert",
								"This choisse already exist");
						exit = 1;
						break;
					}
				}
				if (exit == 1) {
					break;
				}

			}

			if (!profile.equals("")) {
				showProfile = showProfile + "\n" + arrayCategory[posCategory]
						+ " > " + listInterest[posInterest] + " > "
						+ listPreference[posPreference];
				profile = profile + "," + arrayCategory[posCategory] + ","
						+ listInterest[posInterest] + ","
						+ listPreference[posPreference];
			} else {
				showProfile = arrayCategory[posCategory] + " > "
						+ listInterest[posInterest] + " > "
						+ listPreference[posPreference];
				profile = arrayCategory[posCategory] + ","
						+ listInterest[posInterest] + ","
						+ listPreference[posPreference];
			}

			text.setText(showProfile);
			break;

		case (R.id.user_profile_ok):

			if (profile.equals("")) {
				Tools.alertDialog(context, "Alert", "Empty item");
				break;
			}

			if (name.getText().toString().equals("")) {
				Tools.alertDialog(context, "Alert", "Empty name");
				break;
			}

			String profileString = (Manager.getDb().getUserAsArray(Manager
					.getCurrentPhoneUser().get(4))).get(7);

			if (profileString.equals("")) {
				profile = "0-" + name.getText().toString() + ":" + profile;
				Manager.getDb().addProfile(
						Manager.getCurrentPhoneUser().get(4), profile);
				profile = "-1";
				break;
			}

			String[] profilesString = profileString.split("-");
			int exit = 0;
			for (int i = 0; i < profilesString.length; i++) {
				String[] profileName = profilesString[i].split(":");
				if (profileName[0].equals(name.getText().toString())) {
					exit = 1;
					Tools.alertDialog(context, "Alert",
							"This profile name already exists");
					break;
				}
			}
			if (exit == 1) {
				break;
			}
			;

			profile = profileString + "-" + name.getText().toString() + ":"
					+ profile;
			Manager.getDb().addProfile(Manager.getCurrentPhoneUser().get(4),
					profile);
			profile = "-1";
			break;
		}
	}

	/**
	 * Sets the array category.
	 */
	private void setArrayCategory() {

		posCategory = 0;
		posInterest = 0;
		posPreference = 0;
		arrayCategory = new String[5];

		arrayCategory[0] = "Music";
		arrayCategory[1] = "Sport";
		arrayCategory[2] = "Film";
		arrayCategory[3] = "Art";
		arrayCategory[4] = "Party";

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, arrayCategory);

		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category.setAdapter(adaptador);

		category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,
					android.view.View v, int position, long id) {
				posCategory = position;
				setArrayInterest();

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	/**
	 * Sets the array interest.
	 */
	private void setArrayInterest() {

		arrayInterest = new String[5][3];
		arrayInterest[0][1] = "Classic";
		arrayInterest[0][2] = "Pop";
		arrayInterest[0][0] = "General";
		arrayInterest[1][1] = "Basketball";
		arrayInterest[1][2] = "Football";
		arrayInterest[1][0] = "General";
		arrayInterest[2][1] = "Action";
		arrayInterest[2][2] = "Romance";
		arrayInterest[2][0] = "General";
		arrayInterest[3][1] = "Sculpture";
		arrayInterest[3][2] = "Pinture";
		arrayInterest[3][0] = "General";
		arrayInterest[4][1] = "Birthday";
		arrayInterest[4][2] = "Work";
		arrayInterest[4][0] = "General";
		listInterest = new String[arrayInterest[posCategory].length];

		for (int i = 0; i < arrayInterest[posCategory].length; i++) {
			listInterest[i] = arrayInterest[posCategory][i];
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, listInterest);

		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		interest.setAdapter(adaptador);
		interest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,
					android.view.View v, int position, long id) {
				posInterest = position;
				setArrayPreference();
				if (posInterest == 0) {
					preference.setEnabled(false);
				} else {
					preference.setEnabled(true);
				}

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	/**
	 * Sets the array preference.
	 */
	private void setArrayPreference() {

		arrayPreference = new String[11][3];
		arrayPreference[0][2] = "";
		arrayPreference[0][1] = "";
		arrayPreference[0][0] = "General";
		arrayPreference[1][2] = "Vivaldi";
		arrayPreference[1][1] = "Bach";
		arrayPreference[1][0] = "General";
		arrayPreference[2][2] = "Bon Jovi";
		arrayPreference[2][1] = "Elton John";
		arrayPreference[2][0] = "General";
		arrayPreference[3][2] = "Chicago Bull";
		arrayPreference[3][1] = "Los Angeles Lakers";
		arrayPreference[3][0] = "General";
		arrayPreference[4][2] = "Real Madrid";
		arrayPreference[4][1] = "Paris Saint-Germain";
		arrayPreference[4][0] = "General";
		arrayPreference[5][2] = "Fast and Furious V";
		arrayPreference[5][1] = "Avatar";
		arrayPreference[5][0] = "General";
		arrayPreference[6][2] = "Titanic";
		arrayPreference[6][1] = "Anna and the king";
		arrayPreference[6][0] = "General";
		arrayPreference[7][2] = "Statue";
		arrayPreference[7][1] = "Architectural Sculpture";
		arrayPreference[7][0] = "General";
		arrayPreference[8][2] = "Impressionism";
		arrayPreference[8][1] = "Abstrat Styles";
		arrayPreference[8][0] = "General";
		arrayPreference[9][2] = "Family";
		arrayPreference[9][1] = "Friends";
		arrayPreference[9][0] = "General";
		arrayPreference[10][2] = "Socialization";
		arrayPreference[10][1] = "Bussiness celebration";
		arrayPreference[10][0] = "General";

		listPreference = new String[arrayPreference[posCategory * 2
				+ posInterest].length];
		for (int i = 0; i < arrayPreference[posCategory * 2 + posInterest].length; i++) {
			listPreference[i] = arrayPreference[posCategory * 2 + posInterest][i];
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, listPreference);

		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preference.setAdapter(adaptador);

		preference
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							android.view.View v, int position, long id) {
						posPreference = position;
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * Sets the profile.
	 *
	 * @param profile the new profile
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * Sets the spinner.
	 *
	 * @param array_spinner the new spinner
	 */
	public void setSpinner(String[] array_spinner) {

	}

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public Spinner getCategory() {
		return category;
	}

	/**
	 * Gets the save.
	 *
	 * @return the save
	 */
	public Button getSave() {
		return save;
	}

}
