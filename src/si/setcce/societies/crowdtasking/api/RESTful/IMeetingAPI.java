package si.setcce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author: Simon Jureša
 */
public interface IMeetingAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    String getMeeting(@PathParam("querytype") String querytype,
                      @QueryParam("id") Long meetingId,
                      @QueryParam("csId") Long csId,
                      @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response newMeeting(@PathParam("querytype") String querytype,
                        @FormParam("taskId") Long taskId,
                        @FormParam("meetingSubject") String meetingSubject,
                        @FormParam("meetingDescription") String meetingDescription,
                        @FormParam("meetingCS") Long csId,
                        @FormParam("taskStart") String taskStart,
                        @FormParam("taskEnd") String taskEnd,
                        @FormParam("meetingIdToSign") String meetingIdToSignStr,
                        @FormParam("downloadUrl") String downloadUrl,
                        @QueryParam("registrationId") String regId,
                        @FormParam("minute") String minute,
                        @Context HttpServletRequest request);
}
