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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator.BaseUser;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator.User1;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator.User2;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator.User3;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator.User4;
import org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator.User5;
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

	private PullThread pullThread;

	public static final String PANEL_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 10, novisualpadding, ins 4, wrap 1"; // ,
																													// debug
																													// 2000";
	public static final String PANEL_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String PANEL_ROW_CONSTRAINTS = "[][fill, grow]";

	public static final String FEEDBACK_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 0, novisualpadding, ins 0, wrap 1"; // ,
																													// debug
																													// 2000";
	public static final String FEEDBACK_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String FEEDBACK_ROW_CONSTRAINTS = "[fill, grow]";

	private static final String SOCIETIES_IDENTIFIER = "ict-societies.eu";
	final static String YRNA_URL = "http://157.159.160.188:8080/YouRNotAloneServer";
	private static String SOCIETIES_XMLRPC_IP = "213.239.194.180";

	private String USER_ID;
	private boolean NO_USER;
	private String userName;

	private String userEmail = "john@doe.ar";
	private String testUserPassword = "password";
	private String userFirstname = "firstname";
	private String userLastname = "lastname";
	private String userInstitute = "DLR";
	private String skills = "";
	private String userCountry = "Germany";
	private String languages;
	private String[] languagesArray;
	private String[] skillsArray;

	// @Autowired(required=true)
	private ICtxBroker externalCtxBroker;
	// @Autowired(required=true)
	private ICommManager commMgr;

	private CtxEntityIdentifier ownerCtxId;
	private Requestor requestor;
	private IIdentity cssOwnerId;

	private ArrayList<CtxIdentifier> requestedCtxIds = new ArrayList<CtxIdentifier>();

	private IIdentityManager idMgr;

	



	@Autowired(required = true)
	public WantToHelp(ICtxBroker externalCtxBroker, ICommManager commMgr) {
		printAndLog("********** " + this.getClass() + " instantiated");

		this.externalCtxBroker = externalCtxBroker;
		this.commMgr = commMgr;
		
		
		xmlRpcClient_IWTH = new XMLRPCClient_IWTH();

		printAndLog("********** commMgr=" + commMgr);
		printAndLog("********** contextBroker=" + externalCtxBroker);
		

		userName = commMgr.getIdManager().getThisNetworkNode()
				.getIdentifier();
		int userNumber = -1;
		if (userName.startsWith("user"))
			userNumber = Integer.parseInt(userName.substring(4)); // starts with
																	// "user" -
																	// i.e. 4
																	// digits
		else
			NO_USER = true;
		if (userNumber < 10)
			USER_ID = "0" + userNumber;
		else
			USER_ID = "" + userNumber;

		try {
			cssOwnerId = commMgr.getIdManager().fromJid(
					commMgr.getIdManager().getThisNetworkNode().getBareJid());// e.g.
			// resolves to "xcmanager.societies.local"
			requestor = new Requestor(cssOwnerId);
			
			ownerCtxId = externalCtxBroker.retrieveIndividualEntityId(requestor, cssOwnerId).get();
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		} 

		this.idMgr = commMgr.getIdManager();

		this.cssOwnerId = this.getLocalIdentity();
		// cssOwnerId = commMgr.getIdManager().fromJid(commMgr.getIdManager().getThisNetworkNode().getBareJid());

//		initialiseContext(externalCtxBroker, commMgr, requestor);
		
		


		activate();
	}
	
	
	/**
	 * 
	 */
	private void initialiseContext(ICtxBroker ctxBroker, ICommManager commMgr,
			Requestor requestor){


		try {

			if (ownerCtxId==null)
				ownerCtxId = ctxBroker.retrieveIndividualEntityId(requestor,
					cssOwnerId).get();

//			if (ownerCtxId.getOwnerId().equals("john.societies.local")) {
//				BaseUser john = new John();
//				addContext(john);
//			} else if (ownerCtxId.getOwnerId().equals("jane.societies.local")) {
//				BaseUser jane = new Jane();
//				addContext(jane);
//
//			} else 
			if (ownerCtxId.getOwnerId().equals("user1.societies.local")) {
				BaseUser user1 = new User1();
				addContext(user1);

			} else if (ownerCtxId.getOwnerId().equals("user2.societies.local")) {
				BaseUser user2 = new User2();
				addContext(user2);

			} else if (ownerCtxId.getOwnerId().equals("user3.societies.local")) {
				BaseUser user3 = new User3();
				addContext(user3);

			} else if (ownerCtxId.getOwnerId().equals("user4.societies.local")) {
				BaseUser user4 = new User4();
				addContext(user4);

			} else if (ownerCtxId.getOwnerId().equals("user5.societies.local")) {
				BaseUser user5 = new User5();
				addContext(user5);

			} else if (ownerCtxId.getOwnerId().equals("xcmanager.societies.local")) {
				BaseUser user1 = new User1();
				addContext(user1);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		
		

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	void sendDataToCSDMandYRNA() {
		
		// Communicate with SOCIETIES to get context
		try {
			getUserDataFromCSS();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		printAndLog("*IW2H* ********* sendDataToCSDMandYRNA after context query skills="+skills);
		
		String skillsAndLanguages= skills;
		if (skills==null || skills.isEmpty()) skillsAndLanguages = languages;
		else if (skills.endsWith(",") && languages!=null) skillsAndLanguages += languages;
		else if (languages!=null && !languages.isEmpty()) skillsAndLanguages+=","+languages;
		if (skillsAndLanguages==null) skillsAndLanguages="";
		
		// Communicate with CSDM
		if (NO_USER)
			USER_ID = "00"; // TODO remove
		String XMLRPC_SERVER_ADDRESS = SOCIETIES_XMLRPC_IP + ":543" + USER_ID; // TODO for no-userX users
		printAndLog("********** CSDM xmlrpc signInUser > "
				+ xmlRpcClient_IWTH.signInUser(userEmail, testUserPassword,
						userLastname, userFirstname, userInstitute,
						XMLRPC_SERVER_ADDRESS, skillsAndLanguages));

		// Communicate with YRNA
		Volunteer volunteer = new Volunteer(userEmail, userFirstname,
				userLastname, userInstitute, userCountry, userEmail);

		if (languages != null)
			languagesArray = languages.split(",");
		else languagesArray = null;
		if (skills != null)
			skillsArray = skills.split(",");
		else skillsArray = null;
		
		sendVolunteerToYRNA(volunteer);

	}
	
	public void updateUserDataInCSSandYRNA(UserData userData) {
		if (requestor != null && ownerCtxId != null) {
			try {
				
				if (userData.getSkills()!=null){
					String skillsString = "";
					for (String skill: userData.getSkills())
						skillsString += skill + ",";
					if (skillsString.endsWith(",")) skillsString = skillsString.substring(0, skillsString.length()-1);
					
					printAndLog("*IW2H* updating change from CSDM to Societies. Skills:="+skillsString);
					updateContextAttribute(ownerCtxId, CtxAttributeTypes.SKILLS,skillsString);
				}

				if (userData.getLastName()!=null && !userData.getLastName().isEmpty())
					updateContextAttribute(ownerCtxId, CtxAttributeTypes.NAME_LAST,userData.getLastName());
				if (userData.getFirstName()!=null && !userData.getFirstName().isEmpty())
					updateContextAttribute(ownerCtxId, CtxAttributeTypes.NAME_FIRST,userData.getFirstName());
				if (userData.getEmail()!=null && !userData.getEmail().isEmpty())
					updateContextAttribute(ownerCtxId, CtxAttributeTypes.EMAIL,userData.getEmail());
				if (userData.getInstitute()!=null)
					updateContextAttribute(ownerCtxId, CtxAttributeTypes.AFFILIATION,userData.getInstitute());
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CtxException e) {
				e.printStackTrace();
			}
		}

		Volunteer volunteer = new Volunteer(userEmail, userData.getFirstName(),
				userData.getLastName(), userData.getInstitute(), userCountry,
				userData.getEmail());
		// TODO what happens if user changes his emailAddress in CSDM? Answer: CSDM cannot cope with it! So does not need handling here.

		if (userData.getSkills()!=null){
			for (String skill : userData.getSkills())
				volunteer.addSkill(skill.trim());
			languagesArray = null;
			skillsArray = null;
		}
		
		sendVolunteerToYRNA(volunteer);
	}

	/*
	 * commMgr.getIdManager().getThisNetworkNode().getDomain() = societies.local
	 * commMgr.getIdManager().getThisNetworkNode().getNodeIdentifier() = rich
	 * commMgr.getIdManager().getThisNetworkNode().getIdentifier() = xcmanager | userX
	 */
	public void getUserDataFromCSS() throws Exception {

		printAndLog("********** retrieve user data from CSS ... ");

		if (ownerCtxId==null)
			ownerCtxId = externalCtxBroker.retrieveIndividualEntityId(requestor,
					cssOwnerId).get();

		String name = null;

		CtxEntity ownerEntity = (CtxEntity) externalCtxBroker.retrieve(
				requestor, ownerCtxId).get();

		Iterator<CtxAttribute> foundAttrsIt = ownerEntity.getAttributes(
				CtxAttributeTypes.NAME).iterator();
		if (foundAttrsIt.hasNext())
			name = foundAttrsIt.next().getStringValue();
		if (name == null || name.equals("")) {
			// foundAttrsIt =
			// ownerEntity.getAttributes(CtxAttributeTypes.ID).iterator();
			// if (foundAttrsIt.hasNext())
			// name = foundAttrsIt.next().getStringValue();
			name = userName;
		}
		printAndLog("********** Name=" + name);

		// For CSDM, every user needs first and last name;
		if (name != null && !name.equals("")) {
			userLastname = name;
			userFirstname = name;
		}

		CtxAttribute attribute = null;
		
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.NAME_LAST)
				.iterator();
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			userLastname = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}

		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.NAME_FIRST)
				.iterator();
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			userFirstname = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}
			

		printAndLog("********** FirstName LastName:" + userFirstname + " "
				+ userLastname);

		if ((userLastname == null || userLastname.isEmpty())
				&& (userFirstname == null || userFirstname.isEmpty())) {
			
			
			System.out.println("TEST CASE ONLY. NO USER DATA AVAILABLE.");
			
			
			CtxAttribute nameLast = externalCtxBroker.createAttribute(
					requestor, ownerCtxId,
					CtxAttributeTypes.NAME_LAST).get();
			nameLast.setStringValue("Test");
			externalCtxBroker.update(requestor, nameLast).get();
			CtxAttribute nameFirst = externalCtxBroker.createAttribute(
					requestor, ownerCtxId,
					CtxAttributeTypes.NAME_FIRST).get();
			nameFirst.setStringValue("First");
			nameFirst = (CtxAttribute) externalCtxBroker.update(requestor,
					nameFirst).get();
		}

		userEmail = cssOwnerId + "@" + SOCIETIES_IDENTIFIER;
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.EMAIL)
				.iterator();
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			userEmail = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}
			
		// testUserEmail =
		// testUserFirstname+"."+testUserLastname+"@ict-societies.eu";
		printAndLog("********** cssOwnerId="+cssOwnerId+", firstname: "+userFirstname+ ", lastname: "+userLastname+", name: "+name+", email: " + userEmail);

		testUserPassword = userFirstname.toLowerCase()
				+ userLastname.toLowerCase();
		printAndLog("********** password: " + testUserPassword);

		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.AFFILIATION)
				.iterator();
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			userInstitute = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}

		foundAttrsIt = ownerEntity.getAttributes(
				CtxAttributeTypes.ADDRESS_HOME_COUNTRY).iterator();
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			userCountry = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}

		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.LANGUAGES)
				.iterator();
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			languages = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}
			

		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.SKILLS)
				.iterator();
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			skills = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}
			
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.INTERESTS)
				.iterator();
		String interests = null;
		if (foundAttrsIt.hasNext()){
			attribute = foundAttrsIt.next();
			interests = attribute.getStringValue();
			if (!requestedCtxIds.contains(attribute.getId())) requestedCtxIds.add(attribute.getId());
		}
			

		if (interests != null && !interests.equals("")) {
			if (skills == null || skills.equals(""))
				skills = interests;
			else if (skills.endsWith(","))
				skills += interests;
			else
				skills += "," + interests;
		}

	}

	/**
	 * @param volunteer
	 */
	private void sendVolunteerToYRNA(Volunteer volunteer) {

		if (skillsArray!=null && skillsArray.length!=0)
			for (String skill : skillsArray)
				volunteer.addSkill(skill.trim());
		if (languagesArray!=null && languagesArray.length!=0)
			for (String language : languagesArray)
				volunteer.addSpokenLanguage(language.trim());

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(UriBuilder.fromUri(YRNA_URL)
				.build());

		ClientResponse r = service.path("rest").path("/")
				.path(volunteer.getID()).accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, volunteer);
		printAndLog("********** send data to YRNA platform: " + r);
		
		printAndLog("*IW2H* updating change from CSDM to YRNA. Volunteer:="+volunteer.toString());
		
	}

	private void updateContextAttribute(CtxEntityIdentifier cssOwnerId, String type, String value) throws InterruptedException, ExecutionException, CtxException{
		final List<CtxIdentifier> ctxIds = this.externalCtxBroker.lookup(requestor, cssOwnerId,
				CtxModelType.ATTRIBUTE, type).get();
		if (!ctxIds.isEmpty()) {
			CtxAttribute attr = (CtxAttribute) externalCtxBroker.retrieve(requestor, ctxIds.get(0)).get();
			attr.setStringValue(value);
			this.externalCtxBroker.update(requestor,attr);
			LOG.debug("*IW2H* attribute updated: "+type+"="+value);
		} else {
			final CtxAttribute attr = this.externalCtxBroker.createAttribute(requestor,
					cssOwnerId, type).get();
			attr.setStringValue(value);
			this.externalCtxBroker.update(requestor,attr);
			LOG.debug("*IW2H* attribute created: "+type+"="+value);
		}
	}

	private void printAndLog(String string) {
		if (feedbackTextArea != null)
			feedbackTextArea.append(string + "\n");

		LOG.debug(string);
		// System.out.println(string);

	}
	
	
	private IIdentity getLocalIdentity() {

		IIdentity cssOwnerId = null;
		INetworkNode cssNodeId = this.idMgr.getThisNetworkNode();
		try {
			cssOwnerId = this.idMgr.fromJid(cssNodeId.getBareJid());
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}

		return cssOwnerId;
	}

	private void addContext(BaseUser user) {

		if (LOG.isInfoEnabled())
			LOG.info("Updating initial context values");

		String value;

		try {

			// AGE
			value = user.getAge();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.AGE,
						value);

			// BIRTHDAY
			value = user.getBirthday();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.BIRTHDAY,
						value);

			// EMAIL
			value = user.getEmail();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.EMAIL,
						value);

			// FRIENDS
			value = user.getFriends();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.FRIENDS,
						value);

			// INTERESTS
			value = user.getInterests();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId,
						CtxAttributeTypes.INTERESTS, value);

			// LANGUAGES
			value = user.getLanguages();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId,
						CtxAttributeTypes.LANGUAGES, value);

			// LOCATION_COORDINATES
			value = user.getLocationCoordinates();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId,
						CtxAttributeTypes.LOCATION_COORDINATES, value);

			// LOCATION_SYMBOLIC
			value = user.getLocationSymbolic();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId,
						CtxAttributeTypes.LOCATION_SYMBOLIC, value);

			// MOVIES
			value = user.getMovies();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.MOVIES,
						value);

			// NAME
			value = user.getName();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.NAME,
						value);

			// OCCUPATION
			value = user.getOccupation();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId,
						CtxAttributeTypes.OCCUPATION, value);

			// POLITICAL_VIEWS
			value = user.getPoliticalViews();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId,
						CtxAttributeTypes.POLITICAL_VIEWS, value);

			// SEX
			value = user.getSex();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.SEX,
						value);

			// STATUS
			value = user.getStatus();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.STATUS,
						value);

			// SKILLS
			value = user.getSkills();
			if (value != null && !value.isEmpty())
				this.updateContextAttribute(ownerCtxId, CtxAttributeTypes.SKILLS,
						value);

		} catch (Exception e) {
			LOG.info("error when initializing context data: "
					+ e.getLocalizedMessage());
		}
		
		
	}

	

	/*
	 * Currently only called from the end of the constructor
	 */
	public void activate() {

		LOG.info("*IW2H* activate: starting to sync with two web-platforms...");
		
		sendDataToCSDMandYRNA();

		// ++++++++++++ GUI start ++++++++++++

		// otherwise it does not startup in VIRGO
		// UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		// frame = new JFrame("IWantToHelp");
		// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// frame.addWindowListener(new WindowEventHandler());
		//
		// JPanel panel = new JPanel();
		// panel.setLayout(new MigLayout(PANEL_LAYOUT_CONSTRAINTS,
		// PANEL_COLUMN_CONTSTRAINTS, PANEL_ROW_CONSTRAINTS));
		// Dimension panelDimension = new Dimension(1200, 768);
		// panel.setPreferredSize(panelDimension);
		// frame.getContentPane().add(panel);
		//
		// feedbackTextArea = new JTextArea("");
		// feedbackTextArea.setEditable(false);
		// DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		// caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
		// JPanel feedbackPanel = new JPanel(new
		// MigLayout(FEEDBACK_LAYOUT_CONSTRAINTS, FEEDBACK_COLUMN_CONTSTRAINTS,
		// FEEDBACK_ROW_CONSTRAINTS));
		// feedbackPanel.add(scrollPane);
		//
		// subscribe = new
		// JButton("I want to help: subscribe to CSDM platform");
		// subscribe.setActionCommand(subscribeCommand);
		// subscribe.addActionListener(this);
		// unsubscribe = new JButton("I am done");
		// unsubscribe.setActionCommand(unsubscribeCommand);
		// unsubscribe.addActionListener(this);
		// unsubscribe.setVisible(false);
		//
		// panel.add(subscribe);
		// panel.add(unsubscribe);
		// panel.add(feedbackPanel);
		//
		// frame.pack();
		// frame.setVisible(true);

		// ------------ GUI end ------------

		// ++++++++++++ pull thread start ++++++++++++

		pullThread = new PullThread();
		pullThread.start();
		pullThread.setCheckData(true);

		// ------------ pull thread end ------------
		
		MyCtxChangeEventListener myContextUpdateListener = new MyCtxChangeEventListener(this); 
		for (CtxIdentifier ctxId: requestedCtxIds)
			try {
				externalCtxBroker.registerForChanges(requestor, myContextUpdateListener, ctxId);
			} catch (CtxException e) {
				e.printStackTrace();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase(subscribeCommand)) {

			// dummyTest();
			sendDataToCSDMandYRNA();

			// pullThread.setCheckData(true);
			subscribe.setVisible(false);
			unsubscribe.setVisible(true);
		} else if (command.equalsIgnoreCase(unsubscribeCommand)) {
			// pullThread.setCheckData(false);
			subscribe.setVisible(true);
			unsubscribe.setVisible(false);
		}
	}

	@Override
	public void provideHelp() {
		printAndLog("********** provide help");
	}

	
	private class PullThread extends Thread {

		private boolean run = true;
		private boolean checkData = false;
		private int pullIntervalInSeconds = 10;
		private UserData oldUserData;

		@Override
		public void run() {
			UserData userData = null;
			while (run) {
				if (checkData) {
					userData = xmlRpcClient_IWTH.getUserData(userEmail);
					printAndLog(userEmail + " settings from CSDM> " + userData
							+ "");
					if (oldUserData==null || (userData != null
							&& !userData.toString().equals(oldUserData.toString()))) {
						updateUserDataInCSSandYRNA(userData);
						oldUserData = userData;
					}
					else
						printAndLog("UserData in CSDM platform unchanged. Continue.");

				}
				try {
					sleep(pullIntervalInSeconds * 1000);
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
	

	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		private WantToHelp wantToHelp;

		/**
		 * @param wantToHelp
		 */
		public MyCtxChangeEventListener(WantToHelp wantToHelp) {
			this.wantToHelp = wantToHelp;
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** CREATED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** REMOVED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {
			LOG.info(event.getId() + ": *** UPDATED event ***");
			wantToHelp.sendDataToCSDMandYRNA();
		}
	}

}