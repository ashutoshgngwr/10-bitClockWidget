package ashutoshgngwr.github.com.tenbitclockwidget;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

public class ClockWidgetApplication extends Application {

    private static ClockWidgetApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // Load default values from widget_preference.xml file to shared preferences.
        PreferenceManager.setDefaultValues(this, R.xml.widget_preference, false);
    }

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }
}
