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

package org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class WriteThread extends Thread{
	
	private Socket socket;
	@SuppressWarnings("unused")
	private IProcessMessage processMessageInterface;

	public WriteThread(Socket socket, IProcessMessage processMessageInterface) {
		setName("WriteThread");
		this.socket = socket;
		this.processMessageInterface = processMessageInterface;
	}
	
	private Vector<String[]> writeRequestVector = new Vector<String[]>();
	private boolean isWaiting;
	
	public void write(String protocol, String payload){
		String[] request = {protocol, payload};
		writeRequestVector.add(request);
		
		if (this.isWaiting)
			this.interrupt();
		
	}
	
	@Override
	public void run() {
		
		while(socket.isConnected()){
			
			if (!writeRequestVector.isEmpty()){
				
				String[] currentWriteRequest = writeRequestVector.remove(0);
				
//				System.out.println("Sending message of type " + currentWriteRequest[0] + " with content \"" + currentWriteRequest[1] + "\"");
				
				byte[] bytesToSend = currentWriteRequest[1].getBytes();
				byte[] header = new DMTCSSProtocol(currentWriteRequest[0], bytesToSend.length).calculateHeaderAndSize();
				
				try {
					Tools.copy(new ByteArrayInputStream(header), socket.getOutputStream());
					Tools.copy(new ByteArrayInputStream(bytesToSend), socket.getOutputStream());
//					logger.debug("Data item " + currentWriteRequest.header.streamID + " sent.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
			if (writeRequestVector.isEmpty())
				try {
					isWaiting = true;
					sleep(60000);
				} catch (InterruptedException e) {
					isWaiting = false;
				}
		}
		
	}
	
	public void shutdown() {
		if (this.isWaiting)
			this.interrupt();
	}

}
