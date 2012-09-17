package org.societies.enterprise.collabtools.acquisition;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
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

public class ContextSubscriber
implements IContextSubscriber, Observer
{
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	private PersonRepository personRepository;
	private SessionRepository sessionRepository;
	private int counter = 0;
	private Person lastIndivudal;
	private String cis;

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

	public void setContext(String type, String context, String person) throws Exception {
		logger.info("******************************* Changing Context: " + person + ", " + type + ", " + context);
		Person individual = null;
		if (type == "name") {
			this.counter++;
			logger.info("counter..."+this.counter);
			if (this.counter >= 5) {
				enrichedCtx();
				setupWeightBetweenPeople(this.lastIndivudal);
				if (this.counter == 5) {
					logger.info("Starting Context Monitor...");
					CtxMonitor thread = new CtxMonitor(this.personRepository, this.sessionRepository);

					thread.start();
				}
			}
			individual = this.personRepository.createPerson(person);
			individual.setLongTermCtx("name", context);
			logger.info("******************************* Person.NAME: " + this.personRepository.getPersonByName(person).getName());
		}

		individual = this.personRepository.getPersonByName(person);
		this.lastIndivudal = individual;
		if (type == "interests")
			individual.setLongTermCtx("interests", context);
		else if (type == "work")
			individual.setLongTermCtx("work", context);
		else if (type == "company")
			individual.setLongTermCtx("company", context);
		else if (type == "location")
			individual.addContextStatus( (individual.getLastStatus() == null ? "" : individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.STATUS)), context, sessionRepository );
		else if (type == "status")
			individual.addContextStatus( context, (individual.getLastStatus() == null ? "" : individual.getLastStatus().getShortTermCtx(ShortTermCtxTypes.LOCATION)), sessionRepository );
	}
	
	public void setCommunity(String cis)
	{
		this.cis = cis;
	}

	public void setContext(String type, String[] context, String person)
	{
		Person individual = this.personRepository.getPersonByName(person);
		individual.setLongTermCtx("interests", context);
	}

	public void enrichedCtx() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException
	{
		ContextAnalyzer ctxRsn = new ContextAnalyzer(this.personRepository);
		ctxRsn.incrementInterests();
	}

	public void setupWeightBetweenPeople(Person person)
	{
		Map<Person, Integer> persons = this.personRepository.getPersonWithSimilarInterests(person);
		for (Map.Entry<Person, Integer> entry : persons.entrySet()) {
			//Similarity Formula is: similar interests/ min(personA, personB)
			float weight = ContextAnalyzer.personInterestsSimilarity(entry.getValue(), entry.getKey(), person);
			person.addFriend(entry.getKey(),weight);  
		}
	}
}