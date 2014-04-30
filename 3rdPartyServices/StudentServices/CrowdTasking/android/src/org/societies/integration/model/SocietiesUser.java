package org.societies.integration.model;

/**
 * Created by Simon Jureša on 20.8.2013.
 */
public class SocietiesUser {
    private String userId;
    private String name;
    private String foreName;
    private String email;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name="";
        }
        this.name = name;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        if (foreName == null) {
            foreName="";
        }
        this.foreName = foreName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) {
            email="";
        }
        this.email = email;
    }

}
