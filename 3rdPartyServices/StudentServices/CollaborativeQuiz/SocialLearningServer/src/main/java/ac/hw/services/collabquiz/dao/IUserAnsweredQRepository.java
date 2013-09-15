package ac.hw.services.collabquiz.dao;

import java.util.List;

import ac.hw.services.collabquiz.entities.UserAnsweredQ;



public interface IUserAnsweredQRepository {
	List<UserAnsweredQ> list();
	List<UserAnsweredQ> getByJID(String jid);
	void insert(UserAnsweredQ userAnsweredQ);
	void insertList(List<UserAnsweredQ> userAnsweredQ);
	void update(List<UserAnsweredQ> userAnsweredQ);
	void physicalDelete(UserAnsweredQ userAnsweredQ);
}
