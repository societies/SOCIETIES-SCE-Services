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
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.acquisition.RelTypes;
import org.societies.enterprise.collabtools.acquisition.ShortTermContextUpdates;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.enterprise.collabtools.interpretation.ContextAnalyzer;

/**
 * Rule Engine can evaluate rules and execute actions for the collaborative sessions.
 *
 * @author cviana
 *
 */


public class Engine {

	private static Logger log = LoggerFactory.getLogger(Engine.class);
	public PersonRepository personRepository;
	public SessionRepository sessionRepository;
	private Hashtable<String, HashSet<Person>> hashCtxList = new Hashtable<String, HashSet<Person>>(10,10);
	private final List<Rule> rules = new ArrayList<Rule>();

	/**
	 * @param sessionRepository 
	 * @param personRepository 
	 * 
	 */
	public Engine(PersonRepository personRepository, SessionRepository sessionRepository) {
		this.personRepository = personRepository;
		this.sessionRepository = sessionRepository;
	}
	
	/** 
	 * Handles the initialization 
	 * 
	 * @param rules The rules which define the system.
	 * 
	 * */
	void setRules(final Collection<Rule> rules){
		for(Rule r : rules){
			this.rules.add(r);
			log.info("added rule: " + r);
		}
		//order by priority
	    Collections.sort(this.rules,Collections.reverseOrder());
	}
	public Hashtable<String, HashSet<Person>> getMatchingResults() {
		log.info("\r\n\r\n*****Evaluating rules...*****");
		long start = System.currentTimeMillis();
		//Format ctx info and people
		Hashtable<String, HashSet<Person>> matchingRules = new Hashtable<String, HashSet<Person>>(10,10);
		for(Rule r : this.rules){
			matchingRules = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType());
			log.info("matched rule: " + r.getName() + " with priority: "+ r.getPriority()*10 +"%");
			Enumeration<String> e = matchingRules.keys();
			while (e.hasMoreElements()) {
				log.info("matchingRules: " + e.nextElement());
			}
		}
		
		log.info("*****Engine evaluation completed in " + (System.currentTimeMillis()-start) + " ms*****\r\n");
		return hashCtxList;
		
	}

	/**
	 * @param operator Filter operators available in {@link Operators}
	 * @param ctx Context information 
	 * @param value Value if wants to compare. Null for SAME or DIFFERENT operators
	 * @param ctxType Context type. Can be {@link ShortTermCtxTypes} or {@link LongTermCtxTypes}
	 * @return hashtable of persons
	 */
	public Hashtable<String, HashSet<Person>> evaluateRule(Operators operator, final String ctxAttribute, String value, final String ctxType) {
		HashSet<Person> personHashSet = new HashSet<Person>();
		for (Person person : personRepository.getAllPersons() ) {
			personHashSet.add(person);
		}
		//Check if ctxType is short term or long term
		switch (ctxType.equals(ShortTermCtxTypes.class.getSimpleName()) ? 1 : 2){
		//short term
		case 1:
			switch (operator){
			case SAME:
				return getPersonsWithMatchingShortTermCtx(ctxAttribute, personHashSet);
			case DIFFERENT:

			case EQUAL:
//				if(number == (filterNumber)) return true;
//				else return false;
			case NOT_EQUAL:
//				if(number != (filterNumber))  return true;
//				else return false;
			case GREATER:
//				if(number > (filterNumber))  return true;
//				else return false;
			case GREATER_OR_EQUAL:
//				if(number >= (filterNumber))  return true;
//				else return false;
			case LESS:
//				if(number < (filterNumber))  return true;
//				else return false;
			case LESS_OR_EQUAL:
//				if(number <= (filterNumber))  return true;
//				else return false;
			}
			break;
		//long term
		case 2:
			switch (operator){
			case SAME:
				return getPersonsWithMatchingLongTermCtx(ctxAttribute, personHashSet);
			case DIFFERENT:

			case EQUAL:
//				if(number == (filterNumber)) return true;
//				else return false;
			case NOT_EQUAL:
//				if(number != (filterNumber))  return true;
//				else return false;
			case GREATER:
//				if(number > (filterNumber))  return true;
//				else return false;
			case GREATER_OR_EQUAL:
//				if(number >= (filterNumber))  return true;
//				else return false;
			case LESS:
//				if(number < (filterNumber))  return true;
//				else return false;
			case LESS_OR_EQUAL:
//				if(number <= (filterNumber))  return true;
//				else return false;
			}
			break;
		}


		return null;
	}
	
	/**
	 * @param ctxAtributte Context attribute. The context needs to be short term
	 * @param hashsetPersons
	 * @return Hashtable of everyone in the graph with same context attribute
	 */
	public synchronized Hashtable<String, HashSet<Person>> getPersonsWithMatchingShortTermCtx(final String ctxAtributte, HashSet<Person> personHashSet) {
		//Compare ctxAtributte
		this.hashCtxList.clear();
		Set<ShortTermContextUpdates> lastestUpdates = new HashSet<ShortTermContextUpdates>();
		Iterator<Person> it = personHashSet.iterator();
		while(it.hasNext()) {
			lastestUpdates.add(it.next().getLastStatus());
		}
		lastestUpdates.removeAll(Collections.singleton(null));
		ShortTermContextUpdates[] statusUpdateArray = new ShortTermContextUpdates[lastestUpdates.size()];
		lastestUpdates.toArray(statusUpdateArray);
		return  getUniqueElements(statusUpdateArray, ctxAtributte);	
	}

	/**
	 * @param ctxAtributte Context attribute. The context needs to be long term
	 * @param hashsetPersons
	 * @return Hashtable of everyone in the graph with same context attribute
	 */
	@SuppressWarnings("unchecked")
	public synchronized Hashtable<String, HashSet<Person>> getPersonsWithMatchingLongTermCtx(final String ctxAtributte, HashSet<Person> hashsetPersons) {
		//For long term context types
		this.hashCtxList.clear();
		Person[] person = new Person[hashsetPersons.size()];
		hashsetPersons.toArray(person);
		for (Person p : person) {
			log.info(p.getLongTermCtx(ctxAtributte));
		}
		Person[] temp = new Person[person.length]; // null array of persons
		int count = 0;
		for(int j = 0; j < person.length; j++) {
			if(hasSameLongTermCtx(person[j], temp, ctxAtributte))
				temp[count++] = person[j];
		}
		log.info("Number of persons with context "+ctxAtributte);
		Hashtable<String, HashSet<Person>>  hashCtxList = new Hashtable<String, HashSet<Person>>(10,10);
		hashCtxList = (Hashtable<String, HashSet<Person>>) this.hashCtxList.clone();
		return hashCtxList;
	}


	/**
	 * @return Hashtable of everyone in the graph with same context attribute
	 */
	public Hashtable<String, HashSet<Person>> getAllWithSameCtx(final String ctxAttribute, final String ctxType) {
		HashSet<Person> personHashSet = new HashSet<Person>();
		for (Person person : personRepository.getAllPersons() ) {
			personHashSet.add(person);
		}
		//Check context type, long or short
		if (ctxType.equals(ShortTermCtxTypes.class.getSimpleName())){
			return getPersonsWithMatchingShortTermCtx(ctxAttribute, personHashSet);
		}
		else {
			return getPersonsWithMatchingLongTermCtx(ctxAttribute, personHashSet);
		}
	}

	/**
	 * @param sessionName Name of the session
	 * @param hashSet
	 * @return Hashtable of persons
	 */
	public Hashtable<String, HashSet<Person>> getPersonsByWeight(String sessionName, HashSet<Person> hashsetPersons) {
		//For long term context types
		Hashtable<String, HashSet<Person>> hashCtxListTemp = new Hashtable<String, HashSet<Person>>(10,10);
		if (!hashsetPersons.isEmpty()) {
			Person[] person = new Person[hashsetPersons.size()];
			hashsetPersons.toArray(person);
			ArrayList<Float> elements = new ArrayList<Float>(); 
			for (Person p : person) {
				Iterable<Relationship> knows = p.getUnderlyingNode().getRelationships(RelTypes.SIMILARITY, Direction.OUTGOING);
				while (knows.iterator().hasNext()) {
					Relationship rel = knows.iterator().next();
					elements.add((Float) rel.getProperty("weight"));//Property of relationship similarity
				}
			}
			float weight = ContextAnalyzer.getAutoThreshold(elements);
			log.info("automaticThresholding: "+weight);
			HashSet<Person> newHashsetPersons = new HashSet<Person>();
			HashSet<Long> hashsetTemp = new HashSet<Long>();
			for (Person individual : person) {
				for (Person otherPerson : person) {
					Relationship rel = individual.getFriendRelationshipTo(otherPerson);
					//Check by relationship ID if the weight was included in the hashset
					if (rel != null &&  !hashsetTemp.contains(rel.getId())) {
						//							log.info(((Float)rel.getProperty("weight")));
						hashsetTemp.add(rel.getId());
						if ((Float)rel.getProperty("weight") >= weight) {
							newHashsetPersons.add(individual);
							newHashsetPersons.add(otherPerson);
						}
					}
				}
			}
			hashCtxListTemp.put(sessionName, newHashsetPersons);
			return hashCtxListTemp;
		}
		else {
			hashCtxListTemp.put(sessionName, hashsetPersons);
		}
		return hashCtxListTemp;

	}

	private boolean hasSameShortTermCtx(ShortTermContextUpdates ctx, ShortTermContextUpdates[] temp, final String ctxAttribute) {
		HashSet<Person> hashsetTemp;
		hashsetTemp = hashCtxList.get(ctx.getShortTermCtx(ctxAttribute));
		for(int j = 0; j < temp.length; j++) {
			if(temp[j] != null && ctx.getShortTermCtx(ctxAttribute).equals(temp[j].getShortTermCtx(ctxAttribute))) {
				if (hashsetTemp==null) {
					//has first element?
					hashsetTemp = new HashSet<Person>();
					hashsetTemp.add(temp[j].getPerson());
				}
				hashsetTemp.add(ctx.getPerson());
				hashCtxList.put(ctx.getShortTermCtx(ctxAttribute), hashsetTemp);
				return false;
			}
		}
		return true;
	}

	/**
	 * @param person
	 * @param people
	 * @param ctxAtributte 
	 * @return True if person has same context attribute
	 */
	private boolean hasSameLongTermCtx(Person person, Person[] people, final String ctxAtributte) {
		HashSet<Person> hashsetTemp;
		hashsetTemp = hashCtxList.get(person.getLongTermCtx(ctxAtributte));
		for(int j = 0; j < people.length; j++) {
			if(people[j] != null && person.getLongTermCtx(ctxAtributte).equals(people[j].getLongTermCtx(ctxAtributte))) {
				if (hashsetTemp==null) {
					//has first element?
					hashsetTemp = new HashSet<Person>();
					hashsetTemp.add(people[j]);
				}
				hashsetTemp.add(person);
				hashCtxList.put(person.getLongTermCtx(ctxAtributte), hashsetTemp);
				return false;
			}
		}
		return true;
	}

	/**
	 * @param statusArray
	 * @param ctxAttribute
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Hashtable<String, HashSet<Person>>  getUniqueElements(ShortTermContextUpdates[] statusArray, final String ctxAttribute) {
		ShortTermContextUpdates[] temp = new ShortTermContextUpdates[statusArray.length]; // null elements
		log.info("Number of persons: "+temp.length+" with context "+ctxAttribute);
		int count = 0;
		for(int j = 0; j < statusArray.length; j++) {
			if(hasSameShortTermCtx(statusArray[j], temp, ctxAttribute))
				temp[count++] = statusArray[j];
		}
		Hashtable<String, HashSet<Person>>  hashCtxList = new Hashtable<String, HashSet<Person>>(10,10);
		hashCtxList = (Hashtable<String, HashSet<Person>>) this.hashCtxList.clone();
		return hashCtxList;
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
