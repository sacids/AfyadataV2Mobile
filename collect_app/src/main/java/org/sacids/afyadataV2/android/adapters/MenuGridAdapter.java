package org.sacids.afyadataV2.android.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sacids.afyadataV2.android.R;

/**
 * Created by administrator on 13/09/2017.
 */

public class MenuGridAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] menuTitle;
    private final Integer[] menuImage;

    public MenuGridAdapter(Activity context, String[] itemName, Integer[] imgId) {
        super(context, R.layout.grid_row_menu, itemName);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.menuTitle = itemName;
        this.menuImage = imgId;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.grid_row_menu, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        txtTitle.setText(menuTitle[position]);
        imageView.setImageResource(menuImage[position]);

        return rowView;
    }
}
