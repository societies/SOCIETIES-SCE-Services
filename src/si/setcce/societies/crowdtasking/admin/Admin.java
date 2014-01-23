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
package si.setcce.societies.crowdtasking.admin;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import si.setcce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.setcce.societies.crowdtasking.model.*;
import si.setcce.societies.crowdtasking.model.dao.CommunityDAO;
import si.setcce.societies.crowdtasking.model.dao.TaskDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static si.setcce.societies.crowdtasking.model.dao.OfyService.ofy;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
public class Admin extends HttpServlet {
    private static final long serialVersionUID = -6134754657162343082L;
    private static final String SENDER = "No Reply <setcce.research@gmail.com>";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        response.getWriter().write("<html><body>");

        String action = request.getParameter("action");
        String kind = request.getParameter("kind");
        if ("admin".equalsIgnoreCase(action)) {
            response.sendRedirect("/admin.html");
            return;
        }
        if ("count".equalsIgnoreCase(action)) {
            int count = ofy().load().type(Event.class).count();
            response.getWriter().write("count: " + count);
        }
        if ("clean".equalsIgnoreCase(action)) {
            if ("task".equalsIgnoreCase(kind)) {
                String id = request.getParameter("id");
                deleteTask(new Long(id));
            }
/*
            List<Community> communities = ofy().load().type(Community.class).list();
            for (Community community : communities) {
                Set<Ref<CTUser>> membersRefs = community.getMembers();
                if (membersRefs != null) {
                    List<CTUser> removeUsers = new ArrayList();
                    for (Ref<CTUser> userRef : membersRefs) {
                        CTUser user = UsersAPI.getUser(userRef);
                        if (!"".equalsIgnoreCase(user.getSocietiesEntityId())) {
                            removeUsers.add(user);
                        }
                    }
                    if (removeUsers.size() > 0) {
                        for (CTUser user : removeUsers) {
                            membersRefs.remove(Ref.create(Key.create(CTUser.class, user.getId())));
                        }
                        community.setMembers(membersRefs);
                        community.setModified(new Date());
                        CommunityDAO.saveCommunity(community);
                    }
                }
            }
*/
        }
        if ("loadCommunities4User".equalsIgnoreCase(action)) {
            Long userId = new Long(request.getParameter("user"));
            CTUser user = UsersAPI.getUserById(userId);
            List<Community> communities = CommunityDAO.loadCommunities4User(user);
            List<Ref<Community>> communityRefs = new ArrayList<>();
            for (Community community : communities) {
                communityRefs.add(Ref.create(Key.create(Community.class, community.getId())));
            }
            response.getWriter().write("communities: " + communities.size());
            response.getWriter().write("<br>communityRefs: " + communityRefs.size());
            communityRefs = new ArrayList<>();
            for (Community community : communities) {
                response.getWriter().write("<br>Community: " + community.getName());
                if (community.getMembers().contains(Ref.create(Key.create(CTUser.class, user.getId())))) {
                    communityRefs.add(Ref.create(Key.create(Community.class, community.getId())));
                }
                for (Ref<CTUser> userRef : community.getMembers()) {
                    response.getWriter().write("<br>User: " + UsersAPI.getUser(userRef).getId());
                    if (UsersAPI.getUser(userRef).getId().longValue() == user.getId().longValue()) {
                        response.getWriter().write(" <-- naš user");
                    }
                }
            }
            response.getWriter().write("<br>communityRefs2: " + communityRefs.size());
        }


        //System.out.println("kind: "+kind);
        //JavaMail.sendJavaMail(SENDER, "simon.juresa@setcce.si", "hoj", "navaden text", "HTML text", getBody());
        //sendMeetingRequest1();
        //sendMeetingRequest2();
//        response.getWriter().write("refreshCommunities (4th)...");
//        refreshCommunities();
        //convertTasks();
        //convertEvents();
        long diff = System.currentTimeMillis() - startTime;
        response.getWriter().write("<br><br>time: " + diff);
        response.getWriter().write("<br><br><a href='/admin.html'>admin</a>");
        response.getWriter().write("</body></html>");
    }

    private void deleteTask(Long taskId) {
        // task
        Task task = TaskDao.loadTask(taskId);
        if (task == null) {
            return;
        }
        // comments
        List<Comment> comments = ofy().load().type(Comment.class).filter("taskRef", Ref.create(Key.create(Task.class, taskId))).list();
        // likes
        deleteLikes(ofy().load().type(Like.class).filter("taskId", taskId).list());
        for (Comment comment : comments) {
            deleteLikes(ofy().load().type(Like.class).filter("commentId", comment.getId()).list());
            ofy().delete().entity(comment);
        }
        // meetings
        for (Meeting meeting : task.getMeetings()) {
            System.out.println(meeting);
            ofy().delete().entity(meeting);
        }
        ofy().delete().entity(task);
    }

    private void deleteLikes(List<Like> likes) {
        for (Like lajk : likes) {
            ofy().delete().entity(lajk);
            System.out.println(lajk);
        }
    }

    private void refreshCommunities() {
        for (Community community : ofy().load().type(Community.class).list()) {
            CommunityDAO.saveCommunity(community);
        }
    }

    private void convertTasks() {
        Query<Task> q = ofy().load().type(Task.class);
        for (Task task : q) {
            if (task.getStatus() != null) {
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
            }
            List<String> communityJids = task.getCommunityJids();
            List<Ref<Community>> communityJidRefs = new ArrayList<>();
            if (communityJids != null) {
                for (String jid : communityJids) {
                    communityJidRefs.add(Ref.create(Key.create(Community.class, jid)));
                }
                task.setCommunityJidRefs(communityJidRefs);
            }
            ofy().save().entities(task);
/*
            List<Community> communities = task.getCommunities();
            for (Community community:communities) {
                community.addTask(task);
                ofy().save().entity(community);
            }
*/
        }
    }

    private void convertEvents() {
        Query<Event> q = ofy().load().type(Event.class);
        int i = 1;
        for (Event event : q) {
            System.out.println(i + ":" + event.getId());
            event.convertEventText();
            ofy().save().entity(event);
            i++;
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
        SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

        return "BEGIN:VCALENDAR\n" +
                "VERSION:1.0\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:" + iCalendarDateFormat.format(new Date()) + "\n" +
                "DTEND:" + iCalendarDateFormat.format(new Date()) + "\n" +
                "SUMMARY:Hej hoj!\n" +
                "LOCATION:nekje že\n" +
                "DESCRIPTION:opis mitinga\n" +
                "PRIORITY:3\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR\n";
    }
}
