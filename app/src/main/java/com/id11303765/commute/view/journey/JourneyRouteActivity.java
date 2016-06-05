package com.id11303765.commute.view.journey;

import android.content.DialogInterface;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.id11303765.commute.R;
import com.id11303765.commute.controller.JourneyRouteLegVisualAdapter;
import com.id11303765.commute.controller.JourneyRoutesListAdapter;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.JourneyLeg;
import com.id11303765.commute.model.JourneyManager;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.utils.AbstractExpandableDataProvider;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class JourneyRouteActivity extends AppCompatActivity implements RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private Journey mJourney;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String journeyPK = getIntent().getStringExtra(Constants.INTENT_JOURNEY_ROUTE);
        mJourney = JourneyManager.findJourney(journeyPK);

        if (mJourney != null) {
            setUpScreen(savedInstanceState);
        }else{
            Log.d(Constants.ERROR_LOG, "Could not find journey");
            finish();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.journey_route_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                this.onBackPressed();
                return true;
            case R.id.journey_route_open_bottom_sheet:
                new BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                        .sheet(R.menu.journey_route_bottom_sheet_menu)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO
                            }
                        }).show();
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

        //Set Up RecyclerView all rows in loop
        // if first, add the Start box and make the line above it gone.
        // if last, add the End box and make the line below it gone.


    }

    private void setUpRouteVisualisation(Bundle savedInstanceState){
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_journey_routes_list_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);

        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        //adapter
        final JourneyRouteLegVisualAdapter routeAdapter = new JourneyRouteLegVisualAdapter(getDataProvider(), mJourney.getJourneyLegs());
        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(routeAdapter);       // wrap for expanding
        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Need to disable them when using animation indicator.
        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(false);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z1_mdpi)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(this, R.xml.list_divider_h), true));

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);


    }

    private void setUpTopInfo(){
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

        String title = "Route " + getIntent().getStringExtra(Constants.INTENT_JOURNEY_ROUTE_NUMBER);
        setTitle(title);

        DateFormat date = new SimpleDateFormat(getString(R.string.am_pm_time_format), Locale.ENGLISH);
        StopTime startStopTime = null;
        for (StopTime st : mJourney.getJourneyLegs().get(0).getTimetable().getStopTimes()) {
            if (st.getStop().equals(mJourney.getJourneyLegs().get(0).getStartStop())) {
                startStopTime = st;
            }
        }
        fromTime.setText(date.format(startStopTime.getDepartureTime()));

        int lastLeg = mJourney.getJourneyLegs().size() - 1;
        int lastStopPos = mJourney.getJourneyLegs().get(lastLeg).getTimetable().getStopTimes().size() - 1;
        StopTime endStopTime = null;
        for (StopTime st : mJourney.getJourneyLegs().get(lastLeg).getTimetable().getStopTimes()) {
            if (st.getStop().equals(mJourney.getJourneyLegs().get(0).getEndStop())) {
                endStopTime = st;
            }
        }
        toTime.setText(date.format(endStopTime.getArrivalTime()));

        long duration = mJourney.getArrivalTime().getTime() - mJourney.getDepartureTime().getTime();
        String durationText = "(" + Common.getDurationTime(duration, true, true, false).trim() + ")";
        durationTextView.setText(durationText);
        price.setText(String.valueOf(mJourney.getmPrice()));

        int transfers = mJourney.getJourneyLegs().size() - 1;
        String transferText = " " + String.valueOf(transfers);
        transfersTextView.setText(transferText);

        Common.makeViewVisible(speedLayout, mJourney.isFast());
        Common.makeViewVisible(priceLayout, mJourney.isCheap());
        Common.makeViewVisible(convenienceLayout, mJourney.isConvenient());
        ((ImageView)speedLayout.getChildAt(0)).getDrawable().setAlpha(Constants.OPAQUE);
        ((ImageView)priceLayout.getChildAt(0)).getDrawable().setAlpha(Constants.OPAQUE);
        ((ImageView)convenienceLayout.getChildAt(0)).getDrawable().setAlpha(Constants.OPAQUE);
        for (JourneyLeg jl : mJourney.getJourneyLegs()) {
            int mode = jl.getTimetable().getStopTimes().get(0).getStop().getStopType();
            Common.setTransportModes(mode, trainImage, busImage, ferryImage, lightRailImage);
        }
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        int childItemHeight = getResources().getDimensionPixelSize(R.dimen.journey_route_visual_stop_height);
        int topMargin = (int) (getResources().getDisplayMetrics().density * 16); // top-spacing: 16dp
        int bottomMargin = topMargin; // bottom-spacing: 16dp

        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, topMargin, bottomMargin);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public AbstractExpandableDataProvider getDataProvider() {
        return this.getDataProvider();
    }

}
