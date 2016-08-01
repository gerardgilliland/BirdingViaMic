package com.modelsw.birdingviamic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class UpgradeDialog extends Activity implements View.OnClickListener {
    private static final String TAG = "UpgradeDialog";
    TextView textName;
    TextView textTitle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.upgrade_dialog);
        textName = (TextView) findViewById(R.id.upgrade_text);
        //textName.setText((CharSequence) Main.displayName);
        findViewById(R.id.later_button).setOnClickListener(this);
        findViewById(R.id.yes_button).setOnClickListener(this);
        setResult(2);  // later -- in case they escape out
        Log.d(TAG, "set default: Later");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes_button: {
                Log.d(TAG, "Yes" );
                Main.myUpgrade = 1;
                setResult(1);
                finish();
                break;
            }
            case R.id.later_button: {
                Log.d(TAG, "Later" );
                Main.myUpgrade = 2;
                setResult(2);
                finish();
                break;
            }

        } // switch

    } // on click

}

