package com.modelsw.birdingviamic;

import static com.modelsw.birdingviamic.Main.existingName;
import static com.modelsw.birdingviamic.Main.isRunCnnModel;
import static com.modelsw.birdingviamic.Main.songPathDir;

import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.database.Cursor;

@TargetApi(10)
public class PlaySong extends AppCompatActivity implements OnClickListener {
	private static final String TAG = "PlaySong";
	int activeSong = 0; // loop thru list
	private String activeRedList = " ";
	private String activeRegion = " ";
	AdjustView av;
	public static float[] audioNorm;
	public static float aveEnergy;
	public float aveEnergyRatio = 1;
	public static int base = 1024;
	private int baseAvailable = 0;
	public static int baseStep = 2;
	RelativeLayout bottomButtons;
	public static float[] bufIn;
	public static float[] bufImagOut;
	public static float[] bufRealOut;
	public static int cntr;
	private int cntSingle = 0;
	float coefB = 0;
	float coefCorr = 0;
	private CurveFit curveFit;
	public static int curVolume = 0;
	boolean debugFile = false; // EVERYTHING to fftCompleteFile -- make it a short file or it won't fit in the spreadsheet
	boolean debugMaxJ = false; // test to writeTestFftAscii
	boolean debugTopFreq = false; // writes to the database -- now disabled and writes to file
	boolean debugMelFilter = false;
	boolean debugId = false;  // Save IdentifyFile
	boolean debugVoiced = true;
	private Button defineButton;
	private int defineInx = 0;
	private int defineRef = 0;
	private int defineSeg = 0;
	long delayTime = 0;
	private DecodeFileJava dfj;
	private int dimFix = 0;
	public static float distance[];
	public static float dtc[];
	public static Button editButton;
	private static float[] energy;
	private static float[] energyPhraseMax;
	public static float energyValue[];
	private static float extent = 1;
	private int filterCntr = 0;
	private boolean filterActive = false;
	public static float filterAve = 0;
	private static int flag = 0;
	private FFTjava fft;
	private FftBas fftbas;
	public static float[] filterKernel;
	private static FileOutputStream fos1 = null;
	private static FileOutputStream fos2 = null;
	private static FileOutputStream fos3 = null;
	private static FileOutputStream fos4 = null;
	private static FileOutputStream fos5 = null;
	private static FileOutputStream fos6 = null;
	public static int[][] freqRank;
	private float fullMult; //doesn't seem to do anything
	private Handler handler;
	public static int highFreqCutoff;
	// for id debug idXXxxxx only used in print log which is currently commented out
	private int idCntName = 0; // count of matches in id
	private float idCoefB = 0;
	private float idCorrCoef = 0;
	private Button identifyButton;
	private int identifiedRef;
	private String idFix = null;
	private int idHighFreq = 0;
	private int idLowFreq = 0;
	private static int idMatchRefInx = 0;
	private float idMDist = 0;
	private float idMEnergy = 0;
	private float idMFreq = 0;  // to compare memory with selected database record
	private float idMSamples = 0;
	private float idMQuality = 0;
	private float idMVoice = 0;
	private String idNam = null;
	private int idPct = 0;
	private int idRecords = 0;
	private int idRef = 0; // reference number
	private float idSdDist = 0;
	private float idSdEnergy = 0;
	private float idSdFreq = 0;
	private float idSdSamples = 0;
	private float idSdQuality = 0;
	private float idSdVoice = 0;
	private float idSilence = 0;
	private float idSilPhr = 0;
	private int idSum = 0;  // totals of mean error (lower is better)
	public static int idistance[];
	private static int[] ienergy;
	public static float[] imagIn;
	private static float[] imagKernelFreq;
	private static int incSize;
	public static int interrupt = 40;
	public static boolean isInit = false;
	//public static boolean isPlaying = false;
	private boolean isSetCriteria = false; // only use this for calculating new identify criteria
	//	1) delete from Identify in aSQLiteManager
	//	2) set this flag true - compile and run the app
	//	3) set debug false (or ignore the results -- they are not valid)
	//	4) run the set of 32 birds in id.txt (autoDef on, enhance on, smoothing on, web off, use location off)
	//	5) in aSQLiteManager select table identify then menu > export to csv file
	//	6) copy identify.csv to computer with file Manager and input to Identify.xls
	//  7) use the average results in the criteria in identify
	public static float lenMs = 0;  // length of time in millisec since start of song
	private int[] locationAtMax;  // used to adjust fft frames
	public static int lowFreqCutoff;
	public static float manFilterAve = 0;
	private static float maxEn; // really max rms energy used to scale energy from bufIn
	public static float maxEnergy; // used as power
	private int maxTCrit = 0;
	private static int maxVoiced = 0;
	public static float[][] maxPwr;
	public static float melFilter[];
	public static float mfcc[][]; // [record][coordinate] cepstra, delta, deltadelta,
	private static String mFileName = null;
	public static MediaPlayer mPlayer;  // static in attempt to retain image
	private float mult = 1f;
	public VisualizerView mVisualizerView;
	public static int numCepstra = 16;  // was 12
	private int numMelFilters = 128;  // gg was 30 -- then 32 now 64 -- WAIT -- IT IS 128
	public static int numSamplesPlayed = 0;
	int cbin[] = new int[numMelFilters + 2];
	private float offset = 0;
	private float originalMean = 0;
	private float pctTop = 0f;
	private int[] phraseAdj;
	private static int phrases;
	int phraseCntr = 0; // count of phrases bounded by silence -- note birders call them strophe
	private int[] phraseEnd;
	private int[] phraseStart;
	private int[] phraseSilence;
	public static int[] pitch;
	private Button playButton;
	private static int previousRef = 0;
	private static int previousInx = 0;
	public static float[] pwr;
	char q = 34;
	private String qry = "";
	public static int quality[];
	private static int qualLow;
	private static int qualHigh;
	public static int rankCntr = 4;
	private static float[] realKernelFreq;
	public static int records;
	int result = 0;
	private static float[] rmsEnergy;
	private Cursor rs;  // I see cursor as RecordSet (rs)
	private Cursor rsTot;
	private int[] samplesToMax;  // now in each record
	public static float scalePxPerMs;
	private static float silPhrRatio = 0;
	private float snMean = 0;
	private float stdDev;
	private float stdLim;
	private long startTime;
	public static int stepSize; // 512 = 1024/2
	float[] temp = new float[base / 2];
	private int tolerance = 20; // number of times to check for data from the database before giving up (increment delta each pass)
	Toolbar toolbar;
	int trillLen = 0;
	public int[] voiced;
	public int[] voicedFrame;
	private static int[] voicedFreq;
	public static int vvtop = 168;
	public static int vvHeight = 1515;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		Context mContext = this;
		Log.d(TAG, "onCreate newStartStop:" + Main.isNewStartStop);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		curVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.d(TAG, "onCreate curVolume:" + curVolume);
		am.setSpeakerphoneOn(true);
		setContentView(R.layout.play_song);
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

		bottomButtons = (RelativeLayout) findViewById(R.id.bottom_buttons);
		playButton = (Button) findViewById(R.id.play_button);
		//buttonHeight = playButton.getHeight(); // it is zero here
		//Log.d(TAG, "onCreate buttonHeight:" + buttonHeight);
		findViewById(R.id.play_button).setOnClickListener(this);
		editButton = (Button) findViewById(R.id.edit_button);  // why does the define button hold teal and the edit button does not hold teal
		findViewById(R.id.edit_button).setOnClickListener(this);
		defineButton = (Button) findViewById(R.id.define_button);
		findViewById(R.id.define_button).setOnClickListener(this);
		identifyButton = (Button) findViewById(R.id.identify_button);
		findViewById(R.id.identify_button).setOnClickListener(this);
		mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
		mVisualizerView.mCanvasBitmap = null;
		mVisualizerView.mCanvas = null;
		mVisualizerView.mPaint = null;
		if (songPathDir == null || Main.songdata == null) {
			finish();
			return;
		}
		mFileName = Main.songpath + existingName;
		if (mFileName == "") {
			String msg = "Please select a file to Play.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			Log.d(TAG, msg);
			finish();
			return;
		}
		Main.db = Main.songdata.getWritableDatabase();
		Main.isNewStartStop = false;
		dfj = new DecodeFileJava();
		fft = new FFTjava();
		fftbas = new FftBas();
		curveFit = new CurveFit();
		handler = new Handler();
		if (Main.isLoadDefinition == true) {
			playButton.performClick();
		}
		Main.fileReshowExisting = true;  // if no other activities are done.
	}

	public void onClick(View v) {
		//stopPlaying();
		switch (v.getId()) {
			case R.id.play_button: {
				Log.d(TAG, "Play pressed");
				Main.adjustViewOption = "";
				Main.isEdit = false;  // re-added 3/10/16
				Main.isPlaying = true;
				Main.isIdentify = false;
				//removeAdjustView();
				try {
					startSong(v);
					// added these two from Edit - now the frequency and time and bird name show up !!!
					addLineRenderer();
					showAdjustView();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case R.id.edit_button: {
				Log.d(TAG, "0) Edit button -- call showAdjustView");
				Main.adjustViewOption = "";
				Main.isEdit = true;
				Main.isPlaying = false;
				Main.isIdentify = false;
				stopPlaying();
				editButton.setBackgroundColor(getResources().getColor(R.color.teal));
				editButton.setTextColor(getResources().getColor(R.color.linen));
				if (mVisualizerView != null) {
					mVisualizerView.release();
				}
				addLineRenderer();
				showAdjustView();
				break;
			}
			case R.id.define_button: {
				Log.d(TAG, "Define pressed");
				Main.adjustViewOption = "";
				Main.isEdit = false;
				Main.isPlaying = false;
				Main.isIdentify = false;
				stopPlaying();
				removeAdjustView();
				if (mVisualizerView != null) {
					mVisualizerView.release();
				}
				definePressed(v);
				break;
			}
			case R.id.identify_button: {
				Log.d(TAG, "identify pressed:");
				Main.isEdit = false;
				Main.isPlaying = false;
				Main.isIdentify = true;
				stopPlaying();
				removeAdjustView();
				if (mVisualizerView != null) {
					mVisualizerView.release();
				}
				/*
				Android SFTP songs from BirdingViaMic to Host (modelsw server)
				which will transfer the songs to NVIDIA model
				which will attempt an ID and send the Bird Name reference number back ...
				*/
				Log.d(TAG, "Main.isRunCnnModel: " + isRunCnnModel);
				// send this to the model before starting internal identify
				// ??????????????? can I move this to play button ????????????????????? NOT YET
				if (isRunCnnModel == true) {
					Log.d(TAG, "RunCnnModel Server defined");
					// open web page that updates MySqlServer with file to be passed
					//String data = "https://www.modelsw.com/Nvidia/GetSpecieRef.php?FileName=" + existingName;
					//String data = "https://www.modelsw.com/Nvidia/GetSpecieRef.php?FileName=/storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/" + existingName;
					Log.d(TAG, "existingName: " + existingName + " length: " + existingName.length() );
					if (existingName.length() > 0) {
						Intent intent = new Intent(this, StartCnn.class); // updates MySql on server
						Log.d(TAG, "Run StartCnn");
						startActivity(intent);
						// returns to here
					}
				}
				identifyPressed(v);
				break;
			}
		} //switch
	}

	void definePressed(View v) {
		Log.d(TAG, "definePressed ref:" + Main.existingRef);
		if (Main.existingRef == 0) {
			Toast.makeText(this, "You can NOT define an Unknown, Sorry!", Toast.LENGTH_LONG).show();
			Log.d(TAG, "toast: You can NOT define an Unknown, Sorry!");
			return;
		} else {
			if (Main.isLoadDefinition == true) {
				Main.isIdentify = false;
				if (isSetCriteria == true) {
					qry = "DELETE from DefineDetail";
					Main.db.execSQL(qry);
					Log.d(TAG, "Delete from DefineDetail");
					qry = "DELETE from DefineTotals";
					Main.db.execSQL(qry);
					Log.d(TAG, "Delete from DefineTotals");
				}
			}
			identifyPressed(v);
			//Log.d(TAG, "visualizer detail filterAve:" + VisualizerView.filterAve);

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		if (rs != null) {
			rs.close();
		}
		if (rsTot != null) {
			rsTot.close();
		}
		mVisualizerView.release(); // gg added 2/28/16
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		cleanUp();
	}

	public void stopPlaying() {
		Log.d(TAG, "stopPlaying()");
		if (mPlayer == null) {
			return;
		}
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
		}
		mPlayer.reset();
		mPlayer.release();
		mPlayer = null;
		Main.isPlaying = false;
		mVisualizerView.release();  // this stopped the VisualizerView from crashing
	}

	private void cleanUp() {
		Log.d(TAG, "cleanUp");
		if (mPlayer != null) {
			mVisualizerView.release();
			removeAdjustView();
			mPlayer.release();
			mPlayer = null;
			Main.isPlaying = false;
		}
		if (rs != null) {
			rs.close();
		}
		if (rsTot != null) {
			rsTot.close();
		}
		if (Main.db != null) {
			//db.releaseReference();
			//db.close();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		clearEdit();

	}

	public void clearEdit() {
		editButton.setBackgroundColor(getResources().getColor(R.color.linen));
		editButton.setTextColor(getResources().getColor(R.color.teal));
		Main.isEdit = false;
		editButton.invalidate();
	}

	protected void showAdjustView() {
		// I click the Id button and instead of going to read the file I am plotting what has already been plotted.
		// this is wasting 2 seconds (depending on complexity of data)
		// you can see this -- touch id -- and after plot the Id button is recognized.
		// click Id -- if just visualizer view is showing - it goes for the data -- else is plots existing before
		// click Def -- if not defined it works as it should  -- if defined it hangs as well.
		Log.d(TAG, "1) **** showAdjustView before loading AdjustView");
		vvtop = toolbar.getHeight();
		vvHeight = mVisualizerView.getBottom();
		av = new AdjustView(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		Log.d(TAG, "1a) **** showAdjustView vvtop:" + vvtop + " av:" + av);
		addContentView(av, params);
		Log.d(TAG, "1b) **** showAdjustView after addContentView");
	}

	protected void removeAdjustView() {
		if (av != null) {
			((ViewGroup) av.getParent()).removeView(av);
			av.invalidate();
			av = null;
		}
		clearEdit();
	}

	private void addLineRenderer() {
		Log.d(TAG, "addLineRenderer" );
		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(24);
		linePaint.setAntiAlias(true);
		linePaint.setColor(Color.argb(255, 255, 255, 255));
		int top = mVisualizerView.getTop();
		int bottom = mVisualizerView.getBottom();
		int left = mVisualizerView.getLeft();
		int right = mVisualizerView.getRight();
		//Main.lengthEachRecord = ((float) (bottom-top) / (float) (Main.totalCntr+1));
		// it can go from startup to record to play without getting button height thus no text size
		Main.buttonHeight = playButton.getHeight();
		//Main.buttonHeight  = playButton.getBottom() - playButton.getTop(); // works
		Log.d(TAG, "*  *  addLineRenderer buttonHeight:" + Main.buttonHeight);
		Log.d(TAG, "*  *  Toolbar top:" + toolbar.getTop() + " bottom:" + toolbar.getBottom() + " left:" + toolbar.getLeft() + " right:" + toolbar.getRight());
		Log.d(TAG, "*  *  VisualizerView top:" + top + " bottom:" + bottom + " left:" + left + " right:" + right);
		Log.d(TAG, "*  *  Buttons top:" + bottomButtons.getTop() + " bottom:" + bottomButtons.getBottom() + " left:" + bottomButtons.getLeft() + " right:" + bottomButtons.getRight());
		Log.d(TAG, "*  *  playButton top:" + playButton.getTop() + " bottom:" + playButton.getBottom() + " left:" + playButton.getLeft() + " right:" + playButton.getRight());
		//Log.d(TAG, "*  *  before addLineRenerer Main.lengthEachRecord:" + Main.lengthEachRecord);
		//LineRenderer lineRenderer = new LineRenderer(linePaint);
		//mVisualizerView.addRenderer(lineRenderer);
		isInit = true; // used in AdjustView
	}

	public void startSong(View view) throws IllegalStateException, IOException {
		//isInit = true; // used in AdjustView
		if (mPlayer != null) cleanUp();
		Log.d(TAG, "startSong isNewStartStop:" + Main.isNewStartStop + " songCounter:" + Main.songCounter
				+ " thisSong:" + Main.thisSong + " activeSong:" + activeSong);
		if (activeSong == Main.songCounter && Main.isLoadDefinition == false) {
			activeSong--;  // use the last known
		}
		if ((Main.songCounter > 0) && (activeSong < Main.songCounter)) {
			Main.thisSong = activeSong;
			existingName = Main.songs[Main.selectedSong[Main.thisSong]]; // first song selected
			Log.d(TAG, "startSong inside if .. Main.songpath:" + Main.songpath);
			Log.d(TAG, "startSong inside if .. existingName:" + existingName);
			mFileName = Main.songpath + existingName;
			Log.d(TAG, "startSong inside if .. mFileName:" + mFileName);
			Main.existingRef = Main.ref[Main.selectedSong[Main.thisSong]];
			Main.existingInx = Main.inx[Main.selectedSong[Main.thisSong]];
			Main.existingSeg = Main.seg[Main.selectedSong[Main.thisSong]];
			Log.d(TAG, "startSong ref:" + Main.existingRef);
			qry = "SELECT Start, Stop, LowFreqCutoff, HighFreqCutoff, FilterStart, FilterStop, SourceMic FROM SongList" +
					" WHERE FileName = " + q + existingName + q +
					" AND Ref = " + Main.existingRef +
					" AND Inx = " + Main.existingInx +
					" AND Seg = " + Main.existingSeg +
					" AND Path = " + Main.path;
//            Log.d(TAG, "startSong qry:" + qry);
			rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			int cntr = rs.getCount();
			if (cntr > 0) {
				rs.moveToFirst();
				Main.songStartAtLoc = rs.getInt(0);
				Main.songStopAtLoc = rs.getInt(1);
				Main.lowFreqCutoff = (int) (((float)rs.getInt(2)/Main.hzPerStep)+ 0.5f);  // frequency in the database; 0->511 everywhere else
				Main.highFreqCutoff = (int) (((float)rs.getInt(3)/Main.hzPerStep)+ 0.5f);
				Main.filterStartAtLoc = rs.getInt(4) ; // millisec
				Main.filterStopAtLoc = rs.getInt(5);
				Main.sourceMic = rs.getInt(6);
				Log.d(TAG, "startSong SongList selected:" + mFileName + " thisSong:" + Main.thisSong);
				Log.d(TAG, "startSong newStartStop:" + Main.isNewStartStop + " songStartAtLoc:" + Main.songStartAtLoc + " songStopAtLoc:" + Main.songStopAtLoc);
				Log.d(TAG, "startSong filterStartAtLoc:" + Main.filterStartAtLoc + " filterStopAtLoc:" + Main.filterStopAtLoc);
				Log.d(TAG, "startSong lowFreqCutoff:" + Main.lowFreqCutoff + " highFreqCutoff:" + Main.highFreqCutoff + " sourceMic:" + Main.sourceMic );
			} else {
				Main.songStartAtLoc = 0;
				Main.songStopAtLoc = 0;
				Main.lowFreqCutoff = 0;
				Main.highFreqCutoff = 0;
				Main.filterStartAtLoc = 0;
				Main.filterStopAtLoc = 0;
			}
			rs.close();
			activeSong++;  // for next pass

		} else { // it is not on the list yet it has just been recorded or auto define is done
			if (Main.isLoadDefinition == true) {
				Main.songCounter = 0;
				Log.d(TAG, "startSong else .. Main.isLoadDefinition:" + Main.isLoadDefinition);
				Toast.makeText(this, "LoadDefinitions completed.", Toast.LENGTH_LONG).show();
				cleanUp();
				finish();
				return;
			}
			existingName = Main.recordedName;
			Log.d(TAG, "startSong else .. Main.songpath:" + Main.songpath);
			Log.d(TAG, "startSong else .. existingName:" + existingName);
			mFileName = Main.songpath + existingName;  // use last known
			Log.d(TAG, "startSong else .. mFileName:" + mFileName);
			Main.songStartAtLoc = 0;
			Main.songStopAtLoc = 0;
			Main.lowFreqCutoff = 0;
			Main.highFreqCutoff = 0;
			Main.filterStartAtLoc = 0;
			Main.filterStopAtLoc = 0;
		}
		if (existingName == null) {
			Log.d(TAG, "No songs selected from the list or it is recently recorded");
			Toast.makeText(this, "Please select a song from the list or record a song to play.", Toast.LENGTH_LONG).show();
			cleanUp();
		}

		// don't know if these two are needed
		mVisualizerView.mCanvasBitmap = null;
		mVisualizerView.mCanvas = null;

		try {
			Log.d(TAG, "before mPlayer = new MediaPlayer");
			if (mPlayer == null) {
				Log.d(TAG, "mPlayer==null" );
				mPlayer = new MediaPlayer();
			} else {
				Log.d(TAG, "else before mPlayer.reset" );
				mPlayer.reset();
			}
			// ???????????????????????????????????????????????????????????????????????????????
			// I have been missing mFileName, Frequency, and Time on PlaySong.
			// I have been getting the lines around the edge
			// on trying to add any text to the canvas I failed
			// BUT I edited song, saved it, and it had the fileName, Frequency, and time when played.
			// AND now they are back when I play any song.
			// when are they initialized ??
			// study VisualizerView -- it is all there !!
			// ???????????????????????????????????????????????????????????????????????????????
			Log.d(TAG, "existingName to be played / visualized:" + existingName);
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			Main.duration = mPlayer.getDuration();    // millisec

			Main.audioDataLength = (int) ((float) Main.duration / 1000f * 22050);  // samples = sec * samples/sec
			//Main.audioDataLength -= Main.audioDataLength % base;
			Log.d(TAG, "audioDataLength:" + Main.audioDataLength + " duration:" + Main.duration);
			//Main.stopAt = (Main.audioDataLength) / 2048;  // records  22050/11025 = 2 * 1024 = 2048
			//Main.totalCntr = Main.stopAt + 1;  // records the 1 is to avoid overflow.
			int seek = 0; // millisec
			Main.audioDataSeek = 0; // samples
			if (Main.songStartAtLoc > 0 || Main.songStopAtLoc > 0) {
				seek = (int) Main.songStartAtLoc;  // millisec
				Main.audioDataSeek = (int) (float) (Main.songStartAtLoc / 1000f * 22050); // samples  Visualizer View seek to record number
				if (Main.songStopAtLoc == 0) {
					Main.songStopAtLoc = Main.duration;
				}
				Main.duration = Main.songStopAtLoc - Main.songStartAtLoc; // millisec
				Main.audioDataLength = (int) ((float) Main.duration / 1000f * 22050);  // samples = sec * samples/sec

				//Main.stopAt = (Main.audioDataLength) / 2048;  // records  22050/11025 = 2 * 1024 = 2048
				//Main.totalCntr = Main.stopAt + 1;  // records the 1 is to avoid overflow.
				Log.d(TAG, "mPlayer NEW duration:" + Main.duration + " NEW audioDataLength:" + Main.audioDataLength );
			}
			Log.d(TAG, "startPlaying: seek:" + seek);
			mPlayer.seekTo(seek);
			//Log.d(TAG, "startPlaying: Main.totalCntr:" + Main.totalCntr +  " stopAt:" + Main.stopAt );
			Log.d(TAG, "startPlaying: duration:" + Main.duration);
			result = 0;
			filterCntr = 0;
			filterActive = false;
			Log.d(TAG, "startSong: isLoadDefinition:" + Main.isLoadDefinition);
			if (Main.isLoadDefinition == true) {
				stopPlaying();
				if (Main.sourceMic == 0) {  // SourceMic  0=pre-recorded 1=internal 2=external
					defineButton.performClick();
				} else {
					identifyButton.performClick();
				}
			} else {
				Main.isPlaying = true;
				Calendar cal = Calendar.getInstance();
				Log.d(TAG, "gc start");
				System.gc();
				Log.d(TAG, "gc complete");
				/*if (Main.audioDataLength == 0) {
					String msg = "Are you a robot? Please select a file to Play.";
					Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
					Log.d(TAG, msg);
					finish();
					return;
				}*/
				mPlayer.start();
				startTime = cal.getTimeInMillis();
				Log.d(TAG, "1) startSong: startTime:" + startTime);
				Main.shortCntr = 0;
				mVisualizerView.flash(); // shortCntr still 0 -- fill in the axes
				Log.d(TAG, "2) decodeFile in background");
				new Thread(new Runnable() {
					public void run() {
						Log.d(TAG, "start getPcmData with Decode java");
						Main.isDecodeBackground = true;
						result = dfj.getPcmData(); // put it in the short bigendian buffer audioData[]
					}
				}).start();
				bufIn = new float [base]; // supplies 1024 floats
				imagIn = new float [base];  // default filled with zeros
				bufRealOut = new float [base]; // returns 1024 floats -- 512 real floats and 512 inverted (not usable) floats
				bufImagOut = new float [base]; // returns 1024 floats -- 512 real floats and 512 imaginary inverse floats (not usable data)
				long delay = 0;
				// wait for decode to have some data
				int testPeriod = 22050 * 4; // number of seconds to number of shorts
				int mBase = Math.min(testPeriod, (int)(22.050 * Main.duration-base));
				while (Main.shortCntr < mBase) {
					long now = cal.getTimeInMillis();
					delay = now - startTime;
					if (delay > 1000) break;
				}
				Log.d(TAG, "3) delay:" + delay + " shortCntr:" + Main.shortCntr);
				Log.d(TAG, "4) start runnable: startTime:" + startTime + " isPlaying:" + Main.isPlaying);
				// a quick look for max in first testPeriod seconds
				int mMax = 0;
				int mLoc = 0;
				for (int j = 0; j< mBase; j++) {
					if (mMax < Main.audioData[j]) {
						mMax = Main.audioData[j];
						mLoc = j;
					}
				}
				Log.d(TAG, "4a) mLoc:" + mLoc + " mMax:" + mMax);
				mLoc -= mLoc%base;
				Log.d(TAG, "4b) mLoc:" + mLoc + " mMax:" + mMax);
				for (int i=0; i<base; i++) {
					bufIn[i] = (float)(Main.audioData[mLoc+i]);
				}
				fft.windowFunc(3, base, bufIn); // hanning -- sharper than hamming
				fftbas.fourierTransform(base, bufIn, imagIn, bufRealOut, bufImagOut, false);
				mVisualizerView.setInitialMax();
				handler.postDelayed(runnable, 1);
			}
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed on file:" + mFileName);
			//	Toast.makeText(this, "Suspect file format -- failed to Read " + mFileName, Toast.LENGTH_LONG).show();
			Main.isPlaying = false;
			result = 0;
			return;
		}

		if(mPlayer != null) {
			Log.d(TAG, "mPlayer setOnCompletionListener isPlaying:" + Main.isPlaying );
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				// @Override
				public void onCompletion(MediaPlayer mediaPlayer) {
					stopPlaying();
					Log.d(TAG, "startSong onCompletionListener isPlaying=" + Main.isPlaying );
				}
			});
		}

		if(mPlayer != null) {
			Log.d(TAG, "mPlayer setOnErrorListener isPlaying:" + Main.isPlaying );
			mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
					stopPlaying();
					Log.d(TAG, "startSong onErrorListener what:" + what + " extra:" + extra );
					return false;
				}
			});
		}

	} // startSong

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// find the location in the extracted file.
			// file is 22050 bytes saved X file length in seconds.
			// or 22.050 bytes saved each ms.
			Calendar cal = Calendar.getInstance();
			long now = cal.getTimeInMillis();
			lenMs = (float) (now - startTime); // location at this time.
			int len = (int) lenMs;
			// note this 22.050 samples / mSec is samples stored
			// when file is 44.100 samples / mSec mono or 22.050 stereo, I store every other one
			float recf = lenMs * 22.050f; // samples per mSec location in the file in samples
			numSamplesPlayed = (int) recf;
			//Log.d(TAG, "runnable.run() isPlaying:" + Main.isPlaying + " len(ms):" + len + " duration:" + Main.duration);
			if ((len < Main.duration) && Main.isPlaying) {
				handler.postDelayed(this, interrupt);
				updateVisualize();
			} else {
				Log.d(TAG, "runnable isPlaying:" + Main.isPlaying + " len(ms):" + len + " duration:" + Main.duration);
				Log.d(TAG, "final played:" + numSamplesPlayed + " decoded:" + Main.shortCntr);
				handler.removeCallbacksAndMessages(null);
				Log.d(TAG, "removeCallbacksAndMessages");
				stopPlaying();
			}
		}
	};

	public void updateVisualize() {
		//Log.d(TAG, "updateVisualize played:" + numSamplesPlayed + " decoded:" + Main.shortCntr);
		if ((numSamplesPlayed+base) < Main.shortCntr) {
			for (int i=0; i<base; i++) {
				bufIn[i] = (float)(Main.audioData[numSamplesPlayed+i]);
			}
			fft.windowFunc(3, base, bufIn); // hanning -- sharper than hamming
			fftbas.fourierTransform(base, bufIn, imagIn, bufRealOut, bufImagOut, false);
			mVisualizerView.flash();
		}
	}

	// not used
	void warningMessage (final CharSequence idText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(idText)
				.setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Log.d(TAG, "warning:" + idText);
					}
				});
		builder.show();
	}

	public void identifyPressed(View view) {
		Log.d(TAG, "identify or Define (common)- decode result:" + result );
		String msg;
		if (Main.audioDataLength == 0) {
			msg = "Can not identify file.";  // tapped id instead of play
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			return;
		}
		if (result == 0) { // hasn't been run -- it returns 1 on success
			Main.isDecodeBackground = false;
			result = dfj.getPcmData(); // put it in the short bigendian buffer audioData[]
		}
		if (result <= 0) {
			if (result == 0) {
				msg = "Unable to analyze the file";
			}
			if (result == -1) {
				msg = "Unable to analyze the file -- suspect Sample Rate";
			} else if (result == -2) {
				//Main.isLoadDefinition = false;
				msg = "Decoder failed on " + existingName;
				Main.db.beginTransaction();
				if (Main.isIdentify == true) {
					qry = "UPDATE SongList SET Identified = 2";
				} else {
					qry = "UPDATE SongList SET Defined = 2";
				}
				qry +=	" WHERE FileName = " + q + existingName + q +
						" AND Path = " + Main.path +
						" AND Ref = " + Main.existingRef +
						" AND Inx = " + Main.existingInx +
						" AND Seg = " + Main.existingSeg;
				Main.db.execSQL(qry);
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
			} else if (result == -3) {
				msg = "Codec interrupted before completion";
			} else {
				msg = "Unable to read the file";
			}
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			Main.songCounter = 0;
			Main.fileReshowExisting = true;
			cleanUp();
			finish();
			result = 0;
			return;
		}
		Log.d(TAG, "return from Decode java");
		String idButton = "identify"; // only used in log
		Main.db.beginTransaction();
		if (Main.isIdentify == true) {
			Log.d(TAG, "identifyPressed");
			defineRef = 0;
			defineInx = 0;
			defineSeg = 0;
			if (isSetCriteria == false) {
				qry = "DELETE from Identify";
				Main.db.execSQL(qry);
				Log.d(TAG, "Delete from Identify file");
			}
		} else {
			idButton = "define";
			Log.d(TAG, "definePressed");
			defineRef = Main.existingRef;
			defineInx = Main.existingInx;
			defineSeg = Main.existingSeg;
		}
		Log.d(TAG, "run delete queries" );
		qry = "DELETE from DefineTotals" +
				" WHERE Ref = " + defineRef +
				" AND Inx = " + defineInx +
				" AND Seg = " + defineSeg;
		Main.db.execSQL(qry);
		qry = "DELETE from DefineDetail" +
				" WHERE Ref = " + defineRef +
				" AND Inx = " + defineInx +
				" AND Seg = " + defineSeg;
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();

		Log.d(TAG, "start buildFft()");
		result = buildFft();
		Log.d(TAG, "return from buildFft() result:" + result);
		if (result == -1) {
			return;
		}

		if (Main.isLoadDefinition == true) { // continue on loading definitions additional
			if (Main.songCounter > 0) {
				playButton.performClick();
				return;
			} else {
				return;
			}
		}

		if (Main.isShowDetail == true || Main.isShowDefinition == true) {
			Log.d(TAG, "show Defined or Detail data in VisualizerView");
			if (Main.isShowDefinition == true && Main.isShowDetail == false) {
				Main.adjustViewOption = "showDefinitionData";
			}
			if (Main.isShowDefinition == true && Main.isShowDetail == true) {
				Main.adjustViewOption = "showDetailAndDefinitionData";
			}

			mVisualizerView.flash();
		}

	}


	// ******************************* identify ***********************************
	// called from fft because I need to identify phrases as they occur. --- MEMORY
	void identify(int mode) {
		//Log.d(TAG, "identify mode:" + mode + " pc:" + pc);
		int pc = 0;
		if (mode == 0) {
			maxTCrit = 0;
			qry = "SELECT Area FROM Region WHERE IsSelected = 1";
			Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			rs.moveToFirst();
			activeRegion = " ";  // " LA MA NA SA "
			while (!rs.isAfterLast()) {
				activeRegion += rs.getString(0)+ " ";
				rs.moveToNext();
			}
			Log.d(TAG, "active region:" + activeRegion);
			rs.close();
			qry = "SELECT Type FROM RedList WHERE IsSelected = 1";
			rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			rs.moveToFirst();
			activeRedList = " ";  // " NE DD LC NT ? "
			while (!rs.isAfterLast()) {
				activeRedList += rs.getString(0)+ " ";
				rs.moveToNext();
			}
			rs.close();
			Log.d(TAG, "activeRedList:" + activeRedList);

			return;
		}
		if (mode == 1) { // find the unknown -- read each total from the file
			qry = "SELECT Ref, Inx, Seg, Phrase, Silence, Records, " +
					" FreqMean, FreqStdDev, VoicedMean, VoicedStdDev," +
					" EnergyMean, EnergyStdDev, DistMean, DistStdDev," +
					" QualityMean, QualityStdDev, SampMean, SampStdDev, Slope, SilPhrRatio " +
					" FROM DefineTotals WHERE Ref = 0 ORDER BY Phrase";
			Cursor rsUnk = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			int phraseCntr = rsUnk.getCount();
			rsUnk.moveToFirst();
			for (pc = 0; pc < phraseCntr; pc++) {
				//pc = rsUnk.getInt(3); // current phrase
				int iSilence = rsUnk.getInt(4);
				int phraseLen = rsUnk.getInt(5);
				float meanFreq = rsUnk.getFloat(6);
				float stdDevFreq = rsUnk.getFloat(7);
				float meanVoiced = rsUnk.getFloat(8);
				float stdDevVoiced = rsUnk.getFloat(9);
				float meanEnergy = rsUnk.getFloat(10);
				float stdDevEnergy = rsUnk.getFloat(11);
				float meanDist = rsUnk.getFloat(12);
				float stdDevDist = rsUnk.getFloat(13);
				float meanQuality = rsUnk.getFloat(14);
				float stdDevQuality = rsUnk.getFloat(15);
				float meanSamples = rsUnk.getFloat(16);
				float stdDevSamples = rsUnk.getFloat(17);
				float slopeU = rsUnk.getFloat(18);
				float silPhrRatioU = rsUnk.getFloat(19);

				qry = "SELECT Phrase, Record, Freq, Voiced, Energy, Distance, Quality, Samp" +
						" FROM DefineDetail WHERE Ref = 0 AND Phrase =" + pc + " ORDER BY Record";
				Cursor rsUnkD = Main.songdata.getReadableDatabase().rawQuery(qry, null);
				phraseLen = rsUnkD.getCount();
				rsUnkD.moveToFirst();
				int[] pitch = new int[phraseLen];
				int[] voPhrase = new int[phraseLen];
				int[] ienergy = new int[phraseLen];
				int[] idistance = new int[phraseLen];
				int[] quality = new int[phraseLen];
				int[] stmPhrase = new int[phraseLen];
				for (int k = 0; k<phraseLen; k++) {
					pitch[k] = rsUnkD.getInt(2);
					voPhrase[k] = rsUnkD.getInt(3);
					ienergy[k] = rsUnkD.getInt(4);
					idistance[k] = rsUnkD.getInt(5);
					quality[k] = rsUnkD.getInt(6);
					stmPhrase[k] = rsUnkD.getInt(7);
					rsUnkD.moveToNext();
				}
				rsUnkD.close();

				tolerance = 20;
				//56B ave*3 -- Identify10.xls
				float critPhrase = 13.70558376f;
				float critSilence = 1f;
				float critFreqMn= 43.63248731f;
				float critFreqDv = 25.24263959f;
				float critVoicedMn = 61.39796954f;
				float critVoicedDv = 42.39593909f;
				float critEnergyMn = 44.58883249f;
				float critEnergyDv = 57.82233503f;
				float critDistanceMn = 44.5857868f;
				float critDistanceDv = 17.71979695f;
				float critQualityMn = 29.72284264f;
				float critQualityDv = 22.32182741f;
				float critSamplesToMaxMn = 11.2142132f;
				float critSamplesToMaxDv = 10.35837563f;
				float critSilPhrRatio = 8.579695431f;
				float critCoefB = 12.77360406f;
				float critCorrCoef = 1f;
				float critLocation = 1;
				if (isSetCriteria == true) {
					tolerance = 200;
					critPhrase = 1;
					critFreqMn = 1;
					critFreqDv = 1;
					critVoicedMn = 1;
					critVoicedDv = 1;
					critEnergyMn = 1;
					critEnergyDv = 1;
					critDistanceMn = 1;
					critDistanceDv = 1;
					critQualityMn = 1;
					critQualityDv = 1;
					critSamplesToMaxMn = 1;
					critSamplesToMaxDv = 1;
					critSilPhrRatio = 1;
					critCoefB = 1;
					critCorrCoef = 1;
					critSilence = 1;
					critLocation = 1;
				}
				//			Log.d(TAG, "plen:" + critPhrase + " freq:" + critFreq + " voic:" + critVoiced +
				//					" ener:" + critEnergy + " dist:" + critDistance + " qual:" + critQuality +
				//					" samp:" + critSamplesToMax + " spr:" + critSilPhrRatio + " cB:" + critCoefB + " cc:" + critCorrCoef);

				//	        Log.d(TAG, "Unk phrase:" + pc + " pLen:" + phraseLen + " sil:" + iSilence +
				//					" mF:" + meanFreq +	" sdF:" + stdDevFreq +
				//	        		" mV:" + meanVoiced + " sdV:" + stdDevVoiced + " mE:" + meanEnergy + " sdE:" + stdDevEnergy +
				//	        		" mD:" + meanDist + " sdD:" + stdDevDist + " mQ:" + meanQuality + " sdQ:" + stdDevQuality +
				//	        		" mS:" + meanSamples + " sdS:" + stdDevSamples + " spr:" + critSilPhrRatio + " cB:" + slopeU + " cC:" + coefCorr);

				boolean hasTotals = false;
				int tCrit = 1;
				while (hasTotals == false) {
					// get the totals first to limit the detail -- I don't have Ref, Inx, Seg, or Phrase as criteria -- any phrase is ok
					qry = "SELECT DefineTotals.Ref, Inx, Seg, Phrase, Silence, Records, " +
							" FreqMean, FreqStdDev, VoicedMean, VoicedStdDev," +
							" EnergyMean, EnergyStdDev, DistMean, DistStdDev," +
							" QualityMean, QualityStdDev, SampMean, SampStdDev, Slope, SilPhrRatio, " +
							" (Abs(Records - " + phraseLen + ") + " +
							/* " Abs(Silence - " + iSilence + ") + " + */
							" Abs(FreqMean - " + meanFreq + ") + " +
							" Abs(FreqStdDev - " + stdDevFreq + ") + " +
							" Abs(VoicedMean - " + meanVoiced + ") + " +
							" Abs(VoicedStdDev - " + stdDevVoiced + ") + " +
							" Abs(EnergyMean - " + meanEnergy + ") + " +
							" Abs(EnergyStdDev - " + stdDevEnergy + ") + " +
							" Abs(DistMean - " + meanDist + ") + " +
							" Abs(DistStdDev - " + stdDevDist + ") + " +
							" Abs(QualityMean - " + meanQuality + ") + " +
							" Abs(QualityStdDev - " + stdDevQuality + ") + " +
							" Abs(SampMean - " + meanSamples + ") + " +
							" Abs(SampStdDev - " + stdDevSamples + ") + " +
							" Abs(Slope - " + slopeU + ") + " +
							" Abs(SilPhrRatio - " + silPhrRatioU + ")) AS SumVars";
					qry += ", CodeName.InArea, CodeName.RedList, CodeName.Region" +
							" FROM DefineTotals JOIN CodeName ON DefineTotals.Ref = CodeName.Ref ";
					qry += " WHERE FreqMean BETWEEN " + (meanFreq - critFreqMn * tCrit) + " AND " + (meanFreq + critFreqMn * tCrit);
					qry += " AND FreqStdDev BETWEEN " + (stdDevFreq - critFreqDv * tCrit) + " AND " + (stdDevFreq + critFreqDv * tCrit);
					qry += " AND VoicedMean BETWEEN " + (meanVoiced - critVoicedMn * tCrit) + " AND " + (meanVoiced + critVoicedMn * tCrit);
					qry += " AND VoicedStdDev BETWEEN " + (stdDevVoiced - critVoicedDv * tCrit) + " AND " + (stdDevVoiced + critVoicedDv * tCrit);
					qry += " AND EnergyMean BETWEEN " + (meanEnergy - critEnergyMn * tCrit) + " AND " + (meanEnergy + critEnergyMn * tCrit);
					qry += " AND EnergyStdDev BETWEEN " + (stdDevEnergy - critEnergyDv * tCrit) + " AND " + (stdDevEnergy + critEnergyDv * tCrit);
					qry += " AND DistMean BETWEEN " + (meanDist - critDistanceMn * tCrit) + " AND " + (meanDist + critDistanceMn * tCrit);
					qry += " AND DistStdDev BETWEEN " + (stdDevDist - critDistanceDv * tCrit) + " AND " + (stdDevDist + critDistanceDv * tCrit);
					qry += " AND QualityMean BETWEEN " + (meanQuality - critQualityMn * tCrit) + " AND " + (meanQuality + critQualityMn * tCrit);
					qry += " AND QualityStdDev BETWEEN " + (stdDevQuality - critQualityDv * tCrit) + " AND " + (stdDevQuality + critQualityDv * tCrit);
					qry += " AND SampMean BETWEEN " + (meanSamples - critSamplesToMaxMn * tCrit) + " AND " + (meanSamples + critSamplesToMaxMn * tCrit);
					qry += " AND SampStdDev BETWEEN " + (stdDevSamples - critSamplesToMaxDv * tCrit) + " AND " + (stdDevSamples + critSamplesToMaxDv * tCrit);
					qry += " AND Slope BETWEEN " + (slopeU - critCoefB * tCrit) + " AND " + (slopeU + critCoefB * tCrit);
					qry += " AND SilPhrRatio BETWEEN " + (silPhrRatioU - critSilPhrRatio * tCrit) + " AND " + (silPhrRatioU + critSilPhrRatio * tCrit);
					qry += " AND Records BETWEEN " + (phraseLen - critPhrase * tCrit) + " AND " + (phraseLen + critPhrase * tCrit);
					if (Main.isUseLocation == true) {
						qry += " AND CodeName.InArea = 1";
					}
					qry += " AND DefineTotals.Ref > 0"; // don't include unidentified (they are saved when debug true and would be read here)
					qry += " ORDER BY SumVars";
					//Log.d(TAG, "identify totals qry:" + qry);
					rsTot = Main.songdata.getReadableDatabase().rawQuery(qry, null);
					int tcntr = rsTot.getCount();
					//Log.d(TAG, "identify totals tCrit:" + tCrit + " tcntr:" + tcntr + " qry:" + qry);
					rsTot.moveToFirst();
					if (!rsTot.isAfterLast()) {
						int[] usedRef = new int[tcntr];
						boolean bestRec = false;
						for (int t = 0; t < tcntr; t++) { // number of DefineTotals records returned with last query
							// totals data
							if (t == 0) {  // sorted by best
								bestRec = true;
							}
							int tRef = rsTot.getInt(0);
							usedRef[t] = tRef;
							int skipRef = 0;
							// only allow same reference ONCE for each phrase. (was twice) -- the test is below
							for (int ck = 0; ck < t; ck++) {
								if (tRef == usedRef[ck]) {
									skipRef++;
								}
							}
							// the inArea rsTot.getInt(21) is covered with isUseLocation
							String tRedList = rsTot.getString(22); // LC
							boolean isRedList = activeRedList.contains(tRedList); // a true shows LC is in the string " NE DD LC NT ? "
							//Log.d(TAG, "skipRef:" + skipRef + " tRedList:" + tRedList + " isRedList:" + isRedList);

							String tRegion = rsTot.getString(23); // NA
							boolean isRegion = activeRegion.contains(tRegion); // a true shows NA exists in the string " LA MA NA SA "
							if (tRegion.contains(";") && isRegion == false) {
								String[] tRegn = tRegion.split(";");
								for (int r = 0; r < tRegn.length; r++) {
									isRegion = activeRegion.contains(tRegn[r]);
									if (isRegion == true) {
										break;
									}
								}
							}
							//Log.d(TAG, "skipRef:" + skipRef + " tRegion:" + tRegion + " isRegion:" + isRegion);
							if (skipRef < 1 && isRegion == true && isRedList == true) {  // this reference has not been seen more than ONCE this phrase
								int tInx = rsTot.getInt(1);
								int tSeg = rsTot.getInt(2);
								int tPhrase = rsTot.getInt(3);
								int tSilence = rsTot.getInt(4);
								int tRecords = rsTot.getInt(5);  // number of detail records
								float tFreq = rsTot.getFloat(6);
								float tFreqSd = rsTot.getFloat(7);
								float tVoiced = rsTot.getFloat(8);
								float tVoicedSd = rsTot.getFloat(9);
								float tEnergy = rsTot.getFloat(10);
								float tEnergySd = rsTot.getFloat(11);
								float tDist = rsTot.getFloat(12);
								float tDistSd = rsTot.getFloat(13);
								float tQuality = rsTot.getFloat(14);
								float tQualitySd = rsTot.getFloat(15);
								float tSamples = rsTot.getFloat(16);
								float tSamplesSd = rsTot.getFloat(17);
								float tCoefB = rsTot.getFloat(18);
								float tSilPhrRatio = rsTot.getFloat(19);
								float sumVars = rsTot.getFloat(20);
								if (tRef == previousRef && tInx == previousInx) {
									idMatchRefInx++;
								}
								// these data are only used in log which is currenly commented out (below)
								if (bestRec == true) {
									idSilence = Math.abs(tSilence - iSilence);
									idRecords = Math.abs(tRecords - phraseLen);
									idMFreq = Math.abs(tFreq - meanFreq);  // to compare memory with selected database record
									idSdFreq = Math.abs(tFreqSd - stdDevFreq);
									idMVoice = Math.abs(tVoiced - meanVoiced);
									idSdVoice = Math.abs(tVoicedSd - stdDevVoiced);
									idMEnergy = Math.abs(tEnergy - meanEnergy);
									idSdEnergy = Math.abs(tEnergySd - stdDevEnergy);
									idMDist = Math.abs(tDist - meanDist);
									idSdDist = Math.abs(tDistSd - stdDevDist);
									idMQuality = Math.abs(tQuality - meanQuality);
									idSdQuality = Math.abs(tQualitySd - stdDevQuality);
									idMSamples = Math.abs(tSamples - meanSamples);
									idSdSamples = Math.abs(tSamplesSd - stdDevSamples);
									idSilPhr = Math.abs(tSilPhrRatio - silPhrRatioU);
									idCoefB = Math.abs(tCoefB - slopeU);
								}
								Main.db.beginTransaction();
								ContentValues val = new ContentValues();
								val.put("Ref", tRef);
								val.put("Cntr", (int) Math.abs(tRecords - phraseLen));
								val.put("Criteria", "Records" + pc + "_" + t);
								Main.db.insert("Identify", null, val);
								int len = phraseLen / 50;
								if (len > 0) {
									for (int l = 0; l < len; l++) {
										val.put("Ref", tRef);
										val.put("Cntr", 0);
										val.put("Criteria", "length" + pc + "_" + t + "_" + l);
										Main.db.insert("Identify", null, val);
									}
								}
								if (pc > 0) {
									val.put("Ref", tRef);
									val.put("Cntr", (int) Math.abs(tSilence - iSilence));
									val.put("Criteria", "Silence");
									Main.db.insert("Identify", null, val);
								}
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tFreq - meanFreq)));
								val.put("Criteria", "meanFreq");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tFreqSd - stdDevFreq)));
								val.put("Criteria", "stdDevFreq");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tVoiced - meanVoiced)));
								val.put("Criteria", "meanVoiced");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tVoicedSd - stdDevVoiced)));
								val.put("Criteria", "stdDevVoiced");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tEnergy - meanEnergy)));
								val.put("Criteria", "meanEnergy");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tEnergySd - stdDevEnergy)));
								val.put("Criteria", "stdDevEnergy");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tDist - meanDist)));
								val.put("Criteria", "meanDist");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tDistSd - stdDevDist)));
								val.put("Criteria", "stdDevDist");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tQuality - meanQuality)));
								val.put("Criteria", "meanQuality");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tQualitySd - stdDevQuality)));
								val.put("Criteria", "stdDevQuality");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tSamples - meanSamples)));
								val.put("Criteria", "meanSamples");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tSamplesSd - stdDevSamples)));
								val.put("Criteria", "stdDevSamples");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tSilPhrRatio - silPhrRatioU)));
								val.put("Criteria", "silPhrRatio");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) (Math.abs(tCoefB - slopeU)));
								val.put("Criteria", "slope");
								Main.db.insert("Identify", null, val);
								val.put("Ref", tRef);
								val.put("Cntr", (int) sumVars); // already subtracted in query
								val.put("Criteria", "sumVars");
								Main.db.insert("Identify", null, val);
								Main.db.setTransactionSuccessful();
								Main.db.endTransaction();
								val.clear();
								//							Log.d(TAG, " db tCrit:" + tCrit + " Ref:" + tRef + " Inx:" + tInx + " phrase:" + tPhrase +
								//									" len:" + tRecords + " sil:" + tSilence + " tF:" + tFreq + " sdF:" + tFreqSd +
								//									" tV:" + tVoiced + " sdV:" + tVoicedSd + " tE:" + tEnergy + " sdE:" + tEnergySd +
								//									" tD:" + tDist + " sdD:" + tDistSd + " tQ:" + tQuality + " sdQ:" + tQualitySd +
								//									" sdS:" + tSamplesSd +
								//									" spr:" + tSilPhrRatio + " cb:" + tCoefB + " cc:" + tCorrCoef +
								//									" sumVars:" + sumVars);
								//							if (bestRec == true) {   // just first one
								//								Log.d(TAG, " best freq:" + idMFreq + " sdFreq:" + idSdFreq +
								//									" energy:" + idMEnergy + " sdEnergy:" + idSdEnergy + " dist:" + idMDist + " sdDist:" + idSdDist +
								//									" qual:" + idMQuality + " sdQual:" + idSdQuality + " samp:" + idMSamples + " sdSamp:" + idSdSamples +
								//									" spr:" + idSilPhr + " cb:" + idCoefB + " cc:" + idCorrCoef +
								//									" sumVars:" + sumVars );
								//								bestRec = false;
								//							}
								// compare phrase in memory with the detail associated with this selected totals record
								qry = "SELECT * from DefineDetail" +
										" WHERE Ref = " + tRef +
										" AND Inx = " + tInx +
										" AND Seg = " + tSeg +
										" AND Phrase = " + tPhrase +
										" ORDER BY Record";
								rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
								int dcntr = rs.getCount();
								if (dcntr > 0 && phraseLen > 0) {
									// compare detail record found with the unknown record of the current phrase
									// if unknown is shorter move it through defined to find best match
									// if defined is shorter move it through unknown to find best match
									// if they are the same length make defined two longer and duplicate the first and last
									// defined is now longer and unknown will move through defined to find best match
									int fit = 0;
									if (dcntr == phraseLen) {
										dcntr +=2;
										fit=1;
									}
									int[] dFreq = new int[dcntr];
									int[] dVoic = new int[dcntr];
									float[] dEner = new float[dcntr];
									float[] dDist = new float[dcntr];
									float[] dQual = new float[dcntr];
									float[] dSamp = new float[dcntr];

									rs.moveToFirst();
									int dRef = rs.getInt(0);
									int dInx = rs.getInt(1);
									int dSeg = rs.getInt(2);
									int dPhrase = rs.getInt(3);
									int dRecord = rs.getInt(4);
									if (fit==1) {  // duplicate the first set
										dFreq[0] = rs.getInt(5);
										dVoic[0] = rs.getInt(6);
										dEner[0] = rs.getInt(7);
										dDist[0] = rs.getInt(8);
										dQual[0] = rs.getInt(9);
										dSamp[0] = rs.getInt(10);
									}
									for (int k = fit; k < dcntr-fit; k++) {
										dFreq[k] = rs.getInt(5);
										dVoic[k] = rs.getInt(6);
										dEner[k] = rs.getInt(7);
										dDist[k] = rs.getInt(8);
										dQual[k] = rs.getInt(9);
										dSamp[k] = rs.getInt(10);
										rs.moveToNext();
									}
									if (fit==1) {  // duplicate the last set
										rs.moveToLast();
										dFreq[dcntr-fit] = rs.getInt(5);
										dVoic[dcntr-fit] = rs.getInt(6);
										dEner[dcntr-fit] = rs.getInt(7);
										dDist[dcntr-fit] = rs.getInt(8);
										dQual[dcntr-fit] = rs.getInt(9);
										dSamp[dcntr-fit] = rs.getInt(10);
									}

									// the data has now been loaded -- time to compare
									int bestFreq = 0;
									int bestVoic = 0;
									float bestEner = 0;
									float bestDist = 0;
									float bestQual = 0;
									float bestSamp = 0;
									int dF = 0;
									int dV = 0;
									float dE = 0;
									float dD = 0;
									float dQ = 0;
									float dS = 0;
									float temp = 0;
									int savFitF = 0;
									int savFitV = 0;
									int savFitE = 0;
									int savFitD = 0;
									int savFitQ = 0;
									int savFitS = 0;
									int fitCntr = Math.abs(dcntr - phraseLen);

									for (fit = 0; fit <= fitCntr; fit++) {
										dF = 0;
										dV = 0;
										dE = 0;
										dD = 0;
										dQ = 0;
										dS = 0;
										if (dcntr > phraseLen) { // the defined data is longer than the unknown phraseLen
											for (int k = 0; k < phraseLen; k++) {
												temp = pitch[k] - dFreq[fit + k];
												dF += temp * temp;
												temp = voPhrase[k] - dVoic[fit + k];
												dV += temp * temp;
												temp = ienergy[k] - dEner[fit + k];
												dE += temp * temp;
												temp = idistance[k] - dDist[fit + k];
												dD += temp * temp;
												temp = quality[k] - dQual[fit + k];
												dQ += temp * temp;
												temp = stmPhrase[k] - dSamp[fit + k];
												dS += temp * temp;
											} // next rec in this phrase
										} else { // the unknown phraseLen is the same or longer than the defined data
											for (int k = 0; k < dcntr; k++) {
												temp = pitch[fit + k] - dFreq[k];
												dF += temp * temp;
												temp = voPhrase[fit + k] - dVoic[k];
												dV += temp * temp;
												temp = ienergy[fit + k] - dEner[k];
												dE += temp * temp;
												temp = idistance[fit + k] - dDist[k];
												dD += temp * temp;
												temp = quality[fit + k] - dQual[k];
												dQ += temp * temp;
												temp = stmPhrase[fit + k] - dSamp[k];
												dS += temp * temp;
											} // next rec in this phrase
										}
										if (fit == 0) {
											bestFreq = dF;
											bestVoic = dV;
											bestEner = dE;
											bestDist = dD;
											bestQual = dQ;
											bestSamp = dS;
										} else { // pick the lowest
											if (bestFreq > dF) {
												bestFreq = dF;
												savFitF = fit;
											}
											if (bestVoic > dV) {
												bestVoic = dV;
												savFitV = fit;
											}
											if (bestEner > dE) {
												bestEner = dE;
												savFitE = fit;
											}
											if (bestDist > dD) {
												bestDist = dD;
												savFitD = fit;
											}
											if (bestQual > dQ) {
												bestQual = dQ;
												savFitQ = fit;
											}
											if (bestSamp > dS) {
												bestSamp = dS;
												savFitS = fit;
											}
										}
									}
									int div = dcntr;
									if (dcntr > phraseLen) { // divide by the loop counter used
										div = phraseLen;
									}
									bestFreq /= div;
									bestFreq = (int) Math.sqrt(bestFreq);
									bestVoic /= div;
									bestVoic = (int) Math.sqrt(bestVoic);
									bestEner /= div;
									bestEner = (float) Math.sqrt(bestEner);
									bestDist /= div;
									bestDist = (float) Math.sqrt(bestDist);
									bestQual /= div;
									bestQual = (float) Math.sqrt(bestQual);
									bestSamp /= div;
									bestSamp = (float) Math.sqrt(bestSamp);
									Main.db.beginTransaction();
									val.put("Ref", tRef);
									val.put("Cntr", bestFreq);
									val.put("Criteria", "Freq" + savFitF);
									Main.db.insert("Identify", null, val);
									val.put("Ref", tRef);
									val.put("Cntr", bestVoic);
									val.put("Criteria", "Voiced" + savFitV);
									Main.db.insert("Identify", null, val);
									val.put("Ref", tRef);
									val.put("Cntr", (int) bestEner);
									val.put("Criteria", "Energy" + savFitE);
									Main.db.insert("Identify", null, val);
									val.put("Ref", tRef);
									val.put("Cntr", (int) bestDist);
									val.put("Criteria", "Dist" + savFitD);
									Main.db.insert("Identify", null, val);
									val.put("Ref", tRef);
									val.put("Cntr", (int) bestQual);
									val.put("Criteria", "Quality" + savFitQ);
									Main.db.insert("Identify", null, val);
									val.put("Ref", tRef);
									val.put("Cntr", (int) bestSamp);
									val.put("Criteria", "Samples" + savFitS);
									Main.db.insert("Identify", null, val);
									Main.db.setTransactionSuccessful();
									Main.db.endTransaction();
									val.clear();
								} // if dcntr > 0
								rs.close();
							} // skipRef is false
							rsTot.moveToNext();
						} // next t totals record
					} // if there are rsTot records
					if (maxTCrit < tCrit) {
						maxTCrit = tCrit;
						//maxTPhrase = pc;  // phrase cntr -- never used
					}
					tCrit++; // look for wider tolerance
					if (tcntr > 0 || tCrit > tolerance) {  // allow less tolerance here?
						hasTotals = true;  // totals has data or I'm in trouble
					}
				} // while hasTotals test
				rsTot.close();

				//return;  // for next phrase
				rsUnk.moveToNext(); // read the next phrase
			}
			rsUnk.close();
			return;
		} // mode == 1

		if (mode == 2) { // this song is done
			// if cnn request result (load it at the bottom after current)
			if (isRunCnnModel == true) {

			}
			qry = "SELECT Identify.Ref, COUNT(Identify.Ref) AS CntName, SUM(Cntr) AS SumCntr, " +
					"CommonName, Region, SubRegion " +
					"FROM Identify JOIN CodeName ON Identify.Ref = CodeName.Ref " +
					"WHERE Identify.Ref != 0 " +
					"GROUP BY Identify.Ref " +
					"ORDER BY CntName DESC, SumCntr";
			Log.d(TAG, "Identify qry:" + qry);
			// rawQuery for Select queries
			rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			int cntr = rs.getCount();
			int conTot = 0; // confidence total
			if (cntr > 0) {
				int displayCount = 0;
				String displayName = "";
				String nam;
				int cntRef;
				int cntName;
				int cntSum = 0;
				int efficTot = 0;
				rs.moveToFirst();
				idRef = -1;
				while (!rs.isAfterLast()) {
					cntRef = rs.getInt(0);
					cntName = rs.getInt(1);
					cntSum = rs.getInt(2);
					nam = rs.getString(3);
					Log.d(TAG, "ID ref:" + cntRef + " cnt:" + cntName + " sum:" + cntSum + " nam:" + nam);
					if (idRef == -1) {
						idRef = cntRef;
						idCntName = cntName;
						idSum = cntSum;
						idNam = nam;
					}
					if (cntName > 0) {
						conTot += cntName;
					}
					rs.moveToNext();
				} // while
				if (conTot == 0) {
					conTot = 1;
				}
				// run through the list again this time with percents
				rs.moveToFirst();
				identifiedRef = rs.getInt(0);
				int othcon = 0;
				while (!rs.isAfterLast()) {
					cntRef = rs.getInt(0);
					cntName = rs.getInt(1);
					cntSum = rs.getInt(2);
					nam = rs.getString(3);
					String loc = rs.getString(4) + " : " + rs.getString(5);
					int calccon = (int) (((float) cntName / (float) conTot)* 100f + 0.5f);
					if (calccon < 0) {
						calccon = 0;
					}
					if (displayCount < 4) {
						displayName += nam + " " + calccon + "\n\t" + loc + "\n"  ;
						displayCount++;
					} else {
						othcon += (int) ((float) cntName + 0.5f) ;
					}
					rs.moveToNext();
				}
				rs.close();
				if (othcon < 0) {
					othcon = 0;
				}
				othcon = (int) (((float) othcon / (float) conTot)* 100f + 0.5f);
				displayName += "others " + othcon + "\n";  // name confidence%
				//			Toast.makeText(this, namId, Toast.LENGTH_LONG).show();
				final CharSequence idText = displayName;
				Main.displayName = displayName;

				if (Main.isLoadDefinition == true) {
					if(identifiedRef == Main.existingRef) {
						updateSpecAndName(identifiedRef, 1);
					} else {
						updateSpecAndName(identifiedRef, 0);
					}
					if (Main.isDebug == true) {
						Log.d(TAG, "*** Identify Done with isLoadDefinition set true");
						writeId(0);
						writeId(2);
					}
					rs.close();
					rsTot.close();
					return;
				}

				if (Main.isIdentify == true && Main.isShowWeb == false) {
					if (Main.isShowDefinition == true || Main.isShowDetail == true) {
						Log.d(TAG, "show definition or detail from Identify before dialog.");
						mVisualizerView.flash();
					}
				}

				// open the dialog to display the result held in Main.displayName
				Intent idd = new Intent(this, IdentifyDialog.class);
				Log.d(TAG, "startActivityForResult request identify_dialog.");
				Main.myRequest = 1;
				startActivityForResult(idd, 1);

			} else {
				if (Main.isLoadDefinition == false) {
					CharSequence idText = "Unable to Identify Song";
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(idText)
							.setCancelable(true)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									Log.d(TAG, "Song Identify: CANCEL and close()");
								}
							});
					builder.show();  //  ************************ memory leak if Unable to Identify Song
					Log.d(TAG, "Song Identify: db close()" );
				}
				Toast.makeText(this, "Unable to Identify Song", Toast.LENGTH_LONG).show();
			}
			Log.d(TAG, "*** Identify Done");
			if (Main.isDebug == true) {
				writeId(0);
				writeId(2);
			}
		} // mode == 2

	} // identify

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult requestCode:" + Main.myRequest + " resultCode:" + Main.myResult);
		if (requestCode == 1) {
			switch (resultCode) {
				case 0: { // No
					updateSpecAndName(identifiedRef, 0);  // not accepted = 0
					Log.d(TAG, "Song Identify: NO selected -- update spec and close()");
					break;
				}
				case 1: { // Ok
					if (identifiedRef != 0) { // this section is NOW Enabled
						updateSpecAndName(identifiedRef, 1);
						Log.d(TAG, "Song Identify: OK selected -- update spec and close");
					}
					break;
				}
				case 2: {  // Cancel
					//updateSpecAndName(identifiedRef, 2); // leave it alone
					Log.d(TAG, "Song Identify: Cancel selected");
					break;
				}
			}
		}
		return;
	}



	public void updateSpecAndName(int identifiedRef, int accepted) {
		int enh = 0;
		if (Main.isEnhanceQuality == true ){
			enh = 1;
		}
		int aut = 0;
		if (Main.isAutoFilter == true ){
			aut = 1;
		}
		int smo = 0;
		if (Main.isUseSmoothing == true){
			smo = 1;
		}
		Log.d(TAG, "Entering updateSpecAndName identifiedRef:" + identifiedRef + " accepted:" + accepted
				+ " existingName:" + existingName + " existingRef:" + Main.existingRef + " existingInx:" + Main.existingInx
				+ " existingSeg:" + Main.existingSeg);

		// update identified if I rejected the Identification
		if (accepted == 0) {
			Main.db.beginTransaction();
			qry = "UPDATE SongList" +
					" SET Identified = " + identifiedRef +  // the user DIS-agreed with identified song (but don't change the Ref)
					", AutoFilter = " + aut +
					", Enhanced = " + enh +
					", Smoothing =" + smo +
					" WHERE FileName = " + q + existingName + q +
					" AND Path = " + Main.path +
					" AND Ref = " + Main.existingRef +
					" AND Inx = " + Main.existingInx +
					" AND Seg = " + Main.existingSeg;
			Log.d(TAG, "Song Identify Update SongList qry:" + qry);
			Main.db.execSQL(qry);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			return;
		}
		// update identified if I accepted the Identification and the reference is not 0 and it matched the existing reference
		if ((accepted == 1) && (Main.existingRef > 0) && (Main.existingRef == identifiedRef)) {
			// Update identified =1;
			// Identified (but it doesn't add data to DefineTotals or DefineDetail -- nor delete it if it is there)
			Main.db.beginTransaction();
			qry = "UPDATE SongList" +
					" SET Identified = 1" +  // the user agreed with identified song = 1 else 0 or identified Ref
					", AutoFilter = " + aut +
					", Enhanced = " + enh +
					", Smoothing = " + smo +
					" WHERE FileName = " + q + existingName + q +
					" AND Path = " + Main.path +
					" AND Ref = " + Main.existingRef +
					" AND Inx = " + Main.existingInx +
					" AND Seg = " + Main.existingSeg;
			Log.d(TAG, "Song Identify Update Ref matches SongList qry:" + qry);
			Main.db.execSQL(qry);
			updateLastIdentified();
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			//db.close();
			if (Main.isShowWeb == true) {
				Log.d(TAG, "showWebFromIdentify (accepted Existing id)" );
				Main.wikipedia = true;
				Main.xenocanto = false;
				Main.showWebFromIdentify = true;
				finish();
			}
			return;
		}
		// update identified if I accepted the Identification and the reference is not 0 but the two don't match
		if ((accepted == 1) && (Main.existingRef > 0) && (identifiedRef > 0) && (Main.existingRef != identifiedRef)) {
			// it is in a limbo -- defined as one bird identified as another but accepted as this new identification
			// (same result as rejected)
			Main.db.beginTransaction();
			qry = "UPDATE SongList" +
					" SET Identified = 1" + // identifiedRef +  // the user agreed with identified song -- so live with it.
					", AutoFilter = " + aut +
					", Enhanced = " + enh +
					", Smoothing = " + smo +
					" WHERE FileName = " + q + existingName + q +
					" AND Path = " + Main.path +
					" AND Ref = " + Main.existingRef +
					" AND Inx = " + Main.existingInx +
					" AND Seg = " + Main.existingSeg;
			Log.d(TAG, "Song Identify Update SongList qry:" + qry);
			Main.db.execSQL(qry);
			updateLastIdentified();
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			return;
		}
		// update the Spec and Inx in the existing song if current ref is unidentified and identifiedRef found is something else
		if(accepted == 1 && Main.existingRef == 0 && identifiedRef > 0) {
			qry = "SELECT MAX(Inx) AS MaxInx FROM SongList" +
					" WHERE Ref = " + identifiedRef +
					" AND Path = " + Main.path;
			Log.d(TAG, "updateSpecAndName qry:" + qry);
			rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			rs.moveToFirst();
			int maxInx = rs.getInt(0)+1;  // increment the last known inx
			String at = existingName.substring(0, 1);  // @
			if (at.equals("@")) {
				qry = "SELECT CommonName FROM CodeName" +
						" WHERE Ref = '" + identifiedRef + "'";
				rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
				rs.moveToFirst();
				int lenExist = existingName.length();
				String extn = existingName.substring(lenExist-4); // the extension ".m4a" or ".wav"
				String comname = rs.getString(0);  // common name from the database
				int comlen = comname.length(); // the length of 'partially' compressed common name from the database
				qry = "SELECT FileName, Inx, Seg FROM SongList" +
						" WHERE FileName LIKE " + q + comname + "%" + q +
						" AND Path = " + Main.path;	        	 // it won't do any good to sort 100 is less than 20
				rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
				rs.moveToFirst();
				int cntr = rs.getCount();
				int fileInx = 0;
				int thisInx = 0;
				int inxOf = -1;
				String nums = "0123456789";
				boolean foundAlpha = false;
				for (int i=0; i<cntr; i++) {  // this area keeps me from duplicate file names
					String nam = rs.getString(0); // fileName5V30.m4a
					Log.d(TAG, "fileName:" + nam );
					int lennam = nam.length() - 4; // remove the dot and extension
					nam = nam.substring(comlen, lennam);  // 5V30
					lennam = nam.length();
					String ch = "";
					Log.d(TAG, "nam->" + nam + "<- lennam:" + lennam );
					if (lennam > 0) {
						for (int n = 0; n < lennam; n++) {
							ch = nam.substring(n, n+1);
							Log.d(TAG, "ch:" + ch);
							if (nums.contains(ch)) {
								ch = nam.substring(0, n+1);
								Log.d(TAG, "nums.contains(ch))");
							} else {
								ch = nam.substring(0, n);
								Log.d(TAG, "foundAnAlpha");
								ch = null;
								break;
							}
						}
						if (ch != null) {
							thisInx = Integer.parseInt(ch);
						}
					}
					if (fileInx < thisInx) {
						fileInx = thisInx;
					}
					rs.moveToNext();
				} // next i
				rs.close();
				fileInx++;  // one greater than any found
				String newName = comname + fileInx + extn;  //
				// Identifed (but it doesn't add data to DefineTotals or DefineDetail) -- nor should it.
				Main.db.beginTransaction();
				qry = "UPDATE SongList" +
						" SET FileName = " + q + newName + q +
						", Path = " + Main.path +
						", Ref = " + identifiedRef +
						", Inx = " + maxInx +
						", Seg = " + Main.existingSeg +
						", Identified = 1" +  // the app identified a recorded song
						", AutoFilter = " + aut +
						", Enhanced =" + enh +
						", Smoothing = " + smo +
						" WHERE FileName = " + q + existingName + q +
						" AND Path = " + Main.path +
						" AND Ref = 0"  +
						" AND Inx = " + Main.existingInx +
						" AND Seg = " + Main.existingSeg;
				Log.d(TAG, "Song Identify With @ Update SongList qry:" + qry);
				Main.db.execSQL(qry);
				updateLastIdentified();
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
				File from = new File(Main.songpath, existingName );
				Log.d(TAG, "Rename from:" + from);
				File to = new File(Main.songpath, newName.trim());
				Log.d(TAG, "Rename   to:" + to);
				from.renameTo(to);  // rename the file
				existingName = newName;
				Main.existingRef = identifiedRef;   // enabled so I can use web
				Main.fileRenamed = true;
			} else { // don't change the name -- just update the ref and Inx
				Main.db.beginTransaction();
				qry = "UPDATE SongList" +
						" SET Ref = " + identifiedRef +
						", Inx = " + maxInx +
						", Seg = " + Main.existingSeg +
						", Identified = 1" +  // the app identified a recorded song
						", AutoFilter = " + aut +
						", Enhanced =" + enh +
						", Smoothing =" + smo +
						" WHERE FileName = " + q + existingName + q +
						" AND Path = " + Main.path +
						" AND Ref = 0"  +
						" AND Inx = " + Main.existingInx +
						" AND Seg = " + Main.existingSeg;
				Log.d(TAG, "Song Identify keep name Update SongList qry:" + qry);
				Main.existingRef = identifiedRef;
				Main.db.execSQL(qry);
				updateLastIdentified();
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
			}
			if (Main.isShowWeb == true && (Main.existingRef > 0) && (Main.existingRef < Main.userRefStart)) { // don't look for userdefined spec on the web
				Log.d(TAG, "showWebFromIdentify (accepted New id)" );
				Main.wikipedia = true;
				Main.xenocanto = false;
				Main.showWebFromIdentify = true;
				finish();
			}
		}  // update the Spec

	}

	public void updateLastIdentified() {
		Log.d(TAG, "*** Update LastIdentified " + existingName);
		String format = "yyyy_MMdd_HH.mm.ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		long iNow = System.currentTimeMillis();
		Main.db.beginTransaction();
		qry = "UPDATE LastKnown" +
				" SET FileName = " + q + existingName + q + ", " +
				" LastDate = '" + sdf.format(iNow) + "'" +
				" WHERE Activity = 'Identified'";
		//Log.d(TAG, "LastIdentified qry=" + qry);
		Main.db.execSQL(qry);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();

	}

	public void saveDetail(int mode, int pc) {
		// called if define to save detail data -- also saves totals for this phrase
		// note saveDetail always called on Define can be called if Identify and isShowDefinition or isDebug
		int enh = 0;
		if (Main.isEnhanceQuality == true ){
			enh = 1;
		}
		int aut = 0;
		if (Main.isAutoFilter == true ){
			aut = 1;
		}
		int smo = 0;
		if (Main.isUseSmoothing == true){
			smo = 1;
		}
		if (mode == 1) { // mode 1 write the details and totals
			int istart = phraseStart[pc];  // rec number starting this phrase
			int iend = phraseEnd[pc]; // rec number ending this phrase
			int isilence = phraseSilence[pc]; // records counted before this phrase
			int rec = 0;
			float meanFreq = 0;
			float meanVoiced = 0;
			float meanEnergy = 0;
			float meanDist = 0;
			float meanQuality = 0;
			float meanSamples = 0;
			float stdDevFreq = 0;
			float stdDevVoiced = 0;
			float stdDevEnergy = 0;
			float stdDevDist = 0;
			float stdDevQuality = 0;
			float stdDevSamples = 0;
			for (int i=istart; i <= iend; i++) {
				Main.db.beginTransaction();
				ContentValues val = new ContentValues();
				val.put("Ref", defineRef);
				val.put("Inx", defineInx);
				val.put("Seg", defineSeg);
				val.put("Phrase", pc);
				val.put("Record", rec);
				val.put("Freq", pitch[rec]); // int -- zero crossing pitch -- it was but now pwr[j]
				val.put("Voiced", voiced[i]);
				val.put("Energy", ienergy[rec]); // this is energy from bufIn not power out from fft
				val.put("Distance", idistance[rec]); // this is from power --> melFilter -- > distance out from fft
				val.put("Quality", quality[rec]);
				val.put("Samp", samplesToMax[i]); // now 1 if peak else 0 -- this WAS length (in samples) from last max
				Main.db.insert("DefineDetail", null, val);  // SQLiteDiskIOException: disk I/O error (code 1802)
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
				val.clear();
				meanFreq += pitch[rec] * pitch[rec]; // sum the square
				meanVoiced += voiced[i] * voiced[i];
				meanEnergy += ienergy[rec] * ienergy[rec];
				meanDist += idistance[rec] * idistance[rec]; // I've already squared coef to get distance -- so now what -- for now I'll sqr the original??
				meanQuality += quality[rec] * quality[rec];
				meanSamples += samplesToMax[i];  // now this is a counter
				rec++;
			} // next i

			meanFreq /= rec; // rec incremented above -- is now phraseLen
			meanFreq = (float) Math.sqrt(meanFreq);
			meanVoiced /= rec;
			meanVoiced = (float) Math.sqrt(meanVoiced);
			meanEnergy /= rec;
			meanEnergy = (float) Math.sqrt(meanEnergy);
			meanDist /= rec;
			meanDist = (float) Math.sqrt(meanDist);
			meanQuality /= rec;
			meanQuality = (float) Math.sqrt(meanQuality);
			if (meanSamples > 0) {
				meanSamples = (float) rec / (float) meanSamples; // this is records / peak for this phrase
			}
			//meanSamples = (float) Math.sqrt(meanSamples);

			rec = 0;
			for (int i=istart; i <= iend; i++) {

				stdDevFreq += (float) Math.pow((double) (meanFreq - pitch[rec]),2); // sum the square
				stdDevVoiced += (float) Math.pow((double) (meanVoiced - voiced[i]),2);
				stdDevEnergy += (float) Math.pow((double) (meanEnergy - ienergy[rec]),2);
				stdDevDist += (float) Math.pow((double) (meanDist - idistance[rec]),2);
				stdDevQuality += (float) Math.pow((double) (meanQuality - quality[rec]),2);
				stdDevSamples += (float) Math.pow((double) (meanSamples - samplesToMax[i]), 2);  // probably garbage
				rec++;
			} // next i
			stdDevFreq /= rec; // incremented above -- now is phraseLen
			stdDevFreq = (float) Math.sqrt(stdDevFreq);
			stdDevVoiced /= rec;
			stdDevVoiced = (float) Math.sqrt(stdDevVoiced);
			stdDevEnergy /= rec;
			stdDevEnergy = (float) Math.sqrt(stdDevEnergy);
			stdDevDist /= rec;
			stdDevDist = (float) Math.sqrt(stdDevDist);
			stdDevQuality /= rec;
			stdDevQuality = (float) Math.sqrt(stdDevQuality);
			stdDevSamples /= rec;
			stdDevSamples = (float) Math.sqrt(stdDevSamples);
			Main.db.beginTransaction();
			ContentValues val = new ContentValues();
			val.put("Ref", defineRef);
			val.put("Inx", defineInx);
			val.put("Seg", defineSeg);
			val.put("Phrase", pc);
			val.put("Silence", isilence);
			val.put("Records", rec);
			val.put("FreqMean", meanFreq); // float
			val.put("FreqStdDev", stdDevFreq);
			val.put("VoicedMean", meanVoiced);
			val.put("VoicedStdDev", stdDevVoiced);
			val.put("EnergyMean", meanEnergy);  // this is delta dEnergy
			val.put("EnergyStdDev", stdDevEnergy);
			val.put("DistMean", meanDist);
			val.put("DistStdDev", stdDevDist);
			val.put("QualityMean", meanQuality);
			val.put("QualityStdDev", stdDevQuality);
			val.put("SampMean", meanSamples);  // records per peak -- less than 16 is seen as a trill
			val.put("SampStdDev", stdDevSamples); // probably garbage
			val.put("Slope", coefB);
			val.put("SilPhrRatio", silPhrRatio);  // by song -- i.e. same value for every totals record
			Main.db.insert("DefineTotals", null, val);
			Main.db.setTransactionSuccessful();
			Main.db.endTransaction();
			val.clear();
			if (Main.isIdentify == false) {
//	        	Log.d(TAG, "Define Ref:" + defineRef + " phrase:" + pc +
//	        		" len:" + rec + " sil:" + isilence + " mF:" + meanFreq + " sdF:" + stdDevFreq +
//	        		" mV:" + meanVoiced + " sdV:" + stdDevVoiced + " mE:" + meanEnergy + " sdE:" + stdDevEnergy +
//	        		" mD:" + meanDist + " sdD:" + stdDevDist + " mQ:" + meanQuality + " sdQ:" + stdDevQuality +
//	        		" mS:" + meanSamples + " sdS:" + stdDevSamples);
				// save previous ref if define use previous if isIdentify
				//Log.d(TAG, "SaveDetail Updating previousRef:" + previousRef + " definedRef:" + defineRef + " isIdentify:" + Main.isIdentify + " mode:" + mode);
				previousRef = defineRef; // for debugId
				previousInx = defineInx;
				idMatchRefInx = 0;
			}
		}

		if (mode == 2) { // mode 2 update the song list
			Log.d(TAG, "*** Update SongList " + existingName);
			if (Main.isIdentify == false) { // defined
				Main.db.beginTransaction();
				qry = "UPDATE SongList" +
						" SET Defined = 1" +
						", AutoFilter = " + aut +
						", Enhanced =" + enh +
						", Smoothing =" + smo +
						" WHERE FileName = " + q + existingName + q +
						" AND Path = " + Main.path +
						" AND Ref = " + Main.existingRef +
						" AND Inx = " + Main.existingInx +
						" AND Seg = " + Main.existingSeg;
				Main.db.execSQL(qry);
				Log.d(TAG, "*** Update LastKnown " + existingName);
				String format = "yyyy_MMdd_HH.mm.ss";
				SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
				long iNow = System.currentTimeMillis();
				qry = "UPDATE LastKnown" +
						" SET FileName = " + q + existingName + q + ", " +
						" LastDate = '" + sdf.format(iNow) + "'" +
						" WHERE Activity = 'Defined'";
				Main.db.execSQL(qry);
				Main.db.setTransactionSuccessful();
				Main.db.endTransaction();
			}
		}

	}

	public void writeId(int mode) {
		try {
			String sb = null;
			if (mode == 0) {  // id
				Log.d(TAG, "Save Id (identify)" );
				fos5 = new FileOutputStream( Main.definepath + "id.txt", true);  // append
				sb = "file:" + mFileName + " _" + Main.existingInx + "." + Main.existingSeg + "\n";
				// sb = sb + " len:" + bestPhraseLenT2 + ":" + bestPhraseLen + ":" + bestPhraseLenD2 + ":" + bestPhraseLenD4 +
				sb = sb + "trillLen:" + trillLen;
				sb = sb + ", ratio:" + aveEnergyRatio;
				sb = sb + ", mic:" + Main.sourceMic;
				sb = sb + ", AutoFilter:" + Main.isAutoFilter;
				sb = sb + ", Enhanced:" + Main.isEnhanceQuality;
				sb = sb + ", snMean:" + snMean;
				sb = sb + "\n";
				sb = sb + " phraseCntr:" + phraseCntr;
				sb = sb + ", ID ref:" + idRef;
				sb = sb + ", cnt:" + idCntName;
				sb = sb + ", sum:" + idSum;
				sb = sb + ", pctTop:" + pctTop;
				sb = sb + ", nam:" + idNam;
				sb = sb + "\n";
				sb = sb + " previousRef:" + previousRef;
				sb = sb + ", previousInx:" + previousInx;
				sb = sb + ", origMean:" + originalMean;
				sb = sb + ", idMatchRefInx:" + idMatchRefInx;
				sb = sb + ", maxTCrit:" + maxTCrit;
				sb = sb + ", Smooth:" + Main.isUseSmoothing;
				sb = sb + "\n";
				sb = sb + "\n";
				byte buf[] = sb.getBytes();
				fos5.write(buf);
			}
			if (mode == 1) { // defined
				Log.d(TAG, "Save Id (Defined)" );
				String format = "yyyy_MMdd_HH.mm.ss";
				SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
				long iNow = System.currentTimeMillis();
				fos5 = new FileOutputStream( Main.definepath + "id.txt", true);  // append
				sb = "file:" + mFileName + " _" + Main.existingInx + "." + Main.existingSeg +
						",,,,," + sdf.format(iNow);
				sb = sb + "\n";
				//sb = sb + " len:" + bestPhraseLenT2 + ":" + bestPhraseLen + ":" + bestPhraseLenD2 + ":" + bestPhraseLenD4 +
				sb = sb + "trillLen:" + trillLen;
				sb = sb + ", ratio:" + aveEnergyRatio;
				sb = sb + ", mic:" + Main.sourceMic;
				sb = sb + ", AutoFilter:" + Main.isAutoFilter;
				sb = sb + ", Enhanced:" + Main.isEnhanceQuality;
				sb = sb + ", snMean:" + snMean;
				sb = sb + "\n";
				sb = sb + " definedRef:" + Main.existingRef;
				sb = sb + ", definedInx:" + Main.existingInx;
				sb = sb + ", definedCntr:" + phraseCntr;
				sb = sb + ", origMean:" + originalMean;
				sb = sb + ", fix:" + idFix;
				sb = sb + ", Smooth:" + Main.isUseSmoothing;
				sb = sb + "\n";
				byte buf[] = sb.getBytes();
				fos5.write(buf);
			}
			if (mode == 2) { // close
				fos5.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeMfcc(int mode, int pc) {
		try {
			String sb = null;
			if (mode == 0) {
				Log.d(TAG, "Save mfcc" );
				fos1 = new FileOutputStream( Main.definepath + "mfcc.txt" );
			}
			if (mode == 1) {
				int istart = phraseStart[pc];
				int iend = phraseEnd[pc];
				int isilence = phraseSilence[pc];
				int rec = 0;
				sb = "" + pc;
				sb = sb + ", phraseMax:";
				sb = sb + ", " + energyPhraseMax[pc];;
				sb = sb + ", fullMult:";
				sb = sb + ", " + fullMult; // fft max power freq
				sb = sb + ", ";
				sb = sb + "\n";
				byte buf[] = sb.getBytes();
				fos1.write(buf);
				for (int i=istart; i <= iend; i++) {
					sb = "" + pc;
					sb = sb + ", " + rec;
					sb = sb + ", " + pitch[rec]; // int -- zero crossing pitch
					sb = sb + ", " + voiced[i];
					sb = sb + ", " + ienergy[rec]; // fft max power freq
					sb = sb + ", " + idistance[rec];
					sb = sb + "\n";
					buf = sb.getBytes();
					fos1.write(buf);
					rec++;
				} // next i
				sb = "\n";
				buf = sb.getBytes();
				fos1.write(buf);
				//Log.d(TAG, "Phrase Ended phraseLength:" + rec);
			}
			if (mode == 2) {
				fos1.close();
			}
		} catch( Exception e ) {
			Log.e("writeMfcc failed ", "Exception: " + e.toString());
		}
	}

	public void writeVoicedFrame() {
		Log.d(TAG, "Save VoicedFrame:"+ mFileName );
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( Main.definepath + "voiced.txt" );
			for (int i=0; i < records-1; i++) {
				String sb = "" + i;
				sb = sb + ", " + voiced[i];
				sb = sb + ", " + voicedFrame[i];
				sb = sb + ", " + rmsEnergy[i];
				sb = sb + ", " + voicedFreq[i];
				sb = sb + ", " + locationAtMax[i];
				sb = sb + ", " + samplesToMax[i];
				sb = sb + "\n";
				byte buf[] = sb.getBytes();
				fos.write(buf);
			} // next i
			fos.close();
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}

	public void writeFloatFftAscii() {
		Log.d(TAG, "Save float FFT ascii data (everything)" );
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( Main.definepath + "fftCompleteFile.txt" );
			for (int i=0; i < records; i++) {
				for (int j=0; j<(base/2); j++) {
					String sb = i + "," + j + "," ;
					sb = sb + pwr[j] + "\n";
					byte buf[] = sb.getBytes();
					fos.write(buf);
				}
			} // next i
			fos.close();
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}

	public void writeTestFftAscii() {
		Log.d(TAG, "Save Test FFT" );
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( Main.definepath + "fftTestFile.txt" );
			for (int i=0; i < records; i++) {
				String sb = i + "," + maxPwr[i][0] + "," + maxPwr[i][1] + "\n";
				byte buf[] = sb.getBytes();
				fos.write(buf);
			} // next i
			fos.close();
		} catch( Exception e ) {
			Log.e("saveLayout failed: ", e.toString() );
		}
	}

	public void writeSignalNoise(int len, float[] power, float[] noise, float[] signal) {
		Log.d(TAG, "Save signal noise" );
		FileOutputStream fos = null;
		//float conv = (float) 11025f/512f; // mult by i below to get freq but excel just chooses integers
		try {
			fos = new FileOutputStream( Main.definepath + "SignalNoise.txt" );
			for (int i=0; i < len; i++) {
				String sb = i + "," + power[i] + "," + noise[i] + "," + signal[i] + "\n";
				byte buf[] = sb.getBytes();
				fos.write(buf);
			} // next i
			fos.close();
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}

	public void writeKernelInFreqDomain(int len, float[] real, float[] imag) {
		Log.d(TAG, "Save KernelInFreqDomain" );
		//float conv = (float) 11025f/512f;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( Main.definepath + "KernelInFreqDomain.txt" );
			for (int i=0; i < len; i++) {
				float result = (float) Math.sqrt(real[i]*real[i]+imag[i]*imag[i]);
				String sb = i + "," + real[i] + "," + imag[i] + "," + result + "\n";
				byte buf[] = sb.getBytes();
				fos.write(buf);
			} // next i
			fos.close();
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}

	public void writeFilterKernel(int len, float[] kernel) {
		Log.d(TAG, "Save filterKernel" );
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( Main.definepath + "filterKernel.txt" );
			for (int i=0; i < len; i++) {
				String sb = i + "," + kernel[i] + "\n";
				byte buf[] = sb.getBytes();
				fos.write(buf);
			} // next i
			fos.close();
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}

	public void writeMelFilter(int mode, int pc, int rec) {
		try {
			if (mode == 0) {
				Log.d(TAG, "Save melFilter data");
				fos2 = new FileOutputStream( Main.definepath + "melFilter.txt" );
			}
			if (mode == 1) {
				String sb = "" + pc + ", " + rec;
				for (int j=0; j<(numMelFilters); j++) {
					sb += ", " + melFilter[j];
				}
				sb += "\n";
				byte buf[] = sb.getBytes();
				fos2.write(buf);
			}
			if (mode == 2) {
				fos2.close();
			}
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}

	public void writeDtc(int mode, int pc, int rec) {
		try {
			if (mode == 0) {
				Log.d(TAG, "Save dtc data");
				fos4 = new FileOutputStream( Main.definepath + "dtc.txt" );
			}
			if (mode == 1) {
				String sb = "" + pc + ", " + rec;
				for (int j=0; j<numCepstra; j++) {		// currently 16 (was 12)
					sb += ", " + dtc[j];
				}
				sb += "\n";
				byte buf[] = sb.getBytes();
				fos4.write(buf);
			}
			if (mode == 2) {
				fos4.close();
			}
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}


	@SuppressLint("LongLogTag")
	public void writeFftMaxAscii(int mode, int pc, int i, float[]temp) {
		try {
			if (mode == 0) {
				Log.d(TAG, "Save float FFT max data");
				fos3 = new FileOutputStream( Main.definepath + "fftFloatAscii.txt" );
			}
			if (mode == 1) {
				try {
					for (int j=0; j<(base); j++) {
						String sb = pc + "," + i + "," + j + "," ;
						sb = sb + temp[j] + "\n";
						byte buf[] = sb.getBytes();
						fos3.write(buf);
					}
				}catch (Exception e) {
					Log.e("save fftFloatAscii.txt failed: ", e.toString() );
				}
			}
			if (mode == 2) {
				fos3.close();
			}
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}

	/*
	public void writeFftMaxPwr(int mode, int pc, int i) {
		try {
			if (mode == 0) {
				Log.d(TAG, "Save fftMaxPwr");
				fos6 = new FileOutputStream( Main.definepath + "fftMaxPwr.txt" );
			}
			if (mode == 1) {
				try {
					String sb = pc + "," + i;
					for (int j=0; j<rankCntr; j++) {
						sb += "," + freqRank[i][j] + "," + maxPwr[i][j];
					}
					sb += "\n";
					byte buf[] = sb.getBytes();
					fos6.write(buf);
				}catch (Exception e) {
					Log.e("save fftMaxPwr.txt failed ", "Exception: " + e.toString() );
				}
			}
			if (mode == 2) {
				fos6.close();
			}
		} catch( Exception e ) {
			Log.e("save fftMaxPwr failed ", "Exception: " + e.toString() );
		}
	}
*/
	@SuppressLint("LongLogTag")
	public void writeVoicedMaxPwrRec(int mode, int i, int[] freqRank, float[] maxPwr) {
		try {
			if (mode == 0) {
				Log.d(TAG, "Save VoicedMaxPwrRec");
				fos6 = new FileOutputStream( Main.definepath + "VoicedMaxPwrRec.txt" );
			}
			if (mode == 1) {
				try {
					String sb = "" + i;
					for (int j = 0; j < rankCntr; j++) {
						sb += "," + freqRank[j] + "," + maxPwr[j];
					}
					sb += "\n";
					byte buf[] = sb.getBytes();
					fos6.write(buf);
				}catch (Exception e) {
					Log.e("save fftFloatAscii.txt failed: ", e.toString() );
				}
			}
			if (mode == 2) {
				fos6.close();
			}
		} catch( Exception e ) {
			Log.e("saveLayout failed ", "Exception: " + e.toString() );
		}
	}




	@SuppressLint("LongLogTag")
	public void writeVoicedMaxPwr() {
		try {
			Log.d(TAG, "Save VoicedMaxPwr");
			fos6 = new FileOutputStream( Main.definepath + "VoicedMaxPwr.txt" );
			try {
				for (int i=0; i<records; i++) {
					String sb = "" + i;
					for (int j = 0; j < rankCntr; j++) {
						sb += "," + freqRank[i][j] + "," + maxPwr[i][j];
					}
					sb += "\n";
					byte buf[] = sb.getBytes();
					fos6.write(buf);
				}
			} catch (Exception e) {
				Log.e("save VoicedMaxPwr.txt failed: ", e.toString() );
			}
			fos6.close();
		} catch( Exception e ) {
			Log.e("save VoicedMaxPwr.txt failed: ", e.toString() );
		}
	}


	// ***************************************** fft ************************************************
	private int buildFft() {
		// I am working with big endian shorts in a byte buffer normalized to 32767 in audioRecord
		// I have (or attempted to) skipped and/or truncated the song so when I get here I am accepting all in audioData
		// findVoiced will split the song into phrases
		Log.d(TAG, "buildFft isIdentify:" + Main.isIdentify);
		if (debugMaxJ == true) {
			writeFftMaxAscii(0, 0, 0, temp);  // open the file to write
		}
		if (Main.isDebug == true) {
			//writeFftMaxPwr(0, 0, 0);  // open the file to write
		}
		if (Main.isDebug == true) {
			writeMfcc(0,0); // open
		}
		if (Main.isDebug == true) {
			writeMelFilter(0,0,0); // open
		}
		//if (Main.isIdentify == true) {
		//	identify(0,0); // open
		//}

		Main.maxPower = 0f;
		Main.maxPowerRec = 0;
		Main.maxPowerJ = 0;

		// *************** defined ******************
		stepSize = base/baseStep; // 512 = 1024/2
		incSize = stepSize / 2;  // 256 / 22050 = 11.6 ms step records or 86.13 records per sec.
		records = (Main.audioDataLength-base)/ incSize;  // base/4 repeats - base doesn't repeat
		// *************** defined ******************

		Log.d(TAG, "* * * * records:" + records + " stepSize:" + stepSize + " incSize:" + incSize );
		bufIn = new float [base]; // supplies 1024 floats
		imagIn = new float [base];
		for (int i=0; i<base; i++) {  // just do it once
			imagIn[i] = 0f;
		}
		bufRealOut = new float [base]; // returns 1024 floats -- 512 real floats and 512 inverted (not usable) floats
		bufImagOut = new float [base]; // returns 1024 floats -- 512 real floats and 512 imaginary inverse floats (not usable data)
		voicedFreq = new int[records];  // full length of file
		int rec = 0;
		baseAvailable = Main.audioData.length - base;

		findVoiced();  // ************************* split into phrases ***********************************
		fullMult = 32767f / extent; // possible max short / extent (largest short found in file)
		rec = 0;
		for (int pc = 0; pc < phraseCntr; pc++) {
			float shiftVoiced = (float) phraseAdj[pc] / (base/baseStep);
			//Log.d(TAG, "fft phrase:" + pc + " phraseAdj:" + phraseAdj[pc] + " shiftVoiced:" + shiftVoiced);

			int phraseLen = (phraseEnd[pc]-phraseStart[pc])+1;
			int istart = phraseStart[pc]*incSize + phraseAdj[pc];
			int prevPitch = voicedFreq[phraseStart[pc]-1];
			if (istart<0) {  // allow for preEmphasis or if greater than half move to closest
				istart += incSize;
			}
			int iend = phraseEnd[pc]*incSize + phraseAdj[pc];  // phraseAdj shifts phrase so max Energy is centered in bufIn
			if (iend > baseAvailable) {  // was less than 0 -- now allow for preEmphasis
				iend -= incSize;
			}

			mfcc = new float[phraseLen][numMelFilters]; // cepstra, delta, deltadelta, dEnergy, delta, deltadelta
			pitch = new int[phraseLen];
			energy = new float[phraseLen];
			ienergy = new int[phraseLen];
			energyValue = new float[phraseLen];
			distance = new float[phraseLen];
			idistance = new int[phraseLen];
			quality = new int[phraseLen];
			rec = 0; // record for this phrase
			for (int i = istart; i <= iend; i += incSize) { // incSize = 256
				rmsEnergy[phraseStart[pc]+rec] = 0;
				int[] freqRankR = new int[rankCntr]; // used in maxPwr 4 wide one record long
				float[] maxPwrR = new float[rankCntr]; // used in maxPwr 4 wide one record long
				// WALK CAREFULLY HERE -- CONVOLUTION WORKS WITH 512 audioNorm and 512 zeros
				// NOW I AM LOADING WITH 1024 audioNorm !!!
				for (int j = 0; j < base; j++) { // 0 to 1023
					bufIn[j] = audioNorm[i + j];
				}
				if (Main.isEnhanceQuality == false) { // if not using filter kernel use hanning window instead
					fft.windowFunc(3, base, bufIn); // hanning
				}
				fftbas.fourierTransform(base, bufIn, imagIn, bufRealOut, bufImagOut, false);
				pwr = new float[base/2];
				for (int j = 1; j < stepSize; j++) {
					if (Main.isEnhanceQuality == true) {
						// convolute to apply filter kernel
						// Multiply the frequency spectrum by the frequency response (complex multiply per EQUATION 9-1)
						float temp = bufRealOut[j] * realKernelFreq[j] - bufImagOut[j] * imagKernelFreq[j];
						bufImagOut[j] = bufRealOut[j] * imagKernelFreq[j] + bufImagOut[j] * realKernelFreq[j];
						bufRealOut[j] = temp;
					}
					// now power
					if (j > lowFreqCutoff && j < highFreqCutoff) {
						pwr[j] = (float) Math.sqrt(bufRealOut[j] * bufRealOut[j] + bufImagOut[j] * bufImagOut[j]);
					}
					// maxEnergy is really maxPower
					// already found before fix1 and after fix3 -- but not with offsets so find it again
					// using existing maxEnergy up to here -- i.e. not zeroed to start again.
					if (maxEnergy < pwr[j]) {
						maxEnergy = pwr[j];
					}
					if (rmsEnergy[phraseStart[pc]+rec] < pwr[j]) { // new rmsEnergy
						rmsEnergy[phraseStart[pc]+rec] = pwr[j];
						pitch[rec] = j;
					}
					for (int k = 0; k < rankCntr; k++) { // concepts from version 15Q (that's been a while!)
						if (maxPwrR[k] < pwr[j]) {
							for (int n = rankCntr - 1; n > k; n--) {
								freqRankR[n] = freqRankR[n - 1];  // move them down a row to make room for the new max
								maxPwrR[n] = maxPwrR[n - 1];
							}
							freqRankR[k] = j;
							maxPwrR[k] = pwr[j]; // add the new max
							break; // to nextj
						}
					}
				}  // next j
				// do smoothing here
				// using pitch (one phrase) instead of voicedFreq (full file)

				if (Main.isUseSmoothing == true) { // && Main.lowFreqCutoff == 0) { // the data will be here now
					int min = Math.abs(prevPitch - pitch[rec]); // compare prevFreq to thisFreq
					if (min > 8) { // previous freq not close to this freq
						//Log.d(TAG, "enter smoothing rec:" + rec + " min:" + min);

						int aveFreq = 0;
						for (int k = 0; k < rankCntr; k++) {
							aveFreq += freqRankR[k];
						}
						aveFreq /= rankCntr; // of this group
						int cntBelow = 0;
						int cntAbove = 0;
						for (int k = 0; k < rankCntr; k++) {
							if (aveFreq < freqRankR[k]) {
								cntAbove++;
							} else {
								cntBelow++;
							}
						}
						//Log.d(TAG, "smoothing aveFreq:" + aveFreq + " cntBelow:" + cntBelow + " cntAbove:" + cntAbove);
						if (cntBelow >= 2 && aveFreq < pitch[rec]) { // this one high - two are low
							//Log.d(TAG, "smoothing force lower");
							min = Math.abs(prevPitch - freqRankR[0]);
							int minN = 0;
							for (int n = 1; n < rankCntr; n++) {
								int test = Math.abs(prevPitch - freqRankR[n]);
								if (min > test) {
									min = test;
									minN = n;
								}
							}
							rmsEnergy[phraseStart[pc]+rec] = maxPwrR[minN]; //
							pitch[rec] = freqRankR[minN];
							//voiced[phraseStart[pc]+rec] = (int) (rmsEnergy[phraseStart[pc]+rec - 1] / rmsEnergy[phraseStart[pc]+rec] * voiced[phraseStart[pc]+rec - 1]);
							//voiced[phraseStart[pc]+rec] = (int) (maxPwrR[minN]/maxPwrR[0]) * voiced[phraseStart[pc]+rec];
						}
						if (cntAbove >= 2 && aveFreq > pitch[rec]) { // this one low - three are high
							//Log.d(TAG, "smoothing force higher");
							min = Math.abs(prevPitch - freqRankR[0]);
							int minN = 0;
							for (int n = 1; n < rankCntr; n++) {
								int test = Math.abs(prevPitch - freqRankR[n]);
								if (min > test) {
									min = test;
									minN = n;
								}
							}
							rmsEnergy[phraseStart[pc]+rec] = maxPwrR[minN];
							pitch[rec] = freqRankR[minN];
							//voiced[phraseStart[pc]+rec] = (int) (rmsEnergy[phraseStart[pc]+rec - 1] / rmsEnergy[phraseStart[pc]+rec] * voiced[phraseStart[pc]+rec - 1]);  // fill in the silence
							//voiced[phraseStart[pc]+rec] = (int) (maxPwrR[minN]/maxPwrR[0]) * voiced[phraseStart[pc]+rec];
						}
						prevPitch = pitch[rec];
					}
				} // smoothing
				//voiced[phraseStart[pc]+rec] = (int) ((float) voiced[phraseStart[pc]+rec] - (float) voiced[phraseStart[pc]+rec]*shiftVoiced
				//		+ (float) voiced[phraseStart[pc]+rec+1]*shiftVoiced + 0.5f);

				//Log.d(TAG, "phrase:" + pc + " rec:" + rec + " maxPwr:" + maxPwr[rec] + " lowCut:" + lowFreqCutoff + " freq0:" + freqRank[rec][0] + " freq1:" + freqRank[rec][1]);
				if (debugMaxJ == true) {
					writeFftMaxAscii(1, pc, rec, bufIn);  // the data
				}
				if (Main.isDebug == true) {
					//writeFftMaxPwr(1, pc, rec);
				}

				int min = stepSize;
				int max = 0;
				for (int k = 0; k < rankCntr; k++) {
					if (freqRankR[k]> qualLow && freqRankR[k] < qualHigh) {
						min = Math.min(min, freqRankR[k]);
						max = Math.max(max, freqRankR[k]);
					}
				}
				quality[rec] = max - min; // 3 is pure clear the higher the number the more scratchy (power spread out)
				melFilter(rec);  // also store in mfcc -- convert the 512 power/frequency records to 64 mel or linear
				calcEnergy(rec, pc);  // store rms in energyValue
				if (Main.isDebug == true) {
					//	writeMelFilter(1, pc, rec); // write one record
				}
				rec++;
			} // next i
			normalizeMelFilter(phraseLen, pc);  // produces distance  mfcc is normalized
			energyDelta(phraseLen, pc);  // energyValue to delta energy - simple subtract

			if (Main.isDebug == true) {
				writeMfcc(1, pc); // write phrase
			}
			curveFit.calcPolynomial(rec, 0); // rec now is phraseLen calculates pitch polynomial
			coefB = curveFit.polyCoef[1];  // un-modified slope -- coefA not used - it just tracks freq
			coefCorr = curveFit.corrCoef;
			// ******************* ALWAYS SAVE DETAIL -- BUT DON'T CALL IDENTIFY UNTIL DONE SAVING DETAIL GG 5/9/16
			//Log.d(TAG, "fft return from poly coefB:" + coefB + " coefCoor:" + coefCorr);
			//if (Main.isIdentify == true) {
			//	if (Main.isShowDefinition == true || Main.isDebug == true) {  // debug saves id.txt
			//		saveDetail(1, pc);
			//	}
			//	identify(1, pc); // analyze phrase
			//} else {
			saveDetail(1, pc); // always write phrase <-- ******  right here *********************************
			//}
			// ******************* ALWAYS SAVE DETAIL -- BUT DON'T CALL IDENTIFY UNTIL DONE SAVING DETAIL GG 5/9/16
		} // next phrase
		if (Main.isDebug == true) {
			writeMfcc(2,0); // close
		}
		if (Main.isDebug == true) {
			writeMelFilter(2, 0, 0); // close
		}
		if (debugMaxJ == true) {
			writeFftMaxAscii(2, 0, 0, temp); // close
		}
		if (Main.isDebug == true) {
			//writeFftMaxPwr(2, 0, 0); // close
		}

		if (Main.isIdentify == true) { // identify
			// now do everything
			identify(0); // open
			identify(1); // identify -- 1 modified to read phrases from totals for each phrase
			identify(2); // identify totals saves id
			// ****************** included above *****************
			//if (Main.isShowDefinition == true || Main.isDebug == true) {
			//	saveDetail(2,0);
			//}
			//identify(2,0); // identify totals saves id
			// ****************** included above *****************
		} else { // define
			saveDetail(2,0); // close
			if (Main.isDebug == true) {
				writeId(1); // define
				writeId(2); // close
			}
		}

		return 0;
	} // buildFft

	private void melFilter(int rec) {
		int step = (base/2) / numMelFilters;  // 512/128 = 4
		melFilter = new float[numMelFilters];
		for (int k = 0; k < numMelFilters; k++) {
			for (int i=-step; i<step; i++) {
				int j = k*step+i;
				if (j >=0 && j<(base/2)) {
					melFilter[k] += pwr[j];
				}
			}
			mfcc[rec][k] = melFilter[k];
		}
	}

	private void normalizeMelFilter(int phraseLen,int pc) {
		// I can use the same array and write back into it with difference
		float sum = 0;
		float mean;
		// 1.loop through each mfcc coeff
		for (int j = 0; j < numMelFilters; j++) {
			// calculate mean
			sum = 0;
			int cntr = 0;
			for (int i = 0; i < phraseLen; i++) {
				sum += mfcc[i][j];// ith coeff of all frames
				cntr++;
			}
			mean = sum / cntr;
			// subtract
			for (int i = 0; i < phraseLen; i++) {
				mfcc[i][j] = mfcc[i][j] - mean;  // write over the original with the normalized
			}
		}
		// gg - mfcc^2 summed in any order will give the same distance -- distance is normalized then rms and
		for (int i = 0; i < phraseLen; i++) {
			for (int j = 0; j < numMelFilters; j++) {
				distance[i] += mfcc[i][j] * mfcc[i][j];
			}
			distance[i] /= numMelFilters;
			//idistance[i] = (int)(Math.sqrt(distance[i])/fullMult + 0.5);
			//idistance[i] = (int)(Math.sqrt(distance[i]) / energyPhraseMax[pc] * fullMult + 0.5);
			idistance[i] = (int)(Math.sqrt(distance[i]) * 512f/maxEnergy + 0.5); // this is really maxPower for the whole file
		}
	}

	public void energyDelta(int phraseLen, int pc) {
		// just subtract the previous -- to get delta
		energy[0]=energyValue[0];
		float min = energy[0];
		float max = energy[0];
		// remember this is delta -- e.g. the ramp is loud but never changes so energy is straight line
		for (int i=1; i<phraseLen; i++){
			energy[i] = energyValue[i] - energyValue[i-1];  // energy can now be plus or minus
			ienergy[i] = (int) (energy[i] * 512f/maxEn + 0.5);
		}
	}

	public void calcEnergy(int rec, int pc) {  // was log now rms dEnergy for the current record
		// this is different than distance this is on bufIn and distance is on melFilter (which came from pwr)
		// mel filter is normalized
		// NOTE: bufIn is size base but I repeat each incSize so by using incSize I am not duplicating any data and thus not averaging
		float sum = 0;
		// bufIn is size base but I repeat each base/2 <-- WRONG --> so by using base/2 I am not duplicating any data and thus not averaging by two
		// MAYBE averaging over 4 records (each 256) will give me better results but now this energy is not averaged.
		// I am only taking one quarter of bufIn because I'll get the next piece for the next record next time
		// note: I am reading 1024 of bufIn each rec -- If I go back to reading just 256 -- I will need to find that 256 here -- not first 256
		for (int j = 0; j < incSize; j++) {  // this is incSize = 256 as well as the divide below
			float temp = bufIn[j]; // this is in the time domain
			sum += temp * temp; // sum the square
		}
		energyValue[rec] = (float) Math.sqrt(sum/incSize);  // rmsEnergy for one record
		//energyValue[rec] = (float) Math.sqrt(sum/(base/2));  // rmsEnergy for one record

	}


// findVoiced ********************************************************************************

	private void findVoiced() {  // mark the frames that are above background noise
		// I compensate for edit start and stop (audioDataLength) DecodeFileJava
		// The song here has data removed that is not between start and stop.
		stdLim = 3.0f;
		int voiceLim = 255;  // will be set to zero on normal or a low number if high noise
		//Log.d(TAG, "find Voiced -- Start Normalization:" + mFileName  );
		short minVal = 32767;
		short maxVal = -32767;
		short shortVal;
		int mSum = 0;
		int maxRec = 0;
		int minRec = 0;
		float autoFilter = 0;  // average normalized
		float voicedMeanFreq = 0;
		int totalSilence = 0;

		// Manual filter and AutoFilter can both be true
		boolean isManualFilter = Main.filterStartAtLoc > 0 || Main.filterStopAtLoc > 0 || Main.lowFreqCutoff > 0 || Main.highFreqCutoff > 0;
		boolean isEnableAutoFilter = Main.isAutoFilter == true;
		String audsrc = "";

		// get values required to normalize
		for (int i = 0; i < Main.audioDataLength; i++) {
			shortVal = (short) (Main.audioData[i]);
			if (minVal > shortVal) { // maximum extent can be positive or negative
				minVal = shortVal;
				minRec = i;
			}
			if (maxVal < shortVal) {
				maxVal = shortVal;
				maxRec = i;
			}
			mSum += shortVal;
			if (isEnableAutoFilter == true) {
				autoFilter += shortVal * shortVal;  // summing for average later
			}
		}
		extent = maxVal;
		if (Math.abs(minVal) > Math.abs(maxVal)) {
			extent = Math.abs(minVal);
			maxRec = minRec;
		}
		float mCount = (float) Main.audioData.length; // use actual not extra
		offset = -mSum / mCount;
		float ratio = 1.0f;  // 10 ^ 0 = 1   i.e.  Math.pow(10.0, dbLevelReq/20)
		mult = (ratio / extent);
		if (isEnableAutoFilter == true) {
			autoFilter = autoFilter / mCount;
			autoFilter = (float) Math.sqrt(autoFilter); // this is mean shortVal
			autoFilter = (autoFilter + offset) * mult;  // this is mean normalized
		} // else autoFilter is 0 if using edit begin and end aka filterStartAtLoc and filterEndAtLoc
		Log.d(TAG, "voiced() normalize with extent:" + extent + " offset:" + offset + " mult:" + mult + " autoFilter:" + autoFilter);
		int cntr = Main.audioDataLength - base;  // the usable file length without overflows
		cntr -= cntr % base;
		Log.d(TAG, "voiced() audioDataLength:" + Main.audioDataLength + " stepSize:" + stepSize + " incSize:" + incSize);
		int filterDataStart = 0;
		int filterDataStop = 0;
		int filterDataCntr = 1;  // avoid divide by zero
		int freqDataCntr = 1;
		if (isManualFilter == true) {
			// convert from millisec to records
			float convert = (float) records / (float) Main.duration;   // records/milisec
			filterDataStart = (int) (0.5f + (float) Main.filterStartAtLoc * convert);    // records
			filterDataStop = (int) (0.5f + (float) Main.filterStopAtLoc * convert);    // records
			if (filterDataStart > 0 && filterDataStop == 0) {
				filterDataStop = records;
			}
			if (filterDataStart < 0) {
				filterDataStart = 0;
			}
			if (filterDataStop > records) {
				filterDataStop = records;
			}
			Log.d(TAG, "voiced() filterDataStart(records):" + filterDataStart + " filterDataStop(records):" + filterDataStop);
		}
		Log.d(TAG, "maxRec (i):" + maxRec + " cntr(i):" + cntr + " filterStart(rec):" + filterDataStart + " filterStop(rec):" + filterDataStop);
		audioNorm = new float[Main.audioDataLength];
		maxEnergy = 0; // this is really maxPower
		maxEn = 0;  // this is really maxEnergy
		aveEnergy = 0;
		float[] imagIn = new float[base];
		for (int i = 0; i < base; i++) {  // just do it once
			imagIn[i] = 0f;
		}
		bufRealOut = new float[base]; // returns 256 real floats and 256 inverted (not usable) floats
		bufImagOut = new float[base]; // returns 256 real floats and 256 imaginary inverse floats (not usable data)
		int ampCrossing = 0; // count amplitude crossings -- for estimate of pure frequency
		int maxAmpCrossing = 0;
		int prevSign = 0;
		int sign = 0;
		int rec = 0;
		locationAtMax = new int[records]; // location within the record used to offset the phrase in the fft
		// find Max frequency
		for (int i = 0; i < cntr; i += incSize) {
			int energyAtJ;
			float tempx = 0;
			float rmsBuf = 0;
			int maxLoc = 0;
			for (int j = 0; j < incSize; j++) { // 0 to 128
				// normalize audioData
				audioNorm[i + j] = (Main.audioData[i + j] + offset) * mult;
				// calc rmsBuf (rmsEnergy for one record)
				tempx = Math.abs(audioNorm[i + j]);
				rmsBuf += tempx * tempx;
				// find max energy location
				energyAtJ = (int) Math.abs(Main.audioData[i + j] + offset);
				if (maxLoc < energyAtJ) {
					maxLoc = energyAtJ;
					locationAtMax[rec] = j;  // location of max energy for each record -- selected later to be only used once per phrase
				}
			}
			rmsBuf = (float) Math.sqrt(rmsBuf/incSize);  // rmsEnergy for one record
			if (maxEn < rmsBuf) { // max energy for the file
				maxEn = rmsBuf;
			}
			rec++;
		}
		int autoFilterFreq = 0;
		// find root mean square energy
		rmsEnergy = new float[records];
		voicedFrame = new int[records];  // holds -1 or phrase number
		float mean = 0;
		rec = 0;
		voicedMeanFreq = 0;
		// find voicedFreq and rmsEnergy (max of record)
		for (int i = 0; i < cntr; i += incSize) {  // inc every 128 (5.8 ms) -- base 1024 -- stepSize 512 or (21.5 hz per step = 11025/512)
			ampCrossing = 0; // count amplitude zero crossings -- for estimate of pure frequency
			prevSign = 0;
			sign = 0;
			rmsEnergy[rec] = 0;
			for (int j = 0; j < base; j++) { // 0 to 1023
				bufIn[j] = audioNorm[i + j];
			}
			fft.windowFunc(3, base, bufIn); // hanning
			fftbas.fourierTransform(base, bufIn, imagIn, bufRealOut, bufImagOut, false);
			for (int j = 1; j < stepSize; j++) { // 1 to 511
				float temp = (float) Math.sqrt(bufRealOut[j] * bufRealOut[j] + bufImagOut[j] * bufImagOut[j]);
				if (rmsEnergy[rec] < temp) {
					rmsEnergy[rec] = temp;
					voicedFreq[rec] = j; // the same as in main fft
					if (maxEnergy < temp) {
						maxEnergy = temp;
					}
				}
				// find pitch from counting x axis crossing
				prevSign = sign;
				sign = (int) Math.signum(bufIn[j]);
				if (prevSign != sign) {
					ampCrossing++;
				}
				if (maxAmpCrossing < ampCrossing) { // this should be double because it changes twice each wave.
					maxAmpCrossing = ampCrossing;
				}
			} // next j

			mean += rmsEnergy[rec] * rmsEnergy[rec];
			aveEnergy += rmsEnergy[rec];  // toward (max) aveEnergy
			voicedMeanFreq += voicedFreq[rec];
			//Log.d(TAG, "voiced() rec:" + rec + " voicedFreq:" + voicedFreq[rec] + " rmsEnergy:" + rmsEnergy[rec] );
			rec++;
		}
		maxAmpCrossing /= 2f;
		mean = (float) Math.sqrt(mean / rec);  // Definition one ***** power
		voicedMeanFreq = voicedMeanFreq / rec; // using fft freq not count crossing zero
		int bestPhraseCount = 0;
//		bestPhraseLen = 0;
		// find filterAve and number of samples to peak
		stdDev = 0;
		filterAve = 0;
		manFilterAve = 0;
		int manFilterDataCntr = 0;
		int minFreq = stepSize;
		int maxFreq = 0;  // never used
		float wtFreq = 0;
		float totEnergy = 0;
		float meanFrac = mean / 4f;
		for (int i = 1; i < records; i++) {
			float temp = rmsEnergy[i] - mean;
			stdDev += temp * temp;
			wtFreq += rmsEnergy[i] * voicedFreq[i];
			totEnergy += rmsEnergy[i];
			// note: these are different than final because of recalc
			if (rmsEnergy[i] > meanFrac) {
				if (minFreq > voicedFreq[i]) {
					minFreq = voicedFreq[i];
					Log.d(TAG, "rmsEnergy:" + rmsEnergy[i] + " meanFrac:" + meanFrac + " minFreq:" + minFreq + " voicedFreq[" + i + "]:" + voicedFreq[i]);
				}
				if (maxFreq < voicedFreq[i]) {
					maxFreq = voicedFreq[i];
					//Log.d(TAG, "rmsEnergy:" + rmsEnergy[i] + " meanFrac:" + meanFrac + " maxFreq:" + maxFreq + " voicedFreq[" + i + "]:" + voicedFreq[i]);
				}
			}
			// keep track of the energy in the filter area. also above and below mean
			if (isEnableAutoFilter == true && rmsEnergy[i] < mean) {
				filterAve += rmsEnergy[i];
				filterDataCntr++;
			}
			if (isManualFilter == true && i >= filterDataStart && i <= filterDataStop) {
				manFilterAve += rmsEnergy[i];
				manFilterDataCntr++;
			}

			if ((isEnableAutoFilter == true && voicedFreq[i] < voicedMeanFreq) || (isManualFilter == true && i >= filterDataStart && i <= filterDataStop)) {
				autoFilterFreq += voicedFreq[i]; // *rmsEnergy[i]; // power weighted average -- divided by total power below
				freqDataCntr++;
			}
			if (rmsEnergy[i - 1] < mean && rmsEnergy[i] > mean) { // cross above mean
				bestPhraseCount++;
				//Log.d(TAG, "cross above mean at i:" + i );
			}
		} // next i
		wtFreq /= totEnergy;
		int iwtFreq = (int) (wtFreq +.5);
		Log.d(TAG, "voiced mean:" + mean + " meanFrac:" + meanFrac + " minFreq:" + minFreq + " maxFreq:" + maxFreq + " wtFreq:" + wtFreq);
		// autoFilterFreq /= filterAve; // this is currently the sum not the average
		autoFilterFreq /= freqDataCntr; // average frequency of voiced below mean frequency	-- used as dimFix in fix = 0
		stdDev = (float) Math.sqrt(stdDev / records);
		Log.d(TAG, "audioNorm mean:" + mean + " stdDev:" + stdDev);
		originalMean = mean;
		aveEnergy /= records;
		filterAve /= filterDataCntr;
		manFilterAve /= manFilterDataCntr;

		if (filterAve == 0) {
			filterAve = aveEnergy / 32;
		}
		float manFilterStdDev = 0;
		if (isManualFilter == true && (filterDataStart > 0 || filterDataStop > 0) && filterDataCntr > 0) {
			for (int i = filterDataStart; i < filterDataStop; i++) {
				manFilterStdDev += (float) Math.pow((double) (manFilterAve - rmsEnergy[i]),2);
			}
			manFilterStdDev = (float) Math.sqrt(manFilterStdDev/manFilterDataCntr);
			Log.d(TAG, "voiced() manFilterAve:" + manFilterAve + " manFilterStdDev:" + manFilterStdDev + " manFilterDataCntr:" + manFilterDataCntr);
		}


		Log.d(TAG, "voiced() isAuto:" + isEnableAutoFilter + " isManual:" + isManualFilter +
				" filterAve:" + filterAve + " filterDataCntr:" + filterDataCntr + " autoFilterFreq:" + autoFilterFreq + " freqDataCntr:" + freqDataCntr++);
		float inverseMult = 1.0f / mult;
		float meanToFilter = mean / filterAve;
		float meanToAve = mean / aveEnergy;
		int cntrLow;
		int prevCntrLow = 0;
		cntrLow = 0;
		float stdDevToMeanRatio = stdDev / originalMean;

		Log.d(TAG, "mean:" + mean + " stdDev:" + stdDev + " stdDevToMeanRatio:" + stdDevToMeanRatio);
		Log.d(TAG, "maxAmpCrossing:" + maxAmpCrossing + " voicedMeanFreq:" + voicedMeanFreq + " minFreq:" + minFreq + " maxFreq:" + maxFreq);
		Log.d(TAG, "aveEnergy:" + aveEnergy + " filterAve:" + filterAve + " stdLim:" + stdLim +
				" autoFilter:" + autoFilter + " inverseMult:" + inverseMult + " meanToFilter:" + meanToFilter +
				" meanToAve:" + meanToAve + " bestPhraseCount:" + bestPhraseCount + " records:" + records);

		voiced = new int[records]; // 0 if quiet up to 255 if song
		lowFreqCutoff = Main.lowFreqCutoff; //
		if (lowFreqCutoff == 0) {
			lowFreqCutoff = minFreq-1;  // don't use because minFreq is a dimension -- if you use it here you will not do fix=0
		}
		highFreqCutoff = Main.highFreqCutoff;
		if (highFreqCutoff == 0) {
			//highFreqCutoff = maxAmpCrossing + 1; // doesn't work -- affects phrasing
			//highFreqCutoff = maxFreq/2+maxFreq;  // doesn't work -- affects phrasing
			//highFreqCutoff = stepSize;  // max alone works
			highFreqCutoff = maxFreq+2;
		}
		Log.d(TAG, "voiced() lowFreqCutoff:" + lowFreqCutoff + " highFreqCutoff:" + highFreqCutoff);  // used in melFilter
		int avePhrases = 0;
		int[] phrAnal = new int[records / 2]; // phrase
		int[] silAnal = new int[records / 2]; // silence
		int[] phrSize = new int[records / 2]; // total voiced in the phrase
		float[] glob = new float[records / 2];
		// defined here -- but dimFix currently 0 will change below
		float[] fixMean = new float[dimFix];
		float[] fixGlob = new float[dimFix];
		int[] fixPhrases = new int[dimFix];
		int[] fixSingle = new int[dimFix];
		float[] signalToNoise = new float[dimFix];
		float[] signal = new float[base / 2];
		filterKernel = new float[base];
		int aveSilence = 0;
		int aveSize = 0;
		snMean = originalMean;
		float meanInc = 0;
		int fixStop = dimFix;
		float aveSigToNoise = 0;
		float ratioSigToNoise = 0;
		int bestSignalToNoiseM = 0;
		int minSignalToNoiseM = 0;
		maxVoiced = 0;
		int maxVoicedM = 0;
		int minVoiced = records;
		int cntrVoiced = 0;
		int minVoicedFreq = 0;
		int lowFreqInc = 0;

		//  loop fix phrases
		float partOfMaxEnergy = maxEnergy / 512;
		float autoGain = 1;
		float[] mNoiseThreshold = new float[stepSize];
		float[] powerThreshold = new float[stepSize];
		int fixStart = 0;
		if (lowFreqCutoff > 0) {  // here is where you bypass fix = 0
			fixStart = 1;
		}
		if (Main.existingRef == 39998 || Main.existingRef == 39999) { // ramp
			fixStart = 1;
			lowFreqCutoff = 0;
			highFreqCutoff = 512;
		}
		int fixEnd = 3;
		if (Main.isEnhanceQuality == true) {
			fixEnd = 4;
		}
		int fixOffset = 0;
		idFix = "";  // "Fix:" will be prepended in writeId
		for (int fix = fixStart; fix < fixEnd; fix++) {
			Log.d(TAG, "* * * * * pass:" + fix + " * * * * " + existingName);
			switch (fix) {
				case 0:  // variable lowFreqCutoff
					if (autoFilterFreq == 0) {
						autoFilterFreq = 1;
					}
					//dimFix = (int) autoFilterFreq;
					dimFix = (int) Math.max(minFreq,iwtFreq);
					lowFreqCutoff = 0;
					lowFreqInc = 1;
					fixMean = new float[dimFix];
					fixGlob = new float[dimFix];
					fixPhrases = new int[dimFix];
					fixSingle = new int[dimFix];
					signalToNoise = new float[dimFix];
					snMean = 0; //filterAve/8; // was 0 -- keep the junk out
					meanInc = 0;
					fixStop = dimFix;
					aveSigToNoise = 0;
					ratioSigToNoise = 0;
					Log.d(TAG, "bestPhraseCount:" + bestPhraseCount);
					Log.d(TAG, "loop dimFix:" + dimFix + " lowFreq from 0 to:" + dimFix);
					break;
				case 1:  // wide range -- variable snMean
					qualLow = lowFreqCutoff; // save for use with quality
					qualHigh = highFreqCutoff;
					Log.d(TAG, "qualLow:" + qualLow + " qualHigh:" + qualHigh);
				case 4: // wide range
					lowFreqInc = 0;
					dimFix = 64;
					snMean = 0; // filterAve/8; // was 0
					//meanInc = (filterAve * 2) / dimFix; //
					meanInc = aveEnergy / dimFix;
					fixMean = new float[dimFix];
					fixGlob = new float[dimFix];
					fixPhrases = new int[dimFix];
					fixSingle = new int[dimFix];
					signalToNoise = new float[dimFix];
					fixStop = dimFix;
					ratioSigToNoise = 0;
					aveSigToNoise = 0;
					Log.d(TAG, "bestPhraseCount:" + bestPhraseCount);
					Log.d(TAG, "loop dimFix:" + dimFix + " snMean:" + snMean + " meanInc:" + meanInc + " to:" + (snMean + meanInc * dimFix));
					break;
				case 2:    // narrow down range
				case 5: // narrow down range
					snMean -= meanInc * 2;
					dimFix = 16;
					meanInc /= 4;
					snMean += meanInc;
					fixMean = new float[dimFix];
					fixGlob = new float[dimFix];
					fixPhrases = new int[dimFix];
					signalToNoise = new float[dimFix];
					fixStop = dimFix;
					ratioSigToNoise = 0;
					aveSigToNoise = 0;
					Log.d(TAG, "bestPhraseCount:" + bestPhraseCount);
					Log.d(TAG, "narrow range loop snMean:" + snMean + " meanInc:" + meanInc + " dimFix:" + dimFix);
					break;
				case 3: // custom filter
					fixStop = 1;  // to less than one or one time
					meanInc = 0;
					ratioSigToNoise = 0;
					aveSigToNoise = 0;
					idLowFreq = lowFreqCutoff;
					idHighFreq = highFreqCutoff;
					Log.d(TAG, "bestPhraseCount:" + bestPhraseCount);
					Log.d(TAG, "custom loop snMean:" + snMean + " meanInc:" + meanInc);
					break;
				case 6: // run with best
					fixStop = 1;  // to less than one or one time
					meanInc = 0;
					ratioSigToNoise = 0;
					aveSigToNoise = 0;
					Log.d(TAG, "bestPhraseCount:" + bestPhraseCount);
					Log.d(TAG, "best loop snMean:" + snMean + " meanInc:" + meanInc);
					break;
			}
			while (snMean < 0) { // get out of the gutter
				snMean += meanInc;
			}
			if (maxVoiced > 0) {
				autoGain *= (float) (512f / maxVoiced);
			}
			Log.d(TAG, "fix:" + fix + " maxVoiced:" + maxVoiced + " autoGain:" + autoGain);
			maxVoiced = 0;
			maxVoicedM = -1;
			for (int m = 0; m < fixStop; m++) {
				// apply stdDev to voiced -- the whole file
				//stdDev = snMean * stdDevToMeanRatio; // removed 10/8/14 -- its back 10/29 -- its gone again -- i'm baaaaack
				// disabled --  I think it is an error to include stdDev here because I don't use it in the loop but I use it in trills after the loop
				maxVoiced = 0;
				int partOfPhrase = 0;
				voiced = new int[records]; // 0 if quiet, up to 255 if song -- i think it is 128 max now
				int aveVoiced = 0;
				for (int i = 1; i < records - 1; i++) {
					if (voicedFreq[i] > lowFreqCutoff && voicedFreq[i] < highFreqCutoff) {  // here is where lowFreqCutoff is used when fix = 1 and 2
						if (rmsEnergy[i] > snMean) {
							voiced[i] = (int) ((rmsEnergy[i] - snMean) * autoGain); // + 0.5f); // the 0.5 was adding trailing 1's
						}
					}
					if ((isManualFilter == true && i >= filterDataStart && i <= filterDataStop) || (voiced[i] < 0)) {
						voiced[i] = 0;
					}
					if (maxVoiced < voiced[i]) {
						maxVoiced = voiced[i];
					}
				}

				if (fix == 3) {  // custom filter from fix = 3
					int prevLen = 0; // use this to stay away from the end of the phrase
					// think about variable fade tried 2 4 1 back to 3
					int fade = 3; // Main.criteriaMultiply; gg 3/6/15 changed from 5 3 5 2 1 3 4 5 2 -- 3/12 fade was 2 now -- 3 is zero errors 2 is two errors
					prevLen = voiced[0];
					mNoiseThreshold = new float[stepSize];
					powerThreshold = new float[stepSize];
					rec = 0;
					for (int i = 0; i < cntr; i += incSize) {
						for (int j = 0; j < base; j++) { // 0 to 1023
							bufIn[j] = audioNorm[i + j];
						}
						// DON'T DO HANNING -- i want noise at the power and frequency it is  -- fft.windowFunc(3, base, bufIn);
						fftbas.fourierTransform(base, bufIn, imagIn, bufRealOut, bufImagOut, false);
						if (voiced[rec] == 0) {
							if (prevLen == 0) { // I am now in silence area
								for (int j = 0; j < stepSize; j++) { // 0 to 511
									float temp = (float) Math.sqrt(bufRealOut[j] * bufRealOut[j] + bufImagOut[j] * bufImagOut[j]);
									if (mNoiseThreshold[j] < temp) {
										mNoiseThreshold[j] = temp; // keep track of the noise at all frequencies
									}
								}
							} else { // let the previous voiced fade
								if (prevLen > fade) {
									prevLen = fade;
								}
								prevLen--; // decrement
							}
						} else { // find the max during voiced
							for (int j = 0; j < stepSize; j++) { // 0 to 511
								float temp = (float) Math.sqrt(bufRealOut[j] * bufRealOut[j] + bufImagOut[j] * bufImagOut[j]);
								if (powerThreshold[j] < temp) {
									powerThreshold[j] = temp; // keep track of the power at all frequencies
								}
							}
							prevLen++; // keep track of previous len and don't look for noise until 5 now 3 silences or len of prev
						}
						rec++;
					} // next i
					for (int j = 0; j < stepSize; j++) { // Weiner filter signal = power^2 / (power^2 + noise^2)
						// weiner signal to noise filter figure 17.1 p.308
						float p2 = powerThreshold[j] * powerThreshold[j];
						float n2 = mNoiseThreshold[j] * mNoiseThreshold[j];
						signal[j] = p2 / (p2 + n2);
					}

					if (Main.isDebug) {
						writeSignalNoise(stepSize, powerThreshold, mNoiseThreshold, signal);
					}
					// step1: signal is in the real the frequency response in rectangular form the size of base/2
					float[] imag = new float[base / 2]; // zeros in imaginary note this is freq domain len base/2
					//bufIn = new float[base]; // zero out bufin -- it is going to hold the impulse response
					fftbas.inverseDFT(base, signal, imag, filterKernel);
					// result is now in the time domain in bufIn with len of base and is called an "impulse response"
					// step2: now convert it from an inpulse response to it's namesake a filterKernel
					fftbas.customFilter(base, stepSize, filterKernel); // the larger the filterLen number the better the match - limit is base/2
					// filterKernel is now a window like hanning or hamming but is for noise in this song
					// test the filterKernel
					realKernelFreq = new float[stepSize]; // real part of the filter's frequency response - kernel is 512 + 512 zeros
					imagKernelFreq = new float[stepSize]; // imaginary part of the filter's frequency response
					fftbas.forwardDFT(base, filterKernel, imagIn, realKernelFreq, imagKernelFreq);
					if (Main.isDebug) {
						writeKernelInFreqDomain(stepSize, realKernelFreq, imagKernelFreq); // pwr = sqrt(bufRealOut^2 + bufImagOut^2); should match signal
					}
					Log.d(TAG, "find mean using FilterKernel");
					voicedFrame = new int[records];  // holds -1 or phrase number
					rmsEnergy = new float[records];
					maxEnergy = 0;
					rec = 0;
					int[] freqRankR = new int[rankCntr]; // used in maxPwr 4 one record long
					float[] maxPwrR = new float[rankCntr]; // used in maxPwr 4 one record long
					float origAveEnergy = aveEnergy;
					float origFilterAve = filterAve;
					float origManFilterAve = manFilterAve;
					aveEnergy = 0;
					maxAmpCrossing = 0;
					mean = 0;
					int phraseAdj = 0;
					for (int i = 0; i < cntr; i += incSize) {
						rmsEnergy[rec] = 0;
						freqRankR = new int[rankCntr]; // used in maxPwr 4 one record long
						maxPwrR = new float[rankCntr]; // used in maxPwr 4 one record long
						// warning this is modified from convolution that works with 255 bufIn and 255 zeros
						for (int j = 0; j < base; j++) { // 0 to 1023
							bufIn[j] = audioNorm[i + j];
						}
						fftbas.fourierTransform(base, bufIn, imagIn, bufRealOut, bufImagOut, false);

						for (int j = 1; j < stepSize; j++) {
							// convolution
							float temp = bufRealOut[j] * realKernelFreq[j] - bufImagOut[j] * imagKernelFreq[j];
							bufImagOut[j] = bufRealOut[j] * imagKernelFreq[j] + bufImagOut[j] * realKernelFreq[j];
							bufRealOut[j] = temp;
							// now power
							temp = (float) Math.sqrt(bufRealOut[j] * bufRealOut[j] + bufImagOut[j] * bufImagOut[j]);
							if (rmsEnergy[rec] < temp) {
								rmsEnergy[rec] = temp;
								voicedFreq[rec] = j; // the same as in main fft
								if (maxEnergy < temp) {
									maxEnergy = temp;
								}
							}
							for (int k = 0; k < rankCntr; k++) { // concepts from version 15Q (that's been a while!)
								if (maxPwrR[k] < temp) {
									for (int n = rankCntr - 1; n > k; n--) {
										freqRankR[n] = freqRankR[n - 1];  // move them down a row to make room for the new max
										maxPwrR[n] = maxPwrR[n - 1];
									}
									freqRankR[k] = j;
									maxPwrR[k] = temp; // add the new max
									break; // to nextj
								}
							}

						} // next j

						mean += rmsEnergy[rec] * rmsEnergy[rec];
						aveEnergy += rmsEnergy[rec];  // toward aveEnergy
						rec++;
					} // next i

					Log.d(TAG, "original mean:" + originalMean + " origFilterAve:" + origFilterAve + " origManFilterAve:" + origManFilterAve);
					mean = (float) Math.sqrt(mean / rec); // definition #2 *********** IS IT EVER USED ??
					partOfMaxEnergy = maxEnergy / 512;
					aveEnergy /= records;

					filterAve = 0;
					filterDataCntr = 0;
					manFilterAve = 0;
					manFilterDataCntr = 0;
					for (int i=0; i < records; i++) {
						if (isEnableAutoFilter == true && rmsEnergy[i] < mean) {
							filterAve += rmsEnergy[i];
							filterDataCntr++;
						}
						if (isManualFilter == true && i >= filterDataStart && i <= filterDataStop) {
							manFilterAve += rmsEnergy[i];
							manFilterDataCntr++;
						}
					}
					filterAve /= filterDataCntr;
					manFilterAve /= manFilterDataCntr;
					Log.d(TAG, "new mean:" + mean + " new filterAve:" + filterAve + " new manFilterAve:" + manFilterAve);
					aveEnergyRatio = aveEnergy / origAveEnergy;
					Log.d(TAG, "fix3 original:" + origAveEnergy + " newAveEnergy:" + aveEnergy + " ratio:" + aveEnergyRatio);

					manFilterStdDev = 0;
					if (isManualFilter == true && (filterDataStart > 0 || filterDataStop > 0) && filterDataCntr > 0) {
						for (int i = filterDataStart; i < filterDataStop; i++) {
							manFilterStdDev += (float) Math.pow((double) (manFilterAve - rmsEnergy[i]),2);
						}
						manFilterStdDev = (float) Math.sqrt(manFilterStdDev/manFilterDataCntr);
						Log.d(TAG, "voiced() manFilterAve:" + manFilterAve + " manFilterStdDev:" + manFilterStdDev + " manFilterDataCntr:" + manFilterDataCntr);
					}
					//originalMean = mean;
				} // fix == 3

				phrAnal = new int[records / 2]; // phrase
				silAnal = new int[records / 2]; // silence
				phrSize = new int[records / 2]; // total voiced in the phrase
				voiceLim = 255;
				aveVoiced = voiced[0];
				int twoback = 0;
				cntSingle = 0; // was never incremented -- now it is how does that affect fixSingle[]
				int cntSN = 0;
				aveSilence = 0;
				avePhrases = 0;
				aveSize = 0;
				float meanGlob = 0;
				// 	find the silence lengths and phrase lengths
				// note: phrases is 0 right now
				phrases = 0;
				if (voiced[phrases] == 0) {
					silAnal[phrases]++;
					aveSilence++;
				} else {
					phrAnal[phrases]++;
					phrSize[phrases] += voiced[phrases];
					avePhrases++;
				}
				if (records > 3) {  // clear out the last two records to force a stop if phrase is active else it won't see the last phrase.
					voiced[records - 1] = 0;
					voiced[records - 2] = 0;
				}
				for (int i = 1; i < records; i++) {
					if (voiced[i-1] == 0 && voiced[i] > 0 && voiced[i+1] == 0) {  // phrase length of 1
						// I don't delete single voiced here -- it messes up signal / noise  --> voiced[i] = 0; // delete single voiced
						cntSingle++;
					}
					if (voiced[i] > 0 && voiced[i-1] == 0) {
						if (rmsEnergy[i] > 0 && rmsEnergy[i - 1] > 0) {
							ratioSigToNoise += rmsEnergy[i] / rmsEnergy[i - 1]; // starting voiced ratio  -- this / prev
							aveSigToNoise += (rmsEnergy[i] + rmsEnergy[i - 1])/2; // starting voiced -- this / prev -- was ratio now average

						}
					}
					if (voiced[i] == 0 && voiced[i-1] > 0) {
						if (rmsEnergy[i - 1] > 0 && rmsEnergy[i] > 0) {
							ratioSigToNoise += rmsEnergy[i - 1] / rmsEnergy[i]; // returning to silence -- prev / this
							aveSigToNoise += (rmsEnergy[i - 1] + rmsEnergy[i])/2; // returning to silence -- prev / this -- was ratio now average
						}
						phrases++;
					}
					if (voiced[i] == 0) {
						silAnal[phrases]++;
						aveSilence++;
					} else {
						phrAnal[phrases]++;
						phrSize[phrases] += voiced[i];
						aveSize += voiced[i];
						avePhrases++;
					}

					aveVoiced += voiced[i];
					if (voiceLim > voiced[i - 1] && voiceLim > voiced[i]) {  // same logic as phrasing below
						voiceLim = voiced[i - 1];  // should become zero if not noisy recording
					}
					if (maxVoiced <= voiced[i]) { // find the last voiced = 256
						maxVoiced = voiced[i];
						maxVoicedM = m;
					}

				} // next record
				if (phrases > 0) {
					avePhrases /= phrases;
					aveSigToNoise /= phrases;
					ratioSigToNoise /= phrases;
					glob = new float[phrases];
					for (int i = 0; i < phrases; i++) {
						glob[i] = (float) (silAnal[i] + phrAnal[i]) * phrSize[i];
						meanGlob += glob[i]; // sum of all phrases for this mean

					}
				}
				aveVoiced /= records;
				totalSilence = aveSilence;
				aveSilence -= silAnal[0];
				meanGlob -= glob[0];  // use average silence for phrase 0
				glob[0] = (float) (aveSilence + phrAnal[0]) * phrSize[0];
				meanGlob += glob[0]; // this one value for each meanInc
				if (phrases > 0) {
					meanGlob /= phrases;
				}
				if (phrases > 1) {
					aveSilence /= (phrases - 1);
				}
				//Log.d(TAG, "aveVoiced:" + aveVoiced + " aveSilence:" + aveSilence);
				//Log.d(TAG, " **** voiceLim:" + voiceLim + " snMean:" + snMean + " m:" + m
				//	+ " cntSingle:" + cntSingle + " phrases:" + phrases);
				fixPhrases[m] = phrases;
				fixMean[m] = snMean;
				fixSingle[m] = cntSingle;
				signalToNoise[m] = ratioSigToNoise;
				fixGlob[m] = meanGlob;
				snMean += meanInc;
				lowFreqCutoff += lowFreqInc;
				//Log.d(TAG, "m:" + m + " phrases:" + fixPhrases[m] + " snMean:" + fixMean[m]
				//		+ " s/n:" + signalToNoise[m] + " maxVoiced:" + maxVoiced);
			} // next m -- go back with next snMean

			// attemped but didn't improve so maybe a good concept but wrong test criteria -- removed for now
			//if (fix == 3 && fixEnd == 4 && fixMean[0] < signalToNoise[0]){
			//	fixEnd = 7;
			//	Log.d(TAG, "**** extending fixEnd to 7 -- snMean:" + fixMean[0] + " s/n:" + signalToNoise[0] + "****");
			//}

			int fixOption = fix;
			// only 3 and 6 have fixStop = 1 -- this is fixStop -- a loop counter not dimension
			if (fixStop > 1) { 	// only fix 0,1,2,4,5 apply
				curveFit.polyPhrase(dimFix, fixPhrases);
				float phrA = curveFit.polyCoef[0];
				float phrB = curveFit.polyCoef[1];
				float phrCC = curveFit.corrCoef;
				curveFit.polySN(dimFix, signalToNoise);
				float snA = curveFit.polyCoef[0];
				float snB = curveFit.polyCoef[1];
				float snCC = curveFit.corrCoef;
				Log.d(TAG, "curveFit phrA:" + phrA + ",B:" + phrB + " cc:" + phrCC + " s/nA:" + snA + ",B:" + snB + " cc:" + snCC);

				// *********** SELECT DELTA (10+fix) or MAX (20+fix) or MAX GLOB (30+fix), mostcommon phrase (40+fix), MidGroup (50+fix) **********************************
				switch (fix) {
					case 0: { // low freq
						fixOffset = 10; // smallest delta s/n
						break;
					}
					case 1: { // wide range
						fixOffset = 10; // smallest delta s/n
						break;
					}
					case 2: { // narrow range
						fixOffset = 10; // smallest delta s/n
						break;
					}
					case 4: { // wide range
						fixOffset = 10; // smallest delta s/n -- was 50 midGroup then test for smallest delta s/n
						break;
					}
					case 5: { // narrow range
						fixOffset = 10; // smallest delta s/n
						break;
					}
				}
				if (fixPhrases[fix] == 0) {
					fixOffset = 50;
					Log.d(TAG, "ZERO phrase so use mid group:" + fixOffset);
				}

				fixOption = fixOffset + fix;
				Log.d(TAG, "fix:" + fix + " fixOption:" + fixOption);
				idFix += fixOption;
			} // fixStop > 1
			switch (fixOption) {
				case 30: // find max glob
				case 31:
				case 32:
				case 33:
				case 34:
					/* 30s NOT USED
					float maxChange = 1;
					int maxChangeM = 0;
					for (int m = 0; m < dimFix - 1; m++) {
						if (fixGlob[m + 1] > fixGlob[maxChangeM]) {
							maxChangeM = m + 1;  // bad label
						}
					}
					if (fix == 0) {
						lowFreqCutoff = maxChangeM;
					} else {
						snMean = fixMean[maxChangeM];
					}
					bestPhraseCount = fixPhrases[maxChangeM];
					Log.d(TAG, "maxChangeM:" + maxChangeM + " s/n:" + signalToNoise[maxChangeM] + " lowFreqCutoff:" + lowFreqCutoff + " highFreqCutoff:" + highFreqCutoff);
					30s NOT USED */
					break;
				case 10:
				case 11: // wide range
				case 14: // wide range
				case 12: // narrow range
				case 15: // narrow range
					float sortVersionSN = 0;
					float[] sortSN = new float[dimFix];
					float[] snGroup = new float[dimFix + 1];
					int[] snGroupCount = new int[dimFix + 1];
					int[] snPhrase = new int[dimFix + 1];
					float maxSN = 0; // incase nothing matches
					int maxSNM = 0;
					for (int m = 0; m < dimFix; m++) {
						sortSN[m] = signalToNoise[m];
						if (maxSN < signalToNoise[m]) {
							maxSN = signalToNoise[m];
							maxSNM = m;
						}
						//Log.d(TAG, "m:" + m + " beforeSort:" + sortSN[m]);
					}
					Arrays.sort(sortSN);
					int snGroupCntr = 0;
					for (int n = 0; n < dimFix; n++) {
						if (sortSN[n] == snGroup[snGroupCntr]) { // s/n matches prev
							snGroupCount[snGroupCntr]++; // count the number of s/n of this size
						} else { // a new s/n
							if (snGroupCount[snGroupCntr] > 1) {
								Log.d(TAG, "there are:" + snGroupCount[snGroupCntr] + " of count:" + fixPhrases[snPhrase[snGroupCntr]] + " and s/n of:" + snGroup[snGroupCntr]);
							}
							snGroupCntr++;
							snGroup[snGroupCntr] = sortSN[n]; // the new s/n
							snGroupCount[snGroupCntr] = 1; // the count of this new s/n
							for (int m = 0; m < dimFix; m++) {
								if (signalToNoise[m] == sortSN[n]) {
									snPhrase[snGroupCntr] = m;
									break;
								}
							}
						}
					} // the last one logged below
					Log.d(TAG, "last -- there are:" + snGroupCount[snGroupCntr] + " of count:" + fixPhrases[snPhrase[snGroupCntr]] + " and s/n of:" + snGroup[snGroupCntr]);
					int maxGroup = 0;  // max group of sn
					int maxGroupPhrase = 0; // max group of fixPhrases
					int groupPhrase = 0;
					int bestMeanPointer = 0;
					int[] snPhraseGroup = new int[snGroupCntr + 1];
					if (snGroupCount[snGroupCntr] > 1) {
						groupPhrase = snPhraseGroup[snGroupCntr]; // a new group
						maxGroup = snGroupCount[snGroupCntr];  // the new group count
						int i = snPhrase[snGroupCntr];
						if (fixPhrases[i] > 1) {
							bestMeanPointer = i;
							sortVersionSN = signalToNoise[i];
							Log.d(TAG, "there are:" + snGroupCount[snGroupCntr] + " of count:" + fixPhrases[snPhrase[snGroupCntr]] + " and s/n of:" + snGroup[snGroupCntr]);
						}
					}
					for (int n = 0; n <= snGroupCntr; n++) { // need <= to get the last snGroupCntr
						if (snGroupCount[n] > 1) {
							int i = snPhrase[n]; // i = pointer into dimFix
							if (fixPhrases[i] > 1) {
								Log.d(TAG, "pointer into dimFix i:" + i + " points to phrase of length:" + fixPhrases[i] + " and s/n of:" + signalToNoise[i]);
								for (int m = i; m >= 0; m--) { // look down from current pointer until phrase length changes
									if (fixPhrases[m] == fixPhrases[i]) {
										snPhraseGroup[n]++;
									} else { // the phrase length changed
										break;
									}
								}
								for (int m = i + 1; m < dimFix; m++) { // look up from current pointer until phrase length changes
									if (fixPhrases[m] == fixPhrases[i]) {
										snPhraseGroup[n]++;
									} else { // the phrase length changed
										break;
									}
								}
								Log.d(TAG, "at i:" + i + " there are:" + snGroupCount[n] + " s/n of:" + signalToNoise[i]
										+ " assoc w/:" + snPhraseGroup[n] + " phrase count of:" + fixPhrases[i]);
								// the maxGroup of snCount is currently disabled
								if (maxGroup < snGroupCount[n]) {
									maxGroup = snGroupCount[n];
									groupPhrase = snPhraseGroup[n]; // associated group
									bestMeanPointer = i;
									//bestPhraseCount = fixPhrases[i];
									sortVersionSN = signalToNoise[i];
									Log.d(TAG, "  selected at:" + i + " with:" + snGroupCount[n] + " s/n of:" + signalToNoise[i]
											+ " assoc w/:" + snPhraseGroup[n] + " phrases of:" + bestPhraseCount);
								}
								if (maxGroup == snGroupCount[n]) {
									if (groupPhrase < snPhraseGroup[n]) {
										groupPhrase = snPhraseGroup[n]; // a new group
										maxGroup = snGroupCount[n];  // the new group count
										bestMeanPointer = i;
										//bestPhraseCount = fixPhrases[i];
										sortVersionSN = signalToNoise[i];
										Log.d(TAG, "  improved at:" + i + " with:" + snGroupCount[n] + " s/n of:" + signalToNoise[i]);
									}
								}

							}
						} // snGroupCount > 1
					} // next n

					float minDeltaSN = signalToNoise[1];  // gg was 0
					float bestSignalToNoise = minDeltaSN; // gg was 0
					float deltaSN = 0;
					int minDeltaM = 0;
					for (int m = 1; m < dimFix; m++) {
						deltaSN = Math.abs(signalToNoise[m - 1] - signalToNoise[m]);  // look for smallest change in s/n
						if (minDeltaSN > deltaSN && fixPhrases[m] > 1) {
							minDeltaSN = deltaSN;
							minDeltaM = m;
							minSignalToNoiseM = m;
							bestPhraseCount = fixPhrases[m];
							bestSignalToNoise = signalToNoise[m];
							//Log.d(TAG, "--> delta s/n m:" + m + " phrases:" + bestPhraseCount + " s/n:" + bestSignalToNoise);
						}
					}
					if (snGroupCntr > 1 && bestSignalToNoise == sortVersionSN && fixPhrases[bestMeanPointer] > 1) {
						Log.d(TAG, "AGREE with s/n:" + bestSignalToNoise + " maxGroupSN:" + maxGroup);
					} else {
						Log.d(TAG, "MISMATCH sortVersion s/n:" + sortVersionSN + " firstMin:" + bestSignalToNoise);
						if (sortVersionSN == 0 || fixPhrases[bestMeanPointer] <= 1) {
							sortVersionSN = bestSignalToNoise;
							bestMeanPointer = minSignalToNoiseM;
							//bestPhraseCount = fixPhrases[bestMeanPointer];
						}
					}
					if (fix == 0) { // lowFreqCutoff
						//lowFreqCutoff = bestMeanPointer;  // first of longest
						//lowFreqCutoff = bestMeanPointer + maxGroup - 1; // changed to last of longest 12/29/14 maxGroup = snGroupCount[n]
						//lowFreqCutoff = maxSNM - 1;
						lowFreqCutoff = Math.max(bestMeanPointer, maxSNM) - 1;
						//lowFreqCutoff = Math.max(lowFreqCutoff, maxGroup) - 1;
						if (lowFreqCutoff < 0) { // can happen if bestMeanPointer, maxSNM, maxGroup are 0 and I subtract 1.
							lowFreqCutoff = 0;
						}
						//bestPhraseCount = fixPhrases[bestMeanPointer];
						bestPhraseCount = fixPhrases[lowFreqCutoff]; // changed to be consistant but the same phrase count
						Log.d(TAG, "s/n:" + sortVersionSN + " maxSNM:" + maxSNM + " bestMeanPointer:" + bestMeanPointer +
								" maxGroup:" + maxGroup + " lowFreqCutoff:" + lowFreqCutoff + " highFreqCutoff:" + highFreqCutoff);
					} else { // snMean
						//bestPhraseCount = fixPhrases[bestMeanPointer];
						snMean = 0;
						int cntrBestMean = 0;
						for (int m = 0; m < dimFix; m++) { // mean
							if (sortVersionSN == signalToNoise[m]) {
								snMean += fixMean[m];
								cntrBestMean++;
							}
						}
						snMean /= cntrBestMean;
						Log.d(TAG, "phrases:" + bestPhraseCount + " snMean:" + snMean +
								" m:" + bestMeanPointer + " s/n:" + sortVersionSN
								+ " lowFreq:" + lowFreqCutoff);
					}
					break;
				case 40: // find most common phrase count -- then find best s/n in that phrase count
				case 41: // wide range
				case 44: // wide range
				case 42: // narrow range
				case 45: // narrow range
					/* 40s NOT USED
					int sortVersionPhrase = 0;
					int[] sortPhrase = new int[dimFix];
					int[] phraseGroup = new int[dimFix + 1];
					int[] phraseGroupCount = new int[dimFix + 1];
					int maxPhrase = 0; // incase nothing matches
					int maxPhraseM = 0;
					int phraseGroupCntr = 0;
					for (int m = 0; m < dimFix; m++) {
						sortPhrase[m] = fixPhrases[m];
						//Log.d(TAG, "m:" + m + " beforeSort:" + sortPhrase[m]);
					}
					Arrays.sort(sortPhrase);
					for (int n = 0; n < dimFix; n++) {
						if (sortPhrase[n] == phraseGroup[phraseGroupCntr]) { // phrase matches prev
							phraseGroupCount[phraseGroupCntr]++; // count the number of phrases of this size
						} else { // a new phrase count
							if (phraseGroupCount[phraseGroupCntr] > 1) {
								Log.d(TAG, "there are:" + phraseGroupCount[phraseGroupCntr] + " phrases of count:" + phraseGroup[phraseGroupCntr]);
							}
							phraseGroupCntr++;
							phraseGroup[phraseGroupCntr] = sortPhrase[n]; // the new phrase
							phraseGroupCount[phraseGroupCntr] = 1; // the count of this new phrase
						}
					} // the last one logged below
					Log.d(TAG, "last -- there are:" + phraseGroupCount[phraseGroupCntr] + " of count:" + phraseGroup[phraseGroupCntr]);
					// bestPhrase is max count -- i.e. most common phrase count found
					int bestPhrase = 0;
					for (int i = 0; i <= phraseGroupCntr; i++) {
						if (maxPhrase < phraseGroupCount[i]) {
							maxPhrase = phraseGroupCount[i];
							bestPhrase = phraseGroup[i];
							Log.d(TAG, " * * * there are:" + maxPhrase + " of count:" + bestPhrase);
						}
					}
					boolean match = false;
					float snMeanTot = 0;
					int snMatchCount = 0;
					int changeN = 0;
					int maxtest = 0;  // 0 = least change s/n   1 = max s/n
					if (maxtest == 1) {
						maxSN = 0;
						for (int n = 0; n < dimFix; n++) {
							if (fixPhrases[n] == bestPhrase) {
								if (maxSN < signalToNoise[n]) {
									maxSN = signalToNoise[n];
									snMean = fixMean[n];  // default if nothing matches
									changeN = n;
									bestPhraseCount = fixPhrases[n];
									Log.d(TAG, " maxtest:" + maxtest + " snMean:" + snMean + " bestPhraseCount:" + bestPhraseCount + " sn:" + signalToNoise[changeN]);
								}
							}
						}
						for (int n = 1; n < dimFix; n++) {
							if (fixPhrases[n] == bestPhrase) {
								if (signalToNoise[n] == maxSN) {
									snMeanTot += fixMean[n];
									snMatchCount++;
									match = true;
								}
							}
						}
					} else {// maxtest
						bestSignalToNoise = 999;
						float minChangeSN = 999;
						for (int n = 1; n < dimFix; n++) {
							if (fixPhrases[n] == bestPhrase) {
								minChangeSN = Math.abs(signalToNoise[n - 1] - signalToNoise[n]);
								if (bestSignalToNoise > minChangeSN) {
									bestSignalToNoise = minChangeSN;
									snMean = fixMean[n];  // default if nothing matches
									bestPhraseCount = fixPhrases[n];
									snMatchCount = 0;
									changeN = n;
									Log.d(TAG, " maxtest:" + maxtest + " snMean:" + snMean + " bestPhraseCount:" + bestPhraseCount + " sn:" + signalToNoise[changeN]);
								}
							}
						}
						for (int n = 1; n < dimFix; n++) {
							if (fixPhrases[n] == bestPhrase) {
								if (signalToNoise[n] == signalToNoise[changeN]) {
									snMeanTot += fixMean[n];
									snMatchCount++;
									match = true;
								}
							}
						}
					} // maxtest
					if (match == true) { // it will always be true
						snMean = snMeanTot / snMatchCount;
						Log.d(TAG, " match is true snMean:" + snMean + " bestPhraseCount:" + bestPhraseCount + " snMeanTot:" + snMeanTot + " snMatchCount:" + snMatchCount);
					} else { // didn't find duplicates
						Log.d(TAG, " no duplicates snMean:" + snMean + " bestPhraseCount:" + bestPhraseCount + " sn:" + signalToNoise[changeN]);
					}
					40s NOT USED */
					break;
				// 50s only used if phrase count 0
				case 50: // find most common phrase count -- then pick the middle of that - then look for any better in that range
				case 51: // wide range
				case 54: // wide range
				case 52: // narrow range
				case 55: // narrow range
					int sortVersionPhrase = 0;
					int[] sortPhrase = new int[dimFix + 1];
					int[] phraseGroup = new int[dimFix + 1];
					int[] phraseGroupCount = new int[dimFix + 1];
					int maxPhrase = 0; // incase nothing matches
					int maxPhraseM = 0;
					int phraseGroupCntr = 0;
					for (int m = 0; m < dimFix; m++) {
						// this is different in that there is no sort -- I want them in order to count the count of most common.
						if (phraseGroup[phraseGroupCntr] == fixPhrases[m]) { // phrase matches prev
							phraseGroupCount[phraseGroupCntr]++; // count the number of phrases of this size
						} else {
							phraseGroupCntr++;
							phraseGroup[phraseGroupCntr] = fixPhrases[m]; // the new phrase
							sortPhrase[phraseGroupCntr] = m;  // using sort phrase differently  -- to remember where this group starts
							phraseGroupCount[phraseGroupCntr] = 1; // the count of this new phrase
						}
					}
					for (int n = 1; n <= phraseGroupCntr; n++) { // there is no zero
						if (maxPhrase < phraseGroupCount[n]) {
							maxPhrase = phraseGroupCount[n];
							maxPhraseM = sortPhrase[n];
						}
					}
					if (maxPhraseM == 0) {
						maxPhraseM++;
						maxPhrase--;
					}
					int midPoint = maxPhraseM + maxPhrase/2;
					snMean = fixMean[midPoint];  // incase nothing is better
					// change from fixMean to minChangeSN
					float minChangeSN = Math.abs(signalToNoise[midPoint - 1] - signalToNoise[midPoint]);
					for (int m = maxPhraseM; m < (maxPhraseM + maxPhrase); m++) {
						float temp = Math.abs(signalToNoise[m - 1] - signalToNoise[m]);
						if (minChangeSN > temp) {
							Log.d(TAG, " better midPoint m:" + m  + " temp:" + temp  + " minChangeSN:" + minChangeSN + " new snMean:" + fixMean[m]);
							minChangeSN = temp;
							snMean = fixMean[m];
							midPoint = m;
						}
					}
					if (fix == 0) { // lowFreqCutoff
						lowFreqCutoff = midPoint; // changed to last of longest 12/29/14 maxGroup = snGroupCount[n]
					}
					Log.d(TAG, "there are:" + maxPhrase + " phrases of count:" + phraseGroup[phraseGroupCntr]
							+ " starting at:" + maxPhraseM + " with midPoint:" + midPoint + " snMean:" + snMean);
					break;
				// using max s/n
				case 20: // lowFreqCutoff
				case 21: // wide range
				case 24: // wide range
				case 22: // narrow down
				case 25: // narrow down
					/* 20s NOT USED
					int firstPeak = 0;
					int firstPeakM = 0;
					int centerMin = records;
					int centerMinM = 0;
					int lastPeak = 0;
					int lastPeakM = 0;
					int lowest = records;
					int lowestM = -1;
					float phraseMean = 0;
					float singleMean = 0;
					float phraseGlob = 0;
					float signalToNoiseMean = 0;
					Log.d(TAG, "Finding snMean with phrase mean and signal to noise");
					Log.d(TAG, "autoFilter:" + autoFilter + " bestPhraseCount:" + bestPhraseCount);
					for (int m = 0; m < dimFix; m++) { // mean
						phraseMean += fixPhrases[m] * fixPhrases[m];
						singleMean += fixSingle[m] * fixSingle[m];
						signalToNoiseMean += signalToNoise[m] * signalToNoise[m];
						phraseGlob += fixGlob[m] * fixGlob[m];
					}
					phraseMean /= dimFix;
					phraseMean = (float) Math.sqrt(phraseMean);
					singleMean /= dimFix;
					singleMean = (float) Math.sqrt(singleMean);
					signalToNoiseMean /= dimFix;
					signalToNoiseMean = (float) Math.sqrt(signalToNoiseMean);
					phraseGlob /= dimFix;
					phraseGlob = (float) Math.sqrt(phraseGlob);
//   	 		singleMean /= 2;
					Log.d(TAG, "phrase mean:" + phraseMean + " singleMean:" + singleMean + " s/n mean:" + signalToNoiseMean + " phraseGlob:" + phraseGlob);

					float stdDevPhrase = 0;
					float stdDevSingle = 0;
					float stdDevSignalToNoise = 0;
					float stdDevGlob = 0;
					for (int m = 0; m < dimFix; m++) { // stdDev
						float temp = phraseMean - fixPhrases[m];
						stdDevPhrase += temp * temp;
						temp = singleMean - fixSingle[m];
						stdDevSingle += temp * temp;
						temp = signalToNoiseMean - signalToNoise[m];
						stdDevSignalToNoise += temp * temp;
						temp = phraseGlob - fixGlob[m];
						stdDevGlob += temp * temp;
					}
					stdDevPhrase /= dimFix;
					stdDevPhrase = (float) Math.sqrt(stdDevPhrase);
					stdDevSingle /= dimFix;
					stdDevSingle = (float) Math.sqrt(stdDevSingle);
					stdDevSignalToNoise /= dimFix;
					stdDevSignalToNoise = (float) Math.sqrt(stdDevSignalToNoise);
					stdDevGlob /= dimFix;
					stdDevGlob = (float) Math.sqrt(stdDevGlob);
					Log.d(TAG, "phrase stdDev:" + stdDevPhrase + " stdDevSingle:" + stdDevSingle + " s/n std:" + stdDevSignalToNoise + " glob std:" + stdDevGlob);

					// decide which s/n level to use
					bestSignalToNoiseM = 0;
					bestSignalToNoise = 0;
					minDeltaSN = signalToNoise[0];
					int minDeltaSNM = 0;
					deltaSN = 0;
					int cntrBestMean = 0;
					boolean found = false;
					for (int m = 1; m < dimFix; m++) { // starts at one
						if (bestSignalToNoise < signalToNoise[m]) {
							Log.d(TAG, " max-> m:" + m + " phrases:" + fixPhrases[m] + " snMean:" + fixMean[m] + " signalToNoise[m]:" + signalToNoise[m]);
							bestSignalToNoise = signalToNoise[m];
							bestSignalToNoiseM = m;
							snMean = fixMean[m];
							bestPhraseCount = fixPhrases[m];
							found = true;
						}
					}

					if (found == false) {
						snMean = 0;
						bestSignalToNoiseM = 0;
						for (int m = 0; m < dimFix; m++) { // mean
							if (stdDevGlob > 0) {
								if (Math.abs(fixGlob[m] - phraseGlob) / stdDevGlob < stdLim) {
									if (Math.abs(signalToNoise[m] - signalToNoiseMean) / stdDevSignalToNoise < stdLim) {
										snMean += fixMean[m];
										cntrBestMean++;
										bestSignalToNoiseM = m;
									}
								}
							} else {
								snMean += fixMean[m];
								cntrBestMean++;
							}
						}
						snMean /= cntrBestMean;

					} else { // found == true
						snMean = 0;
						for (int m = 0; m < dimFix; m++) { // mean
							if (signalToNoise[m] == bestSignalToNoise) {
								snMean += fixMean[m];
								cntrBestMean++;
							}
						}
						snMean /= cntrBestMean;
					}
					Log.d(TAG, "phrases:" + fixPhrases[bestSignalToNoiseM] + " snMean:" + snMean + " cntr:" + cntrBestMean
							+ " m:" + bestSignalToNoiseM + " found:" + found + " s/n:" + bestSignalToNoise
							+ " lowFreq:" + lowFreqCutoff);
					if (snMean == 0 && fix > 0) {
						snMean = fixMean[dimFix / 2];
						bestPhraseCount = fixPhrases[dimFix / 2];
						Log.d(TAG, "* * * failed to find Best snMean * * * revert to:" + snMean);
					}
					20s NOT USED */
					break;
				case 3: // custom filter
				case 6: // done
					break;

			} // switch
		} // next fix

		samplesToMax = new int[records];  // samples (Length) between max energy
		int cntrSamplesToMax = 0;
		// find the peak and mark it with a one
		float phraseMax = Math.abs(audioNorm[0]);
		for (int i = 2; i < records -2; i++) {
			if (rmsEnergy[i] > rmsEnergy[i-1]){
				if (rmsEnergy[i-1] > rmsEnergy[i-2]){
					if (rmsEnergy[i] > rmsEnergy[i+1]){
						if (rmsEnergy[i+1] > rmsEnergy[i+2]){
							samplesToMax[i]=1;  // all zeros except set peaks to 1 -- used in DefineDetail
							cntrSamplesToMax++;
						}
					}
				}
			}

		} // next i
		if (cntrSamplesToMax == 0) {
			trillLen = 0;
		} else {
			trillLen = records / cntrSamplesToMax; // records per peak -- this includes silence so is not meaningful
		}
		// but samplesToMax is meaningful and used in DefineDetail;
		//Log.d(TAG, "trills -- records:" + records + "/ cntrSamplesToMax:" + cntrSamplesToMax + " =trillLen:" + trillLen);

		// remove voiced anywhere in the file if filter exists and energy is less than 3sigma of the average in the filter.
		int cntrRemovedViaFilter = 0;
		if (isManualFilter == true && (filterDataStart > 0 || filterDataStop > 0) && filterDataCntr > 0) {
			float man3Sigma = manFilterAve + manFilterStdDev*3f;
			Log.d(TAG, "isManualFilter man3Sigma:" + man3Sigma );
			for (int i = 2; i < records-2; i++) {
				if (voiced[i] > 0 && rmsEnergy[i] < man3Sigma) {
					if (rmsEnergy[i-1] > man3Sigma && rmsEnergy[i] < man3Sigma && rmsEnergy[i+1] > man3Sigma) {
						// leave it alone if it is a single silence else zero out if will be single phrase
						if (rmsEnergy[i-2] < man3Sigma || rmsEnergy[i+2] < man3Sigma ) {
							voiced[i] = 0;
							cntrRemovedViaFilter++;
						}
					} else {
						voiced[i] = 0;
						cntrRemovedViaFilter++;
					}
				}
			}
		}

		int cntrRemovedViaFreq = 0;

		// remove voiced anywhere in the file if frequecy outside a manual limit
		if (isManualFilter == true && (Main.lowFreqCutoff > 0 || Main.highFreqCutoff > 0)) {
			for (int i = 0; i < records; i++) {
				if (voiced[i] > 0) {
					if (voicedFreq[i] < Main.lowFreqCutoff) {
						voiced[i] = 0;
						cntrRemovedViaFreq++;
					}
					if (Main.highFreqCutoff > 0 && voicedFreq[i] > Main.highFreqCutoff) {
						voiced[i] = 0;
						cntrRemovedViaFreq++;
					}
				}
			}
		}

		Log.d(TAG, "*** find Voiced");
		Log.d(TAG, " file:" + mFileName + " _" + Main.existingInx + "." + Main.existingSeg );
		Log.d(TAG, " AutoFilter:" + isEnableAutoFilter + " Strt:" + Main.filterStartAtLoc + " Stop:" + Main.filterStopAtLoc
				+ " AutoFilterOption:" + Main.isAutoFilter  + " mic:" + Main.sourceMic + " isEnhanceQuality:" + Main.isEnhanceQuality);
		Log.d(TAG, " mean:" +  originalMean + " stdDev:" + stdDev + " filterAve:" + filterAve + " snMean:" + snMean);
		Log.d(TAG, " cntrRemovedViaFilter:" + cntrRemovedViaFilter + " cntrRemovedViaFreq:" + cntrRemovedViaFreq);
		Log.d(TAG, " stdLim:" + stdLim + " voiceLim:" + voiceLim
				+ " debug:" + Main.isDebug + " audsrc:" + audsrc + " filterCntr:" + filterCntr );
		Log.d(TAG, " aveEnergy:" + aveEnergy + " lowFreqCutoff:" + lowFreqCutoff);

		phraseCntr = 0; // count of phrases bounded by silence
		int silence = 0;
		int phraseLength = 0;
		boolean isPhrase = false;
		int lookAhead = 3;
		int startRec = -1;
		int endRec = -1;
		int thisRecordIsGreater = 0;
		float bestStartRatio = 1.0f; // this/prev > 2;
		float bestEndRatio = 1.0f;    // this/prev < .6;
		int startI = 0;
		float pwrTotal = 0;
		int maxVoiced = 0;

		if (records > 3) {  // clear out the first and last two records to force a start and stop if phrase is active else it won't see the last phrase.
			voiced[0] = 0;
			voiced[1] = 0;
			voiced[records - 1] = 0;
			voiced[records - 2] = 0;
		}

		for (int i=0; i < records-2; i++) {
			if (voiced[i] == 0 && voiced[i+1] > 0 && voiced[i+2] == 0) {  // phrase length of 1
				voiced[i+1] = 0; // delete single voiced
			}
			if (voiced[i] > 0 && voiced[i+1] == 0 && voiced[i+2] > 0) {  // fill silence length of 1 -- note first the single voiced deleted above
				if (rmsEnergy[i+1] > snMean) {
					voiced[i+1] = (int) ((rmsEnergy[i+1] - snMean) * autoGain); // probably freq exceeded limits
					if (voiced[i+1] < 1) {
						voiced[i+1] = 1; // fill single silence
					}
				} else {
					voiced[i+1] = 1; // fill single silence
				}
			}
			if (isPhrase == false) {
				if (voiced[i] > voiceLim && voiced[i+1] > voiceLim ) { // two long -- is single voiced removed above ? -- yes
					isPhrase = true;
				}
			}
			if (isPhrase == true) {
				if (voiced[i] <= voiceLim) {
					isPhrase = false;
				}
			}
			if (isPhrase == true) {  // phrase continues and it is NOT end of recording
				phraseLength++;
				voicedFrame[i] = phraseCntr;
			} else {  // voiced = 0
				silence++;
				voicedFrame[i] = -1;
				if (phraseLength > 0) {
					phraseCntr++; // point to next phrase
					phraseLength = 0;
					silence = 1;  // count this one ( it got you here )
				}
			} // endif phrase has ended

		}


		phraseStart = new int[phraseCntr+1];
		phraseEnd = new int[phraseCntr+1];
		phraseSilence = new int[phraseCntr+1];
		phraseAdj = new int[phraseCntr+1];
		energyPhraseMax = new float[phraseCntr+1];
		int lastPhraseEnd = 0;
		maxVoiced = 0;

		for (int i = 1; i < records-2; i++) {
			int prev = voicedFrame[i-1];
			int phrase = voicedFrame[i];
			int next = voicedFrame[i+1];
			if(prev == -1 && phrase > -1){
				phraseSilence[phrase] = i - lastPhraseEnd - 1;
				phraseStart[phrase] = i;
			}
			if (maxVoiced < voiced[i]) {
				maxVoiced = voiced[i];
			}
			//Log.d(TAG, "findVoiced start:" + phraseStart[phrase]);
			if(next == -1 && phrase > -1) {
				phraseEnd[phrase] = i;
				lastPhraseEnd = i;
				maxVoiced = 0;
				//Log.d(TAG, "findVoiced phrase:" + phrase + " silence:" + phraseSilence[phrase]
				//		+ " len:" + (phraseEnd[phrase]-phraseStart[phrase]+1) + " start:" + phraseStart[phrase]  + " end:" + phraseEnd[phrase]);
			}
		}

		int pc = 0;
		while (pc < phraseCntr) {
			int istart = phraseStart[pc]; // the first voiced
			int iend = phraseEnd[pc];  // the last voiced
			int j = 0;
			for (int i=istart; i<=iend; i++) {  // gg modified from < to <=
				j += voiced[i];
			}
			if (j == (iend-istart+1)) {  // they are all 1's so remove this phrase -- added +1
				if (pc < phraseCntr) {
					for (int k = pc; k < phraseCntr-1; k++) {
						phraseStart[k] = phraseStart[k+1];
						phraseEnd[k] = phraseEnd[k+1];
						if (k == pc) {
							phraseSilence[k] = phraseSilence[k] + phraseSilence[k + 1] + j;
						} else {
							phraseSilence[k] = phraseSilence[k + 1];
						}
					}
				}
				phraseCntr--;
			} else { // re-test the new phrase you shifted down else increment to new phrase
				pc++;
			}
		} // while

		int silCntr = 0;
		int phrCntr = 0;
		for (pc = 0; pc < phraseCntr; pc++) {
			silCntr += phraseSilence[pc];
			phrCntr += (phraseEnd[pc]-phraseStart[pc]+1);
		}
		Log.d(TAG, "findVoiced silCntr:" + silCntr + " phrCntr:" + phrCntr + " lastPhraseEnd:" + lastPhraseEnd + " cntSingle:" + cntSingle);

		// do NOT Normalize each phrase -- but DO adjust start to align maxEnergy
		int totalPhrase = 0;
		totalSilence = 0;
		for (pc = 0; pc < phraseCntr; pc++) {
			int phraseLen = (phraseEnd[pc]-phraseStart[pc])+1;
			int istart = phraseStart[pc]*incSize;
			int iend = phraseEnd[pc]*incSize;
			phraseMax = 0;
			int phraseMaxLoc = 0;
			for (int i=istart; i<=iend; i++) {
				float temp = Math.abs(audioNorm[i]);
				if (phraseMax < temp) {
					phraseMax = temp;
					phraseMaxLoc = i;
				}
			} // next i
			energyPhraseMax[pc] = phraseMax;
			// set start of each phrase as a function of maxRecord
			// I want max to be in the center of 512 so adjust to 256 and it will fall in the crack twice and be in the center once
			phraseAdj[pc] = phraseMaxLoc % incSize;   // modulo
			// now move max value to closest incSize
			if (phraseAdj[pc] > incSize/2) { // greater than half
				phraseStart[pc]++;
				phraseSilence[pc]++;
			} else { // less than half
				phraseEnd[pc]--;
				if (pc+1 < phraseCntr) {
					phraseSilence[pc+1]++;
				}
			}

// ************** possible re-enable this --was disabled because picking up freq outside of phrase **********
			if (pc > 0) {
				totalSilence += phraseSilence[pc]; // not including silence 0
			}
			totalPhrase += phraseLen;
		} // next phrase

		if (phraseCntr > 0) {
			int temp = totalSilence / phraseCntr;  // use the average for silence[0]
			totalSilence += temp;
		}
		if (phraseCntr == 0 && records > 5) {
			totalSilence = phraseSilence[0];
			totalPhrase = records - 4 - totalSilence;
			phraseStart[0] = phraseSilence[0]+1;
			phraseEnd[0] = phraseStart[0] + totalPhrase;
			phraseCntr = 1;
		}
		silPhrRatio = (totalPhrase*100f)/(totalSilence+totalPhrase); // it is now really percent phrase of total
		Log.d(TAG, "pctPhrase:" + silPhrRatio + " totSilence:" + totalSilence + " silence[0]:" + phraseSilence[0] + " totalPhrase:" + totalPhrase + " records:" + records);

		if (Main.isDebug == true) {
			writeVoicedFrame();
		}

	} // findVoiced


} // PlaySong