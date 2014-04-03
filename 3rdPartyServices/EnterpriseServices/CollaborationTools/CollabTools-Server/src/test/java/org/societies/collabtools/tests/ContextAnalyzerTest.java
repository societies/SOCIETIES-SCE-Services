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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.acquisition.LongTermCtxTypes;
import org.societies.collabtools.acquisition.Person;
import org.societies.collabtools.acquisition.PersonRepository;
import org.societies.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes;
import org.societies.collabtools.interpretation.ContextAnalyzer;
import org.societies.collabtools.runtime.Operators;

/**
 * ContextAnalyzer unit tests
 *
 * @author Christopher Lima
 *
 */
public class ContextAnalyzerTest {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ContextAnalyzerTest.class);

	private static final Random r = new Random( System.currentTimeMillis() );
	/**
	 * @return
	 */
	private static String[] getRandomInterests() {
		final String[] interests={"bioinformatics", "web development", "semantic web", "requirements analysis", "system modeling", 
				"project planning", "project management", "software engineering", "software development", "technical writing"};
		Set<String> finalInterests = new HashSet<String>();
		for(int i=0; i<3; i++){
			String temp = interests[r.nextInt(interests.length)];
			//Check if duplicated
			if (!finalInterests.contains(temp))
				finalInterests.add(temp);
			else
				i--;
		}
		return finalInterests.toArray(new String[0]);
	}
	/**
	 * @return
	 */
	private static String getRandomLocation() {
		final String[] location={"Work","Home","Gym"};
		return location[r.nextInt(3)];
	}

	/**
	 * @return
	 */
	private static String getRandomOccupation() {
		final String[] work={"Manager","Developer","Beta Tester"};
		return work[r.nextInt(3)];
	}



	/**
	 * @return
	 */
	private static String getRandomStatus() {
		final String[] status={"Online","Busy","Away"};
		return status[r.nextInt(3)];
	}

	/**
	 * @return
	 */
	private static String getRandomWorkPosition() {
		final String[] workPosition={"Manager","Marketing","Programmer"};
		return workPosition[r.nextInt(3)];
	}

	//	private static SessionRepository sessionRepository;
	private ContextAnalyzer ctxRsn;

	private  GraphDatabaseService personGraphDb, sessionGraphDb;

	//	private  Index<Node> indexShortTermCtx;
	private  PersonRepository personRepository;

	/**
	 * @param ctxRsn
	 */
	public ContextAnalyzerTest() {
		int random = new Random().nextInt(100);
		personGraphDb = new GraphDatabaseFactory().newEmbeddedDatabase("target/persontestdb00"  + random);
		sessionGraphDb = new GraphDatabaseFactory().newEmbeddedDatabase("target/sessiontestdb00"  + random);
		//	    indexShortTermCtx = personGraphDb.index().forNodes("CtxNodes", MapUtil.stringMap("to_lower_case", "true" ) );
		personRepository = new PersonRepository(personGraphDb);
		//		sessionRepository = new SessionRepository(sessionGraphDb, new CollabApps());

		LOG.info("personGraphDb path: "+"target/persontestdb00"  + random);
		//		LOG.info("sessionGraphDb path: "+"target/sessiontestdb00"  + random);

		//        ctxSub = new ContextSubscriber(null,personRepository, sessionRepository);
		LOG.info("Setup done...");

		this.ctxRsn = new ContextAnalyzer(personRepository);
	}

	private void createPersons(int nrOfPersons) throws Exception
	{
		for (int i = 0; i < nrOfPersons; i++)
		{
			Person person = personRepository.createPerson("person#" + i);
			//Set long term context
			person.setLongTermCtx(LongTermCtxTypes.NAME, "person#" + i);
			person.setLongTermCtx(LongTermCtxTypes.COLLAB_APPS, new String[] { "chat" });
			person.setLongTermCtx(LongTermCtxTypes.OCCUPATION, getRandomOccupation());
			person.setLongTermCtx(LongTermCtxTypes.INTERESTS, getRandomInterests());
			person.setLongTermCtx(LongTermCtxTypes.WORK_POSITION, getRandomWorkPosition());
			person.setLongTermCtx("age", "20");

			//Set short term context
			//			String [] response = new String [] {ShortTermCtxTypes.STATUS, getRandomStatus(), person.getName()};
			//			ctxSub.update(null, response);
			//			
			//			response = new String [] {ShortTermCtxTypes.LOCATION, getRandomLocation(), person.getName()};
			//			ctxSub.update(null, response);
		}
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#ContextAnalyzer(org.societies.collabtools.acquisition.PersonRepository)}.
	 */
	@Ignore
	public void testContextAnalyzer() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#enrichedCtx(java.lang.String)}.
	 */
	@Test
	public void testEnrichedCtx() {
		ctxRsn.enrichedCtx(LongTermCtxTypes.INTERESTS);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#getAllPersonsWithSameCtx(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetAllPersonsWithSameCtx() {
		HashMap<String, HashSet<Person>>  ht1 = ctxRsn.getAllPersonsWithSameCtx(LongTermCtxTypes.INTERESTS, ShortTermCtxTypes.class.getSimpleName());
		HashMap<String, HashSet<Person>>  ht2 = ctxRsn.getAllPersonsWithSameCtx(LongTermCtxTypes.NAME, LongTermCtxTypes.class.getSimpleName());
		Assert.assertNotNull(ht1);
		Assert.assertNotNull(ht2);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#getAutoThreshold(java.util.ArrayList)}.
	 */
	@Test
	public void testGetAutoThreshold() {
		ArrayList<Double> elements = new ArrayList<Double>();
		double threshold = ContextAnalyzer.getAutoThreshold(elements);
		Assert.assertNotNull(threshold);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#getPersonsPerSimilarity(java.lang.String, java.util.HashSet, java.lang.String)}.
	 */
	@Test
	public void testGetPersonsPerSimilarity() {
		try {
			createPersons(4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<Person> personHashSet = new HashSet<Person>();
		personHashSet.add(personRepository.getPersonByName("person#0"));
		personHashSet.add(personRepository.getPersonByName("person#1"));
		personHashSet.add(personRepository.getPersonByName("person#2"));
		personHashSet.add(personRepository.getPersonByName("person#3"));

		//Twice for test
		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CONCEPT, null);
		ctxRsn.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);
		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CATEGORY, null);
		ctxRsn.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);
		HashSet<Person> matchingRules = new HashSet<Person>(10,10);
		matchingRules = ctxRsn.getPersonsPerSimilarity(personHashSet, LongTermCtxTypes.INTERESTS);
		LOG.info("Matching results: {}", matchingRules.toString());
		Assert.assertNotNull(matchingRules);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#getSimilarityPerPersons}.
	 */
	@Test
	public void testGetSimilarityPerPersons() {
		try {
			createPersons(4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<Person> personHashSet = new HashSet<Person>();
		personHashSet.add(personRepository.getPersonByName("person#0"));
		personHashSet.add(personRepository.getPersonByName("person#1"));
		personHashSet.add(personRepository.getPersonByName("person#2"));
		personHashSet.add(personRepository.getPersonByName("person#3"));

		//Twice for test
		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CONCEPT, null);
		ctxRsn.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);
		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CATEGORY, null);
		ctxRsn.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);

		HashMap<Person, HashMap<Person, Double>> participantsMatrix = ctxRsn.getPersonMatrix(LongTermCtxTypes.INTERESTS);
		
		LOG.info(participantsMatrix.toString());
		Assert.assertNotNull(participantsMatrix);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#getPersonsWithMatchingLongTermCtx(java.lang.String, java.util.HashSet)}.
	 */
	@Test
	public void testGetPersonsWithMatchingLongTermCtx() {
		HashSet<Person> hashsetPersons = new HashSet<Person>();
		HashMap<String, HashSet<Person>>  ht1 = ctxRsn.getPersonsWithMatchingLongTermCtx(LongTermCtxTypes.INTERESTS, hashsetPersons);
		Assert.assertNotNull(ht1);
	}	

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#getPersonsWithMatchingShortTermCtx(org.societies.collabtools.runtime.Operators, java.lang.String, java.util.HashSet)}.
	 */
	@Test
	public void testGetPersonsWithMatchingShortTermCtx() {
		HashSet<Person> hashsetPersons = new HashSet<Person>();
		HashMap<String, HashSet<Person>>  ht1 = ctxRsn.getPersonsWithMatchingShortTermCtx(Operators.SIMILAR, LongTermCtxTypes.INTERESTS, hashsetPersons);
		Assert.assertNotNull(ht1);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#incrementCtx(java.lang.String, org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes, org.societies.collabtools.acquisition.Person)}.
	 */
	@Test
	public void testIncrementCtx() {
		for (int i = 0; i < 4; i++)
		{
			Person person;
			try {
				person = personRepository.createPerson("person#" + i);
				//Set long term context
				person.setLongTermCtx(LongTermCtxTypes.NAME, "person#" + i);
				String[] interests = {"music, cinema, Aquatic Invertebrates, Nanoparticles Toxicity, Aquatic Ecotoxicology, gym, yoga"};
				person.setLongTermCtx(LongTermCtxTypes.INTERESTS, interests);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CONCEPT, null);
		// context enrichment considering previous concept performed
//		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CATEGORY, null);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#incrementCtx(java.lang.String, org.societies.collabtools.api.IIncrementCtx.EnrichmentTypes, org.societies.collabtools.acquisition.Person)}.
	 */
	@Test
	public void testIncrementCtxNull() {
		for (int i = 0; i < 4; i++)
		{
			Person person;
			try {
				person = personRepository.createPerson("person#" + i);
				//Set long term context
				person.setLongTermCtx(LongTermCtxTypes.NAME, "person#" + i);
				String[] interests = {};
				person.setLongTermCtx(LongTermCtxTypes.INTERESTS, interests);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CONCEPT, null);
		// context enrichment considering previous concept performed
//		ctxRsn.incrementCtx(LongTermCtxTypes.INTERESTS, EnrichmentTypes.CATEGORY, null);
	}
	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#personCtxSimilarity(int, java.lang.String, org.societies.collabtools.acquisition.Person, org.societies.collabtools.acquisition.Person)}.
	 */
	@Test
	public void testPersonCtxSimilarity() {
		//		ctxRsn.personCtxSimilarity(0, LongTermCtxTypes.INTERESTS, personA, personB);
	}

	/**
	 * Test method for {@link org.societies.collabtools.interpretation.ContextAnalyzer#setupWeightAmongPeople(java.lang.String)}.
	 */
	@Test
	public void testSetupWeightAmongPeople() {
		ctxRsn.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);
	}	


}
