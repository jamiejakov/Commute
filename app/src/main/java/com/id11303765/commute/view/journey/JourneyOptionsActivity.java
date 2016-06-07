package com.id11303765.commute.view.journey;

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
import com.id11303765.commute.view.SettingsActivity;
import com.id11303765.commute.view.StopSearchActivity;

public class JourneyOptionsActivity extends AppCompatPreferenceActivity {
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new JourneyOptionsActivity.JourneyOptionsFragment()).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.journey_options));
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


    /**
     * Binds the preference summary to the selected value
     *
     * @param preference - preference item to use
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * Preference fragment class
     * Necessary for preferences to function.
     */
    public static class JourneyOptionsFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.journey_preferences);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.default_search_pref)));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        }
    }
}
