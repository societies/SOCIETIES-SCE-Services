package com.asocom.model;

public class SmsChat {
	private int image;
	private String dateSms;
	private String user;
	private String message;

	public SmsChat(int image, String dateSms, String user, String message) {
		super();
		this.image = image;
		this.dateSms = dateSms;
		this.user = user;
		this.message = message;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public String getDateSms() {
		return dateSms;
	}

	public void setDateSms(String dateSms) {
		this.dateSms = dateSms;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "SmsChat [image=" + image + ", dateSms=" + dateSms + ", user="
				+ user + ", message=" + message + "]";
	}

}
