/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.enterprise.collabtools;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.enterprise.collabtools.interpretation.ContextAnalyzer;
import org.societies.enterprise.collabtools.runtime.SessionRepository;
import org.xml.sax.SAXException;


public class TestUtils {

    private static final Random r = new Random( System.currentTimeMillis() );
	private static int nrOfPersons;
    private PersonRepository personRepository;
    private SessionRepository sessionRepository;
    
    public TestUtils(PersonRepository personRepository,  SessionRepository sessionRepository) {
    	this.personRepository = personRepository;
        this.sessionRepository = sessionRepository;
	}
    
	public void createPersons(int nrOfPersons) throws Exception
    {
		TestUtils.nrOfPersons = nrOfPersons;
        for ( int i = 0; i < nrOfPersons; i++ )
        {    	
            Person person = personRepository.createPerson( "person#" + i);
            //Set long term context
            person.setLongTermCtx(Person.NAME, "person#" + i);
            person.setLongTermCtx(Person.COLLAB_APPS, new String[] { "chat" });
            System.out.println("Person#" +i+" created" );
        }
    }
	
    public void deleteSocialGraph()
    {
//    	clearDirectory( new File("target/PersonsGraphDb"));
        for ( Person person : personRepository.getAllPersons() )
        {
            personRepository.deletePerson( person );
        }
    }

	public void setupFriendsBetweenPeople(String ctxType)
    {
        for ( Person person : personRepository.getAllPersons() )
        {
//                person.addFriend( getRandomPerson() );
        	Map<Person, Integer> persons = personRepository.getPersonWithSimilarCtx(person, ctxType);
			for (Map.Entry<Person, Integer> entry : persons.entrySet()) {
				//Similarity Formula is: (similar ctx/ personA + similar ctx/personB) / 2
				float weight = ContextAnalyzer.personCtxSimilarity(entry.getValue(), ctxType, entry.getKey(), person);
        		person.addFriend(entry.getKey(),weight);  
			}
        }
    }

	/**
	 * 
	 */
	  public void createMockShortTermCtx()
	  {
	    Map<String, String> shortTermCtx = new HashMap<String, String>();
	    for (Person friend : this.personRepository.getAllPersons()) {
	      shortTermCtx.put(ShortTermCtxTypes.STATUS, getRandomStatus());
	      shortTermCtx.put(ShortTermCtxTypes.LOCATION, getRandomLocation());
	      friend.addContextStatus(shortTermCtx, this.sessionRepository);
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
                + r.nextInt(TestUtils.nrOfPersons) );
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
	

//	/**
//	 * @param person
//	 * @return
//	 */
//	private Person getRandomFriendOf(Person person) {
//        ArrayList<Person> friends = new ArrayList<Person>();
//        IteratorUtil.addToCollection( person.getFriends().iterator(), friends );
//        return friends.get( r.nextInt( friends.size() ) );
//	}
//
//	/**
//	 * @return
//	 */
//	private Person getRandomPersonWithFriends() {
//		Person p;
//        do
//        {
//            p = getRandomPerson();
//        }
//        while ( p.getNrOfFriends() == 0 );
//        return p;
//	}
	
	public void changeLocation(){
		this.createMockShortTermCtx();
	}

	
	public void menu() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		Scanner scan = new Scanner(System.in);
		int menu = 0;
		System.out.println("===========================================");
		System.out.println("|              MENU SELECTION             |");
		System.out.println("===========================================");
		System.out.println("| Options:                                |");
		System.out.println("|   1. Create persons                     |");
		System.out.println("|   2. Setup friends with same interests  |");
		System.out.println("|   3. Reasoning interests                |");
		System.out.println("|   4. Delete social graph                |");
		System.out.println("|   5. Change location                    |");
		System.out.println("|   6. Exit                               |");
		System.out.println("===========================================");

		boolean quit = false;
		do{
			System.out.print(" Select option: ");
			menu = scan.nextInt();
			System.out.println();
			switch(menu) {
			case 1:
				System.out.print(" Number of persons: ");
				int numberOfPersons = scan.nextInt();
				try {
					createPersons(numberOfPersons);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				createMockLongTermCtx();
				createMockShortTermCtx();
				break;
			case 2:
				setupFriendsBetweenPeople(LongTermCtxTypes.INTERESTS);
				break;
			case 3:
				enrichedCtx();
				break;
			case 4:
				deleteSocialGraph();
				break;
			case 5:
				createMockShortTermCtx();
				break;
			case 6:
				quit = true;
				break;
			default:
				System.out.println("Invalid Entry!");
			}
		}
		while (!quit);
	}

	/**
	 * @throws XPathExpressionException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public void enrichedCtx() throws XPathExpressionException, IOException,
			SAXException, ParserConfigurationException {
		ContextAnalyzer ctxRsn = new ContextAnalyzer(personRepository);
		ctxRsn.incrementInterests();
	}
	
//	private static void clearDirectory(File path)
//    {
//        try
//        {
//            FileUtils.deleteRecursively( path );
//        }
//        catch ( IOException e )
//        {
//            if ( GraphDatabaseSetting.osIsWindows() )
//            {
//                System.err.println( "Couldn't clear directory, and that's ok because this is Windows. Next " +
//                		EmbeddedGraphDatabase.class.getSimpleName() + " will get a new directory" );
//                e.printStackTrace();
//            }
//            else
//            {
//                throw new RuntimeException( "Couldn't not clear directory" );
//            }
//        }
//    }

	
}
