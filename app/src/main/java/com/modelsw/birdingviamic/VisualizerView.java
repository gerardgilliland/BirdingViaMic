package com.modelsw.birdingviamic;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

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
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
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
	float adjAve = 0.8f;
	private float aveCntr = 0;
	public static int bitmapWidth;
	public static int bitmapHeight;
	int cnt = 0;
	Context ctx;
	//float drawLimit = 0;
	private float filterAve = 0;
	private float highlight = 2.8f;
	static Matrix matrix = new Matrix();
	boolean isDrawOnce = false;
	boolean isInit = false;
	private float lowLimit = 0;
	public byte[] mBytes;
	private float maxPower = 0;
	public static Bitmap mCanvasBitmap;
	public static Canvas mCanvas;
	//private byte[] mFFTBytes;
	private static float minPwrLimit = 0.02f;
	private static Rect mRect = new Rect();
	public static Visualizer mVisualizer;
	public static Paint mPaint = new Paint();
	float mxpre = 1;
	public static int once = 0;
	float paintWidth = 1;
	String qry = "";
	Cursor rs;
	Cursor rs1;
	float scaleH;
	float scaleW = 0.5f;
	float screenCenterW;
	float screenCenterH;
	int vvtop = 168;
	int vvHeight = 1515;

	public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		ctx = context;
		init();
		//VisualizerView view = (VisualizerView) findViewById(R.id.visualizerView);
	}

	public VisualizerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VisualizerView(Context context) {
		this(context, null, 0);
	}

	private void init() {
		Log.d(TAG, "init");
		once = 0;
		filterAve = 0;
		aveCntr = 0;
		lowLimit = 0;
		mBytes = null;
		//mFFTBytes = null;
		if(mPaint == null) {
			mPaint = new Paint();
		}
		mPaint.setColor(Color.BLACK);
		vvtop = PlaySong.vvtop;
		vvHeight = PlaySong.vvHeight;
		isInit = PlaySong.isInit;

	}


	/**
	 * Call to release the resources used by VisualizerView. Like with the
	 * MediaPlayer it is good practice to call this method
	 */
	public void release() {
		Log.d(TAG, "release");
		Log.d(TAG,"fa:" + (int)filterAve + " a/l:" + filterAve/lowLimit + " x/a:" + maxPower/filterAve + " ct:" + (int)aveCntr); // + " pc:" + prevCnt + " h:" + isPrevHigh );
		if (mVisualizer != null) {
			mVisualizer.release();
		}

	}

	/**
	 * Call this to make the visualizer flash. Useful for flashing at the start
	 * of a song/loop etc...
	 */
	public void flash() {
		//Log.d(TAG, "flash");
		invalidate();
	}

	// called from PlaySong
	public void setInitialMax(){
		filterAve = 0;
		aveCntr = 1;
		lowLimit = 0;
		maxPower = 1;
		mxpre = 1;
		cnt = 0;
		int stepSize = base/2; // 512
		float pwr = 0;
		for (int j = 2; j < stepSize; j++) {
			float rfk = PlaySong.bufRealOut[j];
			float ifk = PlaySong.bufImagOut[j];
			float magnitude = rfk * rfk + ifk * ifk;
			pwr = (float) (Math.sqrt(magnitude));
			//Log.d(TAG, "j:" + j + " pwr:" + pwr + " lowLimit:" + lowLimit + " maxPower:" + maxPower);
			if (pwr > lowLimit) {
				filterAve = (float)(((double)filterAve * aveCntr + pwr) / (aveCntr+1));
				aveCntr++;
				//drawLimit = filterAve * adjAve;
				if (maxPower < pwr) {
					float prevMaxPower = maxPower;
					float prevLowLimit = lowLimit;
					maxPower = pwr;
					lowLimit = maxPower * minPwrLimit;
					float mxpre = (maxPower-lowLimit)/(prevMaxPower-prevLowLimit);
					filterAve *= mxpre;
					if (filterAve > maxPower) {
						filterAve = maxPower;
					}
					aveCntr /= mxpre;
					if (aveCntr < 1) {
						aveCntr = 1;
					}
				}
			}
		} // next j
		Log.d(TAG, "setInitialMax lowLimit:" + lowLimit + " maxPower:" + maxPower);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.d(TAG, "onDraw: Main.isPlaying=" + Main.isPlaying );
		if (Main.duration == 0) {
			Log.d(TAG, "onDraw duration:" + Main.duration );
			return;
		}
		if(Main.isPlaying == true) {
			// Create canvas once we're ready to draw
			int h = canvas.getHeight();
			int w = canvas.getWidth();

			if (once == 0) {
				//Log.d(TAG, "onDraw  h:" + h + " w:" + w);
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
				PlaySong.scalePxPerMs = ((float) Main.bitmapHeight / (float) Main.duration);
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
				float oneSec = PlaySong.scalePxPerMs * 1000f;  //  384 / 6.224 = oneSecond every 61.7 pixels
				Log.d(TAG, "time duration:" + d + " oneSec:" + oneSec );
				int di = d / 1000;  // 6 sec
				int incr = (int) ((scaleText*3)/oneSec);
				if (incr < 1) {
					incr = 1;
				}
				mPaint.setTextAlign(Paint.Align.LEFT);
				float biasTime = scaleText/2;
				for (int i = 0; i <= di; i+=incr) {
					String si = "" + i;
					float tim = (float) oneSec * i;
					mCanvas.drawText(si, biasTime, tim+biasTime, mPaint);
				}
				mPaint.setColor(ContextCompat.getColor(ctx, R.color.linen));
				mPaint.setTextAlign(Paint.Align.RIGHT);
				mCanvas.drawText(Main.existingName, w - scaleText, 3.0f * scaleText, mPaint);
				Log.d(TAG, "onDraw existingName:" + Main.existingName);
				Log.d(TAG, "lowLimit:" + lowLimit + " maxPower:" + maxPower);
				paintWidth = scalePxPerMs * 32;
				if(paintWidth < 2) {
					paintWidth = 2;
				}
				if(paintWidth > 8) {
					paintWidth = 8;
				}
				mPaint.setStrokeWidth(paintWidth);
				mPaint.setColor(Color.GRAY);
			}
			if (Main.shortCntr == 0) {
				return;
			}
			// realtime visualizer
			int stepSize = base/2; // 512
			scaleW = ((float) bitmapWidth) / stepSize;
			Float time = PlaySong.lenMs * PlaySong.scalePxPerMs;
			float pwr = 0;
			float jFreq = 0;
			for (int j = 2; j < stepSize; j++) {
				float rfk = PlaySong.bufRealOut[j];
				float ifk = PlaySong.bufImagOut[j];
				float magnitude = rfk * rfk + ifk * ifk;
				pwr = (float) (Math.sqrt(magnitude));
				if (pwr > lowLimit) {
					filterAve = (float)(((double)filterAve * aveCntr + pwr) / (aveCntr+1));
					aveCntr++;
					//drawLimit = filterAve * adjAve;
					if (maxPower < pwr) {
						float prevMaxPower = maxPower;
						float prevLowLimit = lowLimit;
						maxPower = pwr;
						lowLimit = maxPower * minPwrLimit;
						float mxpre = (maxPower-lowLimit)/(prevMaxPower-prevLowLimit);
						filterAve *= mxpre;
						if (filterAve > maxPower) {
							filterAve = maxPower;
						}
						aveCntr /= mxpre;
						if (aveCntr < 1) {
							aveCntr = 1;
						}
						//Log.d(TAG, "cnt:" + cnt + " fa:" + (int) filterAve + " x/pre:" + mxpre + " ct:" + (int) aveCntr);
						//drawLimit = filterAve-1;
					}
				}
				if (pwr > filterAve) {
					jFreq = j * scaleW;
					mCanvas.drawPoint(jFreq, time, mPaint);
				}
			} // next j
			//Log.d(TAG,"cnt:" + cnt + " fa:" + (int)filterAve + " a/l:" + (int)(filterAve/lowLimit) + " x/a:" + (int)(maxPower/filterAve) + " ct:" + (int)aveCntr); // + " pc:" + prevCnt + " h:" + isPrevHigh );
			cnt++;


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
		Log.d(TAG, "showDetailData records:" + records + " aveEnergy:" + aveEnergy);
		mPaint.setStrokeWidth(3);
		float y = 0f;
		FFTjava fft;
		FftBas fftbas;
		fft = new FFTjava();
		fftbas = new FftBas();
		int base = PlaySong.base; // 1024
		int stepSize = base/2; // 512
		int incSize = stepSize/2; // 256
		//maxEnergy = 0;
		filterAve = 2.25f;
		aveCntr = 0;
		lowLimit = 0;
		float prevY = 0;
		Main.audioDataLength -= Main.audioDataLength % base;
		int cntRec = (Main.audioDataLength-base);  // the usable file length without overflows
		scaleW = ((float) bitmapWidth) / stepSize; // (PlaySong.base/2);  // i.e. 512
		scaleH = (float) bitmapHeight / (cntRec/incSize); // length of screen (vs width)
		Log.d(TAG, "VisualizerView() detailData audioDataLength:" + Main.audioDataLength);
		Log.d(TAG, "VisualizerView() cntRec:" + cntRec + " screenHeight:" + bitmapHeight + " scaleH:" + scaleH + " base:" + base);
		Log.d(TAG, "VisualizerView() mCanvas:" + mCanvas + " mPaint:" + mPaint + " scaleW:" + scaleW);

		int low = Main.lowFreqCutoff;
		int hi = Main.highFreqCutoff;
		if (hi == 0) {
			hi = 512;
		}
		//float myMaxEnergy = 0;
		Log.d(TAG, "VisualizerView() low:" + low + " hi:" + hi);

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
					if (temp > filterAve) {
						mPaint.setColor(Color.RED);
						VisualizerView.mCanvas.drawPoint(jFreq, y, mPaint); // null pointer crash
					}
					if (temp > filterAve * highlight) {
						mPaint.setColor(Color.LTGRAY);
						VisualizerView.mCanvas.drawPoint(jFreq, y, mPaint);
					}
				}
			}
			//Log.d(TAG, "detail ave:" + filterAve + " low:" + lowLimit + " cntr:" + aveCntr);
			prevY = y;
			y += scaleH;
		} // end of file
		//canvas.save();
		isDrawOnce = true;

	}


	void definitionData(Canvas canvas) {
		Log.d(TAG, "showDefinitionData");
		//canvas.restore();
		if (Main.songpath == null || Main.songdata == null) {
			return;
		}
		mPaint.setStrokeWidth(4);
		scaleH = (float) bitmapHeight / (float) (records); // pixels per record        // *** HERE
		scaleW = ((float) bitmapWidth) / (base/2);  // i.e. 512 -- so screen width represents 11025 hz
		screenCenterW = bitmapWidth/2.0f;
		screenCenterH = bitmapHeight/2.0f;
		Log.d(TAG, "scaleH:" + scaleH + " scaleW:" + scaleW + " screenCenterH:" + screenCenterH + " screenCenterW:" + screenCenterW);

		float y = scaleH;  // first silence is missing record 0 so start with 1 -- is it ??
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
					//mPaint.setColor(getResources().getColor(R.color.linen));
					mPaint.setColor(Color.WHITE); // energy
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
					mPaint.setColor(ContextCompat.getColor(ctx, R.color.teal));
					x3 = (rs1.getInt(3) * scaleW/4f); // make it fit the screen
					mCanvas.drawLine(prevX3, prevY, x3, y, mPaint);
				}
				if(Main.isViewQuality == true) {
					mPaint.setColor(ContextCompat.getColor(ctx, R.color.sienna));
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


}

