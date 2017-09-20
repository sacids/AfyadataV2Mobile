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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.adapters.FormDetailsAdapter;
import org.sacids.afyadataV2.android.adapters.model.FormDetails;
import org.sacids.afyadataV2.android.adapters.model.SearchableData;
import org.sacids.afyadataV2.android.adapters.model.SearchableForm;
import org.sacids.afyadataV2.android.database.AfyaDataV2DB;
import org.sacids.afyadataV2.android.web.RestClient;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "Search";

    private Toolbar mToolbar;
    private ActionBar actionBar;

    private Context context = this;
    private ProgressDialog pDialog;

    private AfyaDataV2DB db;

    private List<FormDetails> formList = new ArrayList<FormDetails>();
    private ListView listView;
    private FormDetailsAdapter adapter;

    private static final String TAG_LABEL = "label";
    private static final String TAG_VALUE = "value";

    private Spinner inputForm;
    private Spinner inputLabel;
    private EditText inputValue;

    private long formId;
    private String jrFormId;
    private String label;
    private String value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = new AfyaDataV2DB(this);

        setToolbar();

        setViews();

        loadSpinnerForm();

        //listViews
        listView = (ListView) findViewById(R.id.list_search);
        adapter = new FormDetailsAdapter(this, formList);
        listView.setAdapter(adapter);

        //onclick
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = inputValue.getText().toString();

                if (value == null || value.length() == 0) {
                    inputValue.setError(getString(R.string.required_search_value));
                } else {
                    fetchFormDetails();
                }
            }
        });
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
        mToolbar.setTitle(getString(R.string.nav_item_search));
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //set Up View
    public void setViews() {
        inputForm = (Spinner) findViewById(R.id.form);
        inputLabel = (Spinner) findViewById(R.id.label);
        inputValue = (EditText) findViewById(R.id.value);
    }

    //spinner for form
    private void loadSpinnerForm() {
        // Spinner Drop down elements
        final List<SearchableForm> formList = db.getSearchableFormsList();

        ArrayList<String> StringFormList = new ArrayList<>();
        for (SearchableForm form : formList) {
            StringFormList.add(form.getTitle());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, StringFormList);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        inputForm.setAdapter(dataAdapter);

        inputForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchableForm sf = formList.get(position);
                formId = sf.getId(); //formId
                jrFormId = sf.getJrFormId(); //jrFormId
                Log.d(TAG, "selected " + sf.toString());

                //load another data
                loadSpinnerData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //spinner for data value
    private void loadSpinnerData() {
        // Spinner Drop down elements
        final List<SearchableData> dataList = db.getSearchableDataList(formId);

        ArrayList<String> StringDataList = new ArrayList<>();
        for (SearchableData data : dataList) {
            StringDataList.add(data.getLabel());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, StringDataList);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        inputLabel.setAdapter(dataAdapter);

        inputLabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchableData sf = dataList.get(position);
                label = sf.getValue();
                Log.d(TAG, "selected " + sf.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //get form details from server
    private void fetchFormDetails() {
        // Progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        pDialog.setMessage(getResources().getString(R.string.lbl_waiting));
        pDialog.show();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        RequestParams params = new RequestParams();
        params.add("form_id", formId + "");
        params.add("search_for", value);
        params.add("field", label);

        RestClient.get("/api/v3/search/form", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray formArray = response.getJSONArray("form_details");

                        for (int i = 0; i < formArray.length(); i++) {
                            JSONObject obj = formArray.getJSONObject(i);
                            FormDetails formDetails = new FormDetails();
                            formDetails.setLabel(obj.getString(TAG_LABEL));
                            formDetails.setValue(obj.getString(TAG_VALUE));
                            //add to list
                            formList.add(formDetails);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, getString(R.string.msg_nothing_display), Toast.LENGTH_SHORT).show();
                    }
                    //progress visibility
                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "Failed " + responseString);
                //progress visibility
                pDialog.dismiss();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
