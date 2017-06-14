package ashutoshgngwr.github.com.tenbitclockwidget;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ClockWidgetSettings {

    protected static int getDotSize() {
        return Integer.parseInt(getPreferences().getString("dot_size", "0"));
    }

    protected static int getUpdateFrequency() {
        return Integer.parseInt(getPreferences().getString("update_frequency", "0"));
    }

    protected static boolean shouldDisplaySeparator() {
        return getPreferences().getBoolean("display_separator", false);
    }

    protected static int getClockAMColor() {
        return getPreferences().getInt("am_color", 0);
    }

    protected static int getClockPMColor() {
        return getPreferences().getInt("pm_color", 0);
    }

    protected static int getClockBackgroundColor() {
        return getPreferences().getInt("background_color", 0);
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(ClockWidgetApplication.getContext());
    }
}
