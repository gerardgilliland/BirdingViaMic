package com.modelsw.birdingviamic;
/*
 * 49.Q before cleanup and export to GitHub
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.media.AudioManager;
import android.os.Environment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class Main extends AppCompatActivity implements OnClickListener {
	private static final String TAG = "Main";
	public static String adjustViewOption;
	public static int[] adLoc;
	public static int adLocCntr = 0;
	public static int HELP = 0;
	public static int ADS = 1;
	public static int SONG_PATH = 2;
	public static int WEB_LIST = 3;
	public static int MY_LIST = 4;
	public static int REGION = 5;
	public static int RED_LIST = 6;
	public static int LOCATION = 7;
	public static int OPTIONS = 8;
	public static int SPECIES_LIST = 9;
	public static int SONG_LIST = 10;
	public static int MAIN = 11;
	public static int alertRequest = 0; // 2 = delete files; 3 = delete species
	public static short[] audioData;
	public static int audioDataLength;
	public static int audioDataSeek;
	public static int audioSource;
	public static int bitmapWidth;
	public static int bitmapHeight;
	public static int buttonHeight;
	public static Boolean[] ck;
	public static int countMic = 0;
	public static int cntrFftCall; // counts the blocks of 1024 fft data (512)
	public static int[] cntrFftCallTotal; // sum of power of each fft Block
	public static String commonName;
	public static String customPathLocation = null;
	public static String databaseName;
	public static int databaseVersion = 77;
	public static SQLiteDatabase db;
	public static String definepath = null;
	public static File definePathDir;
	public static String displayName = "";
	public static int duration;
	public static String environment = null;   // /storage/sdcard0
	public static int existingInx;
	public static String existingLink;
	public static String existingName;
	public static int existingRef;
	public static String existingRedList;
	public static String existingRegion;
	public static int existingSeg;
	public static String existingSpec;
	public static String existingSpecName;
	public static String existingSubRegion;
	public static String existingWebName;
	public static Boolean fileRenamed = false; // used in restart to refresh the songlist
	public static Boolean fileReshowExisting = false; // used in restart to refresh the songlist
	public static int fftcntr = 0; // pointer - increments for every line saved
	public static byte[] fftdata;
	public static float filterMaxPower;  // max of fft power calc between exclude filterStartAtLoc and filterStopAtLoc
	public static int filterStartAtLoc = 0;
	public static int filterStopAtLoc = 0;
	public static int highFreqCutoff = 0;  // user entered from adjust view
	public static float hzPerStep = 11025f / 512f;  // 21.53 hz per step
	public static int[] inx;
	public static boolean isAutoLocation = true;
	public static boolean isBatchDownload = false;
	public static boolean isBirder = false;
	public static boolean isDebug = false;
	public static boolean isEdit = false; // set true when edit button on play is tapped
	public static boolean isEditActive = false; // set true when inside the AdjustView screen
	public static boolean isEnhanceQuality = true; // build s/n kernel and apply to normalized audio
	public static boolean isExternalMic = false;
	public static boolean isFilterExists = false;
	public static Boolean isIdentify = true;
	public static boolean isLoadDefinition = false;  // (set in options) any checked in the list -- define if not mic -- identify if mic
	public static boolean isNewStartStop = false;
	public static boolean isOptionAutoFilter = true;  // find mean in Voiced overruled if manual filter
	public static boolean isPlaying = false;
	public static boolean isSampleRate = false;
	public static boolean isShowDetail = false;
	public static boolean isShowDefinition = false;  // Show the definition (frequency, distance) on the adjust view screen
	public static boolean isSortByName = true;
	public static boolean isShowAds = false;
	public static boolean isStartRecordScreen = false;
	public static boolean isStartRecording = false;
	public static boolean isShowWeb = false;
	public static boolean isUseAudioRecorder = false;
	public static boolean isUseLocation = false;
	public static boolean isUseSmoothing = false;
	public static boolean isViewDistance = true;
	public static boolean isViewEnergy = true;
	public static boolean isViewFrequency = true;
	public static boolean isViewQuality = true;
	public static boolean isWebLink = false;
	public static int latitude = 40;
	public static float lengthEachRecord = 5.0f; // number of records * lengthEachRecord = size required for dimension of bitmap
	private Button songButton;
	public static int listOffset = 0;
	public static int longitude = -100;
	public static int lowFreqCutoff = 0;  // user entered from adjust view
	public static int manualLat;
	public static int manualLng;
	public static float maxPower;  // max of fft power calc.
	public static int maxPowerJ = 0; // frequency where power is max (don't know if it is lo or hi for harmonics and percent peak)
	public static int maxPowerRec = 0; // record at which the max power occurred
	//public static Bitmap mCanvasBitmap;
	public static String metaData = null;
	public static int myRequest = 0;
	public static int myResult = 0;
	public static int myUpgrade = 0;
	public static String newLink;
	public static String newName;
	public static String newRedList;
	public static String newRegion;
	public static String newSubRegion;
	public static int newRef;
	public static String newSpec;
	public static String newSpecName;
	public static int newSpecRef;
	public static String newWebName;
	public static boolean optionsRead = false; // set true when read and in memory
	public static int path = 1;  // internal songs are default
	public static int phoneLat;
	public static int phoneLng;
	public String qry = "";
	private Button recordButton;
	public static String recordedName;
	public static int[] ref;  // reference (to replace defineName)
	private Cursor rs;
	public static int sampleRate = 22050;
	public static int sampleRateOption = 0;
	public static int[] scoreAd;
	public static int[] seg;
	public static int[] selectedSong;
	public String sharedStorage;
	public String sharedDefine;
	public static Boolean showPlayFromList = false;
	public static Boolean showPlayFromRecord = false;
	public static Boolean showWebFromIdentify = false;
	public static int songCounter = 0;  // count of songs selected (checked)
	public static String songpath = null;   // environment + /birdingviamic/Songs/ or environment + /iBird_Lite/ or custom
	public static String[] songs; // names from the file (to string)
	public static String[] songsCombined;  // the listing (fileName newLine Spec Inx Seg)
	public static SongData songdata;
	public static int songsDbLen; // count of songs in the SongList
	public static File[] songFile; // names from the file (file format)
	public static File songPathDir;
	public static int songStartAtLoc = 0;
	public static int songStopAtLoc = 0;
	public static int sourceMic = 0;
	public static int[] speciesRef;
	public static int specOffset = 0;
	public static Boolean specRenamed = false;
	public static int stopAt = 0;
	public static int thisSong = 0;  // current song
	public static int totalCntr = 0;  // I have to keep this for mediaPlayer and visualizerView
	Toolbar toolbar;
	public static int userRefStart = 40000;
	private int versionNum = 0;
	private String refUpgradeFile = "";
	private Button webButton;
	public static int webOffset = 0;
	public static Boolean webRenamed = false;
	public static boolean wikipedia = true;  // true show identified bird - false bring up a different web site 
	public static boolean xenocanto;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// action bar toolbar
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setLogo(R.drawable.treble_clef_linen);
		toolbar.setTitleTextColor(getResources().getColor(R.color.teal));
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Navigation Icon tapped");
			}
		});

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		findViewById(R.id.record_button).setOnClickListener(this);
		recordButton = (Button) findViewById(R.id.record_button);
		findViewById(R.id.play_button).setOnClickListener(this);
		findViewById(R.id.song_button).setOnClickListener(this);
		songButton = (Button) findViewById(R.id.song_button);
		findViewById(R.id.select_songpath_button).setOnClickListener(this);
		findViewById(R.id.options_button).setOnClickListener(this);
		findViewById(R.id.species_button).setOnClickListener(this);
		//findViewById(R.id.signin_button).setOnClickListener(this);
		findViewById(R.id.help_button).setOnClickListener(this);
		findViewById(R.id.redlist_button).setOnClickListener(this);
		findViewById(R.id.region_button).setOnClickListener(this);
		findViewById(R.id.location_button).setOnClickListener(this);
		findViewById(R.id.my_list_button).setOnClickListener(this);
		findViewById(R.id.web_browser_button).setOnClickListener(this);
		webButton = (Button) findViewById(R.id.web_browser_button);
		findViewById(R.id.register_button).setOnClickListener(this);
		environment = Environment.getExternalStorageDirectory().getAbsolutePath();
		String packageName = getPackageName(); // com.modelsw.birdingviamic
		songPathDir = getExternalFilesDir("Song"); // File
		definePathDir = getExternalFilesDir("Define"); // File
		definepath = definePathDir.toString() + "/"; // String
		databaseName = definepath + "BirdSongs.db";
		Log.d(TAG, "onCreate environment:" + environment + " songPathDir:" + songPathDir +
				" definePathDir:" + definePathDir + " databaseName:" + databaseName);
		// move the data from assets to definepath and songpath
		sharedStorage = Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName;
		// test in loadAssets and only load if missing
		Log.d(TAG, "make the Define directory");
		new File(definePathDir.toString()).mkdirs();
		Log.d(TAG, "go load the database");
		loadAssets("Define");

		Log.d(TAG, "make the Song directory");
		new File(songPathDir.toString()).mkdirs(); // doesn't do any harm if dir exists -- adds if missing
		Log.d(TAG, "go load the song files");
		loadAssets("Song");

		// I really have fixed the database leaking problem -- this is the ONLY NEW SongData in the WHOLE application
		songdata = new SongData(this, Main.databaseName, null, Main.databaseVersion);
		db = songdata.getWritableDatabase();
		// database loaded
		readTheSongPath();

		switch (path) {
			case 1: {
				songpath = Main.songPathDir.toString() + "/";
				break;
			}
			case 2: {
				songpath = environment + "/" + getResources().getString(R.string.path2_location) + "/";
				break;
			}
			case 3: {
				songpath = environment + "/" + getResources().getString(R.string.path3_location) + "/";
				sharedDefine = environment + "/" + getResources().getString(R.string.path3_define) + "/";
				break;
			}
			case 4: {
				songpath = environment + "/" + getResources().getString(R.string.path4_location) + "/";
				sharedDefine = environment + "/" + getResources().getString(R.string.path4_define) + "/";
				break;
			}
			case 5: {
				songpath = environment + "/" + getResources().getString(R.string.path5_location) + "/";
				sharedDefine = environment + "/" + getResources().getString(R.string.path5_define) + "/";
				break;
			}
			case 6: {
				songpath = environment + "/" + getResources().getString(R.string.path6_location) + "/";
				sharedDefine = environment + "/" + getResources().getString(R.string.path6_define) + "/";
				break;
			}
			case 7: {
				songpath = environment + "/" + customPathLocation + "/";
				break;
			}
		}
		Log.d(TAG, "onCreate definepath:" + definepath + " songpath:" + songpath);
		if (sharedDefine != null) {
			Log.d(TAG, "onCreate sharedDefine:" + sharedDefine);
		}

		readTheOptions();
		if (adLocCntr == 0) {
			adLocCntr = 12;
			adLoc = new int[adLocCntr];
			scoreAd = new int[adLocCntr];
		}
		commonName = "CommonName: ";

		// ****************************
		checkVersion();
		// ****************************

		readTheLocationFile();

		if (isBatchDownload == true && path > 1) {
			checkForNewFiles();
		}
		if (isStartRecordScreen == true) {
			recordButton.performClick();
		}
	}


	public void onClick(View v) {
		int resId;
		switch (v.getId()) {
			case R.id.record_button: {
				Log.d(TAG, "onClick Record");
				if (isUseAudioRecorder == true) {
					Intent ar = new Intent(this, AudioRecorder.class);
					startActivity(ar);
				} else {
					Intent rcs = new Intent(this, RecordSong.class);
					startActivity(rcs);
				}
				break;
			}
			case R.id.play_button: {
				Log.d(TAG, "onClick Play");
				wikipedia = true;
				xenocanto = false;
				Intent ps = new Intent(this, PlaySong.class);
				startActivity(ps);
				break;
			}
			case R.id.song_button: {
				buttonHeight = songButton.getHeight();
				Log.d(TAG, "onClick List buttonHeight:" + buttonHeight);
				wikipedia = true;
				xenocanto = false;
				isWebLink = false;
				existingRef = 0;
				Intent sl = new Intent(this, SongList.class);
				startActivity(sl);
				break;
			}
			case R.id.select_songpath_button: {
				Log.d(TAG, "onClick SongPath");
				Intent sl = new Intent(this, SelectSongPath.class);
				startActivity(sl);
				break;
			}
			case R.id.options_button: {
				Log.d(TAG, "onClick Options");
				Intent o = new Intent(this, Options.class);
				startActivity(o);
				break;
			}
			case R.id.species_button: {
				Log.d(TAG, "onClick Edit Species");
				wikipedia = false;
				xenocanto = true;
				isWebLink = false;
				existingRef = 0;
				Intent esd = new Intent(this, SpeciesList.class);
				startActivity(esd);
				break;
			}
			case R.id.web_browser_button: {
				Log.d(TAG, "onClick WebBrowser");
				if ((isShowWeb == true) && (wikipedia == true || xenocanto == true) && (existingRef > 0) && (existingRef < userRefStart)) {
					qry = "SELECT Spec FROM CodeName" +
							" WHERE Ref = " + existingRef;
					Cursor rs = songdata.getReadableDatabase().rawQuery(qry, null);
					rs.moveToFirst();
					existingSpec = rs.getString(0);
					rs.close();
					Log.d(TAG, "Start WebBrowser existingSpec:" + existingSpec);
					Intent wb = new Intent(this, WebBrowser.class);
					startActivity(wb);
				} else {
					if (isWebLink == true) {
						Log.d(TAG, "isWebLink = true Start WebBrowser");
						Intent wb = new Intent(this, WebBrowser.class);
						startActivity(wb);
					} else {
						Log.d(TAG, "Nothing selected Start WebList");
						wikipedia = false;
						xenocanto = false;
						Intent wb = new Intent(this, WebList.class);
						startActivity(wb);
					}
				}
				break;
			}

			case R.id.redlist_button: {
				Log.d(TAG, "onClick RedList");
				Intent red = new Intent(this, RedList.class);
				startActivity(red);
				break;
			}

			case R.id.region_button: {
				Log.d(TAG, "onClick Ads");
				Intent rl = new Intent(this, RegionList.class);
				startActivity(rl);
				break;
			}

			case R.id.location_button: {
				Log.d(TAG, "onClick Location");
				Intent sl = new Intent(this, ShowLocation.class);
				startActivity(sl);
				break;
			}

			case R.id.my_list_button: {
				Log.d(TAG, "onClick List");
				Intent ml = new Intent(this, MyList.class);
				startActivity(ml);
				break;
			}

			case R.id.register_button: {
				Log.d(TAG, "onClick register_button");
				wikipedia = false;
				xenocanto = false;
				Intent r = new Intent(this, Register.class);
				startActivity(r);
				break;
			}

			case R.id.help_button: {
				Log.d(TAG, "onClick Help");
				Intent h = new Intent(this, HelpActivity.class);
				startActivity(h);
				break;
			}

		} // switch
	} // onClick

	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		//db.close();

	}

	@Override
	protected void onResume() {
		super.onResume();


		Log.d(TAG, "onResume newStartStop:" + isNewStartStop);
		if (isNewStartStop == true) {
			isNewStartStop = false;
			Intent nss = new Intent(this, PlaySong.class);
			startActivity(nss);
		}

		Log.d(TAG, "onResume showPlayFromRecord:" + showPlayFromRecord);
		if (showPlayFromRecord == true) {
			showPlayFromRecord = false;
			// copy the recorded song into the existing name and start the PlaySong screen
			existingName = songpath + recordedName;
			Intent spfr = new Intent(this, PlaySong.class);
			startActivity(spfr);
		}

		Log.d(TAG, "onResume showPlayFromList:" + showPlayFromList);
		if (showPlayFromList == true) {
			showPlayFromList = false;
			// use the generated list of songs 
			Intent spfl = new Intent(this, PlaySong.class);
			startActivity(spfl);
		}

		Log.d(TAG, "onResume fileRenamed:" + fileRenamed);
		if (fileRenamed == true) {
			//fileRenamed = false;  will be cleared to false later in SongList
			Intent rn = new Intent(this, SongList.class);
			startActivity(rn);
		}

		Log.d(TAG, "onResume specRenamed:" + specRenamed);
		if (specRenamed == true) {
			//fileRenamed = false;  will be cleared to false later in SongList
			Intent sn = new Intent(this, SpeciesList.class);
			startActivity(sn);
		}

		Log.d(TAG, "onResume webRenamed:" + webRenamed);
		if (webRenamed == true) {
			//fileRenamed = false;  will be cleared to false later in SongList
			Intent wn = new Intent(this, WebList.class);
			startActivity(wn);
		}

		// keep this next to last -- if nothing else runs and return from play song then go back to the list where you were
		// I had to move it above web -- because web quit working -- web has to be last.
		Log.d(TAG, "onResume fileReshowExisting:" + fileReshowExisting + " existingName:" + existingName);
		if (fileReshowExisting == true) {
			//fileReshowExisting = false;  will be cleared to false later in SongList
			Intent rn = new Intent(this, SongList.class);
			startActivity(rn);
		}

		// keep this last or it won't work
		Log.d(TAG, "onResume showWebFromIdentify:" + showWebFromIdentify);
		if (showWebFromIdentify == true) {
			showWebFromIdentify = false;
			webButton.performClick();
		}
		// load single xc file from web
		if (Main.xenocanto == true && existingRef > 0 && isBatchDownload == false) {  // don't load XC files (from here) if startup
			environment = Environment.getExternalStorageDirectory().getAbsolutePath();
			String downloadPath = environment + "/Download/";
			Log.d(TAG, "*** onResume() XC downloadPath:" + downloadPath);        // crash if songPath is null
			int pathLen = downloadPath.length();
			File dir = new File(downloadPath);
			Log.d(TAG, "*** onResume() XC onCreate: dir:" + dir);
			Main.songFile = dir.listFiles();
			if (Main.songFile == null) {
				Log.d(TAG, "onCreate songFile is null -- closing");
				String msg = "invalid path:" + downloadPath;
				Log.d(TAG, msg);
				//finish();
			} else {
				int songsFileLen = Main.songFile.length;
				Log.d(TAG, "*** onResume() XC songFileLength:" + songsFileLen);        // crash if songPath is null
				Main.songs = new String[songsFileLen];
				for (int i = 0; i < songsFileLen; i++) {
					Main.songs[i] = Main.songFile[i].toString().substring(pathLen);
					if ((Main.songs[i].substring(0, 2).equals("XC")) || (Main.songs[i].substring(0, 8).equals("download"))) {
						qry = "Select CommonName from CodeName where Ref=" + existingRef;
						rs = songdata.getReadableDatabase().rawQuery(qry, null);
						rs.moveToFirst();
						commonName = rs.getString(0);
						int len = commonName.length();
						String filname = Main.songs[i];    // XCxxxxxxx-American Robin other info.ext
						Log.d(TAG, "xeno-canto:" + filname);  // American Robin other info-XCxxxxxxx.ext
						int loc = filname.indexOf(commonName);
						newName = "";
						if (loc > 0 && len > 0) { // found it
							newName = filname.substring(loc, loc + len) + " " + filname.substring(0, loc) + filname.substring(loc + len);
						} else {
							if (filname.equals("download.bin")) {  // download.bin
								int dotExt = filname.indexOf(".");    // .bin
								int fillen = filname.length();
								String ext = filname.substring(dotExt, fillen); // .bin
								try {
									BufferedReader buf = new BufferedReader(new FileReader(downloadPath + filname));
									// get file descriptor
									char[] buffer = new char[24];
									buf.read(buffer);
									Log.d(TAG, "buffer:" + buffer[0] + "_" + buffer[1] + "_" + buffer[2] + "_" + buffer[3]);
									if ((buffer[0] == 'I') && (buffer[1] == 'D') && (buffer[2] == '3')) {
										ext = ".mp3";
									}
									if ((buffer[20] == '3') && (buffer[21] == 'g') && (buffer[22] == 'p') && (buffer[23] == '4')) {
										ext = ".m4a";
									}
									if ((buffer[0] == 'O') && (buffer[1] == 'g') && (buffer[2] == 'g')) {
										ext = ".ogg";
									}
									if ((buffer[8] == 'W') && (buffer[9] == 'A') && (buffer[10] == 'V') && (buffer[11] == 'E')) {
										ext = ".wav";
									}
									buf.close();
								} catch (IOException e) {
									Log.e(TAG, "open failed on file:" + filname);
								}
								Log.d(TAG, "ext:" + ext);
								filname = filname.substring(0, dotExt); // download.
								filname = "@_" + filname + ext;
							}
							newName = commonName + " " + filname;
						}
						Log.d(TAG, "*** onResume() XC newName:" + newName);        // crash if songPath is null
						qry = "SELECT MAX(Inx) AS MaxInx FROM SongList" +
								" WHERE Ref = " + existingRef;
						rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
						rs.moveToFirst();
						int maxInx = rs.getInt(0) + 1;  // increment the last known inx
						int sr = 4;  // unknown
						Main.db.beginTransaction();
						ContentValues val = new ContentValues();
						val.put("Ref", existingRef);
						val.put("Inx", maxInx);
						val.put("Seg", 0);
						val.put("Path", Main.path);
						val.put("FileName", newName);
						val.put("Start", 0);
						val.put("Stop", 0);
						val.put("Identified", 0);
						val.put("Defined", 0);
						val.put("AutoFilter", 0);
						val.put("Enhanced", 0);
						val.put("Smoothing", 0);
						val.put("SourceMic", 0);
						val.put("SampleRate", sr); // solve it when you read it.
						val.put("AudioSource", 0);
						val.put("LowFreqCutoff", 0);
						val.put("HighFreqCutoff", 0);
						val.put("FilterStart", 0);
						val.put("FilterStop", 0);
						Main.db.insert("SongList", null, val);
						Main.db.setTransactionSuccessful();
						Main.db.endTransaction();
						val.clear();
						Main.songFile[i].renameTo(new File(Main.songpath + newName));
					}
				}
			}
		}

	}

	public void readTheOptions() {
		Log.d(TAG, "readTheOptions");
		qry = "SELECT Value FROM Options WHERE Name = 'AutoFilter'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isOptionAutoFilter = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'EnhanceQuality'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isEnhanceQuality = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ShowDefinition'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isShowDefinition = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ViewDistance'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isViewDistance = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ViewEnergy'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isViewEnergy = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ViewFrequency'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isViewFrequency = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ViewQuality'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isViewQuality = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ShowDetail'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isShowDetail = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ShowWeb'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isShowWeb = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'SortByName'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isSortByName = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'UseLocation'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isUseLocation = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'UseAudioRecorder'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isUseAudioRecorder = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'UseSmoothing'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isUseSmoothing = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'SampleRate'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isSampleRate = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'StartRecordScreen'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isStartRecordScreen = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'StartRecording'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isStartRecording = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'BatchDownload'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isBatchDownload = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'LoadDefinition'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isLoadDefinition = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'ShowAds'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isShowAds = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Options WHERE Name = 'Debug'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isDebug = (rs.getInt(0) != 0);
		rs.close();
		optionsRead = true;
	}

	public void loadAssets(String folder) {
		AssetManager assetManager = getAssets();
		String[] inFile = null;
		try {
			inFile = assetManager.list(folder);
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		File outFile = null;
		InputStream in = null;
		OutputStream out = null;
		int inFileLen = 0;

		if (inFile == null) {
			Log.d(TAG, "loadAsset inFile is null -- returning");
			return;
		}
		inFileLen = inFile.length; // number of files
	    if (inFileLen == 0) {
            Log.d(TAG, "loadAsset inFileLen == 0 -- returning" );
	        return;
	    }
		Log.d(TAG, "loadAsset inFileLen:" + inFileLen);
		for (int i = 0; i < inFileLen; i++) {
			tryNext:
			try {
				if (folder.equals("Define")) {
					in = assetManager.open("Define/" + inFile[i]);
					outFile = new File(definePathDir + "/" + inFile[i]);
					if (outFile.exists()) { // if the database is out there already don't load an empty one and trash the users files
						Log.d(TAG, "loadAssets outFile exists -- not loading:" + outFile);
						break tryNext;
					}
				}
				if (folder.equals("Song")) {
					in = assetManager.open("Song/" + inFile[i]);
					outFile = new File(songPathDir, inFile[i]);
					if (outFile.exists()) {
						Log.d(TAG, "loadAssets outFile exists -- not loading:" + outFile);
						break tryNext;
					}
				}
				Log.d(TAG, "loadAssets in:" + in);
				Log.d(TAG, "loadAssets outFile:" + outFile);
				out = new FileOutputStream(outFile);
				copyFile(in, out);

			} catch (IOException e) {
				Log.e(TAG, "Failed to copy asset file: " + inFile[i], e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						Log.e(TAG, "Failed in.close() error:" + e);
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						Log.e(TAG, "Failed out.close() error:" + e);
					}
				}
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public void readTheSongPath() {
		Log.d(TAG, "readTheSongPath");
		qry = "SELECT Path, CustomPath FROM SongPath";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		Main.path = rs.getInt(0);
		Main.customPathLocation = rs.getString(1);
		Log.d(TAG, " * * readTheSongPath path:" + path + " customPathLocation:" + customPathLocation);
	}

	public void readTheLocationFile() {
		Log.d(TAG, "read the Location file");

		qry = "SELECT Value FROM Location WHERE Name = 'AutoLocation'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		isAutoLocation = (rs.getInt(0) != 0);
		qry = "SELECT Value FROM Location WHERE Name = 'PhoneLat'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		phoneLat = rs.getInt(0);
		qry = "SELECT Value FROM Location WHERE Name = 'PhoneLng'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		phoneLng = rs.getInt(0);
		qry = "SELECT Value FROM Location WHERE Name = 'ManualLat'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		manualLat = rs.getInt(0);
		qry = "SELECT Value FROM Location WHERE Name = 'ManualLng'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		manualLng = rs.getInt(0);
		rs.close();

		if (Main.isAutoLocation == true) {
			Main.latitude = phoneLat;
			Main.longitude = phoneLng;
		} else {
			Main.latitude = manualLat;
			Main.longitude = manualLng;
		}
	}

	private void rebuildNameList() {
		// this is a one time event -- change file name from existing to species common name plus existing numbers and extension
		Log.d(TAG, "rebuildNameList");
		//pathBase = environment + "/birdingviamic/";
		songpath = songPathDir + "/";
		int pathLen = songpath.length();
		File dir = new File(songpath);
		Log.d(TAG, "onCreate: dir:" + dir);
		Main.songFile = dir.listFiles();
		if (Main.songFile == null) {
			Log.d(TAG, "rebuildNameList songFile is null -- closing");
			String msg = "invalid path:" + songpath;
			Log.d(TAG, msg);
			//finish();
		} else {
			int songsFileLen = Main.songFile.length;
			Main.songs = new String[songsFileLen];
			char q = 34; // double quote to avoid crash on single quote
			String nums = "0123456789";
			for (int i = 0; i < songsFileLen; i++) {
				songs[i] = songFile[i].toString().substring(pathLen);
				String oldName = songs[i];
				if (!oldName.contains("@_") || !oldName.contains("XC")) { // don't mess with unknowns and Xeno-Canto files
					qry = "SELECT FileName, CodeName.CommonName, CodeName.Ref, Inx, Seg " +
							" FROM SongList JOIN CodeName ON SongList.Ref = CodeName.Ref" +
							" WHERE path = " + path +
							" AND FileName = " + q + oldName + q;

					Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
					rs.moveToFirst();
					int cntr = rs.getCount();
					if (cntr == 1) { // don't solve the world -- work on the ones with possible success
						existingRef = rs.getInt(2);
						if (existingRef > 0) { // don't change the name or it will become "Unknown"
							existingInx = rs.getInt(3);
							existingSeg = rs.getInt(4);
							String thisInx = "";
							String nam = rs.getString(0); // existing file name
							String comnam = rs.getString(1); // // species common name
							thisInx = "";
							int lennam = nam.length() - 4; // remove the dot and extension
							String ext = nam.substring(lennam); // .m4a
							nam = nam.substring(0, lennam);  // fileName5V30
							lennam = nam.length(); //
							String ch = "";
							if (lennam > 0) {
								for (int j = 0; j < lennam; j++) { // look for a number
									ch = nam.substring(j, j + 1);
									//	Log.d(TAG, "ch:" + ch);
									if (nums.contains(ch)) { // i found a number
										thisInx = nam.substring(j);
										break;
									}
								}
							}
							newName = comnam + thisInx + ext;  //
							songFile[i].renameTo(new File(Main.songpath + newName));
							Log.d(TAG, "oldName:" + oldName + " newName:" + newName);
						} // don't do Unknowns
					} // cntr == 1
					rs.close();

					Main.db.beginTransaction();
					qry = "UPDATE SongList" +
							" SET FileName = " + q + newName + q +
							" WHERE FileName = " + q + oldName + q +
							" AND Path = " + path +
							" AND Ref = " + existingRef +
							" AND Inx = " + existingInx +
							" AND Seg = " + existingSeg;
					//Log.d(TAG, "Song Identity With @ Update SongList qry:" + qry);
					Main.db.execSQL(qry);
					Main.db.setTransactionSuccessful();
					Main.db.endTransaction();
				} // skip the @_
			} // next i
		} // song file null test

	} // rebuildNameList()


	void checkVersion() { // ONLY RUN THIS ONCE !!!
		// this is a one time event -- it will run if it finds RefUpgrade61.csv AND CodeName61.csv
		// it deletes those two files on completion
		// change Ref to new version In SongList, DefineTotals, DefineDetail
		// replace CodeName with the new data
		Log.d(TAG, "CheckVersion");
		// upgrade from version 5.4 to 6.1 -- WITHOUT Loosing the existing songlist or defines.
		// the table RefUpgrade contains the Species Reference number for the existing version 5.4 and the new version 6.1
		// It has to be the full file because names and species changed
		// i need a dialog to upgrade -- only ask if upgrade files exist -- yes, later
		// no files return -- else ask for upgrade
		// yes - do it now
		// later - return here the next startup
		// I have to load the files out of Assets Define into definepath before I get here
		// Always during transfer from Assets I delete the files first in Define then if they exist in Assets I transfer them
		// I retain an empty.csv file if they have been loaded so I don't transfer again. -- NO I don't think I empty a file out !!
		// they won't be in the next version -- just the new database which won't be loaded if exists -- true
		// And I delete before I transfer so no files
		qry = "SELECT Num from Version";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		versionNum = rs.getInt(0);  // this is the old version 54 the first time you check and 61 the next time you check
		rs.close();
		Scanner refup = null;
		try {
			// see if it is already loaded
			File dir = new File(definepath);
			File[] matches = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith("RefUpgrade") && name.endsWith(".csv");
				}
			});
			if (matches.length == 0) {  // no RefUpgrade files exist
				Log.d(TAG, "Exit checkVersion -- No RefUpgradeXX.csv files Exist");
				return;
			}
			String verNum = "RefUpgrade" + versionNum + ".csv";
			refUpgradeFile = matches[0].getName();
			if (verNum.equals(refUpgradeFile)){	// look for versionNum in the file name -- will not match
				// it has already been loaded -- now I have enough info to delete the files
				Log.d(TAG, "checkVersion version matches -- deleting " + refUpgradeFile);
				File file = new File(definepath + refUpgradeFile);
				if (file.exists()) {
					boolean deleted = file.delete();
				}
				Log.d(TAG, "checkVersion  -- deleting CodeName" + versionNum + ".csv");
				file = new File(definepath + "CodeName" + versionNum + ".csv");
				if (file.exists()) {
					boolean deleted = file.delete();
				}
				Log.d(TAG, "Exit checkVersion");
				return;
			}
			refup = new Scanner(new BufferedReader(new FileReader(definepath + refUpgradeFile)));
			// it will crash out of this function under "catch" below if file is missing -- else load a dialog
			Intent ugd = new Intent(this, UpgradeDialog.class);
			Log.d(TAG, "checkVersion startActivityForResult request upgrade_dialog.");
			Main.myRequest = 1;
			startActivityForResult(ugd, 1);

		} catch (Exception e) {
			// the files don't exist leave quitely.
			Log.d(TAG, "Exit checkVersion -- " + refUpgradeFile + " does NOT Exist:" + e);
			return;
		}
	} // checkVersion


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		Log.d(TAG, "onActivityResult requestCode:" + Main.myRequest + " resultCode:" + Main.myUpgrade);
		if (requestCode == 1) {
			if (resultCode == 0 || resultCode == 2) {
				return; // later
			}
			upgradeSpecies(resultCode); // else 1 = upgrade
		}
	}

	void upgradeSpecies(int myUpgrade) {
//		1:  // we are going to convert
		Scanner refup = null;
		int versionNew = 0;
		try {
			refup = new Scanner(new BufferedReader(new FileReader(definepath + refUpgradeFile)));
			Log.d(TAG, "checkVersion: Beyond the dialog. upgradeSpeciesAnswer:" + myUpgrade);
			Log.d(TAG, "checkVersion: We are going to convert");
			Main.db.beginTransaction();
			qry = "DELETE FROM RefUpgrade";  // clear out the table first it may have previous data
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();

			int versionExist = 0;
			versionNew = 0;
			// the upgrade file first line has existingVersion, newVersion numbers
			// followed by old ref, new ref pairs
			ContentValues val = new ContentValues();
			try {
				String line = refup.nextLine();
				String[] tokens = line.split(",");
				if (tokens.length > 0) {
					versionExist = Integer.parseInt(tokens[0]);
					versionNew = Integer.parseInt(tokens[1]);
					Log.d(TAG, "checkVersion:" + refUpgradeFile + " versionExist:" + versionExist + " ShouldMatch:" + versionNum
							+ " VersionNew:" + versionNew);
					if (versionExist != versionNum) {
						Log.d(TAG, "checkVersion:" + refUpgradeFile + " versionExist:" + versionExist + " FAILED to Match:" + versionNum);
						String msg = "Species Upgrade STOPPED Existing Version:" + versionExist + " FAILED to Match:" + versionNum;
						Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
						return;
					}
				}
				// load the table RefUpgrade from the csv file RefUpgradeXX.csv
				Log.d(TAG, "checkVersion load the table RefUpgrade from the csv file RefUpgradeXX.csv");
				while ((line = refup.nextLine()) != null) {
					tokens = line.split(",");
					if (tokens.length == 2) {
						Main.db.beginTransaction();
						val.put("RefExist", Integer.parseInt(tokens[0]));
						val.put("RefNew", Integer.parseInt(tokens[1]));
						Main.db.insert("RefUpgrade", null, val);
						Main.db.setTransactionSuccessful();
						Main.db.endTransaction();
						val.clear();
					}
				}
			} catch (Exception e) {
				Log.d(TAG, "internal error loading RefUpgrade:" + e);
			} finally {
				refup.close(); // does this close cause an exception ?
				Log.d(TAG, "checkVersion Close and Cleanup file RefUpgrade");
			}
		} catch (Exception e) {
			// the files don't exist leave quitely.
			Log.d(TAG, "checkVersion Exit -- " + refUpgradeFile + " does NOT Exist:" + e);
			return;
		}

		Log.d(TAG, "checkVersion RefUpgradeXX.csv read in successfully");
		// load the CodeName table
		Scanner codnam;
		try {  // match the new version number in the name
			codnam = new Scanner(new BufferedReader(new FileReader(definepath + "CodeName" + versionNew + ".csv")));
			// it will crash out of this function under "catch" below if file is missing -- else build a dialog
			Log.d(TAG, "checkVersion delete existing from table then re-load the CodeName table");
			Main.db.beginTransaction();
			qry = "DELETE FROM CodeName WHERE Ref > 0 AND Ref < 39997";
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			ContentValues val = new ContentValues();
			try {
				String line = "";
				while ((line = codnam.nextLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length > 0) {
						if (!tokens[0].equals("Ref")) { // thus I can handle file headers or no file headers
							int ref = Integer.parseInt(tokens[0]);
							if (ref > 0 && ref < 39997) {
								Main.db.beginTransaction();
								val = new ContentValues();
								val.put("Ref", ref);
								val.put("Spec", tokens[1]);
								val.put("CommonName", tokens[2]);
								val.put("Region", tokens[3]);
								val.put("SubRegion", tokens[4]);
								val.put("RedList", tokens[5]);
								val.put("InArea", Integer.parseInt(tokens[6]));
								val.put("MinX", Integer.parseInt(tokens[7]));
								val.put("MinY", Integer.parseInt(tokens[8]));
								val.put("MaxX", Integer.parseInt(tokens[9]));
								val.put("MaxY", Integer.parseInt(tokens[10]));
								Main.db.insert("CodeName", null, val);
								Main.db.setTransactionSuccessful();
								Main.db.endTransaction();
								val.clear();
							}
						}
					}
				}
			} catch (Exception e) {
				Log.d(TAG, "checkVersion internal error loading CodeName:" + e);
			} finally {
				codnam.close();
				Log.d(TAG, "checkVersion closed codnam");

			}
		} catch (Exception e) {
			// the files don't exist leave quitely.
			Log.d(TAG, "Exit checkVersion -- CodeName" + versionNew + ".csv does NOT Exist:" + e);
			return;
		}
		Log.d(TAG, "checkVersion CodeName" + versionNew + ".csv read in successfully");

		Log.d(TAG, "checkVersion Update the SongList");

		qry = "SELECT SongList.Ref, RefUpgrade.RefNew" +
				" FROM SongList JOIN RefUpgrade ON SongList.Ref = RefUpgrade.RefExist" +
				" WHERE SongList.Ref > 0 AND SongList.Ref < 39997";
		int bias100k = 100000; // move all the Ref to greater than 100000
		Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cntr = rs.getCount();
		Log.d(TAG, "checkVersion Modify SongList cntr:" + cntr);
		int tempRef = 0;
		// move the existing Ref out of the way so the new ref doesn't conflict with different old refs
		for (int i = 0; i < cntr; i++) {
			existingRef = rs.getInt(0);
			tempRef = rs.getInt(1);
			Main.db.beginTransaction();
			if (tempRef == -1) {
				qry = "DELETE FROM SongList WHERE Ref=" + existingRef;
			} else {
				tempRef += bias100k;
				qry = "UPDATE SongList" +
						" SET Ref = " + tempRef +
						" WHERE Ref = " + existingRef;
			}
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			rs.moveToNext();
		} // next i
		rs.close();
		// now remove the bias
		qry = "SELECT Ref FROM SongList WHERE Ref > 100000";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		cntr = rs.getCount();
		Log.d(TAG, "SecondPass SongList cntr:" + cntr);
		for (int i = 0; i < cntr; i++) {
			tempRef = rs.getInt(0);
			existingRef = tempRef - bias100k;
			Main.db.beginTransaction();
			qry = "UPDATE SongList" +
					" SET Ref = " + existingRef +
					" WHERE Ref = " + tempRef;
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			rs.moveToNext();
		} // next i
		rs.close();
		// DefineTotals
		Log.d(TAG, "checkVersion Update DefineTotals");
		qry = "SELECT DefineTotals.Ref, RefUpgrade.RefNew" +
				" FROM DefineTotals JOIN RefUpgrade ON DefineTotals.Ref = RefUpgrade.RefExist" +
				" WHERE DefineTotals.Ref > 0 AND DefineTotals.Ref < 39997";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		cntr = rs.getCount();

		Log.d(TAG, "checkVersion Update DefineTotals cntr:" + cntr);
		tempRef = 0;
		// move the existing Ref out of the way so the new ref doesn't conflict with different old refs
		for (int i = 0; i < cntr; i++) {
			existingRef = rs.getInt(0);
			tempRef = rs.getInt(1);
			Main.db.beginTransaction();
			if (tempRef == -1) {
				qry = "DELETE FROM DefineTotals WHERE Ref=" + existingRef;
			} else {
				tempRef += bias100k;
				qry = "UPDATE DefineTotals" +
						" SET Ref = " + tempRef +
						" WHERE Ref = " + existingRef;
			}
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			rs.moveToNext();
		} // next i
		rs.close();
		// now remove the bias
		qry = "SELECT Ref FROM DefineTotals WHERE Ref > 100000";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		cntr = rs.getCount();
		Log.d(TAG, "checkVersion SecondPass DefineTotals cntr:" + cntr);
		for (int i = 0; i < cntr; i++) {
			tempRef = rs.getInt(0);
			existingRef = tempRef - bias100k;
			Main.db.beginTransaction();
			qry = "UPDATE DefineTotals" +
					" SET Ref = " + existingRef +
					" WHERE Ref = " + tempRef;
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			rs.moveToNext();
		} // next i
		rs.close();
		// DefineDetail

		Log.d(TAG, "checkVersion Update DefineDetail");
		qry = "SELECT DefineDetail.Ref, RefUpgrade.RefNew" +
				" FROM DefineDetail JOIN RefUpgrade ON DefineDetail.Ref = RefUpgrade.RefExist" +
				" WHERE DefineDetail.Ref > 0 AND DefineDetail.Ref < 39997";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		cntr = rs.getCount();
		Log.d(TAG, "checkVersion Modify DefineDetail cntr:" + cntr);
		tempRef = 0;
		// move the existing Ref out of the way so the new ref doesn't conflict with different old refs
		for (int i = 0; i < cntr; i++) {
			existingRef = rs.getInt(0);
			tempRef = rs.getInt(1);
			Main.db.beginTransaction();
			if (tempRef == -1) {
				qry = "DELETE FROM DefineDetail WHERE Ref=" + existingRef;
			} else {
				tempRef += bias100k;
				qry = "UPDATE DefineDetail" +
						" SET Ref = " + tempRef +
						" WHERE Ref = " + existingRef;
			}
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			rs.moveToNext();
		} // next i
		rs.close();
		// now remove the bias
		qry = "SELECT Ref FROM DefineDetail WHERE Ref > 100000";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		cntr = rs.getCount();
		Log.d(TAG, "checkVersion SecondPass DefineDetail cntr:" + cntr);
		for (int i = 0; i < cntr; i++) {
			tempRef = rs.getInt(0);
			existingRef = tempRef - bias100k;
			Main.db.beginTransaction();
			qry = "UPDATE DefineDetail" +
					" SET Ref = " + existingRef +
					" WHERE Ref = " + tempRef;
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			rs.moveToNext();
		} // next i
		rs.close();

		Log.d(TAG, "checkVersion Delete From RefUpgrade table");
		Main.db.beginTransaction();
		qry = "DELETE FROM RefUpgrade";
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();

		// you can't upgrade more than once old to new --> ok; old to new --> existing new to new --> trash
		Log.d(TAG, "checkVersion Update Version to:" + versionNew);
		Main.db.beginTransaction();
		qry = "UPDATE Version SET Num = " + versionNew;
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();


		Log.d(TAG, "checkVersion Delete RefUpgradeXX.csv");
		File file = new File(definepath + "RefUpgrade" + versionNew + ".csv");
		if (file.exists()) {
			boolean deleted = file.delete();
		}
		Log.d(TAG, "checkVersion Delete CodeName" + versionNew + ".csv");
		file = new File(definepath + "CodeName" + versionNew + ".csv");
		if (file.exists()) {
			boolean deleted = file.delete();
		}
		String msg = "Species Upgrade complete now Version:" + versionNew;
		Log.d(TAG, "checkVersion " + msg);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		Main.alertRequest = 6; // database upgrade complete
		Intent dbc = new Intent(this, Alert1ButtonDialog.class);
		startActivityForResult(dbc, Main.alertRequest);  // request == 6 == show database upgrade complete

	} // upgradeSpecies


	void checkForNewFiles() {  // moves them out of selected path to local/Songs/ directory.
		// and filter out of selected path Define to local/Define/ directory
		// note: this is only called if path > 1 and only if isBatchDownload is true
		// after moving the files, path is re-set to 1 and isBatchDownload is set false.
		// songpath is selected path which is > 1 -- could be null if invalid path from custom.
		if (songpath == null) {
			String msg = "invalid path";
			Log.d(TAG, msg);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			return;
		}
		if (sharedDefine != null) {
			int pathLen = sharedDefine.length();
			Log.d(TAG, "* checkForNewFiles() sharedDefine:" + sharedDefine);
			File dirDef = new File(sharedDefine);
			Log.d(TAG, "onCreate: dirDef:" + dirDef);
			File[] defineFile = dirDef.listFiles();
			int defFileLen = 0;
			if (defineFile != null) {
				defFileLen = defineFile.length;
				Log.d(TAG, "* checkForNewDefineFiles() defFileLen:" + defFileLen);
				//String[] defines = new String[defFileLen]; // names from the folder
				for (int i = 0; i < defFileLen; i++) {
					//defines[i] = defineFile[i].toString().substring(pathLen);
					//String nam = defines[i];
					String nam = defineFile[i].toString().substring(pathLen);
					int extLoc = nam.length() - 4;
					String ext = nam.substring(extLoc);
					Log.d(TAG, " definefile:" + nam + " ext:" + ext );
					if (ext.equalsIgnoreCase(".csv")) { // only transfers csv files
						Boolean success = defineFile[i].renameTo(new File(definepath + nam));
						Log.d(TAG, " did i move file:" + nam + " ?:" + success);
						if (nam.equals("filter.csv")) {
							loadFilterData();  // it is loaded into database
							//deleteFile(nam); // crash on has a file separater
						}
					}
				}
			}
		}
		// localPath is songPath = 1 from getExternalFilesDir("Song");
		String localPath = Main.songPathDir.toString() + "/";
		Log.d(TAG, "* checkForNewFiles() path:" + path + " songpath:" + songpath);
		int pathLen = songpath.length();
		File dir = new File(songpath);
		Log.d(TAG, "onCreate: dir:" + dir);
		Main.songFile = dir.listFiles();
		String nums = "0123456789";
		String ch = "";
		int cntr = 0;
		int songsFileLen = 0;
		if (Main.songFile == null) {
			Log.d(TAG, "* checkForNewFiles() songFile is null -- closing");
			String msg = "invalid path:" + songpath;
			Log.d(TAG, msg);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			return;
		} else {
			songsFileLen = Main.songFile.length;
			Log.d(TAG, "* checkForNewFiles() songFileLength:" + songsFileLen);        // crash if songPath is null
			Main.songs = new String[songsFileLen];
			for (int i = 0; i < songsFileLen; i++) {
				Main.songs[i] = Main.songFile[i].toString().substring(pathLen);
				//  01234567890123456789012345678901234
				String nam = Main.songs[i];        // "16 White-crowned Sparrow Song 1.mp3"
				//Log.d(TAG, "* checkForNewFiles() songs[" + i + "] " + nam );
				Boolean isAlbumNumber = false;
				Boolean isStartsWithXC = false;
				int chLoc = nam.indexOf(' '); // 2
				if (chLoc > 0 && chLoc < 5) {
					ch = nam.substring(0, chLoc);
					//Log.d(TAG, "ch:" + ch);

					if (nums.contains(ch.substring(0, 1)) == true && nums.contains(nam.substring(chLoc + 1, chLoc + 2)) == false) {
						isAlbumNumber = true;
						// find the first beyond the name -- its a number followed by a space followed by a letter
						int songLoc = nam.indexOf(" Song"); // 24
						int callLoc = nam.indexOf(" Call"); // -1
						int drumLoc = nam.indexOf(" Drum"); // -1
						int extLoc = nam.length() - 4;
						int afterName = Math.max(chLoc, extLoc); // 31
						if (afterName > chLoc) {
							if (songLoc > chLoc) {
								afterName = Math.min(songLoc, afterName); // 24
							}
							if (callLoc > chLoc) {
								afterName = Math.min(callLoc, afterName);
							}
							if (drumLoc > chLoc) {
								afterName = Math.min(drumLoc, afterName);
							}
							if (afterName == extLoc) { // just the name.ext
								newName = nam.substring(chLoc + 1, afterName) + ch + nam.substring(afterName);
							} else {  // additional words between
								newName = nam.substring(chLoc + 1, afterName) + ch + nam.substring(afterName + 1);
							}
							Main.songFile[i].renameTo(new File(localPath + newName));
							cntr++;
						}
					}
				} else if (nam.substring(0, 2).equals("XC")) {    // XC179353-Northern Mockingbird 140414-002.mp3
					chLoc = nam.indexOf("-");                    // 0123456789012345678901234567890123456789012
					isStartsWithXC = true;
					if (chLoc > 3) {
						ch = nam.substring(0, chLoc);  // XC179353
						int extLoc = nam.length() - 4;
						if (extLoc > chLoc) {  // check for .mp3 file
							// Northern Mockingbird 140414-002-XC179353.mp3
							newName = nam.substring(chLoc + 1, extLoc) + "-" + ch + nam.substring(extLoc);
							Main.songFile[i].renameTo(new File(localPath + newName));
							cntr++;
						}
					}
				}
				if ((isAlbumNumber == false) && (isStartsWithXC == false)) { // for Birding Via Mic_XX external apps or full name Download
					int extLoc = nam.length() - 4;
					String ext = nam.substring(extLoc);
					if (ext.equalsIgnoreCase(".mp3") || ext.equalsIgnoreCase(".m4a") ||
							ext.equalsIgnoreCase(".wav") || ext.equalsIgnoreCase(".ogg")) {
						newName = nam; // it's a song file load it like it is.
						Main.songFile[i].renameTo(new File(localPath + newName));
						cntr++;
					}

				}
				Log.d(TAG, "* checkForNewFiles() newName:" + newName);
			} // finished loading files
			// check the Define folder for filter.csv
		}
		Main.db.beginTransaction();
		qry = "DELETE FROM SongList WHERE Path = " + path;
		Main.db.execSQL(qry);
		path = 1;
		songpath = Main.songPathDir.toString() + "/";
		isBatchDownload = false;
		qry = "UPDATE SongPath SET Path = " + path;
		Main.db.execSQL(qry);
		qry = "UPDATE Options SET Value = 0 WHERE Name = 'BatchDownload'";
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();

		String msg = "Files in Download:" + songsFileLen + " Files Loaded:" + cntr;
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		Log.d(TAG, "* return from checkForNewFiles() path:" + path + " songpath:" + songpath);
	}

	void loadFilterData() { // called only on transfer from external data if filter.csv file now exists
		Log.d(TAG, "loadFilterData()");
		Scanner filtr = null;
		File localFilter = null;
		try {
			localFilter = new File(definePathDir.toString() + "/filter.csv");
			filtr = new Scanner(new BufferedReader(new FileReader(localFilter)));
			Log.d(TAG, "loadFilterData - clear existing filter -- load new data");
			Main.db.beginTransaction();
			qry = "DELETE FROM Filter";  // clear out the table first it may have previous data
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			// the filter file  has XCxxxxxx, filterType, integerValue
			ContentValues val = new ContentValues();
			try {
				String line;
				String[] tokens;
				while ((line = filtr.nextLine()) != null) {
					tokens = line.split(",");
					if (tokens.length == 3) {
						Main.db.beginTransaction();
						val.put("XcName", tokens[0]);
						val.put("FilterType", tokens[1]);
						val.put("FilterVal", Integer.parseInt(tokens[2]));
						Log.d(TAG, " filter data:" + tokens[0] + "," + tokens[1] + "," + Integer.parseInt(tokens[2]));
						Main.db.insert("Filter", null, val);
						Main.db.setTransactionSuccessful();
						Main.db.endTransaction();
						val.clear();
					}
				}
			} catch (Exception e) {
				Log.d(TAG, "internal error loading filter:" + e);
			} finally {
				isFilterExists = true;
				filtr.close(); // does this close cause an exception ? -- file will be deleted on return -- no it is left there
				Log.d(TAG, "Close and Cleanup file filter");
			}
		} catch (Exception e) {
			// the file dosn't exist leave quitely.
			Log.d(TAG, "Exit checkVersion -- filter.csv does NOT Exist:" + e);
			isFilterExists = false;
			return;
		}
		//boolean success = deleteFile(localFilter.toString()); // crash on contains file separator
		//Log.d(TAG, " delete filter.csv ?" + success);

	}
}