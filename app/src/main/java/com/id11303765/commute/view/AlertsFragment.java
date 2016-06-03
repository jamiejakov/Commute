package com.id11303765.commute.view;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.id11303765.commute.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AlertsFragment extends Fragment {
    View mCustomView;

    public AlertsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_alerts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.alerts);
        GetAlertsAsync getAlerts = new GetAlertsAsync();
        getAlerts.execute();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    private void getAlerts(){
        StringBuilder urlBuilder = new StringBuilder("https://api.transport.nsw.gov.au/v1/gtfs/alerts/sydneytrains");
        URL url = null;
        try {
            url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer 5e290175-9398-439d-8159-29a3bd141d1e");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line+"\n");

            }
            rd.close();

            conn.disconnect();
            System.out.println(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of Timetables connecting the 2 stops for the daily commute.
     */
    private class GetAlertsAsync extends AsyncTask<Void, Void, Void> {

        private ProgressDialog mDialog;


        GetAlertsAsync() {
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
           // mDialog.show();
            mDialog.setMessage(getString(R.string.loading_alerts));
            mDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            getAlerts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (this.mDialog.isShowing()) {
                this.mDialog.dismiss();
            }
        }
    }
}
