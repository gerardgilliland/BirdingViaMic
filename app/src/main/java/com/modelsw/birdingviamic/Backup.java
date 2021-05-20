package com.modelsw.birdingviamic;

// https://stackoverflow.com/questions/29867121/how-to-copy-a-file-to-another-directory-programmatically

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Backup extends AppCompatActivity implements android.view.View.OnClickListener {
    private static final String TAG = "Backup";
    Toolbar toolbar;
    private Button backup;
    private static String databaseName; // birdingviamic/Define/BirdSongs.db
    private static String destinationpath = null;  // /Download
    private static String sourcepath = null; // birdingviamic/
    private static String sourceDefine = null; // birdingviamic/Define
    private static String sourceSong = null;  //  birdingviamic/Song
    private static String destinationDefine = null; // /Download/Define
    private static String destinationSong = null; // /Download/Song
    private static String displayName = ""; // used in PlaySong identify to hold identification
    private static String downloadpath = null; // /Download
    private static String filename = "RedList.csv";
    private static String qry;
    private EditText result;
    private EditText result1;
    private EditText result2;
    //private static String status = "Unknown";
    private static String status1 = "Unknown1";
    private static String status2 = "Unknown2";
    private static int filesCopied = 0;
    private static int filesFailed = 0;
    private static int tablesCopied = 0;
    private static int tablesFailed = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);
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
        findViewById(R.id.backup_button).setOnClickListener(this);
        //result = (EditText) findViewById(R.id.result);
        result1 = (EditText) findViewById(R.id.result1);
        result2 = (EditText) findViewById(R.id.result2);
        //result.setText("Backup Result: ");
        init();
    }

    private void init() {
        filesCopied = 0;
        filesFailed = 0;
        tablesCopied = 0;
        tablesFailed = 0;
        databaseName = "BirdSongs.db";
        sourcepath = Environment.getExternalStorageDirectory().getAbsolutePath(); // storage/emulated/0/
        sourceDefine = getExternalFilesDir("Define").toString() + "/"; // birdingviamic/Define/
        sourceSong = getExternalFilesDir("Song").toString() + "/"; // birdingviamic/Song/
        destinationpath = "/storage/emulated/0/Download/"; //x Download/
        destinationDefine = destinationpath + "Define/";  //x Download/Define/
        destinationSong = destinationpath + "Song/";  //x Download/Song/

        //destinationpath += databaseName;
        Log.i(TAG, "sourcepath: " + sourcepath);
        Log.i(TAG, "sourceDefine: " + sourceDefine); //x
        Log.i(TAG, "sourceSong: " + sourceSong); //x
        Log.i(TAG, "destinationpath: " + destinationpath);  //x
        Log.i(TAG, "destinationDefine: " + destinationDefine); //x
        Log.i(TAG, "destinationSong: " + destinationSong);  //x
    }

    private String exportFolders() {
        Log.i(TAG, "here in exportFolders()");
        boolean status = false;
        File dir = null;
        Log.i(TAG, "make the Define directory");
        dir = new File(destinationDefine);
        status = dir.exists();
        if (status == false) {
            status = dir.mkdir();
        }
        if (status == true) {
            Log.i(TAG, "go save the define files");
            saveAssets(sourceDefine, destinationDefine);
        } else {
            return "failed";
        }
        Log.i(TAG, "make the Song directory");
        dir = new File(destinationSong);
        status = dir.exists();
        if (status == false) {
            status = dir.mkdir();
        }
        if (status == true) {
            Log.i(TAG, "go save the song files");
            saveAssets(sourceSong, destinationSong);
        } else {
            return "failed";
        }
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

/*
 *	 the tables that use Ref: CodeName, DefineDetail, DefineTotals, Identify, SongList
 *	 the table that could be user modified: BirdWebSites, Filter, LastKnown, Location, Options, SongPath
 *	 the tables that I control: RedList, RefUpgrade, Region, Version
 */
    private String exportCSV() {
        // the csv files are copied directly to the Download/Define folder so they are not duplicated in the local birdingviamic/Define folder
        String[] tables = new String[]{ "CodeName", "DefineDetail", "DefineTotals", "Identify", "SongList",
                "BirdWebSites", "Filter", "LastKnown", "Location", "Options", "SongPath",
                "RedList", "RefUpgrade", "Region", "Version"};

        //status2 = "tablesCopied: " + tablesCopied + " tablesFailed: " + tablesFailed;

        for (int t = 0; t < tables.length; t++) {
            String tbl = tables[t];
            switch (tbl) {
                case "CodeName":
                    try {
                        String txt = null;
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Spec, CommonName, Region, SubRegion, RedList, InArea, MinX, MinY, MaxX, MaxY\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = rs.getInt(0);
                            String Spec = rs.getString(1);
                            String CommonName = rs.getString(2);
                            String Region = rs.getString(3);
                            String SubRegion = rs.getString(4);
                            String RedList = rs.getString(5);
                            int InArea = rs.getInt(6);
                            int MinX = rs.getInt(7);
                            int MinY = rs.getInt(8);
                            int MaxX = rs.getInt(9);
                            int MaxY = rs.getInt(10);
                            txt += Ref + "," + Spec + "," + CommonName + "," + Region + "," + SubRegion + "," + RedList + "," + InArea + "," + MinX + "," + MinY + "," + MaxX + "," + MaxY + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Inx, Seg, Phrase, Record, Freq, Voiced, Energy, Distance, Quality, Samp\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = rs.getInt(0);
                            int Inx = rs.getInt(1);
                            int Seg = rs.getInt(2);
                            int Phrase = rs.getInt(3);
                            int Record = rs.getInt(4);
                            int Freq = rs.getInt(5);
                            int Voiced = rs.getInt(6);
                            int Energy = rs.getInt(7);
                            int Distance = rs.getInt(8);
                            int Quality = rs.getInt(9);
                            int Samp = rs.getInt(10);
                            txt += Ref + "," + Inx + "," + Seg + "," + Phrase + "," + Record + "," + Freq + "," + Voiced + "," + Energy + "," + Distance + "," + Quality + "," + Samp + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Inx, Seg, Phrase, Silence, Records, FreqMean, FreqStdDev, VoicedMean, VoicedStdDev, EnergyMean, EnergyStdDev, DistMean, DistStdDev, QualityMean, QualityStdDev,  SampMean, SampStdDev, Slope, SilPhrRatio\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = rs.getInt(0);
                            int Inx = rs.getInt(1);
                            int Seg = rs.getInt(2);
                            int Phrase = rs.getInt(3);
                            int Silence = rs.getInt(4);
                            int Records = rs.getInt(5);
                            Float FreqMean = rs.getFloat(6);
                            Float FreqStdDev = rs.getFloat(7);
                            Float VoicedMean = rs.getFloat(8);
                            Float VoicedStdDev = rs.getFloat(9);
                            Float EnergyMean = rs.getFloat(10);
                            Float EnergyStdDev = rs.getFloat(11);
                            Float DistMean = rs.getFloat(12);
                            Float DistStdDev = rs.getFloat(13);
                            Float QualityMean = rs.getFloat(14);
                            Float QualityStdDev = rs.getFloat(15);
                            Float SampMean = rs.getFloat(16);
                            Float SampStdDev = rs.getFloat(17);
                            Float Slope = rs.getFloat(18);
                            Float SilPhrRatio = rs.getFloat(19);
                            txt += Ref + "," + Inx + "," + Seg + "," + Phrase + "," + Silence + "," + Records + "," + FreqMean + "," + FreqStdDev + "," + VoicedMean + "," + VoicedStdDev + "," + EnergyMean + "," + EnergyStdDev + "," + DistMean + "," + DistStdDev + "," + QualityMean + "," + QualityStdDev + "," + SampMean + "," + SampStdDev + "," + Slope + "," + SilPhrRatio + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Cntr, Criteria\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = rs.getInt(0);
                            int Cntr = rs.getInt(1);
                            String Criteria = rs.getString(2);
                            txt += Ref + "," + Cntr + "," + Criteria + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Ref, Inx, Seg, Path, FileName, Start, Stop, Identified, Defined, AutoFilter, Enhanced, Smoothing, SourceMic, SampleRate, AudioSource, LowFreqCutoff, HighFreqCutoff, FilterStart, FilterStop, Stereo\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Ref = rs.getInt(0);
                            int Inx = rs.getInt(1);
                            int Seg = rs.getInt(2);
                            int Path = rs.getInt(3);
                            String FileName = rs.getString (4);
                            int Start = rs.getInt(5);
                            int Stop = rs.getInt(6);
                            int Identified = rs.getInt(7);
                            int Defined = rs.getInt(8);
                            int AutoFilter = rs.getInt(9);
                            int Enhanced = rs.getInt(10);
                            int Smoothing = rs.getInt(11);
                            int SourceMic = rs.getInt(12);
                            int SampleRate = rs.getInt(13);
                            int AudioSource = rs.getInt(14);
                            int LowFreqCutoff = rs.getInt(15);
                            int HighFreqCutoff = rs.getInt(16);
                            int FilterStart = rs.getInt(17);
                            int FilterStop = rs.getInt(18);
                            int Stereo = rs.getInt(19);
                            txt += Ref + "," + Inx + "," + Seg + "," + Path + "," + FileName + "," + Start + "," + Stop + "," + Identified + "," + Defined + "," + AutoFilter + "," + Enhanced + "," + Smoothing + "," + SourceMic + "," + SampleRate + "," + AudioSource + "," + LowFreqCutoff + "," + HighFreqCutoff + "," + FilterStart + "," + FilterStop + "," + Stereo + "\n";
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "id, WebSiteText, WebSiteLink\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int id = rs.getInt(0);
                            String WebSiteText = rs.getString(1);
                            String WebSiteLink = rs.getString(2);
                            txt += id + "," + WebSiteText + "," + WebSiteLink + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "XcName, FilterType, FilterVal\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String XcName = rs.getString(0);
                            String FilterType = rs.getString(1);
                            int FilterVal= rs.getInt(2);
                            txt += XcName + "," + FilterType + "," + FilterVal + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "FileName, LastDate, Activity\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String FileName = rs.getString(0);
                            String LastDate = rs.getString(1);
                            String Activity = rs.getString(2);
                            txt += FileName + "," + LastDate + "," + Activity + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Name, Value\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String Name = rs.getString(0);
                            int Value = rs.getInt(1);
                            txt += Name + "," + Value + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Name, Value\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String Name = rs.getString(0);
                            int Value = rs.getInt(1);
                            txt += Name + "," + Value + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Path, CustomPath\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Path = rs.getInt(0);
                            String CustomPath = rs.getString(1);
                            txt += Path + "," + CustomPath + "\n";
                            rs.moveToNext();
                        } // next i
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
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "ix, type, FullName, isSelected\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int ix = rs.getInt(0);  // 0
                            String type = rs.getString(1);  // NE
                            String FullName = rs.getString(2);  // Not Evaluated
                            int isSelected = rs.getInt(3);  // 1
                            txt += ix + "," + type + "," + FullName + "," + isSelected + "\n";
                            rs.moveToNext();
                        } // next i
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
                case "RefUpgrade":
                    /*
                    // this ties current database with Download/database to build table RefUgrade
                    // *********************
                    SELECT CodeName.Ref AS NewRef, CodeName92.Ref AS OldRef INTO RefUpgrade
                    FROM CodeName INNER JOIN CodeName92 ON CodeName.Spec = CodeName92.Spec
                    ORDER BY CodeName.Ref;
                    // *********************
                    */
                    tablesFailed += 1;
                    Log.i(TAG, "csv file FAILED: " + tbl);
                    break;
                case "Region":
                    try {
                        String txt = null;
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Area, FullName, isSelected\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            String Area = rs.getString(0);  // NE
                            String FullName = rs.getString(1);  // Not Evaluated
                            int isSelected = rs.getInt(2);  // 1
                            txt += Area + "," + FullName + "," + isSelected + "\n";
                            rs.moveToNext();
                        } // next i
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
                case "Version":  // this is IOC version -- one row one column
                    try {
                        String txt = null;
                        File csvfile = new File(destinationDefine, tbl + ".csv");
                        FileWriter writer = new FileWriter(csvfile);
                        txt = "Num\n";
                        qry = "Select " + txt + " from " + tbl;
                        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
                        rs.moveToFirst();
                        int cntr = rs.getCount();
                        for (int i = 0; i < cntr; i++) {
                            int Num = rs.getInt(0);  // 1
                            txt += Num + "\n";
                            rs.moveToNext();
                        } // next i
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
                default:
                    Log.i(TAG, "table not found: " + tbl);
                    tablesFailed += 1;
            }
        }
        status2 = "tablesCopied: " + tablesCopied + " tablesFailed: " + tablesFailed;
        return status2;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backup_button: {
                status1 = exportFolders();
                Log.i(TAG, "Export Folders status: " + status1);
                result1.setText("Export status: " + status1);
                status2 = exportCSV();
                Log.i(TAG, "csv save status: " + status2);
                result2.setText("CSV Save Result: " + status2);
            }
        }

    }

    /*
    public class BackupSongData extends SQLiteOpenHelper {
        private static final String TAG = "SongData";

        public BackupSongData(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db){

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }
     */
}