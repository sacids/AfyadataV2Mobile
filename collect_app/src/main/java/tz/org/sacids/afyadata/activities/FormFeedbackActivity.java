package tz.org.sacids.afyadata.activities;

import android.app.ProgressDialog;
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
import android.widget.EditText;
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
import tz.org.sacids.afyadata.R;
import tz.org.sacids.afyadata.adapters.FeedbackListAdapter;
import tz.org.sacids.afyadata.adapters.FormFeedbackListAdapter;
import tz.org.sacids.afyadata.adapters.model.Feedback;
import tz.org.sacids.afyadata.adapters.model.Form;
import tz.org.sacids.afyadata.adapters.model.FormDetails;
import tz.org.sacids.afyadata.adapters.model.FormFeedback;
import tz.org.sacids.afyadata.app.PrefManager;
import tz.org.sacids.afyadata.preferences.PreferenceKeys;
import tz.org.sacids.afyadata.web.RestClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FormFeedbackActivity extends AppCompatActivity {

    private static final String TAG = "FormFeedback";

    Toolbar mToolbar;
    ActionBar mActionBar;

    Context mContext = this;
    PrefManager mPrefManager;
    ProgressBar mProgressBar;
    ProgressDialog mProgressDialog;
    SharedPreferences mSharedPreferences;


    //string variable
    String mServerURL;
    String mUsername;
    String mMessage;

    //EditText
    EditText mEditFeedback;

    Form mForm;
    FormFeedback mFormFeedback;
    ListView mListView;
    List<FormFeedback> mFeedbackList = new ArrayList<FormFeedback>();
    FormFeedbackListAdapter mListAdapter;

    private static final String TAG_ID = "id";
    private static final String TAG_FORM_ID = "form_id";
    private static final String TAG_INSTANCE_ID = "instance_id";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SENDER = "sender";
    private static final String TAG_REPLY_BY = "reply_by";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_DATE_CREATED = "date_created";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_feedback);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = mSharedPreferences.getString(PreferenceKeys.KEY_USERNAME, null);
        mServerURL = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        mForm = (Form) Parcels.unwrap(getIntent().getParcelableExtra("form"));
        setToolbar();

        mPrefManager = new PrefManager(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mListView = (ListView) findViewById(R.id.lists);
        mListAdapter = new FormFeedbackListAdapter(this, mFeedbackList);
        mListView.setAdapter(mListAdapter);

        //check network connectivity and fetch forms
        if (ni == null || !ni.isConnected()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_SHORT).show();
        } else {
            fetchFormFeedback();
        }

        //on submitting feedback
        setAppViews();
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
                mFeedbackList.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                fetchFormFeedback();
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
        mToolbar.setTitle(getString(R.string.nav_item_reports) + mForm.getInstanceName());
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    //setAppViews
    private void setAppViews() {
        //For submitting mFeedback to server
        mEditFeedback = (EditText) findViewById(R.id.write_feedback);

        //onClick submit
        findViewById(R.id.btn_submit_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessage = mEditFeedback.getText().toString();

                if (mMessage.length() == 0 || mMessage == null) {
                    Toast.makeText(mContext, getString(R.string.required_feedback), Toast.LENGTH_SHORT).show();
                } else {
                    postFeedback();
                }
            }
        });
    }


    //TODO : Fetch Form Feedback
    private void fetchFormFeedback() {
        RequestParams params = new RequestParams();
        params.add("instance_id", mForm.getInstanceId());
        params.add("username", mUsername);

        RestClient.get(mServerURL + "/api/v3/reports/feedback", params, new JsonHttpResponseHandler() {
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

                            //Model formFeedback
                            FormFeedback formFeedback = new FormFeedback();
                            formFeedback.setId(obj.getLong(TAG_ID));
                            formFeedback.setFormId(obj.getString(TAG_FORM_ID));
                            formFeedback.setInstanceId(obj.getString(TAG_INSTANCE_ID));
                            formFeedback.setMessage(obj.getString(TAG_MESSAGE));
                            formFeedback.setSender(obj.getString(TAG_SENDER));
                            formFeedback.setReplyBy(obj.getString(TAG_REPLY_BY));
                            formFeedback.setUsername(obj.getString(TAG_USERNAME));
                            formFeedback.setDateCreated(obj.getString(TAG_DATE_CREATED));

                            //add list to array
                            mFeedbackList.add(formFeedback);
                        }
                        mListAdapter.notifyDataSetChanged();
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


    //Function to post details to the server
    private void postFeedback() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.lbl_waiting));
        mProgressDialog.show();

        RequestParams params = new RequestParams();
        params.add("form_id", mForm.getFormId());
        params.add("username", mUsername);
        params.add("message", mMessage);
        params.add("instance_id", mForm.getInstanceId());
        params.add("sender", "user");
        params.add("status", "pending");

        //append chat at last
        mFormFeedback = new FormFeedback();
        mFormFeedback.setFormId(mForm.getFormId());
        mFormFeedback.setUsername(mUsername);
        mFormFeedback.setMessage(mMessage);
        mFormFeedback.setSender("user");
        mFormFeedback.setInstanceId(mFormFeedback.getInstanceId());
        mFormFeedback.setReplyBy(String.valueOf(0));

        RestClient.post(mServerURL + "/api/v3/reports/send_feedback", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mProgressDialog.dismiss();

                Log.d(TAG, response.toString());

                mFeedbackList.add(mFormFeedback);
                mListAdapter.notifyDataSetChanged();
                mEditFeedback.setText("");
                Log.d(TAG, "Saving feedback success");

                Toast.makeText(mContext, getString(R.string.success_feedback),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mProgressDialog.dismiss();
                Toast.makeText(mContext, getString(R.string.error_feedback),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
