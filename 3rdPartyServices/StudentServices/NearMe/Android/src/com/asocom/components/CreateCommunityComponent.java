package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class CreateCommunityComponent extends LinearLayout {

	/**
	 * Numero maximo de caracteres del campo nombre
	 */
	private static final int NAME_MAX_LENGTH = 8;

	/**
	 * Numero maximo de caracteres del campo description
	 */
	private static final int DESCRIPTION_MAX_LENGTH = 200;

	/**
	 * Numero maximo de caracteres del campo date
	 */
	public static final int PASSWORD_MAX_LENGTH = 8;

	private ImageView image;
	private EditText communityName;
	private EditText communityDescription;
	private RadioGroup radioGroup;
	private RadioGroup radioGroup2;
	private Spinner category;
	private Spinner interest;
	private Spinner preference;
	private Button save;
	private Button cancel;
	private Context context;

	private int posCategory;
	private int posInterest;
	private int posPreference;
	private String[] arrayCategory;
	private String[][] arrayInterest;
	private String[][] arrayPreference;
	private String[] listInterest;
	private String[] listPreference;
	private String profile;

	public CreateCommunityComponent(Context context) {
		super(context);
		this.context = context;
		init(context);

	}

	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.create_community, this, true);
		image = (ImageView) findViewById(R.id.create_community_image);
		communityName = (EditText) findViewById(R.id.create_community_name);
		communityName
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						NAME_MAX_LENGTH) });
		communityDescription = (EditText) findViewById(R.id.create_community_description);
		communityDescription
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						DESCRIPTION_MAX_LENGTH) });
		radioGroup = (RadioGroup) findViewById(R.id.create_community_radio_group);
		radioGroup2 = (RadioGroup) findViewById(R.id.create_community_radio_group2);
		category = (Spinner) findViewById(R.id.create_community_category);
		interest = (Spinner) findViewById(R.id.create_community_interest);
		preference = (Spinner) findViewById(R.id.create_community_preference);
		save = (Button) findViewById(R.id.create_community_save);
		cancel = (Button) findViewById(R.id.create_community_cancel);

		profile = "";
		setArrayCategory();
		setArrayInterest();
		setArrayPreference();

	}

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
						profile = arrayCategory[posCategory] + ","
								+ listInterest[posInterest] + ","
								+ listPreference[posPreference];
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

	}

	public String getProfile() {
		return profile;
	}

	public void setSpinner(String[] array_spinner) {

	}

	public void setImage(int icon) {
		image.setImageResource(Tools.userImage(icon));
	}

	public String getCommunityName() {
		return communityName.getText().toString();
	}

	public void setCommunityName(String text) {
		this.communityName.setText(text);
	}

	public String getCommunityDescription() {
		return communityDescription.getText().toString();
	}

	public void setCommunityDescription(String text) {
		this.communityDescription.setText(text);
	}

	public RadioGroup getRadioGroup() {
		return radioGroup;
	}

	public RadioGroup getRadioGroup2() {
		return radioGroup2;
	}

	public Spinner getCategory() {
		return category;
	}

	public Button getSave() {
		return save;
	}

	public Button getCancel() {
		return cancel;
	}

}
