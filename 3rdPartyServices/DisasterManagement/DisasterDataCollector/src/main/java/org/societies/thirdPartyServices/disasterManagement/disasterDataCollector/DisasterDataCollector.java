package org.societies.thirdPartyServices.disasterManagement.disasterDataCollector;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt.IDataToCSSFromDMT;
import org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt.SocketThread;

public class DisasterDataCollector implements BundleActivator, IDataToCSSFromDMT {
	
	private JFrame frame;
	private JTextArea feedbackTextArea;
	
	public DisasterDataCollector() {
		
		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		
		frame = new JFrame("DisasterDataCollectorFrame");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 10, 10));
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		
		feedbackTextArea = new JTextArea();
		feedbackTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
	    JPanel feedbackPanel = new JPanel(new BorderLayout());
	    feedbackPanel.add(scrollPane);
	    
	    panel.add(feedbackPanel);
	}
	
	private SocketThread socketThread;
	public void start(BundleContext context) throws Exception {
//	public void activate() {
		feedbackTextArea.append("!!! DisasterDataCollector Service started !!!\n");
		frame.pack();
		frame.setVisible(true);
		
		socketThread = new SocketThread(6957, this);
		socketThread.start();
	}

	public void stop(BundleContext context) throws Exception {
//	public void deactivate() {		
		feedbackTextArea.append("### DisasterDataCollector Service stopped ###\n");
		frame.dispose();
	}
	
	public static void main(String[] args) throws Exception {
		DisasterDataCollector disasterDataCollector = new DisasterDataCollector();
		disasterDataCollector.start(null);
//		disasterDataCollector.activate();
	}

	@Override
	public void setPosition(double latitude, double longitude, double elevation, int satNumber) {
		feedbackTextArea.append("setPosition "+latitude+" "+longitude+" "+elevation+" "+satNumber);
	}

	@Override
	public void setDirection(double roll, double pitch, double yaw) {
		feedbackTextArea.append("setDirection "+roll+" "+pitch+" "+yaw);
	}

	@Override
	public void gpsConnected(boolean connected) {
		feedbackTextArea.append("setDirection "+connected);
	}

	@Override
	public void compassConnected(boolean connected) {
		feedbackTextArea.append("compassConnected "+connected);
	}

	@Override
	public void viewLoaded(String viewXML) {
		feedbackTextArea.append("viewLoaded "+viewXML);
	}

	@Override
	public void poisSent() {
		feedbackTextArea.append("poisSent");
	}
}
