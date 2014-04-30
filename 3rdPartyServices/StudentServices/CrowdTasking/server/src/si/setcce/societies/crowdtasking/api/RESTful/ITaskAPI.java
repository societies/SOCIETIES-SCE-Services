package si.setcce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author: Simon Jureša
 */
public interface ITaskAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    String getTask(@DefaultValue("0") @QueryParam("id") Long id,
                   @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response postTask(
            @FormParam("vwTaskId") String taskId,
            @FormParam("activeComments") List<Long> activeComments,
            @FormParam("inform") List<String> informChannels,
            @FormParam("messageBody") String message,
            @FormParam("action") String action,
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("taskCommunity") List<Long> communities,
            @FormParam("taskCommunityJids") List<String> communityJids,
            @FormParam("taskTags") String tagsString,
            @Context HttpServletRequest request) throws IOException, URISyntaxException;
}
