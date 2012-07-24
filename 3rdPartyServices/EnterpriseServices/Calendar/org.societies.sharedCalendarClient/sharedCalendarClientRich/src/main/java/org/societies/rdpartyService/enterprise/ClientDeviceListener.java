package org.societies.rdpartyService.enterprise;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;

public class ClientDeviceListener {
	private static Logger log = LoggerFactory.getLogger(ClientDeviceListener.class);

	public void onBind(IDevice service, Map properties){
		log.debug("Found a device:"+service.getDeviceName());
		IDriverService[] services = service.getServices();
		for (IDriverService iDriverService : services) {
			log.debug("Provided Service: "+iDriverService.getServiceDescription());
			for (IAction action : iDriverService.getActions()){
				log.debug("Provided action: "+action.getActionName());
			}			
		}
	}

	public void onUnbind(IDevice service, Map properties){
		log.debug("Lost a device:"+service.getDeviceName());
	}
}
