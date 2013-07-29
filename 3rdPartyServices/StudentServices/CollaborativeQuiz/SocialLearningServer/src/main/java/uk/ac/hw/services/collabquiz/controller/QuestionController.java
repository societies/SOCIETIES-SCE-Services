package uk.ac.hw.services.collabquiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uk.ac.hw.services.collabquiz.Logic.QuestionDifficulty;
import uk.ac.hw.services.collabquiz.dao.IQuestionRepository;
import uk.ac.hw.services.collabquiz.dao.impl.QuestionRepository;
import uk.ac.hw.services.collabquiz.entities.Question;
import uk.ac.hw.services.collabquiz.entities.Question;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

@Controller("questionController")
@ManagedBean(name = "questionController")
@ViewScoped
public class QuestionController extends BasePageController {

    @Autowired
    private IQuestionRepository questionRepository;

    private List<Question> questions;

    // Creating new question
    private Question newQuestion = new Question();

    private Question selectedQuestion = new Question();

    // Question selected using checkbox
    private Question[] selectedQuestions;

    public QuestionController() {
        log.debug("QuestionController ctor()");

        //this.questionRepository = new QuestionRepository();

//        try {
//            String driverClass = "com.mysql.jdbc.Driver";
//            Class.forName(driverClass);
//            log.debug("Successfully loaded class " + driverClass);
//        } catch (ClassNotFoundException e) {
//            log.error("Error loading mysql class", e);
//        }
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("postConstruct ()");

        try {
            questions = questionRepository.list();
            if(questions==null){
                log.debug("question list is null");
            }
        } catch (Exception ex) {
            log.error("Error loading questions from repository, none loaded", ex);
            questions = new ArrayList<Question>();
        }

        if (log.isDebugEnabled())
            log.debug("Loaded " + questions.size() + " questions from DB");
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void growl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Second Message", "Additional Info Here..."));
    }

    public IQuestionRepository getQuestionRepository() {
        return questionRepository;
    }

    public Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public void setSelectedQuestion(Question selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
    }

    public Question[] getSelectedQuestions() {
        return selectedQuestions;
    }

    public void setSelectedQuestions(Question[] selectedQuestions) {
        this.selectedQuestions = selectedQuestions;
    }

    public void setQuestionRepository(IQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question getNewQuestion() {
        return newQuestion;
    }

    public void setNewQuestion(Question newQuestion) {
        this.newQuestion = newQuestion;
    }

    public void addQuestion() {
        questionRepository.insert(newQuestion);
        questions.add(newQuestion);
        newQuestion = new Question(); //Safe longterm solution?
    }

    public void deleteQuestion() {
        for (Question current : selectedQuestions) {
            //log.debug("deleteing: " + current +" OUT OF: " + selectedCategories.toString());
            questionRepository.physicalDelete(current);
            questions.remove(current);
        }
    }

    public QuestionDifficulty[] getAvailableDifficulties() {
        return QuestionDifficulty.values();
    }
}
