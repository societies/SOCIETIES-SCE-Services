package si.stecce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author: Simon Jureša
 */
public interface ITasksAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    String getTasks(@PathParam("querytype") String querytype,
                    @QueryParam("searchString") String searchString,
                    @QueryParam("communityId") Long communityId,
                    @QueryParam("communityJids") String communityJidsJson,
                    @QueryParam("societiesUser") boolean societiesUser,
                    @Context HttpServletRequest request);
}
