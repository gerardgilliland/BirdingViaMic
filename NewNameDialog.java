package com.modelsw.birdingviamic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewNameDialog extends Activity implements OnClickListener {
	private static final String TAG = "NewNameDialog";
	private String existingSpec;
	private int iref = 0; // unidentified bird
    private static char q = 34;
	private String qry = "";
	private Cursor rs;  // I see cursor as RecordSet (rs)
	private Spinner spinner;
	private EditText textName;
    TextView textTitle;


    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "*** 3 *** onCreate existingName:" + Main.existingName + " existingRef:" + Main.existingRef + " existingInx:" + Main.existingInx);	
        super.onCreate(savedInstanceState);
        if (Main.songpath == null || Main.songdata == null) {
            return;
        }
        Main.db = Main.songdata.getWritableDatabase();
        setContentView(R.layout.newname_dialog);
		findViewById(R.id.done_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        textName = (EditText) findViewById(R.id.newname_text);
        textName.setText((CharSequence) Main.existingName);
        textName.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.species_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        loadSpinnerData();
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selection = (String) arg0.getSelectedItem();
             	Log.d(TAG, "onItemSelected:" + selection + " offset:" + arg2 );
                qry = "SELECT Ref FROM CodeName" +
                		" WHERE CommonName = " + q + selection + q;  // double quote because of ' in the names
        		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null); 
             	Log.d(TAG, "onItemSelected qry:" + qry );
                rs.moveToFirst();
                Main.newRef = rs.getInt(0);
             	Log.d(TAG, "onItemSelected newRef:" + Main.newRef );
                rs.close();
                //Main.db.close();  // do i need to add this as well?
            }

            public void onNothingSelected(AdapterView<?> arg0) {
             	Log.d(TAG, "onNothingSelected arg0:" + arg0 );
            }
        });
        
	}

    private void loadSpinnerData() {
        // Spinner Drop down elements
		Log.d(TAG, "loadSpinnerData:" + Main.existingName + " existingRef:" + Main.existingRef);	
        List<String> lables = this.getAllLabels();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
        // Drop down layout style 
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // attach data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(iref);   // preposition to existing spec (found in getAllLabels below)        	
    }
 
    
    private List<String> getAllLabels() {
        List<String> labels = new ArrayList<String>();
     	Log.d(TAG, "getAllLabels entry with existingRef:" + Main.existingRef  );
   	    qry = "SELECT Area from Region WHERE isSelected = 1";
   		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
   		rs.moveToFirst();
   		int cntr = rs.getCount();
   		String[] area = new String[cntr];
   		for (int i = 0; i<cntr; i++) {
   			area[i] = rs.getString(0);
			rs.moveToNext();
   		}
   		rs.close();
     	qry = "PRAGMA case_sensitive_like = 1";
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
		rs.close();
		qry = "SELECT CommonName, Ref FROM CodeName";
		if (cntr == 0) {
			qry += " WHERE Region = 'none'";
		} else {
			int oneTime = 0;
			for (int i = 0; i< cntr; i++) {
				if (oneTime == 0) {
					qry += " WHERE (Region like '%" + area[i] + "%'";
					oneTime++;
				} else {
					qry += " OR Region like '%" + area[i] + "%'";
				}
				if (area[i].equals("Worldwide")) {
					qry += " OR SubRegion like '%introduced worldwide%'";
				}
			}
			qry += ")"; 
			if (Main.isUseLocation == true) {
				qry += " AND InArea = 1 ";
			}
			if (Main.isSortByName == true) {
				qry += " ORDER BY CommonName";
			} else {
				qry += " ORDER BY Ref";
			}
		}
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null); 
        if (rs.moveToFirst()) {
         	Log.d(TAG, "getAllLabels entry before add:" + Main.existingRef);
         	int existRef = Main.existingRef;
        	int i = 0;
            do {            	 
//            	String labelSpec = rs.getString(1);
//             	Log.d(TAG, "getAllLabels:'" + existSpec + "' labelSpec:'" + labelSpec + "' i:" + i );
                if (existRef == rs.getInt(1)) {
                	iref = i;
                 	Log.d(TAG, "getAllLabels Match In Loop existingRef:" + existRef + " iref (offset in list):" + iref);
                }
                labels.add(rs.getString(0));  // common name
                i++;
            } while (rs.moveToNext());
        }
		qry = "PRAGMA case_sensitive_like = 0";  // clear -- back to normal like aka Na == NA
		rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        rs.close();
        return labels;
    }

    
	public void onClick(View v) {
		switch (v.getId()) {

            case R.id.done_button: {
                Log.d(TAG, "*** 3a *** textName:" + textName);
                String newName = textName.getText().toString();
                boolean nameMatch = false;
                Log.d(TAG, "*** 3b *** check for existing:" + newName);
                if (!newName.equals(Main.existingName)) {
                    for (int i = 0; i < Main.songs.length; i++) {
                        if (newName.equals(Main.songs[i])) {
                            Log.d(TAG, "*** 3d *** filename exists:" + newName);
                            nameMatch = true;
                            break;
                        }
                    }
                }
                if (nameMatch == true) {
                    Toast.makeText(this, "This file already exists.\nPlease enter a different one.", Toast.LENGTH_LONG).show();
                    break;
                }
                Main.newName = newName;
                //Log.d(TAG, "*** 4 *** tryforspec Main.newName:" + Main.newName +  " existingInx:" + Main.existingInx);
                //Main.newRef = tryForSpec(Main.newName);  // don't do it here
                Log.d(TAG, "*** 5 *** doneButton  Main.newRef:" + Main.newRef + " existingInx:" + Main.existingInx);
                setResult(1);
                finish();
                break;
            }
            case R.id.cancel_button: {
                Log.d(TAG, "*** 3a *** textName:" + textName );
                setResult(0);
                finish();
                break;
            }
		} // switch
	
	} // on click

} // NewNameDialog
