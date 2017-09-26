package org.sacids.afyadataV2.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.app.PrefManager;
import org.sacids.afyadataV2.android.preferences.PreferenceKeys;
import org.sacids.afyadataV2.android.preferences.PreferencesActivity;
import org.sacids.afyadataV2.android.web.RestClient;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";

    private Button btnLogin;
    private EditText inputCode;
    private EditText inputMobile;
    private EditText inputPassword;

    private String code;
    private String mobile;
    private String password;

    private ProgressDialog pDialog;
    private Context context = this;
    private PrefManager prefManager;
    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    private static final String KEY_USER_ID = "uid";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new PrefManager(this);

        setViews();

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                code = inputCode.getText().toString();
                mobile = inputMobile.getText().toString();
                password = inputPassword.getText().toString();

                if (mobile == null || mobile.length() == 0) {
                    inputMobile.setError(getString(R.string.lbl_mobile_required));
                } else if (password == null || password.length() == 0) {
                    inputPassword.setError(getString(R.string.lbl_password_required));
                } else {
                    checkLogin();
                }

            }
        });

        //link to register
        findViewById(R.id.btn_link_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(context, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    //setViews
    private void setViews() {
        inputCode = (EditText) findViewById(R.id.code);
        inputMobile = (EditText) findViewById(R.id.mobile);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.button_login);

        //set country code
        inputCode.setText(getCountryCode());
        inputCode.setEnabled(false);
    }

    //Login function
    public void checkLogin() {
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setMessage(getString(R.string.lbl_waiting));
        pDialog.show();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        RequestParams params = new RequestParams();
        params.add("code", code);
        params.add("mobile", mobile);
        params.add("password", password);
        //params.add("gcm_id", gcmRegId);

        RestClient.post(serverUrl + "/api/v3/auth/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                pDialog.dismiss();

                try {
                    boolean error = response.getBoolean("error");

                    if (!error) {
                        String userId = response.getString(KEY_USER_ID);
                        JSONObject obj = response.getJSONObject("user");

                        String username = obj.getString(PreferenceKeys.KEY_USERNAME);
                        String firstName = obj.getString(KEY_FIRST_NAME);
                        String lastName = obj.getString(KEY_LAST_NAME);

                        //login session
                        prefManager.createLogin(userId);

                        //set FirstName and LastName
                        prefManager.setFirstName(firstName);
                        prefManager.setLastName(lastName);

                        //save variables to shared Preference
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(PreferenceKeys.KEY_USERNAME, username);
                        editor.putString(PreferenceKeys.KEY_PASSWORD, password);
                        editor.commit();

                        //start main activity
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    } else {
                        String message = response.getString("error_msg");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.hide();
                Log.d(TAG, "Server response " + responseString);
            }
        });
    }


    //return country_code
    public String getCountryCode() {
        String CountryID = "";
        String CountryCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.country_codes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryCode = g[0];
                break;
            }
        }
        return CountryCode;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
