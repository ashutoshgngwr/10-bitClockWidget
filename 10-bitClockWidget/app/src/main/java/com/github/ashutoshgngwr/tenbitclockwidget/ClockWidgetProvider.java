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
