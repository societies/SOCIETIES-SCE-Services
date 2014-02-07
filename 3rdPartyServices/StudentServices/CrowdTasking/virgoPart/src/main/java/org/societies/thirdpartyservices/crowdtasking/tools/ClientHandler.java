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
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdpartyservices.crowdtasking.CrowdTasking;


public class ClientHandler implements Runnable {

	Logger log = LoggerFactory.getLogger(this.getClass());
	private Socket socket;
	private ObjectOutputStream out ;
	private ObjectInputStream in = null;

	private String clientMessage;
	private CrowdTasking crowdTasking;

	public ClientHandler(Socket socket, CrowdTasking crowdTasking){
		this.socket = socket;
		this.crowdTasking = crowdTasking;
		this.log.debug("Client Handler created");
	}

	public void run(){
		this.log.debug("In run method");

		this.getStreams();
		this.getClientMessage();
		//this.sendMessage(message);
	}

	private void getStreams(){
		try{
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			this.log.debug("OutputStream created");

			in = new ObjectInputStream(socket.getInputStream());
			this.log.debug("Input Stream created");
		}catch(IOException e){
			this.log.debug("ERROR CREATING INPUT/OUTPUT STREAM:" + e.getMessage());
			this.closeConnection();
		}
	}

	private void getClientMessage(){
		try{
			Object x;
			boolean reading = true;
			while(reading){
				x=in.readObject();
				if(x instanceof String){
					clientMessage = (String)x;			
					this.log.info("Received a message from Android: " + clientMessage);
					if (crowdTasking.getMyServiceID() != null){
						this.sendMessage(crowdTasking.getMyServiceID().getServiceInstanceIdentifier());
					}
					else {
						this.sendMessage("No service id.");
					}
				}
			}		
		}catch(ClassNotFoundException e){
			this.log.debug("ClassNotFoundException: " + e.getMessage());
		}catch(InvalidClassException e){
			this.log.debug("InvalidClassException: " + e.getMessage());
		}catch(IOException e) {
			this.log.debug("IOException: " + e.getMessage());
		}
	}

	public void sendMessage(String message){
		this.log.debug("In sendMessage");
		try{
			this.log.debug("Attempting to send service id: " + message);
			out.writeObject(message);
			this.log.debug("Message sent to client: " + message);
			out.flush();
			out.reset();
		}
		catch(IOException ioException){
			this.log.debug("ERROR WHILE SENDING LOCATION!" + ioException.getStackTrace());
		}
	}

	private void closeConnection(){
		try{
			this.log.debug("Closing connection");
			in.close();
			out.close();
			socket.close();
		}catch(IOException e){
			this.log.debug("ERROR CLOSING CONNECTION" + e.getMessage());
		}
	}
}
