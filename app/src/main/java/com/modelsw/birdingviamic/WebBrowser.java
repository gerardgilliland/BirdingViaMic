package com.modelsw.birdingviamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class WebBrowser extends Activity {
	private String TAG = "WebBrowser";
	private Button goButton;	
	private EditText urlText;
	private WebView webView;

	
    /** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_browser);
        // Get a handle to all user interface elements
        urlText = (EditText) findViewById(R.id.url_field);
        goButton = (Button) findViewById(R.id.go_button);
        webView = (WebView) findViewById(R.id.web_view);
		// attemping to make long tap and zoom work in lollipop
		webView.getSettings().getAllowUniversalAccessFromFileURLs();
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);

        // Setup event handlers
        goButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "goButton clicked");
				openBrowser();
        	}
        }); // goButton 
        if (Main.wikipedia == true) { 
        	if (Main.existingSpec == null) {        	
            	Log.d(TAG, "No bird selected ");        
				Toast.makeText(this, "Please select a bird from the song list.", Toast.LENGTH_LONG).show();
        	} else {
        		Log.d(TAG, "wikipedia onCreate goButton with existingSpec:" + Main.existingSpec);
            	urlText.setText("http://en.m.wikipedia.org/wiki/" + Main.existingSpec);
            	goButton.performClick();
				//String txt = urlText.getText().toString();
				//Log.d(TAG, "Browser txt:" + txt);
				//ReadHtml readHtml = new ReadHtml(txt); // crashes
				//Uri uri = Uri.parse(txt);
				//Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				//startActivity(intent);
            	Main.existingSpec = null;
            	Main.wikipedia = false;
				Main.fileReshowExisting = true;
			}
        } else if (Main.xenocanto == true) { 
        	if (Main.existingSpec == null) {        	
            	Log.d(TAG, "No bird selected ");        
				Toast.makeText(this, "Please select a bird from the species list.", Toast.LENGTH_LONG).show();
        	} else {
        		//String existingSpec = Main.existingSpec.replace (' ', '-');
        		Log.d(TAG, "xenocanto onCreate goButton with existingSpec:" + Main.existingSpec);
            	//urlText.setText("http://www.xeno-canto.org/species/" + existingSpec);
				// “http://www.xeno-canto.org/explore?query=nr:171727“ replacing “XC” in the filename with “nr:”
        		String txt = "http://www.xeno-canto.org/explore?query=gen:" + Main.existingSpec;
        		if (Main.isUseLocation == true) {
        			txt += "+lat:" + Main.latitude + "+lon:" + Main.longitude;
        		}
        		txt += "+q:A";
            	urlText.setText(txt);
            	goButton.performClick();
        	}
        } else {
    		Log.d(TAG, "WebList selected onCreate goButton:");
        	urlText.setText(WebList.webLink);
			String txt = urlText.getText().toString();
			Log.d(TAG, "Browser txt:" + txt);
			//ReadHtml readHtml = new ReadHtml(txt); // crashes
			//Uri uri = Uri.parse(txt);
			//Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			//startActivity(intent);
        	goButton.performClick();
			//txt = urlText.getText().toString();
			//Log.d(TAG, "Browser txt:" + txt);
			//ReadHtml readHtml = new ReadHtml(txt); // crashes
			//uri = Uri.parse(txt);
			//intent = new Intent(Intent.ACTION_VIEW, uri);
			//startActivity(intent);
    		Main.isWebLink = false;
        }
        if (urlText == null) {
    		Log.d(TAG, "WebList url is null:");
        	urlText.setOnEditorActionListener(new OnEditorActionListener() {
        		public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
        			if (actionId == EditorInfo.IME_NULL) {
        				openBrowser();
        				InputMethodManager imm = (InputMethodManager)
        				getSystemService(INPUT_METHOD_SERVICE);
        				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        				return true;
        			} // if 
        			return false;
        		}    	
        	}); // urlText
        }
    } // onCreate
    
    // Open a browser on the URL specified in the text box
    private void openBrowser() {
    	//webView.getSettings().setJavaScriptEnabled(true);
		Log.d(TAG, "openBrowser -- loading url");
    	//webView.loadUrl(urlText.getText().toString());
    	String txt = urlText.getText().toString();
    	Log.d(TAG, "Browser txt:" + txt);
    	//ReadHtml readHtml = new ReadHtml(txt); // crashes
		Uri uri = Uri.parse(txt);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
		finish();
    } // openBrowser

    private void closeBrower() {
		Log.d(TAG, "closingBrowser isWebLink:false exisingSpec:" + Main.existingSpec);
		Main.isWebLink = false;
		//Main.existingSpec = null;
		finish();
    }
    
} // WebView