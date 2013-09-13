package uk.ac.hw.services.collabquiz.dao;

import uk.ac.hw.services.collabquiz.entities.Question;

import java.util.List;

public interface IQuestionRepository {

    /**
     * Will return ALL questions
     */
    List<Question> list();

    List<Question> listByCategory(int categoryId);

    Question getByID(int questionId);

    void insert(Question question);

    void update(Question question);

    /**
     * Perform a physical delete (instead of a logical delete)
     */
    void physicalDelete(Question question);

}
