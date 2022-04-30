package com.modelsw.birdingviamic;
/*

* 55.f birdsongs.db ioc world bird list version 61 -- 2016.03.16
* 55.l fix load definitions -- was failing if song was stereo test main.sourcemic == 0 needed (main.sourcemic & 1) == 0
* 		fix lastknown identified -- in clean database -- was 'identified ' (with space at end), and in runpatch()
* 55.m	use entire screen regardless of song length -- was use part if less than 5 seconds.
*		center time numbers -- was draw time numbers above the time.
* 55.n fix metadata mp3 comment needs to use tcop (next atom below comm) length is wrong and recordists end comments differently.
* 55.t fix m4a and wav players --
* 55.u fixcrashonbuildrampfixrampwithonephrase
* 55.w clean up code
* 55.x rewrite identify to use database instead of memory for each phrase
* 55.y totals working with database before modify detail
* 55.z totals and detail using database once per phrase per ref new identify
* 56.a using database and fix shift on best fit
* 56.b breakout criteria to mean and stddev in identify
* 56.c remove percent from identify doesn't work with one ref per phrase
* 56.d before finding codec changing lengths_unsolved_ver57
* 56.e add permissions testing_ver58
* 56.g cleanup permissions sourcemic and samplerate ver60
* 56.h move batch file inside of select file path no reboot required
* 56.i permissions don't wait until user responds
* 56.j fix permissions wait move load inside path
* 56.k _ver61 plus a few minor changes
* 56.l work on decode
* 56.m add decode file error test ver62
* 56.n fix decoder error ver63
* 56.o add stereo field to songlist
* 56.p fix stereo field getting close to local visualizerview
* 56.q time not the same between functions
* 56.r local visualizer working yea
* 56.s getting pretty clean
* 56.t working on visualizer
* 56.u removed renderer
* 56.v re-added renderer for edit
* 56.w visualizer code the same results not the same
* 56.x using low and hi limits
* 56.y visualizer using average modify help
* 56.z remove accounts requirement from code and manifest_ver64
* 57.a working on realtime plot
* 57.b minor changes extend range in visualizerview
* 57.c extend range of visualizer_ver65
* 57.d start song before decode else song hangs
* 57.e decoder seek works with m4a not with mp3
* 57.f should be same as 57.e just saving in case
* 57.g seek works and fix 48000 adjust_ver66
* 57.h documentation
* 57.i set initial max in plot to remove noise
* 57.j minor cleanup
* 57.k remove noise cleanup decoder and visualizerview _ver67
* 57.l fix filter not saving fullname _ver68
* 57.m if song data null then finish why is flash on playsong
* 57.n under android studio 3.1.4 using sdk 28 -- ver 69 -- loaded 10/28/18
* 57.o add option to save decoded file
* 57.p upgrade from ioc world bird list 61 to version 92 with new birdlife bounding boxes -- 2019.09.12 -- code ver 70
* 57.q move upgrade from main to songlist -- code ver 71
* 57.r move upgrade back to main from songlist - code ver 71 -- ioc version 92
* 57.s copy upgrade logic from 57.n -- use 57.p to load version 92 -- code ver 72
*      add missing savepcmdata to options in database dbversion 82
* 57.t clean with upgrade detail imbedded in totals to avoid memory failure but fails on super.start()
* 57.u not clean but runs with upgrade detail imbedded in totals. -- code ver 73 -- ioc version 92
* 57.v load in google play but complains version 61 after delete and install (did i load ioc version 61 by mistake? )
* 57.w code ver 75; db ver 83; verified ico ver 92
* 57.x with 57.t clean upgrade -- code ver 76; db ver 83; verified ico ver 92
* 57.y start with 57.x -- attempt to fix crash from google robot attempting to run an empty file. code ver 77;
* 		not loaded in google -- i can't make it fail here. -- staying with 57.x
* 57.z upgrade to android 10; sdk 29; ver 78
* ***** failed attempts at laoding ondemand assets:
* 58.a attempt to add assets -- failed and lost code
* 58.b start with 57.z ver 78; run as aab file -- and back up as 58.b -- still using external songs.
* 58.c start with 58.b ver 79; run as aab file -- change assetpack name: birdingviamicassets
* 58.c ver 79; run as aab file -- 4 asset packs songsnw; songsow; songsoa; songsnac;
* 58.d ver 80; -- old world set as install at delivery -- the others set as on demand
* 58.e ver 81; -- attempt to get through step 7 of on demand -- doesnt load
* 58.f ver 82; -- build an apk -- start with 57u; menu -- refactor -- migrate to androidx
* 58.g ver 83; -- start with 58e -- using new install c:/users/owner/.android\keystore.jks alias name: upload
* 58.h ver 84; -- attempt to enable songsxx to be ondemand
* error app-release.aab your android app bundle is signed with the wrong key.
* ensure that your app bundle is signed with the correct signing key and try again:
* sha1: a5:55:f8:49:d6:be:fe:55:ac:d5:4c:c2:24:d8:28:bf:7a:47:62:6e.
* request upload the existing c:/users\owner/.android/keystore.jks to be loaded for com.modelsw.birdingviamic
* 58.h1 ver 84 -- loaded in play as ver 83 (from gradle) with string at 84 --example ondemand does not compile
* i saw an apk (not aab bundle) out there as v 85
* 58.i ver 86 -- working on asset packs.
* 58.j ver 87 -- loading on closed testing -- until i get asset packs working
* 58.k ver 88 -- assetpacklocation -- compiles -- but messed up attempting load asset packs on demand
* 58.l ver 89 -- remove load asset packs code  (the pack are still there - the code to load them is not.
* 58.m_v90 -- clean up select song path so it will install.
* 58.n_v91 -- add load asset pack songsnw in selectsongpath and loadassetpack
* 58.o_v92	-- add loadassetpack class -- add showpermission class
* 58.p_v93 -- store assetpackname on main -- i have removed all the calls in loadassetpack
* 58.q_v94 -- get debug stack overflow in loadassetpack > loadonepack -- totalsize = assetlocation.size(); suspect not seeing asset.
* start over with birdingviamic57zandroid10sdk29asappbundle
* 58.s_v96 -- no ondemand assets -- will load externally. remove addassetpack.java
* 58.t_v97 -- add other external asset names to strings, select_song_path.xml, selectsongpath.java
* 58.u_v98 -- fix crash on play if no song is selected - drop table meta_data it is loaded from song
* 58v.99 -- fix path for nac to remove _ (aka new name) - so it will load
* 58w.100 -- loading new codename before i change to backup and remove.
* 58x.101 -- loadassets was messing up the database and songdata -- backed out to 58o.v92 -- need to save define and song folders in backup
* 58x.101 -- saving one csv file
* 58y.102 -- saving define and song folders to download/define and download/song.
* 58z.103 -- saving csv files to download/define
* 59a.104 -- cleanup backup files
* 59b.105 -- load options from database on startup -- store database in temp folder if database exists in define
* 59c.106 -- transfer the old database to download -- move new database out of temp --  update new database with data from old database
* 59d.107 -- start over with blank database and previously copied define and song folders in the download folder.
* 59e.108 -- success exporting old database csv files in dowlload/define to new database in /birdingviamic/define
* 59f.109 -- remove all but copy folders in backup, fix string too large in help was > 32767, broke out credits and added them below -- success
* 59g.110 -- change location to round was integer
* 59h.111 -- add restore option in backup include websites (was not saving before) -- upgradespecies and converttables not done yet.
* 59i.112 -- upgradespecies and converttables from restore (under backup).
* 			add progress bar to upgrade -- but it doesn't understand time.
* 59j.113 -- fix newnamedialog.java to load the specie from the species list before opening the songs name dialog.
* 59k.114 -- remove south american and african lists they were duplicates.
* 59l.115 -- re-add load assets on startup -- the app can't see the database unless i transfer it.
* 59m.116 -- no changes -- using new keystore birdingviamic.jks
* the last two times i modified and rebuilt my app bundle, i got a bad block error during build on birdingviamic.jks
	i closed android studio, rebooted, reopened, rebuilt, re-selected the key from the keystore -- success -- loaded in google play.
	i could rebuild my keystore but i worry that the sha1 will be different and you will require me to change my app name.
	what do you recommend? -- they recommended build a new keystore -- so here it is.
	keytool -list -v -alias birding -keystore birdingviamic.jks
		 sha1: c2:07:27:75:d4:16:6a:55:f5:7a:5f:64:4c:d1:60:bd:cb:29:3f:59
	THE NEW KEYSTORE WORKS
* old -->   the last keystore was
* old -->   c:/users/owner/.android>keytool -list -v -alias birding -keystore birdingviamic.jks
* old -->  sha1: e3:99:77:ba:b4:c7:6f:b1:c7:46:c7:9e:77:1b:3b:8c:eb:7e:25:c9

* 59n.117 -- work on UpgradeDialog progressBar -- Unsuccessful -- I had to drop back to UpgradeDialog copied from 59l
* 59o.118 -- fix backup -- it was running backup when clicked then restore immediately -- added return at the end of each OnClick case
* 59p.119 -- fix location input for manual input if null set to zero.
* 59q.120 -- clean up StartCnn -- sends existing file to Nvidia for analysis -- BROKEN
* 59r.121 -- start over with send CNN to Nvidia
* 59s.122 -- Starting Send file to Nvidia -- loads file name in MySql on Nvidia -- No Sftp loaded yet
* 59t.123 -- ?
* 59u.124 -- still can't SFTP but this is fix for can't laod external apps (which fails) Android 12 --API 31
* 59w.124 -- start over with app that loads external files with Android 9 -- fails with Android 12
* 60b.125 -- start over with 59w -- before load songs and filter from Download/SongsXX
* 60c.126 -- load songs and filter from Download/SongsXX works Android 10
* 60d.127 -- implement storage permissions from // https://www.youtube.com/watch?v=_IbfUJS13h8 -- attempt from Download folder
* 60e.128 -- attempt to laod from external storage (original NAC location - with new permissions from 60d.127)
* 60f.129 -- remove duplicate permissions code. fix load filter on startup
* 60g.130 -- run after load github load master with Rebase

*	 the tables that use ref: codename, definedetail, definetotals, identify, songlist
*	 the table that could be user modified: birdwebsites, filter, lastknown, location, options, songpath
*	 the tables that i control: redlist, refupgrade, region, version
* i need to but haven't implemented setfastscrollenabled on species list during song rename (can't find it)
* 	 i get around this by telling the user to select the specie first.

 Java Run commands -- https://www.sqlitetutorial.net/sqlite-java/
 	connecting to db, creating db, creating table, insert data, query data, update data, delete data, manage transaction, write/read blob
 Github -- https://github.com/gerardgilliland/BirdingViaMic
		click add file -- choose Upload files -- choose your files --
	 	then enter reason at the bottom -- choose Commit directly to the master branch and click Commit Changes
 NOTE: All (at least most) TrebleClef.jpg images are in: C:\OSJ\GerardRoot
 Also, there are a lot of images and songs at: J:\Users\Gerard
 CHECK build.gradle for the 4 items that need to be updated: 1)versionCode 2)versionName 3)versionName(in string) 4) DatabaseVersion (in Main)
 Where is android studio building my .apk file? >	YourApplication\app\build\outputs\apk name: app-release.apk
 or -- YourApplication\app\release\app.aab
  https://www.youtube.com/watch?v=_IbfUJS13h8
  5:08 take permissions

*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;  //for displaying time
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;  // for displaying time
import java.util.Scanner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.media.AudioManager;
import android.os.Environment;

import static java.lang.Integer.parseInt;


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
	public static int CnnId = 0; // new id created when Option CNN set  CnnId
	public static String customPathLocation = null;
	public static String databaseName; // birdingviamic/Define/BirdSongs.db
	public static int databaseVersion = 88; // increment if change database -- calls SongData
	public static SQLiteDatabase db;
	public static String definepath = null; // birdingviamic/Define
	public static File definePathDir; // file format
	public static String displayName = ""; // used in PlaySong identify to hold identification
	public static String downloadFile;
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
	public String filterFileName = "";
	public static int filterStartAtLoc = 0; // set in AdjustView -- used in PlaySong
	public static int filterStopAtLoc = 0; // set in AdjustView -- used in PlaySong
	public static int highFreqCutoff = 0;  // user entered from adjust view
	public static float hzPerStep = 11025f / 512f;  // 21.53 hz per step -- in the file as hz; 0-511 everywhere else.
	public static int IOC_Version = 0; // it gets set in init -- be sure to make it 0 before loading in Google Play
	public static int[] inx;  // holds existingInx from songList
	public static boolean isAutoLocation = true; // ShowLocation use GPS vs manual
	public static boolean isCheckPermissions = false; // set true if you need to check permissions (Android 6.0+)
	public static boolean isDebug = false; // save extra files
	public static boolean isDecodeBackground = true; // manual=false / background=true
	public static boolean isEdit = false; // set true when edit button on play is tapped
	public static boolean isEnhanceQuality = true; // build s/n kernel and apply to normalized audio
	public static boolean isExternalMic = false; // set true when plugged in else false if internal mic
	public static boolean isFilterExists = false; // has a manual filter been set in AdjustView
	public static boolean isFilterFileExists = false; // does the filter.csv file exist in the Define folder
	public static Boolean isIdentify = true;  // has PlaySong identify button been pushed
	public static boolean isLoadDefinition = false;  // (set in options) any checked in the list -- define if not mic -- identify if mic
	public static boolean isNewStartStop = false; // set in AdjustView for song segment
	public static boolean isAutoFilter = true;  // find mean in Voiced overruled if manual filter
	public static boolean isPlaying = false; // is the song currently playing
	public static boolean isRestoreOldDatabase = false; // the BackupVersion matches versionExists
	public static boolean isRunCnnModel = true;  // run the external Convolutinal Neural Network Model from PlaySong/identify
	public static boolean isSampleRate = false; // option used for recording false=22050, true=44100
	public static boolean isSavePcmData = false; // save audioData output from DecodeFileJava
	public static boolean isShowDetail = true; // show the fft on the visualizerView screen
	public static boolean isShowDefinition = true;  // Show the definition (frequency, distance, energy, quality) on the VisualizerView screen
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
	public static String mFileName;
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
	public static int permCntr = 7;
	public static String[] permissions = new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.MANAGE_EXTERNAL_STORAGE,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION };
	public static int phoneLat;
	public static int phoneLng;
	private Button playButton;
	public String qry = "";
	public String tempqry = "";
	private Button recordButton;
	public static String recordedName;
	public static int[] ref;  // reference (to replace defineName)
	int requestCode;
	int resultCode;
	private Cursor rs; // I think of Cursor as Record Set
	private Cursor temprs; // I think of Cursor as Record Set
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
	public static int specieRefSelected;
	public static String specieSelected;
	public static int[] speciesRef;
	public static int specOffset = 0;
	public static Boolean specRenamed = false;
	public static int stereoFlag = 0; // used in sampleRateOption 0=mono / 1=stereo
	//public static int stopAt = 0;
	int targetSdkVersion;
	public static String tempDatabaseName; // Download/Define/BirdSongs.db
	public static SQLiteDatabase tempdb;
	public static String tempdefinepath = null; // Download/Define/
	public static File tempdefinepathDir; // file format
	public static TempSongData tempsongdata = null;
	public static int thisSong = 0;  // current song
	//public static int totalCntr = 0;  // I have to keep this for mediaPlayer and visualizerView
	Toolbar toolbar;
	public static int userRefStart = 40000;
	private int versionNum = 0;
	public static int versionExist = 0;
	private String refUpgradeFile = "";
	private Button webButton;
	public static int webOffset = 0;
	public static Boolean webRenamed = false;
	public static boolean wikipedia = true;  // true show identified bird - false bring up a different web site
	public static boolean xenocanto;
	Bundle savedInstanceState;

	@RequiresApi(api = Build.VERSION_CODES.R)
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
			Intent pd = new Intent(this, PermissionDetail.class);
			startActivityForResult(pd, 99);
		}
	} // OnCreate

	public void init() {
		Log.d(TAG, "**** App is through checking permissions. ****");
		environment = Environment.getExternalStorageDirectory().getAbsolutePath();
		songPathDir = getExternalFilesDir("Song"); // File
		definePathDir = getExternalFilesDir("Define"); // File
		tempdefinepathDir = new File(environment + "/Download/Define"); // File
		definepath = definePathDir.toString() + "/"; // String
		tempdefinepath = tempdefinepathDir.toString() + "/"; // String
		databaseName = definepath + "BirdSongs.db";
		filterFileName = definepath + "filter.csv";
		tempDatabaseName = tempdefinepath + "BirdSongs.db";
		Log.d(TAG, "init() environment:" + environment + "\n songPathDir:" + songPathDir +
				"\n definePathDir:" + definePathDir + "\n databaseName:" + databaseName);
		Log.d(TAG, "make the Define directory");
		new File(definePathDir.toString()).mkdirs();
		Log.d(TAG, "load the database");
		loadAssets("Define");
		// I really have fixed the database leaking problem -- this is the ONLY NEW SongData in the WHOLE application
		songdata = new SongData(this, Main.databaseName, null, Main.databaseVersion);
		db = songdata.getWritableDatabase();
		// this will be the NEW database
		// get the new IOC_Version out of the new BirdingViaMic/Define/BirdSongs.db
		if (IOC_Version == 0) { // first run thru init
			qry = "SELECT Num FROM Version";
			Cursor rs = songdata.getWritableDatabase().rawQuery(qry, null);
			rs.moveToFirst();
			IOC_Version = rs.getInt(0); // should be 111 -- update
			Log.d(TAG, "Checking IOC_Version: " + IOC_Version);
			rs.close();
			// I can get the old Num out of table Version and compare
			File file = new File(tempDatabaseName);
			if(file.exists()) {
				Log.d(TAG, "database exists:" + tempDatabaseName);
				tempsongdata = new TempSongData(this, Main.tempDatabaseName, null, Main.databaseVersion);
				tempdb = tempsongdata.getWritableDatabase();
				tempqry = "SELECT Num FROM Version";
				temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
				temprs.moveToFirst();
				versionExist = temprs.getInt(0); // now it is not zero -- it should be the old one
				temprs.close();
				Log.d(TAG, "versionExist:" + versionExist + " New IOC Version: " + IOC_Version);
				if (IOC_Version > versionExist) { // the jumping off point has arrived
					Intent ugd = new Intent(this, UpgradeDialog.class);
					Log.d(TAG, "checkVersion startActivityForResult request upgrade_dialog.");
					Main.myRequest = 1;
					startActivityForResult(ugd, 1);
					// if they don't upgrade, on restart, IOC_Version is set to 0
					// so they will get back here the next time they start - but not before
				}
				if (IOC_Version <= versionExist) {
					// IOC_Version = 111
					// if they upgraded versionExist = 100092
					// if they backed up or restored versionExist = 111
					Log.d(TAG, "checkVersion MATCHES or updated.");
					tempdb.close();
				}
			} else {
				versionExist = 0;
			}


		}
		// database loaded
		Log.d(TAG, "make the Song directory");
		new File(songPathDir.toString()).mkdirs(); // doesn't do any harm if dir exists -- adds if missing
		Log.d(TAG, "go load the song files");
		loadAssets("Song");

		readTheSongPath(); // this is path only -- no access to SongList yet
		// I have disabled all but path = 1 here in main.
		songpath = Main.songPathDir.toString() + "/";
		Log.d(TAG, "onCreate \n definepath:" + definepath + "\n songpath:" + songpath);
		if (sharedDefine != null) { // these are paths 2-9 that are used in SelectSongPath
			Log.d(TAG, "onCreate sharedDefine:" + sharedDefine);
		}
		readTheOptions();

		commonName = "CommonName: ";
		readTheLocationFile();

		if (isStartRecordScreen == true) { // will this run ?? -- I have an existing Song Folder only
			recordButton.performClick();
		}
	} // init

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
				Log.d(TAG, "onClick Songs");
				wikipedia = true;
				xenocanto = false;
				isWebLink = false;
				existingRef = 0;
				Intent sl = new Intent(this, SongList.class);
				startActivity(sl);
				break;
			}
			case R.id.songpath_button: {
				Log.d(TAG, "onClick Path");
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
				Log.d(TAG, "onClick Species");
				wikipedia = false;
				xenocanto = true;
				isWebLink = false;
				existingRef = 0;
				Intent esd = new Intent(this, SpeciesList.class);
				startActivity(esd);
				break;
			}

			case R.id.web_browser_button: {
				Log.d(TAG, "onClick WebSites");
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
				Log.d(TAG, "onClick RedList");
				Intent red = new Intent(this, RedList.class);
				startActivity(red);
				break;
			}

			case R.id.region_button: {
				Log.d(TAG, "onClick Region");
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
				Log.d(TAG, "onClick MyList");
				Intent ml = new Intent(this, MyList.class);
				startActivity(ml);
				break;
			}

			case R.id.backup_button: {
				Log.d(TAG, "onClick Backup");
				Intent bu = new Intent(this, Backup.class);
				startActivity(bu);
				break;
			}

			case R.id.register_button: {
				Log.d(TAG, "onClick Register");
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

		Log.d(TAG, "onResume isRestoreOldDatabase: " + isRestoreOldDatabase);
		if (isRestoreOldDatabase == true) {
			isRestoreOldDatabase = false;
			init();  // will force an upgrade
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

		File filter = new File(filterFileName);
		isFilterFileExists = filter.exists();
		if (isFilterFileExists) { // move the filter data from the csv file to the database before you open the song data
			loadFilterData();
		}

		Log.d(TAG, "check option isRunCnnModel: " + isRunCnnModel);
		if (isRunCnnModel == true) {
			if (Main.CnnId == 0) {
				Log.d(TAG, "GetCnnId");
				Intent gci = new Intent(this, GetCnnId.class);
				startActivity(gci);
			}
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
	} // onResume


	public void readTheOptions() { // from the database and update the Main.OptionName object
		Log.d(TAG, "readTheOptions");
		isAutoFilter = OptionList("AutoFilter");
		isEnhanceQuality = OptionList("EnhanceQuality");
		isUseSmoothing = OptionList("UseSmoothing");
		isShowDefinition = OptionList("ShowDefinition");
		isViewDistance = OptionList("ViewDistance");
		isViewEnergy = OptionList("ViewEnergy");
		isViewFrequency = OptionList("ViewFrequency");
		isViewQuality = OptionList("ViewQuality");
		isShowDetail = OptionList("ShowDetail");
		isRunCnnModel = OptionList("RunCnnModel");
		isShowWeb = OptionList("ShowWeb");
		isSortByName = OptionList("SortByName");
		isUseLocation = OptionList("UseLocation");
		isUseAudioRecorder = OptionList("UseAudioRecorder");
		isStereo = OptionList("Stereo");
		isStartRecordScreen = OptionList("StartRecordScreen");
		isStartRecording = OptionList("StartRecording");
		isSavePcmData = OptionList("SavePcmData");
		isLoadDefinition = OptionList("LoadDefinition");
		isDebug = OptionList("Debug");
		// don't call the option list this is not a boolean answer.
		qry = "Select Value from Options WHERE Name = 'CnnId'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst(); // returns  an integer
		Main.CnnId = rs.getInt(0);
		optionsRead = true;
	}

	public boolean OptionList(String nam) {
		qry = "SELECT Value FROM Options WHERE Name = '" + nam + "'";
		rs = songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst(); // returns  an integer
		return (rs.getInt(0) > 0);  // convert int to boolean
	}


	public void loadAssets(String folder) {
		AssetManager assetManager = getAssets(); //Provides access to an application's raw asset files
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
					Log.d(TAG, "loadAsset in:" + in);
					outFile = new File(definePathDir + "/" + inFile[i]);  // out to definePathDir
					Log.d(TAG, "loadAsset outFile:" + outFile);
				} // Define
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

	// the filter.csv should be loaded into the Filter table and the filter.csv should be deleted
	// thus there should be no filter.csv after the load
	void loadFilterData() { // called only on transfer from external data if filter.csv file now exists
		Log.d(TAG, "loadFilterData()");
		Scanner filtr = null;
		File localFilter = null;
		try {
			localFilter = new File(filterFileName); // filter.csv
			filtr = new Scanner(new BufferedReader(new FileReader(localFilter)));
			Log.d(TAG, "loadFilterData in table from new filter.csv");
			// the filter database has FileName XCxxxxxx.m4a, filterType, integerValue
			try {
				String line;
				String[] tokens;
				while ((line = filtr.nextLine()) != null) {
					tokens = line.split(",");
					if (tokens.length == 3) {
						ContentValues val = new ContentValues();
						Main.db.beginTransaction();
						val.put("XcName", tokens[0]);  // it was XC123456 now it is the Full Name XC123456.m4a
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
				Main.isFilterFileExists = true;
				filtr.close(); // does this close cause an exception ? -- file will be deleted on return -- no it is left there
				Log.d(TAG, "newName filter loaded in database: " + Main.isFilterFileExists );
			}
		} catch (Exception e) {
			// the file dosen't exist leave quitely.
			Log.d(TAG, "Exit checkVersion -- filter.csv does NOT Exist:" + e);
			Main.isFilterFileExists = false;
			return;
		}
		boolean success = localFilter.delete();
		Log.d(TAG, " delete filter.csv ?" + success);
	} // loadFilterData

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
			// tempdb.close(); // closed in UpgradeSpecies after updating version number
		}
		if (requestCode == 99) {
			Log.d(TAG, "BACK FROM PERMISSIONS requestCode:" + requestCode + " resultCode:" + resultCode);
			init();
		}
        /*
		if (resultCode == RESULT_OK) {
			if (requestCode == 100) {
				if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
					if (Environment.isExternalStorageManager()) {
						Toast.makeText(this, "Permission Granted in Android 11", Toast.LENGTH_LONG).show();
					} else {
						takePermission();
					}
				}
			}
		} */
	} // onActivityResult


} // Main
