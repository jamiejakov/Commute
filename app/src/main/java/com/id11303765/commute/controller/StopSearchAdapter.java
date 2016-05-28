package com.id11303765.commute.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.model.Stop;

import java.util.ArrayList;

/**
 * Created by jamiejakov on 28/05/2016.
 */

public class StopSearchAdapter extends RecyclerView.Adapter<StopSearchAdapter.StopSearchViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Stop> mStopList;
    private Context mContext;

    public StopSearchAdapter(Context context, ArrayList<Stop> stopList) {
        mInflater = LayoutInflater.from(context);
        mStopList = stopList;
        mContext = context;
    }

    @Override
    public StopSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_item_station_search_row, parent, false);
        return new StopSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StopSearchViewHolder holder, int position) {
        Stop currentStopData = mStopList.get(position);
        holder.mName.setText(currentStopData.getName());
    }

    @Override
    public int getItemCount() {
        return mStopList.size();
    }


    /***********************************
    * View Holder and Interface classes
    *************************************/

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
            mName = (TextView) view.findViewById(R.id.adapter_item_station_search_text_view);
            mImage = (ImageView) view.findViewById(R.id.adapter_item_station_search_transport_image_view);
        }

        @Override
        public void onClick(View v) {

        }
    }
}