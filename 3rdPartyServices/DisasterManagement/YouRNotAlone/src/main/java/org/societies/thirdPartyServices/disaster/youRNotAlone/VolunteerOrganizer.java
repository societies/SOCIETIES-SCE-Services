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
import java.util.Iterator;
import java.util.Set;

import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;

class VolunteerOrganizer {
	private HashMap<String,Volunteer> volunteers;
	
	public VolunteerOrganizer(){
		this.volunteers = new HashMap<String,Volunteer>();
	}
	
	public void loadVolunteers(HashMap<String,Volunteer> volunteers){
		this.volunteers = volunteers;
	}
	
	public void loadVolunteers(ArrayList<Volunteer> volunteersList){
		for(int i=0;i<volunteersList.size();i++)
			this.volunteers.put(volunteersList.get(i).getID(), volunteersList.get(i));
	}
	
	public void addVolunteer(Volunteer v){
		this.volunteers.put(v.getID(), v);
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
		Set<String> IDs = this.volunteers.keySet();
	    Iterator<String> iter = IDs.iterator();
	    while (iter.hasNext()) {
	    	Volunteer v = this.volunteers.get(iter.next());
	    	Boolean ok = true;
	    	for(int i=0;i<properties.size();i++)
	    		ok = ok && v.findPropertie(properties.get(i));
	    	if(ok)
	    		group.add(v);
	    }
	    return group;
	}
}
