package ac.hw.services.collabquiz.comms;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;















import ac.hw.services.collabquiz.dao.ICategoryRepository;
import ac.hw.services.collabquiz.dao.IQuestionRepository;
import ac.hw.services.collabquiz.dao.IUserAnsweredQRepository;
import ac.hw.services.collabquiz.dao.IUserScoreRepository;
import ac.hw.services.collabquiz.dao.impl.*;
import ac.hw.services.collabquiz.entities.Category;
import ac.hw.services.collabquiz.entities.Question;
import ac.hw.services.collabquiz.entities.UserAnsweredQ;
import ac.hw.services.collabquiz.entities.UserScore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



public class CommsServerListener implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(CommsServerListener.class);


	private ServerSocket serverSocket;
	private int port;
	private String address;
	private IQuestionRepository questionRepo;
	private ICategoryRepository categoryRepo;
	private IUserAnsweredQRepository userAnswerRepo;
	private IUserScoreRepository userScoreRepo;
	private static final Type collectionType = new TypeToken<List<UserAnsweredQ>>(){}.getType();

	public CommsServerListener()
	{
		init();
	}

	public void run() {
		listen();		
	}

	public void init() {
		this.userAnswerRepo= new UserAnsweredQRepository();
		this.userScoreRepo=new UserScoreRepository();
		this.questionRepo = new QuestionRepository();
		this.categoryRepo= new CategoryRepository();
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
						if(result.matches("RETRIEVE_SCORES"))
						{
							log.debug("RETRIEVING SCORES!");
							//GET SCORES
							List<UserScore> userScoresList = userScoreRepo.list();
							if(!userScoresList.isEmpty())
							{
								log.debug("REPLY ISNT EMPTY");
								String sendScores = objectToJSON(userScoresList);
								out.println(sendScores);
							}
							else
							{
								log.debug("REPLY IS EMPTY");
								out.println("NULL");
							}

						}
						else if(result.matches("RETRIEVE_USER_HISTORY"))
						{
							log.debug("RETRIEVING USER ANSWERED Q'S");
							//NEXT LINE SHOULD BE USER
							result = stdIn.readLine();
							//IF USER IS SOCIETIES
							List<UserAnsweredQ> userAnswered = null;
							if(result.contains(".societies"))
							{
								userAnswered = userAnswerRepo.getByJID(result);	
							}						
							if(userAnswered!=null)
							{
								String sendUserAnswered = objectToJSON(userAnswered);
								out.println(sendUserAnswered);
							}
							else
							{
								out.println("NULL");
							}
						}
						else if(result.matches("RETRIEVE_QUESTIONS"))
						{
							log.debug("RETRIEVING QUESTIONS!");
							List<Question> questionList = questionRepo.list();
							if(!questionList.isEmpty())
							{
								String sendQuestion = objectToJSON(questionList);
								out.println(sendQuestion);
							}
							else
							{
								out.println("NULL");
							}

						}
						else if(result.matches("RETRIEVE_CATEGORIES"))
						{
							log.debug("RETRIEVING CATEGORIES!");
							List<Category> categoryList = categoryRepo.list();
							
							if(!categoryList.isEmpty())
							{
								String sendCategory = objectToJSON(categoryList);
								out.println(sendCategory);
							}
							else
							{
								out.println("NULL");
							}

						}
						else if(result.matches("UPLOAD_PROGRESS"))
						{
							log.debug("Received progress update...");
							result = stdIn.readLine();
							UserScore updateUser = new Gson().fromJson(result, UserScore.class);
							log.debug("Received user update...");
							log.debug("USER: "+ updateUser.getUserJid() +"\n SCORE: "+String.valueOf(updateUser.getScore()));
							userScoreRepo.update(updateUser);
							log.debug("Updated user information in DB");
							log.debug("Now getting update on questions answered");
							List<UserAnsweredQ> alreadyAsked = userAnswerRepo.getByJID(updateUser.getUserJid());
							result = stdIn.readLine();
							if(result!=null && !result.isEmpty())
							{
								
								log.debug("Received questions from GUI");
								List<UserAnsweredQ> answeredQ = new Gson().fromJson(result, collectionType);
								answeredQ.removeAll(alreadyAsked);
								log.debug("Received following question IDs");
								for(UserAnsweredQ u : answeredQ)
								{
									log.debug(String.valueOf(u.getQuestion().getQuestionID()));
								}
								log.debug("Now updating the database");
								//List<UserAnsweredQ> answeredQ = Arrays.asList(new Gson().fromJson(result, UserAnsweredQ.class));
								userAnswerRepo.update(answeredQ);
							}
							log.debug("Insertered correctly!");
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
