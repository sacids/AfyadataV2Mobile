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
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import tz.org.sacids.afyadata.R;
import tz.org.sacids.afyadata.adapters.CampaignListAdapter;
import tz.org.sacids.afyadata.adapters.model.Campaign;
import tz.org.sacids.afyadata.database.AfyaDataV2DB;
import tz.org.sacids.afyadata.preferences.PreferenceKeys;
import tz.org.sacids.afyadata.web.BackgroundClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CampaignListActivity extends AppCompatActivity {

    private static String TAG = "Campaign";
    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Context context = this;
    private ProgressDialog pDialog;

    private List<Campaign> campaignList = new ArrayList<Campaign>();
    private GridView gridView;
    private CampaignListAdapter adapter;

    //AfyaData database
    private AfyaDataV2DB db;
    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    //variable Tag
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TYPE = "type";
    private static final String TAG_ICON = "icon";
    private static final String TAG_JR_FORM_ID = "jr_form_id";
    private static final String TAG_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);

        setToolbar();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //database
        db = new AfyaDataV2DB(this);

        gridView = (GridView) findViewById(R.id.grid_view);

        campaignList = db.getCampaignList();

        if (campaignList.size() > 0) {
            refreshDisplay();
        } else {
            Toast.makeText(context, getString(R.string.msg_nothing_display), Toast.LENGTH_LONG).show();
        }

        //check network connectivity and fetch campaign
        if (ni == null || !ni.isConnected())
            Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchCampaignTask().execute();

        //Onclick item
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Campaign campaign = campaignList.get(position);

                Intent intent = new Intent(context, CampaignActivity.class);
                intent.putExtra("campaign", Parcels.wrap(campaign));
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
        mToolbar.setTitle(getString(R.string.nav_item_forms));
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //refresh display
    private void refreshDisplay() {
        adapter = new CampaignListAdapter(this, campaignList);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Background Task
    class FetchCampaignTask extends AsyncTask<Void, Void, Void> {

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

            BackgroundClient.get(serverUrl + "/api/v3/campaign", param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray campaignArray = response.getJSONArray("campaign");

                            for (int i = 0; i < campaignArray.length(); i++) {
                                JSONObject obj = campaignArray.getJSONObject(i);
                                Campaign cmp = new Campaign();

                                cmp.setId(obj.getInt(TAG_ID));
                                cmp.setTitle(obj.getString(TAG_TITLE));
                                cmp.setType(obj.getString(TAG_TYPE));
                                cmp.setIcon(obj.getString(TAG_ICON));
                                cmp.setJrFormId(obj.getString(TAG_JR_FORM_ID));
                                cmp.setDescription(obj.getString(TAG_DESCRIPTION));

                                if (!db.isCampaignExist(cmp)) {
                                    db.addCampaign(cmp);
                                } else {
                                    db.updateCampaign(cmp);
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
            campaignList = db.getCampaignList();

            if (campaignList.size() > 0) {
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
