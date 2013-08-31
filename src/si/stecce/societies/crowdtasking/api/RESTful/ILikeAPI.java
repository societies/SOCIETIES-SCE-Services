package si.stecce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author: Simon Jureša
 */
public interface ILikeAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON })
    String getLike(@PathParam("querytype") String querytype,
                   @QueryParam("vwTaskId") Long taskId,
                   @QueryParam("commentId") Long commentId,
                   @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response setLike(@PathParam("querytype") String querytype,
                     @FormParam("vwTaskId") Long taskId,
                     MultivaluedMap<String, String> formParams,
                     @Context HttpServletRequest request);
}
