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
package org.societies.thirdpartyservices.networking.directory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


/**
 * This is the Class accepted by the NetowrkingDirectory when  user wants to register
 * with a network zoned. 
 * 
 * @author mmannion
 * @version 1.0
 */

@Entity
@Table(name = "NZUserDetails")
public class NZUserDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7819484667842436359L;
	/**
	 * 
	 */
	
	private String userid;
	private String displayName;
	private String company;
	private String dept;
	private String position;
	
	private String facebookID;
	private String twitterID;
	private String linkedInID;
	

	public Set<NZEducation> education;
	public Set<NZEmployment> employment;

	/** @return the education Records */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="userdetails") 
	@Cascade(CascadeType.DELETE)
	public Set<NZEducation> getEducation() {
		return education;
	}

	public void setEducation(Set<NZEducation> education) {
		this.education = education;
	}

	/** @return the employment Records */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="userdetails") 
	@Cascade(CascadeType.DELETE)
	public Set<NZEmployment> getEmployment() {
		return employment;
	}

	public void setEmployment(Set<NZEmployment> employment) {
		this.employment = employment;
	}


	
	
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
	
	@Column(name = "Department")
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
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

	public NZUserDetails() {
		super();
		education = new HashSet<NZEducation>();
		employment = new HashSet<NZEmployment>();
	}
	
	

}