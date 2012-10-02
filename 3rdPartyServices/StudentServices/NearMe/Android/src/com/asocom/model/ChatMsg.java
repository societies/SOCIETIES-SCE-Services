/*
 * 
 */
package com.asocom.model;

// TODO: Auto-generated Javadoc
/**
 * The Class SmsChat.
 */
public class ChatMsg {
	
	/** The image. */
	private int image;
	
	/** The date sms. */
	private String dateSms;
	
	/** The user. */
	private String user;
	
	/** The message. */
	private String message;

	/**
	 * Instantiates a new sms chat.
	 *
	 * @param image the image
	 * @param dateSms the date sms
	 * @param user the user
	 * @param message the message
	 */
	public ChatMsg(int image, String dateSms, String user, String message) {
		super();
		this.image = image;
		this.dateSms = dateSms;
		this.user = user;
		this.message = message;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public int getImage() {
		return image;
	}

	/**
	 * Sets the image.
	 *
	 * @param image the new image
	 */
	public void setImage(int image) {
		this.image = image;
	}

	/**
	 * Gets the date sms.
	 *
	 * @return the date sms
	 */
	public String getDateSms() {
		return dateSms;
	}

	/**
	 * Sets the date sms.
	 *
	 * @param dateSms the new date sms
	 */
	public void setDateSms(String dateSms) {
		this.dateSms = dateSms;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SmsChat [image=" + image + ", dateSms=" + dateSms + ", user="
				+ user + ", message=" + message + "]";
	}

}
