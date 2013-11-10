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
package org.societies.collabtools.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class ExternalCtxBrokerConnector extends Observable {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ExternalCtxBrokerConnector.class);


	/** The 3P Context Broker service reference. */
	private ICtxBroker ctxBroker;
	private IContextAware3pService ca3pService;

	@Autowired(required=true)
	public ExternalCtxBrokerConnector(ICtxBroker externalCtxBroker, ICisManager cisManager, IContextAware3pService ca3pService) throws Exception {

		LOG.info("*** ca3pService : {} instantiated", ca3pService);

		this.ctxBroker = externalCtxBroker;
		this.ca3pService = ca3pService;
		this.ca3pService.setListener(new MyCtxChangeEventListener());
	}

	/**
	 * @return retrieveLookupMembersCtxAttributes
	 * @throws InvalidFormatException 
	 */
	public HashMap<String,HashMap<String,String[]>> retrieveLookupMembersCtxAttributes(Object cisID) {
		try {
			return this.ca3pService.retrieveLookupMembersCtxAttributes(cisID);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		//TODO: FIX THIS!
		return null;
	}

	/**
	 * @return retrieveLookupMembersCtxAttributes
	 * @throws InvalidFormatException 
	 */
	public HashMap<String, String> retrieveCommunityCtxAttributes(Object cisID) {
		try {
			//TODO: Can retrieve any ctx, for now only language
			return this.ca3pService.retrieveCommunityCtxAttributes(cisID, CtxAttributeTypes.LANGUAGES);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param String cisID
	 */
	public void registerForContextChanges(Object cisID) {
		//registerForContextChanges()
		try {
			this.ca3pService.registerForContextChanges(cisID);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param String cisID
	 */
	public void unregisterContextChanges(Object cisID) {
		//registerForContextChanges()
		try {
			this.ca3pService.unregisterContextChanges(cisID);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}


	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		private Set<String> members = new CopyOnWriteArraySet<String>();

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
			LOG.info("event.getId is {}", event.getId().getModelType().toString());
			Set<String> newMember = null;
			if (event.getId().getModelType().toString().contentEquals("ASSOCIATION")) {
				try {
					CtxAssociation hasMembersAssoc = (CtxAssociation) ctxBroker.retrieve(ca3pService.getRequestor(), event.getId()).get();
					LOG.info("hasMembersAssoc {}", hasMembersAssoc.toString());
					Set<String> currentMembers = new HashSet<String>();
					for (CtxEntityIdentifier cisCtxId : hasMembersAssoc.getChildEntities())
						currentMembers.add(cisCtxId.getOwnerId());
					LOG.info("Current members {}", currentMembers);

					// find old members
					Set<String> oldMembers = new HashSet<String>(this.members);
					oldMembers.removeAll(currentMembers);
					LOG.info("Old members {}", oldMembers);            

					// find new member
					newMember = new HashSet<String>(currentMembers);
					newMember.removeAll(this.members);
					LOG.info("New member {}", newMember);   

					// update members
					this.members.clear();
					this.members.addAll(currentMembers);
					LOG.info("Members {}", this.members);   

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
				
				//Retrieving context from the new member
				List<String> attrTypes = new ArrayList<String>(); 

				attrTypes.add(CtxAttributeTypes.OCCUPATION); 
				attrTypes.add(CtxAttributeTypes.WORK_POSITION); 
				attrTypes.add(CtxAttributeTypes.LOCATION_SYMBOLIC); 
				attrTypes.add(CtxAttributeTypes.STATUS); 
				attrTypes.add(CtxAttributeTypes.INTERESTS); 
				attrTypes.add(CtxAttributeTypes.ID);
								
				final List<CtxIdentifier> attrIdList = new ArrayList<CtxIdentifier>(); 
				for (final String attrType : attrTypes) { 
					List<CtxIdentifier> attrIds;
					try {
						IIdentity newUserID = ca3pService.getIdMgr().fromJid(newMember.toString());
						LOG.info("newUserID: {}", newUserID);
						attrIds = ctxBroker.lookup(ca3pService.getRequestor(), newUserID, CtxModelType.ATTRIBUTE, attrType).get();
						attrIdList.addAll(attrIds); 
						final List<CtxModelObject> ctxModelObjs = ctxBroker.retrieve(ca3pService.getRequestor(), attrIdList).get();
						for (final CtxModelObject modelObj : ctxModelObjs) { 
							final CtxAttribute attr = (CtxAttribute) modelObj; 
							if (attr != null) {
								String [] response;
								response = new String [] {attr.getId().getType(), attr.getStringValue(), newMember.toString()};
								LOG.info("newUser");
								LOG.info("Ctx value type: "+response[0]);
								LOG.info("Ctx new value: "+response[1]);
								LOG.info("Ctx person: "+response[2]);

								// Notify observers of change
								setChanged();
								notifyObservers(response);
							} 
						} 		
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (CtxException e) {
						e.printStackTrace();
					} catch (InvalidFormatException e) {
						e.printStackTrace();
					} 

				} 		
			}
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
			LOG.info("event.getId is {}", event.getId().getModelType().toString());
			//Different from association
			if (event.getId().getModelType().toString().contentEquals("ATTRIBUTE")) {
				CtxAttribute ctxAttr = null;
				String person = null;
				try {

					ctxAttr = (CtxAttribute) ctxBroker.retrieve(ca3pService.getRequestor(), event.getId()).get();
					LOG.debug("ctxValue getScope: "+ctxAttr.getScope());
					LOG.debug("ctxValue getOwnerId: "+ctxAttr.getOwnerId());

					String getOnlyNameSubstring[] = ctxAttr.getOwnerId().split("\\.");
					person = getOnlyNameSubstring[0];


				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (CtxException e) {
					e.printStackTrace();
				}
				String [] response = new String [] {ctxAttr.getType(), ctxAttr.getStringValue(), person};
				LOG.info("Ctx value type: "+response[0]);
				LOG.info("Ctx new value: "+response[1]);
				LOG.info("Ctx person: "+response[2]);


				// Notify observers of change
				setChanged();
				notifyObservers(response);
			}			
		}
	}

}