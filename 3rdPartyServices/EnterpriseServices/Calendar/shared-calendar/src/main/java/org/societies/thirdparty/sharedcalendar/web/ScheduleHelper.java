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
package org.societies.thirdparty.sharedcalendar.web;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.context.FacesContext;

import org.primefaces.component.schedule.Schedule;
import org.primefaces.model.ScheduleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdparty.sharedcalendar.api.CalendarPreference;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ScheduleHelper implements Serializable {

	private CalendarController parent;
	private Date startDate;
	private Date endDate;
	private String view;
	private Schedule schedule;
	private ScheduleModel model;
	private boolean isToday;
	private String date;

	static final Logger log = LoggerFactory.getLogger(ScheduleHelper.class);
	
	public ScheduleHelper(CalendarController parent) {
		this.parent = parent;
		this.isToday = true;
	}

	public void changeDate(){
		
		log.debug("Change Date called for {}", date);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime((Date) getSchedule().getInitialDate());
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		
		if(date.equals("today")){
			cal = today;
			isToday = true;
		} else{
			int moveValue = (date.equals("next") ? 1 : -1 );
		    if(view.equals("agendaDay")){
		         cal.add(Calendar.DAY_OF_MONTH, moveValue);
		         isToday = today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
		        		 today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
		         
		    } else
		    	if(view.equals("agendaWeek")){
		    		cal.add(Calendar.WEEK_OF_MONTH, moveValue);
			         isToday = today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
			        		 today.get(Calendar.WEEK_OF_MONTH) == cal.get(Calendar.WEEK_OF_MONTH);
		    	} else
		    		if(view.equals("month")){
		    			cal.add(Calendar.MONTH, moveValue);
		    			isToday = today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
		    					today.get(Calendar.MONTH) == cal.get(Calendar.MONTH);
		    		}
		}
		
		log.debug("{} view, today? {}",view,isToday);
		schedule.setInitialDate(cal.getTime());

	}
	
	public String getScheduleHeader(){
		Date ourDate = (Date) getSchedule().getInitialDate();
		
		log.debug("ourDate: {}", ourDate);
		if(ourDate == null){
			ourDate = new Date();
			getSchedule().setInitialDate(ourDate);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		
		if(view.equals("agendaWeek")){
			Calendar thisWeek = Calendar.getInstance();
			thisWeek.setTime(ourDate);
			StringBuilder headerBuilder = new StringBuilder();
			thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			thisWeek.add(Calendar.DAY_OF_MONTH, -7);
			dateFormat.applyLocalizedPattern("MMM dd");
			headerBuilder.append(dateFormat.format(thisWeek.getTime())).append(" - ");
			thisWeek.add(Calendar.DAY_OF_MONTH, 7);
			thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			dateFormat.applyLocalizedPattern("MMM dd, yyyy");
			headerBuilder.append(dateFormat.format(thisWeek.getTime()));
			return headerBuilder.toString();
		}
		
		if(view.equals("month"))
			dateFormat.applyPattern("MMMM yyyy");
		
		if(view.equals("agendaDay"))
			dateFormat.applyPattern("EEEEE, MMM dd, yyyy");
		
		return dateFormat.format(ourDate);
			
	}
	
	/**
	 * @return
	 */
	private Schedule getSchedule() {
			schedule = (Schedule) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:schedule");
			model = (ScheduleModel) schedule.getValue();
			if(view == null)
				view = schedule.getView();

		return schedule;
	}

	public boolean isToday(){
		return isToday;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public Date getEndDate(){
		return endDate;
	}
	
	public String getView(){
	//	view = parent.getPreferences().getPreference(CalendarPreference.CALENDAR_VIEW);
		log.debug("getView: {}",view);
		if(view == null)
			view = schedule.getView();
		
		return this.view;
	}
	
	public void setView(String view){
		this.view = view;
		parent.getPreferences().setPreference(CalendarPreference.CALENDAR_VIEW, view);
	}
	
	public void selectView(){
		log.debug("selectView");
		String prefView = parent.getPreferences().getPreference(CalendarPreference.CALENDAR_VIEW);
		if(prefView != null)
			this.view = prefView;
		
		getSchedule().setView(view);
		
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

}
