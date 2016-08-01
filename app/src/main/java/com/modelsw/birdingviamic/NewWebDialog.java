package com.modelsw.birdingviamic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class NewWebDialog  extends Activity implements OnClickListener {
	private static final String TAG = "NewWebDialog";
    TextView textTitle;
	private TextView textName;
	private EditText editText;
	private TextView textLink;
	private EditText editLink;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate existingName:" + Main.existingWebName );	
        super.onCreate(savedInstanceState);      
        setContentView(R.layout.newweb_dialog);
        textTitle = (TextView) findViewById(R.id.add_rename_title);
        String title = "";
        if (Main.myRequest == 1) { // 1=add 2=rename
            title = getString(R.string.add_web_label);
        }
        if (Main.myRequest == 2) { // 1=add 2=rename
            title = getString(R.string.rename_web_label);
        }
        textTitle.setText(title);
        editText = (EditText) findViewById(R.id.new_name_text);
        editText.setText(Main.newWebName);
        editText.setOnClickListener(this);
        editLink = (EditText) findViewById(R.id.new_link_text);
        editLink.setText(Main.newLink);
        editLink.setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.done_button).setOnClickListener(this);
        Log.d(TAG, "*** 1c *** onCreate: existingWebName:" + Main.existingWebName + " newWebName:" + Main.newWebName +
 			  " existingLink:" + Main.existingLink + " newLink:" + Main.newLink );
        //Log.d(TAG, "*** 1d *** onCreate: textName:" + textName.getText() + " editText:" + editText.getText() +
 		//	  " textLink:" + textLink.getText() + " editLink:" + editLink.getText());
	}

	public void onClick(View v) {
		switch (v.getId()) {

            case R.id.done_button: {
                Main.newWebName = editText.getText().toString();
                Main.newLink = editLink.getText().toString();
                Log.d(TAG, "done newWebName:" + Main.newWebName + " newLink:" + Main.newLink);
                setResult(1);
                finish();
                break;
            }
            case R.id.cancel_button: {
                setResult(0);
                finish();
                break;
            }

        } // switch
	
	} // on click

}

