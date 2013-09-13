package uk.ac.hw.services.collabquiz.dao;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.hw.services.collabquiz.Logic.QuestionDifficulty;
import uk.ac.hw.services.collabquiz.dao.impl.QuestionRepository;
import uk.ac.hw.services.collabquiz.entities.Question;

import java.util.List;

public class QuestionRepositoryTest {
    private QuestionRepository questionRepository;

    @Before
    public void setupTest() {
        questionRepository = new QuestionRepository();
    }

    @Test
    public void insert_getByID_insert_getByID_list_physicalDelete_getByID_list() throws Exception {

        // Question test object 1 *****************************
        final Question input1 = new Question();

        input1.setQuestionText("Capital of China?");
        input1.setAnswer1("Beijing");
        input1.setAnswer2("Hong Kong");
        input1.setAnswer3("Anyang");
        input1.setAnswer4("Hanoi");

        input1.setCorrectAnswer(1);
        input1.setCategoryID(1);
        input1.setPointsIfCorrect(1);
        input1.setDifficulty(QuestionDifficulty.Medium);
        // **************************************************

        // Question test object 2 *****************************
        final Question input2 = new Question();

        input2.setQuestionText("How many times have Brazil won the FIFA world cup??");
        input2.setAnswer1("7");
        input2.setAnswer2("6");
        input2.setAnswer3("5");
        input2.setAnswer4("0");

        input2.setCorrectAnswer(3);
        input2.setCategoryID(3);
        input2.setPointsIfCorrect(1);
        input2.setDifficulty(QuestionDifficulty.Hard);
        // **************************************************

        // insert input1
        questionRepository.insert(input1);
        int id1 = input1.getQuestionID();
        Assert.assertTrue(-1 != id1);

        // getByID input1
        final Question returnedInput1 = questionRepository.getByID(id1);
        Assert.assertNotNull(returnedInput1);
        compareItems(input1, returnedInput1);

        // insert input2
        questionRepository.insert(input2);
        int id2 = input2.getQuestionID();
        Assert.assertTrue(-1 != id2);

        // getByID input2
        final Question returnedInput2 = questionRepository.getByID(id2);
        Assert.assertNotNull(returnedInput2);
        compareItems(input2, returnedInput2);

        // list
        final List<Question> list1 = questionRepository.list();
        Assert.assertNotNull(list1);
        Assert.assertEquals(2, list1.size());

        // physicalDelete
        questionRepository.physicalDelete(input1);

        // getByID
        final Question returnedInput2_2 = questionRepository.getByID(id2);
        Assert.assertNotNull(returnedInput2_2);
        compareItems(input2, returnedInput2_2);

        // list
        final List<Question> list2 = questionRepository.list();
        Assert.assertNotNull(list2);
        Assert.assertEquals(1, list2.size());

    }

    @Test
    public void listByCategory() {

        // listByCategory test 1 *****************************
        Question input1 = new Question();

        input1.setQuestionText("Capital of China?");
        input1.setAnswer1("Beijing");
        input1.setAnswer2("Hong Kong");
        input1.setAnswer3("Anyang");
        input1.setAnswer4("Hanoi");

        input1.setCorrectAnswer(1);
        input1.setCategoryID(1);
        input1.setPointsIfCorrect(1);
        input1.setDifficulty(QuestionDifficulty.Medium);

        questionRepository.insert(input1);
        // **************************************************

        // listByCategory test 2 *****************************
        Question input2 = new Question();

        input2.setQuestionText("Capital of France?");
        input2.setAnswer1("Paris");
        input2.setAnswer2("Marseille");
        input2.setAnswer3("Barcelona");
        input2.setAnswer4("Berlin");

        input2.setCorrectAnswer(1);
        input2.setCategoryID(1);
        input2.setPointsIfCorrect(1);
        input2.setDifficulty(QuestionDifficulty.Easy);

        questionRepository.insert(input2);
        // **************************************************

        // listByCategory test 3 *****************************
        final Question input3 = new Question();

        input2.setQuestionText("How many times have Brazil won the FIFA world cup??");
        input2.setAnswer1("7");
        input2.setAnswer2("6");
        input2.setAnswer3("5");
        input2.setAnswer4("0");

        input2.setCorrectAnswer(3);
        input2.setCategoryID(3);
        input2.setPointsIfCorrect(1);
        input2.setDifficulty(QuestionDifficulty.Hard);
        // **************************************************

        // listByCategory test 4 *****************************
        Question input4 = new Question();

        input4.setQuestionText("Which of these was not a German, WWII Concentration Camp?");
        input4.setAnswer1("Auschwitz");
        input4.setAnswer2("Bełżec");
        input4.setAnswer3("Crystal Lake");
        input4.setAnswer4("Jasenovac");

        input4.setCorrectAnswer(3);
        input4.setCategoryID(4);
        input4.setPointsIfCorrect(1);
        input4.setDifficulty(QuestionDifficulty.Easy);

        questionRepository.insert(input4);
        // **************************************************

        List<Question> allItems = questionRepository.list();
        Assert.assertNotNull(allItems);
        Assert.assertEquals(4, allItems.size());

        List<Question> category1items = questionRepository.listByCategory(1);
        Assert.assertNotNull(category1items);
        Assert.assertEquals(2, category1items.size());

        List<Question> category3items = questionRepository.listByCategory(3);
        Assert.assertNotNull(category3items);
        Assert.assertEquals(1, category3items.size());
        compareItems(input3, category3items.get(0));

        List<Question> category4items = questionRepository.listByCategory(4);
        Assert.assertNotNull(category4items);
        Assert.assertEquals(1, category4items.size());
        compareItems(input4, category4items.get(0));
    }

    private void compareItems(Question expectedInput, Question actualInput) {
        Assert.assertEquals(expectedInput.getQuestionID(), actualInput.getQuestionID());
        Assert.assertEquals(expectedInput.getQuestionText(), actualInput.getQuestionText());
        Assert.assertEquals(expectedInput.getAnswer1(), actualInput.getAnswer1());
        Assert.assertEquals(expectedInput.getAnswer2(), actualInput.getAnswer2());
        Assert.assertEquals(expectedInput.getAnswer3(), actualInput.getAnswer3());
        Assert.assertEquals(expectedInput.getAnswer4(), actualInput.getAnswer4());

        Assert.assertEquals(expectedInput.getCorrectAnswer(), actualInput.getCorrectAnswer());
        Assert.assertEquals(expectedInput.getCategoryID(), actualInput.getCategoryID());
        Assert.assertEquals(expectedInput.getPointsIfCorrect(), actualInput.getPointsIfCorrect());
        Assert.assertEquals(expectedInput.getDifficulty(), actualInput.getDifficulty());
    }
}
