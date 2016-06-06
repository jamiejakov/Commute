package com.id11303765.commute.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.id11303765.commute.R;
import com.id11303765.commute.model.JourneyLeg;
import com.id11303765.commute.model.Route;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.utils.Common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class JourneyRouteLegVisualAdapter extends AbstractExpandableItemAdapter<JourneyRouteLegVisualAdapter.RouteLegViewHolder, JourneyRouteLegVisualAdapter.StopViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<JourneyLeg> mJourneyLegList;
    private Activity mActivity;
    private Context mContext;
    private String mArrivalTimeForNext;
    private boolean[] mOpen;
    private int[] mRotationDirection;

    public JourneyRouteLegVisualAdapter(Context context, Activity activity, ArrayList<JourneyLeg> journeyLegs) {
        mActivity = activity;
        mContext = context;
        mJourneyLegList = journeyLegs;
        mOpen = new boolean[mJourneyLegList.size()];
        mRotationDirection = new int[mJourneyLegList.size()];
        for (int i = 0; i < mOpen.length; i++) {
            mOpen[i] = false;
        }
        for (int i = 0; i < mRotationDirection.length; i++) {
            mRotationDirection[i] = 1;
        }
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return mJourneyLegList.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mJourneyLegList.get(groupPosition).getTimetable().getStopTimes().size() - 2;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
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
        final View v = inflater.inflate(R.layout.adapter_item_journey_stop_row, parent, false);
        return new StopViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(RouteLegViewHolder holder, int groupPosition, int viewType) {
        JourneyLeg currentJourneyLeg = mJourneyLegList.get(groupPosition);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mActivity.getString(R.string.am_pm_time_format), Locale.US);

        if (groupPosition == 0) {
            Common.makeViewVisible(holder.mArrivalTime, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                holder.mStopIndicator.setBackground(mActivity.getDrawable(R.drawable.stop_start_shape));
            }else{
                holder.mStopIndicator.setBackground(mActivity.getResources().getDrawable(R.drawable.stop_start_shape));
            }
            GradientDrawable stopIndicatorShape = (GradientDrawable) holder.mStopIndicator.getBackground();
            stopIndicatorShape.setColor(currentJourneyLeg.getTimetable().getTrip().getRoute().getColor());
            holder.mStopIndicator.setText(R.string.start);
            StopTime lastStopTime = currentJourneyLeg.getTimetable().getStopTimes().get(currentJourneyLeg.getTimetable().getStopTimes().size() - 1);
            mArrivalTimeForNext = simpleDateFormat.format(lastStopTime.getArrivalTime());
        } else {
            Common.makeViewVisible(holder.mArrivalTime, true);
            holder.mArrivalTime.setText(mArrivalTimeForNext);
        }

        holder.mDepartureTime.setText(simpleDateFormat.format(currentJourneyLeg.getTimetable().getStopTimes().get(0).getDepartureTime()));
        holder.mStopName.setText(currentJourneyLeg.getStartStop().getShortName());
        String stopCountString = String.valueOf(currentJourneyLeg.getTimetable().getStopTimes().size() - 1) + "\nstops";
        holder.mStopCount.setText(stopCountString);
        holder.mLineLine.setBackgroundColor(currentJourneyLeg.getTimetable().getTrip().getRoute().getColor());
        holder.mTransportMode.setImageResource(currentJourneyLeg.getStartStop().getStopType());
        holder.mTransportMode.setVisibility(View.VISIBLE);
        if (currentJourneyLeg.getTimetable().getTrip().getRoute().getAgency().getID().equals(mActivity.getString(R.string.regional_trains_agency))) {
            holder.mTransportMode.setImageResource(R.drawable.tnsw_icon_regional_train);
        }

        Route selectedRoute = currentJourneyLeg.getTimetable().getTrip().getRoute();

        String line = selectedRoute.getLongName();
        if (selectedRoute.getAgency().getID().equals(mActivity.getString(R.string.sydney_trains_agency))) {
            holder.mLineIdFrame.setVisibility(View.VISIBLE);
            GradientDrawable lineShape = (GradientDrawable) holder.mLineIdFrame.getBackground();
            lineShape.setColor(selectedRoute.getColor());

            holder.mLineIdFrameText.setText(line.substring(0, 2));
            holder.mLineName.setText(line.substring(2, line.length()));

        } else {
            holder.mLineName.setText(line);
            holder.mLineIdFrame.setVisibility(View.GONE);
        }

        String desString = "for " + currentJourneyLeg.getTimetable().getTrip().getHeadSign();
        holder.mDestination.setText(desString);

        holder.mFromPlatform.setText(currentJourneyLeg.getStartStop().getName());
        holder.mToPlatform.setText(currentJourneyLeg.getEndStop().getName());

        if (!mOpen[groupPosition]) {
            mRotationDirection[groupPosition] = -1;
            holder.mExpand.setRotation(180);
            mOpen[groupPosition] = !mOpen[groupPosition];
        } else {
            holder.mExpand.setRotation(0);
            mRotationDirection[groupPosition] = 1;
            mOpen[groupPosition] = !mOpen[groupPosition];
        }
        RotateAnimation rotate = new RotateAnimation(0f, mRotationDirection[groupPosition] * 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setFillAfter(true);
        rotate.setDuration(300);
        holder.mExpand.startAnimation(rotate);

    }

    @Override
    public void onBindChildViewHolder(StopViewHolder holder, int groupPosition, int childPosition, int viewType) {
        StopTime currentStopTime = mJourneyLegList.get(groupPosition).getTimetable().getStopTimes().get(childPosition + 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mActivity.getString(R.string.am_pm_time_format), Locale.US);
        holder.mArrivalAtStopTime.setText(simpleDateFormat.format(currentStopTime.getArrivalTime()));

        GradientDrawable stopIndicatorShape = (GradientDrawable) holder.mStopIndicator.getBackground();
        JourneyLeg leg = mJourneyLegList.get(groupPosition);
        stopIndicatorShape.setStroke(8, leg.getTimetable().getTrip().getRoute().getColor());
        holder.mLegLine.setBackgroundColor(leg.getTimetable().getTrip().getRoute().getColor());

        holder.mStopName.setText(currentStopTime.getStop().getShortName());
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(RouteLegViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }


    /**
     * Class that links the items from the adapter_item XML layout file to variables
     */

    static class RouteLegViewHolder extends AbstractExpandableItemViewHolder {
        private TextView mArrivalTime;
        private TextView mDepartureTime;
        private LinearLayout mExpandLayout;
        private TextView mStopCount;
        private ImageView mExpand;
        private TextView mStopIndicator;
        private FrameLayout mLineLine;
        private TextView mStopName;
        private ImageView mTransportMode;
        private FrameLayout mLineIdFrame;
        private TextView mLineIdFrameText;
        private TextView mDestination;
        private TextView mLineName;
        private TextView mFromPlatform;
        private TextView mToPlatform;

        RouteLegViewHolder(View view) {
            super(view);
            mArrivalTime = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_arrival_time_text);
            mDepartureTime = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_departure_time_text);
            mExpandLayout = (LinearLayout) view.findViewById(R.id.adapter_item_journey_leg_row_expand_stops_button_ll);
            mStopCount = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_expand_text);
            mExpand = (ImageView) view.findViewById(R.id.adapter_item_journey_leg_row_expand_arrow_image);
            mStopIndicator = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_stop_indicator_shape_tv);
            mLineLine = (FrameLayout) view.findViewById(R.id.adapter_item_journey_leg_row_leg_line_fl);
            mStopName = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_stop_name);
            mTransportMode = (ImageView) view.findViewById(R.id.adapter_item_journey_leg_row_transport_mode_image);
            mLineIdFrame = (FrameLayout) view.findViewById(R.id.adapter_item_journey_leg_row_line_number_frame);
            mLineIdFrameText = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_line_number_tv);
            mDestination = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_destination_tv);
            mLineName = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_line_name_tv);
            mFromPlatform = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_stop_from_name);
            mToPlatform = (TextView) view.findViewById(R.id.adapter_item_journey_leg_row_stop_to_name);
        }
    }

    static class StopViewHolder extends AbstractExpandableItemViewHolder {
        private TextView mArrivalAtStopTime;
        private TextView mStopIndicator;
        private FrameLayout mLegLine;
        private TextView mStopName;

        StopViewHolder(View view) {
            super(view);
            mArrivalAtStopTime = (TextView) view.findViewById(R.id.adapter_item_stop_row_time);
            mStopIndicator = (TextView) view.findViewById(R.id.adapter_item_journey_stop_row_stop_indicator_shape_tv);
            mLegLine = (FrameLayout) view.findViewById(R.id.adapter_item_journey_stop_row_leg_line_fl);
            mStopName = (TextView) view.findViewById(R.id.adapter_item_journey_stop_row_stop_name_tv);
        }
    }

}
