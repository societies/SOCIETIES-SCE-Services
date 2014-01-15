package si.setcce.societies.crowdtasking.api.RESTful.impl;

import com.google.gson.Gson;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.cmd.Query;
import si.setcce.societies.crowdtasking.api.RESTful.ITagAPI;
import si.setcce.societies.crowdtasking.model.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static si.setcce.societies.crowdtasking.model.dao.OfyService.ofy;

@Path("/tag")
public class TagAPI implements ITagAPI {
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getTag(@DefaultValue("0") @QueryParam("id") Long id) {
        Gson gson = new Gson();
        if (id != 0) {
            Tag tag = null;
            try {
                tag = ofy().load().type(Tag.class).id(id).get();
            } catch (NotFoundException e) {
            }
            return gson.toJson(tag);
        }
        List<Tag> list = getTags();
        return gson.toJson(list);
    }

    public static List<Tag> getTags() {
        Query<Tag> q = ofy().load().type(Tag.class);
        ArrayList<Tag> list = new ArrayList<Tag>();
        for (Tag tag : q) {
            list.add(tag);
        }
        return list;
    }

    public static Tag getTag(String tagName) {
        return ofy().load().type(Tag.class).filter("tagName", tagName).first().get();
    }

    public static void updateTag(Tag tag) {
        ofy().save().entity(tag);
    }

    public static void updateTags(List<Tag> tags) {
        ofy().save().entities(tags);
    }

    public static Map<String, Tag> getTagsMap() {
        HashMap<String, Tag> tagMap = new HashMap<String, Tag>();
        List<Tag> tags = getTags();
        for (Tag tag : tags) {
            tagMap.put(tag.getTagName(), tag);
        }
        return tagMap;
    }

    //public static
}
