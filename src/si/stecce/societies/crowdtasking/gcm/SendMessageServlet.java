package si.stecce.societies.crowdtasking.gcm;

/**
 * Created with IntelliJ IDEA.
 * User: juresas
 * Date: 22.10.2013
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */

import si.stecce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.stecce.societies.crowdtasking.model.ApplicationSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static si.stecce.societies.crowdtasking.model.dao.OfyService.ofy;

/**
 * Servlet that sends a message to a device.
 * <p>
 * This servlet is invoked by AppEngine's Push Queue mechanism.
 */
@SuppressWarnings("serial")
public class SendMessageServlet extends BaseServlet {

    private static final String HEADER_QUEUE_COUNT = "X-AppEngine-TaskRetryCount";
    private static final String HEADER_QUEUE_NAME = "X-AppEngine-QueueName";
    private static final int MAX_RETRY = 3;

    static public final String PARAMETER_DEVICE = "device";
    static public final String PARAMETER_MESSAGE = "message";
    static public final String PARAMETER_MULTICAST = "multicastKey";

    private Sender sender;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        sender = newSender(config);
    }

    /**
     * Creates the {@link Sender} based on the servlet settings.
     */
    protected Sender newSender(ServletConfig config) {
        ApplicationSettings appSettings = UsersAPI.getApplicationSettings();
        if (appSettings.getApiKey() == null) {
            appSettings.setApiKey("AIzaSyDMXKCe9sX8e4thtUaQwBABitdVdabz-Pw");
            ofy().save().entity(appSettings).now();
        }
        String key = appSettings.getApiKey();
        return new Sender(key);
    }

    /**
     * Indicates to App Engine that this task should be retried.
     */
    private void retryTask(HttpServletResponse resp) {
        resp.setStatus(500);
    }

    /**
     * Indicates to App Engine that this task is done.
     */
    private void taskDone(HttpServletResponse resp) {
        resp.setStatus(200);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        super.doGet(req, resp);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Processes the request to add a new message.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (req.getHeader(HEADER_QUEUE_NAME) == null) {
            throw new IOException("Missing header " + HEADER_QUEUE_NAME);
        }
        String retryCountHeader = req.getHeader(HEADER_QUEUE_COUNT);
        logger.fine("retry count: " + retryCountHeader);
        if (retryCountHeader != null) {
            int retryCount = Integer.parseInt(retryCountHeader);
            if (retryCount > MAX_RETRY) {
                logger.severe("Too many retries, dropping task");
                taskDone(resp);
                return;
            }
        }
        String regId = req.getParameter(PARAMETER_DEVICE);
        String message = req.getParameter(PARAMETER_MESSAGE);
        if (regId != null) {
            sendSingleMessage(regId, message, resp);
            return;
        }
        String multicastKey = req.getParameter(PARAMETER_MULTICAST);
        if (multicastKey != null) {
            sendMulticastMessage(multicastKey, message, resp);
            return;
        }
        logger.severe("Invalid request!");
        taskDone(resp);
        return;
    }

    private Message createMessage(String text) {
        Message message = new Message.Builder().addData("text", text).build();
        return message;
    }

    private void sendSingleMessage(String regId, String messageText, HttpServletResponse resp) {
        logger.info("Sending message to device " + regId);
        Message message = createMessage(messageText);
        Result result;
        try {
            result = sender.sendNoRetry(message, regId);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception posting " + message, e);
            taskDone(resp);
            return;
        }
        if (result == null) {
            retryTask(resp);
            return;
        }
        if (result.getMessageId() != null) {
            logger.info("Succesfully sent message to device " + regId);
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // same device has more than on registration id: update it
                logger.finest("canonicalRegId " + canonicalRegId);
                Datastore.updateRegistration(regId, canonicalRegId);
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // application has been removed from device - unregister it
                Datastore.unregister(regId);
            } else {
                logger.severe("Error sending message to device " + regId
                        + ": " + error);
            }
        }
    }

    private void sendMulticastMessage(String multicastKey, String messageText,
                                      HttpServletResponse resp) {
        // Recover registration ids from datastore
        List<String> regIds = Datastore.getMulticast(multicastKey);
        Message message = createMessage(messageText);
        MulticastResult multicastResult;
        try {
            multicastResult = sender.sendNoRetry(message, regIds);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception posting " + message, e);
            multicastDone(resp, multicastKey);
            return;
        }
        boolean allDone = true;
        // check if any registration id must be updated
        if (multicastResult.getCanonicalIds() != 0) {
            List<Result> results = multicastResult.getResults();
            for (int i = 0; i < results.size(); i++) {
                String canonicalRegId = results.get(i).getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    String regId = regIds.get(i);
                    Datastore.updateRegistration(regId, canonicalRegId);
                }
            }
        }
        if (multicastResult.getFailure() != 0) {
            // there were failures, check if any could be retried
            List<Result> results = multicastResult.getResults();
            List<String> retriableRegIds = new ArrayList<String>();
            for (int i = 0; i < results.size(); i++) {
                String error = results.get(i).getErrorCodeName();
                if (error != null) {
                    String regId = regIds.get(i);
                    logger.warning("Got error (" + error + ") for regId " + regId);
                    if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                        // application has been removed from device - unregister it
                        Datastore.unregister(regId);
                    }
                    if (error.equals(Constants.ERROR_UNAVAILABLE)) {
                        retriableRegIds.add(regId);
                    }
                }
            }
            if (!retriableRegIds.isEmpty()) {
                // update task
                Datastore.updateMulticast(multicastKey, retriableRegIds);
                allDone = false;
                retryTask(resp);
            }
        }
        if (allDone) {
            multicastDone(resp, multicastKey);
        } else {
            retryTask(resp);
        }
    }

    private void multicastDone(HttpServletResponse resp, String encodedKey) {
        Datastore.deleteMulticast(encodedKey);
        taskDone(resp);
    }
}