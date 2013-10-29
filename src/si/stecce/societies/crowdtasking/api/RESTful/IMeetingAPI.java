package si.stecce.societies.crowdtasking.api.RESTful;

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
    @Produces({MediaType.APPLICATION_JSON })
    String getMeeting(@PathParam("querytype") String querytype,
                      @QueryParam("id") Long meetingId,
                      @QueryParam("csId") Long csId);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response newMeeting(@PathParam("querytype") String querytype,
                        @FormParam("taskId") Long taskId,
                        @FormParam("meetingSubject") String meetingSubject,
                        @FormParam("meetingDescription") String meetingDescription,
                        @FormParam("meetingCS") Long csId,
                        @FormParam("taskStart") String taskStart,
                        @FormParam("taskEnd") String taskEnd,
                        @FormParam("meetingId4CommSign") Long meetingId4CommSign,
                        @FormParam("downloadUrl") String downloadUrl,
                        @Context HttpServletRequest request);
}
