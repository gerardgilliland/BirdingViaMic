package com.modelsw.birdingviamic;

//import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
//import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
//import android.os.Build;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

//import java.util.ArrayList;
//import java.util.List;

@TargetApi(23)
public class PermissionDetail extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PermissionDetail";
    Button dismiss_button;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    int targetSdkVersion;
    Toolbar toolbar;

    //@RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.treble_clef_linen);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.teal));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Navigation Icon tapped");
            }
        });

        Button dismiss = (Button) findViewById(R.id.dismiss_button);
        findViewById(R.id.dismiss_button).setOnClickListener(this);
        int result = checkPermissions();
		checkPermissions_10(); // moved from main in android 10
        Log.d(TAG, "if permCntr:" + Main.permCntr + " = result:"  + result + " then exit");
        if (result == Main.permCntr){
            dismiss.performClick();
        }
		

    } // onCreate

    private int checkPermissions() {
        try {
            int cntr = 0;
            for (String p : Main.permissions) {
                int result = ContextCompat.checkSelfPermission(this, p); // result --> 0=GRANTED; -1=DENIED
                // should you show why you need this permission -- false == it is already GRANTED; true == explain why you need it.
                boolean showRationale = shouldShowRequestPermissionRationale( p );
                Log.d(TAG, "check:" + p + " result:" + result + " Rationale:" + showRationale);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    if (showRationale == true) {
                        Main.listPermissionsNeeded.add(p);
                        Log.d(TAG, "missing permission:" + p);
                        cntr--;
                    } else {
                        Log.d(TAG, "NOT missing permission:" + p + " rationale:" + showRationale);  // not granted but not required (Android 9)
                    }
                }
                cntr++;
            }
            return cntr;

        } catch (NoSuchMethodError e) { // this showed up from a android 5.1
            Log.e(TAG, "error:" + e);   // not supposed to be in this class if less than 6.0
            return 0;
        }
    }

	private boolean checkPermissions_10() {
		try {
			final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			targetSdkVersion = info.applicationInfo.targetSdkVersion;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "error name not found:" + e);
			return true; // problems with package -- don't get lost in permissions
		}
		// For Android < Android M, self permissions are always granted.
		Log.d(TAG, "targetSdkVersion:" + targetSdkVersion);
		Log.d(TAG, "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT
				+ " Build.VERSION_CODES.M:" + Build.VERSION_CODES.M);
		if (targetSdkVersion < Build.VERSION_CODES.M) {
			return true;
		}
		listPermissionsNeeded = new ArrayList<String>();
		for (String p:permissions) {
			int result = ContextCompat.checkSelfPermission(this, p);
			Log.d(TAG, "result:" + result + " permission:" + p);
			if (result != PackageManager.PERMISSION_GRANTED && result != 0) {
				listPermissionsNeeded.add(p);
				Log.d(TAG, "missing permission:" + p + " result:" + result);
			}
		}
		if (listPermissionsNeeded.isEmpty()) { // either none added or failed to add
			return true;
		} else {
			return false; // list is not empty go get permissions
		}
	} // checkPermissions_10
	
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dismiss_button:
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                Log.d(TAG, "dismiss_button permissions");
                finish();
                break;

        }
    }

}
