package org.societies.enterprise.collabtools.acquisition;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.Activator;
import org.societies.enterprise.collabtools.api.IContextSubscriber;
import org.societies.enterprise.collabtools.interpretation.ContextAnalyzer;
import org.societies.enterprise.collabtools.runtime.CtxMonitor;
import org.societies.enterprise.collabtools.runtime.SessionRepository;
import org.xml.sax.SAXException;

public class ContextSubscriber implements IContextSubscriber, Observer
{
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	private PersonRepository personRepository;
	private SessionRepository sessionRepository;
	ContextConector ctxConnector = new ContextConector(this);
	private String cisID;
	

	public void initialCtx () {
		HashMap<String, HashMap<String, String[]>> persons = ctxConnector.getInitialContext(cisID);
		Iterator<String> personIterator = persons.keySet().iterator();

		   
		while (personIterator.hasNext()) {  
		   String personKey = personIterator.next().toString();  
		   HashMap<String, String[]> ctxAttributes = persons.get(personKey);
		   Iterator<String> ctxIterator = ctxAttributes.keySet().iterator();
		   logger.info("Person with context attributes: "+personKey + " " + ctxAttributes);  
		   while (ctxIterator.hasNext()) {  
			   String ctxKey = ctxIterator.next().toString(); 
			   String[] ctxArray = ctxAttributes.get(ctxKey);
			   try {
				this.setContext(ctxKey, ctxArray, personKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		   
		}
		
		logger.info("Starting Context Monitor...");
		CtxMonitor thread = new CtxMonitor(this.personRepository, this.sessionRepository);
		thread.start();
	}
	
	public ContextSubscriber(PersonRepository personRepository, SessionRepository sessionRepository)
	{
		this.personRepository = personRepository;
		this.sessionRepository = sessionRepository;
	}

	public void update(Observable o, Object arg)
	{
		String[] msg = (String[])arg;
		logger.info("******************************* update event  ***************** " + msg[0]);
		Person individual = this.personRepository.getPersonByName(msg[2]);
		String context = msg[1];
		String type = msg[0];
		if (type == "location")
			individual.addContextStatus( individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.STATUS), context, sessionRepository );
		else if (type == "status")
			individual.addContextStatus( context, individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION), sessionRepository );
	}

	private void setContext(String type, String[] context, String person) throws Exception {
		logger.info("******************************* Adding Context for: " + person + ", " + type + ", " + context);
		//TODO: FIX THIS
		Person individual = null;
		if (type == "name") {
			individual = this.personRepository.createPerson(person);
			individual.setLongTermCtx("name", context);
		}
		else if (type == "interests")
			individual.setLongTermCtx("interests", context);
		else if (type == "work")
			individual.setLongTermCtx("work", context[0]);
		else if (type == "company")
			individual.setLongTermCtx("company", context[0]);
		else if (type == "location")
			individual.addContextStatus( (individual.getLastStatus() == null ? "" : individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.STATUS)), context[0], sessionRepository );
		else if (type == "status")
			individual.addContextStatus( context[0], (individual.getLastStatus() == null ? "" : individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION)), sessionRepository );
	}
	
	public void setCommunity(String cisID)
	{
		this.cisID = cisID;
	}

	private void enrichedCtx() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException
	{
		ContextAnalyzer ctxRsn = new ContextAnalyzer(this.personRepository);
		ctxRsn.incrementInterests();
	}

	private void setupWeightBetweenPeople(Person person)
	{
		Map<Person, Integer> persons = this.personRepository.getPersonWithSimilarInterests(person);
		for (Map.Entry<Person, Integer> entry : persons.entrySet()) {
			//Similarity Formula is: similar interests/ min(personA, personB)
			float weight = ContextAnalyzer.personInterestsSimilarity(entry.getValue(), entry.getKey(), person);
			person.addFriend(entry.getKey(),weight);  
		}
	}
}