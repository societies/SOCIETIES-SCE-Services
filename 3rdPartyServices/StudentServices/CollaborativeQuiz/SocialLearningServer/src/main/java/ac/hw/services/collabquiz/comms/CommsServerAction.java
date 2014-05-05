package ac.hw.services.collabquiz.comms;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ac.hw.services.collabquiz.ICollabQuizServer;
import ac.hw.services.collabquiz.dao.ICategoryRepository;
import ac.hw.services.collabquiz.dao.IQuestionRepository;
import ac.hw.services.collabquiz.entities.AnsweredQuestions;
import ac.hw.services.collabquiz.entities.Category;
import ac.hw.services.collabquiz.entities.Cis;
import ac.hw.services.collabquiz.entities.Question;
import ac.hw.services.collabquiz.entities.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CommsServerAction implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(CommsServerListener.class);

	private Socket clientSocket;

	private ICollabQuizServer collabQuiz;

	//MESSAGE TYPES
	private static final String GET_USER = "GET_USER";
	private static final String NEXT_QUESTION = "NEXT_QUESTION";
	private static final String ANSWER_QUESTION = "ANSWER_QUESTION";
	private static final String GET_ALL_USERS = "GET_ALL_USERS";
	private static final String GET_ALL_CIS = "GET_ALL_CIS";
	private static final String GET_CATEGORIES = "GET_CATEGORIES";
	private static final String GET_USER_INTERESTS = "GET_USER_INTERESTS";
	private static final String GET_CIS = "GET_CIS";

	private static final String NULL_REPLY = "NULL";
	private static final String TRUE_REPLY = "TRUE";

	public CommsServerAction(ICollabQuizServer collabQuiz, Socket clientSocket) {
		this.collabQuiz = collabQuiz;
		this.clientSocket = clientSocket;
	}

	public void run() {

		String result = "";
		BufferedReader stdIn = null;
		PrintWriter out = null;
		try{
			stdIn = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()),true);
			result = stdIn.readLine();
			//result = stdIn.readLine();
			if(result.equals(GET_USER)) {
				result = stdIn.readLine();
				User user = this.collabQuiz.getUser(result);
				String reply = objectToJSON(user);
				out.println(reply);
			} else if (result.equals(NEXT_QUESTION)) {
				Question question = null;
				String userID = stdIn.readLine();
				String cisName = stdIn.readLine();
				String cat = stdIn.readLine();
				if(cisName.equals(NULL_REPLY)) {
					cisName = null;
				}
				if(cat.equals(NULL_REPLY)) {
					cat = null;
					
				}
				question = this.collabQuiz.getRandomQuestion(userID, cisName, cat);
				String reply;
				if(question!=null) {
					reply = objectToJSON(question);
				} else {
					reply = NULL_REPLY;
				}
				out.println(reply);
			} else if(result.equals(ANSWER_QUESTION)) {
				result = stdIn.readLine();
				AnsweredQuestions answeredQ = (AnsweredQuestions) new Gson().fromJson(result, AnsweredQuestions.class);
				this.collabQuiz.answerQuestion(answeredQ);
				String reply = TRUE_REPLY;
				out.println(reply);
			} else if(result.equals(GET_ALL_USERS)) {
				List<User> users = collabQuiz.getAllUsers();
				String reply = objectToJSON(users);
				out.println(reply);
			} else if(result.equals(GET_ALL_CIS)) {
				List<Cis> ciss = collabQuiz.getAllCis();
				String reply = objectToJSON(ciss);
				out.println(reply);
			} else if (result.equals(GET_CIS)) {
				result = stdIn.readLine();
				Cis cis = collabQuiz.getCis(result);
				String reply = objectToJSON(cis);
				out.println(reply);
			} else if(result.equals(GET_CATEGORIES)) {
				List<Category> categories = collabQuiz.getAllCategories();
				String reply = objectToJSON(categories);
				out.println(reply);
			} else if(result.equals(GET_USER_INTERESTS)) {
				String userID = stdIn.readLine();
			//	this.clientSocket
			} else {
				log.debug("REQUEST DOESN'T MATCH: " + result);
			}



		}catch (Exception e) {
			log.debug("Other exception " + e);
			StackTraceElement[] a = e.getStackTrace();
			for(int x = 0; x < a.length; x++)
			{
				log.debug(a[x].toString());
			}


		}
		try{
			out.close();
			stdIn.close();
			clientSocket.close();
		}catch(Exception e){}

	}



	private String objectToJSON(Object data)
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

}
