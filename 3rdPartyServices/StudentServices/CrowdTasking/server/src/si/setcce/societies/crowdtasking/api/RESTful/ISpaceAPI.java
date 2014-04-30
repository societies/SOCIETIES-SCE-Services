package si.setcce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author: Simon Jureša
 */
public interface ISpaceAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    String getSpace(@DefaultValue("0") @QueryParam("id") Long id,
                    @DefaultValue("0") @QueryParam("userId") Long userId,
                    @QueryParam("scope") String scope,
                    @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response newSpace(@FormParam("communityId") Long communityId,
                      @FormParam("spaceId") Long spaceId,
                      @FormParam("spaceName") String name,
                      @FormParam("scope") String scope,
                      @FormParam("symbolicLocation") String symbolicLocation,
                      @Context HttpServletRequest request) throws IOException, URISyntaxException;
}
