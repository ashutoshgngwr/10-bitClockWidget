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

        // always set result OK because all the widget settings are optional for user to configure.
        setResult(RESULT_OK, getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();

        // send update broadcast to ClockWidgetProvider
        sendBroadcast(new Intent(ClockWidgetProvider.ACTION_UPDATE_CLOCK));
    }
}
