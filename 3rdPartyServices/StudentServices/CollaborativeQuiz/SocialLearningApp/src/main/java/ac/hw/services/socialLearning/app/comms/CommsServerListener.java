package ac.hw.services.socialLearning.app.comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CommsServerListener implements Runnable {
	
    private static final Logger log = LoggerFactory.getLogger(CommsServerListener.class);

	
	private ServerSocket serverSocket;
	private int port;
	private InetAddress address;
	
	public CommsServerListener()
	{
		init();
	}

	public void run() {
		listen();		
	}
	
	public void init() {
		try {
			this.serverSocket = new ServerSocket(0);
			this.port = this.serverSocket.getLocalPort();
			this.address = this.serverSocket.getInetAddress();
		} catch (IOException e) {
			log.debug("Error when trying to get port and address!");
		}
	}
	
	public void listen() {
		try {
			serverSocket = new ServerSocket(port);
			while(true)
			{
				log.debug("Listening for connection from GUI");

				Socket clientSocket = serverSocket.accept();
				String result = "";
				try{
					BufferedReader stdIn = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					result = stdIn.readLine();
					//RETURN RESULT TO APP
				}catch(Exception e){e.printStackTrace();}

			}
		}catch (IOException e) {
			log.debug("IO Exception - Socket is closed");
			return;
		}
	}
	
	public int getSocket(){
		return this.port;
	}
	
	public String getAddress(){
		return this.address.toString();
	}
	
}
