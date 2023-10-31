package org.mozambique.app.afyadata.activities;

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
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.adapters.ChatListAdapter;
import org.mozambique.app.afyadata.adapters.model.Feedback;
import org.mozambique.app.afyadata.app.PrefManager;
import org.mozambique.app.afyadata.database.AfyaDataV2DB;
import org.mozambique.app.afyadata.preferences.PreferenceKeys;
import org.mozambique.app.afyadata.web.RestClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.mozambique.app.afyadata.utilities.AfyaDataUtils.loadLanguage;

public class ChatListActivity extends AppCompatActivity {
    static final String TAG = "Chat";

    Toolbar mToolbar;
    ActionBar mActionBar;
    Context mContext = this;
    private PrefManager mPrefManager;

    ProgressDialog mProgressDialog;
    SharedPreferences mSharedPreferences;

    //model and db
    AfyaDataV2DB db;

    //variable
    String mId;
    String mTitle;
    String mFormId;
    String mSender;
    String mReply;
    String mInstanceId;

    //listView
    Feedback mFeedback = null;
    ListView mListView;
    List<Feedback> mFeedbackList = new ArrayList<Feedback>();
    ChatListAdapter mChatListAdapter;

    //string variable
    String mServerURL;
    String mUsername;
    String mMessage;

    //EditText
    EditText mEditFeedback;

    //group name
    String mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage(this);
        setContentView(R.layout.activity_chat_list);

        mPrefManager = new PrefManager(mContext);

        //get intent variable
        mId = getIntent().getStringExtra("id");
        mTitle = getIntent().getStringExtra("title");
        mFormId = getIntent().getStringExtra("form_id");
        mSender = getIntent().getStringExtra("sender");
        mReply = getIntent().getStringExtra("reply_by");
        mInstanceId = getIntent().getStringExtra("instance_id");

        mFeedback = new Feedback();
        //mFeedback.setId(Long.parseLong(mId));
        //mFeedback.setTitle(mTitle);

        setToolbar();

        db = new AfyaDataV2DB(this);
        mFeedbackList = db.getFeedbackByInstanceId(mInstanceId);
        mListView = (ListView) findViewById(R.id.list_feedback);

        if (mFeedbackList.size() > 0) {
            mChatListAdapter = new ChatListAdapter(this, mFeedbackList);
            mListView.setAdapter(mChatListAdapter);
            mChatListAdapter.notifyDataSetChanged();
        }

        //set group
        mGroup = mPrefManager.getGroup();

        //setup views
        setAppViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);

        if (mGroup.equalsIgnoreCase("CAW")) {
            MenuItem menuItem = menu.findItem(R.id.action_report_case);
            menuItem.setVisible(false);
        }

        //return true;
        return super.onCreateOptionsMenu(menu);
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

            case R.id.action_report_case:
                reportCase();
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
        mToolbar.setTitle(getString(R.string.nav_item_chat));
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
        //start activity
        startActivity(new Intent(mContext, FormDetailsActivity.class)
                .putExtra("id", mId)
                .putExtra("title", mTitle)
                .putExtra("form_id", mFormId)
                .putExtra("sender", mSender)
                .putExtra("reply_by", mReply)
                .putExtra("instance_id", mInstanceId));
    }

    //report case
    private void reportCase(){
        //start new activity
        startActivity(new Intent(mContext, FormReportCaseActivity.class)
                .putExtra("form_id", mFormId)
                .putExtra("instance_id", mInstanceId));

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
        params.add("form_id", mFormId);
        params.add("username", mUsername);
        params.add("message", mMessage);
        params.add("instance_id", mInstanceId);
        params.add("sender", "user");
        params.add("status", "pending");

        //append chat at last
        mFeedback.setFormId(mFormId);
        mFeedback.setUserName(mUsername);
        mFeedback.setMessage(mMessage);
        mFeedback.setSender("user");
        mFeedback.setInstanceId(mInstanceId);
        mFeedback.setReplyBy(mPrefManager.getUserId());
        mFeedback.setStatus("pending");

        RestClient.post(mServerURL + "/api/v3/feedback/send", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mProgressDialog.dismiss();

                Log.d(TAG, response.toString());

                //update adapter
                mFeedbackList.add(mFeedback);
                mChatListAdapter.notifyDataSetChanged();
                mEditFeedback.setText("");//clear mFeedback posted
                Log.d(TAG, "Saving feedback success");

                Toast.makeText(mContext, getResources().getString(R.string.success_feedback), Toast.LENGTH_SHORT).show();
//                //start new Activity
//                finish();
//                startActivity(new Intent(mContext, ChatListActivity.class)
//                        .putExtra("feedback", Parcels.wrap(mFeedback)));
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
