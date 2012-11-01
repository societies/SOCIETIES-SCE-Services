package org.societies.thirdpartyservices.ijacket;

import org.societies.android.api.cis.SocialContract;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;




public class MainActivityCursorLoader extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private static final String LOG_TAG = MainActivityCursorLoader.class.getName();
    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;
    Uri COMUNITIES_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMINITIES);

    
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "on activity created on the list fragment");
		setEmptyText("loading communities");
		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);
		
        // Start out with a progress indicator.
        setListShown(false);

       	String[] columns = new String[] {SocialContract.Communities.NAME}; // field to display
       	int to[] = new int[] {android.R.id.text1}; // display item to bind the data

		// Create an empty adapter we will use to display the loaded data.
		 mAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_spinner_item, null, columns ,to, 0);
		
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
        
        setListAdapter(mAdapter);
    }
    
    
    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.d("FragmentComplexList", "Item clicked: " + id);
        Cursor c = ((SimpleCursorAdapter)l.getAdapter()).getCursor();
        c.moveToPosition(position);
		int i = c.getColumnIndex(SocialContract.Communities.GLOBAL_ID); 
		String comJid = c.getString(i);
		i = c.getColumnIndex(SocialContract.Communities._ID); 
		String comLocalId = c.getString(i);
	    Log.d("LOG_TAG", "found community with JID " + comJid + " and id " + comLocalId);
	    
	    
        SharedPreferences mypref = getActivity().getSharedPreferences(IJacketApp.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = mypref.edit();
		e.putString(IJacketApp.CIS_JID_PREFERENCE_TAG, comJid);
		e.commit();

	        
        
    }
    

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       
         String[] mProjection = null;
    	 String mSelectionClause = null;
    	 String[] mSelectArgument = null;
    	 String mSelectionOrder = null;
    	 
    	 Log.d(LOG_TAG, "on create loader");
        
        return new CursorLoader(getActivity(), COMUNITIES_URI,
        		mProjection, mSelectionClause, mSelectArgument,
        		mSelectionOrder);
				 
	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        
		Log.d(LOG_TAG, "on load finished");
		// Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
            Log.d(LOG_TAG, "set list show");
        } else {
            setListShownNoAnimation(true);
            Log.d(LOG_TAG, "set list show no animation");
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
		
	}
	
}
