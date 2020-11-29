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

import android.app.Application;
import android.content.Context;

import androidx.preference.PreferenceManager;

public class ClockWidgetApplication extends Application {

	private static ClockWidgetApplication mInstance;

	static Context getContext() {
		return mInstance.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		// Load default values from widget_preference.xml file to shared preferences.
		PreferenceManager.setDefaultValues(this, R.xml.widget_preference, false);
	}
}
