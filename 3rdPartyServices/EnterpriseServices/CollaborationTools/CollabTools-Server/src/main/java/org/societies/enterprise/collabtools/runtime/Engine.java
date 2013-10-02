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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.enterprise.collabtools.api.IEngine;
import org.societies.enterprise.collabtools.interpretation.ContextAnalyzer;

/**
 * Rule Engine can evaluate rules and execute actions for the collaborative sessions.
 *
 * @author cviana
 *
 */


public class Engine implements IEngine {

	private static Logger log = LoggerFactory.getLogger(Engine.class);
	public PersonRepository personRepository;
	public SessionRepository sessionRepository;

	private List<Rule> rules = new ArrayList<Rule>();
	private ContextAnalyzer ctxRsn;

	/**
	 * @param sessionRepository 
	 * @param personRepository 
	 * 
	 */
	public Engine(PersonRepository personRepository, SessionRepository sessionRepository) {
		this.personRepository = personRepository;
		this.sessionRepository = sessionRepository;
		this.ctxRsn = new ContextAnalyzer(personRepository);
	}

	/** 
	 * Insert individual rule
	 * 
	 * @param rules individual rule for the engine.
	 * 
	 * */
	public synchronized void insertRule(Rule rule){
		if (rule.getOperator().equals(Operators.SIMILAR)){
			// context enrichment with Concept and Category
			ctxRsn.enrichedCtx(rule.getCtxAttribute());
			//Assigning new weights or update the existing one
			ctxRsn.setupWeightAmongPeople(rule.getCtxAttribute());
		}
		this.rules.add(rule);

		//order by priority
		Collections.sort(this.rules,Collections.reverseOrder());
		log.info("inserted rule: " + rule);
	}

	public synchronized void deleteRule(Rule rule){
		this.rules.remove(rule);
		//order by priority
		Collections.sort(this.rules,Collections.reverseOrder());
		log.info("delete rule: " + rule);
	}

	/** 
	 * Handles the initialization 
	 * 
	 * @param rules The rules which define the system.
	 * 
	 * */
	public synchronized void setRules(final Collection<Rule> rules){
		for(Rule r : rules){
			if (r.getOperator().equals(Operators.SIMILAR)){
				// context enrichment with Concept and Category
				ctxRsn.enrichedCtx(r.getCtxAttribute());
				//Assigning new weights or update the existing one
				ctxRsn.setupWeightAmongPeople(r.getCtxAttribute());
			}
			this.rules.add(r);
			log.info("added rule: " + r);
		}
		//order by priority
		Collections.sort(this.rules,Collections.reverseOrder());
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.runtime.IEngine#getRules()
	 */
	@Override
	public synchronized List<Rule> getRules() {
		return rules;
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.runtime.IEngine#getMatchingResults()
	 */
	@Override
	public Hashtable<String, HashSet<Person>> getMatchingResultsByPriority() {
		log.info("\r\n\r\n*****Evaluating rules...*****");
		long start = System.currentTimeMillis();
		//Format ctx info and people
		Hashtable<String, HashSet<Person>> matchingRules = new Hashtable<String, HashSet<Person>>(10,10);
		for(Rule r : this.rules){
			if (matchingRules.isEmpty()) {
				matchingRules = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);
			}
			else {
				Enumeration<String> iterator = matchingRules.keys();
				while(iterator.hasMoreElements()) {
					String htKey = iterator.nextElement();
					HashSet<Person> primarySet  = matchingRules.get(htKey);
					Hashtable<String, HashSet<Person>> htTemp = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), primarySet);
					//Check here for SAME operator
					HashSet<Person> secondarySet  = htTemp.get(r.getCtxAttribute());
					primarySet.retainAll(secondarySet);
					matchingRules.put(htKey, primarySet);					
				}
			}
			log.info("matched rule: " + r.getName() + " with priority "+ r.getPriority()+" and weight "+r.getWeight()*10 +"%");
			Enumeration<String> e = matchingRules.keys();
			while (e.hasMoreElements()) {
				log.info("matchingRules: " + e.nextElement());
			}
		}

		log.info("*****Engine evaluation completed in " + (System.currentTimeMillis()-start) + " ms*****\r\n");
		return matchingRules;
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.runtime.IEngine#evaluateRule(org.societies.enterprise.collabtools.runtime.Operators, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Hashtable<String, HashSet<Person>> evaluateRule(Operators operator, final String ctxAttribute, String value, final String ctxType, HashSet<Person> personHashSet) {
		if (personHashSet == null) {
			personHashSet = new HashSet<Person>();
			//Get all people in the graph 
			for (Person person : personRepository.getAllPersons()) {
				personHashSet.add(person);
			}
		}

		Iterator<Person> itPerson = personHashSet.iterator();
		HashSet<Person> setPersons = new HashSet<Person>();
		Hashtable<String, HashSet<Person>> tablePersons = new Hashtable<String, HashSet<Person>>();

		//Check if ctxType is short term or long term
		switch (ctxType.equals(ShortTermCtxTypes.class.getSimpleName()) ? 1 : 2){

		//short term
		case 1:			
			switch (operator){
			case SAME:
				return ctxRsn.getPersonsWithMatchingShortTermCtx(operator, ctxAttribute, personHashSet);
			case DIFFERENT:

			case EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLastShortTermUpdate().getShortTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) == Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else if(value.equalsIgnoreCase(valueToCompare)) {
						setPersons.add(person);
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;

			case NOT_EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLastShortTermUpdate().getShortTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) != Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else if(!value.equalsIgnoreCase(valueToCompare)) {
						setPersons.add(person);
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case GREATER:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLastShortTermUpdate().getShortTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) > Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case GREATER_OR_EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLastShortTermUpdate().getShortTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) >= Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case LESS:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLastShortTermUpdate().getShortTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) < Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case LESS_OR_EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLastShortTermUpdate().getShortTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) <= Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			}
			break;

			//********Long term
		case 2:
			switch (operator){
			case SAME:
				return ctxRsn.getPersonsWithMatchingLongTermCtx(ctxAttribute, personHashSet);
			case DIFFERENT:
				//TODO: fix this
				return tablePersons;
			case EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLongTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) == Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else if(value.equalsIgnoreCase(valueToCompare)) {
						setPersons.add(person);
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case NOT_EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLongTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) != Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else if(!value.equalsIgnoreCase(valueToCompare)) {
						setPersons.add(person);
					}						
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case GREATER:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLongTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) > Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}							
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case GREATER_OR_EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLongTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) >= Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}							
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case LESS:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLongTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) < Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}							
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case LESS_OR_EQUAL:
				while(itPerson.hasNext()) {
					Person person = itPerson.next();
					String valueToCompare = person.getLongTermCtx(ctxAttribute);
					//Check if the given value is a string or a number
					if (isNumeric(value)) {
						if(Double.parseDouble(value) <= Double.parseDouble(valueToCompare)){
							setPersons.add(person);
						}
					}
					else {
						//TODO: Fix for string values...
					}							
				}
				tablePersons.put(ctxAttribute, setPersons);
				return tablePersons;
			case SIMILAR:
				if (isNumeric(value)) {
					//TODO: Fix for numeric values...
					return tablePersons;
				}
				else {
					return ctxRsn.getPersonsBySimilarity(ctxAttribute, personHashSet, ctxAttribute);
				}						
			}
			break;
		}
		return tablePersons;
	}

	@Override
	public Hashtable<String, HashSet<Person>> evaluateRule(String ruleName) {
		for (Rule r : rules) {
			if (r.getName().equalsIgnoreCase(ruleName)){
				return evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);
			}
		}
		throw new IllegalArgumentException("Rule name doesn't exist!");
//		return new Hashtable<String, HashSet<Person>>();		
	}

	/**
	 * 
	 * Return true if the string is a numeric value.
	 * 
	 * @return True for numeric values
	 */
	private static boolean isNumeric(String str)
	{
		for (char character : str.toCharArray())
		{
			if (!Character.isDigit(character)) {
				return false;
			}
		}
		return true;
	}

	//	public static <T> List<T> getDuplicate(Collection<T> list) {
	//
	//		final List<T> duplicatedObjects = new ArrayList<T>();
	//		Set<T> set = new HashSet<T>() {
	//			private static final long serialVersionUID = 1L;
	//
	//			@Override
	//			public boolean add(T e) {
	//				if (contains(e)) {
	//					duplicatedObjects.add(e);
	//				}
	//				return super.add(e);
	//			}
	//		};
	//		for (T t : list) {
	//			set.add(t);
	//		}
	//		return duplicatedObjects;
	//	}

	//	public static <T> boolean hasDuplicate(Collection<T> list) {
	//		if (getDuplicate(list).isEmpty())
	//			return false;
	//		return true;
	//	}

	//	public static boolean checkDuplicate(List<ShortTermContextUpdates> list) {
	//		HashSet<String> set = new HashSet<String>();
	//		for (int i = 0; i < list.size(); i++) {
	//			boolean val = set.add(list.get(i).getShortTermCtx(ShortTermCtxTypes.STATUS));
	//			if (val == false) {
	//				return val;
	//			}
	//		}
	//		return true;
	//	}

}
