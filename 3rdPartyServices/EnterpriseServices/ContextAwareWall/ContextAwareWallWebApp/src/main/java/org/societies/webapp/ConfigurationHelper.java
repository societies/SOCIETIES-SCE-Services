package org.societies.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationHelper {
private static final Logger log = LoggerFactory.getLogger(ConfigurationHelper.class);
	
	/* Config file name */					   
	private final static String CONFIG_FILE = "/config.properties";
	
	private static final String PZ_SERVER_URL = "context.aware.wall.pz.prd.url"; 
	private static final String MOCK_ENTITY = "context.aware.wall.mock.entity";
	private static final String MOCK_ENTITY_ACTIVE = "context.aware.wall.mock.entity.active";
			
		
	static ConfigurationHelper instance = new ConfigurationHelper();
	private final Properties prop;
	
	public static ConfigurationHelper instance(){
		return instance;
	}
	
	protected ConfigurationHelper(){
		prop = new Properties();
		 
		InputStream inputStream = null;
    	try {
    		inputStream =getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
    		
    		//load a properties file
    		prop.load(inputStream);
    		
    		if (prop == null || prop.size() == 0){
    			log.error("Error ! PZ Properties weren't read from ("+CONFIG_FILE+")");
    		}
    		
    		String propertiesStr = "";
    		for (@SuppressWarnings("rawtypes") Entry entry :prop.entrySet()){
    			propertiesStr += entry.getKey() + " : " + entry.getValue()+ " ;\n ";
    		}
    		log.info("-- Context Aware Wall Properties -- \n"+ propertiesStr);
    		
    	}catch (IOException ex) {
    		log.error("Exception msg: "+ex.getMessage()+"\t cause: "+ex.getCause(),ex);
    	}catch (Exception e) {
    		log.error("Exception msg: "+e.getMessage()+"\t cause: "+e.getCause(),e);
		}finally{
    		if (inputStream != null){
    			try {
					inputStream.close();
				} catch (IOException e) {}
    		}
    	}
	}
	
	
	public String getServerUrl(){
		return prop.getProperty(PZ_SERVER_URL);
	}
	
	public String getMockEntity(){
		return prop.getProperty(MOCK_ENTITY);
	}
	
	public boolean getMockEntityActive(){
		String str = prop.getProperty(MOCK_ENTITY_ACTIVE);
		if (str == null || str.trim().length() == 0){
			return false;
		}
		return true;
	}
	
}
