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
package org.societies.enterprise.collabtools.acquisition;

import static org.societies.enterprise.collabtools.acquisition.RelTypes.A_PERSON;
import static org.societies.enterprise.collabtools.acquisition.RelTypes.REF_PERSONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.Activator;
import org.societies.enterprise.collabtools.runtime.SessionRepository;

import scala.actors.threadpool.Arrays;

public class PersonRepository
{
    private final GraphDatabaseService graphDb;
    private final Index<Node> index;
    private final Node personRefNode;
	private SessionRepository sessionRep;
	
	private static final Logger logger  = LoggerFactory.getLogger(PersonRepository.class);

    public PersonRepository(GraphDatabaseService graphDb, Index<Node> index)
    {
        this.graphDb = graphDb;
        this.index = index;

        personRefNode = getPersonsRootNode(graphDb);
    }

    private Node getPersonsRootNode(GraphDatabaseService graphDb)
    {
        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                REF_PERSONS, Direction.OUTGOING );
        if ( rel != null )
        {
            return rel.getEndNode();
        } else
        {
            Transaction tx = this.graphDb.beginTx();
            try
            {
                Node refNode = this.graphDb.createNode();
                this.graphDb.getReferenceNode().createRelationshipTo( refNode,
                        REF_PERSONS );
                tx.success();
                return refNode;
            }
            finally
            {
                tx.finish();
            }
        }
    }

    public Person createPerson(String name) throws Exception
    {
        // to guard against duplications we use the lock grabbed on ref node
        // when
        // creating a relationship and are optimistic about person not existing
        Transaction tx = graphDb.beginTx();
        try
        {
            Node newPersonNode = graphDb.createNode();
            personRefNode.createRelationshipTo( newPersonNode, A_PERSON );
            // lock now taken, we can check if  already exist in index
            Node alreadyExist = index.get( Person.NAME, name ).getSingle();
            if ( alreadyExist != null )
            {
                tx.failure();
                throw new Exception( "Person with this name already exists " );
            }
            newPersonNode.setProperty( Person.NAME, name );
            index.add( newPersonNode, Person.NAME, name );
            tx.success();
            Person person = new Person( newPersonNode );
            //TODO:VERIFIY OBSERVER
            logger.info("Person added: "+name);
            return person;
        }
        finally
        {
            tx.finish();
        }

    }

    public Person getPersonByName( String name )
    {
        Node personNode = index.get( Person.NAME, name ).getSingle();
        if ( personNode == null )
        {
            throw new IllegalArgumentException( "Person[" + name
                    + "] not found" );
        }
        return new Person( personNode );
    }
    
    public boolean hasPerson(String name)
    {
        Node personNode = index.get( Person.NAME, name ).getSingle();
        if (personNode == null)
        	return false;
        return true;
    }
    
    public Map<Person, Integer> getPersonWithSimilarInterests(Person self)
    {
    	Map<Person,Integer> persons = new HashMap<Person, Integer>();
    	for ( Person person : getAllPersons() )
    	{
        	int counter = 0;
    		if (!self.equals(person)) {
    			List<String> personInterest = Arrays.asList(person.getInterests());
    			for (String match : self.getInterests()) {    			
    				if (personInterest.contains(match)) {
    					counter++;
    					persons.put(person, counter);
    				}
    			}
    		}

    	}
    	return persons;
    }
    
    public Person[] getPersonsByProperty(String property, String value)
    {
    	List<Person> persons = new ArrayList<Person>();
    	//E.g. LongTermCtxTypes.COMPANY
    	for ( Node personNode : index.query(property, value) )
    	{
    		Person person = new Person(personNode);
    		persons.add(person);
    	}
//        IndexManager indexManager =  graphDb.index();
//        Index<Node> index = indexManager.forNodes("company",MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext"));
//        IndexHits<Node> hits = index.query("company", new QueryContext("*"));
//        Node personNode = index.get( Person.NAME, name ).getSingle();
//        if ( personNode == null )
//        {
//            throw new IllegalArgumentException( "Property from Person[" + name
//                    + "] not found" );
//        }
//        Person person = new Person( personNode );
//        return person.getInterest(property);
		return (Person[]) persons.toArray();
    }

    public void deletePerson(Person person)
    {
        Transaction tx = graphDb.beginTx();
        try
        {
            Node personNode = person.getUnderlyingNode();
            index.remove( personNode, Person.NAME, person.getName() );
            for ( Person friend : person.getFriends() )
            {
                person.removeFriend( friend );
            }
            personNode.getSingleRelationship( A_PERSON, Direction.INCOMING ).delete();

            for ( ShortTermContextUpdates status : person.getStatus() )
            {
                Node statusNode = status.getUnderlyingNode();
                for ( Relationship r : statusNode.getRelationships() )
                {
                    r.delete();
                }
                statusNode.delete();
            }

            personNode.delete();
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    public Iterable<Person> getAllPersons()
    {
        return new IterableWrapper<Person, Relationship>(
                personRefNode.getRelationships( A_PERSON ) )
        {
            @Override
            protected Person underlyingObjectToObject( Relationship personRel )
            {
                return new Person( personRel.getEndNode() );
            }
        };
    }
}
