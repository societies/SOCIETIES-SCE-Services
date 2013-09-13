package org.temp;

import org.json.JSONException;
import org.json.JSONObject;
import org.temp.CISIntegeration.CISBridge;
import org.temp.CISIntegeration.CISData;
import java.util.*;
import org.societies.api.activity.IActivity;

public class NearMeActivity implements IActivity{

	private long time;
	private String verb;
	private String actor;
	private String object;
	private String target;
	private String published;

	private Long id;


	public NearMeActivity()
	{
		this.setId(UUID.randomUUID().timestamp());
		this.setActor("");
		this.setObject("");
		this.setTarget("");
		this.setVerb("");
		this.setPublished("0");
	}
	
	public  String getVerb() {
		return verb;
	}


	public  void setVerb(String verb) {
		this.verb = verb;
	}

	public  String getActor() {
		return actor;
	}

	public  void setActor(String actor) {
		this.actor = actor;
	}

	public  String getObject() {
		return object;
	}

	public  void setObject(String object) {
		this.object = object;
	}

	public  String getTarget() {
		return target;
	}

	public  void setTarget(String target) {
		this.target = target;
	}
	
	public void setId(Long id){
		this.id=id;
	}
	
	public  Long getId() {
		return id;
	}

	public  long getTime() {
		return time;
	}

	public  void setTime(long time) {
		this.time = time;
	}

	public  String getPublished() {
		return published;
	}

	public  void setPublished(String published) {
		this.published = published;
	}
	
	public  String toString(){
        return getPublished()+":"+getActor()+":"+getVerb()+":"+getObject()+":"+getTarget();
    }
}