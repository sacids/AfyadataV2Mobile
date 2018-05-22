package tz.org.sacids.afyadata.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.parceler.Parcels;
import tz.org.sacids.afyadata.R;
import tz.org.sacids.afyadata.activities.FormFeedbackActivity;
import tz.org.sacids.afyadata.activities.FormViewActivity;
import tz.org.sacids.afyadata.adapters.model.Form;

import java.util.List;

/**
 * Created by administrator on 08/02/2018.
 */

public class FormListAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<Form> mFormList;
    private LayoutInflater inflater;

    public FormListAdapter(Activity activity, List<Form> forms) {
        this.mActivity = activity;
        this.mFormList = forms;
    }

    @Override
    public int getCount() {
        return mFormList.size();
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

        if (inflater == null)
            inflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_form, null);

        //TextView
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView instanceName = (TextView) convertView.findViewById(R.id.instanceName);
        TextView feedback = (TextView) convertView.findViewById(R.id.feedback);
        TextView username = (TextView) convertView.findViewById(R.id.username);

        final Form form = mFormList.get(position);
        title.setText(form.getTitle());
        instanceName.setText(form.getInstanceName());
        feedback.setText(String.valueOf(form.getFeedback()));
        username.setText(form.getUser());

        //OnClick feedback
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, FormFeedbackActivity.class).
                        putExtra("form", Parcels.wrap(form)));
            }
        });

        return convertView;
    }
}
