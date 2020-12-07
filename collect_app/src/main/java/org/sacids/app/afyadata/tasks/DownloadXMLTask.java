package org.sacids.app.afyadata.tasks;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.app.afyadata.R;
import org.sacids.app.afyadata.adapters.model.Feedback;
import org.sacids.app.afyadata.adapters.model.Forms;
import org.sacids.app.afyadata.database.AfyaDataV2DB;
import org.sacids.app.afyadata.preferences.PreferenceKeys;
import org.sacids.app.afyadata.web.BackgroundClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DownloadXMLTask extends IntentService {

    private static String TAG = "DownloadXML";

    // A integer, that identifies each notification uniquely
    public static final int NOTIFICATION_ID = 1;

    SharedPreferences mSharedPreferences;
    String mServerURL;
    String mUserName;

    String mURL;
    String mFileName;

    //AfyaData database
    private AfyaDataV2DB mDB;

    //TAGS
    private static final String TAG_ID = "id";
    private static final String TAG_FORM_ID = "form_id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DOWNLOAD_URL = "download_url";

    public DownloadXMLTask() {
        super("DownloadXMLTask");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mUserName = mSharedPreferences.getString(PreferenceKeys.KEY_USERNAME, null);
            mServerURL = mSharedPreferences.getString(PreferenceKeys.KEY_SERVER_URL,
                    getString(R.string.default_server_url));

            //database
            mDB = new AfyaDataV2DB(getApplicationContext());
            fetchForms();
        }
    }


    //fetchForms from server
    private void fetchForms() {
        //params
        RequestParams param = new RequestParams();
        param.add("username", mUserName);

        //Background service
        BackgroundClient.get(mServerURL + "/api/v3/forms/lists", param, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("response", response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray formsArray = response.getJSONArray("forms");

                        for (int i = 0; i < formsArray.length(); i++) {
                            JSONObject obj = formsArray.getJSONObject(i);

                            Forms forms = new Forms();
                            forms.setId(obj.getLong(TAG_ID));
                            forms.setFormId(obj.getString(TAG_FORM_ID));
                            forms.setTitle(obj.getString(TAG_TITLE));
                            forms.setDescription(obj.getString(TAG_DESCRIPTION));
                            forms.setDownloadUrl(obj.getString(TAG_DOWNLOAD_URL));
                            mURL = obj.getString(TAG_DOWNLOAD_URL);

                            //TODO: insert/update into database
                            if (!mDB.isFormExist(forms))
                                mDB.addForm(forms);
                            else
                                mDB.updateForm(forms);

                            //TODO: Download file
                            downLoadFile(mURL);
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
    }

    //download file
    private void downLoadFile(String downloadURL) {
        setupFileName(downloadURL);

        String destinationPath = setupDestinationFolderPath();
        Log.i(TAG, "destinationPath=" + destinationPath);

        File savedFile = new File(destinationPath, mFileName);


        //check file existence
        if (!savedFile.exists()) {

            InputStream stream = null;
            FileOutputStream fos = null;
            try {

                URL url = new URL(downloadURL);
                stream = url.openConnection().getInputStream();
                InputStreamReader reader = new InputStreamReader(stream);
                fos = new FileOutputStream(savedFile.getPath());
                int next = -1;
                while ((next = reader.read()) != -1) {
                    fos.write(next);
                }
                // successfully finished
                //result = Activity.RESULT_OK;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    //setupFileName
    private void setupFileName(String downloadUri) {
        Log.i(TAG, "setupFileName");
        mFileName = null;

        String sa[] = downloadUri.split("/");

        if (sa.length > 0) {
            mFileName = sa[sa.length - 1];
            sa = mFileName.split("\\?");
            if (sa.length > 0) {
                mFileName = sa[0];
            }
        }

        Log.i(TAG, "mFileName=" + mFileName);
    }

    //setupDestination Folder Path
    private String setupDestinationFolderPath() {
        Log.i(TAG, "setupDestinationFolderPath");
        String destinationPath = null;

        if (isSdCardAvailable()) {
            StringBuilder dp = new StringBuilder();
            File mFile = null;

            //append path
            dp.append(Environment.getExternalStoragePublicDirectory(""));
            dp.append("/odk/forms/");

            destinationPath = dp.toString();
            mFile = new File(destinationPath);
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
        }
        return (destinationPath);
    }

    /**
     * Can we read and write to the sdcard?
     *
     * @return
     */
    private boolean isSdCardAvailable() {
        String storageState = Environment.getExternalStorageState();
        boolean available = false;

        if (Environment.MEDIA_MOUNTED.equals(storageState)) {
            available = true;
        }

        return available;
    }
}
