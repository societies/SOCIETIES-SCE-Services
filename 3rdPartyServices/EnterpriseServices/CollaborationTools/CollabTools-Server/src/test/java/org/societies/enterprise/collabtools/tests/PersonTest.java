/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.societies.enterprise.collabtools.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.IteratorUtil;
import org.societies.enterprise.collabtools.TestUtils;
import org.societies.enterprise.collabtools.acquisition.ContextSubscriber;
import org.societies.enterprise.collabtools.acquisition.ShortTermContextUpdates;
import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.PersonRepository;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.enterprise.collabtools.runtime.CollabApps;
import org.societies.enterprise.collabtools.runtime.SessionRepository;

public class PersonTest
{
	private static final Random r = new Random( System.currentTimeMillis() );
	private static GraphDatabaseService graphDb, sessionGraphDb;
	private static Index<Node> index;
	private static Index<Node> indexStatus;
	private static PersonRepository personRepository;
	private static SessionRepository sessionRepository;
	private static int nrOfPersons;

	@BeforeClass
	public static void setup() throws Exception
	{
		int random = new Random().nextInt(100);
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "target/persontestdb"  + random);
		sessionGraphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "target/sessiontestdb"  + random);
		index = graphDb.index().forNodes( "nodes" );
		indexStatus = graphDb.index().forNodes( "status" );
		personRepository = new PersonRepository( graphDb, index );
		sessionRepository = new SessionRepository( sessionGraphDb,index, new CollabApps(null) );
		registerShutdownHook();

	}

	@Before
	public void doBefore() throws Exception
	{
		deleteSocialGraph();
		nrOfPersons = 4;
		createPersons();
		setupFriendsBetweenPeople( 10 );
	}

	@AfterClass
	public static void teardown()
	{
		graphDb.shutdown();
	}

	@Test
	public void addStatusAndRetrieveIt() throws Exception
	{
		Map<String, String> shortTermCtx = new HashMap<String, String>();
		for (int i = 0; i < 20; i++) {
			Person person = getRandomPerson();
			shortTermCtx.put(ShortTermCtxTypes.STATUS, "Testing");
			shortTermCtx.put(ShortTermCtxTypes.LOCATION, "School");

			person.addContextStatus(shortTermCtx, null);
			shortTermCtx.clear();

			ShortTermContextUpdates update = person.getStatus().iterator().next();
			assertThat(update, CoreMatchers.<Object> notNullValue());
			assertThat(update.getShortTermCtx(ShortTermCtxTypes.STATUS), equalTo("Testing!"));
			assertThat(update.getPerson(), equalTo(person));
			System.out.println("Person: "+person.getName()+" Status: "+update.getShortTermCtx(ShortTermCtxTypes.STATUS));
		}
	}

	//    @Test
	//    public void YouMightKnow() throws Exception
	//    {
	//    	String[] features = new String[] { "name", "company" };
	//        Person person = getRandomPerson();
	//    	YouMightKnow mightKnow = new YouMightKnow( person, features, 2 );
	//    	List<List<Node>> suggestions = mightKnow.result;
	//        printMightKnow( suggestions, features );
	////        Person recommendation = IteratorUtil.single( person.getFriendRecommendation( 1 ).iterator() );
	////        printMightKnow( mightKnow.findYouMightKnow(person) , features );
	//    }

	@Test
	public void multipleStatusesComeOutInTheRightOrder() throws Exception
	{
		ArrayList<String> statuses = new ArrayList<String>();
		statuses.add( "Test1" );
		statuses.add( "Test2" );
		statuses.add( "Test3" );

		Person person = getRandomPerson();
		Map<String, String> shortTermCtx = new HashMap<String, String>();
		for ( String status : statuses )
		{
			shortTermCtx.put(ShortTermCtxTypes.STATUS, status);
			shortTermCtx.put(ShortTermCtxTypes.LOCATION, "location");
			person.addContextStatus(shortTermCtx, null );
			shortTermCtx.clear();
		}

		int i = statuses.size();
		for ( ShortTermContextUpdates update : person.getStatus() )
		{
			i--;
			assertThat( update.getShortTermCtx(ShortTermCtxTypes.STATUS), equalTo( statuses.get( i ) ) );
			System.out.println("Person: "+person.getName()+" Status: "+update.getShortTermCtx(ShortTermCtxTypes.STATUS));
		}
	}

	@Test
	public void removingOneFriendIsHandledCleanly()
	{
		Person person1 = personRepository.getPersonByName( "person#1" );
		Person person2 = personRepository.getPersonByName( "person#2" );
		person1.addFriend( person2, 1 );

		int noOfFriends = person1.getNrOfFriends();

		person1.removeFriend( person2 );

		int noOfFriendsAfterChange = person1.getNrOfFriends();

		assertThat( noOfFriends, equalTo( noOfFriendsAfterChange + 1 ) );
	}

	@Test
	public void retrieveStatusUpdatesInDateOrder() throws Exception
	{
		Person person = getRandomPersonWithFriends();
		int numberOfStatuses = 20;

		Map<String, String> shortTermCtx = new HashMap<String, String>();

		for ( int i = 0; i < numberOfStatuses; i++ )
		{
			Person friend = getRandomFriendOf(person);
			shortTermCtx.put(ShortTermCtxTypes.STATUS, "Dum-deli-dum...");
			shortTermCtx.put(ShortTermCtxTypes.LOCATION, "location");
			friend.addContextStatus(shortTermCtx , null );
			shortTermCtx.clear();
		}

		ArrayList<ShortTermContextUpdates> updates = fromIterableToArrayList( person.friendStatuses() );
		assertThat( updates.size(), equalTo( numberOfStatuses ) );
		assertUpdatesAreSortedByDate( updates );
	}

	@Test
	public void friendsOfFriendsWorks() throws Exception
	{
		Person person = getRandomPerson();
		Person friend = getRandomFriendOf( person );

		for ( Person friendOfFriend : friend.getFriends() )
		{
			if ( !friendOfFriend.equals( person ) )
			{ // You can't be friends with yourself.
				//                assertThat( person.getFriendsOfFriends(), hasItems( friendOfFriend ) );
			}
		}
	}

	@Test
	public void shouldReturnTheCorrectPersonFromAnyStatusUpdate() throws Exception
	{
		Person person = getRandomPerson();
		Map<String, String> shortTermCtx = new HashMap<String, String>();
		shortTermCtx.put(ShortTermCtxTypes.LOCATION, null);

		shortTermCtx.put(ShortTermCtxTypes.STATUS, "Foo");
		person.addContextStatus(shortTermCtx, null );
		shortTermCtx.clear();

		shortTermCtx.put(ShortTermCtxTypes.STATUS, "Bar");
		person.addContextStatus(shortTermCtx, null );
		shortTermCtx.clear();

		shortTermCtx.put(ShortTermCtxTypes.STATUS, "Baz");
		person.addContextStatus(shortTermCtx, null );

		for(ShortTermContextUpdates status : person.getStatus())
		{
			assertThat(status.getPerson(), equalTo( person ));
			System.out.println("Person: "+status.getPerson()+" Status: "+status.getShortTermCtx(ShortTermCtxTypes.STATUS));
		}

		//        for ( Node node : index.query( "name", "*e*" ) )
		//        {
		//            // This will return
		//        	Person p = new Person(node);
		//        	System.out.println(p.getName());
		//        	System.out.println(p.getCompany());
		//        }

		for ( Node node : indexStatus.query( ShortTermCtxTypes.STATUS, "*o*" ) )
		{
			// This will return
			ShortTermContextUpdates s = new ShortTermContextUpdates(node);
			System.out.println(s.getShortTermCtx(ShortTermCtxTypes.STATUS));
		}
	}

	@Test
	public void getPathBetweenFriends() throws Exception
	{
		deleteSocialGraph();
		Person start = personRepository.createPerson( "start"  );
		start.setLongTermCtx(LongTermCtxTypes.WORK, "company" );
		Person middleMan1 = personRepository.createPerson( "middle1" );
		middleMan1.setLongTermCtx(LongTermCtxTypes.WORK, "company" );
		Person middleMan2 = personRepository.createPerson( "middle2" );
		middleMan2.setLongTermCtx(LongTermCtxTypes.WORK, "company" );
		Person endMan = personRepository.createPerson( "endMan");
		endMan.setLongTermCtx(LongTermCtxTypes.WORK, "company" );

		// Start -> middleMan1 -> middleMan2 -> endMan

		start.addFriend( middleMan1, 0 );
		middleMan1.addFriend( middleMan2, 0 );
		middleMan2.addFriend( endMan, 0 );

		Iterable<Person> path = start.getShortestPathTo( endMan, 4 );

		assertPathIs( path, start, middleMan1, middleMan2, endMan );
		//assertThat( path, matchesPathByProperty(Person.NAME, "start", "middle1", "middle2", "endMan"));
	}

	@Test
	public void singleFriendRecommendation() throws Exception
	{
		deleteSocialGraph();
		Person a = personRepository.createPerson( "a" );
		Person b = personRepository.createPerson( "b" );
		Person c = personRepository.createPerson( "c" );
		Person d = personRepository.createPerson( "d" );
		Person e = personRepository.createPerson( "e" );

		// A is friends with B,C and D
		a.addFriend( b, 0 );
		a.addFriend( c, 0 );
		a.addFriend( d, 0 );

		// E is also friend with B, C and D
		e.addFriend( b, 0 );
		e.addFriend( c, 0 );
		e.addFriend( d, 0 );

		Person recommendation = IteratorUtil.single( a.getFriendRecommendation( 1 ).iterator() );

		assertThat( recommendation, equalTo( e ) );
	}

	@Test
	public void weightedFriendRecommendation() throws Exception
	{
		deleteSocialGraph();
		Person a = personRepository.createPerson( "a" );
		Person b = personRepository.createPerson( "b" );
		Person c = personRepository.createPerson( "c" );
		Person d = personRepository.createPerson( "d" );
		Person e = personRepository.createPerson( "e" );
		Person f = personRepository.createPerson( "f" );


		// A is friends with B,C and D
		a.addFriend( b, 0 );
		a.addFriend( c, 0 );
		a.addFriend( d, 0 );

		// E is only friend with B
		e.addFriend( b, 0 );

		// F is friend with B, C, D
		f.addFriend( b, 0 );
		f.addFriend( c, 0 );
		f.addFriend( d, 0 );

		ArrayList<Person> recommendations = fromIterableToArrayList( a.getFriendRecommendation( 2 ).iterator() );

		//Recommend F to A
		assertThat( recommendations.get( 0 ), equalTo( f ));
		//Recommend E to A
		assertThat( recommendations.get( 1 ), equalTo( e ));
	}

	static void printMightKnow( final List<List<Node>> suggestions,
			final String[] properties )
	{
		for ( List<Node> suggestion : suggestions )
		{
			List<String> matchingProps = new ArrayList<String>();
			for ( String property : properties )
			{
				if ( propSame( suggestion, property ) )
				{
					matchingProps.add( property );
				}
			}
			System.out.println( name( suggestion.get( 0 ) ) + " might know "
					+ name( suggestion.get( suggestion.size() - 1 ) ) + " through "
					+ name( suggestion.subList( 1, suggestion.size() - 1 ) ) );
			System.out.println( ", matching features: "
					+ Arrays.toString( matchingProps.toArray() ) );
		}
	}

    //TODO:
	@Test
	public void contextSubscribe() throws Exception
	{
		TestUtils test = new TestUtils(personRepository, sessionRepository);
//		test.createPersons(1);
		test.createMockLongTermCtx();
		test.createMockShortTermCtx();

		ContextSubscriber ctxSub = new ContextSubscriber(personRepository, sessionRepository);
		//format: model type, string ctx value, person
		String[] arg = {"locationSymbolic","Funfair","person#0"};
		ctxSub.update(null, arg);
	}

	private static String name( final Node node )
	{
		return (String) node.getProperty( Person.NAME );
	}

	private static String name( final List<Node> nodes )
	{
		String[] names = new String[nodes.size()];
		for ( int i = 0; i < names.length; i++ )
		{
			names[i] = (String) nodes.get( i ).getProperty( Person.NAME );
		}
		return Arrays.toString( names );
	}

	private static boolean propSame( final List<Node> nodes,
			final String propName )
	{
		Object value = nodes.get( 0 ).getProperty( propName );
		for ( int i = 1; i < nodes.size(); i++ )
		{
			if ( !value.equals( nodes.get( i ).getProperty( propName ) ) )
			{
				return false;
			}
		}
		return true;
	}

	private <T> ArrayList<T> fromIterableToArrayList( Iterator<T> iterable )
	{
		ArrayList<T> collection = new ArrayList<T>();
		IteratorUtil.addToCollection( iterable, collection );
		return collection;
	}

	private void assertPathIs( Iterable<Person> path,
			Person... expectedPath )
	{
		ArrayList<Person> pathArray = new ArrayList<Person>();
		IteratorUtil.addToCollection( path, pathArray );
		assertThat( pathArray.size(), equalTo( expectedPath.length ) );
		for ( int i = 0; i < expectedPath.length; i++ )
		{
			assertThat( pathArray.get( i ), equalTo( expectedPath[ i ] ) );
		}
	}

	private static void setupFriendsBetweenPeople( int maxNrOfFriendsEach )
	{
		for ( Person person : personRepository.getAllPersons() )
		{
			int nrOfFriends = r.nextInt( maxNrOfFriendsEach ) + 1;
			for ( int j = 0; j < nrOfFriends; j++ )
			{
				person.addFriend( getRandomPerson(), j );
			}
		}
	}

	private static Person getRandomPerson()
	{
		return personRepository.getPersonByName( "person#"
				+ r.nextInt( nrOfPersons ) );
	}

	private void deleteSocialGraph()
	{
		for ( Person person : personRepository.getAllPersons() )
		{
			personRepository.deletePerson( person );
		}
	}

	private Person getRandomFriendOf( Person p )
	{
		ArrayList<Person> friends = new ArrayList<Person>();
		IteratorUtil.addToCollection( p.getFriends().iterator(), friends );
		return friends.get( r.nextInt( friends.size() ) );
	}

	private Person getRandomPersonWithFriends()
	{
		Person p;
		do
		{
			p = getRandomPerson();
		}
		while ( p.getNrOfFriends() == 0 );
		return p;
	}

	private static void createPersons() throws Exception
	{
		for ( int i = 0; i < nrOfPersons; i++ )
		{
			personRepository.createPerson( "person#" + i );
			System.out.println("person#" +i );
		}
	}

	private void assertUpdatesAreSortedByDate(
			ArrayList<ShortTermContextUpdates> statusUpdates )
	{
		Date date = new Date( 0 );
		for ( ShortTermContextUpdates update : statusUpdates )
		{
			org.junit.Assert.assertTrue( date.getTime() < update.getDate().getTime() );
			// TODO: Should be assertThat(date, lessThan(update.getDate));
		}
	}

	private static void registerShutdownHook()
	{
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime()
		.addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}
}