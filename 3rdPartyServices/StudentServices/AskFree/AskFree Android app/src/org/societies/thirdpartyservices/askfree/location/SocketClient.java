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
package org.societies.thirdpartyservices.askfree.location;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.societies.android.api.contentproviders.CSSContentProvider;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class SocketClient implements Runnable{

	private final static String LOG_TAG = "SocketClient";
	
	protected static final String LOCATION_CHANGE = "org.societies.thirdpartyservices.askfree.location.LOCATION_CHANGE";

	private String symbolicLocation;
	private Socket requestSocket;
	
	private String SERVERPORT;
	private String SERVER_IP;
	
	private ObjectInputStream in = null;
	private ObjectOutputStream out =null;
	
	Context context;
	private boolean connected = true;

	private String cssId;

	public SocketClient(Context context){
		this.context = context;	
		Log.d(LOG_TAG, "SocketClient object created");
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		this.SERVER_IP = sharedPref.getString("ipAddress", "");
		this.SERVERPORT = sharedPref.getString("port","");
	}

	public void run(){
		try{
			//1. creating a socket to connect to the server
			Log.d(LOG_TAG, "Connecting to SocketServer");
			requestSocket = new Socket(SERVER_IP, Integer.parseInt(SERVERPORT));
			Log.d(LOG_TAG, "Connected to SocketServer " + requestSocket.getInetAddress() + " on port " + requestSocket.getPort());
			
			//2. get Input/Output stream
			in = new ObjectInputStream(requestSocket.getInputStream());
			Log.d(LOG_TAG, "Input Stream created");
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			Log.d(LOG_TAG,"OutputStream created");
			
			//3: Communicating with the server
			try{					
				cssId = getCssId();
				Log.d(LOG_TAG, "Got cssId from Societies app: " + cssId);

				//Output
				out.writeObject(cssId);
				Log.d(LOG_TAG, "User cssId sent to the server: " + cssId);
				out.reset();

				//Input
				Object x;
				while(connected){
					Log.d(LOG_TAG, "Client is listening for server message...");
					x=in.readObject();
					if(x instanceof String){
						setSymbolicLocation((String)x);
						Log.d(LOG_TAG, "received location: " + getSymbolicLocation());
					}
				}
			}catch(ClassNotFoundException e){
				Log.e(LOG_TAG, "ClassNotFoundException: " + e);
				this.closeConnection();
			}
			catch(InvalidClassException e){
				Log.e(LOG_TAG, "InvalidClassException: " + e);
				this.closeConnection();
			}
		}catch(UnknownHostException e){
			Log.e(LOG_TAG, "You are trying to connect to an unknown host! " + e);
			this.closeConnection();
		}
		catch(IOException e){
			Log.e(LOG_TAG, "IOException: " + e);
		}
		
	}
	
	public void closeConnection(){
		//4: Closing connection
		try{
			in.close();
			out.close();
			requestSocket.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	public String getCssId() {
		Cursor cursor = context.getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, null, null, null, null);
		cursor.moveToFirst();
		String cssId = cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY));
		Log.d(LOG_TAG, "Get CSSId: " + cssId);
		return cssId;
	}

	/**
	 * @return the symbolicLocation
	 */
	public String getSymbolicLocation() {
		Log.d(LOG_TAG, "getSymbolicLocation()");
		return symbolicLocation;
	}

	/**
	 * @param symbolicLocation the symbolicLocation to set
	 */
	public void setSymbolicLocation(String symbolicLocation) {
		Log.d(LOG_TAG, "setSymbolicLocation() :" + symbolicLocation);
		this.symbolicLocation = symbolicLocation;
		
		Intent i = new Intent(LOCATION_CHANGE);
		i.putExtra("symloc", symbolicLocation);
		context.sendBroadcast(i);
		Log.d(LOG_TAG, "Intent" + i + "sent");
	}
	
}
