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
package ac.hw.mytv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.societies.api.css.devicemgmt.display.IDisplayableService;
import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class MyTvClient implements IDisplayableService, IActionConsumer{

	SocketClient socketClient;
	SocketServer socketServer;
	CommandHandler commandHandler;
	IUserActionMonitor uam;
	IIdentity userID;
	ServiceResourceIdentifier myServiceID;
	String myServiceType;
	List<String> myServiceTypes;
	
	//personalisable parameters

	public MyTvClient(){
		initialiseMyTvClient();  //just for testing!!!
	}

	public void initialiseMyTvClient(){

		//set service type
		myServiceType = "Multimedia";
		myServiceTypes = new ArrayList<String>();
		myServiceTypes.add(myServiceType);

		//get user ID

		//get service ID
		
		//start server listening for connections from GUI
		commandHandler = new CommandHandler();
		socketServer = new SocketServer(commandHandler);
		socketServer.start();
	}

	@Override
	public void serviceStarted(String guiIpAddress){
		//service is started
	}

	@Override
	public void serviceStopped(String guiIpAddress){
		//service is stopped
	}


	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {
		return myServiceID;
	}

	@Override
	public String getServiceType() {
		return myServiceType;
	}

	@Override
	public List<String> getServiceTypes() {
		return myServiceTypes;
	}

	@Override
	public boolean setIAction(IIdentity identity, IAction action) {

		return false;
	}
	
	private void setChannel(int channel){
		
	}
	
	private void setVolume(int volume){
		
	}

	public void setUam(IUserActionMonitor uam){
		this.uam = uam;
	}

	public static void main(String[] args) throws IOException{
		new MyTvClient();
	}




	/*
	 * Handle commands from GUI
	 */
	public class CommandHandler{
		public void connectToGUI(String gui_ip){
			System.out.println("Connecting to service GUI on IP address: "+gui_ip);
			if(socketClient != null){
				if(socketClient.isConnected()){
				}
			}
			socketClient = new SocketClient(gui_ip);
			socketClient.connect();
		}
		
		public void processUserAction(String actionString){
			System.out.println("Processing user action: "+actionString);
			String[] actionParts = actionString.split("\n");
			String parameterName = actionParts[0];
			String value = actionParts[1];
			
			//create action object and send to uam
			IAction action = new Action(myServiceID, myServiceType, parameterName, value);
			uam.monitor(userID, action);
		}
	}
}