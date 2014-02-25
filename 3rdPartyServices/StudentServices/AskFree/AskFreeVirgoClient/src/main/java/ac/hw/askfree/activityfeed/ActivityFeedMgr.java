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
package ac.hw.askfree.activityfeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;


/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class ActivityFeedMgr implements IActivityFeedAF{
	
	private static Logger log = LoggerFactory.getLogger(ActivityFeedMgr.class);
	
	private ICisManager cisManager;
	private IActivityFeedManager activityFeedManager;
	private ICommManager commManager;
	
	public ActivityFeedMgr(){
		log.debug("public constructor of ActivityFeed");
	}
	
	public void init(){
		log.debug("init method of ActivityFeed");
		
	}
	
	public void postToActivityFeed(String actor, String verb, String object, String target){
		IActivityFeed activityFeed = null;
		log.debug("creating activity feed");
		activityFeed = this.activityFeedManager.getOrCreateFeed(actor, actor, false);
		
		IActivity notifyActivity = activityFeed.getEmptyIActivity();
		
		notifyActivity.setActor(actor);
		notifyActivity.setVerb(verb);
		notifyActivity.setObject(object);
		notifyActivity.setTarget(target);
		log.debug("activity feed created");
		
		log.debug("Inserting activity into CIS");
		activityFeed.addActivity(notifyActivity,new IActivityFeedCallback() {

			@Override
			public void receiveResult(MarshaledActivityFeed activityFeedObject) {
				log.debug("Added an activity to the Activity Feed.");
				
			}
			
		});

		log.debug("Checking that cis has activity");
		activityFeed.getActivities("0 " + Long.toString(System.currentTimeMillis()), new IActivityFeedCallback(){

			@Override
			public void receiveResult(MarshaledActivityFeed activityFeedObject) {
				log.debug(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().toString());
			}

		});
	}
	

	/**
	 * @return the cisManager
	 */
	public ICisManager getCisManager() {
		return cisManager;
	}

	/**
	 * @param cisManager the cisManager to set
	 */
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	/**
	 * @return the activityFeedManager
	 */
	public IActivityFeedManager getActivityFeedManager() {
		log.debug("getActivityFeedManager(): " + activityFeedManager);
		return activityFeedManager;
	}

	/**
	 * @param activityFeedManager the activityFeedManager to set
	 */
	public void setActivityFeedManager(IActivityFeedManager activityFeedManager) {
		this.activityFeedManager = activityFeedManager;
		log.debug("setActivityFeedManager(): " + activityFeedManager);
	}

	/**
	 * @return the commManager
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
}

























