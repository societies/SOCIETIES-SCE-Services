package si.setcce.societies.crowdtasking.api.RESTful;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author: Simon Jureša
 */
public interface ITagAPI {
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    String getTag(@DefaultValue("0") @QueryParam("id") Long id);
}
