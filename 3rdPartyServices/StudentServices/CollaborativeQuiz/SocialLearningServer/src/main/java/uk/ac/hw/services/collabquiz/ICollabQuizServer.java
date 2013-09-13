package uk.ac.hw.services.collabquiz;

import java.util.List;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import uk.ac.hw.services.collabquiz.entities.Question;


public interface ICollabQuizServer {
	
	List<Question> getQuestions();
	ServiceResourceIdentifier getServerServiceId();
	int getPort();
	String getAddress();
	void checkUser(String jid);

}
