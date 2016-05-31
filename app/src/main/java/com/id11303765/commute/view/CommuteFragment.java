package com.id11303765.commute.view;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.model.Commute;
import com.id11303765.commute.model.CommuteManager;
import com.id11303765.commute.model.Route;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.model.Trip;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;


public class CommuteFragment extends Fragment {
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

        mCommuteLegs = new ArrayList<>();
        setUpData();
        setUpScreen();


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        //((ViewGroup) mTabView.getParent()).removeView(mTabView);
        super.onStop();
    }

    private void setUpScreen() {
        Route selectedRoute = mSelectedTimetable.getTrip().getRoute();

        ImageView transportImage = (ImageView) getActivity().findViewById(R.id.fragment_commute_transport_mode_image);
        transportImage.setImageResource(mSelectedCommute.getStartStop().getStopType());
        if (selectedRoute.getAgency().getID().equals("X0000")){
            transportImage.setImageResource(R.drawable.tnsw_icon_regional_train);
        }


        String line = selectedRoute.getLongName();
        FrameLayout routeType = (FrameLayout) getActivity().findViewById(R.id.fragment_commute_line_number_frame);
        if (selectedRoute.getAgency().getID().equals("x0001")){
            routeType.setVisibility(View.VISIBLE);
            GradientDrawable route_shape = (GradientDrawable) routeType.getBackground();
            route_shape.setColor(selectedRoute.getColor());
            TextView route_line_number = (TextView) getActivity().findViewById(R.id.fragment_commute_line_number_tv);
            route_line_number.setText(line.substring(0,2));
        }
        else{

            routeType.setVisibility(View.GONE);
        }
        TextView route_line_name = (TextView) getActivity().findViewById(R.id.fragment_commute_line_name_tv);
        route_line_name.setText(line.substring(2,line.length()));


    }

    private Timetable findClosestTimetable() {
        ArrayList<Timetable> timetables = mSelectedCommute.getTripTimetables();
        Timetable timetable = null;
        Date currentDate = new Date(System.currentTimeMillis());
        for (Timetable t : timetables) {
            for (StopTime st : t.getStopTimes()){
                if (timetable == null || st.getStop() == mSelectedCommute.getStartStop() && st.getDepartureTime().after(currentDate)){
                    timetable = t;
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
