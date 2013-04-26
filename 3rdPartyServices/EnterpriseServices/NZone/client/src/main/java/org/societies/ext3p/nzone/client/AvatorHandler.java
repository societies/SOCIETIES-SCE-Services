
package org.societies.ext3p.nzone.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;





	
	
		
public class AvatorHandler implements Runnable {
			
		public NZoneClient client;
		public String userid;
		
		private static final Logger LOG = LoggerFactory.getLogger(AvatorHandler.class);
			
			
		public AvatorHandler(NZoneClient client, String userid) {
			this.client = client;
			this.userid= userid;
				
		}
			
		@Override
		public void run() {
			String imageStr = this.getAvatar(userid);
			try {
				if ((imageStr != null) && !(imageStr.isEmpty()))
				{
					this.client.avatarMapLock.acquire();
					this.client.avatarMap.put(userid, "data:image/png;base64," + imageStr);
					this.client.avatarMapLock.release();
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private enum MethodType {
	        ADD,
	        DELETE,
	        ENABLE,
	        DISABLE,
	        UPDATE,
	        LOGIN,
	        VCARD;
	    }

			
		/**
		* identity does not required domain, eg, john
		*/
		public String getAvatar(String identity) {
				String OPENFIRE_PLUGIN = "http://%s:9090/plugins/societies/societies";
				String xmppUrl = String.format(OPENFIRE_PLUGIN, this.client.getOpenfireIpAddress());
				
				
				Map<String, String> params = new LinkedHashMap<String, String>();
				// strip identity
				if (identity.indexOf('.') > 0)
					params.put("username", identity.substring(0, identity.indexOf('.')));
				else
					params.put("username", identity);
				
		        params.put("secret", "defaultSecret");
			        
		        String resp = postData(MethodType.VCARD, xmppUrl, params);
		        try {
		            if (resp.isEmpty()) {
		            	//LOG.error("Error logging onto openfire Account : Empty Response: Url was " + xmppUrl);
		            	return "";
		            }
		            //CHECK RESPONSE - DOES ACCOUNT ALREADY EXIST
		            Document respDoc = loadXMLFromString(resp);
		            if (respDoc.getDocumentElement().getNodeName().equals("error")) {
		            	LOG.error("Username/password incorrect");
		            }
		            else {
		            	NodeList listing = respDoc.getElementsByTagName("BINVAL");
		            	return  listing.item(0).getTextContent();
			            	
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		            System.out.println("Error logging onto openfire Account");
		        }
				return "";
			        
			}

			private String postData(MethodType method, String openfireUrl, Map<String, String> params) {
				try { 
					StringBuffer data = new StringBuffer();
					for(String s : params.keySet()) {
						String tmp = URLEncoder.encode(s, "UTF-8") + "=" + URLEncoder.encode((String)params.get(s), "UTF-8") +  "&";
						data.append(tmp);
					}
					//ADD METHOD
					String methodStr = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(method.toString().toLowerCase(), "UTF-8");
					data.append(methodStr);
						
			        // Send data 
			        URL url = new URL(openfireUrl); 
			        URLConnection conn = url.openConnection(); 
			        conn.setDoOutput(true); 
			        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
			        wr.write(data.toString()); 
			        wr.flush(); 
				  
			        // Get the response 
			        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			        StringBuffer sb = new StringBuffer();
			        String line; 
			        while ((line = rd.readLine()) != null) {  
			        	sb.append(line);
			        } 
			        wr.close(); 
			        rd.close();
				        
			        //RESPONSE CODE
			        return sb.toString();
				        
			    } catch (Exception e) { 

			    }
				return ""; 
			}

			private Document loadXMLFromString(String xml) throws Exception {
			        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			        DocumentBuilder builder = factory.newDocumentBuilder();
			        InputSource is = new InputSource(new StringReader(xml));
			        return builder.parse(is);
		    }
}	
	
	
