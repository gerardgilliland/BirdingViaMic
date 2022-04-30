package com.modelsw.birdingviamic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class GetCnnId extends Activity {
    String TAG = "GetCnnId";
    String url;
    Scanner sc;
    StringBuffer sb;
    String result;
    //WebView browser = (WebView) findViewById(R.id.webview);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // it has not be been used before -- go get an Id from the MySql database on the server

        //url = "https://www.modelsw.com/Nvidia/GetCnnId.php";
        //browser.loadUrl(url);
        //Retrieving the contents of the specified page
        //Instantiating the StringBuffer class to hold the result
        //Retrieving the String from the String Buffer object
        //result = browser.toString();
        //Removing the HTML tags
        //result = result.replaceAll("<[^>]*>", "");
        //System.out.println(result);
        //int id = result.indexOf("CnnId=");
        int id = 8;
        Main.CnnId = id;
        Log.d(TAG, "OnCreate CnnId: " + id);
        // store it in SQLite database in this app
        String qry = "UPDATE Options SET Value = " + id + " WHERE Name =  'CnnId'";
        Main.db.execSQL(qry);
        //sc.close();
    }
}
