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
package org.societies.collabtools.runtime;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.acquisition.LongTermCtxTypes;
import org.societies.collabtools.acquisition.Person;
import org.societies.collabtools.acquisition.PersonRepository;
import org.societies.collabtools.acquisition.ShortTermCtxTypes;


/**
 * Context monitor analyze the users information to check if the rules matches
 *
 * @author cviana
 *
 */
public class CtxMonitor extends Observable implements Runnable, Observer {

	public Engine engine;
	private SessionRepository sessionRepository;
	private static final Logger logger  = LoggerFactory.getLogger(CtxMonitor.class);

	public CtxMonitor (PersonRepository personRepository, SessionRepository sessionRepository) {
		this.engine = new Engine(personRepository);
		this.sessionRepository = sessionRepository;

		//Default rules when the FW starts, location and interests
		Rule r01 = new Rule("r01",Operators.SAME, ShortTermCtxTypes.LOCATION, "--", 1, 0.5 ,ShortTermCtxTypes.class.getSimpleName());
		Rule r02 = new Rule("r02",Operators.SIMILAR, LongTermCtxTypes.INTERESTS, "--", 2, 0.4 ,LongTermCtxTypes.class.getSimpleName());
		//		Rule r03 = new Rule("r03",Operators.EQUAL, ShortTermCtxTypes.STATUS, "Available", 3, 0.1 ,ShortTermCtxTypes.class.getSimpleName()); //Check status of the user e.g busy, on phone, driving...
		List<Rule> rules = Arrays.asList(r01, r02);
		this.engine.setRules(rules);
	}

	public synchronized void run(){
		logger.info("Checking if people context match");

		//First rule: location Mandatory
		Hashtable<String, HashSet<Person>> personsfirstRule = engine.evaluateRule("r01");

		if (!personsfirstRule.isEmpty()) {
			Enumeration<String> iterator = personsfirstRule.keys();
			//For each different location, apply the following rules...
			while(iterator.hasMoreElements()) {
				//Session name = actual location
				String sessionName = iterator.nextElement();
				HashSet<Person> possibleMembers = engine.getMatchingResultsByPriority().get(sessionName);
				//Check conflict if actual users in the session
				if (!(possibleMembers).isEmpty()) {
					if (!this.sessionRepository.containSession(sessionName)) {
						logger.info("Starting a new session..");
						logger.info("Inviting people..");
						System.out.println("New Engine: "+possibleMembers.toString());
						this.sessionRepository.createSession(sessionName);
					}
					String[] membersIncluded = this.sessionRepository.addMembers(sessionName, possibleMembers);

					//System.out.println("New Engine: "+possibleMembers.toString());

					// Notify observers of change
					if (membersIncluded.length>0){
						Hashtable<String, String[]> response = new Hashtable<String, String[]>();
						response.put(sessionName, membersIncluded);
						setChanged();
						notifyObservers(response);
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.run();
	}
}
