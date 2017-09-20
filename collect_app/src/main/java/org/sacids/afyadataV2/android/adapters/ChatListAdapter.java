package org.sacids.afyadataV2.android.adapters;

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

import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.adapters.model.Feedback;

import java.util.List;

/**
 * Created by administrator on 15/09/2017.
 */

public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Feedback> feedbackList;
    private SharedPreferences mSharedPreferences;
    private String username;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public ChatListAdapter(Context context, List<Feedback> feedback) {
        this.context = context;
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
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        username = mSharedPreferences.getString(KEY_USERNAME, null);


        Feedback feedback = feedbackList.get(position);

        if (feedback.getReplyBy().equals("0") && feedback.getUserName().equals(username)) {
            convertView = li.inflate(R.layout.feedback_item_right, null);
        } else {
            convertView = li.inflate(R.layout.feedback_item_left, null);
        }

        TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        if (Build.VERSION.SDK_INT >= 24) {
            tvMessage.setText(Html.fromHtml(feedback.getMessage(), Build.VERSION.SDK_INT));
        } else {
            tvMessage.setText(Html.fromHtml(feedback.getMessage())); // or for older api
        }

        return convertView;
    }
}

