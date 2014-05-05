/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
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

package ac.hw.mytv;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.activity.MarshaledActivity;

import com.google.gson.Gson;

import ac.hw.mytv.ActivityList;
import ac.hw.mytv.MyTvClient.CommandHandler;

/**
 * @author Eliza
 *
 */
public class CommsServerAction implements Runnable{

	private Socket client;
	private PrintWriter out;
	private BufferedReader in;

	private static final String GUI_STARTED = "GUI_STARTED";
	private static final String GUI_STOPPED = "GUI_STOPPED";
	private static final String USER_ACTION = "USER_ACTION";
	private static final String CHANNEL_PREFERENCE_REQUEST = "CHANNEL_PREFERENCE_REQUEST";
	private static final String MUTED_PREFERENCE_REQUEST = "MUTED_PREFERENCE_REQUEST";
	private static final String ACTIVITY_PREFERENCE_REQUEST = "ACTIVITY_PREFERENCE_REQUEST";
	private static final String CHANNEL_INTENT_REQUEST = "CHANNEL_INTENT_REQUEST";
	private static final String MUTED_INTENT_REQUEST = "MUTED_INTENT_REQUEST";
	private static final String RECEIVED = "RECEIVED";
	private static final String FAILED = "FAILED";
	private static final String START_MSG = "START_MSG";
	private static final String END_MSG = "END_MSG";
	//ACTIVITY FEED MESSAGES
	private static final String ACTIVITY_FEED_REQUEST = "ACTIVITY_FEED_REQUEST";

	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	private CommandHandler commandHandler;
	private int port;
	private MyTvClient myTVClient;
	

	public CommsServerAction(Socket socket, MyTvClient myTVClient, int port){
		this.client = socket;
		this.port = port;
		this.commandHandler = myTVClient.getCommandHandler();
		this.myTVClient = myTVClient;

	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub



		if(LOG.isDebugEnabled()) LOG.debug("Connection accepted from GUI!");

		try {
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			if(LOG.isDebugEnabled()) LOG.debug("Accept failed: "+port);
			e.printStackTrace();
			return;
		}

		try{
			String start = in.readLine();
			if(LOG.isDebugEnabled()) LOG.debug("Got new input: "+start);
			if(start.equalsIgnoreCase(START_MSG)){
				if(LOG.isDebugEnabled()) LOG.debug("Processing new message...");

				//loop to get rest of message
				String message = "";
				boolean reading = true;
				while(reading){
					if(LOG.isDebugEnabled()) LOG.debug("running through while again...");
					String next = in.readLine();
					if(LOG.isDebugEnabled()) LOG.debug("next = "+next);
					if(!next.equalsIgnoreCase(END_MSG)){
						if(LOG.isDebugEnabled()) LOG.debug("Inside if");
						message = message+next+"\n";
					}else{
						if(LOG.isDebugEnabled()) LOG.debug("Inside else");
						reading  = false;
					}
				}
				if(LOG.isDebugEnabled()) LOG.debug("message = "+message);

				//handle message
				String[] splitData = message.split("\n");
				if(LOG.isDebugEnabled()) LOG.debug("splitData length = "+splitData.length);
				String command = splitData[0];
				if (command.equalsIgnoreCase(GUI_STARTED)){
					if(LOG.isDebugEnabled()) LOG.debug(GUI_STARTED+" message received");
					out.println(RECEIVED);
					out.close();
					String gui_ip = splitData[1];
					commandHandler.connectToGUI(gui_ip);
					myTVClient.startActivityTask();
				}else if (command.equalsIgnoreCase(USER_ACTION)){
					if(LOG.isDebugEnabled()) LOG.debug(USER_ACTION+" message received");
					out.println(RECEIVED);
					out.close();
					String parameterName = splitData[1].trim();
					String value = splitData[2].trim();
					commandHandler.processUserAction(parameterName, value);

				}else if(command.equalsIgnoreCase(CHANNEL_PREFERENCE_REQUEST)){
					if(LOG.isDebugEnabled()) LOG.debug(CHANNEL_PREFERENCE_REQUEST+" message received");
					String response = commandHandler.getChannelPreference();
					out.println(response);
					out.close();

				}else if(command.equalsIgnoreCase(MUTED_PREFERENCE_REQUEST)){
					if(LOG.isDebugEnabled()) LOG.debug(MUTED_PREFERENCE_REQUEST+" message received");
					String response = commandHandler.getMutedPreference();
					out.println(response);
					out.close();

				}else if(command.equalsIgnoreCase(ACTIVITY_PREFERENCE_REQUEST)){
					if(LOG.isDebugEnabled()) LOG.debug(ACTIVITY_PREFERENCE_REQUEST+" message received");
					String response = commandHandler.getActivityPreference();
					out.println(response);
					out.close();

				}else if(command.equalsIgnoreCase(CHANNEL_INTENT_REQUEST)){
					if(LOG.isDebugEnabled()) LOG.debug(CHANNEL_INTENT_REQUEST+" message received");
					String response = commandHandler.getChannelIntent();
					out.println(response);
					out.close();

				}else if(command.equalsIgnoreCase(MUTED_INTENT_REQUEST)){
					if(LOG.isDebugEnabled()) LOG.debug(MUTED_INTENT_REQUEST+" message received");
					String response = commandHandler.getMutedIntent();
					out.println(response);
					out.close();

				}else if (command.equalsIgnoreCase(GUI_STOPPED)){
					if(LOG.isDebugEnabled()) LOG.debug(GUI_STOPPED+" message received");
					out.println(RECEIVED);
					out.close();
					myTVClient.stopActivityTask();
					commandHandler.disconnectFromGUI();
				}

				else{
					if(LOG.isDebugEnabled()) LOG.debug("Unknown command received from MyTvUI: "+command);
					out.println(FAILED);
					out.close();
				}
			}
		} catch (IOException e) {
			if(LOG.isDebugEnabled()) LOG.debug("Read failed");
			out.println(FAILED);
			out.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
