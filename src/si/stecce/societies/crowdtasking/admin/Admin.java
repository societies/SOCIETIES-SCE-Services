/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
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
package si.stecce.societies.crowdtasking.admin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.cmd.Query;
import si.stecce.societies.crowdtasking.model.Event;
import si.stecce.societies.crowdtasking.model.Task;
import si.stecce.societies.crowdtasking.model.TaskStatus;

import static si.stecce.societies.crowdtasking.model.dao.OfyService.ofy;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
public class Admin extends HttpServlet {
	private static final long serialVersionUID = -6134754657162343082L;
	private static final String SENDER = "No Reply <setcce.research@gmail.com>";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		long startTime = System.currentTimeMillis();
		//JavaMail.sendJavaMail(SENDER, "simon.juresa@setcce.si", "hoj", "navaden text", "HTML text", getBody());
		//sendMeetingRequest1();
		//sendMeetingRequest2();
        convertTasks();
		long diff = System.currentTimeMillis() - startTime;
		response.getWriter().write("time: " + diff);
	}

    private void convertTasks() {
        Query<Task> q = ofy().load().type(Task.class);
        for (Task task: q) {
            switch (task.getStatus()) {
                case "open":
                    task.setTaskStatus(TaskStatus.OPEN);
                    break;
                case "inprogress":
                    task.setTaskStatus(TaskStatus.IN_PROGRESS);
                    break;
                case "finished":
                    task.setTaskStatus(TaskStatus.FINISHED);
                    break;
            }

            ofy().save().entity(task);
        }
    }

    private void convertEvents() {
        Query<Event> q = ofy().load().type(Event.class);
        for (Event event: q) {
            event.convertEventText();
            ofy().save().entity(event);
        }
    }

    /*
	private void sendMeetingRequest2() {

		Multipart multipart = new MimeMultipart();
	    try {
			MimeBodyPart plainPart = new MimeBodyPart();
			plainPart.setContent(getBody(), "text/calendar;method=REQUEST");
			multipart.addBodyPart(plainPart);
			
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(getBody(), "text/html");
			multipart.addBodyPart(htmlPart);
			
			MimeBodyPart attPart = new MimeBodyPart();
			attPart.setContent(getBody(), "text/plain");
			attPart.setFileName("meeting.ics");
			multipart.addBodyPart(attPart);
			
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			Message mail = new MimeMessage(session);
			mail.setFrom(new InternetAddress(SENDER));
			mail.addRecipient(Message.RecipientType.TO,
		            new InternetAddress("simon@juresa.si", "Simon Jureša"));
			mail.setSubject("meeting invitation!");
			mail.setContent(multipart);
			Transport.send(mail);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private void sendMeetingRequest1() {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Message msg = new MimeMessage(session);

	    try {
			msg.setFrom(new InternetAddress(SENDER));
		    msg.addRecipient(Message.RecipientType.TO,
		            new InternetAddress("simon@juresa.si", "Simon Jureša"));
		    msg.setSubject("meeting invitation!");
		    msg.addHeader("Content-Type", "text/calendar");
		    msg.setContent(getBody(), "text/calendar;method=REQUEST");

		    Transport.send(msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
*/
	private String getBody() {
		SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat  ("yyyyMMdd'T'HHmmss'Z'");
		
		return "BEGIN:VCALENDAR\n"+
				"VERSION:1.0\n"+
				"BEGIN:VEVENT\n"+
				"DTSTART:"+iCalendarDateFormat.format(new Date())+"\n"+
				"DTEND:"+iCalendarDateFormat.format(new Date())+"\n"+
				"SUMMARY:Hej hoj!\n"+
				"LOCATION:nekje že\n"+
				"DESCRIPTION:opis mitinga\n"+
				"PRIORITY:3\n"+
				"END:VEVENT\n"+
				"END:VCALENDAR\n";
	}
}
