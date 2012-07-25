package org.societies.thirdPartyServices.disasterManagement.iWantToHelp;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IWantToHelp implements IIWantToHelp, BundleActivator {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(IWantToHelp.class);
	
	private JFrame frame;
	private JTextArea feedbackTextArea;

	public IWantToHelp() {

		// otherwise it does not startup in VIRGO
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());

		frame = new JFrame("IWantToHelpFrame");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 10, 10));
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		feedbackTextArea = new JTextArea("");
		feedbackTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)feedbackTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
	    JPanel feedbackPanel = new JPanel(new BorderLayout());
	    feedbackPanel.add(scrollPane);
	    
	    panel.add(feedbackPanel);
	}
	
	public void start(BundleContext context) throws Exception {
		feedbackTextArea.append("!!! org.societies.thirdPartyServices.disasterManagement.IWantToHelp Service started !!!\n");
		frame.pack();
		frame.setVisible(true);
	}

	public void stop(BundleContext context) throws Exception {
		feedbackTextArea.append("### org.societies.thirdPartyServices.disasterManagement.IWantToHelp Service stopped ###\n");
		frame.dispose();
	}
	
	public static void main(String[] args) throws Exception {
		IWantToHelp iWantToHelp = new IWantToHelp();
		iWantToHelp.start(null);
	}

	@Override
	public void provideHelp() {
		LOG.info("provide help");
	}

}
