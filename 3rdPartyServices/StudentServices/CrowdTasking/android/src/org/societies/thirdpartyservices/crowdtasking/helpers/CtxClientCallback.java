package org.societies.thirdpartyservices.crowdtasking.helpers;

import java.util.List;

import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClientCallback;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;

public class CtxClientCallback implements ICtxClientCallback {

	@Override
	public CtxException getException() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreatedAssociation(CtxAssociationBean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreatedAttribute(CtxAttributeBean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreatedEntity(CtxEntityBean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onException(CtxException arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLookupCallback(List<CtxIdentifierBean> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemovedModelObject(CtxModelObjectBean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRetrieveCtx(CtxModelObjectBean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRetrievedEntityId(CtxEntityIdentifierBean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateCtx(CtxModelObjectBean arg0) {
		// TODO Auto-generated method stub
		
	}

}
