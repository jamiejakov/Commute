package com.id11303765.commute.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.Stop;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


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
        View view = mInflater.inflate(R.layout.adapter_item_station_search_row, parent, false);
        return new StopSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StopSearchViewHolder holder, int position) {
        Journey currentJourneyData = mJourneyList.get(position);
        holder.mPosition.setText(position);
        DateFormat date = new SimpleDateFormat(mActivity.getString(R.string.am_pm_time_format), Locale.ENGLISH);
        StopTime startStopTime = currentJourneyData.getTripTimetables().get(0).getStopTimes().get(0);
        Date departureTime = startStopTime.getDepartureTime();
        holder.mFromTime.setText(date.format(departureTime));
        int lastTimetable = currentJourneyData.getTripTimetables().size() - 1;
        int lastStop = currentJourneyData.getTripTimetables().get(lastTimetable).getStopTimes().size()-1;
        Date arrivalTime = currentJourneyData.getTripTimetables().get(lastTimetable).getStopTimes().get(lastStop).getArrivalTime();
        holder.mToTime.setText(date.format(arrivalTime));
        holder.mDuration.setText(Common.getDurationTime(currentJourneyData.getTimeInMillis()));
        holder.mPrice.setText("$2.95");
        holder.mTransfers.setText(currentJourneyData.getTripTimetables().size());

        String transferLocs = startStopTime.getStop().getShortName() + mActivity.getString(R.string.arrow_right);
        for (int i = 1; i< currentJourneyData.getTripTimetables().size()-1; i++) {
            Timetable t = currentJourneyData.getTripTimetables().get(i);
            transferLocs += t.getStopTimes().get(0).getStop().getShortName() + mActivity.getString(R.string.arrow_right);
        }
        transferLocs += currentJourneyData.getTripTimetables().get(lastTimetable).getStopTimes().get(lastStop).getStop().getShortName();
        holder.mTransferLocations.setText(transferLocs);

        setVisibility(holder.mSpeedCircle,currentJourneyData.isFast());
        setVisibility(holder.mPriceCircle,currentJourneyData.isCheap());
        setVisibility(holder.mConvenienceCircle,currentJourneyData.isConvenient());
        for (Timetable t : currentJourneyData.getTripTimetables()){
            setTransportModes(holder, t.getStopTimes().get(0).getStop().getStopType());
        }

    }

    private void setTransportModes(StopSearchViewHolder holder, int mode){
        switch (mode){
            case R.drawable.tnsw_icon_train:
                setVisibility(holder.mTrain, true);
            case R.drawable.tnsw_icon_light_rail:
                setVisibility(holder.mLightRail, true);
            case R.drawable.tnsw_icon_ferry:
                setVisibility(holder.mFerry, true);
        }
    }

    private void setVisibility(View view, boolean enabled){
        if(enabled){
            view.setVisibility(View.GONE);
        }
        else{
            view.setVisibility(View.VISIBLE);
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
        private ImageView mFerry;
        private ImageView mLightRail;
        private View mSpeedCircle;
        private View mPriceCircle;
        private View mConvenienceCircle;

        /**
         * Initialising all the UI elements and linking them to the xml layout file
         */
        public StopSearchViewHolder(View view) {
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
            mFerry = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_transport_mode_ferry_image);
            mLightRail = (ImageView) view.findViewById(R.id.adapter_item_journey_route_row_transport_mode_light_rail_image);
            mSpeedCircle = view.findViewById(R.id.adapter_item_journey_route_row_speed_circle);
            mPriceCircle = view.findViewById(R.id.adapter_item_journey_route_row_price_circle);
            mConvenienceCircle = view.findViewById(R.id.adapter_item_journey_route_row_convenience_circle);

            RelativeLayout row = (RelativeLayout) itemView.findViewById(R.id.adapter_item_station_search_relative_layout);
            row.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent intent = new Intent();
            //intent.putExtra(Constants.INTENT_SELECTED_STOP_NAME, mName.getText());
            //mActivity.setResult(mIntentRequest, intent);
            //mActivity.finish();
        }
    }
}
