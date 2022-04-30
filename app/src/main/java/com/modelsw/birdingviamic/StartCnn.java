package com.modelsw.birdingviamic;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

// called with:
// https://www.modelsw.com/GetSpecieRef.php?CnnId=0&FileName=/storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/American Crow XC172882.m4a

public class StartCnn extends Activity {
    String TAG = "StartCnn";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "existingName: " + Main.existingName + " **************** StartCnn" );
        // open web page that updates MySqlServer with file to be passed
        // https://www.modelsw.com/Nvidia/GetSpecieRef.php?CnnId=0&FileName=/storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/American Crow XC172882.m4a
        // MySql can NOT be updated remotely with Android -- I can update it from php file on server.
        // see GetSpecieRef.php on the server.
        setContentView(R.layout.startcnn);
        // this sends the fileName to MySql server and creates a new record .
        String sCnnId = new String(String.valueOf(Main.CnnId));
        WebView browser = (WebView) findViewById(R.id.webview);
        String loc = "https://www.modelsw.com/Nvidia/GetSpecieRef.php?CnnId=" + sCnnId + "&FileName=/storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/" + Main.existingName;
        Log.d(TAG, loc);
        browser.loadUrl(loc);
        finish();
    }

}