package org.mozambique.app.afyadata.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.mozambique.app.afyadata.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.loadLanguage;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "About";
    private Context context = this;

    private TextView openLicence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage(AboutActivity.this);
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
        findViewById(R.id.app_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://afyadata.sacids.org"));
                startActivity(webIntent);
            }
        });

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
