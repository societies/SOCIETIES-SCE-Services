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

import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.ContextSubscriber;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.api.ICollabAppConnector;
import org.societies.enterprise.collabtools.api.ICollabApps;
import org.societies.enterprise.collabtools.api.IContextSubscriber;
import org.societies.enterprise.collabtools.api.IEngine;
import org.societies.enterprise.collabtools.runtime.CollabApps;
import org.societies.enterprise.collabtools.runtime.SessionRepository;


public class Activator implements BundleActivator
{

	private static final Logger logger  = LoggerFactory.getLogger(Activator.class);

	private GraphDatabaseService personGraphDb;
	private GraphDatabaseService sessionGraphDb;
	private SessionRepository sessionRepository;
	private PersonRepository personRepository;

	private Index<Node> indexPerson, indexSession, indexShortTermCtx;
	@SuppressWarnings("rawtypes")
	private ServiceRegistration ctxSubServiceRegistration, collabAppsRegistration, engineRegistration/*, serviceRegistration, indexServiceRegistration*/;

	ContextSubscriber ctxSub;
	CollabApps collabApps;
	ResourceBundle rs;

	@Override
	public void start(BundleContext context) throws Exception
	{
		//Configuration file: config.properties
		rs = ResourceBundle.getBundle("config"); 

		//comment this for persistence
		//	    FileUtils.deleteRecursively(new File("databases/PersonsGraphDb"));
		//	    FileUtils.deleteRecursively(new File("databases/SessionsGraphDb"));


		//Apps and server
		//load a properties file
		ICollabAppConnector chat = new ChatAppIntegrator(rs.getString("applications"), rs.getString("server"));
		ICollabAppConnector[] connectorsApp = {chat};
		this.collabApps = new CollabApps(connectorsApp);

		this.setup();

		this.ctxSub = new ContextSubscriber(this, personRepository, sessionRepository);

		//OSGi registration
		//        serviceRegistration = context.registerService(GraphDatabaseService.class.getName(), personGraphDb, new Hashtable<String,String>() );
		//        logger.info("registered " + serviceRegistration.getReference());

		//        sessionGraphDbRegistration = context.registerService(GraphDatabaseService.class.getName(), sessionGraphDb, new Hashtable<String,String>() );
		//        logger.info("registered " + sessionGraphDbRegistration.getReference());

		//        indexServiceRegistration = context.registerService(Index.class.getName(), indexPerson, new Hashtable<String,String>() );

		ctxSubServiceRegistration =context.registerService(IContextSubscriber.class.getName(), ctxSub, null);

		collabAppsRegistration =context.registerService(ICollabApps.class.getName(), collabApps, null);
		
		engineRegistration =context.registerService(IEngine.class.getName(), ctxSub.monitor.conditions, null);

	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		ctxSubServiceRegistration.unregister();
		collabAppsRegistration.unregister();
		engineRegistration.unregister();
		//    	serviceRegistration.unregister();
		//    	sessionGraphDbRegistration.unregister();
		//        indexServiceRegistration.unregister();
		personGraphDb.shutdown();
		sessionGraphDb.shutdown(); 
	}

	/**
	 * 
	 */
	public void setup() {
		//index providers
		IndexProvider lucene = new LuceneIndexProvider();
		ArrayList<IndexProvider> provs = new ArrayList<IndexProvider>();
		provs.add(lucene);
		ListIndexIterable providers = new ListIndexIterable();
		providers.setIndexProviders(provs);

		//cache providers
		ArrayList<CacheProvider> cacheList = new ArrayList<CacheProvider>();
		cacheList.add(new SoftCacheProvider());

		//database setup
		logger.info("Database setup...");
		GraphDatabaseFactory gdbf = new GraphDatabaseFactory();
		gdbf.setIndexProviders(providers);
		gdbf.setCacheProviders(cacheList);
		int random = new Random().nextInt(100);
		personGraphDb = gdbf.newEmbeddedDatabase(rs.getString("personspath") + random );
		sessionGraphDb = gdbf.newEmbeddedDatabase(rs.getString("sessionspath") + random);
		indexPerson = personGraphDb.index().forNodes("PersonNodes");
		indexSession = sessionGraphDb.index().forNodes("SessionNodes");
		indexShortTermCtx = personGraphDb.index().forNodes("CtxNodes");
		
		personRepository = new PersonRepository(personGraphDb, indexPerson);
		sessionRepository = new SessionRepository(sessionGraphDb, indexSession, collabApps);

		//Caching last recently used for Location
		((LuceneIndex<Node>) indexShortTermCtx).setCacheCapacity("name", 3000);
	}  

}
