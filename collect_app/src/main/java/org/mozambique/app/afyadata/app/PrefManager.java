package org.mozambique.app.afyadata.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by administrator on 12/09/2017.
 */

public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AfyaDataV2";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    // All Shared Preferences Keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_USER_ID = "user_id";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //setFirstTimeLaunch
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    //UserId
    public void setUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    //Username
    public void setUseName(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }

    public void updateUserName(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUserName() {
        return pref.getString(KEY_USERNAME, null);
    }


    //set FirstName
    public void setFirstName(String firstName) {
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.commit();
    }

    public void updateFirstName(String firstName) {
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.apply();
    }

    public String getFirstName() {
        return pref.getString(KEY_FIRST_NAME, null);
    }

    //set LastName
    public void setLastName(String lastName) {
        editor.putString(KEY_LAST_NAME, lastName);
        editor.commit();
    }

    public void updateLastName(String lastName) {
        editor.putString(KEY_LAST_NAME, lastName);
        editor.apply();
    }

    public String getLastName() {
        return pref.getString(KEY_LAST_NAME, null);
    }

    //createLogin
    public void createLogin(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("username", pref.getString(KEY_USERNAME, null));
        profile.put("first_name", pref.getString(KEY_FIRST_NAME, null));
        profile.put("last_name", pref.getString(KEY_LAST_NAME, null));
        profile.put("user_id", pref.getString(KEY_USER_ID, null));
        return profile;
    }
}
