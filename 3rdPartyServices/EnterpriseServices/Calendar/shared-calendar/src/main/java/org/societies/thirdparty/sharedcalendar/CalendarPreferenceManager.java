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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.RequestorService;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.thirdparty.sharedcalendar.api.CalendarPreference;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarPreferenceManager implements IActionConsumer {

	static final Logger log = LoggerFactory.getLogger(CalendarPreferenceManager.class);
	
	private RequestorService myrequestor;
	private IIdentity myId;
	private String serviceType;
	private ServiceResourceIdentifier mySRI;

	private IPersonalisationManager personalisation;

	private ICommManager commManager;

	private IServices serviceMgmt;

	private IUserActionMonitor userAction;

	private ConcurrentHashMap<CalendarPreference, String> calendarPreferences;

	public IUserActionMonitor getUserAction() {
		return userAction;
	}

	public void setUserAction(IUserActionMonitor userAction) {
		this.userAction = userAction;
	}
	
	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public IPersonalisationManager getPersonalisation() {
		return personalisation;
	}

	public void setPersonalisation(IPersonalisationManager personalisation) {
		this.personalisation = personalisation;
	}
	
	public CalendarPreferenceManager() {
		log.debug("Calendar Preference Manager");
		calendarPreferences = new ConcurrentHashMap<CalendarPreference,String>(CalendarPreference.values().length,1f,1);
	}

	public void setPreference(CalendarPreference preferenceName, String preferenceValue){
		
		log.debug("Set a Preference ( {}, {} )", preferenceName, preferenceValue );
		
		IAction myAction = new Action(getServiceIdentifier(), getServiceType(), preferenceName.toString(), preferenceValue);
		getUserAction().monitor(getMyId(), myAction);
		
		if(!calendarPreferences.containsKey(preferenceName))
			calendarPreferences.put(preferenceName, preferenceValue);
	}
	
	public String getPreference(CalendarPreference preference){
		log.debug("Getting Preference: {} : {} ", preference,calendarPreferences.get(preference));
		return calendarPreferences.get(preference);
		
	}	
	
	public String getPreferenceManual(CalendarPreference preference){
		log.debug("Getting PreferenceManually: {}", preference);
		
		String result = null;
		
		try{
			
			if(myrequestor == null){
				myrequestor = new RequestorService(getMyId(), getServiceIdentifier());
			}
			
			Future<IAction> actionAsync = getPersonalisation().getPreference(myrequestor, getMyId(), getServiceType(), getServiceIdentifier(), preference.toString());
			IAction action = actionAsync.get();
			
			if(action != null){
				log.debug("Preference retrieved: {} => {}", action.getparameterName(), action.getvalue());
				result = action.getvalue();
				
			} else{
				log.debug("Preference was not retrieved!");
			}
		} catch(Exception ex){
			log.error("There was an exception getting a Preference from the Personalisation Manager : {}", ex);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * @return
	 */
	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {

		if(mySRI == null)
			mySRI = getServiceMgmt().getMyServiceId(getClass());
		
		return mySRI;
	}

	/**
	 * @return
	 */
	@Override
	public String getServiceType() {
		if(serviceType == null){
			serviceType = getServiceMgmt().getMyCategory(getServiceIdentifier());
		}
		return serviceType;
	}

	@Override
	public List<String> getServiceTypes() {
		List<String> result = new ArrayList<String>();
		result.add(getServiceType());
		return result;
	}
	
	/**
	 * @return
	 */
	private IIdentity getMyId() {
		if(myId == null){
			try{
				String jid = getCommManager().getIdManager().getThisNetworkNode().getJid();
				myId = getCommManager().getIdManager().fromJid(jid);
			} catch(Exception ex){
				log.error("Exception: {}", ex.getMessage());
				ex.printStackTrace();
			}
			
		}
		return myId;
	}


	@Override
	public List<PersonalisablePreferenceIdentifier> getPersonalisablePreferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setIAction(IIdentity id, IAction action) {
		log.debug("Personalisation Manager setting preference for {} : {}",action.getparameterName(),action.getvalue());
		log.debug("CalendarPreference.valueOf: {}",CalendarPreference.valueOf(action.getparameterName()));
		calendarPreferences.put(CalendarPreference.valueOf(action.getparameterName()), action.getvalue());
		return true;
	}


}
