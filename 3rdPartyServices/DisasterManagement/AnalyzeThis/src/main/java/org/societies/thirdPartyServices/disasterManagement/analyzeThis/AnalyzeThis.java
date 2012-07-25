package org.societies.thirdPartyServices.disasterManagement.analyzeThis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.broker.ICtxBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyzeThis implements IAnalyzeThis, BundleActivator, ActionListener {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(AnalyzeThis.class);
	
	private XMLRPCClient xmlrpcClient;

	private JFrame frame;
	private JButton subscribe;
	private JTextArea textArea;

	@Autowired(required=true)	
	private ICtxBroker externalCtxBroker;

	public ICtxBroker getExternalCtxBroker() {
		return externalCtxBroker;
	}

	public void setExternalCtxBroker(ICtxBroker externalCtxBroker) {
		textArea.append("got externalCtxBroker: " + externalCtxBroker+" \n");
		this.externalCtxBroker = externalCtxBroker;
	}

	public AnalyzeThis() {
		LOG.info("*** " + this.getClass() + " instantiated");
		
		xmlrpcClient = new XMLRPCClient();
		
		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());

		
		frame = new JFrame("AnalyzeThisFrame");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(new JLabel("subscribe to DLR platform"), BorderLayout.NORTH);
		subscribe = new JButton("subscribe");
		subscribe.setPreferredSize(new Dimension(200,30));
		subscribe.addActionListener(this);
		frame.getContentPane().add(subscribe, BorderLayout.CENTER);
		textArea = new JTextArea("");
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(textArea);
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.setPreferredSize(new Dimension(600,200));
	    panel.add(scrollPane);
	    frame.getContentPane().add(panel, BorderLayout.SOUTH);
	}

	public void sayHello(String string) {
		textArea.append("analyze this: " + string+" \n");

	}

	public void start(BundleContext context) throws Exception {
		textArea.append("!!! org.societies.thirdPartyServices.disasterManagement.AnalyzeThis Service started !!!\n");
		frame.pack();
		frame.setVisible(true);

	}

	public void stop(BundleContext context) throws Exception {
		textArea.append("### org.societies.thirdPartyServices.disasterManagement.AnalyzeThis Service stopped ###\n");
		frame.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		textArea.append("%%% add user: "+getXmlrpcClient().addUser("korbinian@doe.ar", "password", "lastname", "firstname", "institute" )+" %%%\n");
	}
	
	public XMLRPCClient getXmlrpcClient() {
		return xmlrpcClient;
	}

	public static void main(String[] args) throws Exception {
		AnalyzeThis analyzeThis = new AnalyzeThis();
		analyzeThis.start(null);
	}

}