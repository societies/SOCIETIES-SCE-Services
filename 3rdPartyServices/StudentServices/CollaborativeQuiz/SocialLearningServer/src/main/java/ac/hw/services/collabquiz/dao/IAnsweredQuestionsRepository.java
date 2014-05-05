package ac.hw.services.collabquiz.dao;

import java.util.List;

import ac.hw.services.collabquiz.entities.AnsweredQuestions;



public interface IAnsweredQuestionsRepository {
	List<AnsweredQuestions> list();
	List<AnsweredQuestions> getByJID(String jid);
	void insert(AnsweredQuestions userAnsweredQ);
	void insertList(List<AnsweredQuestions> userAnsweredQ);
	void update(List<AnsweredQuestions> userAnsweredQ);
	void deleteAll(String id);
	void physicalDelete(AnsweredQuestions userAnsweredQ);
	public List<AnsweredQuestions> getByCisName(String cisName);
}
