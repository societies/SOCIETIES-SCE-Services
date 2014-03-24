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
package org.societies.collabtools.runtime;

import static org.societies.collabtools.acquisition.RelTypes.NEXT;
import static org.societies.collabtools.acquisition.RelTypes.REALTIME_STATUS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.acquisition.LongTermCtxTypes;
import org.societies.collabtools.acquisition.Person;
import org.societies.collabtools.acquisition.RelTypes;
import org.societies.collabtools.api.ICollabApps;

/**
 * Class responsible to provide the session structure
 *
 * @author cviana
 *
 */
public class Session {

	private static final Logger logger = LoggerFactory.getLogger(Session.class);


	static final String DATE = "date";
	static final String DATE_FORMAT = "HH:mm:ss dd-MM-yyyy";
	public static final String SESSION = "session";
	public static final String ID = "id";
	public static final String MEMBERS_INVITED = "membersInvited";
	public static final String MEMBERS_PARTICIPATING = "membersParticipating";
	public static final String ROLE = "role";
	public static final String CHAIR = "id";
	public static final String VISITOR = "visitor";
	public static final String LANGUAGE = "language";
	private final Node underlyingNode;
	private ICollabApps collabApps;
	private Index<Node> index;

	Session(Node sessionNode, CollabApps collabApps)
	{
		this.underlyingNode = sessionNode;
		this.collabApps = collabApps;
		//Lucene index
		this.index = this.underlyingNode.getGraphDatabase().index().forNodes("SessionNodes", MapUtil.stringMap("type", "fulltext", "to_lower_case", "true") );
	}

	/**
	 * @param endNode
	 */
//	public Session(Node sessionNode) {
//		this.underlyingNode = sessionNode;
//	}

	public Node getUnderlyingNode()
	{
		return this.underlyingNode;
	}

	public Iterator<Person> getMembers()
	{
		Iterator<Node> personNode = index.query(LongTermCtxTypes.NAME, this.getSessionName()).iterator();
		ArrayList<Person> persons = new ArrayList<Person>();
		while (personNode.hasNext()) {
			persons.add(new Person(personNode.next()));
		}
		return persons.iterator();
		//Searching graph
		//			TraversalDescription travDesc = Traversal.description()
		//					.breadthFirst()
		//					.relationships(RelTypes.PARTICIPATE, Direction.OUTGOING)
		//					.evaluator(Evaluators.excludeStartPosition());
		//
		//			ArrayList<Person> persons = new ArrayList<Person>();
		//			for (Path friendPath : travDesc.traverse(new Node[] { this.getLastSessionHistoryStatus() }))
		//			{
		//				persons.add(new Person(friendPath.endNode()));
		//			}
		//			return persons.iterator();
	}

	public String[] getCollabApp(Person member) {
		return member.getCollabApps();
	}

	public void setCollabApp(Person member, String[] collabApps) {
		member.setCollabApps(collabApps);
	}

	/**
	 * Return the name of the session
	 */
	public String getSessionName()
	{
		String sessioName = (String)this.underlyingNode.getProperty(SESSION);
		return sessioName.toLowerCase();
	}

	/**
	 * Set the language of the session
	 */
	public void setLanguage(String language)
	{
		logger.debug("Language set: {}",language);
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			underlyingNode.setProperty(LANGUAGE, language);
			//TODO: create index?
			//index.add(underlyingNode, property, language);
			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}

	/**
	 * Get the language of the session
	 */
	public String getLanguage()
	{
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			String language = (String)this.underlyingNode.getProperty(LANGUAGE);
			tx.success();
			return language;
		}
		finally
		{
			tx.finish();
		}
	}
	
	/**
	 * Get members that really accepted the invitation to join a session
	 */
	public String[] getMembersParticipating()
	{
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			String[] membersParticipating  = (String[]) this.getLastSessionHistoryStatus().getProperty(Session.MEMBERS_PARTICIPATING, null);
			tx.success();
			return membersParticipating;
		}
		finally
		{
			tx.finish();
		}
	}
	
	public String[] getMembersInterests()
	{
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			String[] membersParticipating  = (String[]) this.getLastSessionHistoryStatus().getProperty(LongTermCtxTypes.INTERESTS, null);
			tx.success();
			return membersParticipating;
		}
		finally
		{
			tx.finish();
		}
	}

	/**
	 * Include members that really accepted the invitation to join a session
	 * 
	 * @param membersParticipating Array of members to include in LastSessionHistoryStatus
	 */
	public void setMembersParticipating(String[] membersParticipating)
	{
		Map<String, String[]> ctxSessionHistory = new HashMap<String, String[]>();
		ctxSessionHistory.put(MEMBERS_PARTICIPATING, membersParticipating);
		this.addSessionHistoryStatus(ctxSessionHistory);
//		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
//		try
//		{
//			this.getLastSessionHistoryStatus().setProperty(MEMBERS_PARTICIPATING, membersParticipating);
//			tx.success();
//		}
//		finally
//		{
//			tx.finish();
//		}
	}

	/**
	 * Add member to join a session
	 * @param member name of the member
	 * @param role Role possibles are CHAIR and VISITOR
	 */
	public void addMember(Person member, String role, String msg)
	{
		Transaction tx = this.underlyingNode.getGraphDatabase().beginTx();
		try
		{
			logger.debug("Adding member to session {} : {}", getSessionName(), member.getName());
			Node tempNode = this.underlyingNode.getGraphDatabase().createNode();

			for (String key : member.getUnderlyingNode().getPropertyKeys()) {
				tempNode.setProperty(key, member.getUnderlyingNode().getProperty(key));
			}
			index.add(tempNode, LongTermCtxTypes.NAME, this.getSessionName());

			//				//Insert members in session history..
			//				List<String> membersList = new ArrayList<String>();
			//				Iterator<Person> personIt = this.getMembers();
			//				while (personIt.hasNext()) {
			//					membersList.add(personIt.next().getName());
			//				}
			//				HashMap<String, String[]> ctxSessionHistory = new HashMap<String, String[]>();
			//				ctxSessionHistory.put(Session.MEMBERS, membersList.toArray(new String[0]));

			//Finally add to the status node
			//				getLastSessionHistoryStatus().createRelationshipTo(tempNode, RelTypes.PARTICIPATE).setProperty(Session.ROLE, role);

			this.inviteMember(member, msg);

			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}

	/**
	 * 
	 * @param member
	 */
	public synchronized void removeMember(Person member)
	{
		Transaction tx = this.underlyingNode.getGraphDatabase().beginTx();
		try
		{
			Iterator<Node> personNode = index.query(LongTermCtxTypes.NAME, this.getSessionName()).iterator();
			Node personNodeToDelete = null;
			Node temp = null;


			while (personNode.hasNext()) {
				temp = personNode.next();
				if (member.getName().equals(new Person(temp).getName())) {
					personNodeToDelete = temp;
					break;
				}
			}
			if (null == personNodeToDelete)
			{
				throw new IllegalArgumentException("Person[" + member.getName() + "] not found");
			}

			index.remove(personNodeToDelete, LongTermCtxTypes.NAME, this.getSessionName());


			//Updating members in session history...
			List<String> membersList = new ArrayList<String>();
			Iterator<Person> personIt = this.getMembers();
			while (personIt.hasNext()) {
				membersList.add(personIt.next().getName());
			}
			HashMap<String, String[]> ctxSessionHistory = new HashMap<String, String[]>();
			logger.debug("List of members in session history after person leaves: {}",Arrays.toString(membersList.toArray(new String[0])));
			ctxSessionHistory.put(Session.MEMBERS_INVITED, membersList.toArray(new String[0]));
//			Node sessionHistoryNode = this.addSessionHistoryStatus(ctxSessionHistory);

			//				personNode = index.get(Person.NAME, this.getSessionName()).iterator();
			//				while (personNode.hasNext()) {
			//					temp = (Node)personNode.next();
			//					Iterable<Relationship> rel = temp.getRelationships();
			//					String property = null;
			//					for (Relationship relationship : rel) {
			//						property = (String) relationship.getProperty(ROLE);
			//					 }			
			//					sessionHistoryNode.createRelationshipTo(temp, RelTypes.PARTICIPATE).setProperty(Session.ROLE, property);
			//				}
			//
			//				System.out.println("Person " + member.getName() + " removed from session " + this.getSessionName());

			this.kickMember(member);

			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}

	//		private void inviteMembers()
	//		{
	//			Iterator<Person> it = getMembers();
	//			Set<String> membersSet = new HashSet<String>();
	//			while (it.hasNext()) {
	//				Person member = (Person)it.next();
	//				membersSet.add(member.getName());
	//				inviteMember(member);
	//			}
	//			Map<String, String[]> ctxSessionHistory = new HashMap<String, String[]>();
	//			System.out.println("membersSet.toArray(new String[0]" +Arrays.toString(membersSet.toArray(new String[0])));
	//			ctxSessionHistory.put(MEMBERS, membersSet.toArray(new String[0]));
	//			addSessionHistoryStatus(ctxSessionHistory);
	//		}

	private void inviteMember(Person member, String msg)
	{
		String[] collabAppsAvailables = member.getArrayLongTermCtx(LongTermCtxTypes.COLLAB_APPS);
		this.collabApps.sendInvite(member.getName(), collabAppsAvailables, getSessionName(), getLanguage(), msg);
	}

	private void kickMember(Person member)
	{
		String[] collabAppsAvailables = member.getArrayLongTermCtx(LongTermCtxTypes.COLLAB_APPS);
		this.collabApps.sendKick(member.getName(), collabAppsAvailables, getSessionName());
	}


	public Node addSessionHistoryStatus(Map<String, String[]> ctxSessionHistory)
	{
		Transaction tx = this.underlyingNode.getGraphDatabase().beginTx();
		Node newStatus;
		try
		{
			SessionHistory oldStatus;
			if (getHistoryStatus().iterator().hasNext()){
				oldStatus = getHistoryStatus().iterator().next();
			} 
			else {
				oldStatus = null;
			}

			newStatus = createNewSessionHistoryNode(ctxSessionHistory);

			if (null != oldStatus){
				underlyingNode.getSingleRelationship(RelTypes.REALTIME_STATUS, Direction.OUTGOING).delete();
				newStatus.createRelationshipTo(oldStatus.getUnderlyingNode(), RelTypes.NEXT );
			}

			underlyingNode.createRelationshipTo(newStatus, RelTypes.REALTIME_STATUS);  
			tx.success();
		}
		finally{
			tx.finish();
		}
		return newStatus;
	}

	/**
	 * @return
	 */
	 private Iterable<SessionHistory> getHistoryStatus() {
		Relationship firstStatus = underlyingNode.getSingleRelationship(
				REALTIME_STATUS, Direction.OUTGOING );
		if (null == firstStatus)
		{
			return Collections.emptyList();
		}

		// START SNIPPET: getStatusTraversal
		TraversalDescription traversal = Traversal.description().
				depthFirst().
				relationships(NEXT).
				evaluator(Evaluators.all());
		// END SNIPPET: getStatusTraversal


		return new IterableWrapper<SessionHistory, Path>(
				traversal.traverse(firstStatus.getEndNode()) )
				{
			@Override
			protected SessionHistory underlyingObjectToObject(Path path)
			{
				return new SessionHistory(path.endNode());
			}
				};
	 }

	 //Session history node model
	 private Node createNewSessionHistoryNode(Map<String, String[]> ctxSessionHistory)
	 {
		 Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		 try
		 {
			 Node newCtx = this.underlyingNode.getGraphDatabase().createNode();
			 //Including date stamp
			 SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			 newCtx.setProperty(DATE, formatter.format(new Date().getTime()));

			 Node lastNodeStatus = getLastSessionHistoryStatus();
			 if (null != lastNodeStatus) {
				 for (String propertyKey : lastNodeStatus.getPropertyKeys()) {
					 if (!propertyKey.equals(DATE)) {
						 newCtx.setProperty(propertyKey, lastNodeStatus.getProperty(propertyKey) );
					 }
				 }
			 } 

			 for (Map.Entry<String, String[]> entry : ctxSessionHistory.entrySet()) {
				 newCtx.setProperty(entry.getKey(), entry.getValue());
			 }
			 tx.success();
			 return newCtx;
		 }
		 finally
		 {
			 tx.finish();
		 }
	 }

	 public Node getLastSessionHistoryStatus()
	 {
		 Relationship firstStatus = underlyingNode.getSingleRelationship(
				 REALTIME_STATUS, Direction.OUTGOING);
		 //Check status is empty
		 if (null == firstStatus)
		 {
			 //	    		Node newStatus = createNewSessionHistoryNode(new HashMap<String, Object>()); 
			 //	    	    underlyingNode.createRelationshipTo(newStatus, RelTypes.STATUS);
			 //				return newStatus;
//			 throw new IllegalArgumentException("Status not found!");
			 return null;
		 }
		 return firstStatus.getEndNode();
	 }
	 
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((null == underlyingNode) ? 0 : underlyingNode.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if ( this == obj )  
				return true;
			if (obj instanceof Session)
				return true;
			if (this == obj)
				return true;
			if (null == obj)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Session other = (Session) obj;
			if (null == underlyingNode) {
				if (null != other.underlyingNode)
					return false;
			} else if (!underlyingNode.equals(other.underlyingNode))
				return false;
			return true;
		}

}
