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
package org.societies.thirdpartyservices.city.sourceapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueMetrics;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

/**
 * Describe your class here...
 *
 * @author rdaviesX
 *
 */
public class SourceTestImpl implements ISourceTest {

	
	private ICtxBroker externalCtxBroker;
	private ICtxBroker internalCtxBroker;
	
	
	/** The 3P Context Broker service reference. */
	private ICtxBroker ctxBroker;
	
	// The IIdentity of the context data owner, i.e. the target CSS (or CIS)
	IIdentity targetId;
	
	

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private CtxIdentifier myCtxAttributeStringIdentifier;


	public void RunInit() {
		log.info("RICH: runInit starting v1");

		// create a context entity
		String ownerId = "cityWatch";
		String type = "Community Garden";
		Long objectNumber = (long) 1;
		CtxEntityIdentifier ceId = new CtxEntityIdentifier(ownerId, type, objectNumber);
		
		CtxEntity entity = new CtxEntity(ceId);
		
		
		
		
		
		
		
		
		
		
		
	}
	

	public void doSomething() {
		log.info("RICH: SourceTest doSomething.");
		
		//cxtBrokerService.
		
		String ownerId = "ownerId";
		String type = "type";
		Long objectNumber = (long) 1234;
		CtxEntityIdentifier ceId = new CtxEntityIdentifier(ownerId, type, objectNumber);
		
		CtxEntity entity = new CtxEntity(ceId);
		
	}

	public void SourceTestImpl() {
		log.info("RICH: SourceTest bundle instantiated.");
	}

	
	
	private void createUpdateContextModelObjects(IIdentity requestorId, IIdentity cssOwnerId, CtxModelObject associationUsesService, CtxEntity operator, CtxEntity deviceCtxEntity){
		 // The IIdentity of the requestor of context data
		Requestor requestor = new Requestor(requestorId);
		
		// retrieve the CtxEntityIdentifier of the CSS owner context entity  based on IIdentity
		CtxEntityIdentifier ownerEntityId;
		try {
			ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(requestor, targetId).get();
		
			// create a context attribute under the CSS owner context entity
			CtxAttribute aboutMeAttr = this.ctxBroker.createAttribute(requestor, ownerEntityId, CtxAttributeTypes.ABOUT).get();
			// assign a String value to the attribute 
			aboutMeAttr.setStringValue("a cool CSS user");
			aboutMeAttr.setValueType(CtxAttributeValueType.STRING);
			// update the attribute in the Context DB
			aboutMeAttr = (CtxAttribute) this.ctxBroker.update(requestor, aboutMeAttr).get();
			
			// create a context entity that represents a device
			CtxEntity deviceEntity = this.ctxBroker.createEntity(requestor, targetId, CtxEntityTypes.DEVICE).get();
			// get the context identifier of the created entity
			CtxEntityIdentifier deviceEntityId = deviceEntity.getId();
			
			// create an attribute to model the name of the device entity
			CtxAttribute deviceNameAttr = this.ctxBroker.createAttribute(requestor, deviceEntityId, CtxAttributeTypes.NAME).get();
			// assign a String value to the attribute 
			deviceNameAttr.setStringValue("device1234");
			deviceNameAttr.setValueType(CtxAttributeValueType.STRING);
			// update the attribute in the Context DB
			deviceNameAttr = (CtxAttribute) this.ctxBroker.update(requestor, deviceNameAttr).get();
					
			// create an attribute to model the temperature of the device
			CtxAttribute deviceTempAttr = this.externalCtxBroker.createAttribute(requestor, deviceCtxEntity.getId(), CtxAttributeTypes.TEMPERATURE).get();
			// assign a double value and set value type and metric
			deviceTempAttr.setDoubleValue(25.0);
			deviceTempAttr.setValueType(CtxAttributeValueType.DOUBLE);
			deviceTempAttr.setValueMetric(CtxAttributeValueMetrics.CELSIUS);
			// update the attribute in the Context DB
			deviceTempAttr = (CtxAttribute) this.externalCtxBroker.update(requestor, deviceTempAttr).get();
			
			// create an attribute with a Binary value
			CtxAttribute deviceBinAttr = this.ctxBroker.createAttribute(requestor, deviceEntityId, "serializableData").get();
			
			// this is a mock Serializable class
			MockBlobClass blob = new MockBlobClass();
			byte[] blobBytes = null;
			try {
				blobBytes = SerialisationHelper.serialise(blob);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deviceBinAttr.setBinaryValue(blobBytes);
			deviceBinAttr.setValueType(CtxAttributeValueType.BINARY);
			
			// update the attribute in the Context DB
			deviceBinAttr = (CtxAttribute) this.ctxBroker.update(requestor, deviceBinAttr).get();
			
			//create an Association
			CtxAssociation usesServiceAssoc = this.externalCtxBroker.createAssociation(requestor, cssOwnerId, CtxAssociationTypes.USES_SERVICES).get();
			//add child entities 
			usesServiceAssoc.addChildEntity(operator.getId());
			usesServiceAssoc.addChildEntity(deviceEntity.getId());
			//update entities
			usesServiceAssoc = (CtxAssociation) this.ctxBroker.update(requestor, associationUsesService).get();			
			
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
	

	
	private void retryLookUpContextInformation(Requestor requestor, IIdentity cssOwnerId, CtxIdentifier weightAttrIdentifier) {

		// if the CtxEntityID or CtxAttributeID is known the retrieval is
		// performed by using the ctxBroker.retrieve(CtxIdentifier) method
		// alternatively context identifiers can be retrieved with the help of
		// lookup mehtods
		CtxEntityIdentifier deviceCtxEntIdentifier = null;
		try {
			List<CtxIdentifier> idsEntities = this.externalCtxBroker.lookup(requestor, cssOwnerId, CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			if (idsEntities.size() > 0) {
				deviceCtxEntIdentifier = (CtxEntityIdentifier) idsEntities.get(0);
			}
			// the retrieved identifier is used in order to retrieve the context
			// model object (CtxEntity)
			CtxEntity retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, deviceCtxEntIdentifier).get();

			// Retrieve CtxAttributes assigned to retrievedCtxEntity
			Set<CtxAttribute> ctxAttrSet = retrievedCtxEntity.getAttributes(CtxAttributeTypes.ID);

			if (ctxAttrSet.size() > 0) {
				List<CtxAttribute> ctxAttrList = new ArrayList(ctxAttrSet);
				CtxAttribute ctxAttr = ctxAttrList.get(0);
			}
			// retrieve ctxAttribute with the binary value based on a known
			// identifier
			CtxAttribute ctxAttributeWeight = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, weightAttrIdentifier).get();

			// deserialise object
			try {
				MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(ctxAttributeWeight.getBinaryValue(), this.getClass().getClassLoader());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	 
	private void removeContextModelObjects(Requestor requestor, CtxIdentifier identifier){
		try {
			this.internalCtxBroker.remove(requestor, identifier);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void SubscribForAndReactToContextChangeEvents(Requestor requestor, CtxEntityIdentifier myCtxEntityIdentifier) {
		// The ICtxBroker interface provides methods for registering CtxChangeEventListeners in order to listen for context change events.
		// There are two ways to subscriber for context change event notification:

		try {
			// 1a. Register listener by specifying the context attribute identifier
			this.internalCtxBroker.registerForChanges(requestor, new MyCtxChangeEventListener(), myCtxAttributeStringIdentifier);

			// 1b. Register listener by specifying the context attribute scope and type
			this.internalCtxBroker.registerForChanges(requestor, new MyCtxChangeEventListener(), myCtxEntityIdentifier, "DeviceID");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private List<CtxHistoryAttribute> MaintainHistoryOfContextAttributes(CtxAttribute ctxAttributeString, Requestor requestor, CtxAttributeIdentifier ctxAttributeStringIdentifier, Date startDate, Date endDate){
		// by setting the history recording flag to true the CtxAttribute values will be stored to Context History Database upon update
		ctxAttributeString.setHistoryRecorded(true);
		
		// Retrieval of Context history data for a specified time period
		// if null values are used for starting and ending Date the whole set of history data is retrieved
		
		List<CtxHistoryAttribute> ctxHistoryData = null; 
		
		try {
			ctxHistoryData =  internalCtxBroker.retrieveHistory(requestor, (CtxAttributeIdentifier) ctxAttributeStringIdentifier, startDate, endDate).get();
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
		
		return ctxHistoryData;
	}

	
	
	
	/**
	 * @return the cxtBrokerService
	 */
	public ICtxBroker getCxtBrokerService() {
		return ctxBroker;
	}

	/**
	 * @param cxtBrokerService the cxtBrokerService to set
	 */
	public void setCxtBrokerService(ICtxBroker ictxBroker) {
		this.ctxBroker = ictxBroker;
	}
	
}
