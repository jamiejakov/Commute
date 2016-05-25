package com.id11303765.commute;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class JourneyFragment extends Fragment {
    View mCustomView;
    float mElevation;

    public JourneyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // mCustomView = inflater.inflate(R.layout.journey_toolbar, container, false);
        //AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_app_bar);
        //appBar.addView(mCustomView);

        return inflater.inflate(R.layout.fragment_journey, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.plan_your_journey);
        AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_appbar);
        mElevation = appBar.getElevation();
        appBar.setElevation(0);
    }

    @Override
    public void onStop(){
       // ((ViewGroup) mCustomView.getParent()).removeView(mCustomView);
        AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_appbar);
        appBar.setElevation(mElevation);
        super.onStop();
    }
}
