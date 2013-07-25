package si.stecce.societies.crowdtasking.model;

import java.io.Serializable;

import com.google.appengine.api.users.User;

public class AuthenticatedUser implements Serializable {
	private static final long serialVersionUID = 3537314632523365525L;
	private AuthenticationPrvider provider;
	private String federatedIdentity;
	private String userId;
	private String nickName;
	private String email;
	private String firstName, lastName;

	public AuthenticatedUser() {	
	}
	
	public AuthenticatedUser(User user) {
		// gugl is on develpoment
    	this.federatedIdentity = user.getFederatedIdentity() == null ? "gugl" : user.getFederatedIdentity();
		this.provider = AuthenticationPrvider.getAuthenticationPrvider(federatedIdentity);
		this.userId = user.getUserId();
		this.nickName = user.getNickname();
		this.email = user.getEmail();
	}
	
	public AuthenticationPrvider getProvider() {
		return provider;
	}
	public void setProvider(AuthenticationPrvider provider) {
		this.provider = provider;
	}
	public String getFederatedIdentity() {
		return federatedIdentity;
	}
	public void setFederatedIdentity(String federatedIdentity) {
		this.federatedIdentity = federatedIdentity;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
}
