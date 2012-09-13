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

import java.net.ServerSocket;
import java.net.Socket;

/**
 * use case 1: view
 * - on restore view notify CSS (users loads view)
 * - while e.g. same timestamp CSS sets view
 * use case 2: send POIs
 * - users manually sends POIs to other DMTs
 * - CSS triggers automatically sendPOIs
 * additionally:
 * - CSS gets position (every second)
 * - CSS gets direction (every second)
 * NICE TO HAVE
 * use case 3: synchronise POIs based on category
 * - on startup the CSS is informed about the categories in scope
 * - CSS organizes the synchronisation of these categories
 */

public class SocketThread extends Thread implements IProcessMessage {

	private int port;
	private boolean run = true;

	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private WriteThread writer;
	private ListenThread listen;

	private IDataToCSSFromDMT dataToCSSinterface;

	

	public SocketThread(int port, IDataToCSSFromDMT dataToCSSinterface) {
		setName("DMTService waiting for ServerSocket on port "+port);
		this.port = port;
		this.dataToCSSinterface = dataToCSSinterface;
	}

	@Override
	public void run() {
//		System.out.println("starting socket server on " + port);
		
		try {
			serverSocket = new ServerSocket(port);
			
			
			while(run){
				clientSocket = serverSocket.accept();
				
				listen = new ListenThread(clientSocket, this);
				listen.start();
				writer = new WriteThread(clientSocket, this);
				writer.start();
			}
			
			
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@SuppressWarnings("deprecation")
	public void shutdown() {
		//System.out.println("Shutting down socketThread");

		try {
			if (clientSocket != null)
				clientSocket.close();
		} catch (Exception e) {
//			e.printStackTrace();
		} 

		try {
			serverSocket.close();
		} catch (Exception e) {
//			e.printStackTrace();
		} 
		
		try {
			writer.shutdown();
		} catch (Exception e) {
//			e.printStackTrace();
		} 
		
		try {
			listen.shutdown();
		} catch (Exception e) {
//			e.printStackTrace();
		}

		if (writer != null)
			writer.stop();
		
		run = false;
	}

	public void exceptionCaught(Exception e) {
		if ("Connection reset".equals(e.getMessage())){
//			System.out.println("Somebody disconnected from port " + clientSocket.getPort());
			return;
		}
		
		e.printStackTrace();
		//shutdown();
	}

	/** Process a message coming from a DMT 
	 * @param protocolType
	 * @param message
	 */
	public void processMessage(String protocolType, String message){
		
		if (protocolType.equals(DMTCSSProtocol.SENDPOIS_MANUALLY_TRIGGERED)){
			dataToCSSinterface.poisSent();
			
		} else if (protocolType.equals(DMTCSSProtocol.VIEW_LOADED)){
			dataToCSSinterface.viewLoaded(message);
			
		} else if (protocolType.equals(DMTCSSProtocol.GPS_STATUS)){
			dataToCSSinterface.gpsConnected(new Boolean(message));
			
		} else if (protocolType.equals(DMTCSSProtocol.POSITION_UPDATE)){
			String[] splittedString = message.split(DMTCSSProtocol.DELIMITER);
			dataToCSSinterface.setPosition(new Double(splittedString[0]), new Double(splittedString[1]), new Double(splittedString[2]), new Integer(splittedString[3]));
			
		} else if (protocolType.equals(DMTCSSProtocol.COMPASS_STATUS)){
			dataToCSSinterface.compassConnected(new Boolean(message));
			
		} else if (protocolType.equals(DMTCSSProtocol.DIRECTION_UPDATE)){
			String[] splittedString = message.split(DMTCSSProtocol.DELIMITER);
			dataToCSSinterface.setDirection(new Double(splittedString[0]), new Double(splittedString[1]), new Double(splittedString[2]));
			
		} else {
//			System.out.println("Error: Could not decode message " + message + " with protocol " + protocolType);
		}
	}

	public void sendPOIs() {
		writer.write(DMTCSSProtocol.TRIGGER_SENDPOIS, "");
	}

	public void addPOI(String POIXML) {
		if (writer!=null)
			writer.write(DMTCSSProtocol.ADD_POI, POIXML);
	}

	public void processView(String viewXML) {
		// process received view
		setView(viewXML);
	}

	public void setView(String viewXML) {
		if (writer!=null)
			writer.write(DMTCSSProtocol.SET_VIEW, viewXML);
	}

	public void setAllViews(String viewXML) {
		setView(viewXML);
	}
}
