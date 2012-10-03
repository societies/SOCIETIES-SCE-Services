/*
 * 
 */
package com.asocom.tools;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseManager.
 */
public class DatabaseManager {
	// the Activity or Application that is creating an object from this class.
	/** The context. */
	Context context;

	// a reference to the database used by this application/object
	/** The db. */
	private SQLiteDatabase db;

	// These constants are specific to the database. They should be
	// changed to suit your needs.
	/** The D b_ name. */
	private final String DB_NAME = "PVCDataBase";
	
	/** The D b_ version. */
	private final int DB_VERSION = 1;

	// These constants are specific to the database table. They should be
	// changed to suit your needs.
	/** The TABL e1_ name. */
	private final String TABLE1_NAME = "users";
	
	/** The TABL e1_ ro w_ id. */
	private final String TABLE1_ROW_ID = "id";
	
	/** The TABL e1_ ro w_ name. */
	private final String TABLE1_ROW_NAME = "name";
	
	/** The TABL e1_ ro w_ description. */
	private final String TABLE1_ROW_DESCRIPTION = "description";
	
	/** The TABL e1_ ro w_ dat e_ o f_ birth. */
	private final String TABLE1_ROW_DATE_OF_BIRTH = "date_of_birth";
	
	/** The TABL e1_ ro w_ gender. */
	private final String TABLE1_ROW_GENDER = "gender";
	
	/** The TABL e1_ ro w_ email. */
	private final String TABLE1_ROW_EMAIL = "email";
	
	/** The TABL e1_ ro w_ password. */
	private final String TABLE1_ROW_PASSWORD = "password";
	
	/** The TABL e1_ ro w_ image. */
	private final String TABLE1_ROW_IMAGE = "image";
	
	/** The TABL e1_ ro w_ profile. */
	private final String TABLE1_ROW_PROFILE = "profile";
	
	/** The NUMBE r_ o f_ field. */
	public final int NUMBER_OF_FIELD = 8;

	/** The TABL e2_ name. */
	private final String TABLE2_NAME = "friends";
	
	/** The TABL e2_ ro w_ id. */
	private final String TABLE2_ROW_ID = "id";
	
	/** The TABL e2_ ro w_ name. */
	private final String TABLE2_ROW_NAME = "name";
	
	/** The TABL e2_ ro w_ description. */
	private final String TABLE2_ROW_DESCRIPTION = "description";
	
	/** The TABL e2_ ro w_ dat e_ o f_ birth. */
	private final String TABLE2_ROW_DATE_OF_BIRTH = "date_of_birth";
	
	/** The TABL e2_ ro w_ gender. */
	private final String TABLE2_ROW_GENDER = "gender";
	
	/** The TABL e2_ ro w_ email. */
	private final String TABLE2_ROW_EMAIL = "email";
	
	/** The TABL e2_ ro w_ use r_ email. */
	private final String TABLE2_ROW_USER_EMAIL = "user_email";
	
	/** The TABL e2_ ro w_ image. */
	private final String TABLE2_ROW_IMAGE = "image";

	/**
	 * Instantiates a new database manager.
	 *
	 * @param context the context
	 */
	public DatabaseManager(Context context) {
		this.context = context;
		// create or open the database
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
	}

	/**
	 * Closedb.
	 */
	public void closedb() {
		db.close();
	}

	/**
	 * Adds the user.
	 *
	 * @param name the name
	 * @param description the description
	 * @param date_of_birth the date_of_birth
	 * @param gender the gender
	 * @param email the email
	 * @param password the password
	 * @param image the image
	 */
	public void addUser(String name, String description, String date_of_birth,
			String gender, String email, String password, String image) {

		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put(TABLE1_ROW_NAME, name);
		values.put(TABLE1_ROW_DESCRIPTION, description);
		values.put(TABLE1_ROW_DATE_OF_BIRTH, date_of_birth);
		values.put(TABLE1_ROW_GENDER, gender);
		values.put(TABLE1_ROW_EMAIL, email);
		values.put(TABLE1_ROW_PASSWORD, password);
		values.put(TABLE1_ROW_IMAGE, image);
		values.put(TABLE1_ROW_PROFILE, "");

		// ask the database object to insert the new data
		try {
			db.insert(TABLE1_NAME, null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Adds the friend.
	 *
	 * @param name the name
	 * @param description the description
	 * @param date_of_birth the date_of_birth
	 * @param gender the gender
	 * @param email the email
	 * @param user_email the user_email
	 * @param image the image
	 */
	public void addFriend(String name, String description,
			String date_of_birth, String gender, String email,
			String user_email, String image) {

		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put(TABLE2_ROW_NAME, name);
		values.put(TABLE2_ROW_DESCRIPTION, description);
		values.put(TABLE2_ROW_DATE_OF_BIRTH, date_of_birth);
		values.put(TABLE2_ROW_GENDER, gender);
		values.put(TABLE2_ROW_EMAIL, email);
		values.put(TABLE2_ROW_USER_EMAIL, user_email);
		values.put(TABLE2_ROW_IMAGE, image);

		// ask the database object to insert the new data
		try {
			db.insert(TABLE2_NAME, null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Delete user.
	 *
	 * @param email the email
	 */
	public void deleteUser(String email) {

		// Eliminar un registro
		// db.execSQL("DELETE FROM TABLE1_NAME WHERE TABLE1_ROW_NAME=Frand");

		// ask the database manager to delete the row of given id
		try {
			db.delete(TABLE1_NAME, TABLE1_ROW_EMAIL + "=" + "'" + email + "'",
					null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Delete friend.
	 *
	 * @param email the email
	 */
	public void deleteFriend(String email) {
		// ask the database manager to delete the row of given id
		try {
			db.delete(TABLE2_NAME, TABLE2_ROW_EMAIL + "=" + "'" + email + "'",
					null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Update user.
	 *
	 * @param name the name
	 * @param description the description
	 * @param date_of_birth the date_of_birth
	 * @param gender the gender
	 * @param email the email
	 * @param password the password
	 * @param image the image
	 */
	public void updateUser(String name, String description,
			String date_of_birth, String gender, String email, String password,
			String image) {
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put(TABLE1_ROW_NAME, name);
		values.put(TABLE1_ROW_DESCRIPTION, description);
		values.put(TABLE1_ROW_DATE_OF_BIRTH, date_of_birth);
		values.put(TABLE1_ROW_GENDER, gender);
		values.put(TABLE1_ROW_EMAIL, email);
		values.put(TABLE1_ROW_PASSWORD, password);
		values.put(TABLE1_ROW_IMAGE, image);

		// ask the database object to update the database row of given rowID
		try {
			db.update(TABLE1_NAME, values, TABLE1_ROW_EMAIL + "=" + "'" + email
					+ "'", null);
		} catch (Exception e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Adds the profile.
	 *
	 * @param email the email
	 * @param profile the profile
	 */
	public void addProfile(String email, String profile) {
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put(TABLE1_ROW_PROFILE, profile);

		// ask the database object to update the database row of given rowID
		try {
			db.update(TABLE1_NAME, values, TABLE1_ROW_EMAIL + "=" + "'" + email
					+ "'", null);
		} catch (Exception e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Update friend.
	 *
	 * @param name the name
	 * @param description the description
	 * @param date_of_birth the date_of_birth
	 * @param gender the gender
	 * @param email the email
	 * @param user_email the user_email
	 * @param image the image
	 */
	public void updateFriend(String name, String description,
			String date_of_birth, String gender, String email,
			String user_email, String image) {
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put(TABLE2_ROW_NAME, name);
		values.put(TABLE2_ROW_DESCRIPTION, description);
		values.put(TABLE2_ROW_DATE_OF_BIRTH, date_of_birth);
		values.put(TABLE2_ROW_GENDER, gender);
		values.put(TABLE2_ROW_EMAIL, email);
		values.put(TABLE2_ROW_USER_EMAIL, user_email);
		values.put(TABLE2_ROW_IMAGE, image);

		// ask the database object to update the database row of given rowID
		try {
			db.update(TABLE2_NAME, values, TABLE2_ROW_EMAIL + "=" + "'" + email
					+ "'", null);
		} catch (Exception e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Gets the user as array.
	 *
	 * @param email the email
	 * @return the user as array
	 */
	public ArrayList<String> getUserAsArray(String email) {

		ArrayList<String> rowArray = new ArrayList<String>();
		Cursor cursor;

		try {
			cursor = db.query(TABLE1_NAME, new String[] { TABLE1_ROW_NAME,
					TABLE1_ROW_DESCRIPTION, TABLE1_ROW_DATE_OF_BIRTH,
					TABLE1_ROW_GENDER, TABLE1_ROW_EMAIL, TABLE1_ROW_PASSWORD,
					TABLE1_ROW_IMAGE, TABLE1_ROW_PROFILE }, TABLE1_ROW_EMAIL
					+ "=" + "'" + email + "'", null, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					rowArray.add(cursor.getString(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
					rowArray.add(cursor.getString(3));
					rowArray.add(cursor.getString(4));
					rowArray.add(cursor.getString(5));
					rowArray.add(cursor.getString(6));
					rowArray.add(cursor.getString(7));
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		return rowArray;
	}

	/**
	 * Gets the friend as array.
	 *
	 * @param email the email
	 * @return the friend as array
	 */
	public ArrayList<String> getFriendAsArray(String email) {

		ArrayList<String> rowArray = new ArrayList<String>();
		Cursor cursor;

		try {
			cursor = db.query(TABLE2_NAME, new String[] { TABLE2_ROW_NAME,
					TABLE2_ROW_DESCRIPTION, TABLE2_ROW_DATE_OF_BIRTH,
					TABLE2_ROW_GENDER, TABLE2_ROW_EMAIL, TABLE2_ROW_USER_EMAIL,
					TABLE2_ROW_IMAGE }, TABLE2_ROW_USER_EMAIL + "=" + "'"
					+ email + "'", null, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					rowArray.add(cursor.getString(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
					rowArray.add(cursor.getString(3));
					rowArray.add(cursor.getString(4));
					rowArray.add(cursor.getString(5));
					rowArray.add(cursor.getString(6));
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		return rowArray;
	}

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	public ArrayList<String> getUsers() {

		ArrayList<String> rowArray = new ArrayList<String>();
		Cursor cursor;

		try {
			cursor = db.query(TABLE1_NAME, new String[] { TABLE1_ROW_NAME,
					TABLE1_ROW_DESCRIPTION, TABLE1_ROW_DATE_OF_BIRTH,
					TABLE1_ROW_GENDER, TABLE1_ROW_EMAIL, TABLE1_ROW_PASSWORD,
					TABLE1_ROW_IMAGE, TABLE1_ROW_PROFILE }, TABLE1_ROW_EMAIL
					+ "!=" + "'" + null + "'", null, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					rowArray.add(cursor.getString(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
					rowArray.add(cursor.getString(3));
					rowArray.add(cursor.getString(4));
					rowArray.add(cursor.getString(5));
					rowArray.add(cursor.getString(6));
					rowArray.add(cursor.getString(7));
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		return rowArray;
	}

	/**
	 * Gets the friends.
	 *
	 * @return the friends
	 */
	public ArrayList<String> getFriends() {

		ArrayList<String> rowArray = new ArrayList<String>();
		Cursor cursor;

		try {
			cursor = db.query(TABLE1_NAME, new String[] { TABLE2_ROW_NAME,
					TABLE2_ROW_DESCRIPTION, TABLE2_ROW_DATE_OF_BIRTH,
					TABLE2_ROW_GENDER, TABLE2_ROW_EMAIL, TABLE2_ROW_USER_EMAIL,
					TABLE2_ROW_IMAGE }, TABLE2_ROW_USER_EMAIL + "!=" + "'"
					+ null + "'", null, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					rowArray.add(cursor.getString(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
					rowArray.add(cursor.getString(3));
					rowArray.add(cursor.getString(4));
					rowArray.add(cursor.getString(5));
					rowArray.add(cursor.getString(6));
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		return rowArray;
	}

	/**
	 * The Class CustomSQLiteOpenHelper.
	 */
	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
		
		/**
		 * Instantiates a new custom sq lite open helper.
		 *
		 * @param context the context
		 */
		public CustomSQLiteOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {

			//
			// This string is used to create the database. It should
			// be changed to suit your needs.
			String newTableQueryString1 = "CREATE TABLE " + TABLE1_NAME + "("
					+ TABLE1_ROW_ID
					+ " integer primary key autoincrement not null, "
					+ TABLE1_ROW_NAME + " TEXT, " + TABLE1_ROW_DESCRIPTION
					+ " TEXT, " + TABLE1_ROW_DATE_OF_BIRTH + " TEXT, "
					+ TABLE1_ROW_GENDER + " TEXT, " + TABLE1_ROW_EMAIL
					+ " TEXT, " + TABLE1_ROW_PASSWORD + " TEXT, "
					+ TABLE1_ROW_IMAGE + " TEXT," + TABLE1_ROW_PROFILE
					+ " TEXT" + ")";

			// execute the query string to the database.
			db.execSQL(newTableQueryString1);

			// This string is used to create the database. It should
			// be changed to suit your needs.
			String newTableQueryString2 = "CREATE TABLE " + TABLE2_NAME + "("
					+ TABLE2_ROW_ID
					+ " integer primary key autoincrement not null, "
					+ TABLE2_ROW_NAME + " TEXT, " + TABLE2_ROW_DESCRIPTION
					+ " TEXT, " + TABLE2_ROW_DATE_OF_BIRTH + " TEXT, "
					+ TABLE2_ROW_GENDER + " TEXT, " + TABLE2_ROW_EMAIL
					+ " TEXT, " + TABLE2_ROW_USER_EMAIL + " TEXT,"
					+ TABLE2_ROW_IMAGE + " TEXT" + ")";

			db.execSQL(newTableQueryString2);
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}