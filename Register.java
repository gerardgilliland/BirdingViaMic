package com.modelsw.birdingviamic;

import java.text.SimpleDateFormat;
import java.util.Locale;

//import org.apache.http.protocol.HTTP;
import java.net.HttpURLConnection;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.content.pm.PackageInfo;

public class Register extends AppCompatActivity implements OnClickListener {
    String TAG = "Register";
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
        toolbar.setTitleTextColor(getResources().getColor(R.color.teal));
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
				+ "\nEmailAddress:" + getEmail(this) 
				+ "\nManufacturer:" + getManufacturer()
		 		+ "\nModel:" + getModel()
		 		+ "\nAndroidVersion:" + getAndroidVersion()
		 		+ "\nAndroidRelease:" + getAndroidRelease()
		 		+ "\nBirdingViaMic:" + getBirdingViaMicVersion(this)
				+ "\nComments:";
		
	    versionInfo = (TextView) findViewById(R.id.version_info);
	    versionInfo.setText(info);
	    
	    comments = (EditText) findViewById(R.id.comments);
	    View registerNowButton = findViewById(R.id.register_now_button);
	    registerNowButton.setOnClickListener(this);
	    //View websiteButton = findViewById(R.id.website_button);
	    //websiteButton.setOnClickListener(this);

	    
	}

	public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.register_now_button:
			emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "birdingviamic@modelsw.com", null));
    	    //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"birdingviamic@modelsw.com"}); // recipients
    	    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Register_BirdingViaMic");
    	    emailIntent.putExtra(Intent.EXTRA_TEXT, info + comments.getText() + "\n");
    		startActivityForResult(emailIntent, 1);    		
    		break;
		/*
    	case R.id.website_button:
    		Intent i = new Intent(this, WebList.class);
    		Main.wikipedia = false;
	    	Main.xenocanto = false;
            startActivity(i);   
	    	break;
	    */
    	} // switch
	}	  

	public static String getEmail(Context context) {
	    AccountManager accountManager = AccountManager.get(context); 
	    Account account = getAccount(accountManager);

	    if (account == null) {
	      return null;
	    } else {
	      return account.name;
	    }
	  }

	private static Account getAccount(AccountManager accountManager) {
	    Account[] accounts = accountManager.getAccountsByType("com.google");
	    Account account;
	    if (accounts.length > 0) {
	      account = accounts[0];      
	    } else {
	      account = null;
	    }
	    return account;
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

}