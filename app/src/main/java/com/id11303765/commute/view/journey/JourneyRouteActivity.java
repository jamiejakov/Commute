package com.id11303765.commute.view.journey;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.id11303765.commute.R;
import com.id11303765.commute.controller.JourneyRouteLegVisualAdapter;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.JourneyLeg;
import com.id11303765.commute.model.JourneyManager;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity displaying the journey selected.
 * Gives overall info on the top
 * and a pretty route visualisation below
 */
public class JourneyRouteActivity extends AppCompatActivity implements RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener, View.OnClickListener {
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private Journey mJourney;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String journeyPK = getIntent().getStringExtra(Constants.INTENT_JOURNEY_ROUTE);
        mJourney = JourneyManager.findJourney(journeyPK);
        mActivity = this;

        if (mJourney != null) {
            setUpScreen(savedInstanceState);
        } else {
            Log.d(Constants.TAG_ERROR_LOG, getString(R.string.could_not_find_journey));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journey_route_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            case R.id.journey_route_open_bottom_sheet:
                createBottomSheet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(
                    SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                    mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser) {
    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_journey_route_end_stop_name:
                Intent intent = new Intent(mActivity, JourneyStopMapActivity.class);
                JourneyLeg lastLeg = mJourney.getJourneyLegs().get(mJourney.getJourneyLegs().size() - 1);
                intent.putExtra(Constants.INTENT_JOURNEY_STOP_NAME, lastLeg.getEndStop().getShortName());
                intent.putExtra(Constants.INTENT_JOURNEY_STOP_LAT, lastLeg.getEndStop().getLat());
                intent.putExtra(Constants.INTENT_JOURNEY_STOP_LON, lastLeg.getEndStop().getLon());
                mActivity.startActivityForResult(intent, Constants.JOURNEY_STOP_MAP_REQUEST);
                break;
        }
    }

    /**
     * Set up screen UI
     *
     * @param savedInstanceState -
     */
    private void setUpScreen(Bundle savedInstanceState) {
        setContentView(R.layout.activity_journey_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_journey_route_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpTopInfo();
        setUpRouteVisualisation(savedInstanceState);
    }

    /**
     * Bind the data from the Journey to the UI
     */
    private void setUpTopInfo() {
        DateFormat date = new SimpleDateFormat(Constants.DATE_FORMAT_HH_MM_SPACE_AM, Locale.ENGLISH);
        TextView fromTime = (TextView) findViewById(R.id.activity_journey_route_from_time_text);
        TextView toTime = (TextView) findViewById(R.id.activity_journey_route_to_time_text);
        TextView durationTextView = (TextView) findViewById(R.id.activity_journey_route_duration_text);
        TextView price = (TextView) findViewById(R.id.activity_journey_route_price_amount_text);
        TextView transfersTextView = (TextView) findViewById(R.id.activity_journey_route_transfers_number_text);
        ImageView trainImage = (ImageView) findViewById(R.id.activity_journey_route_transport_mode_train_image);
        ImageView busImage = (ImageView) findViewById(R.id.activity_journey_route_transport_mode_bus_image);
        ImageView ferryImage = (ImageView) findViewById(R.id.activity_journey_route_transport_mode_ferry_image);
        ImageView lightRailImage = (ImageView) findViewById(R.id.activity_journey_route_transport_mode_light_rail_image);
        LinearLayout speedLayout = (LinearLayout) findViewById(R.id.activity_journey_route_speed_ll);
        LinearLayout priceLayout = (LinearLayout) findViewById(R.id.activity_journey_route_price_ll);
        LinearLayout convenienceLayout = (LinearLayout) findViewById(R.id.activity_journey_route_convenience_ll);
        assert fromTime != null;
        assert toTime != null;
        assert price != null;
        assert durationTextView != null;
        assert transfersTextView != null;
        assert speedLayout != null;
        assert priceLayout != null;
        assert convenienceLayout != null;

        String title = mJourney.getStartStop().getShortName() + getString(R.string.to_with_spaces) + mJourney.getEndStop().getShortName() +
                getString(R.string.dash_route) + getIntent().getStringExtra(Constants.INTENT_JOURNEY_ROUTE_NUMBER);
        setTitle(title);

        int lastLegPos = mJourney.getJourneyLegs().size() - 1;

        fromTime.setText(date.format(mJourney.getDepartureTime()));
        toTime.setText(date.format(mJourney.getArrivalTime()));

        long duration = mJourney.getArrivalTime().getTime() - mJourney.getDepartureTime().getTime();
        String durationText = "(" + Common.getDurationTime(duration, true, true, false).trim() + ")";

        durationTextView.setText(durationText);
        price.setText(String.valueOf(mJourney.getmPrice()));

        // number of transfers is the same as the size of the array of journeys - 1
        String transferText = " " + String.valueOf(lastLegPos);
        transfersTextView.setText(transferText);

        Common.makeViewVisible(speedLayout, mJourney.isFast());
        Common.makeViewVisible(priceLayout, mJourney.isCheap());
        Common.makeViewVisible(convenienceLayout, mJourney.isConvenient());
        ((ImageView) speedLayout.getChildAt(0)).getDrawable().setAlpha(Constants.OPAQUE);
        ((ImageView) priceLayout.getChildAt(0)).getDrawable().setAlpha(Constants.OPAQUE);
        ((ImageView) convenienceLayout.getChildAt(0)).getDrawable().setAlpha(Constants.OPAQUE);

        View[] transportViews = new View[]{trainImage, busImage, ferryImage, lightRailImage};
        for (JourneyLeg journeyLeg : mJourney.getJourneyLegs()) {
            Common.makeViewVisible(transportViews[journeyLeg.getStartStop().getStopType() - 1], true);
        }
    }

    /**
     * Setup the RecyclerView for the visual journey line between the stops
     * With collapsable stations.
     *
     * @param savedInstanceState -
     */
    private void setUpRouteVisualisation(Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_journey_routes_list_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;

        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        //adapter
        final JourneyRouteLegVisualAdapter routeAdapter = new JourneyRouteLegVisualAdapter(this, mJourney.getJourneyLegs());
        RecyclerView.Adapter mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(routeAdapter);
        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        animator.setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mWrappedAdapter);
        recyclerView.setItemAnimator(animator);
        recyclerView.setHasFixedSize(false);

        mRecyclerViewExpandableItemManager.attachRecyclerView(recyclerView);

        setUpEndStopInfo();
    }

    /**
     * Bind the data about the last stop to the UI
     */
    private void setUpEndStopInfo() {
        TextView destinationTime = (TextView) findViewById(R.id.activity_journey_route_end_time);
        TextView stopIndicator = (TextView) findViewById(R.id.activity_journey_route_end_stop_indicator);
        TextView stopName = (TextView) findViewById(R.id.activity_journey_route_end_stop_name);
        FrameLayout lineLine = (FrameLayout) findViewById(R.id.activity_journey_stop_row_leg_line_fl);
        assert destinationTime != null;
        assert lineLine != null;
        assert stopName != null;
        assert stopIndicator != null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_HH_MM_SPACE_AM, Locale.US);
        destinationTime.setText(simpleDateFormat.format(mJourney.getArrivalTime()));

        GradientDrawable stopIndicatorShape = (GradientDrawable) stopIndicator.getBackground();
        JourneyLeg lastLeg = mJourney.getJourneyLegs().get(mJourney.getJourneyLegs().size() - 1);

        int color = lastLeg.getTimetable().getTrip().getRoute().getColor();
        stopIndicatorShape.setColor(color);
        lineLine.setBackgroundColor(color);

        stopName.setText(lastLeg.getEndStop().getShortName());
        stopName.setOnClickListener(this);
    }

    /**
     * Bottom sheet that allows the users to save to calendar
     * and set journey as commute.
     */
    private void createBottomSheet() {
        new BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                .sheet(R.menu.journey_route_bottom_sheet_menu)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.journey_route_set_commute:
                                setCommute();
                                break;
                            case R.id.journey_route_add_to_cal:
                                saveToCalendar();
                                break;
                        }
                    }
                }).show();
    }

    /**
     * Set commute action when clicking on the option in the bottom sheet
     * Display snackbar when complete
     */
    private void setCommute() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        CoordinatorLayout baseView = (CoordinatorLayout) findViewById(R.id.activity_journey_route_coordinator);
        assert baseView != null;

        editor.putString(getString(R.string.home_preference), mJourney.getJourneyLegs().get(0).getStartStop().getShortName());
        editor.putString(getString(R.string.work_preference), mJourney.getJourneyLegs().get(mJourney.getJourneyLegs().size() - 1).getEndStop().getShortName());
        editor.apply();

        Snackbar.make(baseView, R.string.set_commute_success_message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Save to calendar action when clicking on the option in the bottom sheet
     * Display snackbar when complete
     */
    private void saveToCalendar() {
        CoordinatorLayout baseView = (CoordinatorLayout) findViewById(R.id.activity_journey_route_coordinator);
        assert baseView != null;
        Intent intent = new Intent(Intent.ACTION_INSERT);

        intent.setType(getString(R.string.calendar_event));
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, mJourney.getDepartureTime().getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, mJourney.getArrivalTime().getTime());
        intent.putExtra(CalendarContract.Events.TITLE, getString(R.string.journey_from) +
                mJourney.getJourneyLegs().get(0).getStartStop().getShortName() + getString(R.string.to_with_spaces) +
                mJourney.getJourneyLegs().get(mJourney.getJourneyLegs().size() - 1).getEndStop().getShortName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, createCalendarDescription());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mJourney.getJourneyLegs().get(0).getStartStop().getShortName());
        startActivity(intent);

        Snackbar.make(baseView, R.string.calendar_event_success, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Create the amazingly beautiful and meaningful calendar description
     *
     * @return the description
     */
    private String createCalendarDescription() {
        DateFormat date = new SimpleDateFormat(Constants.DATE_FORMAT_HH_MM_AM, Locale.ENGLISH);
        long duration = mJourney.getArrivalTime().getTime() - mJourney.getDepartureTime().getTime();
        int transfers = mJourney.getJourneyLegs().size() - 1;

        String description = mJourney.getJourneyLegs().get(0).getStartStop().getShortName() + getString(R.string.to_arrow_right) +
                mJourney.getJourneyLegs().get(mJourney.getJourneyLegs().size() - 1).getEndStop().getShortName() + getString(R.string.enter);
        description += date.format(mJourney.getDepartureTime()) + getString(R.string.to_arrow_right) +
                date.format(mJourney.getArrivalTime()) + getString(R.string.enter);
        description += getString(R.string.line) + getString(R.string.enter);
        description += getString(R.string.duration) + Common.getDurationTime(duration, true, true, false) + getString(R.string.enter);
        description += getString(R.string.transfers) + String.valueOf(transfers) + getString(R.string.enter);
        description += getString(R.string.price_$) + String.valueOf(mJourney.getmPrice()) + getString(R.string.enter);
        description += getString(R.string.line) + getString(R.string.enter);
        for (JourneyLeg leg : mJourney.getJourneyLegs()) {
            Date departureTime = leg.getTimetable().getStopTimes().get(0).getDepartureTime();
            Date arrivalTime = leg.getTimetable().getStopTimes().get(leg.getTimetable().getStopTimes().size() - 1).getArrivalTime();
            description += getString(R.string.stop_square) + leg.getStartStop().getShortName() + getString(R.string.enter);
            description += getString(R.string.to_stop_arrow_down) + date.format(departureTime) +
                    getString(R.string.tilda) + date.format(arrivalTime) + getString(R.string.enter);
            description += getString(R.string.to_stop_arrow_down) + leg.getTimetable().getTrip().getRoute().getLongName() + getString(R.string.enter);
        }
        description += getString(R.string.stop_square) + mJourney.getJourneyLegs().get(mJourney.getJourneyLegs().size() - 1).getEndStop().getShortName();

        Log.d(Constants.TAG_ADD_TO_CALENDAR, description);
        return description;
    }

    /**
     * Adjusts the scroll when the group recyclerView is expanded.
     *
     * @param groupPosition -
     */
    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        int childItemHeight = getResources().getDimensionPixelSize(R.dimen.journey_route_visual_stop_height);
        int margin = (int) (getResources().getDisplayMetrics().density * getResources().getDimensionPixelSize(R.dimen.fab_margin));

        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, margin, margin);
    }
}
