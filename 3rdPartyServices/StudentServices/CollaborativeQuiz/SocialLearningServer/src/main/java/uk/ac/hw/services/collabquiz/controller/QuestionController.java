package uk.ac.hw.services.collabquiz.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import uk.ac.hw.services.collabquiz.dao.IQuestionRepository;
import uk.ac.hw.services.collabquiz.dao.QuestionRepository;
import uk.ac.hw.services.collabquiz.entities.Question;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@Controller
@ManagedBean(name = "questionController")
@ViewScoped
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    private IQuestionRepository questionRepository;

    private List<Question> questions;

    public QuestionController() {
        log.debug("QuestionController ctor()");
        questionRepository = new QuestionRepository();
    }


    @PostConstruct
    public void postConstruct() {
        log.debug("postConstruct()");

        questions = questionRepository.list();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void growl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Second Message", "Additional Info Here..."));
    }

}
