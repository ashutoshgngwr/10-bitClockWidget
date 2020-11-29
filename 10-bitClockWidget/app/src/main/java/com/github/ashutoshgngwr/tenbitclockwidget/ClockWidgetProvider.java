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

import androidx.core.content.ContextCompat;

public class ClockWidgetProvider extends AppWidgetProvider {

	protected static final String ACTION_UPDATE_CLOCK = "action_update_clock";
	private static final String TAG = ClockWidgetProvider.class.getSimpleName();
	private static final int RC_OPEN_CLOCK = 0x12;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction()) || ACTION_UPDATE_CLOCK.equals(intent.getAction()))
			onUpdate(context, AppWidgetManager.getInstance(context), null);
		else
			super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// get widget ids for all available instances
		int[] ids = appWidgetManager.getAppWidgetIds(
			new ComponentName(context.getPackageName(), getClass().getName()));

		if (ids.length == 0) {
			Log.d(TAG, "Nothing to update... Bailing out!");
			return; // No widget is added to home screen. Bailing out!
		}

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.clock_widget_layout);
		remoteViews.setImageViewBitmap(R.id.iv_clock, ClockWidgetRenderer.renderBitmap());
		remoteViews.setOnClickPendingIntent(R.id.iv_clock, createOnClickPendingIntent(context));
		appWidgetManager.updateAppWidget(ids, remoteViews);
		Log.d(TAG, "Finished updating widget!");

		// start clock update service if it wasn't running already.
		onEnabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		if (ClockWidgetUpdateService.isRunning()) {
			return;
		}

		Log.d(TAG, "Start widget update service...");
		ContextCompat.startForegroundService(context, new Intent(context, ClockWidgetUpdateService.class));
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "Stopping widget update service...");
		context.stopService(new Intent(context, ClockWidgetUpdateService.class));
	}

	// Creates PendingIntent for default activity of default alarm clock application
	private PendingIntent createOnClickPendingIntent(Context context) {
		return PendingIntent.getActivity(
			context, RC_OPEN_CLOCK,
			new Intent()
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				.setAction(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
					? AlarmClock.ACTION_SHOW_ALARMS : AlarmClock.ACTION_SET_ALARM),
			PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
