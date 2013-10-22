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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClient {

	private static Logger LOG = LoggerFactory.getLogger(SocketClient.class);
	String guiIpAddress;
	Socket echoSocket;
	PrintWriter out;
	BufferedReader in;
	boolean connected;

	public SocketClient(String guiIpAddress){
		this.guiIpAddress = guiIpAddress;
		connected = false;
	}

	public boolean connect(){
		try{
			InetAddress address = InetAddress.getByName(guiIpAddress);
			echoSocket = new Socket(address, 4322);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			connected = true;
			return  true;
		} catch (UnknownHostException e) {
			if(LOG.isDebugEnabled()) LOG.debug("Don't know about host: "+guiIpAddress);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: "+guiIpAddress);
			if(LOG.isDebugEnabled()) LOG.debug("Couldn't get I/O for "
					+ "the connection to: "+guiIpAddress);
		} 
		return false;
	}

	public void disconnect(){
		if (echoSocket!=null){
			try {
				out.close();
				in.close();
				echoSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		connected = false;
	}

	public boolean sendMessage(String message) {
		if (connected){
			try{
				out.println(message);
				out.flush();
				if(LOG.isDebugEnabled()) LOG.debug("Sent message: "+message);
				String input = in.readLine();
				disconnect();
				if(LOG.isDebugEnabled()) LOG.debug("received: "+input);
				if (input.contains("RECEIVED")){
					return true;
				}else{
					return false;
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}else{
			if (connect()){
				sendMessage(message);
				return true;
			}
		}
		return false;
	}

	public boolean isConnected(){
		return connected;
	}
}
