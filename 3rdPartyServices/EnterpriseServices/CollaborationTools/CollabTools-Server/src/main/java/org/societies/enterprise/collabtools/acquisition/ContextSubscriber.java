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
package org.societies.enterprise.collabtools.acquisition;

import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.Activator;
import org.societies.enterprise.collabtools.api.IContextSubscriber;
import org.societies.enterprise.collabtools.interpretation.ContextAnalyzer;
import org.societies.enterprise.collabtools.runtime.CtxMonitor;
import org.societies.enterprise.collabtools.runtime.SessionRepository;
import org.xml.sax.SAXException;

/**
 * Describe your class here...
 *
 * @author cviana
 *
 */
public class ContextSubscriber implements IContextSubscriber, Observer {

	private static final Logger logger  = LoggerFactory.getLogger(Activator.class);
	private PersonRepository personRepository;
	private SessionRepository sessionRepository;
	private int counter = 0;
	private Person lastIndivudal;

	/**
	 * @param personRepository
	 * @param sessionRepository 
	 */
	public ContextSubscriber(PersonRepository personRepository, SessionRepository sessionRepository) {
		this.personRepository = personRepository;
		this.sessionRepository = sessionRepository;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		String[] msg = (String[]) arg;
		logger.info("******************************* update event  ***************** "+msg[0]);
		Person individual = this.personRepository.getPersonByName(msg[2]);
		String context = msg[1];
		String type = msg[0];
		if (type == ShortTermCtxTypes.LOCATION)
			individual.setLongTermCtx(ShortTermCtxTypes.LOCATION, context);
		else if (type == ShortTermCtxTypes.STATUS)
			individual.setLongTermCtx(ShortTermCtxTypes.STATUS, context);
	}

	public void setContext(final String type, String context, String person) throws Exception {
		logger.info("******************************* Changing Context: "+person+", "+type+", "+context);
		Person individual = null;
		if (type == Person.NAME) {
			counter++;
			if (counter > 5) {
				enrichedCtx();
				setupWeightBetweenPeople(lastIndivudal);
				if (counter == 5){
					logger.info("Starting Context Monitor..." );
					CtxMonitor thread = new CtxMonitor(personRepository, sessionRepository);

					thread.start();
				}
			}
			individual = this.personRepository.createPerson(person);
			individual.setLongTermCtx(Person.NAME, context);
			logger.info("******************************* Person.NAME: "+this.personRepository.getPersonByName(person).getName());
		}
			
		individual = this.personRepository.getPersonByName(person);
		lastIndivudal = individual;
		if (type == LongTermCtxTypes.INTERESTS)
			individual.setLongTermCtx(LongTermCtxTypes.INTERESTS, context);
		else if (type == LongTermCtxTypes.WORK)
			individual.setLongTermCtx(LongTermCtxTypes.WORK, context);
		else if (type == LongTermCtxTypes.COMPANY)
			individual.setLongTermCtx(LongTermCtxTypes.COMPANY, context);
		else if (type == ShortTermCtxTypes.LOCATION)
			individual.setLongTermCtx(ShortTermCtxTypes.LOCATION, context);
		else if (type == ShortTermCtxTypes.STATUS)
			individual.setLongTermCtx(ShortTermCtxTypes.STATUS, context);

	}

	public void setContext(final String type, String[] context, String person) {
		Person individual = personRepository.getPersonByName(person);
		individual.setLongTermCtx(LongTermCtxTypes.INTERESTS, context);
	}

	public void enrichedCtx() throws XPathExpressionException, IOException,
	SAXException, ParserConfigurationException {
		ContextAnalyzer ctxRsn = new ContextAnalyzer(personRepository);
		ctxRsn.incrementInterests();
	}
	
	public void setupWeightBetweenPeople(Person person)
	{
		Map<Person, Integer> persons = personRepository.getPersonWithSimilarInterests(person);
		for (Map.Entry<Person, Integer> entry : persons.entrySet()) {
			//Similarity Formula is: similar interests/ min(personA, personB)
			float weight = ContextAnalyzer.personInterestsSimilarity(entry.getValue(), entry.getKey(), person);
			person.addFriend(entry.getKey(),weight);  
		}

	}

}
