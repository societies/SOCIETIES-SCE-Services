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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.neo4j.helpers.collection.PositionedIterator;

class CheckAllCtxActivityStreamIterator implements Iterator<ShortTermContextUpdates> {
    private ArrayList<PositionedIterator<ShortTermContextUpdates>> ctxUpdates = new ArrayList<PositionedIterator<ShortTermContextUpdates>>();
    private StatusUpdateComparator comparator = new StatusUpdateComparator();

    public CheckAllCtxActivityStreamIterator( Person person )
    {
        for ( Person friend : person.getFriends() )
        {
            Iterator<ShortTermContextUpdates> iterator = friend.getStatus().iterator();
            if (iterator.hasNext()) {
                ctxUpdates.add(new PositionedIterator<ShortTermContextUpdates>(iterator));
            }
        }

        sort();
    }

    public boolean hasNext()
    {
        return ctxUpdates.size() > 0;
    }

    public ShortTermContextUpdates next()
    {
        if ( ctxUpdates.size() == 0 )
        {
            throw new NoSuchElementException();
        }
        // START SNIPPET: getActivityStream
        PositionedIterator<ShortTermContextUpdates> first = ctxUpdates.get(0);
        ShortTermContextUpdates returnVal = first.current();

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

    private class StatusUpdateComparator implements Comparator<PositionedIterator<ShortTermContextUpdates>> {
        public int compare(PositionedIterator<ShortTermContextUpdates> a, PositionedIterator<ShortTermContextUpdates> b) {
            return a.current().getDate().compareTo(b.current().getDate());
        }
    }
}
