
package org.societies.schmoozer.model;

import javax.validation.constraints.Size;

public class NewAccountForm {
   
	@Size(min = 1, max = 20)
    private String userid;
    @Size(min = 1, max = 20)
    private String password;
    @Size(min = 1, max = 50)
    private String displayName;
    
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
