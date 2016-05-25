package com.id11303765.commute.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.id11303765.commute.R;


public class CommuteFragment extends Fragment {
    View mTabView;

    public CommuteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*mTabView = inflater.inflate(R.layout.app_bar_commute, container, false);
        AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_appbar);
        appBar.addView(mTabView);*/

        return inflater.inflate(R.layout.fragment_commute, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.commute);

        /*TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.app_bar_commute_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("MOSHI MOSHI"));
        tabLayout.addTab(tabLayout.newTab().setText("DIS IS DOG"));
        tabLayout.addTab(tabLayout.newTab().setText("such wow"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);*/
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        //((ViewGroup) mTabView.getParent()).removeView(mTabView);
        super.onStop();
    }
}
