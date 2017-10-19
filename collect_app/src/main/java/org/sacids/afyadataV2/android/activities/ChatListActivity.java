package org.sacids.afyadataV2.android.activities;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.adapters.ChatListAdapter;
import org.sacids.afyadataV2.android.adapters.model.Campaign;
import org.sacids.afyadataV2.android.adapters.model.Feedback;
import org.sacids.afyadataV2.android.database.AfyaDataV2DB;
import org.sacids.afyadataV2.android.preferences.PreferenceKeys;
import org.sacids.afyadataV2.android.preferences.PreferencesActivity;
import org.sacids.afyadataV2.android.web.RestClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "Chat";

    private Toolbar mToolbar;
    private ActionBar actionBar;


    private Feedback feedback = null;
    private AfyaDataV2DB db;

    private List<Feedback> chatList = new ArrayList<Feedback>();
    private ChatListAdapter chatAdapter;
    private ListView listFeedback;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;
    private String username;
    private String message;

    private ImageView btnFeedback;
    private EditText writeFeedback;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        feedback = (Feedback) Parcels.unwrap(getIntent().getParcelableExtra("feedback"));

        setToolbar();

        db = new AfyaDataV2DB(this);

        chatList = db.getFeedbackByInstanceId(feedback.getInstanceId());

        listFeedback = (ListView) findViewById(R.id.list_feedback);

        if (chatList.size() > 0) {
            refreshDisplay();
        }

        //For submitting feedback to server
        writeFeedback = (EditText) findViewById(R.id.write_feedback);
        btnFeedback = (ImageView) findViewById(R.id.btn_submit_feedback);

        //if submit feedback
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = writeFeedback.getText().toString();

                if (writeFeedback.getText().length() < 1) {
                    writeFeedback.setError(getResources().getString(R.string.required_feedback));
                } else {
                    //post to the server
                    postFeedbackToServer();
                }
            }
        });
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
        mToolbar.setTitle(getString(R.string.nav_item_feedback) + " > " + feedback.getTitle());
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //show form details
    private void showFormDetails() {
        Intent feedbackIntent = new Intent(ChatListActivity.this, FormDetailsActivity.class);
        feedbackIntent.putExtra("feedback", Parcels.wrap(feedback));
        startActivity(feedbackIntent);
    }

    //refresh display
    private void refreshDisplay() {
        chatAdapter = new ChatListAdapter(this, chatList);
        listFeedback.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    //Function to post details to the server
    private void postFeedbackToServer() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString(PreferenceKeys.KEY_USERNAME, null);
        serverUrl = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.lbl_waiting));
        progressDialog.show();

        final RequestParams params = new RequestParams();
        params.add("form_id", feedback.getFormId());
        params.add("username", username);
        params.add("message", message);
        params.add("instance_id", feedback.getInstanceId());
        params.add("sender", "user");
        params.add("status", "pending");

        //append chat at last
        feedback.setFormId(feedback.getFormId());
        feedback.setUserName(username);
        feedback.setMessage(message);
        feedback.setSender("user");
        feedback.setInstanceId(feedback.getInstanceId());
        feedback.setReplyBy(String.valueOf(0));
        feedback.setStatus("pending");

        RestClient.post(serverUrl + "/api/v3/feedback/send", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();

                Log.d(TAG, response.toString());

                chatList.add(feedback);
                chatAdapter.notifyDataSetChanged();
                writeFeedback.setText("");//clear feedback posted
                Log.d(TAG, "Saving feedback success");

                Toast.makeText(ChatListActivity.this, getResources().getString(R.string.success_feedback),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
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
