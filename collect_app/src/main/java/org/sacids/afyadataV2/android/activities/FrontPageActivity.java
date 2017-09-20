package org.sacids.afyadataV2.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.app.Preferences;

import java.util.Locale;


import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.os.Build.VERSION_CODES.N;

public class FrontPageActivity extends AppCompatActivity {

    private Context context = this;
    private SharedPreferences mSharedPreferences;


    String[] language;
    private Spinner spinnerLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        mSharedPreferences = getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);

        setViews();

        //Available Languages
        language = new String[]{
                getString(R.string.lbl_choose_language),
                getString(R.string.lang_english),
                getString(R.string.lang_swahili),
                getString(R.string.lang_french),
                getString(R.string.lang_portuguese)
        };

        loadSpinnerLanguage();
    }

    //setViews
    public void setViews() {
        //button signIn
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        //button SignUp
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegisterActivity.class));
            }
        });
    }

    //spinner for language change
    private void loadSpinnerLanguage() {
        //Spinner language
        spinnerLanguage = (Spinner) findViewById(R.id.spinner);
        spinnerLanguage.setAdapter(new SpinnerAdapter(this, R.layout.custom_spinner, language));

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mLanguage = parent.getItemAtPosition(position).toString();

                switch (mLanguage) {
                    case "English":
                        setLocale("en");
                        startActivity(new Intent(context, FrontPageActivity.class));
                        finish();
                        break;

                    case "Swahili":
                        setLocale("sw");
                        startActivity(new Intent(context, FrontPageActivity.class));
                        finish();
                        break;

                    case "French":
                        setLocale("fr");
                        startActivity(new Intent(context, FrontPageActivity.class));
                        finish();
                        break;

                    case "Portuguese":
                        setLocale("pt");
                        startActivity(new Intent(context, FrontPageActivity.class));
                        finish();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //setLocale
    private void setLocale(String mLanguageCode) {
        Locale locale = new Locale(mLanguageCode);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        context.getResources().updateConfiguration(
                config,
                context.getResources().getDisplayMetrics());


        //update language
        mSharedPreferences.edit().putString(Preferences.DEFAULT_LOCALE, mLanguageCode).commit();
        mSharedPreferences.edit().putBoolean(Preferences.FIRST_TIME_APP_OPENED, false).commit();
    }

    //SpinnerAdapter
    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View spinnerLanguage = inflater.inflate(R.layout.custom_spinner, parent, false);

            TextView change_language = (TextView) spinnerLanguage.findViewById(R.id.change_language);
            change_language.setText(language[position]);

            return spinnerLanguage;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
