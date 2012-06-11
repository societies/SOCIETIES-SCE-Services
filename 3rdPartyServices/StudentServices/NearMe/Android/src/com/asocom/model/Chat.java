package com.asocom.model;

import java.util.ArrayList;


public class Chat {

	private ArrayList<SmsChat> smsList = new ArrayList<SmsChat>();
	private int newMessages;

	public Chat() {
		super();
		smsList = new ArrayList<SmsChat>();
		this.newMessages = 0;
	}

	public ArrayList<SmsChat> getSmsList() {
		return smsList;
	}

	public void addSms(SmsChat sms) {
		this.smsList.add(sms);
	}

	public int getNewMessages() {
		return newMessages;
	}

	public void addNewMessages() {
		newMessages++;
	}

	public void resetNewMessages() {
		this.newMessages = 0;
	}

	@Override
	public String toString() {
		return "Chat [smsList=" + smsList + ", newMessages=" + newMessages
				+ "]";
	}

}
