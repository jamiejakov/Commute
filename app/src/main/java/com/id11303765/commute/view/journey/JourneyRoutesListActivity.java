package com.id11303765.commute.view.journey;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
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

public class JourneyRoutesListActivity extends AppCompatActivity {
    private ArrayList<Journey> mJourneys;
    private JourneyRoutesListAdapter mJourneyRoutesListAdapter;
    private TabLayout mTabLayout;

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

    private void setUpTabs(){
        mTabLayout = (TabLayout) findViewById(R.id.app_bar_journey_route_list_tab_layout);
        TabLayout.Tab speedTab = mTabLayout.newTab().setText(R.string.speed);
        speedTab.setIcon(R.drawable.ic_directions_speed_black_24dp);
        speedTab.getIcon().setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        mTabLayout.addTab(speedTab);
        speedTab.getIcon().setAlpha(Constants.DESELECTED);
        TabLayout.Tab priceTab = mTabLayout.newTab().setText(R.string.price);
        priceTab.setIcon(R.drawable.ic_attach_money_black_24dp);
        priceTab.getIcon().setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        priceTab.getIcon().setAlpha(Constants.DESELECTED);
        mTabLayout.addTab(priceTab);
        TabLayout.Tab convenienceTab = mTabLayout.newTab().setText(R.string.convenience);
        convenienceTab.setIcon(R.drawable.ic_event_seat_black_24dp);
        convenienceTab.getIcon().setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        convenienceTab.getIcon().setAlpha(Constants.DESELECTED);
        mTabLayout.addTab(convenienceTab);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int defaultTabPref = Integer.parseInt(sharedPreferences.getString(getString(R.string.default_search_pref), "1"));
        mTabLayout.getTabAt(defaultTabPref-1).select();
        mTabLayout.getTabAt(defaultTabPref-1).getIcon().setAlpha(Constants.OPAQUE);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(Constants.OPAQUE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(Constants.DESELECTED);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Required empty constructor
            }
        });

    }

    private void setUpData(){
        String startStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_START_STOP);
        String endStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_END_STOP);
        boolean departAt =  getIntent().getBooleanExtra(Constants.INTENT_TIME_DEPART_AT_BOOL, true);
        mJourneys = new ArrayList<>();
        mJourneys.clear();


        setTitle("Routes To " + endStopShortName);

        ArrayList<String> stops = new ArrayList<>();
        stops.add(startStopShortName);
        stops.add(endStopShortName);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String opalCardType = sharedPreferences.getString(getString(R.string.opal_card_type), "1");

        Calendar time = getTimeWithPeakAdjust(departAt);

        JourneyRoutesListActivity.LoadJourneysAsync loadCommute = new JourneyRoutesListActivity.LoadJourneysAsync(stops, this, Integer.parseInt(opalCardType),departAt, time);
        loadCommute.execute();
    }

    private Calendar getTimeWithPeakAdjust(boolean departAt){
        Calendar time = getTime();
        if (Common.isPeak(time)){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
            try {
                if (time.getTime().after(simpleDateFormat.parse(getString(R.string.evening_peak_start)))){
                    if (departAt){
                        time.setTime(simpleDateFormat.parse(getString(R.string.evening_peak_end)));
                    }else{
                        time.setTime(simpleDateFormat.parse(getString(R.string.evening_peak_start)));
                    }

                }else{
                    if (departAt){
                        time.setTime(simpleDateFormat.parse(getString(R.string.morning_peak_end)));
                    }else{
                        time.setTime(simpleDateFormat.parse(getString(R.string.morning_peak_start)));
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    private Calendar getTime(){
        Calendar time = Common.getNow();

        String fullText = getIntent().getStringExtra(Constants.INTENT_TIME_OPTION);
        if (!fullText.equals(getString(R.string.leave_now))) {
            String[] columns = fullText.split(",");

            String timeString;
            if (columns[0].contains(getString(R.string.depart_at))) {
                timeString = columns[0].trim().substring(Math.min(columns[0].length(), getString(R.string.depart_at).length()));
            } else {
                timeString = columns[0].trim().substring(Math.min(columns[0].length(), getString(R.string.arrive_by).length()));
            }
            time = Common.parseStringToCal(timeString, "hh:mma");
        }
        return time;
    }

    private void continueSetup() {
        TextView noResults = (TextView) findViewById(R.id.activity_journey_no_results_text);
        if (mJourneys.size() == 0){
            Common.makeViewVisible(noResults, true);
        }else{
            Common.makeViewVisible(noResults, false);
            RecyclerView stopRecyclerView = (RecyclerView) findViewById(R.id.activity_journey_routes_list_recyclerview);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            assert stopRecyclerView != null;
            stopRecyclerView.setLayoutManager(layoutManager);
            mJourneyRoutesListAdapter = new JourneyRoutesListAdapter(this, this, mJourneys);
            stopRecyclerView.setAdapter(mJourneyRoutesListAdapter);
            mJourneyRoutesListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Loads the list of Timetables connecting the 2 stops for the daily commute.
     */
    private class LoadJourneysAsync extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... params) {
            Journey journey = JourneyManager.getJourney(mStops, mDepartAt, mDepartAtOrArriveByTime, mOpalType);
            if (journey!=null){
                mJourneys.add(journey);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            continueSetup();
        }
    }

}
