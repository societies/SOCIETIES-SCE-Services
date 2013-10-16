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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.acquisition.ContextSubscriber;
import org.societies.collabtools.acquisition.LongTermCtxTypes;
import org.societies.collabtools.acquisition.Person;
import org.societies.collabtools.acquisition.PersonRepository;
import org.societies.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.collabtools.interpretation.ContextAnalyzer;
import org.societies.collabtools.runtime.CollabApps;
import org.societies.collabtools.runtime.SessionRepository;

/**
 * Unit tests for Context Subscriber
 *
 * @author Christopher Lima
 *
 */
public class CtxSubscriberTest {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(RuleEngineTest.class);
	
	private static final Random r = new Random( System.currentTimeMillis() );
	private static GraphDatabaseService personGraphDb, sessionGraphDb;
	private static Index<Node> indexPerson, indexSession;
	private static PersonRepository personRepository;
	private static SessionRepository sessionRepository;
	private static int nrOfPersons = 5;
    private static ContextSubscriber ctxSub;
	private static ContextAnalyzer ctxRsn;

	/**
	 * Test method for {@link org.societies.collabtools.acquisition.ContextSubscriber#ContextSubscriber(org.societies.collabtools.Activator, org.societies.collabtools.acquisition.PersonRepository, org.societies.collabtools.runtime.SessionRepository)}.
	 */
	@Test
	public void testContextSubscriber() {
		ctxSub = new ContextSubscriber(null, personRepository, sessionRepository);
		try {
			createPersons(nrOfPersons);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.societies.collabtools.acquisition.ContextSubscriber#initialCtx(java.lang.Object)}.
	 */
	@Ignore
	public void testInitialCtx() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.collabtools.acquisition.ContextSubscriber#stopCtx(java.lang.Object)}.
	 */
	@Ignore
	public void testStopCtx() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.collabtools.acquisition.ContextSubscriber#update(java.util.Observable, java.lang.Object)}.
	 * @throws Exception 
	 */
	@Test
	public void testUpdate() throws Exception {
		int i = nrOfPersons++;
        Person person = personRepository.createPerson( "person#" + i);
        //Set long term context
        person.setLongTermCtx(LongTermCtxTypes.NAME, "person#" + i);
        person.setLongTermCtx(LongTermCtxTypes.COLLAB_APPS, new String[] { "chat" });
        person.setLongTermCtx(LongTermCtxTypes.OCCUPATION, getRandomOccupation());
        person.setLongTermCtx(LongTermCtxTypes.INTERESTS, getRandomInterests());
        person.setLongTermCtx(LongTermCtxTypes.COMPANY, getRandomCompanies());
        System.out.println("Person#" +i+" created");
        
        //Set ShortTerm Ctx
		String [] response = new String [] {ShortTermCtxTypes.STATUS, getRandomStatus(), person.getName()};
		ctxSub.update(null, response);
		response = new String [] {ShortTermCtxTypes.LOCATION, getRandomLocation(), person.getName()};
		ctxSub.update(null, response);
		
		person.setLongTermCtx(LongTermCtxTypes.INTERESTS, new String[] {"AB","BC","CD","AE"});
	}

	/**
	 * Test method for {@link org.societies.collabtools.acquisition.ContextSubscriber#getSessions()}.
	 */
	@Ignore
	public void testGetSessions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.collabtools.acquisition.ContextSubscriber#getSessionLanguage(java.lang.String)}.
	 */
	@Ignore
	public void testGetSessionLanguage() {
		ctxSub.getSessionLanguage("sessionName");
	}

	/**
	 * Test method for {@link org.societies.collabtools.acquisition.ContextSubscriber#getMonitor()}.
	 */
	@Test
	public void testGetMonitor() {
		Assert.assertNotNull(ctxSub.getMonitor());
	}
	
	@BeforeClass
	public static void setup() throws Exception
	{
		int random = new Random().nextInt(100);
		personGraphDb = new GraphDatabaseFactory().newEmbeddedDatabase("target/persontestdb0"  + random);
		sessionGraphDb = new GraphDatabaseFactory().newEmbeddedDatabase("target/sessiontestdb0"  + random);
	    indexPerson = personGraphDb.index().forNodes("PersonNodes");
	    indexSession = sessionGraphDb.index().forNodes("SessionNodes");
		personRepository = new PersonRepository(personGraphDb, indexPerson);
		sessionRepository = new SessionRepository(sessionGraphDb,indexSession, new CollabApps());
        ctxSub = new ContextSubscriber(null,personRepository, sessionRepository);
        ctxRsn = new ContextAnalyzer(personRepository);
		LOG.info("personGraphDb path: "+"target/persontestdb0"  + random);
		LOG.info("sessionGraphDb path: "+"target/sessiontestdb0"  + random);
		LOG.info("Setup done...");
	}

//	@Before
//	public void doBefore() throws Exception
//	{
////		deleteSocialGraph();
//		createPersons(nrOfPersons);
////		enrichedCtx(LongTermCtxTypes.INTERESTS);
////		ctxRsn.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);
//	}

	@AfterClass
	public static void teardown()
	{
		personGraphDb.shutdown();
		sessionGraphDb.shutdown();
	}
	
	private static void createPersons(int nrOfPersons) throws Exception
	{
		for (int i = 0; i < nrOfPersons; i++)
		{
			Person person = personRepository.createPerson("person#" + i);
	        //Set long term context
	        person.setLongTermCtx(LongTermCtxTypes.NAME, "person#" + i);
	        person.setLongTermCtx(LongTermCtxTypes.COLLAB_APPS, new String[] { "chat" });
	        person.setLongTermCtx(LongTermCtxTypes.OCCUPATION, getRandomOccupation());
	        person.setLongTermCtx(LongTermCtxTypes.INTERESTS, getRandomInterests());
	        person.setLongTermCtx(LongTermCtxTypes.COMPANY, getRandomCompanies());
	        person.setLongTermCtx("age", "20");
	        
	        //Set short term context
			String [] response = new String [] {ShortTermCtxTypes.STATUS, getRandomStatus(), person.getName()};
			ctxSub.update(null, response);
			
			response = new String [] {ShortTermCtxTypes.LOCATION, getRandomLocation(), person.getName()};
			ctxSub.update(null, response);
		}
	}


	private void deleteSocialGraph()
	{
		for (Person person : personRepository.getAllPersons())
		{
			personRepository.deletePerson(person);
		}
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
	private static String getRandomStatus() {
		final String[] status={"Online","Busy","Away"};
		return status[r.nextInt(3)];
	}
	
	/**
	 * @return
	 */
	private static String getRandomCompanies() {
		final String[] companies={"PTIn","TI","Intel"};
		return companies[r.nextInt(3)];
	}
	
	/**
	 * @return
	 */
	private static String getRandomOccupation() {
		final String[] work={"Manager","Developer","Beta Tester"};
		return work[r.nextInt(3)];
	}	

}
