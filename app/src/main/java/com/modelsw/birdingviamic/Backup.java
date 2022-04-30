package com.modelsw.birdingviamic;

// https://stackoverflow.com/questions/29867121/how-to-copy-a-file-to-another-directory-programmatically

import android.annotation.SuppressLint;
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
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import static com.modelsw.birdingviamic.Main.tempDatabaseName;
import static com.modelsw.birdingviamic.Main.tempsongdata;
import static com.modelsw.birdingviamic.Main.definepath;
import static com.modelsw.birdingviamic.Main.tempdefinepath;
import static com.modelsw.birdingviamic.Main.versionExist;
import static com.modelsw.birdingviamic.Main.IOC_Version;

// this is called from the Main screen "Backup" button
// it will copy the running BirdingViaMic Define and Song Folders to the Android/Download folder.
// It does not ask any questions or require any version control.
// there is a warning on the Backup Screen saying:
// it will write over any Define and Song Folders that exist in the Android/Download folder.

public class Backup extends AppCompatActivity implements android.view.View.OnClickListener {
    private static final String TAG = "Backup";
    Toolbar toolbar;
    private static Button backup;
    private static Button restore;
    int bias100k = 100000; // move all the Ref to greater than 100000
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
    private static EditText backupResults;
    private static EditText restoreResults;
    private static int filesCopied = 0;  // used for backup and restore
    private static int filesFailed = 0;  // used for backup and restore

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

        init();
    }

    private void init() {
        filesCopied = 0;
        filesFailed = 0;
        backup = (Button) findViewById(R.id.backup_button);
        backup.setOnClickListener(this);
        restore = (Button) findViewById(R.id.restore_button);
        //result = (EditText) findViewById(R.id.result);
        backupResults = (EditText) findViewById(R.id.backup_results);
        backupResults.setText("Unknown");
        restoreResults = (EditText) findViewById(R.id.restore_results);
        restoreResults.setText("Unknown");
        databaseName = "BirdSongs.db";
        sourcepath = Environment.getExternalStorageDirectory().getAbsolutePath(); // storage/emulated/0/
        sourceDefine = getExternalFilesDir("Define").toString() + "/"; // birdingviamic/Define/
        sourceSong = getExternalFilesDir("Song").toString() + "/"; // birdingviamic/Song/
        destinationpath = "/storage/emulated/0/Download/"; //x Download/
        destinationDefine = destinationpath + "Define/";  //x Download/Define/
        destinationSong = destinationpath + "Song/";  //x Download/Song/
        File file = new File(destinationDefine);
        File file1 = new File(Main.tempDatabaseName);
        if(file.exists() && file1.exists()) {
            restore.setEnabled(true);
            restore.setOnClickListener(this);
            restore.setText("Restore");
        } else {
            restore.setEnabled(false);
            restore.setText("Restore is Disabled");
        }
        //destinationpath += databaseName;
        Log.i(TAG, "sourcepath: " + sourcepath);
        Log.i(TAG, "sourceDefine: " + sourceDefine); //x
        Log.i(TAG, "sourceSong: " + sourceSong); //x
        Log.i(TAG, "destinationpath: " + destinationpath);  //x
        Log.i(TAG, "destinationDefine: " + destinationDefine); //x
        Log.i(TAG, "destinationSong: " + destinationSong);  //x
    }

    private String exportFolders() {
        filesCopied = 0;
        filesFailed = 0;
        Log.i(TAG, "here in exportFolders()");
        boolean status = false;
        File dir = null;
        Log.i(TAG, "make the Define directory");
        dir = new File(destinationDefine);
        status = dir.exists();
        if (status == false) {
            status = dir.mkdir();
        } else {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        Log.i(TAG, "go save the define files");
        saveAssets(sourceDefine, destinationDefine);
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
        }
        Log.i(TAG, "go save the song files");
        saveAssets(sourceSong, destinationSong);
        String backupStatus = "files backed up: " + filesCopied + " filesFailed: " + filesFailed;
        return backupStatus;
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
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "copyFile status: failed ");
            filesFailed += 1;
        }
    } // copyFile

    private String importFolders() {
        // I am using the exportFolders code and existing names and copying from /Download To /BirdingViaMic
        // So everything looks BACKWARDS.
        filesCopied = 0;
        filesFailed = 0;
        Log.i(TAG, "here in importFolders()");
        boolean status = false;
        File dir = null;
        Log.i(TAG, "make the Define directory");
        dir = new File(sourceDefine); // BirdingViaMic/Define
        status = dir.exists();
        if (status == false) {
            status = dir.mkdir();
        } else {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        Log.i(TAG, "save the define files");
        saveAssets(destinationDefine, sourceDefine); // from Download/Define to BirdingViaMic/Define
        Log.i(TAG, "make the Song directory");
        dir = new File(sourceSong); // BirdingViaMic/Songs
        status = dir.exists();
        if (status == false) {
            status = dir.mkdir();
        } else {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        Log.i(TAG, "save the song files");
        saveAssets(destinationSong, sourceSong); // from Download/Song to BirdingViaMic/Song
        String restoreStatus = "files restored: " + filesCopied + " filesFailed: " + filesFailed;
        return restoreStatus;
    }

    /*   there are no decisions for the user in Backup
     *   -- the entire Define Folder and entire database BirdSongs.db are transferred.
     *   and the entire Song folder is transferred.
     *	 the tables that use Ref: CodeName, DefineDetail, DefineTotals, Identify, SongList
     *	 the table that could be user modified: BirdWebSites, Filter, LastKnown, Location, Options, SongPath
     *	 the tables that I control: RedList, RefUpgrade, Region, Version
     *
     * I made the buttons Final and added return at the end of each case in OnClick -- that solved my problem.
        Backup shows:
        Files backed up: 46 Files failed 0
        Restore shows:
        Unknown
        Restore button states: Restore is Disabled
     */
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backup_button: {
                Log.i(TAG, "Backup clicked:");
                String backupStatus = exportFolders();
                Log.i(TAG, backupStatus);
                backupResults.setText(backupStatus);
                restoreResults.setText("Unknown");
                return;
            }
            case R.id.restore_button: {
                // if restore is clicked and versionExist EQUALS IOC_version -- all is well -- just file copy like backup in reverse
                // else if versionExist < IOC_Version or greater than 100000 then a databases exists that will have a different species number
                // so set the versionExists back to original and and IOC_Version to 0 and call Main.onResume which will call Main.init()
                // that will start the Upgrade Dialog, UpgradeSpecies, ConvertTables -- in either case the backup gets restored.
                // WARNING -- WHAT HAPPENS IF NO BACKUP EXISTS -- tempsongdata doesn't exist -- crash
                Log.i(TAG, "Restore clicked:");
                String msg = "Restore FAILS to open tempsongdata";
                Log.d(TAG, msg);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                /*
                Main.db = Main.songdata.getWritableDatabase();
                qry = "SELECT Num FROM Version";
                Cursor rs = Main.songdata.getWritableDatabase().rawQuery(qry, null);
                rs.moveToFirst();
                IOC_Version = rs.getInt(0); // should be 111
                rs.close();
                Main.db.close();
                tempsongdata = new TempSongData(this, Main.tempDatabaseName, null, Main.databaseVersion);
                //Main.tempdb = Main.tempsongdata.getWritableDatabase();
                String tempqry = "SELECT Num FROM Version";
                Cursor temprs = tempsongdata.getReadableDatabase().rawQuery(tempqry, null);
                temprs.moveToFirst();
                versionExist = temprs.getInt(0); // now it is not zero -- it should be the old one
                temprs.close();
                if (IOC_Version != versionExist) {
                    if (versionExist > bias100k) { // 100092
                        versionExist -= bias100k;   // 92
                        tempqry = "UPDATE Version SET Num = " + versionExist;
                        Main.tempdb.execSQL(tempqry);
                        Main.tempdb.close();
                    }
                    IOC_Version = 0;
                    Main.isRestoreOldDatabase = true; // Upgrade Dialog ...
                    finish(); // force Main.onResume() to init()
                } else {
                    String restoreStatus = importFolders();
                    Log.i(TAG, restoreStatus);
                    restoreResults.setText(restoreStatus);
                    backupResults.setText("Unknown");
                }
                */
                return;
            }
        }
    }

}