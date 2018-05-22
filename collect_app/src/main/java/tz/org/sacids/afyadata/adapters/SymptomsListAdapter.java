package tz.org.sacids.afyadata.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import tz.org.sacids.afyadata.R;
import tz.org.sacids.afyadata.adapters.model.Symptom;

import java.util.List;

/**
 * Created by administrator on 16/09/2017.
 */

public class SymptomsListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Symptom> symptomsList;
    private LayoutInflater inflater;

    public SymptomsListAdapter(Activity activity, List<Symptom> symptom) {
        this.activity = activity;
        this.symptomsList = symptom;
    }

    @Override
    public int getCount() {
        return symptomsList.size();
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
            convertView = inflater.inflate(R.layout.list_row_symptom, null);

        //TextView
        TextView title = (TextView) convertView.findViewById(R.id.title);

        //Symptom Model
        Symptom symptom = symptomsList.get(position);
        title.setText(symptom.getTitle());

        return convertView;
    }

}
