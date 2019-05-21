/*
 *     Copyright (C) 2017  Ashutosh Gangwar
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ashutoshgngwr.tenbitclockwidget;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class ClockWidgetPreferenceFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  private Preference.OnPreferenceClickListener extrasPreferenceClickListener
      = new Preference.OnPreferenceClickListener() {
    @Override
    public boolean onPreferenceClick(Preference preference) {
      Intent activityIntent = new Intent(getActivity(), WebViewExtrasActivity.class);

      switch (preference.getKey()) {
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

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.widget_preference, rootKey);

    ListPreference dotSize = (ListPreference) findPreference("dot_size");
    dotSize.setSummary(dotSize.getEntry());

    Preference about = findPreference("about");
    about.setOnPreferenceClickListener(extrasPreferenceClickListener);

    Preference help = findPreference("help");
    help.setOnPreferenceClickListener(extrasPreferenceClickListener);
  }

  // Listening for changes in SharedPreferences to get updated values of ListPreference.
  // Adding OnPreferenceChangeListener gives old value for ListPreference#getEntry()
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference preference = findPreference(key);
    if (preference instanceof ListPreference)
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
