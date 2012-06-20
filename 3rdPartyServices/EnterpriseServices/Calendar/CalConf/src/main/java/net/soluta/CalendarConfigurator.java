package net.soluta;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import java.awt.GridLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FileChooserUI;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CalendarConfigurator {

	private JFrame frmCalendarpropgenerator;
	private JTextField txtClientId;
	private JTextField txtClientSecret;
	private JTextField txtAuthCode;
	private JTextField txtStorePropFilePath;
	private JTextArea console;
	private JButton btnGetAuthCode;
	private JButton btnGenPropFile;
	private JButton btnChooseFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CalendarConfigurator window = new CalendarConfigurator();
					window.frmCalendarpropgenerator.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "An error is occurred.");
					//e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public CalendarConfigurator() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	private void initialize() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		frmCalendarpropgenerator = new JFrame();
		frmCalendarpropgenerator.setTitle("CalendarPropGenerator");
		frmCalendarpropgenerator.setResizable(false);
		frmCalendarpropgenerator.setBounds(100, 100, 892, 506);
		frmCalendarpropgenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCalendarpropgenerator.getContentPane().setLayout(null);
		
		JLabel lblCliendId = new JLabel("Cliend ID:");
		lblCliendId.setBounds(45, 77, 90, 14);
		frmCalendarpropgenerator.getContentPane().add(lblCliendId);
		
		JLabel lblNewLabel = new JLabel("Client secret:");
		lblNewLabel.setBounds(45, 127, 108, 14);
		frmCalendarpropgenerator.getContentPane().add(lblNewLabel);
		
		txtClientId = new JTextField();
		txtClientId.setBounds(202, 74, 410, 20);
		frmCalendarpropgenerator.getContentPane().add(txtClientId);
		txtClientId.setColumns(10);
		
		txtClientSecret = new JTextField();
		txtClientSecret.setBounds(202, 124, 410, 20);
		frmCalendarpropgenerator.getContentPane().add(txtClientSecret);
		txtClientSecret.setColumns(10);
		
		 console = new JTextArea();
		 console.setLineWrap(true);
		 console.setWrapStyleWord(true);
		console.setBounds(10, 259, 864, 199);
		frmCalendarpropgenerator.getContentPane().add(console);
		
		JLabel lblAccesstoken = new JLabel("Authorization Code:");
		lblAccesstoken.setBounds(45, 179, 163, 14);
		frmCalendarpropgenerator.getContentPane().add(lblAccesstoken);
		
		txtAuthCode = new JTextField();
		txtAuthCode.setEditable(false);
		txtAuthCode.setBounds(202, 176, 410, 20);
		frmCalendarpropgenerator.getContentPane().add(txtAuthCode);
		txtAuthCode.setColumns(10);
		
		btnGenPropFile = new JButton("Generate properties file");
		btnGenPropFile.setEnabled(false);
		btnGenPropFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String code = txtAuthCode.getText();
				if (code.isEmpty()){
					JOptionPane.showMessageDialog(null, "Insert the Auth code to proceed.");
				}else{
				// End of Step 1 <--

				// Step 2: Exchange -->
//				AccessTokenResponse response = new GoogleAuthorizationCodeGrant(
//						httpTransport, jsonFactory, clientId, clientSecret, code,
//						redirectUrl).execute();
				// End of Step 2 <--
				HttpTransport httpTransport = new NetHttpTransport();
				JacksonFactory jsonFactory = new JacksonFactory();
				String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
				String scope = "https://www.googleapis.com/auth/calendar";
				GoogleAuthorizationCodeTokenRequest tokenReq = new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory, txtClientId.getText(), txtClientSecret.getText(), code, redirectUrl);
				GoogleTokenResponse tokenResp;
				try {
					tokenResp = tokenReq.execute();
				
				TokenResponse returnedToken = new TokenResponse();
				returnedToken.setAccessToken(tokenResp.getAccessToken());
				returnedToken.setRefreshToken(tokenResp.getRefreshToken());
				// Set and store tokens - old
//				accessToken = response.accessToken;
//				refreshToken = response.refreshToken;
				// Set and store tokens - new
				String accessToken = returnedToken.getAccessToken();
				String refreshToken = returnedToken.getRefreshToken();
				
				//Set the properties file
				Properties prop = new Properties();
				prop.setProperty("clientId", txtClientId.getText());
				prop.setProperty("clientSecret", txtClientSecret.getText());
				prop.setProperty("accessToken", accessToken);
				prop.setProperty("refreshToken", refreshToken);
				String tmpFilePath=txtStorePropFilePath.getText();
				if ((tmpFilePath.endsWith("\\"))||(tmpFilePath.endsWith("/"))){
					tmpFilePath=tmpFilePath+"bk.properties";
				}else{
					tmpFilePath=tmpFilePath+"/bk.properties";
				}
				prop.store(new FileOutputStream(new File(tmpFilePath), false), null);
				
				//Show ok dialog
				JOptionPane.showMessageDialog(null, "File generated successfully!");
				//ResetField and button
				btnChooseFile.setEnabled(true);
				btnGenPropFile.setEnabled(false);
				btnGetAuthCode.setEnabled(true);
				txtAuthCode.setText("");
				txtAuthCode.setEditable(false);
				txtClientId.setEditable(true);
				txtClientSecret.setEditable(true);
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					
				}}
			}

			
		});
		btnGenPropFile.setBounds(660, 169, 186, 45);
		frmCalendarpropgenerator.getContentPane().add(btnGenPropFile);
		
		btnGetAuthCode = new JButton("Get Auth Code");
		btnGetAuthCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((txtClientId.getText().isEmpty())||(txtClientSecret.getText().isEmpty())||(txtStorePropFilePath.getText().isEmpty())){
					JOptionPane.showMessageDialog(null, "All fields are required to proceed.");
				}else{
				btnChooseFile.setEnabled(false);
				btnGetAuthCode.setEnabled(false);
				txtClientId.setEditable(false);
				txtClientSecret.setEditable(false);
				
				
				HttpTransport httpTransport = new NetHttpTransport();
				JacksonFactory jsonFactory = new JacksonFactory();

				// The clientId and clientSecret are copied from the API Access tab on
				// the Google APIs Console

				// Or your redirect URL for web based applications.
				String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
				String scope = "https://www.googleapis.com/auth/calendar";

				try {
					// Step 1: Authorize -->
					String authorizationUrl = new GoogleAuthorizationRequestUrl(txtClientId.getText(),
							redirectUrl, scope).build();

					// Point or redirect your user to the authorizationUrl.
					console.setText("Go to the following link in your browser:\n "+authorizationUrl);
					txtAuthCode.setEditable(true);
					btnGenPropFile.setEnabled(true);
				
			}catch (Exception ex) {
				// TODO: handle exception
			}}
			}});
		btnGetAuthCode.setBounds(660, 83, 121, 43);
		frmCalendarpropgenerator.getContentPane().add(btnGetAuthCode);
		
		JLabel lblStorePropertiesFile = new JLabel("Store properties file path:");
		lblStorePropertiesFile.setBounds(45, 28, 186, 14);
		frmCalendarpropgenerator.getContentPane().add(lblStorePropertiesFile);
		
		txtStorePropFilePath = new JTextField();
		txtStorePropFilePath.setEditable(false);
		txtStorePropFilePath.setBounds(202, 25, 410, 20);
		frmCalendarpropgenerator.getContentPane().add(txtStorePropFilePath);
		txtStorePropFilePath.setColumns(10);
		
		 btnChooseFile = new JButton("Browse");
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.showOpenDialog(null);
               if( chooser.getSelectedFile()!=null){
               File f = chooser.getSelectedFile();
                txtStorePropFilePath.setText(f.getAbsolutePath());
			}}
		});
		btnChooseFile.setBounds(660, 24, 91, 23);
		frmCalendarpropgenerator.getContentPane().add(btnChooseFile);
	}
	
}
