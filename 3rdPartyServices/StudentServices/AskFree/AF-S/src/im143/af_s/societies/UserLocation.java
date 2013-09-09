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
package im143.af_s.societies;

import java.util.List;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClientCallback;
import org.societies.android.api.context.model.CtxAttributeTypes;
import org.societies.android.remote.helper.ContextClientHelper;
import org.societies.android.api.context.ICtxClientHelper;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.identity.RequestorBean;

import android.content.Context;
import android.util.Log;

/**
 * @author Jiannis Retrieve user's current location from context
 */
public class UserLocation {

	private Context mContext;
	 ContextClientHelper helper;
	private CtxEntityIdentifierBean retrEntId;
	private CtxIdentifierBean locationIdentifier;

	public UserLocation(Context context) {
		mContext = context;
		this.helper = new ContextClientHelper(mContext);
	}

	// step 1: Retrieve the corresponding
	// individual context entity using the following method:
	public void retrieveIndividualEntityId(){
		this.helper = new ContextClientHelper(mContext);
		
		CallbackObj callbackObj = new CallbackObj(helper, this);
		
		helper.setUpService(callbackObj);
	}

	// Step 2: Then the 3P service will have to use the lookup method including
	// the necessary information:
/*	public void retrieveContext(){
		this.helper = new ContextClientHelper(mContext);
		helper.setUpService(new IMethodCallback() {

			@Override
			public void returnAction(String result) {
				// TODO Auto-generated method stub

			}

			@Override
			public void returnException(String exception) {
				// TODO Auto-generated method stub

			}

			@Override
			public void returnAction(boolean resultFlag) {
				try{
					helper.lookup(RequestorBean requestor, CtxEntityIdentifierBean retrEntId, 
							CtxModelTypeBean.ATTRIBUTE, CtxAttributeTypes.LOCATION_COORDINATES, new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onException(CtxException exception) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							//locationIdentifier is used in Step3 in order to retrieve location.
							locationIdentifier = lookupList.get(0);

							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									latch.countDown();

								}

								@Override
								public void returnAction(String result) {
									// TODO Auto-generated method stub

								}

								@Override
								public void returnException(String exception) {
									// TODO Auto-generated method stub

								}

							});


						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {										
							// TODO Auto-generated method stub

						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {									
							// TODO Auto-generated method stub

						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							// TODO Auto-generated method stub

						}
					});				
				}catch(CtxException e){
					e.printStackTrace();
				}
			}
		});
	}*/

	// Step 3: Finally it will retrieve the CtxModelObject
	// that includes the desired location using the CtxIdentifierBean from the
	// lookup method.
/*	public void retrieveCtxModelObject(){
		this.helper = new ContextClientHelper(mContext);
		helper.setUpService(new IMethodCallback() {

			@Override
			public void returnException(String exception) {
				// TODO Auto-generated method stub

			}

			@Override
			public void returnAction(String result) {
				// TODO Auto-generated method stub

			}

			@Override
			public void returnAction(boolean resultFlag) {
				try{
					helper.retrieve(RequestorBean requestor, CtxIdentifierBean locationIdentifier, 
							new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onException(CtxException exception) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							CtxAttributeBean retrAttr = (CtxAttributeBean) modelObject;
							String LOG_TAG = "onRetrieveCtx";
							Log.d(LOG_TAG, "location retrieved: " + retrAttr.getStringValue() +
									" from attrId: " + retrAttr.getId().getString());
							helper.tearDownService(new IMethodCallback(){

								@Override
								public void returnAction(boolean resultFlag) {
									// TODO Auto-generated method stub

								}

								@Override
								public void returnAction(String result) {
									// TODO Auto-generated method stub

								}

								@Override
								public void returnException(String exception) {
									// TODO Auto-generated method stub

								}

							});

						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							// TODO Auto-generated method stub

						}
					});
				}catch(CtxException e){
					e.printStackTrace();
				}

			}

		});
	}
*/
	/**
	 * @return the retrEntId
	 */
	public CtxEntityIdentifierBean getRetrEntId() {
		return retrEntId;
	}

	/**
	 * @param retrEntId the retrEntId to set
	 */
	public void setRetrEntId(CtxEntityIdentifierBean retrEntId) {
		this.retrEntId = retrEntId;
		//TODO: FIX!
		this.helper.tearDownService(null);
	}
}
