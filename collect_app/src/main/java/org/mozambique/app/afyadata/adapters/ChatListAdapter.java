package org.mozambique.app.afyadata.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.adapters.model.Feedback;
import org.mozambique.app.afyadata.app.PrefManager;

import java.util.List;

/**
 * Created by administrator on 15/09/2017.
 */

public class ChatListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Feedback> feedbackList;
    private SharedPreferences mSharedPreferences;
    private PrefManager mPrefManager;
    private String mUsername;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public ChatListAdapter(Context context, List<Feedback> feedback) {
        this.mContext = context;
        this.feedbackList = feedback;
    }

    @Override
    public int getCount() {
        return feedbackList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mPrefManager = new PrefManager(mContext);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mUsername = mSharedPreferences.getString(KEY_USERNAME, null);

        Feedback feedback = feedbackList.get(position);

        //check for server
        if (feedback.getSender().equalsIgnoreCase("server")) {
            convertView = li.inflate(R.layout.feedback_item_left, null);
        } else {
            if (feedback.getReplyBy().equalsIgnoreCase(mPrefManager.getUserId()))
                convertView = li.inflate(R.layout.feedback_item_right, null);
            else
                convertView = li.inflate(R.layout.feedback_item_left, null);
        }

        //textViewMessage
        TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        if (Build.VERSION.SDK_INT >= 24)
            tvMessage.setText(Html.fromHtml(feedback.getMessage(), Build.VERSION.SDK_INT));
        else
            tvMessage.setText(Html.fromHtml(feedback.getMessage())); // or for older api

        return convertView;
    }
}

