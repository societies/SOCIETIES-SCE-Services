package ac.hw.services.collabquiz.dao;

import java.util.List;

import ac.hw.services.collabquiz.entities.User;


public interface IUserRepository {
	List<User> list();
	User getByJID(String jid);
	void insert(User userScore);
	void update(User userScore);
	void physicalDelete(User userScore);

}
