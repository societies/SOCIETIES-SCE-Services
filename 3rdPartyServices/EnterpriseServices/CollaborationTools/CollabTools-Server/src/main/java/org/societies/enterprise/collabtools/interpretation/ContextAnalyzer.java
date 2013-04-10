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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.api.IContextAnalyzer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Context Analyzer implementation suing Alchemy api
 *
 * @author cviana
 *
 */
public class ContextAnalyzer implements IContextAnalyzer {
	
	public static enum EnrichmentTypes {
		CONCEPT, CATEGORY
	}

	private PersonRepository personRepository;
	//TODO: api key hardcoded....
	final String APIKEY = "ca193cc1d3101c225266787a3d5fc1f810b52f02";

	/**
	 * @param personRepository
	 */
	public ContextAnalyzer(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	//Concepts enriched by Alchemy API
	private final String[] ctxEnrichment(String[] contexts, String ctxType, EnrichmentTypes enrichmentType){
		// Create an AlchemyAPI object.
		//AlchemyAPI api key, enable to 1000 queries a day
		AlchemyAPISimple alchemyObj = AlchemyAPISimple.GetInstanceFromString(APIKEY);

		Set<String> ctxCollection = new HashSet<String>(); 
		// Extract concept tags for a text string.
		for (String ctx : contexts) {
			ctxCollection.add(ctx);
			//Check if context is null
			System.out.println("Context "+ctxType+ " enriched by "+enrichmentType+": "+ctx);
			//Check if word has at least 5 letters
			if (ctx.length() > 5){
				Document doc = null;
				try {
					if (enrichmentType.equals(EnrichmentTypes.CATEGORY)) {
						doc = alchemyObj.TextGetCategory(ctx);
					}
					if (enrichmentType.equals(EnrichmentTypes.CONCEPT)) {
						doc = alchemyObj.TextGetRankedConcepts(ctx);
					}
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (doc != null) {
					NodeList result = doc.getElementsByTagName("text");
					for (int i = 0; i < result.getLength(); i++) {
						ctxCollection.add(result.item(i).getTextContent().toLowerCase());
					}
				}
			}
		}
		return ctxCollection.toArray(new String[ctxCollection.size()]);
	}
	
	//Categorization enriched by Alchemy API
	
	/**
	 * 
	 * ctxType Long term Context type. e.g LongTermCtxTypes.INTERESTS
	 * enrichmentType concept or category
	 * 
	 */
	public void incrementCtx(String ctxType, EnrichmentTypes enrichmentType) {
    	for (Person friend :personRepository.getAllPersons()) {
    		String[] newContexts = ctxEnrichment(friend.getArrayLongTermCtx(ctxType), ctxType.toString(), enrichmentType);
    		friend.setLongTermCtx(ctxType, newContexts);
    		System.out.println(enrichmentType+" done!");
    	}		
	}
	
	static public float personCtxSimilarity (int similarCtx, String ctxType, Person personA, Person personB) {
		//Similarity Formula is: (similar ctx/ personA + similar ctx/personB) / 2
		//Check if there is no similarity between both
		if (similarCtx != 0) {
			float PersonAweight = (float)similarCtx/personA.getArrayLongTermCtx(ctxType).length;
			float PersonBweight = (float)similarCtx/personB.getArrayLongTermCtx(ctxType).length ;
			return (PersonAweight + PersonBweight) / 2;
		}
		else
			throw new IllegalArgumentException("There is no similarity between this individuals");
	}

	//Based on automatic thresholding. Formula: (avg < Mean/avg > Mean)/2
	static public float getAutoThreshold(ArrayList<Float> elements) {
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
			if (initialThreshold == finalThreshold) {
				done = true;
			}
			else
				initialThreshold = finalThreshold;
		}
		return finalThreshold;
	}
	
	public void setupWeightBetweenPeople(Person person, String property)
	{
		Map<Person, Integer> persons = this.personRepository.getPersonWithSimilarCtx(person, property);
		for (Map.Entry<Person, Integer> individual : persons.entrySet()) {
			//Similarity Formula is: W = (matched/personA) + (matched/personB) / 2
			float weight = ContextAnalyzer.personCtxSimilarity(individual.getValue(), property, individual.getKey(), person);
			person.addFriend(individual.getKey(),weight);  
		}
	}

}
