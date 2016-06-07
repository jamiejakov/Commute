package com.id11303765.commute.view;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.model.Commute;
import com.id11303765.commute.model.CommuteManager;
import com.id11303765.commute.model.Route;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Class which displays the times for the chosen commute
 * Commute preferences are in the SettingsActivity
 */
public class CommuteFragment extends Fragment implements View.OnClickListener {
    private ArrayList<Commute> mCommuteLegs;
    private Commute mSelectedCommute;
    private Timetable mSelectedTimetable;
    private boolean mIsTravelTo;
    private FloatingActionButton mSwapFab;
    private CountDownTimer mCountdownTimer;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private int mStopTimeCount;
    private TextView mNextServiceText;
    private int mRotationDirection;

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
        mCommuteLegs = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_commute, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwapFab = (FloatingActionButton) getActivity().findViewById(R.id.fragment_journey_search_fab);
        mSwapFab.setOnClickListener(this);
        mPrevButton = (ImageButton) getActivity().findViewById(R.id.fragment_commute_prev_service_button);
        mPrevButton.setOnClickListener(this);
        mNextButton = (ImageButton) getActivity().findViewById(R.id.fragment_commute_next_service_button);
        mNextButton.setOnClickListener(this);
        mNextServiceText = (TextView) getActivity().findViewById(R.id.fragment_commute_next_service_text);
        mRotationDirection = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPrevButton.setBackground(getActivity().getDrawable(R.drawable.swap_button_ripple));
            mNextButton.setBackground(getActivity().getDrawable(R.drawable.swap_button_ripple));
        }
    }

    @Override
    public void onResume() {
        super.onStart();
        setUpData();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_journey_search_fab:
                swap();
                break;
            case R.id.fragment_commute_prev_service_button:
                showPrev();
                break;
            case R.id.fragment_commute_next_service_button:
                showNext();
                break;
        }
    }

    /**
     * Swaps the direction of the commute
     * Home -> Work
     * Work -> Home
     */
    private void swap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!mIsTravelTo) {
            mRotationDirection = -1;
            mSwapFab.setRotation(180);
        }
        else{
            mSwapFab.setRotation(0);
            mRotationDirection = 1;
        }
        RotateAnimation rotate = new RotateAnimation(0f, mRotationDirection * 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setFillAfter(true);
        rotate.setDuration(300);
        mSwapFab.startAnimation(rotate);

        mIsTravelTo = !mIsTravelTo;
        editor.putBoolean(Constants.COMMUTE_TO_OR_FROM_PREF, mIsTravelTo);
        editor.apply();

        setUpData();
    }

    /**
     * Shows the previous services if it exists
     */
    private void showPrev() {
        Calendar now = Calendar.getInstance();
        now.setTime(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getDepartureTime());
        Timetable tmp = Common.findClosestTimetable(mSelectedCommute.getTripTimetables(), mSelectedCommute.getStartStopShortName(), now, false, true);
        if (tmp != null) {
            mSelectedTimetable = tmp;
            setUpScreen();
            checkIfNextExist(false, mPrevButton, mNextButton);
        }

    }

    /**
     * Shows the next services if it exists
     */
    private void showNext() {
        Calendar now = Calendar.getInstance();
        now.setTime(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getDepartureTime());
        Timetable tmp = Common.findClosestTimetable(mSelectedCommute.getTripTimetables(), mSelectedCommute.getStartStopShortName(), now, true, true);
        if (tmp != null) {
            mSelectedTimetable = tmp;
            setUpScreen();
            checkIfNextExist(true, mNextButton, mPrevButton);
        }
    }

    /**
     * Check if the service exists
     * Changes the opacity of the button appropriately
     * @param forNext - true: next service; false: previous service
     * @param button1 - prev button or next button
     * @param button2 - next button or prev button
     */
    private void checkIfNextExist(boolean forNext, ImageButton button1, ImageButton button2) {
        Calendar now = Calendar.getInstance();
        now.setTime(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getDepartureTime());
        Timetable tmp = Common.findClosestTimetable(mSelectedCommute.getTripTimetables(), mSelectedCommute.getStartStopShortName(), now, forNext, true);
        if (tmp == null) {
            button1.setEnabled(false);
            button1.getDrawable().setAlpha(Constants.DESELECTED);
        } else {
            button2.setEnabled(true);
            button1.getDrawable().setAlpha(Constants.OPAQUE);
        }
    }

    /**
     * Set up ALL the UI elements
     * Bind data to them
     */
    private void setUpScreen() {
        ImageView transportImage = (ImageView) getActivity().findViewById(R.id.fragment_commute_transport_mode_image);
        RelativeLayout bottomSheet = (RelativeLayout) getActivity().findViewById(R.id.fragment_commute_bottom_sheet_ll);
        TextView routeLineName = (TextView) getActivity().findViewById(R.id.fragment_commute_line_name_tv);
        TextView destination = (TextView) getActivity().findViewById(R.id.fragment_commute_commute_destination_tv);
        FrameLayout routeType = (FrameLayout) getActivity().findViewById(R.id.fragment_commute_line_number_frame);
        TextView startStop = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_from_name);
        TextView endStop = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_to_name);
        TextView departureTime = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_from_time);
        TextView arrivalTime = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_to_time);

        DateFormat date = new SimpleDateFormat(Constants.DATE_FORMAT_HH_MM_SPACE_AM, Locale.ENGLISH);
        Route selectedRoute = mSelectedTimetable.getTrip().getRoute();
        mStopTimeCount = mSelectedTimetable.getStopTimes().size();

        getActivity().setTitle(getActivity().getString(R.string.commute) + getString(R.string.to_with_spaces) + mSelectedCommute.getEndStopShortName());

        transportImage.setImageResource(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getStop().getImage());
        transportImage.setVisibility(View.VISIBLE);
        if (selectedRoute.getAgency().getID().equals(getString(R.string.regional_trains_agency))) {
            transportImage.setImageResource(R.drawable.tnsw_icon_regional_train);
        }

        String line = selectedRoute.getLongName();
        if (selectedRoute.getAgency().getID().equals(getString(R.string.sydney_trains_agency))) {
            routeType.setVisibility(View.VISIBLE);
            GradientDrawable lineShape = (GradientDrawable) routeType.getBackground();
            lineShape.setColor(selectedRoute.getColor());

            TextView routeLineNumber = (TextView) getActivity().findViewById(R.id.fragment_commute_line_number_tv);
            routeLineNumber.setText(line.substring(0, 2));
            routeLineName.setText(line.substring(2, line.length()));

        } else {
            routeLineName.setText(line);
            routeType.setVisibility(View.GONE);
        }

        String desString = getString(R.string.for_space) + mSelectedTimetable.getTrip().getHeadSign();
        destination.setText(desString);

        startStop.setText(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getStop().getName());
        endStop.setText(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 1).getStop().getName());

        bottomSheet.setBackgroundColor(selectedRoute.getColor());

        Date departureTimeDate = mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getDepartureTime();
        Date arrivalTimeDate = mSelectedTimetable.getStopTimes().get(mStopTimeCount - 1).getArrivalTime();
        departureTime.setText(date.format(departureTimeDate));
        arrivalTime.setText(date.format(arrivalTimeDate));

        checkIfNextExist(false, mPrevButton, mNextButton);

        setEtd(departureTimeDate);
    }

    /**
     * Set the estimated time of train departure
     * @param departureTime - time when train departs the station
     */
    private void setEtd(Date departureTime) {
        final TextView etdText = (TextView) getActivity().findViewById(R.id.fragment_commute_bottom_sheet_eta);
        Calendar now = Common.getNow();
        Calendar dep = Calendar.getInstance();
        dep.setTime(departureTime);


        final long millis = dep.getTimeInMillis() - now.getTimeInMillis();
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
            mCountdownTimer = null;
        }
        if (millis > 0) {
            mNextServiceText.setText(R.string.next_service_departing_in);
            mCountdownTimer = new CountDownTimer(millis, 1000) {
                public void onTick(long millisUntilFinished) {
                    etdText.setText(Common.getDurationTime(millisUntilFinished, true, true, true));
                }

                public void onFinish() {
                    continueSetup();
                }
            }.start();
        } else {
            etdText.setText("");
            mNextServiceText.setText(R.string.service_already_departed);
        }
    }

    /**
     * Set up the data needed to be displayed in the UI
     * Call the async task to gather data from DB
     */
    private void setUpData() {
        mCommuteLegs.clear();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String startStopShortName = sharedPreferences.getString(getString(R.string.home_preference), getString(R.string.home_preference_summary));
        String endStopShortName = sharedPreferences.getString(getString(R.string.work_preference), getString(R.string.work_preference_summary));
        mIsTravelTo = sharedPreferences.getBoolean(Constants.COMMUTE_TO_OR_FROM_PREF, true);
        if (mIsTravelTo) {
            LoadCommuteAsync loadCommute = new LoadCommuteAsync(startStopShortName, endStopShortName);
            loadCommute.execute();
        } else {
            LoadCommuteAsync loadCommute = new LoadCommuteAsync(endStopShortName, startStopShortName);
            loadCommute.execute();
        }
    }

    /**
     * Once async task is done, continue the setup of data and then start setting up the screen.
     */
    private void continueSetup() {
        mSelectedCommute = mCommuteLegs.get(0);
        Timetable tmp = Common.findClosestTimetable(mSelectedCommute.getTripTimetables(), mSelectedCommute.getStartStopShortName(), Common.getNow(), true, true);
        if (tmp != null) {
            mSelectedTimetable = tmp;
            setUpScreen();
        }
    }

    /**
     * Loads the list of Timetables connecting the 2 stops for the daily commute.
     */
    private class LoadCommuteAsync extends AsyncTask<Void, Void, Commute> {

        private ProgressDialog mDialog;
        private String mStartStopShortName;
        private String mEndStopShortName;


        LoadCommuteAsync(String start, String end) {
            mStartStopShortName = start;
            mEndStopShortName = end;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.show();
            mDialog.setMessage(getString(R.string.loading_timetable));
            mDialog.setCancelable(false);
        }

        @Override
        protected Commute doInBackground(Void... params) {
            return CommuteManager.getCommute(mStartStopShortName, mEndStopShortName);
        }

        @Override
        protected void onPostExecute(Commute commute) {
            mCommuteLegs.add(commute);
            if (this.mDialog.isShowing()) {
                this.mDialog.dismiss();
            }
            continueSetup();
        }
    }
}
