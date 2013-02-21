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

package org.societies.thirdPartyServices.disasterManagement.wantToHelp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.data.UserData;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.data.Volunteer;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.xmlrpc.XMLRPCClient_IWTH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


@SuppressWarnings("unused")
@Service
public class WantToHelp implements IWantToHelp, ActionListener {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(WantToHelp.class);
	
	private XMLRPCClient_IWTH xmlRpcClient_IWTH;

	public static final String subscribeCommand = "subscribe";
	public static final String unsubscribeCommand = "unsubscribe";
	private JFrame frame;
	private JTextArea feedbackTextArea; 
	private JButton subscribe;
	private JButton unsubscribe;
	
//	private PullThread pullThread;
	
	
	public static final String PANEL_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 10, novisualpadding, ins 4, wrap 1"; //, debug 2000";
	public static final String PANEL_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String PANEL_ROW_CONSTRAINTS = "[][fill, grow]";
	
	public static final String FEEDBACK_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 0, novisualpadding, ins 0, wrap 1"; //, debug 2000";
	public static final String FEEDBACK_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String FEEDBACK_ROW_CONSTRAINTS = "[fill, grow]";

	private static final String SOCIETIES_IDENTIFIER = "ict-societies.eu";
	final static String YRNA_URL = "http://157.159.160.188:8080/YouRNotAloneServer";
	private static String SOCIETIES_XMLRPC_IP = "213.239.194.180";

	private String USER_ID;
	
	private String userEmail = "korbinian.frank@dlr.de";
	private String testUserPassword = "password";	
	private String userFirstname = "firstname";
	private String userLastname = "lastname";
	private String userInstitute = "institute";
	private String skills = "skills,myOnes";
	private String userCountry = "DLR";
	private String languages;
	
	
//	@Autowired(required=true)	
	private ICtxBroker externalCtxBroker;
//	@Autowired(required=true)
	private ICommManager commMgr;

	private boolean NO_USER;



	@Autowired(required=true)
	public WantToHelp(ICtxBroker externalCtxBroker, ICommManager commMgr) {
		printAndLog("********** " + this.getClass() + " instantiated");
		
		this.externalCtxBroker = externalCtxBroker;
		this.commMgr = commMgr;
		
		xmlRpcClient_IWTH = new XMLRPCClient_IWTH();

		printAndLog("********** commMgr="+commMgr);
		printAndLog("********** contextBroker="+externalCtxBroker);
		
		// ++++++++++++   GUI start   ++++++++++++
		
		// otherwise it does not startup in VIRGO
//		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
//		frame = new JFrame("IWantToHelp");
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.addWindowListener(new WindowEventHandler());
//
//		JPanel panel = new JPanel();
//		panel.setLayout(new MigLayout(PANEL_LAYOUT_CONSTRAINTS, PANEL_COLUMN_CONTSTRAINTS, PANEL_ROW_CONSTRAINTS));
//		Dimension panelDimension = new Dimension(1200, 768); 
//		panel.setPreferredSize(panelDimension);
//		frame.getContentPane().add(panel);
//		
//		feedbackTextArea = new JTextArea("");
//		feedbackTextArea.setEditable(false);
//		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
//		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
//	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
//	    JPanel feedbackPanel = new JPanel(new MigLayout(FEEDBACK_LAYOUT_CONSTRAINTS, FEEDBACK_COLUMN_CONTSTRAINTS, FEEDBACK_ROW_CONSTRAINTS));
//	    feedbackPanel.add(scrollPane);
//		
//		subscribe = new JButton("I want to help: subscribe to CSDM platform");
//		subscribe.setActionCommand(subscribeCommand);
//		subscribe.addActionListener(this);
//		unsubscribe = new JButton("I am done");
//		unsubscribe.setActionCommand(unsubscribeCommand);
//		unsubscribe.addActionListener(this);
//		unsubscribe.setVisible(false);
//
//		panel.add(subscribe);
//		panel.add(unsubscribe);
//	    panel.add(feedbackPanel);
//	    
//	    frame.pack();
//		frame.setVisible(true);
	    
		// ------------   GUI end   ------------

		
		// ++++++++++++   pull thread start   ++++++++++++		
		
//		pullThread = new PullThread();
//		pullThread.start();
//		pullThread.setCheckData(false);
		
		// ------------   pull thread end   ------------
		
		
		sendDataToCSDMandYRNA();
	}
	
//	@PostConstruct
	public void activate() throws Exception {
		printAndLog("********** on activate -> WantToHelp service started");

		// ++++++++++++   GUI start   ++++++++++++
		
//		frame.pack();
//		frame.setVisible(true);

		// ------------   GUI end   ------------
		
		// ++++++++++++   pull thread start   ++++++++++++	
		
//		pullThread = new PullThread();
//		pullThread.start();
//		pullThread.setCheckData(false);
		
		// ------------   pull thread end   ------------
		
	}

//	@PreDestroy
	public void deactivate() throws Exception {
		printAndLog("********** on deactivate -> WantToHelp service stopped");
		
		// ++++++++++++   GUI start   ++++++++++++
		
		frame.dispose();
		
		// ------------   GUI end   ------------
		
		// ++++++++++++   pull thread start   ++++++++++++	
		
//		pullThread.setRun(false);

		// ------------   pull thread end   ------------
	}
	
	/*
	 * commMgr.getIdManager().getThisNetworkNode().getDomain() = societies.local
	 * commMgr.getIdManager().getThisNetworkNode().getNodeIdentifier() = rich
	 * commMgr.getIdManager().getThisNetworkNode().getIdentifier() = xcmanager | userX
	 */
	public void getUserDataFromCSS() throws Exception{
		
		printAndLog("********** retrieve user data from CSS ... ");
		
		String userName = commMgr.getIdManager().getThisNetworkNode().getIdentifier();
		
		int userNumber = -1;
		if (userName.startsWith("user"))
			 userNumber = Integer.parseInt(userName.substring(4)); // starts with "user" - i.e. 4 digits
		else NO_USER=true;
		if (userNumber <10) USER_ID = "0"+userNumber;
		else USER_ID = ""+userNumber;
		
		IIdentity cssOwnerId = commMgr.getIdManager().fromJid(commMgr.getIdManager().getThisNetworkNode().getBareJid());  // e.g. resolves to "xcmanager.societies.local"
		Requestor requestor = new Requestor(cssOwnerId);

		CtxEntityIdentifier ownerEntityIdentifier = externalCtxBroker.retrieveIndividualEntityId(requestor, cssOwnerId).get();
		
		String name = null;
		
		CtxEntity ownerEntity = (CtxEntity) externalCtxBroker.retrieve(requestor, ownerEntityIdentifier).get();

		Iterator<CtxAttribute> foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.NAME).iterator();
		if (foundAttrsIt.hasNext())
			name = foundAttrsIt.next().getStringValue();
		if (name == null || name.equals("")){
//			foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.ID).iterator();
//			if (foundAttrsIt.hasNext())
//				name = foundAttrsIt.next().getStringValue();
			name = userName;
		}
		printAndLog("********** Name="+name);
		
		//For CSDM, every user needs first and last name;
		if (name !=null && !name.equals("")){
			userLastname = name;
			userFirstname = name;
		}
			
		
		
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.NAME_LAST).iterator();
		if (foundAttrsIt.hasNext())
			userLastname = foundAttrsIt.next().getStringValue();
		
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.NAME_FIRST).iterator();
		if (foundAttrsIt.hasNext())
			userFirstname = foundAttrsIt.next().getStringValue();

		printAndLog("********** FirstName LastName:"+userFirstname + " "+ userLastname);
		
		if ((userLastname==null || userLastname.equals("")) && (userFirstname==null || userFirstname.equals("")))
		{
			System.out.println("TEST CASE ONLY. NO USER DATA AVAILABLE.");
				CtxAttribute nameLast = externalCtxBroker.createAttribute(requestor, ownerEntityIdentifier, CtxAttributeTypes.NAME_LAST).get();
				nameLast.setStringValue("Test");
				externalCtxBroker.update(requestor, nameLast).get();
				CtxAttribute nameFirst = externalCtxBroker.createAttribute(requestor, ownerEntityIdentifier, CtxAttributeTypes.NAME_FIRST).get();
				nameFirst.setStringValue("First");
				nameFirst = (CtxAttribute) externalCtxBroker.update(requestor, nameFirst).get();
		}

		userEmail = cssOwnerId + "@" + SOCIETIES_IDENTIFIER;
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.EMAIL).iterator();
		if (foundAttrsIt.hasNext())
			userEmail = foundAttrsIt.next().getStringValue();
		//testUserEmail = testUserFirstname+"."+testUserLastname+"@ict-societies.eu";
		printAndLog("********** email: "+userEmail);

		testUserPassword = userFirstname.toLowerCase()+userLastname.toLowerCase();
		printAndLog("********** password: "+testUserPassword);
		
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.AFFILIATION).iterator();
		if (foundAttrsIt.hasNext())
			userInstitute = "institute";

		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.LANGUAGES).iterator();
		if (foundAttrsIt.hasNext())
			languages = foundAttrsIt.next().getStringValue();

		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.SKILLS).iterator();
		if (foundAttrsIt.hasNext())
			skills = foundAttrsIt.next().getStringValue();
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.INTERESTS).iterator();
		String interests = null;
		if (foundAttrsIt.hasNext())
			interests = foundAttrsIt.next().getStringValue();
		
		if (interests!=null && !interests.equals("")){
			if (skills==null || skills.equals(""))
				skills = interests;
			else if (skills.endsWith(","))
				skills += interests;
			else skills += ","+interests;
		}		

		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.ADDRESS_HOME_COUNTRY).iterator();
		if (foundAttrsIt.hasNext())
			userCountry = foundAttrsIt.next().getStringValue();
		
	}
	
	public void updateUserDataInCSS() {
		// TODO update user data in CSS
	}

	@Override
	public void provideHelp() {
		printAndLog("********** provide help");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase(subscribeCommand)) {

			//dummyTest();
			sendDataToCSDMandYRNA();
			
//			pullThread.setCheckData(true);
			subscribe.setVisible(false);
			unsubscribe.setVisible(true);
		} else if (command.equalsIgnoreCase(unsubscribeCommand)) {
//			pullThread.setCheckData(false);
			subscribe.setVisible(true);
			unsubscribe.setVisible(false);
		}  
	}
	
	private void dummyTest(){
	   	Volunteer hulk_hogan = new Volunteer("100","Hulk","Hogan","WWF", "USA","hulk@hogan.com");
			hulk_hogan.addSpokenLanguage("english");
			hulk_hogan.addSpokenLanguage("brutal");
			hulk_hogan.addSkill("Backbreaker");
			hulk_hogan.addSkill("Piledriver");
			hulk_hogan.addSkill("Ganso Bomb");
			hulk_hogan.addSkill("Spinebuster");
			
			WebResource service = Client.create(new DefaultClientConfig()).resource(UriBuilder.fromUri("http://157.159.160.188:8080/YouRNotAloneServer").build());
			System.out.println("yrna add: "+service.path("rest").path("/").path(hulk_hogan.getID()).accept(MediaType.APPLICATION_XML).put(ClientResponse.class, hulk_hogan));
	}
	
	private void sendDataToCSDMandYRNA(){
		printAndLog("********** sendDataToCSDMandYRNA trying to get user data");
		// Communicate with SOCIETIES to get context
		try {
			getUserDataFromCSS();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		

		
		// Communicate with CSDM
		if (NO_USER) USER_ID = "00";  //TODO remove
		String XMLRPC_SERVER_ADDRESS = SOCIETIES_XMLRPC_IP+":543"+USER_ID; //TODO
		printAndLog("********** CSDM xmlrpc signInUser > "+xmlRpcClient_IWTH.signInUser(userEmail, testUserPassword, userLastname, userFirstname, userInstitute, XMLRPC_SERVER_ADDRESS, skills ));
		

		
		// Communicate with YRNA
		Volunteer volunteer = new Volunteer(userEmail, userFirstname, userLastname, userInstitute, userCountry, userEmail);
		String[] languagesArray = {};
		if (languages!= null) languages.split(",");
		String[] skillsArray = {};
		if (skills!=null) skills.split(",");
		for (String skill: skillsArray)
			volunteer.addSkill(skill.trim());
		for (String language: languagesArray)
			volunteer.addSpokenLanguage(language.trim());
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(UriBuilder.fromUri(YRNA_URL).build());

		ClientResponse r = service.path("rest").path("/")
				.path(volunteer.getID()).accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, volunteer);
		printAndLog("********** send data to YRNA platform: "+r);

	}
	

	private void printAndLog(String string) {
		if (feedbackTextArea!=null)
			feedbackTextArea.append(string+"\n");
		
		LOG.info(string);
		
	}

	private class PullThread extends Thread {
		
		private boolean run = true;
		private boolean checkData = false;
		private int pullIntervalInSeconds = 1;
		
		@Override
		public void run() {
			UserData userData = null;
			while(run){
				if (checkData) {
					userData = xmlRpcClient_IWTH.getUserData(userEmail);
					printAndLog(userEmail + " settings from CSDM> "+ userData + "");
					if (userData != null)
						updateUserDataInCSS();
				}
				try {
					sleep(pullIntervalInSeconds*10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void setRun(boolean run) {
			this.run = run;
		}
		
		public void setCheckData(boolean check) {
			this.checkData = check;
		}
	}
	
	
	/**
	 * main method for testing
	 */
	public static void main(String[] args) throws Exception {
		WantToHelp iWantToHelp = new WantToHelp(null, null);
		iWantToHelp.activate();
	}
	
	private class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			try {
				deactivate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ICommManager getCommMgr() {
		return commMgr;
	}

	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
	}

	public ICtxBroker getExternalCtxBroker() {
		return externalCtxBroker;
	}

	public void setExternalCtxBroker(ICtxBroker externalCtxBroker) {
		this.externalCtxBroker = externalCtxBroker;
	}

}