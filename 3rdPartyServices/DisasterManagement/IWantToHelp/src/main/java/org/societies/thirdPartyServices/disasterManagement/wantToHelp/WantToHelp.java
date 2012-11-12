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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WantToHelp implements IWantToHelp, ActionListener {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(WantToHelp.class);
	
	private XMLRPCClient_IWTH xmlRpcClient_IWTH;
	private PullThread pullThread;

	public static final String subscribeCommand = "subscribe";
	public static final String unsubscribeCommand = "unsubscribe";
	
	private JFrame frame;
	private JTextArea feedbackTextArea; 
	private JButton subscribe;
	private JButton unsubscribe;
	
	public static final String PANEL_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 10, novisualpadding, ins 4, wrap 1"; //, debug 2000";
	public static final String PANEL_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String PANEL_ROW_CONSTRAINTS = "[][fill, grow]";
	
	public static final String FEEDBACK_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 0, novisualpadding, ins 0, wrap 1"; //, debug 2000";
	public static final String FEEDBACK_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String FEEDBACK_ROW_CONSTRAINTS = "[fill, grow]";

	private static final String SOCIETIES_IDENTIFIER = "ict-societies.eu";
	
	private String testUserEmail = "korbinian.frank@dlr.de";
	private String testUserPassword = "password";	
	private String testUserFirstname = "firstname";
	private String testUserLastname = "lastname";
	private String testUserInstitute = "institute";
	
	@Autowired(required=true)	
	private ICtxBroker externalCtxBroker;
	@Autowired(required=true)
	private ICommManager commMgr;
	
	public WantToHelp() {
		LOG.info("*** " + this.getClass() + " instantiated");
		
		xmlRpcClient_IWTH = new XMLRPCClient_IWTH();

		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		
		frame = new JFrame("IWantToHelp");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowEventHandler());

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout(PANEL_LAYOUT_CONSTRAINTS, PANEL_COLUMN_CONTSTRAINTS, PANEL_ROW_CONSTRAINTS));
		Dimension panelDimension = new Dimension(1200, 768); 
		panel.setPreferredSize(panelDimension);
		frame.getContentPane().add(panel);
		
		feedbackTextArea = new JTextArea("");
		feedbackTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
	    JPanel feedbackPanel = new JPanel(new MigLayout(FEEDBACK_LAYOUT_CONSTRAINTS, FEEDBACK_COLUMN_CONTSTRAINTS, FEEDBACK_ROW_CONSTRAINTS));
	    feedbackPanel.add(scrollPane);
		
		subscribe = new JButton("I want to help: subscribe to CSDM platform");
		subscribe.setActionCommand(subscribeCommand);
		subscribe.addActionListener(this);
		unsubscribe = new JButton("I am done");
		unsubscribe.setActionCommand(unsubscribeCommand);
		unsubscribe.addActionListener(this);
		unsubscribe.setVisible(false);

		panel.add(subscribe);
		panel.add(unsubscribe);
	    panel.add(feedbackPanel);
	}
	
	@PostConstruct
	public void activate() throws Exception {
		feedbackTextArea.append("on activate -> WantToHelp service started\n");

		pullThread = new PullThread();
		pullThread.start();
		pullThread.setCheckData(false);
		
		frame.pack();
		frame.setVisible(true);
		
	}

	@PreDestroy
	public void deactivate() throws Exception {
		feedbackTextArea.append("on deactivate -> WantToHelp service stopped\n");

		pullThread.setRun(false);
		
		frame.dispose();
	}
	
	public void getUserDataFromCSS() throws Exception{
		feedbackTextArea.append("retrieve user data from CSS ... \n");
		IIdentity cssOwnerId = commMgr.getIdManager().fromJid(commMgr.getIdManager().getThisNetworkNode().getBareJid());
		Requestor requestor = new Requestor(cssOwnerId);
		
		
		CtxEntityIdentifier ownerEntityIdentifier = externalCtxBroker.retrieveIndividualEntityId(requestor, cssOwnerId).get();
		

		CtxAttribute nameLast = externalCtxBroker.createAttribute(requestor, ownerEntityIdentifier, CtxAttributeTypes.NAME_LAST).get();
		nameLast.setStringValue("Test");
		externalCtxBroker.update(requestor, nameLast).get();
		CtxAttribute nameFirst = externalCtxBroker.createAttribute(requestor, ownerEntityIdentifier, CtxAttributeTypes.NAME_FIRST).get();
		nameFirst.setStringValue("First");
		nameFirst = (CtxAttribute) externalCtxBroker.update(requestor, nameFirst).get();
//		feedbackTextArea.append("nameFirst="+nameFirst.getStringValue()+"\n");	
		
		CtxEntity ownerEntity = (CtxEntity) externalCtxBroker.retrieve(requestor, ownerEntityIdentifier).get();
//		feedbackTextArea.append("retrieve completed\n");
		
		Iterator<CtxAttribute> foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.NAME_LAST).iterator();
		if (foundAttrsIt.hasNext())
			testUserLastname = foundAttrsIt.next().getStringValue();
//		feedbackTextArea.append(testUserLastname+"\n");
		
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.NAME_FIRST).iterator();
		if (foundAttrsIt.hasNext())
			testUserFirstname = foundAttrsIt.next().getStringValue();
//		feedbackTextArea.append(testUserFirstname+"\n");	

		testUserEmail = cssOwnerId + "@" + SOCIETIES_IDENTIFIER;
		foundAttrsIt = ownerEntity.getAttributes(CtxAttributeTypes.EMAIL).iterator();
		if (foundAttrsIt.hasNext())
			testUserEmail = foundAttrsIt.next().getStringValue();
		//testUserEmail = testUserFirstname+"."+testUserLastname+"@ict-societies.eu";
		feedbackTextArea.append("email: "+testUserEmail+"\n");

		// TODO replace following strings by getting CtxAttributeTypes
		testUserPassword = testUserFirstname+"__"+testUserLastname;
		testUserInstitute = "institute";
		
		// TODO retrieve data from CSS
	}
	
	public void updateUserDataInCSS() {
		// TODO update user data in CSS
	}

	@Override
	public void provideHelp() {
		LOG.info("provide help");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase(subscribeCommand)) {
			// TODO retrieve data from CSS
			try {
				//feedbackTextArea.append("before\n");
				getUserDataFromCSS();
				//feedbackTextArea.append("after\n\n\n\n\n\n\n\n\n\n\n");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			feedbackTextArea.append("xmlrpc on 'login' > "+xmlRpcClient_IWTH.signInUser(testUserEmail, testUserPassword, testUserLastname, testUserFirstname, testUserInstitute)+"\n");
			
			pullThread.setCheckData(true);
			subscribe.setVisible(false);
			unsubscribe.setVisible(true);
		} else if (command.equalsIgnoreCase(unsubscribeCommand)) {
			pullThread.setCheckData(false);
			subscribe.setVisible(true);
			unsubscribe.setVisible(false);
		}  
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
					userData = xmlRpcClient_IWTH.getUserData(testUserEmail);
					feedbackTextArea.append(testUserEmail + " settings from webDLRPHP> "+ userData + "\n");
					if (userData != null)
						updateUserDataInCSS();
				}
				try {
					sleep(pullIntervalInSeconds*1000);
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
		WantToHelp iWantToHelp = new WantToHelp();
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
		//textArea.append("got externalCtxBroker: " + externalCtxBroker+" \n");
		this.externalCtxBroker = externalCtxBroker;
	}
}