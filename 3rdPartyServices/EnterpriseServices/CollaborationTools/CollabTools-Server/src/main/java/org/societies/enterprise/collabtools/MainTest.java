/**
 * 
 */
package org.societies.enterprise.collabtools;

import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.index.impl.lucene.LuceneIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.acquisition.RelTypes;
import org.societies.enterprise.collabtools.interpretation.ContextAnalyzer;
import org.societies.enterprise.collabtools.runtime.CtxMonitor;
import org.societies.enterprise.collabtools.runtime.SessionRepository;

/**
 * @author cviana
 *
 */
public class MainTest {

	private static final Logger logger  = LoggerFactory.getLogger(MainTest.class);

	private static GraphDatabaseService graphDb;
	private static PersonRepository personRepository;
	private static SessionRepository sessionRepository = new SessionRepository(); 
	private static Index<Node> indexPerson;
	private static Index<Node> indexShortTermCtx;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		
		//Database setup
		logger.info("Database setup");
		GraphDatabaseFactory gdbf = new GraphDatabaseFactory();
		graphDb = gdbf.newEmbeddedDatabase( "target/PersonsGraphDb" );
		indexPerson = graphDb.index().forNodes( "PersonNodes" );
		indexShortTermCtx = graphDb.index().forNodes( "CtxNodes" );
		personRepository = new PersonRepository( graphDb, indexPerson);
		registerShutdownHook();

		//Caching last recently used for Location
		( (LuceneIndex<Node>) indexShortTermCtx ).setCacheCapacity( "name", 3000 );

		TestUtils test = new TestUtils(personRepository, sessionRepository);
		//Clean graph DB
		test.deleteSocialGraph();
		
//		test.menu();
		test.createPersons(5); //5 people by default
//		Creating some updates
		test.createMockLongTermCtx();
		test.createMockShortTermCtx();
		test.enrichedCtx();
		test.setupFriendsBetweenPeople();
//		for ( Person person : personRepository.getAllPersons() )
//    	{
//			Iterable<Relationship> knows = person.getUnderlyingNode().getRelationships(RelTypes.KNOWS);
//			ArrayList<Float> elements = new ArrayList<Float>(); 
//			while (knows.iterator().hasNext()) {
//				Relationship rel = knows.iterator().next();
//				System.out.println(rel.getProperty("weight"));
//				elements.add((Float) rel.getProperty("weight"));
//
//			}
//			System.out.println("automaticThresholding: "+ContextAnalyzer.automaticThresholding(elements));
//    	}
		
//		YouMightKnow ymn = new YouMightKnow(personRepository.getPersonByName("person#"+3), new String[] {"project planning"}, 5);
//		ymn.printMightKnow(ymn.findYouMightKnow(personRepository.getPersonByName("person#"+3)) , new String[] {"project planning"} );

		System.out.println("TestUtils completed" );

		//Find people to create dynamic relationship

		System.out.println("Starting Context Monitor..." );
		CtxMonitor thread = new CtxMonitor(personRepository, sessionRepository);
		thread.start();

//		Thread.sleep(7 * 1000);
//		//Creating more updates
//		test.createMockShortTermCtx();
//		Thread.sleep(3 * 1000);

		//        logger.info("Shutting down graphDb" );
		//        registerShutdownHook();
	}
	/**
	 * 
	 */
	private static void registerShutdownHook() {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}

}
