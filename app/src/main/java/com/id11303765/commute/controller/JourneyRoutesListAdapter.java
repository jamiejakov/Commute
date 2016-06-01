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
import com.id11303765.commute.utils.Constants;

import java.util.ArrayList;


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
        Journey currentStopData = mJourneyList.get(position);
        //String name = currentStopData.getShortName();
        //holder.mName.setText(name);
        //int image = currentStopData.getStopType();
        //if (image != 0) {
         //   holder.mImage.setImageResource(image);
        //}
    }

    @Override
    public int getItemCount() {
        return mJourneyList.size();
    }

    /**
     * Class that links the items from the adapter_item XML layout file to variables
     */
    class StopSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mName;
        private ImageView mImage;

        /**
         * Initialising all the UI elements and linking them to the xml layout file
         */
        public StopSearchViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mName = (TextView) view.findViewById(R.id.adapter_item_journey_route_row_route_bullet);
            mImage = (ImageView) view.findViewById(R.id.adapter_item_station_search_transport_image_view);
            RelativeLayout row = (RelativeLayout) itemView.findViewById(R.id.adapter_item_station_search_relative_layout);
            row.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent intent = new Intent();
            intent.putExtra(Constants.INTENT_SELECTED_STOP_NAME, mName.getText());
            //mActivity.setResult(mIntentRequest, intent);
            //mActivity.finish();
        }
    }
}
