package com.modelsw.birdingviamic;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import com.modelsw.birdingviamic.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.modelsw.birdingviamic.PlaySong.*;


@TargetApi(16)
public class VisualizerView extends View {  // disabled --> View implements OnTouchListener

	private static final String TAG = "VisualizerView";
	private byte[] mBytes;
	private byte[] mFFTBytes;
	private static Rect mRect = new Rect();
	public static Visualizer mVisualizer;
	private static Set<Renderer> mRenderers;
	static Matrix matrix = new Matrix();
	//static Matrix savedMatrix = new Matrix();
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	public static int bitmapWidth;
	public static int bitmapHeight;
	boolean isDrawOnce = false;
	boolean isInit = false;
	public static Bitmap mCanvasBitmap;
	public static Canvas mCanvas;
	public static Paint mPaint = new Paint();
	public static int once = 0;
	String qry = "";
	Cursor rs;
	Cursor rs1;
	float scaleH;
	float scaleW = 0.5f;
	float screenCenterW;
	float screenCenterH;
	public static int shortonce = 0;
	int vvtop = 168;
	int vvHeight = 1515;

	public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init();
		VisualizerView view = (VisualizerView) findViewById(R.id.visualizerView);
	}

	public VisualizerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VisualizerView(Context context) {
		this(context, null, 0);
	}

	private void init() {
		mBytes = null;
		mFFTBytes = null;
		if(mPaint == null) {
			mPaint = new Paint();
		}
		mPaint.setColor(Color.argb(255, 255, 255, 255));
		mRenderers = new HashSet<Renderer>();
		vvtop = PlaySong.vvtop;
		vvHeight = PlaySong.vvHeight;
		isInit = PlaySong.isInit;

	}

	/**
	 * Links the visualizer to a player
	 * @param player - MediaPlayer instance to link to
	 */
	public void link(final MediaPlayer player) {
		once = 0;
//		if (Main.isLoadDefinition == true) {
//			return;
//		}
		if(player == null) {
			throw new NullPointerException("Cannot link to null MediaPlayer");
		}
		// Create the Visualizer object and attach it to our media player.
		mVisualizer = new Visualizer(player.getAudioSessionId());
		int sm = mVisualizer.setScalingMode(mVisualizer.SCALING_MODE_NORMALIZED); // best for visualizing music -- according to android development
		Log.d(TAG, "link scalingMode:" + sm);
		int minCaptureSize = Visualizer.getCaptureSizeRange()[0];
		int maxCaptureSize = Visualizer.getCaptureSizeRange()[1];
		Log.d(TAG, "link: minCaptureSize:" + minCaptureSize + " maxCaptureSize:" + maxCaptureSize ); // 128 and 1024
		mVisualizer.setCaptureSize(maxCaptureSize / 2); // 512
		int captureRate = 11025; // this lines up the AdjustView data with Visualizer data -- see also LineRenderer
		//int captureRate = Visualizer.getMaxCaptureRate()/2; // this lines up the ramp -- however see LineRenderer
		Log.d(TAG, "link: captureRate:" + captureRate);

		// Pass through Visualizer data to VisualizerView
		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
			// @Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
			}

			// @Override
			public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) { // why is samplingRate 44100000 ? maybe -- millihertz and bytes not shorts
				//Log.d(TAG, "link: onFftDataCapture isPlaying:" + Main.isPlaying + " bytes.length:" + bytes.length);
				if (Main.isPlaying == true && bytes.length > 0) {
					//Log.d(TAG, "onFftDataCapture: samplingRate:" + samplingRate );
					updateVisualizerFFT(bytes);
				}
			}

		};

		mVisualizer.setDataCaptureListener(captureListener, captureRate, false, true);
		// Enable Visualizer and disable when we're done with the stream
		mVisualizer.setEnabled(true);  // I never do disable this but I do release it -- see release below

		Log.d(TAG, "onDraw: I AM HERE playing:" + Main.isPlaying);


	}  // link

	public void addRenderer(Renderer renderer) {
		if(renderer != null) {
			mRenderers.add(renderer);
		}
	}

	public void clearRenderers() {
		Log.d(TAG, "clearRenderers" );
		mRenderers.clear();
	}

	/**
	 * Call to release the resources used by VisualizerView. Like with the
	 * MediaPlayer it is good practice to call this method
	 */
	public void release() {
		Log.d(TAG, "release");
		if (mVisualizer != null) {
			mVisualizer.release();
		}

	}

	/**
	 * Pass FFT data to the visualizer. Typically this will be obtained from the
	 * Android Visualizer.OnDataCaptureListener call back. See
	 * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
	 * @param bytes
	 */
	public void updateVisualizerFFT(byte[] bytes) {
		//Log.d(TAG, "link: updateVisualizerFFT isPlaying:" + Main.isPlaying + " cntr:" + Main.cntrFftCall);
		mFFTBytes = bytes;
		invalidate();
	}

	boolean mFlash = false;

	/**
	 * Call this to make the visualizer flash. Useful for flashing at the start
	 * of a song/loop etc...
	 */
	public void flash() {
		Log.d(TAG, "flash");
		//onDraw(mCanvas);
		mFlash = true;
		invalidate();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.d(TAG, "onDraw: Main.isPlaying=" + Main.isPlaying );
		if (Main.totalCntr == 0) {
			Log.d(TAG, "onDraw totalCntr:" + Main.totalCntr );
			return;
		}
		if(Main.isPlaying == true) {
			// Create canvas once we're ready to draw
			int h = getHeight();
			int w = getWidth();
			//Log.d(TAG, "onDraw screenHeight:" + h + " width:" + w + " buttonHeight:" + Main.buttonHeight );
			//h -= Main.buttonHeight;
			//Log.d(TAG, "onDraw new adjusted screenHeight:" + h + " width:" + w);
			// next 4 commented out lines from Adjust view
			//int stepSize = base / PlaySong.baseStep;
			//int cntr = Main.audioDataLength-base;  // the usable file length without overflows
			//scaleH = (float) screenHeight / PlaySong.records;
			// moved to PlaySong
			Main.lengthEachRecord = ((float) h / (float) (Main.totalCntr));  // *** HERE
			if (Main.stopAt < 50) {   // just use part of the screen for shorter than 5 seconds.
				Main.lengthEachRecord = (float) h / 50f;
				h = (int)((Main.lengthEachRecord * (float) (Main.totalCntr))); // *** HERE
				if (shortonce == 0) {
					Log.d(TAG, "Short song -- onDraw stopAd:" + (Main.stopAt) + " lengthEachRecord:" + Main.lengthEachRecord + " h:" + h);
					shortonce = 1;
				}
			}

			if (once == 0) {
				Log.d(TAG, "onDraw stopAt:" + Main.stopAt + " lengthEachRecord:" + Main.lengthEachRecord + " h:" + h);
				once = 1;
			}
			if(mPaint == null) {
				mPaint = new Paint();
			}
			if(mCanvasBitmap == null) {
				mRect.set(0, 0, w, h);
				mCanvasBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
				bitmapWidth = w;
				bitmapHeight = h;
				Main.bitmapWidth = bitmapWidth; // is the visualizer the same as adjust view ????
				Main.bitmapHeight = bitmapHeight;
				Log.d(TAG, "onDraw: width:" + bitmapWidth + " height:" + bitmapHeight + " buttonHeight:" + Main.buttonHeight );
			}
			if(mCanvas == null) { // one time
				mCanvas = new Canvas(mCanvasBitmap);
				w--; // subtract one so the lines will be inside the bitmap
				h--;
				scaleW = ((float) w) / (base/2);  // i.e. 512
				Log.d(TAG, "canvas height:" + h + " width:" + w);
				mPaint.setColor(Color.WHITE);
				mCanvas.drawLine(0, 0, w, 0, mPaint);
				mCanvas.drawLine(w, 0, w, h, mPaint);
				mCanvas.drawLine(w, h, 0, h, mPaint);
				mCanvas.drawLine(0, h, 0, 0, mPaint);
				//mPaint.setColor(Color.GREEN);
				//float scaleText = scaleW * 5;
				float scaleText = Main.buttonHeight / 4;
				mPaint.setTextSize(scaleText);
				mPaint.setColor(Color.WHITE);
				mPaint.setTextAlign(Paint.Align.CENTER);
				Log.d(TAG, "onDraw: width:" + bitmapWidth + " height:" + bitmapHeight + " scale:" + scaleW + " scaleText:" + scaleText);
				for (int i=0; i<12; i++) {
					String si = "" + i;
					float freq = (float)(i*1000) / (float)(11025.f / (base/2)) * scaleW ;
					mCanvas.drawText(si, freq, scaleText, mPaint);
				}
				int d = Main.duration;   // 6224 ms
				float totLen = (Main.stopAt) * Main.lengthEachRecord;  // 64 records * width of 6 = 384
				float oneSec = totLen / (float)((float) d / 1000f);  //  384 / 6.224 = oneSecond every 61.7 pixels
				Log.d(TAG, "time duration:" + d + " stopAt:" + (Main.stopAt) + " totLen:" + totLen + " oneSec:" + oneSec );
				int di = d / 1000;  // 6 sec
				int incr = (int) ((scaleText*3)/oneSec);
				if (incr < 1) {
					incr = 1;
				}
				mPaint.setTextAlign(Paint.Align.LEFT);
				for (int i = 1; i <= di; i+=incr) {
					String si = "" + i;
					float tim = (float) oneSec * i;
					mCanvas.drawText(si, scaleText/2, tim, mPaint);
				}
				mPaint.setColor(getResources().getColor(R.color.linen));
				mPaint.setTextAlign(Paint.Align.RIGHT);
				mCanvas.drawText(Main.existingName, w - scaleText, 3.0f * scaleText, mPaint);
				Log.d(TAG, "existingName:" + Main.existingName);
			}

			if (mFFTBytes != null) {
				FFTData fftData = new FFTData(mFFTBytes);
				for(Renderer r : mRenderers) {
					r.render(mCanvas, fftData, mRect);
					//Log.d(TAG, "inside loop Renderer r:" + r.offset);
				}
				//Log.d(TAG, "after onDraw: cntrFftCall:" + Main.cntrFftCall  );
			} else {
				Log.d(TAG, "mFFTBytes null cntr:" + Main.cntrFftCall);
			}



			//canvas.drawBitmap(mCanvasBitmap, matrix, null);
			//canvas.save();
			//if(mFlash) {
			//    mFlash = false;
			//    mCanvas.drawPaint(mPaint);
			//}

			//Log.d(TAG, "isPlaying:" + Main.isPlaying + " cntr:" + Main.cntrFftCall );

		} else {  // is playing is false
			Log.d(TAG, "onDraw adjustViewOption:" + Main.adjustViewOption );
			if (Main.adjustViewOption == null) {
				return;
			}
			if (mPaint == null || mCanvas == null || mCanvasBitmap == null) {
				Log.d(TAG, "check nulls: paint:" + mPaint + " canvas:" + mCanvas + " bitmap:" + mCanvasBitmap );
				return;
			}
			if (Main.adjustViewOption == "showDetailAndDefinitionData") {
				Log.d(TAG, "onDraw: 'showDetailAndDefinitionData'" );
				detailData(canvas);
				definitionData (canvas);
			}
			if (Main.adjustViewOption == "showDefinitionData") { // draw the data minJ maxJ if Define is pushed
				Log.d(TAG, "onDraw: 'showDefinitionData'" );
				definitionData(canvas);
			}

			//release();  // added 1/8/14
		} // is playing test
		//java.lang.NullPointerException: Attempt to invoke virtual method 'boolean android.graphics.Bitmap.isRecycled()' on a null object reference
		if (mCanvasBitmap == null) {
			return;
		}
		canvas.drawBitmap(mCanvasBitmap, matrix, null);

	} // on draw

	void detailData(Canvas canvas) {
		Log.d(TAG, "onDraw: showDetailData");
		Log.d(TAG, "showDetailData totalCntr:" + Main.totalCntr + " records:" + records + " aveEnergy:" + aveEnergy);
		canvas.restore();
		mPaint.setStrokeWidth(3);
//		if (canvas == null || mPaint == null) {
//			String msg = "Missing canvas or paint -- out of memory? -- try exit and restart. Sorry!!";
//			Context ctx = getContext();
//			Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
//			return;
//		}
		float y = 0f;
		FFTjava fft;
		FftBas fftbas;
		fft = new FFTjava();
		fftbas = new FftBas();
		int base = PlaySong.base; // 1024
		int stepSize = base/2; // 512
		int incSize = stepSize/2; // 128

		float prevY = 0;
		Main.audioDataLength -= Main.audioDataLength % base;
		int cntRec = (Main.audioDataLength-base);  // the usable file length without overflows
		scaleW = ((float) bitmapWidth) / stepSize; // (PlaySong.base/2);  // i.e. 512
		scaleH = (float) bitmapHeight / (cntRec/incSize); // length of screen (vs width)
		Log.d(TAG, "VisualizerView() detailData audioDataLength:" + Main.audioDataLength + " lengthEachRecord:" + Main.lengthEachRecord);
		Log.d(TAG, "VisualizerView() cntRec:" + cntRec + " screenHeight:" + bitmapHeight + " scaleH:" + scaleH + " base:" + base);
		Log.d(TAG, "VisualizerView() mCanvas:" + mCanvas + " mPaint:" + mPaint + " scaleW:" + scaleW);

		int low = Main.lowFreqCutoff;
		int hi = Main.highFreqCutoff;
		if (hi == 0) {
			hi = 512;
		}
		float myMaxEnergy = 0;

		for (int i=0; i<cntRec; i+=incSize) {
			for (int j=0; j<base; j++) { // load buffer 0 to 1024
				bufIn[j] = audioNorm[i+j];
			}
			fft.windowFunc(3, base, bufIn); // hanning -- sharper than hamming
			fftbas.fourierTransform(base, bufIn, imagIn, bufRealOut, bufImagOut, false);
			for (int j=low; j<hi; j++) { // 0 to 255
				float temp = (float) Math.sqrt(bufRealOut[j] * bufRealOut[j] + bufImagOut[j] * bufImagOut[j]);
				float jFreq = (j * scaleW); // make it fit the screen
				if (jFreq > scaleW && jFreq < (bitmapWidth - scaleW) && y > scaleW  && y < (bitmapHeight-scaleW)) {  // leave the border, name and numbers
					if (temp > filterAve && temp <= filterAve*6f ) {
						mPaint.setColor(Color.RED);
						VisualizerView.mCanvas.drawPoint(jFreq, y, mPaint); // null pointer crash
					}
					if (temp > filterAve*6f) {
						mPaint.setColor(Color.WHITE);
						VisualizerView.mCanvas.drawPoint(jFreq, y, mPaint);
					}
				}
			}
			prevY = y;
			y += scaleH;
		} // end of file
		//canvas.save();
		Log.d(TAG, "onDraw: completed showDetailData myMaxEnergy:" + myMaxEnergy);
		isDrawOnce = true;

	}


	void definitionData(Canvas canvas) {
		Log.d(TAG, "showDefinitionData");
		//canvas.restore();
		mPaint.setStrokeWidth(3);
		scaleH = (float) bitmapHeight / (float) (records); // pixels per record        // *** HERE
		scaleW = ((float) bitmapWidth) / (base/2);  // i.e. 512 -- so screen width represents 11025 hz
		screenCenterW = bitmapWidth/2.0f;
		screenCenterH = bitmapHeight/2.0f;
		Log.d(TAG, "scaleH:" + scaleH + " scaleW:" + scaleW + " screenCenterH:" + screenCenterH + " screenCenterW:" + screenCenterW);

		float y = scaleH;  // first silence is missing record 0 so start with 1 -- is it ??
		Log.d(TAG, "showDefinitionData totalCntr:" + Main.totalCntr + " lengthEachRecord:" + Main.lengthEachRecord);
		Log.d(TAG, "definitionData() playSong.Records:" + records + " screenHeight:" + bitmapHeight + " scaleH:" + scaleH);
		if (Main.isIdentify == true) {
			qry = "SELECT Phrase, Silence, Records" +
					" FROM DefineTotals" +
					" WHERE Ref = 0" +
					" AND Inx = 0" +
					" AND Seg = 0" +
					" ORDER BY Phrase ";
		} else {
			qry = "SELECT Phrase, Silence, Records" +
					" FROM DefineTotals" +
					" WHERE Ref = " + Main.existingRef +
					" AND Inx = " + Main.existingInx +
					" AND Seg = " + Main.existingSeg +
					" ORDER BY Phrase ";
		}
		Log.d(TAG, "definitionData: database qry:" + qry);
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.moveToFirst();
		int cntr = rs.getCount();
		Log.d(TAG, "definitionData: database cntr:" + cntr);
		for (int i = 0; i< cntr; i++) {
			int phrase = rs.getInt(0);
			int silence = rs.getInt(1);
			int records = rs.getInt(2);
			y += (float) (silence) * scaleH; // now you see it - now you don't (the +1)
			// -- it is finally gone -- found the missing silence in findVoiced moving short phrases to silence (plus 1)
			if (Main.isIdentify == true) {
				qry = "SELECT Freq, Voiced, Energy, Distance, Quality, Samp" +
						" FROM DefineDetail" +
						" WHERE Ref = 0" +
						" AND Inx = 0" +
						" AND Seg = 0" +
						" AND Phrase =" + phrase +
						" ORDER BY Record ";
			} else {
				qry = "SELECT Freq, Voiced, Energy, Distance, Quality, Samp" +
						" FROM DefineDetail" +
						" WHERE Ref = " + Main.existingRef +
						" AND Inx = " + Main.existingInx +
						" AND Seg = " + Main.existingSeg +
						" AND Phrase =" + phrase +
						" ORDER BY Record ";
			}
			//Log.d(TAG, "onDraw DetailData: qry:" + qry);
			rs1 = Main.songdata.getReadableDatabase().rawQuery(qry, null);
			//Log.d(TAG, "onDraw DetailData: rs1.count:" + rs1.getCount());
			rs1.moveToFirst();
			float prevX0 = (rs1.getInt(0) * scaleW); //freq
			//float prevX1 = (rs1.getInt(1) * scaleW); // voiced
			//float prevX2 = (rs1.getInt(2)/2 * scaleW + screenCenterW); // energy
			float prevX2 = (rs1.getInt(2) * scaleW/2f + screenCenterW); // energy
			float prevX3 = (rs1.getInt(3) * scaleW/4f); // distance
			float prevX4 = (bitmapWidth - rs1.getInt(4) * scaleW); // quality
			//float prevX5 = (rs1.getInt(5) * scaleW); // harmonic
			float prevY = y;
			float x0 = prevX0;
			float x2 = prevX2;
			float x3 = prevX3;
			float x4 = prevX4;
			y += scaleH;
			for (int j = 1; j<records; j++) {
				if(Main.isViewFrequency == true) {
					mPaint.setColor(getResources().getColor(R.color.linen));
					x0 = (rs1.getInt(0) * scaleW); // make it fit the screen
					//NullPointerException: Attempt to invoke virtual method 'void android.graphics.Canvas.drawLine(float, float, float, float, android.graphics.Paint)' on a null object reference
					mCanvas.drawLine(prevX0, prevY, x0, y, mPaint);
					//Log.d(TAG, "mPaint:" + mPaint + " x0:" + x0 + " y:" + y + " prevX0:" + prevX0 + " prevY:" + prevY);

				}
				//mPaint.setColor(Color.YELLOW); // voiced
				//float x1 = (rs1.getInt(1) * scaleW); // make it fit the screen
				//mCanvas.drawLine(prevX1, prevY, x1, y, mPaint);
				if(Main.isViewEnergy == true) {
					mPaint.setColor(Color.BLUE); // energy
					//x2 = (rs1.getInt(2) / 2 * scaleW + screenCenterW); // make it fit the screen -- center zero
					x2 = (rs1.getInt(2) * scaleW/2f + screenCenterW); // make it fit the screen -- center zero
					mCanvas.drawLine(prevX2, prevY, x2, y, mPaint);
					//Log.d(TAG, "mPaint:" + mPaint + " x2:" + x2 + " y:" + y + " prevX2:" + prevX2 + " prevY:" + prevY);
				}
				if(Main.isViewDistance == true) {
					mPaint.setColor(getResources().getColor(R.color.teal));
					x3 = (rs1.getInt(3) * scaleW/4f); // make it fit the screen
					mCanvas.drawLine(prevX3, prevY, x3, y, mPaint);
				}
				if(Main.isViewQuality == true) {
					mPaint.setColor(getResources().getColor(R.color.sienna));
					x4 = (bitmapWidth - rs1.getInt(4) * scaleW); // make it fit the screen
					mCanvas.drawLine(prevX4, prevY, x4, y, mPaint);
				}
				//mPaint.setColor(Color.MAGENTA); // samplesToMax
				//float x5 = (rs1.getInt(5) * scaleW*4); // make it fit the screen
				//mCanvas.drawLine(prevX5, prevY, x5, y, mPaint);
				prevX0 = x0;
				//prevX1 = x1;
				prevX2 = x2;
				prevX3 = x3;
				prevX4 = x4;
				//prevX5 = x5;
				prevY = y;
				y += scaleH;
				rs1.moveToNext();
			}
			rs1.close();
			rs.moveToNext();
		}
		//canvas.drawBitmap(mCanvasBitmap, matrix, null);
		//canvas.save();
		rs.close();
		Log.d(TAG, "definitionData: completed showDefinitionData" );
		isDrawOnce = true;
	}



	public void setImageMatrix(Matrix matrix) {
		// TODO Auto-generated method stub
	}

}

