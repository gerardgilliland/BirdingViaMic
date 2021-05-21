package com.modelsw.birdingviamic;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.net.HttpURLConnection;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.content.pm.PackageInfo;

public class Register extends AppCompatActivity implements OnClickListener {
    String TAG = "Register";
	String qry = "";
	Cursor rs; // I think of Cursor as Record Set
	EditText comments;
	String dateFmt; 
	Intent emailIntent;
	String info;
	long iNow;
	TextView versionInfo;
    Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

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

        iNow = System.currentTimeMillis();
		String format = "yyyy-MM-dd HH:mm";        	
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		dateFmt = sdf.format(iNow);
		info =  "DateTime:" + dateFmt 
				+ "\nManufacturer:" + getManufacturer()
		 		+ "\nModel:" + getModel()
		 		+ "\nAndroidVersion:" + getAndroidVersion()
		 		+ "\nAndroidRelease:" + getAndroidRelease()
		 		+ "\nBirdingViaMic:" + getBirdingViaMicVersion(this)
				+ "\nIocVersion:" + getIocVersion()
				+ "\nComments:";
		
	    versionInfo = (TextView) findViewById(R.id.version_info);
	    versionInfo.setText(info);
	    
	    comments = (EditText) findViewById(R.id.comments);
	    View registerNowButton = findViewById(R.id.register_now_button);
	    registerNowButton.setOnClickListener(this);

	    
	}

	public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.register_now_button:
			emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "birdingviamic@modelsw.com", null));
    	    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Register_BirdingViaMic");
    	    emailIntent.putExtra(Intent.EXTRA_TEXT, info + comments.getText() + "\n");
    		startActivityForResult(emailIntent, 1);    		
    		break;
    	} // switch
	}	  


	public static String getAndroidVersion() {
	    int iVer = Build.VERSION.SDK_INT;
		return String.format("%d", iVer);
	}
	  
	public static String getAndroidRelease() {
	    return Build.VERSION.RELEASE;
	}
	  
	public static String getManufacturer() {
		  return Build.MANUFACTURER;
	  }
	  
	public static String getModel() {
		  return Build.MODEL;
	  }
	  
	public static String getBirdingViaMicVersion(Context ctx) {
	    return ctx.getResources().getString(R.string.app_version_name);
	}

	public String getIocVersion() {
		qry = "SELECT Num from Version";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int dbVer = rs.getInt(0);  // this is the old version 61, or 92 if not upgraded -- the new version 111 if done.
		rs.close();
		return String.format("%d", dbVer);
	}


}