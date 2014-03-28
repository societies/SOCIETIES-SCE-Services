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
package org.societies.collabtools.tests;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.junit.Test;
import org.societies.collabtools.api.IIncrementCtx;
import org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes;
import org.societies.collabtools.interpretation.IncrementCtx;
import org.w3c.dom.Document;

import scala.actors.threadpool.Arrays;

/**
 * Unit tests for Context information increments
 *
 * @author Chris Lima
 *
 */
public class IncrementCtxTest {
	
	private static final Random r = new Random(System.currentTimeMillis());

	//TODO: api key hardcoded....Change to config.propreties
	final String APIKEY = "ca193cc1d3101c225266787a3d5fc1f810b52f02";
	
	//Creating an AlchemyAPI object.
	//AlchemyAPI api key, enable to 1000 queries a day
	IIncrementCtx alchemyObj = IncrementCtx.GetInstanceFromString(APIKEY);

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.IncrementCtx#GetInstanceFromString(java.lang.String)}.
	 */
	@Test
	public void testGetInstanceFromString() {
		alchemyObj = IncrementCtx.GetInstanceFromString(APIKEY);
		Assert.assertNotNull(alchemyObj);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.IncrementCtx#incrementString(java.lang.String, org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes)}.
	 */
	@Test
	public void testIncrementStringTrial() {
		String ctx = "Aquatic Ecotoxicology, Terrestrial Ecotoxicology, Aquatic Invertebrates, Terrestrial Invertebrates, " +
				"Bioaccumulation and Biomagnification, Oxidative Stress Biomarkers, Environmental Toxicology, Biodiversity and Conservation, " +
				"Metabolomics, Proteomics, Nanoparticles, Pesticides, Metals, " +
				"R Statistical Software, IBM SPSS Statistics, Minitab Statistics Package" ;
		String ctx1 = "Biomarkers in Ecotoxicology";
		System.out.println("\nOriginal ctx: "+ctx);
		String [] response = alchemyObj.incrementString(ctx1, EnrichmentTypes.CONCEPT);
		System.out.println("Response concept: "+Arrays.toString(response));
		response = alchemyObj.incrementString(ctx1, EnrichmentTypes.CATEGORY);
		System.out.println("Response category: "+Arrays.toString(response));
		Assert.assertNotNull(Arrays.toString(response), response);
		response = alchemyObj.incrementString(ctx1, EnrichmentTypes.TAXONOMY);
		System.out.println("Response taxonomy: "+Arrays.toString(response));
		Assert.assertNotNull(Arrays.toString(response), response);
	}
	
	/**
	 * Test method for {@link org.societies.collabtools.interpretation.IncrementCtx#incrementString(java.lang.String, org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes)}.
	 */
	@Test
	public void testIncrementString() {
		for (String ctx : getRandomInterests()){
			System.out.println("Original ctx: "+ctx);
			String [] response = alchemyObj.incrementString(ctx, EnrichmentTypes.CONCEPT);
			System.out.println("Response concept: "+Arrays.toString(response));
			response = alchemyObj.incrementString(ctx, EnrichmentTypes.CATEGORY);
			System.out.println("Response category: "+Arrays.toString(response));
			Assert.assertNotNull(response.toString(), response);
		}
	}
	
	/**
	 * 
	 * @return A string array with 7 interests
	 */
	private static String[] getRandomInterests() {
		final String[] interests={"bioinformatics", "web development", "semantic web", "requirements analysis", "system modeling", 
				"project planning", "project management", "software engineering", "software development", "technical writing"};
		Set<String> finalInterests = new HashSet<String>();
		for(int i=0; i<7; i++){
			String temp = interests[r.nextInt(interests.length)];
			//Check if duplicated
			if (!finalInterests.contains(temp))
				finalInterests.add(temp);
			else
				i--;
		}
		return finalInterests.toArray(new String[0]);
	}

}
