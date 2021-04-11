package org.mozambique.app.afyadata.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.mozambique.app.afyadata.R;

import org.mozambique.app.afyadata.app.PrefManager;
import org.mozambique.app.afyadata.fragments.MenuFragment;
import org.mozambique.app.afyadata.preferences.PreferenceKeys;
import org.mozambique.app.afyadata.preferences.PreferencesActivity;
import org.mozambique.app.afyadata.receivers.FeedbackReceiver;
import org.mozambique.app.afyadata.tasks.DownloadSearchableForm;
import org.mozambique.app.afyadata.tasks.DownloadXMLTask;
import org.mozambique.app.afyadata.utilities.ToastUtils;
import org.mozambique.app.afyadata.web.RestClient;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Main";

    Toolbar mToolbar;
    Context mContext = this;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    MenuFragment mCurrentFragment;


    PrefManager mPrefManager;
    SharedPreferences mSharedPreferences;
    ConnectivityManager connectivityManager;
    NetworkInfo mNetworkInfo;
    private String serverUrl;

    //TODO: app version variables
    PackageInfo mPackageInfo;
    private long mCurrentVersion;
    private long newVersion;
    private String packageName;

    //Pending Intent variables
    PendingIntent mPendingIntent;
    AlarmManager mAlarmManager;

    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        mNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (mNetworkInfo == null || !mNetworkInfo.isConnected()) {
            ToastUtils.showLongToast(R.string.no_connection);
        }

        mPrefManager = new PrefManager(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //default fragment view
        if (savedInstanceState == null) {
            mCurrentFragment = new MenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, mCurrentFragment).commit();
        }

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //Initializing NavigationView
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                Fragment fragment = null;

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.action_home:
                        //Remain here
                        return true;

                    case R.id.action_about:
                        startActivity(new Intent(mContext, AboutActivity.class));
                        return true;

                    case R.id.action_symptoms:
                        startActivity(new Intent(mContext, SymptomsListActivity.class));
                        return true;

                    case R.id.action_search:
                        startActivity(new Intent(mContext, SearchActivity.class));
                        return true;

                    case R.id.action_settings:
                        //settings
                        startActivity(new Intent(mContext, PreferencesActivity.class));
                        return true;

                    case R.id.action_language:
                        //settings
                        startActivity(new Intent(mContext, LanguageActivity.class));
                        return true;

                    case R.id.action_sign_out:
                        //clear session
                        mPrefManager.clearSession();
                        //start new Intent
                        Intent signOut = new Intent(mContext, LoginActivity.class);
                        signOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(signOut);
                        finish();
                        return true;

                    default:
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //check App updates
        packageName = getPackageName();
        updateAppVersion();

        // Start the service
        startService(new Intent(this, DownloadSearchableForm.class));
        startService(new Intent(this, DownloadXMLTask.class));

        // Retrieve a PendingIntent that will perform a broadcast
        Intent feedbackIntent = new Intent(this, FeedbackReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, feedbackIntent, 0);

        // Set the alarm here.
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1800000; // 30 minutes
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, mPendingIntent);

        // Get the message from the intent
        Intent intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String formFeedback = extras.getString("feedback");
            if (formFeedback.equalsIgnoreCase("formFeedback")) {
                startActivity(new Intent(this, FeedbackListActivity.class));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            default:
                return false;
        }
    }


    //update App Version
    public void updateAppVersion() {
        RestClient.get(serverUrl + "/api/v3/auth/version", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "Server response " + response.toString());
                try {
                    mCurrentVersion = getAppVersionCode();
                    if (response.getString("status")
                            .equalsIgnoreCase("success")) {
                        JSONObject jobj = response.getJSONObject("app_version");
                        newVersion = jobj.getLong("version");
                        if (newVersion > mCurrentVersion)
                            displayUpdateAppDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "on Failure " + responseString);
            }
        });

    }

    //display update dialog
    public void displayUpdateAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.update_status));
        builder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id="
                                            + packageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id="
                                            + packageName)));
                        }
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // show number of cancel
                    }
                });
        builder.create().show();
    }

    public long getAppVersionCode() throws PackageManager.NameNotFoundException {
        mPackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return mPackageInfo.versionCode;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.on_back_pressed), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 1000);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
