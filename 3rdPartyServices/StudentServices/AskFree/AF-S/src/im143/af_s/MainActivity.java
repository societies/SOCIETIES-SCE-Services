package im143.af_s;


import java.util.HashMap;
import org.json.JSONObject;
import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.AutobahnConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener {
	
	//Button subscribeBtn;
	Button publishBtn;
	TextView status;
	TextView userId;
	EditText questionEditText;
	Spinner spinner;
	
	JSONParser jsonParser= new JSONParser();
	
	private static final String topic1 = "http://modules.com/mobile";
	private static final String topic1ID = "F21MC";
	private static final String topic2 = "http://modules.com/aid";
	private static final String topic2ID = "F21AD";
	private String topic;
	private int position;
	private String question;
	private boolean chooseModule;
	
	SessionManager session;

	protected static final String TAG = "AF";
	
	private static final int MENU_ITEM_LOGOUT = Menu.FIRST;
	private static final int MENU_ITEM_REFRESH = 2;
	
	private final AutobahnConnection mConnection = new AutobahnConnection();
	 
	private void WAMPConnection() {
	 
	   final String wsuri = "ws://54.218.113.176:8080";
	 
	   mConnection.connect(wsuri, new Autobahn.SessionHandler() {
		   
	      @Override
	      public void onOpen() {
	    	  Log.d(TAG, "Status: Connected to " + wsuri);
	    	  status.setText("Connected");
	    	  publishBtn.setEnabled(true);
	    	  spinner.setEnabled(true);
	      }
	 
	      @Override
	      public void onClose(int code, String reason) {
	    	  Log.d(TAG, "code"+code+", reason"+reason);
	    	  status.setText("Disconnected");
	    	  publishBtn.setEnabled(false);
	    	  spinner.setEnabled(false);
	      }
	   });
	}
	

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
		
		//subscribeBtn = (Button) findViewById(R.id.subscribeButton);
		publishBtn = (Button) findViewById(R.id.submitButton);		
		status = (TextView) findViewById(R.id.statusTextView);
		questionEditText = (EditText) findViewById(R.id.questionEditText);	
		spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setOnItemSelectedListener(this);
		userId = (TextView) findViewById(R.id.user_idTextView);
				
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.modules_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		// displaying user data
        userId.setText("User id: "+ this.getUserId());
		
		if(isOnline()){
			//connect to websocket server
			WAMPConnection();			
		}else{
			publishBtn.setEnabled(false);
			questionEditText.setEnabled(false);
			spinner.setEnabled(false);
			Toast.makeText(getApplicationContext(), "Please Connect to the Internet", Toast.LENGTH_LONG).show();
			status.setText("Status: No Internet Connection");
		}
		
		
		publishBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(!getChooseModule()){
            		Toast.makeText(getApplicationContext(), "Please choose a module", Toast.LENGTH_LONG).show();
            	}else{
            		setQuestion();
                	publish();
            	}
            }
        }); 
	}
	
	//spinner
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
		//set color of text at position 0
		//((TextView)parent.getChildAt(0)).setTextColor(Color.rgb(244, 122, 55));
		
		// On selecting a spinner item
        String module = parent.getItemAtPosition(pos).toString();
        int position = parent.getSelectedItemPosition(); 
        
        if (position == 0){
        	 Toast.makeText(parent.getContext(), "Choose a module",
                     Toast.LENGTH_LONG).show();
        	 this.setChooseModule(false);
        }else if(position == 1){ 
	        Toast.makeText(parent.getContext(), "You selected: " + module,
	                Toast.LENGTH_LONG).show();
	        
	        subscribe(topic1);
	        
	        this.setTopic(module);
	        this.setTopicPosition(position);
	        this.setChooseModule(true);
        }
        else if(position == 2){
        	Toast.makeText(parent.getContext(), "You selected: " + module,
	                Toast.LENGTH_LONG).show();
        	
        	subscribe(topic2);
	        
	        this.setTopic(module);
	        this.setTopicPosition(position);
	        this.setChooseModule(true);
        }
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
	
/* **************************************************************************************************** */
	
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
		int menuItemRefreshOrder = Menu.NONE;
		int menuItemRefreshText = R.string.menu_item_refresh;
		
		//Create the Menu Item and keep a reference to it
		MenuItem menuItem = menu.add(groupId,menuItemId,menuItemOrder,menuItemText);
		
		MenuItem menuItemRefresh = menu.add(groupId,menuItemRefreshId,menuItemRefreshOrder,menuItemRefreshText);
		
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		menuItemRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
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
				return true;
			}else{
				WAMPConnection();
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
			mConnection.subscribe(topic, MyEvent1.class, new Autobahn.EventHandler() {
				
				@Override
				public void onEvent(String topic, Object event) {

					MyEvent1 evt = (MyEvent1) event;
					
					Log.d(TAG, "Event received " + evt.toString());
					Log.d(TAG, "Event received.Question: " + evt.getQuestion());
					Log.d(TAG, "Event received.Question: " + evt.getQId());
					//alert("Event received : " + evt.toString());
					
					if(evt.getType()==1){
						alertDialog(evt.getQuestion(),evt.getQId(),topic);
					}else if(evt.getType() == 0){
						Toast.makeText(getApplicationContext(), "This question has no meanigfull context", Toast.LENGTH_LONG).show();
					}				
				}
			});
	}
	
	public void publish(){
		
		String s_id = this.getUserId();
		
        //submit question to db
		SubmitQuestionTask submitQuest = new SubmitQuestionTask(this);
		
		
		if(this.getTopicPosition() == 1){			
			JSONObject json = jsonParser.writeJSON(this.getQuestion(), "1");
			mConnection.publish(topic1, json.toString());
			submitQuest.execute(this.getQuestion(),s_id,topic1ID);
		}
		else if(this.getTopicPosition() == 2){
			JSONObject json = jsonParser.writeJSON(this.getQuestion(), "1");
			mConnection.publish(topic2, json.toString());
			submitQuest.execute(this.getQuestion(),s_id,topic2ID);
		}
		//clean question field
		questionEditText.setText("");
	}
	
	   
	public void alertDialog(String message, final int q_id,final String topic){
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
				JSONObject json = jsonParser.writeJSON(q_Id, "3");
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
}
