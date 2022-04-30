package com.modelsw.birdingviamic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;
import android.os.Handler;
import android.widget.ProgressBar;


public class UpgradeDialog extends Activity implements View.OnClickListener {
    private static final String TAG = "UpgradeDialog";
    private static Handler handler;
    private static ProgressBar progressBar;
    private int progressStatus;
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
                // Start long running operation in a background thread
                UpgradeSpecies upgradeSpecies = new UpgradeSpecies(this); // copy the old data to csv files in this new app.
                ConvertTables convertTables = new ConvertTables (this);  // import the csv files -- upgrade Ref where needed
                // progress bar 150 seconds completion time -- warn the user 2-3 minutes
                // https://www.journaldev.com/9629/android-progressbar-example
                progressStatus = 0;
                handler = new Handler();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                new Thread(new Runnable() {
                    public void run() {
                        while (progressStatus < 150) { // 150 seconds id 2 and 1/2 minutes
                            progressStatus += 1;
                            // Update the progress bar and display the
                            //current value in the text view
                            handler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressStatus);
                                }
                            });
                            try {
                                // Sleep for 200 milliseconds.
                                Thread.sleep(1000); // 1 second
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
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

