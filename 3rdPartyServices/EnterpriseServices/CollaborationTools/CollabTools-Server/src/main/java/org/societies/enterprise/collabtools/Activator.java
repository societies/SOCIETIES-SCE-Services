package org.societies.enterprise.collabtools;

import java.util.ArrayList;
import java.util.Hashtable;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexProvider;
import org.neo4j.index.lucene.LuceneIndexProvider;
import org.neo4j.kernel.ListIndexIterable;
import org.neo4j.kernel.impl.cache.CacheProvider;
import org.neo4j.kernel.impl.cache.SoftCacheProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.runtime.ShortTermCtxMonitor;
import org.societies.enterprise.collabtools.runtime.SessionRepository;


public class Activator implements BundleActivator
{
	
	private static final Logger logger  = LoggerFactory.getLogger(Activator.class);
 
    private static GraphDatabaseService graphDb;
    private static Index<Node> indexPerson;
    private ServiceRegistration serviceRegistration;
    private ServiceRegistration indexServiceRegistration;
    
    private SessionRepository sessionRepository = new SessionRepository();
    private PersonRepository personRepository;
 
    @Override
    public void start( BundleContext context ) throws Exception
    {
        //the cache providers
        ArrayList<CacheProvider> cacheList = new ArrayList<CacheProvider>();
        cacheList.add( new SoftCacheProvider() );
 
        //the index providers
        IndexProvider lucene = new LuceneIndexProvider();
        ArrayList<IndexProvider> provs = new ArrayList<IndexProvider>();
        provs.add( lucene );
        ListIndexIterable providers = new ListIndexIterable();
        providers.setIndexProviders( provs );
 
        //the database setup
        logger.info("Database setup");
        GraphDatabaseFactory gdbf = new GraphDatabaseFactory();
        gdbf.setIndexProviders( providers );
        gdbf.setCacheProviders( cacheList );
        graphDb = gdbf.newEmbeddedDatabase( "target/PersonsGraphDb" );
        indexPerson = graphDb.index().forNodes( "PersonNodes" );
        personRepository = new PersonRepository( graphDb, indexPerson);
 
        //the OSGi registration
        serviceRegistration = context.registerService(GraphDatabaseService.class.getName(), graphDb, new Hashtable<String,String>() );
        System.out.println( "registered " + serviceRegistration.getReference() );
        
        indexServiceRegistration = context.registerService(
                Index.class.getName(), indexPerson,
                new Hashtable<String,String>() );
        
        //Setting up GraphDB
        TestUtils test = new TestUtils(personRepository, sessionRepository);
        test.createPersons(5);
        test.setupFriendsBetweenPeople();
        
        System.out.println("Starting Context Monitor..." );
        ShortTermCtxMonitor thread = new ShortTermCtxMonitor(personRepository, sessionRepository);
		thread.start();
     
 
    }
 
    @Override
    public void stop( BundleContext context ) throws Exception
    {
        serviceRegistration.unregister();
        indexServiceRegistration.unregister();
        graphDb.shutdown();
 
    }
 
}
