package si.setcce.societies.crowdtasking;

import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.*;
import javax.mail.internet.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class JavaMail {
    private JavaMail() {
    }

    public static String sendJavaMail(String from, String recipient, String subject, String msgBody,
                                      String htmlBody, String attachmentData) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject(subject);
            msg.setText(msgBody);
            if (attachmentData != null) {
                Multipart mp = new MimeMultipart();

                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent("<html><body>" + htmlBody + "</body></html>", "text/html");
                mp.addBodyPart(htmlPart);

                MimeBodyPart attachment = new MimeBodyPart();
                //attachment.setFileName("meeting.ics");
                msg.addHeader("Content-Class", "urn:content-classes:calendarmessage");
                msg.addHeader("Content-ID", "calendar_message");
                msg.setHeader("Content-Transfer-Encoding", "8bit");
                attachment.setContent(attachmentData, "text/calendar;charset=UTF-8;name=meeting.ics;method=REQUEST");
                //attachment.setContent(attachmentData, "text/calendar");
                mp.addBodyPart(attachment);

                msg.setContent(mp);
            }

            System.out.println("Sending email to: " + recipient);
            Transport.send(msg);
            System.out.println("Message sent.");
            return "Ok";

        } catch (AddressException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (MessagingException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static void sendMeetingRequest(String from, String recipient, String subject) {
        System.out.println("sendMeetingRequest");

        //register the text/calendar mime type
        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap) MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        //register the handling of text/calendar mime type
        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar; x-java-content-handler=com.sun.mail.handlers.text_plain");

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            // Create an alternative Multipart
            Multipart multipart = new MimeMultipart("alternative");

            //part 1, html text
            BodyPart messageBodyPart = buildHtmlTextPart();
            multipart.addBodyPart(messageBodyPart);

            // Add part two, the calendar
            BodyPart calendarPart = buildCalendarPart();
            multipart.addBodyPart(calendarPart);

            //Put the multipart in message
            message.setContent(multipart);

            // send the message
            //Transport.send(msg);
            System.out.println("Sending email to: " + recipient);
            Transport.send(message);
            System.out.println("Message sent.");
        } catch (AddressException e) {
            System.out.println("Error:" + e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Error:" + e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static BodyPart buildHtmlTextPart() throws MessagingException {

        MimeBodyPart descriptionPart = new MimeBodyPart();

        //Note: even if the content is specified as being text/html, outlook won't read correctly tables at all
        // and only some properties from div:s. Thus, try to avoid too fancy content
        String content = "<font size='2'>simple meeting invitation</font>";
        descriptionPart.setContent(content, "text/calendar; charset=utf-8");

        return descriptionPart;
    }

    //define somewhere the icalendar date format
    private static SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");

    private static BodyPart buildCalendarPart() throws Exception {

        BodyPart calendarPart = new MimeBodyPart();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 3);
        Date end = cal.getTime();

        //check the icalendar spec in order to build a more complicated meeting request
        String calendarContent =
                "BEGIN:VCALENDAR\n" +
                        "METHOD:REQUEST\n" +
                        "PRODID: BCP - Meeting\n" +
                        "VERSION:2.0\n" +
                        "BEGIN:VEVENT\n" +
                        "DTSTAMP:" + iCalendarDateFormat.format(start) + "\n" +
                        "DTSTART:" + iCalendarDateFormat.format(start) + "\n" +
                        "DTEND:" + iCalendarDateFormat.format(end) + "\n" +
                        "SUMMARY:test request\n" +
                        "UID:324\n" +
                        "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE:MAILTO:setcce.researchr@gmail.com\n" +
                        "ORGANIZER:MAILTO:setcce.researchr@gmail.com\n" +
                        "LOCATION:on the net\n" +
                        "DESCRIPTION:learn some stuff\n" +
                        "SEQUENCE:0\n" +
                        "PRIORITY:5\n" +
                        "CLASS:PUBLIC\n" +
                        "STATUS:CONFIRMED\n" +
                        "TRANSP:OPAQUE\n" +
                        "BEGIN:VALARM\n" +
                        "ACTION:DISPLAY\n" +
                        "DESCRIPTION:REMINDER\n" +
                        "TRIGGER;RELATED=START:-PT00H15M00S\n" +
                        "END:VALARM\n" +
                        "END:VEVENT\n" +
                        "END:VCALENDAR";

        calendarPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
        calendarPart.addHeader("Content-ID", "calendar_message");
        calendarPart.setContent(calendarContent, "text/calendar;method=REQUEST;name=meeting.ics");
        calendarPart.setFileName("meeting.ics");

        return calendarPart;
    }
}
