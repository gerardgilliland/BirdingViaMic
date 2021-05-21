package com.modelsw.birdingviamic;
/*
 * 55.F BirdSongs.db IOC World Bird List version 61 -- 2016.03.16
 * 55.L fix Load Definitions -- was failing if song was Stereo test Main.sourceMic == 0 needed (Main.sourceMic & 1) == 0
 * 		fix LastKnown Identified -- in Clean Database -- was 'Identified ' (with space at end), and in runPatch()
 * 55.M	use entire screen regardless of song length -- was use part if less than 5 seconds.
 *		center time numbers -- was draw time numbers above the time.
 * 55.N fix Metadata mp3 Comment needs to use TCOP (next atom below COMM) Length is wrong and recordists end comments differently.
 * 55.T Fix M4a And Wav Players --
 * 55.U FixCrashOnBuildRampFixRampWithOnePhrase
 * 55.W Clean up code
 * 55.X Rewrite Identify To Use Database Instead Of Memory For Each Phrase
 * 55.Y Totals Working With Database Before Modify Detail
 * 55.Z Totals And Detail Using Database Once Per Phrase Per Ref New Identify
 * 56.A Using Database And Fix Shift On Best Fit
 * 56.B Breakout Criteria To Mean And StdDev In Identify
 * 56.C Remove Percent From Identify Doesn't Work With One Ref Per Phrase
 * 56.D Before Finding Codec Changing Lengths_Unsolved_Ver57
 * 56.E Add Permissions Testing_Ver58
 * 56.G Cleanup Permissions SourceMic And SampleRate Ver60
 * 56.H Move Batch File Inside Of Select File Path No Reboot Required
 * 56.I Permissions Don't Wait Until User Responds
 * 56.J Fix Permissions Wait Move Load Inside Path
 * 56.K _Ver61 Plus A Few Minor Changes
 * 56.L Work On Decode
 * 56.M Add Decode File Error Test Ver62
 * 56.N Fix Decoder Error Ver63
 * 56.O Add Stereo Field To SongList
 * 56.P Fix Stereo Field Getting Close To Local VisualizerView
 * 56.Q Time Not The Same Between Functions
 * 56.R Local Visualizer Working YEA
 * 56.S Getting Pretty Clean
 * 56.T Working On Visualizer
 * 56.U Removed Renderer
 * 56.V Re-Added Renderer For Edit
 * 56.W Visualizer Code The Same Results Not The Same
 * 56.X Using Low And Hi Limits
 * 56.Y Visualizer Using Average Modify Help
 * 56.Z Remove Accounts Requirement From Code And Manifest_Ver64
 * 57.A Working On Realtime Plot
 * 57.B Minor Changes Extend Range In VisualizerView
 * 57.C Extend Range Of Visualizer_Ver65
 * 57.D Start Song Before Decode Else Song Hangs
 * 57.E Decoder Seek Works With M4a Not With Mp3
 * 57.F Should Be Same As 57.E Just Saving In Case
 * 57.G Seek Works And Fix 48000 Adjust_Ver66
 * 57.H Documentation
 * 57.I Set Initial Max In Plot To Remove Noise
 * 57.J Minor Cleanup
 * 57.K Remove Noise Cleanup Decoder And VisualizerView _Ver67
 * 57.L Fix Filter Not Saving FullName _Ver68
 * 57.M If Song Data Null Then Finish Why Is Flash On PlaySong
 * 57.N Under Android Studio 3.1.4 using SDK 28 -- Ver 69 -- Loaded 10/28/18
 * 57.O Add option to save decoded file
 * 57.P Upgrade from ioc World Bird List 61 to version 92 with new BirdLife bounding boxes -- 2019.09.12 -- code ver 70
 * 57.Q Move upgrade from Main to SongList -- code ver 71
 * 57.R Move upgrade back to Main from SongList - code ver 71 -- ioc version 92
 * 57.S Copy upgrade logic from 57.N -- use 57.P to load version 92 -- code ver 72
 *      add missing SavePcmData to Options in database dbVersion 82
 * 57.T Clean with upgrade Detail imbedded in Totals to avoid memory failure but Fails On super.Start()
 * 57.U Not Clean but Runs with upgrade detail imbedded in totals. -- code ver 73 -- ioc version 92
 * 57.V load in Google Play but complains version 61 after delete and install (did I load ioc version 61 by mistake? )
 * 57.W code ver 75; db ver 83; verified ico ver 92
 * 57.X with 57.T clean upgrade -- code ver 76; db ver 83; verified ico ver 92
 * 57.Y start with 57.X -- attempt to fix crash from google robot attempting to run an empty file. code ver 77;
 * 		NOT LOADED in Google -- I can't make it fail here. -- staying with 57.X
 * 57.Z Upgrade to Android 10; SDK 29; ver 78
 * ***** failed attempts at laoding OnDemand Assets:
 * 58.A attempt to add assets -- failed and lost code
 * 58.B start with 57.Z ver 78; run as aab file -- and BACK UP as 58.B -- still using external songs.
 * 58.C start with 58.B ver 79; run as aab file -- change assetpack name: BirdingViaMicAssets
 * 58.C ver 79; run as aab file -- 4 asset packs SongsNW; SongsOW; SongsOA; SongsNAC;
 * 58.D ver 80; -- Old World set as install at Delivery -- the others set as On Demand
 * 58.E ver 81; -- attempt to get through step 7 of On Demand -- doesnt load
 * 58.F ver 82; -- Build an APK -- start with 57U; menu > Refactor > Migrate to AndroidX
 * 58.G ver 83; -- start with 58E -- using new install C:\Users\Owner\.android\keystore.jks Alias name: upload
 * 58.H ver 84; -- attempt to enable SongsXX to be OnDemand
 * error app-release.aab Your Android App Bundle is signed with the wrong key.
 * Ensure that your App Bundle is signed with the correct signing key and try again:
 * SHA1: A5:55:F8:49:D6:BE:FE:55:AC:D5:4C:C2:24:D8:28:BF:7A:47:62:6E.
 * request upload the existing C:\Users\Owner\.android\keystore.jks to be loaded for com.modelsw.birdingviamic
 * 58.H1 ver 84 -- loaded in Play as ver 83 (from gradle) with string at 84 --Example OnDemand Does not compile
 * I saw an APK (not aab bundle) out there as V 85
 * 58.I ver 86 -- working on asset packs.
 * 58.J ver 87 -- Loading on Closed testing -- until I get asset packs working
 * 58.K ver 88 -- AssetPackLocation -- compiles -- but messed up attempting load asset packs On Demand
 * 58.L ver 89 -- remove load asset packs code  (the pack are still there - the code to load them is not.
 * 58.M_V90 -- clean up Select Song Path so it will install.
 * 58.N_V91 -- add Load Asset Pack SongsNW in SelectSongPath and LoadAssetPack
 * 58.O_V92	-- add LoadAssetPack class -- add ShowPermission class
 * 58.P_V93 -- store assetPackName on Main -- I have removed all the calls in LoadAssetPack
 * 58.Q_V94 -- get debug stack overflow in LoadAssetPack > LoadOnePack -- totalSize = assetLocation.size(); suspect not seeing asset.
 * Start over with BirdingViaMic57ZAndroid10Sdk29AsAppBundle
 * 58.S_V96 -- No OnDemand Assets -- will load externally. remove AddAssetPack.java
 * 58.T_V97 -- Add other External Asset Names to Strings, select_song_path.xml, SelectSongPath.java
 * 58.U_V98 -- Fix crash on Play if no song is selected - drop table meta_data it is loaded from song
 * 58V.99 -- fix path for NAC to remove _ (aka new name) - so it will load
 * 58W.100 -- Loading New CodeName before I change to Backup and Remove.
 * 58X.101 -- LoadAssets was messing up the database and SongData -- backed out to 58O.V92 -- Need to Save Define and Song folders in Backup
 * 58X.101 -- Saving one CSV file
 * 58Y.102 -- Saving Define and Song folders to Download/Define and Download/Song.
 * 58Z.103 -- Saving CSV files to Download/Define
 * 59A.104 -- Cleanup Backup files
 *	 the tables that use Ref: CodeName, DefineDetail, DefineTotals, Identify, SongList
 *	 the table that could be user modified: BirdWebSites, Filter, LastKnown, Location, Options, SongPath
 *	 the tables that I control: RedList, RefUpgrade, Region, Version
 *
 * I need to but haven't implemented setFastScrollEnabled on Species list during Song Rename (can't find it)
 *
 */
// NOTE: All (at least most) TrebleClef.jpg images are in: C:\OSJ\GerardRoot 
// Also, there are a lot of images and songs at: J:\Users\Gerard
// CHECK build.gradle for the 4 items that need to be updated: 1)versionCode 2)versionName 3)versionName(in string) 4) DatabaseVersion (in Main)
// Where is android studio building my .apk file? >	YourApplication\app\build\outputs\apk name: app-release.apk
// or -- YourApplication\app\release\app.aab
//  https://play.google.com/console/developers/5224623645443335130/app-list

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;  //for displaying time
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;  // for displaying time
import java.util.Scanner;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.media.AudioManager;
import android.os.Environment;


public class Main extends AppCompatActivity implements OnClickListener {
	private static final String TAG = "Main";
	public static String adjustViewOption; // AdjustView: clear, move, save, exclude, cancel, edit -- used in playSong, VisualizerView
	public static int alertRequest = 0; // 2=delete files; 3=delete species; 4=delete web; 5=meta data info box; 6=database upgrade complete;
	public AssetManager assetManager = null;  // the assetManager
	public static short[] audioData;  // the entire song -- -32767 +32767 (16 bit)
	public static int audioDataLength;  // the usable file length without overflows
	public static int audioDataSeek;  // in playSong and decodeFile
	public static int audioSource = -1; // stored in SongList -- 0=default, 1=mic, 5=camcorder, 6 voice recognition, -1=unknown
	public static int bitmapWidth; // set from VisualizerView used in AdjustView
	public static int bitmapHeight; // set from VisualizerView used in AdjustView
	public static int buttonHeight;  // fails to set in Main -- set in PlaySong used in VisualizerView
	public static Boolean[] ck;  // used in SongList -- song selected
	public static String codeNameFile;
	public static String commonName; // stored in species table CodeName
	public static String customPathLocation = null;
	public static String databaseName; // birdingviamic/Define/BirdSongs.db
	public static int databaseVersion = 84; // increment if change database -- calls SongData
	public static SQLiteDatabase db;
	public static String definepath = null; // birdingviamic/Define
	public static File definePathDir; // file format
	public static String displayName = ""; // used in PlaySong identify to hold identification
	public static int duration; // song length in milliseconds
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
	public static int filterStartAtLoc = 0; // set in AdjustView -- used in PlaySong
	public static int filterStopAtLoc = 0; // set in AdjustView -- used in PlaySong
	public static int highFreqCutoff = 0;  // user entered from adjust view
	public static float hzPerStep = 11025f / 512f;  // 21.53 hz per step -- in the file as hz; 0-511 everywhere else.
	public static int[] inx;  // holds existingInx from songList
	public static boolean isAutoLocation = true; // ShowLocation use GPS vs manual
	public static boolean isCheckPermissions = false; // set true if you need to check permissions (Android 6.0+)
	public static boolean isDebug = false; // save extra files
	public static boolean isDecodeBackground = true; // manual=false / background=true
	public static boolean isEdit = false; // set true when edit button on play is tapped
	public static boolean isEnhanceQuality = true; // build s/n kernel and apply to normalized audio
	public static boolean isExternalMic = false; // set true when plugged in else false if internal mic
	public static boolean isFilterExists = false; // has a manual filter been set in AdjustView
	public static Boolean isIdentify = true;  // has PlaySong identify button been pushed
	public static boolean isLoadDefinition = false;  // (set in options) any checked in the list -- define if not mic -- identify if mic
	public static boolean isNewStartStop = false; // set in AdjustView for song segment
	public static boolean isOptionAutoFilter = true;  // find mean in Voiced overruled if manual filter
	public static boolean isPlaying = false; // is the song currently playing
	public static boolean isSampleRate = false; // option used for recording false=22050, true=44100
	public static boolean isSavePcmData = false; // save audioData output from DecodeFileJava
	public static boolean isShowDetail = false; // show the fft on the visualizerView screen
	public static boolean isShowDefinition = false;  // Show the definition (frequency, distance, energy, quality) on the VisualizerView screen
	public static boolean isSortByName = true; // songList sort by CommonName vs sort by species
	public static boolean isStartRecordScreen = false; // start the app with the record screen showing
	public static boolean isStartRecording = false; // start the app with the record screen showing and start recording
	public static boolean isShowWeb = false; // option if true attempt show xeno-canto or Wikipedia
	public static boolean isStereo = false; // vs mono
	public static boolean isUseAudioRecorder = false; // option true = wav / false media = m4a
	public static boolean isUseLocation = false; // option - limit list in identification
	public static boolean isUseSmoothing = false; // option - use smoothing
	public static boolean isViewDistance = true; // option - display distance in VisualizerView
	public static boolean isViewEnergy = true; // option - display energy in VisualizerView
	public static boolean isViewFrequency = true; // option - display frequency in VisualizerView
	public static boolean isViewQuality = true; // option - display quality in VisualizerView
	public static boolean isWebLink = false; // in WebList if true show web page else Wikipedia or xeno-canto
	public static int latitude = 40; // location can be auto or manual
	//public static float lengthEachRecord = 5.0f; // number of records * lengthEachRecord = size required for dimension of bitmap
	public static int listOffset = 0;
	public static List<String> listPermissionsNeeded;
	public static int longitude = -100; // location can be auto or manual
	public static int lowFreqCutoff = 0;  // user entered from adjust view
	public static int manualLat;
	public static int manualLng;
	public static float maxPower;  // max of fft power calc.
	public static int maxPowerJ = 0; // frequency where power is max (don't know if it is lo or hi for harmonics and percent peak)
	public static int maxPowerRec = 0; // record at which the max power occurred
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
	public static int permCntr = 6;
	public static String[] permissions = new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION };
	public static int phoneLat;
	public static int phoneLng;
	private Button playButton;
	public String qry = "";
	private Button recordButton;
	public static String recordedName;
	public static int[] ref;  // reference (to replace defineName)
	private Cursor rs; // I think of Cursor as Record Set
	private Cursor rsd; // record set Detail
	public static int sampleRate = 22050;
	public static int sampleRateOption = 0;
	public static int[] seg;
	public static int[] selectedSong;
	public static String sharedDefine;
	public static int shortCntr;
	public static Boolean showPlayFromList = false;
	public static Boolean showPlayFromRecord = false;
	public static Boolean showWebFromIdentify = false;
	private Button songButton;
	public static int songCounter = 0;  // count of songs selected (checked)
	public static String songpath = null;   // environment + /birdingviamic/Songs/ or custom
	public static String[] songs; // names from the file (to string)
	public static String[] songsCombined;  // the listing (fileName newLine Spec Inx Seg)
	public static SongData songdata = null;
	public static int songsDbLen; // count of songs in the SongList
	public static File[] songFile; // names from the file (file format)
	public static File songPathDir;
	public static int songStartAtLoc = 0;
	public static int songStopAtLoc = 0;
	public static int sourceMic = 0;
	public static int[] speciesRef;
	public static int specOffset = 0;
	public static Boolean specRenamed = false;
	public static int stereoFlag = 0; // used in sampleRateOption 0=mono / 1=stereo
	//public static int stopAt = 0;
	int targetSdkVersion;
	public static int thisSong = 0;  // current song
	//public static int totalCntr = 0;  // I have to keep this for mediaPlayer and visualizerView
	Toolbar toolbar;
	public static int userRefStart = 40000;
	private int versionNum = 0;
	private String refUpgradeFile = "";
	private Button webButton;
	public static int webOffset = 0;
	public static Boolean webRenamed = false;
	public static boolean wikipedia = true;  // true show identified bird - false bring up a different web site
	public static boolean xenocanto;
	Bundle savedInstanceState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// action bar toolbar
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setLogo(R.drawable.treble_clef_linen);
		toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.teal));
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Navigation Icon tapped");
			}
		});
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		findViewById(R.id.backup_button).setOnClickListener(this);
		findViewById(R.id.help_button).setOnClickListener(this);
		findViewById(R.id.location_button).setOnClickListener(this);
		findViewById(R.id.my_list_button).setOnClickListener(this);
		findViewById(R.id.options_button).setOnClickListener(this);
		findViewById(R.id.play_button).setOnClickListener(this);
		findViewById(R.id.record_button).setOnClickListener(this);
		findViewById(R.id.redlist_button).setOnClickListener(this);
		findViewById(R.id.region_button).setOnClickListener(this);
		findViewById(R.id.register_button).setOnClickListener(this);
		findViewById(R.id.song_button).setOnClickListener(this);
		findViewById(R.id.songpath_button).setOnClickListener(this);
		findViewById(R.id.species_button).setOnClickListener(this);
		findViewById(R.id.web_browser_button).setOnClickListener(this);
		playButton = (Button) findViewById(R.id.play_button);
		recordButton = (Button) findViewById(R.id.record_button);
		songButton = (Button) findViewById(R.id.song_button);
		webButton = (Button) findViewById(R.id.web_browser_button);
		if (Main.songpath == null || Main.songdata == null) {
			isCheckPermissions = true;
			boolean ckPerms = checkPermissions();
			if (ckPerms == true) {
				isCheckPermissions = false;
				init();
				Log.d(TAG, "App is enabled. Permissions granted.");
			} else {
				Log.d(TAG, "App is degraded. Request enable permissions.");
				Intent pd = new Intent(this, PermissionDetail.class);
				startActivityForResult(pd, 99);
			}
		}
	}

	public void init() {
		Log.d(TAG, "**** App is through checking permissions. ****");
		environment = Environment.getExternalStorageDirectory().getAbsolutePath();
		songPathDir = getExternalFilesDir("Song"); // File
		definePathDir = getExternalFilesDir("Define"); // File
		definepath = definePathDir.toString() + "/"; // String
		databaseName = definepath + "BirdSongs.db";
		Log.d(TAG, "onCreate environment:" + environment + " songPathDir:" + songPathDir +
				" definePathDir:" + definePathDir + " databaseName:" + databaseName);
		// test in loadAssets and only load if missing
		Log.d(TAG, "make the Define directory");
		new File(definePathDir.toString()).mkdirs();
		Log.d(TAG, "go load the database");
		//loadAssets("Define");

		Log.d(TAG, "make the Song directory");
		new File(songPathDir.toString()).mkdirs(); // doesn't do any harm if dir exists -- adds if missing
		Log.d(TAG, "go load the song files");
		//loadAssets("Song");

		// I really have fixed the database leaking problem -- this is the ONLY NEW SongData in the WHOLE application
		songdata = new SongData(this, Main.databaseName, null, Main.databaseVersion);
		db = songdata.getWritableDatabase();
		// ******************** WHERE TO GO ?? *************************
		// this will be the old database if it exists -- I don't delete it in loadAssets
		// so I can get the exiting Num out of table Version.
		// If I have a Ref111.csv with Ref and Spec with the new database 
		// it can be loaded during LoadAssets 
		// then I have the existing Ref Spec in the database
		// I think I can use the new java code to access the old CodeName and New Ref111.csv
		// So I should be able to add a RefUgrade table to the existing database.
		// ******************** WHERE TO GO ?? *************************
		// database loaded
		readTheSongPath();
		// I have disabled all but path = 1 here in main.
		songpath = Main.songPathDir.toString() + "/";
		Log.d(TAG, "onCreate definepath:" + definepath + " songpath:" + songpath);
		if (sharedDefine != null) { // these are paths 2-9 that are used in SelectSongPath
			Log.d(TAG, "onCreate sharedDefine:" + sharedDefine);
		}
		readTheOptions();
		commonName = "CommonName: ";
		// ****************************
		//checkVersion();
		// ****************************
		readTheLocationFile();

		if (isStartRecordScreen == true) {
			recordButton.performClick();
		}
	} // init


	private boolean checkPermissions() {
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
	}

	public void onClick(View v) {
		int resId;
		switch (v.getId()) {
			case R.id.record_button: {
				Log.i(TAG, "onClick Record");
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
				Log.i(TAG, "onClick Play");
				wikipedia = true;
				xenocanto = false;
				Intent ps = new Intent(this, PlaySong.class);
				startActivity(ps);
				break;
			}
			case R.id.song_button: {
				Log.i(TAG, "onClick Songs");
				wikipedia = true;
				xenocanto = false;
				isWebLink = false;
				existingRef = 0;
				Intent sl = new Intent(this, SongList.class);
				startActivity(sl);
				break;
			}
			case R.id.songpath_button: {
				Log.i(TAG, "onClick Path");
				Intent sl = new Intent(this, SelectSongPath.class);
				startActivity(sl);
				break;
			}
			case R.id.options_button: {
				Log.i(TAG, "onClick Options");
				Intent o = new Intent(this, Options.class);
				startActivity(o);
				break;
			}
			case R.id.species_button: {
				Log.i(TAG, "onClick Species");
				wikipedia = false;
				xenocanto = true;
				isWebLink = false;
				existingRef = 0;
				Intent esd = new Intent(this, SpeciesList.class);
				startActivity(esd);
				break;
			}

			case R.id.web_browser_button: {
				Log.i(TAG, "onClick WebSites");
				if ((isShowWeb == true) && (wikipedia == true || xenocanto == true) && (existingRef > 0) && (existingRef < userRefStart)) {
					qry = "SELECT Spec FROM CodeName" +
							" WHERE Ref = " + existingRef;
					Cursor rs = songdata.getReadableDatabase().rawQuery(qry, null);
					rs.moveToFirst();
					existingSpec = rs.getString(0);
					rs.close();
					Log.d(TAG, "Start WebSites existingSpec:" + existingSpec);
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
				Log.i(TAG, "onClick RedList");
				Intent red = new Intent(this, RedList.class);
				startActivity(red);
				break;
			}

			case R.id.region_button: {
				Log.i(TAG, "onClick Region");
				Intent rl = new Intent(this, RegionList.class);
				startActivity(rl);
				break;
			}

			case R.id.location_button: {
				Log.i(TAG, "onClick Location");
				Intent sl = new Intent(this, ShowLocation.class);
				startActivity(sl);
				break;
			}

			case R.id.my_list_button: {
				Log.i(TAG, "onClick MyList");
				Intent ml = new Intent(this, MyList.class);
				startActivity(ml);
				break;
			}

			case R.id.backup_button: {
				Log.i(TAG, "onClick Backup");
				Intent bu = new Intent(this, Backup.class);
				startActivity(bu);
				break;
			}

			case R.id.register_button: {
				Log.i(TAG, "onClick Register");
				wikipedia = false;
				xenocanto = false;
				Intent r = new Intent(this, Register.class);
				startActivity(r);
				break;
			}

			case R.id.help_button: {
				Log.i(TAG, "onClick Help");
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
		Log.d(TAG, "onResume isCheckPermissions:" + isCheckPermissions);
		if (isCheckPermissions == true) {
			isCheckPermissions = false;
			return;
		}

		Log.d(TAG, "onResume check songpath or songdata");
		if (songpath == null || songdata == null) {
			Log.d(TAG, "** onResume songpath or songdata is null");
			init();
		}

		Log.d(TAG, "onResume newStartStop:" + isNewStartStop);
		if (isNewStartStop == true) {
			isNewStartStop = false;
			//Intent nss = new Intent(this, PlaySong.class);
			//startActivity(nss);
			playButton.performClick();
		}

		Log.d(TAG, "onResume showPlayFromRecord:" + showPlayFromRecord);
		if (showPlayFromRecord == true) {
			showPlayFromRecord = false;
			// copy the recorded song into the existing name and start the PlaySong screen
			existingName = songpath + recordedName;
			//Intent spfr = new Intent(this, PlaySong.class);
			//startActivity(spfr);
			playButton.performClick();
		}

		Log.d(TAG, "onResume showPlayFromList:" + showPlayFromList);
		if (showPlayFromList == true) {
			showPlayFromList = false;
			// use the generated list of songs
			//Intent spfl = new Intent(this, PlaySong.class);
			//startActivity(spfl);
			playButton.performClick();
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
			//fileRenamed = false;  will be cleared to false later in WebList
			//Intent wn = new Intent(this, WebList.class);
			//startActivity(wn);
			webButton.performClick();
		}

		// keep this next to last -- if nothing else runs and return from play song then go back to the list where you were
		// I had to move it above web -- because web quit working -- web has to be last.
		// fileReshowExisting flag also used on deleted files
		Log.d(TAG, "onResume fileReshowExisting:" + fileReshowExisting);
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
		if (Main.xenocanto == true && existingRef > 0) { //  && isBatchDownload == false) {  // don't load XC files (from here) if startup
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
						val.put("AudioSource", -1);
						val.put("Stereo", -1);
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
	} // onResume


	public void readTheOptions() {
		Log.d(TAG, "readTheOptions");
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
					in = assetManager.open("Define/" + inFile[i]); // in from assets
					Log.i(TAG, "loadAsset in:" + in);
					outFile = new File(definePathDir + "/" + inFile[i]);  // out to definePathDir
					Log.i(TAG, "loadAsset outFile:" + outFile);
					if (outFile.exists()) { // if the database or any file is there already don't load an empty one and trash the users files
						Log.i(TAG, "loadAssets outFile exists -- not loading:" + outFile);
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
	} // loadAssets


	// this loads the files to Define, or Song folders
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	} // copyFile

	public void readTheSongPath() {
		Log.d(TAG, "readTheSongPath");
		qry = "SELECT Path, CustomPath FROM SongPath";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		//Main.path = rs.getInt(0);
		path = 1;  // always point to internal in main
		Main.customPathLocation = rs.getString(1);
		Log.d(TAG, " * * readTheSongPath path:" + path + " customPathLocation:" + customPathLocation);
	} // readTheSongPath

	public void readTheLocationFile() { // not file anymore; it is in database
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
	} // readTheLocationFile


	public String getTime () {  // called during startRecording()
		String format = "HH.mm.ss.mmm";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		long iNow = System.currentTimeMillis();
		String currentTime = "sdf.format(iNow)";  // HH.mm.ss.mmm
		return currentTime;
	} // getTime


    

	
	void checkVersion() { // ONLY RUN THIS ONCE !!!
	    // ****** I NEED TO UNDERSTAND THE FOLLOWING COMMENTS -- AND SAY WHAT IT TRUE *******
		// ****** I THINK PART OF IT IS PAST VERSION, FUTURE VERSION, OR MY IMAGINATION *******
		// this is a one time event
		// it will run if the Define folder already contains Birdsongs.db -- if empty the new one was loaded -- else here to upgrade
		// I used to load RefUpgradeXX.csv but now I build it here using the existing CodeName
		// NO -- the reason is I have the latest version in CodeNameXX.csv but don't know the existing version when I get loaded.
		// NO -- it deletes those two files on completion
		// change Ref to new version In SongList, DefineTotals, DefineDetail, Identify
		// replace CodeName with the new data
		Log.d(TAG, "CheckVersion");
		// upgrade from version from 9.2 to 11.1 -- WITHOUT Loosing the existing songlist or defines.
		// I actually do that in upgradeSpecies
		// the table RefUpgrade needs to contain the Species Reference number for the existing version 9.2 and the new version 11.1
		// It has to be the full file because names and species changed
		// i need a dialog to upgrade -- only ask if upgrade files exist -- yes, later
		// no files return -- else ask for upgrade
		// yes - do it now
		// later - return here the next startup
		// NO -- I have to load the files out of Assets Define into definepath before I get here
		// ?? -- Always during transfer from Assets I delete the files first in Define then if they exist in Assets I transfer them
		// NO -- I retain an empty.csv file if they have been loaded so I don't transfer again. -- NO I don't think I empty a file out !!
		// ?? -- they won't be in the next version -- just the new database which won't be loaded if exists -- true
		// ?? -- And I delete before I transfer so no files
		// ?? -- I can't delete the new RefUpgrade. It needs to be available for future as the existing.
	    // ****** I NEED TO UNDERSTAND THE ABOVE COMMENTS -- AND SAY WHAT IT TRUE *******
		qry = "SELECT Num from Version";  // this is from Version table in the existing database
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		versionNum = rs.getInt(0);  // this is the old version 61 the first time you check and 92 the next time you check
		rs.close();
		Log.i(TAG, "checkVersion versionNum " + versionNum);
        int upgradeCntr = 0;
		Scanner refup = null;
		try {
			// see if it is already loaded
			File dir = new File(definepath);
			File[] files = dir.listFiles();
            String refUpgradeFileName = "RefUpgrade" + versionNum + ".csv";
            String codeNameFileName =  "CodeName" + versionNum + ".csv";
            int checkVersionNum1 = 0;
            int checkVersionNum2 = 0;
            for (File inFile : files) {
                if (inFile.getName().equals(refUpgradeFileName)) {
                    boolean deleted = inFile.delete();
                    Log.d(TAG, "checkVersion version matches -- deleting " + refUpgradeFileName + " " + deleted);
                }
                if (inFile.getName().equals(codeNameFileName)) {
                    boolean deleted = inFile.delete();
                    Log.d(TAG, "checkVersion version matches -- deleting " + codeNameFileName + " " + deleted);
                }
                String nam1 = inFile.getName().substring(0, 10);
                if (nam1.equals("RefUpgrade")) {
                    int dot = inFile.getName().indexOf(".csv");
                    String refUpVer = inFile.getName().substring(10, dot);
                    checkVersionNum1 = Integer.parseInt(refUpVer);
                    if (checkVersionNum1 > versionNum) {
                        Log.d(TAG, "checkVersion RefUpgrade is new file version:" + checkVersionNum1);
                        upgradeCntr++;
						refUpgradeFile = inFile.getName();
                    }
                }
				String nam2 = inFile.getName().substring(0, 8);
                if (nam2.equals("CodeName")) {
                    int dot = inFile.getName().indexOf(".csv");
                    String codeVer = inFile.getName().substring(8, dot);
                    checkVersionNum2 = Integer.parseInt(codeVer);
					if (checkVersionNum2 > versionNum) {
						Log.d(TAG, "checkVersion CodeName is new file version:" + checkVersionNum2);
						upgradeCntr++;
						codeNameFile = inFile.getName();
					}
                }
            } // next
			
            if ((upgradeCntr == 2) && (checkVersionNum1 == checkVersionNum2)) {
				Intent ugd = new Intent(this, UpgradeDialog.class);
				Log.d(TAG, "checkVersion startActivityForResult request upgrade_dialog.");
				Main.myRequest = 1;
				startActivityForResult(ugd, 1);
			} else {
				Log.d(TAG, "Exit checkVersion");
				return;
            }

		} catch (Exception e) {
			// the files don't exist leave quitely.
			Log.d(TAG, "catch checkVersion -- refUpgradeFileXX does NOT Exist:" + e);
			return;
		}
	} // checkVersion
    



	@Override
	protected void onActivityResult ( int requestCode, int resultCode, Intent data) {
		// this was called by StartActivityForResult
		// Check which request we're responding to
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult requestCode:" + Main.myRequest + " resultCode:" + Main.myUpgrade);
		if (requestCode == 1) {
			if (resultCode == 0 || resultCode == 2) {
				return; // later
			}
			//upgradeSpecies(); // else 1 = upgrade
		}
		if (requestCode == 99) {
			Log.d(TAG, "BACK FROM PERMISSIONS requestCode:" + requestCode + " resultCode:" + resultCode);
			init();
		}
	} // onActivityResult
	void upgradeSpecies () {
//		1:  // we are going to convert
		Scanner refup = null;
		int versionNew = 0;
		try {
			refup = new Scanner(new BufferedReader(new FileReader(definepath + refUpgradeFile)));
			Log.d(TAG, "upgradeSpecies: Beyond the dialog. upgradeSpeciesAnswer:" + myUpgrade);
			Log.d(TAG, "upgradeSpecies: We are going to convert");
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
					Log.d(TAG, "upgradeSpecies:" + refUpgradeFile + " versionExist:" + versionExist + " ShouldMatch:" + versionNum
							+ " VersionNew:" + versionNew);
					Log.i(TAG, "versionNum " + versionNum + " time: " + getTime() +  " songpath:" + songpath) ;

					if (versionExist != versionNum) {
						Log.d(TAG, "upgradeSpecies:" + refUpgradeFile + " versionExist:" + versionExist + " FAILED to Match:" + versionNum);
						String msg = "Species Upgrade STOPPED Existing Version:" + versionExist + " FAILED to Match:" + versionNum;
						Log.i(TAG, "versionNum " + versionNum + " time: " + getTime() +  " songpath:" + songpath) ;

						Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
						refup.close();
						return;
					}
				}
				// load the table RefUpgrade from the csv file RefUpgradeXX.csv
				Log.d(TAG, "upgradeSpecies load the table RefUpgrade from the csv file RefUpgradeXX.csv");
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
				Log.d(TAG, "upgradeSpecies close RefUpgrade file ");
				refup.close();
			} catch (Exception e) {
				Log.d(TAG, "upgradeSpecies catch internal error loading RefUpgrade:" + e);
			} finally {
				Log.d(TAG, "upgradeSpecies try -> at finally ");
			}
		} catch (Exception e) {
			// the files don't exist leave quitely.
			Log.d(TAG, "upgradeSpecies Exit -- " + refUpgradeFile + " does NOT Exist:" + e);
			return;
		}

		Log.d(TAG, "upgradeSpecies RefUpgradeXX.csv read in successfully");
		// load the CodeName table
		Scanner codnam;
		try {  // match the new version number in the name
			codnam = new Scanner(new BufferedReader(new FileReader(definepath + "CodeName" + versionNew + ".csv")));
			// it will crash out of this function under "catch" below if file is missing -- else build a dialog
			Log.d(TAG, "upgradeSpecies delete existing from table");
			Main.db.beginTransaction();
			qry = "DELETE FROM CodeName WHERE Ref > 0 AND Ref < 39997";
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			ContentValues val = new ContentValues();
			Log.d(TAG, "upgradeSpecies populate code name with new version");
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
				Log.d(TAG, "upgradeSpecies internal error loading CodeName:" + e);
			} finally {
				codnam.close();
				Log.d(TAG, "upgradeSpecies closed codnam");

			}
		} catch (Exception e) {
			// the files don't exist leave quitely.
			Log.d(TAG, "Exit upgradeSpecies -- CodeName" + versionNew + ".csv does NOT Exist:" + e);
			return;
		}
		Log.d(TAG, "upgradeSpecies CodeName" + versionNew + ".csv read in successfully");

		Log.d(TAG, "upgradeSpecies Update the SongList");

		qry = "SELECT SongList.Ref, RefUpgrade.RefNew" +
				" FROM SongList JOIN RefUpgrade ON SongList.Ref = RefUpgrade.RefExist" +
				" WHERE SongList.Ref > 0 AND SongList.Ref < 39997";
		int bias100k = 100000; // move all the Ref to greater than 100000
		Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cntr = rs.getCount();
		Log.d(TAG, "upgradeSpecies Modify SongList cntr:" + cntr);
		int tempRef = 0;
		// move the existing Ref out of the way so the new ref doesn't conflict with different old refs
		// I dont know if this is necessary but it works
		for (int i = 0; i < cntr; i++) {
			existingRef = rs.getInt(0);
			tempRef = rs.getInt(1);
			Main.db.beginTransaction();
			tempRef += bias100k;
			qry = "UPDATE SongList" +
					" SET Ref = " + tempRef +
					" WHERE Ref = " + existingRef;
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
		Log.d(TAG, "upgradeSpecies SecondPass SongList cntr:" + cntr);
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
		Log.d(TAG, "upgradeSpecies Update DefineTotals");
		qry = "SELECT DefineTotals.Ref, RefUpgrade.RefNew" +
				" FROM DefineTotals JOIN RefUpgrade ON DefineTotals.Ref = RefUpgrade.RefExist" +
				" WHERE DefineTotals.Ref > 0 AND DefineTotals.Ref < 39997";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		cntr = rs.getCount();
		Log.d(TAG, "upgradeSpecies Update DefineTotals cntr:" + cntr);
		tempRef = 0;
		// move the existing Ref out of the way so the new ref doesn't conflict with different old refs
		for (int i = 0; i < cntr; i++) {
			existingRef = rs.getInt(0);
			tempRef = rs.getInt(1);
			Main.db.beginTransaction();
			if (tempRef == 0) {
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

			// upgrade detail for this existing ref -- moved inside loop to return only this existing ref -- crashing on out of memory
			// DefineDetail
			//Log.d(TAG, "upgradeSpecies Update DefineDetail");
			//qry = "SELECT DefineDetail.Ref, RefUpgrade.RefNew" +
			//		" FROM DefineDetail JOIN RefUpgrade ON DefineDetail.Ref = RefUpgrade.RefExist" +
			//		" WHERE DefineDetail.Ref > 0 AND DefineDetail.Ref < 39997;
			qry = "SELECT DefineDetail.Ref" +
					" FROM DefineDetail" +
					" WHERE DefineDetail.Ref =" + existingRef;
			rsd = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			rsd.moveToFirst();
			int cntrd = rsd.getCount();
			//Log.d(TAG, "upgradeSpecies Modify DefineDetail cntr:" + cntr);
			//tempRef = 0;
			// move the existing Ref out of the way so the new ref doesn't conflict with different old refs
			for (int id = 0; id < cntrd; id++) {
				Main.db.beginTransaction();
				if (tempRef == 0) {
					qry = "DELETE FROM DefineDetail WHERE Ref=" + existingRef;
				} else {
					qry = "UPDATE DefineDetail" +
							" SET Ref = " + tempRef +
							" WHERE Ref = " + existingRef;
				}
				Main.db.execSQL(qry);
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
				rsd.moveToNext();
			} // next id
			rsd.close();
			rs.moveToNext();
		} // next i
		rs.close();

		// now remove the bias
		qry = "SELECT Ref FROM DefineTotals WHERE Ref > 100000";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		cntr = rs.getCount();
		Log.d(TAG, "upgradeSpecies SecondPass DefineTotals cntr:" + cntr);
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

			// now remove the bias
			qry = "SELECT Ref FROM DefineDetail WHERE Ref =" + tempRef;
			rsd = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			rsd.moveToFirst();
			int cntrd = rsd.getCount();
			//Log.d(TAG, "upgradeSpecies SecondPass DefineDetail cntr:" + cntr);
			for (int id = 0; id < cntrd; id++) {
				Main.db.beginTransaction();
				qry = "UPDATE DefineDetail" +
						" SET Ref = " + existingRef +
						" WHERE Ref = " + tempRef;
				Main.db.execSQL(qry);
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
				rsd.moveToNext();
			} // next i
			rsd.close();
			rs.moveToNext();
		} // next i
		rs.close();

		Log.d(TAG, "upgradeSpecies Delete From RefUpgrade table");
		Main.db.beginTransaction();
		qry = "DELETE FROM RefUpgrade";
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();

		// you can't upgrade more than once; old to new --> ok; existing new to new --> trash
		Log.d(TAG, "upgradeSpecies Update Version to:" + versionNew);
		Main.db.beginTransaction();
		qry = "UPDATE Version SET Num = " + versionNew;
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();
		qry = "Select Num from Version";
		Main.db.execSQL(qry);
		int Num = rs.getInt(0);
		Log.i(TAG, "versionNum " + Num + " time: " + getTime() + " songpath:" + songpath);


		Log.d(TAG, "upgradeSpecies Delete " + refUpgradeFile);
		File file = new File(definepath + refUpgradeFile);
		if (file.exists()) {
			boolean deleted = file.delete();
		}
		Log.d(TAG, "upgradeSpecies Delete " + codeNameFile);
		file = new File(definepath + codeNameFile);
		if (file.exists()) {
			boolean deleted = file.delete();
		}
		String msg = "Species Upgrade complete now Version:" + versionNew;
		Log.d(TAG, "upgradeSpecies " + msg);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		Main.alertRequest = 6; // database upgrade complete
		Intent dbc = new Intent(this, Alert1ButtonDialog.class);
		startActivityForResult(dbc, Main.alertRequest);  // request == 6 == show database upgrade complete

	} // upgradeSpecies



} // Main


