package si.stecce.societies.crowdtasking.api.RESTful;

import static si.stecce.societies.crowdtasking.model.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import si.stecce.societies.crowdtasking.model.Tag;

import com.google.gson.Gson;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.cmd.Query;

@Path("/tag")
public class TagAPI {
	@GET
	@Produces({MediaType.APPLICATION_JSON })
	public String getTask(@DefaultValue("0") @QueryParam("id") Long id) {
		Gson gson = new Gson();
		if (id != 0) {
			Tag tag = null;
			try {
				tag = ofy().load().type(Tag.class).id(id).get();
			} catch (NotFoundException e) {}
			return gson.toJson(tag);
		}
		List<Tag> list = getTags();
		return gson.toJson(list);
	}

	public static List<Tag> getTags() {
		Query<Tag> q = ofy().load().type(Tag.class);
		ArrayList<Tag> list = new ArrayList<Tag>();
		for (Tag tag: q) {
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
		for (Tag tag: tags) {
			tagMap.put(tag.getTagName(), tag);
		}
		return tagMap;
	}
	
	//public static 
}
