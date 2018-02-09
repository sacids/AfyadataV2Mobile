package org.sacids.afyadataV2.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.afyadataV2.R;
import org.sacids.afyadataV2.adapters.model.FormFeedback;

import java.util.List;

public class FormFeedbackListAdapter extends BaseAdapter {
    private Context context;
    private List<FormFeedback> feedbackList;

    public FormFeedbackListAdapter(Context context, List<FormFeedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
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

        FormFeedback formFeedback = feedbackList.get(position);

        if (formFeedback.getSender().equals("server"))
            convertView = li.inflate(R.layout.feedback_item_left, null);
        else
            convertView = li.inflate(R.layout.feedback_item_right, null);

        TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        if (Build.VERSION.SDK_INT >= 24) {
            tvMessage.setText(Html.fromHtml(formFeedback.getMessage(), Build.VERSION.SDK_INT));
        } else {
            tvMessage.setText(Html.fromHtml(formFeedback.getMessage())); // or for older api
        }

        return convertView;
    }
}


