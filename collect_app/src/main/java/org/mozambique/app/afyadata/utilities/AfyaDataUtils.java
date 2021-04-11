package org.mozambique.app.afyadata.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import org.mozambique.app.afyadata.app.Preferences;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static org.mozambique.app.afyadata.preferences.PreferenceKeys.KEY_APP_LANGUAGE;

/**
 * Created by administrator on 13/09/2017.
 */

public class AfyaDataUtils {

    private static final String MOBILE_PATTERN = "^[0-9]{12}$";


    /**
     * Regex to validate the mobile number
     * mobile number should be of 12 digits length
     *
     * @param mobile
     * @return
     */
    public static boolean isValidPhoneNumber(String mobile) {
        return mobile.matches(MOBILE_PATTERN);
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }


    public static void loadLanguage(Context context) {
        Locale locale = new Locale(getLangCode(context));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        LocaleHelper localeHelper = new LocaleHelper();
        localeHelper.updateLocale(context);
    }

    public static String getLangCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);
        String langCode = preferences.getString(Preferences.LANGUAGE, AfyaDataLanguages.SWAHILI.getLanguage());
        return langCode;
    }

    //setAppLanguage
    public static void setAppLanguage(Activity context, String selectedLanguage) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            conf.setLocale(locale);
        } else {
            conf.locale = new Locale(selectedLanguage);
            res.updateConfiguration(conf, dm);
        }

        SharedPreferences mSharedPreferences = context.getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Preferences.DEFAULT_LOCALE, selectedLanguage);
        editor.putBoolean(Preferences.FIRST_TIME_APP_OPENED, false);
        editor.putString(Preferences.LANGUAGE, selectedLanguage);
        editor.apply();

        SharedPreferences.Editor edit = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        edit.putString(KEY_APP_LANGUAGE, selectedLanguage);
        edit.apply();
        LocaleHelper localeHelper = new LocaleHelper();
        localeHelper.updateLocale(context);

        context.recreate();
    }
}