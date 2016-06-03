package com.id11303765.commute.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.AppCompatPreferenceActivity;
import com.id11303765.commute.utils.Constants;


public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AppPreferenceFragment()).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(Constants.INTENT_SETTINGS_BACK, Constants.INTENT_SETTINGS_BACK);
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    public static class AppPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            final Preference homePreference = findPreference(getString(R.string.home_preference));
            final Preference workPreference = findPreference(getString(R.string.work_preference));

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            homePreference.setSummary(sharedPreferences.getString(getString(R.string.home_preference),getString(R.string.home_preference_summary)));
            homePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), StopSearchActivity.class);
                    intent.putExtra(Constants.INTENT_REQUEST, Constants.SETTINGS_HOME_TO_SEARCH_REQUEST);
                    String text = workPreference.getSummary().toString();
                    if (!text.matches("") && !text.matches(getString(R.string.work_preference_summary))){
                        intent.putExtra(Constants.INTENT_SEARCH_EXCLUDE, text);
                    }
                    startActivityForResult(intent, Constants.SETTINGS_HOME_TO_SEARCH_REQUEST);
                    return true;
                }
            });
            workPreference.setSummary(sharedPreferences.getString(getString(R.string.work_preference), getString(R.string.work_preference_summary)));
            workPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), StopSearchActivity.class);
                    intent.putExtra(Constants.INTENT_REQUEST, Constants.SETTINGS_WORK_TO_SEARCH_REQUEST);
                    String text = homePreference.getSummary().toString();
                    if (!text.matches("") && !text.matches(getString(R.string.home_preference_summary))){
                        intent.putExtra(Constants.INTENT_SEARCH_EXCLUDE, text);
                    }
                    startActivityForResult(intent, Constants.SETTINGS_WORK_TO_SEARCH_REQUEST);
                    return true;
                }
            });
            bindPreferenceSummaryToValue(findPreference(getString(R.string.opal_card_type)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_launch_screen_preference)));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            String value;
            SharedPreferences.Editor editor;
            super.onActivityResult(requestCode, resultCode, data);
            final Preference homePreference = findPreference(getString(R.string.home_preference));
            final Preference workPreference = findPreference(getString(R.string.work_preference));
            switch (requestCode){
                case Constants.SETTINGS_HOME_TO_SEARCH_REQUEST:
                    if (data != null){
                        value = data.getStringExtra(Constants.INTENT_SELECTED_STOP_NAME);
                        homePreference.setSummary(value);
                        editor = homePreference.getSharedPreferences().edit();
                        editor.putString(getString(R.string.home_preference),value);
                        editor.apply();
                    }
                    break;
                case Constants.SETTINGS_WORK_TO_SEARCH_REQUEST:
                    if (data != null){
                        value = data.getStringExtra(Constants.INTENT_SELECTED_STOP_NAME);
                        workPreference.setSummary(value);
                        editor = workPreference.getSharedPreferences().edit();
                        editor.putString(getString(R.string.work_preference),value);
                        editor.apply();
                    }
                    break;
            }
        }
    }
}
