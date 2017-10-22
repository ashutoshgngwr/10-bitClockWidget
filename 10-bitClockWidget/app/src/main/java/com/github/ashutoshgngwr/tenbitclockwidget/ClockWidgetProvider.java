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

import android.app.AlarmManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ClockWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction()))
			onUpdate(context, AppWidgetManager.getInstance(context), null);
		else
			super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetIds[]) {
		// get widget ids for all available instances
		int ids[] = appWidgetManager.getAppWidgetIds(
				new ComponentName(context.getPackageName(), getClass().getName()));

		if (ids.length == 0)
			return; // No widget is added to home screen. Bailing out!

		// start update service
		Intent serviceIntent = new Intent(context, ClockWidgetUpdateService.class);
		context.startService(serviceIntent);
	}

	@Override
	public void onEnabled(Context context) {
		onUpdate(context, AppWidgetManager.getInstance(context), null);
	}

	@Override
	public void onDisabled(Context context) {
		// Cancel all existing alarms for clock update service.
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(ClockWidgetUpdateService.createClockUpdateIntent(context));
	}
}
