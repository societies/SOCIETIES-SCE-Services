/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.thirdPartyServices.disaster.youRNotAlone.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.societies.thirdPartyServices.disaster.youRNotAlone.util.*;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Volunteer {
	private String ID;
	private String firstname;
	private String lastname;
	private String institut;
	private String country;
	private String email;
	private int languageCount;
	private int skillCount;

	@XmlJavaTypeAdapter(MyMapAdapter.class)
	private HashMap<String,Integer> spokenLanguages;
	
	@XmlJavaTypeAdapter(MyMapAdapter.class)
	private HashMap<String,Integer> expertiseSkills;
	
	public Volunteer() {} 
	
	public Volunteer(String ID, String firstname, String lastname, 
			String institut, String country, String email,
			HashMap<String,Integer> spokenLanguages,
			HashMap<String,Integer> expertiseSkills){
		this.ID = ID;
		this.firstname = firstname;
		this.lastname = lastname;
		this.institut = institut;
		this.country = country;
		this.email = email;
		this.spokenLanguages = spokenLanguages;
		this.expertiseSkills = expertiseSkills;
	}
	
	public Volunteer(String ID, String firstname, String lastname, String institut,
			String country, String email){
		this.ID = ID;
		this.firstname = firstname;
		this.lastname = lastname;
		this.institut = institut;
		this.country = country;
		this.email = email;
		this.spokenLanguages = new HashMap<String,Integer>();
		this.expertiseSkills = new HashMap<String,Integer>();
	}
	
	public void addSpokenLanguage(String lang){
		this.spokenLanguages.put(lang, 2);
	}
	
	public void addSpokenLanguage(String lang, int level){
		this.spokenLanguages.put(lang, level);
	}
	
	public void addSkill(String skill){
		this.expertiseSkills.put(skill,2);
	}
	
	public void addSkill(String skill,int level){
		this.expertiseSkills.put(skill,level);
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getInstitut() {
		return institut;
	}

	public void setInstitut(String institut) {
		this.institut = institut;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public HashMap<String,Integer> getSpokenLanguages() {
		return spokenLanguages;
	}

	public void setSpokenLanguages(HashMap<String,Integer> spokenLanguages) {
		this.spokenLanguages = spokenLanguages;
	}

	public HashMap<String,Integer> getExpertiseSkills() {
		return expertiseSkills;
	}

	public void setExpertiseSkills(HashMap<String,Integer> expertiseSkills) {
		this.expertiseSkills = expertiseSkills;
	}
	
	public int getLanguageCount() {
		return languageCount;
	}

	public void setLanguageCount(int languageCount) {
		this.languageCount = languageCount;
	}

	public int getSkillCount() {
		return skillCount;
	}

	public void setSkillCount(int skillCount) {
		this.skillCount = skillCount;
	}
	
	public boolean findLanguage(String lang){
		Set<String> langs = this.spokenLanguages.keySet();
	    Iterator<String> iter = langs.iterator();
	    while (iter.hasNext()) {
	    	if(iter.next().equalsIgnoreCase(lang))
	    		return true;
	    }
	    return false;
	}
	
	public boolean findSkill(String skill){
		Set<String> skills = this.expertiseSkills.keySet();
	    Iterator<String> iter = skills.iterator();
	    while (iter.hasNext()) {
	    	if(iter.next().equalsIgnoreCase(skill))
	    		return true;
	    }
	    return false;
	}
	
	public boolean findPropertie(String pro){
		boolean ok = false;
		ok = ok || this.institut.equalsIgnoreCase(pro);
		ok = ok || this.country.equalsIgnoreCase(pro);
		ok = ok || this.findLanguage(pro);
		ok = ok || this.findSkill(pro);
	    return ok;
	}
	
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("id : "+this.ID+"\n");
		s.append("name : "+this.firstname+" "+this.lastname+"\n");
		s.append("institut : "+this.institut+"\n");
		s.append("country : "+this.country+"\n");
		s.append("email : "+this.email+"\n");
		
		Set<String> setLangs = this.spokenLanguages.keySet();
	    Iterator<String> iter1 = setLangs.iterator();
	    StringBuilder langs = new StringBuilder();
	    while (iter1.hasNext()) {
	      langs.append(iter1.next()+";");
	    }
		s.append("languages : "+langs+"\n");
		
		Set<String> setSkills = this.expertiseSkills.keySet();
	    Iterator<String> iter2 = setSkills.iterator();
	    StringBuilder skills = new StringBuilder();
	    while (iter2.hasNext()) {
	      skills.append(iter2.next()+";");
	    }
		s.append("expertise skills : "+skills+"\n");
		
		return s.toString();
	}
	
}


