package org.mozambique.app.afyadata.activities;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.utilities.ImageLoader;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.loadLanguage;

public class SymptomActivity extends AppCompatActivity {

    static final String TAG = "Symptom";
    Toolbar mToolbar;
    ActionBar actionBar;

    //string variable
    String mId;
    String mTitle;
    String mPhoto;
    String mDescription;

    //private Symptom symptom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage(SymptomActivity.this);
        setContentView(R.layout.activity_symptom);

        //get intent variables
        mId = getIntent().getStringExtra("id");
        mTitle = getIntent().getStringExtra("title");
        mPhoto = getIntent().getStringExtra("photo");
        mDescription = getIntent().getStringExtra("description");

        //symptom = (Symptom) Parcels.unwrap(getIntent().getParcelableExtra("symptom"));

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
        mToolbar.setTitle(getString(R.string.nav_item_symptoms) + " > " + mTitle);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //setViews
    public void setViews() {
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        ImageView photo = (ImageView) findViewById(R.id.photo);

        title.setText(mTitle);

        //setText Description
        if (Build.VERSION.SDK_INT >= 24) {
            description.setText(Html.fromHtml(mDescription, Build.VERSION.SDK_INT));
        } else {
            description.setText(Html.fromHtml(mDescription)); // or for older api
        }

        // Loader image - will be shown before loading image
        int loader = R.drawable.cow;

        ImageLoader imgLoader = new ImageLoader(this);
        imgLoader.displayImage(mPhoto, loader, photo);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
