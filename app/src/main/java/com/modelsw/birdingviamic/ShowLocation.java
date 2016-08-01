package com.modelsw.birdingviamic;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLocation extends AppCompatActivity implements LocationListener, View.OnClickListener {
	private static final String TAG = "ShowLocation";
	private TextView latitudeField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private EditText manLat;
	private EditText manLng;
	private int manualLat = 0;
	private int manualLng = 0;
	private RadioButton manualLocation;
	private int phoneLat = 0;
	private int phoneLng = 0;
	private RadioButton phoneLocation;
	private String provider;
	private String qry = "";
	private TextView locationStats;
	Toolbar toolbar;
	private Button update;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

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

		latitudeField = (TextView) findViewById(R.id.TextViewPhoneLat);
		longitudeField = (TextView) findViewById(R.id.TextViewPhoneLng);
		phoneLocation = (RadioButton) findViewById(R.id.phone_location);
		phoneLocation.setOnClickListener(this);
		manualLocation = (RadioButton) findViewById(R.id.manual_location);
		manualLocation.setOnClickListener(this);
		manLat = (EditText) findViewById(R.id.man_lat);
		manLng = (EditText) findViewById(R.id.man_lng);
		locationStats = (TextView) findViewById(R.id.location_stats);
		findViewById(R.id.update_button).setOnClickListener(this);
		// read from text file
		Log.d(TAG, "read the Location file");
		qry = "SELECT Value FROM Location WHERE Name = 'AutoLocation'";
		Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		Main.isAutoLocation = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Location WHERE Name = 'PhoneLat'";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		phoneLat = rs.getInt(0);
		qry = "SELECT Value FROM Location WHERE Name = 'PhoneLng'";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		phoneLng = rs.getInt(0);
		qry = "SELECT Value FROM Location WHERE Name = 'ManualLat'";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		manualLat = rs.getInt(0);
		qry = "SELECT Value FROM Location WHERE Name = 'ManualLng'";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		manualLng = rs.getInt(0);
		rs.close();

		manLat.setText(String.valueOf(manualLat));
		manLng.setText(String.valueOf(manualLng));

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider -> use default
		Criteria criteria = new Criteria();
		provider = null;
		Location location = null;
		try {
			provider = locationManager.getBestProvider(criteria, false);
			if (provider != null) {
				location = locationManager.getLastKnownLocation(provider);
			} else {
				latitudeField.setText("Location not available");
				longitudeField.setText("Location not available");
			}
		// Initialize the location fields
			if (location != null) {
				Log.d(TAG, "Provider " + provider + " has been selected.");
				onLocationChanged(location);
			} else {
				latitudeField.setText("Location not available");
				longitudeField.setText("Location not available");
				//   altitudeField.setText("Location not available");
			}
			if (Main.isAutoLocation == true) { // set when read location.txt above
				phoneLocation.setChecked(true);
				Main.latitude = phoneLat;
				Main.longitude = phoneLng;
			} else {
				manualLocation.setChecked(true);
				manualLat = Integer.parseInt(manLat.getText().toString());
				Main.latitude = manualLat;
				manualLng = Integer.parseInt(manLng.getText().toString());
				Main.longitude = manualLng;
			}
		} catch (SecurityException e) {
			Log.e(TAG, "Get Location manager -- Location Security exception:" + e);
		}
		showStats();
	}

	/* Request updates at startup -- does this work ??? or is it a to do */
	@Override
	protected void onResume() {
		super.onResume();
		try {
			locationManager.requestLocationUpdates(provider, 3600000, 5000, this);
		} catch (SecurityException e) {
			Log.e(TAG, "OnResume Location Security exception:" + e);
		}
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		try {
		locationManager.removeUpdates(this);
		} catch (SecurityException e) {
			Log.e(TAG, "OnPause Location Security exception:" + e);
		}
	}

	public void onLocationChanged(Location location) {
		phoneLat = (int) (location.getLatitude());
		phoneLng = (int) (location.getLongitude());
		manualLat = Integer.parseInt(manLat.getText().toString());
		Log.d(TAG, "onLocationChanged manualLat:" + manualLat);
		manualLng = Integer.parseInt(manLng.getText().toString());
		Log.d(TAG, "onLocationChanged manualLng:" + manualLng);

		latitudeField.setText(String.valueOf(phoneLat));
		longitudeField.setText(String.valueOf(phoneLng));
		if (Main.isAutoLocation == true) { // set when read location.txt above
			phoneLocation.setChecked(true);
			Main.latitude = phoneLat;
			Main.longitude = phoneLng;
		} else {
			manualLocation.setChecked(true);
			Main.latitude = manualLat;
			Main.longitude = manualLng;
		}
		Log.d(TAG, "onLocationChanged Main.latitude:" + Main.latitude);

	}


	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.phone_location: {
				Main.isAutoLocation = true;
				phoneLocation.setChecked(true);
				Main.latitude = phoneLat;
				Main.longitude = phoneLng;
				break;
			}
			case R.id.manual_location: {
				Main.isAutoLocation = false;
				manualLocation.setChecked(true);
				manualLat = Integer.parseInt(manLat.getText().toString());
				manualLng = Integer.parseInt(manLng.getText().toString());
				Main.latitude = manualLat;
				Main.longitude = manualLng;
				break;
			}
			case R.id.update_button: {
				Log.d(TAG, "save the location file");
				manualLat = Integer.parseInt(manLat.getText().toString());
				manualLng = Integer.parseInt(manLng.getText().toString());

				Main.db.beginTransaction();
				int temp = Main.isAutoLocation ? 1 : 0;
				String qry = "UPDATE Location SET Value = " + temp + " WHERE Name =  'AutoLocation'";
				Main.db.execSQL(qry);
				qry = "UPDATE Location SET Value = " + phoneLat + " WHERE Name =  'PhoneLat'";
				Main.db.execSQL(qry);
				qry = "UPDATE Location SET Value = " + phoneLng + " WHERE Name =  'PhoneLng'";
				Main.db.execSQL(qry);
				qry = "UPDATE Location SET Value = " + manualLat + " WHERE Name =  'ManualLat'";
				Main.db.execSQL(qry);
				qry = "UPDATE Location SET Value = " + manualLng + " WHERE Name =  'ManualLng'";
				Main.db.execSQL(qry);
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();

				qry = "SELECT Ref, InArea, MinX, MinY, MaxX, MaxY FROM CodeName ";
				Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
				rs.moveToFirst();
				while (!rs.isAfterLast()) {
					int ref = rs.getInt(0);
					int inArea = rs.getInt(1);
					int minX = rs.getInt(2);
					int minY = rs.getInt(3);
					int maxX = rs.getInt(4);
					int maxY = rs.getInt(5);
					// cross the date line
					if (maxX < minX && minX < Main.longitude) { // short-tailed shearwater 70 to -116 main at +170
						maxX += 360; // shearwater now at 70 to 244
					}
					if (maxX < minX && Main.longitude < maxX) { // short-tailed shearwater 70 to -116 main at -170
						minX -= 360; // shearwater now at -290 to -116
					}
					int isLngLat = 0;
					if (minY < Main.latitude && maxY > Main.latitude && minX < Main.longitude && maxX > Main.longitude) {
						isLngLat = 1; // we are within the bounding box area
					}
					if (inArea == 0) {
						if (isLngLat == 1) {  // update inArea to 1
							try {
								Main.db.beginTransaction();
								try {
									qry = "UPDATE CodeName SET InArea = 1 WHERE Ref = " + ref;
									Main.db.execSQL(qry);
									Main.db.setTransactionSuccessful();
								} finally {
									Main.db.endTransaction();
								}
							} catch (Exception e) {
								Log.e(TAG, "Database Exception: " + e.toString());
							}
						} // else leave it 0
					} else {  // inArea == 1
						if (isLngLat == 0) {  // update inArea to 0
							try {
								Main.db.beginTransaction();
								try {
									qry = "UPDATE CodeName SET InArea = 0 WHERE Ref = " + ref;
									Main.db.execSQL(qry);
									Main.db.setTransactionSuccessful();
								} finally {
									Main.db.endTransaction();
								}
							} catch (Exception e) {
								Log.e(TAG, "Database Exception: " + e.toString());
							}
						} // else leave it 1
					}
					rs.moveToNext();
				}

			} // button

		} // switch
		showStats();
	} // on click

	void showStats() {
		// Within Selected Regions
		qry = "SELECT Area from Region WHERE isSelected = 1";
		Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cntr = rs.getCount();
		String[] area = new String[cntr];
		for (int i = 0; i < cntr; i++) {
			area[i] = rs.getString(0);
			rs.moveToNext();
		}
		// within red list
		qry = "SELECT Type from RedList WHERE isSelected = 1";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cntrR = rs.getCount();
		String[] type = new String[cntrR];
		for (int i = 0; i < cntrR; i++) {
			type[i] = rs.getString(0);
			rs.moveToNext();
		}

		qry = "SELECT Count(Ref) from CodeName";
		qry += " WHERE Ref > 39997";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int syud = rs.getInt(0) + 1;

		String stats = "Species:\n";
		qry = "SELECT Count(Ref) from CodeName";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cnt = rs.getInt(0);
		int subtot = cnt - syud;
		stats += "\tWorld Bird List:" + subtot + "\n";
		stats += "\tSystem and User Defined:" + syud + "\n";
		stats += "\tTotal:" + cnt + "\n";
		// within redlist
		int oneTime = 0;
		if (cntrR == 0) {
			qry += " WHERE RedList = 'None'";
		} else {
			for (int i = 0; i < cntrR; i++) {
				if (oneTime == 0) {
					qry += " WHERE (RedList = '" + type[i] + "'";
					oneTime++;
				} else {
					qry += " OR RedList = '" + type[i] + "'";
				}
			}
			qry += ")";
		}
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToLast();
		cnt = rs.getInt(0);
		stats += "\tWithin Selected Red List:" + cnt + "\n";

		String qry1 = "PRAGMA case_sensitive_like = 1";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry1, null);
		if (cntr == 0) {
			qry += " AND Region = 'none'";
		} else {
			oneTime = 0;
			for (int i = 0; i < cntr; i++) {
				if (oneTime == 0) {
					qry += " AND (Region like '%" + area[i] + "%'";
					oneTime++;
				} else {
					qry += " OR Region like '%" + area[i] + "%'";
				}
				if (area[i].equals("Worldwide")) {
					qry += " OR CodeName.SubRegion like '%introduced worldwide%'";
				}
			}
			qry += ")";
			rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			rs.moveToLast();
			cnt = rs.getInt(0);
			stats += "\tWithin Selected Regions:" + cnt + "\n";

			// within selected location
			qry += " AND InArea = 1 ";
			rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			rs.moveToLast();
			cnt = rs.getInt(0);
			stats += "\tWithin Selected Location:" + cnt + "\n";
		}
		qry = "PRAGMA case_sensitive_like = 0";  // clear -- back to normal like aka Na == NA
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.close();
		locationStats.setText(stats);

	}

	@Override
	public void onStart() {
		super.onStart();
/*
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"ShowLocation Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.modelsw.birdingviamic/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
*/
	}

	@Override
	public void onStop() {
		super.onStop();
/*
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"ShowLocation Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.modelsw.birdingviamic/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
*/
	}
} // show location
		