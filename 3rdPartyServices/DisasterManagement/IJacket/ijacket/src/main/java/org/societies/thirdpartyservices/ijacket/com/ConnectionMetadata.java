/*
* Copyright 2012 Anders Eie, Henrik Goldsack, Johan Jansen, Asbj�rn 
* Lucassen, Emanuele Di Santo, Jonas Svarvaa, Bj�rnar H�kenstad Wold
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/
package org.societies.thirdpartyservices.ijacket.com;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ConnectionMetadata {
	
	private String deviceVersion;
	private String name;
	private String address;
	
	private HashMap<String, String> applicationDownloadLinks;
	
	private HashSet<String> servicesSupported;
	private HashMap<String, HashSet<Integer>> servicePin;
	
	public interface Service {
		String name();
	}

	/**
	 * List of default services a remote device can support. This list can be extended by
	 * creating a new Enum that extends the Service interface. The names of each enumeration must
	 * be unique.
	 */
	public enum DefaultServices implements Service {
		SERVICE_LED_LAMP,
		SERVICE_LCD_SCREEN,
		SERVICE_RGB_LAMP,
		SERVICE_SERVO_MOTOR,
		SERVICE_VIBRATION,
		SERVICE_TEMPERATURE_SESNOR,
		SERVICE_SPEAKER
	}	

	/**
	 * Constructs a ConnectionMetadata object out of a JSON object
	 * @param deviceInfo JSONObject representation of the ConnectionMetadata
	 * @param address an unique address specifier represented as a String (mac address, IP address, phone number, etc.)
	 */
	public ConnectionMetadata(JSONObject deviceInfo) {
        Log.v(getClass().getSimpleName(), "Recieved JSON string: " + deviceInfo.toString());
		
		//Get custom name
		name = deviceInfo.optString("name", "Unnamed");
		
		//Get version
		deviceVersion = deviceInfo.optString("version", "0.0.0");
		if(!deviceVersion.equals(Protocol.LIBRARY_VERSION)) 
			Log.w(getClass().getSimpleName(), "Remote device Firmware (" + deviceVersion + ") is not same version as ComLib (" + Protocol.LIBRARY_VERSION + ")");
		
		//Device address
		address = deviceInfo.optString("address", "No Address Specified");
		
		//Get services supported
		servicesSupported = new HashSet<String>();
		servicePin = new HashMap<String, HashSet<Integer>>();
		JSONArray services = deviceInfo.optJSONArray("services");
		if(services != null) {
			for(int i = 0; i < services.length(); i++) {
				JSONObject element = services.optJSONObject(i);
				
				//Service name
				String serviceName = element.optString("id");
				if(serviceName.equals("")) continue;
				serviceName = "SERVICE_" + serviceName;
				servicesSupported.add(serviceName);
				servicePin.put(serviceName, new HashSet<Integer>());
				
				//Pins associated with this service
				for(String pin : element.optString("pins").split(",")) {
					try{
						servicePin.get(serviceName).add( Integer.parseInt(pin) );
					}
					catch (NumberFormatException ex) {}
				}
			}
		}
			
		//Get download links
		applicationDownloadLinks = new HashMap<String, String>();
		JSONArray downloadLinks = deviceInfo.optJSONArray("links");
		if(downloadLinks != null) {
			for(int i = 0; i < downloadLinks.length(); i++) {
				JSONObject pair;
				try {
					pair = downloadLinks.getJSONObject(i);
					String name = pair.getString("name");
					String link = pair.getString("link");
					applicationDownloadLinks.put(name, link);
				} catch (JSONException e) {
					//Failed to get link
					Log.v(getClass().getName(), "Failed to parse JSON link: " + e.getMessage());
					continue;
				}
			}
		}
				
	}
	
	/**
	 * Retrieves a list of all Applications associated with the remote module.
	 * You can use getApplicationDownloadLink() to retrieve the download
	 * link for the specified application.
	 * @return an array of Strings containing the name of each application
	 */
	public String[] getApplications() {
		Collection<String> links = applicationDownloadLinks.keySet();
		return links.toArray( new String[links.size()] );
	}
	
	/**
	 * Returns an array of all associated pins to the service
	 * @param service which service we want to retrieve the pins for
	 * @return an array of Integer objects (empty if there are no specific pins)
	 */
	public Integer[] getServicePins(String service){
		HashSet<Integer> set = servicePin.get(service);		
		return set.toArray(new Integer[set.size()]);
	}
	
	/**
	 * Gets the download link for the application for this device.
	 * @param platform which platform the application is for
	 * @return URI of the application for this device
	 * @see DefaultPlatforms
	 */
	public String getApplicationDownloadLink(String applicationName){
		return applicationDownloadLinks.get(applicationName);
	}
	
	/**
	 * Checks if a specific service is supported by the remote device
	 * @param service which service is requested? 
	 * @return returns true if the specified service is supported by the remote device
	 * @see DefaultServices
	 */
	public boolean isServiceSupported(String service){
		return servicesSupported.contains(service);
	}
	
	/**
	 * Returns a String array of all services supported by the remote device
	 * @return a String array where each element is a single service supported
	 */
	public String[] getServicesSupported() {
		return servicesSupported.toArray(new String[servicesSupported.size()]);
	}
	
	/**
	 * Returns a String representation of the name of the remote device
	 * @return the human friendly name of this device
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a unique String representation of the address of the remote device.
	 * Could be an IP address, MAC address, phone number, frequency, etc.
	 * @return String representing the remote device location
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Retrieves the device version on the remote device (usually ComLib version of the firmware)
	 * @return a String representation of the version (example: "2.0.0")
	 */
	public String getDeviceVersion() {
		return deviceVersion;
	}
}
