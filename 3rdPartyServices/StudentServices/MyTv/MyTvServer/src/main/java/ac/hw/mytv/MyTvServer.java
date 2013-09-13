package ac.hw.mytv;

import javax.xml.ws.ServiceMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.services.ServiceMgmtEvent;
import org.societies.api.services.ServiceMgmtEventType;
import org.societies.api.services.ServiceUtils;


public class MyTvServer extends EventListener
{
	
	private IServices serviceMgmt;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	private IEventMgr evMgr;
	private ServiceResourceIdentifier myServiceId;
	

	
	public void initialiseMyTvServer(){
		this.registerForServiceEvents();
	}

	private void registerForServiceEvents(){
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.SERVICE_STARTED+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle)" +
				")";
		this.evMgr.subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		this.logging.debug("Subscribed to "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");
	}

	private void unregisterForServiceEvents()
	{
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.NEW_SERVICE+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle)" +
				")";

		this.evMgr.unSubscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		//this.evMgr.subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		this.logging.debug("Unsubscribed from "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");
	}
	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public IEventMgr getEvMgr() {
		return evMgr;
	}

	public void setEvMgr(IEventMgr evMgr) {
		this.evMgr = evMgr;
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		logging.debug("Received internal event: "+event.geteventName());

	//	if(event.geteventName().equalsIgnoreCase("NEW_SERVICE")){
			//logging.debug("Received SLM event");
			ServiceMgmtEvent slmEvent = (ServiceMgmtEvent) event.geteventInfo();
			logging.debug("EventBundle: " + slmEvent.getBundleSymbolName());
			if (slmEvent.getBundleSymbolName().equalsIgnoreCase("ac.hw.mytv.MyTVServer")){
				this.logging.debug("Received SLM event for my bundle");
				if (slmEvent.getEventType().equals(ServiceMgmtEventType.SERVICE_STARTED)){
					myServiceId = this.serviceMgmt.getMyServiceId(this.getClass());
					this.logging.debug("Received my serviceId: "+ServiceUtils.serviceResourceIdentifierToString(myServiceId));
		
					this.unregisterForServiceEvents();
				}
			
		}
		
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		// TODO Auto-generated method stub
		
	}

	public ServiceResourceIdentifier getMyServiceId() {
		return myServiceId;
	}


}
