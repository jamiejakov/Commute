package com.id11303765.commute.view.journey;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.fabtransitionactivity.SheetLayout;
import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;


public class JourneyFragment extends Fragment implements View.OnClickListener, SheetLayout.OnFabAnimationEndListener{
    private View mCustomView;
    private float mElevation;
    private SheetLayout mSheetLayout;
    private FloatingActionButton mFab;


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
        return inflater.inflate(R.layout.fragment_journey, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.plan_your_journey);

        LinearLayout optionsButtonLL = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_more_options_ll);
        optionsButtonLL.setOnClickListener(this);

        mSheetLayout = (SheetLayout) getActivity().findViewById(R.id.fragment_journey_sheet_layout);
        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fragment_journey_search_fab);
        mFab.setOnClickListener(this);

        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_journey_more_options_ll:
                startActivity(new Intent(getActivity(),JourneyOptionsActivity.class));
                break;
            case R.id.fragment_journey_search_fab:
                mSheetLayout.expandFab();
                break;
        }
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(getActivity(), JourneyRoutesListActivity.class);
        startActivityForResult(intent, Constants.JOURNEY_FAB_TO_LIST_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.JOURNEY_FAB_TO_LIST_REQUEST){
            mSheetLayout.contractFab();
        }
    }
}
