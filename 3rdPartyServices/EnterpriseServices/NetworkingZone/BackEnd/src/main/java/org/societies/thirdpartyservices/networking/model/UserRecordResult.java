package org.societies.thirdpartyservices.networking.model;


public class UserRecordResult {

    protected UserResult result;
    protected UserRecord userRec;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link UserResult }
     *     
     */
    public UserResult getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserResult }
     *     
     */
    public void setResult(UserResult value) {
        this.result = value;
    }

    /**
     * Gets the value of the userRec property.
     * 
     * @return
     *     possible object is
     *     {@link UserRecord }
     *     
     */
    public UserRecord getUserRec() {
        return userRec;
    }

    /**
     * Sets the value of the userRec property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserRecord }
     *     
     */
    public void setUserRec(UserRecord value) {
        this.userRec = value;
    }

}
