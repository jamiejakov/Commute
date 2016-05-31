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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.model.Commute;
import com.id11303765.commute.model.CommuteManager;
import com.id11303765.commute.model.DatabaseHelper;
import com.id11303765.commute.model.Route;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class CommuteFragment extends Fragment implements View.OnClickListener {
    private View mTabView;
    private ArrayList<Commute> mCommuteLegs;
    private Commute mSelectedCommute;
    private Timetable mSelectedTimetable;
    private boolean mIsTravelTo;
    private FloatingActionButton mSwapFab;
    private CountDownTimer mCountdownTimer;

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
    }

    @Override
    public void onStart() {
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
        }
    }

    private void swap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (mIsTravelTo) {
            mSwapFab.setImageDrawable(getActivity().getDrawable(R.drawable.ic_png_swap_back));
        } else {
            mSwapFab.setImageDrawable(getActivity().getDrawable(R.drawable.ic_png_swap_forward));
        }

        mIsTravelTo = !mIsTravelTo;
        editor.putBoolean(Constants.COMMUTE_TO_OR_FROM_PREF, mIsTravelTo);
        editor.apply();

        setUpData();
    }

    private void setUpScreen() {
        mSelectedTimetable = findClosestTimetable();
        Route selectedRoute = mSelectedTimetable.getTrip().getRoute();

        getActivity().setTitle(getActivity().getString(R.string.commute) + " to " + mSelectedCommute.getEndStopShortName());

        ImageView transportImage = (ImageView) getActivity().findViewById(R.id.fragment_commute_transport_mode_image);
        transportImage.setImageResource(mSelectedTimetable.getStopTimes().get(0).getStop().getStopType());
        transportImage.setVisibility(View.VISIBLE);
        if (selectedRoute.getAgency().getID().equals("X0000")) {
            transportImage.setImageResource(R.drawable.tnsw_icon_regional_train);
        }

        RelativeLayout bottomSheet = (RelativeLayout) getActivity().findViewById(R.id.fragment_commute_bottom_sheet_ll);
        TextView route_line_name = (TextView) getActivity().findViewById(R.id.fragment_commute_line_name_tv);

        TextView destination = (TextView) getActivity().findViewById(R.id.fragment_commute_commute_destination_tv);
        String desString = "to " + selectedRoute.getLongName();

        String line = selectedRoute.getLongName();
        FrameLayout routeType = (FrameLayout) getActivity().findViewById(R.id.fragment_commute_line_number_frame);
        if (selectedRoute.getAgency().getID().equals("x0001")) {
            routeType.setVisibility(View.VISIBLE);
            GradientDrawable route_shape = (GradientDrawable) routeType.getBackground();
            route_shape.setColor(selectedRoute.getColor());

            TextView route_line_number = (TextView) getActivity().findViewById(R.id.fragment_commute_line_number_tv);
            route_line_number.setText(line.substring(0, 2));
            route_line_name.setText(line.substring(2, line.length()));

            desString = "to " + mSelectedTimetable.getTrip().getHeadSign();
        } else {
            route_line_name.setText(line);
            routeType.setVisibility(View.GONE);
        }

        destination.setText(desString);

        TextView startPlatform = (TextView) getActivity().findViewById(R.id.fragment_commute_station_from_name);
        startPlatform.setText(mSelectedTimetable.getStopTimes().get(0).getStop().getName());
        TextView endPlatform = (TextView) getActivity().findViewById(R.id.fragment_commute_station_to_name);
        endPlatform.setText(mSelectedTimetable.getStopTimes().get(1).getStop().getName());

        bottomSheet.setBackgroundColor(selectedRoute.getColor());
        DateFormat date = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        TextView departureTime = (TextView) getActivity().findViewById(R.id.fragment_commute_station_from_time);
        Date departureTimeDate = mSelectedTimetable.getStopTimes().get(0).getDepartureTime();
        departureTime.setText(date.format(departureTimeDate));
        TextView arrivalTime = (TextView) getActivity().findViewById(R.id.fragment_commute_station_to_time);
        arrivalTime.setText(date.format(mSelectedTimetable.getStopTimes().get(1).getArrivalTime()));


        setETA(departureTimeDate);
    }

    public void setETA(Date departureTime) {
        final TextView etaText = (TextView) getActivity().findViewById(R.id.fragment_commute_bottom_sheet_eta);
        Calendar now = Calendar.getInstance();
        now.set(1970, 0, 1);
        Calendar dep = Calendar.getInstance();
        dep.setTime(departureTime);


        final long millis = dep.getTimeInMillis() - now.getTimeInMillis();
        if (mCountdownTimer!=null){
            mCountdownTimer.cancel();
        }
        mCountdownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                String time;
                if (millisUntilFinished > Constants.ONE_MINUTE * 60) {
                    time = String.format(Locale.ENGLISH, "%2dhr %02dmin %02dsec",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                    TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) -
                                    TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                    );
                } else {
                    time = String.format(Locale.ENGLISH, "%02dmin %02dsec",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                    );
                }
                etaText.setText(time);
            }

            public void onFinish() {
                setUpScreen();
            }
        }.start();
    }

    private Timetable findClosestTimetable() {
        Timetable timetable = null;
        ArrayList<Timetable> timetables = mSelectedCommute.getTripTimetables();
        Calendar now = Calendar.getInstance();
        now.set(1970, 0, 1);
        Calendar currentTimetableTime = Calendar.getInstance();
        for (Timetable t : timetables) {
            StopTime st = t.getStopTimes().get(0);
            Calendar departureTime = Calendar.getInstance();
            departureTime.setTime(st.getDepartureTime());

            if (st.getStop().getShortName().equals(mSelectedCommute.getStartStopShortName()) && departureTime.after(now)) {
                if (timetable == null || departureTime.before(currentTimetableTime)) {
                    timetable = t;
                    currentTimetableTime.setTime(st.getDepartureTime());
                }

            }
        }
        return timetable;
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
        setUpScreen();
    }

    /**
     * Populates the internal database with the GTFS data from Transport NSW
     */
    private class LoadCommuteAsync extends AsyncTask<Void, Void, Void> {

        private ProgressDialog mDialog;
        private String mStartStopShortName;
        private String mEndStopShortName;


        public LoadCommuteAsync(String start, String end) {
            mStartStopShortName = start;
            mEndStopShortName = end;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.show();
            mDialog.setMessage("Loading Timetable");
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
