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
package org.societies.collabtools;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.impl.lucene.LuceneIndex;
import org.neo4j.kernel.impl.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.acquisition.LongTermCtxTypes;
import org.societies.collabtools.acquisition.PersonRepository;
import org.societies.collabtools.api.AbstractCollabAppConnector;
import org.societies.collabtools.runtime.CollabApps;
import org.societies.collabtools.runtime.SessionRepository;

/**
 * 
 * Main class to test without OSGi bundle compliment
 * 
 * @author cviana
 *
 */
public class MainTest {

	private static final Logger logger  = LoggerFactory.getLogger(MainTest.class);

	private static GraphDatabaseService personGraphDb;
	private static GraphDatabaseService sessionGraphDb;
	private static PersonRepository personRepository;
	private static SessionRepository sessionRepository; 
	private static Index<Node> indexShortTermCtx;
	
	private static Properties prop = new Properties();
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		prop.load(new FileInputStream("src/main/resources/config.properties"));

	    FileUtils.deleteRecursively(new File(prop.getProperty("personspath")));
	    FileUtils.deleteRecursively(new File(prop.getProperty("sessionspath")));
		
		//Database setup
		logger.info("Database setup");
		GraphDatabaseFactory gdbf = new GraphDatabaseFactory();
	    personGraphDb = gdbf.newEmbeddedDatabase(prop.getProperty("personspath"));
	    sessionGraphDb = gdbf.newEmbeddedDatabase(prop.getProperty("sessionspath"));
	    indexShortTermCtx = personGraphDb.index().forNodes("CtxNodes", MapUtil.stringMap("to_lower_case", "true" ) );
	    
	    //Synchronous integration
	    AbstractCollabAppConnector chat = new ChatAppIntegrator(prop.getProperty("applications"), prop.getProperty("server"));
	    AbstractCollabAppConnector[] connectorsApp = {chat};
	    CollabApps collabApps = new CollabApps(connectorsApp);

	    personRepository = new PersonRepository(personGraphDb);
	    sessionRepository = new SessionRepository(sessionGraphDb, collabApps);
//		registerShutdownHook();

		//Caching last recently used for Location
		((LuceneIndex<Node>) indexShortTermCtx).setCacheCapacity("name", 3000);

		TestUtils test = new TestUtils(personRepository, sessionRepository);
		//Clean graph DB
		test.deleteSocialGraph();
		
//		test.menu();
		test.createPersons(11); //5 people by default
		
//		Creating some updates
		test.createMockLongTermCtx();
		Thread.sleep(5 * 1000);
		test.createMockShortTermCtx();
//		test.enrichedCtx(LongTermCtxTypes.INTERESTS);
//		test.setupWeightAmongPeople(LongTermCtxTypes.INTERESTS);

		
//		YouMightKnow ymn = new YouMightKnow(personRepository.getPersonByName("person#"+3), new String[] {"project planning"}, 5);
//		ymn.printMightKnow(ymn.findYouMightKnow(personRepository.getPersonByName("person#"+3)) , new String[] {"project planning"} );

		System.out.println("TestUtils completed");

//		ContextSubscriber ctxSub = new ContextSubscriber(null, personRepository, sessionRepository);

		
		//Creating more updates
		while (true) {
			// 5 sec
			Thread.sleep(5 * 1000);
			test.createMockShortTermCtx();
//			test.insertNewPerson();
		}
		

		//        logger.info("Shutting down graphDb" );
		//        registerShutdownHook();
	}
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static void registerShutdownHook() {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				personGraphDb.shutdown();
				sessionGraphDb.shutdown();
			}
		} );
	}

}
