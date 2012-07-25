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

/**
 * Describe your class here...
 *
 * @author dingqi
 *
 */

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;

public class VolunteerServer {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;

	public VolunteerServer(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	//Application integration 		
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Volunteer getVolunteer() {
		Volunteer v = YouRNotAloneDAO.instance.getVO().getVolunteers().get(id);
		if(v==null)
			throw new RuntimeException("Get: volunteer with " + id +  " not found");
		return v;
	}

	// For the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Volunteer getVolunteerHTML() {
		Volunteer v = YouRNotAloneDAO.instance.getVO().getVolunteers().get(id);
		if(v==null)
			throw new RuntimeException("Get: volunteer with " + id +  " not found");
		return v;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putVolunteer(JAXBElement<Volunteer> vxml) {
		Volunteer v = vxml.getValue();
		return putAndGetResponse(v);
	}

	@DELETE
	public Response deleteVolunteer() {
		Response res;
		Volunteer v = YouRNotAloneDAO.instance.getVO().getVolunteers().remove(id);
		if(v==null){
			res = Response.noContent().build();
			throw new RuntimeException("Delete: Todo with " + id +  " not found");
		}
		else
			res = Response.ok().build();
		return res;
	}

	private Response putAndGetResponse(Volunteer v) {
		Response res;
		if(YouRNotAloneDAO.instance.getVO().getVolunteers().containsKey(v.getID())) {
			res = Response.ok().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		YouRNotAloneDAO.instance.getVO().getVolunteers().put(v.getID(), v);
		return res;
	}


}
