package com.modelsw.birdingviamic;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class WebList extends AppCompatActivity {
	private static final String TAG = "WebList";
    private WebAdapter adapter;
    public static Boolean[] chk;
	private static Context ctx;
    public static int existingWebId;
    public static int[] id;
    public static boolean isWebSiteSelected;
    private ListView list;
    private static int myRequest = 0;
    private static char q = 34;
	private String qry = "";
	private Cursor rs;  // I see cursor as RecordSet (rs)
    public static int selectedWeb; // index of the web site clicked on
    public static String[] webCombined;
    public static int webDbLen; // count of web sites
    public static String webLink; // the text for the web site
    public static EditText webText;
    Toolbar toolbar;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        isWebSiteSelected = false;
        setContentView(R.layout.weblist_header);
        // action bar toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setLogo(R.drawable.treble_clef_linen);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.teal));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Navigation Icon tapped");
                finish();
            }
        });

        Main.db = Main.songdata.getWritableDatabase();
		buildList();
        list = (ListView) findViewById(R.id.list);  
        adapter = new WebAdapter(this, webCombined);  // fileName on one line and Spec Inx Seg on second line
        list.setAdapter(adapter);
        list.setFastScrollEnabled(true);
        
		Button rename=(Button)findViewById(R.id.rename_button);
		rename.setOnClickListener(listener);
		Button delete=(Button)findViewById(R.id.delete_button);
		delete.setOnClickListener(listener);
		Button add=(Button)findViewById(R.id.add_button);
		add.setOnClickListener(listener);
		Button go=(Button)findViewById(R.id.go_button);
		go.setOnClickListener(listener);
		if (Main.webRenamed == true) {
			list.setSelection(Main.webOffset);
		}
		Main.webRenamed = false;
		existingWebId = 0;
    } // onCreate

    
    void buildList() {
   	    Log.d(TAG, "buildList read the BirdWebSites table");
   	    qry = "SELECT id, WebSiteText, WebSiteLink from BirdWebSites ORDER BY WebSiteText";
   		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
   		webDbLen = rs.getCount();
   		webCombined = new String[webDbLen];
   		chk = new Boolean[webDbLen];
   		id = new int[webDbLen];
   		rs.moveToFirst();
   		for (int i = 0; i<webDbLen; i++) {
   			id[i] = rs.getInt(0);
   			webCombined[i] = rs.getString(1) + "\n\t" + rs.getString(2); 
   			chk[i] = false;
			rs.moveToNext();
   		}
   		rs.close();
        Log.d(TAG, "loaded TheWebList len:" + webDbLen);    
    } // buildList

    
    public OnClickListener listener = new OnClickListener() {  // for non-list items -- i.e. buttons
      	public void onClick(View v) {
      	switch (v.getId()) {
        	case R.id.rename_button:
            	if (webLink == null || isWebSiteSelected == false ) {
            		String msg = "Please select a Web Site name to change.";
            		warningToast(msg);
            	} else {
                	Log.d(TAG, "rename button tapped selectedWeb:" + selectedWeb);
                	int nl = webLink.indexOf("\n\t");
                	Main.existingWebName = webCombined[selectedWeb].substring(0,nl);
                	Main.existingLink = webCombined[selectedWeb].substring(nl+2);
                	existingWebId = id[selectedWeb];
                	Main.newWebName = Main.existingWebName;
                	Main.newLink = Main.existingLink;
                	myRequest = 2;
                    Main.myRequest = myRequest;
                	getNewName();
            	}
    	    	break;
        	case R.id.delete_button:
        		Log.d(TAG, "delete button tapped");
            	if (webLink == null || isWebSiteSelected == false) {
            		warningToast("Please select a Web Site to Delete.");
            	} else {
            		deleteSelectedFile(); 
            	}
    	    	break;
        	case R.id.add_button:
            	Log.d(TAG, "add button tapped");
           	    qry = "SELECT MAX(id) FROM BirdWebSites";
           		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
           		rs.moveToFirst();
    			existingWebId = rs.getInt(0)+1;
      		    Main.existingWebName = "";
		    	Main.existingLink = "";
		    	Main.newWebName = null;
		    	Main.newLink = null;
            	myRequest = 1;
                Main.myRequest = myRequest;
		    	getNewName();
    	    	break;
        	case R.id.go_button:
            	Log.d(TAG, "go button tapped");
            	if (webLink == null || isWebSiteSelected == false) {
            		warningToast("Please select a Web Site before tapping Go");
            	} else {
            		Log.d(TAG, "webLink selected:" + webLink);
            		webLink = webLink.substring(webLink.indexOf("\n\t")+2);
            		Log.d(TAG, "Start WebBrowser webLink:" + webLink ); 
            		Main.showWebFromIdentify = true;
            		Main.isWebLink = true;
            		finish(); 
            	}
        		break;
       	} // switch
    } // onclick
    }; // onClickListener (because it does NOT extend OnClickListener)

    public void getNewName() {
  	    if (existingWebId == 0 && myRequest == 2) {
            Log.d(TAG, "*** 1a *** getNewName existing name is null -- returning");
    		Toast.makeText(this, "Please select a name to change.", Toast.LENGTH_LONG).show();
    		return;
    	}
    	if (myRequest == 1) {
    		Main.existingWebName = "";
    		Main.existingLink = "";
    	}
     	Log.d(TAG, "*** 1b *** getNewName: existingWebName:" + Main.existingWebName + " newWebName:" + Main.newWebName +
     		  " existingLink:" + Main.existingLink + " newLink:" + Main.newLink);
  		Intent nwd = new Intent(this, NewWebDialog.class);
  		startActivityForResult(nwd, myRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	 
        //         super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
		Log.d(TAG, "*** 6 *** onActivityResult requestCode:" + requestCode  ); 
        if (requestCode == 1) { // add
        	Log.d(TAG, "*** 7 *** onActivityResult resultCode:" + resultCode  );
            // Make sure the request was successful
            if (resultCode == 1) {
            	// The user picked a FileName.
            	if (!Main.newWebName.isEmpty()) { // if NOT blank add 
            		try {       	
            			Log.d(TAG, "db begin transaction");
            			Main.db.beginTransaction();
            			try {
            				ContentValues val = new ContentValues();
            				Log.d(TAG, "Add Web:" + Main.newWebName + " Link:" + Main.newLink);
            				val.put("id", existingWebId);
            				val.put("WebSiteText", Main.newWebName);
            				val.put("WebSiteLink", Main.newLink);
            				Main.db.insert("BirdWebSites", null, val);
            				Main.db.setTransactionSuccessful();
            			} finally {
            				Main.db.endTransaction();
            				Log.d(TAG, "db end transaction");
            			}
            		} catch( Exception e ) {
            			Log.e(TAG, "Database Exception: " + e.toString() );     	    
            		}
            	}  // if not empty
            } // result code 1
			Main.webRenamed = true;
			finish();
        }  // request code 1
        if (requestCode == 2) { // rename
        	if(resultCode == 1) {
        		Log.d(TAG, "rename clicked");
		        try {        	
  		          	Log.d(TAG, "db begin transaction");
  		            Main.db.beginTransaction();
  		            try {
  		            	qry = "UPDATE BirdWebSites" +
  		            		" SET WebSiteText = " + q + Main.newWebName + q +
  		            		", WebSiteLink = " + q + Main.newLink + q;
  		            	qry += " WHERE id = " + existingWebId; 
  		            	Log.d(TAG, "Rename qry:" + qry);
  		            	Main.db.execSQL(qry);
  		            	Main.db.setTransactionSuccessful();
  		            } finally {
  		            	Main.db.endTransaction();
  		            	Log.d(TAG, "db end transaction");
  		            }
		        } catch( Exception e ) {
		           	Log.e(TAG, "Database Exception: " + e.toString() );     	    
		        }
       	  	} // resultCode = 1
			Main.webRenamed = true;
			finish();
        }
		if (requestCode == 4) { // delete web name
			Log.d(TAG, "deleteOk resultCode:" + resultCode);
			deleteOk(resultCode);
		}
    }
 
    void renameFile () {
    	  return;
      }
    
    private void deleteSelectedFile() { // this doesn't delete any files -- just the web site text
		Log.d(TAG, "in deleteSelectedFile");
		int nl = webLink.indexOf("\n\t");
		Main.existingWebName = webCombined[selectedWeb].substring(0,nl);
		Main.alertRequest = 4; // delete selected web
		Intent a3b = new Intent(this, Alert3ButtonDialog.class);
		startActivityForResult(a3b, Main.alertRequest);  // request == 4 == delete selected web

	} // deleteSelectedWeb

    private void deleteOk(int id) {
    	char q = 34;
     	Log.d(TAG, "deleteOk option:" + id);
     	if (id == 1) { // 1 = no - cancel
     		Toast.makeText(this, "Delete Canceled", Toast.LENGTH_LONG).show();
			//finish(); // cancel
     	}
		if (id == 0) { // 0 = yes
			Main.db.beginTransaction();
			qry = "DELETE FROM BirdWebSites" +
					" WHERE WebSiteText = " + q + Main.existingWebName + q;
			Log.d(TAG, "Delete qry:" + qry);
			Main.db.execSQL(qry);
			Main.webRenamed = true;
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			Log.d(TAG, "db end transaction");
			finish();
		}
	}
       	  
    @Override
    public void onDestroy() {
          list.setAdapter(null);
          super.onDestroy();
      }

    void warningToast(String msg) {
    	  Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
      }
    
} // WebList

