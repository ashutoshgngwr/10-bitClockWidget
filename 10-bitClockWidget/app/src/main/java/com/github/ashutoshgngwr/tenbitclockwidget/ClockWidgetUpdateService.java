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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Calendar;

public class ClockWidgetUpdateService extends Service implements Runnable {

  private static final int FOREGROUND_ID = 0x201;
  private static final int RC_PREFERENCE_ACTIVITY = 0x827;
  private static final String NOTIFICATION_CHANNEL_ID = "default";

  private Handler mHandler;
  private BroadcastReceiver mUpdateReceiver = new ClockWidgetProvider();

  private static volatile boolean isRunning = false;

  protected static boolean isRunning() {
    return isRunning;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    createNotificationChannel();
    startForeground(FOREGROUND_ID, createNotification());
    run();
    return START_STICKY;
  }

  private Notification createNotification() {
    return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentText(getString(R.string.service_notification__description))
        .setContentIntent(
            PendingIntent.getActivity(
                this, RC_PREFERENCE_ACTIVITY,
                new Intent(this, ClockWidgetPreferenceActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT))
        .setSmallIcon(R.drawable.ic_notification_default)
        .build();
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.notification_channel_default__name);
      String description = getString(R.string.notification_channel_default__description);

      int importance = NotificationManager.IMPORTANCE_MIN;
      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
      channel.setDescription(description);
      channel.setShowBadge(false);

      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      assert notificationManager != null;
      notificationManager.createNotificationChannel(channel);
    }
  }

  @Override
  public void onCreate() {
    isRunning = true;
    mHandler = new Handler();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(ClockWidgetProvider.ACTION_UPDATE_CLOCK);
    LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver, intentFilter);
  }

  @Override
  public void onDestroy() {
    mHandler.removeCallbacks(this);
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateReceiver);
    isRunning = false;
  }

  @Override
  public void run() {
    LocalBroadcastManager
        .getInstance(this)
        .sendBroadcast(new Intent(ClockWidgetProvider.ACTION_UPDATE_CLOCK));
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.SECOND, 0);
    calendar.add(Calendar.MINUTE, 1);
    mHandler.postDelayed(this, calendar.getTimeInMillis() - System.currentTimeMillis());
  }
}
