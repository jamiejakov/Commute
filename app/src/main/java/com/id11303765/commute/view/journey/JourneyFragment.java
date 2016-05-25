package com.id11303765.commute.view.journey;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.fabtransitionactivity.SheetLayout;
import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;
import com.id11303765.commute.view.StationSearchActivity;


public class JourneyFragment extends Fragment implements View.OnClickListener, SheetLayout.OnFabAnimationEndListener{
    private View mCustomView;
    private float mElevation;
    private SheetLayout mSheetLayout;
    private FloatingActionButton mFab;
    private LinearLayout mAppBarStyleLinearLayout;
    private Button mDepartureButton;
    private Button mDestinationButton;
    private LinearLayout mDepartureLL;
    private LinearLayout mDestinationLL;


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

        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fragment_journey_search_fab);
        mSheetLayout = (SheetLayout) getActivity().findViewById(R.id.fragment_journey_sheet_layout);
        mAppBarStyleLinearLayout = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_search_linearLayout);
        mDepartureButton = (Button) getActivity().findViewById(R.id.fragment_journey_departure_button);
        mDestinationButton = (Button) getActivity().findViewById(R.id.fragment_journey_destination_button);

        mDepartureLL = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_from_ll);
        mDestinationLL = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_to_ll);

        setUpOnClickListeners();
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
        AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_appbar);
        appBar.setElevation(mElevation);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_journey_more_options_ll:
                startActivityForResult(new Intent(getActivity(), JourneyOptionsActivity.class),
                        Constants.JOURNEY_OPTIONS_TO_ACTIVITY_REQUEST);
                break;
            case R.id.fragment_journey_search_fab:
                mSheetLayout.expandFab();
                break;
            case R.id.fragment_journey_departure_button:
                startActivityForResult(new Intent(getActivity(), StationSearchActivity.class),
                        Constants.JOURNEY_DEPARTURE_TO_SEARCH_REQUEST);
                break;
            case R.id.fragment_journey_destination_button:
                startActivityForResult(new Intent(getActivity(), StationSearchActivity.class),
                        Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST);
                break;
            case R.id.fragment_journey_swap_button:
                swap();

                /*int selected = mAppBarStyleLinearLayout.indexOfChild(v);
                TransitionManager.beginDelayedTransition(mAppBarStyleLinearLayout, new ChangeBounds());
                ((ViewGroup) mDepartureLL.getParent()).removeView(mDepartureLL);
                mAppBarStyleLinearLayout.addView(mDepartureLL, mAppBarStyleLinearLayout.getChildCount());*/
                break;
        }
    }


    private void swap(){
        View cur = mAppBarStyleLinearLayout.getChildAt(0);
        View v = mAppBarStyleLinearLayout.getChildAt(2);
        TransitionManager.beginDelayedTransition(mAppBarStyleLinearLayout, new ChangeBounds());
        mAppBarStyleLinearLayout.removeView(v);
        mAppBarStyleLinearLayout.addView(v, 0);
        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams)cur.getLayoutParams();
        cur.setLayoutParams(v.getLayoutParams());
        v.setLayoutParams(params);
        mAppBarStyleLinearLayout.removeView(cur);
        mAppBarStyleLinearLayout.addView(cur);
    }


    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(getActivity(), JourneyRoutesListActivity.class);
        startActivityForResult(intent, Constants.JOURNEY_FAB_TO_LIST_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.JOURNEY_FAB_TO_LIST_REQUEST:
                mSheetLayout.contractFab();
                break;
            case Constants.JOURNEY_OPTIONS_TO_ACTIVITY_REQUEST:
                break;
            case Constants.JOURNEY_DEPARTURE_TO_SEARCH_REQUEST:
                break;
            case Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST:
                break;
        }
    }


    private void setUpOnClickListeners(){
        LinearLayout optionsButtonLL = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_more_options_ll);
        optionsButtonLL.setOnClickListener(this);

        ImageButton swapButton = (ImageButton) getActivity().findViewById(R.id.fragment_journey_swap_button);
        swapButton.setOnClickListener(this);

        mDepartureButton.setOnClickListener(this);
        mDestinationButton.setOnClickListener(this);

        mFab.setOnClickListener(this);
        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);
    }
}
