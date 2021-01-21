package com.modelsw.birdingviamic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;


public class AdjustView extends ViewGroup implements OnTouchListener {
    private static final String TAG = "AdjustView";
    private String qry = "";
    private Cursor rs;
    private Cursor rs1;
    int screenWidth;
    int screenHeight;
    int density;
    Paint mPaint;
    int sq = 128;
    float sq2;
    float screenCenterW;
    float screenCenterH;
    float finalStart;
    float finalStop;
    float finalLow;
    float finalHigh;
    float finalBegin;
    float finalEnd;
    float currentXlen;
    float currentYlen;
    static int low = 0;
    float lowX;
    float lowY;
    static int high = 1;
    float highX;
    float highY;
    static int begin = 2;
    float beginX;
    float beginY;
    static int end = 3;
    float endX;
    float endY;
    static int start = 4;
    float startX;
    float startY;
    static int stop = 5;
    float stopX;
    float stopY;
    float saveX;
    float saveY;
    float cancelX;
    float cancelY;
    float excludeX;
    float excludeY;
    boolean yStopHasChanged = false;
    float edge;
    float halfway;
    int activeLine; // low, high, start, stop
    String start_label;
    String stop_label;
    String begin_label;
    String end_label;
    String identify_label;
    String save_label;
    String exclude_label;
    String cancel_label;
    String low_label;
    String high_label;
    float prevYstart;
    float prevXstart;
    float prevYstop;
    float prevXstop;
    float prevX;
    float top;
    View headerArea;
    View vv;
    Canvas canvas;
    float bottom;
    static boolean isDrawOnce = false;
    static boolean isSaveOnce = false;
    static boolean isExcludeOnce = false;
    static boolean isCancelOnce = false;
    static boolean isInit = false;
    float y;
    float scaleH;
    int base = PlaySong.base; // 1024
    int stepSize = base/2; // 512
    int incSize = stepSize/4; // 128
    int bytesAvailable;
    String song;
    float scaleW = 0.5f;
    char q = 34;
    int vvtop = 168;
    int vvHeight = 1515;



    public AdjustView(Context context) {
        super(context);
        vvtop = PlaySong.vvtop;
        vvHeight = PlaySong.vvHeight;
        isInit = PlaySong.isInit;

        Log.d(TAG, "2) AdjustView(context) vvtop:" + vvtop + " isInit:" + isInit);
        //if (VisualizerView.mCanvasBitmap == null || VisualizerView.mCanvas == null || VisualizerView.mPaint == null) {
        if (VisualizerView.mCanvasBitmap == null || VisualizerView.mCanvas == null) {
            Log.d(TAG, "2a) AdjustView returning -- bitmap or canvas is missing.");
            //String msg = "Bitmap is missing";
            //Context ctx = getContext();
            //Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            return;
        }

        mPaint = new Paint();
        //mPaint.setTextSize(16);  // labels -- note 24 too big for motorola need
        //mPaint.setTextSize(12);  // sp?
        float scaleText = Main.buttonHeight / 4;
        mPaint.setTextSize(scaleText);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mPaint.setTextAlign(Paint.Align.CENTER);
        save_label = context.getString(R.string.save_lable);
        exclude_label = context.getString(R.string.exclude_lable);
        cancel_label = context.getString(R.string.cancel_label);
        low_label = context.getString(R.string.low_label);
        high_label = context.getString(R.string.high_label);
        begin_label = context.getString(R.string.begin_label);
        end_label = context.getString(R.string.end_label);
        start_label = context.getString(R.string.start_label);
        stop_label = context.getString(R.string.stop_label);
        //currentYstart = 0 ;
        // The bitmap length is Main.totalCntr * Main.lengthEachRecord
        // or more importantly the lengthEachRecord is bitmapLength / totalCntr
        screenWidth = Main.bitmapWidth; // canvas.getWidth();
        screenHeight = Main.bitmapHeight; //canvas.getHeight();
        scaleW = ((float) screenWidth) / (base/2);  // i.e. 512 -- so screen width represents 11025 hz
        scaleH = (float) (screenHeight / (float) Main.duration); // scaleH = pixels / ms
        sq = Main.buttonHeight;
        sq2 = (float) (sq/2);
        edge = sq2;
        screenCenterW = screenWidth/2.0f;
        screenCenterH = screenHeight/2.0f;
        saveX = screenWidth-edge;
        saveY = screenCenterH;
        excludeX = screenWidth-edge;
        excludeY = screenCenterH/2;
        cancelX = screenWidth-edge;
        cancelY = screenCenterH+screenCenterH/2;
        song = Main.songs[Main.selectedSong[Main.thisSong]];
        if (Main.songdata == null || Main.songpath == null) { // knocked out of memory re-init the database
            return;
        }
        Main.db = Main.songdata.getWritableDatabase();
        Log.d(TAG, "3) AdjustView isInit:" + isInit );
        if (isInit == true) {
            //Log.d(TAG, "existing from PlaySong" );
            if (Main.lowFreqCutoff == 0) {
                finalLow = 0;
            } else {
                finalLow = Main.lowFreqCutoff * scaleW + 0.5f;
            }
            if (Main.highFreqCutoff == 0) {
                finalHigh = screenWidth;
            } else {
                finalHigh = Main.highFreqCutoff * scaleW + 0.5f;
            }
            if (Main.filterStartAtLoc == 0) {
                finalBegin = 0;
            } else {
                finalBegin = (float) Main.filterStartAtLoc * scaleH + 0.5f;
            }
            if (Main.filterStopAtLoc == 0) {
                finalEnd = screenHeight;
            } else {
                finalEnd = (float) Main.filterStopAtLoc * scaleH + 0.5f;
            }
            finalStart = 0; // these are not seen here on the existing song -- already cut to these limits and new file start is zero
            finalStop = screenHeight; // final stop will be set to 0 in database
            // initial handle positions -- after this they float
            lowY = edge; // top of the screen
            highY = screenHeight - edge;  // bottom of the screen
            beginX = screenWidth - edge; // right side of the screen
            if (finalBegin > 0) { // move to left of squares
                beginX = excludeX - edge*2; // right side of the screen
            }
            endX = edge; // left side of the screen
            startX = screenWidth/2; // right side of the screen
            stopX = screenWidth/2; // left side of the screen
            isSaveOnce = false;
            isExcludeOnce = false;
            isCancelOnce = false;
        }
        isInit = false;
        Log.d(TAG, "4) AdjustView isInit:" + isInit );
        //Log.d(TAG, "5) AdjustView: Main.totalCntr:" + Main.totalCntr);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        headerArea = new View(getContext());
        headerArea.layout(l, t, r, vvtop);
        headerArea.setFocusable(true);
        headerArea.requestLayout();
        Log.d(TAG, "6) onLayout headerArea:" + l + "," + t + "," + r + "," + vvtop);
        vv = new View(getContext());
        vv.layout(l, vvtop, r, (vvtop + vvHeight));
        vv.setFocusable(true);
        setWillNotDraw(false); // this allows invalidate to work
        vv.requestLayout();
        setOnTouchListener(this);
        Log.d(TAG, "7) onLayout vv:" + l + "," + vvtop + "," + r + "," + (vvtop + vvHeight));
        //if (VisualizerView.mCanvasBitmap == null || VisualizerView.mCanvas == null || VisualizerView.mPaint == null) {
        if (VisualizerView.mCanvasBitmap == null) {
            Log.d(TAG, "7a) returning -- bitmap is missing.");
            //String msg = "Bitmap is missing";
            //Context ctx = getContext();
            //Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "8) onLayout canvas vvtop:" + vvtop + " isInit:" + isInit);
        canvas = new Canvas(VisualizerView.mCanvasBitmap);
        //invalidate();
    }

    void adjustableLines(Canvas canvas) {
        Log.d(TAG, "adjustableLines isInit:" + isInit);
        // low freq Cutoff -- label at upper left -- line is on Y axis
        mPaint.setColor(Color.GREEN);
        if (finalLow < screenCenterW) { // put the label to the right of the line
            lowX = finalLow + edge;
            if (Math.abs(finalLow) > Math.abs(finalHigh - finalLow)) { // unless it is closer to high than the left side
                lowX = finalLow - edge;
            }
        } else { // put label to the left of the line
            lowX = finalLow - edge;
        }
        canvas.drawLine(finalLow, 0, finalLow, screenHeight, mPaint);
        canvas.drawText(low_label, lowX, lowY, mPaint);
        // high freq cutoff -- label at lower right -- line is on Y axis
        if (finalHigh > screenCenterW) { // put the label below the line
            highX = finalHigh - edge;
            if (Math.abs(screenWidth-finalHigh) > Math.abs(finalHigh - finalLow)) { // unless it is closer to low than the right side
                highX = finalHigh + edge;
            }
        } else { // put label to the right of the line
            highX = finalHigh + edge;
        }
        canvas.drawLine(finalHigh, 0, finalHigh, screenHeight, mPaint);
        canvas.drawText(high_label, highX, highY, mPaint);
        // filter begin -- label at upper right -- line is on X axis
        mPaint.setColor(Color.RED);
        if (finalBegin < screenCenterH) { // put the label below the line
            beginY = finalBegin + edge;
            if (Math.abs(finalBegin) > Math.abs(finalEnd - finalBegin)) { // unless it is closer to stop than the top
                beginY = finalBegin - edge;
            }
        } else { // put label above the line
            beginY = finalBegin - edge;
        }
        canvas.drawLine(0, finalBegin, screenWidth, finalBegin, mPaint);
        canvas.drawText(begin_label, beginX, beginY, mPaint);
        // filter end -- label at lower left -- line is on X axis
        if (finalEnd > screenCenterH) { // put the label above the line
            endY = finalEnd - edge;
            if (Math.abs(screenHeight - finalEnd) > Math.abs(finalEnd - finalBegin)) { // unless it is closer to start than the bottom
                endY = finalEnd + edge;
            }
        } else { // put label below the line
            endY = finalEnd + edge;
        }
        canvas.drawLine(0, finalEnd, screenWidth, finalEnd, mPaint);
        canvas.drawText(end_label, endX, endY, mPaint);
        // song start -- label at upper center -- line is on X axis
        mPaint.setColor(Color.WHITE);
        if (finalStart < screenCenterH) { // put the label below the line
            startY = finalStart + edge;
            if (Math.abs(finalStart) > Math.abs(finalStop - finalStart)) { // unless it is closer to stop than the top
                startY = finalStart - edge;
            }
        } else { // put label above the line
            startY = finalStart - edge;
        }
        canvas.drawLine(0, finalStart, screenWidth, finalStart, mPaint);
        canvas.drawText(start_label, startX, startY, mPaint);
        // filter stop -- label at lower center -- line is on X axis
        if (finalStop > screenCenterH) { // put the label above the line
            stopY = finalStop - edge;
            if (Math.abs(screenHeight - finalStop) > Math.abs(finalStop - finalStart)) { // unless it is closer to start than the bottom
                stopY = finalStop + edge;
            }
        } else { // put label below the line
            stopY = finalStop + edge;
        }
        canvas.drawLine(0, finalStop, screenWidth, finalStop, mPaint);
        canvas.drawText(stop_label, stopX, stopY, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw() just entered isInit:" + isInit);

        //		Log.d(TAG, "onDraw: drawOnce:" + drawOnce + " w:" + screenWidth + " h:" + screenHeight  + " sq:" + sq + " ratio:" + ratio);
        if (Main.adjustViewOption == "edit") {  // last pointer up waiting to save or exclude or cancel
            Log.d(TAG, "onDraw: 'edit'" );
            // the "save" square
            mPaint.setColor(Color.BLACK);
            canvas.drawRect(saveX-edge+1, saveY-edge+1, saveX+edge-1, saveY+edge-1, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawLine(saveX-edge, saveY-edge, saveX+edge, saveY-edge, mPaint);
            canvas.drawLine(saveX+edge, saveY-edge, saveX+edge, saveY+edge, mPaint);
            canvas.drawLine(saveX+edge, saveY+edge, saveX-edge, saveY+edge, mPaint);
            canvas.drawLine(saveX-edge, saveY+edge, saveX-edge, saveY-edge, mPaint);
            canvas.drawText(save_label, saveX, saveY, mPaint);
            // the "exclude" square
            mPaint.setColor(Color.BLACK);
            canvas.drawRect(excludeX-edge+1, excludeY-edge+1, excludeX+edge-1, excludeY+edge-1, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawLine(excludeX-edge, excludeY-edge, excludeX+edge, excludeY-edge, mPaint);
            canvas.drawLine(excludeX+edge, excludeY-edge, excludeX+edge, excludeY+edge, mPaint);
            canvas.drawLine(excludeX+edge, excludeY+edge, excludeX-edge, excludeY+edge, mPaint);
            canvas.drawLine(excludeX-edge, excludeY+edge, excludeX-edge, excludeY-edge, mPaint);
            canvas.drawText(exclude_label, excludeX, excludeY, mPaint);
            // the "cancel" square
            mPaint.setColor(Color.BLACK);
            canvas.drawRect(cancelX-edge+1, cancelY-edge+1, cancelX+edge-1, cancelY+edge-1, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawLine(cancelX-edge, cancelY-edge, cancelX+edge, cancelY-edge, mPaint);
            canvas.drawLine(cancelX+edge, cancelY-edge, cancelX+edge, cancelY+edge, mPaint);
            canvas.drawLine(cancelX+edge, cancelY+edge, cancelX-edge, cancelY+edge, mPaint);
            canvas.drawLine(cancelX-edge, cancelY+edge, cancelX-edge, cancelY-edge, mPaint);
            canvas.drawText(cancel_label, cancelX, cancelY, mPaint);
            // draw the adjustable lines
            adjustableLines(canvas); // draw the lines
        }


        if (Main.adjustViewOption == "save" || Main.adjustViewOption == "exclude") {
            Log.d(TAG, "onDraw: 'save' or 'exclude'" );
            adjustableLines(canvas);
        }

        if (Main.adjustViewOption == "cancel") {
            Log.d(TAG, "onDraw: 'cancel'" );
            canvas.restore();
            // I need to reset the lines first
        }

        if (Main.adjustViewOption == "move") {
            Log.d(TAG, "onDraw: 'move'");
            switch (activeLine) {
                case 0: // low
                    mPaint.setColor(Color.GREEN);
                    canvas.drawLine(finalLow, 0, finalLow, screenHeight, mPaint);
                    canvas.drawText(low_label, lowX, lowY, mPaint);
                    break;
                case 1: // high
                    mPaint.setColor(Color.GREEN);
                    canvas.drawLine(finalHigh, 0, finalHigh, screenHeight, mPaint);
                    canvas.drawText(high_label, highX, highY, mPaint);
                    break;
                case 2: // begin
                    mPaint.setColor(Color.RED);
                    canvas.drawLine(0, finalBegin, screenWidth, finalBegin, mPaint);
                    canvas.drawText(begin_label, beginX, beginY, mPaint);
                    break;
                case 3: // end
                    mPaint.setColor(Color.RED);
                    canvas.drawLine(0, finalEnd, screenWidth, finalEnd, mPaint);
                    canvas.drawText(end_label, endX, endY, mPaint);
                    break;
                case 4: // start
                    mPaint.setColor(Color.WHITE);
                    canvas.drawLine(0, finalStart, screenWidth, finalStart, mPaint);
                    canvas.drawText(start_label, startX, startY, mPaint);
                    break;
                case 5: // stop
                    mPaint.setColor(Color.WHITE);
                    canvas.drawLine(0, finalStop, screenWidth, finalStop, mPaint);
                    canvas.drawText(stop_label, stopX, stopY, mPaint);
                    break;
            }

            if (Main.adjustViewOption == "clear") {
                Log.d(TAG, "onDraw: 'clear'" );
                canvas.restore();
            }

            if (Main.adjustViewOption == "clearExclude") {
                Log.d(TAG, "onDraw: 'clearExclude'" );
                canvas.restore();

            }
        }

    } // onDraw


    public boolean onTouch (View v, MotionEvent event) {
        v.setTop(vvtop);
        Log.d(TAG, "************** onTouch: isInit:" + isInit);
        PlaySong.editButton.setBackgroundColor(getResources().getColor(R.color.linen));
        PlaySong.editButton.setTextColor(getResources().getColor(R.color.teal));
        final float x = event.getX();
        final float y = event.getY();
        //Log.d(TAG, "onTouch event:" + event + " x:" + x + " y:" + y);
        Log.d(TAG, "onTouch x:" + x + " y:" + y);
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                //Log.d(TAG, "ActionDown x:" + x);
                if (y > screenHeight) {
                    Log.d(TAG, "ActionDown 'clear'");
                    Main.adjustViewOption = "clear";
                    invalidate();
                    return false;
                }
                if (y < 0) {
                    //Log.d(TAG, "ActionDown y:" + y + " negative");
                    return false;
                }
                // low freq
                if (((x-edge) < lowX) && ((x+edge) > lowX) && ((y-edge) < lowY) && ((y+edge) > lowY)) {
                    if (finalLow < lowX) { // line to the left of the label
                        finalLow = x - edge;
                        if (finalLow < 0) {
                            finalLow = 0;
                        }
                        lowX = finalLow + edge;
                    } else { // line to the right of the label
                        finalLow = x + edge;
                        if (finalLow > screenWidth) {
                            finalLow = screenWidth;
                        }
                        lowX = finalLow - edge;
                    }
                    lowY = y; // let the label float
                    if (y < edge) {
                        lowY = edge;
                    }
                    if (y > (screenHeight-edge)) {
                        lowY = screenHeight - edge;
                    }
                    activeLine = 0;
                    Log.d(TAG, "ActionDown 'move low'");
                    Main.adjustViewOption = "move";
                    invalidate();
                    return true;
                } // low

                // high freq
                if (((x-edge) < highX) && ((x+edge) > highX) && ((y-edge) < highY) && ((y+edge) > highY)) {
                    if (finalHigh < highX) { // line to the left of the label
                        finalHigh = x - edge;
                        if (finalHigh < 0) {
                            finalHigh = 0;
                        }
                        highX = finalHigh + edge;
                    } else { // line to the right of the label
                        finalHigh = x + edge;
                        if (finalHigh > screenWidth) {
                            finalHigh = screenWidth;
                        }
                        highX = finalHigh - edge;
                    }
                    highY = y; // let the label float
                    if (y < edge) {
                        highY = edge;
                    }
                    if (y > (screenHeight-edge)) {
                        highY = screenHeight - edge;
                    }
                    activeLine = 1;
                    Log.d(TAG, "ActionDown 'move high'");
                    Main.adjustViewOption = "move";
                    invalidate();
                    return true;
                } // high

                // begin
                if (((x-edge) < beginX) && ((x+edge) > beginX) && ((y-edge) < beginY) && ((y+edge) > beginY)) {
                    if (finalBegin < beginY) { // line above the label
                        finalBegin = y - edge;
                        if (finalBegin < 0) {
                            finalBegin = 0;
                        }
                        beginY = finalBegin + edge;
                    } else { // line below the label
                        finalBegin = y + edge;
                        if (finalBegin > screenHeight) {
                            finalBegin = screenHeight;
                        }
                        beginY = finalBegin - edge;
                    }
                    beginX = x; // let the label float
                    if (x < edge) {
                        beginX = edge;
                    }
                    if (finalBegin < excludeY-sq) {  // near top
                        if (x > (screenWidth-edge)) {
                            beginX = screenWidth - edge;
                        }
                    } else { // unless it is over the squares
                        if (x > (excludeX-edge*2)) {
                            beginX = excludeX-edge*2;
                        }
                    }
                    activeLine = 2;
                    Log.d(TAG, "ActionDown 'move begin'");
                    Main.adjustViewOption = "move";
                    invalidate();
                    return true;
                } // begin

                // end
                if (((x-edge) < endX) && ((x+edge) > endX) && ((y-edge) < endY) && ((y+edge) > endY)) {
                    if (finalEnd < endY) { // line above the label
                        finalEnd = y - edge;
                        if (finalEnd < 0) {
                            finalEnd = 0;
                        }
                        endY = finalEnd + edge;
                    } else { // line below the label
                        finalEnd = y + edge;
                        if (finalEnd > screenHeight) {
                            finalEnd = screenHeight;
                        }
                        endY = finalEnd - edge;
                    }
                    endX = x; // let the label float
                    if (x < edge) {
                        endX = edge;
                    }
                    if (x > (screenWidth-edge)) {
                        endX = screenWidth - edge;
                    }
                    activeLine = 3;
                    Log.d(TAG, "ActionDown 'move end'");
                    Main.adjustViewOption = "move";
                    invalidate();
                    return true;
                } // end

                // start
                if (((x-edge) < startX) && ((x+edge) > startX) && ((y-edge) < startY) && ((y+edge) > startY)) {
                    if (finalStart < startY) { // line above the label
                        finalStart = y - edge;
                        if (finalStart < 0) {
                            finalStart = 0;
                        }
                        startY = finalStart + edge;
                    } else { // line below the label
                        finalStart = y + edge;
                        if (finalStart > screenHeight) {
                            finalStart = screenHeight;
                        }
                        startY = finalStart - edge;
                    }
                    startX = x; // let the label float
                    if (x < edge) {
                        startX = edge;
                    }
                    if (x > (screenWidth-edge)) {
                        startX = screenWidth - edge;
                    }
                    activeLine = 4;
                    Log.d(TAG, "ActionDown 'move start'");
                    Main.adjustViewOption = "move";
                    invalidate();
                    return true;
                } // start

                // stop
                if (((x-edge) < stopX) && ((x+edge) > stopX) && ((y-edge) < stopY) && ((y+edge) > stopY)) {
                    if (finalStop < stopY) { // line above the label
                        finalStop = y - edge;
                        if (finalStop < 0) {
                            finalStop = 0;
                        }
                        stopY = finalStop + edge;
                    } else { // line below the label
                        finalStop = y + edge;
                        if (finalStop > screenHeight) {
                            finalStop = screenHeight;
                        }
                        stopY = finalStop - edge;
                    }
                    stopX = x; // let the label float
                    if (x < edge) {
                        stopX = edge;
                    }
                    if (x > (screenWidth-edge)) {
                        stopX = screenWidth - edge;
                    }
                    activeLine = 5;
                    Log.d(TAG, "ActionDown 'move stop'");
                    Main.adjustViewOption = "move";
                    invalidate();
                    return true;
                } // stop

                // check for inside the save square
                if (((x-edge) < saveX) && ((x+edge) > saveX) && ((y-edge) < saveY) && ((y+edge) > saveY)) {
                    Log.d(TAG, "ActionDown 'save'");
                    Main.adjustViewOption = "save";
                    saveSquare();
                    ((Activity)getContext()).finish();  // it works !!!!
                } // save square

                // exclude square -- modifies existing record
                if (((x-edge) < excludeX) && ((x+edge) > excludeX) && ((y-edge) < excludeY) && ((y+edge) > excludeY)) {
                    Log.d(TAG, "ActionDown 'exclude'");
                    Main.adjustViewOption = "exclude";
                    excludeSquare();
                    return false;
                } // exclude square

                // cancel square -- remove all limits
                if (((x-edge) < cancelX) && ((x+edge) > cancelX) && ((y-edge) < cancelY) && ((y+edge) > cancelY)) {
                    Log.d(TAG, "ActionDown 'cancel'");
                    Main.adjustViewOption = "cancel";
                    cancelSquare();
                    return false;
                }

                Log.d(TAG, "ActionDown 'move'");
                Main.adjustViewOption = "move";
                invalidate();
                return true;
            } // action down


            case MotionEvent.ACTION_UP: { // finger up
                //if (isInit == true) {
                //    Log.d(TAG, "1b) ActionUp x:" + x + " y:" + y + " isInit:" + isInit );
                //    return true;
                //}
                Log.d(TAG, "ActionUp 'edit'");
                Main.adjustViewOption = "edit";
                invalidate();
                break; // return true below
            }
        } 	// switch
        return true;
    }

    boolean saveSquare() {
        // I come back here when my finger bounces or moves a tiny bit
        // so I need to disable it the second time but clear the flag when I get back to ListView - thus not static
        if (isSaveOnce == true) {
            Log.d(TAG, "Blocking any ActionDown on Save" );
            return false;
        }
        Log.d(TAG, "ActionDown inside Save square" );
        Log.d(TAG, "start:" + finalStart + " stop:" + finalStop + " begin:" + finalBegin + " end:" + finalEnd
                + " low:" + finalLow + " high:" + finalHigh );
        if (finalStart == 0 && finalStop == screenHeight) {
            return true;  // don't save a new record -- but frequency cutoff (currentX) may have changed
            // you have to click "exclude"
        }
        Main.isNewStartStop = true;


        int oldStart = Main.songStartAtLoc; // msec = start pixels / pixel/ms = ms
        int oldStop = Main.songStopAtLoc;
        Log.d(TAG, "oldStart:" + oldStart + " oldStop:" + oldStop);
        int newStart = 0;
        if (finalStart > 0) {
            newStart = (int) ((finalStart / scaleH) + 0.5);
        }
        int newStop = 0;
        if (finalStop < screenHeight || oldStart > 0) {
            newStop = (int) ((finalStop / scaleH) + 0.5);
        }
        Log.d(TAG, "newStart:" + newStart + " newStop:" + newStop );
        Main.songStartAtLoc = oldStart + newStart;
        Main.songStopAtLoc = oldStart + newStop;  // this is not a bug -- I do mean oldStart + newStop
        Log.d(TAG, "new Adjusted StartAtLoc:" + Main.songStartAtLoc + " new Adjusted songStopAtLoc:" + Main.songStopAtLoc );
        Log.d(TAG, "ActionDown closing AdjustView songStartAtLoc:" + Main.songStartAtLoc + " songStopAtLoc:" + Main.songStopAtLoc );
        //String defineName = Main.specInxSeg[Main.selectedSong[Main.thisSong]];
        int ref = Main.ref[Main.selectedSong[Main.thisSong]];
        qry = "SELECT MAX(Seg) AS MaxSeg FROM SongList" +
                " WHERE Ref = " + Main.existingRef +
                " AND Inx = " + Main.existingInx +
                " AND FileName = " + q + song + q;
        //Log.d(TAG, "ActionDown qry:" + qry );
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        // int cntr = rs.getCount();
        rs.moveToFirst();
        int maxSeg = rs.getInt(0)+1; // get a new segment
        Main.existingSeg = maxSeg;
        qry = "SELECT SourceMic, SampleRate, AudioSource FROM SongList" +
                " WHERE Ref = " + Main.existingRef +
                " AND Inx = " + Main.existingInx +
                " AND FileName = " + q + song + q;
        Log.d(TAG, "ActionDown qry:" + qry );
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        // int cntr = rs.getCount();
        rs.moveToFirst();
        int sourceMic = rs.getInt(0);
        int sampleRate = rs.getInt(1);
        int audioSource = rs.getInt(2);
        Main.db.beginTransaction();
        ContentValues val = new ContentValues();
        val.put("Ref", Main.existingRef);
        val.put("Inx", Main.existingInx);
        val.put("Seg", Main.existingSeg);
        val.put("Path", Main.path);
        val.put("FileName", song);
        val.put("Start", Main.songStartAtLoc);
        val.put("Stop", Main.songStopAtLoc);
        val.put("Identified", 0);
        val.put("Defined", 0);
        val.put("AutoFilter", 0);
        val.put("Enhanced", 0);
        val.put("Smoothing", 0);
        val.put("SourceMic", sourceMic);
        val.put("SampleRate", sampleRate);
        val.put("AudioSource", audioSource);
/*      val.put("lowFreqCutoff", (int) ((float) Main.lowFreqCutoff * Main.hzPerStep + 0.5));
        val.put("HighFreqCutoff", (int) ((float) Main.highFreqCutoff * Main.hzPerStep + 0.5));
        int fstr = Main.filterStartAtLoc - Main.songStartAtLoc;
            if (fstr < 0) {
                fstr = 0;
            }
        int fstp = Main.filterStopAtLoc - Main.songStartAtLoc;
            if (fstp > Main.songStopAtLoc || fstp < 0 || Main.filterStopAtLoc > Main.songStopAtLoc) {
                fstp = 0;
            }
        val.put("FilterStart", fstr);
        val.put("FilterStop", fstp);
*/
        // don't save frequency or filters
        val.put("lowFreqCutoff", 0);
        val.put("HighFreqCutoff", 0);
        val.put("FilterStart", 0);
        val.put("FilterStop", 0);
        Main.db.insert("SongList", null, val);
        Main.db.setTransactionSuccessful();
        Main.db.endTransaction();
        val.clear();
        rs.close();
        // NOTE: This is a new record but it is NOT in songs, songsCombined, etc.
        int cntr = Main.songsDbLen;  // actual count in the database  I've got room -- I added 20
        // cntr++; // why ?? -- I think I need to do this but there is a null between the last and the one I added
        // so code loop uses 0 to < cntr So I am storing at cntr.
        Main.songs[cntr] = song;
        Main.ck[cntr] = true;
        // Main.thisSong = cntr; // will this work?? -- dream on !!!!  -- it gets set to zero sometime.
        Main.selectedSong[Main.thisSong] = cntr;  // I'll try this in that this song is 0 so overwrite the original 8 with a 13
        cntr++;  // now i increment
        Main.songsDbLen = cntr;
        // can I re-plot this or can I show list where new segment exists.
        Main.isNewStartStop = true;
        // gg - disabled ((Activity)getContext()).finish();
        // i need to save these above in SongList  FilterStart, FilterStop,

        // update Filter table
        Log.d(TAG, "saveSquare song:" + song);
        // Start
        if (Main.songStartAtLoc > 0) {
            qry = "SELECT * FROM Filter WHERE XcName = " + q + song + q  + " AND FilterType = 'Start'";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            Main.db.beginTransaction();
            if (rs.getCount() == 0) {
                val = new ContentValues();
                val.put("XcName", song);
                val.put("FilterType", "Start");
                val.put("FilterVal", Main.songStartAtLoc);
                Main.db.insert("Filter", null, val);
                val.clear();
            } else {
                String qry1 = "UPDATE Filter SET FilterVal = " + Main.songStartAtLoc +
                        " WHERE XcName = " + q + song + q + " AND FilterType = 'Start'";
                Main.db.execSQL(qry1);
            }
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();
        }
        // Stop
        if (Main.songStopAtLoc > 0) {
            qry = "SELECT * FROM Filter WHERE XcName = " + q + song + q + " AND FilterType = 'Stop'";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            Main.db.beginTransaction();
            if (rs.getCount() == 0) {
                val = new ContentValues();
                val.put("XcName", song);
                val.put("FilterType", "Stop");
                val.put("FilterVal", Main.songStopAtLoc);
                Main.db.insert("Filter", null, val);
                val.clear();
            } else {
                String qry1 = "UPDATE Filter SET FilterVal = " + Main.songStopAtLoc +
                        " WHERE XcName = " + q + song + q + " AND FilterType = 'Stop'";
                Main.db.execSQL(qry1);
            }
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();
        }

        Main.adjustViewOption = "clear";
        invalidate();
        isSaveOnce = true;
        return false;

    }

    boolean excludeSquare() {
        // I come back here when my finger bounces or moves a tiny bit
        // so I need to disable it the second time but clear the flag when I get back to ListView - thus not static
        if (isExcludeOnce == true) {
            Log.d(TAG, "Blocking any ActionDown on Exclude" );
            return false;
        }

        Log.d(TAG, "ActionDown inside Exclude " );
        Log.d(TAG, "start:" + finalStart + " stop:" + finalStop + " begin:" + finalBegin + " end:" + finalEnd
                + " low:" + finalLow + " high:" + finalHigh );
        if (finalBegin == 0) {
            Main.filterStartAtLoc = 0;
        } else {
            Main.filterStartAtLoc = (int) ((finalBegin / scaleH) + 0.5);
        }
        if (finalEnd == screenHeight) {
            Main.filterStopAtLoc = 0;
            finalEnd = 0;
        } else {
            Main.filterStopAtLoc = (int) ((finalEnd / scaleH) + 0.5);
        }
        if (finalLow == 0) {
            Main.lowFreqCutoff = 0;
        } else {
            Main.lowFreqCutoff = (int) ((finalLow / scaleW) + 0.5);
        }
        if (finalHigh == screenWidth) {
            Main.highFreqCutoff = 0;
            finalHigh = 0;
        } else {
            Main.highFreqCutoff = (int) ((finalHigh / scaleW) + 0.5);
        }
        Main.db.beginTransaction();
        qry = "UPDATE SongList" +
                " SET FilterStart = " + Main.filterStartAtLoc +
                ", FilterStop = " + Main.filterStopAtLoc +
                ", LowFreqCutoff = " + (int) ((float) Main.lowFreqCutoff * Main.hzPerStep + 0.5) +  // freq only in database 0->511 everywhere else
                ", HighFreqCutoff = " + (int) ((float) Main.highFreqCutoff * Main.hzPerStep + 0.5)  +
                " WHERE Ref = " + Main.existingRef +
                " AND Inx = " + Main.existingInx +
                " AND Seg = " + Main.existingSeg +
                " AND Path = " + Main.path +
                " AND FileName = " + q + song + q;
        Main.db.execSQL(qry);
        Main.db.setTransactionSuccessful();
        Main.db.endTransaction();

        // update Filter table
        Log.d(TAG, "excludeSquare song:" + song);
        // LowFreqCutoff
        if (Main.lowFreqCutoff > 0) {
            qry = "SELECT * FROM Filter WHERE XcName = " + q + song + q + " AND FilterType = 'LowFreqCutoff'";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            Main.db.beginTransaction();
            if (rs.getCount() == 0) {  // insert
                ContentValues val = new ContentValues();
                val.put("XcName", song);
                val.put("FilterType", "LowFreqCutoff");
                val.put("FilterVal", (int) (Main.lowFreqCutoff * Main.hzPerStep + 0.5));
                Main.db.insert("Filter", null, val);
                val.clear();
            } else { // update
                String qry1 = "UPDATE Filter SET FilterVal = " + (int) (Main.lowFreqCutoff * Main.hzPerStep + 0.5) +
                        " WHERE XcName = " + q + song + q + " AND FilterType = 'LowFreqCutoff'";
                Main.db.execSQL(qry1);
            }
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();
        }
        // HighFreqCutoff
        if (Main.highFreqCutoff > 0) {
            qry = "SELECT * FROM Filter WHERE XcName = " + q  + song + q + " AND FilterType = 'HighFreqCutoff'";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            Main.db.beginTransaction();
            if (rs.getCount() == 0) { // insert
                ContentValues val = new ContentValues();
                val.put("XcName", song);
                val.put("FilterType", "HighFreqCutoff");
                val.put("FilterVal", (int) (Main.highFreqCutoff * Main.hzPerStep + 0.5));
                Main.db.insert("Filter", null, val);
                val.clear();
            } else { // update
                String qry1 = "UPDATE Filter SET FilterVal = " + (int) (Main.highFreqCutoff * Main.hzPerStep + 0.5) +
                        " WHERE XcName = " + q + song + q + " AND FilterType = 'HighFreqCutoff'";
                Main.db.execSQL(qry1);
            }
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();
        }
        // FilterStart (begin)
        if (Main.filterStartAtLoc > 0) {
            qry = "SELECT * FROM Filter WHERE XcName = " + q + song + q + " AND FilterType = 'FilterStart'";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            Main.db.beginTransaction();
            if (rs.getCount() == 0) {
                ContentValues val = new ContentValues();
                val.put("XcName", song);
                val.put("FilterType", "FilterStart");
                val.put("FilterVal", Main.filterStartAtLoc);
                Main.db.insert("Filter", null, val);
                val.clear();
            } else {
                String qry1 = "UPDATE Filter SET FilterVal = " + Main.filterStartAtLoc +
                        " WHERE XcName = " + q + song + q + " AND FilterType = 'FilterStart'";
                Main.db.execSQL(qry1);
            }
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();
        }
        // FilterStop (end)
        if (Main.filterStopAtLoc > 0) {
            qry = "SELECT * FROM Filter WHERE XcName = " + q + song + q + " AND FilterType = 'FilterStop'";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            Main.db.beginTransaction();
            if (rs.getCount() == 0) {
                ContentValues val = new ContentValues();
                val.put("XcName", song);
                val.put("FilterType", "FilterStop");
                val.put("FilterVal", Main.filterStopAtLoc);
                Main.db.insert("Filter", null, val);
                val.clear();
            } else {
                String qry1 = "UPDATE Filter SET FilterVal = " + Main.filterStopAtLoc +
                        " WHERE XcName = " + q + song + q + " AND FilterType = 'FilterStop'";
                Main.db.execSQL(qry1);
            }
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();
        }

        Main.adjustViewOption = "clearExclude";
        invalidate();
        isExcludeOnce = true;
        return true;
    } // exclude square

    boolean cancelSquare() {
        // NOTE: cancel modifies the record by zeroing existing data -- this is not escape or quit -- use the back key for that
        // ALSO:  Main.songStartAtLoc and Main.songStopAtLoc are not modified with cancel -- delete the record if that is what you want
        Log.d(TAG, "ActionDown inside Cancel" );
        Log.d(TAG, "start:" + finalStart + " stop:" + finalStop + " begin:" + finalBegin + " end:" + finalEnd
                + " low:" + finalLow + " high:" + finalHigh );
        finalLow = 0;
        finalHigh = 0;
        finalBegin = 0;
        finalEnd = 0;
        //finalStart = 0;
        //finalStop = 0;
        Main.filterStartAtLoc = 0; // msec = start pixels / pixel/ms = ms
        Main.filterStopAtLoc = 0;
        Main.lowFreqCutoff = 0;
        Main.highFreqCutoff = 0;
        //Main.songStartAtLoc = 0; // msec = start pixels / pixel/ms = ms
        //Main.songStopAtLoc = 0;
        Main.db.beginTransaction();
        qry = "UPDATE SongList" +
                " SET FilterStart = 0" +
                ", FilterStop = 0" +
                ", LowFreqCutoff = 0" +
                ", HighFreqCutoff = 0" +
                " WHERE Ref = " + Main.existingRef +
                " AND Inx = " + Main.existingInx +
                " AND Seg = " + Main.existingSeg +
                " AND Path = " + Main.path +
                " AND FileName = " + q + song + q;
        Main.db.execSQL(qry);
        qry = "DELETE FROM Filter WHERE XcName = " + q + song + q;
        Main.db.execSQL(qry);
        Main.db.setTransactionSuccessful();
        Main.db.endTransaction();
        Main.adjustViewOption = "clear";
        invalidate();
        isCancelOnce = true;
        return true;
    }

}
