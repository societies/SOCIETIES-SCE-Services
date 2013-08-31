package si.stecce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author: Simon Jureša
 */
public interface ICommentAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON })
    String getComment(@QueryParam("taskId") Long taskId,
                      @DefaultValue("false") @QueryParam("execution") boolean execution,
                      @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response newComment(@FormParam("vwTaskId") Long taskId,
                        @FormParam("commentText") String commentText,
                        @FormParam("execution") boolean execution,
                        @Context HttpServletRequest request,
                        @Context HttpServletResponse servletResponse) throws IOException, URISyntaxException;
}
