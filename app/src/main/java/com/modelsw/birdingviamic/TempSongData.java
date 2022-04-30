package com.modelsw.birdingviamic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TempSongData extends SQLiteOpenHelper {
    private static final String TAG = "TempSongData";

    public TempSongData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase tempdb) {
        Log.d(TAG, "onCreate table and (maybe) insert records");
        // disable the following line if you upgrade database then re-enable
        //	if (db != null) { // i was getting null pointer
        //		Main.db.enableWriteAheadLogging();   // re-add trying to find SQLiteDiskIOException: disk I/O error (code 1802)
        //	}
    }

    @Override
    public void onUpgrade(SQLiteDatabase tempdb, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        // I will only get here if dbOldVersion is incremented
    }

}
