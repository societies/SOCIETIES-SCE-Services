package im143.af_s;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
SharedPreferences preferences;
	
	Editor editor;
	
	Context context;
	
	private static final String PREF_NAME = "SessionInf";
	

    private static final String IS_LOGIN = "IsLoggedIn";
     

    public static final String KEY_USER_ID = "user_id";
     
   
    public static final String KEY_PASSWORD = "password";
     

    public SessionManager(Context context){
        this.context = context;
        //create a shared preference
        preferences = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        
        //Modify a shared preference
        editor = preferences.edit();
    }
    
    //create a session
    public void createLoginSession(String user_id, String password){   	
    	
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
         
        // Storing username in preference
        editor.putString(KEY_USER_ID, user_id);
         
        // Storing password in preference
        editor.putString(KEY_PASSWORD, password);
         
        // save changes
        editor.apply();
    }
    
    //retrieve session data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
     
        user.put(KEY_USER_ID, preferences.getString(KEY_USER_ID, null));
         
        user.put(KEY_PASSWORD, preferences.getString(KEY_PASSWORD, null));
         
        return user;
    }
    
    //check if a session exists
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            context.startActivity(i);
        }        
    }
    
    //return login status
    public boolean isLoggedIn(){
        return preferences.getBoolean(IS_LOGIN, false);
    }
    
    //logout user
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        
        // After logout redirect user to Login Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         
        // Staring Login Activity
        context.startActivity(i);
    }
}
