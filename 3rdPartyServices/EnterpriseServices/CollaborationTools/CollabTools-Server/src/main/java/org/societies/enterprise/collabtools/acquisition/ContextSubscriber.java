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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.api.IContextSubscriber;
import org.societies.enterprise.collabtools.runtime.SessionRepository;

public class ContextSubscriber implements IContextSubscriber, Observer
{
	private static final Logger logger = LoggerFactory.getLogger(ContextSubscriber.class);
	private PersonRepository personRepository;
	private SessionRepository sessionRepository;
	InternalContextConnector ctxConnector = new InternalContextConnector(this);
	

	public void initialCtx(Object parameter) {
		//Parameter in this case is the group ID/cisID
		HashMap<String, HashMap<String, String[]>> persons = ctxConnector.getInitialContext(parameter);
		Iterator<String> personIterator = persons.keySet().iterator();

		while (personIterator.hasNext()) {  
			String personKey = personIterator.next().toString();  
			HashMap<String, String[]> ctxAttributes = persons.get(personKey);
			Iterator<String> ctxIterator = ctxAttributes.keySet().iterator();
			logger.info("Person with context attributes: "+ personKey + " " + ctxAttributes);
			while (ctxIterator.hasNext()) {
				String ctxKey = ctxIterator.next().toString(); 
				String[] ctxArray = ctxAttributes.get(ctxKey);
				try {
					//Set ctx with type of context, array of strings and person name
					this.setContext(ctxKey, ctxArray, personKey);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}  
		}
	}
	
	public void registerForContextChanges(Object parameter) {
		ctxConnector.shortTermCtxUpdates(parameter);
	}
	
	public ContextSubscriber(PersonRepository personRepository, SessionRepository sessionRepository)
	{
		this.personRepository = personRepository;
		this.sessionRepository = sessionRepository;
	}

	public void update(Observable o, Object arg)
	{
		//Arrays values: model type, string ctx value, person
		String[] msg = (String[])arg;
		logger.info("******************************* update event  ***************** " + msg[0]);
		logger.info("******************************* update event  ***************** " + msg[1]);
		logger.info("******************************* update event  ***************** " + msg[2]);
	    Map<String, String> shortTermCtx = new HashMap<String, String>();
	    Person individual = this.personRepository.getPersonByName(msg[2]);
	    String context = msg[1];
	    String type = msg[0];
	    if (type == "locationSymbolic") {
	      shortTermCtx.put(individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.STATUS), context);
	      individual.addContextStatus(shortTermCtx, this.sessionRepository);
	    }
	    else if (type == "status") {
	      shortTermCtx.put(context, individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION));
	      individual.addContextStatus(shortTermCtx, this.sessionRepository);
	    }
	}

	private void setContext(String type, String[] context, String person) throws Exception {
		logger.info("******************************* Adding Context for: " + person + ", " + type + ", " + context[0].toString());
		if (this.personRepository.hasPerson(person)) {
			Person individual = this.personRepository.getPersonByName(person);
		      Map<String, String> shortTermCtx = new HashMap<String, String>();
		      if (type == LongTermCtxTypes.INTERESTS) {
		        individual.setLongTermCtx(LongTermCtxTypes.INTERESTS, context);
		      } else if (type == LongTermCtxTypes.WORK) {
		        individual.setLongTermCtx(LongTermCtxTypes.WORK, context[0]);
		      } else if (type == LongTermCtxTypes.COMPANY) {
		        individual.setLongTermCtx(LongTermCtxTypes.COMPANY, context[0]);
		      } else if (type == ShortTermCtxTypes.LOCATION) {
		        shortTermCtx.put(individual.getLastStatus() == null ? "" : individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.STATUS), context[0]);
		        individual.addContextStatus(shortTermCtx, this.sessionRepository);
		      }
		      else if (type == ShortTermCtxTypes.STATUS) {
		        shortTermCtx.put(context[0], individual.getLastStatus() == null ? "" : individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION));
		        individual.addContextStatus(shortTermCtx, this.sessionRepository);
		      }
		}
		else {
			//Otherwise will be the first element, name : if (type == "name")
			Person individual = this.personRepository.createPerson(person);
			individual.setLongTermCtx(Person.NAME, context);
		}
	}

}



