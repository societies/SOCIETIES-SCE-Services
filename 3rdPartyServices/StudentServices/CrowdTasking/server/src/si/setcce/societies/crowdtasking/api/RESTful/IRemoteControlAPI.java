package si.setcce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author: Simon Jureša
 */
public interface IRemoteControlAPI {
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    Response get(@PathParam("querytype") String querytype,
                 @DefaultValue("") @QueryParam("page") String page,
                 @DefaultValue("") @QueryParam("taskId") String taskId,
                 @DefaultValue("") @QueryParam("channelId") String channelId,
                 @DefaultValue("") @QueryParam("communityId") String communityId,
                 @Context HttpServletRequest request);
}
