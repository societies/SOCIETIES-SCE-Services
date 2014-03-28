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
import java.util.HashMap;
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
	private HashMap<String, HashSet<Person>> hashCtxList = new HashMap<String, HashSet<Person>>(10);
	
	//TODO: api key hardcoded....Change to config.propreties
	final String APIKEY = "ca193cc1d3101c225266787a3d5fc1f810b52f02";
	private boolean weightAlreadyAssigned = false;

	/**
	 * @param personRepository
	 */
	public ContextAnalyzer(final PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	/**
	 * 	Context enrichment with Concept and Category
	 * 
	 * @param ctxAttribute Context attribute. e.g LongTermCtxTypes.INTERESTS
	 */
	public final void enrichedCtx(String ctxAttribute)
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
	@Override
	public final void incrementCtx(final String ctxType, final EnrichmentTypes enrichmentType, Person person) {
		//Considering all persons to increment when null
		if (null == person) {
			ExecutorService executor = Executors.newCachedThreadPool();
			List<FutureTask<Boolean>> results = new ArrayList<FutureTask<Boolean>>();
			for (final Person friend :personRepository.getAllPersons()) {
				//Check if this ctxTyp was incremented
				if (null != friend.getArrayLongTermCtx("ORIGINAL_"+ctxType)){
					continue;
				}
				// Start thread for the first half of the numbers
				FutureTask<Boolean> task = new FutureTask<Boolean>(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						String[] originalCtx;
						//Keeping the original ctx information
//						originalCtx = friend.getArrayLongTermCtx(ctxType);
						
						//Check if this ctxTyp was incremented
						if (friend.getArrayLongTermCtx("ORIGINAL_"+ctxType) == null) {
							//Taking the original ctx information
							originalCtx = friend.getArrayLongTermCtx(ctxType);
							//Saving the original ctx in a different property
							friend.setLongTermCtx("ORIGINAL_"+ctxType, originalCtx);
						}
						else {
							originalCtx = friend.getArrayLongTermCtx("ORIGINAL_"+ctxType);
						}
						
						//Adding the new ctx info + original ctx info
						String[] newContexts = ctxEnrichment(originalCtx, ctxType.toString(), enrichmentType);
						friend.setLongTermCtx(ctxType, newContexts);
						if (newContexts.length != originalCtx.length) {
							log.debug("{} enrichment done for person {}",enrichmentType,friend.getName());
							System.out.println("Done");
						}
						else {
							log.debug("{} enrichment WAS NOT done for person {}",enrichmentType,friend.getName());
							System.out.println("NOT Done");
						}

						return true;
					}
				});
				results.add(task);
				executor.execute(task);
			}
			for(FutureTask<Boolean> fut : results) {
				try {
					fut.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			executor.shutdown();
		}
		else {
			String[] originalCtx;
			if (null == person.getArrayLongTermCtx("ORIGINAL_"+ctxType)) {
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

	/**
	 * Check if there is no similarity between both persons.
	 * Similarity Formula is: (similar ctx/ personA + similar ctx/personB) / 2
	 * 
	 * @param similarCtx
	 * @param ctxType context type
	 * @param personA
	 * @param personB
	 * @return
	 */
	static public double personCtxSimilarity (int similarCtx, String ctxType, Person personA, Person personB) {
		if (0 != similarCtx) {
			double PersonAweight = (double)similarCtx/personA.getArrayLongTermCtx(ctxType).length;
			double PersonBweight = (double)similarCtx/personB.getArrayLongTermCtx(ctxType).length;
			return (PersonAweight + PersonBweight) / 2;
		}
		else
			throw new IllegalArgumentException("There is no similarity between this individuals");
	}


	/**
	 * Based on automatic thresholding. Formula: (avg < Mean/avg > Mean)/2
	 * 
	 * @param elements
	 * @return a double value as a cut point
	 */
	public static double getAutoThreshold(final ArrayList<Double> elements) {
		double initialThreshold = 0;
		for (double value : elements)
		{
			initialThreshold += value;
		}
		initialThreshold = initialThreshold / elements.size();
		double finalThreshold = 0;
		boolean done = false;
		while (!done) {
			double avgG1 = 0,  avgG2 = 0;
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
			if (Double.isNaN(finalThreshold)){
				return initialThreshold;
			}
			//Because doubleing point calculations may involve rounding, calculated double and double values may not be accurate
			if (Math.abs(initialThreshold - finalThreshold) < .0000001) {
				done = true;
			}
			else
				initialThreshold = finalThreshold;
		}
		return finalThreshold;
	}
	
	
	/**
	 * @param operator Operators available in org.societies.collabtools.runtime.Operators
	 * @param ctxAtributte Context attribute. The context can range from interests, occupation, etc..
	 * @param hashsetPersons Persons to analyze
	 * @return HashMap of everyone in the graph with same context attribute
	 */
	public synchronized HashMap<String, HashSet<Person>> getPersonsWithMatchingShortTermCtx(Operators operator, final String ctxAtributte, HashSet<Person> personHashSet) {
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
	 * @return HashMap of everyone in the graph with same context attribute
	 */
	@SuppressWarnings("unchecked")
	public synchronized HashMap<String, HashSet<Person>> getPersonsWithMatchingLongTermCtx(final String ctxAtributte, HashSet<Person> hashsetPersons) {
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
		HashMap<String, HashSet<Person>> hashCtxList = (HashMap<String, HashSet<Person>>) this.hashCtxList.clone();
		return hashCtxList;
	}


	/**
	 * @param ctxAtributte Context attribute. The context can range from interests, occupation, etc..
	 * @param ctxType The context type needs to be long term or short term
	 * @return HashMap of everyone in the graph with same context attribute
	 */
	public final HashMap<String, HashSet<Person>> getAllPersonsWithSameCtx(final String ctxAttribute, final String ctxType) {
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
	 * @return HashMap Return the results with a session name and the persons
	 */
	public final HashSet<Person> getPersonsPerSimilarity(HashSet<Person> hashsetPersons, String ctxAttribute) {
		if (!hashsetPersons.isEmpty()) {
			ArrayList<Double> weightValues = this.getWeightSum(hashsetPersons, ctxAttribute);
			double threshold = ContextAnalyzer.getAutoThreshold(weightValues);
			log.info("automaticThresholding: {}",threshold);
			HashSet<Person> newHashsetPersons = new HashSet<Person>();
			HashSet<Long> hashsetTemp = new HashSet<Long>();
			for (Person individual : hashsetPersons) {
				for (Person otherPerson : hashsetPersons) {
					Relationship rel = individual.getPersonRelationshipTo(otherPerson, ctxAttribute);
					//Check by relationship ID if the weight was included in the hashset
					if (null != rel &&  !hashsetTemp.contains(rel.getId())) {
						//							log.info(((double)rel.getProperty("weight")));
						hashsetTemp.add(rel.getId());
						if ((Double)rel.getProperty("weight") >= threshold) {
							newHashsetPersons.add(individual);
							newHashsetPersons.add(otherPerson);
						}
					}
				}
			}
			return newHashsetPersons;
		}
		else {
			return hashsetPersons;
		}
	}
	
	/**
	 * This method generate weight among the relationship of all person nodes based on similarity
	 * 
	 * @param CtxAttribute LongTermCtx attribute
	 */
	public final void setupWeightAmongPeople(String ctxAttribute)
	{
		if (!weightAlreadyAssigned) {
			log.info("\r\n\r\n*****Assign weight for {} *****", ctxAttribute);
			long start = System.currentTimeMillis();
			for (Person person : personRepository.getAllPersons())
			{
				Map<Person, Integer> persons = personRepository.getPersonWithSimilarCtx(person, ctxAttribute);
				log.debug("{} CtxAttribute {}", person.getName(), Arrays.toString(person.getArrayLongTermCtx(ctxAttribute)));
				log.info("\r\n\r\nCalculating for person {} \r\n", person.getName());
				for (Map.Entry<Person, Integer> entry : persons.entrySet()) {
					//Similarity Formula is: (similar ctx/ personA + similar ctx/personB) / 2
					double weight = ContextAnalyzer.personCtxSimilarity(entry.getValue(), ctxAttribute, entry.getKey(), person);
					person.addSimilarityRelationship(entry.getKey(), weight, ctxAttribute);
					log.info("Weight {} assigned for person {}", weight, entry.getKey());
				}
			}
			log.info("*****Weight assigned in {} ms*****\r\n",(System.currentTimeMillis()-start));
			weightAlreadyAssigned = true;
		}
	}

	
	private boolean hasSameShortTermCtx(final ShortTermContextUpdates ctx, final ShortTermContextUpdates[] temp, final String ctxAttribute) {
		HashSet<Person> hashsetTemp;
		hashsetTemp = hashCtxList.get(ctx.getShortTermCtx(ctxAttribute));
		for(int j = 0; j < temp.length; j++) {
			if(null != temp[j] && ctx.getShortTermCtx(ctxAttribute).equals(temp[j].getShortTermCtx(ctxAttribute))) {
				if (null == hashsetTemp) {
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
	private boolean hasSameLongTermCtx(final Person person, final Person[] people, final String ctxAtributte) {
		HashSet<Person> hashsetTemp;
		hashsetTemp = hashCtxList.get(person.getLongTermCtx(ctxAtributte));
		for(int j = 0; j < people.length; j++) {
			if(null != people[j] && person.getLongTermCtx(ctxAtributte).equals(people[j].getLongTermCtx(ctxAtributte))) {
				if (null == hashsetTemp) {
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
	private HashMap<String, HashSet<Person>>  getUniqueElements(final ShortTermContextUpdates[] statusArray, final String ctxAttribute) {
		ShortTermContextUpdates[] temp = new ShortTermContextUpdates[statusArray.length]; // null elements
		log.debug("Number of persons: {} with context [}",temp.length,ctxAttribute);
		int count = 0;
		for(int j = 0; j < statusArray.length; j++) {
			if(hasSameShortTermCtx(statusArray[j], temp, ctxAttribute))
				temp[count++] = statusArray[j];
		}
		HashMap<String, HashSet<Person>>  hashCtxList = (HashMap<String, HashSet<Person>>) this.hashCtxList.clone();
		return hashCtxList;
	}

	/**
	 * @param person persons as a start node
	 * @param ctxAttribute LongTermCtx attribute
	 * @param threshold cut-off point
	 * @return a HashMap with a person key and his relationships weights with other persons
	 */
	public final HashMap<Person, Double> getSimilarityPerPerson(final Person person, final String ctxAttribute, final double threshold) {
		Iterable<Relationship> relationships = person.getSimilarityRelationships(ctxAttribute);
		HashMap<Person, Double> hashWeightList = new HashMap<Person, Double>(10);
		for (Relationship rel: relationships) {
			Double weight = (Double)rel.getProperty("weight");
			log.debug("weight {}", weight);
			if (weight>=threshold){
				//Verifying if A is the end node. (a) --- REL_TYPE ---> (b)
				Person a = new Person(rel.getStartNode());
				Person b = new Person(rel.getEndNode());
				if (person.equals(b))  {
					hashWeightList.put(a, weight);
					log.debug("Person {} has relationship with: {}", person, a.getName());
				}
				else {
					hashWeightList.put(b, weight);
					log.debug("Person {} has relationship with: {}", person, b.getName());
				}

			}
		}
		return hashWeightList;
	}

	/**
	 * @param ctxAttribute Long Term ctx type
	 * @return a HashMap with format: Person and his weight
	 */
	public final HashMap<Person, HashMap<Person, Double>> getPersonMatrix(final String ctxAttribute) {
		HashMap<Person, HashMap<Person, Double>> participantsMatrix = new HashMap<Person, HashMap<Person, Double>>();
		HashMap<Person, Double> matchingResults = new  HashMap<Person, Double>();
		ArrayList<Double> weightSum = this.getWeightSum(null, ctxAttribute);
		double threshold = ContextAnalyzer.getAutoThreshold(weightSum);
		log.info("Autothreshold {}", threshold);
		for (Person person : personRepository.getAllPersons()) {
			matchingResults = this.getSimilarityPerPerson(person, ctxAttribute, threshold);
			//Remove empty sets
			if (!matchingResults.values().isEmpty()){
				participantsMatrix.put(person, matchingResults);
			}
			//Remove cases where results have 2-3 and 3-2 which is the same similarity
//			Set<Person> group = matchingResults.keySet();
//			for (Person individual : group){
//				HashMap<Person, Double> personToCompare = participantsMatrix.get(individual);
//				if (personToCompare != null && personToCompare.containsKey(person)){
//					participantsMatrix.get(individual).remove(person);
//					if (participantsMatrix.get(individual).isEmpty()){
//						participantsMatrix.remove(individual);
//					}
//				}
//			}
		}
		return participantsMatrix;
	}
	
	public final ArrayList<Double> getWeightSum(final HashSet<Person> hashsetPersons, final String ctxAttribute){
		ArrayList<Double> weightValues = new ArrayList<Double>(); 
		if (null == hashsetPersons){
			for (Person person : personRepository.getAllPersons()) {				
				Iterable<Relationship> similarityWeights = person.getUnderlyingNode().getRelationships(DynamicRelationshipType.withName("SIMILARITY_"+ctxAttribute), Direction.OUTGOING);
				for (Relationship rel : similarityWeights) {
					log.debug("startNode: {}", new Person(rel.getStartNode()));
					log.debug("endNode: {}",new Person(rel.getEndNode()));
					weightValues.add((Double) rel.getProperty("weight"));//Property of relationship similarity
				}
			}
		}
		else {
			for (Person person : hashsetPersons) {				
				Iterable<Relationship> similarityWeights = person.getUnderlyingNode().getRelationships(DynamicRelationshipType.withName("SIMILARITY_"+ctxAttribute), Direction.OUTGOING);
				for (Relationship rel : similarityWeights) {
					log.debug("startNode: {}", new Person(rel.getStartNode()));
					log.debug("endNode: {}",new Person(rel.getEndNode()));
					weightValues.add((Double) rel.getProperty("weight"));//Property of relationship similarity
				}
			}
		}		
		return weightValues;		
	}	
}
