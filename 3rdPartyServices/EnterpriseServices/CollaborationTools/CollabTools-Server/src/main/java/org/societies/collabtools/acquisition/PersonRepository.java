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

import static org.societies.collabtools.acquisition.RelTypes.A_PERSON;
import static org.societies.collabtools.acquisition.RelTypes.A_PERSON_REMOVED;
import static org.societies.collabtools.acquisition.RelTypes.REF_PERSONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 */
public class PersonRepository
{
    /**
     * Field graphDb.
     */
    private final GraphDatabaseService graphDb;
    /**
     * Field indexPerson.
     */
    private final Index<Node> indexPerson;
    /**
     * Field personRefNode.
     */
    private final Node personRefNode;
	
	/**
	 * Field logger.
	 */
	private static final Logger logger  = LoggerFactory.getLogger(PersonRepository.class);

    /**
     * Constructor for PersonRepository.
     * @param graphDb GraphDatabaseService
     */
    public PersonRepository(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
        this.indexPerson = this.graphDb.index().forNodes("PersonNodes", MapUtil.stringMap("type", "fulltext", "to_lower_case", "true" ) );

        personRefNode = getPersonsRootNode(graphDb);
    }

    /**
     * Method getPersonsRootNode.
     * @param graphDb GraphDatabaseService
     * @return Node
     */
    private Node getPersonsRootNode(GraphDatabaseService graphDb) {
        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                REF_PERSONS, Direction.OUTGOING );
        if ( null != rel )
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

    /**
     * Method createPerson.
     * @param name String
     * @return Person
     * @throws Exception
     */
    public Person createPerson(String name) throws Exception {
        // to guard against duplications we use the lock grabbed on ref node
        // when
        // creating a relationship and are optimistic about person not existing
        Transaction tx = graphDb.beginTx();
        try
        {
            Node newPersonNode = graphDb.createNode();
            personRefNode.createRelationshipTo(newPersonNode, A_PERSON);
            // lock now taken, we can check if  already exist in index
            Node alreadyExist = indexPerson.query(LongTermCtxTypes.NAME, name).getSingle();
            if (null != alreadyExist)
            {
                tx.failure();
                throw new Exception("Person with this name already exists: "+alreadyExist.getProperty(LongTermCtxTypes.NAME));
            }
            newPersonNode.setProperty(LongTermCtxTypes.NAME, name);
            indexPerson.add(newPersonNode, LongTermCtxTypes.NAME, name);
            tx.success();
            Person person = new Person(newPersonNode);
            //TODO:VERIFIY OBSERVER
            logger.info("Person added: "+name);
            return person;
        }
        finally
        {
            tx.finish();
        }

    }

    /**
     * Method getPersonByName.
     * @param name String
     * @return Person
     */
    public Person getPersonByName(String name) {
        Node personNode = indexPerson.query(LongTermCtxTypes.NAME, name).getSingle();
        if ( null == personNode )
        {
            throw new IllegalArgumentException( "Person[" + name
                    + "] not found" );
        }
        return new Person(personNode);
    }
    
    /**
     * Method hasPerson.
     * @param name String
     * @return boolean
     */
    public boolean hasPerson(String name) {
        Node personNode = indexPerson.query(LongTermCtxTypes.NAME, name).getSingle();
        if (null == personNode)
        	return false;
        return true;
    }
    
    /**
     * Method getPersonWithSimilarCtx.
     * @param self Person
     * @param property String
     * @return Map<Person,Integer>
     */
    public Map<Person, Integer> getPersonWithSimilarCtx(Person self, String property) {
    	Map<Person,Integer> persons = new HashMap<Person, Integer>();
    	for (Person person : getAllPersons())
    	{
    		logger.info("Comparing {} with {}" ,self, person.getName());
//			if (self.getArrayLongTermCtx(property) == null || person.getArrayLongTermCtx(property) == null)
//				continue;
        	int counter = 0;
    		if (!self.equals(person)) {
    			List<String> personCtx = Arrays.asList(person.getArrayLongTermCtx(property));
    			for (String match : self.getArrayLongTermCtx(property)) {    			
    				if (personCtx.contains(match)) {
    					counter++;
    					persons.put(person, counter);
    				}
    			}
    		}

    	}
    	return persons;
    }
    
    /**
     * Method getPersonsByProperty.
     * @param property String
     * @param value String
     * @return Person[]
     */
    public Person[] getPersonsByProperty(String property, String value) {
    	List<Person> persons = new ArrayList<Person>();
    	//E.g. LongTermCtxTypes.COMPANY
    	for (Node personNode : indexPerson.query(property, value))
    	{
    		Person person = new Person(personNode);
    		persons.add(person);
    	}
//        IndexManager indexManager =  graphDb.index();
//        Index<Node> index = indexManager.forNodes("company",MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext"));
//        IndexHits<Node> hits = index.query("company", new QueryContext("*"));
//        Node personNode = index.get( LongTermCtxTypes.NAME, name ).getSingle();
//        if ( personNode == null )
//        {
//            throw new IllegalArgumentException( "Property from Person[" + name
//                    + "] not found" );
//        }
//        Person person = new Person( personNode );
//        return person.getInterest(property);
		return persons.toArray(new Person[persons.size()]);
    }

    /**
     * Method deletePerson.
     * @param person Person
     */
    public void deletePerson(Person person) {
        Transaction tx = graphDb.beginTx();
        try
        {
            Node personNode = person.getUnderlyingNode();
            indexPerson.remove( personNode, LongTermCtxTypes.NAME, person.getName() );
            for (Person friend : person.getFriends())
            {
                person.removeFriend(friend);
            }
            personNode.getSingleRelationship(A_PERSON, Direction.INCOMING).delete();
            personRefNode.createRelationshipTo(personNode, A_PERSON_REMOVED);

//            for (ShortTermContextUpdates status : person.getStatus())
//            {
//                Node statusNode = status.getUnderlyingNode();
//                for (Relationship r : statusNode.getRelationships())
//                {
//                    r.delete();
//                }
//                statusNode.delete();
//            }
//
//            personNode.delete();
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    /**
     * Method getAllPersons.
     * @return Iterable<Person>
     */
    public Iterable<Person> getAllPersons() {
        return new IterableWrapper<Person, Relationship>(
                personRefNode.getRelationships(A_PERSON) )
        {
            @Override
            protected Person underlyingObjectToObject(Relationship personRel)
            {
                return new Person(personRel.getEndNode());
            }
        };
    }
    
    /**
     * Method getRelationships.
     * @param relationshipType String
     * @return Iterable<Relationship>
     */
    public Iterable<Relationship> getRelationships(String relationshipType) {
    	Iterable<Relationship> relationships = GlobalGraphOperations.at(graphDb).getAllRelationships();
    	ArrayList<Relationship> results = new ArrayList<Relationship>();
    	//Check for static enum
    	for (RelTypes rel : RelTypes.values()){
    		if (rel.name().equals(relationshipType)){
    			for(Relationship relationship : relationships)
    	        {
    	            if (relationship.isType(rel))
    	            {
    	            	results.add(relationship);
    	            }
    	        }
    			return results;
    		}
    	}
    	//Check for dynamic relationship
    	for(Relationship relationship : relationships)
        {
            if (relationship.isType(DynamicRelationshipType.withName(relationshipType)))
            {
            	results.add(relationship);
            }
        }
		return results;
    }
    
    /**
     * Method size.
     * @return int
     */
    public int size() {
    	int size = 0;
    	for(@SuppressWarnings("unused") Person person : this.getAllPersons()) {
    		size++;
    	}
    	return size;
    }
}
