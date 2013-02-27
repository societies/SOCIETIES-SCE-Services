/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.thirdPartyServices.disasterManagement.analyzeThis.xmlrpc;

import org.apache.log4j.BasicConfigurator;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLRPCServer_AT {
	private static final Logger LOG = LoggerFactory.getLogger(XMLRPCServer_AT.class);
    public static final int DEFAULT_PORT = 54321;
    
    public XMLRPCServer_AT() throws Exception {
    	this(DEFAULT_PORT);
    }

    /**
	 * @param port2
	 */
	public XMLRPCServer_AT(int port) throws Exception {
    	// testing for sending data to YRNA 
    	// must be moved to AnalyzeThis.java
    	
    	// read data from societies database
    	/*
    	Volunteer hulk_hogan = new Volunteer("100","Hulk","Hogan","WWF", "USA","hulk@hogan.com");
		hulk_hogan.addSpokenLanguage("english");
		hulk_hogan.addSpokenLanguage("brutal");
		hulk_hogan.addSkill("Backbreaker");
		hulk_hogan.addSkill("Piledriver");
		hulk_hogan.addSkill("Ganso Bomb");
		hulk_hogan.addSkill("Spinebuster");
		
		WebResource service = Client.create(new DefaultClientConfig()).resource(UriBuilder.fromUri("http://157.159.160.188:8080/YouRNotAloneServer").build());
		System.out.println("yrna add: "+service.path("rest").path("/").path(hulk_hogan.getID()).accept(MediaType.APPLICATION_XML).put(ClientResponse.class, hulk_hogan));
    	*/
    	
    	WebServer webServer = new WebServer(port);
        
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
      
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.addHandler("TrustHandler", TrustHandler.class);
        xmlRpcServer.setHandlerMapping(phm);
      
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        webServer.start();
        LOG.info("^^^^^^^^^^^^^^^^^^ web server for XML RPC started on port "+port+" ...");
	}

	public static void main(String[] args) throws Exception {
    	BasicConfigurator.configure();
        new XMLRPCServer_AT();
    }
}
