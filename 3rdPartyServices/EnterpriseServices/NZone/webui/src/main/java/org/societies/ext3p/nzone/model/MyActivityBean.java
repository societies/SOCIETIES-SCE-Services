package org.societies.ext3p.nzone.model;  
  
import java.io.Serializable;   
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
  
@SuppressWarnings("serial")
@ApplicationScoped
@Controller(value = "myActivityBean") 
public class MyActivityBean implements Serializable{
	
	private static Logger log = LoggerFactory.getLogger(MyActivityBean.class);
	
	

    private List<String> activity;
       
    
    public List<String> getActivity() {
		return activity;
	}


	public void setActivity(List<String> activity) {
		this.activity = activity;
	}


	MyActivityBean()
    {
    	activity = new ArrayList<String>();
    	
    	String act1= new String("John installed Server NZoone");
    	String act2= new String("John joined Community NZone");
    	String act3 = new String("John shared information with Community NZone");
    	activity.add(act3);
    	activity.add(act2);
    	activity.add(act1);
    	
    }
    
   
	   
}  
