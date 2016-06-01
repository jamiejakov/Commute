package com.id11303765.commute.view.journey;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.fabtransitionactivity.SheetLayout;
import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;
import com.id11303765.commute.view.StopSearchActivity;


public class JourneyFragment extends Fragment implements View.OnClickListener, SheetLayout.OnFabAnimationEndListener{
    private View mCustomView;
    private float mElevation;
    private SheetLayout mSheetLayout;
    private FloatingActionButton mFab;
    private LinearLayout mSeachButtonsLinearLayout;
    private ImageButton mSwapButton;
    private Button mSearchButton1;
    private Button mSearchButton2;


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
        mSwapButton = (ImageButton) getActivity().findViewById(R.id.fragment_journey_swap_button);
        mSeachButtonsLinearLayout = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_search_buttons_ll);

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
        Intent intent;
        String text;
        switch (v.getId()){
            case R.id.fragment_journey_more_options_ll:
                startActivityForResult(new Intent(getActivity(), JourneyOptionsActivity.class),
                        Constants.JOURNEY_OPTIONS_TO_ACTIVITY_REQUEST);
                break;
            case R.id.fragment_journey_search_fab:
                mSheetLayout.expandFab();
                break;
            case R.id.fragment_journey_departure_button:
                intent = new Intent(getActivity(), StopSearchActivity.class);
                intent.putExtra(Constants.INTENT_REQUEST, Constants.JOURNEY_DEPARTURE_TO_SEARCH_REQUEST);
                text = mSearchButton2.getText().toString();
                if (!text.matches("")){
                    intent.putExtra(Constants.INTENT_SEARCH_EXCLUDE, text);
                }
                startActivityForResult(intent, Constants.JOURNEY_DEPARTURE_TO_SEARCH_REQUEST);
                break;
            case R.id.fragment_journey_destination_button:
                intent = new Intent(getActivity(), StopSearchActivity.class);
                intent.putExtra(Constants.INTENT_REQUEST, Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST);
                text = mSearchButton1.getText().toString();
                if (!text.matches("")){
                    Log.d(Constants.SEARCHTAG, "AHAHAHAHAH " + text);
                    intent.putExtra(Constants.INTENT_SEARCH_EXCLUDE, text);
                }
                startActivityForResult(intent, Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST);
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
        Button a = (Button) mSeachButtonsLinearLayout.getChildAt(0);
        Button b = (Button) mSeachButtonsLinearLayout.getChildAt(1);

        String tempHint = b.getHint().toString();
        TransitionManager.beginDelayedTransition(mSeachButtonsLinearLayout, new ChangeBounds());
        mSeachButtonsLinearLayout.removeView(b);
        b.setHint(a.getHint());
        mSeachButtonsLinearLayout.addView(b, 0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)a.getLayoutParams();
        a.setLayoutParams(b.getLayoutParams());
        b.setLayoutParams(params);
        mSeachButtonsLinearLayout.removeView(a);
        a.setHint(tempHint);
        mSeachButtonsLinearLayout.addView(a);
    }


    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(getActivity(), JourneyRoutesListActivity.class);
        intent.putExtra(Constants.INTENT_SEARCH_JOURNEY_START_STOP, ((Button) mSeachButtonsLinearLayout.getChildAt(0)).getText());
        intent.putExtra(Constants.INTENT_SEARCH_JOURNEY_END_STOP, ((Button) mSeachButtonsLinearLayout.getChildAt(1)).getText());
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
                if (data != null){
                    mSearchButton1.setText(data.getStringExtra(Constants.INTENT_SELECTED_STOP_NAME));
                    mSearchButton1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                }
                break;
            case Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST:
                if (data != null){
                    mSearchButton2.setText(data.getStringExtra(Constants.INTENT_SELECTED_STOP_NAME));
                    mSearchButton2.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                }

                break;
        }
    }


    private void setUpOnClickListeners(){
        LinearLayout optionsButtonLL = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_more_options_ll);
        optionsButtonLL.setOnClickListener(this);


        mSwapButton.setOnClickListener(this);

        mSearchButton1 = (Button) getActivity().findViewById(R.id.fragment_journey_departure_button);
        mSearchButton1.setOnClickListener(this);
        mSearchButton1.setTransformationMethod(null);
        mSearchButton2 = (Button) getActivity().findViewById(R.id.fragment_journey_destination_button);
        mSearchButton2.setOnClickListener(this);
        mSearchButton2.setTransformationMethod(null);

        mFab.setOnClickListener(this);
        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);
    }
}
