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
package org.societies.enterprise.collabtools.interpretation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.acquisition.RelTypes;
import org.societies.enterprise.collabtools.acquisition.ShortTermContextUpdates;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.enterprise.collabtools.runtime.SessionRepository;

/**
 * Describe your class here...
 *
 * @author cviana
 *
 */
public class ContextOperators {

	private static Logger log = LoggerFactory.getLogger(ContextOperators.class);
	public PersonRepository personRepository;
	public SessionRepository sessionRepository;
	//	private Index<Node> indexStatus;
	// Start with ten, expand by ten when limit reached
	private Hashtable<String, HashSet<Person>>  hashCtxList = new Hashtable<String, HashSet<Person>>(10,10);

	/**
	 * @param sessionRepository 
	 * @param personRepository 
	 * 
	 */
	public ContextOperators(PersonRepository personRepository, SessionRepository sessionRepository) {
		this.personRepository = personRepository;
		this.sessionRepository = sessionRepository;
	}

	public synchronized Hashtable<String, HashSet<Person>> getPersonsWithMatchingShortTermCtx(final String ctxAtributte, HashSet<Person> personHashSet) {
		//Compare ctxAtributte
		this.hashCtxList.clear();
		Set<ShortTermContextUpdates> lastUpdates = new HashSet<ShortTermContextUpdates>();
		Iterator<Person> it = personHashSet.iterator();
		while(it.hasNext()) {
			lastUpdates.add(it.next().getLastStatus());
		}
		lastUpdates.removeAll(Collections.singleton(null));
		ShortTermContextUpdates[] statusUpdateArray = new ShortTermContextUpdates[lastUpdates.size()];
		lastUpdates.toArray(statusUpdateArray);
		return  getUniqueElements(statusUpdateArray, ctxAtributte);	
	}

	/**
	 * @param ctxAtributte
	 * @param hashsetPersons
	 * @return 
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
	 * @return Hashtable of all graph people in the same location
	 */
	public Hashtable<String, HashSet<Person>> getAllWithSameCtx(final String ctxValue, final String ctxType) {
		HashSet<Person> personHashSet = new HashSet<Person>();
		for (Person person : personRepository.getAllPersons() ) {
			personHashSet.add(person);
		}
		//Check context type, long or short
		if (ctxType.equals(ShortTermCtxTypes.class.getSimpleName())){
			return getPersonsWithMatchingShortTermCtx(ctxValue, personHashSet);
		}
		else {
			return getPersonsWithMatchingLongTermCtx(ctxValue, personHashSet);
		}
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
	 * @return
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

	/**
	 * @param interests
	 * @param hashSet
	 * @return
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
