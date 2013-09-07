package org.societies.thirdparty.sharedcalendar.web;

import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;


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
import org.societies.thirdparty.sharedcalendar.api.CalendarPreference;
import org.societies.thirdparty.sharedcalendar.api.ISharedCalendar;
import org.societies.thirdparty.sharedcalendar.api.CalendarConverter;
import org.societies.thirdparty.sharedcalendar.api.ICalendarResultCallback;
import org.societies.thirdparty.sharedcalendar.api.UserWarning;
import org.societies.thirdparty.sharedcalendar.api.schema.Calendar;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;
import org.societies.thirdparty.sharedcalendar.api.schema.SharedCalendarResult;


@ManagedBean(name="calendarWebController")
@SessionScoped
public class CalendarController {
	
	private static long _DEADLINE = 3000;
	static final Logger log = LoggerFactory.getLogger(CalendarController.class);
	
	private List<Calendar> calendars;
	private IIdentity myId;
	private ServiceResourceIdentifier mySRI;
	
	private IIdentity getId(){
		
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
	
	private ServiceResourceIdentifier getSRI(){
		
		if(mySRI != null)
			return mySRI;
		
		try{
			mySRI = getServiceMgmt().getMyServiceId(getClass());
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return mySRI;
	}
	
	private HashMap<String, CalendarEvent> currentEvents;

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
	
	@ManagedProperty(value = "#{userActionMonitor}")
	private IUserActionMonitor userActionMonitor;
	
    @SuppressWarnings("UnusedDeclaration")
	public IUserActionMonitor getUserActionMonitor() {
		return userActionMonitor;
	}

    @SuppressWarnings("UnusedDeclaration")
	public void setUserActionMonitor(IUserActionMonitor userActionMonitor) {
		this.userActionMonitor = userActionMonitor;
	}
    
	@ManagedProperty(value = "#{personalisationManager}")
	private IPersonalisationManager personalisationManager;
	
    @SuppressWarnings("UnusedDeclaration")
	public IPersonalisationManager getPersonalisationManager() {
		return personalisationManager;
	}

    @SuppressWarnings("UnusedDeclaration")
	public void setPersonalisationManager(IPersonalisationManager personalisationManager) {
		this.personalisationManager = personalisationManager;
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
    
    @ManagedProperty(value = "#{ctxBroker}")
    private ICtxBroker ctxBroker;
    
    @SuppressWarnings("UnusedDeclaration")
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

    @SuppressWarnings("UnusedDeclaration")
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
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

	
	private boolean locationToggle;
	
	public boolean isLocationToggle() {
		return locationToggle;
	}

	public void setLocationToggle(boolean locationToggle) {
		this.locationToggle = locationToggle;
	}
	
	public String getMyLocation(){
		String myLocation = mycontext.getMyLocation();
		if(myLocation != null)
			return myLocation;
		else
			return "Not Available";
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
		log.debug("Set recommendedEvent: " + recommendedEvent);
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
	
	EventConverter eventConverter;
	private String serviceType;
	private RequestorService requestor;
	private CalendarContextUtils mycontext;
	
	public void setEventConverter(EventConverter eventConverter){
		this.eventConverter = eventConverter;
	}
	
	public EventConverter getEventConverter(){
		return this.eventConverter;
	}
	
	public CalendarController() {
		if(log.isDebugEnabled())
			log.debug("CalendarController");
		
		eventModel = new DefaultScheduleModel();
		eventConverter = new EventConverter(this);
		
		myId = null;
		recommendedEvent = null;
		recommendedEvents = new ArrayList<CalendarEvent>();
		searchResults = new ArrayList<CalendarEvent>();
		allEvents = false;
		agendaToggle = false;
		locationToggle = false;
		
		Event testEvent = new Event();
		testEvent.setCalendarId("teste");
		testEvent.setName("teste");
		testEvent.setLocation("teste");
		testEvent.setDescription("teste");
		recommendedEvents.add(new CalendarEvent(testEvent,this));
		
	}


	@PostConstruct
	public void doPostConstruct(){
		if(log.isDebugEnabled())
			log.debug("postConstruct!");
	}
	
	public void preRender(){
		if(log.isDebugEnabled())
			log.debug("preRenderMethod called!");
		
		try{
			myId = getId();
			if(selectedNode == null){
				
				requestor = new RequestorService(myId, getSRI());
				mycontext = new CalendarContextUtils(getCtxBroker(), myId, commManager);
				serviceType = getServiceMgmt().getMyCategory(getSRI());
				
				getCalendars();
				
				selectedNode = getPreference(CalendarPreference.VIEW_CALENDAR);
				
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
		log.debug("Preference: {}", getPreference(CalendarPreference.VIEW_CALENDAR));
		
		viewCalendar(selectedNode);
			
		updateRecommendedEvents();
		updateUserWarnings();
	}
	
	public void selectCalendar(){
		
		log.debug("Selecting calendar: {}", selectedNode);
		
		if(selectedNode != null){
			
			setPreference(CalendarPreference.VIEW_CALENDAR,selectedNode);
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
		if(log.isDebugEnabled())
			log.debug("We need to update the Calendar we are viewing!");
		
		if(log.isDebugEnabled())
			log.debug("Getting the events for this calendar!");
		
		CalendarResultCallback callback = new CalendarResultCallback();

		if(!nodeId.equals("mysubscribedevents"))
			getSharedCalendar().retrieveEvents(callback, nodeId);
		else
			getSharedCalendar().getSubscribedEvents(callback, getId().getBareJid());
		
		SharedCalendarResult result = callback.getResult();
		List<Event> calendarEvents = result.getEventList();
		
		if(log.isDebugEnabled())
			log.debug("Got the events for the calendar: " + calendarEvents.size());
		
		currentEvents = new HashMap<String,CalendarEvent>();
		eventModel.clear();
		
		for(Event event: calendarEvents){
			log.debug("Adding event: {}", event.getName());
			
			Date startDate = CalendarConverter.asDate(event.getStartDate());
			Date endDate = CalendarConverter.asDate(event.getEndDate());
			ScheduleEvent newEvent = new DefaultScheduleEvent(event.getName(),startDate,endDate);
			eventModel.addEvent(newEvent);
			currentEvents.put(newEvent.getId(),new CalendarEvent(event,getCalendarName(event.getNodeId()),this));
			
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
		
		if(log.isDebugEnabled())
			log.debug("Returned " + recommendedEvents.size() + " recommended Events!");
		
		
		if(recommendedEvents.size() < 1 && currentEvents != null){
			for(CalendarEvent curEvent : currentEvents.values()){
				log.info("curEvent: " + curEvent.getTitle());
				recommendedEvents.add(curEvent);
			}
		}
		
		if(recommendedEvents.size() < 1){
			log.debug("No events?! No way!");
			Event testEvent = new Event();
			testEvent.setCalendarId("teste");
			testEvent.setName("No recommended Events");
			testEvent.setLocation("teste");
			testEvent.setDescription("teste");
			recommendedEvents.add(new CalendarEvent(testEvent,this));
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
		societiesEvent = currentEvents.get(selectedEvent.getId());
		
	}
	
	public void onDateSelect(SelectEvent selectEvent) {
		if(selectedNode == null)
			selectedNode = getId().getBareJid();
		
		event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
		societiesEvent = new CalendarEvent(this);
		societiesEvent.setStartDate((Date) selectEvent.getObject());
		societiesEvent.setEndDate((Date) selectEvent.getObject());
		societiesEvent.setEventId("dateSelectEvent");

		if(!selectedNode.equals("mysubscribedevents")){
			societiesEvent.setNodeId(selectedNode);
			societiesEvent.setCalendarName(getCalendarName(selectedNode));
		} else{
			societiesEvent.setNodeId(myId.getBareJid());
			societiesEvent.setCalendarName(getCalendarName(myId.getBareJid()));
		}
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
		
		Event updatedEvent = currentEvents.get(scheduleEvent.getId()).getSocietiesEvent();
				
		Date originalStart = CalendarConverter.asDate(updatedEvent.getStartDate());
		Date originalEnd = CalendarConverter.asDate(updatedEvent.getEndDate());
		
		if(log.isDebugEnabled()){
			log.debug("Original Start Time: " + originalStart);
			log.debug("New start time: " + scheduleEvent.getStartDate());
			log.debug("Original End Time: " + originalEnd);
			log.debug("New end time: " + scheduleEvent.getEndDate());
		}
		
		updatedEvent.setStartDate(CalendarConverter.asXMLGregorianCalendar(scheduleEvent.getStartDate()));
		updatedEvent.setEndDate(CalendarConverter.asXMLGregorianCalendar(scheduleEvent.getEndDate()));
		
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
    		log.debug("Save event received, event is " + event.getId());
    		log.debug("event title" + event.getTitle());
    	}
    	
    	societiesEvent.setTitle(event.getTitle());
    	societiesEvent.setStartDate(event.getStartDate());
    	societiesEvent.setEndDate(event.getEndDate());
        societiesEvent.setCreatorId(getId().getBareJid());
        
		CalendarResultCallback callback = new CalendarResultCallback();

        if(event.getId() == null) {
        	
            if(log.isDebugEnabled())
            	log.debug("Adding event to Calendar!");
            
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
    	
		CalendarResultCallback callback = new CalendarResultCallback();
    	getSharedCalendar().unsubscribeFromEvent(callback, societiesEvent.getEventId(), getId().getBareJid());
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
		
		if(recommendedEvent != null){
			if(log.isDebugEnabled())
				log.debug("User picked Event: " + recommendedEvent.getTitle());
			societiesEvent = recommendedEvent;
			event = new DefaultScheduleEvent(recommendedEvent.getTitle(),recommendedEvent.getStartDate(),recommendedEvent.getEndDate());
			event.setId("recommendedEvent");
			
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
	
	private String getCalendarName(String nodeId){

		for(Calendar calendar: calendars){
			if(calendar.getNodeId().equals(nodeId))
				return calendar.getName();
		}
		return null;
	}
	
	
	private String getPreference(CalendarPreference preference){
		if(log.isDebugEnabled())
			log.debug("Getting Preference: " + preference);
		
		String result = null;
		
		try{
		
			Future<IAction> actionAsync = getPersonalisationManager().getPreference(requestor, myId, serviceType, mySRI, preference.toString());
			IAction action =actionAsync.get();
			
			if(action != null){
				if(log.isDebugEnabled())
					log.debug("Preference retrieved: " + action.getparameterName() + " =>  " + action.getvalue());
				
				result = action.getvalue();
			} else {
				if(log.isDebugEnabled())
					log.debug("Preference was not retrieved");
			}
		} catch(Exception ex){
			log.error("There was an exception getting a Preference from the Personalisation Manager :" + ex);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	private void setPreference(CalendarPreference preferenceName, String preferenceValue){
		if(log.isDebugEnabled())
			log.debug("Set a Preference ("+preferenceName+','+preferenceValue+')');
		
		//IAction myAction = new Action(getSRI(), serviceType, preferenceName, preferenceValue, false, true, false);
		IAction myAction = new Action(getSRI(), serviceType, preferenceName.toString(), preferenceValue);
		getUserActionMonitor().monitor(getId(), myAction);
	}
	
	// SEARCH STUFF
	
	private CalendarEvent searchEvent;
	private List<CalendarEvent> searchResults;
	
	public List<CalendarEvent> getSearchResults() {
		if(searchResults == null)
			searchResults = new ArrayList<CalendarEvent>();
			
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
			log.debug("Search Event == null");
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
		}
		return searchEvent;
	}
	
	public void prepareEventSearch(ActionEvent actionEvent){
		if(log.isDebugEnabled())
			log.debug("We need to prepare the dialog for the event searching stuff");
		
		log.debug("Selected Node is: " + selectedNode);
		
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


		String searchCreator = getPreference(CalendarPreference.SEARCH_CREATOR);
		if(searchCreator != null)
			searchEvent.setCreatorId(searchCreator);
		
		String searchLocation = getPreference(CalendarPreference.SEARCH_LOCATION);
		if(searchLocation != null)
			searchEvent.setLocation(searchLocation);
		
		String searchKeyword = getPreference(CalendarPreference.SEARCH_KEYWORD);
		if(searchKeyword != null)
			searchEvent.setTitle(searchKeyword);
		
		String searchCalendar = getPreference(CalendarPreference.SEARCH_CALENDAR);
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
		
		if(searchEvent.getTitle() != null)
			setPreference(CalendarPreference.SEARCH_KEYWORD,searchEvent.getTitle());
		if(searchEvent.getLocation() != null)
			setPreference(CalendarPreference.SEARCH_LOCATION,searchEvent.getLocation());
		if(searchEvent.getCreatorId() != null)
			setPreference(CalendarPreference.SEARCH_CREATOR,searchEvent.getCreatorId());
		if(searchEvent.getNodeId() != null)
			setPreference(CalendarPreference.SEARCH_CALENDAR,searchEvent.getNodeId());
		
		ICalendarResultCallback calendarResultCallback = new CalendarResultCallback();

		if(searchEvent.getNodeId() == null || searchEvent.getNodeId().equals("allCalendars")){
			if(log.isDebugEnabled())
				log.debug("Searching in all Calendars!");
			if(searchEvent.getTitle() == null)
				searchEvent.setTitle("milonga");
			
			getSharedCalendar().findEventsAll(calendarResultCallback, searchEvent.getSocietiesEvent());
		} else{
			if(log.isDebugEnabled())
				log.debug("Searching in a specific Calendar, for node: " + searchEvent.getNodeId());
			
			getSharedCalendar().findEventsInCalendar(calendarResultCallback, searchEvent.getNodeId(), searchEvent.getSocietiesEvent());
		}
		
		SharedCalendarResult myResult = calendarResultCallback.getResult();
		List<Event> foundEvents = myResult.getEventList();
		
		if(log.isDebugEnabled())
			log.debug("Found " + foundEvents.size() + " events!");
		searchResults = new ArrayList<CalendarEvent>(foundEvents.size());
		
		for(Event foundEvent : foundEvents){
			searchResults.add(new CalendarEvent(foundEvent,getCalendarName(foundEvent.getNodeId()),this));
		}
		
		if(searchResults.isEmpty()){
			CalendarEvent placeholderEvent = new CalendarEvent(this);
			placeholderEvent.setCalendarName("");
			placeholderEvent.setTitle("No search results...");
			searchResults.add(placeholderEvent);
		}
		
		setSelectedEvent(searchResults.get(0));
		
	}
	
	private CalendarEvent selectedEvent;
	
	public void setSelectedEvent(CalendarEvent selectedEvent){
		log.debug("Set selectedEvent: " + selectedEvent);
		this.selectedEvent = selectedEvent;
	}
	
	public CalendarEvent getSelectedEvent(){
		return selectedEvent;
	}
	
	public void viewSearchEvent(){
		if(log.isDebugEnabled())
			log.debug("View searched event!");
		
		if(selectedEvent != null){
			if(log.isDebugEnabled())
				log.debug("User picked Event: " + selectedEvent.getTitle());
			societiesEvent = selectedEvent;
			event = new DefaultScheduleEvent(societiesEvent.getTitle(),societiesEvent.getStartDate(),societiesEvent.getEndDate());
			event.setId("searchResultEvent");
			
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
		log.debug("getSocietiesEventNode: " + societiesEventNode);

		return societiesEventNode;
	}

	public void setSocietiesEventNode(String societiesEventNode) {
		log.debug("setSocietiesEventNode: " + societiesEventNode);
		this.societiesEventNode = societiesEventNode;
		if(societiesEvent != null)
			societiesEvent.setNodeId(societiesEventNode);
	}
	
	public void updateLocation(){
		if(log.isDebugEnabled())
			log.debug("Updating location for: " + event);
		
		if(true){
			String myLocation = mycontext.getMyLocation();
			if(myLocation != null){
				if(log.isDebugEnabled())
					log.debug("Setting location to: " + myLocation);
				searchEvent.setLocation(myLocation);
			} else{
				searchEvent.setLocation("Not Available");
			}
		}
	}
}
