package org.sacids.app.afyadata.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.app.afyadata.R;
import org.sacids.app.afyadata.adapters.model.Feedback;

import java.util.List;

/**
 * Created by administrator on 13/09/2017.
 */

public class FeedbackListAdapter extends BaseAdapter {
    private Context context;
    private List<Feedback> feedbackList;

    public FeedbackListAdapter(Context context, List<Feedback> feedbackList) {
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
        convertView = li.inflate(R.layout.list_item_feedback, null);

        Feedback feedback = feedbackList.get(position);

        TextView form_name = (TextView) convertView.findViewById(R.id.form_name);
        form_name.setText(feedback.getTitle());

        TextView message = (TextView) convertView.findViewById(R.id.message);

        if (Build.VERSION.SDK_INT >= 24) {
            message.setText(Html.fromHtml(feedback.getMessage(), Build.VERSION.SDK_INT));
        } else {
            message.setText(Html.fromHtml(feedback.getMessage())); // or for older api
        }

        TextView chr_name = (TextView) convertView.findViewById(R.id.chr_name);
        chr_name.setText(feedback.getChrName());

        TextView created_at = (TextView) convertView.findViewById(R.id.created_at);
        created_at.setText(feedback.getDateCreated());

        return convertView;
    }
}

