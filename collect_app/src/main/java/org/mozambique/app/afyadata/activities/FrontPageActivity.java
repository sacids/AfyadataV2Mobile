package org.mozambique.app.afyadata.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.app.Preferences;
import org.mozambique.app.afyadata.utilities.AfyaDataLanguages;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.loadLanguage;
import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.setAppLanguage;

public class FrontPageActivity extends AppCompatActivity {

    private Context mContext = this;
    private SharedPreferences mSharedPreferences;


    String[] mLanguage;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadLanguage(FrontPageActivity.this);

        setContentView(R.layout.activity_front_page);
        mSharedPreferences = getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);

        setViews();

        //Available Languages
        mLanguage = new String[]{
                getString(R.string.lbl_choose_language),
                getString(R.string.lang_english),
                getString(R.string.lang_swahili),
                getString(R.string.lang_french),
                getString(R.string.lang_portuguese),
                getString(R.string.lang_thai),
                getString(R.string.lang_vietnamese),
                getString(R.string.lang_chinese),
                getString(R.string.lang_khmer),
        };

        loadSpinnerLanguage();
    }

    //setViews
    public void setViews() {
        //button signIn
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });

        //button SignUp
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });
    }

    //spinner for language change
    private void loadSpinnerLanguage() {
        //Spinner language
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setAdapter(new SpinnerAdapter(this, R.layout.custom_spinner, mLanguage));

        mSpinner.post(new Runnable() {
            @Override
            public void run() {
                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedLanguage = parent.getItemAtPosition(position).toString();

                        Activity _activity = FrontPageActivity.this;

                        switch (selectedLanguage) {
                            case "Swahili":
                                setAppLanguage(_activity, AfyaDataLanguages.SWAHILI.getLanguage());
                                break;

                            case "English":
                                setAppLanguage(_activity, AfyaDataLanguages.ENGLISH.getLanguage());
                                break;

                            case "French":
                                setAppLanguage(_activity, AfyaDataLanguages.FRENCH.getLanguage());
                                break;

                            case "Portuguese":
                                setAppLanguage(_activity, AfyaDataLanguages.PORTUGUESE.getLanguage());
                                break;

                            case "Thai":
                                setAppLanguage(_activity, AfyaDataLanguages.THAI.getLanguage());
                                break;

                            case "Vietnamese":
                                setAppLanguage(_activity, AfyaDataLanguages.VIETNAMESE.getLanguage());
                                break;

                            case "Chinese":
                                setAppLanguage(_activity, AfyaDataLanguages.CHINESE.getLanguage());
                                break;

                            case "Khmer":
                                setAppLanguage(_activity, AfyaDataLanguages.KHMER.getLanguage());
                                break;

                            default:
                                setAppLanguage(_activity, AfyaDataLanguages.PORTUGUESE.getLanguage());
                                break;
                        }
                        recreate();
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

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View spinnerLanguage = inflater.inflate(R.layout.custom_spinner, parent, false);

            TextView change_language = (TextView) spinnerLanguage.findViewById(R.id.change_language);
            change_language.setText(mLanguage[position]);

            return spinnerLanguage;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
