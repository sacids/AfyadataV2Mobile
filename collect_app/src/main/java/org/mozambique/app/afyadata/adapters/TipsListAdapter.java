package org.mozambique.app.afyadata.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.mozambique.app.afyadata.R;
import org.mozambique.app.afyadata.adapters.model.Tips;

import java.util.List;

/**
 * Created by administrator on 13/09/2017.
 */

public class TipsListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Tips> tipsList;
    private LayoutInflater inflater;

    public TipsListAdapter(Activity activity, List<Tips> tips) {
        this.activity = activity;
        this.tipsList = tips;
    }

    @Override
    public int getCount() {
        return tipsList.size();
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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_tips, null);

        //TextView
        TextView name = (TextView) convertView.findViewById(R.id.name);

        //Tips Model
        Tips tp = tipsList.get(position);

        name.setText(tp.getTitle());

        return convertView;
    }

}
