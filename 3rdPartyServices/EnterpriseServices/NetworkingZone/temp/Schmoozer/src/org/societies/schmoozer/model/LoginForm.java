
package org.societies.schmoozer.model;

import javax.validation.constraints.Size;

public class LoginForm {
   
	@Size(min = 1, max = 50)
    private String userId;
    @Size(min = 1, max = 20)
    private String password;
    
    
    String buttonLabel;


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getButtonLabel() {
		return buttonLabel;
	}


	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}
    
    
    
}
