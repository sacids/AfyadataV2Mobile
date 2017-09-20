package org.sacids.afyadataV2.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import org.parceler.Parcels;
import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.adapters.FormDetailsAdapter;
import org.sacids.afyadataV2.android.adapters.model.Feedback;
import org.sacids.afyadataV2.android.adapters.model.FormDetails;
import org.sacids.afyadataV2.android.database.AfyaDataV2DB;
import org.sacids.afyadataV2.android.web.BackgroundClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FormDetailsActivity extends AppCompatActivity {

    private static String TAG = "Feedback";
    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Context context = this;
    private ProgressDialog pDialog;

    private List<FormDetails> formList = new ArrayList<FormDetails>();
    private ListView listView;
    private FormDetailsAdapter formAdapter;

    private Feedback feedback = null;

    //AfyaData database
    private AfyaDataV2DB db;

    private static final String TAG_ID = "id";
    private static final String TAG_LABEL = "label";
    private static final String TAG_TYPE = "type";
    private static final String TAG_VALUE = "value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_details);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        feedback = (Feedback) Parcels.unwrap(getIntent().getParcelableExtra("feedback"));

        setToolbar();

        listView = (ListView) findViewById(R.id.list_forms);

        //initialize database
        db = new AfyaDataV2DB(this);

        formList = db.getFormDetails(feedback.getInstanceId());

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
        mToolbar.setTitle(getString(R.string.nav_item_form_details) + " > " + feedback.getTitle());
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
            param.add("table_name", feedback.getFormId());
            param.add("instance_id", feedback.getInstanceId());

            BackgroundClient.get("/api/v3/feedback/form_details", param, new JsonHttpResponseHandler() {
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
                                formDetails.setInstanceId(feedback.getInstanceId());

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
            formList = db.getFormDetails(feedback.getInstanceId());

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
