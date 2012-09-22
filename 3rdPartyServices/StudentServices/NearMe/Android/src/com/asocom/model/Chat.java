/*
 * 
 */
package com.asocom.model;

import java.util.ArrayList;


// TODO: Auto-generated Javadoc
/**
 * The Class Chat.
 */
public class Chat {

	/** The sms list. */
	private ArrayList<ChatMsg> smsList = new ArrayList<ChatMsg>();
	
	/** The new messages. */
	private int newMessages;

	/**
	 * Instantiates a new chat.
	 */
	public Chat() {
		super();
		smsList = new ArrayList<ChatMsg>();
		this.newMessages = 0;
	}

	/**
	 * Gets the sms list.
	 *
	 * @return the sms list
	 */
	public ArrayList<ChatMsg> getSmsList() {
		return smsList;
	}

	/**
	 * Adds the sms.
	 *
	 * @param sms the sms
	 */
	public void addSms(ChatMsg sms) {
		this.smsList.add(sms);
	}

	/**
	 * Gets the new messages.
	 *
	 * @return the new messages
	 */
	public int getNewMessages() {
		return newMessages;
	}

	/**
	 * Adds the new messages.
	 */
	public void addNewMessages() {
		newMessages++;
	}

	/**
	 * Reset new messages.
	 */
	public void resetNewMessages() {
		this.newMessages = 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Chat [smsList=" + smsList + ", newMessages=" + newMessages
				+ "]";
	}

}
