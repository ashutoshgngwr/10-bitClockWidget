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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

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

	protected static int getUpdateFrequency() {
		return Integer.parseInt(getPreferences().getString("update_frequency", "0"));
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

	public static Integer getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			Log.w(ClockWidgetSettings.class.getSimpleName(), e);
			return 0;
		}
	}
}
