package org.sacids.afyadataV2.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.app.Preferences;
import org.sacids.afyadataV2.android.utilities.AfyaDataUtils;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.os.Build.VERSION_CODES.N;
import static org.sacids.afyadataV2.android.utilities.AfyaDataUtils.setLocale;

public class LanguageActivity extends AppCompatActivity {

    private static final String TAG = "Language";

    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Context context = this;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                setLocale(context,mSharedPreferences,"sw");
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

        });

        findViewById(R.id.btn_french).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(context,mSharedPreferences,"fr");
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

        });

        findViewById(R.id.btn_english).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(context,mSharedPreferences,"en");
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

        });

        findViewById(R.id.btn_portuguese).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(context,mSharedPreferences,"pt");
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
