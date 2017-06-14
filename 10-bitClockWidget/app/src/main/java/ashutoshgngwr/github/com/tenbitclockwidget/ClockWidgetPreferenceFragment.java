package ashutoshgngwr.github.com.tenbitclockwidget;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class ClockWidgetPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.widget_preference);

        ListPreference dotSize = (ListPreference) findPreference("dot_size");
        dotSize.setSummary(dotSize.getEntry());

        ListPreference updateFrequency = (ListPreference) findPreference("update_frequency");
        updateFrequency.setSummary(updateFrequency.getEntry());

    }

    // Listening for changes in SharedPreferences to get updated values of ListPreference.
    // Adding OnPreferenceChangeListener gives old value for ListPreference#getEntry()
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if(preference instanceof ListPreference)
            preference.setSummary(((ListPreference) preference).getEntry());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
