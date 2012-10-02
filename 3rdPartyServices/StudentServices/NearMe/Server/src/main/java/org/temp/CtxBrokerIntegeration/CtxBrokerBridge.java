package org.temp.CtxBrokerIntegeration;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.RequestorService;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.temp.CISIntegeration.ContextBinder;

public class CtxBrokerBridge {
	private String uid;
	private static ConcurrentHashMap<String, CtxBrokerBridge> bdg = new ConcurrentHashMap<String, CtxBrokerBridge>();
	private static ServiceResourceIdentifier myServiceID = null;
	private static Requestor requestorService;
	private static Object sync = new Object();
	private static IIdentity serviceIdentity = null;
	private CtxBrokerBridge(String uid) {
		if (myServiceID == null) {
			synchronized (sync) {
				if (myServiceID == null) {
					myServiceID = new ServiceResourceIdentifier();
					try {
						myServiceID
								.setIdentifier(new URI(
										"css://nikosk@societies.org/ContextAware3pService"));
						serviceIdentity= ContextBinder.getComMgt().getIdManager().getThisNetworkNode();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					requestorService = new RequestorService(serviceIdentity,
							myServiceID);
				}
			}
		}
		this.uid = uid;
	}

	public static CtxBrokerBridge getBridge(String uid) {
		if (!bdg.containsKey(uid))
			bdg.put(uid, new CtxBrokerBridge(uid));

		return bdg.get(uid);
	}

	public void renewProximityData(ProximityData data) {
		try {
			IIdentity cssid = ContextBinder.getComMgt().getIdManager()
					.fromJid(uid);
			List<CtxIdentifier> ids 
			= ContextBinder.getCtxBrk().lookup(requestorService, cssid,CtxModelType.ENTITY, "PROX").get();
			CtxAttribute ctxv=null;
			if(ids==null||ids.size()==0){
				CtxEntity ctxe=ContextBinder.getCtxBrk().createEntity(requestorService, cssid, "PROX").get();
				ctxv=ContextBinder.getCtxBrk().createAttribute(requestorService, ctxe.getId(), "proximity").get();
			}else{
				CtxIdentifier id=ids.get(0);
				CtxEntity ctxe=(CtxEntity) ContextBinder.getCtxBrk().retrieve(requestorService, id).get();
				ctxv=new ArrayList<CtxAttribute>(ctxe.getAttributes("proximity")).get(0);
			}
			ctxv.setStringValue(data.toString());
			ContextBinder.getCtxBrk().update(requestorService, ctxv).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public void cleanProximityData() {
		try {
			IIdentity cssid = ContextBinder.getComMgt().getIdManager()
					.fromJid(uid);
			List<CtxIdentifier> ids 
			= ContextBinder.getCtxBrk().lookup(requestorService, cssid,CtxModelType.ENTITY, "PROX").get();
			CtxAttribute ctxv=null;
			if(ids==null||ids.size()==0){
				CtxEntity ctxe=ContextBinder.getCtxBrk().createEntity(requestorService, cssid, "PROX").get();
				ctxv=ContextBinder.getCtxBrk().createAttribute(requestorService, ctxe.getId(), "proximity").get();
			}else{
				CtxIdentifier id=ids.get(0);
				CtxEntity ctxe=(CtxEntity) ContextBinder.getCtxBrk().retrieve(requestorService, id).get();
				ctxv=new ArrayList<CtxAttribute>(ctxe.getAttributes("proximity")).get(0);
			}
			ctxv.setStringValue("");
			ContextBinder.getCtxBrk().update(requestorService, ctxv).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}
