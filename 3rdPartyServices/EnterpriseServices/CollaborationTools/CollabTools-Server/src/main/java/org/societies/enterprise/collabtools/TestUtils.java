package org.societies.enterprise.collabtools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.neo4j.helpers.collection.IteratorUtil;
import org.societies.enterprise.collabtools.Interpretation.ContextIncrementation;
import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.runtime.SessionRepository;
import org.xml.sax.SAXException;

import scala.actors.threadpool.Arrays;


public class TestUtils {

    private static final Random r = new Random( System.currentTimeMillis() );
	private static int nrOfPersons = 5;
    private PersonRepository personRepository;
    private SessionRepository sessionRep;
    
    public TestUtils(PersonRepository personRepository,  SessionRepository sessionRep) {
    	this.personRepository = personRepository;
        this.sessionRep = sessionRep;
	}
    
	public void createPersons() throws Exception
    {
        for ( int i = 0; i < nrOfPersons; i++ )
        {    	
            Person person = personRepository.createPerson( "person#" + i);
            //Set long term context
            person.setLongTermCtx(Person.NAME, "person#" + i);
            System.out.println("person#" +i+" created" );
        }
    }
	
    public void deleteSocialGraph()
    {
        for ( Person person : personRepository.getAllPersons() )
        {
            personRepository.deletePerson( person );
        }
    }

	public void setupFriendsBetweenPeople( int maxNrOfFriendsEach )
    {
        for ( Person person : personRepository.getAllPersons() )
        {
//                person.addFriend( getRandomPerson() );
        	Person[] persons = getPersonWithSimilarInterests(person);
        	for (Person individual : persons)
        		person.addFriend(individual);    
        }
    }

	/**
	 * 
	 */
    public void createMockShortTermCtx() {
    	//        Person person = getRandomPersonWithFriends();
    	//        int numberOfStatuses = 10;
    	//
    	//        for ( int i = 0; i < numberOfStatuses; i++ )
    	//        {
    	//            Person friend = getRandomFriendOf( person );
    	//            friend.addContextStatus( "Dum-deli-dum..."+i, getRandomLocation() );
    	//        }
    	for (Person friend :personRepository.getAllPersons()) {
    		friend.addContextStatus( getRandomStatus(), getRandomLocation(), sessionRep );
    	}
    }

	/**
	 * 
	 */
	public void createMockLongTermCtx() {
    	for (Person friend :personRepository.getAllPersons()) {
        	friend.setLongTermCtx(LongTermCtxTypes.WORK, getRandomWork());
        	friend.setLongTermCtx(LongTermCtxTypes.INTERESTS, getRandomInterests());
        	friend.setLongTermCtx(LongTermCtxTypes.COMPANY, getRandomCompanies());
    	}	
	}
	

    private Person getRandomPerson()
    {
        return personRepository.getPersonByName( "person#"
                + r.nextInt( nrOfPersons ) );
    }

    private Person[] getPersonWithSimilarInterests(Person self)
    {
    	ArrayList<Person> persons = new ArrayList<Person>();
    	for ( Person person : personRepository.getAllPersons() )
    	{
    		if (!self.equals(person)) {
    			List<String> personInterest = Arrays.asList(person.getInterests());
    			for (String match : self.getInterests()) {    			
    				if (personInterest.contains(match)) {
    					persons.add(person);
    					break;
    				}
    			}
    		}

    	}
    	return persons.toArray(new Person[persons.size()]);
    }

	/**
	 * @return
	 */
	private static String getRandomLocation() {
		final String[] location={"Work","Home","Gym"};
		return location[r.nextInt(3)];
	}	

	/**
	 * @return
	 */
	private static String[] getRandomInterests() {
		final String[] interests={"bioinformatics", "web development", "semantic web", "requiremens analysis", "system modeling", 
				"project planning", "project management", "software engineering", "software development", "technical writing"};
		Set<String> finalInterests = new HashSet<String>();
		for(int i=0; i<3; i++){
			String temp = interests[r.nextInt(interests.length)];
			//Check if duplicated
			if (!finalInterests.contains(temp))
				finalInterests.add(temp);
			else
				i--;
		}
		return finalInterests.toArray(new String[0]);
	}
	
	/**
	 * @return
	 */
	private static String getRandomStatus() {
		final String[] status={"Online","Busy","Away"};
		return status[r.nextInt(3)];
	}
	
	/**
	 * @return
	 */
	private static String getRandomCompanies() {
		final String[] companies={"PTIn","TI","Intel"};
		return companies[r.nextInt(3)];
	}
	
	/**
	 * @return
	 */
	private static String getRandomWork() {
		final String[] work={"Manager","Developer","Beta Tester"};
		return work[r.nextInt(3)];
	}	
	

	/**
	 * @param person
	 * @return
	 */
	private Person getRandomFriendOf(Person person) {
        ArrayList<Person> friends = new ArrayList<Person>();
        IteratorUtil.addToCollection( person.getFriends().iterator(), friends );
        return friends.get( r.nextInt( friends.size() ) );
	}

	/**
	 * @return
	 */
	private Person getRandomPersonWithFriends() {
		Person p;
        do
        {
            p = getRandomPerson();
        }
        while ( p.getNrOfFriends() == 0 );
        return p;
	}
	
	public void changeLocation(){
		
	}

	/**
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws XPathExpressionException 
	 * 
	 */
	public void incrementInterests() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		ContextIncrementation increment = new ContextIncrementation();
    	for (Person friend :personRepository.getAllPersons()) {
    		String[] newInterests = increment.ctxIncremantationByConcept(friend.getInterests());
    		friend.setLongTermCtx(LongTermCtxTypes.INTERESTS, newInterests);
    	}		
	}

	
}
