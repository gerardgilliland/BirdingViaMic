package com.modelsw.birdingviamic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appdatasearch.GetRecentContextCall;

// import org.apache.http.protocol.HTTP;
import java.net.HttpURLConnection;
import java.io.IOException;


public class MyList extends AppCompatActivity {
    private static final String TAG = "MyList";

    Intent emailIntent;
    private String qry = "";
    private TextView defList;
    private TextView idList;
    Toolbar toolbar;
    char q = 34;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list);

        // action bar toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setLogo(R.drawable.treble_clef_linen);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.teal));
        onCreateOptionsMenu();
        toolbar.showOverflowMenu();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Navigation Icon tapped");
                finish();
            }
        });

        idList = (TextView) findViewById(R.id.id_list);
        showIdList();
        defList = (TextView) findViewById(R.id.def_list);
        showDefList();

    }

    public boolean onCreateOptionsMenu() {
        final MenuInflater menuInflater = getMenuInflater();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        // COMMENTED OUT BECAUSE I AM USING XML DEFINED ITEMS NOT ADDING ITEMS HERE
        // It is also possible add items here. Use a generated id from
        // resources (ids.xml) to ensure that all menu ids are distinct.
        //MenuItem locationItem = menu.add(0, R.id.menu_option1, 0, R.string.meta_data);
        //locationItem.setIcon(R.drawable.ic_action_location);

        // Need to use MenuItemCompat methods to call any action item related methods
        //MenuItemCompat.setShowAsAction(locationItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                share();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void showIdList() {
        String stats = "Identified:\n";
        Main.db = Main.songdata.getWritableDatabase();
        // includes any Identified regardless of source
        qry = "SELECT CommonName, Count(SongList.Ref)" +
                " FROM SongList JOIN CodeName ON SongList.Ref = CodeName.Ref" +
                " WHERE Identified = 1" +
                " AND SongList.Ref > 0" +
                " GROUP BY CommonName " +
                " ORDER BY CommonName";
        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        int cntr = rs.getCount();
        if (cntr > 0) {
            rs.moveToFirst();
            for (int i = 0; i < cntr; i++) {
                stats += "\t" + (i + 1) + " " + rs.getString(0) + " (" + rs.getInt(1) + ")\n";
                rs.moveToNext();
            }
        }

        rs.close();
        qry = "SELECT FileName, LastDate FROM LastKnown WHERE Activity = 'Identified'";
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        cntr = rs.getCount();
        if (cntr > 0) {
            rs.moveToFirst();
            stats += "\tLast Identified:\n";
            stats += "\t\t" + rs.getString(0) + "\n";
            stats += "\t\tOn:" + rs.getString(1) + "\n";
        }
        rs.close();
        idList.setText(stats);
    }

    void showDefList(){
        String stats = "Defined:\n";
        Main.db = Main.songdata.getWritableDatabase();
        qry = "SELECT CommonName, Count(SongList.Ref)" +
                " FROM SongList JOIN CodeName ON SongList.Ref = CodeName.Ref" +
                " WHERE Defined = 1" +
                " AND SongList.Ref > 0" +
                " GROUP BY CommonName " +
                " ORDER BY CommonName";
        Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        int cntr = rs.getCount();
        rs.moveToFirst();
        for (int i = 0; i< cntr; i++) {
            stats += "\t" + (i+1) + " " + rs.getString(0) + " (" + rs.getInt(1) + ")\n";
            rs.moveToNext();
        }
        rs.close();
        qry = "SELECT FileName, LastDate FROM LastKnown WHERE Activity = 'Defined'";
        rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
        cntr = rs.getCount();
        if (cntr > 0) {
            rs.moveToFirst();
            stats += "\tLast Defined:\n";
            stats += "\t\t" + rs.getString(0) + "\n";
            stats += "\t\tOn:" + rs.getString(1) + "\n";
        }
        rs.close();
        defList.setText(stats);
    }

    void share() {
        Log.d(TAG, "share");
        //emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
        //emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Birding Via Mic - My List");
        emailIntent.putExtra(Intent.EXTRA_TEXT, idList.getText() + "\n" + defList.getText() + "\n");
        startActivityForResult(emailIntent, 1);
    }

    public static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

}
