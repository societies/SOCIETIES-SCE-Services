package org.societies.thirdparty.enterprise.sharedCalendar.web;

import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


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
import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback;
import org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient;
import org.societies.thirdparty.sharedCalendar.api.UserWarning;
import org.societies.thirdparty.sharedCalendar.api.XMLGregorianCalendarConverter;


@ManagedBean(name="calendarWebController")
@SessionScoped
public class CalendarWebController {
	
	private static long _DEADLINE = 3000;
	static final Logger log = LoggerFactory.getLogger(CalendarWebController.class);
	
	private List<Calendar> calendars;
	private IIdentity myId;
	
	private IIdentity myId(){
		
		if(myId != null)
			return myId;
		
		String id = getCommManager().getIdManager().getThisNetworkNode().getBareJid();
		try{
			myId = getCommManager().getIdManager().fromJid(id);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return myId;
	}
	
	private HashMap<String, Event> currentEvents;

		
	@ManagedProperty(value = "#{sharedCalendarClient}")
	private ISharedCalendarClient sharedCalendar;
	
    @SuppressWarnings("UnusedDeclaration")
	public ISharedCalendarClient getSharedCalendar() {
		return sharedCalendar;
	}
	
    @SuppressWarnings("UnusedDeclaration")
	public void setSharedCalendar(ISharedCalendarClient sharedCalendar) {
		this.sharedCalendar = sharedCalendar;
	}
	
	@ManagedProperty(value = "#{commManager}")
	private ICommManager commManager;
	
    @SuppressWarnings("UnusedDeclaration")
	public ICommManager getCommManager() {
		return commManager;
	}

    @SuppressWarnings("UnusedDeclaration")
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	

	// PROPERTIES that the Webpage will access
	private String selectedNode;
	
	public void setSelectedNode(String selectedNode){
		this.selectedNode = selectedNode;
	}
	
	public String getSelectedNode(){
		return this.selectedNode;
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
	
    public ScheduleEvent getEvent() {  
        return event;  
    }  
  
    public void setEvent(ScheduleEvent event) {  
        this.event = event;  
    }
	
    
    private CalendarEvent societiesEvent;
    
    public CalendarEvent getSocietiesEvent(){
    	return this.societiesEvent;
    }
    
    public void setSocietiesEvent(CalendarEvent societiesEvent){
    	this.societiesEvent=societiesEvent;
    }
    
    
	private List<CalendarEvent> recommendedEvents;
	
	public void setRecommendedEvents(List<CalendarEvent> recommendedEvents){
		this.recommendedEvents = recommendedEvents;
	}
	
	public List<CalendarEvent> getRecommendedEvents(){
		return recommendedEvents;
	}
	
	
	private CalendarEvent recommendedEvent;
	
	public void setRecommendedEvent(CalendarEvent recommendedEvent){
		this.recommendedEvent = recommendedEvent;
	}
	
	public CalendarEvent getRecommendedEvent(){
		return recommendedEvent;
	}
	
	private List<String> eventAttendees;
	
	public void setEventAttendees(List<String> eventAttendees){
		this.eventAttendees=eventAttendees;
	}
	
	public List<String> getEventAttendees(){
		List<String> event = new ArrayList<String>();
		event.add("Coisas");
		event.add("Coisas2");
		return event;
	}
    
	
	public boolean isMyEvent(){
		if(event.getId() == null)
			return true;
		
		boolean isMine = false;
		
		try{
			IIdentity creatorId = getCommManager().getIdManager().fromJid(societiesEvent.getCreatorId());
			isMine = creatorId.equals(myId());
		} catch(Exception ex){
			log.error("Exception occured: " + ex);
			ex.printStackTrace();
		}
		
		return isMine;
	}
	
	public boolean isSubscribed(){
		if(event.getId() == null)
			return false;
		boolean isSubs = false;
		
		try{
			List<String> attendees = societiesEvent.getAttendees();

			for(String attendee : attendees){
				IIdentity attendeeId =  getCommManager().getIdManager().fromJid(attendee);
				if(myId().equals(attendeeId)){
					isSubs=true;
					break;
				}
			}
			
		} catch(Exception ex){
			log.error("Exception occured: " + ex);
			ex.printStackTrace();
		}
		
		return isSubs;		
		
	}
	
	public boolean isEditable(){
		boolean isEdit = false;
			
		if(event.getId() == null)
			return true;
		
		if(event.getId().equals("recommendedEvent"))
			return false;
		
		if(isMyEvent())
			return true;

		
		return isEdit;
	}
	
	public CalendarWebController() {
		if(log.isDebugEnabled())
			log.debug("CalendarWebController");
		
		eventModel = new DefaultScheduleModel();
		myId = null;
		recommendedEvent = null;
		recommendedEvents = new ArrayList<CalendarEvent>();
		Event testEvent = new Event();
		testEvent.setCalendarId("teste");
		testEvent.setEventSummary("teste");
		testEvent.setLocation("teste");
		testEvent.setEventDescription("test");
		recommendedEvents.add(new CalendarEvent(testEvent));
		
	}

	/*
	@PostConstruct
	public void initController(){
		if(log.isDebugEnabled())
			log.debug("init Controller!");
		
		try{
			myId = myId();
			selectedNode = myId.getBareJid();
			recommendedEvents = new ArrayList<CalendarEvent>();
			Event testEvent = new Event();
			testEvent.setCalendarId("teste");
			testEvent.setEventSummary("teste");
			testEvent.setLocation("teste");
			recommendedEvents.add(new CalendarEvent(testEvent));
			//viewCalendar(selectedNode);
			//updateRecommendedEvents();
		} catch(Exception ex){
			log.error("Exception in initController: " + ex);
			ex.printStackTrace();
		}
	}
	
	*/
	
	public void selectCalendar(){
		
		if(log.isDebugEnabled())
			log.debug("Selecting calendar: " + selectedNode);
		
		if(log.isDebugEnabled())
			log.debug(":P");
		
		if(selectedNode != null){
			ServiceResourceIdentifier myServiceID=null;
			IAction action = new Action( myServiceID, "calendar", "calendar",selectedNode,true,true,false);
			
			viewCalendar(selectedNode);
		} else{
			selectedNode = myId().getBareJid();
			viewCalendar(selectedNode);
		}
		
	}
	
	public List<Calendar> getCalendarList(){
		
		if(log.isDebugEnabled())
			log.debug("Getting Calendar list");
		
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		getSharedCalendar().getAllCalendars(callback);
		
		SharedCalendarResult result = callback.getResult();
		List<Calendar> calendarList = result.getCalendarList();
		
		this.calendars = calendarList;
		
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
			eventModel.addEvent(newEvent);
			currentEvents.put(newEvent.getId(),event);
			
		}
		
	}
	
	
	public void updateRecommendedEvents(){
		if(log.isDebugEnabled())
			log.debug("Updating the recommended Events");
		
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		getSharedCalendar().getRecommendedEvents(callback, myId().getBareJid());
		SharedCalendarResult result = callback.getResult();
		
		List<Event> recEvents = result.getEventList();
		
		recommendedEvents = new ArrayList<CalendarEvent>();
		
		for(Event recEvent : recEvents){
			recommendedEvents.add(new CalendarEvent(recEvent));
		}
		
		if(log.isDebugEnabled())
			log.debug("Returned " + recommendedEvents.size() + " recommended Events!");
		
		if(recommendedEvents.size() < 1 && currentEvents != null){
			for(Event curEvent : currentEvents.values()){
				log.info("curEvent: " + curEvent.getEventSummary());
				recommendedEvents.add(new CalendarEvent(curEvent));
			}
		}
		
		if(log.isDebugEnabled())
			log.debug("Final " + recommendedEvents.size() + " recommended Events!");
		
		//addMessage("Updated Events!",recommendedEvents.size() + "events!");
	}
	
	
	public void onEventSelect(SelectEvent selectEvent) {
		if(log.isDebugEnabled())
			log.debug("Selected an Event!: ");
		
		// We need to get all the event information!
		ScheduleEvent selectedEvent = (ScheduleEvent) selectEvent.getObject();
		
		if(log.isDebugEnabled())
			log.debug("Selected Primefaces Event: " + selectedEvent.getTitle() + " : " + selectedEvent.getId());
		
		event = selectedEvent; 
		societiesEvent = new CalendarEvent(currentEvents.get(selectedEvent.getId()));
		
	}
	
	public void onDateSelect(SelectEvent selectEvent) {
		if(selectedNode == null)
			selectedNode = myId().getBareJid();
		
		event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
		societiesEvent = new CalendarEvent();
		societiesEvent.setStartDate((Date) selectEvent.getObject());
		societiesEvent.setEndDate((Date) selectEvent.getObject());
		
		if(log.isDebugEnabled()){
			log.debug("Date selected: " + (Date) selectEvent.getObject());
		}
	}
	
	public void onEventMove(ScheduleEntryMoveEvent event) {
		
		if(log.isDebugEnabled())
			log.debug("Event was moved!: " + event.getScheduleEvent().getId());

		addMessage("Event moved","New start date: " + event.getScheduleEvent().getStartDate() +", New end date: " + event.getScheduleEvent().getEndDate());  

        updateEvent(event.getScheduleEvent());

	}
	
	public void onEventResize(ScheduleEntryResizeEvent event) {
		if(log.isDebugEnabled())
			log.debug("Event was resized!");

		addMessage("Event resized","New start date: " + event.getScheduleEvent().getStartDate() +", New end date: " + event.getScheduleEvent().getEndDate());  

        updateEvent(event.getScheduleEvent());

	}
	
	private void updateEvent(ScheduleEvent scheduleEvent){
		
		Event updatedEvent = currentEvents.get(scheduleEvent.getId());
				
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
			
			addMessage("Event Updated!", updatedEvent.getEventSummary() + " had its date/duration changed!");
		} else{
			if(log.isDebugEnabled())
				log.debug("Problem updating the event!");
			
			ScheduleEvent revertedEvent = new DefaultScheduleEvent(scheduleEvent.getTitle(),originalStart,originalEnd,scheduleEvent.getStyleClass());
			revertedEvent.setId(scheduleEvent.getId());
			
			eventModel.updateEvent(revertedEvent);
			
			addMessage("Event Not Updated!", updatedEvent.getEventSummary() + " couldn't be changed!");

		}
	}
	
    public void saveEvent(ActionEvent actionEvent) {
    	
    	if(log.isDebugEnabled()){
    		log.debug("Save event received, event is " + event.getId());
    	}
    	
    	societiesEvent.setTitle(event.getTitle());
    	societiesEvent.setStartDate(event.getStartDate());
    	societiesEvent.setEndDate(event.getEndDate());
        societiesEvent.setCreatorId(myId().getBareJid());
        
		CalendarWebResultCallback callback = new CalendarWebResultCallback();

        if(event.getId() == null) {
        	
            if(log.isDebugEnabled())
            	log.debug("Adding event to Calendar!");
            
    		getSharedCalendar().createEvent(callback, societiesEvent.getSocietiesEvent(), selectedNode);
    		                        
        }
        else{
            if(log.isDebugEnabled())
            	log.debug("Updating Event to Calendar!");
            
            getSharedCalendar().updateEvent(callback, societiesEvent.getSocietiesEvent());

        }
        
        SharedCalendarResult saveResult = callback.getResult();
        
        if(saveResult.isLastOperationSuccessful()){
    		
			addMessage("Event Saved","Event was saved to the Calendar!");
			

        	if(event.getId() == null){
            	if(log.isDebugEnabled())
            		log.debug("Adding event to model");
        		eventModel.addEvent(event);
        	}
        	else{
            	if(log.isDebugEnabled())
            		log.debug("Updating event in model");
        		eventModel.updateEvent(event);
        	}
        	
        	currentEvents.put(event.getId(), saveResult.getEvent());
        	log.debug("eventId:" + event.getId());
		}
		else{
			if(log.isDebugEnabled())
				log.debug("Problem saving Event to the calendar!");
			
			addMessage("Event Not Saved","Event was NOT saved to the Calendar!");
		}
		
        event = new DefaultScheduleEvent();
        societiesEvent = new CalendarEvent();
    }  
    
    public void deleteEvent(ActionEvent actionEvent) {
    	if(log.isDebugEnabled())
    		log.debug("Delete event called, oh dear!");
    	
    	if(event.getId() == null)
    		return;
    	
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
    	getSharedCalendar().deleteEvent(callback, societiesEvent.getEventId(), societiesEvent.getNodeId());
    	SharedCalendarResult result = callback.getResult();
    	
    	if(result.isLastOperationSuccessful()){
    		if(log.isDebugEnabled())
    			log.debug("Delete successful!");
    		currentEvents.remove(event.getId());
    		eventModel.deleteEvent(event);
    		
    		addMessage("Event Deleted!","Deleted " + societiesEvent.getTitle() + " from current calendar!");
    		
    	} else{
    		if(log.isDebugEnabled())
    			log.debug("Delete NOT successful!");
    		addMessage("Event NOT Deleted!","Couldn't delete " + societiesEvent.getTitle() + " from current calendar!");
    	}
    }
    
    public void joinEvent(ActionEvent actionEvent){
    	if(log.isDebugEnabled())
    		log.debug("Joining Event!");
    	
    	if(societiesEvent == null)
    		return;
    	
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
    	getSharedCalendar().subscribeToEvent(callback, societiesEvent.getEventId(), societiesEvent.getNodeId(), myId().getBareJid());
    	SharedCalendarResult result = callback.getResult();
    	
    	if(result.isSubscribingResult()){
    		if(log.isDebugEnabled())
    			log.debug("Joined event!");
    		addMessage("Join Completed","Joined the event " + societiesEvent.getTitle());
    	} else{
    		if(log.isDebugEnabled())
    			log.debug("Couldn't subscribe!");
    		addMessage("Join failed!","Couldn't join the event " + societiesEvent.getTitle());
    	}
    		
    }
 
    public void leaveEvent(ActionEvent actionEvent){
    	if(log.isDebugEnabled())
    		log.debug("LeavingEvent!");
    	
    	if(societiesEvent == null)
    		return;
    	
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
    	getSharedCalendar().unsubscribeFromEvent(callback, societiesEvent.getEventId(), myId().getBareJid());
    	SharedCalendarResult result = callback.getResult();
    	
    	if(result.isSubscribingResult()){
    		if(log.isDebugEnabled())
    			log.debug("Left event!");
    		addMessage("Leave Completed","Left the event " + societiesEvent.getTitle());
    	} else{
    		if(log.isDebugEnabled())
    			log.debug("Couldn't leave!");
    		addMessage("Join failed!","Couldn't leave the event " + societiesEvent.getTitle());
    	}
    		
    }
    
	public void viewRecommendedEvent(){
		if(log.isDebugEnabled())
			log.debug("View recommended event!");
		
		if(recommendedEvent == null)
			return;

		CalendarEvent ourEvent = null;
		for(CalendarEvent recEvent: recommendedEvents){
				if(recEvent.getEventId().equals(recommendedEvent)){
					ourEvent = recEvent;
					ourEvent.setCalendarName(getCalendarName(recEvent.getCalendarId()));
					break;
				}		
		}
		
		if(ourEvent != null){
			if(log.isDebugEnabled())
				log.debug("User picked Event: " + ourEvent.getTitle());
			societiesEvent = ourEvent;
			event = new DefaultScheduleEvent(ourEvent.getTitle(),ourEvent.getStartDate(),ourEvent.getEndDate());
			event.setId("recommendedEvent");
			
		} else{
			if(log.isDebugEnabled())
				log.debug("Oops, no event selected?!");
			societiesEvent = new CalendarEvent();
			event = new DefaultScheduleEvent();
		}
		
		
	}
	
	protected void addMessage(String title, String detail) {
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
	
	private String getCalendarName(String calendarId){
		for(Calendar calendar: calendars){
			if(calendar.getCalendarId().equals(calendarId))
				return calendar.getSummary();
		}
		return null;
	}

}
