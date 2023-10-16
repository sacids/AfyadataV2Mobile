package org.mozambique.app.afyadata.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.adapters.FormDetailsAdapter;
import org.mozambique.app.afyadata.adapters.model.Form;
import org.mozambique.app.afyadata.adapters.model.FormDetails;
import org.mozambique.app.afyadata.app.PrefManager;
import org.mozambique.app.afyadata.preferences.PreferenceKeys;
import org.mozambique.app.afyadata.web.RestClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.loadLanguage;

public class FormViewActivity extends AppCompatActivity {

    private static final String TAG = "Details";

    Toolbar mToolbar;
    ActionBar mActionBar;

    Context mContext = this;
    PrefManager mPrefManager;

    SharedPreferences mSharedPreferences;
    String mServerURL;

    Form mForm;
    ListView mListView;
    List<FormDetails> mFormDetailsList = new ArrayList<FormDetails>();
    FormDetailsAdapter mFormDetailsAdapter;

    ProgressBar mProgressBar;

    private static final String TAG_ID = "id";
    private static final String TAG_LABEL = "label";
    private static final String TAG_TYPE = "type";
    private static final String TAG_VALUE = "value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage(this);
        setContentView(R.layout.activity_form_view);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mForm = (Form) Parcels.unwrap(getIntent().getParcelableExtra("form"));
        setToolbar();

        mPrefManager = new PrefManager(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mListView = (ListView) findViewById(R.id.lists);
        mFormDetailsAdapter = new FormDetailsAdapter(this, mFormDetailsList);
        mListView.setAdapter(mFormDetailsAdapter);

        //check network connectivity and fetch forms
        if (ni == null || !ni.isConnected()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_SHORT).show();
        } else {
            fetchFormDetails();
        }
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
        mToolbar.setTitle(getString(R.string.nav_item_reports) + " : " + mForm.getInstanceName());
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    //TODO : Fetch Form Details
    private void fetchFormDetails() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mServerURL = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        RequestParams params = new RequestParams();
        params.add("table_name", mForm.getFormId());
        params.add("instance_id", mForm.getInstanceId());

        RestClient.get(mServerURL + "/api/v3/reports/form_details", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                mProgressBar.setVisibility(View.GONE);

                //Log.d(TAG, response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray formArray = response.getJSONArray("form_details");

                        for (int i = 0; i < formArray.length(); i++) {
                            JSONObject obj = formArray.getJSONObject(i);

                            //Model formDetails
                            FormDetails formDetails = new FormDetails();
                            formDetails.setId(obj.getLong(TAG_ID));
                            formDetails.setLabel(obj.getString(TAG_LABEL));
                            formDetails.setType(obj.getString(TAG_TYPE));
                            formDetails.setValue(obj.getString(TAG_VALUE));
                            formDetails.setInstanceId(mForm.getInstanceId());

                            //add list to array
                            mFormDetailsList.add(formDetails);
                        }
                        mFormDetailsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "Server response " + responseString);
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
