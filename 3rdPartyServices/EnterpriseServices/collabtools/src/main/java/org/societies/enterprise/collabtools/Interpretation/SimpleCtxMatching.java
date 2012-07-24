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
package org.societies.enterprise.collabtools.Interpretation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.enterprise.collabtools.acquisition.Person;
import org.societies.enterprise.collabtools.acquisition.ShortTermCtxTypes;
import org.societies.enterprise.collabtools.acquisition.ContextUpdates;

/**
 * Describe your class here...
 *
 * @author cviana
 *
 */
public class SimpleCtxMatching {
	
	private static Logger log = LoggerFactory.getLogger(SimpleCtxMatching.class);
//	private Index<Node> indexStatus;
	// Start with ten, expand by ten when limit reached
	private Hashtable<String, HashSet<Person>> hashCtxList = new Hashtable<String, HashSet<Person>>(10,10);

	/**
	 * @param indexStatus
	 */
	public SimpleCtxMatching(Index<Node> indexStatus) {
//		this.indexStatus = indexStatus;
	}

	/**
	 * 
	 */
	public SimpleCtxMatching() {
		// TODO Auto-generated constructor stub
	}

	public synchronized Hashtable<String, HashSet<Person>> getPersonsWithMatchingCtx(final String ctxAtributte, HashSet<ContextUpdates> statusHashset) {
		//Compare symbolic location
		ContextUpdates [] statusUpdateArray = new ContextUpdates[statusHashset.size()];
		statusHashset.toArray(statusUpdateArray);
		if (ctxAtributte.equals(ShortTermCtxTypes.LOCATION)){
			return  getUniqueElements(statusUpdateArray, ShortTermCtxTypes.LOCATION);
		}
		//For others short term context types
		return  getUniqueElements(statusUpdateArray, ctxAtributte);	
	}

	
	/**
	 * @param unique
	 * @param statusList
	 * @return
	 */
	private int[] getDuplicateIndices(ContextUpdates[] unique, ContextUpdates[] statusList) {
        int[] indices = new int[unique.length];
        for(int j = 0; j < unique.length; j++) {
            for(int k = 0; k < statusList.length; k++) {
                if(unique[j].equals(statusList[k]))
                    indices[j]++;
            }
        }
        return indices;
	}

	/**
	 * @param statusList
	 * @return
	 */
	private Hashtable<String, HashSet<Person>>  getUniqueElements(ContextUpdates[] statusArray, final String ctxAttribute) {
		ContextUpdates[] temp = new ContextUpdates[statusArray.length]; // null elements
		System.out.println("Number of persons: "+temp.length);
		int count = 0;
		if (ctxAttribute.equals(ShortTermCtxTypes.LOCATION)) { 
			for(int j = 0; j < statusArray.length; j++) {
				if(isSameLocation(statusArray[j], temp))
					temp[count++] = statusArray[j];
			}
		}
		return hashCtxList;
		
//		StatusUpdate[] uniqueStrs = new StatusUpdate[count];
//		System.arraycopy(temp, 0, uniqueStrs, 0, count);
//		return uniqueStrs;
	}
	
	private boolean isSameLocation(ContextUpdates status, ContextUpdates[] temp) {
		HashSet<Person> listTemp;
		listTemp = hashCtxList.get(status.getLocation());
    	for(int j = 0; j < temp.length; j++) {
    		if(temp[j] != null && status.getLocation().equals(temp[j].getLocation())) {
    			if (listTemp==null) {
    				//has first element?
    				listTemp = new HashSet<Person>();
        			listTemp.add(temp[j].getPerson());
        			System.out.println("temp[j] "+j+temp[j].getPerson()+temp[j].getLocation());
    			}
				listTemp.add(status.getPerson());
				System.out.println("status "+status.getPerson()+status.getLocation());
    			hashCtxList.put(status.getLocation(), listTemp);
    			return false;
    		}
    	}
    	return true;
	}

	/**
	 * @param statusText
	 * @param temp
	 * @return
	 */
	private boolean isUnique(String statusText, ContextUpdates[] temp) {
        for(int j = 0; j < temp.length; j++) {
            if(temp[j] != null && statusText.equals(temp[j].getStatusText()))
                return false;
        }
        return true;
	}

	public static <T> List<T> getDuplicate(Collection<T> list) {

		final List<T> duplicatedObjects = new ArrayList<T>();
		Set<T> set = new HashSet<T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean add(T e) {
				if (contains(e)) {
					duplicatedObjects.add(e);
				}
				return super.add(e);
			}
		};
		for (T t : list) {
			set.add(t);
		}
		return duplicatedObjects;
	}

	public static <T> boolean hasDuplicate(Collection<T> list) {
		if (getDuplicate(list).isEmpty())
			return false;
		return true;
	}
	
	public static boolean checkDuplicate(List<ContextUpdates> list) {
		HashSet<String> set = new HashSet<String>();
		for (int i = 0; i < list.size(); i++) {
			boolean val = set.add(list.get(i).getStatusText());
			if (val == false) {
				return val;
			}
		}
		return true;
	}

}
