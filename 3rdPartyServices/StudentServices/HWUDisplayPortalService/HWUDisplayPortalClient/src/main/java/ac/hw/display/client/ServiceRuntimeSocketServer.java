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
package ac.hw.display.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class ServiceRuntimeSocketServer extends Thread{


	private DisplayPortalClient displayService;
	private ServerSocket server;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	public static final String started_Service = "STARTED_SERVICE";
	public static final String stopped_Service = "STOPPED_SERVICE";

	public ServiceRuntimeSocketServer(DisplayPortalClient displayService){
		this.displayService = displayService;

	}

	@Override
	public void run(){
		this.listenSocket();
	}
	public void listenSocket(){

		try{
			server = new ServerSocket(2121); 
		} catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			
		}

		try{
			client = server.accept();
		} catch (IOException e) {
			System.out.println("Accept failed: 4444");
			
		}

		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Accept failed: 4444");
			System.exit(-1);
		}

		while(true){
			try{
				String line = in.readLine();
				if (line.contains(started_Service)){
					String serviceName = line.substring(started_Service.length()+1);
					this.displayService.notifyServiceStarted(serviceName);
				}else if (line.contains(stopped_Service)){
					String serviceName = line.substring(stopped_Service.length()+1);
					this.displayService.notifyServiceStopped(serviceName);
				}
				//Send data back to client
				//out.println(line);
			} catch (IOException e) {
				System.out.println("Read failed");
				finalize();
			}
		}
	}


	@Override
	protected void finalize(){
		//Clean up 
		try{
			in.close();
			out.close();
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close.");
		}
	}
}
