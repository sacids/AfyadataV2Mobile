package org.sacids.afyadataV2.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.app.Preferences;
import org.sacids.afyadataV2.android.utilities.AfyaDataLanguages;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.sacids.afyadataV2.android.utilities.AfyaDataUtils.loadLanguage;
import static org.sacids.afyadataV2.android.utilities.AfyaDataUtils.setAppLanguage;

public class LanguageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Context context = this;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadLanguage(LanguageActivity.this);
        setContentView(R.layout.activity_language);

        mSharedPreferences = getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);

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
        mToolbar.setTitle(getString(R.string.nav_item_language));
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //setViews
    public void setViews() {
        findViewById(R.id.btn_swahili).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppLanguage(LanguageActivity.this, AfyaDataLanguages.SWAHILI.getLanguage());
                startActivity(new Intent(context, SplashScreenActivity.class));
                finish();
            }
        });

        findViewById(R.id.btn_english).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppLanguage(LanguageActivity.this, AfyaDataLanguages.ENGLISH.getLanguage());
                startActivity(new Intent(context, SplashScreenActivity.class));
                finish();
            }

        });

        findViewById(R.id.btn_french).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppLanguage(LanguageActivity.this, AfyaDataLanguages.FRENCH.getLanguage());
                startActivity(new Intent(context, SplashScreenActivity.class));
                finish();
            }

        });

        findViewById(R.id.btn_portuguese).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppLanguage(LanguageActivity.this, AfyaDataLanguages.PORTUGUESE.getLanguage());
                startActivity(new Intent(context, SplashScreenActivity.class));
                finish();
            }

        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}