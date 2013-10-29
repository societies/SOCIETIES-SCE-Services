package org.societies.thirdparty.sharedcalendar.web;

import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;


import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


import org.primefaces.component.schedule.Schedule;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.RequestorService;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.thirdparty.sharedcalendar.CalendarContextUtils;
import org.societies.thirdparty.sharedcalendar.CalendarPreferenceManager;
import org.societies.thirdparty.sharedcalendar.api.CalendarPreference;
import org.societies.thirdparty.sharedcalendar.api.ISharedCalendar;
import org.societies.thirdparty.sharedcalendar.api.CalendarConverter;
import org.societies.thirdparty.sharedcalendar.api.ICalendarResultCallback;
import org.societies.thirdparty.sharedcalendar.api.UserWarning;
import org.societies.thirdparty.sharedcalendar.api.schema.Calendar;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;
import org.societies.thirdparty.sharedcalendar.api.schema.SharedCalendarResult;


@ManagedBean(name="calendarController")
@SessionScoped
public class CalendarController {
	
	private static long _DEADLINE = 3000;
	static final Logger log = LoggerFactory.getLogger(CalendarController.class);
	
	private List<Calendar> calendars;
	private IIdentity myId;
	private ServiceResourceIdentifier mySRI;
	
	protected IIdentity getId(){
		
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
	
	protected ServiceResourceIdentifier getSRI(){
		
		if(mySRI != null)
			return mySRI;
		
		try{
			mySRI = getServiceMgmt().getMyServiceId(getClass());
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return mySRI;
	}
	
	protected HashMap<String, CalendarEvent> currentEvents;

	public List<CalendarEvent> getCurrentEvents() {
		return new ArrayList<CalendarEvent>(currentEvents.values());
	}


	@ManagedProperty(value = "#{sharedCalendarClient}")
	private ISharedCalendar sharedCalendar;
	
    @SuppressWarnings("UnusedDeclaration")
	public ISharedCalendar getSharedCalendar() {
		return sharedCalendar;
	}
	
    @SuppressWarnings("UnusedDeclaration")
	public void setSharedCalendar(ISharedCalendar sharedCalendar) {
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
    
	@ManagedProperty(value = "#{serviceMgmt}")
	private IServices serviceMgmt;
	
    @SuppressWarnings("UnusedDeclaration")
	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

    @SuppressWarnings("UnusedDeclaration")
	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}
    
    
    @ManagedProperty(value = "#{calendarContext}")
    private CalendarContextUtils calendarContext;
    
    @SuppressWarnings("UnusedDeclaration")
	public CalendarContextUtils getCalendarContext() {
		return calendarContext;
	}

    @SuppressWarnings("UnusedDeclaration")
	public void setCalendarContext(CalendarContextUtils calendarContext) {
		this.calendarContext = calendarContext;
	}
    
    
    @ManagedProperty(value = "#{calendarPreferences}")
    private CalendarPreferenceManager preferences;
    
    @SuppressWarnings("UnusedDeclaration")
	public CalendarPreferenceManager getPreferences() {
		return preferences;
	}

    @SuppressWarnings("UnusedDeclaration")
	public void setPreferences(CalendarPreferenceManager preferences) {
    	log.debug("Set Preferences!");
		this.preferences = preferences;
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
	
    
    private boolean agendaToggle;
    
	public boolean isAgendaToggle() {
		return agendaToggle;
	}

	public void setAgendaToggle(boolean agendaToggle) {
		this.agendaToggle = agendaToggle;
	}

	
	private boolean searchLocationToggle;
	
	public boolean isSearchLocationToggle() {
		return searchLocationToggle;
	}

	public void setSearchLocationToggle(boolean locationToggle) {
		this.searchLocationToggle = locationToggle;
	}
	
	private boolean locationToggle;
	
	public boolean isLocationToggle() {
		return locationToggle;
	}

	public void setLocationToggle(boolean locationToggle) {
		this.locationToggle = locationToggle;
	}
	
	public String getMyLocation(){
		String myLocation = getCalendarContext().getMyLocation();
		if(myLocation != null)
			return myLocation;
		else
			return "Not Available";
	}
	
	private CalendarSchedule eventModel;
	
    public LazyScheduleModel getEventModel() {
        return eventModel;  
    }  
    
    
	/**
	 * @return the scheduleHelper
	 */
	public ScheduleHelper getScheduleHelper() {
		return scheduleHelper;
	}

	/**
	 * @param scheduleHelper the scheduleHelper to set
	 */
	public void setScheduleHelper(ScheduleHelper scheduleHelper) {
		this.scheduleHelper = scheduleHelper;
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
		if(recommendedEvents.isEmpty())
			recommendedEvents.add(new CalendarEvent(defaultRecommendedEvent,this));
		return recommendedEvents;
	}

	private CalendarEvent recommendedEvent;
	
	public void setRecommendedEvent(CalendarEvent recommendedEvent){
		this.recommendedEvent = recommendedEvent;
	}
	
	public CalendarEvent getRecommendedEvent(){
		return recommendedEvent;
	}
	
	public List<String> getEventAttendees(){
		List<String> eventAttendees = new ArrayList<String>();
		if(societiesEvent == null || societiesEvent.getAttendees() == null || societiesEvent.getAttendees().isEmpty()){
			eventAttendees.add("No attendees...");
		} else{
			for(String attendeeId: societiesEvent.getAttendees()){
				try{
					eventAttendees.add(getCommManager().getIdManager().fromJid(attendeeId).getIdentifier());
				} catch(Exception ex){
					log.warn("Couldn't change {} to an Id...",attendeeId);
				}
			}
			
		}
		
		return eventAttendees;
	}
    
	
	public boolean isMyEvent(){
		if(event.getId() == null)
			return true;
		
		boolean isMine = false;
		
		try{
			IIdentity creatorId = getCommManager().getIdManager().fromJid(societiesEvent.getCreatorId());
			isMine = creatorId.equals(getId());
		} catch(Exception ex){
			log.error("Exception occured: " + ex);
			ex.printStackTrace();
		}
		
		return isMine;
	}
	
	public boolean isMyEvent(String creatorJid){
		
		boolean isMine = false;
		
		try{
			IIdentity creatorId = getCommManager().getIdManager().fromJid(creatorJid);
			isMine = creatorId.equals(getId());
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
				if(getId().equals(attendeeId)){
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
	
	public boolean isMultipleCalendars(){
		boolean isMultipleEvents= false;
		
		if(isAllEvents())
			isMultipleEvents = true;
		if(selectedNode.equals("mysubscribedevents"))
			isMultipleEvents = true;
		
		return isMultipleEvents;
	}
	
	private EventConverter eventConverter;
	private Event defaultRecommendedEvent;
	private Event defaultSearchEvent;
	private ScheduleHelper scheduleHelper;
	
	public void setEventConverter(EventConverter eventConverter){
		this.eventConverter = eventConverter;
	}
	
	public EventConverter getEventConverter(){
		return this.eventConverter;
	}
	
	public CalendarController() {
		if(log.isDebugEnabled())
			log.debug("CalendarController");
		
		eventModel = new CalendarSchedule(this);
		eventConverter = new EventConverter(this);
		
		myId = null;
		recommendedEvent = null;
		searchEvent = null;
		recommendedEvents = new ArrayList<CalendarEvent>();
		searchResults = new ArrayList<CalendarEvent>();
		allEvents = false;
		agendaToggle = false;
		locationToggle = false;
		setScheduleHelper(new ScheduleHelper(this));
		defaultRecommendedEvent = new Event();
		defaultSearchEvent = new Event();
		
		defaultRecommendedEvent.setEventId("recEvent");
		defaultRecommendedEvent.setName("No Recommended Events");
		defaultSearchEvent.setEventId("searchEvent");
		defaultSearchEvent.setName("No Search Results");
		
		societiesEvent = new CalendarEvent(this);
		societiesEvent.setDescription(" ");
		societiesEvent.setLocation("");
		societiesEvent.setAttendees(new ArrayList<String>());
		
		
		setSelectedEvent(getSearchResults().get(0));
		setRecommendedEvent(getRecommendedEvents().get(0));
	}

	public String getDateSelect(){
		return getScheduleHelper().getDate();
	}
	
	public void setDateSelect(String date){
		log.debug("DateSelect {}",date);
		getScheduleHelper().setDate(date);
	}
	
	public void changeCalendarDate(String date){
		log.debug("changeCalendarDate");
		setDateSelect(date);
		getScheduleHelper().changeDate();
	}
	
	public void previousDate(ActionEvent actionEvent){
		getScheduleHelper().setDate("previous");
		getScheduleHelper().changeDate();
	}
	
	public void nextDate(ActionEvent actionEvent){
		getScheduleHelper().setDate("next");
		getScheduleHelper().changeDate();
	}
	
	public void currentDate(ActionEvent actionEvent){
		getScheduleHelper().setDate("today");
		getScheduleHelper().changeDate();
	}
	
	public void selectView(){
		getScheduleHelper().selectView();
	}
	
	public void preRender(){
		if(log.isDebugEnabled())
			log.debug("preRenderMethod called!");
		
		try{
			myId = getId();
			getScheduleHelper().selectView();
			
			if(selectedNode == null){
				
				getCalendars();
				
				selectedNode = getPreferences().getPreference(CalendarPreference.VIEW_CALENDAR);
				log.debug("selectedNode: {}",selectedNode);
				
				if(selectedNode == null)
					selectedNode = myId.getBareJid();
				
				viewCalendar(selectedNode);
				updateRecommendedEvents();
				
			}else{
				log.debug("No init needed: {}", selectedNode);
				log.debug("societiesEventNode: {}", getSocietiesEventNode());
				log.debug("searchCalendarNode: {}", getSearchCalendarNode());
				log.debug("event {}", event);
			}
		} catch(Exception ex){
			log.error("Exception in initController: {}", ex);
			ex.printStackTrace();
		}
		
	}
	
	public void updateWebApp(){
		log.debug("Updating Web App Stuff!");
		getCalendars();
		
		viewCalendar(selectedNode);
			
		updateRecommendedEvents();
		updateUserWarnings();
	}
	
	public void selectCalendar(){
		
		log.debug("Selecting calendar: {}", selectedNode);
		
		if(selectedNode != null){
			
			preferences.setPreference(CalendarPreference.VIEW_CALENDAR,selectedNode);
			viewCalendar(selectedNode);
		} else{
			selectedNode = getId().getBareJid();
			viewCalendar(selectedNode);
		}
		
	}
	
	public List<Calendar> getCalendarList(){
		return this.calendars;

		
	}
	
	private List<Calendar> eventCalendarList;
	public List<Calendar> getEventCalendarList(){
		return eventCalendarList;
	}
	
	private void getCalendars(){
		if(log.isDebugEnabled())
			log.debug("Getting Calendar list");
		
		CalendarResultCallback callback = new CalendarResultCallback();
		getSharedCalendar().getAllCalendars(callback);
		
		SharedCalendarResult result = callback.getResult();
		List<Calendar> calendarList = result.getCalendarList();
		
		this.calendars = calendarList;
		
	}
	
	private void viewCalendar(String nodeId){
	
		log.debug("We need to update the Calendar we are viewing, for node {}", nodeId);
		
		CalendarResultCallback callback = new CalendarResultCallback();
		CalendarResultCallback secondCallback = null;
		
		if(!nodeId.equals("mysubscribedevents")){
			getSharedCalendar().retrieveEvents(callback, nodeId);
			if(allEvents){
				secondCallback = new CalendarResultCallback();
				getSharedCalendar().getSubscribedEvents(callback, getId().getBareJid());
			}
		}
		else
			getSharedCalendar().getSubscribedEvents(callback, getId().getBareJid());
		
		SharedCalendarResult result = callback.getResult();
		List<Event> calendarEvents = result.getEventList();
		
		currentEvents = new HashMap<String,CalendarEvent>();
		Schedule mySchedule = (Schedule) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:schedule");
		String view = mySchedule.getView();
		log.debug("My view is: {}",view);
		log.debug("My initial date is: {}", mySchedule.getInitialDate() );
		eventModel.clear();
		
		for(Event event: calendarEvents){
			log.debug("Adding event: {}", event.getName());
			
			ScheduleEvent newEvent = new DefaultScheduleEvent(event.getName(),event.getStartDate(),event.getEndDate());
			eventModel.addEvent(newEvent);
			currentEvents.put(newEvent.getId(),new CalendarEvent(event,getCalendarName(event.getNodeId()),this));
		}
		
		if(secondCallback != null){
			result = callback.getResult();
			List<Event> subscribedEvents = result.getEventList();
			
			for(Event event: subscribedEvents){
				log.debug("Adding subs event: {}", event.getName());
				// First we check if the current event is there...
				CalendarEvent ourEvent = new CalendarEvent(event,getCalendarName(event.getNodeId()),this);
				Set<Entry<String, CalendarEvent>> eventSet = currentEvents.entrySet();
				if(currentEvents.containsValue(ourEvent)){
					Iterator<Entry<String, CalendarEvent>> myIt = eventSet.iterator();
					while(myIt.hasNext()){
						Entry<String, CalendarEvent> calendarEntry = myIt.next();
						if(calendarEntry.getValue().equals(ourEvent)){
							eventModel.deleteEvent(eventModel.getEvent(calendarEntry.getKey()));
							myIt.remove();
						}
					}
				} 
				
				ScheduleEvent newEvent = new DefaultScheduleEvent(event.getName(),event.getStartDate(),event.getEndDate());
				eventModel.addEvent(newEvent);
				currentEvents.put(newEvent.getId(),new CalendarEvent(event,getCalendarName(event.getNodeId()),this));
			}
		}
		
	}
	
	
	public void updateRecommendedEvents(){
		if(log.isDebugEnabled())
			log.debug("Updating the recommended Events");
		
		CalendarResultCallback callback = new CalendarResultCallback();
		getSharedCalendar().getRecommendedEvents(callback, getId().getBareJid());
		SharedCalendarResult result = callback.getResult();
		
		List<Event> recEvents = result.getEventList();
		
		recommendedEvents = new ArrayList<CalendarEvent>();
		
		for(Event recEvent : recEvents){
			recommendedEvents.add(new CalendarEvent(recEvent,getCalendarName(recEvent.getNodeId()),this));
		}
		
		log.debug("Returned {} recommended Events!", recommendedEvents.size());
		
		setRecommendedEvent(getRecommendedEvents().get(0));
	}
	
	
	public void onEventSelect(SelectEvent selectEvent) {
		if(log.isDebugEnabled())
			log.debug("Selected an Event!: ");
		
		// We need to get all the event information!
		ScheduleEvent selectedEvent = (ScheduleEvent) selectEvent.getObject();
		
		log.debug("Selected Primefaces Event: {} : {}", selectedEvent.getTitle(), selectedEvent.getId());
		
		event = selectedEvent; 
		societiesEvent = currentEvents.get(selectedEvent.getId());
		
		ICalendarResultCallback callback = new CalendarResultCallback();
		getSharedCalendar().viewEvent(callback , societiesEvent.getEventId(), societiesEvent.getNodeId());
		Event ourEvent = callback.getResult().getEvent();
		if(ourEvent != null){
			societiesEvent.setSocietiesEvent(ourEvent);
			currentEvents.put(selectedEvent.getId(), societiesEvent);
		} else{
			log.warn("Couldn't find {} on the backend?!",societiesEvent.getTitle());
		}
		setLocationToggle(false);
		
	}
	
	public void onDateSelect(SelectEvent selectEvent) {
		if(selectedNode == null)
			selectedNode = getId().getBareJid();
		
		String eventTitle = preferences.getPreference(CalendarPreference.CREATE_TITLE);
		if(eventTitle == null)
			eventTitle = "";
		event = new DefaultScheduleEvent(eventTitle, (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
		
		societiesEvent = new CalendarEvent(this);
		societiesEvent.setStartDate((Date) selectEvent.getObject());
		societiesEvent.setEndDate((Date) selectEvent.getObject());
		societiesEvent.setEventId("dateSelectEvent");

		if(!selectedNode.equals("mysubscribedevents")){
			societiesEvent.setNodeId(selectedNode);
			societiesEvent.setCalendarName(getCalendarName(selectedNode));
		} else{
			String calendarId = preferences.getPreference(CalendarPreference.CREATE_CALENDAR);
			if(calendarId == null || getCalendarName(calendarId) == null)
				calendarId = myId.getBareJid();
			societiesEvent.setNodeId(calendarId);
			societiesEvent.setCalendarName(getCalendarName(calendarId));
		}
		
		if(isLocationToggle())
			updateLocation();
		else{
			String prefLocation = preferences.getPreference(CalendarPreference.CREATE_LOCATION);
			if(prefLocation != null)
				societiesEvent.setLocation(prefLocation);
		}
		
		log.debug("Date selected: {}", (Date) selectEvent.getObject());
			
		
	}
	
	public void onEventMove(ScheduleEntryMoveEvent event) {
		
		if(log.isDebugEnabled())
			log.debug("Event was moved!: " + event.getScheduleEvent().getId());

		//addMessage("Event moved","New start date: " + event.getScheduleEvent().getStartDate() +", New end date: " + event.getScheduleEvent().getEndDate());  

        updateEvent(event.getScheduleEvent());

	}
	
	public void onEventResize(ScheduleEntryResizeEvent event) {
		if(log.isDebugEnabled())
			log.debug("Event was resized!");

		//addMessage("Event resized","New start date: " + event.getScheduleEvent().getStartDate() +", New end date: " + event.getScheduleEvent().getEndDate());  

        updateEvent(event.getScheduleEvent());

	}
	
	private void updateEvent(ScheduleEvent scheduleEvent){
		
		CalendarEvent changedEvent = currentEvents.get(scheduleEvent.getId());
		Event updatedEvent = changedEvent.getSocietiesEvent();
		
		Date originalStart = updatedEvent.getStartDate();
		Date originalEnd = updatedEvent.getEndDate();
		
		if(!changedEvent.isMyEvent()){
			log.debug("Trying to change an event we don't own?! We can't do that!");
			ScheduleEvent revertedEvent = new DefaultScheduleEvent(scheduleEvent.getTitle(),originalStart,originalEnd,scheduleEvent.getStyleClass());
			revertedEvent.setId(scheduleEvent.getId());
		
			eventModel.updateEvent(revertedEvent);
			
			addMessage("Event Not Updated!", updatedEvent.getName() + " couldn't be changed, as we don't own it!");

			return;
		}

		
		if(log.isDebugEnabled()){
			log.debug("Original Start Time: " + originalStart);
			log.debug("New start time: " + scheduleEvent.getStartDate());
			log.debug("Original End Time: " + originalEnd);
			log.debug("New end time: " + scheduleEvent.getEndDate());
		}
		
		updatedEvent.setStartDate(scheduleEvent.getStartDate());
		updatedEvent.setEndDate(scheduleEvent.getEndDate());
		
		ICalendarResultCallback callback = new CalendarResultCallback();
		getSharedCalendar().updateEvent(callback , updatedEvent);
		
		Boolean result = callback.getResult().isLastOperationSuccessful();
		
		if(result){
			if(log.isDebugEnabled())
				log.debug("We updated the event successfully!");
			currentEvents.put(scheduleEvent.getId(), new CalendarEvent(updatedEvent,getCalendarName(updatedEvent.getNodeId()),this));
			eventModel.updateEvent(scheduleEvent);
			
			addMessage("Event Updated!", updatedEvent.getName() + " had its date/duration changed!");
		} else{
			if(log.isDebugEnabled())
				log.debug("Problem updating the event!");
			
			ScheduleEvent revertedEvent = new DefaultScheduleEvent(scheduleEvent.getTitle(),originalStart,originalEnd,scheduleEvent.getStyleClass());
			revertedEvent.setId(scheduleEvent.getId());
			
			eventModel.updateEvent(revertedEvent);
			
			addMessage("Event Not Updated!", updatedEvent.getName() + " couldn't be changed!");

		}
	}
	
    public void saveEvent(ActionEvent actionEvent) {
    	
    	if(log.isDebugEnabled()){
    		log.debug("Save event received, event is {}",event.getId());
    		log.debug("event title {}", event.getTitle());
    	}
    	
    	societiesEvent.setTitle(event.getTitle());
    	societiesEvent.setStartDate(event.getStartDate());
    	societiesEvent.setEndDate(event.getEndDate());
        societiesEvent.setCreatorId(getId().getBareJid());
        
		CalendarResultCallback callback = new CalendarResultCallback();

        if(event.getId() == null) {
        	
            log.debug("Adding event {} to Calendar!",societiesEvent.getTitle());
            
    		getSharedCalendar().createEvent(callback, societiesEvent.getSocietiesEvent(), societiesEvent.getNodeId());
    		                        
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
        	
        	Event saveEvent = saveResult.getEvent();
        	
        	currentEvents.put(event.getId(),new CalendarEvent(saveEvent,getCalendarName(saveEvent.getNodeId()),this));
        	log.debug("eventId:" + event.getId());
		}
		else{
			if(log.isDebugEnabled())
				log.debug("Problem saving Event to the calendar!");
			
			addMessage("Event Not Saved","Event was NOT saved to the Calendar!");
		}
		
        event = new DefaultScheduleEvent();
        societiesEvent = new CalendarEvent(this);
    }  
    
    public void deleteEvent(ActionEvent actionEvent) {
    	if(log.isDebugEnabled())
    		log.debug("Delete event called, oh dear!");
    	
    	if(event.getId() == null)
    		return;
    	
		CalendarResultCallback callback = new CalendarResultCallback();
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
    	
		CalendarResultCallback callback = new CalendarResultCallback();
    	getSharedCalendar().subscribeToEvent(callback, societiesEvent.getEventId(), societiesEvent.getNodeId(), getId().getBareJid());
    	SharedCalendarResult result = callback.getResult();
    	
    	if(result.isSubscribingResult()){
    		societiesEvent.setSocietiesEvent(result.getEvent());
    		currentEvents.put(event.getId(), societiesEvent);
    		addMessage("Join Completed","Joined the event " + societiesEvent.getTitle());
    		recommendedEvents.remove(societiesEvent);
    	} else{
    		addMessage("Join failed!","Couldn't join the event " + societiesEvent.getTitle());
    	}
    		
    }
 
    public void leaveEvent(ActionEvent actionEvent){
    	if(log.isDebugEnabled())
    		log.debug("LeavingEvent!");
    	
    	if(societiesEvent == null)
    		return;
    	
		CalendarResultCallback callback = new CalendarResultCallback();
    	getSharedCalendar().unsubscribeFromEvent(callback, societiesEvent.getEventId(), societiesEvent.getNodeId(),getId().getBareJid());
    	SharedCalendarResult result = callback.getResult();
    	
    	if(result.isSubscribingResult()){
    		societiesEvent.setSocietiesEvent(result.getEvent());
    		currentEvents.put(event.getId(), societiesEvent);
    		addMessage("Leave Completed","Left the event " + societiesEvent.getTitle());
    	} else{
    		addMessage("Join failed!","Couldn't leave the event " + societiesEvent.getTitle());
    	}
    		
    }
    
	public void viewRecommendedEvent(){
		if(log.isDebugEnabled())
			log.debug("View recommended event!");
		
		if(recommendedEvent != null){
			
			log.debug("User picked Event: {}", recommendedEvent.getTitle());
			
			ICalendarResultCallback callback = new CalendarResultCallback();
			getSharedCalendar().viewEvent(callback , recommendedEvent.getEventId(), recommendedEvent.getNodeId());
			Event ourEvent = callback.getResult().getEvent();
			if(ourEvent != null){
				recommendedEvent.setSocietiesEvent(ourEvent);
			} else{
				log.warn("Couldn't find {} on the backend?!",recommendedEvent.getTitle());
			}
			
			societiesEvent = recommendedEvent;
			event = new DefaultScheduleEvent(recommendedEvent.getTitle(),recommendedEvent.getStartDate(),recommendedEvent.getEndDate());
			event.setId("recommendedEvent");
			setLocationToggle(false);
			
		} else{
			if(log.isDebugEnabled())
				log.debug("Oops, no event selected?!");
			societiesEvent = new CalendarEvent(this);
			event = new DefaultScheduleEvent();
		}
			
	}
	
	protected void addMessage(String title, String detail) {
		if(log.isDebugEnabled())
			log.debug("Adding a message to the Growl stuff: " + title + " : " + detail);
		
		FacesMessage faceMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, title, detail);
		FacesContext.getCurrentInstance().addMessage("bigrowl", faceMessage );
	}
	
	public void updateUserWarnings(){
		if(log.isDebugEnabled())
			log.debug("Updating user warnings!");
		
		List<UserWarning> userWarnings = getSharedCalendar().getUserWarnings();
		
		for(UserWarning warning: userWarnings){
			addMessage(warning.getTitle(),warning.getDetail());
		}
		
	}
	
	protected String getCalendarName(String nodeId){

		for(Calendar calendar: calendars){
			if(calendar.getNodeId().equals(nodeId))
				return calendar.getName();
		}
		return null;
	}
	

	// SEARCH STUFF
	
	private CalendarEvent searchEvent;
	private List<CalendarEvent> searchResults;
	
	public List<CalendarEvent> getSearchResults() {
		if(searchResults.isEmpty())
			searchResults.add(new CalendarEvent(defaultSearchEvent,this));
		
		return searchResults;
	}

	public void setSearchResults(List<CalendarEvent> searchResults) {
		this.searchResults = searchResults;
	}

	public void setSearchEvent(CalendarEvent searchEvent){
		this.searchEvent = searchEvent;
	}
	
	public CalendarEvent getSearchEvent(){
		if(searchEvent == null){
			log.debug("Search Event was null");
			Event event = new Event();
			event.setEventId("searchEvent");
			
			if("mysubscribedevents".equals(selectedNode)){
				searchEvent = new CalendarEvent(event,"All Calendars",this);
				searchEvent.setNodeId("allCalendars");
				searchCalendarNode = "allCalendars";
			} else{
				searchEvent = new CalendarEvent(event,getCalendarName(selectedNode),this);
				searchEvent.setNodeId(selectedNode);
				searchCalendarNode = selectedNode;
			}
		}
		return searchEvent;
	}
	
	public void prepareEventSearch(ActionEvent actionEvent){
		if(log.isDebugEnabled())
			log.debug("We need to prepare the dialog for the event searching stuff");
		
		log.debug("Selected Node is: {}", selectedNode);
		
		Event event = new Event();
		event.setEventId("searchEvent");
			
		if(selectedNode.equals("mysubscribedevents")){
			searchEvent = new CalendarEvent(event,"All Calendars",this);
			searchEvent.setNodeId("allCalendars");
			searchCalendarNode = "allCalendars";
		} else{
			searchEvent = new CalendarEvent(event,getCalendarName(selectedNode),this);
			searchEvent.setNodeId(selectedNode);
			searchCalendarNode = selectedNode;
		}


		String searchCreator = preferences.getPreference(CalendarPreference.SEARCH_CREATOR);
		if(searchCreator != null)
			searchEvent.setCreatorId(searchCreator);
		
		String searchLocation = preferences.getPreference(CalendarPreference.SEARCH_LOCATION);
		if(searchLocation != null){
			setSearchLocationToggle(false);
			searchEvent.setLocation(searchLocation);
		}
		else{
			if(isSearchLocationToggle()){
				updateLocation();
			}
		}
		
		String searchKeyword = preferences.getPreference(CalendarPreference.SEARCH_KEYWORD);
		if(searchKeyword != null)
			searchEvent.setTitle(searchKeyword);
		
		String searchCalendar = preferences.getPreference(CalendarPreference.SEARCH_CALENDAR);
		if(searchCalendar != null){
			searchEvent.setNodeId(searchCalendar);
			searchCalendarNode = searchCalendar;
		}
		
	}
	
	public void doEventSearch(ActionEvent actionEvent){
		if(log.isDebugEnabled()){
			log.debug("Searching for events that fulfill certain criteria!");
			log.debug("Search Keyword: {}", searchEvent.getTitle());
			log.debug("Search creator: {}", searchEvent.getCreatorId());
			log.debug("Search calendar node: {}", searchCalendarNode);
			log.debug("Search location: {}",searchEvent.getLocation());
			log.debug("Search from date: {}",searchEvent.getStartDate());
			log.debug("Search to date: {}",searchEvent.getEndDate());
		}
		
		// Learning and user action stuff
		if(log.isDebugEnabled())
			log.debug("Now doing the learning stuff");
		searchEvent.setNodeId(searchCalendarNode);
		
		if(searchEvent.getTitle() != null && !searchEvent.getTitle().isEmpty())
			preferences.setPreference(CalendarPreference.SEARCH_KEYWORD,searchEvent.getTitle());
		if(searchEvent.getLocation() != null && !searchEvent.getLocation().isEmpty())
			preferences.setPreference(CalendarPreference.SEARCH_LOCATION,searchEvent.getLocation());
		if(searchEvent.getCreatorId() != null && !searchEvent.getCreatorId().isEmpty())
			preferences.setPreference(CalendarPreference.SEARCH_CREATOR,searchEvent.getCreatorId());
		if(searchEvent.getNodeId() != null && !searchEvent.getNodeId().isEmpty())
			preferences.setPreference(CalendarPreference.SEARCH_CALENDAR,searchEvent.getNodeId());
		
		ICalendarResultCallback calendarResultCallback = new CalendarResultCallback();

		if(searchEvent.getNodeId() == null || searchEvent.getNodeId().equals("allCalendars")){
			if(log.isDebugEnabled())
				log.debug("Searching in all Calendars!");
			if(searchEvent.getTitle() == null)
				searchEvent.setTitle("milonga");
			
			getSharedCalendar().findEventsAll(calendarResultCallback, searchEvent.getSocietiesEvent());
		} else{
			log.debug("Searching in a specific Calendar, for node: {}", searchEvent.getNodeId());
			
			getSharedCalendar().findEventsInCalendar(calendarResultCallback, searchEvent.getNodeId(), searchEvent.getSocietiesEvent());
		}
		
		SharedCalendarResult myResult = calendarResultCallback.getResult();
		List<Event> foundEvents = myResult.getEventList();
		
		log.debug("Found {} events!",foundEvents.size());
		searchResults = new ArrayList<CalendarEvent>(foundEvents.size());
		
		for(Event foundEvent : foundEvents){
			searchResults.add(new CalendarEvent(foundEvent,getCalendarName(foundEvent.getNodeId()),this));
		}
		
		setSelectedEvent(getSearchResults().get(0));
		getPreferences().setPreference(CalendarPreference.CALENDAR_ACTION, "searchEvent");

	}
	
	private CalendarEvent selectedEvent;
	
	public void setSelectedEvent(CalendarEvent selectedEvent){
		log.debug("Set selectedEvent: {} ", selectedEvent);
		this.selectedEvent = selectedEvent;
	}
	
	public CalendarEvent getSelectedEvent(){
		return selectedEvent;
	}

	public void viewSearchEvent(){
		if(log.isDebugEnabled())
			log.debug("View searched event!");
		
		if(selectedEvent != null){
			
			log.debug("User picked Event: {}", selectedEvent.getTitle());
			
			ICalendarResultCallback callback = new CalendarResultCallback();
			getSharedCalendar().viewEvent(callback , selectedEvent.getEventId(), selectedEvent.getNodeId());
			Event ourEvent = callback.getResult().getEvent();
			if(ourEvent != null){
				selectedEvent.setSocietiesEvent(ourEvent);
			} else{
				log.warn("Couldn't find {} on the backend?!",selectedEvent.getTitle());
			}
			
			societiesEvent = selectedEvent;
			event = new DefaultScheduleEvent(societiesEvent.getTitle(),societiesEvent.getStartDate(),societiesEvent.getEndDate());
			event.setId("searchResultEvent");
			setLocationToggle(false);
			
		} else{
			if(log.isDebugEnabled())
				log.debug("Oops, no event selected?!");
			societiesEvent = new CalendarEvent(this);
			event = new DefaultScheduleEvent();
		}
			
	}

	private String searchCalendarNode;
	
	public String getSearchCalendarNode() {
		if(searchEvent != null && searchEvent.getNodeId() != null)
			societiesEventNode = searchEvent.getNodeId();
		
		return searchCalendarNode;
	}

	public void setSearchCalendarNode(String searchCalendarNode) {
		this.searchCalendarNode = searchCalendarNode;
		
		if(searchEvent != null)
			searchEvent.setNodeId(searchCalendarNode);
	}

	private String societiesEventNode;

	public String getSocietiesEventNode() {
		if(societiesEvent != null && societiesEvent.getNodeId() != null)
			societiesEventNode = societiesEvent.getNodeId();

		return societiesEventNode;
	}

	public void setSocietiesEventNode(String societiesEventNode) {
		log.debug("setSocietiesEventNode: {}", societiesEventNode);
		this.societiesEventNode = societiesEventNode;
		if(societiesEvent != null)
			societiesEvent.setNodeId(societiesEventNode);
	}
	
	public void updateLocation(){
		String myLocation = getCalendarContext().getMyLocation();
		log.debug("Setting location to: {}", myLocation);
		
		if(myLocation != null){
					
			if(searchEvent != null)
				searchEvent.setLocation(myLocation);

			if(societiesEvent != null)
				societiesEvent.setLocation(myLocation);

		} else{
			if(searchEvent != null)
				searchEvent.setLocation("Not Available");
			if(societiesEvent != null)
				societiesEvent.setLocation("Not Available");
		}
	}
}
