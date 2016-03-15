package com.modelsw.birdingviamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SongList extends AppCompatActivity {
	private static final String TAG = "SongList";
    private SongAdapter adapter;
	private Boolean foundExisting = false;
	private Boolean foundRenamed = false;
    private ListView list;
	private String qry = "";
	private Cursor rs;  // I see cursor as RecordSet (rs)
	private Cursor rsCk; 
	private String songPath = null;
	private int filtLow;
	private int filtHi;
	private int filtBeg;
	private int filtEnd;
	private int filtStrt;
	private int filtStop;
	private int songsDbLen;  // count of songs in Database
	private int songsFileLen; // count of songs in file
	private int songsLen;  // the max of the above plus some space
	Toolbar toolbar;
	private byte[] metaBuffer;

    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate songpath:" + Main.songpath + " songdata:" + Main.songdata);
		if (Main.songdata == null || Main.songpath == null) {
			Intent sm = new Intent(this, Main.class); // restart it is not in memory.
			startActivity(sm);
		}
        setContentView(R.layout.songlist_header );

        // action bar toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setLogo(R.drawable.treble_clef_linen);
        toolbar.setTitleTextColor(getResources().getColor(R.color.teal));
		onCreateOptionsMenu();
		toolbar.showOverflowMenu();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Navigation Icon tapped");
                finish();
            }
        });
		songPath = Main.songpath;
		if (Main.songdata == null) {
			Main.songdata = new SongData(this, Main.databaseName, null, Main.databaseVersion);
		}
		buildList();
		Main.db = Main.songdata.getWritableDatabase();
		qry = "SELECT FileName from SongList WHERE FileName = '@_RampSource.wav'";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		if (rs.getCount() == 0 && Main.isDebug == true) {
			AudioRecorder ar = new AudioRecorder();
			ar.buildRampFile();
		}
		rs.close();
        list = (ListView) findViewById(R.id.list);  // list is in song_list.xml
        adapter = new SongAdapter(this, Main.songsCombined);  // fileName on one line and Spec Inx Seg on second line
        list.setAdapter(adapter);
        list.setFastScrollEnabled(true);
       	Main.songStartAtLoc = 0;
       	Main.songStopAtLoc = 0;   	
       	Main.isNewStartStop = false;
       	Main.showPlayFromList = false;
		Log.d(TAG, "onCreate path:" + Main.path );
		Button rename=(Button)findViewById(R.id.rename_button);
		Button delete=(Button)findViewById(R.id.delete_button);
		rename.setOnClickListener(listener);
		delete.setOnClickListener(listener);
		Button play=(Button)findViewById(R.id.play_button);
		play.setOnClickListener(listener);
		if (Main.fileRenamed == true || Main.fileReshowExisting == true) {
			list.setSelection(Main.listOffset-1);
		}
		Main.fileRenamed = false;
		Main.fileReshowExisting = false;
    } // onCreate

	public boolean onCreateOptionsMenu() {
		final MenuInflater menuInflater = getMenuInflater();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu from the resources by using the menu inflater.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_metadata:
				try {
					showMetaData();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case R.id.menu_share:
				emailFile();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	void buildList() {
    	char q = 34;
		InputStream is;
		Log.d(TAG, "buildList:" + songPath );		// crash if songPath is null		
        int pathLen = songPath.length();
        File dir = new File(songPath);
        if (dir.exists() == false) {
        	String msg = "Your song path:" + songPath + " is invalid";
        	Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            Log.d(TAG, msg);
			finish();
        	return;
        } 
       	Log.d(TAG, "buildList: dir:" + dir);
    	Main.songFile = dir.listFiles();
    	if (Main.songFile == null) {    		
    		Log.d(TAG, "onCreate songFile is null -- closing" );        
        	String msg = "invalid path:" + songPath;
        	Log.d(TAG, msg);
     	   	finish();
			return;
    	}
		Log.d(TAG, "onCreate songFile is not null -- length:" + Main.songFile.length );
		String z = "zzzzzzzz";
		ContentValues val = new ContentValues();
		qry = "SELECT FileName FROM SongList" +
			" WHERE FileName = '" + z + "'"; 
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		if (rs.getCount() == 0) {
			Main.db.beginTransaction();
			val.put("Ref", 39997);
			val.put("Inx", 1);
			val.put("Seg", 0);
			val.put("Path", Main.path);				
			val.put("FileName", z);
			val.put("Start", 0);
			val.put("Stop", 0);
			val.put("Identified", 0);  
			val.put("Defined", 0);
   			val.put("AutoFilter", 0);
   			val.put("Enhanced", 0);
   			val.put("Smoothing", 0);
   			val.put("SourceMic", 0);
            val.put("SampleRate", 0);
            val.put("AudioSource", 0);
   			val.put("LowFreqCutoff", 0);
   			val.put("HighFreqCutoff", 0);
   			val.put("FilterStart", 0);
   			val.put("FilterStop", 0);
   			Main.db.insert("SongList", null, val);
   			Main.db.setTransactionSuccessful();
   			Main.db.endTransaction();
   			val.clear();
		}
		rs.close();
		qry = "SELECT FileName FROM SongList" +
				" WHERE Path = " + Main.path +
				" GROUP BY FileName" + 
    			" ORDER BY FileName";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		songsDbLen = rs.getCount();  // count (indexed 0 to < count
		songsFileLen = Main.songFile.length;
		Log.d(TAG, "buildList songsDbLen:" + songsDbLen + " songsFilesLen:" + songsFileLen );
		songsLen = Math.max(songsDbLen, songsFileLen);  // for dim of arrays
		songsLen += 20;  // save more than enough room for additions.		
    	Main.songs = new String[songsLen]; 
    	Log.d(TAG, "START Update database isFilterExists:" + Main.isFilterExists );
    	for (int i=0; i<songsFileLen; i++) {    		
    		Main.songs[i] = Main.songFile[i].toString().substring(pathLen);
			Log.d(TAG, "buildList songs[" + i + "] " + Main.songs[i]);
    	}
    	for (int i=songsFileLen; i<songsLen; i++) {    		
    		Main.songs[i] = z;
    	}
    	Arrays.sort(Main.songs);
		int ifile = 0;
		int ref = 0;
		String atSign = null;
		int sourceMic = 0;
		int maxInx = 0;
		// remember: there are duplicate names in the database file=xxx seg=0; file=xxx seg=1
		if (songsDbLen == 0) {
			Log.d(TAG, "EMPTY Database" );
			for (ifile = 0; ifile < songsFileLen; ifile++) {
				Log.d(TAG, "Adding '" + Main.songs[ifile] + "' to the SongList ref:" + ref);
				atSign = Main.songs[ifile].substring(0,1);  
				sourceMic = 0; // default preRecorded
				filtLow = 0;
				filtHi = 0;
				filtBeg = 0;
				filtEnd = 0;
				if (atSign.equals("@")) { // song starts with "@_date ...
					int lenExist = Main.songs[ifile].length();
					String ext = Main.songs[ifile].substring(lenExist-4); // the extension ".m4a" or ".wav"
                    // 0=pre-recorded 1=internal.wav, 2=internal.m4a, 3=external.wav, 4=external.m4a
					if (ext.equals(".wav")) {
						sourceMic = 1;
					} else { // m4a
						sourceMic = 2;				
					}
                    if (Main.isExternalMic == true) {
                        sourceMic += 2;
                    }
					ref = 0;
				} else {
					ref = tryForSpec(Main.songs[ifile]);  // the file name				
					if (Main.isFilterExists = true) {
						checkForFilter(Main.songs[ifile]);
					}
				}
				if (ref == 0) {
					maxInx = 0;
				} else {
					qry = "SELECT MAX(Inx) AS MaxInx FROM SongList" +
						" WHERE Ref = " + ref;
					rsCk = Main.songdata.getReadableDatabase().rawQuery(qry, null);
					rsCk.moveToFirst();
					maxInx = rsCk.getInt(0)+1;  // increment the last known inx
					rsCk.close();
				}

				Main.db.beginTransaction();
				val = new ContentValues();
				val.put("Ref", ref);
				val.put("Inx", maxInx);
				val.put("Seg", 0);
				val.put("Path", Main.path);				
				val.put("FileName", Main.songs[ifile]);
				val.put("Start", 0);
				val.put("Stop", 0);
				val.put("Identified", 0);  
				val.put("Defined", 0);
           	   	val.put("AutoFilter", 0);
				val.put("Enhanced", 0);
	   			val.put("Smoothing", 0);
				val.put("SourceMic", sourceMic);
                val.put("SampleRate", 4);  // 0=22050, 1=44100 2=24000, 3=48000, 4=unknown
                val.put("AudioSource", Main.audioSource); // Use existing -- 0=default, 1=mic, 5=camcorder, 6 voice recognition
                val.put("LowFreqCutoff", filtLow);
				val.put("HighFreqCutoff", filtHi);
				val.put("FilterStart", filtBeg);
				val.put("FilterStop", filtEnd);
				Main.db.insert("SongList", null, val);
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
				val.clear();
			} // next ifile
		} else { // not empty database
			rs.moveToFirst();
			int rsLen = rs.getCount();
			while (!rs.isAfterLast()) {
				String nam = rs.getString(0);
				int result = nam.compareTo(Main.songs[ifile]); // -1 if file < dbName, 0 if dbName = file, +1 if file > dbName
				//Log.d(TAG, "  " + Main.songs[ifile] + "<-file " + result + " db->" + nam );
				if (result < 0) { // file < dbName -- so the dbName needs to be deleted
					// this is deleting from the song list as always to keep the song files and database in sync
					// when this code is executed the file HAS ALREADY BEEN deleted. -- this deletes the record in the database.
					// when Delete option within SongList is executed the file and record are both deleted and this code is not run.
					Log.d(TAG, "Deleting " + nam + " from SongList");
					Main.db.beginTransaction();
						qry = "DELETE FROM SongList" +
								" WHERE Path = " + Main.path +
								" AND FileName = " + q + nam + q;
						Main.db.execSQL(qry);
						Main.db.setTransactionSuccessful();
						Main.db.endTransaction();
					rs.moveToNext();
				}
				if (result == 0) { // do nothing (except keep the files in sync)
					rs.moveToNext();
					ifile++;
				}
				if (result > 0) {  // file > dbName name -- so the song needs to be added
					//Log.d(TAG, "Adding '" + Main.songs[ifile] + "' to the SongList");
					atSign = Main.songs[ifile].substring(0,1);  
					sourceMic = 0; // default preRecorded
					filtLow = 0;
					filtHi = 0;
					filtBeg = 0;
					filtEnd = 0;
					filtStrt = 0;
					filtStop = 0;
					if (atSign.equals("@")) { // song starts with "@_date ...
                        int lenExist = Main.songs[ifile].length();
                        String ext = Main.songs[ifile].substring(lenExist-4); // the extension ".m4a" or ".wav"
                        // 0=pre-recorded 1=internal.wav, 2=internal.m4a, 3=external.wav, 4=external.m4a
                        if (ext.equals(".wav")) {
                            sourceMic = 1;
                        } else { // m4a
                            sourceMic = 2;
                        }
                        if (Main.isExternalMic == true) {
                            sourceMic += 2;
                        }
						ref = 0;
					} else {
						ref = tryForSpec(Main.songs[ifile]);  // the file name
						if (Main.isFilterExists = true) {
							checkForFilter(Main.songs[ifile]);
						}
					}
					if (ref == 0) {
						maxInx = 0;
					} else {
						qry = "SELECT MAX(Inx) AS MaxInx FROM SongList" +
							" WHERE Ref = " + ref;
						rsCk = Main.songdata.getReadableDatabase().rawQuery(qry, null);
						rsCk.moveToFirst();
						maxInx = rsCk.getInt(0)+1;  // increment the last known inx
						rsCk.close();
					} 
					
	   	            try {        	
	   	            	Log.d(TAG, "db begin transaction -- adding file:" + Main.songs[ifile]);
	   	            	Main.db.beginTransaction();
   	            		val = new ContentValues();
	   	            	try {
	   	            		val.put("Ref", ref);
	   	            		val.put("Inx", maxInx);
	   	            		val.put("Seg", 0);
	   	            		val.put("Path", Main.path);
	   	            		val.put("FileName", Main.songs[ifile]);
	   	            		val.put("Start", 0);
	   	            		val.put("Stop", 0);
	   	            		val.put("Identified", 0);  
	   	            		val.put("Defined", 0);
	   	            	   	val.put("AutoFilter", 0);
	   	            		val.put("Enhanced", 0);
	   	        			val.put("Smoothing", 0);
	   	            		val.put("SourceMic", sourceMic);
                            val.put("SampleRate", Main.sampleRateOption);  // 0=22050, 1=44100
                            val.put("AudioSource", Main.audioSource); // 0=default, 1=mic, 5=camcorder, 6 voice recognition
	   	            		val.put("LowFreqCutoff", filtLow);
	   	            		val.put("HighFreqCutoff", filtHi);
	   	            		val.put("FilterStart", filtBeg);
	   	            		val.put("FilterStop", filtEnd);
	   	            		Main.db.insert("SongList", null, val);
	   	            		Main.db.setTransactionSuccessful();					
	   	            	} finally {
	   	            		Main.db.endTransaction();
	   	            		val.clear();
	   	            		//Log.d(TAG, "db end transaction");
	   	            	}
	   	            } catch( Exception e ) {
	   	            	Log.e(TAG, "Database Exception: " + e.toString() );     	    
	   	            }
					if (filtStrt > 0 || filtStop > 0) {
						Log.d(TAG, "Start from XC file:" + filtStrt + " Stop:" + filtStop);
						qry = "SELECT MAX(Seg) AS MaxSeg FROM SongList" +
								" WHERE Ref = " + ref + " AND Inx = " + maxInx;
						rsCk = Main.songdata.getReadableDatabase().rawQuery(qry, null);
						rsCk.moveToFirst();
						int maxSeg = rsCk.getInt(0)+1;  // increment the last known seg
						rsCk.close();
						try {
							//Log.d(TAG, "db begin transaction");
							Main.db.beginTransaction();
							val = new ContentValues();
							try {
								val.put("Ref", ref);
								val.put("Inx", maxInx);
								val.put("Seg", maxSeg);
								val.put("Path", Main.path);
								val.put("FileName", Main.songs[ifile]);
								val.put("Start", filtStrt);
								val.put("Stop", filtStop);
								val.put("Identified", 0);
								val.put("Defined", 0);
								val.put("AutoFilter", 0);
								val.put("Enhanced", 0);
								val.put("Smoothing", 0);
								val.put("SourceMic", sourceMic);
								val.put("SampleRate", Main.sampleRateOption);  // 0=22050, 1=44100
								val.put("AudioSource", Main.audioSource); // 0=default, 1=mic, 5=camcorder, 6 voice recognition
								val.put("LowFreqCutoff", 0);
								val.put("HighFreqCutoff", 0);
								val.put("FilterStart", 0);
								val.put("FilterStop", 0);
								Main.db.insert("SongList", null, val);
								Main.db.setTransactionSuccessful();
							} finally {
								Main.db.endTransaction();
								val.clear();
								Log.d(TAG, "new record from filter XC start and stop");
							}
						} catch( Exception e ) {
							Log.e(TAG, "failed to add record from XC filter Start Stop: " + e.toString() );
						}

					}
					// don't increment database let the file catch up
					ifile++;
				}
			} // while
			
		} // else database has data 
    	Log.d(TAG, "END Update database");
		// at this point the database names match the file names
		// now build the list which includes the location and options
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

		// build the list from the latest database
		qry = "PRAGMA case_sensitive_like = 1";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);

		qry = "SELECT FileName, CodeName.Spec, CodeName.Ref, Inx, Seg, Identified, Defined, AutoFilter, Enhanced, Smoothing," +
                " SourceMic, SampleRate, AudioSource," +
				" CodeName.Region, CodeName.SubRegion, CodeName.RedList," +
				" LowFreqCutoff, HighFreqCutoff, FilterStart, FilterStop" +
				" FROM SongList JOIN CodeName ON SongList.Ref = CodeName.Ref" + 
				" WHERE path = " + Main.path ;
		if (cntr == 0) {
			qry += " AND CodeName.Region = 'none'";
		} else {
			int oneTime = 0;
			for (int i = 0; i< cntr; i++) {
				if (oneTime == 0) {
					qry += " AND (CodeName.Region like '%" + area[i] + "%'";
					oneTime++;
				} else {
					qry += " OR CodeName.Region like '%" + area[i] + "%'";
				}
				if (area[i].equals("Worldwide")) {
					qry += " OR CodeName.SubRegion like '%introduced worldwide%'";
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
				qry += " ORDER BY FileName";
			} else {
				qry += " ORDER BY CodeName.Ref";
			}
		}
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		songsDbLen = rs.getCount(); // no extra 20 now
		Main.songsDbLen = songsDbLen;
		songsLen = songsDbLen + 20;
		Main.songs = new String[songsLen];
		Main.songsCombined = new String[songsLen];
		Main.ref = new int[songsLen];
		Main.inx = new int[songsLen];
		Main.seg = new int[songsLen];
		Main.ck = new Boolean[songsLen];
		Main.selectedSong = new int[songsLen];
		rs.moveToFirst();
		for (int i=0; i<songsDbLen; i++) {
			Main.songs[i] = rs.getString(0);  // the file name
			Main.ref[i] = rs.getInt(2);
			Main.inx[i] = rs.getInt(3);
			Main.seg[i] = rs.getInt(4);
			int iref = Main.ref[i];
			int iden = rs.getInt(5); // identified (set if Identified) only from play song id button
			int idef = rs.getInt(6); // Defined
			int iaut = rs.getInt(7); // AutoFilter
			int ienh = rs.getInt(8); // Enhanced
			int ismo = rs.getInt(9); // Smoothing
			int imic = rs.getInt(10); // SourceMic  0=pre-recorded (.) 1=internal.wav (m), 2=internal.m4a (m), 3=external.wav (x), 4=external.m4a (x)
            int isrt = rs.getInt(11); // SampleRate 0=22050, 1=44100, 2=24000, 3=48000, 4=unknown
            int iaud = rs.getInt(12); // AudioSource 0,1,5, or 6
			int if1 = rs.getInt(16); //  lowFreqCutoff
			int if2 = rs.getInt(17); //  highFreqCutoff
			int if3 = rs.getInt(18); // filterStart
			int if4 = rs.getInt(19); // filter Stop
			String def = " ";

			// SOURCE
            switch (imic) { // first . microphone
                case 0: {
                    def = def + ".";  // pre-recorded
                    iaud = 0;
                    break;
                }
                case 1:
                case 2: {
                    def = def + "m"; // internal
                    break;
                }
                case 3:
                case 4: {
                    def = def + "x"; // external
                    break;
                }
            }
            switch(isrt) { // second . sample rate
                case 0: {
                    def = def + "0";  // 0 = 22050
                    break;
                }
                case 1: {
                    def = def + "1"; // 1 = 44100
                    break;
                }
				case 2: {
					def = def + "2";  // 0 = 24000
					break;
				}
				case 3: {
					def = def + "3"; // 1 = 48000
					break;
				}
				case 4: {
					def = def + "."; // unknown
					break;
				}
            }
            switch(iaud) { // third . audio source
                case 0: {
                    def = def + ".";  // the default was used
                    break;
                }
                case 1: {
                    def = def + "m";  // the microphone was used
                    break;
                }
                case 5: {
                    def = def + "c";  // the camcorder was used
                    break;
                }
                case 6: {
                    def = def + "v";  // the voice recognition was used
                    break;
                }
			}

			// PROCESS
			if (if1 > 0 || if2 > 0 || if3 > 0 || if4 > 0) { /// fourth . Filter  -- low freq, high freq, begin end noise
				def = def + "f"; // manual filter exists
			} else if (iaut == 1) {
				def = def + "a";
			} else {
				def = def + ".";  // not used
			}
			if (ienh == 0) { // fifth . enhanced
				def = def + ".";  // not processed
			} else {
				def = def + "e";  // use digital filter
			}
			if (ismo == 0) { // sixth . smoothing
				def = def + ".";  // not processed
			} else {
				def = def + "s";  // use smoothing
			}

			// RESULT
			if (iden == 0) {  // seventh identified set Identified to 0 to remove "i" from SongList
				def = def + "."; // not identified
			} else if (iden == 1) {
				def = def + "i"; // identified
			} else if (iden > 1) {
				def = def + "?";  // rejected identification  the identifiedRef didn't match the definedRef
			}
			if (idef == 0) { // eighth . the bird has a species and has been analyzed and saved  -- set Defined to 0 to remove "d" from the SongList
				def = def + ".";  // not defined
			} else {
				def = def + "d";  // defined
			}
			if (Main.fileRenamed == true) {
				if (foundRenamed == false) {
					if (Main.existingName == null || Main.songs == null) {
						Main.fileRenamed = false;
						finish();
						return;
					}
					if (Main.existingName.equals(Main.songs[i])) {
						Main.listOffset = i;
						foundRenamed = true;
					}
				}
			}

			if (Main.fileReshowExisting == true) {
				if (foundExisting == false) {
					if (Main.existingName == null || Main.songs == null) {
						Main.fileReshowExisting = false;
						finish();
						return;
					}
					if (Main.existingName.equals(Main.songs[i])) {
						Main.listOffset = i;
						foundExisting = true;
					}
				}
			}
			String specInxSeg = rs.getString(1) + "_" + rs.getInt(3) + "." + rs.getInt(4) + " " + rs.getString(15);  // Species name_1.0 RedList
			Main.songsCombined[i] = rs.getString(0) + def + "\n\t" + specInxSeg + "\n\t" + rs.getString(13) + " : " + rs.getString(14); // fileName newline specIncSeg
			Main.ck[i] = false;
			rs.moveToNext();
		}
		qry = "PRAGMA case_sensitive_like = 0";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.close();
		
    } // buildList
    
    int tryForSpec(String filname) {
    	char q = 34;
    	int ref = 0;  // unknown
//    	Log.d(TAG, "try for spec filname:" + filname);    	
    	int fillen; // the length of the common name from the database
		fillen = filname.length(); // the length of the common name from the database
    	String fil3 = filname.substring(0,3);  // the name can be as short as 3 before blanks
    	qry = "SELECT Ref, CommonName FROM CodeName WHERE CommonName LIKE " + q + fil3 + "%" + q; // don't look at all of them
		rsCk = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		int cntr = rsCk.getCount();
    	if (cntr == 0) {
        	Log.d(TAG, "returning ref:" + ref + " Unknown");
        	rsCk.close();
    		return ref;  // Unknown
    	}
		rsCk.moveToFirst();
    	for (int i = 0; i < cntr; i++) {
    		String comname = rsCk.getString(1);  // common name from the database
    		int comlen = comname.length(); // the length of the common name from the database
    		if (comlen < fillen) { // the database name is shorter than the file name - continue ( file has .wav or .m4a)
	        	//Log.d(TAG, "filname:" + filname + " comname:" + comname);
    			String filcheck = filname.substring(0,comlen); // make them the same length
    			if (comname.equalsIgnoreCase(filcheck)) {  
    				ref = rsCk.getInt(0);
    	        	//Log.d(TAG, "returning ref:" + ref + " comname:" + comname);
    	        	rsCk.close();
    				return ref; // the reference (if you get lucky)
    			}
    		}
    		rsCk.moveToNext();
    	}
    	rsCk.close();
    	Log.d(TAG, "returning ref:" + ref + " Unknown");
		return ref; 
    }

	// Main.isFilterExist == true to get to here. -- filter is in the database
	public void checkForFilter(String filName) { // filter loaded at the time the file is loaded.
		Log.d(TAG, "checkForFilter filName:" + filName);
		int locStart = filName.indexOf("XC");
		if (locStart == -1) {
			return;
		}
		int locEnd = filName.indexOf(".",locStart);
		int locEndDash  = filName.indexOf("-",locStart); // in case it was loaded externally
		if (locEndDash > 2) {
			locEnd = Math.min(locEnd,locEndDash);
		}
		String xcNam = filName.substring(locStart, locEnd);
		Log.d(TAG, "checkForFilter xcNam:" + xcNam);
		qry = "SELECT XcName, FilterType, FilterVal FROM Filter WHERE XcName = '" + xcNam + "'";
		Cursor rsF = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		int cntr = rsF.getCount();
		if (cntr == 0) {
			rsF.close();
			return;  // no filter
		}
		rsF.moveToFirst();
		Log.d(TAG, "checkForFilter cntr:" + cntr);
		for (int f = 0; f < cntr; f++) {
			String typ = rsF.getString(1);
			if (typ.equals("LowFreqCutoff")) {
				filtLow = rsF.getInt(2);
			} else if (typ.equals("HighFreqCutoff")) {
				filtHi = rsF.getInt(2);
			} else if (typ.equals("FilterStart")) {
				filtBeg = rsF.getInt(2);
			} else if (typ.equals("FilterStop")) {
				filtEnd = rsF.getInt(2);
			} else if (typ.equals("Start")) {
				filtStrt = rsF.getInt(2);
			} else if (typ.equals("Stop")) {
				filtStop = rsF.getInt(2);
			}
			rsF.moveToNext();
		}
		rsF.close();
		Main.db.beginTransaction();
		qry = "DELETE FROM Filter WHERE XcName = '" + xcNam + "'";
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();
		return;
	}

    public OnClickListener listener = new OnClickListener() {  // for non-list items -- i.e. buttons -- see SongAdaptor for click on list
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rename_button:
                    Log.d(TAG, "*** 1 *** Rename clicked existingInx:" + Main.existingInx);
                    getNewName(); // I will now allow rename of files regardless of path
                    break;
                case R.id.delete_button:
                    Log.d(TAG, "Delete clicked");
                    deleteSelectedFile();  // I will now delete files regardless of path
                    break;
                case R.id.play_button:
                    Log.d(TAG, "Play clicked");
                    Main.showPlayFromList = true; // showPlayScreen();
                    finish();
                    break;
            } // switch
        } // onclick
    }; // onClickListener (because it does NOT extend OnClickListener)

    public void getNewName() {
        if (Main.existingName == null) {
            Log.d(TAG, "*** 1a *** getNewName existing name is null -- returning");
            Toast.makeText(this, "Please select a name to change.", Toast.LENGTH_LONG).show();
            return;
        }
        //    	  Main.newName = null; // clear it before you ask for new name.
        Log.d(TAG, "*** 2 *** existing name:" + Main.existingName + " existingRef:" + Main.existingRef + " existingInx:" + Main.existingInx);
        Main.newName = null;
        Main.newRef = 0;
        Intent nnd = new Intent(this, NewNameDialog.class);
        startActivityForResult(nnd, 1);  // request == 1
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Log.d(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);
        if (requestCode == 1) { // rename
			Log.d(TAG, "*** 6 *** onActivityResult requestCode:" + requestCode + " existingInx:" + Main.existingInx);
            Log.d(TAG, "*** 7 *** onActivityResult resultCode:" + resultCode);
            // Make sure the request was successful
            if (resultCode == 1) {
                // The user picked a FileName.
                Log.d(TAG, "*** 8 *** data:" + data);
                if (Main.newName == null) {
                    Log.d(TAG, "newName is null:");
                    return;
                } else {
                    Log.d(TAG, "*** 9 *** Main.newName: " + Main.newName);
                    renameFile();
                    Main.fileRenamed = true;
                    finish();
                    rs.close();
                }
            }
            if (resultCode == 0) {
                // The user canceled
                Log.d(TAG, "*** 8 *** data:" + data);
                Log.d(TAG, "new name canceled:");
                Main.fileRenamed = false;
                return;
            }

        }
		if (requestCode == 2) { // delete file(s)
			deleteOk(resultCode);
		}
		if (requestCode == 5) {  // meta data info box anything clicked
			return;
		}
    }

    public void renameFile() {
        char q = 34;
        if (Main.newName.equals(Main.existingName)) {
            Toast.makeText(this, "The File name didn't change", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Directory:" + songPath);
            File from = new File(songPath, Main.existingName);
            Log.d(TAG, "Rename from:" + from);
            File to = new File(songPath, Main.newName.trim());
            Log.d(TAG, "Rename   to:" + to);
            from.renameTo(to);  // rename the file
            // update the database
            try {
                Log.d(TAG, "db begin transaction");
                Main.db.beginTransaction();
                try {
                    qry = "UPDATE SongList " +
                            " SET FileName = " + q + Main.newName + q +
                            " WHERE FileName = " + q + Main.existingName + q +
                            " AND Path = " + Main.path;
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

        }
        // now check the spec
        Log.d(TAG, "*** 10 *** existingRef: " + Main.existingRef + " newRef:" + Main.newRef);
        if (Main.newRef != Main.existingRef) {
            // update the database
            int maxInx = 0;
            int maxSeg = 0;
            if (Main.newRef > 0) {
                qry = "SELECT MAX(Inx) AS MaxInx, MAX(Seg) AS MaxSeg FROM SongList" +
                        " WHERE Ref = " + Main.newRef;
                rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);

                if (rs.getCount() != 0) {
                    rs.moveToFirst();
                    maxInx = rs.getInt(0) + 1;  // increment the last known inx
                    Log.d(TAG, "*** 11 *** maxInx:" + maxInx + " maxSeg:" + maxSeg);
                }
                rs.close();
            }

            try {
                Log.d(TAG, "db begin transaction");
                Main.db.beginTransaction();
                try {
                    qry = "UPDATE SongList " +
                            " SET Ref = " + Main.newRef + ", " +
                            " Inx = " + maxInx + ", " +
                            " Seg = " + maxSeg +
                            " WHERE FileName = " + q + Main.newName + q +
                            " AND Path = " + Main.path +
                            " AND Ref = " + Main.existingRef +
                            " AND Inx = " + Main.existingInx +
                            " AND Seg = " + Main.existingSeg;
                    Log.d(TAG, "qry:" + qry);
                    Main.db.execSQL(qry);
                    if (Main.existingRef > 0) {
                        // I have to query to see if existing is in totals before I attempt update
                        qry = "SELECT Ref FROM DefineTotals" +
                                " WHERE Ref = " + Main.existingRef +
                                " AND Inx = " + Main.existingInx +
                                " AND Seg = " + Main.existingSeg;
                        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        if (rs.getCount() > 0) {
                            qry = "UPDATE DefineTotals SET Ref = " + Main.newRef +
                                    " WHERE Ref = " + Main.existingRef +
                                    " AND Inx = " + Main.existingInx +
                                    " AND Seg = " + Main.existingSeg;
                            Main.db.execSQL(qry);
                        }
                        rs.close();
                        qry = "SELECT Ref FROM DefineDetail" +
                                " WHERE Ref = " + Main.existingRef +
                                " AND Inx = " + Main.existingInx +
                                " AND Seg = " + Main.existingSeg;
                        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        if (rs.getCount() > 0) {
                            qry = "UPDATE DefineDetail SET Ref = " + Main.newRef +
                                    " WHERE Ref = " + Main.existingRef +
                                    " AND Inx = " + Main.existingInx +
                                    " AND Seg = " + Main.existingSeg;
                            Main.db.execSQL(qry);
                        }
                        rs.close();
                    } // existing Ref > 0
                } finally {
                    Main.db.setTransactionSuccessful();
                    Main.db.endTransaction();
                    Log.d(TAG, "db end transaction");
                }
            } catch (Exception e) {
                Log.e(TAG, "Database Exception: " + e.toString());
            }

        }
    }

    private void deleteSelectedFile() {
		if (Main.songCounter > 0) {
			Main.alertRequest = 2; // delete selected files
			Intent a3b = new Intent(this, Alert3ButtonDialog.class);
			startActivityForResult(a3b, Main.alertRequest);  // request == 2 == delete selected files
		} else {
			Toast.makeText(this, "Nothing selected to delete.", Toast.LENGTH_LONG).show();
		}
    } // deleteSelectedFile


    private void deleteOk(int id) {
        char q = 34;
        Log.d(TAG, "deleteOk option:" + id);
        if (id == 2) { // 2 = no - cancel
            Toast.makeText(this, "Delete Canceled", Toast.LENGTH_LONG).show();
            return; // cancel
        }

        Cursor rsDel = null;
        for (int i = 0; i < Main.songsDbLen; i++) {
            if (Main.ck[i] == true) {
                Main.existingName = Main.songs[i];
                Main.existingRef = Main.ref[i];
                Main.existingInx = Main.inx[i];
                Main.existingSeg = Main.seg[i];
                qry = "SELECT count(*) FROM SongList" +
                        " WHERE FileName = " + q + Main.existingName + q +
                        " AND Inx = " + Main.existingInx +
                        " AND Seg = " + Main.existingSeg +
                        " AND Path = " + Main.path;
                Log.d(TAG, "count qry:" + qry);
                rsDel = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                rsDel.moveToFirst();
                int cntr = rsDel.getInt(0);
                Log.d(TAG, "deleteSelectedFile count potential files:" + cntr);
                rsDel.close();
                try {
                    Log.d(TAG, "db begin transaction");
                    Main.db.beginTransaction();
                    try {
                        if (id == 0 || id == 1) { // 0= file and definition 1 = definition only
                            Log.d(TAG, "delete definition with ref:" + Main.existingRef + " inx:" + Main.existingInx + " seg:" + Main.existingSeg);
                            qry = "DELETE FROM DefineTotals" +
                                    " WHERE Ref = " + Main.existingRef +
                                    " AND Inx = " + Main.existingInx +
                                    " AND Seg = " + Main.existingSeg;
                            Main.db.execSQL(qry);
                            qry = "DELETE FROM DefineDetail" +
                                    " WHERE Ref = " + Main.existingRef +
                                    " AND Inx = " + Main.existingInx +
                                    " AND Seg = " + Main.existingSeg;
                            Main.db.execSQL(qry);
                            if (Main.existingSeg > 0) { // remove the segment
                                qry = "DELETE FROM SongList" +
                                        " WHERE FileName = " + q + Main.existingName + q +
                                        " AND Inx = " + Main.existingInx +
                                        " AND Seg = " + Main.existingSeg +
                                        " AND Path = " + Main.path;
                                Main.db.execSQL(qry);
                            }
                            if (id == 1) {
                                qry = "UPDATE SongList " +
                                        " SET Defined = 0" +
                                        " WHERE FileName = " + q + Main.existingName + q +
                                        " AND Path = " + Main.path +
                                        " AND Ref = " + Main.existingRef +
                                        " AND Inx = " + Main.existingInx +
                                        " AND Seg = " + Main.existingSeg;
                                Log.d(TAG, "qry:" + qry);
                                Main.db.execSQL(qry);
                            }
                        } // definition
                    } finally {
                        Main.db.setTransactionSuccessful();
                        Main.db.endTransaction();
                        Log.d(TAG, "db end transaction");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Database Exception: " + e.toString());
                }

                boolean deleted = false;
                if (id == 0) { // 0 = file and definition
                    if (cntr == 1 && Main.existingSeg == 0) { // I counted it before i deleted it
                        Log.d(TAG, "deleteFile name:" + Main.songpath + Main.existingName);
                        File file = new File(Main.songpath + Main.existingName);
                        deleted = file.delete();
                    }
                    Log.d(TAG, "deleteFile was file deleted?" + deleted);
                }
            }
        } // next i
        Main.fileReshowExisting = true;
        rsDel.close();
        //db.close();
        finish();
    } // deleteOk

	void showMetaData() throws IOException {
		if (Main.existingName == null) {
			Toast.makeText(this, "Please select a song first.", Toast.LENGTH_LONG).show();
			return;
		}
		Main.metaData = "";
		String filePath = Main.songpath + Main.existingName;
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(filePath);
		if (filePath.indexOf(".mp3") > 0 || filePath.indexOf(".ogg") > 0 || filePath.indexOf(".wav") > 0 ) {
			Main.metaData = "ALBUM:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) + "\n";
			Main.metaData += "ARTIST:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + "\n";
			Main.metaData += "BITRATE:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) + "\n";
			Main.metaData += "DURATION:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) + "\n";
			Main.metaData += "GENRE:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) + "\n";
			Main.metaData += "MIMETYPE:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) + "\n";
			Main.metaData += "NUM_TRACKS:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS) + "\n";
			Main.metaData += "KEY_TITLE:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) + "\n";
			//Log.d(TAG, "mp3 metadata:" + Main.metaData);
		}

		if (filePath.indexOf(".m4a") > 0) {
			// [4 bytes atom length] [4 bytes atom name] [4 bytes len] [4 bytes "data"] [4 len of data] [data] [contents of the atom, if any]
			// 3B,"©nam",33,"data",1,0,"American ...." (length 33-12)
			metaBuffer = new byte[512];
			File selected = new File(Main.songpath + Main.existingName);
			Long startAt = selected.length()-512;
			readFile(selected, startAt);
			char[] hexArray = "0123456789ABCDEF".toCharArray();
			char[] hexChars = new char[metaBuffer.length * 2];
			StringBuilder metaHex = new StringBuilder();
			for ( int j = 0; j < metaBuffer.length; j++ ) {
				int v = metaBuffer[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
				metaHex.append(hexChars[j*2]);
				metaHex.append(hexChars[j*2+1]);
//					Log.d(TAG, "m4a hexChars at:" + j + " " + hexChars[j*2] + hexChars[j*2+1]);
			}
//			Log.d(TAG, "read hexChars:" + metaHex);
			StringBuilder meta = new StringBuilder();
			for (int i = 0; i < metaHex.length(); i+=2) {
				String str = metaHex.substring(i, i+2);
				meta.append((char)Integer.parseInt(str, 16));
			}
			Log.d(TAG, "m4a metaHex.length():" + metaHex.length());
			Log.d(TAG, "m4a meta.length():" + meta.length());
			int i = meta.indexOf("meta");
			Log.d(TAG, "m4a meta at:" + i);
			i = meta.indexOf("©alb");
			if (i > 0) {
				int len = Integer.parseInt(metaHex.substring(i * 2 + 10, i * 2 + 16),16);
				Log.d(TAG, "m4a ©alb at:" + i + " len:" + len);
				Main.metaData += "ALBUM:" + meta.substring(i + 12, i + len + 4) + "\n";
			}
			String nam = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			if (nam != "") {
				Main.metaData += "KEY_TITLE:" + nam + "\n";
			} else {
				i = meta.indexOf("©nam");
				if (i > 0) {
					int len = Integer.parseInt(metaHex.substring(i * 2 + 10, i * 2 + 16),16);
					Log.d(TAG, "m4a ©nam at:" + i + " len:" + len);
					nam = meta.substring(i + 12, i + len + 4);
					Main.metaData += "NAME:" + nam + "\n";
				}
			}
			Main.metaData += "DURATION:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) + "\n";
			Main.metaData += "MIMETYPE:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) + "\n";
			Main.metaData += "NUM_TRACKS:" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS) + "\n";
			i = meta.indexOf("©cmt");
			if (i > 0) {
				int len = Integer.parseInt(metaHex.substring(i * 2 + 10, i * 2 + 16),16);
				Log.d(TAG, "m4a ©alb at:" + i + " len:" + len);
				Main.metaData += "COMMENT:" + meta.substring(i + 12, i + len + 4) + "\n";
			}
			i = meta.indexOf("©gen");
			if (i > 0) {
				int len = Integer.parseInt(metaHex.substring(i * 2 + 10, i * 2 + 16), 16);
				Log.d(TAG, "m4a ©gen at:" + i + " len:" + len);
				Main.metaData += "GENUS:" + meta.substring(i + 12, i + len + 4) + "\n";
			}
			//Log.d(TAG, "m4a metadata:" + Main.metaData);

		}
		if (Main.metaData == "") {
			Main.metaData = "Meta Data NOT available";
		}

		Main.alertRequest = 5; // meta data info box
		Intent mdib = new Intent(this, Alert1ButtonDialog.class);
		startActivityForResult(mdib, Main.alertRequest);  // request == 5 == show meta data info box
	}

	public void readFile(File file, Long startAt) throws IOException {
		metaBuffer = new byte[512];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			ios.skip(startAt);
			if (ios.read(metaBuffer) == -1) {
				throw new IOException(
						"EOF reached while trying to read the whole file");
			}
		} finally {
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}
	}

	void emailFile() {
		Log.d(TAG, "emailFile");
		if (Main.existingName == null) {
			Toast.makeText(this, "Please select a song first.", Toast.LENGTH_LONG).show();
			return;
		}

		//emailIntent = new Intent(Intent.ACTION_SEND);
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
		//emailIntent.setType(HTTP.PLAIN_TEXT_TYPE); // fails
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""}); // recipients
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, Main.existingName);
		emailIntent.putExtra(Intent.EXTRA_TEXT, Main.songsCombined[Main.listOffset] + "\n");
		File file = new File(Main.songpath + Main.existingName);
		Uri uri = Uri.fromFile(file);
		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivityForResult(emailIntent, 1);
	}

	@Override
    public void onDestroy() {
        list.setAdapter(null);
        super.onDestroy();
    }

} // SongList

