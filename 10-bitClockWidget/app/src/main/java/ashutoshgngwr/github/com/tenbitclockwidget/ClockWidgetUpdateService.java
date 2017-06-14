package ashutoshgngwr.github.com.tenbitclockwidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.util.Calendar;

public class ClockWidgetUpdateService extends IntentService {

    public ClockWidgetUpdateService() {
        super("ClockWidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        Bitmap clockBitmap = createClockBitmap();

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.clock_widget_layout);
        remoteViews.setImageViewBitmap(R.id.iv_clock, clockBitmap);

        widgetManager.updateAppWidget(intent.getIntArrayExtra("ids"), remoteViews);
    }

    private Bitmap createClockBitmap() {
        // get current time in 12-hour format
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis()); // set current time
        int hour = c.get(Calendar.HOUR), minute = c.get(Calendar.MINUTE),
                am_pm = c.get(Calendar.AM_PM);

        int width = Math.round(getResources().getDimension(R.dimen.widget_width)),
            height = Math.round(getResources().getDimension(R.dimen.widget_height)),
            dot_radius = px(ClockWidgetSettings.getDotSize()), // get dot radius as set by user
            dot_size = dot_radius * 2,
            // dotSpacingX = (totalwidth - size of 5 dots + 5px (separator width + padding)) / 5
            dotSpacingX = (width - dot_size * 5 - px(5)) / 6,
            paddingX = dotSpacingX, // use spacing between dots as left and right padding.
            dotSpacingY = (height - dot_size * 2) / 3,
            paddingY = dotSpacingY; // calculated in same manner as paddingX

        // create a bitmap of widget's size
        Bitmap clockBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(clockBitmap);
        canvas.drawColor(ClockWidgetSettings.getClockBackgroundColor()); // set clock's background color.

        Paint p = new Paint();
        p.setAntiAlias(true);

        // set clock's color based on time.
        p.setColor(am_pm == Calendar.AM ?
                ClockWidgetSettings.getClockAMColor() : ClockWidgetSettings.getClockPMColor());

        for(int i = 0; i < 4; i++) {
            if((hour >> i & 1) == 1)
                p.setStyle(Paint.Style.FILL);
            else
                p.setStyle(Paint.Style.STROKE);

            // cx = paddingX + (width + spacing of previous dots) + radius of current dot
            // cy = paddingY + radius of dot [for line 1],
            // cy = paddingY + radius of dot + height of line 1 + dotSpacingY [for line 2]
            canvas.drawCircle(paddingX + (1 - i % 2) * (dot_size + dotSpacingX) + dot_radius,
                    paddingY + dot_radius + (i / 2 == 0 ? dotSpacingY + dot_size : 0),
                    dot_radius, p);
        }

        // cx = padding + width of 2 dots + spacing of 2 dots + 5dp (separator padding + width)
        int marginX = paddingX + (dot_size + dotSpacingX) * 2 + px(5);

        for(int i = 0; i < 6; i++) {
            if((minute >> i & 1) == 1)
                p.setStyle(Paint.Style.FILL);
            else
                p.setStyle(Paint.Style.STROKE);

            // cx = marginX + (width + spacing of previous dots) + radius of current dot.
            // cy = paddingY + radius of dot [for line 1]
            // cy = paddingY + radius of dot + height of line 1 + dotSpacingY [for line 2]
            canvas.drawCircle(marginX + (2 - i % 3) * (dot_size + dotSpacingX) + dot_radius,
                    paddingY + dot_radius + (i / 3 == 0 ? dotSpacingY + dot_size : 0),
                    dot_radius, p);
        }

        if(ClockWidgetSettings.shouldDisplaySeparator()) {
            float x1 = paddingX + dot_size * 2 + dotSpacingX * 1.5F + px(2);

            p.setAlpha(0x70);

            // from center-axis of line 1's dot to center-axis of line 2's dot
            canvas.drawLine(x1, paddingY + dot_radius, x1,
                    paddingY + dot_radius + dotSpacingY + dot_size, p);
        }

        return clockBitmap;
    }

    private int px(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()));
    }
}
