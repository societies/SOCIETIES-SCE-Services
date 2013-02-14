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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.thirdPartyServices.disasterManagement.analyzeThis.data.TicketData;
import org.societies.thirdPartyServices.disasterManagement.analyzeThis.xmlrpc.XMLRPCClient_AT;
import org.societies.thirdPartyServices.disasterManagement.analyzeThis.xmlrpc.XMLRPCServer_AT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyzeThis implements IAnalyzeThis, ActionListener {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(AnalyzeThis.class);

	private XMLRPCServer_AT xmlRpcServer;
	private XMLRPCClient_AT xmlRpcClient_AT;
	private PullThread pullThread;
	
	public static final String subscribeCommand = "subscribe";
	public static final String unsubscribeCommand = "unsubscribe";
	public static final String addticketCommand = "addticket";

	private JFrame frame;
	private JButton subscribe;
	private JButton unsubscribe;
	private JTextArea feedbackTextArea;
	private JButton addticket;
	
	public static final String PANEL_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 10, novisualpadding, ins 4, wrap 1"; //, debug 2000";
	public static final String PANEL_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String PANEL_ROW_CONSTRAINTS = "[][fill, grow]";
	
	public static final String FEEDBACK_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 0, novisualpadding, ins 0, wrap 1"; //, debug 2000";
	public static final String FEEDBACK_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String FEEDBACK_ROW_CONSTRAINTS = "[fill, grow]";

	private static final int BASIS_PORT = 54300;
	
	
	@Autowired(required=true)
	private ICommManager commMgr;
	@Autowired(required=true)
	private ITrustEvidenceCollector trustEvidenceCollector;
	@Autowired(required=true)
	private ITrustBroker trustBroker;


	public AnalyzeThis() {
		LOG.info("*** " + this.getClass() + " instantiated");
		
		xmlRpcClient_AT = new XMLRPCClient_AT();

		String xmppDomain = commMgr.getIdManager().getThisNetworkNode().getDomain();
		int userNumber = Integer.parseInt(xmppDomain.substring(4, xmppDomain.indexOf('.'))); // subdomain always to start with "user" - i.e. 4 digits
		int port = BASIS_PORT + userNumber;
		
    	BasicConfigurator.configure();
        try {
			xmlRpcServer = new XMLRPCServer_AT(port);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		
		
		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		
		frame = new JFrame("AnalyzeThis");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowEventHandler());
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout(PANEL_LAYOUT_CONSTRAINTS, PANEL_COLUMN_CONTSTRAINTS, PANEL_ROW_CONSTRAINTS));
		Dimension panelDimension = new Dimension(1200, 768); 
		panel.setPreferredSize(panelDimension);
		frame.getContentPane().add(panel);
		
		subscribe = new JButton("subscribe to recent CSDM requests");
		subscribe.setActionCommand(subscribeCommand);
		subscribe.addActionListener(this);
		unsubscribe = new JButton("I am done");
		unsubscribe.setActionCommand(unsubscribeCommand);
		unsubscribe.addActionListener(this);
		unsubscribe.setVisible(false);
		
		addticket = new JButton("add ticket");
		addticket.setActionCommand(addticketCommand);
		addticket.addActionListener(this);

		feedbackTextArea = new JTextArea("");
		feedbackTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
	    JPanel feedbackPanel = new JPanel(new MigLayout(FEEDBACK_LAYOUT_CONSTRAINTS, FEEDBACK_COLUMN_CONTSTRAINTS, FEEDBACK_ROW_CONSTRAINTS));
	    feedbackPanel.add(scrollPane);

	    panel.add(subscribe);
	    panel.add(unsubscribe);
	    panel.add(feedbackPanel);
	}
	
	@PostConstruct
	public void activate() throws Exception {
		feedbackTextArea.append("on activate -> AnalyzeThis service started\n");

		pullThread = new PullThread();
		pullThread.start();
		pullThread.setCheckData(false);
		
		frame.pack();
		frame.setVisible(true);	
	}

	@PreDestroy
	public void deactivate() throws Exception {
		feedbackTextArea.append("on deactivate ->AnalyzeThis service stopped ... \n");

		pullThread.setRun(false);
		
		frame.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase(subscribeCommand)) {
			pullThread.setCheckData(true);
			subscribe.setVisible(false);
			unsubscribe.setVisible(true);
		} else if (command.equalsIgnoreCase(unsubscribeCommand)) {
			pullThread.setCheckData(false);
			subscribe.setVisible(true);
			unsubscribe.setVisible(false);
		} else if (command.equalsIgnoreCase(addticketCommand)) {}
	}
	
	private class PullThread extends Thread {
		
		private boolean run = true;
		private boolean checkData = false;
		private int pullIntervalInSeconds = 1;
		
		private final SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		private Map<Integer, TicketData> ticketDataMap = new HashMap<Integer, TicketData>();
		
		@Override
		public void run() {
			while(run){
				if (checkData) {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.SECOND, pullIntervalInSeconds);
					
					for (TicketData ticketData : xmlRpcClient_AT.getTickets(mysqlFormat.format(cal.getTime()))) 
						if (!ticketDataMap.containsKey(ticketData.getID())) {
							feedbackTextArea.append("new request> "+ ticketData + "\n");
							ticketDataMap.put(ticketData.getID(), ticketData);
						}
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
		AnalyzeThis analyzeThis = new AnalyzeThis();
		analyzeThis.activate();
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

	/**
	 * @return the trustEvidenceCollector
	 */
	public ITrustEvidenceCollector getTrustEvidenceCollector() {
		return trustEvidenceCollector;
	}

	/**
	 * @param trustEvidenceCollector the trustEvidenceCollector to set
	 */
	public void setTrustEvidenceCollector(ITrustEvidenceCollector trustEvidenceCollector) {
		this.trustEvidenceCollector = trustEvidenceCollector;
	}

	/**
	 * @return the trustBroker
	 */
	public ITrustBroker getTrustBroker() {
		return trustBroker;
	}

	/**
	 * @param trustBroker the trustBroker to set
	 */
	public void setTrustBroker(ITrustBroker trustBroker) {
		this.trustBroker = trustBroker;
	}
}