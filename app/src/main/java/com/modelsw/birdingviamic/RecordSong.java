package com.modelsw.birdingviamic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.database.Cursor;
import android.widget.Toast;

public class RecordSong extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "RecordSong";
    //public static SQLiteDatabase db;
    //public static SongData songdata;
    int audsrc = 0;
    int chanCnt = 1;
    Context ctx = this;
    private boolean fileSaved = false;
    private static String mFileName = null;
    //private static String pathBase = null;
    private static String[] items;
    private static File[] songFile;
    boolean mStartRecording;
    boolean mStartListening;
    boolean mStartPlaying;
    private TextView fileDate;
    private Button mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private CharSequence cs = null;
    private Button mListenButton = null;
    private Button mPlayButton = null;
    private MediaPlayer mPlayer = null;
    //    short[] audioData;
    public boolean recording;
    Toolbar toolbar;
    TextView adStatus;

    public RecordSong() {  // constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_song);

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
        String qry = "SELECT Value from Options WHERE Name='audsrc'";
        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        rs.moveToFirst();
        audsrc = rs.getInt(0);
        rs.close();

        TextView mediaType = (TextView) findViewById(R.id.media_type);
        AudioManager am1 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        Main.isExternalMic = am1.isWiredHeadsetOn();
        String mType = "";
        if (Main.isExternalMic == true) {
            mType = " External";
        } else {
            mType = " Internal";
        }
        if (Main.isStereo == true) {
            mType += " Stereo";
            Main.stereoFlag = 1;
            chanCnt = 2;
        } else {
            mType += " Mono";
            Main.stereoFlag = 0;
            chanCnt = 1;
        }
        if (Main.isSampleRate == true) {
            mType += " 44100";
            Main.sampleRate = 44100;
            Main.sampleRateOption = 1;
        } else {
            mType += " 22050";
            Main.sampleRate = 22050;
            Main.sampleRateOption = 0;
        }
        mType += ".m4a";
        mediaType.setText(mType);
        RadioButton mic0 = (RadioButton) findViewById(R.id.mic_0);
        RadioButton mic1 = (RadioButton) findViewById(R.id.mic_1);
        RadioButton mic5 = (RadioButton) findViewById(R.id.mic_5);
        RadioButton mic6 = (RadioButton) findViewById(R.id.mic_6);

        findViewById(R.id.mic_0).setOnClickListener(this);
        findViewById(R.id.mic_1).setOnClickListener(this);
        findViewById(R.id.mic_5).setOnClickListener(this);
        findViewById(R.id.mic_6).setOnClickListener(this);

        qry = "SELECT Value from Options WHERE Name='audsrc'";
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        if (rs.getCount() == 1) {
            rs.moveToFirst();
            audsrc = rs.getInt(0);
        }
        rs.close();

        switch (audsrc) {
            case 0: {
                mic0.performClick();
                break;
            }
            case 1: {
                mic1.performClick();
                break;
            }
            case 5: {
                mic5.performClick();
                break;
            }
            case 6: {
                mic6.performClick();
                break;
            }
        }

        mRecordButton = (Button) findViewById(R.id.record_button);
        findViewById(R.id.record_button).setOnClickListener(this);
        mListenButton = (Button) findViewById(R.id.listen_button);
        findViewById(R.id.listen_button).setOnClickListener(this);
        mPlayButton = (Button) findViewById(R.id.play_button);
        findViewById(R.id.play_button).setOnClickListener(this);
        fileDate = (TextView) findViewById(R.id.file_date);
        cs = "";
        fileDate.setText(cs);
        mStartRecording = true;
        mStartListening = true;
        mStartPlaying = true;
        Main.showPlayFromRecord = false;
        if (Main.isStartRecording == true) {
            mRecordButton.performClick();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            //mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        Main.db.beginTransaction();
        String qry = "UPDATE Options SET Value =" + audsrc  + " WHERE Name='audsrc'";
        Main.db.execSQL(qry);
        qry = "UPDATE Options SET Value =2 WHERE Name='media'";
        Main.db.execSQL(qry);
        Main.db.setTransactionSuccessful();
        Main.db.endTransaction();

    }

    /* removed because I moved the record button outside of the scroll.
        // this allows the record button to act on release of touch and doesn't interpret the touch as scroll
        public boolean dispatchTouchEvent(MotionEvent ev) {
            // Normal event dispatch to this container's children, ignore the return value
            super.dispatchTouchEvent(ev);
            // Always consume the event so it is not dispatched further up the chain
            Log.d(TAG, "dispatchTouchEvent ev:" + ev.toString() );
            return false;
        }
    */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_button: {
                Log.d(TAG, "onClick Record mStartRecording:" + mStartRecording);
                onRecord(mStartRecording);
                if (mStartRecording) {
                    mRecordButton.setText("Stop recording");
                    fileSaved = false;
                } else {
                    mRecordButton.setText("Record");
                }
                mStartRecording = !mStartRecording;
                break;
            }
            case R.id.listen_button: {
                if (fileSaved == false) {
                    break;
                }
                Log.d(TAG, "onClick Listen mStartListening:" + mStartListening );
                onListen(mStartListening);
                if (mStartListening) {
                    mListenButton.setText("Stop Listening");
                } else {
                    mListenButton.setText("Listen");
                }
                mStartListening = !mStartListening;
                break;
            }
            case R.id.play_button: {  // load play screen via main onResume()
                Log.d(TAG, "onClick Play");
                Main.songCounter = 0; // clear the list so this one will play
                Main.showPlayFromRecord = true; // force the play screen to appear

                finish();
            }
            case R.id.mic_0: {
                Log.d(TAG, "onClick mic_0 Default");
                audsrc = 0;
                break;
            }
            case R.id.mic_1: {
                Log.d(TAG, "onClick mic_1 Mic");
                audsrc = 1;
                break;
            }
            case R.id.mic_5: {
                Log.d(TAG, "onClick mic_5 Camcorder");
                audsrc = 5;
                break;
            }
            case R.id.mic_6: {
                Log.d(TAG, "onClick mic_6 Voice Recognition");
                audsrc = 6;
                break;
            }

        } // switch
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onListen(boolean start) {
        if (start) {
            startListening();
        } else {
            stopListening();
        }
    }

    private void startListening() {
        long duration = 0;
        try {
            if (mFileName == null) {
                String msg = "Nothing recorded to listen to.";
                showToast(msg);
                return;
            }
            Log.d(TAG, "startListening");
            Log.d(TAG, "mFileName:" + mFileName);
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.start();
                duration = mPlayer.getDuration();
            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
                String msg = "Failed to prepare recording.";
                showToast(msg);
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Illegal StateException:" + e); // not initialized
            String msg = "Not initialized yet.";
            showToast(msg);
        }
        // I need this on Completion listener because the file length extends until I click stop
        // even though the song is through playing
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            // @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Main.isPlaying = false;
                Log.d(TAG, "startListening onCompletion" );
                mListenButton.performClick();
            }
        });
    }

    public void showToast(final String msg)	{
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void stopListening() {
        if (mPlayer == null) {
            Log.d(TAG, "stopListening() mPlayer is null" );
            return;
        }
        Log.d(TAG, "stopListening" );
        //mPlayer.reset();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        //AudioManager am1 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //Main.isExternalMic = am1.isWiredHeadsetOn();
        // the following crashes on null
        //IntentFilter iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        //Intent iStatus = this.registerReceiver(null, iFilter);
        //Main.isExternalMic = iStatus.getIntExtra("state", 0) == 1;
        // WITH external mic plugged in:
        // 0 d DEFAULT Samsung blocks - Motorola - internal works - external mic increase
        // 1 m MIC Samsung blocks - Motorola - internal works - external mic increase
        // 2 VOICE_UPLINK ALL Blocked - crash on motorola
        // 3 VOICE_DOWNLINK ALL Blocked - crash on motorola
        // 4 VOICE_CALL ALL Blocked - crash on motorola
        // 5 c CAMCORDER Works - does it have auto gain control - Motorola - internal works - external mic no change in volume -- doesn't work -- doesn't block
        // 6 v VOICE_RECOGNITION Works -- does it have auto gain control - internal works on motorola - external works
        // 7 VOICE_COMMUNICATION Blocks - internal works on motorola - external not work
        // 8 REMOTE_SUBMIX - crash on motorola
        /*
            https://source.android.com/devices/audio/implement.html
            Source tuning
            For AudioSource tuning, there are no explicit requirements on audio gain or audio processing with the exception of voice recognition (VOICE_RECOGNITION).
            The requirements for voice recognition are:
               "flat" frequency response (+/- 3dB) from 100Hz to 4kHz
                close-talk config: 90dB SPL reads RMS of 2500 (16bit samples)
                level tracks linearly from -18dB to +12dB relative to 90dB SPL
                THD < 1% (90dB SPL in 100 to 4000Hz range)
                8kHz sampling rate (anti-aliasing)
                Effects/pre-processing must be disabled by default

            Examples of tuning different effects for different sources are:
            Noise Suppressor
                Tuned for wind noise suppressor for CAMCORDER
                Tuned for stationary noise suppressor for VOICE_COMMUNICATION
            Automatic Gain Control
                Tuned for close-talk for VOICE_COMMUNICATION and main phone mic
                Tuned for far-talk for CAMCORDER
         */

        // audsrc = MediaRecorder.AudioSource.MIC
        mFileName = tempFile();  // path/date.m4a
        mRecorder = new MediaRecorder();
        Log.d(TAG, "MediaRecorder sampleRate:" + Main.sampleRate + " audsrc:" + audsrc + " isExternalMic:" + Main.isExternalMic);
        cs = "........";
        fileDate.setText(cs);
        try {
            try {
                mRecorder.setAudioSource(audsrc); // MediaRecorder.AudioSource.CAMCORDER works and does NOT have Auto Gain Control
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);       // MPEG_4 works -- try adts for fraunhofer
                mRecorder.setAudioChannels(chanCnt);  // 1=mono 2=stereo
                mRecorder.setAudioSamplingRate(Main.sampleRate);  // 22050 / 44100
                mRecorder.setAudioEncodingBitRate(96000); // i tried 44100 it changed to 96000 -- the encoder needs this rate to save the data -- removed -- does it work?
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);  // AAC  works
                mRecorder.setOutputFile(mFileName);  // full path
                Log.d(TAG, "startRecord setupFile and audioData");
                mRecorder.prepare();
            } catch(IOException e) {
                e.printStackTrace();
                String msg = "Failed to prepare audio source:" + audsrc + ". Try another Mic Option.";
                showToast(msg);
                return;
            }
            mRecorder.start();
        } catch (RuntimeException e) {
            e.printStackTrace();
            String msg = "Failed to setAudioSource:" + audsrc + ". Try another Mic Option.";
            showToast(msg);
            return;
        }
    }


    private void stopRecording() {
        //RecordThread recordThread = new RecordThread();
        //recording = false;
        //recordThread.quit();
        cs = "Saving file ...";  // the time of the recording
        fileDate.setText(cs);
        fileSaved = false;
        Log.d(TAG, "stopRecording fileSaved:" + fileSaved);
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            String msg = "Error stopRecording:" + e;
            Log.d(TAG, msg);
            return;
        }
        InputStream inStream = null;
        OutputStream outStream = null;
        try{

            File file1 = new File(mFileName);  // full path
            File file2 = new File(Main.songpath + "zztemp.m4a"); // mp4 works, .aac works
            inStream = new FileInputStream(file1);
            outStream = new FileOutputStream(file2); // for override file content
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, length);
            }
            if (inStream != null)inStream.close();
            if (outStream != null)outStream.close();
            Log.d(TAG, "File Copied from:" + file1);
            Log.d(TAG, "File Copied   to:" + file2);
        } catch(IOException e) {
            e.printStackTrace();
        }
        Main.existingName = Main.recordedName;
        Main.existingRef = 0;
        Main.existingInx = 0;
        Main.existingSeg = 0;
        // 0=pre-recorded, 1=internal mic, 2=external mic
        Main.sourceMic = Main.isExternalMic ? 2 : 1; // 2 = external / 1 internal mic
        Main.stereoFlag = Main.isStereo ? 1 : 0; // 1 = stereo / 0 = mono
        Main.audioSource = audsrc;
        Log.d(TAG, "stop recording Main.existingName:" + Main.existingName);
        Log.d(TAG, "stop recording mFileName:" + mFileName);
        try {
            Log.d(TAG, "db begin transaction");
            Main.db.beginTransaction();
            ContentValues val = new ContentValues();
            try {
                val.put("Ref", 0);
                val.put("Inx", 0);
                val.put("Seg", 0);
                val.put("Path", Main.path);
                val.put("FileName", Main.existingName);
                val.put("Start", 0);
                val.put("Stop", 0);
                val.put("Identified", 0);
                val.put("Defined", 0);
                val.put("AutoFilter", 0);
                val.put("Enhanced", 0);
                val.put("Smoothing", 0);
                val.put("SourceMic", Main.sourceMic);
                val.put("SampleRate", Main.sampleRateOption);  // 0=22050, 1=44100
                val.put("AudioSource", Main.audioSource); // 0=default, 1=mic, 5=camcorder, 6 voice recognition
                val.put("Stereo", Main.stereoFlag); // 0=mono, 1=stereo
                val.put("LowFreqCutoff", 0);
                val.put("HighFreqCutoff", 0);
                val.put("FilterStart", 0);
                val.put("FilterStop", 0);
                Main.db.insert("SongList", null, val);
                Main.db.setTransactionSuccessful();
            } finally {
                val.clear();
                Main.db.endTransaction();
                cs = "Recorded at: " + Main.recordedName.substring(2, 20);  // the time of the recording
                fileDate.setText(cs);
                fileSaved = true;
                Log.d(TAG, "db end transaction fileSaved:" + fileSaved);
            }
        } catch( Exception e ) {
            Log.e(TAG, "Database Exception: " + e.toString() );
        }
    }

    public String tempFile() {  // called during startRecording()
        String format = "yyyy_MMdd_HH.mm.ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        long iNow = System.currentTimeMillis();
        Main.recordedName = "@_" + sdf.format(iNow) + ".m4a";  // @_yyyy_MMdd_HH.mm.ss.3gp move to top of list (before A) .mp4 works  -- trying aac
        mFileName = Main.songpath + Main.recordedName; // file name is the date
        Log.d(TAG, "mFileName:" + mFileName);
        return mFileName;

    }


}
