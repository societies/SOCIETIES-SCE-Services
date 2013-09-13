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

package org.societies.thirdPartyServices.disasterManagement.disasterDataCollector;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt.IDataToCSSFromDMT;
import org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt.SocketThread;
import org.springframework.stereotype.Service;

@Service
public class DisasterDataCollector implements IDisasterDataCollector, IDataToCSSFromDMT, ActionListener {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(DisasterDataCollector.class);
	
	private SocketThread socketThread;

	public static final String sendViewCommand = "sendview";
	public static final String syncPOIsCommand = "syncPOIs";
	
	private JFrame frame;
	private JTextArea feedbackTextArea;
	private JButton sendViewButton;
	private JButton syncPOIsButton;
	
	public static final String PANEL_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 10, novisualpadding, ins 4, wrap 1"; //, debug 2000";
	public static final String PANEL_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String PANEL_ROW_CONSTRAINTS = "[fill, grow][]";
	
	public static final String FEEDBACK_LAYOUT_CONSTRAINTS = "hidemode 3, gap 0 0, novisualpadding, ins 0, wrap 1"; //, debug 2000";
	public static final String FEEDBACK_COLUMN_CONTSTRAINTS = "[fill, grow]";
	public static final String FEEDBACK_ROW_CONSTRAINTS = "[fill, grow]";
	
	
	//@Autowired(required=true)	
	private ICtxBroker externalCtxBroker;
	//@Autowired(required=true)
	private ICommManager commMgr;
	
	private String lastView = null;
	
	public DisasterDataCollector() {
		LOG.info("*** " + this.getClass() + " instantiated");
		
		socketThread = new SocketThread(6957, this);
		
		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		
		frame = new JFrame("DisasterDataCollector");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowEventHandler());

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout(PANEL_LAYOUT_CONSTRAINTS, PANEL_COLUMN_CONTSTRAINTS, PANEL_ROW_CONSTRAINTS));
		Dimension panelDimension = new Dimension(1200, 768); 
		panel.setPreferredSize(panelDimension);
		frame.getContentPane().add(panel);
		
		sendViewButton = new JButton("send view to DMT");
		sendViewButton.setActionCommand(sendViewCommand);
		sendViewButton.addActionListener(this);
		sendViewButton.setEnabled(false);

		syncPOIsButton = new JButton("syncronise all Points Of Interest (POI) on DMT");
		syncPOIsButton.setActionCommand(syncPOIsCommand);
		syncPOIsButton.addActionListener(this);
		
		feedbackTextArea = new JTextArea();
		feedbackTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
	    JPanel feedbackPanel = new JPanel(new MigLayout(FEEDBACK_LAYOUT_CONSTRAINTS, FEEDBACK_COLUMN_CONTSTRAINTS, FEEDBACK_ROW_CONSTRAINTS));
	    feedbackPanel.add(scrollPane);
	    
	    panel.add(feedbackPanel);
	    panel.add(sendViewButton);
	    panel.add(syncPOIsButton);
	}

	@PostConstruct
	public void activate() {
		feedbackTextArea.append("on activate -> DisasterDataCollector service started\n");
		
		frame.pack();
		frame.setVisible(true);
		
		socketThread.start();
	}
	
	@PreDestroy
	public void deactivate() {
		feedbackTextArea.append("on deactivate -> DisasterDataCollector service stopped ... \n");
		
		frame.dispose();
		
		socketThread.shutdown();
	}
	
	@Override
	public void setPosition(double latitude, double longitude, double elevation, int satNumber) {
		feedbackTextArea.append("received from DMT > new position ( "+latitude+" "+longitude+" "+elevation+" "+satNumber+" ) \n");
	}

	@Override
	public void setDirection(double roll, double pitch, double yaw) {
		feedbackTextArea.append("received from DMT > new direction ( "+roll+" "+pitch+" "+yaw+" ) \n");
	}

	@Override
	public void gpsConnected(boolean connected) {
		feedbackTextArea.append("received from DMT > GPS connected ( "+connected+" ) \n");
	}

	@Override
	public void compassConnected(boolean connected) {
		feedbackTextArea.append("received from DMT > compass connected ( "+connected+" ) \n");
	}

	@Override
	public void viewLoaded(String viewXML) {
		feedbackTextArea.append("received from DMT > new view \n"); //> "+viewXML+" \n");
		lastView = viewXML;
		sendViewButton.setEnabled(true);
	}

	@Override
	public void poisSent() {
		feedbackTextArea.append("received from DMT > sync request \n");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
//		System.out.println("action command: "+command);
		if (command.equalsIgnoreCase(sendViewCommand)) {
			socketThread.setView(lastView);
		} else if (command.equalsIgnoreCase(syncPOIsCommand)) {
			socketThread.sendPOIs();
		}
	}
	
	/**
	 * main method for testing
	 */
	public static void main(String[] args) throws Exception {
		DisasterDataCollector disasterDataCollector = new DisasterDataCollector();
		disasterDataCollector.activate();
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