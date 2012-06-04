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
package org.societies.rdPartyService.enterprise.sharedCalendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

/**
 * Describe your class here...
 * 
 * @author solutanet
 * TODO Google is changing API once a week. Change the deprecated method invocation.
 */
public class SharedCalendarUtil {

	private  String clientId;
	private  String clientSecret;
	private  String accessToken;
	private  String refreshToken;
	private  Calendar service = null;
	private Properties props;
	private static Logger log=LoggerFactory.getLogger(SharedCalendarUtil.class);

	
	
	// For test purpose only
	private static final String testCalendarId = "soluta.net_n1i86mmq647g7pmc573uslm1d4@group.calendar.google.com";

	// Test main
	public static void main(String[] argj) throws Exception {
		SharedCalendarUtil testCalendar = new SharedCalendarUtil();
		testCalendar.setUp();
		// testCalendar.retrieveAllCalendar();
		//testCalendar.retrieveAllEvents(testCalendarId);
		// testCalendar.createEvent(testCalendarId, "eventXX",
		// "description eventxx", new Date(), new Date(), "Attendee Name",
		// "toni@toni.com");
		// testCalendar.findEventsUsingQuery(testCalendarId, "eventXX");
		String calendarId=testCalendar.createCalendar("Summary");
		System.out.println(calendarId);
		
	}
	
	/**
	 * Constructor that initialize the class field
	 */
	public SharedCalendarUtil(){
		this.readAndSetProperties();
		this.setUp();
	}

	/**
	 * @param clientId
	 * @param clientSecret
	 * @param accessToken
	 * @param refreshToken
	 */
	public SharedCalendarUtil(String clientId, String clientSecret,
			String accessToken, String refreshToken) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.setUp();
	}

	/**
	 * Utility methods
	 */
	
	

	/**
	 * This method returns all calendars of the user associated to the Google account
	 * @return
	 * @throws Exception
	 */
	protected List<CalendarListEntry> retrieveAllCISCalendar(String CISId) throws Exception {
		CalendarList calendarList = service.calendarList().list().execute();
		List<CalendarListEntry> returnedCalendarList = new ArrayList<CalendarListEntry>();
		while (true) {
			for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
//				log.info(calendarListEntry.getSummary());
//				log.info(calendarListEntry.getDescription());
//				log.info(calendarListEntry.getLocation());
				returnedCalendarList.add(calendarListEntry);
			}
			String pageToken = calendarList.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				calendarList = service.calendarList().list()
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}
		
		return returnedCalendarList;
	}

	protected List<Event> retrieveAllEvents(String calendarId)
			throws IOException {
		com.google.api.services.calendar.model.Events events = service.events()
				.list(calendarId).execute();
		List<Event> returnedList = new ArrayList<Event>();
		while (true) {
			for (Event event : events.getItems()) {
//				log.info(event.getSummary());
//				log.info(event.getAttendees().get(0).getDisplayName());
//				log.info(event.getDescription());
//				log.info(event.getId());
//				log.info(event.getLocation());
//				log.info(event.getStart().toString());
//				log.info(event.getEnd().toString());
				returnedList.add(event);
			}
			String pageToken = events.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				events = service.events().list(calendarId)
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}
		return returnedList;
	}
	
	/**
	 * This method is used to create a calendar
	 * @param calendarSummary
	 * @return the calendarId
	 * @throws IOException
	 */
	protected String createCalendar(String calendarSummary) throws IOException{
		com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();

		calendar.setSummary(calendarSummary);
		
		
		//calendar.setTimeZone("Rome");

		com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(calendar).execute();
	return createdCalendar.getId();
	}
	
	

	/**
	 * This method is used to create an event inside a specified calendar
	 * the method is not exposed to the Societies clients but can be used only by the 3rd party service provider
	 * @param calendarId
	 * @param eventTitle
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @param attendeeName
	 * @param attendeeEmail
	 * @return
	 * @throws IOException
	 */
	protected String createEvent(String calendarId, String eventTitle,
			String description, Date startDate, Date endDate,String location) throws IOException {
		Event event = new Event();
		event.setSummary(eventTitle);
		event.setDescription(description);

		// set dates

		DateTime start = new DateTime(startDate,TimeZone.getDefault());
		event.setStart(new EventDateTime().setDateTime(start));

		DateTime end = new DateTime(endDate, TimeZone.getDefault());
		event.setEnd(new EventDateTime().setDateTime(end));

		event.setLocation(location);
		// Store event
		Event createdEvent = service.events().insert(calendarId, event)
				.execute();

		return createdEvent.getId();
	}

	/**
	 * This method retrieve events inside a calendar using keyword
	 * @param calendarId
	 * @param query
	 * @return
	 * @throws IOException
	 */
	protected List<Event> findEventsUsingQuery(String calendarId, String query)
			throws IOException {
		Events events = service.events().list(calendarId).setQ(query).execute();
		List<Event> returnedEventList = new ArrayList<Event>();
		for (Event event : events.getItems()) {
//			log.info(event.getSummary());
//			log.info(event.getDescription());
			returnedEventList.add(event);
		}
		return returnedEventList;
	}

	/**
	 * This method finds and event using its id
	 * @param calendarId
	 * @param eventId
	 * @return
	 * @throws IOException
	 */
	protected Event findEventUsingId(String calendarId, String eventId)
			throws IOException {

		Event event = service.events().get(calendarId, eventId).execute();

//		log.info(event.getSummary());
		return event;
	}
	
	/**
	 * Update an existing event
	 * @throws IOException 
	 */
	protected void updateEvent(String calendarId,Event eventToUpdate ) throws IOException{
		

		Event updatedEvent = service.events().update(calendarId, eventToUpdate.getId(), eventToUpdate).execute();
	}
	
	/**
	 * Delete an existing calendar
	 * @param calendarId
	 * @throws IOException
	 */
	protected void deleteCalendar(String calendarId) throws IOException{
		service.calendars().delete(calendarId).execute();
	}
	
	protected void deleteEvent(String calendarId, String eventId) throws IOException{
		service.events().delete(calendarId, eventId).execute();
	}
	
	
	
	
	/**
	 * This method is used to set up the token used to communicate with Google bakend
	 * Tokens are read from the properties file backEnd.properties inside Meta-INF/conf
	 * The properties that have to be set up before start the application are clientId and clientSecret taken from
	 * the google API console (https://code.google.com/apis/console).
	 * The first time the application starts the authorization code must be supplied following the instructions on the console. 
	 */
	protected void setUp() {
		
try{
			HttpTransport httpTransport = new NetHttpTransport();
			JacksonFactory jsonFactory = new JacksonFactory();
			GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
					accessToken, httpTransport, jsonFactory, clientId,
					clientSecret, refreshToken);

			Calendar tmpService = Calendar.builder(httpTransport, jsonFactory)
					.setApplicationName("SCalendar")
					.setHttpRequestInitializer(accessProtectedResource).build();
			service = tmpService;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	/**
	 * This method is called when tokens are not specified inside the backEnd.properties file.
	 * 
	 * @param properties
	 * @param path
	 * @throws Exception
	 */
	protected void setupAuthorization(Properties properties, URL path)
			throws Exception {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();

		// The clientId and clientSecret are copied from the API Access tab on
		// the Google APIs Console

		// Or your redirect URL for web based applications.
		String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
		String scope = "https://www.googleapis.com/auth/calendar";

		// Step 1: Authorize -->
		String authorizationUrl = new GoogleAuthorizationRequestUrl(clientId,
				redirectUrl, scope).build();

		// Point or redirect your user to the authorizationUrl.
		log.info("Go to the following link in your browser:");
		log.info(authorizationUrl);

		// Read the authorization code from the standard input stream.
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		log.info("What is the authorization code?");
		String code = in.readLine();
		// End of Step 1 <--

		// Step 2: Exchange -->
		AccessTokenResponse response = new GoogleAuthorizationCodeGrant(
				httpTransport, jsonFactory, clientId, clientSecret, code,
				redirectUrl).execute();
		// End of Step 2 <--

		// Set and store tokens
		accessToken = response.accessToken;
		refreshToken = response.refreshToken;
		properties.setProperty("accessToken", accessToken);
		properties.setProperty("refreshToken", refreshToken);
		properties.store(new FileOutputStream(new File(path.getPath()), true),
				null);

	}
	
	/**
	 * Read properties from a file
	 * Used to test the library outside an Osgi container
	 */
	private void readAndSetProperties(){
		props = new Properties();
		URL url = ClassLoader
				.getSystemResource("META-INF/spring/backEnd.properties");
		InputStream inputStream = null;
		try {
			inputStream = url.openStream();
			props.load(inputStream);

			clientId = props.getProperty("clientId");
			clientSecret = props.getProperty("clientSecret");
			accessToken = props.getProperty("accessToken");
			refreshToken = props.getProperty("refreshToken");

			if (accessToken == null || refreshToken == null || accessToken.equalsIgnoreCase("")|| refreshToken.equalsIgnoreCase("")) {
				setupAuthorization(props, url);
			}}catch (Exception e) {
				// TODO: handle exception
			}finally {
				if (inputStream != null)
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	}}
	
	
}
