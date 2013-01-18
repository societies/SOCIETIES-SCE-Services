package org.societies.thirdPartyServices.disasterManagement.analyzeThis.xmlrpc;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.BasicConfigurator;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class XMLRPCServer_AT {
    public static final int port = 54321;
    
    public XMLRPCServer_AT() throws Exception {
    	// testing for sending data to YRNA 
    	// must be moved to AnalyzeThis.java
    	
    	// read data from societies database
    	Volunteer hulk_hogan = new Volunteer("100","Hulk","Hogan","WWF", "USA","hulk@hogan.com");
		hulk_hogan.addSpokenLanguage("english");
		hulk_hogan.addSpokenLanguage("brutal");
		hulk_hogan.addSkill("Backbreaker");
		hulk_hogan.addSkill("Piledriver");
		hulk_hogan.addSkill("Ganso Bomb");
		hulk_hogan.addSkill("Spinebuster");
		
		WebResource service = Client.create(new DefaultClientConfig()).resource(UriBuilder.fromUri("http://157.159.160.188:8080/YouRNotAloneServer").build());
		System.out.println("yrna add: "+service.path("rest").path("/").path(hulk_hogan.getID()).accept(MediaType.APPLICATION_XML).put(ClientResponse.class, hulk_hogan));
    	
    	WebServer webServer = new WebServer(port);
        
        XmlRpcServer server = webServer.getXmlRpcServer();
      
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.addHandler("TrustHandler", TrustHandler.class);
        server.setHandlerMapping(phm);
      
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) server.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        webServer.start();
        System.out.println("web server for XML RPC started ...");
    }

    public static void main(String[] args) throws Exception {
    	BasicConfigurator.configure();
        new XMLRPCServer_AT();
    }
}
