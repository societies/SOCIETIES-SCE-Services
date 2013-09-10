package si.stecce.societies.crowdtasking.api.RESTful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author: Simon Jureša
 */
public interface ICommunityAPI {
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    String getCommunity(@PathParam("querytype") String querytype,
                        @QueryParam("communityId") Long communityId,
                        @QueryParam("ownerJid") String ownerJid,
                        @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response createCommunity(
            @PathParam("querytype") String querytype,
            @FormParam("userJid") String userJid,
            @FormParam("communityId") String communityId,
            @FormParam("communityJid") String communityJid,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("spaces") List<Long> csIds,
            @FormParam("members") List<Long> members,
            @FormParam("memberId") Long memberId,
            @Context HttpServletRequest request);
}
