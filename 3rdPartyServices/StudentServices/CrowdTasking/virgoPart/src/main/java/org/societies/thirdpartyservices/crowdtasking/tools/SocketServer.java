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
package org.societies.thirdpartyservices.crowdtasking.tools;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdpartyservices.crowdtasking.CrowdTasking;


/**
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class SocketServer implements Runnable{

	Logger log = LoggerFactory.getLogger(this.getClass());

	private ServerSocket providerSocket;
	private Socket connection = null;

	private CrowdTasking crowdTasking;

	//	private ServiceResourceIdentifier myServiceId;
	//	private IIdentity serverId;
	//	private String cssId;

	//	public SocketServer(ServiceResourceIdentifier serviceId, IIdentity serverId){
	//		this.myServiceId = serviceId;
	//		this.serverId = serverId;
	//	}

	public SocketServer(CrowdTasking crowdTasking){
		this.crowdTasking = crowdTasking;
	}

	public void openSocket(){
		try{
			this.log.debug("Opening port 38980");
			this.providerSocket = new ServerSocket(38980);
			this.log.debug("Waiting for connection : " + providerSocket.getInetAddress().getHostAddress());

		}catch(IOException ioEx){
			this.log.debug("ERROR OPENNING PORT" + ioEx.getMessage());
			System.exit(1);
		}
	}

	public void run(){
		this.openSocket();
		while(true){
			try{
				connection = providerSocket.accept();        				
				this.log.debug("client connected: " + connection.getInetAddress().getHostName());
				(new Thread(new ClientHandler(connection,crowdTasking))).start();
			}catch(IOException e){
				this.log.debug("ERROR ACCEPTING CONNECTION" + e.getMessage());
				return;
			}
		}
	}
}