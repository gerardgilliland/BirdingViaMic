package com.modelsw.birdingviamic;

import java.io.FileOutputStream;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.os.Build;



public class LineRenderer extends Renderer {
	private static final String TAG = "LineRenderer";
	private Paint mPaint;
	// was scale -- it is the same as AdjustView incY in concept but is log10
	private float paintWidth;
	float scaleW;
	float filterAve = 2.25f;
	int cntr = 2048;
	String manuf;
	private static float time;
	public LineRenderer(Paint paint) {
		super();
		manuf = Build.MANUFACTURER;
		mPaint = paint;
		//paintWidth = (float) Math.log10(Main.lengthEachRecord*2);  // width of the paint (used to scale power) -- maybe an option
		//paintWidth = (float) Main.bitmapHeight / Main.stopAt;
		paintWidth = Main.lengthEachRecord/2;
		//paintWidth = 1;
		//paintWidth = (float) Main.lengthEachRecord/2;  // width of the paint (used to scale power) -- maybe an option
		//if(Main.stopAt < 100) {
		//	paintWidth *= (float) Main.stopAt / 100f;
		//}
		if(paintWidth < 1) {
			paintWidth = 1;
		}
		if(paintWidth > 8) {
			paintWidth = 8;
		}

		time = 0;
		mPaint.setStrokeWidth(paintWidth);
		Log.d(TAG, "LineRenderer: paintWidth:" + paintWidth);
		mPaint.setColor(Color.GRAY);
	}

	/*
    http://developer.samsung.com/forum/board/thread/view.do?boardName=GeneralB&messageId=238327&frm=7&tagValue=S4&curPage=1
    https://github.com/felixpalmer/android-visualizer/issues/5#issuecomment-25900391
    http://developer.samsung.com/forum/board/thread/view.do?boardName=GeneralB&messageId=238465
    VisualizerView data return from the onFftDataCapture is always zero on Samsung S5 and lollipop.
    Works on short (less than 40 second) files.
    http://developer.samsung.com/forum/thread/visualizer-view-fails-on-samsung-s5/201/284176?boardName=SDK&startId=zzzzz~
    https://community.verizonwireless.com/thread/899976?q=Visualizer%20View
    also when using as shortend song via save start and end the song again fails to call this and the song doesn't stop when it should
    */
	@Override
	public void onRender(Canvas canvas, FFTData data, Rect rect) {
		// if i ask for max capture rate it returns 10000, I want 11025 so I scale by screenWidth / 1024/2 * 0.907
		if (manuf.equals("samsung")) {
			if (DecodeFileJava.skipAdj == 0) {  // skipAdj is non zero when sample rate is 12000
				scaleW = (float) ((float) rect.width() / (float)(data.bytes.length/2*10000/11025)); // samsung
			} else {
				scaleW = (float) ((float) rect.width() / (float)(data.bytes.length/2*10000/12000)); // samsung
			}
		} else {
			scaleW = (float) ((float) rect.width() / (float) (data.bytes.length / 2));  // motorola
		}

		//scaleW = (float) ((float) rect.width() / (float) (data.bytes.length / 2));  // test

		float maxPwr = 0;
		float pwr = 0;
		float jFreq = 0;
		for (int j = 2; j < data.bytes.length/2 - 1; j+=2) {  // data: min0;max1;r2,i3,r4,i5 ... r510, i511
			float rfk = (float) data.bytes[j];
			float ifk = (float) data.bytes[j + 1];
			float magnitude = rfk * rfk + ifk * ifk;
			pwr = (float) (Math.sqrt(magnitude));  // was log10
			jFreq = j * scaleW;
			time = Main.lengthEachRecord * Main.cntrFftCall;
			if (pwr > filterAve) {
				if (filterAve < 3.0) {
					filterAve -= filterAve / cntr;
					filterAve += pwr / cntr;
					cntr--;
				}
				canvas.drawPoint(jFreq, time, mPaint);
				if (pwr > filterAve*6) {
					canvas.drawPoint(jFreq, time + paintWidth, mPaint);
					canvas.drawPoint(jFreq, time - paintWidth, mPaint);
					canvas.drawPoint(jFreq + paintWidth, time, mPaint);
					canvas.drawPoint(jFreq - paintWidth, time, mPaint);
				}
			} else {
				if (filterAve > 1.5) {
					filterAve -= filterAve / cntr;
					filterAve += pwr / cntr;
					cntr++;
				}
			}

		} // next i

		Main.cntrFftCall++;
		//Log.d(TAG, "LineRenderer: filterAve:" + filterAve + " cntr:" + cntr + " cntrFftCall:" + Main.cntrFftCall);
		if (Main.cntrFftCall > Main.stopAt)  {
			if (PlaySong.mPlayer != null) {  // may already be stopped.
				Log.d(TAG, "onRender requesting to stop the plot");
				PlaySong.stopPlaying();
			}
		}
	}
}