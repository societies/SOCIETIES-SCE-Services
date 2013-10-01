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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.identity.IIdentity;
import org.societies.thirdparty.sharedcalendar.CalendarAsyncTask.Task;
import org.societies.thirdparty.sharedcalendar.api.schema.Calendar;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarCleanerUpdater implements Runnable {

	static final Logger log=LoggerFactory.getLogger(CalendarCleanerUpdater.class);
	private SharedCalendar parent;
	private long interval;
	private boolean run;
	
	public CalendarCleanerUpdater(SharedCalendar parent, long interval) {
		log.debug("CalendarCleanerUpdater Starting...");
		this.parent = parent;
		this.interval = interval;
		this.run = true;
	}
	
	public void shutdown(){
		log.debug("Shutting down cleaner...");
		this.run = false;
	}

	@Override
	public void run() {
		log.debug("Initializating our calendar cleaner thread!");
		sleep(1);
		
		checkLocalCis();
		checkForRemoteCis();
		refreshRecommendedEvents();
		
		log.debug("Now running the thread every interval of {} seconds",interval);
		while(run){
			
			try {
				Thread.sleep(interval*1000);
			} catch (InterruptedException e) {
				log.warn("Sleeping was interrupted earlier than expected!");
				e.printStackTrace();
			}
			
			checkForRemoteCis();
			
			if(parent.getContext().isContextChange()){
				log.debug("Context has changed since last check, so we update the recommended events...");
				refreshRecommendedEvents();
				parent.getContext().setContextChange(false);
			} else{
				log.debug("No need to recheck the recommended events, our context hasn't changed!");
			}
			
			if(parent.getRecentEvents().size() > 80){
				log.debug("We clean out events that have passed...");
				Iterator<String> keys = parent.getRecentEvents().keySet().iterator();
				Date now = new Date();
				while(keys.hasNext()){
					Event eventTest = parent.getRecentEvents().get(keys.next());
					if(eventTest.getEndDate().before(now)){
						log.debug("Removing {} from Map.",eventTest.getName());
						keys.remove();
					}
				}
			} else{
				log.debug("Recent Events cache isn't that big yet, so we do nothing...");
			}
			
		}

	}

	/**
	 * 
	 */
	private void checkLocalCis() {
		log.debug("Checking our local, existing CIS...");
		List<ICisOwned> ourCisList = parent.getCisManager().getListOfOwnedCis();
		for(ICisOwned ourCis : ourCisList ){
			log.debug("Checking {} for a Calendar",ourCis.getName());
			try{
				IIdentity cisId = parent.getIIdentityFromJid(ourCis.getCisId());
				Calendar cisCalendar = parent.retrieveCalendar(cisId,parent.getMyId());
				if(cisCalendar == null){
					log.debug(ourCis.getName() + " doesn't have a Calendar, creating.");
					
					String calendarId = parent.createCalendar(ourCis.getName(),cisId);
					if(calendarId != null)
						if(log.isDebugEnabled())
							log.debug("Created calendar for " + ourCis.getName());
				} 
			} catch(Exception ex){
				log.error("Exception while trying to process calendar for CIS {} : {}",ourCis.getName(),ex.getMessage());
				ex.printStackTrace();
			}

		}
		
	}

	private void refreshRecommendedEvents(){
		log.debug("refreshing the recommendedEvents!");
		
		Iterator<Calendar> calendars = parent.getRecentCalendars().values().iterator();
		while(calendars.hasNext())
			parent.processRecommendedEvents(calendars.next());

	}
	
	private void checkForRemoteCis(){
		
		List<ICis> remoteCisList = parent.getCisManager().getRemoteCis();
		List<CalendarAsyncTask> taskList = new ArrayList<CalendarAsyncTask>(remoteCisList.size());
		for(ICis remoteCis: remoteCisList){
			try{
				log.debug("We are subscribed to CIS: {}. Does it have a calendar?",remoteCis.getName());
				IIdentity node = parent.getCommManager().getIdManager().fromJid(remoteCis.getCisId());
				CalendarAsyncTask task = new CalendarAsyncTask(parent, null, node, parent.getMyId(), Task.CALENDAR);
				taskList.add(task);
				SharedCalendar.executor.execute(task);
			} catch(Exception ex){
				log.error("Exception getting calendar for jid {} : {}",remoteCis.getCisId(),ex.getMessage());
			}
			
		}
		
		for(CalendarAsyncTask task: taskList){
			Calendar cisCalendar = task.getCalendarResult();
			
			if(cisCalendar != null){
				log.debug("The CIS {} has a calendar, so we registered for its events!",task.getNode());
			} else{
				log.debug("CIS {} does not have a calendar!",task.getNode());
			}
		}
	}
	
	private void sleep(long time){
		try {
			log.debug("Sleeping for {} miliseconds, started at {}!",time*1000, System.currentTimeMillis());
			Thread.sleep(time*1000);
			log.debug("Sleeping for {} miliseconds, ended at {}!",time*1000, System.currentTimeMillis());
		} catch (InterruptedException e) {
			log.warn("Sleeping was interrupted earlier than expected!");
			e.printStackTrace();
		}
	}
}
