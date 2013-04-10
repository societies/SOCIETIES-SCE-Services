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
package org.societies.enterprise.collabtools.runtime;

import static org.societies.enterprise.collabtools.acquisition.RelTypes.NEXT;
import static org.societies.enterprise.collabtools.acquisition.RelTypes.STATUS;

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
import org.neo4j.kernel.Traversal;
import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.RelTypes;
import org.societies.enterprise.collabtools.api.ICollabApps;

/**
 * Class responsible to provide the session structure
 *
 * @author cviana
 *
 */
public class Session {

	    static final String DATE = "date";
		static final String DATE_FORMAT = "HH:mm:ss dd-MM-yyyy";
		public static final String SESSION = "session";
		public static final String ID = "id";
		public static final String MEMBERS = "members";
		public static final String ROLE = "role";
		public static final String CHAIR = "id";
		public static final String LISTENER = "listener";
		private final Node underlyingNode;
		private ICollabApps collabApps;

		Session(Node sessionNode, CollabApps collabApps)
		{
			this.underlyingNode = sessionNode;
			this.collabApps = collabApps;
		}

		public Node getUnderlyingNode()
		{
			return this.underlyingNode;
		}

		public Iterator<Person> getMembers()
		{
			Index<Node> index = this.underlyingNode.getGraphDatabase().index().forNodes("SessionNodes");
			Iterator<Node> personNode = index.get(LongTermCtxTypes.NAME, this.getSessionName()).iterator();
			ArrayList<Person> persons = new ArrayList<Person>();
			while (personNode.hasNext()) {
				persons.add(new Person(personNode.next()));
			}
			return persons.iterator();
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

		public String getSessionName()
		{
			return (String)this.underlyingNode.getProperty(SESSION);
		}

		public void addMember(Person member, String role)
		{
			Index<Node> index = this.underlyingNode.getGraphDatabase().index().forNodes("SessionNodes");
			Transaction tx = this.underlyingNode.getGraphDatabase().beginTx();
			try
			{
				System.out.println("Adding member to session " + getSessionName() + ": " + member.getName());
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
				
				this.inviteMember(member);

				tx.success();
			}
			finally
			{
				tx.finish();
			}
		}
		
		public synchronized void removeMember(Person member)
		{
			Index<Node> index = this.underlyingNode.getGraphDatabase().index().forNodes("SessionNodes");
			Transaction tx = this.underlyingNode.getGraphDatabase().beginTx();
			try
			{
				Iterator<Node> personNode = index.get(LongTermCtxTypes.NAME, this.getSessionName()).iterator();
				Node personNodeToDelete = null;
				Node temp = null;


				while (personNode.hasNext()) {
					temp = (Node)personNode.next();
					if (member.getName().equals(new Person(temp).getName())) {
						personNodeToDelete = temp;
						break;
					}
				}
				if (personNodeToDelete == null)
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
				System.out.println("List of members in session history after person leaves: " +Arrays.toString(membersList.toArray(new String[0])));
				ctxSessionHistory.put(Session.MEMBERS, membersList.toArray(new String[0]));
				Node sessionHistoryNode = this.addSessionHistoryStatus(ctxSessionHistory);
				
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

		private void inviteMember(Person member)
		{
			String[] collabAppsAvailables = member.getArrayLongTermCtx(LongTermCtxTypes.COLLAB_APPS);
			this.collabApps.sendInvite(member.getName(), collabAppsAvailables, getSessionName());
		}
		
		private void kickMember(Person member)
		{
			String[] collabAppsAvailables = member.getArrayLongTermCtx(LongTermCtxTypes.COLLAB_APPS);
			this.collabApps.sendKick(member.getName(), collabAppsAvailables, getSessionName());
		}

		public boolean equals(Object o)
		{
			return ((o instanceof Person)) && 
					(this.underlyingNode.equals(((Person)o).getUnderlyingNode()));

		}

	    public Node addSessionHistoryStatus(Map ctxSessionHistory)
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

	            if (oldStatus != null){
	                underlyingNode.getSingleRelationship(RelTypes.STATUS, Direction.OUTGOING).delete();
	                newStatus.createRelationshipTo(oldStatus.getUnderlyingNode(), RelTypes.NEXT );
	            }

	            underlyingNode.createRelationshipTo(newStatus, RelTypes.STATUS);  
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
	    			STATUS, Direction.OUTGOING );
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
	    private Node createNewSessionHistoryNode(Map<String, ?> ctxSessionHistory)
	    {
	        Node newCtx = this.underlyingNode.getGraphDatabase().createNode();
	        for (Map.Entry entry : ctxSessionHistory.entrySet()) {
	            newCtx.setProperty((String)entry.getKey(), entry.getValue());
	          }
	        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
	        newCtx.setProperty(DATE, formatter.format(new Date().getTime()));
	        return newCtx;
	    }
	    
	    public Node getLastSessionHistoryStatus()
	    {
	    	Relationship firstStatus = underlyingNode.getSingleRelationship(
	    			STATUS, Direction.OUTGOING);
	    	//Check status is empty
	    	if (firstStatus == null)
	    	{
//	    		Node newStatus = createNewSessionHistoryNode(new HashMap<String, Object>()); 
//	    	    underlyingNode.createRelationshipTo(newStatus, RelTypes.STATUS);
//				return newStatus;
	    		throw new IllegalArgumentException("Status not found!");
	    	}
	    	return firstStatus.getEndNode();
	    }

	}
