package com.modelsw.birdingviamic;

import android.util.Log;

public class FFTData {
    String TAG = "FFTData";

    public FFTData(byte[] bytes) {  // this is for visualizer view data
        //Log.d(TAG, "cntrFftCall:" + Main.cntrFftCall);
        this.bytes = bytes;
        System.arraycopy(this.bytes, 0, Main.fftdata, 0, 512);
    }
    public byte[] bytes;
}

