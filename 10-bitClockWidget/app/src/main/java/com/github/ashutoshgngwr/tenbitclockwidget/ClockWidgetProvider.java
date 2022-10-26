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
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class ClockWidgetProvider extends AppWidgetProvider {

	private static final String TAG = ClockWidgetProvider.class.getSimpleName();
	private static final int RC_OPEN_CLOCK = 0x12;
	private static final int RC_UPDATE = 0x13;

	private AlarmManager mAlarmManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		mAlarmManager = ContextCompat.getSystemService(context, AlarmManager.class);
		if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction()))
			onUpdate(context, AppWidgetManager.getInstance(context), null);
		else
			super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, @NonNull AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// get widget ids for all available instances
		if (appWidgetIds == null) {
			appWidgetIds = appWidgetManager.getAppWidgetIds(getComponentName(context));
		}

		if (appWidgetIds == null || appWidgetIds.length == 0) {
			Log.d(TAG, "Nothing to update... Bailing out!");
			return; // No widget is added to home screen. Bailing out!
		}

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.clock_widget_layout);
		remoteViews.setImageViewBitmap(R.id.iv_clock, ClockWidgetRenderer.renderBitmap());
		remoteViews.setOnClickPendingIntent(R.id.iv_clock, createOnClickPendingIntent(context));
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		Log.d(TAG, "Finished updating widget!");
		scheduleUpdateClockAlarm(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		scheduleUpdateClockAlarm(context, null);
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "cancelling alarm for update clock broadcast...");
		mAlarmManager.cancel(createUpdateBroadcastPendingIntent(context, null));
	}

	private void scheduleUpdateClockAlarm(Context context, int[] widgetIds) {
		Log.d(TAG, "scheduling alarm for update clock broadcast...");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MINUTE, 1);
		mAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), createUpdateBroadcastPendingIntent(context, widgetIds));
	}

	// Creates PendingIntent for default activity of default alarm clock application
	private PendingIntent createOnClickPendingIntent(Context context) {
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;

		return PendingIntent.getActivity(
			context, RC_OPEN_CLOCK,
			new Intent()
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				.setAction(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
					? AlarmClock.ACTION_SHOW_ALARMS : AlarmClock.ACTION_SET_ALARM),
			flags);
	}

	private PendingIntent createUpdateBroadcastPendingIntent(Context context, int[] widgetIds) {
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
		return PendingIntent.getBroadcast(context, RC_UPDATE, createUpdateBroadcastIntent(context, widgetIds), flags);
	}

	protected static Intent createUpdateBroadcastIntent(Context context, int[] widgetIds) {
		if (widgetIds == null) {
			final ComponentName componentName = getComponentName(context);
			widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
		}

		return new Intent(context, ClockWidgetProvider.class)
			.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
			.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
	}

	private static ComponentName getComponentName(Context context) {
		return new ComponentName(context.getPackageName(), ClockWidgetProvider.class.getName());
	}
}
