package si.setcce.collaborativemeeting;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * Created by Clive on 24.9.2013.
 */
public class MinutesModel implements Serializable {

	private static final long serialVersionUID = -1988642601733856730L;

	private static final String TAG = MinutesModel.class.getSimpleName();

	private String userId;
	private String userName;
	private String minute;
	private Date date;
	private boolean important = false;

	/**
	 * Constructor
	 */
	public MinutesModel() {
	}

	/**
	 * Constructor
	 */
	public MinutesModel(String userId, String userName, String minute, Date date, boolean important) {
		this.userId = userId;
		this.userName = userName;
		this.minute = minute;
		this.date = date;
		this.important = important;
	}
	
	public String getUserName()
	{
		return this.userName;
	}

	public void setUserName(String userName) {
		Log.d(TAG, "User name set to " + userName);
		this.userName = userName;
	}

	/**
	 * 
	 * @return User ID for Crowd Tasking, not for this app
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 
	 * @param userId User ID for Crowd Tasking, not for this app
	 */
	public void setUserId(String userId) {
		Log.d(TAG, "User ID set to " + userId);
		this.userId = userId;
	}

	public String getMinute()
	{
		return this.minute;
	}

	public void setMinute(String minute)
	{
		this.minute = minute;
	}

	public Date getDate() {
		return date;
	}

	public String getDateString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}
	
	@Override
	public String toString() {
		return date + " important=" + important + "; " + userName + ": " + minute;
	}
}
