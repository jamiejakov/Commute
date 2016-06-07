package com.id11303765.commute.view.journey;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.controller.JourneyRoutesListAdapter;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.JourneyManager;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Class that displays the list of the best routes available between the selected start and end stops
 * Should utilise the A* algorithm http://www.wikiwand.com/en/A*_search_algorithm
 * will implement eventually
 */
public class JourneyRoutesListActivity extends AppCompatActivity {
    private ArrayList<Journey> mJourneys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpScreen();
        setUpData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup the UI
     */
    private void setUpScreen(){
        setContentView(R.layout.activity_journey_routes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_journey_routes_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setUpTabs();
    }

    /**
     * Set up the top tabs and their icons
     */
    private void setUpTabs(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.app_bar_journey_route_list_tab_layout);
        assert tabLayout != null;
        TabLayout.Tab speedTab = tabLayout.newTab().setText(R.string.speed);

        speedTab.setIcon(R.drawable.ic_directions_speed_black_24dp);
        Drawable speedIcon = speedTab.getIcon();
        assert speedIcon != null;
        speedIcon.setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        tabLayout.addTab(speedTab);
        speedTab.getIcon().setAlpha(Constants.DESELECTED);

        TabLayout.Tab priceTab = tabLayout.newTab().setText(R.string.price);
        priceTab.setIcon(R.drawable.ic_attach_money_black_24dp);
        Drawable priceIcon = priceTab.getIcon();
        assert priceIcon != null;
        priceIcon.setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        priceIcon.setAlpha(Constants.DESELECTED);
        tabLayout.addTab(priceTab);

        TabLayout.Tab convenienceTab = tabLayout.newTab().setText(R.string.convenience);
        convenienceTab.setIcon(R.drawable.ic_event_seat_black_24dp);
        Drawable convenienceIcon = convenienceTab.getIcon();
        assert convenienceIcon != null;
        convenienceIcon.setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        convenienceIcon.setAlpha(Constants.DESELECTED);
        tabLayout.addTab(convenienceTab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        int defaultTabPref = Integer.parseInt(sharedPreferences.getString(getString(R.string.default_search_pref), "1"));
        TabLayout.Tab tab = tabLayout.getTabAt(defaultTabPref-1);
        if (tab != null && tab.getIcon()!=null) {
            tab.select();
            tab.getIcon().setAlpha(Constants.OPAQUE);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    tabIcon.setAlpha(Constants.OPAQUE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    tabIcon.setAlpha(Constants.DESELECTED);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    /**
     * Setup the initial data and call the Async task to fetch the journeys from the DB
     */
    private void setUpData(){
        String startStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_START_STOP);
        String endStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_END_STOP);
        boolean departAt =  getIntent().getBooleanExtra(Constants.INTENT_TIME_DEPART_AT_BOOL, true);
        mJourneys = new ArrayList<>();

        setTitle(getString(R.string.routes_to_space) + endStopShortName);

        ArrayList<String> stops = new ArrayList<>();
        stops.add(startStopShortName);
        stops.add(endStopShortName);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String opalCardType = sharedPreferences.getString(getString(R.string.opal_card_type), "1");

        Calendar time = getTimeWithPeakAdjust(departAt);

        JourneyRoutesListActivity.LoadJourneysAsync loadCommute = new JourneyRoutesListActivity.LoadJourneysAsync(stops, this, Integer.parseInt(opalCardType),departAt, time);
        loadCommute.execute();
    }

    /**
     * Get the departure/arrival time with the adjustment for Peak Hour if it has been set in journey options
     * @param departAt - true: departure time; false: arrival time
     * @return calendar with adjusted time.
     */
    private Calendar getTimeWithPeakAdjust(boolean departAt){
        Calendar time = getTime();
        if (Common.isPeak(time) && Common.isWorkday(time)){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_HH24_MM, Locale.US);
            try {
                if (time.getTime().after(simpleDateFormat.parse(Constants.EVENING_PEAK_START))){
                    if (departAt){
                        time.setTime(simpleDateFormat.parse(Constants.EVENING_PEAK_END));
                    }else{
                        time.setTime(simpleDateFormat.parse(Constants.EVENING_PEAK_START));
                    }

                }else{
                    if (departAt){
                        time.setTime(simpleDateFormat.parse(Constants.MORNING_PEAK_END));
                    }else{
                        time.setTime(simpleDateFormat.parse(Constants.MORNING_PEAK_START));
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    /**
     * Get the time based on which to search for journey
     * @return calendar with time
     */
    private Calendar getTime(){
        Calendar time = Common.getNow();

        String fullText = getIntent().getStringExtra(Constants.INTENT_TIME_OPTION);
        if (!fullText.equals(getString(R.string.leave_now))) {

            String timeString;
            if (fullText.contains(getString(R.string.depart_at))) {
                timeString = fullText.trim().substring(Math.min(fullText.length(), getString(R.string.depart_at).length()));
            } else {
                timeString = fullText.trim().substring(Math.min(fullText.length(), getString(R.string.arrive_by).length()));
            }
            time = Common.parseStringToCal(timeString, Constants.DATE_FORMAT_HH_MM_AM_DAY_MONTH_YEAR_WEEKDAY);
        }
        return time;
    }

    /**
     * Continue the data setup after the journeys have been fetched from the DB
     */
    private void continueSetup() {
        TextView noResults = (TextView) findViewById(R.id.activity_journey_no_results_text);
        if (mJourneys.size() == 0){
            Common.makeViewVisible(noResults, true);
        }else{
            RecyclerView stopRecyclerView = (RecyclerView) findViewById(R.id.activity_journey_routes_list_recyclerview);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            assert stopRecyclerView != null;

            stopRecyclerView.setLayoutManager(layoutManager);
            JourneyRoutesListAdapter mJourneyRoutesListAdapter = new JourneyRoutesListAdapter(this, this, mJourneys);
            stopRecyclerView.setAdapter(mJourneyRoutesListAdapter);
            mJourneyRoutesListAdapter.notifyDataSetChanged();
            Common.makeViewVisible(noResults, false);
        }
    }

    /**
     * Loads the list of Timetables connecting the 2 stops for the daily commute.
     */
    private class LoadJourneysAsync extends AsyncTask<Void, Void, Journey> {

        private ProgressDialog mDialog;
        private ArrayList<String> mStops;
        private Context mContext;
        private int mOpalType;
        private Calendar mDepartAtOrArriveByTime;
        private boolean mDepartAt;

        LoadJourneysAsync(ArrayList<String> stops, Context context, int opalType, boolean departAt, Calendar time) {
            mStops = stops;
            mContext = context;
            mOpalType = opalType;
            mDepartAt = departAt;
            mDepartAtOrArriveByTime = time;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(mContext);
            mDialog.show();
            mDialog.setMessage(getString(R.string.loading_routes));
            mDialog.setCancelable(false);
        }

        @Override
        protected Journey doInBackground(Void... params) {
            return JourneyManager.getJourney(mStops, mDepartAt, mDepartAtOrArriveByTime, mOpalType);
        }

        @Override
        protected void onPostExecute(Journey journey) {
            if (journey!=null){
                mJourneys.add(journey);
            }
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            continueSetup();
        }
    }

}
