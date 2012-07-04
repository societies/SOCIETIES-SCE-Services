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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.Interpretation.SimpleCtxMatching;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.ContextUpdates;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;


/**
 * Describe your class here...
 *
 * @author cviana
 *
 */
public class ShortTermCtxMonitor extends Thread{

	private PersonRepository personRepository;
	private SessionRepository sessionRepository;
	private ContextActivity retrieveLastStatusUpdates = new ContextActivity();
	private static final Logger logger  = LoggerFactory.getLogger(ShortTermCtxMonitor.class);
	private static final long SECONDS = 5 * 1000;

	public ShortTermCtxMonitor (PersonRepository personRepository, SessionRepository sessionRepository) {
		this.personRepository = personRepository;
		this.sessionRepository = sessionRepository;
	}

	public synchronized void run(){
		try {
			while (true) {
				System.out.println("Checking last people contexts...");
				HashSet<ContextUpdates> lastUpdates = retrieveLastStatusUpdates.getLastContextUpdates(personRepository);

				System.out.println("Checking if people context match");
				SimpleCtxMatching matching = new SimpleCtxMatching();

				//First rule is location
				Hashtable<String, HashSet<Person>> personsSameLocation = matching.getPersonsWithMatchingCtx(ShortTermCtxTypes.LOCATION, lastUpdates);
				if (!personsSameLocation.isEmpty()) {
					Enumeration<String> iterator = personsSameLocation.keys();
					while(iterator.hasMoreElements()) {
						String sessionName = iterator.nextElement();
						if (!sessionRepository.sessionsTable.containsKey(sessionName)) {
							logger.info("If session still doesn't exist, create one");
							logger.info("Starting a new session..");
							sessionRepository.sessionsTable.put(sessionName, personsSameLocation.get(sessionName));
							logger.info("Inviting people..");
							//sessions.create(sessionName);
							//						sessions.invite(sessionName, personsSameLocation.get(sessionName));
						} //Check conflict if actual users in the session
						else if (sessionRepository.differenceBetweenHashSets(personsSameLocation.get(sessionName), sessionRepository.sessionsTable.get(sessionName))){
							if (!personsSameLocation.get(sessionName).isEmpty()) {
								//Compare persons in same location with members in this session
								sessionRepository.sessionsTable.put(sessionName, personsSameLocation.get(sessionName));
								logger.info("Checking if users are in a session..");
							}
							//Second rule: Status
							//Check status of the user e.g busy, on phone, driving...
							//					checkStatus();
							//						sessions.invite(sessionName, personsSameLocation.get(sessionName));
							
							//Check interests...
						}
					}
				}
				System.out.println(sessionRepository.sessionsTable.toString());
				//Sleep in seconds
				Thread.sleep(SECONDS);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
