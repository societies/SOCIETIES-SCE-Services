package org.societies.thirdPartyServices.disasterManagement.iWantToHelp.data;

public class UserData {
	private String firstName = "";
	private String lastName = "";
	
	public UserData(String commaSeperated){
		String[] tokens = commaSeperated.split(",");
		setFirstName(tokens[0]);
		setLastName(tokens[1]);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String toString(){
		return getFirstName() + " - " + getLastName();
	}
}
