package com.modelsw.birdingviamic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;


public class IdentifyDialog extends Activity implements View.OnClickListener {
    private static final String TAG = "IdentifyDialog";
    TextView textName;
    TextView textTitle;

    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify_dialog);
        textName = (TextView) findViewById(R.id.identify_text);
        textName.setText((CharSequence) Main.displayName);
        findViewById(R.id.no_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.ok_button).setOnClickListener(this);
        setResult(2);  // cancel in case they escape out
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_button: {
                Log.d(TAG, "No" );
                Main.myResult = 0;
                setResult(0);
                finish();
                break;
            }
            case R.id.ok_button: {
                Log.d(TAG, "Ok" );
                Main.myResult = 1;
                setResult(1);
                finish();
                break;
            }
            case R.id.cancel_button: {
                Log.d(TAG, "cancel" );
                Main.myResult = 2;
                setResult(2);
                finish();
                break;
            }

        } // switch

    } // on click

}

