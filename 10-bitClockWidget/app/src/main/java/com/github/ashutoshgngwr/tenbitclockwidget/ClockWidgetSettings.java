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

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

class ClockWidgetSettings {

  private static SharedPreferences sharedPreferencesInstance;

  private static SharedPreferences getPreferences() {
    if (sharedPreferencesInstance == null) {
      sharedPreferencesInstance = PreferenceManager.getDefaultSharedPreferences(
          ClockWidgetApplication.getContext());
    }

    return sharedPreferencesInstance;
  }

  static int getDotSize() {
    return Integer.parseInt(getPreferences().getString("dot_size", "0"));
  }

  static boolean shouldDisplaySeparator() {
    return getPreferences().getBoolean("display_separator", false);
  }

  static boolean shouldUse24HourFormat() {
    return getPreferences().getBoolean("24hour_format", false);
  }

  static boolean shouldUse6bitsForHour() {
    return getPreferences().getBoolean("6bits_hour", false);
  }

  static int getClockAMColor() {
    return getPreferences().getInt("am_color", 0);
  }

  static int getClockPMColor() {
    return getPreferences().getInt("pm_color", 0);
  }

  static int getClockBackgroundColor() {
    return getPreferences().getInt("background_color", 0);
  }
}
