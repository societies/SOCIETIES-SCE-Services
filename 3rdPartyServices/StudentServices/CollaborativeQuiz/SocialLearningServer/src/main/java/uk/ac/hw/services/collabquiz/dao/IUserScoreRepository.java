package uk.ac.hw.services.collabquiz.dao;

import java.util.List;

import uk.ac.hw.services.collabquiz.entities.UserScore;

public interface IUserScoreRepository {
	List<UserScore> list();
	UserScore getByJID(String jid);
	void insert(UserScore userScore);
	void update(UserScore userScore);
	void physicalDelete(UserScore userScore);

}
