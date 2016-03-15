package com.modelsw.birdingviamic;

import java.io.FileOutputStream;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;


public class SelectSongPath extends AppCompatActivity implements OnClickListener {
	private static final String TAG = "SelectSongPath";  
	private EditText customPath;
	private RadioButton path1;
	private RadioButton path2;
	private RadioButton path3;
    private RadioButton path4;
    private RadioButton path5;
    private RadioButton path6;
    private RadioButton path7;
	private CharSequence path1Label;
	private CharSequence path2Label;
	private CharSequence path3Label;
    private CharSequence path4Label;
    private CharSequence path5Label;
    private CharSequence path6Label;
    private CharSequence path7Label;
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_song_path);
        // action bar toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setLogo(R.drawable.treble_clef_linen);
        toolbar.setTitleTextColor(getResources().getColor(R.color.teal));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Navigation Icon tapped");
                finish();
            }
        });


/*      View returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(this); */
        path1 = (RadioButton) findViewById(R.id.path_1);
        path1.setOnClickListener(this);
        path1Label = path1.getText();
        path2 = (RadioButton) findViewById(R.id.path_2);
        path2.setOnClickListener(this);
        path2Label = path2.getText();
        path3 = (RadioButton) findViewById(R.id.path_3);
        path3.setOnClickListener(this);
        path3Label = path3.getText();
        path4 = (RadioButton) findViewById(R.id.path_4);
        path4.setOnClickListener(this);
        path4Label = path4.getText();
        path5 = (RadioButton) findViewById(R.id.path_5);
        path5.setOnClickListener(this);
        path5Label = path5.getText();
        path6 = (RadioButton) findViewById(R.id.path_6);
        path6.setOnClickListener(this);
        path6Label = path6.getText();
        path7 = (RadioButton) findViewById(R.id.path_7);
        path7.setOnClickListener(this);
        path7Label = path7.getText();
        customPath = (EditText) findViewById(R.id.custom_path);
        customPath.setOnClickListener(this);
        customPath.setText(Main.customPathLocation);
        Log.d(TAG, "path:" + Main.path + " customPathLocation:" + Main.customPathLocation);
        switch (Main.path) {
        	case 1: path1.setChecked(true); break;
        	case 2: path2.setChecked(true); break;
        	case 3: path3.setChecked(true); break;
            case 4: path4.setChecked(true); break;
            case 5: path5.setChecked(true); break;
            case 6: path6.setChecked(true); break;
            case 7: path7.setChecked(true); break;
        }

}

	public void onClick(View v) {
		// TODO Auto-generated method stub
    	switch (v.getId()) {
/*    	case R.id.return_button:
    		finish();
    		break; */
    	case R.id.path_1:
    		Main.songpath = Main.songPathDir.toString() + "/";
    		Main.path = 1;
    	    Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
    		break;
    	case R.id.path_2:
    		Main.songpath = Main.environment + "/" + getResources().getString(R.string.path2_location) + "/";
    		Main.path = 2;
    	    Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
    		break;
        case R.id.path_3:
            Main.songpath = Main.environment + "/" + getResources().getString(R.string.path3_location) + "/";
            Main.path = 3;
            Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
            break;
        case R.id.path_4:
            Main.songpath = Main.environment + "/" + getResources().getString(R.string.path4_location) + "/";
            Main.path = 4;
            Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
            break;
        case R.id.path_5:
            Main.songpath = Main.environment + "/" + getResources().getString(R.string.path5_location) + "/";
            Main.path = 5;
            Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
            break;
        case R.id.path_6:
            Main.songpath = Main.environment + "/" + getResources().getString(R.string.path6_location) + "/";
            Main.path = 6;
            Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
            break;
    	case R.id.path_7:
    		Main.customPathLocation = customPath.getText().toString();
    		//if (Main.customPathLocation.substring(0, 1) != "/") {
    		//	Main.customPathLocation = "/" + Main.customPathLocation;
    		//}
    		Main.songpath = Main.environment + "/" + Main.customPathLocation + "/";
    		Main.path = 7;
    	    Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
    		break;
    	}
        //onPause();
	}
	
	protected void onPause() {
    	super.onPause();
		Main.customPathLocation = customPath.getText().toString();
	    Log.d(TAG, "onPause saveTheSongPath path:" + Main.path + " customPathLocation:" + Main.customPathLocation);
        Main.db.beginTransaction();
        String qry = "UPDATE SongPath SET Path = " + Main.path + ", CustomPath = '" + Main.customPathLocation + "'";
        Main.db.execSQL(qry);
        Main.db.setTransactionSuccessful();
        Main.db.endTransaction();
   }

}
