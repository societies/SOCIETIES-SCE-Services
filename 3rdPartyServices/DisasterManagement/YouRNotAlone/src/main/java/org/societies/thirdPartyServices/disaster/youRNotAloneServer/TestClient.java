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
package org.societies.thirdPartyServices.disaster.youRNotAloneServer;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.societies.thirdPartyServices.disaster.youRNotAlone.mockedData.MockedData;
import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;


/**
 * Describe your class here...
 *
 * @author dingqi
 *
 */
public class TestClient {
	// server main URL
	final static String MainURL = "http://157.159.160.188:8080/YouRNotAloneServer";
//	final static String MainURL = "http://localhost:8080/YouRNotAloneServer";
	
	
//	test for main client functionality 
	public static void main(String[] args) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());

		//	POST/PUT Test
		System.out.println("Starting post/put test ......");
		MockedData mData = new MockedData();
		for(int i=0;i<mData.getMockedVolunteers().size();i++){
			// Create one Volunteer
			ClientResponse r = createVolunteer(service,mData.getMockedVolunteers().get(i));
//			System.out.println(r.getStatus());
			System.out.println(r.toString());
		}

//		System.out.println("/nStarting get test ......");
//		//GET Test
//		// Get JSON for application
//		String volunteersXML = getVolunteers(service,MediaType.APPLICATION_XML);
//		System.out.println(volunteersXML);
//		// Get XML for application
//		String volunteersJSON = getVolunteers(service,MediaType.APPLICATION_JSON); 
//		System.out.println(volunteersJSON);
//		// Get the Volunteer with id 6
//		String volunteerXML = getVolunteer(service,"6",MediaType.APPLICATION_XML);
//		System.out.println(volunteerXML);
//		// Get the Volunteer with id 6
//		String volunteerJSON = getVolunteer(service,"6",MediaType.APPLICATION_JSON);
//		System.out.println(volunteerJSON);
//		// Get translators
//		String translator = getTranslator(service,MediaType.APPLICATION_JSON);
//		System.out.println(volunteerJSON);
//		// Get group with expertise
//		// expertise skills are seperated by "&"
//		String groupJSON = getVolunteersByExpertise(service,MediaType.APPLICATION_JSON, "internet%20research"); 
//		System.out.println(groupJSON);
//		
		

		//DELETE Test
//		System.out.println("Starting delete test ......");
//		// get Volunteer with id 1
//		System.out.println(deleteVolunteer(service,"1"));
//		System.out.println(deleteAllVolunteer(service,"1&2&3"));
		
//		POST message test
//		ClientResponse r = postMessage(service,"notification test from Dingqi");
//		System.out.println(r.getStatus());
//		System.out.println(r.toString());

	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri(MainURL).build();
	}

	private static ClientResponse createVolunteer(WebResource service,Volunteer v){	
		ClientResponse response = service.path("rest").path("/")
				.path(v.getID()).accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, v);
		// Return code should be 201 == created resource	
		return response;
	}

	private static String getVolunteers(WebResource service,String type){
		return service.path("rest").path("/")
				.accept(type).get(String.class);
	}
	
	private static String getVolunteersByExpertise(WebResource service,String type,String expertise){
		return service.path("rest").path("volunteers/"+expertise).
				accept(type).get(String.class);
	}

	private static String getVolunteer(WebResource service,
			String ID, String type) {
		return service.path("rest").path(ID)
				.accept(type).get(String.class);
	}
	
	private static String deleteVolunteer(WebResource service, String ID){
		return service.path("rest").path(ID).delete(ClientResponse.class).toString();

	}
	
	private static String deleteAllVolunteer(WebResource service, String ID){
		return service.path("rest/volunteers/").path(ID).delete(ClientResponse.class).toString();

	}
	
	private static String getTranslator(WebResource service,String type){
		return service.path("rest").path("/translator")
				.accept(type).get(String.class);
	}
	
	private static ClientResponse postMessage(WebResource service, String msg){	
		Form form = new Form();
	    form.add("msg", msg);
	    form.add("type", "request");
	    form.add("tags", "request");
	    ClientResponse response = service.path("rest").path("/message")
				.type(MediaType.APPLICATION_FORM_URLENCODED)
				.post(ClientResponse.class, form);
		// Return code should be 201 == created resource	
		return response;
	}
	
	
}
