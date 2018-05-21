package org.sacids.afyadataV2.activities;

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
import org.sacids.afyadataV2.R;
import org.sacids.afyadataV2.app.PrefManager;
import org.sacids.afyadataV2.preferences.PreferenceKeys;
import org.sacids.afyadataV2.utilities.ToastUtils;
import org.sacids.afyadataV2.web.RestClient;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";
    private Context mContext = this;

    //Button and EditText
    private Button btnRegister;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputCode;
    private EditText inputMobile;
    private EditText inputPassword;
    private EditText inputPasswordConfirm;

    //string variables
    private String first_name;
    private String last_name;
    private String code;
    private String mobile;
    private String password;
    private String passwordConfirm;

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
        setContentView(R.layout.activity_register);

        prefManager = new PrefManager(this);

        //setViews
        setViews();

        // Register button Click Event
        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                //check for network first
                connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                mNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if (mNetworkInfo == null || !mNetworkInfo.isConnected()) {
                    showInternetAlertDialog();
                } else {
                    //post variables
                    first_name = inputFirstName.getText().toString();
                    last_name = inputLastName.getText().toString();
                    code = inputCode.getText().toString();
                    mobile = inputMobile.getText().toString();
                    password = inputPassword.getText().toString();
                    passwordConfirm = inputPasswordConfirm.getText().toString();

                    if (first_name == null || first_name.length() == 0) {
                        inputFirstName.setError(getString(R.string.lbl_first_name_required));
                    } else if (last_name == null || last_name.length() == 0) {
                        inputLastName.setError(getString(R.string.lbl_last_name_required));
                    } else if (mobile == null || mobile.length() == 0) {
                        inputMobile.setError(getString(R.string.lbl_mobile_required));
                    } else if (password == null || password.length() == 0) {
                        inputPassword.setError(getString(R.string.lbl_password_required));
                    } else if (password.length() < 8) {
                        inputPassword.setError(getString(R.string.lbl_valid_password_required));
                    } else if (passwordConfirm == null || passwordConfirm.length() == 0) {
                        inputPasswordConfirm.setError(getString(R.string.lbl_password_confirm_required));
                    } else if (!password.equals(passwordConfirm)) {
                        inputPasswordConfirm.setError(getString(R.string.lbl_password_match_required));
                    } else {
                        registerUser();
                    }
                }
            }
        });

        // Link to Login Screen
        findViewById(R.id.btn_link_login).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });
    }

    //setup views
    private void setViews() {
        inputFirstName = (EditText) findViewById(R.id.first_name);
        inputLastName = (EditText) findViewById(R.id.last_name);
        inputCode = (EditText) findViewById(R.id.code);
        inputMobile = (EditText) findViewById(R.id.mobile);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordConfirm = (EditText) findViewById(R.id.password_confirm);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        //set country code
        inputCode.setText(getCountryCode());
        inputCode.setEnabled(false);
    }

    //Register function
    public void registerUser() {
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setMessage(getResources().getString(R.string.lbl_waiting));
        pDialog.show();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mServerURL = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        RequestParams params = new RequestParams();
        params.add("first_name", first_name);
        params.add("last_name", last_name);
        params.add("code", code);
        params.add("mobile", mobile);
        params.add("password", password);
        params.add("password_confirm", passwordConfirm);

        RestClient.post(mServerURL + "/api/v3/auth/register", params, new JsonHttpResponseHandler() {
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

                        //Redirect to Main activity
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
