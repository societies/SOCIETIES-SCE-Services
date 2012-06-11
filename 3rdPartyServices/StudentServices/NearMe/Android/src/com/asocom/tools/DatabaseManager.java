package com.asocom.tools;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 */
public class DatabaseManager {
	// the Activity or Application that is creating an object from this class.
	Context context;

	// a reference to the database used by this application/object
	private SQLiteDatabase db;

	// These constants are specific to the database. They should be
	// changed to suit your needs.
	private final String DB_NAME = "PVCDataBase";
	private final int DB_VERSION = 1;

	// These constants are specific to the database table. They should be
	// changed to suit your needs.
	private final String TABLE1_NAME = "users";
	private final String TABLE1_ROW_ID = "id";
	private final String TABLE1_ROW_NAME = "name";
	private final String TABLE1_ROW_DESCRIPTION = "description";
	private final String TABLE1_ROW_DATE_OF_BIRTH = "date_of_birth";
	private final String TABLE1_ROW_GENDER = "gender";
	private final String TABLE1_ROW_EMAIL = "email";
	private final String TABLE1_ROW_PASSWORD = "password";
	private final String TABLE1_ROW_IMAGE = "image";
	private final String TABLE1_ROW_PROFILE = "profile";
	public final int NUMBER_OF_FIELD = 8;

	private final String TABLE2_NAME = "friends";
	private final String TABLE2_ROW_ID = "id";
	private final String TABLE2_ROW_NAME = "name";
	private final String TABLE2_ROW_DESCRIPTION = "description";
	private final String TABLE2_ROW_DATE_OF_BIRTH = "date_of_birth";
	private final String TABLE2_ROW_GENDER = "gender";
	private final String TABLE2_ROW_EMAIL = "email";
	private final String TABLE2_ROW_USER_EMAIL = "user_email";
	private final String TABLE2_ROW_IMAGE = "image";

	/**
	 *
	 */
	public DatabaseManager(Context context) {
		this.context = context;
		// create or open the database
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
	}

	public void closedb() {
		db.close();
	}

	/**
	 * 
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
	 *
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 *
	 */
	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
		public CustomSQLiteOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

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

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}