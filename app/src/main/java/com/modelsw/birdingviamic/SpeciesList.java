package com.modelsw.birdingviamic;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SpeciesList extends AppCompatActivity {
	private static final String TAG = "SpeciesList";
	private SpeciesAdapter adapter;
	public static Boolean[] chk;
	public Boolean foundRenamed = false;
	private ListView list;
	private static int myRequest = 0;
	private static char q = 34;
	private String qry = "";
	private Intent rename;  // this is data sent to dialog that I am not initializing
	private Cursor rs;  // I see cursor as RecordSet (rs)
	public static String[] speciesCombined;
	public static int selectedSpecies; // the species clicked on
	public static int speciesDbLen; // count of species
	public static EditText specText;
	Toolbar toolbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.specieslist_header);
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

		Main.db = Main.songdata.getWritableDatabase();
		buildList();
		list = (ListView) findViewById(R.id.list);
		adapter = new SpeciesAdapter(this, speciesCombined);  // fileName on one line and Spec Inx Seg on second line
		list.setAdapter(adapter);
		list.setFastScrollEnabled(true);

		Button rename=(Button)findViewById(R.id.rename_button);
		rename.setOnClickListener(listener);
		Button delete=(Button)findViewById(R.id.delete_button);
		delete.setOnClickListener(listener);
		Button add=(Button)findViewById(R.id.add_button);
		add.setOnClickListener(listener);
//		specText = (EditText) findViewById(R.id.spec_text);
//		specText.setOnClickListener(listener);
//		commonnameText = (EditText) findViewById(R.id.commonname_text);
		if (Main.specRenamed == true || Main.existingRef > 0) {
			list.setSelection(Main.specOffset -1);
		}
		Main.specRenamed = false;
	} // onCreate

	void buildList() {
		Log.d(TAG, "buildList");
		qry = "SELECT Area from Region WHERE isSelected = 1";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cntr = rs.getCount();
		String[] area = new String[cntr];
		for (int i = 0; i<cntr; i++) {
			area[i] = rs.getString(0);
			rs.moveToNext();
		}
		qry = "SELECT Type from RedList WHERE isSelected = 1";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cntrR = rs.getCount();
		String[] type = new String[cntrR];
		for (int i = 0; i<cntrR; i++) {
			type[i] = rs.getString(0);
			rs.moveToNext();
		}

		qry = "PRAGMA case_sensitive_like = 1";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);

		qry = "SELECT Ref, Spec, CommonName, Region, SubRegion, RedList FROM CodeName";
		if (cntr == 0) {
			qry += " WHERE Region = 'none'";
		} else {
			int oneTime = 0;
			for (int i = 0; i< cntr; i++) {
				if (oneTime == 0) {
					qry += " WHERE (Region like '%" + area[i] + "%'";
					oneTime++;
				} else {
					qry += " OR Region like '%" + area[i] + "%'";
				}
				if (area[i].equals("Worldwide")) {
					qry += " OR SubRegion like '%introduced worldwide%'";
				}
			}
			qry += ")";
			if (cntrR == 0) {
				qry += " AND RedList = 'None'";
			} else {
				oneTime = 0;
				for (int i = 0; i < cntrR; i++) {
					if (oneTime == 0) {
						qry += " AND (RedList = '" + type[i] + "'";
						oneTime++;
					} else {
						qry += " OR RedList = '" + type[i] + "'";
					}
				}
				qry += ")";
			}
			if (Main.isUseLocation == true) {
				qry += " AND InArea = 1 ";
			}
			if (Main.isSortByName == true) {
				qry += " ORDER BY CommonName";
			} else {
				qry += " ORDER BY Ref";
			}
		}
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		speciesDbLen = rs.getCount();
		int extraSpecies = 1;  // 1 more than the last time it was loaded -- so you always have room
		speciesCombined = new String[speciesDbLen + extraSpecies];
		chk = new Boolean[speciesDbLen + extraSpecies];
		Main.speciesRef = new int[speciesDbLen + extraSpecies];
		rs.moveToFirst();
		for (int i=0; i<speciesDbLen; i++) {
			Main.speciesRef[i] = rs.getInt(0);
			String specName = rs.getString(2);
			speciesCombined[i] = specName + " (" + rs.getInt(0) + ")\n\t"
					+ rs.getString(1) + " " + rs.getString(5) + "\n\t"
					+ rs.getString(3) + " : " + rs.getString(4); // CommonName  spec Location
			chk[i] = false;
			if (Main.specRenamed == true) {
				if (foundRenamed == false) {
					if (Main.newSpecName != null) {
						if (Main.newSpecName.equals(specName)) {
							Main.specOffset = i;
							foundRenamed = true;
						}
					}
				}
			} else {  // not renamed
				if(Main.speciesRef[i] == Main.existingRef && Main.existingRef > 0) {
					Main.specOffset = i;
					//Main.specieSelected = speciesCombined[i];
					Main.specieSelected = "YYYYYYYYYYYYY";
				}
			}

			rs.moveToNext();
		}
		qry = "PRAGMA case_sensitive_like = 0";  // clear -- back to normal like aka Na == NA
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.close();
	} // buildList



	public OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			qry = "SELECT Spec, CommonName, Region, SubRegion, RedList FROM CodeName" +
					" WHERE Ref =" + Main.existingRef;
			rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			if (rs.getCount() > 0) {
				rs.moveToFirst();
				Main.existingSpec = rs.getString(0);
				Main.existingSpecName = rs.getString(1);
				Main.existingRegion = rs.getString(2);
				Main.existingSubRegion = rs.getString(3);
				Main.existingRedList = rs.getString(4);
				Log.d(TAG, "OnClickListener ref:" + Main.existingRef + " spec:" + Main.existingSpec + " commonname:" + Main.existingSpecName +
						" region:" + Main.existingRegion + " : " + Main.existingSubRegion + " redList:" + Main.existingRedList +
						" specOffset: " + Main.specOffset);
				Main.xenocanto = true;
				Main.wikipedia = false;
				Main.showWebFromIdentify = true;
			}
			switch (v.getId()) {
				case R.id.rename_button:
					if (Main.existingRef > Main.userRefStart) {
						Main.showWebFromIdentify = false;
						Main.xenocanto = false;
						Main.newSpecName = Main.existingSpecName;
						Main.newSpec = Main.existingSpec;
						Main.newRegion = Main.existingRegion;
						Main.newSubRegion = Main.existingSubRegion;
						Main.newRedList = Main.existingRedList;
						myRequest = 2; // rename
						Main.myRequest = myRequest;
						getNewName();
					} else {
						String msg = "Sorry, you can only change your own definitions.";
						warningToast(msg);
					}
					break;
				case R.id.delete_button:
					Log.d(TAG, "Delete clicked");
					if (Main.existingRef > Main.userRefStart) {
						Main.showWebFromIdentify = false;
						deleteSelectedFile();
					} else {
						String msg = "You can only delete your own names.";
						warningToast(msg);
					}
					break;
				case R.id.add_button:
					Main.showWebFromIdentify = false;
					qry = "SELECT MAX(Ref) AS MaxRef FROM CodeName";
					rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
					rs.moveToFirst();
					Main.newSpecRef = rs.getInt(0)+1;
					Main.existingRef = Main.newSpecRef;
					Main.existingSpec = "";
					Main.existingSpecName = "";
					Main.existingRedList = "";
					Main.newSpecName = null;
					Main.newSpec = null;
					Main.newRedList = null;
					if (Main.isUseLocation == false) {
						Main.existingRegion = "?";
						Main.newRegion = "?";
						Main.existingSubRegion = "Unknown";
						Main.newSubRegion = "Unknown";
					} else {
						Main.existingRegion = "?";
						Main.newRegion = "?";
						Main.existingSubRegion = "Local";
						Main.newSubRegion = "Local";
					}
					Log.d(TAG, "add clicked");
					myRequest = 1; // add
					Main.myRequest = myRequest;
					getNewName();
					//v.requestLayout();
					break;
				//case R.id.check
				// See SpeciesAdapter.java getView
				//	Main.specieSelected = speciesCombined[Main.specOffset];
				//	Log.d(TAG, "***--> Main.specieSelected: " + Main.specieSelected + " Main.specOffset: " + Main.specOffset);
				//	break;
			} // switch
		} // onclick
	}; // onClickListener (because it does NOT extend OnClickListener)

	public void getNewName() {
		if (Main.existingRef == 0) {
			Log.d(TAG, "*** 1a *** getNewName existing name is null -- returning");
			Toast.makeText(this, "Please select a name to change.", Toast.LENGTH_LONG).show();
			return;
		}
		Log.d(TAG, "*** 1b *** getNewName: existingSpecName:" + Main.existingSpecName + " newSpecName:" + Main.newSpecName +
				" existingSpec:" + Main.existingSpec + " newSpec:" + Main.newSpec +
				" existingRange:" + Main.existingRegion + " : " + Main.existingSubRegion + " newRange:" + Main.newRegion + " : " + Main.newSubRegion +
				" existingRedList:" + Main.existingRedList + " newRedList:" + Main.newRedList	);
		Intent nsd = new Intent(this, NewSpecDialog.class);
		Main.myRequest = myRequest;
		startActivityForResult(nsd, myRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//         super.onActivityResult(requestCode, resultCode, data);
		// Check which request we're responding to
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult requestCode:" + requestCode);
		if (requestCode == 1) {  // add a species
			Log.d(TAG, "*** 7 *** onActivityResult resultCode:" + resultCode);
			// Make sure the request was successful
			if (resultCode == 1) {
				// The user picked a FileName.
				if (!Main.newSpecName.isEmpty()) { // if NOT blank add
					try {
						Log.d(TAG, "db begin transaction");
						Main.db.beginTransaction();
						ContentValues val = new ContentValues();
						try {
							Log.d(TAG, "Add Species:" + Main.newSpec + " CommonName:" + Main.newSpecName + " region:" + Main.newRegion + " : " + Main.newSubRegion);
							val.put("Ref", Main.newSpecRef);
							val.put("Spec", Main.newSpec);
							val.put("CommonName", Main.newSpecName);
							val.put("Region", Main.newRegion);
							val.put("SubRegion", Main.newSubRegion);
							val.put("RedList", Main.newRedList);
							if (Main.isUseLocation == false) {
								val.put("InArea", 1);
								val.put("MinX", -180);
								val.put("MinY", -90);
								val.put("MaxX", 180);
								val.put("MaxY", 90);
							} else {
								int lat = Main.latitude;
								int lng = Main.longitude;
								val.put("InArea", 1);
								val.put("MinX", lat - 1);
								val.put("MinY", lng - 1);
								val.put("MaxX", lat + 1);
								val.put("MaxY", lng + 1);
							}
							Main.db.insert("CodeName", null, val);
						} finally {
							Main.db.setTransactionSuccessful();
							Main.db.endTransaction();
							val.clear();
							Log.d(TAG, "db end transaction");
						}
					} catch (Exception e) {
						Log.e(TAG, "Database Exception: " + e.toString());
					}
				}  // if not empty
			} // result code 1
			Main.specRenamed = true;
			finish();
		}  // request code 1
		if (requestCode == 2) {
			if (resultCode == 1) {
				Log.d(TAG, "rename clicked");
				try {
					Log.d(TAG, "db begin transaction");
					Main.db.beginTransaction();
					try {
						qry = "UPDATE CodeName" +
								" SET CommonName = " + q + Main.newSpecName + q +
								", Spec = " + q + Main.newSpec + q +
								", Region = " + q + Main.newRegion + q +
								", SubRegion = " + q + Main.newSubRegion + q +
								", RedList = " + q + Main.newRedList + q;
						if (Main.isUseLocation == false) {
							qry += ", InArea = 1" +
									", MinX = -180" +
									", MinY = -90" +
									", MaxX = 180" +
									", MaxY = 90";
						} else {
							int lat = Main.latitude;
							int lng = Main.longitude;
							qry += ", InArea = 1" +
									", MinX = " + (lng - 1) +
									", MinY = " + (lat - 1) +
									", MaxX = " + (lng + 1) +
									", MaxY = " + (lat + 1);
						}
						qry += " WHERE Ref = " + Main.existingRef;
						Log.d(TAG, "Rename qry:" + qry);
						Main.db.execSQL(qry);
						Main.db.setTransactionSuccessful();
					} finally {
						Main.db.endTransaction();
						Log.d(TAG, "db end transaction");
					}
				} catch (Exception e) {
					Log.e(TAG, "Database Exception: " + e.toString());
				}
			} // resultCode = 1
			Main.specRenamed = true;
			finish();
		}
		if (requestCode == 3) { // delete a species
			deleteOk(resultCode);
		}
	}

	private void deleteSelectedFile() { // you only get here if personal species
		Log.d(TAG, "in deleteSelectedFile" );
		Main.alertRequest = 3; // delete selected species
		Intent a3b = new Intent(this, Alert3ButtonDialog.class);
		startActivityForResult(a3b, Main.alertRequest);  // request == 3 == delete selected species
	} // deleteSelectedFile

	private void deleteOk(int id) {
		char q = 34;
		Log.d(TAG, "deleteOk option:" + id);
		if (id == 1) { // 1 = no - cancel
			Toast.makeText(this, "Delete Canceled", Toast.LENGTH_LONG).show();
			return; // cancel
		}
		//Main.listOffset = 0;
		Main.db.beginTransaction();
		qry = "DELETE FROM CodeName" +
				" WHERE Ref = " + Main.existingRef;
		Log.d(TAG, "Delete qry:" + qry);
		Main.db.execSQL(qry);
		Main.existingRef = 0;
		Main.specRenamed = true;
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();
		Log.d(TAG, "db end transaction");
		finish();
	}

	@Override
	public void onDestroy() {
		list.setAdapter(null);
		super.onDestroy();
	}

	void warningToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

} // EditSpecies

