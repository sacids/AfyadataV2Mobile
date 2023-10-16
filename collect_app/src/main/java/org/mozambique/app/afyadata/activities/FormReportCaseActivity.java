package org.mozambique.app.afyadata.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.adapters.model.Tips;
import org.mozambique.app.afyadata.app.PrefManager;
import org.mozambique.app.afyadata.database.AfyaDataV2DB;
import org.mozambique.app.afyadata.preferences.PreferenceKeys;
import org.mozambique.app.afyadata.tasks.GPSTracker;
import org.mozambique.app.afyadata.web.RestClient;


import java.util.ArrayList;
import java.util.List;

import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.loadLanguage;

public class FormReportCaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static final String TAG = "Report Case";

    Toolbar mToolbar;
    ActionBar mActionBar;
    Context mContext = this;
    private PrefManager mPrefManager;

    ProgressDialog mProgressDialog;
    SharedPreferences mSharedPreferences;

    //model and db
    AfyaDataV2DB db;

    //attend case
    private RadioGroup rgAttendCase;
    private RadioButton rbAttendCase;

    //detected disease
    private List<Tips> tipsList = new ArrayList<Tips>();
    private Spinner spDisease;

    //other disease AND action taken
    private EditText etOtherDisease;
    private EditText etActionTaken;

    //reported on e-mai
    private RadioGroup rgReported;
    private RadioButton rbReported;


    //variable
    String mId;
    String mFormId;
    String mInstanceId;
    String mCaseAttended;
    String diseaseId;
    long mDiseaseId;
    String mOtherDisease;
    String mActionTaken;
    String mReported;

    //string variable
    String mServerURL;
    String mUsername;

    double mLatitude;
    double mLongitude;

    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage(FormReportCaseActivity.this);
        setContentView(R.layout.activity_form_report_case);

        mPrefManager = new PrefManager(mContext);
        db = new AfyaDataV2DB(this);

        //get intent variable
        mId = getIntent().getStringExtra("id");
        mFormId = getIntent().getStringExtra("form_id");
        mInstanceId = getIntent().getStringExtra("instance_id");

        setToolbar();
        initViews();

        // Spinner click listener
        spDisease.setOnItemSelectedListener(this);

        // Loading spinner data from database
        loadSpinnerData();

        //capturing gps
        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            mLatitude = gps.getLatitude();
            mLongitude = gps.getLongitude();

            Log.d(TAG, "latitude => " + mLatitude);
            Log.d(TAG, "longitude => " + mLongitude);
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
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

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //setToolbar
    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.lbl_case_information));
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    //setAppViews
    private void initViews() {
        spDisease = (Spinner) findViewById(R.id.sp_disease_detected);
        etOtherDisease = (EditText) findViewById(R.id.et_other_disease);
        etActionTaken = (EditText) findViewById(R.id.et_action_taken);
        rgAttendCase = (RadioGroup) findViewById(R.id.idRGAttendCase);
        rgReported = (RadioGroup) findViewById(R.id.idRGReported);

        //onClick submit
        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionTaken = etActionTaken.getText().toString();
                mOtherDisease = etOtherDisease.getText().toString();

                // case attended
                int caseId = rgAttendCase.getCheckedRadioButtonId();
                rbAttendCase = (RadioButton) findViewById(caseId);
                mCaseAttended = rbAttendCase.getText().toString();

                //reported
                int reportId = rgReported.getCheckedRadioButtonId();
                rbReported = (RadioButton) findViewById(reportId);
                mReported = rbReported.getText().toString();

                if (mActionTaken.length() == 0 || mActionTaken == null) {
                    Toast.makeText(mContext, getString(R.string.required_feedback), Toast.LENGTH_SHORT).show();
                } else {
                    //todo: post data to the server
                    postCase();
                }
            }
        });
    }


    //load the spinner data from SQLite database
    private void loadSpinnerData() {
        tipsList = db.getTipsList();

        //mListFrom
        ArrayList<String> mFromList = new ArrayList<>();

        mFromList.add(getString(R.string.lbl_select_disease));
        for (Tips tip : tipsList) {
            mFromList.add(tip.getTitle());
        }
        System.out.println("my diseaseList => " + mFromList);

        // Creating mAdapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mFromList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data mAdapter to spinner
        spDisease.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // On selecting a spinner item
        diseaseId = parent.getItemAtPosition(position).toString();

        //validation
        if (diseaseId.equalsIgnoreCase(getString(R.string.lbl_select_disease))) {
            Log.d(TAG, "Do nothing");
        } else {
            Tips tp = tipsList.get(position - 1);
            mDiseaseId = tp.getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    //Function to post details to the server
    private void postCase() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = mSharedPreferences.getString(PreferenceKeys.KEY_USERNAME, null);
        mServerURL = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.lbl_waiting));
        mProgressDialog.show();

        Log.d(TAG, "case attended => " + mCaseAttended);
        Log.d(TAG, "reported => " + mReported);
        Log.d(TAG, "action taken => " + mActionTaken);

        final RequestParams params = new RequestParams();
        params.add("username", mUsername);
        params.add("form_id", mFormId);
        params.add("instance_id", mInstanceId);
        params.add("case_attended", mCaseAttended);
        params.add("disease_id", String.valueOf(mDiseaseId));
        params.add("other_disease", mOtherDisease);
        params.add("action_taken", mActionTaken);
        params.add("reported", mReported);
        params.add("latitude", String.valueOf(mLatitude));
        params.add("longitude", String.valueOf(mLongitude));

        //post data to server
        RestClient.post(mServerURL + "/api/v3/feedback/case_information", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mProgressDialog.dismiss();

                Log.d(TAG, response.toString());

                //clear form
                rbAttendCase.setChecked(false);
                rbReported.setChecked(false);
                etActionTaken.setText("");
                //spDisease.setAdapter(null);
                etOtherDisease.setText("");

                Log.d(TAG, "Data posted to the server...");

                //todo: update feedback status

                Toast.makeText(mContext, getResources().getString(R.string.success_feedback), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mProgressDialog.dismiss();
                Toast.makeText(FormReportCaseActivity.this, getResources().getString(R.string.error_feedback),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

}
