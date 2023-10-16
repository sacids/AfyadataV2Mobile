package org.mozambique.app.afyadata.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.adapters.FormDetailsAdapter;
import org.mozambique.app.afyadata.adapters.model.FormDetails;
import org.mozambique.app.afyadata.database.AfyaDataV2DB;
import org.mozambique.app.afyadata.preferences.PreferenceKeys;
import org.mozambique.app.afyadata.web.BackgroundClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.loadLanguage;

public class FormDetailsActivity extends AppCompatActivity {

    static String TAG = "Feedback";
    Toolbar mToolbar;
    ActionBar actionBar;

    Context mContext = this;
    ProgressDialog pDialog;

    private List<FormDetails> formList = new ArrayList<FormDetails>();
    private ListView listView;
    private FormDetailsAdapter formAdapter;

    //private Feedback feedback = null;

    //AfyaData database
    private AfyaDataV2DB db;
    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    private static final String TAG_ID = "id";
    private static final String TAG_LABEL = "label";
    private static final String TAG_TYPE = "type";
    private static final String TAG_VALUE = "value";

    //variable
    String mId;
    String mTitle;
    String mFormId;
    String mSender;
    String mReply;
    String mInstanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage(FormDetailsActivity.this);
        setContentView(R.layout.activity_form_details);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //get intent variable
        mId = getIntent().getStringExtra("id");
        mTitle = getIntent().getStringExtra("title");
        mFormId = getIntent().getStringExtra("form_id");
        mSender = getIntent().getStringExtra("sender");
        mReply = getIntent().getStringExtra("reply_by");
        mInstanceId = getIntent().getStringExtra("instance_id");

        //feedback = (Feedback) Parcels.unwrap(getIntent().getParcelableExtra("feedback"));
        setToolbar();

        listView = (ListView) findViewById(R.id.list_forms);

        //initialize database
        db = new AfyaDataV2DB(this);

        formList = db.getFormDetails(mInstanceId);

        if (formList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchFormDetailsTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_menu, menu);

        // Return true to display menu
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
        mToolbar.setTitle(getString(R.string.nav_item_form_details) + " > " + mTitle);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }


    //refresh display
    private void refreshDisplay() {
        formAdapter = new FormDetailsAdapter(this, formList);
        listView.setAdapter(formAdapter);
        formAdapter.notifyDataSetChanged();
    }

    //Background task
    class FetchFormDetailsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Progress dialog
            pDialog = new ProgressDialog(FormDetailsActivity.this);
            pDialog.setCancelable(true);
            pDialog.setMessage(getResources().getString(R.string.lbl_waiting));
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            final RequestParams param = new RequestParams();
            param.add("table_name", mFormId);
            param.add("instance_id", mInstanceId);

            Log.d("table_name", mFormId);
            Log.d("instance_id", mInstanceId);

            BackgroundClient.get(serverUrl + "/api/v3/feedback/form_details", param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d(TAG, "Response: " + response.toString());

                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray feedbackArray = response.getJSONArray("form_details");

                            for (int i = 0; i < feedbackArray.length(); i++) {
                                JSONObject obj = feedbackArray.getJSONObject(i);

                                FormDetails formDetails = new FormDetails();
                                formDetails.setId(obj.getLong(TAG_ID));
                                formDetails.setLabel(obj.getString(TAG_LABEL));
                                formDetails.setType(obj.getString(TAG_TYPE));
                                formDetails.setValue(obj.getString(TAG_VALUE));
                                formDetails.setInstanceId(mInstanceId);

                                //check if form details exists
                                if (!db.isFormDetailsExist(formDetails)) {
                                    db.addFormDetails(formDetails);
                                } else {
                                    db.updateFormDetails(formDetails);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d(TAG, "on Failure " + responseString);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            formList = db.getFormDetails(mInstanceId);

            if (formList != null) {
                refreshDisplay();
            }
            pDialog.dismiss();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
