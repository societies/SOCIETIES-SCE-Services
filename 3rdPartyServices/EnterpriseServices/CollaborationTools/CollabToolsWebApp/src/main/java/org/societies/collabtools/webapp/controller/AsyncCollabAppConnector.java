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
package org.societies.collabtools.webapp.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.api.IAsyncCollabAppConnector;

/**
 * Calendar Test
 *
 * @author Christopher Lima
 *
 */
public class AsyncCollabAppConnector implements IAsyncCollabAppConnector {
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncCollabAppConnector.class);
	private String app_name;
	private HashMap<String, String[]> HashMapResults = new HashMap<String, String[]>();


	/**
	 * @return the HashMapResults
	 */
	public HashMap<String, String[]> getHashMapResults() {
		return HashMapResults;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		logger.debug("***************************AsyncCollabAppConnector TEST**************************************");
		HashMapResults = (HashMap<String, String[]>) arg;
		for (Entry<String, String[]> entry : HashMapResults.entrySet()) {
		    logger.debug("Location: {} people: {}",entry.getKey(), Arrays.toString(entry.getValue()));
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.IAsyncCollabAppConnector#getAppName()
	 */
	@Override
	public String getAppName() {
		// TODO Auto-generated method stub
		return this.app_name;
	}


	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.IAsyncCollabAppConnector#setAppName(java.lang.String)
	 */
	@Override
	public void setAppName(String app_name) {
		this.app_name = app_name;
	}

}
