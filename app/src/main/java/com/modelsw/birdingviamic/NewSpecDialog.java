package com.modelsw.birdingviamic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
//import android.widget.Spinner;
import android.widget.Spinner;
import android.widget.TextView;

public class NewSpecDialog extends Activity implements OnClickListener {
	private static final String TAG = "NewSpecDialog";
	private EditText editRegion;
	private EditText editSpec;
	private EditText editSubRegion;
    private String existingRegion;
    private int ireg = 0;  // region offset
    private int ired = 0;  // redlist offseet
    private static char q = 34;
    private String qry = "";
    private Cursor rs;  // I see cursor as RecordSet (rs)
    private String selection;
    private String selectionRed;
    private String region;
    private String redlist;
    private Spinner spinner;
    private Spinner redspinner;
    TextView textTitle;
    TextView textSpecName;
	TextView textRegion;
	TextView textSpec;
	TextView textSubRegion;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate existingName:" + Main.existingSpecName );	
        super.onCreate(savedInstanceState);      
        Main.db = Main.songdata.getWritableDatabase();
        setContentView(R.layout.newspec_dialog);
        textTitle = (TextView) findViewById(R.id.add_rename_title);
        String title = "";
        if (Main.myRequest == 1) { // 1=add 2=rename
            title = getString(R.string.add_species_label);
        }
        if (Main.myRequest == 2) { // 1=add 2=rename
            title = getString(R.string.rename_species_label);
        }
        textTitle.setText(title);
        textSpecName = (EditText) findViewById(R.id.new_commonname_text);
        textSpecName.setText((CharSequence) Main.existingSpecName);
        textSpecName.setOnClickListener(this);
        textSpec = (EditText) findViewById(R.id.new_spec_text);
        textSpec.setText((CharSequence) Main.existingSpec);
        textSpec.setOnClickListener(this);

        textSubRegion = (EditText) findViewById(R.id.new_subregion_text);
        textSubRegion.setText((CharSequence) Main.existingSubRegion);
        textSubRegion.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.region_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        loadSpinnerData();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selection = (String) arg0.getSelectedItem();
                int loc = selection.indexOf(":");
                region = selection.substring(0, loc).trim();
                Log.d(TAG, "onItemSelected:" + selection + " offset:" + arg2);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Log.d(TAG, "onNothingSelected arg0:" + arg0);
            }
        });

        redspinner = (Spinner) findViewById(R.id.redlist_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        loadRedSpinnerData();
        redspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectionRed = (String) arg0.getSelectedItem();
                int loc = selectionRed.indexOf(":");
                redlist = selectionRed.substring(0,loc).trim();
                Log.d(TAG, "onItemSelected:" + selectionRed + " offset:" + arg2);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Log.d(TAG, "onNothingSelected arg0:" + arg0 );
            }
        });
        findViewById(R.id.done_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        Log.d(TAG, "*** 1c *** onCreate: existingSpecName:" + Main.existingSpecName + " newSpecName:" + Main.newSpecName);
        //Log.d(TAG, "*** 1d *** onCreate: textName:" + textName.getText() + " editText:" + editText.getText() +
 		//	  " textSpec:" + textSpec.getText() + " editSpec:" + editSpec.getText() + " textRange:" + textRange.getText() + " editRange:" + editRange.getText());
	}

    private void loadSpinnerData() {
        // Spinner Drop down elements
        Log.d(TAG, "loadSpinnerData:" + Main.existingRegion );
        List<String> labels = this.getAllLabels();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);
        // Drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // attach data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(ireg);   // preposition to existing region (found in getAllLabels below)
    }

    private void loadRedSpinnerData() {
        // Spinner Drop down elements
        Log.d(TAG, "loadRedSpinnerData:" + Main.existingRedList );
        List<String> labels = this.getRedListLabels();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);
        // Drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // attach data adapter to spinner
        redspinner.setAdapter(dataAdapter);
        redspinner.setSelection(ired);   // preposition to existing region (found in getAllLabels below)
    }

    private List<String> getAllLabels() {
        List<String> labels = new ArrayList<String>();
        qry = "SELECT Area, FullName from Region ORDER BY Area";
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        rs.moveToFirst();
        int cntr = rs.getCount();
        String area;
        for (int i = 0; i<cntr; i++) {
            area = rs.getString(0);
            if (area.equals(Main.existingRegion)) {
                ireg = i;
            }
            area += " : " + rs.getString(1);
            labels.add(area);  // common name
            rs.moveToNext();
        }
        rs.close();
        return labels;
    }

    private List<String> getRedListLabels() {
        List<String> labels = new ArrayList<String>();
        qry = "SELECT Type, FullName from RedList ORDER BY ix";
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        rs.moveToFirst();
        int cntr = rs.getCount();
        String type;
        for (int i = 0; i<cntr; i++) {
            type = rs.getString(0);
            if (type.equals(Main.existingRedList)) {
                ired = i;
            }
            type += " : " + rs.getString(1);
            labels.add(type);  // common name
            rs.moveToNext();
        }
        rs.close();
        return labels;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.done_button: {
                Main.newSpecName = textSpecName.getText().toString();
                Main.newSpec = textSpec.getText().toString();
                Main.newRegion = region;
                Main.newSubRegion = textSubRegion.getText().toString();
                if (Main.newSubRegion == null) {
                    Main.newSubRegion = "_";
                }
                Main.newRedList = redlist;
                Log.d(TAG, "done newSpecName:" + Main.newSpecName + " newSpec:" + Main.newSpec + " newRegion:" + Main.newRegion
                            + " newRedList:" + Main.newRedList);
                setResult(1);
                finish();
                break;
            }
            case R.id.cancel_button: {
                //Log.d(TAG, "*** 3a *** textName:" + textRegion);
                setResult(0);
                finish();
                break;
            }

        } // switch

    } // on click

}
