package si.stecce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author: Simon Jureša
 */
public interface IEventAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON })
    String getEvents(@QueryParam("communityId") Long communityId,
                     @QueryParam("spaceId") Long spaceId,
                     @DefaultValue("8") @QueryParam("limit") int limit,
                     @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response logTaskPickedFromPD(@FormParam("eventType") String eventType,
                                 @DefaultValue("-1") @FormParam("taskId") Long taskId,
                                 @Context HttpServletRequest request);
}
