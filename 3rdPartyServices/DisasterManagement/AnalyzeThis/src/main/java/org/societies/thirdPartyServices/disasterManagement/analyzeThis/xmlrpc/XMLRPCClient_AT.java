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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.societies.thirdPartyServices.disasterManagement.analyzeThis.data.TicketData;

public class XMLRPCClient_AT {
	private XmlRpcClient client;

	public XMLRPCClient_AT() {
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(
					"http://213.133.100.232/societies/server.php"));
			client = new XmlRpcClient();
			client.setConfig(config);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add new request/ticket for assistance to CSDM platform.
	 * 
	 * @return ticket status
	 */
	public String addTicket(String shortDescription, String longDescription) {
		String returnString = null;
		try {
			Object[] params = new Object[] { shortDescription, longDescription,
					"" };
			returnString = (String) client.execute("societies.addRequest",
					params);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return returnString;
	}

	/**
	 * Request all tickets since input starting date.
	 * 
	 * @param startingDate
	 * @return Vector of tickets
	 */
	public Vector<TicketData> getTickets(String startingDate) {
		String response = null;
		try {
			Object[] getRequestsParams= new Object[] {startingDate};
			response = (String) client.execute("societies.getRequests", getRequestsParams);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}

		Vector<TicketData> tickets = new Vector<TicketData>();
		if (!response.equalsIgnoreCase("")) {
			String[] responseLines = response.split("\n");
			
			for (String line : responseLines) {
				String[] ticket = line.split("---");
				tickets.add(new TicketData(new Integer(ticket[0]), ticket[1]));
			}
		}
		return tickets;
	}

	/**
	 * main method for testing
	 */
	public static void main(String[] args) {
		System.out
				.println("Starting JavaXMLRPCClient in AnalyzeThis service ...");
		XMLRPCClient_AT xmlrpcClient = new XMLRPCClient_AT();
		// System.out.println("addRequest: " +
		// xmlrpcClient.addRequest("short Description", "long Description"));
		 System.out.println("getRequests> "+xmlrpcClient.getTickets("2012-04-24"));
	}
}
