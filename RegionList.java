package com.modelsw.birdingviamic;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class RegionList extends AppCompatActivity implements android.view.View.OnClickListener {
	private static final String TAG = "RegionList";

    private RegionAdapter adapter;
    private static String[] area;
    public static Boolean[] chk;
	private static Context ctx;
	private String existingRegion;
    public static int existingRegionId;
    public static int[] id;
    public static boolean isRegionSelected;
    private ListView list;
    private static int myRequest = 0;
    private static char q = 34;
	private String qry = "";
    public static String[] regionCombined;
    public static String regionLink; // the text for the web site
	public static int regionDbLen = 0;
    public static EditText regionText;
	private Cursor rs;  // I see cursor as RecordSet (rs)
    public static int selectedRegion; // index of the web site clicked on
    Toolbar toolbar;
	private Button update;
	  
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.region_header);
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
		if (Main.songpath == null || Main.songdata == null) {
			finish();
			return;
		}

        findViewById(R.id.update_button).setOnClickListener(this);
		// read from text file

	    Main.db = Main.songdata.getWritableDatabase();
		buildList();
        list = (ListView) findViewById(R.id.list);  
        adapter = new RegionAdapter(this, regionCombined);  // fileName on one line and Spec Inx Seg on second line
        list.setAdapter(adapter);
        list.setFastScrollEnabled(true);

	}
	
    void buildList() {
   	    Log.d(TAG, "buildList read the Region table");
   	    qry = "SELECT Area, FullName, isSelected from Region ORDER BY Area";
   		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
   		regionDbLen = rs.getCount();
   		regionCombined = new String[regionDbLen];
   		chk = new Boolean[regionDbLen];
   		area = new String[regionDbLen];
   		rs.moveToFirst();
   		for (int i = 0; i<regionDbLen; i++) {
   			area[i] = rs.getString(0);
   			regionCombined[i] = area[i] + " : " + rs.getString(1);
   			int j = rs.getInt(2);
   			if (j == 0) {
   				chk[i] = false;
   			} else {
   				chk[i] = true;
   			}
			rs.moveToNext();
   		}
   		rs.close();
        Log.d(TAG, "loaded the regionList len:" + regionDbLen);    
    } // buildList


	 
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_button: {
		    Log.d(TAG, "save the regions in the database");
		    int ck = 0;
		    for (int i = 0; i<regionDbLen; i++) {
		    	if (chk[i] == true) {
		    		ck = 1;
		    	} else {
		    		ck = 0;
		    	}
				qry = "UPDATE Region" +
					" SET isSelected = " + ck +
					" WHERE Area = '" + area[i] + "'";
					Main.db.execSQL(qry);
		    }
		    break;
		} // button

		} // switch
	} // on click

	
} // show region

