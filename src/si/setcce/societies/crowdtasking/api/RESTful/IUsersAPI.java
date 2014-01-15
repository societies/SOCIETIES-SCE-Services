package si.setcce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author: Simon Jureša
 */
public interface IUsersAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    String getUser(@PathParam("querytype") String querytype,
                   @DefaultValue("0") @QueryParam("limit") int limit,
                   @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response setUser(@PathParam("querytype") String querytype,
                     @FormParam("action") String action,
                     @FormParam("fname") String fname,
                     @FormParam("lname") String lname,
                     @FormParam("email") String email,
                     @FormParam("interests") String interests,
                     @FormParam("executeTask") String executeTask,
                     @FormParam("finalizeTask") String finalizeTask,
                     @FormParam("likeTask") String likeTask,
                     @FormParam("likeComment") String likeComment,
                     @FormParam("newTaskInCommunity") String newTaskInCommunity,
                     @FormParam("joinCommunityRequest") String joinCommunityRequest,
                     @FormParam("newComment") String newComment,
                     @FormParam("picUrl") String picUrl,
                     @FormParam("timeout") long timeout,
                     @FormParam("trustRelationships") String trustRelationshipsJSON,
                     @FormParam("symbolicLocation") String symbolicLocation,
                     @FormParam("userId") String userId,
                     @Context HttpServletRequest request);
}
