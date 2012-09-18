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
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class MyTvClient{ //implements IDisplayableService

	SocketClient socketClient;
	SocketServer socketServer;
	String myIpAddress;
	IUserActionMonitor uam;
	IIdentity userID;
	ServiceResourceIdentifier myServiceId;
	String myServiceType;

	public MyTvClient(){
		//start server listening for connections from GUI
		socketServer = new SocketServer();
		socketServer.start();
	}

	/*public void serviceStarted(String guiIpAddress){
		//connect to server - service GUI
		if(socketClient != null){
			if (socketClient.isConnected()){
				socketClient.disconnect();
			}
		}
		socketClient = new SocketClient(guiIpAddress);
		socketClient.connect();

		//send ipAddress details
		if(!socketClient.sendMessage
				("START_MSG\n" +
						"CLIENT_IP\n"+
						myIpAddress+"\n"+
						"END_MSG")){
			System.out.println("Error - client IP address not sent to GUI");
		}
		
		//send preference update
		if(!socketClient.sendMessage
				("START_MSG\n" +
						"PREF_OUTCOME\n" +
						"volume = mute\n" +
						"END_MSG")){
			System.out.println("Error - could not send preference update to GUI");
		}
	}

	public void serviceStopped(String guiIpAddress){
		//disconnect from server - service GUI
		if(socketClient != null && socketClient.isConnected()){
			socketClient.disconnect();
		}
	}*/

	public void setUam(IUserActionMonitor uam){
		this.uam = uam;
	}

	public static void main(String[] args) throws IOException{
		new MyTvClient();
	}
}