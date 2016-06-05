package com.id11303765.commute.view;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
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


public class CommuteFragment extends Fragment implements View.OnClickListener {
    private View mTabView;
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
        /*mTabView = inflater.inflate(R.layout.app_bar_commute, container, false);
        AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.app_bar_main_appbar);
        appBar.addView(mTabView);*/

        mCommuteLegs = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_commute, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /*TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.app_bar_commute_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("MOSHI MOSHI"));
        tabLayout.addTab(tabLayout.newTab().setText("DIS IS DOG"));
        tabLayout.addTab(tabLayout.newTab().setText("such wow"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);*/

        mSwapFab = (FloatingActionButton) getActivity().findViewById(R.id.fragment_journey_search_fab);
        mSwapFab.setOnClickListener(this);
        mPrevButton = (ImageButton) getActivity().findViewById(R.id.fragment_commute_prev_service_button);
        mPrevButton.setOnClickListener(this);
        mNextButton = (ImageButton) getActivity().findViewById(R.id.fragment_commute_next_service_button);
        mNextButton.setOnClickListener(this);
        mNextServiceText = (TextView) getActivity().findViewById(R.id.fragment_commute_next_service_text);
        mRotationDirection = 1;


    }

    @Override
    public void onStart() {
        super.onStart();
        setUpData();
    }

    @Override
    public void onResume() {
        super.onStart();
        setUpData();
    }

    @Override
    public void onStop() {
        //((ViewGroup) mTabView.getParent()).removeView(mTabView);
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

    private void setUpScreen() {
        Route selectedRoute = mSelectedTimetable.getTrip().getRoute();
        mStopTimeCount = mSelectedTimetable.getStopTimes().size();

        getActivity().setTitle(getActivity().getString(R.string.commute) + " to " + mSelectedCommute.getEndStopShortName());

        ImageView transportImage = (ImageView) getActivity().findViewById(R.id.fragment_commute_transport_mode_image);
        transportImage.setImageResource(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getStop().getStopType());
        transportImage.setVisibility(View.VISIBLE);
        if (selectedRoute.getAgency().getID().equals(getString(R.string.regional_trains_agency))) {
            transportImage.setImageResource(R.drawable.tnsw_icon_regional_train);
        }

        RelativeLayout bottomSheet = (RelativeLayout) getActivity().findViewById(R.id.fragment_commute_bottom_sheet_ll);
        TextView routeLineName = (TextView) getActivity().findViewById(R.id.fragment_commute_line_name_tv);

        TextView destination = (TextView) getActivity().findViewById(R.id.fragment_commute_commute_destination_tv);

        String line = selectedRoute.getLongName();
        FrameLayout routeType = (FrameLayout) getActivity().findViewById(R.id.fragment_commute_line_number_frame);
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

        String desString = "for " + mSelectedTimetable.getTrip().getHeadSign();
        destination.setText(desString);

        TextView startStop = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_from_name);
        startStop.setText(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getStop().getName());
        TextView endStop = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_to_name);
        endStop.setText(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 1).getStop().getName());

        bottomSheet.setBackgroundColor(selectedRoute.getColor());
        DateFormat date = new SimpleDateFormat(getString(R.string.am_pm_time_format), Locale.ENGLISH);
        TextView departureTime = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_from_time);
        Date departureTimeDate = mSelectedTimetable.getStopTimes().get(mStopTimeCount - 2).getDepartureTime();
        departureTime.setText(date.format(departureTimeDate));
        TextView arrivalTime = (TextView) getActivity().findViewById(R.id.fragment_commute_stop_to_time);
        arrivalTime.setText(date.format(mSelectedTimetable.getStopTimes().get(mStopTimeCount - 1).getArrivalTime()));

        checkIfNextExist(false, mPrevButton, mNextButton);

        setETA(departureTimeDate);
    }

    private void setETA(Date departureTime) {
        final TextView etaText = (TextView) getActivity().findViewById(R.id.fragment_commute_bottom_sheet_eta);
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
                    etaText.setText(Common.getDurationTime(millisUntilFinished, true, true, true));
                }

                public void onFinish() {
                    continueSetup();
                }
            }.start();
        } else {
            etaText.setText("");
            mNextServiceText.setText(R.string.service_already_departed);
        }
    }

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
    private class LoadCommuteAsync extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... params) {
            mCommuteLegs.add(CommuteManager.getCommute(mStartStopShortName, mEndStopShortName));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (this.mDialog.isShowing()) {
                this.mDialog.dismiss();
            }
            continueSetup();
        }
    }
}
