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

package org.societies.thirdPartyServices.disaster.youRNotAlone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.societies.thirdPartyServices.disaster.youRNotAlone.model.*;
import org.societies.thirdPartyServices.disaster.youRNotAloneServer.YouRNotAloneDAO;


public class VolunteerOrganizer {
	private HashMap<String,Volunteer> volunteers;
	
	public HashMap<String, Volunteer> getVolunteers() {
		return volunteers;
	}


	public void setVolunteers(HashMap<String, Volunteer> volunteers) {
		this.volunteers = volunteers;
	}

	private ArrayList<String> allLanguages;
	private ArrayList<String> allSkills;

	public VolunteerOrganizer(){
		this.volunteers = new HashMap<String,Volunteer>();
		extractAllLanguagesAndSkills();
	}

	public void deleteAllVolunteers(){
		this.volunteers = new HashMap<String,Volunteer>();
	}

	public void loadVolunteers(HashMap<String,Volunteer> volunteers){
		this.volunteers = volunteers;
		extractAllLanguagesAndSkills();
	}

	public void loadVolunteers(ArrayList<Volunteer> volunteersList){
		for(int i=0;i<volunteersList.size();i++)
			this.volunteers.put(volunteersList.get(i).getID(), volunteersList.get(i));
		extractAllLanguagesAndSkills();
	}

	public void addVolunteer(Volunteer v){
		this.volunteers.put(v.getID(), v);
		System.out.println("add new volunteer");
		ArrayList<String> channels = buildChannelInfo();
		String[][] Logins = buildUserLoginInfo(channels);
		ChannelWriter channelwriter = new ChannelWriter();
		LoginWriter loginwriter = new LoginWriter();
		boolean succes1 = channelwriter.write("D:/Program Files/wamp/www/chat/lib/data/channels.php", channels);
		boolean succes2 = loginwriter.write("D:/Program Files/wamp/www/chat/lib/data/users.php", Logins, channels);
	}


	public ArrayList<String> getAllLanguages() {
		return allLanguages;
	}

	public void setAllLanguages(ArrayList<String> allLanguages) {
		this.allLanguages = allLanguages;
	}

	public ArrayList<String> getAllSkills() {
		return allSkills;
	}

	public void setAllSkills(ArrayList<String> allSkills) {
		this.allSkills = allSkills;
	}

	public int getVolunteerCount(){
		return this.volunteers.size();
	}
	
	private void extractAllLanguagesAndSkills(){
		Set<String> langs = new HashSet<String>();
		Set<String> skills = new HashSet<String>();
		Set<String> IDs = this.volunteers.keySet();
		Iterator<String> iter = IDs.iterator();
		while (iter.hasNext()) {
			Volunteer v = this.volunteers.get(iter.next());
			if(v.getSpokenLanguages()!=null){
				Set<String> templangs = v.getSpokenLanguages().keySet();
				langs.addAll(templangs);
			}
			if(v.getExpertiseSkills()!=null){
				Set<String> tempskills = v.getExpertiseSkills().keySet();
				skills.addAll(tempskills);
			}
		}
		this.allLanguages = new ArrayList<String>(langs);
		this.allSkills = new ArrayList<String>(skills);	
//		System.out.println(allLanguages.size());
//		System.out.println(allSkills.size());
	}

	public ArrayList<Volunteer> getGroupByLanguages(ArrayList<String> langs){
		ArrayList<Volunteer> group = new ArrayList<Volunteer>();
		Set<String> IDs = this.volunteers.keySet();
		Iterator<String> iter = IDs.iterator();
		while (iter.hasNext()) {
			Volunteer v = this.volunteers.get(iter.next());
			Boolean ok = true;
			for(int i=0;i<langs.size();i++)
				ok = ok && v.findLanguage(langs.get(i));
			if(ok)
				group.add(v);
		}
		return group;
	}

	public ArrayList<Volunteer> getGroupBySkills(ArrayList<String> skills){
		ArrayList<Volunteer> group = new ArrayList<Volunteer>();
		Set<String> IDs = this.volunteers.keySet();
		Iterator<String> iter = IDs.iterator();
		while (iter.hasNext()) {
			Volunteer v = this.volunteers.get(iter.next());
			Boolean ok = true;
			for(int i=0;i<skills.size();i++)
				ok = ok && v.findSkill(skills.get(i));
			if(ok)
				group.add(v);
		}
		return group;
	}

	public ArrayList<Volunteer> getGroupByProperties(ArrayList<String> properties){
		ArrayList<Volunteer> group = new ArrayList<Volunteer>();
		ArrayList<Volunteer> groupBackup = new ArrayList<Volunteer>();
		Set<String> IDs = this.volunteers.keySet();
		Iterator<String> iter = IDs.iterator();
		while (iter.hasNext()) {
			Volunteer v = this.volunteers.get(iter.next());
			Boolean ok = true;
			Boolean haveAtLeastOne = false;
			for(int i=0;i<properties.size();i++){
				ok = ok && v.findPropertie(properties.get(i));
				haveAtLeastOne = haveAtLeastOne || v.findPropertie(properties.get(i));
			}
			if(ok)
				group.add(v);
			if(haveAtLeastOne)
				groupBackup.add(v);
		}
		if((group.size()==0)&&(properties.size()>=2))
			return groupBackup;
		return group;
		
	}
	
	public ArrayList<Volunteer> getTranslator(){
		ArrayList<Volunteer> group = new ArrayList<Volunteer>();
		Set<String> IDs = this.volunteers.keySet();
		Iterator<String> iter = IDs.iterator();
		while (iter.hasNext()) {
			Volunteer v = this.volunteers.get(iter.next());
			if(!v.getSpokenLanguages().isEmpty())
				group.add(v);
		}
		return group;
	}
	
	public String[][] buildUserLoginInfo(ArrayList<String> channels){
//		user: user1.societies.local@ict-societies.eu
//		pass: user1user1
		List<Volunteer> temp_volunteers = new ArrayList<Volunteer>();
		temp_volunteers.addAll(YouRNotAloneDAO.instance.getVO().getVolunteers().values());
//		System.out.println(temp_volunteers.size());
		String[][] logins = new String[temp_volunteers.size()][3];
		for(int i=0;i<temp_volunteers.size();i++){
			logins[i][0] = temp_volunteers.get(i).getID();
			String pwd = temp_volunteers.get(i).getID().split("\\.", 2)[0];
			logins[i][1] = pwd+pwd;
			String channelIDs = getChannelIDs(temp_volunteers.get(i), channels);
			logins[i][2] = channelIDs;
//			System.out.println(logins[i][0] + " && " +logins[i][1] + " && " + channelIDs);
		}
		System.out.println(logins.length);
		return logins;
	}
	
	public ArrayList<String> buildChannelInfo(){
//		user: user1.societies.local@ict-societies.eu
//		pass: user1user1
		extractAllLanguagesAndSkills();
		int countL = this.allLanguages.size();
		int countS = this.allSkills.size();
		ArrayList<String> channels = new ArrayList<String>();
		for(int i=0;i<this.allLanguages.size();i++){
			channels.add(allLanguages.get(i));
		}
		for(int i=0;i<this.allSkills.size();i++){
			channels.add(allSkills.get(i));
		}
		System.out.println(channels.size());
		return channels;
	}
	
	private String getChannelIDs(Volunteer v, ArrayList<String> channels){
		String channelIDs = "";
		Iterator<String> iter = v.getSpokenLanguages().keySet().iterator();
		while (iter.hasNext()) {
			int tempID = channels.indexOf(iter.next())+1;
			if(tempID>=1)
				channelIDs = channelIDs + "," + tempID;
		}
		iter = v.getExpertiseSkills().keySet().iterator();
		while (iter.hasNext()) {
			int tempID = channels.indexOf(iter.next())+1;
			if(tempID>=1)
				channelIDs = channelIDs + "," + tempID;
		}
		return channelIDs.substring(1, channelIDs.length());
	}
}
