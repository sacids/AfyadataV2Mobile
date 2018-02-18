package org.sacids.afyadataV2.activities;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.sacids.afyadataV2.R;
import org.sacids.afyadataV2.adapters.ChatListAdapter;
import org.sacids.afyadataV2.adapters.model.Feedback;
import org.sacids.afyadataV2.database.AfyaDataV2DB;
import org.sacids.afyadataV2.preferences.PreferenceKeys;
import org.sacids.afyadataV2.web.RestClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatListActivity extends AppCompatActivity {
    static final String TAG = "Chat";

    Toolbar mToolbar;
    ActionBar mActionBar;
    Context mContext = this;

    ProgressDialog mProgressDialog;
    SharedPreferences mSharedPreferences;

    //model and db
    Feedback mFeedback;
    AfyaDataV2DB db;

    //listView
    ListView mListView;
    List<Feedback> mFeedbackList = new ArrayList<Feedback>();
    ChatListAdapter mChatListAdapter;

    //string variable
    String mServerURL;
    String mUsername;
    String mMessage;

    //EditText
    EditText mEditFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mFeedback = (Feedback) Parcels.unwrap(getIntent().getParcelableExtra("feedback"));
        setToolbar();

        db = new AfyaDataV2DB(this);
        mFeedbackList = db.getFeedbackByInstanceId(mFeedback.getInstanceId());
        mListView = (ListView) findViewById(R.id.list_feedback);

        if (mFeedbackList.size() > 0) {
            mChatListAdapter = new ChatListAdapter(this, mFeedbackList);
            mListView.setAdapter(mChatListAdapter);
            mChatListAdapter.notifyDataSetChanged();
        }

        setAppViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.action_form_details:
                showFormDetails();
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
        mToolbar.setTitle(getString(R.string.nav_item_feedback) + " > " + mFeedback.getTitle());
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

    //show form details
    private void showFormDetails() {
        Intent feedbackIntent = new Intent(ChatListActivity.this, FormDetailsActivity.class);
        feedbackIntent.putExtra("feedback", Parcels.wrap(mFeedback));
        startActivity(feedbackIntent);
    }


    //Function to post details to the server
    private void postFeedback() {
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

        final RequestParams params = new RequestParams();
        params.add("form_id", mFeedback.getFormId());
        params.add("username", mUsername);
        params.add("message", mMessage);
        params.add("instance_id", mFeedback.getInstanceId());
        params.add("sender", "user");
        params.add("status", "pending");

        //append chat at last
        mFeedback.setFormId(mFeedback.getFormId());
        mFeedback.setUserName(mUsername);
        mFeedback.setMessage(mMessage);
        mFeedback.setSender("user");
        mFeedback.setInstanceId(mFeedback.getInstanceId());
        mFeedback.setReplyBy(String.valueOf(0));
        mFeedback.setStatus("pending");

        RestClient.post(mServerURL + "/api/v3/feedback/send", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mProgressDialog.dismiss();

                Log.d(TAG, response.toString());

                mFeedbackList.add(mFeedback);
                mChatListAdapter.notifyDataSetChanged();
                mEditFeedback.setText("");//clear mFeedback posted
                Log.d(TAG, "Saving feedback success");

                Toast.makeText(ChatListActivity.this, getResources().getString(R.string.success_feedback),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mProgressDialog.dismiss();
                Toast.makeText(ChatListActivity.this, getResources().getString(R.string.error_feedback),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
