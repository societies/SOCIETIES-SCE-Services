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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.acquisition.Person;
import org.societies.collabtools.acquisition.PersonRepository;
import org.societies.collabtools.acquisition.RelTypes;
import org.societies.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.collabtools.api.IEngine;
import org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes;
import org.societies.collabtools.interpretation.ContextAnalyzer;

/**
 * Rule Engine can evaluate rules and execute actions for the collaborative sessions.
 *
 * @author cviana
 *
 */


public class Engine implements IEngine {

	private static Logger log = LoggerFactory.getLogger(Engine.class);
	public PersonRepository personRepository;

	private List<Rule> rules = new ArrayList<Rule>();
	private ContextAnalyzer ctxRsn;
	private boolean engineByPriority = true;
	private double weightSum;

	/**
	 * @param sessionRepository 
	 * 
	 */
	public Engine(PersonRepository personRepository) {
		this.personRepository = personRepository;
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
			//			ctxRsn.enrichedCtx(rule.getCtxAttribute());

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
	 * Insert a set of rules that handles the initialization 
	 * 
	 * @param rules The rules which define the system.
	 * 
	 * */
	public synchronized void setRules(final Collection<Rule> rules){
		for(Rule r : rules){
			if (r.getOperator().equals(Operators.SIMILAR)){
				// context enrichment with Concept 
				//				ctxRsn.incrementCtx(r.getCtxAttribute(), EnrichmentTypes.CONCEPT, null);
				//Assigning new weights or update the existing one
				//				ctxRsn.setupWeightAmongPeople(r.getCtxAttribute());
			}
			this.rules.add(r);
			log.info("added rule: " + r);
		}
		//order by priority
		Collections.sort(this.rules,Collections.reverseOrder());
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.runtime.IEngine#getRules()
	 */
	@Override
	public synchronized List<Rule> getRules() {
		return rules;
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.runtime.IEngine#getRulesWeightSum()
	 */
	@Override
	public double getRulesWeightSum() {
		for(Rule r : this.rules){
			this.weightSum += r.getWeight();
		}
		return weightSum;
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.runtime.IEngine#getMatchingResults()
	 */
	@Override
	public HashMap<String, HashSet<Person>> getMatchingResultsByPriority() {
		log.info("\r\n\r\n*****Evaluating rules by priority...*****");
		long start = System.currentTimeMillis();
		//Format ctx info and people
		HashMap<String, HashSet<Person>> matchingRules = new HashMap<String, HashSet<Person>>();
		for(Rule r : this.rules){
			if (matchingRules.isEmpty()) {
				matchingRules = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);
			}
			else {
				Iterator<String> iterator = matchingRules.keySet().iterator();
				while(iterator.hasNext()) {
					String htKey = iterator.next();
					HashSet<Person> primarySet  = matchingRules.get(htKey);
					HashMap<String, HashSet<Person>> htTemp = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), primarySet);
					//Check here for SAME operator
					HashSet<Person> secondarySet  = htTemp.get(r.getCtxAttribute());
					primarySet.retainAll(secondarySet);
					matchingRules.put(htKey, primarySet);
				}
			}
			log.info("matched rule: " + r.getName() + " with priority "+ r.getPriority()+" and weight "+r.getWeight()*10 +"%");
			//			Enumeration<String> e = matchingRules.keys();
			//			while (e.hasMoreElements()) {
			//				log.info("matchingRules: " + e.nextElement());
			//			}
		}
		//Remove entries with empty values
		Iterator<Entry<String, HashSet<Person>>> it = matchingRules.entrySet().iterator();
		while (it.hasNext()) {
		    Entry<String, HashSet<Person>> e = it.next();
		    HashSet<Person> value = e.getValue();
		    if (value.isEmpty()) {
		        it.remove();
		    }
		}
		log.info("*****Engine evaluation by priority completed in " + (System.currentTimeMillis()-start) + " ms*****\r\n");
		return matchingRules;
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.IEngine#getMatchingResultsByRelevance()
	 */
	@Override
	public HashMap<String, HashSet<Person>> getMatchingResultsByRelevance() {
		//Using Multivariate distance matrix
		//1-Determine distance matrix for each features variable based on coordinate
		//2-Aggregate the distance matrix
		log.info("\r\n\r\n*****Evaluating rules by relevance...*****");
		long start = System.currentTimeMillis();
		double weightSum = getRulesWeightSum();
		//Format ctx info and people
		HashMap<String, HashSet<Person>> matchingRules = new HashMap<String, HashSet<Person>>();
		HashMap<Person, Double> personPlusSimilarity = new HashMap<Person,Double>();
		HashSet<Person> allPersonsFromRules;
//		ArrayList<Double> list = new ArrayList<Double>();
		//Building a distance matrix to calculate similarity, including weight
//		Map<Person, HashMap<Person,Double>> weightedDistanceMatrix = new HashMap<Person, HashMap<Person,Double>>();
		for (Person person : personRepository.getAllPersons()) {
			//Beginning with a new hashmap
			personPlusSimilarity.clear();
			for(Rule r : this.rules){
				//Same operator case
				if (r.getOperator().equals(Operators.SAME)){
					matchingRules = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);
					//A set of same context information.E.g. Work, Gym
					for (Entry<String, HashSet<Person>> entry : matchingRules.entrySet()) {
						allPersonsFromRules = entry.getValue();
						//If this person has same ctx of the group then it is 1
						if (allPersonsFromRules.remove(person)) {
							for (Person individual : allPersonsFromRules){
								if (personPlusSimilarity.containsKey(individual)) {
									//if this individual is on the list then is the old value + (1+weight value) 
									personPlusSimilarity.put(individual, personPlusSimilarity.get(individual)+1*r.getWeight());
								} 
								else {
									//if this individual is on the list then is 1 (positive evaluation)
									personPlusSimilarity.put(individual, 1*r.getWeight());
								}
							}
						}
					}
				}
				//Similar operator case
				else if (r.getOperator().equals(Operators.SIMILAR)){
					//For tests only concept enrichment will be done
					ctxRsn.incrementCtx(r.getCtxAttribute(), EnrichmentTypes.CONCEPT, null);
					//Assigning new weights or update the existing one
					ctxRsn.setupWeightAmongPeople(r.getCtxAttribute());
					HashMap<Person, Double> allPersonsMap = ctxRsn.getSimilarityPerPerson(person, r.getCtxAttribute(), 0);
						for (Person individual : allPersonsMap.keySet()){
							if (personPlusSimilarity.containsKey(individual)) {
								//if this individual is on the list then is the old value + weighted similarity 
								personPlusSimilarity.put(individual, personPlusSimilarity.get(individual)+(allPersonsMap.get(individual)*r.getWeight()));
							}
							else { 
								personPlusSimilarity.put(individual, allPersonsMap.get(individual)*r.getWeight());
							}
						}

				}
				//Other cases (Binary operators)
				else {
					matchingRules = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);	
					allPersonsFromRules = matchingRules.get(r.getCtxAttribute());
					//If person is in the result then 1-1 the rule matched
					if (allPersonsFromRules.remove(person)) {
						for (Person individual : allPersonsFromRules){
							if (personPlusSimilarity.containsKey(individual)) {
								//if this individual is on the list then is the old value + (1+weight value) 
								personPlusSimilarity.put(individual, personPlusSimilarity.get(individual)+1*r.getWeight());
							} 
							else {
								//if this individual is on the list then is 1 (presence of attribute)
								personPlusSimilarity.put(individual, 1*r.getWeight());
							}
						}
					}
				}
			}
			for (Person individual : personPlusSimilarity.keySet()){
				double individualFinalWeight = personPlusSimilarity.get(individual)/weightSum;
				personPlusSimilarity.put(individual, individualFinalWeight);
//				list.add(individualFinalWeight);
				person.addSimilarityRelationship(individual, individualFinalWeight, RelTypes.WEIGHTED.name());
				log.debug("WeightSum: "+weightSum);
				log.debug("Calculating with weight: "+individual+ personPlusSimilarity.get(individual));
			}
			//Gower similarity approach
//			weightedDistanceMatrix.put(person, personPlusSimilarity);
		}
		HashMap<Person, HashMap<Person, Double>> participantsMatrix = ctxRsn.getPersonMatrix(RelTypes.WEIGHTED.name());
		//Print distance matrix to visualize
//		for (Person i :weightedDistanceMatrix.keySet()) {
//			System.out.println("weightedDistanceMatrix: " +i.toString()+"->"+ weightedDistanceMatrix.get(i));
//		}
		log.info("*****Engine evaluation by relevance completed in " + (System.currentTimeMillis()-start) + " ms*****\r\n");
		
//		System.out.println("participantsMatrix: "+participantsMatrix);
		//Gower similarity coefficient
//		System.out.println("Autothreshold: "+ContextAnalyzer.getAutoThreshold(list));
//		weightedDistanceMatrix = new TreeMap<Person, HashMap<Person,Double>>(weightedDistanceMatrix);
		
//		Used to compare with autothreshold result
//		double counter = 0;
//		for (double element : list) {
//			counter += element;
//		}
//		System.out.println("Autothreshold: "+counter/list.size());
		HashMap<String, HashSet<Person>> resultsByOperatorSAME = new HashMap<String, HashSet<Person>>();
		for(Rule r : this.rules){
			//Same operator case
			if (r.getOperator().equals(Operators.SAME)){
				matchingRules = evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);
				//A set of same context information.E.g. Work, Gym
				for (Entry<String, HashSet<Person>> entry : matchingRules.entrySet()) {
					HashSet<Person> personsToInclude = new HashSet<Person>();
					for (Person personToCompare : entry.getValue()) {
						if (participantsMatrix.containsKey(personToCompare)){
							personsToInclude.add(personToCompare);
						}
					}
					if (!personsToInclude.isEmpty() && personsToInclude.size()>=2){
						resultsByOperatorSAME.put(entry.getKey(), personsToInclude);
					}
				}
			}
		}

		return resultsByOperatorSAME;
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.runtime.IEngine#evaluateRule(org.societies.collabtools.runtime.Operators, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public HashMap<String, HashSet<Person>> evaluateRule(Operators operator, final String ctxAttribute, String value, final String ctxType, HashSet<Person> personHashSet) {
		if (personHashSet == null) {
			personHashSet = new HashSet<Person>();
			//Get all people in the graph 
			for (Person person : personRepository.getAllPersons()) {
				personHashSet.add(person);
			}
		}

		Iterator<Person> itPerson = personHashSet.iterator();
		HashSet<Person> setPersons = new HashSet<Person>();
		HashMap<String, HashSet<Person>> tablePersons = new HashMap<String, HashSet<Person>>();

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
					//For tests only concept enrichment will be done
					ctxRsn.incrementCtx(ctxAttribute, EnrichmentTypes.CONCEPT, null);
					//Assigning new weights or update the existing one
					ctxRsn.setupWeightAmongPeople(ctxAttribute);
					HashMap<String, HashSet<Person>> result = new HashMap<String, HashSet<Person>>();
					result.put(ctxAttribute, ctxRsn.getPersonsPerSimilarity(personHashSet, ctxAttribute));
					return result;
				}						
			}
			break;
		}
		return tablePersons;
	}

	@Override
	public HashMap<String, HashSet<Person>> evaluateRule(String ruleName) {
		for (Rule r : rules) {
			if (r.getName().equalsIgnoreCase(ruleName)){
				return evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);
			}
		}
		throw new IllegalArgumentException("Rule name doesn't exist!");
		//		return new HashMap<String, HashSet<Person>>();		
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

	/**
	 * @param flag Set engine mode to work with priority or relevance
	 * 
	 */
	public void setEngineModeByPriority(boolean flag) {
		this.engineByPriority  = flag;		
	}

	/**
	 * @param flag Get engine mode. True for priority or false for relevance. Default true
	 * 
	 */
	public boolean getEngineMode() {
		return this.engineByPriority;		
	}

}
