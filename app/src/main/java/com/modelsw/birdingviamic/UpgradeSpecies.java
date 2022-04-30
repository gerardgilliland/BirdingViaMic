package com.modelsw.birdingviamic;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import androidx.core.content.ContextCompat;

import static com.modelsw.birdingviamic.Main.tempDatabaseName;
import static com.modelsw.birdingviamic.Main.tempsongdata;
import static com.modelsw.birdingviamic.Main.definepath;
import static com.modelsw.birdingviamic.Main.tempdefinepath;
import static com.modelsw.birdingviamic.Main.versionExist;
import static com.modelsw.birdingviamic.Main.IOC_Version;

// this is exporting tables and files from the old database
// there is no importing done here
// -- look at ConvertTables for importing into new database
public class UpgradeSpecies {
    private static final String TAG = "UpgradeSpecies";
    private Context context;
    private static String databaseName; // birdingviamic/Define/BirdSongs.db
    //private static String definepath = null; // birdingviamic/Define
    //private static String tempdefinepath = null; // /Download/Define
    private static String destinationSong = null;  //  birdingviamic/Song
    private static String sourceSong = null; // /Download/Song
    //private TempSongData tempsongdata = null;
    private SQLiteDatabase tempdb;
    private static String tempqry;
    private Cursor temprs; // I think of Cursor as Record Set
    private static String status1 = "Unknown1";
    private static String status2 = "Unknown2";
    private static int filesCopied = 0;
    private static int filesFailed = 0;
    private static int tablesCopied = 0;
    private static int tablesFailed = 0;

    //private static String sourcepath = null;  // /Download
    //private static String destinationpath = null; // birdingviamic/
    //private static String displayName = ""; // used in PlaySong identify to hold identification
    //private static String downloadpath = null; // /Download
    //private String temppath = null; // Download/Define/
    //private static String qry;
    //private EditText result2;
    //private Cursor rs; // I think of Cursor as Record Set

    //	1:  // we are going to convert
    // this WAS called indirectly from Main.init()
    // this WAS called by StartActivityForResult
    // the Define folder and Song folder were copied to the Download folder BEFORE this app installs
    // I have opened the temp database 'tempdb' in the Download folder
    // and the new database 'db' in the birdingviamic location
    // I have compared the two versions
    // if the old database version is different than the new
    // I call StartActivity for results.
    // If the user chooses "later" I clear the new IOC_Version so i can repeat the init() next startup.
    // I am here because the user wants to upgrade.
    // UpgradeSpecies() functions:
    //      the old database builds CSV files for the changes needed in the version
    //      and stores those csv files in the new birdingviamic/Define folder
    //      -- the csv files are not loaded in this function
    //      this process of building and transferring the csv files takes about a minute and a half.
    // ConvertTables() functions:
    // 		the new database imports the existing CSV files into the empty tables
    //      it converts the tables needed.

    // https://stackoverflow.com/questions/17917968/get-context-in-non-activity-class
    UpgradeSpecies(UpgradeDialog context) { // constructor
        init();
    }

    private void init() {
        filesCopied = 0;
        filesFailed = 0;
        tablesCopied = 0;
        tablesFailed = 0;

        // source is Downlaod folder; destination is birdingviamic folder
        //sourcepath = "/storage/emulated/0/Download/"; // Download/
        //destinationpath = Environment.getExternalStorageDirectory().getAbsolutePath(); // birdingviamic
        //destinationpath += "/Android/data/com.modelsw.birdingviamic/";
        databaseName = "BirdSongs.db";
        //definepath = --  birdingviamic/Define/ -- imported from Main -- see above
        //tempdefinepath = "/storage/emulated/0/Download/Define/";  // Download/Define/
        destinationSong = "/storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/";  // birdingviamic/Song/
        sourceSong = "/storage/emulated/0/Download/Song/"; // Download/Song/
        tempDatabaseName = tempdefinepath + databaseName; // Download/Define/BirdSongs.db

        //Log.i(TAG, "destinationpath: " + destinationpath);  //x
        //Log.i(TAG, "sourcepath: " + sourcepath);
        Log.i(TAG, "definepath: " + definepath); // /storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Define/
        Log.i(TAG, "tempdefinepath: " + tempdefinepath); // /storage/emulated/0/Download/Define/
        Log.i(TAG, "destinationSong: " + destinationSong);  // /storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/
        Log.i(TAG, "sourceSong: " + sourceSong); // /storage/emulated/0/Download/Song/
        Log.i(TAG, "tempDatabaseName: " + tempDatabaseName);  // /storage/emulated/0/Download/Define/BirdSongs.db


        /*
        I GAVE UP on transferring a table between databases -- I ended up exporting and inporting csv files of the tables.
        -- this commented out code is supposed to transfer tables.
        https://stackoverflow.com/questions/29220677/sqlite-easiest-way-to-copy-a-table-from-one-database-to-another/29221750
        Open the database you are copying from, then run this code to attach the database you are copying to and then copy a table over.
        ATTACH DATABASE 'other.db' AS other;
        INSERT INTO other.tbl
        SELECT * FROM main.tbl;

        Log.d(TAG, "ATTEMPT copying table DefineTotals");
        tempdb = tempsongdata.getWritableDatabase();
        tempqry = "ATTACH DATABASE " + definepath + databaseName + " AS other " +
                "INSERT INTO other.DefineTotals " +
                "SELECT * FROM " + tempdefinepath + databaseName + ".DefineTotals";
        tempdb.execSQL(tempqry);
        Log.d(TAG, "Finish copying table DefineTotals");
         */

        tempdb = tempsongdata.getWritableDatabase();

        status2 = exportCSV();
        //result2 = (EditText) findViewById(R.id.result2); // there are no screens associated with this thus no R.id
        Log.d(TAG, "csv save status2: " + status2);

        status1 = exportSongs();
        Log.d(TAG, "export folders status1: " + status1);

        tempqry = "SELECT Num FROM Version";
        temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
        temprs.moveToFirst();
        Main.versionExist = temprs.getInt(0); // now it is not zero -- it should be the old one
        temprs.close();

        // set the old database to a big number so the process won't run again.
        // this is the only modification I make to the original database and files
        Main.versionExist += 100000;
        tempqry = "UPDATE Version SET Num = " + versionExist;
        tempdb.execSQL(tempqry);

        tempdb.close(); // we are through with the old database

    } // init()

    private String exportCSV() {
        // the temp database in Download/Define/BirdSongs.db is the source
        // the csv files are copied directly to the birdingviamic/Define folder
        // where they will be loaded into the new birdingvaimic/Define/Birdsong.db -- and converted to use the new CodeName Ref number.
        // the tables that use Ref: CodeName, DefineDetail, DefineTotals, Identify, SongList
        // note the old database will only copy Ref and Spec into the table OldRef
        // tables that could be user modified: Filter, LastKnown, Location, SongPath
        // tables that user could select options from: Options, RedList, Region
        // -- I will tell the user it is not updated and they can do that independenly
        // warn the user that when they accept 'Yes' to upgrade, the screen will go dark for at least a minute.
        String[] tables = new String[]{ "CodeName", "DefineDetail", "DefineTotals", "Identify", "SongList",
                "BirdWebSites", "Filter", "LastKnown", "Location", "Options", "SongPath",
                "RedList", "Region"};

        status2 = "tablesCopied: " + tablesCopied + " tablesFailed: " + tablesFailed;

        for (int t = 0; t < tables.length; t++) {
            String tbl = tables[t];
            switch (tbl) {
                case "CodeName":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, "OldRef.csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Spec\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = temprs.getInt(0);
                            String Spec = temprs.getString(1);
                            txt += Ref + "," + Spec + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "DefineDetail":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Inx, Seg, Phrase, Record, Freq, Voiced, Energy, Distance, Quality, Samp\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = temprs.getInt(0);
                            int Inx = temprs.getInt(1);
                            int Seg = temprs.getInt(2);
                            int Phrase = temprs.getInt(3);
                            int Record = temprs.getInt(4);
                            int Freq = temprs.getInt(5);
                            int Voiced = temprs.getInt(6);
                            int Energy = temprs.getInt(7);
                            int Distance = temprs.getInt(8);
                            int Quality = temprs.getInt(9);
                            int Samp = temprs.getInt(10);
                            txt += Ref + "," + Inx + "," + Seg + "," + Phrase + "," + Record + "," + Freq + "," + Voiced + "," + Energy + "," + Distance + "," + Quality + "," + Samp + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "DefineTotals":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Inx, Seg, Phrase, Silence, Records, FreqMean, FreqStdDev, VoicedMean, VoicedStdDev, EnergyMean, EnergyStdDev, DistMean, DistStdDev, QualityMean, QualityStdDev,  SampMean, SampStdDev, Slope, SilPhrRatio\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = temprs.getInt(0);
                            int Inx = temprs.getInt(1);
                            int Seg = temprs.getInt(2);
                            int Phrase = temprs.getInt(3);
                            int Silence = temprs.getInt(4);
                            int Records = temprs.getInt(5);
                            Float FreqMean = temprs.getFloat(6);
                            Float FreqStdDev = temprs.getFloat(7);
                            Float VoicedMean = temprs.getFloat(8);
                            Float VoicedStdDev = temprs.getFloat(9);
                            Float EnergyMean = temprs.getFloat(10);
                            Float EnergyStdDev = temprs.getFloat(11);
                            Float DistMean = temprs.getFloat(12);
                            Float DistStdDev = temprs.getFloat(13);
                            Float QualityMean = temprs.getFloat(14);
                            Float QualityStdDev = temprs.getFloat(15);
                            Float SampMean = temprs.getFloat(16);
                            Float SampStdDev = temprs.getFloat(17);
                            Float Slope = temprs.getFloat(18);
                            Float SilPhrRatio = temprs.getFloat(19);
                            txt += Ref + "," + Inx + "," + Seg + "," + Phrase + "," + Silence + "," + Records + "," + FreqMean + "," + FreqStdDev + "," + VoicedMean + "," + VoicedStdDev + "," + EnergyMean + "," + EnergyStdDev + "," + DistMean + "," + DistStdDev + "," + QualityMean + "," + QualityStdDev + "," + SampMean + "," + SampStdDev + "," + Slope + "," + SilPhrRatio + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "Identify":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Cntr, Criteria\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = temprs.getInt(0);
                            int Cntr = temprs.getInt(1);
                            String Criteria = temprs.getString(2);
                            txt += Ref + "," + Cntr + "," + Criteria + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "SongList":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Inx, Seg, Path, FileName, Start, Stop, Identified, Defined, AutoFilter, Enhanced, Smoothing, SourceMic, SampleRate, AudioSource, LowFreqCutoff, HighFreqCutoff, FilterStart, FilterStop, Stereo\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = temprs.getInt(0);
                            int Inx = temprs.getInt(1);
                            int Seg = temprs.getInt(2);
                            int Path = temprs.getInt(3);
                            String FileName = temprs.getString (4);
                            int Start = temprs.getInt(5);
                            int Stop = temprs.getInt(6);
                            int Identified = temprs.getInt(7);
                            int Defined = temprs.getInt(8);
                            int AutoFilter = temprs.getInt(9);
                            int Enhanced = temprs.getInt(10);
                            int Smoothing = temprs.getInt(11);
                            int SourceMic = temprs.getInt(12);
                            int SampleRate = temprs.getInt(13);
                            int AudioSource = temprs.getInt(14);
                            int LowFreqCutoff = temprs.getInt(15);
                            int HighFreqCutoff = temprs.getInt(16);
                            int FilterStart = temprs.getInt(17);
                            int FilterStop = temprs.getInt(18);
                            int Stereo = temprs.getInt(19);
                            txt += Ref + "," + Inx + "," + Seg + "," + Path + "," + FileName + "," + Start + "," + Stop + "," + Identified + "," + Defined + "," + AutoFilter + "," + Enhanced + "," + Smoothing + "," + SourceMic + "," + SampleRate + "," + AudioSource + "," + LowFreqCutoff + "," + HighFreqCutoff + "," + FilterStart + "," + FilterStop + "," + Stereo + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "BirdWebSites":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "id, WebSiteText, WebSiteLink\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int id = temprs.getInt(0);
                            String WebSiteText = temprs.getString(1);
                            String WebSiteLink = temprs.getString(2);
                            txt += id + "," + WebSiteText + "," + WebSiteLink + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "Filter":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "XcName, FilterType, FilterVal\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String XcName = temprs.getString(0);
                            String FilterType = temprs.getString(1);
                            int FilterVal= temprs.getInt(2);
                            txt += XcName + "," + FilterType + "," + FilterVal + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "LastKnown":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "FileName, LastDate, Activity\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String FileName = temprs.getString(0);
                            String LastDate = temprs.getString(1);
                            String Activity = temprs.getString(2);
                            txt += FileName + "," + LastDate + "," + Activity + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "Location":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Name, Value\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String Name = temprs.getString(0);
                            int Value = temprs.getInt(1);
                            txt += Name + "," + Value + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "Options":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Name, Value\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String Name = temprs.getString(0);
                            int Value = temprs.getInt(1);
                            txt += Name + "," + Value + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "SongPath":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Path, CustomPath\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Path = temprs.getInt(0);
                            String CustomPath = temprs.getString(1);
                            txt += Path + "," + CustomPath + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                case "RedList":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "ix, type, FullName, isSelected\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int ix = temprs.getInt(0);  // 0
                            String type = temprs.getString(1);  // NE
                            String FullName = temprs.getString(2);  // Not Evaluated
                            int isSelected = temprs.getInt(3);  // 1
                            txt += ix + "," + type + "," + FullName + "," + isSelected + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                /*case "RefUpgrade":

                    // this ties current database with Download/database to build table RefUgrade
                    // *********************
                    SELECT CodeName.Ref AS NewRef, CodeName92.Ref AS OldRef INTO RefUpgrade
                    FROM CodeName INNER JOIN CodeName92 ON CodeName.Spec = CodeName92.Spec
                    ORDER BY CodeName.Ref;
                    // *********************

                    tablesFailed += 1;
                    Log.i(TAG, "csv file FAILED: " + tbl);
                    break; */
                case "Region":
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Area, FullName, isSelected\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String Area = temprs.getString(0);  // NE
                            String FullName = temprs.getString(1);  // Not Evaluated
                            int isSelected = temprs.getInt(2);  // 1
                            txt += Area + "," + FullName + "," + isSelected + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;
                /*case "Version":  // this is IOC version -- one row one column
                    try {
                        String txt = null;
                        File csvfile = new File(definepath, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Num\n";
                        tempqry = "Select " + txt + " from " + tbl;
                        Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                        temprs.moveToFirst();
                        int cntr = temprs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Num = temprs.getInt(0);  // 1
                            txt += Num + "\n";
                            temprs.moveToNext();
                        } // next i
                        temprs.close();
                        writer.append(txt);
                        writer.flush();
                        writer.close();
                        tablesCopied += 1;
                        Log.i(TAG, "csv file saved: " + tbl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tablesFailed += 1;
                        Log.i(TAG, "csv file FAILED: " + tbl);
                    }
                    break;*/
                default:
                    Log.i(TAG, "table not found: " + tbl);
                    tablesFailed += 1;
            }
        }
        //status2 = "tablesCopied: " + tablesCopied + " tablesFailed: " + tablesFailed;
        return status2;
    } // exportCSV

    // this is copying Download/Song/songs to an empty BirdingViaMic/Song folder
    // I believe the Filter.csv should be imported regardless of where it exists: Define, or Song
    // I can't judge where it is right now so I am ignoring it.
    // But since the Database is in Download folder, And I am installing that data,
    // I need to reflect the Songlist table with the Songs -- So all songs in Download/Song should be loaded
    // but only those songs.
    // If I have songs in the birdingviamic/Song folder where did they come from. I don't know.
    // So I will clean out the birdingviamic/Song folder before starting the load.
    private String exportSongs() {
        Log.i(TAG, "here in exportSongs()");
        boolean status = false;
        File dir = null;
        Log.i(TAG, "make the Song directory");
        dir = new File(destinationSong);
        status = dir.exists();
        if (status == false) {
            status = dir.mkdir();
        } else {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
            //status = dir.delete();
            //status = dir.mkdir();
        }

        Log.i(TAG, "go save the song files");
        saveAssets(sourceSong, destinationSong);
        status1 = "filesCopied: " + filesCopied + " filesFailed: " + filesFailed;
        return status1;
    }

    private void saveAssets(String source, String destination) {
        Log.i(TAG, "SaveAssets: source: " + source);
        Log.i(TAG, "SaveAssets: destination: " + destination);
        File directory = new File(source);
        File[] files = directory.listFiles();
        Log.i("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.i(TAG, "FileName:" + files[i].getName());
            String in = files[i].toString();
            Log.i(TAG, "in: " + in);
            String out = destination + files[i].getName();
            Log.i(TAG, "out: " + out);
            copyFile(in, out);
        }
    }

    // this loads the files to Define, Song
    private void copyFile (String in, String out) {
        Log.i(TAG, "copyFile: ");
        try {
            File src = new File (in);
            File dst = new File(out);
            FileChannel source = new FileInputStream(src).getChannel();
            long sourcesize = source.size();
            FileChannel destination = new FileOutputStream(dst).getChannel();
            destination.transferFrom(source, 0, sourcesize);
            source.close();
            destination.close();
            Log.i(TAG, "copyFile status: success ");
            filesCopied += 1;
            //return "success";
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "copyFile status: failed ");
            filesFailed += 1;
            //return "failed";
        }

    } // copyFile



}
