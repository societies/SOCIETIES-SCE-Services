package uk.ac.hw.services.collabquiz;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.hw.services.collabquiz.comms.CommsServerListener;
import uk.ac.hw.services.collabquiz.comms.CommsClient;
import uk.ac.hw.services.collabquiz.dao.impl.CategoryRepository;
import uk.ac.hw.services.collabquiz.dao.impl.QuestionRepository;
import uk.ac.hw.services.collabquiz.dao.ICategoryRepository;
import uk.ac.hw.services.collabquiz.dao.IQuestionRepository;
import uk.ac.hw.services.collabquiz.entities.Question;
import uk.ac.hw.services.collabquiz.entities.Category;


public class CollabQuizServer implements ICollabQuizServer {

	private static final Logger log = LoggerFactory.getLogger(CollabQuizServer.class);

	private ICategoryRepository categoryRepo;
	private IQuestionRepository questionRepo;

	private List<Question> questionList;
	private List<Category> categoryList;

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
		this.categoryRepo = new CategoryRepository();
		this.questionRepo = new QuestionRepository();

		//SET UP NEW SOCKET
		commsServerListener = new CommsServerListener();
		//GET PORT & ADDRESS
		this.serverPort = commsServerListener.getSocket();
		this.serverAddress = commsServerListener.getAddress();
		//NEED TO LISTEN AT SOME POINT FOR GUI {C#}
		serverThread = new Thread(commsServerListener);
		serverThread.start();
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
