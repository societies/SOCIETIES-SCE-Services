package org.societies.thirdpartyservices.crowdtasking.helpers;

import java.util.Set;

import org.societies.android.api.privacytrust.trust.ITrustClientCallback;
import org.societies.android.api.privacytrust.trust.TrustException;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;

public class TrustClientCallbackBase implements ITrustClientCallback {

	@Override
	public void onAddedDirectTrustEvidence() {
		System.out.println("onAddedDirectTrustEvidence should not be called!");
	}

	@Override
	public void onException(TrustException arg0) {
		System.out.println("onException should not be called!");
	}

	@Override
	public void onRetrievedTrustRelationship(TrustRelationshipBean arg0) {
		System.out.println("onRetrievedTrustRelationship should not be called!");
	}

	@Override
	public void onRetrievedTrustRelationships(Set<TrustRelationshipBean> arg0) {
		System.out.println("onRetrievedTrustRelationships should not be called!");
	}

	@Override
	public void onRetrievedTrustValue(Double arg0) {
		System.out.println("onRetrievedTrustValue should not be called!");
	}

}
