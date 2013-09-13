/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.ext3p.nzone.server.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * This is the Class accepted by the NetowrkingDirectory when  user wants to register
 * with a network zoned. 
 * 
 * @author mmannion
 * @version 1.0
 */

@Entity
@Table(name = "NZUser")
public class NZUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7819484667842436359L;
	/**
	 * 
	 */
	
	private String userid;
	private String displayName;
	private String email;
	private String homelocation;
	private String sex;
	private String company;
	private String position;
	private String about;
	
	private String facebookID;
	private String twitterID;
	private String linkedInID;
	private String foursqID;
	private String googleplusID;
	
	private String interests;
	private String currentZone;

	/**
	 * @return the userid
	 */
	@Id
	@Column(name = "user_id")
	public String getUserid() {
		return userid;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	/**
	 * @return the company
	 */
	@Column(name = "Company")
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	
	/**
	 * @return the displayName
	 */
	@Column(name = "DisplayName")
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @return the position
	 */
	@Column(name = "Position")
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	
	public void setPosition(String position) {
		this.position = position;
	}
	
	/**
	 * @return the about
	 */
	@Column(name = "About")
	public String getAbout() {
		return about;
	}

	/**
	 * @param about the about
	 */
	
	public void setAbout(String about) {
		this.about = about;
	}

	/**
	 * @return the facebookID
	 */
	@Column(name = "Facebookid")
	public String getFacebookID() {
		return facebookID;
	}

	/**
	 * @param facebookID the facebookID to set
	 */
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}

	/**
	 * @return the twitterID
	 */
	@Column(name = "Twitterid")
	public String getTwitterID() {
		return twitterID;
	}

	/**
	 * @param twitterID the twitterID to set
	 */
	public void setTwitterID(String twitterID) {
		this.twitterID = twitterID;
	}

	/**
	 * @return the linkedInID
	 */
	@Column(name = "Linkedinid")
	public String getLinkedInID() {
		return linkedInID;
	}

	/**
	 * @param linkedInID the linkedInID to set
	 */
	public void setLinkedInID(String linkedInID) {
		this.linkedInID = linkedInID;
	}

	@Column(name = "Foursqid")
	public String getFoursqID() {
		return foursqID;
	}

	public void setFoursqID(String foursqID) {
		this.foursqID = foursqID;
	}

	@Column(name = "Googleplusid")
	public String getGoogleplusID() {
		return googleplusID;
	}

	public void setGoogleplusID(String googleplusID) {
		this.googleplusID = googleplusID;
	}

	/**
	 * @return the email
	 */
	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the homelocation
	 */
	@Column(name = "homelocation")
	public String getHomelocation() {
		return homelocation;
	}

	/**
	 * @param homelocation the homelocation to set
	 */
	public void setHomelocation(String homelocation) {
		this.homelocation = homelocation;
	}

	/**
	 * @return the sex
	 */
	@Column(name = "sex")
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the interests
	 */
	@Column(name = "interests")
	public String getInterests() {
		return interests;
	}

	/**
	 * @param interests the interests to set
	 */
	public void setInterests(String interests) {
		this.interests = interests;
	}

	/**
	 * @return the currentZone
	 */
	@Column(name = "currentzone")
	public String getCurrentZone() {
		return currentZone;
	}

	/**
	 * @param currentZone the currentZone to set
	 */
	public void setCurrentZone(String currentZone) {
		this.currentZone = currentZone;
	}

	public NZUser() {
		super();
	}
	
	

}