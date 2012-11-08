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
package org.societies.enterprise.collabtools.acquisition;

import static org.societies.enterprise.collabtools.acquisition.RelTypes.KNOWS;
import static org.societies.enterprise.collabtools.acquisition.RelTypes.NEXT;
import static org.societies.enterprise.collabtools.acquisition.RelTypes.STATUS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
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
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.societies.enterprise.collabtools.runtime.SessionRepository;

public class Person extends Observable
{
    public static final String NAME = "name";
    public static final String ID = "id";

    // START SNIPPET: the-node
    private final Node underlyingNode;

    Person( Node personNode )
    {
        this.underlyingNode = personNode;
    }

    public Node getUnderlyingNode()
    {
        return underlyingNode;
    }

    // END SNIPPET: the-node

    // START SNIPPET: delegate-to-the-node
    public String getName()
    {
        return (String)underlyingNode.getProperty( NAME );
    }

    // END SNIPPET: delegate-to-the-node
    
	public String getLongTermCtx(String property){
		return (String) underlyingNode.getProperty( property, "" );
	}
	
	//Array of interests
	public String[] getInterests(){
		return (String[]) underlyingNode.getProperty( LongTermCtxTypes.INTERESTS );
	}
	
	public void setLongTermCtx(final String property, String value){
		Index<Node> index = underlyingNode.getGraphDatabase().index().forNodes("PersonNodes");
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			underlyingNode.setProperty( property, value );
			index.add( underlyingNode, property, value );
            tx.success();
        }
        finally
        {
            tx.finish();
        }

	}
	
	public void setLongTermCtx(final String property, String[] values){
		Index<Node> index = underlyingNode.getGraphDatabase().index().forNodes("PersonNodes");
		Transaction tx = underlyingNode.getGraphDatabase().beginTx();
		try
		{
			underlyingNode.setProperty( LongTermCtxTypes.INTERESTS, values );
			index.add( underlyingNode, LongTermCtxTypes.INTERESTS, values );
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

    // START SNIPPET: override
    @Override
    public int hashCode()
    {
        return underlyingNode.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        return o instanceof Person &&
                underlyingNode.equals( ( (Person)o ).getUnderlyingNode() );
    }

    @Override
    public String toString()
    {
        return "Person[" + getName() + "]";
    }

    // END SNIPPET: override

    public void addFriend( Person otherPerson, float weight )
    {
        Transaction tx = underlyingNode.getGraphDatabase().beginTx();
        try
        {
            if ( !this.equals( otherPerson ) )
            {
                Relationship friendRel = getFriendRelationshipTo( otherPerson );
                if ( friendRel == null )
                {
                    underlyingNode.createRelationshipTo( otherPerson.getUnderlyingNode(), KNOWS ).setProperty("weight", weight);
                }
                tx.success();
            }
        }
        finally
        {
            tx.finish();
        }
    }

    public int getNrOfFriends()
    {
        return IteratorUtil.count( getFriends() );
    }

    public Iterable<Person> getFriends()
    {
        return getFriendsByDepth( 1 );
    }

    public void removeFriend( Person otherPerson )
    {
        Transaction tx = underlyingNode.getGraphDatabase().beginTx();
        try
        {
            if ( !this.equals( otherPerson ) )
            {
                Relationship friendRel = getFriendRelationshipTo( otherPerson );
                if ( friendRel != null )
                {
                    friendRel.delete();
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
        return getFriendsByDepth( 2 );
    }

    public Iterable<Person> getShortestPathTo( Person otherPerson,
                                               int maxDepth )
    {
        // use graph algo to calculate a shortest path
        PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
                Traversal.expanderForTypes( KNOWS, Direction.BOTH ), maxDepth );

        Path path = finder.findSinglePath( underlyingNode,
                otherPerson.getUnderlyingNode() );
        return createPersonsFromNodes( path );
    }

    public Iterable<Person> getFriendRecommendation(
            int numberOfFriendsToReturn )
    {
        HashSet<Person> friends = new HashSet<Person>();
        IteratorUtil.addToCollection( getFriends(), friends );

        HashSet<Person> friendsOfFriends = new HashSet<Person>();
        IteratorUtil.addToCollection( getFriendsOfFriends(), friendsOfFriends );

        friendsOfFriends.removeAll( friends );

        ArrayList<RankedPerson> rankedFriends = new ArrayList<RankedPerson>();
        for ( Person friend : friendsOfFriends )
        {
            int rank = getNumberOfPathsToPerson( friend );
            rankedFriends.add( new RankedPerson( friend, rank ) );
        }

        Collections.sort( rankedFriends, new RankedComparer() );
        trimTo( rankedFriends, numberOfFriendsToReturn );

        return onlyFriend( rankedFriends );
    }

    public Iterable<ShortTermContextUpdates> getStatus()
    {
        Relationship firstStatus = underlyingNode.getSingleRelationship(
                STATUS, Direction.OUTGOING );
        if ( firstStatus == null )
        {
            return Collections.emptyList();
        }

        // START SNIPPET: getStatusTraversal
        TraversalDescription traversal = Traversal.description().
                depthFirst().
                relationships( NEXT ).
                evaluator(Evaluators.all());
        // END SNIPPET: getStatusTraversal


        return new IterableWrapper<ShortTermContextUpdates, Path>(
                traversal.traverse( firstStatus.getEndNode() ) )
        {
            @Override
            protected ShortTermContextUpdates underlyingObjectToObject( Path path )
            {
                return new ShortTermContextUpdates( path.endNode() );
            }
        };
    }
    
    public ShortTermContextUpdates getLastStatus()
    {
    	Relationship firstStatus = underlyingNode.getSingleRelationship(
    			STATUS, Direction.OUTGOING );
    	//Check status is empty
    	if ( firstStatus == null )
    	{
    		//TODO:FIX THIS!!
    		return null;
    	}
    	return new ShortTermContextUpdates(firstStatus.getEndNode());
    }
    
    public ArrayList<ShortTermContextUpdates> friendLastStatuses()
    {
    	ArrayList<ShortTermContextUpdates> status = new ArrayList<ShortTermContextUpdates>();
    	for ( Person friend : this.getFriends() )
        {
    		status.add(friend.getLastStatus());
        }
        return status;
    }

    public Iterator<ShortTermContextUpdates> friendStatuses()
    {
        return new CheckAllCtxActivityStreamIterator( this );
    }

    public void addContextStatus( String status, String location, SessionRepository sessionRep )
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
            
            Node newStatus = createNewCtxNode( status, location, sessionRep );

            if ( oldStatus != null )
            {
                underlyingNode.getSingleRelationship( RelTypes.STATUS, Direction.OUTGOING ).delete();
                newStatus.createRelationshipTo( oldStatus.getUnderlyingNode(), RelTypes.NEXT );
            }

            underlyingNode.createRelationshipTo( newStatus, RelTypes.STATUS );  
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

    private Node createNewCtxNode( String text, String location, SessionRepository sessionRep )
    {
        Node newCtx = graphDb().createNode();
        newCtx.setProperty( ShortTermCtxTypes.STATUS, text );
        newCtx.setProperty( ShortTermCtxTypes.LOCATION, location );
        SimpleDateFormat formatter = new SimpleDateFormat(ShortTermContextUpdates.DATE_FORMAT);
        newCtx.setProperty( ShortTermContextUpdates.DATE, formatter.format(new Date().getTime()));
        //Check location changes
        if (contextHasChanged(ShortTermCtxTypes.LOCATION, location)){
            //TODO:Broadcast Observer
            this.addObserver(sessionRep);
            setChanged();
            notifyObservers(this);
        }
        return newCtx;
    }

    /**
	 * @param location 
     * @param location
	 * @return
	 */
    private boolean contextHasChanged(final String context, String location) {
    	ShortTermContextUpdates ctxStatus= getLastStatus();
    	//Check old location with new location
    	if (context.equals(ShortTermCtxTypes.LOCATION))
    		if (ctxStatus != null && !location.equals(ctxStatus.getShortTermCtx(ShortTermCtxTypes.LOCATION))){
    	        System.out.println(ctxStatus.getPerson()+" had location: "+ctxStatus.getShortTermCtx(ShortTermCtxTypes.LOCATION)+" and now has location: "+location);
    			return true;
    		}
    	return false;
    }

	private final class RankedPerson
    {
        final Person person;

        final int rank;

        private RankedPerson( Person person, int rank )
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
        public int compare( RankedPerson a, RankedPerson b )
        {
            return b.getRank() - a.getRank();
        }

    }

    private void trimTo( ArrayList<RankedPerson> rankedFriends,
                         int numberOfFriendsToReturn )
    {
        while ( rankedFriends.size() > numberOfFriendsToReturn )
        {
            rankedFriends.remove( rankedFriends.size() - 1 );
        }
    }

    private Iterable<Person> onlyFriend( Iterable<RankedPerson> rankedFriends )
    {
        ArrayList<Person> retVal = new ArrayList<Person>();
        for ( RankedPerson person : rankedFriends )
        {
            retVal.add( person.getPerson() );
        }
        return retVal;
    }

    public Relationship getFriendRelationshipTo( Person otherPerson )
    {
        Node otherNode = otherPerson.getUnderlyingNode();
        for ( Relationship rel : underlyingNode.getRelationships( KNOWS ) )
        {
            if ( rel.getOtherNode( underlyingNode ).equals( otherNode ) )
            {
                return rel;
            }
        }
        return null;
    }

    private Iterable<Person> getFriendsByDepth( int depth )
    {
        // return all my friends and their friends using new traversal API
        TraversalDescription travDesc = Traversal.description()
                .breadthFirst()
                .relationships( KNOWS )
                .uniqueness( Uniqueness.NODE_GLOBAL )
                .evaluator( Evaluators.toDepth(depth))
                .evaluator( Evaluators.excludeStartPosition() );

        return createPersonsFromPath( travDesc.traverse( underlyingNode ) );
    }

    private IterableWrapper<Person, Path> createPersonsFromPath(
            Traverser iterableToWrap )
    {
        return new IterableWrapper<Person, Path>( iterableToWrap )
        {
            @Override
            protected Person underlyingObjectToObject( Path path )
            {
                return new Person( path.endNode() );
            }
        };
    }

    private int getNumberOfPathsToPerson( Person otherPerson )
    {
        PathFinder<Path> finder = GraphAlgoFactory.allPaths( Traversal.expanderForTypes( KNOWS, Direction.BOTH ), 2 );
        Iterable<Path> paths = finder.findAllPaths( getUnderlyingNode(), otherPerson.getUnderlyingNode() );
        return IteratorUtil.count( paths );
    }

    private Iterable<Person> createPersonsFromNodes( final Path path )
    {
        return new IterableWrapper<Person, Node>( path.nodes() )
        {
            @Override
            protected Person underlyingObjectToObject( Node node )
            {
                return new Person( node );
            }
        };
    }

}
