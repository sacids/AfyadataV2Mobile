package org.sacids.afyadataV2.android.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.sacids.afyadataV2.android.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "About";

    private Toolbar mToolbar;
    private ActionBar actionBar;
    private Context context = this;

    private TextView openLicence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    //setToolbar
//    private void setToolbar() {
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitle(getString(R.string.nav_item_about));
//        setSupportActionBar(mToolbar);
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//    }

    //setViews
    private void setViews() {
        //on click open source licence
        findViewById(R.id.open_source_licence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, OpenSourceLicensesActivity.class));
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
