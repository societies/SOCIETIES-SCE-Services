package ac.hw.services.collabquiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.springframework.beans.factory.annotation.Autowired;

import ac.hw.services.collabquiz.comms.CommsClient;
import ac.hw.services.collabquiz.comms.CommsServerListener;
import ac.hw.services.collabquiz.dao.IAnsweredQuestionsRepository;
import ac.hw.services.collabquiz.dao.ICategoryRepository;
import ac.hw.services.collabquiz.dao.ICisRepository;
import ac.hw.services.collabquiz.dao.IQuestionRepository;
import ac.hw.services.collabquiz.dao.IUserRepository;
import ac.hw.services.collabquiz.dao.impl.AnsweredQuestionsRepository;
import ac.hw.services.collabquiz.dao.impl.CategoryRepository;
import ac.hw.services.collabquiz.dao.impl.CisRepository;
import ac.hw.services.collabquiz.dao.impl.QuestionRepository;
import ac.hw.services.collabquiz.dao.impl.UserRepository;
import ac.hw.services.collabquiz.entities.AnsweredQuestions;
import ac.hw.services.collabquiz.entities.Category;
import ac.hw.services.collabquiz.entities.Cis;
import ac.hw.services.collabquiz.entities.Question;
import ac.hw.services.collabquiz.entities.User;



public class CollabQuizServer implements ICollabQuizServer {

	private static final Logger log = LoggerFactory.getLogger(CollabQuizServer.class);

	private ICategoryRepository categoryRepo;
	private IQuestionRepository questionRepo;
	private IAnsweredQuestionsRepository answeredQuestionsRepo;
	private IUserRepository userRepo;

	private ICisRepository cisRepo;

	private List<Question> questionList;
	private List<Category> categoryList;
	private ICisOwned cisOwned;

	private CommsServerListener commsServerListener;
	private Thread serverThread;
	private int serverPort;
	private String serverAddress;


	private CommsClient commsClient;

	private ICommManager commsManager;

	private ServiceResourceIdentifier myServiceId;
	private IServices services;
	private IIdentity serverIdentity;






	public void init() {
		//DATABASE REPO 
		log.debug("COLLAB QUIZ SERVER STARTED");
		//this.categoryRepo = new CategoryRepository();
		//this.questionRepo = new QuestionRepository();

		this.userRepo = new UserRepository();
		this.answeredQuestionsRepo = new AnsweredQuestionsRepository();
		this.cisRepo = new CisRepository();
		this.questionRepo = new QuestionRepository();
		this.categoryRepo = new CategoryRepository();



		//SET UP NEW SOCKET
		commsServerListener = new CommsServerListener(this);
		//GET PORT & ADDRESS
		this.serverPort = commsServerListener.getSocket();
		this.serverAddress = commsServerListener.getAddress();
		//NEED TO LISTEN AT SOME POINT FOR GUI {C#}
		serverThread = new Thread(commsServerListener);
		serverThread.start();

	}


	//CHECK IF CURRENT USER HAS PREVIOUSLY PLAYED, IF NOT ADD IN TO DB
	@Override
	public void checkUser(String jid)
	{
		if(userRepo.getByJID(jid)==null)
		{
			log.debug("A user doesn't exists! Add the new user!");
			User newUser = new User();
			newUser.setUserJid(jid);
			newUser.setScore(0);
			userRepo.insert(newUser);
		}
	}

	@Override
	public Question getRandomQuestion(String userID, String cisName) {
		return getRandomQuestion(userID, cisName, null);
	}

	@Override
	public Cis getCis(String cisName) {
		Cis cis = this.cisRepo.getByName(cisName);
		if(cis==null) {
			cis = new Cis();
			cis.setCisName(cisName);
			cis.setScore(0);
			cis.setContributors(new HashSet<String>());
			this.cisRepo.insert(cis);
		}
		return cis;
	}

	public void tearDown() {
		this.commsServerListener.kill();
	}

	@Override 
	public synchronized Question getRandomQuestion(String userID, String cisName, String categoryID) {
		log.debug("Getting random question");
		List<Question> allQuestions;
		if(categoryID==null) {
			log.debug("No category selected, getting all questions");
			allQuestions = questionRepo.list();
		} else {
			log.debug("Getting all questions by category");
			//Category category = categoryRepo.getByID(Integer.parseInt(categoryID));
			allQuestions = questionRepo.listByCategory(Integer.parseInt(categoryID));
		}
		List<AnsweredQuestions> answeredQuestions = new ArrayList<AnsweredQuestions>();
		if(cisName==null) {
			log.debug("Getting answered questions for the user " + userID);
			answeredQuestions = answeredQuestionsRepo.getByJID(userID);
		} else {
			log.debug("Getting all questions for the cis " + cisName);
			answeredQuestions = answeredQuestionsRepo.getByCisName(cisName);
		}
		List<Question> availableQuestions = new ArrayList<Question>();
		Iterator<Question> it = allQuestions.iterator();
		while(it.hasNext()) {
			Question q = it.next();
			log.debug("Checking question " + q.getQuestionText() + " is in " + answeredQuestions.toString());
			if(!answeredQuestions.contains(q)) {
				log.debug("The question hasn't been answered! Make it available");
				availableQuestions.add(q);
			}
		}

		if(availableQuestions.size()>0) {
			int randomNum = new Random().nextInt((availableQuestions.size()));
			return availableQuestions.get(randomNum);
		}

		return null;

	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		users = this.userRepo.list();
		return users;
	}

	@Override
	public List<Cis> getAllCis() {
		List<Cis> ciss = new ArrayList<Cis>();
		ciss = this.cisRepo.list();
		return ciss;
	}

	@Override
	public User getUser(String userJID) {
		return this.userRepo.getByJID(userJID);
	}

	@Override
	public synchronized void answerQuestion(AnsweredQuestions answeredQuestion) {
		log.debug("Answering questiong!");
		this.answeredQuestionsRepo.insert(answeredQuestion);
		if(answeredQuestion.getCisName()==null) {
			log.debug("This question does not refer to a CIS");
			User user = this.userRepo.getByJID(answeredQuestion.getUserID());
			Question question = this.questionRepo.getByID(answeredQuestion.getQuestionID());
			if(answeredQuestion.isAnsweredCorrect()) {
				log.debug("This question is answered correcntly!");
				user.setScore(user.getScore()+question.getPointsIfCorrect());
				userRepo.update(user);;
			}
		} else {
			Cis cis = this.cisRepo.getByName(answeredQuestion.getCisName());
			if(cis==null) {
				cis = new Cis();
				cis.setCisName(answeredQuestion.getCisName());
				HashSet<String> contributors = new HashSet<String>();
				cis.setContributors(contributors);
			}
			Question question = this.questionRepo.getByID(answeredQuestion.getQuestionID());
			if(cis.getContributors()== null) {
				HashSet<String> contributors = new HashSet<String>();
				contributors.add(answeredQuestion.getUserID());
				cis.setContributors(contributors);
			} else {
				cis.getContributors().add(answeredQuestion.getUserID());
			}
			if(answeredQuestion.isAnsweredCorrect()) {
				cis.setScore(cis.getScore()+question.getPointsIfCorrect());				
			}
			this.cisRepo.update(cis);;
		}
	}

	@Override
	public List<String> getInterests(String userID) {
		//	this.commsClient.
		return null;
	}

	@Override
	public List<Category> getAllCategories() {
		return this.categoryRepo.list();
	}

	public List<Question> getQuestions(){
		//this.questionList = questionRepo.list();
		return questionRepo.list();
	}

	public void getCategories() {
		this.categoryList = categoryRepo.list();
	}


	@Override
	public ServiceResourceIdentifier getServerServiceId() {
		if (this.myServiceId==null){
			this.myServiceId = this.getServices().getMyServiceId(this.getClass());
			if (this.myServiceId==null){
				this.log.debug("ServiceID could not be retrieved");
			}else{
				this.log.debug("Returning serviceID :"+this.myServiceId);
			}	
		}
		return this.myServiceId;
	}

	@Override
	public int getPort() {
		return this.serverPort;
	}

	@Override
	public String getAddress() {
		return this.serverAddress;
	}


	/*Injection*/

	public ICommManager getCommsManager() {
		return commsManager;
	}

	public void setCommsManager(ICommManager commsManager) {
		this.commsManager = commsManager;
	}

	public IServices getServices() {
		return services;
	}

	public void setServices(IServices services) {
		this.services = services;
	}


	public void helloWorld() {
		// TODO Auto-generated method stub

	}




	//    @Get
	//    public Representation sayHello() {
	//        log.debug("sayHello()");
	//        StringRepresentation representation = new StringRepresentation(javax.ws.rs.core.MediaType.TEXT_PLAIN);
	//        representation.setText("Hello, world!");
	//        return representation;
	//    }

	//    @Get("xml")
	//    public Representation sayXmlHello() {
	//        log.debug("sayXmlHello()");
	//        List<Item> items = new ArrayList<Item>();
	//        items.add(new Item("name1", "desc1"));
	//        items.add(new Item("name2", "desc2"));
	//        items.add(new Item("name3", "desc3"));
	//
	//        try {
	//            DomRepresentation representation = new DomRepresentation(org.restlet.data.MediaType.TEXT_XML);
	//
	//            // Generate a DOM document representing the list of items.
	//            Document d = representation.getDocument();
	//            Element r = d.createElement("items");
	//            d.appendChild(r);
	//            for (Item item : items) {
	//                Element eltItem = d.createElement("item");
	//
	//                Element eltName = d.createElement("name");
	//                eltName.appendChild(d.createTextNode(item.getName()));
	//                eltItem.appendChild(eltName);
	//
	//                Element eltDescription = d.createElement("description");
	//                eltDescription.appendChild(d.createTextNode(item.getDescription()));
	//                eltItem.appendChild(eltDescription);
	//
	//                r.appendChild(eltItem);
	//            }
	//            d.normalizeDocument();
	//
	//            // Returns the XML representation of this document.
	//            return representation;
	//
	//        } catch (IOException e) {
	//            log.error("Error generating response", e);
	//        }
	//
	//        return null;
	//    }
	//
	//    private class Item {
	//
	//        private final String name;
	//        private final String description;
	//
	//        public Item(String name, String description) {
	//
	//            this.name = name;
	//            this.description = description;
	//        }
	//
	//        private String getName() {
	//            return name;
	//        }
	//
	//        private String getDescription() {
	//            return description;
	//        }
	//    }

}
