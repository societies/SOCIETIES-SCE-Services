package org.temp.CtxBrokerIntegeration;

import org.temp.CISIntegeration.*;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxModelObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.RequestorService;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.services.IServices;
import org.temp.CISIntegeration.ContextBinder;


public class CtxBrokerBridge {
	private String uid;
	private static ConcurrentHashMap<String, CtxBrokerBridge> bdg = new ConcurrentHashMap<String, CtxBrokerBridge>();
	private static ServiceResourceIdentifier myServiceID = null;
	private static Requestor requestorService;
	private static Object sync = new Object();
	private static IIdentity serviceIdentity = null;
	
	private ContextBinderInf ctxbinder=ContextBinder.instance;
	
	private CtxBrokerBridge(String uid) {
		if (myServiceID == null) {
			synchronized (sync) {
				if (myServiceID == null) {
					myServiceID = ctxbinder.getServices().getMyServiceId(ContextBinder.class);
					try {
						serviceIdentity=ctxbinder.getComMgt().getIdManager().getThisNetworkNode();
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

	public void reportLocation(String loc) {
		try {
			System.err.println("location:"+loc);
			IIdentity cssid = ctxbinder.getComMgt().getIdManager()
					.fromJid(uid);
			ICtxBroker ctxBrk=ctxbinder.getCtxBrk();
			List<CtxIdentifier> ids 
			= ctxBrk.lookup(requestorService, cssid,CtxModelType.ENTITY, "NearMeLocation").get();
			CtxAttribute ctxv=null;
			if(ids==null||ids.size()==0){
				CtxEntity ctxe=ctxbinder.getCtxBrk().createEntity(requestorService, cssid, "NearMeLocation").get();
				ctxv=ctxbinder.getCtxBrk().createAttribute(requestorService, ctxe.getId(), "location").get();
				ctxv.setStringValue(loc);
				ctxbinder.getCtxBrk().update(requestorService, ctxv).get();
			}else{
//				CtxIdentifier ctxid=ids.get(0);
//				Future<CtxModelObject> ctxF=ctxBrk.retrieve(requestorService, ctxid);
//				CtxEntity ctxe=(CtxEntity)ctxF.get();
//				ctxv=new ArrayList<CtxAttribute>(ctxe.getAttributes("location")).get(0);
//				ctxv.setStringValue(loc);
//				ctxbinder.getCtxBrk().update(requestorService, ctxv).get();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public void renewProximityData(ProximityData data) {
		try {
			System.err.println("proximity:"+data);
			IIdentity cssid = ctxbinder.getComMgt().getIdManager()
					.fromJid(uid);
			List<CtxIdentifier> ids 
			= ctxbinder.getCtxBrk().lookup(requestorService, cssid,CtxModelType.ENTITY, "PROX").get();
			CtxAttribute ctxv=null;
			if(ids==null||ids.size()==0){
				CtxEntity ctxe=ctxbinder.getCtxBrk().createEntity(requestorService, cssid, "PROX").get();
				ctxv=ctxbinder.getCtxBrk().createAttribute(requestorService, ctxe.getId(), "proximity").get();
				ctxv.setStringValue(data.toString());
				ctxbinder.getCtxBrk().update(requestorService, ctxv).get();
			}else{
//				CtxIdentifier id=ids.get(0);
//				CtxEntity ctxe=(CtxEntity) ctxbinder.getCtxBrk().retrieve(requestorService, id).get();
//				ctxv=new ArrayList<CtxAttribute>(ctxe.getAttributes("proximity")).get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public void cleanProximityData() {
		try {
			IIdentity cssid = ctxbinder.getComMgt().getIdManager()
					.fromJid(uid);
			List<CtxIdentifier> ids 
			= ctxbinder.getCtxBrk().lookup(requestorService, cssid,CtxModelType.ENTITY, "PROX").get();
			CtxAttribute ctxv=null;
			if(ids==null||ids.size()==0){
				CtxEntity ctxe=ctxbinder.getCtxBrk().createEntity(requestorService, cssid, "PROX").get();
				ctxv=ctxbinder.getCtxBrk().createAttribute(requestorService, ctxe.getId(), "proximity").get();
				ctxv.setStringValue("");
				ctxbinder.getCtxBrk().update(requestorService, ctxv).get();
			}else{
//				CtxIdentifier id=ids.get(0);
//				CtxEntity ctxe=(CtxEntity) ctxbinder.getCtxBrk().retrieve(requestorService, id).get();
//				ctxv=new ArrayList<CtxAttribute>(ctxe.getAttributes("proximity")).get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}
