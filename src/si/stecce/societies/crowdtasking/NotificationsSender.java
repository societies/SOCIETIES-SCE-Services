/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druĹľbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAĂ‡Ă�O, SA (PTIN), IBM Corp., 
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
package si.stecce.societies.crowdtasking;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import si.stecce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.Comment;
import si.stecce.societies.crowdtasking.model.Community;
import si.stecce.societies.crowdtasking.model.Meeting;
import si.stecce.societies.crowdtasking.model.Task;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
public final class NotificationsSender {
	private static final Logger log = Logger.getLogger(NotificationsSender.class.getName());
	private static final String SENDER = "Crowd Tasking. No Reply <setcce.research@gmail.com>";
	private static SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat  ("yyyyMMdd'T'HHmmss'Z'");

	private NotificationsSender() {}
	
	public static void newMeeting(Meeting meeting, Task task) {
		List<Long> involvedUsers = task.getInvolvedUsers();
		Map<Long, CTUser> usersMap = UsersAPI.getUsersMap(involvedUsers.toArray(new Long[0]));
		for (Long userId:involvedUsers) {
			CTUser user = usersMap.get(userId);
			String organizer = "";
			String organizerEmail = "";
			if (meeting.getOrganizer() != null) {
				organizer = meeting.getOrganizer().getUserName();
				organizerEmail = meeting.getOrganizer().getEmail();
			}
			String eventText = user.getUserName() + " created new meeting "+Util.taskHTMLLink(meeting, task)+" in "+
					meeting.getCollaborativeSpace().getName()+" at "+Util.formatDate(meeting.getStartTime());
			//JavaMail.sendMeetingRequest(SENDER, user.getEmail(), "Let's meet.");
			sendMail(user.getEmail(), "New meeting: "+meeting.getSubject(), "", 
					eventText,
					"BEGIN:VCALENDAR\n"+
					"VERSION:1.0\n"+
					"BEGIN:VEVENT\n"+
					"DTSTAMP:20130108T134500Z\n"+
					"UID:uid4@host1.com\n"+
					"ORGANIZER;CN="+organizer+":MAILTO:"+organizerEmail+"\n"+
					"DTSTART:"+iCalendarDateFormat.format(meeting.getStartTime())+"\n"+
					"DTEND:"+iCalendarDateFormat.format(meeting.getEndTime())+"\n"+
					"SUMMARY:"+meeting.getSubject()+"\n"+
					"LOCATION:"+meeting.getCollaborativeSpace().getName()+"\n"+
					"DESCRIPTION:"+meeting.getDescription()+"\n"+
					"PRIORITY:3\n"+
					"END:VEVENT\n"+
					"END:VCALENDAR");
		}
	}
	
	public static void taskCreated(Task task, Set<CTUser> involvedUsers) {
		for (CTUser user:involvedUsers) {
			if (user.getNotifications().isNewTaskInCommunity()) {
				sendMail(user.getEmail(), "New task '"+task.getTitle()+"' in your community",
						"Hello!\n\rLet as inform you that there is a new task '"+task.getTitle()+"' in your community\n\r"+
						"Here is direct link to a new task: "+Util.taskLink(task.getId())+
						"\n\r\n\nHave a nice day", "", null);
			}
		}
	}
	
	public static void executionStarted(Task task, Set<Long> involvedUsers) {
		Map<Long, CTUser> usersMap = UsersAPI.getUsersMap(involvedUsers.toArray(new Long[0]));
		for (Long userId:involvedUsers) {
			CTUser user = usersMap.get(userId);
			if (user.getNotifications().isExecuteTask()) {
				sendMail(user.getEmail(), "Task '"+task.getTitle()+"' is being executed",
						"Hello!\n\rLet as inform you that task "+task.getTitle()+" is being executed\n\r"+
						"Here is direct link to the task "+Util.taskLink(task.getId())+
						"\n\n\rMessage from task owner: "+task.getExecuteMessage()+
						"\n\r\n\nHave a nice day", "", null);
			}
		}
	}
	
	public static void taskFinalized(Task task, Set<Long> involvedUsers) {
		Map<Long, CTUser> usersMap = UsersAPI.getUsersMap(involvedUsers.toArray(new Long[0]));
		for (Long userId:involvedUsers) {
			CTUser user = usersMap.get(userId);
			if (user.getNotifications().isFinalizeTask()) {
				sendMail(user.getEmail(), "Task '" + task.getTitle()+ "' is being finalized.",
						"Hello!\n\rLet as inform you that task "+task.getTitle()+" is being finalized\n\r"+
						"\n\n\nHave a nice day", "", null);
			}
		}
	}

	public static void taskLiked(Task task) {
		CTUser user = UsersAPI.getUserById(task.getOwnerId());
		if (user.getNotifications().isLikeTask()) {
			sendMail(user.getEmail(), "Someone liked your task '"+task.getTitle()+"'",
					"Hello!\n\rLet us inform you that someone liked your task '"+task.getTitle()+"'."+
					"\n\n\nHave a nice day", "", null);
		}
	}

	public static void commentLiked(Comment comment) {
		Task task = comment.getTask();
		CTUser user = comment.getOwner();
		if (user.getNotifications().isLikeTask()) {
			sendMail(user.getEmail(), "Someone liked your comment on '" + task.getTitle()+ "'",
					"Hello!\n\rLet us inform you that someone liked your comment on task '"+task.getTitle()+"'."+
					"\n\n\nHave a nice day", "", null);
		}
	}
	
	public static void commentOnTaskIParticipate(Task task, Set<Long> involvedUsers) {
		Map<Long, CTUser> usersMap = UsersAPI.getUsersMap(involvedUsers.toArray(new Long[0]));
		for (Long userId:involvedUsers) {
			CTUser user = usersMap.get(userId);
			if (user.getNotifications().isNewComment()) {
				sendMail(user.getEmail(), "New comment on task '" + task.getTitle()+ "'.",
						"Hello!\n\rLet as inform you that there is a new comment on task '"+task.getTitle()+"'.\n\r"+
						"Here is a direct link to the task: "+Util.taskLink(task.getId())+
						"\n\n\nHave a nice day", "", null);
			}
		}
		
	}

	public static void requestToJoinCommunity(Community community, CTUser user) {
		if (user.getNotifications().isJoinCommunityRequest()) {
			sendMail(community.getOwner().getEmail(), "New request to join your community '" + community.getName()+ "'.",
					"Hello!\n\rLet as inform you that user "+user.getUserName()+" wants to join '"+community.getName()+"'.\n\r"+
					"Here is a direct link to the community: "+Util.communityLink(community.getId())+
					"\n\n\nHave a nice day", "", null);
		}
	}

	public static void requestToJoinCommunityApproved(Community community, CTUser member) {
		//if (member.getNotifications().isJoinCommunityRequest()) {
			sendMail(member.getEmail(), "You become a member of community '" + community.getName()+ "'.",
					"Hello!\n\rLet as inform you that community administrator approved your request to join '"+community.getName()+"'.\n\r"+
					"Here is a direct link to the community: "+Util.communityLink(community.getId())+
					"\n\n\nHave a nice day", "", null);
		//}
	}

	private static void sendMail(String recipient, String subject, String body,
			String htmlBody, String attachment) {
		//JavaMail.sendJavaMail(SENDER, recipient, subject, body, htmlBody, attachment);
		
		MailService mailService = MailServiceFactory.getMailService();
		Message mail = new Message(SENDER, recipient, subject, body);
		mail.setHtmlBody(body);
		if (attachment != null) {
			mail.setAttachments(new MailService.Attachment("meeting.ics", attachment.getBytes()));
			JavaMail.sendJavaMail(SENDER, recipient, subject, htmlBody, htmlBody, attachment);
			return;
		}
		try {
			mailService.send(mail);
			log.info("Mail sent to "+recipient+" with subject: "+subject);
		}
		catch (Exception e) {
			log.warning("Error in sendMail() in NotificationsSender class: "+e.getMessage());
		}
	}
}
