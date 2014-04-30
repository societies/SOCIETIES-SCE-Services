package si.setcce.societies.crowdtasking.api.RESTful.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Simon on 10.9.2013.
 */
public class CollaborativeSpaceJS {
    Long id;
    String name;
    String urlMapping;
    String symbolicLocation;

    public CollaborativeSpaceJS(JSONObject space) throws JSONException {
            this.id = space.getLong("id");
            this.name = space.getString("name");
            this.urlMapping = space.getString("urlMapping");
            this.symbolicLocation = space.getString("symbolicLocation");
    }
}
