package com.id11303765.commute.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.id11303765.commute.R;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.JourneyLeg;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.utils.AbstractExpandableDataProvider;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;
import com.id11303765.commute.view.journey.JourneyRouteActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class JourneyRouteLegVisualAdapter extends AbstractExpandableItemAdapter<JourneyRouteLegVisualAdapter.RouteLegViewHolder, JourneyRouteLegVisualAdapter.StopViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<JourneyLeg> mJourneyLegList;
    private ArrayList<StopTime> mStopTimeList;
    private Activity mActivity;
    private AbstractExpandableDataProvider mProvider;

    private static final String TAG = "MyExpandableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {
    }

    public JourneyRouteLegVisualAdapter(AbstractExpandableDataProvider dataProvider, ArrayList<JourneyLeg> journeyLegs) {
        mProvider = dataProvider;
        mJourneyLegList = journeyLegs;
        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public RouteLegViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.adapter_item_journey_leg_row, parent, false);
        return new RouteLegViewHolder(v);
    }

    @Override
    public StopViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.adapter_item_station_search_row, parent, false);
        return new StopViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(RouteLegViewHolder holder, int groupPosition, int viewType) {
        // child item
        final AbstractExpandableDataProvider.BaseData item = mProvider.getGroupItem(groupPosition);
        JourneyLeg currentJourneyLeg = mJourneyLegList.get(groupPosition);


    }

    @Override
    public void onBindChildViewHolder(StopViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // group item
        final AbstractExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);

    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(RouteLegViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
        if (mProvider.getGroupItem(groupPosition).isPinned()) {
            // return false to raise View.OnClickListener#onClick() event
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        return true;
    }


    /**
     * Class that links the items from the adapter_item XML layout file to variables
     */

    static class RouteLegViewHolder extends AbstractExpandableItemViewHolder {
        RouteLegViewHolder(View v) {
            super(v);

        }
    }

    static class StopViewHolder extends AbstractExpandableItemViewHolder {
        StopViewHolder(View v) {
            super(v);
        }
    }

}
