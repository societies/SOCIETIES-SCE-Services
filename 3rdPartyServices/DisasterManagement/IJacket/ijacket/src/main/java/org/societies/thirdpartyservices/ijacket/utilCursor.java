package org.societies.thirdpartyservices.ijacket;

import org.societies.android.api.cis.SocialContract;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class utilCursor {

	Context mContext;
	
	public  static Cursor getCommunitiesWhereIamShared(String serviceId,ContentResolver cr){
	    Uri sharing_uri = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.SHARING);
	  	String mSelectionClause = SocialContract.Sharing._ID_SERVICE + " = ?";
	  	String[] mSelectionArgs = {serviceId+ ""};
	  	Cursor cursorOfSharedCommunities = cr.query(sharing_uri,null,mSelectionClause,mSelectionArgs,null);
	  	return cursorOfSharedCommunities;
	}
	
}
