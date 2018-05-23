package org.sacids.app.afyadata.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.sacids.app.afyadata.R;
import org.sacids.app.afyadata.activities.CampaignListActivity;
import org.sacids.app.afyadata.activities.FeedbackListActivity;
import org.sacids.app.afyadata.activities.FileManagerTabs;
import org.sacids.app.afyadata.activities.FormChooserList;
import org.sacids.app.afyadata.activities.FormDownloadList;
import org.sacids.app.afyadata.activities.HealthTipsListActivity;
import org.sacids.app.afyadata.activities.InstanceChooserList;
import org.sacids.app.afyadata.activities.InstanceUploaderList;
import org.sacids.app.afyadata.activities.ReportsActivity;
import org.sacids.app.afyadata.adapters.MenuGridAdapter;
import org.sacids.app.afyadata.app.PrefManager;
import org.sacids.app.afyadata.utilities.ApplicationConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    static final String TAG = "Menu";
    PrefManager mPrefManager;

    GridView mGridView;
    MenuGridAdapter mMenuGridAdapter;

    //string array variables
    String[] mMenuTitle;
    Integer[] mMenuImage = {
            R.drawable.ic_fill_blank_form,
            R.drawable.ic_edit_saved_form,
            R.drawable.ic_upload_form,
            R.drawable.ic_trash_bin,
            R.drawable.ic_download_form,
            R.drawable.ic_comment,
            R.drawable.ic_view_sent_form,
            R.drawable.ic_health_tips
    };


    View mRootView;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_menu, container, false);

        mPrefManager = new PrefManager(getContext());
        setViews();

        mMenuTitle = new String[]{
                getResources().getString(R.string.enter_data_button),
                getResources().getString(R.string.review_data),
                getResources().getString(R.string.send_data),
                getResources().getString(R.string.manage_files),
                getResources().getString(R.string.get_forms),
                getResources().getString(R.string.nav_item_feedback),
                getResources().getString(R.string.view_sent_forms),
                getResources().getString(R.string.nav_item_tips)
        };

        //mGridView
        mGridView = (GridView) mRootView.findViewById(R.id.grid_menu);

        mMenuGridAdapter = new MenuGridAdapter(getActivity(), mMenuTitle, mMenuImage);
        mGridView.setAdapter(mMenuGridAdapter);

        //Onclick Listener ListView
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), FormChooserList.class));
                        return;

                    case 1:
                        Intent i = new Intent(getActivity(), InstanceChooserList.class);
                        i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                                ApplicationConstants.FormModes.EDIT_SAVED);
                        startActivity(i);
                        return;

                    case 2:
                        startActivity(new Intent(getActivity(), InstanceUploaderList.class));
                        return;

                    case 3:
                        startActivity(new Intent(getActivity(), FileManagerTabs.class));
                        return;

                    case 4:
                        startActivity(new Intent(getActivity(), FormDownloadList.class));
                        return;

                    case 5:
                        startActivity(new Intent(getActivity(), FeedbackListActivity.class));
                        return;

                    case 6:
                        startActivity(new Intent(getActivity(), InstanceChooserList.class)
                                .putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                                        ApplicationConstants.FormModes.VIEW_SENT)
                        );
                        return;

                    case 7:
                        startActivity(new Intent(getActivity(), HealthTipsListActivity.class));
                        return;

                    default:
                        break;
                }
            }
        });

        return mRootView;
    }

    //set Views
    private void setViews() {
        TextView welcomeText = (TextView) mRootView.findViewById(R.id.welcome_text);
        welcomeText.setText(getString(R.string.lbl_welcome) + ", " + mPrefManager.getFirstName() + " " + mPrefManager.getLastName());
    }

}
