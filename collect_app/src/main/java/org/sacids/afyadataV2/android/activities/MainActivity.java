package org.sacids.afyadataV2.android.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.app.PrefManager;
import org.sacids.afyadataV2.android.fragments.MenuFragment;
import org.sacids.afyadataV2.android.preferences.PreferencesActivity;
import org.sacids.afyadataV2.android.tasks.DownloadSearchableForm;
import org.sacids.afyadataV2.android.web.RestClient;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private Toolbar mToolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private MenuFragment currentFragment;

    private Context context = this;
    private PrefManager prefManager;

    private PackageInfo pInfo;
    private long currentVersion;
    private long newVersion;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);

        //default fragment view
        if (savedInstanceState == null) {
            currentFragment = new MenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, currentFragment).commit();
        }

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                Fragment fragment = null;

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.action_home:
                        //Remain here
                        return true;

                    case R.id.action_about:
                        startActivity(new Intent(context, AboutActivity.class));
                        return true;

                    case R.id.action_symptoms:
                        startActivity(new Intent(context, SymptomsListActivity.class));
                        return true;

                    case R.id.action_search:
                        startActivity(new Intent(context, SearchActivity.class));
                        return true;

                    case R.id.action_settings:
                        //settings
                        startActivity(new Intent(context, PreferencesActivity.class));
                        return true;

                    case R.id.action_sign_out:
                        //clear session
                        prefManager.clearSession();
                        //start new Intent
                        Intent signOut = new Intent(context, LoginActivity.class);
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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
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
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        // Start the service
        startService(new Intent(this, DownloadSearchableForm.class));

        //check App updates
        packageName = getPackageName();
        updateAppVersion();
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
        RestClient.get("/api/v3/auth/version", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "Server response " + response.toString());
                try {
                    currentVersion = getAppVersionCode();
                    if (response.getString("status")
                            .equalsIgnoreCase("success")) {
                        JSONObject jobj = response.getJSONObject("app_version");
                        newVersion = jobj.getLong("version");
                        if (newVersion > currentVersion)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionCode;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
