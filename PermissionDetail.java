package com.modelsw.birdingviamic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

@TargetApi(23)
public class PermissionDetail extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PermissionDetail";
    Button dismiss_button;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    int targetSdkVersion;
    Toolbar toolbar;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };


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
        Log.d(TAG, "if permCntr:" + Main.permCntr + " = result:"  + result + " then exit");
        if (result == Main.permCntr){
            dismiss.performClick();
        }

    } // onCreate

    private int checkPermissions() {
        try {
            int cntr = 0;
            for (String p : permissions) {
                int result = ContextCompat.checkSelfPermission(this, p);
                boolean showRationale = shouldShowRequestPermissionRationale(p);
                Log.d(TAG, "check:" + p + " result:" + result + " Rationale:" + showRationale);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    if (showRationale == true) {
                        Main.listPermissionsNeeded.add(p);
                        Log.d(TAG, "missing permission:" + p);
                        cntr--;
                    } else {
                        Log.d(TAG, "NOT missing permission:" + p + " rationale:" + showRationale);
                    }
                }
                cntr++;
            }
            if (!Main.listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        Main.listPermissionsNeeded.toArray(new String[Main.listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return cntr;
            }
            return Main.permCntr;
        } catch (NoSuchMethodError e) { // this showed up from a android 5.1
            Log.e(TAG, "error:" + e);   // not supposed to be in this class if less than 6.0
            return 0;
        }
    }

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

