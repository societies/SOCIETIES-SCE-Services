package org.societies.thirdpartyservices.crowdtasking;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.android.remote.helper.TrustClientHelper;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;
import org.societies.thirdpartyservices.crowdtasking.helpers.MethodCallback;
import org.societies.thirdpartyservices.crowdtasking.helpers.TrustClientCallbackBase;

import si.setcce.societies.android.rest.RestTask;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

public class TrustTask extends
		AsyncTask<String, Void, Set<TrustRelationshipBean>> {
	private static final String TRUST_API_URL = "http://crowdtasking.appspot.com/rest/users/trust";
	private static final String TRUST_RELATIONSHIP_POST = "si.setcce.societies.android.rest.LOG_EVENT";
	private Context context;
	private Set<TrustRelationshipBean> trustRelationships = null;

	public TrustTask(Context context) {
		this.context = context;
	}

	@Override
	protected Set<TrustRelationshipBean> doInBackground(String... params) {
		final CountDownLatch latch = new CountDownLatch(1);
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId("user1.research.setcce.si");
		final TrustedEntityIdBean trustorId = new TrustedEntityIdBean();
		trustorId.setEntityId("user1.research.setcce.si");
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);

		final TrustClientHelper helper = new TrustClientHelper(context);
		helper.setUpService(new MethodCallback() {
			@Override
			public void returnAction(boolean resultFlag) {
				helper.retrieveTrustRelationships(requestor, trustorId,
						new TrustClientCallbackBase() {
							@Override
							public void onRetrievedTrustRelationships(
									Set<TrustRelationshipBean> _trustRelationships) {
								trustRelationships = _trustRelationships;
								helper.tearDownService(new MethodCallback() {
									@Override
									public void returnAction(
											boolean resultFlag) {
										latch.countDown();
									}
								});
							}
						});
			}
		});
		try {
			latch.await(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return trustRelationships;
	}

	@Override
	protected void onPostExecute(Set<TrustRelationshipBean> trustRelationships) {
		if (trustRelationships == null || trustRelationships.isEmpty()) {
			System.out.println("onPostExecute NO trust received");
			return;
		}
		System.out.println("onPostExecute trust received");
		JSONArray trustRelationshipsJSON = new JSONArray();
		for (final TrustRelationshipBean trustRelationship : trustRelationships) {
			JSONObject trustRelationshipJSON = new JSONObject();
			try {
				trustRelationshipJSON.put("entityId", trustRelationship
						.getTrusteeId().getEntityId());
				trustRelationshipJSON.put("entityType", trustRelationship
						.getTrusteeId().getEntityType());
				trustRelationshipJSON.put("valueType",
						trustRelationship.getTrustValueType());
				trustRelationshipJSON.put("trustValue",
						trustRelationship.getTrustValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			trustRelationshipsJSON.put(trustRelationshipJSON);
		}

		HttpPost eventRequest;
		try {
			eventRequest = new HttpPost(new URI(TRUST_API_URL));
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("trustRelationships",
					trustRelationshipsJSON.toString()));
			eventRequest.setEntity(new UrlEncodedFormEntity(parameters));
			RestTask task = new RestTask(context, TRUST_RELATIONSHIP_POST,
					CookieManager.getInstance().getCookie(
							"crowdtasking.appspot.com"));
			task.execute(eventRequest);
		} catch (URISyntaxException e) {
			Log.e("CT4A", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e("CT4A", e.getMessage());
		}
	}
}
