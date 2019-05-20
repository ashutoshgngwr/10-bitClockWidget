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
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.util.Calendar;

public class ClockWidgetUpdateService extends IntentService {

  // Bitmap scaling factor to compensate for larger widget size
  private static final float BITMAP_SCALE = 1.25F;

  private static final int BIT_ALPHA_ACTIVE = 0xFF;
  private static final int BIT_ALPHA_INACTIVE = 0x80;
  private static final int SEPARATOR_LINE_ALPHA = 0x70;

  private static final int RC_OPEN_CLOCK = 0x12;
  private static final int RC_UPDATE_CLOCK = 0x19;

  public ClockWidgetUpdateService() {
    super("ClockWidgetUpdateService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);

    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.clock_widget_layout);
    remoteViews.setImageViewBitmap(R.id.iv_clock, createClockBitmap());
    remoteViews.setOnClickPendingIntent(R.id.iv_clock, createOnClickPendingIntent());
    widgetManager.updateAppWidget(widgetManager.getAppWidgetIds(new ComponentName(getPackageName(),
        ClockWidgetProvider.class.getName())), remoteViews);

    setUpdateAlarm();
  }

  private Bitmap createClockBitmap() {
    // get current time in 12-hour format
    Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR), minute = c.get(Calendar.MINUTE),
        am_pm = c.get(Calendar.AM_PM);

    // scale width & height here and all other DIP values will be scaled in px() function
    int width = Math.round(getResources().getDimension(R.dimen.widget_width) * BITMAP_SCALE),
        height = Math.round(getResources().getDimension(R.dimen.widget_height) * BITMAP_SCALE),
        dot_radius = px(ClockWidgetSettings.getDotSize()), // get dot radius as set by user
        dot_size = dot_radius * 2,
        // dotSpacingX = spacing between 4 columns + left & right padding
        dotSpacingX = (width - dot_size * 5 - px(5)) / 6,
        dotSpacingY = (height - dot_size * 2) / 3; // same as X

    // create a bitmap of widget's size
    Bitmap clockBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(clockBitmap);

    Paint p = new Paint();
    p.setAntiAlias(true);
    p.setStyle(Paint.Style.FILL);

    // set clock's background color.
    p.setColor(ClockWidgetSettings.getClockBackgroundColor());
    canvas.drawRoundRect(new RectF(0, 0, width - 1, height - 1), px(5), px(5), p);

    // set clock's color based on time.
    p.setColor(am_pm == Calendar.AM ?
        ClockWidgetSettings.getClockAMColor() : ClockWidgetSettings.getClockPMColor());

    for (int i = 0; i < 4; i++) {
      if ((hour >> i & 1) == 1)
        p.setAlpha(BIT_ALPHA_ACTIVE);
      else
        p.setAlpha(BIT_ALPHA_INACTIVE);

      // cx = paddingX + (width + spacing of previous dots) + radius of current dot
      // cy = paddingY + radius of dot [for line 1],
      // cy = paddingY + radius of dot + height of line 1 + dotSpacingY [for line 2]
      canvas.drawCircle(dotSpacingX + (1 - i % 2) * (dot_size + dotSpacingX) + dot_radius,
          dotSpacingY + dot_radius + (i / 2 == 0 ? dotSpacingY + dot_size : 0),
          dot_radius, p);
    }

    // cx = padding + width of 2 dots + spacing of 2 dots + 5dp (separator padding + width)
    int marginX = dotSpacingX + (dot_size + dotSpacingX) * 2 + px(5);

    for (int i = 0; i < 6; i++) {
      if ((minute >> i & 1) == 1)
        p.setAlpha(BIT_ALPHA_ACTIVE);
      else
        p.setAlpha(BIT_ALPHA_INACTIVE);

      // cx = marginX + (width + spacing of previous dots) + radius of current dot.
      // cy = paddingY + radius of dot [for line 1]
      // cy = paddingY + radius of dot + height of line 1 + dotSpacingY [for line 2]
      canvas.drawCircle(marginX + (2 - i % 3) * (dot_size + dotSpacingX) + dot_radius,
          dotSpacingY + dot_radius + (i / 3 == 0 ? dotSpacingY + dot_size : 0),
          dot_radius, p);
    }

    if (ClockWidgetSettings.shouldDisplaySeparator()) {
      float x1 = dotSpacingX + dot_size * 2 + dotSpacingX * 1.5F + px(2);

      p.setAlpha(SEPARATOR_LINE_ALPHA);

      // from center-axis of line 1's dot to center-axis of line 2's dot
      canvas.drawLine(x1, dotSpacingY + dot_radius, x1,
          dotSpacingY + dot_radius + dotSpacingY + dot_size, p);
    }

    return clockBitmap;
  }

  private int px(int dp) {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        getResources().getDisplayMetrics()) * BITMAP_SCALE);
  }

  private void setUpdateAlarm() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.SECOND, 0);
    calendar.add(Calendar.MINUTE, 1);
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    if (alarmManager == null)
      return;
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
      alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), createClockUpdateIntent(this));
    else
      alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), createClockUpdateIntent(this));
  }

  // Creates PendingIntent for default activity of default clock application
  private PendingIntent createOnClickPendingIntent() {
    Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
    ActivityInfo clockInfo = getPackageManager().resolveActivity(openClockIntent, 0).activityInfo;
    return PendingIntent.getActivity(this, RC_OPEN_CLOCK,
        getPackageManager().getLaunchIntentForPackage(clockInfo.packageName),
        PendingIntent.FLAG_CANCEL_CURRENT);
  }

  protected static PendingIntent createClockUpdateIntent(Context context) {
    return PendingIntent.getService(context, RC_UPDATE_CLOCK,
        new Intent(context, ClockWidgetUpdateService.class), PendingIntent.FLAG_CANCEL_CURRENT);
  }
}
