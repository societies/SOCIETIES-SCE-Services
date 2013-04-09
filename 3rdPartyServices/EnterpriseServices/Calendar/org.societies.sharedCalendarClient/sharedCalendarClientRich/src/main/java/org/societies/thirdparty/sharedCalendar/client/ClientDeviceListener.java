package org.societies.thirdparty.sharedCalendar.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;

public class ClientDeviceListener {
	private static final Logger log = LoggerFactory.getLogger(ClientDeviceListener.class);

	public void onBind(IDevice service, Map properties){
		if(log.isDebugEnabled())
			log.debug("Found a device:"+service.getDeviceName());
		IDriverService[] services = service.getServices();
		for (IDriverService iDriverService : services) {
			if(log.isDebugEnabled())
				log.debug("Provided Service: "+iDriverService.getServiceDescription());
			for (IAction action : iDriverService.getActions()){
				if(log.isDebugEnabled())
					log.debug("Provided action: "+action.getActionName());
			}			
		}
	}

	public void onUnbind(IDevice service, Map properties){
		if(log.isDebugEnabled())
			log.debug("Lost a device:"+service.getDeviceName());
	}
}
