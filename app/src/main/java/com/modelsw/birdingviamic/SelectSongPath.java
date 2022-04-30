package com.modelsw.birdingviamic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;


import android.content.ContentValues;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.EditText;
import android.widget.Toast;

// THIS CLASS MOVES SONG FILES FROM EXTERNAL LOCATIONS Song folder TO BirdingViaMic/files/Song folder
// AND Filter.csv FILES FROM EXTERNAL LOCATIONS Define folder TO IN BirdingViaMic/files/Define folder
// IT DOES NOT LOAD ANY FILES INTO THE DATABASE.
public class SelectSongPath extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "SelectSongPath";
    private EditText customPath;
    private Button loadNow;
    private RadioButton path1;
    private RadioButton path2;
    private RadioButton path4;
    private RadioButton path5;
    private RadioButton path6;
    private RadioButton path7;
    private RadioButton path99;
    private RadioGroup pathGroup;
    private CharSequence path1Label;
    private CharSequence path2Label;
    private CharSequence path4Label;
    private CharSequence path5Label;
    private CharSequence path6Label;
    private CharSequence path7Label;
    private CharSequence path99Label;
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.song_path);
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


        pathGroup = (RadioGroup) findViewById(R.id.path_group);
        path1 = (RadioButton) findViewById(R.id.path_1);
        path1.setOnClickListener(this);
        path1Label = path1.getText();
        path2 = (RadioButton) findViewById(R.id.path_2);
        path2.setOnClickListener(this);
        path2Label = path2.getText();
        path4 = (RadioButton) findViewById(R.id.path_4);
        path4.setOnClickListener(this);
        path4Label = path4.getText();
        path5 = (RadioButton) findViewById(R.id.path_5);
        path5.setOnClickListener(this);
        path5Label = path5.getText();
        path6 = (RadioButton) findViewById(R.id.path_6);
        path6.setOnClickListener(this);
        path6Label = path6.getText();
        path7 = (RadioButton) findViewById(R.id.path_7);
        path7.setOnClickListener(this);
        path7Label = path7.getText();
        path99 = (RadioButton) findViewById(R.id.path_99);
        path99.setOnClickListener(this);
        path99Label = path99.getText();
        customPath = (EditText) findViewById(R.id.custom_path);
        customPath.setOnClickListener(this);
        customPath.setText(Main.customPathLocation);
        loadNow = (Button) findViewById(R.id.load_now_button);
        loadNow.setOnClickListener(this);
        Log.d(TAG, "path:" + Main.path + " customPathLocation:" + Main.customPathLocation);
        // move the data from assets to definepath and songpath
        String packageName = getPackageName(); // com.modelsw.birdingviamic
        /*
        switch (Main.path) {
        	case 1: path1.setChecked(true); break;
        	case 2: path2.setChecked(true); break;
            case 4: path4.setChecked(true); break;
            case 5: path5.setChecked(true); break;
            case 6: path6.setChecked(true); break;
            case 7: path7.setChecked(true); break;
            case 99: path99.setChecked(true); break;
        }
        if (path1.isChecked() == true) {
            loadNow.setEnabled(false);
        }
        */
    }

    public void onClick(View v) {
        if (path1.isChecked() == false) {
            loadNow.setEnabled(true);
        }
        switch (v.getId()) {
            case R.id.path_1:
                Main.songpath = Main.songPathDir.toString() + "/";
                Main.sharedDefine = null;
                Main.path = 1;
                Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
                break;
            case R.id.path_2:
                Main.songpath = Main.environment + "/" + getResources().getString(R.string.path2_location) + "/";
                Main.sharedDefine = null;
                Main.path = 2;
                Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
                break;
            case R.id.path_4:
                Main.songpath = Main.environment + "/" + getResources().getString(R.string.path4_location) + "/";
                Main.sharedDefine = Main.environment + "/" + getResources().getString(R.string.path4_define) + "/";
                Main.path = 4;
                Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
                break;
            case R.id.path_5:
                Main.songpath = Main.environment + "/" + getResources().getString(R.string.path5_location) + "/";
                Main.sharedDefine = Main.environment + "/" + getResources().getString(R.string.path5_define) + "/";
                Main.path = 5;
                Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
                break;
            case R.id.path_6:
                Main.songpath = Main.environment + "/" + getResources().getString(R.string.path6_location) + "/";
                Main.sharedDefine = Main.environment + "/" + getResources().getString(R.string.path6_define) + "/";
                Main.path = 6;
                Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
                break;
            case R.id.path_7:
                Main.songpath = Main.environment + "/" + getResources().getString(R.string.path7_location) + "/";
                Main.sharedDefine = Main.environment + "/" + getResources().getString(R.string.path7_define) + "/";
                Main.path = 7;
                Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
                break;
            case R.id.path_99:
                // this is stored in the database BirdSongs.db in the table SongPath: default shown as: MyCustomPath...
                Main.customPathLocation = customPath.getText().toString();
                if (Main.customPathLocation.substring(0, 1) != "/") {
                    Main.customPathLocation = "/" + Main.customPathLocation;
                }
                Main.songpath = Main.environment + "/" + Main.customPathLocation + "/";
                Main.sharedDefine = null;
                Main.path = 99;
                Log.d(TAG, "onClick path:" + Main.path + " songpath:" + Main.songpath );
                break;
            case R.id.load_now_button:
                if (path1.isChecked() == false) {
                    checkForNewFiles(); // <-- ***** MOVE THEM NOW *****
                } else {
                    String msg = "Please select an external path";
                    Log.d(TAG, msg);
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        Main.path = 1; // don't confuse the issue
        Main.songpath = Main.songPathDir.toString() + "/";
        Main.customPathLocation = customPath.getText().toString();
        Log.d(TAG, "onPause saveTheSongPath path:" + Main.path + " customPathLocation:" + Main.customPathLocation);
        Main.db.beginTransaction();
        String qry = "UPDATE SongPath SET Path = " + Main.path + ", CustomPath = '" + Main.customPathLocation + "'";
        Main.db.execSQL(qry);
        Main.db.setTransactionSuccessful();
        Main.db.endTransaction();
    }

    void checkForNewFiles() {  // moves them out of selected path to local/Songs/ directory.
        // and filter out of selected path Define to local/Define/ directory
        // note: this is only called if path > 1
        // after moving the files, path is re-set to 1
        // songpath is selected path which is > 1 -- could be null if invalid path from custom.
        if (Main.songpath == null || Main.songdata == null) {
            finish();
            return;
        }

        // Move the filter.csv if it exists
        if (Main.sharedDefine != null) {
            int pathLen = Main.sharedDefine.length();
            Log.d(TAG, "* checkForNewFiles() sharedDefine:" + Main.sharedDefine);
            File dirDef = new File(Main.sharedDefine);
            Log.d(TAG, "onCreate: dirDef:" + dirDef);
            File[] defineFile = dirDef.listFiles();
            int defFileLen = 0;
            if (defineFile != null) {
                defFileLen = defineFile.length;
                Log.d(TAG, "* checkForNewDefineFiles() defFileLen:" + defFileLen);
                //String[] defines = new String[defFileLen]; // names from the folder
                for (int i = 0; i < defFileLen; i++) {
                    //defines[i] = defineFile[i].toString().substring(pathLen);
                    //String nam = defines[i];
                    String nam = defineFile[i].toString().substring(pathLen);
                    if (nam.equals("filter.csv")) {
                        Boolean success = defineFile[i].renameTo(new File(Main.definepath + nam));
                        Log.d(TAG, " did i move file:" + nam + " ?:" + success);
                    }
                }
            }
        }

        // localPath is songPath = 1 from getExternalFilesDir("Song");
        String localPath = Main.songPathDir.toString() + "/";
        Log.d(TAG, "* checkForNewFiles() path:" + Main.path + " songpath:" + Main.songpath);
        int pathLen = Main.songpath.length();
        File dir = new File(Main.songpath);
        Log.d(TAG, "onCreate: dir:" + dir);
        Main.songFile = dir.listFiles();
        String nums = "0123456789";
        String ch = "";
        int cntr = 0;
        int songsFileLen = 0;
        if (Main.songFile == null) {
            Log.d(TAG, "* checkForNewFiles() songFile is null -- closing");
            String msg = "invalid path:" + Main.songpath;
            Log.d(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return;
        } else {
            songsFileLen = Main.songFile.length;
            Log.d(TAG, "* checkForNewFiles() songFileLength:" + songsFileLen);        // crash if songPath is null
            Main.songs = new String[songsFileLen];
            for (int i = 0; i < songsFileLen; i++) {
                Main.songs[i] = Main.songFile[i].toString().substring(pathLen);
                //  01234567890123456789012345678901234
                String nam = Main.songs[i];         // "16 White-crowned Sparrow Song 1.mp3"
                //Log.d(TAG, "* checkForNewFiles() songs[" + i + "] " + nam );
                // this section renames the file name to moves the bird name to the front of the name
                Boolean isAlbumNumber = false;
                Boolean isStartsWithXC = false;
                int chLoc = nam.indexOf(' '); // 2
                if (chLoc > 0 && chLoc < 5) {
                    ch = nam.substring(0, chLoc);
                    //Log.d(TAG, "ch:" + ch);

                    if (nums.contains(ch.substring(0, 1)) == true && nums.contains(nam.substring(chLoc + 1, chLoc + 2)) == false) {
                        isAlbumNumber = true;
                        // find the first beyond the name -- its a number followed by a space followed by a letter
                        int songLoc = nam.indexOf(" Song"); // 24
                        int callLoc = nam.indexOf(" Call"); // -1
                        int drumLoc = nam.indexOf(" Drum"); // -1
                        int extLoc = nam.length() - 4;
                        int afterName = Math.max(chLoc, extLoc); // 31
                        if (afterName > chLoc) {
                            if (songLoc > chLoc) {
                                afterName = Math.min(songLoc, afterName); // 24
                            }
                            if (callLoc > chLoc) {
                                afterName = Math.min(callLoc, afterName);
                            }
                            if (drumLoc > chLoc) {
                                afterName = Math.min(drumLoc, afterName);
                            }
                            if (afterName == extLoc) { // just the name.ext
                                Main.newName = nam.substring(chLoc + 1, afterName) + ch + nam.substring(afterName);
                            } else {  // additional words between
                                Main.newName = nam.substring(chLoc + 1, afterName) + ch + nam.substring(afterName + 1);
                            }
                            Main.songFile[i].renameTo(new File(localPath + Main.newName));
                            cntr++;
                        }
                    }

                    // this section renames XC files by moving XCxxxxx after the bird name and before the .ext
                } else if (nam.substring(0, 2).equals("XC")) {    // XC179353-Northern Mockingbird 140414-002.mp3
                    chLoc = nam.indexOf("-");                     // 01234567890123456789012345678901234567890123
                    isStartsWithXC = true;
                    if (chLoc > 3) {
                        ch = nam.substring(0, chLoc);  // XC179353
                        int extLoc = nam.length() - 4;
                        if (extLoc > chLoc) {  // check for .mp3 file
                            // Northern Mockingbird 140414-002-XC179353.mp3
                            Main.newName = nam.substring(chLoc + 1, extLoc) + "-" + ch + nam.substring(extLoc);
                            Main.songFile[i].renameTo(new File(localPath + Main.newName));
                            cntr++;
                        }
                    }
                }

                // this section load songs that start with names.
                if ((isAlbumNumber == false) && (isStartsWithXC == false)) { // for Birding Via Mic_XX external apps or full name Download
                    int extLoc = nam.length() - 4;
                    String ext = nam.substring(extLoc);
                    if (ext.equalsIgnoreCase(".mp3") || ext.equalsIgnoreCase(".m4a") ||
                            ext.equalsIgnoreCase(".wav") || ext.equalsIgnoreCase(".ogg")) {
                        Main.newName = nam; // it's a song file load it like it is.
                        Boolean success = Main.songFile[i].renameTo(new File(localPath + Main.newName));
                        Log.d(TAG, "newName: " + Main.newName + " transferred status: " + success);
                        cntr++;
                    }

                }
            } // finished moving files -- THEY SHOULD ALL BE IN THE BirdingViaMic/files/Song folder
            // and the filter.csv SHOULD BE IN THE BirdingViaMic/files/Define folder
        }

        Main.db.beginTransaction();
        Main.path = 1;
        pathGroup.check(R.id.path_1);
        Main.sharedDefine = null;
        Main.songpath = Main.songPathDir.toString() + "/";
        String qry = "UPDATE SongPath SET Path = " + Main.path;
        Main.db.execSQL(qry);
        Main.db.setTransactionSuccessful();
        Main.db.endTransaction();
        String msg = "Files in Download:" + songsFileLen + " Files Loaded:" + cntr;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.d(TAG, "* return from checkForNewFiles() path:" + Main.path + " songpath:" + Main.songpath);
    }


}
