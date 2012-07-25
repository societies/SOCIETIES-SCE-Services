package org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;


public class ListenThread extends Thread{
	
	Socket socket;
	private IProcessMessage processMessageInterface;

	public ListenThread(Socket clientSocket, IProcessMessage processMessageInterface) {
		setName("ListenThread");
		this.socket = clientSocket;
		this.processMessageInterface = processMessageInterface;
	}
	
	@Override
	public void run() {
		
		try {
			
			InputStream socketinputStream = socket.getInputStream();
			while(socket.isConnected()){

					byte[] headerLengthB = new byte[4];
					Tools.read2ByteArray(socketinputStream, headerLengthB);

	//				System.out.println(new String(headerLengthB));
					int headerLength = Tools.byteArrayToInt(headerLengthB);
	//				System.out.println("Headerlength:" + headerLength);
					
					if (headerLength > 1000){
						throw new Exception("Header seems to be too long (>1000 bytes)");
					}
					
					byte[] headerB = new byte[headerLength];
					Tools.read2ByteArray(socketinputStream, headerB);

					DMTCSSProtocol header = new DMTCSSProtocol(headerB);
					
					ByteArrayOutputStream bytes = new ByteArrayOutputStream(header.payloadSize);
					Tools.copyAmountOfBytes(socketinputStream, bytes, header.payloadSize);
					
//					System.out.println("Received message of type " + header.protocolType);

					processMessageInterface.processMessage(header.protocolType, new String(bytes.toByteArray()));
					
					
			}		
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
