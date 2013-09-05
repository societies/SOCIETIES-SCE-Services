package ac.hw.services.socialLearning.app.comms;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.hw.services.socialLearning.api.ISocialLearningService;



public class CommsServerListener implements Runnable {
	
    private static final Logger log = LoggerFactory.getLogger(CommsServerListener.class);

	
	private ServerSocket serverSocket;
	private int port;
	private InetAddress address;
	private ISocialLearningService socialApp;
	
	public CommsServerListener(ISocialLearningService socialApp)
	{
		init();
		this.socialApp = socialApp;
	}

	public void run() {
		listen();		
	}
	
	public void init() {
		try {
			log.debug("Starting socket...");
			this.serverSocket = new ServerSocket(0);
			this.port = this.serverSocket.getLocalPort();
			this.address = this.serverSocket.getInetAddress();
			this.serverSocket.close();
		} catch (IOException e) {
			log.debug("Error when trying to get port and address!");
		}
	}
	
	public void listen() {
		try {
			
			while(true)
			{
				serverSocket = new ServerSocket(port);
				BufferedReader stdIn = null;
				PrintWriter out = null;
				
				log.debug("Listening for connection from GUI on port:"+ String.valueOf(port));

				Socket clientSocket = serverSocket.accept();
				String result = "";
				try{
					log.debug("GOT CONNECTION");
					stdIn = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					
					out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()),true);
					
					boolean reading =true;
					while(reading)
					{
						result = stdIn.readLine();
						if(result.matches("REQUEST_SERVER"))
						{
							String reply = socialApp.getServerIPPort();
							if(reply!=null)
							{
								out.println(reply);
							}
							else
							{
								out.println("NULL");
							}
						}
					}
					
					

					//RETURN RESULT TO APP
				}catch(Exception e){e.printStackTrace();}
				finally {
					out.close();
					stdIn.close();
					serverSocket.close();
				}

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
