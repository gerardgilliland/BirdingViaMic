package com.modelsw.birdingviamic;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by Gerard on 6/15/2015.
 */
public class RedList extends AppCompatActivity implements android.view.View.OnClickListener {
    private static final String TAG = "RedList";

    private RegionAdapter adapter;
    private static String type;
    CheckBox cb0;
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    CheckBox cb4;
    CheckBox cb5;
    CheckBox cb6;
    CheckBox cb7;
    CheckBox cb8;
    CheckBox cb9;
    private static boolean[] chk;
    private static Context ctx;
    private String existingRegion;
    public static int existingRedlistId;
    public static int ix;
    public static boolean isRedlistSelected;
    private static int myRequest = 0;
    private static char q = 34;
    private String qry = "";
    public static String redlistCombined;
    public static int redlistLen = 0;
    private Cursor rs;  // I see cursor as RecordSet (rs)
    public static int selectedRegion; // index of the web site clicked on
    Toolbar toolbar;
    private Button update;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_list);

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


        buildList();
        findViewById(R.id.update_button).setOnClickListener(this);
        findViewById(R.id.redlist_check0).setOnClickListener(this);
        findViewById(R.id.redlist_check1).setOnClickListener(this);
        findViewById(R.id.redlist_check2).setOnClickListener(this);
        findViewById(R.id.redlist_check3).setOnClickListener(this);
        findViewById(R.id.redlist_check4).setOnClickListener(this);
        findViewById(R.id.redlist_check5).setOnClickListener(this);
        findViewById(R.id.redlist_check6).setOnClickListener(this);
        findViewById(R.id.redlist_check7).setOnClickListener(this);
        findViewById(R.id.redlist_check8).setOnClickListener(this);
        findViewById(R.id.redlist_check9).setOnClickListener(this);
        Main.db = Main.songdata.getWritableDatabase();

    }

    void buildList() {
        Log.d(TAG, "buildList read the Region table");
        qry = "SELECT ix, Type, FullName, isSelected from RedList ORDER BY ix";
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        redlistLen = rs.getCount();
        rs.moveToFirst();
        chk = new boolean[redlistLen];
        for (int i = 0; i<redlistLen; i++) {
            ix = rs.getInt(0);
            int isSelected = rs.getInt(3);
            if (isSelected == 0) {
                chk[i] = false;
            } else {
                chk[i] = true;
            }
            switch (ix) {
                case 0: {
                    cb0 = (CheckBox) findViewById(R.id.redlist_check0);
                    cb0.setChecked(chk[ix]);
                    break;
                }
                case 1: {
                    cb1 = (CheckBox) findViewById(R.id.redlist_check1);
                    cb1.setChecked(chk[ix]);
                    break;
                }
                case 2: {
                    cb2 = (CheckBox) findViewById(R.id.redlist_check2);
                    cb2.setChecked(chk[ix]);
                    break;
                }
                case 3: {
                    cb3 = (CheckBox) findViewById(R.id.redlist_check3);
                    cb3.setChecked(chk[ix]);
                    break;
                }
                case 4: {
                    cb4 = (CheckBox) findViewById(R.id.redlist_check4);
                    cb4.setChecked(chk[ix]);
                    break;
                }
                case 5: {
                    cb5 = (CheckBox) findViewById(R.id.redlist_check5);
                    cb5.setChecked(chk[ix]);
                    break;
                }
                case 6: {
                    cb6 = (CheckBox) findViewById(R.id.redlist_check6);
                    cb6.setChecked(chk[ix]);
                    break;
                }
                case 7: {
                    cb7 = (CheckBox) findViewById(R.id.redlist_check7);
                    cb7.setChecked(chk[ix]);
                    break;
                }
                case 8: {
                    cb8 = (CheckBox) findViewById(R.id.redlist_check8);
                    cb8.setChecked(chk[ix]);
                    break;
                }
                case 9: {
                    cb9 = (CheckBox) findViewById(R.id.redlist_check9);
                    cb9.setChecked(chk[ix]);
                    break;
                }

            }
            rs.moveToNext();
        }
        rs.close();
        Log.d(TAG, "loaded the redList len:" + redlistLen);
    } // buildList



    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redlist_check0: {
                chk[0] = cb0.isChecked();
                //Log.d(TAG, "chk[0]:" + chk[0]);
                break;
            }
            case R.id.redlist_check1: {
                chk[1] = cb1.isChecked();
                //Log.d(TAG, "chk[1]:" + chk[1]);
                break;
            }
            case R.id.redlist_check2: {
                chk[2] = cb2.isChecked();
                //Log.d(TAG, "chk[2]:" + chk[2]);
                break;
            }
            case R.id.redlist_check3: {
                chk[3] = cb3.isChecked();
                //Log.d(TAG, "chk[3]:" + chk[3]);
                break;
            }
            case R.id.redlist_check4: {
                chk[4] = cb4.isChecked();
                //Log.d(TAG, "chk[4]:" + chk[4]);
                break;
            }
            case R.id.redlist_check5: {
                chk[5] = cb5.isChecked();
                //Log.d(TAG, "chk[5]:" + chk[5]);
                break;
            }
            case R.id.redlist_check6: {
                chk[6] = cb6.isChecked();
                //Log.d(TAG, "chk[6]:" + chk[6]);
                break;
            }
            case R.id.redlist_check7: {
                chk[7] = cb7.isChecked();
                //Log.d(TAG, "chk[7]:" + chk[7]);
                break;
            }
            case R.id.redlist_check8: {
                chk[8] = cb8.isChecked();
                //Log.d(TAG, "chk[8]:" + chk[8]);
                break;
            }
            case R.id.redlist_check9: {
                chk[9] = cb9.isChecked();
                //Log.d(TAG, "chk[9]:" + chk[9]);
                break;
            }
            case R.id.update_button: {
                Log.d(TAG, "save the criteria in the database");
                int ck = 0;
                for (int i = 0; i<redlistLen; i++) {
                    if (chk[i] == true) {
                        ck = 1;
                    } else {
                        ck = 0;
                    }
                    Main.db.beginTransaction();
                    qry = "UPDATE Redlist" +
                            " SET isSelected = " + ck +
                            " WHERE ix = '" + i + "'";
                    Log.d(TAG, "update Redlist qry:" + qry);
                    Main.db.execSQL(qry);
                    Main.db.setTransactionSuccessful();
                    Main.db.endTransaction();
                }
                break;
            } // button

        } // switch
    } // on click


}
