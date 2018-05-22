package tz.org.sacids.afyadata.adapters;

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

import tz.org.sacids.afyadata.R;
import tz.org.sacids.afyadata.adapters.model.FormFeedback;

import java.util.List;

public class FormFeedbackListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FormFeedback> mList;
    private SharedPreferences mSharedPreferences;
    private String mUsername;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public FormFeedbackListAdapter(Context context, List<FormFeedback> feedbackList) {
        this.mContext = context;
        this.mList = feedbackList;
    }

    @Override
    public int getCount() {
        return mList.size();
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

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mUsername = mSharedPreferences.getString(KEY_USERNAME, null);

        FormFeedback formFeedback = mList.get(position);

        if (formFeedback.getReplyBy().equals("0") && formFeedback.getUsername().equals(mUsername))
            convertView = li.inflate(R.layout.feedback_item_right, null);
        else
            convertView = li.inflate(R.layout.feedback_item_left, null);

        TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        if (Build.VERSION.SDK_INT >= 24) {
            tvMessage.setText(Html.fromHtml(formFeedback.getMessage(), Build.VERSION.SDK_INT));
        } else {
            tvMessage.setText(Html.fromHtml(formFeedback.getMessage())); // or for older api
        }

        return convertView;
    }
}


