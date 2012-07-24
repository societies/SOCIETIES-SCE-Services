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
package org.societies.enterprise.collabtools.Interpretation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.api.IContextReasoning;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Describe your class here...
 *
 * @author cviana
 *
 */
public class ContextAnalyzer implements IContextReasoning {

	private PersonRepository personRepository;

	/**
	 * @param personRepository
	 */
	public ContextAnalyzer(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	private final String[] ctxEnrichedByConcept(String[] interests) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException{
		final String APIKEY = "ca193cc1d3101c225266787a3d5fc1f810b52f02";
		// Create an AlchemyAPI object.
		//AlchemyAPI api key, enable to 1000 queries a day
		AlchemyAPISimple alchemyObj = AlchemyAPISimple.GetInstanceFromString(APIKEY);

		Set<String> setInterests = new HashSet<String>(); 
		// Extract concept tags for a text string.
		for (String interest : interests) {
			setInterests.add(interest);
			//Check if interest is null
			System.out.println("Interest: "+interest);
			Document doc = alchemyObj.TextGetRankedConcepts(interest);
			if (doc != null) {
				NodeList result = doc.getElementsByTagName("text");
				for (int i = 0; i < result.getLength(); i++) {
					setInterests.add(result.item(i).getTextContent().toLowerCase());
				}
			}
		}
		return setInterests.toArray(new String[setInterests.size()]);
	}
	
	/**
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws XPathExpressionException 
	 * 
	 */
	public void incrementInterests() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
    	for (Person friend :personRepository.getAllPersons()) {
    		String[] newInterests = ctxEnrichedByConcept(friend.getInterests());
    		friend.setLongTermCtx(LongTermCtxTypes.INTERESTS, newInterests);
    	}		
	}

}
