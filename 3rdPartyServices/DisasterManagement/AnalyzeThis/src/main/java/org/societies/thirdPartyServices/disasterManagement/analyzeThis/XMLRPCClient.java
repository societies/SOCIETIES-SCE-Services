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

package org.societies.thirdPartyServices.disasterManagement.analyzeThis;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class XMLRPCClient {
	private XmlRpcClient client;
//	private SimpleDateFormat dateformat = new SimpleDateFormat( "dd/MM/yyyy HH:mm a" );
	
	public XMLRPCClient () {
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("http://213.133.100.232/societies/server.php"));
			client = new XmlRpcClient();
			client.setConfig(config);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public String addUser(String email, String password, String lastname, String firstname, String institute){
		String returnString = null;
		try {
			Object[] params = new Object[] { email, password, lastname, firstname, institute };
			returnString = (String) client.execute("societies.addUser", params);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return returnString;
	}
		
	public String addRequest(String shortDescription, String longDescription) {
		String returnString = null;
		try {
			Object[] params = new Object[] { shortDescription, longDescription,  ""};
			returnString = (String) client.execute("societies.addRequest", params);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return returnString;
	}
	
	/**
	 * main method for testing
	 */
	public static void main(String[] args) {
		System.out.println("Starting JavaXMLRPCClient in AnalyzeThis service ...");
		XMLRPCClient xmlrpcClient = new XMLRPCClient();
		System.out.println("addUser: " +xmlrpcClient.addUser("john@doe.ar", "password", "lastname", "firstname", "institute" ));
//		System.out.println("addRequest: " + xmlrpcClient.addRequest("short Description", "long Description"));
	}
}
