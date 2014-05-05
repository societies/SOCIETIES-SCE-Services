package ac.hw.services.collabquiz;

import java.util.List;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import ac.hw.services.collabquiz.entities.AnsweredQuestions;
import ac.hw.services.collabquiz.entities.Category;
import ac.hw.services.collabquiz.entities.Cis;
import ac.hw.services.collabquiz.entities.Question;
import ac.hw.services.collabquiz.entities.User;



public interface ICollabQuizServer {
	
	List<Question> getQuestions();
	ServiceResourceIdentifier getServerServiceId();
	int getPort();
	String getAddress();
	void checkUser(String jid);
	
	Question getRandomQuestion(String userID, String cisID);
	Question getRandomQuestion(String userID, String cisID, String categoryID);
	User getUser(String userJid);
	List<User> getAllUsers();
	List<Cis> getAllCis();
	void answerQuestion(AnsweredQuestions answeredQuestion);
	List<Category> getAllCategories();
	List<String> getInterests(String userID);
	Cis getCis(String cisName);

}
