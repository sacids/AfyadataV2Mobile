package org.sacids.afyadataV2.android.activities;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;
import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.adapters.model.Campaign;
import org.sacids.afyadataV2.android.application.Collect;
import org.sacids.afyadataV2.android.provider.FormsProviderAPI;
import org.sacids.afyadataV2.android.utilities.ImageLoader;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CampaignActivity extends AppCompatActivity {

    private static final String TAG = "Health Tips";

    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Campaign campaign = null;
    private Button btnForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        campaign = (Campaign) Parcels.unwrap(getIntent().getParcelableExtra("campaign"));

        setToolbar();
        setViews();

        //handle button visibility
        if (campaign.getType().equalsIgnoreCase("form")) {

            btnForm.setVisibility(View.VISIBLE);
            btnForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //fill blank form
                    String jrFormId = campaign.getJrFormId();
                    String[] selectionArgs = {jrFormId};
                    String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
                    String[] fields = {FormsProviderAPI.FormsColumns._ID};

                    Cursor formCursor = null;
                    try {
                        formCursor = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, fields, selection, selectionArgs, null);
                        if (formCursor.getCount() == 0) {
                            // form does not already exist locally
                            //Download form from server
                            Intent downloadForms = new Intent(getApplicationContext(),
                                    FormDownloadList.class);
                            startActivity(downloadForms);
                        } else {
                            formCursor.moveToFirst();
                            long idFormsTable = Long.parseLong(formCursor.getString(
                                    formCursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID)));
                            Uri formUri = ContentUris.withAppendedId(
                                    FormsProviderAPI.FormsColumns.CONTENT_URI, idFormsTable);

                            Collect.getInstance().getActivityLogger()
                                    .logAction(this, "onListItemClick: ", formUri.toString());

                            String action = getIntent().getAction();
                            if (Intent.ACTION_PICK.equals(action)) {
                                // caller is waiting on a picked form
                                setResult(RESULT_OK, new Intent().setData(formUri));
                            } else {
                                // caller wants to view/edit a form, so launch form entry activity
                                startActivity(new Intent(Intent.ACTION_EDIT, formUri));
                            }//end of function
                        }


                    } finally {
                        if (formCursor != null) {
                            formCursor.close();
                        }
                    }
                }
            });
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
        mToolbar.setTitle(getString(R.string.nav_item_forms) + " > " + campaign.getTitle());
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //setViews
    public void setViews() {
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        ImageView icon = (ImageView) findViewById(R.id.icon);

        title.setText(campaign.getTitle());

        //setText Description
        if (Build.VERSION.SDK_INT >= 24) {
            description.setText(Html.fromHtml(campaign.getDescription(), Build.VERSION.SDK_INT));
        } else {
            description.setText(Html.fromHtml(campaign.getDescription())); // or for older api
        }

        // Loader image - will be shown before loading image
        int loader = R.drawable.ic_afyadata_one;

        ImageLoader imgLoader = new ImageLoader(this);
        imgLoader.displayImage(campaign.getIcon(), loader, icon);

        btnForm = (Button) findViewById(R.id.btn_fill_form);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
