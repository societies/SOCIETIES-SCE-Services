package org.societies.thirdPartyServices.disasterManagement.iWantToHelp.data;

public class TicketData {
	private int id;
	private String headline;
	
	public TicketData() {
		setID(-1);
		setHeadline("");
	}
	
	public TicketData(int id, String headline) {
		setID(id);
		setHeadline(headline);
	}
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public String toString() {
		return getID() + " - " + getHeadline();
	}
}
