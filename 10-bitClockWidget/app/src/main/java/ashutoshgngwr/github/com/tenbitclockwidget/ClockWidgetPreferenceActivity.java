package ashutoshgngwr.github.com.tenbitclockwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ClockWidgetPreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ClockWidgetPreferenceFragment())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // update widget. User might have changed the settings.
        sendBroadcast(new Intent(ClockWidgetProvider.ACTION_UPDATE_CLOCK));
    }
}
