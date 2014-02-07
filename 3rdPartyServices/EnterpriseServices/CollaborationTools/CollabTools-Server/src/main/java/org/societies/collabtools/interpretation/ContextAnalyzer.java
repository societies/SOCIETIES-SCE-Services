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
package org.societies.collabtools.interpretation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.acquisition.Person;
import org.societies.collabtools.acquisition.PersonRepository;
import org.societies.collabtools.acquisition.ShortTermContextUpdates;
import org.societies.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.collabtools.api.IContextAnalyzer;
import org.societies.collabtools.api.IIncrementCtx;
import org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes;
import org.societies.collabtools.runtime.Operators;

/**
 * Context Analyzer class
 *
 * @author cviana
 *
 */
public class ContextAnalyzer implements IContextAnalyzer {

	private static final Logger log = LoggerFactory.getLogger(ContextAnalyzer.class);
	private final PersonRepository personRepository;
	private Hashtable<String, HashSet<Person>> hashCtxList = new Hashtable<String, HashSet<Person>>(10,10);
	
	//TODO: api key hardcoded....Change to config.propreties
	final String APIKEY = "ca193cc1d3101c225266787a3d5fc1f810b52f02";

	/**
	 * @param personRepository
	 */
	public ContextAnalyzer(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	/**
	 * 	Context enrichment with Concept and Category
	 * 
	 * @param ctxAttribute Context attribute. e.g LongTermCtxTypes.INTERESTS
	 */
	public void enrichedCtx(String ctxAttribute)
	{
		log.debug("\r\n\r\n*****Incrementing Ctx*****");
		long start = System.currentTimeMillis();
		this.incrementCtx(ctxAttribute, EnrichmentTypes.CONCEPT, null);
		// context enrichment considering previous concept performed
		this.incrementCtx(ctxAttribute, EnrichmentTypes.CATEGORY, null);
		log.debug("*****Ctx enrichment completed in {} ms*****\r\n", (System.currentTimeMillis()-start));
	}

	/**
	 * Concepts and Categorization enriched by Alchemy API
	 * 
	 * @param contexts context available form the node
	 * @param ctxType Long term Context type. e.g LongTermCtxTypes.INTERESTS
	 * @param enrichmentType concept or category
	 */
	private final String[] ctxEnrichment(String[] contexts, String ctxType, EnrichmentTypes enrichmentType){
		//Creating an AlchemyAPI object.
		//AlchemyAPI api key, enable to 1000 queries a day
		IIncrementCtx alchemyObj = IncrementCtx.GetInstanceFromString(APIKEY);

		Set<String> ctxCollection = new HashSet<String>(); 
		// Extract concept tags for a text string.
		for (String ctx : contexts) {
			ctxCollection.add(ctx);
			//Check if context is null
			if(log.isDebugEnabled())
				log.debug("Context "+ctxType+ " enriched by "+enrichmentType+": "+ctx);
			//Check if word has at least 5 letters
			if (ctx.length() > 5){
				ctxCollection.addAll(Arrays.asList(alchemyObj.incrementString(ctx, enrichmentType)));
			}
		}
		return ctxCollection.toArray(new String[ctxCollection.size()]);
	}


	/**
	 * 
	 * @param ctxType Long term Context type. e.g LongTermCtxTypes.INTERESTS
	 * @param enrichmentType concept, category or even both
	 * @param person if null will perform with all persons
	 */
	public void incrementCtx(final String ctxType, final EnrichmentTypes enrichmentType, Person person) {
		//Considering all persons to increment when null
		if (person == null) {
			ExecutorService executor = Executors.newCachedThreadPool();
			List<FutureTask<Boolean>> results = new ArrayList<FutureTask<Boolean>>();
			for (final Person friend :personRepository.getAllPersons()) {
				//Check if this ctxTyp was incremented
				if (friend.getArrayLongTermCtx("ORIGINAL_"+ctxType) != null){
					continue;
				}
				// Start thread for the first half of the numbers
				FutureTask<Boolean> task = new FutureTask<Boolean>(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						String[] originalCtx;
						//Keeping the original ctx information
						originalCtx = friend.getArrayLongTermCtx(ctxType);
						
//						//Check if this ctxTyp was incremented
//						if (friend.getArrayLongTermCtx("ORIGINAL_"+ctxType) == null) {
//							//Keeping the original ctx information
//							originalCtx = friend.getArrayLongTermCtx(ctxType);
//						}
//						else {
//							originalCtx = friend.getArrayLongTermCtx("ORIGINAL_"+ctxType);
//						}
						
						//Saving the original ctx in a diferent property
						friend.setLongTermCtx("ORIGINAL_"+ctxType, originalCtx);
						//Adding the new ctx info + orignal ctx info
						String[] newContexts = ctxEnrichment(originalCtx, ctxType.toString(), enrichmentType);
						friend.setLongTermCtx(ctxType, newContexts);
						log.debug("{} enrichment done for person {}",enrichmentType,friend.getName());
						return true;
					}
				});
				results.add(task);
				executor.execute(task);
			}
			for(FutureTask<Boolean> fut : results){
				try {
					fut.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			executor.shutdown();
		}
		else {
			String[] originalCtx;
			if (person.getArrayLongTermCtx("ORIGINAL_"+ctxType) == null) {
				//Keeping the original ctx information
				originalCtx = person.getArrayLongTermCtx(ctxType);
			}
			else {
				originalCtx = person.getArrayLongTermCtx("ORIGINAL_"+ctxType);
			}
			//Saving the original ctx in a diferent property
			person.setLongTermCtx("ORIGINAL_"+ctxType, originalCtx);
			//Adding the new ctx info + orignal ctx info
			String[] newContexts = ctxEnrichment(person.getArrayLongTermCtx(ctxType), ctxType.toString(), enrichmentType);
			person.setLongTermCtx(ctxType, newContexts);
			log.debug("{} done!",enrichmentType);
		}

	}

	static public float personCtxSimilarity (int similarCtx, String ctxType, Person personA, Person personB) {
		//Similarity Formula is: (similar ctx/ personA + similar ctx/personB) / 2
		//Check if there is no similarity between both
		if (similarCtx != 0) {
			float PersonAweight = (float)similarCtx/personA.getArrayLongTermCtx(ctxType).length;
			float PersonBweight = (float)similarCtx/personB.getArrayLongTermCtx(ctxType).length;
			return (PersonAweight + PersonBweight) / 2;
		}
		else
			throw new IllegalArgumentException("There is no similarity between this individuals");
	}

	//Based on automatic thresholding. Formula: (avg < Mean/avg > Mean)/2
	public static float getAutoThreshold(ArrayList<Float> elements) {
		float initialThreshold = 0;
		for (float value : elements)
		{
			initialThreshold += value;
		}
		initialThreshold = initialThreshold / elements.size();
		float finalThreshold = 0;
		boolean done = false;
		while (!done) {
			float avgG1 = 0,  avgG2 = 0;
			int nG1 = 0, nG2 = 0;
			for (int i = 0; i < elements.size(); i++) {
				if (elements.get(i) > initialThreshold) {
					avgG1 = elements.get(i) + avgG1;
					nG1++;
				}
				else {
					avgG2 = elements.get(i) + avgG2;
					nG2++;
				}
			}
			avgG1 = avgG1 / nG1;
			avgG2 = avgG2 / nG2;
			finalThreshold = (avgG1 + avgG2) / 2;
			//Check division by zero. Not a Number
			if (Float.isNaN(finalThreshold)){
				return initialThreshold;
			}
			//Because floating point calculations may involve rounding, calculated float and double values may not be accurate
			if (Math.abs(initialThreshold - finalThreshold) < .0000001) {
				done = true;
			}
			else
				initialThreshold = finalThreshold;
		}
		return finalThreshold;
	}
	
	
	/**
	 * @param operator Operators available in 
	 * @param ctxAtributte Context attribute. The context can range from interests, occupation, etc..
	 * @param hashsetPersons Persons to analyze
	 * @return Hashtable of everyone in the graph with same context attribute
	 */
	public synchronized Hashtable<String, HashSet<Person>> getPersonsWithMatchingShortTermCtx(Operators operator, final String ctxAtributte, HashSet<Person> personHashSet) {
		//Compare ctxAtributte
		this.hashCtxList.clear();
		Set<ShortTermContextUpdates> lastestShortTermCtxUpdates = new HashSet<ShortTermContextUpdates>();
		Iterator<Person> itPerson = personHashSet.iterator();
		while(itPerson.hasNext()) {
			lastestShortTermCtxUpdates.add(itPerson.next().getLastShortTermUpdate());
		}
		//remove all null elements
		lastestShortTermCtxUpdates.removeAll(Collections.singleton(null));
		ShortTermContextUpdates[] statusUpdateArray = new ShortTermContextUpdates[lastestShortTermCtxUpdates.size()];
		//Sending clean array back
		lastestShortTermCtxUpdates.toArray(statusUpdateArray);
		return  getUniqueElements(statusUpdateArray, ctxAtributte);	
	}

	/**
	 * @param ctxAtributte Context attribute. The context can range from interests, occupation, etc..
	 * @param hashsetPersons Persons to analyze
	 * @return Hashtable of everyone in the graph with same context attribute
	 */
	@SuppressWarnings("unchecked")
	public synchronized Hashtable<String, HashSet<Person>> getPersonsWithMatchingLongTermCtx(final String ctxAtributte, HashSet<Person> hashsetPersons) {
		//For long term context types
		this.hashCtxList.clear();
		Person[] person = new Person[hashsetPersons.size()];
		hashsetPersons.toArray(person);
		for (Person p : person) {
			log.debug(p.getLongTermCtx(ctxAtributte));
		}
		Person[] temp = new Person[person.length]; // null array of persons
		int count = 0;
		for(int j = 0; j < person.length; j++) {
			if(hasSameLongTermCtx(person[j], temp, ctxAtributte))
				temp[count++] = person[j];
		}
		log.debug("Number of persons with context {}",ctxAtributte);
		Hashtable<String, HashSet<Person>> hashCtxList = (Hashtable<String, HashSet<Person>>) this.hashCtxList.clone();
		return hashCtxList;
	}


	/**
	 * @param ctxAtributte Context attribute. The context can range from interests, occupation, etc..
	 * @param ctxType The context type needs to be long term or short term
	 * @return Hashtable of everyone in the graph with same context attribute
	 */
	public Hashtable<String, HashSet<Person>> getAllPersonsWithSameCtx(final String ctxAttribute, final String ctxType) {
		HashSet<Person> personHashSet = new HashSet<Person>();
		for (Person person : personRepository.getAllPersons() ) {
			personHashSet.add(person);
		}
		//Check context type, long or short
		if (ctxType.equals(ShortTermCtxTypes.class.getSimpleName())){
			//TODO:Fix this, 
			return getPersonsWithMatchingShortTermCtx(null, ctxAttribute, personHashSet);
		}
		else {
			return getPersonsWithMatchingLongTermCtx(ctxAttribute, personHashSet);
		}
	}

	/**
	 * @param sessionName Name of the session
	 * @param hashSet Persons to analyze
	 * @return Hashtable Return the results with a session name and the persons
	 */
	public Hashtable<String, HashSet<Person>> getPersonsBySimilarity(String sessionName, HashSet<Person> hashsetPersons, String ctxAttribute) {
		//For long term context types
		Hashtable<String, HashSet<Person>> hashCtxListTemp = new Hashtable<String, HashSet<Person>>(10,10);
		if (!hashsetPersons.isEmpty()) {
			Person[] person = new Person[hashsetPersons.size()];
			hashsetPersons.toArray(person);
			ArrayList<Float> elements = new ArrayList<Float>(); 
			for (Person p : person) {
				Iterable<Relationship> similarityWeights = p.getUnderlyingNode().getRelationships(DynamicRelationshipType.withName("SIMILARITY_"+ctxAttribute), Direction.OUTGOING);
				while (similarityWeights.iterator().hasNext()) {
					Relationship rel = similarityWeights.iterator().next();
					elements.add((Float) rel.getProperty("weight"));//Property of relationship similarity
				}
			}
			float weight = ContextAnalyzer.getAutoThreshold(elements);
			log.debug("automaticThresholding: {}",weight);
			HashSet<Person> newHashsetPersons = new HashSet<Person>();
			HashSet<Long> hashsetTemp = new HashSet<Long>();
			for (Person individual : person) {
				for (Person otherPerson : person) {
					Relationship rel = individual.getPersonRelationshipTo(otherPerson, ctxAttribute);
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
	

	public void setupWeightAmongPeople(String CtxAttribute)
	{
		log.info("\r\n\r\n*****Assign weight for {} *****",CtxAttribute);
		long start = System.currentTimeMillis();
		for (Person person : personRepository.getAllPersons())
		{
			Map<Person, Integer> persons = personRepository.getPersonWithSimilarCtx(person, CtxAttribute);
			log.debug("{} CtxAttribute {}", person.getName(), Arrays.toString(person.getArrayLongTermCtx(CtxAttribute)));
			log.info("\r\n\r\nCalculating for person {} \r\n", person.getName());
			for (Map.Entry<Person, Integer> entry : persons.entrySet()) {
				//Similarity Formula is: (similar ctx/ personA + similar ctx/personB) / 2
				float weight = ContextAnalyzer.personCtxSimilarity(entry.getValue(), CtxAttribute, entry.getKey(), person);
				person.addSimilarityRelationship(entry.getKey(), weight, CtxAttribute);
				log.info("Weight {} assigned for person {}", weight, entry.getKey());
			}
		}
		log.info("*****Weight assigned in {} ms*****\r\n",(System.currentTimeMillis()-start));
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
	 * @param person Individual as reference for analysis
	 * @param people Array of persons to analyze
	 * @param ctxAtributte Context attribute. The context can range from interests, occupation, etc..
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
	 * Verifies the persons with same ctx string
	 * 
	 * @param statusArray A set of short term ctx to be analyzed. Usually the last ctx available
	 * @param ctxAtributte Context attribute. The context can range from interests, occupation, etc..
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Hashtable<String, HashSet<Person>>  getUniqueElements(ShortTermContextUpdates[] statusArray, final String ctxAttribute) {
		ShortTermContextUpdates[] temp = new ShortTermContextUpdates[statusArray.length]; // null elements
		log.debug("Number of persons: {} with context [}",temp.length,ctxAttribute);
		int count = 0;
		for(int j = 0; j < statusArray.length; j++) {
			if(hasSameShortTermCtx(statusArray[j], temp, ctxAttribute))
				temp[count++] = statusArray[j];
		}
		Hashtable<String, HashSet<Person>>  hashCtxList = (Hashtable<String, HashSet<Person>>) this.hashCtxList.clone();
		return hashCtxList;
	}
}
