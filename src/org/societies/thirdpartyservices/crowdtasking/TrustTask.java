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
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.privacytrust.trust.ITrustClientCallback;
import org.societies.android.api.privacytrust.trust.TrustException;
import org.societies.android.remote.helper.TrustClientHelper;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

import si.setcce.societies.android.rest.RestTask;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

public class TrustTask extends AsyncTask<String, Void, Set<TrustRelationshipBean>> {
	//private static final String TRUST_API_URL = "http://192.168.1.102:8888/rest/users/trust";
	private static final String TRUST_API_URL = "http://crowdtasking.appspot.com/rest/users/trust";
	private static final String TRUST_RELATIONSHIP_POST = "si.setcce.societies.android.rest.LOG_EVENT";
	private Context context;
	private Set<TrustRelationshipBean> trustRelationships = null;

	public TrustTask(Context context) {
		this.context = context;
	}

	@Override
	protected Set<TrustRelationshipBean> doInBackground(String... params) {
		System.out.println("doInBackground");
		final CountDownLatch latch = new CountDownLatch(1);
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId("whatever.setcce.si");
		final TrustedEntityIdBean trustorId = new TrustedEntityIdBean();
		trustorId.setEntityId("user1.research.setcce.si");
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		try {
			final TrustClientHelper helper = new TrustClientHelper(context);
			IMethodCallback methodCallBack = new IMethodCallback() {
				
				/*
				 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
				 */
				@Override
				public void returnException(String exception) {
					System.out.println("setUpService returned exception: " + exception);
				}
	
				/*
				 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
				 */
				@Override
				public void returnAction(String result) {
					System.out.println("setUpService returned action: " + result);
				}
				
				@Override
				public void returnAction(boolean resultFlag) {
					
					helper.retrieveTrustRelationships(requestor, trustorId, 
							new ITrustClientCallback() {
	
						/*
						 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
						 */
						@Override
						public void onAddedDirectTrustEvidence() {
							System.out.println("onAddedDirectTrustEvidence");
							// should not be called!
						}
	
						/*
						 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
						 */
						@Override
						public void onException(TrustException exception) {
							System.out.println("onException");
							exception.printStackTrace();
							// should not be called!
						}
	
						/*
						 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
						 */
						@Override
						public void onRetrievedTrustRelationship(
								TrustRelationshipBean trustRelationship) {
							System.out.println("onRetrievedTrustRelationship");
							// should not be called!
						}
	
						/*
						 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
						 */
						@Override
						public void onRetrievedTrustRelationships(
								Set<TrustRelationshipBean> _trustRelationships) {
							// success!
							trustRelationships = _trustRelationships;
							System.out.println("onRetrievedTrustRelationships success!");
							helper.tearDownService(new IMethodCallback() {
	
								/*
								 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
								 */
								@Override
								public void returnException(String exception) {
									System.out.println("returnException");
								}
	
								/*
								 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
								 */
								@Override
								public void returnAction(String result) {
									System.out.println("returnAction");
								}
	
								/*
								 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
								 */
								@Override
								public void returnAction(boolean resultFlag) {
									System.out.println("returnAction");
									latch.countDown();
								}
							});
						}
	
						@Override
						public void onRetrievedTrustValue(Double trustValue) {
							System.out.println("onRetrievedTrustValue");
							// should not be called!
						}
					});
				}
			};
			System.out.println("setUpService");
			try {
				helper.setUpService(methodCallBack);
			}
			catch (Error e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				System.out.println("waiting");
				latch.await(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		catch (Error e) {
			e.printStackTrace();
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
				trustRelationshipJSON.put("entityId", trustRelationship.getTrusteeId().getEntityId());
				trustRelationshipJSON.put("entityType", trustRelationship.getTrusteeId().getEntityType());
				trustRelationshipJSON.put("valueType", trustRelationship.getTrustValueType());
				trustRelationshipJSON.put("trustValue", trustRelationship.getTrustValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			trustRelationshipsJSON.put(trustRelationshipJSON);
		}
		
		HttpPost eventRequest;
		try {
			eventRequest = new HttpPost(new URI(TRUST_API_URL));
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("trustRelationships",trustRelationshipsJSON.toString()));
			eventRequest.setEntity(new UrlEncodedFormEntity(parameters));
			RestTask task = new RestTask(context, TRUST_RELATIONSHIP_POST, CookieManager.getInstance().getCookie("crowdtasking.appspot.com"));
			task.execute(eventRequest);
		} catch (URISyntaxException e) {
			Log.e("CT4A", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e("CT4A", e.getMessage());
		}
	}
}
