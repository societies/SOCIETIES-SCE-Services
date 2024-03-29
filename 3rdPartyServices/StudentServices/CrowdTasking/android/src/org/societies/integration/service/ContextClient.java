package org.societies.integration.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.CookieManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.context.model.CtxAttributeTypes;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.thirdpartyservices.crowdtasking.CrowdTasking;
import org.societies.thirdpartyservices.crowdtasking.MainActivity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import si.setcce.societies.android.rest.RestTask;

public class ContextClient extends ServiceClientBase {
	private final static String LOG_TAG = "ContextClient";
	CtxAttributeBean locationBean;
	public static final String CHECK_IN_URL = "URL";

	public ContextClient(Context context) {
		super(context);
        serviceName = "ContextClient";
	}

	@Override
	protected Intent getServiceIntent() {
		return new Intent(ICoreSocietiesServices.CONTEXT_SERVICE_INTENT);
	}
	
    @Override
	protected BroadcastReceiver getBroadcastReceiver() {
		return new ClientReceiver();
	}

    @Override
    protected IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ICtxClient.CREATE_ASSOCIATION);
        intentFilter.addAction(ICtxClient.CREATE_ATTRIBUTE);
        intentFilter.addAction(ICtxClient.CREATE_ENTITY);
        intentFilter.addAction(ICtxClient.LOOKUP);
        intentFilter.addAction(ICtxClient.RETRIEVE);
        intentFilter.addAction(ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID);
        intentFilter.addAction(ICtxClient.UPDATE);
        
        return intentFilter;
    }

	private class ClientReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context contextX, Intent intent) {
			Log.i(LOG_TAG, "Received action: " + intent.getAction());
			if (intent.getAction().equals(ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID)) {
				final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
				if (exceptionMessage != null) {
					Log.e(LOG_TAG, exceptionMessage);
					return;
				}
				final CtxEntityIdentifierBean retrievedEntityId;
				final Parcelable pRetrievedEntityId = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				if (pRetrievedEntityId instanceof CtxEntityIdentifierBean)  {
					retrievedEntityId = (CtxEntityIdentifierBean) pRetrievedEntityId;
			        Log.i(LOG_TAG, "on RetrieveEntityId, entityId is: " + retrievedEntityId.getString());
				} else {
					Log.e(LOG_TAG, "Unexpected return value type: "+ ((pRetrievedEntityId != null) ? pRetrievedEntityId.getClass() : "null"));
					return;
				}
				try {
					lookup(requestor, retrievedEntityId, CtxModelTypeBean.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (intent.getAction().equals(ICtxClient.LOOKUP)) {
				final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
				if (exceptionMessage != null) {
					Log.e(LOG_TAG, exceptionMessage);
					return;
				}
				final List<CtxIdentifierBean> ctxIdsList = new ArrayList<CtxIdentifierBean>();
				final Parcelable[] pCtxIdsList = intent.getParcelableArrayExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				for (final Parcelable pCtxIdList : pCtxIdsList) {
					if (pCtxIdList instanceof CtxIdentifierBean) {
						ctxIdsList.add((CtxIdentifierBean) pCtxIdList);
					} else { 
						Log.e(LOG_TAG, "Unexpected return value type: "+ ((pCtxIdList != null) ? pCtxIdList.getClass() : "null"));
						return;
					}
				}
				//Step 3 to retrieve location 
				//retrieve the ctxModelObject that includes the desired location using the ctxIdentifier from step2
				retrieve(requestor, ctxIdsList.get(0));
			}
			else if (intent.getAction().equals(ICtxClient.RETRIEVE)) {
				final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
				if (exceptionMessage != null) {
					Log.e(LOG_TAG, exceptionMessage);
					return;
				}
				final CtxModelObjectBean modelObject;
				final Parcelable pModelObject = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);	
				if (pModelObject instanceof CtxModelObjectBean) {
					modelObject = (CtxModelObjectBean) pModelObject;
					locationBean = (CtxAttributeBean) modelObject;
                    String location = ((CtxAttributeBean) modelObject).getStringValue();
					if (location == null) {
						location = "";
					}
//					Toast.makeText(context, "User location: " + location, Toast.LENGTH_LONG).show();
					String oldLocation = ((CrowdTasking)context).symbolicLocation;
/*
                    if ("".equalsIgnoreCase(oldLocation)) {
                        ((CrowdTasking) context).symbolicLocation = location;
                        oldLocation = location;
                    }
*/
                    // trenutno je sendLocationToServer pri vsakem branju lokacije,
                    // dokler ne bo delal locatioon modified event
                    sendLocationToServer(location);
                    Intent bringToFront = new Intent(context, MainActivity.class);
                    bringToFront.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(bringToFront);
/*
                    if (!oldLocation.equalsIgnoreCase(location)) {
                        Toast.makeText(context, "Location: " + location, Toast.LENGTH_LONG).show();
                        ((CrowdTasking) context).symbolicLocation = location;
                        // TODO: dokončaj
                        sendLocationToServer(context, location);
                    } else {
//                        Toast.makeText(context, "Location is still the same", Toast.LENGTH_LONG).show();
                    }
*/
                    System.out.println("location:"+location);
				} else { 
					Log.e(LOG_TAG, "Unexpected return value type: "+ ((pModelObject != null) ? pModelObject.getClass() : "null"));
					return;
				}
				//((TextView)((Activity)context).findViewById(R.id.location)).setText(((CtxAttributeBean) modelObject).getStringValue());
			}
		}
    }

    private void sendLocationToServer(String location) {
        String DOMAIN = MainActivity.DOMAIN;
        String url = MainActivity.SET_LOCATION_URL;
        HttpPost setLocationRequest;
        try {
            setLocationRequest = new HttpPost(new URI(url));
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("symbolicLocation", location));
            setLocationRequest.setEntity(new UrlEncodedFormEntity(parameters));
            RestTask task = new RestTask(context, MainActivity.CHECK_IN_OUT, CookieManager.getInstance().getCookie(DOMAIN), DOMAIN);
            task.execute(setLocationRequest);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getSymbolicLocation(String cssId) {
        retrieveIndividualEntityId(cssId);
    }

    public void setSymbolicLocation(String newLocation) {
        update(newLocation);
    }


    //Step 1 get Entity
	private void retrieveIndividualEntityId(String cssId) {
//		if (requestor == null)
//			throw new NullPointerException("requestor can't be null");
		if (cssId == null) 
			throw new NullPointerException("cssId can't be null");

//		this.requestor = requestor;
		if (this.connectedToContextClient) {
			android.os.Message outMessage = android.os.Message.obtain(null, 8, 0, 0);
			Bundle outBundle = new Bundle();
			outBundle.putString("client", context.getPackageName());
			outBundle.putParcelable("requestor", requestor);
			outBundle.putString("cssId", cssId);
			outMessage.setData(outBundle);
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
			}
		} else {
			Log.e(LOG_TAG, "Not connected to Context Client service");
		}
	}

	//Step 2 to retrieve location 
	//lookup entities with retrieved entity id from step 1
    private List<CtxIdentifierBean> lookup(RequestorBean requestor,
			CtxEntityIdentifierBean entityId, CtxModelTypeBean modelType,
			String type) throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (entityId == null) 
			throw new NullPointerException("entityId can't be null");
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		
		if (this.connectedToContextClient) {
			//Select target method and create message to convey remote invocation
			android.os.Message outMessage = android.os.Message.obtain(null, 4, 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString("client", context.getPackageName());
			outBundle.putParcelable("requestor", requestor);
			outBundle.putParcelable("entityId", entityId);
			outBundle.putParcelable("modelType", modelType);
			outBundle.putString("type", type);
			outMessage.setData(outBundle);
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
		}
		return null;
	}

	//Step 3 to retrieve location 
	//retrieve the ctxModelObject that includes the desired location using the ctxIdentifier from step2
    private CtxModelObjectBean retrieve(RequestorBean requestor, CtxIdentifierBean identifier) {
		if (this.connectedToContextClient) {
			//Select target method and create message to convey remote invocation
			android.os.Message outMessage = android.os.Message.obtain(null, 7, 0, 0);
			// retrieve(String client, final Requestor requestor, final ACtxIdentifier identifier)
			Bundle outBundle = new Bundle();
			outBundle.putString("client", context.getPackageName());
			outBundle.putParcelable("requestor", requestor);
			outBundle.putParcelable("identifier", identifier);
			outMessage.setData(outBundle);
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
		}

		return null;
	}

	private void update(String newLocation) {
		if (this.connectedToContextClient) {
			locationBean.setStringValue(newLocation);
			Bundle outBundle = getBundle();
			outBundle.putParcelable("requestor", requestor);
			outBundle.putParcelable("object", locationBean);
			callMethod(10, outBundle);
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
		}
	}
}