package uk.ac.hw.services.collabquiz.comms;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






import com.google.gson.Gson;

import uk.ac.hw.services.collabquiz.dao.ICategoryRepository;
import uk.ac.hw.services.collabquiz.dao.IQuestionRepository;
import uk.ac.hw.services.collabquiz.dao.impl.*;
import uk.ac.hw.services.collabquiz.entities.Category;
import uk.ac.hw.services.collabquiz.entities.Question;


public class CommsServerListener implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(CommsServerListener.class);


	private ServerSocket serverSocket;
	private int port;
	private String address;
	private IQuestionRepository questionRepo;
	private ICategoryRepository categoryRepo;

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
			this.address = this.serverSocket.getInetAddress().getLocalHost().getHostAddress();
			this.serverSocket.close();
			log.debug("Socket will listen on: " +address+":"+port);
		} catch (IOException e) {
			log.debug("Error when trying to get port and address!");
		}
	}

	public void listen() {
		try {
			while(true)
			{
				serverSocket = new ServerSocket(port);
				log.debug("Listening for connection from GUI");

				Socket clientSocket = serverSocket.accept();
				String result = "";
				BufferedReader stdIn = null;
				PrintWriter out = null;
				try{
					stdIn = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()),true);
					result = stdIn.readLine();
					boolean reading = true;
					while(reading)
					{
						//result = stdIn.readLine();
						if(result.matches("RETRIEVE_CIS_MEMBERS"))
						{
							//RETRIEVE CIS MEMBERS AND SEND BACK
						}
						else if(result.matches("RETRIEVE_SCORES"))
						{

						}
						else if(result.matches("RETRIEVE_QUESTIONS"))
						{
							log.debug("RETRIEVING QUESTIONS!");
							questionRepo = new QuestionRepository();
							List<Question> questionList = questionRepo.list();
							categoryRepo = new CategoryRepository();
							List<Category> categoryList = categoryRepo.list();
							String sendQuestion = objectToJSON(questionList);
							String sendCategory = objectToJSON(categoryList);
							if(sendQuestion!=null)
							{
								out.println(sendQuestion);
							}
							else
							{
								out.println("NULL");
							}

						}
						else
						{
							log.debug("REQUEST DOESN'T MATCH: " + result);
						}
						reading = false;

					}
				}catch(Exception e)
				{
					log.debug("ERROR READING MSG/SENDING");
					log.debug(e.toString());
				} finally {
					stdIn.close();
					out.close();
					serverSocket.close();
				}


			}
		}catch (IOException e) {
			log.debug("IO Exception - Socket is closed");
			return;
		}
	}

	public String objectToJSON(Object data)
	{
		log.debug("Converting Object to JSON!");
		String jsonData = new Gson().toJson(data);
		if(jsonData!=null)
		{
			return jsonData;
		}
		else
		{
			return "NULL";
		}
		
	}

	public int getSocket(){
		return this.port;
	}

	public String getAddress(){
		return this.address.toString();
	}

}
