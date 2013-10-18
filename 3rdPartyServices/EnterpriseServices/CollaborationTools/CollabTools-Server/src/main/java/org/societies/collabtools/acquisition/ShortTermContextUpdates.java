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
import static org.societies.collabtools.acquisition.RelTypes.STATUS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.Traversal;

public class ShortTermContextUpdates extends Observable
{
    private final Node underlyingNode;
    static final String DATE = "date";
	static final String DATE_FORMAT = "HH:mm:ss dd-MM-yyyy";

    public ShortTermContextUpdates(Node underlyingNode)
    {

        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode()
    {
        return underlyingNode;
    }

    public Person getPerson()
    {
        return new Person(getPersonNode());
    }

    private Node getPersonNode()
    {
        TraversalDescription traversalDescription = Traversal.description().
                depthFirst().
                relationships(NEXT, Direction.INCOMING).
                relationships(STATUS, Direction.INCOMING).
                evaluator(Evaluators.includeWhereLastRelationshipTypeIs(STATUS));

        Traverser traverser = traversalDescription.traverse( getUnderlyingNode() );

        return IteratorUtil.singleOrNull(traverser.iterator()).endNode();
    }

    public String getShortTermCtx(final String property)
    {
        return (String)underlyingNode.getProperty(property, "");
    }

    
    public Date getDate()
    {
        String strdate = (String)underlyingNode.getProperty(DATE);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date date = null;
		try {
			date = (Date)formatter.parse(strdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
            System.err.println("Date property is null");
			e.printStackTrace();
		}
        return date;
    }

}
