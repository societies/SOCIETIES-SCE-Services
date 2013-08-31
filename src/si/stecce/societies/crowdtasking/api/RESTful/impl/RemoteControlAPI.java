/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
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
package si.stecce.societies.crowdtasking.api.RESTful.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import si.stecce.societies.crowdtasking.api.RESTful.IRemoteControlAPI;
import si.stecce.societies.crowdtasking.model.CTUser;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

/**
 * Remote control public display
 *
 * @author Simon Jureša
 *
 */
@Path("/remote/{querytype}")
public class RemoteControlAPI implements IRemoteControlAPI {

	@Override
    @GET
	@Produces({MediaType.TEXT_PLAIN })
	public Response get(@PathParam("querytype") String querytype,
                        @DefaultValue("") @QueryParam("page") String page,
                        @DefaultValue("") @QueryParam("taskId") String taskId,
                        @Context HttpServletRequest request)
	{
		CTUser user = UsersAPI.getLoggedInUser(request.getSession());
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").type("text/plain").build();
		}
		Long spaceId = user.getSpaceId();
		Date checkedInDate = user.getCheckIn();
		if (spaceId == null || checkedInDate == null) {
			return Response.status(Status.FORBIDDEN).entity("You have to check-in first.").type("text/plain").build();
		}
		Date now = new Date();
		long timeOut = UsersAPI.getApplicationSettings().getChekInTimeOut();
		// automatic check-out after set period
		Date toLate = new Date(checkedInDate.getTime()+timeOut);
		if (now.after(toLate)) {
			return Response.status(Status.FORBIDDEN).entity("Check-in timed out.").type("text/plain").build();
		}
		String message=null;
		if ("changeChannel".equalsIgnoreCase(querytype)) {
    		message = "changeTo:/cs/"+SpaceAPI.getCollaborativeSpace(user.getSpaceId()).getUrlMapping()+"?p="+page;
		}
		if ("showTask".equalsIgnoreCase(querytype)) {
    		message = "showTask:"+taskId;
    		EventAPI.logShowTaskOnPd(new Long(taskId), user);
		}
		if ("hideTask".equalsIgnoreCase(querytype)) {
    		message = "hideTask";
		}
		sendMessage(spaceId, message);
		return Response.ok().entity("Request sent.").build();
	}	

	private void sendMessage(Long spaceId, String message) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		channelService.sendMessage(new ChannelMessage(Long.toString(spaceId), message));
	}
}
