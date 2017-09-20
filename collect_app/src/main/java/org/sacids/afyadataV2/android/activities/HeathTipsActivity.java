package org.sacids.afyadataV2.android.activities;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.parceler.Parcels;
import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.adapters.model.Tips;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HeathTipsActivity extends AppCompatActivity {

    private static final String TAG = "Health Tips";

    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Tips tp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath_tips);

        tp = (Tips) Parcels.unwrap(getIntent().getParcelableExtra("tips"));

        setToolbar();

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

    //setToolbar
    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.nav_item_tips) + " > " + tp.getTitle());
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //setViews
    public void setViews() {
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);

        title.setText(tp.getTitle());

        //setText Description
        if (Build.VERSION.SDK_INT >= 24) {
            description.setText(Html.fromHtml(tp.getDescription(), Build.VERSION.SDK_INT));
        } else {
            description.setText(Html.fromHtml(tp.getDescription())); // or for older api
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
