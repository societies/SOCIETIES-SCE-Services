package ac.hw.services.collabquiz.dao;


import java.util.List;

import ac.hw.services.collabquiz.entities.Question;

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
