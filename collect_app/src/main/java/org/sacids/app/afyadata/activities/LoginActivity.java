package org.sacids.app.afyadata.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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
import org.sacids.app.afyadata.R;
import org.sacids.app.afyadata.app.PrefManager;
import org.sacids.app.afyadata.preferences.PreferenceKeys;
import org.sacids.app.afyadata.web.RestClient;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    Context mContext = this;

    private Button btnLogin;
    private EditText inputCode;
    private EditText inputMobile;
    private EditText inputPassword;

    private String code;
    private String mobile;
    private String password;

    private ProgressDialog pDialog;
    private PrefManager prefManager;
    private SharedPreferences mSharedPreferences;
    ConnectivityManager connectivityManager;
    NetworkInfo mNetworkInfo;
    String mServerURL;

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
                //check for network first
                connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                mNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if (mNetworkInfo == null || !mNetworkInfo.isConnected()) {
                    showInternetAlertDialog();
                } else {
                    //post variables
                    code = inputCode.getText().toString();
                    mobile = inputMobile.getText().toString();
                    password = inputPassword.getText().toString();

                    if (mobile == null || mobile.length() == 0) {
                        inputMobile.setError(getString(R.string.lbl_mobile_required));
                    } else if (password == null || password.length() == 0) {
                        inputPassword.setError(getString(R.string.lbl_password_required));
                    } else {
                        userLogin();
                    }
                }
            }
        });

        //link to register
        findViewById(R.id.btn_link_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
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

    //Login
    public void userLogin() {
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setMessage(getString(R.string.lbl_waiting));
        pDialog.show();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mServerURL = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        RequestParams params = new RequestParams();
        params.add("code", code);
        params.add("mobile", mobile);
        params.add("password", password);
        //params.add("gcm_id", gcmRegId);

        RestClient.post(mServerURL + "/api/v3/auth/login", params, new JsonHttpResponseHandler() {
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
                        startActivity(new Intent(mContext, MainActivity.class));
                        finish();
                    } else {
                        String message = response.getString("error_msg");
                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
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

    //show alert dialog
    private void showInternetAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(getString(R.string.app_name));
        alert.setCancelable(false);
        alert.setMessage(getString(R.string.no_connection));
        alert.setNegativeButton(getString(R.string.lbl_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dInterface, int arg1) {
                        dInterface.dismiss();
                    }
                });
        alert.show();
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
