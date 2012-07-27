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

package org.societies.thirdPartyServices.disasterManagement.iWantToHelp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IWantToHelp implements IIWantToHelp, BundleActivator {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(IWantToHelp.class);
	
	private XMLRPCClient xmlrpcClient;
	private GetUserDataThread getUserDataThread;
	
	private JFrame frame;
	private JTextArea feedbackTextArea;

	public IWantToHelp() {

		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		
		xmlrpcClient = new XMLRPCClient();

		frame = new JFrame("IWantToHelpFrame");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowEventHandler());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 10, 10));
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		feedbackTextArea = new JTextArea("");
		feedbackTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
	    JPanel feedbackPanel = new JPanel(new BorderLayout());
		Dimension feedbackDimension = new Dimension(600,400);
		feedbackPanel.setMinimumSize(feedbackDimension);
		feedbackPanel.setPreferredSize(feedbackDimension);
		feedbackPanel.setMaximumSize(feedbackDimension);
	    feedbackPanel.add(scrollPane);
	    
	    panel.add(feedbackPanel);
	}
	
	public void start(BundleContext context) throws Exception {
		feedbackTextArea.append("!!! org.societies.thirdPartyServices.disasterManagement.IWantToHelp Service started !!!\n");
		
		getUserDataThread = new GetUserDataThread();
		getUserDataThread.start();
		
		frame.pack();
		frame.setVisible(true);
	}

	public void stop(BundleContext context) throws Exception {
		feedbackTextArea.append("### org.societies.thirdPartyServices.disasterManagement.IWantToHelp Service stopped ###\n");
		frame.dispose();
		getUserDataThread.setRun(false);
	}
	
	public static void main(String[] args) throws Exception {
		IWantToHelp iWantToHelp = new IWantToHelp();
		iWantToHelp.start(null);
	}

	@Override
	public void provideHelp() {
		LOG.info("provide help");
	}
	
	private class GetUserDataThread extends Thread {
		
		private boolean run = true;

		@Override
		public void run() {
			while(run){
				feedbackTextArea.append(xmlrpcClient.getUserData("user@test.de") + "\n");
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void setRun(boolean run) {
			this.run = run;
		}
	}
	
	private class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			try {
				stop(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
