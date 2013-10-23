package si.stecce.societies.crowdtasking.api.RESTful.impl;

import si.stecce.societies.crowdtasking.model.CTUser;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: juresas
 * Date: 22.10.2013
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
@Path("/gcm")
public class GcmRegistrationAPI {
    @GET
    public Response getTask(@QueryParam("registrationId") String regId, @Context HttpServletRequest request) {
        CTUser user = UsersAPI.getLoggedInUser(request.getSession());
        if (regId != null && !"".equalsIgnoreCase(regId)) {
            user.setGcmRegistrationId(regId);
            UsersAPI.saveUser(user);
            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Registration id is required.").type("text/plain").build();
    }
}
