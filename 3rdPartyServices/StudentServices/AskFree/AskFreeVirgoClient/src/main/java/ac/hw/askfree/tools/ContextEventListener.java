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
package ac.hw.askfree.tools;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

import ac.hw.askfree.AskFree;

/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class ContextEventListener implements CtxChangeEventListener{


	//private final DisplayPortalClient client;
	private final AskFree client;
	Logger LOG = LoggerFactory.getLogger(this.getClass());

	private ICtxBroker ctxBroker;

	//private HashMap<String,Socket> userIdentities;
	private HashMap<String,ClientHandler> userIdentities;

	private final Requestor requestor;


	public ContextEventListener(AskFree client, ICtxBroker ctxBroker, Requestor requestor){
		this.client = client;
		this.ctxBroker = ctxBroker;
		//this.userIdentities = new HashMap<String,Socket>();
		this.userIdentities = new HashMap<String,ClientHandler>();
		this.requestor = requestor;
	}

	/**
	 * This method is called when the AskFreeClient (Android) starts on a user CSS.
	 */
	//public void registerForLocationEvents(IIdentity userIdentity, Socket socket){
	public void registerForLocationEvents(IIdentity userIdentity, ClientHandler cHandler){
		this.LOG.debug("Registering for location events");

		String userId = userIdentity.getBareJid();

		if (this.userIdentities.containsKey(userId)){
			this.LOG.debug("User identity " + userId + "found");
			if(this.userIdentities.get(userId) != cHandler){
				this.LOG.debug("User identity " + userId + "has changed cHandler object");
				this.userIdentities.put(userId,cHandler);
				this.LOG.debug("User:" + userId+ "Updated");
			}else{
				this.LOG.debug("User identity " + userId + "is already registered");
			}
			return;
		}
		
		/*if (this.userIdentities.containsKey(userId)){
			this.LOG.debug("User identity " + userId + "is already registered");
			return;
		}*/
		
		try {
			Future<CtxEntityIdentifier> futureCtxEntityId = this.ctxBroker.retrieveIndividualEntityId(requestor, userIdentity);
			CtxEntityIdentifier ctxEntityId =futureCtxEntityId.get();

			this.ctxBroker.registerForChanges(requestor, this, ctxEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC);

			//Add user to map
			//this.userIdentities.put(userId,socket);
			this.userIdentities.put(userId,cHandler);
			this.LOG.debug("User:" + userId+ " Registered for symloc events");
		} catch (CtxException e) {
			this.LOG.debug("1 " + e.toString());
		} catch (InterruptedException e) {
			this.LOG.debug("2 " + e.toString());
		} catch (ExecutionException e) {
			this.LOG.debug("3 " + e.toString());
		}
	}



	/* (non-Javadoc)
	 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onCreation(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onModification(final CtxChangeEvent event) {
		this.LOG.debug("Received context event: "+event.getId().toUriString());

		new Thread(){
			public void run(){
				String userIdentity = event.getId().getOwnerId();
				LOG.debug("User Identity " + userIdentity);
				CtxIdentifier ctxIdentifier = event.getId();
				LOG.debug("ctxIdentifier " + ctxIdentifier);

				CtxAttribute ctxAttribute;
				try {
					ctxAttribute = (CtxAttribute) ctxBroker.retrieve(requestor,ctxIdentifier).get();
					if (ctxAttribute!=null){
						LOG.debug("futureAttribute " + ctxAttribute.getStringValue());


						LOG.debug("Received context event for "+ctxAttribute.getType()+" with value: "+ctxAttribute.getStringValue());

						//Socket userSocket = userIdentities.get(userIdentity);
						ClientHandler cHandler = userIdentities.get(userIdentity);

						//client.updateUserLocation(userIdentity, userSocket, ctxAttribute.getStringValue());
						client.updateUserLocation(userIdentity, cHandler, ctxAttribute.getStringValue());
					}
					else{
						LOG.debug("Context Attribute is null");
					}

				} catch (CtxException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				LOG.debug("thread of handleInternalEvent method finished executing");
			}
		}.start();



	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}



}
