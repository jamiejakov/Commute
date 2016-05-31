package com.id11303765.commute.view;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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


public class CommuteFragment extends Fragment implements View.OnClickListener{
    private View mTabView;
    private ArrayList<Commute> mCommuteLegs;
    private Commute mSelectedCommute;
    private Timetable mSelectedTimetable;

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

        FloatingActionButton swapFab = (FloatingActionButton) getActivity().findViewById(R.id.fragment_journey_search_fab);
        swapFab.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpData();
        setUpScreen();
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

    private void swap(){

    }

    private void setUpScreen() {
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

        TextView eta = (TextView) getActivity().findViewById(R.id.fragment_commute_bottom_sheet_eta);
        String etaString = getETA(departureTimeDate);
        eta.setText(etaString);
    }

    public String getETA(Date departureTime) {
        Calendar now = Calendar.getInstance();
        now.set(1970,0,1);
        Calendar dep = Calendar.getInstance();
        dep.setTime(departureTime);


        long millis = dep.getTimeInMillis() - now.getTimeInMillis();
        String time;
        if (millis > Constants.ONE_MINUTE*60){
            time = String.format("%2dhr %02dmin %02dsec",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
            );
        }else{
            time = String.format("%02dmin %02dsec",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        }


        return time;
    }

    private Timetable findClosestTimetable() {
        Timetable timetable = null;
        ArrayList<Timetable> timetables = mSelectedCommute.getTripTimetables();
        Calendar now = Calendar.getInstance();
        now.set(1970,0,1);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String startStopShortName = sharedPreferences.getString(getString(R.string.home_preference), getString(R.string.home_preference_summary));
        String endStopShortName = sharedPreferences.getString(getString(R.string.work_preference), getString(R.string.work_preference_summary));
        mCommuteLegs.add(CommuteManager.getCommute(startStopShortName, endStopShortName));
        mSelectedCommute = mCommuteLegs.get(0);
        mSelectedTimetable = findClosestTimetable();
    }


}
