package com.modelsw.birdingviamic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AudioRecorder extends AppCompatActivity implements OnClickListener {
	// this reads the mic and saves the data and reads the data and plays it.
	// And I think it is clearer (I saved it and audacity converted to wav and got better results clicking ID.
	// however, it is bigendian and Media Recorder can't read it
	private static final String TAG = "AudioRecord";
	AudioRecord audioRecord;
	int audsrc = 0;
	private Button mRecordButton;
	private Button mListenButton = null;
	private Button mPlayButton = null;
	private Boolean isRecording = false;
	private static String mFileName = null;
	private static String temp = null;
	private CharSequence cs = null;
	private TextView fileDate;
	private int totalAudioLen = 0;
	boolean mStartRecording;
	boolean mStartListening;
	boolean mStartPlaying;
	private AudioTrack audioTrack;
	Toolbar toolbar;
	private int thisRef = 0;
	private int thisInx = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Main.db = Main.songdata.getWritableDatabase();
		String qry = "SELECT Value from Options WHERE Name='audsrc'";
		Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		audsrc = rs.getInt(0);
		rs.close();

		setContentView(R.layout.audio_recorder);

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

		TextView mediaType = (TextView) findViewById(R.id.media_type);
		if (Main.isSampleRate == false) {
			mediaType.setText("0.wav");
		}
		if (Main.isSampleRate == true) {
			mediaType.setText("1.wav");
		}
		RadioButton mic0 = (RadioButton) findViewById(R.id.mic_0);
		RadioButton mic1 = (RadioButton) findViewById(R.id.mic_1);
		RadioButton mic5 = (RadioButton) findViewById(R.id.mic_5);
		RadioButton mic6 = (RadioButton) findViewById(R.id.mic_6);

		findViewById(R.id.mic_0).setOnClickListener(this);
		findViewById(R.id.mic_1).setOnClickListener(this);
		findViewById(R.id.mic_5).setOnClickListener(this);
		findViewById(R.id.mic_6).setOnClickListener(this);

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
		thisRef = 0;
		thisInx = 0;
		Main.sampleRate = 22050;
		Main.sampleRateOption = 0;
		if (Main.isSampleRate == true) {
			Main.sampleRate = 44100;
			Main.sampleRateOption = 1;
		}
		if (Main.isStartRecording == true) {
			mRecordButton.performClick();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Main.db.beginTransaction();
		String qry = "UPDATE Options SET Value =" + audsrc  + " WHERE Name='audsrc'";
		Main.db.execSQL(qry);
		qry = "UPDATE Options SET Value =1 WHERE Name='media'";
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
				Log.d(TAG, "onClick Record mStartRecording:" + mStartRecording );
				onRecord(mStartRecording);
				if (mStartRecording) {
					mRecordButton.setText("Stop recording");
				} else {
					mRecordButton.setText("Record");
				}
				mStartRecording = !mStartRecording;
				break;
			}
			case R.id.listen_button: {
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
				Log.d(TAG, "onClick mic_0 default");
				audsrc = 0;
				break;
			}
			case R.id.mic_1: {
				Log.d(TAG, "onClick mic_1 mic");
				audsrc = 1;
				break;
			}
			case R.id.mic_5: {
				Log.d(TAG, "onClick mic_5 camcorder");
				audsrc = 5;
				break;
			}
			case R.id.mic_6: {
				Log.d(TAG, "onClick mic_6 voice recognition");
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

	public void startRecording() {
		Thread recordThread = new Thread(new Runnable(){
			public void run() {
				isRecording = true;
				startRecord();
			}
		});
		recordThread.start();
	}

	public void stopRecording() {
		isRecording = false;
		cs = "Recorded at: " + Main.recordedName.substring(2, 20);  // the time of the recording
		fileDate.setText(cs);
		copyFile();
	}

	public void stopListening() {
		if (audioTrack == null) {
			Log.d(TAG, "audioTrack is null");
			return;
		}
		audioTrack.stop();
	}

	private void startRecord() {
		Log.d(TAG, "startRecord");
		temp = tempFile();  // path/date.pcm
		File file = new File(temp);

		try {
			file.createNewFile();
			Log.d(TAG, "startRecord setupFile and audioData");
			OutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

			int minBufferSize = AudioRecord.getMinBufferSize(Main.sampleRate,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			AudioManager am1 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			Main.isExternalMic = am1.isWiredHeadsetOn();

			// WITH external mic plugged in:
			// 0 d DEFAULT Samsung blocks - internal works on motorola - external mic doesn't block but no increase in volume
			// 1 m MIC Samsung blocks - internal works on motorola - external mic works
			// 2 VOICE_UPLINK ALL Blocked - crash on motorola
			// 3 VOICE_DOWNLINK ALL Blocked - crash on motorola
			// 4 VOICE_CALL ALL Blocked - crash on motorola
			// 5 c CAMCORDER Works - does it have auto gain control - internal works on motorola - external doesn't block doesn't gain
			// 6 v VOICE_RECOGNITION Works -- does it have auto gain control - internal works on motorola - external works
			// 7 VOICE_COMMUNICATION Blocks - internal works on motorola - external not work
			// 8 REMOTE_SUBMIX - crash on motorola

			// audsrc = MediaRecorder.AudioSource.MIC
			short[] audioData = new short[minBufferSize];
			audioRecord = new AudioRecord(audsrc,
					Main.sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
			Log.d(TAG, "AudioRecorder audsrc:" + audsrc + " sampleRate:" + Main.sampleRate + " isExternalMic:" + Main.isExternalMic);
			audioRecord.startRecording();

			int sid = audioRecord.getAudioSessionId();
			boolean agcStatus = AutomaticGainControl.isAvailable();
			boolean agcEnabled = false;
			if (agcStatus) {
				final AutomaticGainControl agc = AutomaticGainControl.create(sid);
				agcEnabled = agc.getEnabled();
			}
			Log.d(TAG, "audioRecord agcStatus:" + agcStatus + " sessionID:" + sid + " agcEnabled:" + agcEnabled );

			while(isRecording == true){
				int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
				// Log.d(TAG, "WHILE recording numberOfShort" + numberOfShort);
				for(int i = 0; i < numberOfShort; i++){
					dataOutputStream.writeShort(audioData[i]);
				}
			}
			Log.d(TAG, "Stop recording");
			audioRecord.stop();
			dataOutputStream.close();

		} catch (IOException e) {
			String msg = "Failed to setAudioSource:" + audsrc + ". Try another Mic Option.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	} // start record

	void startListening(){
		Log.d(TAG, "playRecord");
		if (mFileName == null) {
			String msg = "Nothing recorded to listen to.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			Log.d(TAG, "mFileName is null");
			return;
		}
		File file = new File(temp); // path/date.pcm
		int shortSizeInBytes = Short.SIZE/Byte.SIZE;

		int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
		short[] audioData = new short[bufferSizeInBytes];

		try {
			Log.d(TAG, "open dataInputStream file:" + file.toString());
			InputStream inputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
			int i = 0;
			while(dataInputStream.available() > 0){
				audioData[i] = dataInputStream.readShort();
				i++;
			}
			Log.d(TAG, "completed dataInputStream audioData.length" + audioData.length);
			dataInputStream.close();

			audioTrack = new AudioTrack(
					AudioManager.STREAM_MUSIC,  // The audio stream for music playback (vs voice or alarm)
					Main.sampleRate,
					AudioFormat.CHANNEL_OUT_MONO, // was  CHANNEL_IN_FRONT
					AudioFormat.ENCODING_PCM_16BIT,
					bufferSizeInBytes,
					AudioTrack.MODE_STREAM);
			Log.d(TAG, "audioTrack.play");
			audioTrack.play();
			audioTrack.write(audioData, 0, bufferSizeInBytes);  // am i writing to the player
			Log.d(TAG, "audioTrack.getPlayState()" + audioTrack.getPlayState());
			//if (audioTrack.getPlayState() == audioTrack.PLAYSTATE_STOPPED) {
			//	mListenButton.performClick();
			//}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String tempFile() {  // called during startRecording()
		String format = "yyyy_MMdd_HH.mm.ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		long iNow = System.currentTimeMillis();
		int src = Main.sourceMic;  // 0=pre-recorded 1=internal.wav, 2=internal.m4a, 3=external.wav, 4=external.m4a
		Main.recordedName = "@_" + sdf.format(iNow) + ".wav";  // @_yyyy-MMdd_HH.mm.ss.pcm move to top of list (before A)
		mFileName = Main.songpath + Main.recordedName; // file name is the date
		Log.d(TAG, "mFileName:" + mFileName);
		temp = Main.songpath + "zztemp.pcm";
		return temp;
	}

	private void copyFile() {
		InputStream inStream = null;
		OutputStream outStream = null;
		try{

			File file1 =new File(temp);
			File file2 =new File(mFileName);  // full path wave file
			inStream = new FileInputStream(file1);
			outStream = new FileOutputStream(file2); // for override file content
			totalAudioLen = (int) file1.length();
			byte[] header = new byte[44];
			getWaveFileHeader(totalAudioLen, header);
			outStream.write(header, 0, 44);
			byte[] be = new byte[1024];
			byte[] le = new byte[1024];
			int length;
			while ((length = inStream.read(be)) > 0) {
				for (int j=0; j<length-1; j+=2) {
					le[j] = be[j+1];
					le[j+1] = be[j];
				}
				outStream.write(le, 0, length);
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
		Main.sourceMic = 1; // 0=pre-recorded 1=internal.wav, 2=internal.m4a, 3=external.wav, 4=external.m4a
		if(Main.isExternalMic) {
			Main.sourceMic = 3; // external mic
		}
		Main.sampleRateOption = 0;
		if (Main.isSampleRate == true) {
			Main.sampleRateOption = 1;
		}
		if (Main.existingName.equals("@_RampSource.wav") ) {
			Main.sourceMic = 0; // recorded
			Main.sampleRateOption = 0;  // 22050 hz
			thisRef = 39999;
			thisInx = 1;
		}
		Main.audioSource = audsrc;
		Log.d(TAG, "stop recording Main.existingName:" + Main.existingName);
		Log.d(TAG, "stop recording mFileName:" + mFileName);
		Main.db.beginTransaction();
		ContentValues val = new ContentValues();
		val.put("Ref", thisRef);
		val.put("Inx", thisInx);
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
		val.put("LowFreqCutoff", 0);
		val.put("HighFreqCutoff", 0);
		val.put("FilterStart", 0);
		val.put("FilterStop", 0);
		Main.db.insert("SongList", null, val);
		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();
		val.clear();
	}

	private void getWaveFileHeader(int totalAudioLen, byte[] header) {
		Log.d(TAG, "writing wave file header");
		long longSampleRate = Main.sampleRate;
		long byteRate = 44100; // 16 * 22050 * 1 / 8  = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
		int totalDataLen = totalAudioLen + 36;

		header[0] = 'R';  // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';  // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16;  // size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;  // format = 1
		header[21] = 0;
		header[22] = (byte) 1;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) 2;  // block align
		header[33] = 0;
		header[34] = 16;  // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

	}

	public void buildRampFile() {
		Log.d(TAG, "startRamp");
		temp = tempFile(); // creates file @_date.wave
		// overwrite the @_date.wav with @_RampSource.wav
		Main.recordedName = "@_RampSource.wav";
		mFileName = Main.songpath + Main.recordedName; // file name is the date
		File file = new File(temp);
		try {
			file.createNewFile();
			Log.d(TAG, "startRecord setupFile and audioData");
			OutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
			// I am changing frequency by 1/1024 every 2048/8 samples = 22050 / 2048/8 = change 86.13 times per second
			// file should be 1024 * (2048/8) * 2 bytes long = 4194304 bytes long = 95 seconds --NO-- NOW IS SHORTER SEE BELOW
			int base = 2048;  // size that I send to fft
			int step = 8; // parts of a block
			int halfBlock = base/2; // 1024 the max frequency slot -- the size i get back from fft
			int bufferSize = halfBlock * (base / step);
			int bufferSizeInBytes = bufferSize * 2;
			short[] audioData = new short[bufferSizeInBytes];
			float samplesPerSec = Main.sampleRate; // samples per second 16 bit -- 44100 bytes per sec (size of file in one second)
			float nyquistFreq = samplesPerSec / 2;  // 11025
			float freqStep = nyquistFreq / halfBlock; // 11025 / 1024 = 10.766
			float degPerCycle = 360f;
			float deg = 0;
			float dataStep = samplesPerSec / (base/step);  // 22050/256 = frequency changes each second = 86.132
			float pi = 4 * (float) Math.atan(1); // 3.14...
			float twoPi = pi * 2f;  // 6.28...

			int i = 0; // pointer into file ( increment each block of 2048/step) same as record
			// freqStep is also nyquistFreq / halfBlock // 11025 / 1024 = 10.766
			for (i = 0; i < base; i++) {
				dataOutputStream.writeShort(0);
			}
			for (i = 0; i < halfBlock; i++) { // each slot will increase frequency by 10.766 hz
				float currentFreq = (float) i * freqStep; // 0, 10.766, 21.53, ..., 11014.233
				// currentFreq / freqStep is slot
				// A440 = 440 cycles per second
				// A440 / 11025 = X / 1024 = slot 40.86
				// freq / 11025 = slot 41 / 1024 = 441.43
				// cyclesPerSecond * degreesPerCycle = degreesPerSecond / portionOfOneSecond
				float degreesPerStep = currentFreq * degPerCycle / dataStep / (base/step);  // 40.86 * 360 = 14712.163 degrees / 256 samples = 7.183 deg / sample
				//Log.d(TAG, "buildRamp i:" + i + " currentFreq:" + currentFreq);
				for (int j = 0; j<(base/step); j++) { // j < 256
					deg += degreesPerStep; // pick up where you left off so there is no click
					if (deg > 360) {
						deg -= 360;  // modulo
					}
					float sine = (float) Math.sin(deg / 360 * twoPi);
					int n = i*(base/step)+j;
					audioData[n] = (short) (sine * 32767);
					dataOutputStream.writeShort(audioData[n]);
				} // next j in the block of 2048
			} // slot i within 1024
			for (i = 0; i < base; i++) {
				dataOutputStream.writeShort(0);
			}
			outputStream.close();
		} catch (IOException e) {
			Log.d(TAG, "buildRamp IOException:" + e);
			e.printStackTrace();
		}
		copyFile();
	}  // buildRampFile

	public static void saveAudioNormFile(String nam, int strt, int len, float[] audioNorm) {
		// note this is a raw pcm file -- big endian
		Log.d(TAG, "save file:" +  nam);
		File file = new File(nam);  // ".../Define/before.pcm or after.pcm
		try {
			file.createNewFile();
			OutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
			for (int i = strt; i < len; i++) {
				short audioData = (short) (audioNorm[i] * 32767);
				dataOutputStream.writeShort(audioData);
			}
			outputStream.close();
		} catch (IOException e) {
			Log.d(TAG, "saveAudioNormFile IOException:" + e);
			e.printStackTrace();
		}
	}  // saveAudioNormFile

	public static void saveAudioDataFile(String nam, int strt, int len, short[] audioData) {
		// note this is a raw pcm file -- 16 bit -- big endian -- mono -- 22050
		Log.d(TAG, "save file:" +  nam);
		File file = new File(nam);  // ".../Define/audioData.pcm
		try {
			file.createNewFile();
			OutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
			for (int i = strt; i < len; i++) {
				dataOutputStream.writeShort(audioData[i]);
			}
			outputStream.close();
		} catch (IOException e) {
			Log.d(TAG, "saveAudioDataFile IOException:" + e);
			e.printStackTrace();
		}
	}  // saveAudioDataFile

}
