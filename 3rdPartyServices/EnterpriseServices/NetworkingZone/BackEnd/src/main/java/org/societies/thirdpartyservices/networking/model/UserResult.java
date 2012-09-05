package org.societies.thirdpartyservices.networking.model;


public enum UserResult {

    USER_OK,
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS;

    public String value() {
        return name();
    }

    public static UserResult fromValue(String v) {
        return valueOf(v);
    }

}
