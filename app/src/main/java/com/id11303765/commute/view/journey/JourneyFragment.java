package com.id11303765.commute.view.journey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.fabtransitionactivity.SheetLayout;
import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;
import com.id11303765.commute.view.StopSearchActivity;

import java.util.ArrayList;

/**
 * Fragment giving user the option to search for a start and stop location
 * Set the time when to depart/arrive
 * Set other options...
 */
public class JourneyFragment extends Fragment implements View.OnClickListener, SheetLayout.OnFabAnimationEndListener {
    private float mElevation;
    private SheetLayout mSheetLayout;
    private FloatingActionButton mSearchFab;
    private LinearLayout mSearchButtonsLinearLayout;
    private ImageButton mSwapButton;
    private Button mSearchButton1;
    private Button mSearchButton2;
    private Button mTimeButton;
    private int mRotationDirection;
    private boolean mDepartAtOption;

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

        mSearchFab = (FloatingActionButton) getActivity().findViewById(R.id.fragment_journey_search_fab);
        mSheetLayout = (SheetLayout) getActivity().findViewById(R.id.fragment_journey_sheet_layout);
        mSwapButton = (ImageButton) getActivity().findViewById(R.id.fragment_journey_swap_button);
        mSearchButtonsLinearLayout = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_search_buttons_ll);
        mSearchButton1 = (Button) getActivity().findViewById(R.id.fragment_journey_departure_button);
        mSearchButton2 = (Button) getActivity().findViewById(R.id.fragment_journey_destination_button);
        mTimeButton = (Button) getActivity().findViewById(R.id.fragment_journey_time_option_button);

        setUpElements();
    }

    @Override
    public void onStart() {
        super.onStart();
        AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_appbar);

        // Set up ripple effects on views to mimic buttons
        // Lollipop and above only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mElevation = appBar.getElevation();
            appBar.setElevation(0);
            mSearchButton1.setBackground(getActivity().getDrawable(R.drawable.journey_button_shape));
            mSearchButton2.setBackground(getActivity().getDrawable(R.drawable.journey_button_shape));
            mSwapButton.setBackground(getActivity().getDrawable(R.drawable.swap_button_ripple));
        }
    }

    @Override
    public void onStop() {
        AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_appbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBar.setElevation(mElevation);
        }
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        ArrayList<String> excludeArray = new ArrayList<>();
        switch (v.getId()) {
            case R.id.fragment_journey_time_option_button:
                intent = new Intent(getActivity(), JourneyTimeSelectionActivity.class);
                intent.putExtra(Constants.INTENT_TIME_OPTION, mTimeButton.getText().toString());
                startActivityForResult(intent, Constants.JOURNEY_TIME_OPTIONS_TO_ACTIVITY_REQUEST);
                break;
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

                addOtherStopToExcludedArray(excludeArray, mSearchButton2);
                addAirportToExclusionListIfSelected(excludeArray);
                if (!excludeArray.isEmpty()) {
                    intent.putExtra(Constants.INTENT_SEARCH_EXCLUDE, excludeArray);
                }

                startActivityForResult(intent, Constants.JOURNEY_DEPARTURE_TO_SEARCH_REQUEST);
                break;
            case R.id.fragment_journey_destination_button:
                intent = new Intent(getActivity(), StopSearchActivity.class);
                intent.putExtra(Constants.INTENT_REQUEST, Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST);

                addOtherStopToExcludedArray(excludeArray, mSearchButton1);
                addAirportToExclusionListIfSelected(excludeArray);
                if (!excludeArray.isEmpty()) {
                    intent.putExtra(Constants.INTENT_SEARCH_EXCLUDE, excludeArray);
                }

                startActivityForResult(intent, Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST);
                break;
            case R.id.fragment_journey_swap_button:
                swap();
                break;
        }
    }

    /**
     * Launch next Activity when FAB animation ends
     */
    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(getActivity(), JourneyRoutesListActivity.class);
        intent.putExtra(Constants.INTENT_SEARCH_JOURNEY_START_STOP, ((Button) mSearchButtonsLinearLayout.getChildAt(0)).getText());
        intent.putExtra(Constants.INTENT_SEARCH_JOURNEY_END_STOP, ((Button) mSearchButtonsLinearLayout.getChildAt(1)).getText());
        intent.putExtra(Constants.INTENT_TIME_OPTION, mTimeButton.getText().toString());
        intent.putExtra(Constants.INTENT_TIME_DEPART_AT_BOOL, mDepartAtOption);
        startActivityForResult(intent, Constants.JOURNEY_FAB_TO_LIST_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.JOURNEY_FAB_TO_LIST_REQUEST:
                mSheetLayout.contractFab();
                break;
            case Constants.JOURNEY_DEPARTURE_TO_SEARCH_REQUEST:
                if (data != null) {
                    mSearchButton1.setText(data.getStringExtra(Constants.INTENT_SELECTED_STOP_NAME));
                    mSearchButton1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    checkAndEnableFab();
                }
                break;
            case Constants.JOURNEY_DESTINATION_TO_SEARCH_REQUEST:
                if (data != null) {
                    mSearchButton2.setText(data.getStringExtra(Constants.INTENT_SELECTED_STOP_NAME));
                    mSearchButton2.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    checkAndEnableFab();
                }
                break;
            case Constants.JOURNEY_TIME_OPTIONS_TO_ACTIVITY_REQUEST:
                if (data != null) {
                    mTimeButton.setText(data.getStringExtra(Constants.INTENT_TIME_OPTION));
                    mDepartAtOption = (data.getBooleanExtra(Constants.INTENT_TIME_DEPART_AT_BOOL, true));
                }
                break;
        }
    }

    /**
     * Checks whether the other stop has already been selected
     * And add it to the exclusion array for the search if it has
     * Preventing users from selecting the same stop twice.
     *
     * @param excludeArray - array to add the stop to
     * @param button       - other stop selection button to check
     */
    private void addOtherStopToExcludedArray(ArrayList<String> excludeArray, Button button) {
        String text = button.getText().toString();
        if (!text.matches("")) {
            excludeArray.add(text);
        }
    }

    /**
     * Check whether the Avoid Airport Charge option has been selected
     * If it has, prevent users from selecting airport as their destination.
     * Thus they avoid the charge!
     *
     * @param excludeArray - array to add the stops to
     */
    private void addAirportToExclusionListIfSelected(ArrayList<String> excludeArray) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        boolean avoidAirport = sharedPreferences.getBoolean(getString(R.string.avoid_airport_fee), true);
        if (avoidAirport) {
            excludeArray.add(getString(R.string.domestic_airport));
            excludeArray.add(getString(R.string.international_airport));
        }
    }

    /**
     * Swap the from and to locations
     * with a pretty transition animation
     */
    private void swap() {
        Button a = (Button) mSearchButtonsLinearLayout.getChildAt(0);
        Button b = (Button) mSearchButtonsLinearLayout.getChildAt(1);

        RotateAnimation rotate = new RotateAnimation(0f, mRotationDirection * 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setFillAfter(true);
        rotate.setDuration(300);
        mSwapButton.startAnimation(rotate);
        mRotationDirection *= -1;

        String tempHint = b.getHint().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(mSearchButtonsLinearLayout, new ChangeBounds());
        }

        mSearchButtonsLinearLayout.removeView(b);
        b.setHint(a.getHint());
        mSearchButtonsLinearLayout.addView(b, 0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) a.getLayoutParams();
        a.setLayoutParams(b.getLayoutParams());
        b.setLayoutParams(params);
        mSearchButtonsLinearLayout.removeView(a);
        a.setHint(tempHint);
        mSearchButtonsLinearLayout.addView(a);

    }

    /**
     * Check whether both TO and FROM locations have been selected
     * Only enable FAB after they are both selected
     * Preventing user from proceeding until this is done
     */
    private void checkAndEnableFab() {
        String startStopName = ((Button) mSearchButtonsLinearLayout.getChildAt(0)).getText().toString();
        String endStopName = ((Button) mSearchButtonsLinearLayout.getChildAt(1)).getText().toString();
        if (!startStopName.isEmpty() && !endStopName.isEmpty()) {
            mSearchFab.setEnabled(true);
            mSearchFab.getDrawable().setAlpha(Constants.OPAQUE);
        } else {
            mSearchFab.setEnabled(false);
            mSearchFab.getDrawable().setAlpha(Constants.DESELECTED);
        }
    }

    /**
     * Set up on click listeners and other stuff on the UI elements
     * before doing stuff with them later on.
     */
    private void setUpElements() {
        LinearLayout optionsButtonLL = (LinearLayout) getActivity().findViewById(R.id.fragment_journey_more_options_ll);
        optionsButtonLL.setOnClickListener(this);

        mSwapButton.setOnClickListener(this);

        mSearchButton1.setOnClickListener(this);
        mSearchButton1.setTransformationMethod(null);
        mSearchButton2.setOnClickListener(this);
        mSearchButton2.setTransformationMethod(null);

        mSearchFab.setOnClickListener(this);
        mSheetLayout.setFab(mSearchFab);
        mSheetLayout.setFabAnimationEndListener(this);

        mTimeButton.setOnClickListener(this);

        mSearchFab.setEnabled(false);
        mSearchFab.getDrawable().setAlpha(100);
        mRotationDirection = 1;
        mDepartAtOption = true;
    }


}
