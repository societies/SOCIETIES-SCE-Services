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
import org.societies.android.remote.helper.ContextClientHelper;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;

/**
 * Describe your class here...
 *
 * @author Jiannis
 *
 */
public class CtxClientCallback implements ICtxClientCallback {

	private ContextClientHelper helper;
	private UserLocation userLocation;

	public CtxClientCallback(ContextClientHelper helper, UserLocation userLocation){
		this.helper = helper;
		this.userLocation = userLocation;
		
	}
	
	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#getException()
	 */
	@Override
	public CtxException getException() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onCreatedAssociation(org.societies.api.schema.context.model.CtxAssociationBean)
	 */
	@Override
	public void onCreatedAssociation(CtxAssociationBean arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onCreatedAttribute(org.societies.api.schema.context.model.CtxAttributeBean)
	 */
	@Override
	public void onCreatedAttribute(CtxAttributeBean arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onCreatedEntity(org.societies.api.schema.context.model.CtxEntityBean)
	 */
	@Override
	public void onCreatedEntity(CtxEntityBean arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onException(org.societies.android.api.context.CtxException)
	 */
	@Override
	public void onException(CtxException arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onLookupCallback(java.util.List)
	 */
	@Override
	public void onLookupCallback(List<CtxIdentifierBean> arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onRemovedModelObject(org.societies.api.schema.context.model.CtxModelObjectBean)
	 */
	@Override
	public void onRemovedModelObject(CtxModelObjectBean arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onRetrieveCtx(org.societies.api.schema.context.model.CtxModelObjectBean)
	 */
	@Override
	public void onRetrieveCtx(CtxModelObjectBean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {

		//retrEntId is used in Step2 in order to retrieve location
		this.userLocation.setRetrEntId(entityId);

	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.context.ICtxClientCallback#onUpdateCtx(org.societies.api.schema.context.model.CtxModelObjectBean)
	 */
	@Override
	public void onUpdateCtx(CtxModelObjectBean arg0) {
		// TODO Auto-generated method stub
		
	}

}
