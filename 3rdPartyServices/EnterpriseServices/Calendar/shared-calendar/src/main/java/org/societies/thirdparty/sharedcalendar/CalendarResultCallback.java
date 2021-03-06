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
package org.societies.thirdparty.sharedcalendar;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdparty.sharedcalendar.api.ICalendarResultCallback;
import org.societies.thirdparty.sharedcalendar.api.schema.SharedCalendarResult;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarResultCallback implements ICalendarResultCallback {

	static final Logger logger = LoggerFactory.getLogger(CalendarResultCallback.class);
	private final long TIMEOUT = 45;
	
	private BlockingQueue<SharedCalendarResult> resultList;
	
	public CalendarResultCallback() {
		
		resultList = new ArrayBlockingQueue<SharedCalendarResult>(1);
		if(logger.isDebugEnabled())
			logger.debug("CalendarResultCallback created");	
	}

	@Override
	public void receiveResult(SharedCalendarResult returnValue) {
		logger.debug("receivedResult: {}", returnValue);
		
		try {
			if(returnValue != null)
				resultList.put(returnValue);
			else{
				SharedCalendarResult testCal = new SharedCalendarResult();
				testCal.setCalendarId("n/a");
				resultList.put(testCal);
			}
		} catch (InterruptedException e) {
			logger.error("Error putting result in List");
			e.printStackTrace();
		}
		
	}
	
	@Override
	public SharedCalendarResult getResult(){
		try {
			SharedCalendarResult myResult = resultList.poll(TIMEOUT, TimeUnit.SECONDS);
			if(myResult != null && !(myResult.getCalendarId() != null && myResult.getCalendarId().equals("n/a")))
				return myResult;
			else
				return null;
		} catch (InterruptedException e) {
			logger.error("Error getting result in List");
			e.printStackTrace();
			return null;
		}
	}

}
