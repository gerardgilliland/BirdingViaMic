package com.modelsw.birdingviamic;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import static com.modelsw.birdingviamic.Main.db;
import static com.modelsw.birdingviamic.Main.definePathDir;
import static com.modelsw.birdingviamic.Main.definepath;

// this is importing csv files into the new database tables and converting Ref and updating those tables
public class ConvertTables {
    private static final String TAG = "ConvertTables";
    // UpgradeSpecies() loads the csv files from the "old" database
    // ConvertTables() creates a RefUpgrade table with the old version and new IOC_Version.
    // Applies the new Ref for the matching Spec.
    // updates the tables that use Ref: CodeName, DefineDetail, DefineTotals, Identify, SongList
    // note the old database will only copy Ref and Spec into the table OldRef
    // tables that could be user modified: Filter, LastKnown, Location, SongPath
    // tables that user could select options from: Options, RedList, Region
    // I use the old database to update all tables -- and thus reflect the users choices
    // a look at the American Robin:
    // Saved in /Download/Define/BirdSongs.db IOC_Version 92  Ref = 27000
    // the DefineTotals.csv file has been stored in this birdingviamic/Define folder and imported below
    // CodeName (the species table) in this birdingviamic/Define folder has IOC_Version 111  Ref = 27020
    // RefUpgrade table built in this birdingviamic/Define/BirdSongs.db contains: OldRef = 27000; NewRef = 27020
    // in Upgrade (below) DefineTotals table is truncated and loaded from the DefineTotals.csv file
    // Ref in the now loaded DefineTotals table has Ref = 27000;
    // the above Ref is updated with a temporary Ref = 127020;
    // the temporary Ref is updated  with 27020;
    // DefineTotals from the old database is now in the new database with the new Ref = 27020

    private Context context;
    Cursor rs;
    String qry;
    int oldRef; // the existing Ref in the tables to be replaced by a tempRef which is NewRef biased by 100K
    int newRef; // the New Ref replacing the tempRef that has been biased by 100K
    int csvdeletesuccess = 0;
    int csvdeletefail = 0;
    int bias100k = 100000; // move all the Ref to greater than 100000
    Boolean songlistExists = false;
    Boolean definetotalsExists = false;
    Boolean definedetailExists = false;
    Boolean identifyExists = false;

    // Called when the activity is first created.
    public void onCreate(Bundle savedInstanceState) {
        if (Main.songpath == null || Main.songdata == null) {
            return;
        }
        init();
    }

    ConvertTables(UpgradeDialog context) { // constructor
        Log.d(TAG, "constructor");
        init();
    }

    private void init() {
        Log.d(TAG, "init()");
        //db = Main.songdata.getWritableDatabase();
        upgrade(); // convert the tables
        deleteCsvFiles();  // this is after upgradeSpecies runs and the above upgrade() runs (below)
        // delete two tables -- create new tables when needed that reflect the Ref and Spec for a specific version
        qry = "DROP TABLE IF EXISTS RefUpgrade";
        db.execSQL(qry);
        qry = "DROP TABLE IF EXISTS OldRef";
        db.execSQL(qry);
        Log.d(TAG, "delete CSV files -- success:" + csvdeletesuccess + " fail:" + csvdeletefail);
    } // init()

    private void upgrade() {
        Log.d(TAG, "upgrade()");
        // load the old codename table OldRef.csv into OldRef table
        // use it and CodeName from this new database to build a RefUpgrade table.

        Log.d(TAG, "load OldRef.csv into a table");
        qry = "CREATE TABLE IF NOT EXISTS OldRef (Ref INTEGER, Spec TEXT)";
        db.execSQL(qry);
        db.execSQL("DELETE FROM OldRef");
        Scanner oldref;
        try {
            oldref = new Scanner(new BufferedReader(new FileReader(definepath + "OldRef.csv")));
            // it will crash out of this function under "catch" below if file is missing
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = oldref.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Ref")) { // thus I can handle file headers or no file headers
                            int ref = Integer.parseInt(tokens[0]);
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("Ref", ref);
                            val.put("Spec", tokens[1]);
                            db.insert("OldRef", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading OldRef:" + e);
            } finally {
                oldref.close();
                Log.d(TAG, "upgradeSpecies closed OldRef.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- OldRef.csv does NOT Exist:" + e);
            return;
        }

        Log.d(TAG, "load OldRef into table RefUpgrade");
        qry = "CREATE TABLE IF NOT EXISTS RefUpgrade (OldRef INTEGER, NewRef INTEGER)";
        db.execSQL(qry);
        db.execSQL("DELETE FROM RefUpgrade");
        qry = "INSERT INTO RefUpgrade (OldRef, NewRef) " +
                "SELECT OldRef.Ref AS OldRef, CodeName.Ref AS NewRef " +
                "FROM CodeName INNER JOIN OldRef ON CodeName.Spec = OldRef.Spec " +
                "ORDER BY OldRef.Ref";
        db.execSQL(qry);
        Log.d(TAG, "RefUpgrade populated");

        // DefineDetail
        Log.d(TAG, "upgrade() Update DefineDetail");
        Scanner definedetail;
        try {
            definedetail = new Scanner(new BufferedReader(new FileReader(definepath + "DefineDetail.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load DefineDetail.csv into a table");
            db.execSQL("DELETE FROM DefineDetail");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = definedetail.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Ref")) { // thus I can handle file headers or no file headers
                            int ref = Integer.parseInt(tokens[0]);
                            if (ref > 0 && ref < 39997) {
                                db.beginTransaction();
                                val = new ContentValues();
                                val.put("Ref", ref);  // this is the old ref
                                val.put("Inx", tokens[1]);
                                val.put("Seg", tokens[2]);
                                val.put("Phrase", tokens[3]);
                                val.put("Record", tokens[4]);
                                val.put("Freq", tokens[5]);
                                val.put("Voiced", tokens[6]);
                                val.put("Energy", tokens[7]);
                                val.put("Distance", tokens[8]);
                                val.put("Quality", tokens[9]);
                                val.put("Samp", tokens[10]);
                                db.insert("DefineDetail", null, val);
                                db.setTransactionSuccessful();
                                db.endTransaction();
                                val.clear();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading DefineDetail:" + e);
            } finally {
                definedetail.close();
                definedetailExists = true;
                Log.d(TAG, "upgradeSpecies closed DefineDetail.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- DefineDetail.csv does NOT Exist:" + e);
        }
        if(definedetailExists == true) {
            qry = "SELECT DefineDetail.Ref, RefUpgrade.NewRef" +
                    " FROM DefineDetail JOIN RefUpgrade ON DefineDetail.Ref = RefUpgrade.OldRef";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            int cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies Modify DefineDetail cntr:" + cntr);
            // move the new Ref out of the way so it doesn't conflict with different old refs
            int tempRef = 0;
            for (int i = 0; i < cntr; i++) {
                oldRef = rs.getInt(0);  // the Ref from the table to be replaced by ...
                newRef = rs.getInt(1) + bias100k; // ... NewRef which will be temporarily biased out of the way.
                Main.db.beginTransaction();
                qry = "UPDATE DefineDetail" +
                        " SET Ref = " + newRef + " WHERE Ref = " + oldRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
            // now remove the bias
            qry = "SELECT Ref FROM DefineDetail WHERE Ref > 100000";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies SecondPass DefineDetail cntr:" + cntr);
            for (int i = 0; i < cntr; i++) {
                tempRef = rs.getInt(0);
                newRef = tempRef - bias100k;
                Main.db.beginTransaction();
                qry = "UPDATE DefineDetail" +
                        " SET Ref = " + newRef +
                        " WHERE Ref = " + tempRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
        } // definedetailExists

        // DefineTotals
        Log.d(TAG, "upgrade() Update DefineTotals");
        Scanner definetotals;
        try {
            definetotals = new Scanner(new BufferedReader(new FileReader(definepath + "DefineTotals.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load DefineTotals.csv into a table");
            db.execSQL("DELETE FROM DefineTotals");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = definetotals.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Ref")) { // thus I can handle file headers or no file headers
                            int ref = Integer.parseInt(tokens[0]);
                            if (ref > 0 && ref < 39997) {
                                db.beginTransaction();
                                val = new ContentValues();
                                val.put("Ref", ref);  // this is the old ref
                                val.put("Inx", tokens[1]);
                                val.put("Seg", tokens[2]);
                                val.put("Phrase", tokens[3]);
                                val.put("Silence", tokens[4]);
                                val.put("Records", tokens[5]);
                                val.put("FreqMean", tokens[6]);
                                val.put("FreqStdDev", tokens[7]);
                                val.put("VoicedMean", tokens[8]);
                                val.put("VoicedStdDev", tokens[9]);
                                val.put("EnergyMean", tokens[10]);
                                val.put("EnergyStdDev", tokens[11]);
                                val.put("DistMean", tokens[12]);
                                val.put("DistStdDev", tokens[13]);
                                val.put("QualityMean", tokens[14]);
                                val.put("QualityStdDev", tokens[15]);
                                val.put("SampMean", tokens[16]);
                                val.put("SampStdDev", tokens[17]);
                                val.put("Slope", tokens[18]);
                                val.put("SilPhrRatio", tokens[19]);
                                db.insert("DefineTotals", null, val);
                                db.setTransactionSuccessful();
                                db.endTransaction();
                                val.clear();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading DefineTotals:" + e);
            } finally {
                definetotals.close();
                definetotalsExists = true;
                Log.d(TAG, "upgradeSpecies closed DefineTotals.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- DefineTotals.csv does NOT Exist:" + e);
        }
        if(definetotalsExists == true) {
            qry = "SELECT DefineTotals.Ref, RefUpgrade.NewRef" +
                    " FROM DefineTotals JOIN RefUpgrade ON DefineTotals.Ref = RefUpgrade.OldRef";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            int cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies Modify DefineTotals cntr:" + cntr);
            int tempRef = 0;
            // move the new Ref out of the way so it doesn't conflict with different old refs
            for (int i = 0; i < cntr; i++) {
                oldRef = rs.getInt(0);  // the Ref from the table to be replaced by ...
                newRef = rs.getInt(1) + bias100k; // ... NewRef which will be temporarily biased out of the way.
                Main.db.beginTransaction();
                qry = "UPDATE DefineTotals" +
                        " SET Ref = " + newRef + " WHERE Ref = " + oldRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
            // now remove the bias
            qry = "SELECT Ref FROM DefineTotals WHERE Ref > 100000";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies SecondPass DefineTotals cntr:" + cntr);
            for (int i = 0; i < cntr; i++) {
                tempRef = rs.getInt(0);
                newRef = tempRef - bias100k;
                Main.db.beginTransaction();
                qry = "UPDATE DefineTotals" +
                        " SET Ref = " + newRef +
                        " WHERE Ref = " + tempRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
        } // definetotalsExists

        // Identify
        Log.d(TAG, "upgrade() Update Identify");
        Scanner identify;
        try {
            identify = new Scanner(new BufferedReader(new FileReader(definepath + "Identify.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load Identify.csv into a table");
            db.execSQL("DELETE FROM Identify");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = identify.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Ref")) { // thus I can handle file headers or no file headers
                            int ref = Integer.parseInt(tokens[0]);
                            if (ref > 0 && ref < 39997) {
                                db.beginTransaction();
                                val = new ContentValues();
                                val.put("Ref", ref);  // this is the old ref
                                val.put("Cntr", tokens[1]);
                                val.put("Criteria", tokens[2]);
                                db.insert("Identify", null, val);
                                db.setTransactionSuccessful();
                                db.endTransaction();
                                val.clear();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading Identify:" + e);
            } finally {
                identify.close();
                identifyExists = true;
                Log.d(TAG, "upgradeSpecies closed Identify.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- Identify.csv does NOT Exist:" + e);
        }
        if(identifyExists == true) {
            qry = "SELECT Identify.Ref, RefUpgrade.NewRef" +
                    " FROM Identify JOIN RefUpgrade ON Identify.Ref = RefUpgrade.OldRef";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            int cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies Modify Identify cntr:" + cntr);
            // move the new Ref out of the way so it doesn't conflict with different old refs
            int tempRef = 0;
            for (int i = 0; i < cntr; i++) {
                oldRef = rs.getInt(0);  // the Ref from the table to be replaced by ...
                newRef = rs.getInt(1) + bias100k; // ... NewRef which will be temporarily biased out of the way.
                Main.db.beginTransaction();
                qry = "UPDATE Identify" +
                        " SET Ref = " + newRef + " WHERE Ref = " + oldRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
            // now remove the bias
            qry = "SELECT Ref FROM Identify WHERE Ref > 100000";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies SecondPass Identify cntr:" + cntr);
            for (int i = 0; i < cntr; i++) {
                tempRef = rs.getInt(0);
                newRef = tempRef - bias100k;
                Main.db.beginTransaction();
                qry = "UPDATE Identify" +
                        " SET Ref = " + newRef +
                        " WHERE Ref = " + tempRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
        } // identifyExists

        // SongList
        Log.d(TAG, "upgrade() Update the SongList");
        Scanner songlist;
        try {
            songlist = new Scanner(new BufferedReader(new FileReader(definepath + "SongList.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load SongList.csv into a table");
            db.execSQL("DELETE FROM SongList");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = songlist.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Ref")) { // thus I can handle file headers or no file headers
                            int ref = Integer.parseInt(tokens[0]);
                            if (ref > 0 && ref < 39997) {
                                db.beginTransaction();
                                val = new ContentValues();
                                val.put("Ref", ref);  // this is the old ref
                                val.put("Inx", tokens[1]);
                                val.put("Seg", tokens[2]);
                                val.put("Path", tokens[3]);
                                val.put("FileName", tokens[4]);
                                val.put("Start", tokens[5]);
                                val.put("Stop", tokens[6]);
                                val.put("Identified", tokens[7]);
                                val.put("Defined", tokens[8]);
                                val.put("AutoFilter", tokens[9]);
                                val.put("Enhanced", tokens[10]);
                                val.put("Smoothing", tokens[11]);
                                val.put("SourceMic", tokens[12]);
                                val.put("SampleRate", tokens[13]);
                                val.put("AudioSource", tokens[14]);
                                val.put("LowFreqCutoff", tokens[15]);
                                val.put("HighFreqCutoff", tokens[16]);
                                val.put("FilterStart", tokens[17]);
                                val.put("FilterStop", tokens[18]);
                                val.put("Stereo", tokens[19]);
                                db.insert("SongList", null, val);
                                db.setTransactionSuccessful();
                                db.endTransaction();
                                val.clear();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading SongList:" + e);
            } finally {
                songlist.close();
                songlistExists = true;
                Log.d(TAG, "upgradeSpecies closed SongList.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- SongList.csv does NOT Exist:" + e);
        }
        if(songlistExists == true) {
            qry = "SELECT SongList.Ref, RefUpgrade.NewRef" +
                    " FROM SongList JOIN RefUpgrade ON SongList.Ref = RefUpgrade.OldRef";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            int cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies Modify SongList cntr:" + cntr);
            // move the new Ref out of the way so it doesn't conflict with different old refs
            int tempRef = 0;
            for (int i = 0; i < cntr; i++) {
                oldRef = rs.getInt(0);  // the Ref from the table to be replaced by ...
                newRef = rs.getInt(1) + bias100k; // ... NewRef which will be temporarily biased out of the way.
                Main.db.beginTransaction();
                qry = "UPDATE SongList" +
                        " SET Ref = " + newRef + " WHERE Ref = " + oldRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
            // now remove the bias
            qry = "SELECT Ref FROM SongList WHERE Ref > 100000";
            rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            rs.moveToFirst();
            cntr = rs.getCount();
            Log.d(TAG, "upgradeSpecies SecondPass SongList cntr:" + cntr);
            for (int i = 0; i < cntr; i++) {
                tempRef = rs.getInt(0);
                newRef = tempRef - bias100k;
                Main.db.beginTransaction();
                qry = "UPDATE SongList" +
                        " SET Ref = " + newRef +
                        " WHERE Ref = " + tempRef;
                Main.db.execSQL(qry);
                Main.db.setTransactionSuccessful();
                Main.db.endTransaction();
                rs.moveToNext();
            } // next i
            rs.close();
        } // songlistExists

        // Filter
        Log.d(TAG, "upgrade() Update Filter");
        Scanner filter;
        try {
            filter = new Scanner(new BufferedReader(new FileReader(definepath + "Filter.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load Filter.csv into a table");
            db.execSQL("DELETE FROM Filter");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = filter.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("XcName")) { // thus I can handle file headers or no file headers
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("XcName", tokens[0]);
                            val.put("FilterType", tokens[1]);
                            val.put("FilterVal", tokens[2]);
                            db.insert("Filter", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading Filter:" + e);
            } finally {
                filter.close();
                Log.d(TAG, "upgradeSpecies closed Filter.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- Filter.csv does NOT Exist:" + e);
        }

        // LastKnown
        Log.d(TAG, "upgrade() Update Filter");
        Scanner lastknown;
        try {
            lastknown = new Scanner(new BufferedReader(new FileReader(definepath + "LastKnown.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load LastKnown.csv into a table");
            db.execSQL("DELETE FROM LastKnown");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = lastknown.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("FileName")) { // thus I can handle file headers or no file headers
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("FileName", tokens[0]);
                            val.put("LastDate", tokens[1]);
                            val.put("Activity", tokens[2]);
                            db.insert("LastKnown", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading LastKnown:" + e);
            } finally {
                lastknown.close();
                Log.d(TAG, "upgradeSpecies closed LastKnown.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- LastKnown.csv does NOT Exist:" + e);
        }

        // Location
        Log.d(TAG, "upgrade() Update Filter");
        Scanner location;
        try {
            location = new Scanner(new BufferedReader(new FileReader(definepath + "Location.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load Location.csv into a table");
            db.execSQL("DELETE FROM Location");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = location.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Name")) { // thus I can handle file headers or no file headers
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("Name", tokens[0]);
                            val.put("Value", tokens[1]);
                            db.insert("Location", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading Location:" + e);
            } finally {
                location.close();
                Log.d(TAG, "upgradeSpecies closed Location.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- Location.csv does NOT Exist:" + e);
        }

        // SongPath
        Log.d(TAG, "upgrade() Update SongPath");
        Scanner songpath;
        try {
            songpath = new Scanner(new BufferedReader(new FileReader(definepath + "SongPath.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load SongPath.csv into a table");
            db.execSQL("DELETE FROM SongPath");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = songpath.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Path")) { // thus I can handle file headers or no file headers
                            int Path = Integer.parseInt(tokens[0]);
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("Path", Path);
                            val.put("CustomPath", tokens[1]);
                            db.insert("SongPath", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading SongPath:" + e);
            } finally {
                songpath.close();
                Log.d(TAG, "upgradeSpecies closed SongPath.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- SongPath.csv does NOT Exist:" + e);
        }

        // BirdWebSites
        Log.d(TAG, "upgrade() Update BirdWebSites");
        Scanner birdwebsites;
        try {
            birdwebsites = new Scanner(new BufferedReader(new FileReader(definepath + "BirdWebSites.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load BirdWebSites.csv into a table");
            db.execSQL("DELETE FROM BirdWebSites");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = birdwebsites.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("id")) { // thus I can handle file headers or no file headers
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("id", tokens[0]);
                            val.put("WebSiteText", tokens[1]);
                            val.put("WebSiteLink", tokens[2]);
                            db.insert("BirdWebSites", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading BirdWebSites:" + e);
            } finally {
                birdwebsites.close();
                Log.d(TAG, "upgradeSpecies closed BirdWebSites.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- BirdWebSites.csv does NOT Exist:" + e);
        }

        // Options
        Log.d(TAG, "upgrade() Update Options");
        Scanner options;
        try {
            options = new Scanner(new BufferedReader(new FileReader(definepath + "Options.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load Options.csv into a table");
            db.execSQL("DELETE FROM Options");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = options.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Name")) { // thus I can handle file headers or no file headers
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("Name", tokens[0]);
                            val.put("Value", tokens[1]);
                            db.insert("Options", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading Options:" + e);
            } finally {
                options.close();
                Log.d(TAG, "upgradeSpecies closed Options.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- Options.csv does NOT Exist:" + e);
        }

        // RedList
        Log.d(TAG, "upgrade() Update RedList");
        Scanner redlist;
        try {
            redlist = new Scanner(new BufferedReader(new FileReader(definepath + "RedList.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load RedList.csv into a table");
            db.execSQL("DELETE FROM RedList");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = redlist.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("ix")) { // thus I can handle file headers or no file headers
                            int ix = Integer.parseInt(tokens[0]);
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("ix", ix);
                            val.put("type", tokens[1]);
                            val.put("FullName", tokens[2]);
                            val.put("isSelected", tokens[3]);
                            db.insert("RedList", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading RedList:" + e);
            } finally {
                redlist.close();
                Log.d(TAG, "upgradeSpecies closed RedList.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- RedList.csv does NOT Exist:" + e);
        }

        // Region
        Log.d(TAG, "upgrade() Update Region");
        Scanner region;
        try {
            region = new Scanner(new BufferedReader(new FileReader(definepath + "Region.csv")));
            // it will crash out of this function under "catch" below if file is missing
            Log.d(TAG, "load Region.csv into a table");
            db.execSQL("DELETE FROM Region");
            ContentValues val = new ContentValues();
            try {
                String line = "";
                while ((line = region.nextLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        if (!tokens[0].equals("Area")) { // thus I can handle file headers or no file headers
                            db.beginTransaction();
                            val = new ContentValues();
                            val.put("Area", tokens[0]);
                            val.put("FullName", tokens[1]);
                            val.put("isSelected", tokens[2]);
                            db.insert("Region", null, val);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            val.clear();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "internal error loading Region:" + e);
            } finally {
                region.close();
                Log.d(TAG, "upgradeSpecies closed Region.csv");
            }
        } catch (Exception e) {
            // the files don't exist leave quitely.
            Log.d(TAG, "Exit -- Region.csv does NOT Exist:" + e);
        }
    } // upgrade()

    private void deleteCsvFiles() {
        String[] csvfiles = new String[] { "OldRef", "DefineDetail", "DefineTotals", "Identify", "SongList",
                "BirdWebSites", "Filter", "LastKnown", "Location", "Options", "SongPath",
                "RedList", "Region"};
        for (int t = 0; t < csvfiles.length; t++) {
            String tbl = csvfiles[t];
            File target = new File(definepath + tbl + ".csv");
            boolean result = target.delete();
            if (result == true) {
                csvdeletesuccess += 1;
            } else {
                csvdeletefail +=1;
            }
            Log.d(TAG, "deleting: " + target + " result: " + result );
        }
    }
} // ConvertTables

