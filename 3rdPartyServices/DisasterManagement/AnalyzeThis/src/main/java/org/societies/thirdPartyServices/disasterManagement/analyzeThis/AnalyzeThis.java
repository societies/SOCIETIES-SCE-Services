package org.societies.thirdPartyServices.disasterManagement.analyzeThis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyzeThis implements IAnalyzeThis, BundleActivator, ActionListener {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(AnalyzeThis.class);
	
	private XMLRPCClient xmlrpcClient;
	
	public static final String subscribeCommand = "subscribe";
	public static final String addrequestCommand = "addrequest";

	private JFrame frame;
	private JButton subscribe;
	private JTextArea feedbackTextArea;
	private JTextArea headlineTextArea;
	private JButton addrequest;
	private JTextArea textTextArea;
	
	private String emailAddress = "korbinian@doe.ar";
	private String password = "password";	
	private String firstname = "firstname";
	private String lastname = "lastname";
	private String institute = "institute";
	
	@Autowired(required=true)	
	private ICtxBroker externalCtxBroker;
	@Autowired(required=true)
	private ICommManager commMgr;

	public AnalyzeThis() {
		LOG.info("*** " + this.getClass() + " instantiated");
		
		xmlrpcClient = new XMLRPCClient();
		
		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		
		frame = new JFrame("AnalyzeThisFrame");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1,10,10));
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		subscribe = new JButton("subscribe");
		subscribe.setActionCommand(subscribeCommand);
		Dimension buttonDimension = new Dimension(800,80);
		subscribe.setMinimumSize(buttonDimension);
		subscribe.setPreferredSize(buttonDimension);
		subscribe.setMaximumSize(buttonDimension);
		subscribe.addActionListener(this);

		headlineTextArea = new JTextArea();
		Dimension headlineDimension = new Dimension(600,20);
		headlineTextArea.setMinimumSize(headlineDimension);
		headlineTextArea.setPreferredSize(headlineDimension);
		headlineTextArea.setMaximumSize(headlineDimension);

		textTextArea = new JTextArea();
		Dimension textDimension = new Dimension(600,100);
		textTextArea.setMinimumSize(textDimension);
		textTextArea.setPreferredSize(textDimension);
		textTextArea.setMaximumSize(textDimension);
		
		addrequest = new JButton("add request");
		addrequest.setActionCommand(addrequestCommand);
		addrequest.setMinimumSize(buttonDimension);
		addrequest.setPreferredSize(buttonDimension);
		addrequest.setMaximumSize(buttonDimension);
		addrequest.addActionListener(this);

		feedbackTextArea = new JTextArea("");
		feedbackTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
	    JPanel feedbackPanel = new JPanel(new BorderLayout());
	    feedbackPanel.add(scrollPane);

	    panel.add(subscribe);
	    //panel.add(headlineTextArea);
	    //panel.add(textTextArea);
	    //panel.add(addrequest);
	    panel.add(feedbackPanel);
	}

	public void sayHello(String string) {
		feedbackTextArea.append("analyze this: " + string+" \n");

	}

	public void start(BundleContext context) throws Exception {
		feedbackTextArea.append("!!! org.societies.thirdPartyServices.disasterManagement.AnalyzeThis Service started !!!\n");
		frame.pack();
		frame.setVisible(true);
	}
	
	public void activate() throws Exception {
		IIdentity cssOwnerId = commMgr.getIdManager().fromJid(commMgr.getIdManager().getThisNetworkNode().getBareJid());
		Requestor requestor = new Requestor(cssOwnerId);
		
		CtxEntityIdentifier ownerEntityIdentifier = externalCtxBroker.retrieveIndividualEntityId(requestor, cssOwnerId).get();
		CtxEntity ownerEntity = (CtxEntity) externalCtxBroker.retrieve(requestor, ownerEntityIdentifier).get();
		
		lastname = ownerEntity.getAttributes(CtxAttributeTypes.NAME_LAST).iterator().next().getStringValue();
		firstname = ownerEntity.getAttributes(CtxAttributeTypes.NAME_FIRST).iterator().next().getStringValue();
		// TODO replace following strings by getting CtxAttributeTypes
		emailAddress = firstname+"."+lastname+"@societies.eu";
		password = firstname+"__"+lastname;
		institute = "institute";
	}

	public void stop(BundleContext context) throws Exception {
		feedbackTextArea.append("### org.societies.thirdPartyServices.disasterManagement.AnalyzeThis Service stopped ###\n");
		frame.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase(subscribeCommand))
			feedbackTextArea.append("%%% login: "+getXmlrpcClient().addUser(emailAddress, password, lastname, firstname, institute)+" %%%\n");
		else if (command.equalsIgnoreCase(addrequestCommand))
			feedbackTextArea.append("%%% add request> "+getXmlrpcClient().addRequest(headlineTextArea.getText(), textTextArea.getText())+" %%%\n");
	}
	
	public XMLRPCClient getXmlrpcClient() {
		return xmlrpcClient;
	}

	public static void main(String[] args) throws Exception {
		AnalyzeThis analyzeThis = new AnalyzeThis();
		analyzeThis.start(null);
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