package tz.org.sacids.afyadata.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import tz.org.sacids.afyadata.R;
import tz.org.sacids.afyadata.adapters.TipsListAdapter;
import tz.org.sacids.afyadata.adapters.model.Tips;
import tz.org.sacids.afyadata.database.AfyaDataV2DB;
import tz.org.sacids.afyadata.preferences.PreferenceKeys;
import tz.org.sacids.afyadata.web.BackgroundClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HealthTipsListActivity extends AppCompatActivity {

    private static final String TAG = "Health Tips";

    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Context context = this;
    private ProgressDialog pDialog;

    private List<Tips> tipsList = new ArrayList<Tips>();
    private ListView listView;
    private TipsListAdapter adapter;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    //AfyaData database
    private AfyaDataV2DB db;

    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips_list);

        setToolbar();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));


        listView = (ListView) findViewById(R.id.list_tips);

        db = new AfyaDataV2DB(this);

        tipsList = db.getTipsList();

        if (tipsList.size() > 0) {
            refreshDisplay();
        } else {
            Toast.makeText(context, getString(R.string.msg_nothing_display), Toast.LENGTH_LONG).show();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchTipsTask().execute();

        //on item clicks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tips tips = tipsList.get(position);

                Intent intent = new Intent(context, HeathTipsActivity.class);
                intent.putExtra("tips", Parcels.wrap(tips));
                startActivity(intent);
            }
        });
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
        mToolbar.setTitle(getString(R.string.nav_item_tips));
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //refresh display
    private void refreshDisplay() {
        adapter = new TipsListAdapter(this, tipsList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Background Task
    class FetchTipsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // Progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(true);
            pDialog.setMessage(getResources().getString(R.string.lbl_waiting));
            pDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestParams param = new RequestParams();
            //param.add("language", language);

            BackgroundClient.get(serverUrl + "/api/v3/ohkr/disease", param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray diseaseArray = response.getJSONArray("disease");

                            for (int i = 0; i < diseaseArray.length(); i++) {
                                JSONObject obj = diseaseArray.getJSONObject(i);
                                Tips tp = new Tips();
                                tp.setId(obj.getInt(TAG_ID));
                                tp.setTitle(obj.getString(TAG_TITLE));
                                tp.setDescription(obj.getString(TAG_DESCRIPTION));

                                if (!db.isTipExist(tp)) {
                                    db.addTips(tp);
                                } else {
                                    db.updateTip(tp);
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
            tipsList = db.getTipsList();

            if (tipsList.size() > 0) {
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
