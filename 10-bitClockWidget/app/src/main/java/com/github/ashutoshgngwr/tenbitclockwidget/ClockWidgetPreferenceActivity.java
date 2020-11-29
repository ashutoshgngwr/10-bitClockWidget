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
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ClockWidgetPreferenceActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportFragmentManager().beginTransaction()
			.replace(android.R.id.content, new ClockWidgetPreferenceFragment())
			.commit();

		// always set result OK because all widget settings are optional for user to configure.
		setResult(RESULT_OK, getIntent());
	}

	@Override
	protected void onStop() {
		super.onStop();

		// PreferenceActivity is no longer in foreground. Update widget!
		LocalBroadcastManager
			.getInstance(this)
			.sendBroadcast(new Intent(ClockWidgetProvider.ACTION_UPDATE_CLOCK));
	}
}
