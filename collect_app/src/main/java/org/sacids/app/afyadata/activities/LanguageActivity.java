package org.sacids.app.afyadata.activities;

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
import android.widget.RadioGroup;

import org.sacids.app.afyadata.R;
import org.sacids.app.afyadata.app.Preferences;
import org.sacids.app.afyadata.utilities.AfyaDataLanguages;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.sacids.app.afyadata.utilities.AfyaDataUtils.loadLanguage;
import static org.sacids.app.afyadata.utilities.AfyaDataUtils.setAppLanguage;

public class LanguageActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ActionBar mActionBar;

    private Context mContext = this;
    SharedPreferences mSharedPreferences;

    //radio
    RadioGroup mRadioGroup;

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
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    //setViews
    public void setViews() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = mRadioGroup.getCheckedRadioButtonId();

                // Check which radio button was clicked
                switch (selectedId) {
                    case R.id.radio_swahili:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.SWAHILI.getLanguage());
                        break;
                    case R.id.radio_english:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.ENGLISH.getLanguage());
                        break;
                    case R.id.radio_french:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.FRENCH.getLanguage());
                        break;
                    case R.id.radio_portuguese:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.PORTUGUESE.getLanguage());
                        break;
                    case R.id.radio_thai:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.THAI.getLanguage());
                        break;
                    case R.id.radio_vietnamese:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.VIETNAMESE.getLanguage());
                        break;
                    case R.id.radio_chinese:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.CHINESE.getLanguage());
                        break;
                    case R.id.radio_khmer:
                        setAppLanguage(LanguageActivity.this, AfyaDataLanguages.KHMER.getLanguage());
                        break;
                    default:
                        break;
                }
                startActivity(new Intent(mContext, MainActivity.class).
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}