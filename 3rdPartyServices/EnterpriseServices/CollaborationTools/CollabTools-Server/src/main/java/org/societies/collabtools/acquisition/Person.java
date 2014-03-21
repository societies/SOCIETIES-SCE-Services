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
package org.societies.collabtools.acquisition;

import static org.societies.collabtools.acquisition.RelTypes.NEXT;
import static org.societies.collabtools.acquisition.RelTypes.REALTIME_STATUS;
import static org.societies.collabtools.acquisition.RelTypes.SIMILARITY;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.societies.collabtools.runtime.SessionRepository;

public class Person extends Observable implements Comparable<Person>
{
	//	private static final String ID = "id";

	// START SNIPPET: the-node
	private final Node underlyingNode;
	private Index<Node> index;

	public Person(Node personNode)
	{
		this.underlyingNode = personNode;
		this.index = this.underlyingNode.getGraphDatabase().index().forNodes("PersonNodes", MapUtil.stringMap("type", "fulltext", "to_lower_case", "true" ) );

	}

	public Node getUnderlyingNode()
	{
		return underlyingNode;
	}

	// END SNIPPET: the-node

	// START SNIPPET: delegate-to-the-node
	public String getName()
	{
		Object o = underlyingNode.getProperty(LongTermCtxTypes.NAME);
		if (o instanceof String[]) {			
			return Arrays.toString((String[]) o);
		}
		else {
			return (String) o;
		}
	}

	// END SNIPPET: delegate-to-the-node

	public String getLongTermCtx(String property){
		Object o = underlyingNode.getProperty(property.toString(), "");
		if (o instanceof String[]) {			
			return Arrays.toString((String[]) o);
		}
		else {
			return (String) o;
		}
	}

	//Array of ctx
	public String[] getArrayLongTermCtx(String property){
		Object o = this.underlyingNode.getProperty(property, null);
		if (o != null){
			if (o instanceof String[]) {
				return (String[]) o;
			} 
			else {
				return new String[] {o.toString()};
			}
		}
		else {
			//Return empty array if this property not exists
			return null;
		}
	}

	public void setLongTermCtx(String property, String value){
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			underlyingNode.setProperty(property, value);
			index.add(underlyingNode, property, value);
			tx.success();
		}
		finally
		{
			tx.finish();
		}

	}

	public void setLongTermCtx(String property, String[] values){
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();

		//Cleaning array from null and blank words 
		List<String> list = new ArrayList<String>();
		for(String s : values) {
			if(s != null && s.length() > 0) {
				//using trim to remove white space
				list.add(s.trim());
			}
		}
		values = list.toArray(new String[list.size()]);
		System.out.println("Values added: "+getName()+Arrays.toString(values));

		try
		{
			underlyingNode.setProperty(property, values);
			index.add(underlyingNode, property, values);
			//			Example:
			//			index.add( myNode, "Education", new String[] {"Stanford University, Grad School", "Harvard University, MS"} );
			//			index.add( myNode, "Work", new String[] {"Nokia Siemens Networks", "Motorola"} );
			//			Query for it (remember the quote escaping)
			//			index.query( "Education:\"Stanford University, Grad School\" AND Work:Motorola" );
			tx.success();
		}
		finally
		{
			tx.finish();
		}

	}

	@Override
	public int hashCode()
	{
		return underlyingNode.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Person &&
				underlyingNode.equals(((Person)o).getUnderlyingNode());
	}

	@Override
	public String toString()
	{
		return "Person[" + getName() + "]";
	}

	@Override
	public int compareTo(Person o) {
		return this.getName().compareTo(o.getName());
	}

	public void addSimilarityRelationship(Person otherPerson, double weight, String ctxAttribute)
	{
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			if (!this.equals(otherPerson))
			{
				Relationship friendRel = getPersonRelationshipTo(otherPerson, ctxAttribute);
				if (friendRel == null)
				{
					System.out.println("assign weight: "+weight);
					//Creating a dynamic relationship and assigning a weight based on the context attribute type 
					underlyingNode.createRelationshipTo(otherPerson.getUnderlyingNode(), DynamicRelationshipType.withName("SIMILARITY_"+ctxAttribute)).setProperty("weight", weight);
				}
				else if (!friendRel.getProperty("weight").equals(weight)){
					//Updating weight if value is different
					System.out.println("update weight to: "+weight);
					friendRel.setProperty("weight", weight);
				}
				tx.success();
			}
		}
		finally
		{
			tx.finish();
		}
	}

	public Iterable<Relationship> getSimilarityRelationships(String ctxAttribute)
	{
		ArrayList<Relationship> retVal = new ArrayList<Relationship>();
		for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName("SIMILARITY_"+ctxAttribute)))
		{
			retVal.add(rel);
		}
		//return type: //(a) --- REL_TYPE ---> (b)
		return retVal;
	}

	public int getNrOfFriends()
	{
		return IteratorUtil.count(getFriends());
	}

	public Iterable<Person> getFriends()
	{
		return getPersonsByDepth(1);
	}

	public void removeFriend(Person otherPerson)
	{
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			if (!this.equals(otherPerson))
			{
				for (Relationship rel : underlyingNode.getRelationships())
				{
					if (rel != null)
					{
						rel.delete();
					}
				}
				tx.success();
			}
		}
		finally
		{
			tx.finish();
		}
	}

	public Iterable<Person> getFriendsOfFriends()
	{
		return getPersonsByDepth(2);
	}

	public Iterable<Person> getShortestPathTo(Person otherPerson, int maxDepth)
	{
		// use graph algo to calculate a shortest path
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
				Traversal.expanderForTypes(SIMILARITY, Direction.BOTH), maxDepth );

		Path path = finder.findSinglePath( underlyingNode,
				otherPerson.getUnderlyingNode() );
		return createPersonsFromNodes(path);
	}

	public Iterable<Person> getFriendRecommendation(
			int numberOfFriendsToReturn)
			{
		HashSet<Person> friends = new HashSet<Person>();
		IteratorUtil.addToCollection(getFriends(), friends);

		HashSet<Person> friendsOfFriends = new HashSet<Person>();
		IteratorUtil.addToCollection( getFriendsOfFriends(), friendsOfFriends );

		friendsOfFriends.removeAll(friends);

		ArrayList<RankedPerson> rankedFriends = new ArrayList<RankedPerson>();
		for ( Person friend : friendsOfFriends )
		{
			int rank = getNumberOfPathsToPerson(friend);
			rankedFriends.add( new RankedPerson( friend, rank ) );
		}

		Collections.sort(rankedFriends, new RankedComparer());
		trimTo( rankedFriends, numberOfFriendsToReturn );

		return onlyFriend(rankedFriends);
			}

	public Iterable<ShortTermContextUpdates> getStatus()
	{
		Relationship firstStatus = underlyingNode.getSingleRelationship(
				REALTIME_STATUS, Direction.OUTGOING );
		if (firstStatus == null)
		{
			return Collections.emptyList();
		}

		// START SNIPPET: getStatusTraversal
		TraversalDescription traversal = Traversal.description().
				depthFirst().
				relationships(NEXT).
				evaluator(Evaluators.all());
		// END SNIPPET: getStatusTraversal


		return new IterableWrapper<ShortTermContextUpdates, Path>(
				traversal.traverse(firstStatus.getEndNode()) )
				{
			@Override
			protected ShortTermContextUpdates underlyingObjectToObject( Path path )
			{
				return new ShortTermContextUpdates( path.endNode() );
			}
				};
	}

	public ShortTermContextUpdates getLastShortTermUpdate()
	{
		Relationship firstStatus = underlyingNode.getSingleRelationship(
				REALTIME_STATUS, Direction.OUTGOING);
		//Check if status is empty
		if (firstStatus == null)
		{
			//TODO:FIX THIS!!
			return null;
		}
		return new ShortTermContextUpdates(firstStatus.getEndNode());
	}

	public ArrayList<ShortTermContextUpdates> friendLastStatuses()
	{
		ArrayList<ShortTermContextUpdates> status = new ArrayList<ShortTermContextUpdates>();
		for (Person friend : this.getFriends())
		{
			status.add(friend.getLastShortTermUpdate());
		}
		return status;
	}

	@SuppressWarnings("deprecation")
	public Iterator<ShortTermContextUpdates> friendStatuses()
	{
		return new CheckAllCtxActivityStreamIterator(this);
	}

	public void addContextStatus(Map<String, String> shortTermCtx, SessionRepository sessionRep)
	{
		Transaction tx = graphDb().beginTx();
		try
		{       
			ShortTermContextUpdates oldStatus;
			if ( getStatus().iterator().hasNext() )
			{
				oldStatus = getStatus().iterator().next();
			} 
			else
			{
				oldStatus = null;
			}

			Node newStatus = createShortTermCtxNode(shortTermCtx, sessionRep);

			if (oldStatus != null)
			{
				underlyingNode.getSingleRelationship(RelTypes.REALTIME_STATUS, Direction.OUTGOING).delete();
				newStatus.createRelationshipTo(oldStatus.getUnderlyingNode(), RelTypes.NEXT);
			}

			underlyingNode.createRelationshipTo(newStatus, RelTypes.REALTIME_STATUS);  
			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}

	private GraphDatabaseService graphDb()
	{
		return underlyingNode.getGraphDatabase();
	}

	private Node createShortTermCtxNode(Map<String, String> shortTermCtx, SessionRepository sessionRep)
	{
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			Node newCtx = graphDb().createNode();
			//Including date stamp
			SimpleDateFormat formatter = new SimpleDateFormat(ShortTermContextUpdates.DATE_FORMAT);
			newCtx.setProperty(ShortTermContextUpdates.DATE, formatter.format(new Date().getTime()));

			ShortTermContextUpdates oldStatus = getLastShortTermUpdate();
			boolean contextChanged = false;
			if (oldStatus != null) {
				Node lastNodeStatus = oldStatus.getUnderlyingNode();
				for (String propertyKey : getLastShortTermUpdate().getUnderlyingNode().getPropertyKeys()) {
					if (!propertyKey.equals(ShortTermContextUpdates.DATE)) {
						newCtx.setProperty(propertyKey, lastNodeStatus.getProperty(propertyKey) );
					}
				}
			} 
			for (Map.Entry<String, String> entry : shortTermCtx.entrySet()) {
				String shortTermCtxType = entry.getKey().toString();
				String shortTermValue = entry.getValue().toString();
				contextChanged = contextHasChanged(shortTermCtxType, shortTermValue);
				newCtx.setProperty(shortTermCtxType, shortTermValue);
			}
			//Check location changes; First status, second location
			if (contextChanged){
				this.addObserver(sessionRep);
				setChanged();
				notifyObservers(this);
			}
			tx.success();
			return newCtx;
		}
		finally
		{
			tx.finish();
		}
	}

	/**
	 * @param contextType 
	 * @param context
	 * @return
	 */
	private boolean contextHasChanged(final String contextType, String context) {
		ShortTermContextUpdates ctxStatus= this.getLastShortTermUpdate();
		//Verify if previous shortTermctx node exists
		if (ctxStatus != null && !ctxStatus.getShortTermCtx(contextType).isEmpty()) {
			//Verify if the new property to compare exists in old shortTermctx node 
			String propValue= ctxStatus.getShortTermCtx(contextType);
			if (propValue != null && !context.equals(propValue)) {
				System.out.println(ctxStatus.getPerson() + " had context: " + contextType+" "+ propValue + " and now has context: " + context);
				return true;
			}
		}
		return false;
	}

	private final class RankedPerson
	{
		final Person person;
		final int rank;

		private RankedPerson(Person person, int rank)
		{
			this.person = person;
			this.rank = rank;
		}

		public Person getPerson()
		{
			return person;
		}
		public int getRank()
		{
			return rank;
		}

	}

	private class RankedComparer implements Comparator<RankedPerson>
	{
		public int compare(RankedPerson a, RankedPerson b)
		{
			return b.getRank() - a.getRank();
		}
	}

	private void trimTo(ArrayList<RankedPerson> rankedFriends,
			int numberOfFriendsToReturn)
	{
		while (rankedFriends.size() > numberOfFriendsToReturn)
		{
			rankedFriends.remove(rankedFriends.size() - 1);
		}
	}

	private Iterable<Person> onlyFriend(Iterable<RankedPerson> rankedFriends)
	{
		ArrayList<Person> retVal = new ArrayList<Person>();
		for (RankedPerson person : rankedFriends)
		{
			retVal.add(person.getPerson());
		}
		return retVal;
	}

	public Relationship getPersonRelationshipTo(Person otherPerson, String ctxAttribute)
	{
		Node otherNode = otherPerson.getUnderlyingNode();
		for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName("SIMILARITY_"+ctxAttribute)))
		{
			if (rel.getOtherNode(underlyingNode).equals(otherNode))
			{
				return rel;
			}
		}
		return null;
	}
	
	private Iterable<Person> getPersonsByDepth(int depth)
	{
		// return all my friends and their friends using new traversal API
		TraversalDescription travDesc = Traversal.description()
				.breadthFirst()
				.uniqueness( Uniqueness.NODE_GLOBAL )
				.evaluator( Evaluators.toDepth(depth))
				.evaluator( Evaluators.excludeStartPosition() );

		return createPersonsFromPath(travDesc.traverse(underlyingNode) );
	}

	private IterableWrapper<Person, Path> createPersonsFromPath(Traverser iterableToWrap)
	{
		return new IterableWrapper<Person, Path>(iterableToWrap)
				{
			@Override
			protected Person underlyingObjectToObject(Path path)
			{
				return new Person(path.endNode());
			}
				};
	}

	private int getNumberOfPathsToPerson(Person otherPerson)
	{
		PathFinder<Path> finder = GraphAlgoFactory.allPaths( Traversal.expanderForTypes(SIMILARITY, Direction.BOTH), 2 );
		Iterable<Path> paths = finder.findAllPaths( getUnderlyingNode(), otherPerson.getUnderlyingNode() );
		return IteratorUtil.count( paths );
	}

	private Iterable<Person> createPersonsFromNodes(final Path path)
	{
		return new IterableWrapper<Person, Node>(path.nodes()){
			@Override
			protected Person underlyingObjectToObject(Node node)
			{
				return new Person(node);
			}
		};
	}

	public void setCollabApps(String[] collabApps)
	{
		Transaction tx = this.underlyingNode.getGraphDatabase().beginTx();
		try
		{
			this.underlyingNode.setProperty(LongTermCtxTypes.COLLAB_APPS, collabApps);
			index.add(this.underlyingNode, LongTermCtxTypes.COLLAB_APPS, collabApps);
			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}

	public String[] getCollabApps()
	{
		return (String[])this.underlyingNode.getProperty(LongTermCtxTypes.COLLAB_APPS);
	}

	public void addSession(String sessionName)
	{
		//Fix this, has session is not a long term variable
		if (!getUnderlyingNode().hasProperty(LongTermCtxTypes.HAS_SESSION)) {
			setLongTermCtx(LongTermCtxTypes.HAS_SESSION, new String[] { sessionName });
		}
		else
		{
			setLongTermCtx(LongTermCtxTypes.HAS_SESSION, new String[] { sessionName });
		}
	}

}
