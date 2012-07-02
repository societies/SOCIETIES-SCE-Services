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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.neo4j.helpers.collection.PositionedIterator;

class CheckAllCtxActivityStreamIterator implements Iterator<ContextUpdates> {
    private ArrayList<PositionedIterator<ContextUpdates>> ctxUpdates = new ArrayList<PositionedIterator<ContextUpdates>>();
    private StatusUpdateComparator comparator = new StatusUpdateComparator();

    public CheckAllCtxActivityStreamIterator( Person person )
    {
        for ( Person friend : person.getFriends() )
        {
            Iterator<ContextUpdates> iterator = friend.getStatus().iterator();
            if (iterator.hasNext()) {
                ctxUpdates.add(new PositionedIterator<ContextUpdates>(iterator));
            }
        }

        sort();
    }

    public boolean hasNext()
    {
        return ctxUpdates.size() > 0;
    }

    public ContextUpdates next()
    {
        if ( ctxUpdates.size() == 0 )
        {
            throw new NoSuchElementException();
        }
        // START SNIPPET: getActivityStream
        PositionedIterator<ContextUpdates> first = ctxUpdates.get(0);
        ContextUpdates returnVal = first.current();

        if ( !first.hasNext() )
        {
            ctxUpdates.remove( 0 );
        }
        else
        {
            first.next();
            sort();
        }

        return returnVal;
        // END SNIPPET: getActivityStream
    }

    private void sort()
    {
        Collections.sort( ctxUpdates, comparator );
    }

    public void remove()
    {
        throw new UnsupportedOperationException( "Not supported yet..." );
    }

    private class StatusUpdateComparator implements Comparator<PositionedIterator<ContextUpdates>> {
        public int compare(PositionedIterator<ContextUpdates> a, PositionedIterator<ContextUpdates> b) {
            return a.current().getDate().compareTo(b.current().getDate());
        }
    }
}
