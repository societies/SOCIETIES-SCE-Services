package org.societies.thirdparty.enterprise.sharedCalendar;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;

public class DeviceListener{
	private static final Logger log = LoggerFactory.getLogger(DeviceListener.class);

	public void onBind(IDevice service, Map properties){
		log.debug("Found a device:"+service.getDeviceName());
		IDriverService[] services = service.getServices();
		for (IDriverService iDriverService : services) {
			log.debug("Provided Service: "+iDriverService.getServiceDescription());
		}
	}

	public void onUnbind(IDevice service, Map properties){
		log.debug("Lost a device:"+service.getDeviceName());
	}

}
