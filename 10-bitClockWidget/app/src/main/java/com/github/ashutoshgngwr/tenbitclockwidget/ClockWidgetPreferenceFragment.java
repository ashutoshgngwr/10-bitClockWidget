/*
 * Copyright 2017 Ashutosh Gangwar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.ashutoshgngwr.tenbitclockwidget;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class ClockWidgetPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.widget_preference);

        ListPreference dotSize = (ListPreference) findPreference("dot_size");
        dotSize.setSummary(dotSize.getEntry());

        ListPreference updateFrequency = (ListPreference) findPreference("update_frequency");
        updateFrequency.setSummary(updateFrequency.getEntry());

        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(extrasPreferenceClickListener);

        Preference help = findPreference("help");
        help.setOnPreferenceClickListener(extrasPreferenceClickListener);
    }

    private Preference.OnPreferenceClickListener extrasPreferenceClickListener
            = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent activityIntent = new Intent(getActivity(), WebViewExtrasActivity.class);

            switch(preference.getKey()) {
                case "about":
                    activityIntent.putExtra(WebViewExtrasActivity.EXTRA_ASSET_FILE, "about.html");
                    break;
                case "help":
                    activityIntent.putExtra(WebViewExtrasActivity.EXTRA_ASSET_FILE, "help.html");
                    break;
            }

            startActivity(activityIntent);
            return true;
        }
    };

    // Listening for changes in SharedPreferences to get updated values of ListPreference.
    // Adding OnPreferenceChangeListener gives old value for ListPreference#getEntry()
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if(preference instanceof ListPreference)
            preference.setSummary(((ListPreference) preference).getEntry());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
