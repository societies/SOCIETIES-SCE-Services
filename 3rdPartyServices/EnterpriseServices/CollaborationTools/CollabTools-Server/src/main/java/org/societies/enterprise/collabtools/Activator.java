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
package org.societies.enterprise.collabtools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexProvider;
import org.neo4j.index.impl.lucene.LuceneIndex;
import org.neo4j.index.lucene.LuceneIndexProvider;
import org.neo4j.kernel.ListIndexIterable;
import org.neo4j.kernel.impl.cache.CacheProvider;
import org.neo4j.kernel.impl.cache.SoftCacheProvider;
import org.neo4j.kernel.impl.util.FileUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.ContextSubscriber;
import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.interpretation.ContextAnalyzer;
import org.societies.enterprise.collabtools.runtime.CollabApps;
import org.societies.enterprise.collabtools.runtime.CtxMonitor;
import org.societies.enterprise.collabtools.runtime.SessionRepository;


public class Activator implements BundleActivator
{
	
	private static final Logger logger  = LoggerFactory.getLogger(Activator.class);
 
	private static GraphDatabaseService personGraphDb;
	private static GraphDatabaseService sessionGraphDb;
	private static Index<Node> indexPerson, indexSession, indexShortTermCtx;
    private ServiceRegistration serviceRegistration, indexServiceRegistration, ctxSubServiceRegistration;
    
    private SessionRepository sessionRepository;
    private PersonRepository personRepository;
 
    @Override
    public void start( BundleContext context ) throws Exception
    {
        //cache providers
        ArrayList<CacheProvider> cacheList = new ArrayList<CacheProvider>();
        cacheList.add( new SoftCacheProvider() );
 
        //index providers
        IndexProvider lucene = new LuceneIndexProvider();
        ArrayList<IndexProvider> provs = new ArrayList<IndexProvider>();
        provs.add( lucene );
        ListIndexIterable providers = new ListIndexIterable();
        providers.setIndexProviders( provs );
 
        //comment this for persistence
	    FileUtils.deleteRecursively(new File("target/PersonsGraphDb"));
	    FileUtils.deleteRecursively(new File("target/SessionsGraphDb"));
        
        //database setup
        logger.info("Database setup");
        GraphDatabaseFactory gdbf = new GraphDatabaseFactory();
        gdbf.setIndexProviders( providers );
        gdbf.setCacheProviders( cacheList );
//        personGraphDb = gdbf.newEmbeddedDatabase("databases/PersonsGraphDb" + new Random().nextInt(100));
	    personGraphDb = gdbf.newEmbeddedDatabase("databases/PersonsGraphDb");
	    sessionGraphDb = gdbf.newEmbeddedDatabase("databases/SessionsGraphDb");
	    indexPerson = personGraphDb.index().forNodes("PersonNodes");
	    indexSession = sessionGraphDb.index().forNodes("SessionNodes");
	    indexShortTermCtx = personGraphDb.index().forNodes("CtxNodes");
        

	    HashMap<String, String> collabAppsConfig = new HashMap<String, String>();
	    collabAppsConfig.put("chat", "societies.local");
	    CollabApps collabApps = new CollabApps(collabAppsConfig);

        personRepository = new PersonRepository(personGraphDb, indexPerson);
        sessionRepository = new SessionRepository(personGraphDb, indexSession, collabApps);

		//Caching last recently used for Location
		((LuceneIndex<Node>) indexShortTermCtx).setCacheCapacity("name", 3000);

        ContextSubscriber ctxSub = new ContextSubscriber(personRepository, sessionRepository);
 
        //OSGi registration
        serviceRegistration = context.registerService(GraphDatabaseService.class.getName(), personGraphDb, new Hashtable<String,String>() );
        logger.info("registered " + serviceRegistration.getReference() );
        
        indexServiceRegistration = context.registerService(Index.class.getName(), indexPerson, new Hashtable<String,String>() );
        
        ctxSubServiceRegistration =context.registerService(ContextSubscriber.class.getName(), ctxSub, null);
        
        Object cisID = "cis-ad1536de-7d89-43f7-a14a-74e278ed36aa.societies.local";
        
		//Setting up initial context for GraphDB
        ctxSub.initialCtx(cisID);
        
        //Enrichment of ctx
        logger.info("Starting enrichment of context..." );
        ContextAnalyzer ctxRsn = new ContextAnalyzer(this.personRepository);
        ctxRsn.incrementInterests();
		
		//Applying weight between edges
		logger.info("Setup weight among participants..." );
        for (Person person : personRepository.getAllPersons()) {
            ctxRsn.setupWeightBetweenPeople(person, LongTermCtxTypes.INTERESTS);
        }
       
        
        //Registering for ctx changes
        ctxSub.registerForContextChanges(cisID);
        
        //Starting Context Monitor
        logger.info("Starting Context Monitor..." );
        CtxMonitor thread = new CtxMonitor(personRepository, sessionRepository);
		thread.start();    
 
    }
 
    @Override
    public void stop( BundleContext context ) throws Exception
    {
    	ctxSubServiceRegistration.unregister();
        serviceRegistration.unregister();
        indexServiceRegistration.unregister();
        personGraphDb.shutdown();
		sessionGraphDb.shutdown(); 
    }
 
}
