package org.mozambique.app.afyadata.activities;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
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
import org.mozambique.app.afyadata.adapters.FormListAdapter;
import org.mozambique.app.afyadata.adapters.model.Form;
import org.mozambique.app.afyadata.app.PrefManager;
import org.mozambique.app.afyadata.preferences.PreferenceKeys;
import org.mozambique.app.afyadata.web.RestClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReportsActivity extends AppCompatActivity {

    private static final String TAG = "Reports";

    Toolbar mToolbar;
    ActionBar mActionBar;

    Context mContext = this;
    PrefManager mPrefManager;

    SharedPreferences mSharedPreferences;
    String mUserName;
    String mServerURL;

    ListView mListView;
    List<Form> mFormList = new ArrayList<Form>();
    FormListAdapter mFormListAdapter;

    ProgressBar mProgressBar;

    //TAGs
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_FORM_ID = "form_id";
    private static final String TAG_INSTANCE_ID = "instance_id";
    private static final String TAG_INSTANCE_NAME = "instance_name";
    private static final String TAG_JR_FORM_ID = "jr_form_id";
    private static final String TAG_FEEDBACK = "feedback";
    private static final String TAG_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        setToolbar();

        mPrefManager = new PrefManager(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mListView = (ListView) findViewById(R.id.lists);
        mFormListAdapter = new FormListAdapter(this, mFormList);
        mListView.setAdapter(mFormListAdapter);

        //check network connectivity and fetch forms
        if (ni == null || !ni.isConnected()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_SHORT).show();
        } else {
            fetchForms();
        }

        //OnClick listView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Form form = mFormList.get(position);

                //startActivity
                startActivity(new Intent(mContext, FormViewActivity.class).
                        putExtra("form", Parcels.wrap(form)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feedback_menu, menu);

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

            case R.id.action_refresh:
                mFormList.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                fetchForms();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //setToolbar
    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.nav_item_reports));
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    //TODO : Fetch Forms
    private void fetchForms() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserName = mSharedPreferences.getString(PreferenceKeys.KEY_USERNAME, null);
        mServerURL = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        RequestParams params = new RequestParams();
        params.add("username", mUserName);

        RestClient.get(mServerURL + "/api/v3/reports/forms", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                mProgressBar.setVisibility(View.GONE);

                Log.d(TAG, "forms response => " + response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray formArray = response.getJSONArray("forms");

                        for (int i = 0; i < formArray.length(); i++) {
                            JSONObject obj = formArray.getJSONObject(i);

                            Form form = new Form();
                            form.setId(obj.getLong(TAG_ID));
                            form.setTitle(obj.getString(TAG_TITLE));
                            form.setFormId(obj.getString(TAG_FORM_ID));
                            form.setInstanceId(obj.getString(TAG_INSTANCE_ID));
                            form.setInstanceName(obj.getString(TAG_INSTANCE_NAME));
                            form.setJrFormId(obj.getString(TAG_JR_FORM_ID));
                            form.setFeedback(obj.getLong(TAG_FEEDBACK));
                            form.setUser(obj.getString(TAG_USER));

                            //add list to array
                            mFormList.add(form);
                        }
                        mFormListAdapter.notifyDataSetChanged();
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
