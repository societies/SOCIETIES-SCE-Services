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
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					isWaiting = false;
				}
		}
		
	}

}
