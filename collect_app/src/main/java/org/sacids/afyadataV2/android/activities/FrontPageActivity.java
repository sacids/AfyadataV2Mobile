package org.sacids.afyadataV2.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.app.Preferences;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.sacids.afyadataV2.android.utilities.AfyaDataUtils.setLocale;

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

        spinnerLanguage.post(new Runnable() {
            @Override
            public void run() {
                spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedLanguage = parent.getItemAtPosition(position).toString();

                        Log.d("FrontPage", "Selected language " + selectedLanguage);
                        Activity _activity = FrontPageActivity.this;

                        switch (selectedLanguage) {
                            case "English":
                                setLocale(_activity, mSharedPreferences, "en");
                                break;

                            case "Swahili":
                                setLocale(_activity, mSharedPreferences, "sw");
                                break;

                            case "French":
                                setLocale(_activity, mSharedPreferences, "fr");
                                break;

                            case "Portuguese":
                                setLocale(_activity, mSharedPreferences, "pt");
                                break;
                            default:
                                setLocale(_activity, mSharedPreferences, "sw");
                                break;
                        }
                        Intent refresh = new Intent(context, FrontPageActivity.class);
                        startActivity(refresh);
                        finish();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
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
