package com.github.ashutoshgngwr.tenbitclockwidget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.DimenRes;
import android.util.Log;
import android.util.TypedValue;

import java.util.Calendar;

class ClockWidgetRenderer {

  private static final String TAG = ClockWidgetRenderer.class.getSimpleName();

  private static final int BIT_ALPHA_ACTIVE = 0xFF;
  private static final int BIT_ALPHA_INACTIVE = 0x80;
  private static final int SEPARATOR_LINE_ALPHA = 0x70;

  private static ClockWidgetRenderer mInstance;

  private int width = getDimen(R.dimen.widget_width);
  private int height = getDimen(R.dimen.widget_height);
  private Paint mPaint;
  private Bitmap clockBitmap;

  static Bitmap renderBitmap() {
    if (mInstance == null) {
      Log.d(TAG, "Creating a new renderer instance...");
      mInstance = new ClockWidgetRenderer();
    }

    return mInstance.render();
  }

  private ClockWidgetRenderer() {
    clockBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.FILL);
  }

  private int getDimen(@DimenRes int resId) {
    return Math.round(ClockWidgetApplication.getContext().getResources().getDimension(resId));
  }

  private void clearClockBitmap() {
    clockBitmap.eraseColor(Color.TRANSPARENT);
  }

  private Bitmap render() {
    // clear and reuse previously allocated bitmap
    clearClockBitmap();

    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR);
    int minute = calendar.get(Calendar.MINUTE);
    int period = calendar.get(Calendar.AM_PM);
    int dot_radius = px(ClockWidgetSettings.getDotSize());
    int dot_size = dot_radius * 2;
    int dotSpacingX = (width - dot_size * 5 - px(5)) / 6;
    int dotSpacingY = (height - dot_size * 2) / 3; // same as X

    Canvas canvas = new Canvas(clockBitmap);

    // set clock's background color.
    mPaint.setColor(ClockWidgetSettings.getClockBackgroundColor());
    canvas.drawRoundRect(new RectF(0, 0, width - 1, height - 1), px(5), px(5), mPaint);

    // set clock's color based on time.
    mPaint.setColor(period == Calendar.AM
        ? ClockWidgetSettings.getClockAMColor() : ClockWidgetSettings.getClockPMColor());

    for (int i = 0; i < 4; i++) {
      if ((hour >> i & 1) == 1)
        mPaint.setAlpha(BIT_ALPHA_ACTIVE);
      else
        mPaint.setAlpha(BIT_ALPHA_INACTIVE);

      // cx = paddingX + (width + spacing of previous dots) + radius of current dot
      // cy = paddingY + radius of dot [for line 1],
      // cy = paddingY + radius of dot + height of line 1 + dotSpacingY [for line 2]
      canvas.drawCircle(dotSpacingX + (1 - i % 2) * (dot_size + dotSpacingX) + dot_radius,
          dotSpacingY + dot_radius + (i / 2 == 0 ? dotSpacingY + dot_size : 0),
          dot_radius, mPaint);
    }

    // cx = padding + width of 2 dots + spacing of 2 dots + 5dp (separator padding + width)
    int marginX = dotSpacingX + (dot_size + dotSpacingX) * 2 + px(5);

    for (int i = 0; i < 6; i++) {
      if ((minute >> i & 1) == 1)
        mPaint.setAlpha(BIT_ALPHA_ACTIVE);
      else
        mPaint.setAlpha(BIT_ALPHA_INACTIVE);

      // cx = marginX + (width + spacing of previous dots) + radius of current dot.
      // cy = paddingY + radius of dot [for line 1]
      // cy = paddingY + radius of dot + height of line 1 + dotSpacingY [for line 2]
      canvas.drawCircle(marginX + (2 - i % 3) * (dot_size + dotSpacingX) + dot_radius,
          dotSpacingY + dot_radius + (i / 3 == 0 ? dotSpacingY + dot_size : 0),
          dot_radius, mPaint);
    }

    if (ClockWidgetSettings.shouldDisplaySeparator()) {
      float x1 = dotSpacingX + dot_size * 2 + dotSpacingX * 1.5F + px(2);

      mPaint.setAlpha(SEPARATOR_LINE_ALPHA);

      // from center-axis of line 1's dot to center-axis of line 2's dot
      canvas.drawLine(x1, dotSpacingY + dot_radius, x1,
          dotSpacingY + dot_radius + dotSpacingY + dot_size, mPaint);
    }

    return clockBitmap;
  }

  private int px(int dp) {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        ClockWidgetApplication.getContext().getResources().getDisplayMetrics()));
  }
}
