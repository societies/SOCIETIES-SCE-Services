package org.societies.thirdpartyservices.ijacketclient;

import java.util.ArrayList;
import java.util.Random;

import org.societies.android.api.cis.SocialContract;
import org.societies.android.api.cis.SupportedAccountTypes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class IJacketClient extends Activity implements OnItemSelectedListener {

    private static String LOG_TAG = "ijacketCLient";
    //ContentResolver cr = this.getApplication().getContentResolver();
    String communityName = "";
    long communityLocalID = 0;
    String user_name = "defaultUser";
    
    long iJacketServiceId = 0;
    long defaultCISID = 0;
    long my_cssID = 0; 
    long CSS_sharing_jacket = 0;
    
    String accountNameSync = "p2p";
    String accountTypeSync = "p2p";
    
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate IjacketClient");
		

		
		
       Intent intent = getIntent();
        long cisJid = intent.getLongExtra(org.societies.thirdpartyservices.ijacketlib.IJacketDefines.IjacketIntentExtras.CIS_ID, 0);// TODO: set the intent extra
        if(0 != cisJid){
        	// TODO: add as the defaul
        	defaultCISID = cisJid;

        }
        long cssJid = intent.getLongExtra(org.societies.thirdpartyservices.ijacketlib.IJacketDefines.IjacketIntentExtras.CSS_ID, 0);// TODO: set the intent extra
        if(0 != cssJid){
        	// TODO: add as the defaul
        	CSS_sharing_jacket = cssJid;

        } 
        //fillUpAccNameAndType();
        

    	Log.d(LOG_TAG, "going to retrieve service and CSS id");
		retrieveCSSID();
		retrieveIJacketServiceID();
		
		setContentView(R.layout.main);
		Log.d(LOG_TAG, "items added on Spinnet");
		addItemsOnCISSpinner();
		populateVerbSpinner();
		
		
		 
		// should add ITEM on CIS spinner be after?
		

		
		
       
    }
        
    
    public void fillUpAccNameAndType(){
    	ContentResolver cr = this.getApplication().getContentResolver();
		String	mSelectionClause = SocialContract.Me.ACCOUNT_TYPE + " = ?";
		
	    
		Log.d(LOG_TAG, "accountType sync is " + accountTypeSync);
	    
		String[]  mSelectionArgs = {accountTypeSync};
		
    	Cursor c = cr.query(SocialContract.Me.CONTENT_URI,null,mSelectionClause,mSelectionArgs, null);
       	if (null == c || c.getCount() < 1){
       		Log.d(LOG_TAG, "could not my account");
       		
       	}
       	else{
       		if (c.getCount() > 1){ 
       			Log.d(LOG_TAG, "too many box accounts");
       		}else{
       			int i  = c.getColumnIndex(SocialContract.Me.USER_NAME);
   		    	c.moveToNext();
   		    	String user = c.getString(i);
   		    	Log.d(LOG_TAG, "user is " + user);
   		    	if(accountTypeSync.equalsIgnoreCase("p2p") == false)
   		    		accountNameSync = user; 
       		}
       	}

    	
    }
    
    
    /** Called when the user touches the button */
    public void sendMessage(View view) {
    	ContentResolver cr = this.getApplication().getContentResolver();
		
		Log.d(LOG_TAG, "send button pressed");
		
        Uri FEED_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY);
        try{
        	ContentValues mNewValues = new ContentValues();
        	EditText editText = (EditText) findViewById(R.id.edit_message);
        	
        	/*
        	 * Sets the values of each column and inserts the word. The arguments to the "put"
        	 * method are "column name" and "value"
        	 */
        	//mNewValues.put(SocialContract.CommunityActivity.GLOBAL_ID, randomStr());
        	
        	Spinner spinnerVerbs = (Spinner) findViewById( R.id.spinner2 );
        	String verb = spinnerVerbs.getSelectedItem().toString();
        	mNewValues.put(SocialContract.CommunityActivity.ACTOR, user_name);
        	mNewValues.put(SocialContract.CommunityActivity.ACCOUNT_TYPE, accountTypeSync);
        	mNewValues.put(SocialContract.CommunityActivity.ACCOUNT_NAME, accountNameSync);
        	mNewValues.put(SocialContract.CommunityActivity.OBJECT, editText.getText().toString());
        	mNewValues.put(SocialContract.CommunityActivity.VERB, verb);
        	mNewValues.put(SocialContract.CommunityActivity.DIRTY, 1);
        	mNewValues.put(SocialContract.CommunityActivity.GLOBAL_ID,SocialContract.GLOBAL_ID_PENDING);
        	mNewValues.put(SocialContract.CommunityActivity.TARGET, org.societies.thirdpartyservices.ijacketlib.IJacketDefines.AccountData.IJACKET_SERVICE_NAME);
        	mNewValues.put(SocialContract.CommunityActivity._ID_FEED_OWNER, communityLocalID);
        	Log.d(LOG_TAG, "going to inseet: " + user_name + " posted " + editText.getText().toString() + " at " +communityName);
        
        	Uri mNewUri = cr.insert(FEED_URI,mNewValues);
        	
        	if(null != mNewUri){
	        	this.runOnUiThread(new Runnable() {
	                public void run() {
	                    Toast.makeText(IJacketClient.this, "activity published", Toast.LENGTH_SHORT).show();
	                }
	            });
        	}
        	
        }catch (Exception e) {
	   		// TODO Auto-generated catch block
	   		Log.d(LOG_TAG, "exception in the insert");
	   		e.printStackTrace();
	   	}

    }
        
    private void retrieveCSSID(){
    	ContentResolver cr = this.getApplication().getContentResolver();
    	Log.d(LOG_TAG, "going to retrieve CSSID");
    	Uri CSS_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.ME);
        try{
        	//String[] mProjection ={SocialContract.Me.GLOBAL_ID};
        	String mSelectionClause = SocialContract.Me.ACCOUNT_TYPE + " = ?";
        	String[] mSelectionArgs = {SupportedAccountTypes.COM_BOX};
        	cr = this.getApplication().getContentResolver();
	       	 Cursor cursor = cr.query(CSS_URI,null,mSelectionClause,mSelectionArgs,null);
	       	if (null == cursor || cursor.getCount() < 1){
	       		Log.d(LOG_TAG, "could not find the CSS id");
	       		
	       	}
	       	else{
	       		if (cursor.getCount() > 1){ 
	       			Log.d(LOG_TAG, "too many CSS id");
	       		}else{
	       			int i  = cursor.getColumnIndex(SocialContract.Me.USER_NAME);
	   		    	cursor.moveToNext();
	   		    	user_name = cursor.getString(i);
	   		    	Log.d(LOG_TAG, "user is " + user_name);
	   		    	i  = cursor.getColumnIndex(SocialContract.Me._ID_PEOPLE);
	   		    	my_cssID = cursor.getLong(i); 
	   		    	Log.d(LOG_TAG, "my CSS ID " + my_cssID);
	       		}
	       	}
    	}catch (Exception e) {
	   		// TODO Auto-generated catch block
	   		Log.d(LOG_TAG, "exception in the retrieveCSSID");
	   		e.printStackTrace();
	   	}
    
    }    

    private void retrieveIJacketServiceID(){
    	ContentResolver cr = this.getApplication().getContentResolver();
    	Log.d(LOG_TAG, "going to retrieve Ijacket service ID");
    	Uri CSS_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.SERVICES);
        try{
        	//String[] mProjection ={SocialContract.Me.GLOBAL_ID};
        	String mSelectionClause = SocialContract.Services.GLOBAL_ID + " = ?";
        	String[] mSelectionArgs = {org.societies.thirdpartyservices.ijacketlib.IJacketDefines.AccountData.IJACKET_SERVICE_NAME};
        	cr = this.getApplication().getContentResolver();
	       	 Cursor cursor = cr.query(CSS_URI,null,mSelectionClause,mSelectionArgs,null);
	       	if (null == cursor || cursor.getCount() < 1){
	       		Log.d(LOG_TAG, "could not find the service id");
	       		return;
	       	}
	       	else{
       			int i  = cursor.getColumnIndex(SocialContract.Services._ID);
       			cursor.moveToNext();
       			iJacketServiceId = cursor.getLong(i);
   		    	Log.d(LOG_TAG, "serviceID is " + iJacketServiceId);
	       	}
    	}catch (Exception e) {
	   		// TODO Auto-generated catch block
	   		Log.d(LOG_TAG, "exception in the serviceID");
	   		e.printStackTrace();
	   		return ;
	   	}
        return ;
    
    }    

    
    private int getPostiton(long dbId, Cursor c){
        int i;
        c.moveToFirst(); 
        for(i=0;i< c.getCount()-1;i++)  {  
            c.moveToNext();  
            long currId = c.getLong(c.getColumnIndex(SocialContract.Communities._ID));  
            if(currId == dbId)
                return (i+1);  
        }
        return 0;
    }
    
    
    private void populateVerbSpinner(){
    	Spinner spinnerVerbs = (Spinner) findViewById( R.id.spinner2 );
    	if(null == spinnerVerbs) Log.d(LOG_TAG, "verb spinner is null...");
    	ArrayList<String> spinnerArray = new ArrayList<String>();
    	spinnerArray.add( org.societies.thirdpartyservices.ijacketlib.IJacketDefines.Verbs.DISPLAY);
    	spinnerArray.add(org.societies.thirdpartyservices.ijacketlib.IJacketDefines.Verbs.RING);
    	spinnerArray.add(org.societies.thirdpartyservices.ijacketlib.IJacketDefines.Verbs.VIBRATE);
    	// Create an ArrayAdapter using the string array and a default spinner layout
    	ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_dropdown_item, spinnerArray);
    	spinnerVerbs.setAdapter(adapter);
    	return;
    }
    
        
    private void addItemsOnCISSpinner(){
    	ContentResolver cr = this.getApplication().getContentResolver();
        	Spinner spinnerCIS = (Spinner) findViewById( R.id.spinner1 );
        	if(null == spinnerCIS) Log.d(LOG_TAG, "spinner is null...");
    		
    	        Log.d(LOG_TAG, "going to query the social provider");
    	       
    	        cr = this.getApplication().getContentResolver();
    	        
    	        Uri COMUNITIES_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.SHARING);
    	        String mSelectionClause ;
    	        String[] mSelectionArgs;
    	        
    	        // get communities where I Ijacket is shared and by the user which came in the intent
    	        if(0 != CSS_sharing_jacket){
    	        	mSelectionArgs = new String[3];
    	        	mSelectionArgs[0] = this.iJacketServiceId + "";
    	        	mSelectionArgs[1] = SocialContract.ServiceConstants.SERVICE_SHARED;
    	        	// in case the id of the user who is sharing came in the intent
    	        	mSelectionClause = SocialContract.Sharing._ID_SERVICE + " = ? AND " + SocialContract.Sharing.TYPE + " = ? AND " + SocialContract.Sharing._ID_OWNER + " = ?";
    	        	mSelectionArgs[2] = this.CSS_sharing_jacket + "";
    	        }else{
    	        	mSelectionArgs = new String[2];
    	        	mSelectionArgs[0] = this.iJacketServiceId + "";
    	        	mSelectionArgs[1] = SocialContract.ServiceConstants.SERVICE_SHARED;
    	        	mSelectionClause = SocialContract.Sharing._ID_SERVICE + " = ? AND " + SocialContract.Sharing.TYPE + " = ?";
    	        }
    	        Log.d(LOG_TAG, "query is " + mSelectionClause + " and arg 0 is " + mSelectionArgs[0]);
    	        if(mSelectionArgs.length>1) Log.d(LOG_TAG, " and arg1 is " + mSelectionArgs[1]);
            	
    	        // TEMP
    	        Cursor cur = cr.query(COMUNITIES_URI,null,mSelectionClause,mSelectionArgs,null);
    	        int idIndex;
 /*TEMP           	Cursor cursorOfSharedCommunities = cr.query(COMUNITIES_URI,null,mSelectionClause,mSelectionArgs,null);
    	        
            	if(null != cursorOfSharedCommunities && cursorOfSharedCommunities.getCount()>0){
            		
            		Log.d(LOG_TAG, "going to query how many of " + cursorOfSharedCommunities.getCount() + " communities Im member of");

            		String [] sharedCommunities = new String[cursorOfSharedCommunities.getCount()]; // holds all the communities in which the service is shared
            		int idIndex = cursorOfSharedCommunities.getColumnIndex(SocialContract.Sharing._ID_COMMUNITY);
            		for(int i= 0; cursorOfSharedCommunities.moveToNext(); i++)
            			sharedCommunities[i] = cursorOfSharedCommunities.getLong(idIndex) + "";
        	        
            		// now I query to see of those communities, which ones Im a member off
    	        	mSelectionClause = SocialContract.Membership._ID_MEMBER + " = ? AND " + SocialContract.Membership._ID_COMMUNITY + " IN (";
    	        	for(int i=0;i<sharedCommunities.length; i++) mSelectionClause+="?,";
    	        	mSelectionClause = mSelectionClause.substring(0, mSelectionClause.length() -1) + ")";
    	        	Log.d(LOG_TAG, "in clause is " + mSelectionClause);
    	        	String [] selectArgInMembershipQuery = new String [sharedCommunities.length+1]; 
    	        	selectArgInMembershipQuery[0] = this.my_cssID + "";
    	        	System.arraycopy(sharedCommunities, 0, selectArgInMembershipQuery, 1, sharedCommunities.length);
    	        	// done building in clause
    	        	Uri MEMBERSHIP_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.MEMBERSHIP);
    	        	Cursor cur = cr.query(MEMBERSHIP_URI,null,mSelectionClause,selectArgInMembershipQuery,null);
*/            		

                	// Ill add the id of all those communities now on a selection args in order to get
                	// theirs name
    	        	if(null != cur && cur.getCount()>0){
    	        	
	            		Log.d(LOG_TAG, "going to add " + cur.getCount() + " communities in the spinner");
	            		// add to spinner
	            		try{
	        	        	//building in clause
	                		String [] selectCommunities = new String[cur.getCount()]; // holds all the communities in which the service is shared
	                		idIndex = cur.getColumnIndex(SocialContract.Membership._ID_COMMUNITY);
	                		for(int i= 0; cur.moveToNext(); i++)
	                			selectCommunities[i] = cur.getLong(idIndex) + "";

	        	        	mSelectionClause = SocialContract.Communities._ID + " IN (";
	        	        	for(int i=0;i<selectCommunities.length; i++) mSelectionClause+="?,";
	        	        	mSelectionClause = mSelectionClause.substring(0, mSelectionClause.length() -1) + ")";
	        	        	Log.d(LOG_TAG, "in clause is " + mSelectionClause);
	        	        	// done building in clause
	        	        	 COMUNITIES_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMUNITIES);
	        	        	Cursor cursor = cr.query(COMUNITIES_URI,null,mSelectionClause,selectCommunities,null);
	    	    	       	startManagingCursor(cursor);
	
	    	    	       	String[] columns = new String[] {SocialContract.Communities.NAME}; // field to display
	    	    	       	int to[] = new int[] {android.R.id.text1}; // display item to bind the data
	    	    	       	SimpleCursorAdapter ad =  new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,cursor ,columns ,to);
	    	    	        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	    	       	spinnerCIS.setAdapter(ad);
	    	    	       	
	    	    	        spinnerCIS.setOnItemSelectedListener(this);
	    	    	        Log.d(LOG_TAG, "spiner filled up");
	    	    	   		
	    	    	        // setting default position of spinner
	    	    	        if(0 != defaultCISID){
	    	    	        	int defaultCISidInSpinner = getPostiton(defaultCISID,ad.getCursor()); 
	    	    	        	if(0 != defaultCISidInSpinner )
	    	    	        		spinnerCIS.setSelection(defaultCISidInSpinner);
	    	    	        	else
	    	    	        		Log.d(LOG_TAG, "defaultCISIS is not on spinner!");
	    	    	        }
	    	    	        else{
	    	    	        	Log.d(LOG_TAG, "no default CISID");
	    	    	        }
	    	    	        
	    	    	   		//if(null != cursor) cursor.close();
	        		   	}catch (Exception e) {
	        		   		// TODO Auto-generated catch block
	        		   		Log.d(LOG_TAG, "exception in the create");
	        		   		e.printStackTrace();
	        		   	}
	            		 /*TEMP     	        	}
  	        	else{
                		Log.d(LOG_TAG, "IJacket is shared in some communities, but in none that Im part of");
                		//show popup
                		Context context = getApplicationContext();
                		CharSequence text = "IJacket is shared in some communities, but in none that Im part of";
                		int duration = Toast.LENGTH_SHORT;
                		Toast toast = Toast.makeText(context, text, duration);
                		toast.show();
    	        		
    	        	}*/
            	}
            	else{
            		Log.d(LOG_TAG, "IJacket is not shared in any community");
            		//show popup
            		Context context = getApplicationContext();
            		CharSequence text = "Ijacket app is not shared in any community, please make sure to share it before using!";
            		int duration = Toast.LENGTH_SHORT;
            		Toast toast = Toast.makeText(context, text, duration);
            		toast.show();
            	}
            		

    	        

    	        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerArray);
    	        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


    }
        
        

    	@Override
    	public void onItemSelected(AdapterView<?> parent, View view, int pos,
    			long id) {
    		
    		Cursor cursor = (Cursor) parent.getItemAtPosition(pos);
    		int i = cursor.getColumnIndex(SocialContract.Communities.NAME); 
    		communityName = cursor.getString(i);
    		i = cursor.getColumnIndex(SocialContract.Communities._ID); 
    		communityLocalID = cursor.getLong(i);
    	    Log.d(LOG_TAG, "found community with name " + communityName + " and id " + communityLocalID);
    	    
    		
    	}



    	@Override
    	public void onNothingSelected(AdapterView<?> arg0) {
    		// TODO Auto-generated method stub
    		
    	}

}

