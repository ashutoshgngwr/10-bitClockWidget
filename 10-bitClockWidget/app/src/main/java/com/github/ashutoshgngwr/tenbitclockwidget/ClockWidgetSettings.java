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
import android.preference.PreferenceManager;

public class ClockWidgetSettings {

  private static SharedPreferences sharedPreferencesInstance;

  protected static int getDotSize() {
    return Integer.parseInt(getPreferences().getString("dot_size", "0"));
  }

  private static SharedPreferences getPreferences() {
    if (sharedPreferencesInstance == null) {
      sharedPreferencesInstance = PreferenceManager.getDefaultSharedPreferences(
          ClockWidgetApplication.getContext());
    }

    return sharedPreferencesInstance;
  }

  protected static boolean shouldDisplaySeparator() {
    return getPreferences().getBoolean("display_separator", false);
  }

  protected static int getClockAMColor() {
    return getPreferences().getInt("am_color", 0);
  }

  protected static int getClockPMColor() {
    return getPreferences().getInt("pm_color", 0);
  }

  protected static int getClockBackgroundColor() {
    return getPreferences().getInt("background_color", 0);
  }
}
