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
package ac.hw.askfree.sockets;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.hw.askfree.AskFree;

/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class ClientHandler implements Runnable {

	Logger log = LoggerFactory.getLogger(this.getClass());
	private Socket socket;
	private ObjectOutputStream out ;
	private ObjectInputStream in = null;

	private String cssId;
	private AskFree askFree;

	public ClientHandler(Socket socket, AskFree askFree){
		this.socket = socket;
		this.askFree = askFree;
		this.log.debug("Client Handler created");
	}

	public void run(){
		this.log.debug("In run method");

		this.getStreams();
		this.getClientMessage();
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
		JSONObject jObj = null;
		String json = "";
		
		try{
			Object x;
			boolean reading = true;
			while(reading){
				x=in.readObject();
				this.log.info("received message from Android: " + x.toString());
				
				//if(x instanceof String){	
					jObj = new JSONObject(x.toString());
					if (jObj.has("cssid")){
						this.log.info("cssid message");
						try {
							cssId = jObj.getString("cssid");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						//cssId = (String)x;			
						this.log.info("received cssId: " + cssId + " from Android");
						//ADD THIS TO THE MAP (OVERWRITES ANY PREVIOUS)
						askFree.addHandler(cssId, this);
						if (askFree.getSymbolicLocation(cssId) != null){
							this.sendMessage(askFree.getSymbolicLocation(cssId));
							this.log.info("User already has symbolic location: " + askFree.getSymbolicLocation(cssId));
						}
					}else if(jObj.has("activity")){
						this.log.info("received activity");
						try {
							String activity = jObj.getString("activity");
							this.log.info("activity: " + activity);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						//post activity to users css
						
					}
					
				//}
			}		
		}catch(ClassNotFoundException e){
			this.log.debug("ClassNotFoundException: " + e.getMessage());
		}catch(InvalidClassException e){
			this.log.debug("InvalidClassException: " + e.getMessage());
		}catch(IOException e) {
			this.log.debug("IOException: " + e.getMessage());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void sendMessage(String message){
		this.log.info("In sendMessage");
		try{
			this.log.info("Attempting to send location: " + message);
			out.writeObject(message);
			this.log.info("Message sent to client: " + message);
			out.flush();
			out.reset();
		}
		catch(IOException ioException){
			this.log.info("ERROR WHILE SENDING LOCATION!" + ioException.getStackTrace());
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
