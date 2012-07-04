package org.societies.enterprise.collabtools.Interpretation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.societies.enterprise.collabtools.acquisition.LongTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.RelTypes;

public class YouMightKnow {
	List<List<Node>> result = new ArrayList<List<Node>>();
	int maxDistance;
	Set<Node> buddies = new HashSet<Node>();
	Node personNode;

	public YouMightKnow( Person person, String[] features, int maxDistance )
	{
		personNode = person.getUnderlyingNode();
		this.maxDistance = maxDistance;
		for ( Relationship rel : personNode.getRelationships( RelTypes.KNOWS ) )
		{
			buddies.add( rel.getOtherNode( personNode ) );
		}
		findFriends( personNode, Arrays.asList( new Node[] { personNode } ), Arrays.asList( features ), 1 );

	}

	private void findFriends(Node rootNode, List<Node> path, List<String> interests, int depth) {
		for ( Relationship rel : rootNode.getRelationships( RelTypes.KNOWS ) )
		{
			Node personNode = rel.getOtherNode( rootNode );
			if ( (depth > 1 && buddies.contains( personNode )) || path.contains( personNode ) )
			{
				continue;
			}
			List<String> newMatches = new ArrayList<String>();
			for ( String match : interests )
			{
				//TODO: fix!!
				if ( personNode.getProperty(LongTermCtxTypes.INTERESTS).equals( rootNode.getProperty(LongTermCtxTypes.INTERESTS) ) )
				{
					newMatches.add( match );
				}
			}
			if ( newMatches.size() > 0 )
			{
				List<Node> newPath = new ArrayList<Node>( path );
				newPath.add( personNode );
				if ( depth > 1 )
				{
					result.add( newPath );
				}
				if ( depth != maxDistance )
				{
					findFriends( personNode, newPath, newMatches, depth + 1 );
				}
			}
		}
	}
	
	//TODO:Test this code
	  public List<List<Node>> findYouMightKnow( final Person person )
	    {
	        List<List<Node>> suggestions = new ArrayList<List<Node>>();
	        Set<Node> buddies = new HashSet<Node>();
	        personNode = person.getUnderlyingNode();

	        Object f1 = personNode.getProperty( Person.NAME );
	        Object f2 = personNode.getProperty( LongTermCtxTypes.COMPANY );
	        for ( Relationship rel : personNode
	            .getRelationships( RelTypes.KNOWS ) )
	        {
	            buddies.add( rel.getOtherNode( personNode ) );
	        }

	        for ( Node buddyNode : buddies )
	        {
	            boolean buddyF1 = f1.equals( buddyNode.getProperty( Person.NAME ) );
	            boolean buddyF2 = f2.equals( buddyNode.getProperty( LongTermCtxTypes.COMPANY ) );
	            if ( !buddyF1 && !buddyF2 )
	            {
	                continue;
	            }
	            for ( Relationship nextRel : buddyNode
	                .getRelationships( RelTypes.KNOWS ) )
	            {
	                Node mightKnow = nextRel.getOtherNode( buddyNode );
	                if ( mightKnow.equals( personNode ) || buddies.contains( mightKnow ) )
	                {
	                    continue;
	                }
	                boolean allF1 = buddyF1
	                    && f1.equals( mightKnow.getProperty( Person.NAME ) );
	                boolean allF2 = buddyF2
	                    && f2.equals( mightKnow.getProperty( LongTermCtxTypes.COMPANY ) );
	                if ( allF1 || allF2 )
	                {
	                    suggestions.add( Arrays.asList( new Node[] { personNode,
	                        buddyNode, mightKnow } ) );
	                }
	            }
	        }
	        return suggestions;
	    }
	  
	  public static void printMightKnow( final List<List<Node>> suggestions,
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


}
