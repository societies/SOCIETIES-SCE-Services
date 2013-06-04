package org.societies.thirdparty.enterprise.sharedCalendar.web;

import java.util.Date;

import java.util.HashMap;
import java.util.List;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;


import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.ext3p.schema.sharedcalendar.Calendar;
import org.societies.api.ext3p.schema.sharedcalendar.Event;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarResult;
import org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient;
import org.societies.thirdparty.sharedCalendar.api.UserWarning;
import org.societies.thirdparty.sharedCalendar.api.XMLGregorianCalendarConverter;


@ManagedBean(name="calendarWebController")
@RequestScoped
public class CalendarWebController {
	
	private static long _DEADLINE = 3000;
	static final Logger log = LoggerFactory.getLogger(CalendarWebController.class);
		
	@ManagedProperty(value = "#{sharedCalendar}")
	private ISharedCalendarClient sharedCalendar;
	
	
	public ISharedCalendarClient getSharedCalendar() {
		return sharedCalendar;
	}
	
	public void setSharedCalendar(ISharedCalendarClient sharedCalendar) {
		this.sharedCalendar = sharedCalendar;
	}

	
	@ManagedProperty(value = "#{cisManager}")
	private ICisManager cisManager;

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
	
	@ManagedProperty(value = "#{commManager}")
	private ICommManager commManager;
	

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	

	// PROPERTIES that the Webpage will access
	private Calendar selectedCalendar;
	
	public void setSelectedCalendar(Calendar selectedCalendar){
		this.selectedCalendar = selectedCalendar;
	}
	
	public Calendar getSelectedNode(){
		return this.selectedCalendar;
	}
	
	
	private boolean allEvents;
	
    public boolean isAllEvents() {  
        return allEvents;  
    }  
  
    public void setAllEvents(boolean allEvents) {  
        this.allEvents = allEvents;  
    }  
	
    
	private ScheduleModel eventModel;
	
    public ScheduleModel getEventModel() {  
        return eventModel;  
    }  
    
    
	private ScheduleEvent event = new DefaultScheduleEvent();
	private HashMap<String, Event> currentEvents;
	
    public ScheduleEvent getEvent() {  
        return event;  
    }  
  
    public void setEvent(ScheduleEvent event) {  
        this.event = event;  
    }  
	
	public CalendarWebController() {
		if(log.isDebugEnabled())
			log.debug("CalendarWebController");
		
		eventModel = new DefaultScheduleModel();

	}

	
	public void selectCalendar(){
		
		if(log.isDebugEnabled())
			log.debug("Selecting calendar: " + selectedCalendar);
		
		if(selectedCalendar != null){
			viewCalendar(selectedCalendar.getNodeId());
		}
	}
	
	public List<Calendar> getCalendarList(){
		
		if(log.isDebugEnabled())
			log.debug("Getting Calendar list");
		
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		getSharedCalendar().getAllCalendars(callback);
		
		SharedCalendarResult result = callback.getResult();
		List<Calendar> calendarList = result.getCalendarList();
		
		return calendarList;
		
	}
	
	private void viewCalendar(String nodeId){
		if(log.isDebugEnabled())
			log.debug("We need to update the Calendar we are viewing!");
		
		if(log.isDebugEnabled())
			log.debug("Getting the events for this calendar!");
		
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		getSharedCalendar().retrieveEvents(callback, nodeId);
		
		SharedCalendarResult result = callback.getResult();
		List<Event> calendarEvents = result.getEventList();
		
		if(log.isDebugEnabled())
			log.debug("Got the events for the calendar: " + calendarEvents.size());
		
		currentEvents = new HashMap<String,Event>();
		eventModel.clear();
		
		for(Event event: calendarEvents){
			if(log.isDebugEnabled())
				log.debug("Adding event: " + event.getEventSummary());
			
			Date startDate = XMLGregorianCalendarConverter.asDate(event.getStartDate());
			Date endDate = XMLGregorianCalendarConverter.asDate(event.getEndDate());
			ScheduleEvent newEvent = new DefaultScheduleEvent(event.getEventSummary(),startDate,endDate);

			currentEvents.put(newEvent.getId(),event);
			eventModel.addEvent(newEvent);
		}
		
	}
	
	
	public void updateRecommendedEvents(){
		if(log.isDebugEnabled())
			log.debug("Updating the recommended Events");
		
	}
	
	public void onEventSelect(SelectEvent selectEvent) {
		if(log.isDebugEnabled())
			log.debug("Selected an Event!");
		
		// We need to get all the event information!
		ScheduleEvent selectedEvent = (ScheduleEvent) selectEvent.getObject();
		
		if(log.isDebugEnabled())
			log.debug("Selected Event: " + selectedEvent.getTitle() + " : " + selectedEvent.getId());
		
		//Event calendarEvent = currentEvents.get(selectedEvent.getId());
		
	}
	
	public void onDateSelect(SelectEvent selectEvent) {
		event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
	}
	
	public void onEventMove(ScheduleEntryMoveEvent event) {
		
		if(log.isDebugEnabled())
			log.debug("Event was resized!");
/*
		//Event updatedEvent = currentEvents.get(event.getScheduleEvent().getId());
		ScheduleEvent scheduleEvent = event.getScheduleEvent();
		
		Date originalStart = XMLGregorianCalendarConverter.asDate(updatedEvent.getStartDate());
		Date originalEnd = XMLGregorianCalendarConverter.asDate(updatedEvent.getEndDate());
		
		if(log.isDebugEnabled()){
			log.debug("Original Start Time: " + originalStart);
			log.debug("New start time: " + scheduleEvent.getStartDate());
			log.debug("Original End Time: " + originalEnd);
			log.debug("New end time: " + scheduleEvent.getEndDate());
		}
		
		updatedEvent.setStartDate(XMLGregorianCalendarConverter.asXMLGregorianCalendar(scheduleEvent.getStartDate()));
		updatedEvent.setEndDate(XMLGregorianCalendarConverter.asXMLGregorianCalendar(scheduleEvent.getEndDate()));
		
		ICalendarResultCallback callback = new CalendarWebResultCallback();
		getSharedCalendar().updateEvent(callback , updatedEvent);
		
		Boolean result = callback.getResult().isLastOperationSuccessful();
		
		if(result){
			if(log.isDebugEnabled())
				log.debug("We updated the event successfully!");
			currentEvents.put(scheduleEvent.getId(), updatedEvent);
			eventModel.updateEvent(scheduleEvent);
			
			addMessage("Event Updated!", updatedEvent.getEventSummary() + " was moved " + event.getDayDelta() + " days and " + event.getMinuteDelta() + " minutes.");
		} else{
			if(log.isDebugEnabled())
				log.debug("Problem updating the event!");
			
			ScheduleEvent revertedEvent = new DefaultScheduleEvent(scheduleEvent.getTitle(),originalStart,originalEnd,scheduleEvent.getStyleClass());
			revertedEvent.setId(scheduleEvent.getId());
			
			eventModel.updateEvent(revertedEvent);
			
			addMessage("Event Not Updated!", updatedEvent.getEventSummary() + " couldn't be changed!");

		}
		*/
	}
	
	public void onEventResize(ScheduleEntryResizeEvent event) {
		if(log.isDebugEnabled())
			log.debug("Event was resized!");

		/*
		Event updatedEvent = currentEvents.get(event.getScheduleEvent().getId());
		ScheduleEvent scheduleEvent = event.getScheduleEvent();
		
		Date originalStart = XMLGregorianCalendarConverter.asDate(updatedEvent.getStartDate());
		Date originalEnd = XMLGregorianCalendarConverter.asDate(updatedEvent.getEndDate());
		
		if(log.isDebugEnabled()){
			log.debug("Original Start Time: " + originalStart);
			log.debug("New start time: " + scheduleEvent.getStartDate());
			log.debug("Original End Time: " + originalEnd);
			log.debug("New end time: " + scheduleEvent.getEndDate());
		}
		
		updatedEvent.setStartDate(XMLGregorianCalendarConverter.asXMLGregorianCalendar(scheduleEvent.getStartDate()));
		updatedEvent.setEndDate(XMLGregorianCalendarConverter.asXMLGregorianCalendar(scheduleEvent.getEndDate()));
		
		ICalendarResultCallback callback = new CalendarWebResultCallback();
		getSharedCalendar().updateEvent(callback , updatedEvent);
		
		Boolean result = callback.getResult().isLastOperationSuccessful();
		
		if(result){
			if(log.isDebugEnabled())
				log.debug("We updated the event successfully!");
			currentEvents.put(scheduleEvent.getId(), updatedEvent);
			eventModel.updateEvent(scheduleEvent);
			
			addMessage("Event Updated!", updatedEvent.getEventSummary() + " was resized in " + event.getDayDelta() + " days and " + event.getMinuteDelta() + " minutes.");
		} else{
			if(log.isDebugEnabled())
				log.debug("Problem updating the event!");
			
			ScheduleEvent revertedEvent = new DefaultScheduleEvent(scheduleEvent.getTitle(),originalStart,originalEnd,scheduleEvent.getStyleClass());
			revertedEvent.setId(scheduleEvent.getId());
			
			eventModel.updateEvent(revertedEvent);
			
			addMessage("Event Not Updated!", updatedEvent.getEventSummary() + " couldn't be changed!");

		}
		*/
	}
	
	public void viewRecommendedEvent(){
		if(log.isDebugEnabled())
			log.debug("View recommended event!");
		
	}
	
	private void addMessage(String title, String detail) {
		if(log.isDebugEnabled())
			log.debug("Adding a message to the Growl stuff: " + title + " : " + detail);
		
		FacesMessage faceMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, title, detail);
		FacesContext.getCurrentInstance().addMessage(null, faceMessage );
	}
	
	public void updateUserWarnings(){
		if(log.isDebugEnabled())
			log.debug("Updating user warnings!");
		
		List<UserWarning> userWarnings = getSharedCalendar().getUserWarnings();
		
		for(UserWarning warning: userWarnings){
			addMessage(warning.getTitle(),warning.getDetail());
		}
		
	}

}
