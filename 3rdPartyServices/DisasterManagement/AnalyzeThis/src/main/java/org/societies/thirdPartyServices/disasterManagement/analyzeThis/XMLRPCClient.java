package org.societies.thirdPartyServices.disasterManagement.analyzeThis;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class XMLRPCClient {
	private XmlRpcClient client;
	
	public XMLRPCClient () {
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("http://213.133.100.232/societies/server.php"));
			client = new XmlRpcClient();
			client.setConfig(config);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String addUser(String email, String password, String lastname, String firstname, String institute){
		String returnString = null;
		try {
			Object[] params = new Object[] { email, password, lastname, firstname, institute };
			returnString = (String) client.execute("societies.addUser", params);
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnString;
	}
		
	public String addRequest(String shortDescription, String longDescription) {
		String returnString = null;
		try {
			Object[] params = new Object[] { shortDescription, longDescription,  "09/28/2012 12:00 am"};
			returnString = (String) client.execute("societies.addRequest", params);
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnString;
	}
	
	public static void main(String[] args) {
		System.out.println("Starting JavaXMLRPCClient ...");
		XMLRPCClient xmlrpcClient = new XMLRPCClient();
		System.out.println("addUser: " +xmlrpcClient.addUser("john@doe.ar", "password", "lastname", "firstname", "institute" ));
		System.out.println("addRequest: " + xmlrpcClient.addRequest("short Description", "long Description"));
	}
}
