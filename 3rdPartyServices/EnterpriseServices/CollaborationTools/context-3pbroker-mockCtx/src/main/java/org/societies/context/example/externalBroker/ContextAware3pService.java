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

package org.societies.context.example.externalBroker;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.enterprise.collabtools.acquisition.ContextSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author NikosK
 *
 */
@Service
public class ContextAware3pService extends Observable implements IContextAware3pService  {


	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	//services
	private ICommManager commsMgr;
	private IIdentityManager idMgr;
	private ICtxBroker ctxBroker;
	//private IPrivacyPreferenceManager privPrefMgr;

	// identities
	private RequestorService requestorService;
	private IIdentity userIdentity;
	private IIdentity serviceIdentity;
	private ServiceResourceIdentifier myServiceID;
	
	BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
	ServiceReference ctxSubServiceReference= bundleContext.getServiceReference(ContextSubscriber.class.getName());
	ContextSubscriber ctxSub =(ContextSubscriber)bundleContext.getService(ctxSubServiceReference);

	@Autowired(required=true)
	public ContextAware3pService( ICtxBroker ctxBroker, ICommManager commsMgr){
		
		LOG.info("*** ContextAware3pService started");
		

		
		//Registering subscriber
		this.addObserver(ctxSub);

		//services
		this.ctxBroker = ctxBroker;
		this.commsMgr = commsMgr;
		this.idMgr = commsMgr.getIdManager();
		
		LOG.info("ctxBroker: "+this.ctxBroker);
		LOG.info("commsMgr : "+this.commsMgr );
		LOG.info("idMgr : "+this.idMgr );
	
		//identities
		this.userIdentity = this.idMgr.getThisNetworkNode();
		try {
			this.serviceIdentity = this.idMgr.fromJid("cviana@societies.org");
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myServiceID = new ServiceResourceIdentifier();
		myServiceID.setServiceInstanceIdentifier("css://cviana@societies.org/ContextAware3pService");
		try {
			myServiceID.setIdentifier(new URI("css://cviana@societies.org/ContextAware3pService"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestorService = new RequestorService(serviceIdentity, myServiceID);

		LOG.info("userIdentity : "+ userIdentity.getBareJid());
		LOG.info("requestor service : "+requestorService);
	}


	/**
	 * This method demonstrates how to register for context change events in the context database
	 */
	@Override
	public void registerForContextChanges(IIdentity cisID) {

		LOG.info("*** registerForContextChanges");
		
		try {
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(requestorService, cisID).get();
			LOG.info("communityEntityIdentifier retrieved: " +ctxCommunityEntityIdentifier.toString()+ " based on cisID: "+ cisID);
			CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(requestorService, ctxCommunityEntityIdentifier).get();

			Set<CtxEntityIdentifier> ctxMembersIDs = communityEntity.getMembers();
			Iterator<CtxEntityIdentifier> members = ctxMembersIDs.iterator();
			 
			while(members.hasNext()){
				CtxEntityIdentifier member = members.next();
				// 1b. Register listener by specifying the context attribute scope and type
				CtxEntity retrievedCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestorService, member).get();
				Set<CtxAttribute> location = retrievedCtxEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
				this.ctxBroker.registerForChanges(requestorService, new MyCtxChangeEventListener(), location.iterator().next().getId());
				
				Set<CtxAttribute> interests = retrievedCtxEntity.getAttributes(CtxAttributeTypes.INTERESTS);
				this.ctxBroker.registerForChanges(requestorService, new MyCtxChangeEventListener(), interests.iterator().next().getId());
				
				Set<CtxAttribute> status = retrievedCtxEntity.getAttributes(CtxAttributeTypes.STATUS);
				this.ctxBroker.registerForChanges(requestorService, new MyCtxChangeEventListener(), status.iterator().next().getId());
				
				Set<CtxAttribute> name = retrievedCtxEntity.getAttributes(CtxAttributeTypes.NAME);
				this.ctxBroker.registerForChanges(requestorService, new MyCtxChangeEventListener(), name.iterator().next().getId());
				
				Set<CtxAttribute> about = retrievedCtxEntity.getAttributes(CtxAttributeTypes.ABOUT);
				this.ctxBroker.registerForChanges(requestorService, new MyCtxChangeEventListener(), about.iterator().next().getId());
				
//				
//
//				// 2. Update attribute to see some event action
//				CtxAttribute ctxAttr = (CtxAttribute) this.ctxBroker.retrieve(requestorService, location.iterator().next().getId()).get();
//	
//				ctxAttr.setStringValue("newDeviceLocation");
//				ctxAttr = (CtxAttribute) this.ctxBroker.update(requestorService, ctxAttr).get();
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CtxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOG.info("*** registerForContextChanges success");
	}


	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** CREATED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** REMOVED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** UPDATED event ***");
			LOG.info(event.getId().getOwnerId()+": *** UPDATED event ***");
			setChanged();  
			notifyObservers();
		}
	}

	@Override
	public void retrieveLookupCommunityEntAttributes(IIdentity cisID){

		CtxEntityIdentifier ctxCommunityEntityIdentifier;
		try {
			ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(requestorService, cisID).get();
			LOG.info("communityEntityIdentifier retrieved: " +ctxCommunityEntityIdentifier.toString()+ " based on cisID: "+ cisID);
			
			CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(requestorService, ctxCommunityEntityIdentifier).get();
			Set<CtxEntityIdentifier> ctxMembersIDs = communityEntity.getMembers();
			 
			for(CtxEntityIdentifier member : ctxMembersIDs){
				CtxEntity retrievedCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestorService, member).get();
				LOG.info("retrievedCtxEntity: "+retrievedCtxEntity.getId());
				LOG.info("retrievedCtxEntity: "+retrievedCtxEntity.getAttributes().toString());
				Set<CtxAttribute> attrName = retrievedCtxEntity.getAttributes(CtxAttributeTypes.NAME);
				for(CtxAttribute name : attrName)
					LOG.info("Retrieved Name: "+name.getStringValue());
				Set<CtxAttribute> attrLocation = retrievedCtxEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
				for(CtxAttribute location : attrLocation)
					LOG.info("Location: "+location.getStringValue());
				Set<CtxAttribute> attrInterest = retrievedCtxEntity.getAttributes(CtxAttributeTypes.INTERESTS);
				for(CtxAttribute interest : attrInterest)
					LOG.info("Interests: "+interest.getStringValue());
			}
			
			List<CtxIdentifier> communityAttrIDList = this.ctxBroker.lookup(requestorService, ctxCommunityEntityIdentifier, CtxModelType.ATTRIBUTE, CtxAttributeTypes.INTERESTS).get();
			LOG.info("lookup results communityAttrIDList: "+ communityAttrIDList);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * @see org.societies.context.example.externalBroker.IContextAware3pService#getCtxSub()
	 */
	@Override
	public ContextSubscriber getCtxSub() {
		return ctxSub;
		
	}	

}