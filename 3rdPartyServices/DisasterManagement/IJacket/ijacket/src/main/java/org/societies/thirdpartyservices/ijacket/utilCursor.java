package org.societies.thirdpartyservices.ijacket;

import org.societies.android.api.cis.SocialContract;
import org.societies.android.api.cis.SupportedAccountTypes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class utilCursor {
	
	private static final String LOG_TAG = utilCursor.class.getName();

	//Context mContext;
	
	// return cursor from table SocialContract.UriPathIndex.SHARING
	// pointing to the communities where the service is shared
	public  static Cursor getCommunitiesWhereIamShared(String serviceId,ContentResolver cr){
	    Uri sharing_uri = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.SHARING);
	  	String mSelectionClause = SocialContract.Sharing._ID_SERVICE + " = ?";
	  	String[] mSelectionArgs = {serviceId+ ""};
	  	Cursor cursorOfSharedCommunities = cr.query(sharing_uri,null,mSelectionClause,mSelectionArgs,null);
	  	return cursorOfSharedCommunities;
	}
	
	
	// filter on the communities in which I am a member of and which the passed service is shared
	// return cursor to them on the sharing table SocialContract.UriPathIndex.SHARING
	public  static Cursor getMyCommunitiesWhereServiceShared(String serviceId,ContentResolver cr, long myID){
	  
		
		
		// first we check which communities I am a member from
    	String mSelectionClause = SocialContract.Membership._ID_MEMBER + " = ?";
    	String [] selectArgInMembershipQuery = new String [1]; 
    	selectArgInMembershipQuery[0] = myID + "";
    	Uri MEMBERSHIP_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.MEMBERSHIP);
        Cursor cur = cr.query(MEMBERSHIP_URI,null,mSelectionClause,selectArgInMembershipQuery,null);
   		
        if(null != cur && cur.getCount()>0){
    		Log.d(LOG_TAG, "going to query how many of " + cur.getCount() + " communities the service is shared");

    		String [] communities = new String[cur.getCount()]; // holds all the communities in which the service is shared
    		int idIndex = cur.getColumnIndex(SocialContract.Membership._ID_COMMUNITY);
    		for(int i= 0; cur.moveToNext(); i++)
    			communities[i] = cur.getLong(idIndex) + "";
	        
    		// now I query to see of those communities, those which the service is shared in
    		mSelectionClause = SocialContract.Sharing._ID_SERVICE + " = ? and " + SocialContract.Sharing._ID_COMMUNITY + " IN (";
        	for(int i=0;i<communities.length; i++) mSelectionClause+="?,";
        		mSelectionClause = mSelectionClause.substring(0, mSelectionClause.length() -1) + ")";
        	Log.d(LOG_TAG, "in clause is " + mSelectionClause);
        	String [] selectArgs = new String [communities.length+1]; 
        	selectArgs[0] = serviceId +""; //  SocialContract.Sharing._ID_SERVICE
        	System.arraycopy(communities, 0, selectArgs, 1, communities.length);
        	// done building in clause
        	Uri uri = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.SHARING);
        	Cursor finalCursor = cr.query(uri,null,mSelectionClause,selectArgs,null);
   		
        	return finalCursor;
        	
        	
        }else{
        	return null;
        }


		
	}
	
	// take as input, the row which has changed and give the Cursor (from the COMMUNITY_ACTIVITIY table)  to it if it matches
	// the jid on the shared prefereced
	public  static Cursor getActivityCursorFromRowIfMatchesCISonSharedPref(long row, ContentResolver cr, Context ctx){
		SharedPreferences mypref = ctx.getSharedPreferences(IJacketApp.PREF_FILE_NAME, android.content.Context.MODE_PRIVATE );
		long jid = mypref.getLong(IJacketApp.CIS_JID_PREFERENCE_TAG, -1);
		if(jid == -1){
			Log.d("LOG_TAG", "no community on shared pref..." );
			return null;
		}
	 
	 
		 String mSelectionClause = SocialContract.CommunityActivity._ID + " = ? and " + SocialContract.CommunityActivity._ID_FEED_OWNER + " = ? and " + SocialContract.CommunityActivity.TARGET + " = ?" ;
		 String[] mSelectionArgs = {Long.toString(row),jid +"",org.societies.thirdpartyservices.ijacketlib.IJacketDefines.AccountData.IJACKET_SERVICE_NAME};
		 Uri otherUri =  Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY);
		 Cursor cursor = cr.query(otherUri,null,mSelectionClause,mSelectionArgs,null);
		 return cursor;
	}
	
	// take as input, the row which has changed and give the Cursor (from the COMMUNITY_ACTIVITIY table) 
	// to it if it matches the jid of one of the communities where the service is shared
	public  static Cursor getActivityCursorFromRowIfMatchesMyCIS(String serviceId, long row, ContentResolver cr){
		Cursor cursorOfSharedCommunities = getCommunitiesWhereIamShared(serviceId, cr);
		
       	if(null != cursorOfSharedCommunities && cursorOfSharedCommunities.getCount()>0){
    		
    		Log.d(LOG_TAG, "going to query how many of " + cursorOfSharedCommunities.getCount() + " communities Im member of");

    		String [] sharedCommunities = new String[cursorOfSharedCommunities.getCount()]; // holds all the communities in which the service is shared
    		int idIndex = cursorOfSharedCommunities.getColumnIndex(SocialContract.Sharing._ID_COMMUNITY);
    		for(int i= 0; cursorOfSharedCommunities.moveToNext(); i++)
    			sharedCommunities[i] = cursorOfSharedCommunities.getLong(idIndex) + "";
	        
    		// now I query to see of those communities, one of them own the feed
    		String mSelectionClause = SocialContract.CommunityActivity._ID + " = ? and " + SocialContract.CommunityActivity.TARGET + " = ? and " + SocialContract.CommunityActivity._ID_FEED_OWNER + " IN (";
        	for(int i=0;i<sharedCommunities.length; i++) mSelectionClause+="?,";
        		mSelectionClause = mSelectionClause.substring(0, mSelectionClause.length() -1) + ")";
        	
        	
        	Log.d(LOG_TAG, "in clause is " + mSelectionClause);
        	String [] selectArgInMembershipQuery = new String [sharedCommunities.length+2]; 
        	selectArgInMembershipQuery[0] = Long.toString(row); //  SocialContract.CommunityActivity._ID
        	selectArgInMembershipQuery[1] = org.societies.thirdpartyservices.ijacketlib.IJacketDefines.AccountData.IJACKET_SERVICE_NAME; //  SocialContract.CommunityActivity.TARGET
        	System.arraycopy(sharedCommunities, 0, selectArgInMembershipQuery, 2, sharedCommunities.length);
        	// done building in clause
        	Uri MEMBERSHIP_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY);
        	Cursor cur = cr.query(MEMBERSHIP_URI,null,mSelectionClause,selectArgInMembershipQuery,null);
   		
        	return cur;
       	}
       	else{
       		return null;
       	}
		
	}

	
    public static long retrieveUserID( ContentResolver cr){
    	long my_cssID = -1;
    	Log.d(LOG_TAG, "going to retrieve CSSID");
    	Uri CSS_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.ME);
        try{
        	//String[] mProjection ={SocialContract.Me.GLOBAL_ID};
        	String mSelectionClause = SocialContract.Me.ACCOUNT_TYPE + " = ?";
        	String[] mSelectionArgs = new String[1];
        	if(IJacketApp.accountTypeSync.equalsIgnoreCase("p2p"))
        		mSelectionArgs[0] = "p2p";
        	else
        		mSelectionArgs[0] = SupportedAccountTypes.COM_BOX;
	       	
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
	   		    	String user_name = cursor.getString(i);
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
        return my_cssID;
    }    
	
		
}
