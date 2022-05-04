package com.modelsw.birdingviamic;

//import android.Manifest;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.modelsw.birdingviamic.Main.permissions;
import static com.modelsw.birdingviamic.R.id.storage_permission_label;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
//import android.os.Build;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.util.ArrayList;
import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

//import java.util.List;

//@TargetApi(23)
public class PermissionDetail extends AppCompatActivity {
    private static final String TAG = "PermissionDetail";
    // Defining Buttons
    Button dismiss, storage, audio;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    int targetSdkVersion;
    Toolbar toolbar;

    int iver = getAndroidVersion();

    // Defining Permission codes.
    // We can give any value
    // but unique for each permission.
    private static final int STORAGE_PERMISSION_CODE = 100; // Read and write song files
    private static final int AUDIO_PERMISSION_CODE = 101; // record songs
    private static final int INTERNET_PERMISSION_CODE = 102; // Read and write song files
    private static final int LOCATION_PERMISSION_CODE = 103; // Fine and Course
    // I round to the nearest degree which is close enough for bird identification
    // but I need Fine (GPS) and Course (Network)
    // because you might be in a remote area that doesn't receive network and thus requires GPS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        TextView storage_text = (TextView) findViewById(R.id.storage_permission_label);
        storage_text.setVisibility(TextView.GONE);
        Button storage = findViewById(R.id.storage);
        storage.setVisibility(View.GONE);

        TextView audio_text = (TextView) findViewById(R.id.audio_permission_label);
        audio_text.setVisibility(TextView.GONE);
        Button audio = findViewById(R.id.audio);
        audio.setVisibility(View.GONE);

        TextView internet_text = (TextView) findViewById(R.id.internet_permission_label);
        internet_text.setVisibility(TextView.GONE);
        Button internet = findViewById(R.id.internet);
        internet.setVisibility(View.GONE);

        TextView location_text = (TextView) findViewById(R.id.location_permission_label);
        location_text.setVisibility(TextView.GONE);
        Button location = findViewById(R.id.location);
        location.setVisibility(View.GONE);

        Button dismiss = findViewById(R.id.dismiss_button);
        dismiss.setVisibility(View.VISIBLE);

        int result0 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE); // result --> 0=GRANTED; -1=DENIED
        int result1 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE); // result --> 0=GRANTED; -1=DENIED
        int result2 = ContextCompat.checkSelfPermission(this, MANAGE_EXTERNAL_STORAGE); // result --> 0=GRANTED; -1=DENIED
        if (iver < 30) {
            result2 = 0;
        }
        Log.d(TAG, "result0:" + result0 + ", result1:" + result1 + ", result2:" + result2);
        if (result0 + result1 + result2 != 0) {
            storage_text.setVisibility(TextView.VISIBLE);
            storage.setVisibility(View.VISIBLE);
            storage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermission(WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                    checkPermission(READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                    checkPermission(MANAGE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                }
            });
        }

        int result3 = ContextCompat.checkSelfPermission(this, RECORD_AUDIO); // result --> 0=GRANTED; -1=DENIED
        if (result3 != 0) {
            audio_text.setVisibility(TextView.VISIBLE);
            audio.setVisibility(View.VISIBLE);
            audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermission(RECORD_AUDIO, AUDIO_PERMISSION_CODE);
                }
            });
        }

        int result4 = ContextCompat.checkSelfPermission(this, INTERNET); // result --> 0=GRANTED; -1=DENIED
        if (result4 != 0) {
            internet_text.setVisibility(TextView.VISIBLE);
            internet.setVisibility(View.VISIBLE);
            internet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermission(INTERNET, INTERNET_PERMISSION_CODE);
                }
            });
        }

        int result5 = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION); // result --> 0=GRANTED; -1=DENIED
        int result6 = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION); // result --> 0=GRANTED; -1=DENIED
        if (result5 + result5 != 0) {
            location_text.setVisibility(TextView.VISIBLE);
            location.setVisibility(View.VISIBLE);
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermission(ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
                    checkPermission(ACCESS_COARSE_LOCATION, LOCATION_PERMISSION_CODE);
                }
            });
        }

        if (result0 + result1 + result2 + result3 + result4 + result5 + result6 == 0) {
            Toast.makeText(PermissionDetail.this, "All Permissions granted", Toast.LENGTH_LONG).show();
            finish();
        } else {
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    } // onCreate

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)  {
        if (ContextCompat.checkSelfPermission(PermissionDetail.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(PermissionDetail.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(PermissionDetail.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    // This function is called when the user accepts or declines the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompted for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PermissionDetail.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PermissionDetail.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PermissionDetail.this, "Audio Permission Granted", Toast.LENGTH_SHORT) .show();
            } else {
                Toast.makeText(PermissionDetail.this, "Audio Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PermissionDetail.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PermissionDetail.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PermissionDetail.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PermissionDetail.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }




/*
        int result = checkPermissions();
        //checkPermissions_10(); // moved from main in android 10
        Log.d(TAG, "if permCntr:" + Main.permCntr + " = result:"  + result + " then exit");
        if (result == Main.permCntr){
            dismiss.performClick();
        }
		
*/


    private int checkPermissions() {
        try {
            int cntr = 0;
            for (String p : permissions) {
                int result = ContextCompat.checkSelfPermission(this, p); // result --> 0=GRANTED; -1=DENIED
                // show why you need this permission -- false == it is already GRANTED; true == explain why you need it.
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

    /*
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
		Main.listPermissionsNeeded = new ArrayList<String>();
		for (String p:permissions) {
			int result = ContextCompat.checkSelfPermission(this, p);
			Log.d(TAG, "result:" + result + " permission:" + p);
			if (result != PackageManager.PERMISSION_GRANTED && result != 0) {
                Main.listPermissionsNeeded.add(p);
				Log.d(TAG, "missing permission:" + p + " result:" + result);
			}
		}
		if (Main.listPermissionsNeeded.isEmpty()) { // either none added or failed to add
			return true;
		} else {
			return false; // list is not empty go get permissions
		}
	} // checkPermissions_10
	*/

    public static int getAndroidVersion() {
        int iVer = Build.VERSION.SDK_INT;
        return iVer;
    }

}
