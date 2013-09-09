package im143.af_s;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class RepPointsActivity extends ListActivity {
	
	// Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> inboxList;
 
    // products JSONArray
    JSONArray rep_points = null;
    

    private static final String REP_POINTS_URL = "http://54.218.113.176/askfree/getRepPoints.php";
    
    // ALL JSON node names
    private static final String TAG_RES = "res";
    private static final String TAG_FNAME = "fname";
    private static final String TAG_LNAME = "lname";
    private static final String TAG_REP_POINTS = "rep_points";
    
    private static final int MENU_ITEM_REFRESH = Menu.FIRST;

     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rep_points);
		
		// Hashmap for ListView
        inboxList = new ArrayList<HashMap<String, String>>();
  
        // Loading INBOX in Background Thread
        new getRepPoints().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		//Group ID
		int groupId=0;
		//Unique Menu Item identifier. Used for event handling
		int menuItemId = MENU_ITEM_REFRESH;
		//The order position of the item
		int menuItemOrder = Menu.NONE;
		//Text to be displayed for this menu item
		int menuItemText = R.string.menu_item_refresh;
		
		
		//Create the Menu Item and keep a reference to it
		MenuItem menuItem = menu.add(groupId,menuItemId,menuItemOrder,menuItemText);
		
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
			
		//Find which menu item has been selected
		switch(item.getItemId()){
		//check for each known Menu Item
		case(MENU_ITEM_REFRESH):
		//perform menu handler actions
			new getRepPoints().execute();
			
			return true;
		//Return false if you have not handled the Menu Item
		default: return false;
		}
		
		
	}
	
	class getRepPoints extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RepPointsActivity.this);
            pDialog.setMessage("Loading Reputation Points List ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            inboxList.clear();
        }
 
        /**
         * getting RP JSON
         * */
        protected String doInBackground(String... args) {
             
            // getting JSON string from URL
            JSONObject json = jsonParser.getJSONObject(REP_POINTS_URL);
 
            // Check your log cat for JSON reponse
            Log.d("Rep Points JSON: ", json.toString());
 
            try {
                rep_points = json.getJSONArray(TAG_RES);
                // looping through All messages
                for (int i = 0; i < rep_points.length(); i++) {
                    JSONObject c = rep_points.getJSONObject(i);
 
                    // Storing each json item in variable
                    String fname = c.getString(TAG_FNAME);
                    String lname = c.getString(TAG_LNAME);
                    String rpoints = c.getString(TAG_REP_POINTS);
 
                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();
 
                    // adding each child node to HashMap key => value
                    map.put(TAG_FNAME, fname);
                    map.put(TAG_LNAME, lname);
                    map.put(TAG_REP_POINTS, rpoints);
                    
                    
                    // adding HashList to ArrayList
                    inboxList.add(map);
                }
 
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        
         //After completing background task Dismiss the progress dialog
         
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    
                     //Updating parsed JSON data into ListView
                	
                    ListAdapter adapter = new SimpleAdapter(
                            RepPointsActivity.this, inboxList,
                            R.layout.rp_list_item, new String[] { TAG_FNAME, TAG_LNAME, TAG_REP_POINTS },
                            new int[] { R.id.fname, R.id.lname, R.id.points });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }

	
}
