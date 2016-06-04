package com.id11303765.commute.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.JourneyLeg;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;
import com.id11303765.commute.view.journey.JourneyRouteActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class JourneyRoutesListAdapter extends RecyclerView.Adapter<JourneyRoutesListAdapter.StopSearchViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Journey> mJourneyList;
    private Activity mActivity;

    public JourneyRoutesListAdapter(Activity activity, Context context, ArrayList<Journey> stopList) {
        mInflater = LayoutInflater.from(context);
        mJourneyList = stopList;
        mActivity = activity;
    }

    @Override
    public StopSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_item_journey_route_row, parent, false);
        return new StopSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StopSearchViewHolder holder, int position) {
        Journey currentJourneyData = mJourneyList.get(position);
        holder.mPosition.setText(String.valueOf(position + 1));

        DateFormat date = new SimpleDateFormat(mActivity.getString(R.string.am_pm_time_format), Locale.ENGLISH);
        StopTime startStopTime = null;
        for (StopTime st : currentJourneyData.getJourneyLegs().get(0).getTimetable().getStopTimes()) {
            if (st.getStop().equals(currentJourneyData.getJourneyLegs().get(0).getStartStop())) {
                startStopTime = st;
            }
        }
        holder.mFromTime.setText(date.format(startStopTime.getDepartureTime()));

        int lastLeg = currentJourneyData.getJourneyLegs().size() - 1;
        int lastStopPos = currentJourneyData.getJourneyLegs().get(lastLeg).getTimetable().getStopTimes().size() - 1;
        StopTime endStopTime = null;
        for (StopTime st : currentJourneyData.getJourneyLegs().get(lastLeg).getTimetable().getStopTimes()) {
            if (st.getStop().equals(currentJourneyData.getJourneyLegs().get(0).getEndStop())) {
                endStopTime = st;
            }
        }
        holder.mToTime.setText(date.format(endStopTime.getArrivalTime()));

        long duration = currentJourneyData.getArrivalTime().getTime() - currentJourneyData.getDepartureTime().getTime();
        String durationText = "(" + Common.getDurationTime(duration, true, true, false).trim() + ")";
        holder.mDuration.setText(durationText);
        holder.mPrice.setText(String.valueOf(currentJourneyData.getmPrice()));

        int transfers = currentJourneyData.getJourneyLegs().size() - 1;
        String transferText = " " + String.valueOf(transfers);
        holder.mTransfers.setText(transferText);
        if (transfers == 0) {
            holder.mTransfers.setTypeface(holder.mTransfers.getTypeface(), Typeface.BOLD);
        } else {
            holder.mTransfers.setTypeface(holder.mTransfers.getTypeface(), Typeface.NORMAL);
        }

        String transferLocs = startStopTime.getStop().getShortName() + mActivity.getString(R.string.arrow_right);
        for (int i = 1; i < currentJourneyData.getJourneyLegs().size() - 1; i++) {
            Timetable t = currentJourneyData.getJourneyLegs().get(i).getTimetable();
            transferLocs += t.getStopTimes().get(0).getStop().getShortName() + mActivity.getString(R.string.arrow_right);
        }
        transferLocs += endStopTime.getStop().getShortName();
        holder.mTransferLocations.setText(transferLocs);

        Common.makeViewVisible(holder.mSpeedCircle, currentJourneyData.isFast());
        Common.makeViewVisible(holder.mPriceCircle, currentJourneyData.isCheap());
        Common.makeViewVisible(holder.mConvenienceCircle, currentJourneyData.isConvenient());
        holder.mSpeedCircle.getDrawable().setAlpha(Constants.OPAQUE);
        holder.mPriceCircle.getDrawable().setAlpha(Constants.OPAQUE);
        holder.mConvenienceCircle.getDrawable().setAlpha(Constants.OPAQUE);

        for (JourneyLeg jl : currentJourneyData.getJourneyLegs()) {
            int mode = jl.getTimetable().getStopTimes().get(0).getStop().getStopType();
            Common.setTransportModes(mode, holder.mTrain, holder.mBus, holder.mFerry, holder.mLightRail);
        }

    }

    @Override
    public int getItemCount() {
        return mJourneyList.size();
    }

    /**
     * Class that links the items from the adapter_item XML layout file to variables
     */
    class StopSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mPosition;
        private TextView mFromTime;
        private TextView mToTime;
        private TextView mDuration;
        private TextView mPrice;
        private TextView mTransfers;
        private TextView mTransferLocations;
        private ImageView mTrain;
        private ImageView mBus;
        private ImageView mFerry;
        private ImageView mLightRail;
        private ImageView mSpeedCircle;
        private ImageView mPriceCircle;
        private ImageView mConvenienceCircle;

        /**
         * Initialising all the UI elements and linking them to the xml layout file
         */
        StopSearchViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mPosition = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_route_bullet);
            mFromTime = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_from_time_text);
            mToTime = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_to_time_text);
            mDuration = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_duration_text);
            mPrice = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_price_amount_text);
            mTransfers = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_transfers_number_text);
            mTransferLocations = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_transfers_stops_list_text);
            mTrain = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_transport_mode_train_image);
            mBus = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_transport_mode_bus_image);
            mFerry = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_transport_mode_ferry_image);
            mLightRail = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_transport_mode_light_rail_image);
            mSpeedCircle = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_speed_circle);
            mPriceCircle = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_price_circle);
            mConvenienceCircle = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_convenience_circle);

            RelativeLayout row = (RelativeLayout) itemView.findViewById(R.id.adapter_item_journey_route_row_relative_layout);
            row.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent intent = new Intent(mActivity, JourneyRouteActivity.class);
            intent.putExtra(Constants.INTENT_JOURNEY_ROUTE, mJourneyList.get(getAdapterPosition()).getPK());
            intent.putExtra(Constants.INTENT_JOURNEY_ROUTE_NUMBER, mPosition.getText());
            mActivity.startActivityForResult(intent,Constants.JOURNEY_ROUTE_LIST_TO_ROUTE_REQUEST);
        }
    }
}
