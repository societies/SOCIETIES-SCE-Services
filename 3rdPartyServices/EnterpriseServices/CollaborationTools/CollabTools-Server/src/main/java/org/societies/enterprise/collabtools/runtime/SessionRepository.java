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
package org.societies.enterprise.collabtools.runtime;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;

/**
 * Describe your class here...
 *
 * @author cviana
 *
 */
public class SessionRepository implements Observer {
	
	//Session = location, list of persons
	Hashtable<String, HashSet<Person>> sessionsTable = new Hashtable<String, HashSet<Person>>();
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public synchronized void update(Observable o, Object arg) {
		Person person = (Person) arg;
		System.out.println("Person has change location : " + person.getName());
		if (isInSession(person)) {
			//Remove person from session
			System.out.println(person.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION));
			HashSet<Person> persons = sessionsTable.get(person.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION));
			System.out.println("Session table before: "+persons.toString());
			persons.remove(person);
			sessionsTable.put(person.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION), persons);
			System.out.println("Session table after: "+persons.toString());
		}
	}
	

	/**
	 * @param person 
	 * @return
	 */
	private boolean isInSession(Person person) {
		HashSet<Person> persons = sessionsTable.get(person.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION));
		if (persons == null) {
			return false;
		}
		persons.contains(person);
		return true;
	}
	
	/**
	 * @param sessionName 
	 * @return
	 */
	public boolean containSession(String sessionName) {
		return sessionsTable.containsKey(sessionName);
	}


	public synchronized boolean differenceBetweenSessionMembers(HashSet<Person> members, String sessionName) {
//		List<Person> result = ((List<Person>) ((ArrayList<Person>) a).clone());
		HashSet<Person> result = ((HashSet<Person>) ((HashSet<Person>) members).clone());
		result.removeAll(sessionsTable.get(sessionName));
		if (result.isEmpty())
			return false;
		return true;
	}


	/**
	 * @param sessionName
	 * @param hashSet
	 */
	public void inviteMembers(String sessionName, HashSet<Person> persons) {
		sessionsTable.put(sessionName, persons);
	}

}
