package org.societies.thirdPartyServices.disasterManagement.wantToHelp.data;

public class UserData {
	private String firstName = "";
	private String lastName = "";
	private String institute = "";
	private String email = "";
	private String societies_xmlrpc_url = "";
	
	public UserData(String commaSeperated){
		String[] tokens = commaSeperated.split(",");
		if (tokens.length > 3) {
			setFirstName(tokens[0]);
			setLastName(tokens[1]);
			setInstitute(tokens[2]);
			setEmail(tokens[3]);
		}
		if (tokens.length > 4)
			setSocieties_xmlrpc_url(tokens[4]);
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
	
	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSocieties_xmlrpc_url() {
		return societies_xmlrpc_url;
	}

	public void setSocieties_xmlrpc_url(String societies_xmlrpc_url) {
		this.societies_xmlrpc_url = societies_xmlrpc_url;
	}

	public String toString(){
		return getFirstName() + " - " + getLastName() + " - " + getInstitute() + " - " + getEmail() + " - " + getSocieties_xmlrpc_url();
	}
}
