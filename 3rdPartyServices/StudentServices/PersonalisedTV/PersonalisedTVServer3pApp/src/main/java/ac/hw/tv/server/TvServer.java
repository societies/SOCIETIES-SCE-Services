/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ac.hw.tv.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceActionsConstants;
import org.societies.api.css.devicemgmt.model.DeviceMgmtConstants;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceMgmtEventConstants;
import org.societies.api.css.devicemgmt.model.DeviceStateVariableConstants;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
//import org.societies.api.css.devicemgmt.tv.ITvDriver;
//import org.societies.api.css.devicemgmt.tv.TvUpdateEvent;
import org.societies.api.internal.css.devicemgmt.model.DeviceMgmtDriverServiceConstants;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.springframework.osgi.context.BundleContextAware;

import ac.hw.tv.client.api.remote.ITvClient;
import ac.hw.tv.server.api.ITvServer;

public class TvServer extends EventListener implements ITvServer, ServiceTrackerCustomizer, BundleContextAware {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	//private ServiceResourceIdentifier myServiceId;
	//private List<String> myServiceTypes = new ArrayList<String>();
	
	private Hashtable<String, String> tagToPasswordTable;

	private Hashtable<String, String> tagtoIdentityTable;
	
	private Hashtable<String, String> wUnitToSymlocTable;
	private TvGuiFrame frame;
	
	//private ITvDriver tvDriver;
	private IEventMgr eventMgr;
	
	//private Hashtable<String, String> dpiToServiceID;
	Hashtable<String, Timer> tagToTimerTable = new Hashtable<String, Timer>();

	private ITvClient tvClientRemote;
	
	private BundleContext bundleContext;
	
	private ServiceTracker serviceTracker;
	
	private Collection<IDevice> devicesTracked = Collections.synchronizedCollection(new ArrayList<IDevice>()); 
	
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}
	
	
	public void initialiseTvServer(){
		
		
		//String stringFilter = "(&("+ Constants.OBJECTCLASS +"="+ IDevice.class.getName()+")("+DeviceMgmtConstants.DEVICE_TYPE+"="+DeviceTypeConstants.TV_READER+"))";
		
		Filter filter = null;
		//try {
		//	filter = bundleContext.createFilter(stringFilter);
		//} catch (InvalidSyntaxException e) {
		//	e.printStackTrace();
		//}
		
		this.serviceTracker = new ServiceTracker(bundleContext, filter, this);
		this.serviceTracker.open();
		
		

		this.tagtoIdentityTable = new Hashtable<String, String>();
		this.tagToPasswordTable = new Hashtable<String, String>();
		//this.dpiToServiceID = new Hashtable<String, String>();
		TvConfig tvConfig = new TvConfig();
		this.wUnitToSymlocTable = tvConfig.getUnitToSymloc();
		if (this.wUnitToSymlocTable==null){
			this.wUnitToSymlocTable = new Hashtable<String, String>();
			
		}
		String[] options = new String[]{"0.localhost","1.University addresses"};
		String str = (String) JOptionPane.showInputDialog(null, "Select Configuration", "Configuration", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (str.equalsIgnoreCase(options[0])){
			
			for(IDevice iDevice : devicesTracked)
			{
				if (iDevice.getDeviceId().contains("127.0.0.1")) 
				{
					this.registerForTvEvents(iDevice.getDeviceId());
					
					//IDriverService iDriverService = iDevice.getService(DeviceMgmtDriverServiceNames.TV_READER_DRIVER_SERVICE);
					//IAction iAction = iDriverService.getAction(DeviceActionsConstants.TV_CONNECT_ACTION);
					
					//Dictionary<String, Object> dic = new Hashtable<String, Object>();
					
					//dic.put(DeviceStateVariableConstants.IP_ADDRESS_STATE_VARIABLE, "127.0.0.1");
					//iAction.invokeAction(dic);
				}
			}

			//this.tvDriver.connect("127.0.0.1");
		}else{
			
			for(IDevice iDevice : devicesTracked)
			{
				if (iDevice.getDeviceId().contains("137.195.27.197")) 
				{
					this.registerForTvEvents(iDevice.getDeviceId());
					
					//IDriverService iDriverService = iDevice.getService(DeviceMgmtDriverServiceNames.TV_READER_DRIVER_SERVICE);
					//IAction iAction = iDriverService.getAction(DeviceActionsConstants.TV_CONNECT_ACTION);
					
					//Dictionary<String, Object> dic = new Hashtable<String, Object>();
					
					//dic.put(DeviceStateVariableConstants.IP_ADDRESS_STATE_VARIABLE, "137.195.27.197");
					//iAction.invokeAction(dic);
				}
				if (iDevice.getDeviceId().contains("137.195.27.198")) 
				{
					this.registerForTvEvents(iDevice.getDeviceId());
					
					//IDriverService iDriverService = iDevice.getService(DeviceMgmtDriverServiceNames.TV_READER_DRIVER_SERVICE);
					//IAction iAction = iDriverService.getAction(DeviceActionsConstants.TV_CONNECT_ACTION);
					
					//Dictionary<String, Object> dic = new Hashtable<String, Object>();
					
					//dic.put(DeviceStateVariableConstants.IP_ADDRESS_STATE_VARIABLE, "137.195.27.198");
					//iAction.invokeAction(dic);
				}
			}
		}
		frame = new TvGuiFrame(this);
	}
	
	public TvServer(){
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	private void registerForTvEvents(String deviceId){
		//String eventFilter = "(&" + 
		//		"(" + CSSEventConstants.EVENT_NAME + "=" + DeviceMgmtEventConstants.TV_READER_EVENT + ")" +
		//		"(" + CSSEventConstants.EVENT_SOURCE + "=" + deviceId + ")" +
		//		")";
		//this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.TV_UPDATE_EVENT}, eventFilter);
	}
	
	/*
	 * Method called when an TV_UPDATE_EVENT is received
	 */
	public void sendUpdate(String wUnit, String tvTagNumber) {
	
				
		/**if (this.tagtoIdentityTable.containsKey(tvNumber)){
			String dpi = this.tagtoIdentityTable.get(tvNumber);
			//String clientServiceID = this.dpiToServiceID.get(dpi);
			//this.sendUpdateMessage(dpi, clientServiceID, tvNumber, symLoc);
			this.tvClientRemote.sendUpdate(dpi, symLoc, tvNumber);
			this.frame.addRow(dpi, tvNumber, wUnit, symLoc);

		}else{
			//JOptionPane.showMessageDialog(null, "Tag: "+tvNumber+" in location "+symLoc+" not registered to a DPI");
			logging.debug("TV: "+tvNumber+" in location "+symLoc+" not registered to a DPI");
			this.frame.addRow("Unregistered", tvNumber, wUnit, symLoc);
		}*/
		
		
	}

	@Override
	public void registerTvTag(String tvNumber, String dpiAsString, String serviceID, String password) {
		logging.debug("Received request to register TV: "+tvNumber+" from dpi: "+dpiAsString+" and serviceID: "+serviceID);
			/**if (this.tagToPasswordTable.containsKey(tagNumber)){
				String myPass = this.tagToPasswordTable.get(tagNumber);
				logging.debug("Tag exists");
				
			}else{
				
				//this.sendAcknowledgeMessage(dpiAsString, serviceID, 2);
				logging.debug("Registration unsuccessfull. Sent Ack 2");
			}*/
		

		
	}

	private void removeOldRegistration( String dpiAsString){
		if (this.tagtoIdentityTable.contains(dpiAsString)){
			
			Enumeration<String> tags = this.tagtoIdentityTable.keys();
			
			while (tags.hasMoreElements()){
				String tag = tags.nextElement();
				String dpi = this.tagtoIdentityTable.get(tag);
				if (dpi.equalsIgnoreCase(dpiAsString)){
					this.tagtoIdentityTable.remove(tag);
					/*if (this.dpiToServiceID.containsKey(dpiAsString)){
						this.dpiToServiceID.remove(dpiAsString);
					}*/
					//this.frame.setNewDPIRegistered(tag, "");
					return;
				}
			}
		}
		
		/*if (this.dpiToServiceID.containsKey(dpiAsString)){
			this.dpiToServiceID.remove(dpiAsString);
		}*/
	

	}
	public String getPassword() {
		int n = 4;
		char[] pw = new char[n];
		int c  = 'A';
		int  r1 = 0;
		for (int i=0; i < n; i++)
		{
			r1 = (int)(Math.random() * 3);
			switch(r1) {
			case 0: c = '0' +  (int)(Math.random() * 10); break;
			case 1: c = 'a' +  (int)(Math.random() * 26); break;
			case 2: c = 'A' +  (int)(Math.random() * 26); break;
			}
			pw[i] = (char)c;
		}
		return new String(pw);
	}

	public void storePassword(String tagNumber, String password){
		this.tagToPasswordTable.put(tagNumber, password);
		this.tagtoIdentityTable.remove(tagNumber);
	}
	
	public static void main(String[] args) throws IOException{
		TvServer impl = new TvServer();
		System.out.println(impl.getPassword());
		
		
		TvConfig config = new TvConfig();
		impl.wUnitToSymlocTable = config.getUnitToSymloc();
		
	}



	/**
	 * @return the tvClient
	 */
	public ITvClient getTvClientRemote() {
		return tvClientRemote;
	}



	/**
	 * @param tvClientRemote the tvClient to set
	 */
	public void setTvClientRemote(ITvClient tvClientRemote) {
		this.tvClientRemote = tvClientRemote;
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {

		HashMap<String, String> payload = (HashMap<String, String>)event.geteventInfo();
		this.sendUpdate(payload.get("wakeupUnit"), payload.get("tagNumber"));
		this.logging.debug("Received TvUpdateEvent: "+payload.get("wakeupUnit")+" - "+ payload.get("tagNumber"));
		
		
	}





	@Override
	public Object addingService(ServiceReference reference) {
		
		IDevice iDevice = (IDevice) bundleContext.getService(reference);
		
		bindDevice(iDevice);
		
		return iDevice;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {

		
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		
		unbindDevice((IDevice)service);
	}
	
	protected void bindDevice(IDevice device) {
		devicesTracked.add(device);
		System.out.println("TvServer: TV added");
	}

	protected void unbindDevice(IDevice device) {
		devicesTracked.remove(device);
		System.out.println("TvServer: TV removed");
	}
}