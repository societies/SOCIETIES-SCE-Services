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

package ac.hw.display.example;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
/**
 * @author  Administrator
 * @created July 21, 2012
 */
public class ExampleServiceGUI extends JFrame implements ActionListener
{
	static ExampleServiceGUI theExampleServiceGUI;

	JPanel pnPanel0;

	JPanel pnPanel1;
	JTextArea txtNotification;
	JButton btnNotification;

	JPanel pnPanel2;
	JTextField tfTxtImageUrl;
	JButton btnUrlImage;

	JPanel pnPanel3;
	JLabel lbLblServiceAvailable;

	private final IExampleService exampleService;
	/**
	 */
	public static void main( String args[] ) 
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch ( ClassNotFoundException e ) 
		{
		}
		catch ( InstantiationException e ) 
		{
		}
		catch ( IllegalAccessException e ) 
		{
		}
		catch ( UnsupportedLookAndFeelException e ) 
		{
		}
		theExampleServiceGUI = new ExampleServiceGUI(null);
	} 

	/**
	 */
	public ExampleServiceGUI(IExampleService exampleService) 
	{
		super( "Example Service GUI" );
		this.exampleService = exampleService;

		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		pnPanel1.setBorder( BorderFactory.createTitledBorder( "Enter notification text to send to display" ) );
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		txtNotification = new JTextArea(2,10);
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 15;
		gbcPanel1.gridheight = 5;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 20,20,20,20 );
		gbPanel1.setConstraints( txtNotification, gbcPanel1 );
		pnPanel1.add( txtNotification );

		btnNotification = new JButton( "send Notification"  );
		btnNotification.addActionListener(this);
		gbcPanel1.gridx = 8;
		gbcPanel1.gridy = 7;
		gbcPanel1.gridwidth = 7;
		gbcPanel1.gridheight = 3;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 0,200,20,20 );
		gbPanel1.setConstraints( btnNotification, gbcPanel1 );
		pnPanel1.add( btnNotification );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 2;
		gbcPanel0.gridwidth = 20;
		gbcPanel0.gridheight = 8;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		pnPanel2 = new JPanel();
		pnPanel2.setBorder( BorderFactory.createTitledBorder( "Enter URL of image to send to display" ) );
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		pnPanel2.setLayout( gbPanel2 );

		tfTxtImageUrl = new JTextField( );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 15;
		gbcPanel2.gridheight = 4;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets( 20,20,20,20 );
		gbPanel2.setConstraints( tfTxtImageUrl, gbcPanel2 );
		pnPanel2.add( tfTxtImageUrl );

		btnUrlImage = new JButton( "Send Image"  );
		btnUrlImage.addActionListener(this);
		gbcPanel2.gridx = 8;
		gbcPanel2.gridy = 4;
		gbcPanel2.gridwidth = 7;
		gbcPanel2.gridheight = 4;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets( 0,200,20,10 );
		gbPanel2.setConstraints( btnUrlImage, gbcPanel2 );
		pnPanel2.add( btnUrlImage );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 10;
		gbcPanel0.gridwidth = 15;
		gbcPanel0.gridheight = 8;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel2, gbcPanel0 );
		pnPanel0.add( pnPanel2 );

		pnPanel3 = new JPanel();
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		lbLblServiceAvailable = new JLabel( "Not connected to display"  );
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 16;
		gbcPanel3.gridheight = 2;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 1;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbcPanel3.insets = new Insets( 20,30,20,0 );
		gbPanel3.setConstraints( lbLblServiceAvailable, gbcPanel3 );
		pnPanel3.add( lbLblServiceAvailable );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 17;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel3, gbcPanel0 );
		pnPanel0.add( pnPanel3 );

		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btnNotification)){
			if (this.txtNotification.getText().equals("")){
				JOptionPane.showMessageDialog(this, "Notification cannot be empty. Type something!", "Empty Notification", JOptionPane.ERROR_MESSAGE, null);
			}else{
				this.exampleService.sendNotification(txtNotification.getText());
			}
			
		}else if (e.getSource().equals(this.btnUrlImage)){
			
				try {
					URL url = new URL(this.tfTxtImageUrl.getText());
					this.exampleService.displayImage(url);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "URL for image is not valid.", "Malformed URL", JOptionPane.ERROR_MESSAGE, null);
				}
			
		}
		
	} 
} 
