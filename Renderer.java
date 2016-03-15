package com.modelsw.birdingviamic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

abstract public class Renderer
{
  // Have these as members, so we don't have to re-create them each time
  protected float[] mFFTPoints;
  int offset = 0;
  
  public Renderer() {
	  // constructor is empty
  }

  /**
* Implement this method to render the FFT audio data onto the canvas
* @param canvas - Canvas to draw on
* @param data - Data to render
* @param rect - Rect to render into
*/
  abstract public void onRender(Canvas canvas, FFTData data, Rect rect);


  /**
* Render the FFT data onto the canvas
* @param canvas - Canvas to draw on
* @param data - Data to render
* @param rect - Rect to render into
*/
  final public void render(Canvas canvas, FFTData data, Rect rect)
  {
    if (mFFTPoints == null || mFFTPoints.length < data.bytes.length * 4) {
      mFFTPoints = new float[data.bytes.length * 4];
    }
    
	Paint mPaint;
	mPaint = new Paint();
    mPaint.setColor(Color.WHITE);
    onRender(canvas, data, rect);
  }
}
