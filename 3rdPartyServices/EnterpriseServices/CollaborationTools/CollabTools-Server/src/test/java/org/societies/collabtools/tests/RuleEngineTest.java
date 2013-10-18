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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.societies.collabtools.runtime.Engine;
import org.societies.collabtools.runtime.Operators;
import org.societies.collabtools.runtime.Rule;
import org.societies.collabtools.runtime.SessionRepository;

/**
 * Unit tests for Rule engine
 *
 * @author Christopher Viana Lima
 *
 */
public class RuleEngineTest {
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(RuleEngineTest.class);
	
	private static final Random r = new Random( System.currentTimeMillis() );
	private static GraphDatabaseService personGraphDb, sessionGraphDb;
	private static Index<Node> indexPerson, indexSession, indexShortTermCtx;
	private static PersonRepository personRepository;
	private static SessionRepository sessionRepository;
	private static int nrOfPersons = 5;
	
    private static ContextSubscriber ctxSub;
	private static ContextAnalyzer ctxRsn;
	
	private Engine engine = new Engine(personRepository, sessionRepository);
	
	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#insertRule(org.societies.collabtools.runtime.Rule)}.
	 */
	@Test
	public void testInsertRule() {
		Rule r01 = new Rule("r01",Operators.SIMILAR, LongTermCtxTypes.INTERESTS, "--", 1, 0.5 ,LongTermCtxTypes.class.getSimpleName());
		engine.insertRule(r01);
		
		List<Rule> rules = engine.getRules();
		
		Assert.assertEquals(r01, rules.get(0));
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#deleteRule(org.societies.collabtools.runtime.Rule)}.
	 */
	@Test
	public void testDeleteRule() {
		Rule r01 = new Rule("r01",Operators.SAME, ShortTermCtxTypes.LOCATION, "--", 1, 0.5 ,ShortTermCtxTypes.class.getSimpleName());
		Rule r02 = new Rule("r02",Operators.SAME, LongTermCtxTypes.WORK_POSITION, "--", 2, 0.1 ,LongTermCtxTypes.class.getSimpleName());
		Rule r03 = new Rule("r03",Operators.SIMILAR, LongTermCtxTypes.INTERESTS, "--", 3, 0.4 ,LongTermCtxTypes.class.getSimpleName());
		Rule r04 = new Rule("r04",Operators.SAME, ShortTermCtxTypes.STATUS, "--", 4, 0.1 ,ShortTermCtxTypes.class.getSimpleName());
		List<Rule> rules = Arrays.asList(r01, r02, r03, r04);
		engine.setRules(rules);
		
		Assert.assertEquals(4, rules.size());
		engine.deleteRule(r03);
		rules = engine.getRules();
		
		Assert.assertEquals(3, rules.size());
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#setRules(java.util.Collection)}.
	 */
	@Test
	public void testSetRules() {
		Rule r01 = new Rule("r01",Operators.SAME, ShortTermCtxTypes.LOCATION, "--", 1, 0.5 ,ShortTermCtxTypes.class.getSimpleName());
		Rule r02 = new Rule("r02",Operators.SAME, LongTermCtxTypes.WORK_POSITION, "--", 2, 0.1 ,LongTermCtxTypes.class.getSimpleName());
		Rule r03 = new Rule("r03",Operators.SIMILAR, LongTermCtxTypes.INTERESTS, "--", 3, 0.4 ,LongTermCtxTypes.class.getSimpleName());
		Rule r04 = new Rule("r04",Operators.SAME, ShortTermCtxTypes.STATUS, "--", 4, 0.1 ,ShortTermCtxTypes.class.getSimpleName());
		List<Rule> rules = Arrays.asList(r01, r02, r03, r04);
		engine.setRules(rules);
		
		rules = engine.getRules();

		Assert.assertEquals(4, rules.size());
	}
	
	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#setRules(java.util.Collection)}.
	 */
	@Test
	public void testSetRulesWithSimilarOperator() {
		Rule r01 = new Rule("r01",Operators.SIMILAR, LongTermCtxTypes.INTERESTS, "--", 3, 0.4 ,LongTermCtxTypes.class.getSimpleName());
		Rule r02 = new Rule("r02",Operators.SIMILAR, LongTermCtxTypes.OCCUPATION, "--", 4, 0.1 ,LongTermCtxTypes.class.getSimpleName());
		List<Rule> rules = Arrays.asList(r01, r02);
		engine.setRules(rules);
		
		rules = engine.getRules();

		Assert.assertEquals(2, rules.size());
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#getRules()}.
	 */
	@Test
	public void testGetRules() {

		List<Rule> rules = engine.getRules();
		Assert.assertEquals(0, rules.size());
		
		Rule r01 = new Rule("r01",Operators.SAME, ShortTermCtxTypes.LOCATION, "--", 1, 0.5 ,ShortTermCtxTypes.class.getSimpleName());
		Rule r02 = new Rule("r02",Operators.SAME, LongTermCtxTypes.WORK_POSITION, "--", 2, 0.1 ,LongTermCtxTypes.class.getSimpleName());

		rules = Arrays.asList(r01, r02);
		engine.setRules(rules);
		
		rules = engine.getRules();

		Assert.assertEquals(2, rules.size());
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#getMatchingResultsByPriority()}.
	 */
	@Test
	public void testGetMatchingResultsByPriority() {
		Rule r01 = new Rule("r01",Operators.SAME, ShortTermCtxTypes.LOCATION, "--", 1, 0.5 ,ShortTermCtxTypes.class.getSimpleName());
//		Rule r02 = new Rule("r02",Operators.EQUAL, LongTermCtxTypes.COMPANY, "Intel", 2, 0.1 ,LongTermCtxTypes.class.getSimpleName());
		Rule r03 = new Rule("r03",Operators.SIMILAR, LongTermCtxTypes.INTERESTS, "--", 3, 0.4 ,LongTermCtxTypes.class.getSimpleName());
//		Rule r04 = new Rule("r04",Operators.NOT_EQUAL, ShortTermCtxTypes.STATUS, "busy", 4, 0.1 ,ShortTermCtxTypes.class.getSimpleName());
//		Rule r05 = new Rule("r05",Operators.DIFFERENT, LongTermCtxTypes.OCCUPATION, "manager", 4, 0.1 ,ShortTermCtxTypes.class.getSimpleName());
//		Rule r06 = new Rule("r06",Operators.GREATER_OR_EQUAL, "age", "20", 4, 0.1 , LongTermCtxTypes.class.getSimpleName());
//		Rule r07 = new Rule("r07",Operators.LESS, "age", "20", 4, 0.1 ,LongTermCtxTypes.class.getSimpleName());
//		Rule r08 = new Rule("r08",Operators.LESS_OR_EQUAL, "age", "20", 4, 0.1 ,LongTermCtxTypes.class.getSimpleName());
//		List<Rule> rules = Arrays.asList(r01, r02, r03, r04, r05, r06, r07, r08);
		List<Rule> rules = Arrays.asList(r01, r03);
		engine.setRules(rules);
		Hashtable<String, HashSet<Person>> matchingRules = engine.getMatchingResultsByPriority();
		LOG.info(matchingRules.toString());
		Assert.assertNotNull(matchingRules);
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#evaluateRule(org.societies.collabtools.runtime.Operators, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testEvaluateRule() {
		Rule r01 = new Rule("r01",Operators.SAME, ShortTermCtxTypes.LOCATION, "--", 1, 0.5 ,ShortTermCtxTypes.class.getSimpleName());
		Rule r02 = new Rule("r02",Operators.EQUAL, LongTermCtxTypes.WORK_POSITION, "Manager", 2, 0.1 ,LongTermCtxTypes.class.getSimpleName());
		Rule r03 = new Rule("r03",Operators.SIMILAR, LongTermCtxTypes.INTERESTS, "--", 3, 0.4 ,LongTermCtxTypes.class.getSimpleName());
		Rule r04 = new Rule("r04",Operators.NOT_EQUAL, ShortTermCtxTypes.STATUS, "busy", 4, 0.1 ,ShortTermCtxTypes.class.getSimpleName());
		Rule r05 = new Rule("r05",Operators.DIFFERENT, LongTermCtxTypes.OCCUPATION, "manager", 4, 0.1 ,ShortTermCtxTypes.class.getSimpleName());
		Rule r06 = new Rule("r06",Operators.GREATER_OR_EQUAL, "age", "20", 4, 0.1 , LongTermCtxTypes.class.getSimpleName());
		Rule r07 = new Rule("r07",Operators.LESS, "age", "20", 4, 0.1 ,LongTermCtxTypes.class.getSimpleName());
		Rule r08 = new Rule("r08",Operators.LESS_OR_EQUAL, "age", "20", 4, 0.1 ,LongTermCtxTypes.class.getSimpleName());
		List<Rule> rules = Arrays.asList(r01, r02, r03, r04, r05, r06, r07, r08);
		engine.setRules(rules);
		Hashtable<String, HashSet<Person>> matchingRules = new Hashtable<String, HashSet<Person>>();
		for(Rule r : rules){
			// each result is a group of people!!
			Hashtable<String, HashSet<Person>> results = engine.evaluateRule(r.getOperator(), r.getCtxAttribute(), r.getValue(), r.getCtxType(), null);
			matchingRules.putAll(results);
		}
		LOG.info(matchingRules.toString());
		Assert.assertNotNull(matchingRules);
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#getPersonsWithMatchingShortTermCtx(java.lang.String, java.util.HashSet)}.
	 */
	@Test
	public void testGetPersonsWithMatchingShortTermCtx() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#getPersonsWithMatchingLongTermCtx(java.lang.String, java.util.HashSet)}.
	 */
	@Test
	public void testGetPersonsWithMatchingLongTermCtx() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#getAllPersonsWithSameCtx(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetAllWithSameCtx() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.collabtools.runtime.Engine#getPersonsBySimilarity(java.lang.String, java.util.HashSet)}.
	 */
	@Test
	public void testGetPersonsByWeight() {
		fail("Not yet implemented");
	}

	@BeforeClass
	public static void setup() throws Exception
	{
		int random = new Random().nextInt(100);
		personGraphDb = new GraphDatabaseFactory().newEmbeddedDatabase("target/persontestdb00"  + random);
		sessionGraphDb = new GraphDatabaseFactory().newEmbeddedDatabase("target/sessiontestdb00"  + random);
	    indexPerson = personGraphDb.index().forNodes("PersonNodes");
	    indexSession = sessionGraphDb.index().forNodes("SessionNodes");
	    indexShortTermCtx = personGraphDb.index().forNodes("CtxNodes");
		personRepository = new PersonRepository(personGraphDb, indexPerson);
		sessionRepository = new SessionRepository(sessionGraphDb,indexSession, new CollabApps());
        ctxSub = new ContextSubscriber(null,personRepository, sessionRepository);
        ctxRsn = new ContextAnalyzer(personRepository);
		LOG.info("Setup done...");
	}

	@Before
	public void doBefore() throws Exception
	{
		deleteSocialGraph();
		createPersons(nrOfPersons);
//		enrichedCtx(LongTermCtxTypes.INTERESTS);
//		ctxRsn.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);
	}

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
	        person.setLongTermCtx(LongTermCtxTypes.WORK_POSITION, getRandomWorkPosition());
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
	private static String getRandomWorkPosition() {
		final String[] workPosition={"Manager","Marketing","Programmer"};
		return workPosition[r.nextInt(3)];
	}
	
	/**
	 * @return
	 */
	private static String getRandomOccupation() {
		final String[] work={"Manager","Developer","Beta Tester"};
		return work[r.nextInt(3)];
	}	

}
