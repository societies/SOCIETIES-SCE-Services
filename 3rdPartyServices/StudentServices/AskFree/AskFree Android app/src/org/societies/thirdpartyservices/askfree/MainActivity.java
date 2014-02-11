package org.societies.thirdpartyservices.askfree;


import org.societies.thirdpartyservices.askfree.R;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.societies.android.api.contentproviders.CSSContentProvider;
import org.societies.thirdpartyservices.askfree.remotedbdata.GetTopicDataTask;
import org.societies.thirdpartyservices.askfree.remotedbdata.Topic;
import org.societies.thirdpartyservices.askfree.tools.CheckUpdateTask;
import org.societies.thirdpartyservices.askfree.tools.SocketService;

import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.AutobahnConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button publishBtn;
	TextView status;
	TextView userId;
	TextView locationTextView;
	TextView moduleTextView;
	EditText questionEditText;
	Switch smartQuestSwitch; 

	JSONParser jsonParser= new JSONParser();

	private String topic;
	private int position;
	private String question;
	private boolean chooseModule;
	private boolean smartQuestions;
	private boolean connectedWamp;

	private String symbolicLocation;
	private boolean sockService;

	SessionManager session;

	protected static final String TAG = MainActivity.class.getSimpleName();

	private static final int MENU_ITEM_LOGOUT = Menu.FIRST;
	private static final int MENU_ITEM_REFRESH = 2;
	private static final int SETTINGS = 3;
	private static final int UPDATE = 4;

	//protected static final String SERVICE_CONNECTED = "im143.af_s.location.CONNECTED";
	protected static final String LOCATION_CHANGE = "org.societies.thirdpartyservices.askfree.location.LOCATION_CHANGE";
	protected static final String LOCATION_DB_DATA = "org.societies.thirdpartyservices.askfree.location.LOCATION_DB_DATA";
	protected static final String TOPIC_DATA = "org.societies.thirdpartyservices.askfree.remotedbdata.TOPIC_DATA";


	Context appContext; 

	boolean contextClientRunning;

	Receiver receiver;

	HashMap<String,String> locationToURI = new HashMap<String,String>();
	HashMap<String,String> locationToTopicID = new HashMap<String,String>();
	ArrayList<Topic> topicList;

	private boolean locationAvailable = false;

	private final AutobahnConnection mConnection = new AutobahnConnection();

	private void WAMPConnection() {

		final String wsuri = "ws://54.218.113.176:8080";

		mConnection.connect(wsuri, new Autobahn.SessionHandler() {

			@Override
			public void onOpen() {
				Log.d(TAG, "Status: Connected to " + wsuri);
				status.setTextColor(Color.GREEN);
				status.setText("Connected");
				if(getChooseModule()){
					enableScreenComponents(true);
				}
				if(getSymbolicLocation() != null){
					chooseClass(getSymbolicLocation());
				}
				setConnectedWamp(mConnection.isConnected());
			}

			@Override
			public void onClose(int code, String reason) {
				Log.d(TAG, "code"+code+", reason"+reason);
				status.setTextColor(Color.RED);
				status.setText("Disconnected");				
				publishBtn.setEnabled(false);
			}
		});
	}

	/* ************************************** CREATE ACTIVITY ************************************************************** */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//create a session object
		session = new SessionManager(getApplicationContext());

		//get login status, if false kill the activity
		if(session.isLoggedIn() == false){
			finish();
		}
		//start the login activity
		session.checkLogin();

		appContext = this.getApplicationContext();
		receiver = new Receiver();
		registerReceiver(receiver, createIntentFilter());
		Log.d(TAG, "Register broadcast receiver: "+receiver.getClass().getName());

		//GetLocationDataTask getLocationData = new GetLocationDataTask();
		//getLocationData.execute();

		GetTopicDataTask getTopicData = new GetTopicDataTask(appContext,this);
		getTopicData.execute();

		publishBtn = (Button) findViewById(R.id.submitButton);		
		status = (TextView) findViewById(R.id.statusTextView);
		questionEditText = (EditText) findViewById(R.id.questionEditText);	
		userId = (TextView) findViewById(R.id.user_idTextView);
		locationTextView = (TextView) findViewById(R.id.locationTxtView);
		moduleTextView = (TextView) findViewById(R.id.moduleTextView);
		smartQuestSwitch = (Switch) findViewById(R.id.smartQuestSwitch);

		if (savedInstanceState != null) {
			this.sockService = (boolean) savedInstanceState.getBoolean("socketService");
			this.symbolicLocation = (String) savedInstanceState.getString("symbolicLocation");
		}

		if(sockService == false) {
			Intent intent = new Intent(this, SocketService.class);
			Log.d(TAG, "Sending Intent to start SocketService: ");
			startService(intent);
			this.sockService = true;

		}

		// displaying user data
		userId.setText("User id: "+ this.getUserId());

		if(isOnline()){
			//connect to websocket server
			WAMPConnection();
		}else if(!isOnline()){
			this.enableScreenComponents(false);
			alert("Please Connect to the Internet");
			status.setTextColor(Color.RED);
			status.setText("No Internet Connection");
		}


		if (getSymbolicLocation() == null){
			locationTextView.setText("Location: not available");
			this.enableScreenComponents(false);
		}else{
			locationTextView.setText("Location: " +getSymbolicLocation());
			this.enableScreenComponents(false);
		}

		publishBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setQuestion();
				publish();
			}
		});

		smartQuestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					setSmartQuestions(isChecked);
				} else {
					setSmartQuestions(isChecked);
				}
			}
		});
	}


	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putBoolean("socketService", this.sockService);
		outState.putString("symbolicLocation", getSymbolicLocation());
	}

	/* ************************************** OPTIONS MENU ************************************************************** */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		//Group ID
		int groupId=0;
		//Unique Menu Item identifier. Used for event handling
		int menuItemId = MENU_ITEM_LOGOUT;
		//The order position of the item
		int menuItemOrder = Menu.NONE;
		//Text to be displayed for this menu item
		int menuItemText = R.string.menu_item_log_out;

		int menuItemRefreshId = MENU_ITEM_REFRESH;
		//int menuItemRefreshOrder = Menu.NONE;
		int menuItemRefreshText = R.string.menu_item_refresh;

		//Create the Menu Item and keep a reference to it
		MenuItem menuItem = menu.add(groupId,menuItemId,menuItemOrder,menuItemText);
		MenuItem menuItemRefresh = menu.add(groupId,menuItemRefreshId,menuItemOrder,menuItemRefreshText);
		MenuItem menuItemSettings = menu.add(groupId,3,menuItemOrder,R.string.settings);
		MenuItem menuItemUpdate = menu.add(groupId,UPDATE,menuItemOrder,R.string.update_app);

		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		menuItemRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);		
		menuItemSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menuItemUpdate.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		return true;
		
		// MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu, menu);
        //return true;
	}

	/* **************************************************************************************************** */

	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);

		//Find which menu item has been selected
		switch(item.getItemId()){
		//check for each known Menu Item
		case(MENU_ITEM_LOGOUT):
			//perform menu handler actions
			finish();
		session.logoutUser();
		return true;
		case(MENU_ITEM_REFRESH):
			if (mConnection.isConnected()) {
				if(getSymbolicLocation() != null){
					chooseClass(getSymbolicLocation());
				}
				return true;
			}else if(isOnline() == false){
				this.enableScreenComponents(false);
				alert("Please Connect to the Internet");
				status.setTextColor(Color.RED);
				status.setText("No Internet Connection");
			}else if(!mConnection.isConnected()){
				WAMPConnection();
			}						
		return true;
		case(SETTINGS):
			//perform menu handler actions
			Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
		startActivity(intent);
		return true;
		case(UPDATE):
			if(isOnline()){
				//check if there is an updated version of AskFree
				CheckUpdateTask checkUpdateTask= new CheckUpdateTask(this,true);
				checkUpdateTask.execute();
			}else{
				alert("Please Connect to the Internet");
			}
		return true;
		//Return false if you have not handled the Menu Item
		default: return false;
		}		
	}

	/* **************************************************************************************************** */	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mConnection.isConnected()) {
			mConnection.disconnect();
		}
		teardownBroadcastReceiver();
		//finish();
	}

	/* **************************************************************************************************** */

	private static class MyEvent1 {
		public int type;
		public String question;
		public int q_id;

		public MyEvent1(){}
		@Override
		public String toString() {
			return "{type: " + type +
					", question: " + question +
					", q_id: " + q_id +"}";
		}

		public String getQuestion(){
			return question;
		}

		public int getType(){
			return type;
		}

		public int getQId(){
			return q_id;
		}	
	}

	private void subscribe(String topic) {
		Log.d(TAG, "Subscribed to topic " +  topic);
		mConnection.subscribe(topic, MyEvent1.class, new Autobahn.EventHandler() {


			@Override
			public void onEvent(String topic, Object event) {

				MyEvent1 evt = (MyEvent1) event;

				Log.d(TAG, "Event received " + evt.toString());
				Log.d(TAG, "Event received.Question: " + evt.getQuestion());
				Log.d(TAG, "Event received.Question: " + evt.getQId());

				if(evt.getType()==1){
					alertDialog(evt.getQuestion(),evt.getQId(),topic);
				}else if(evt.getType() == 0){
					alert("This question has no meanigfull context");
				}				
			}
		});
	}

	public void publish(){

		if (getSymbolicLocation() == null){
			alert("No location available");
		}

		String s_id = this.getUserId();

		//submit question to db
		SubmitQuestionTask submitQuest = new SubmitQuestionTask(this);
		for(String topicLoc : locationToURI.keySet()){	

			String topicURI = locationToURI.get(topicLoc);
			Log.d(TAG, "publish topicURI: " + topicURI);
			if(getSymbolicLocation().equals(topicLoc)){
				Log.d(TAG, "Symbolic location " + getSymbolicLocation() + " matches location from db " + topicLoc);
				//TODO: add s_id to json message
				if(isSmartQuestions() == true){
					JSONObject json = jsonParser.writeJSON(this.getQuestion(), "1");
					mConnection.publish(topicURI, json.toString());
					Log.d(TAG, "SQ is on ");
				}else{
					//JSONObject json = jsonParser.writeJSON(this.getQuestion(), "2");
					JSONObject json = jsonParser.writeJSON2(this.getQuestion(), getCssId(),"2");
					mConnection.publish(topicURI, json.toString());
					Log.d(TAG, "SQ is off ");
				}

				String topicID = locationToTopicID.get(topicLoc);
				Log.d(TAG, "publish topicID: " + topicID);
				submitQuest.execute(this.getQuestion(),s_id,topicID,topicLoc);
				break;
			}		
		}
		//clean question field
		questionEditText.setText("");
	}


	public void alertDialog(final String message, final int q_id,final String topic){
		Context context = MainActivity.this;
		String title = "Do you mean:";
		String button1String = "Yes";
		String button2String = "No";

		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);

		final String q_Id = Integer.toString(q_id);

		//Yes button
		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//increase question counter in the db table displayed_questions
				IncreaseQuestionCounterTask incQuestCount = new IncreaseQuestionCounterTask();
				incQuestCount.execute(q_Id);

				//create a json object that contains the q_id
				JSONObject json = jsonParser.writeJSON2(q_Id, message, "3");
				Log.d(TAG, json.toString());
				//send this json object to the websocket server in order to 
				//update the counter that is displayed on the screen.
				mConnection.publish(topic, json.toString());
			}
		});

		//No button
		ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//re-send the question as a jsonObject
				JSONObject json = jsonParser.writeJSON(getQuestion(), "2");
				mConnection.publish(topic, json.toString());
			}
		});
		ad.show();
	}


	/* **************************************************************************************************** */	
	//checks if the device is connected to the internet
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/* **************************************************************************************************** */
	protected IntentFilter createIntentFilter() {
		//register broadcast receiver to receive SocietiesEvents return values 
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LOCATION_CHANGE);
		intentFilter.addAction(TOPIC_DATA);

		return intentFilter;
	}

	/* **************************************************************************************************** */
	class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.i(TAG, "Received action: " + intent.getAction());

			if(intent.getAction().equalsIgnoreCase(LOCATION_CHANGE)){

				setSymbolicLocation(intent.getStringExtra("symloc"));

				locationTextView.setText("Location: " + getSymbolicLocation());
				if(isConnectedWamp()){
					chooseClass(getSymbolicLocation());
				}else{
					alert("Not connected to Question Server");
				}
			}
			else if(intent.getAction().equalsIgnoreCase(TOPIC_DATA)){
				Bundle b= intent.getBundleExtra("topicList");
				ArrayList<Topic> tempTopicList = b.getParcelableArrayList("topicList");

				topicList = new ArrayList<Topic>(tempTopicList);

				Log.d(TAG, "tempTopicList size: " + tempTopicList.size() + "TopicList size: " + topicList.size());	

				for(Topic topic: topicList){
					locationToURI.put(topic.getLocationID(), topic.getTopicURI());
					locationToTopicID.put(topic.getLocationID(),topic.getTopicID());
				}
			}

		}
	}

	/* **************************************************************************************************** */
	public String getCssId() {
		Cursor cursor = getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, null, null, null, null);
		cursor.moveToFirst();
		String cssId = cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY));
		Log.d(TAG, "CSSId: " + cssId);
		
		int end = cssId.indexOf('.');
		String username = cssId.substring(0, end);
		return username;
		//return cssId;
	}
	/* **************************************************************************************************** */	
	private void teardownBroadcastReceiver() {
		Log.d(TAG, "Tear down broadcast receiver");
		try {
			unregisterReceiver(this.receiver);
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* **************************************************************************************************** */
	public void chooseClass(String location){

		for(String topicLoc : locationToURI.keySet()){	

			if(location.equals(topicLoc)){
				this.setLocationAvailable(true);
				Log.d(TAG, "location: " + location + " --> GetKey location: " + topicLoc);
				Log.d(TAG, "URI: " + locationToURI.get(topicLoc));
				subscribe(locationToURI.get(topicLoc));

				for(Topic topic: topicList){
					if(topic.getTopicURI().equals(locationToURI.get(topicLoc))){
						this.alert("Module: " + topic.getTopicName());
						moduleTextView.setText("Module: " + topic.getTopicName());
						this.setChooseModule(true);
						this.enableScreenComponents(true);  
						break;
					}	
				}
				break;
			}else{
				this.setLocationAvailable(false);
			}
		}

		if(location.equals(null)){
			Log.d(TAG, "location is null");
			this.alert("Location is not available");
			moduleTextView.setText("Module: Not available");
			this.setChooseModule(false);
			this.enableScreenComponents(false);
		}else if(!isLocationAvailable()){
			Log.d(TAG, "No module available for this location");
			this.alert("No module available for location " + location);
			moduleTextView.setText("Module: Not available");
			this.setChooseModule(false);
			this.enableScreenComponents(false);
		}
	}

	/* **************************************************************************************************** */
	public void enableScreenComponents(boolean arg){
		publishBtn.setEnabled(arg);
		questionEditText.setEnabled(arg);
		Log.d(TAG, "Enable Screen Components " + arg);
	}

	/* **************************************************************************************************** */


	private void alert(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	public void setTopic(String module){
		this.topic = module;
	}

	public String getTopic(){
		return this.topic;
	}

	public void setTopicPosition(int pos){
		this.position=pos;
	}

	public int getTopicPosition(){
		return this.position;
	}

	public void setQuestion(){
		this.question = questionEditText.getText().toString();
	}

	public String getQuestion(){
		return this.question;
	}

	public String getUserId(){
		HashMap<String, String> user = session.getUserDetails();
		return user.get(SessionManager.KEY_USER_ID);
	}

	public void setChooseModule(boolean cm){
		this.chooseModule = cm;
	}

	public boolean getChooseModule(){
		return this.chooseModule;
	}
	
	/**
	 * @return the smartQuestions
	 */
	public boolean isSmartQuestions() {
		return smartQuestions;
	}


	/**
	 * @param smartQuestions the smartQuestions to set
	 */
	public void setSmartQuestions(boolean smartQuestions) {
		this.smartQuestions = smartQuestions;
	}


	/**
	 * @return the isConnectedWamp
	 */
	public boolean isConnectedWamp() {
		Log.d(TAG, "Get isConnectedWamp: " + connectedWamp);
		return connectedWamp;
	}


	/**
	 * @param isConnectedWamp the isConnectedWamp to set
	 */
	public void setConnectedWamp(boolean isConnectedWamp) {
		Log.d(TAG, "Set isConnectedWamp: " + isConnectedWamp);
		this.connectedWamp = isConnectedWamp;
	}


	/**
	 * @return the symbolicLocation
	 */
	public String getSymbolicLocation() {
		return symbolicLocation;
	}


	/**
	 * @param symbolicLocation the symbolicLocation to set
	 */
	public void setSymbolicLocation(String symbolicLocation) {
		this.symbolicLocation = symbolicLocation;
	}

	/**
	 * @return the locationAvailable
	 */
	public boolean isLocationAvailable() {
		return locationAvailable;
	}

	/**
	 * @param locationAvailable the locationAvailable to set
	 */
	public void setLocationAvailable(boolean locationAvailable) {
		this.locationAvailable = locationAvailable;
	}
}
