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

	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private WriteThread writer;

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
			
			
			while(true){
				clientSocket = serverSocket.accept();
				
				new ListenThread(clientSocket, this).start();
				writer = new WriteThread(clientSocket, this);
				writer.start();
			}
			
			
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	public void shutdown() {
//		System.out.println("Shutting down socketThread on ports " + serverSocket.getLocalPort() + " and " + clientSocket.getPort());
		try {
			clientSocket.close();
			serverSocket.close();
		} catch (Exception e) {
			// nothing to do
		}
	}

	public void exceptionCaught(Exception e) {
		if ("Connection reset".equals(e.getMessage())){
//			System.out.println("Somebody disconnected from port " + clientSocket.getPort());
			return;
		}
		
		e.printStackTrace();
		shutdown();
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
