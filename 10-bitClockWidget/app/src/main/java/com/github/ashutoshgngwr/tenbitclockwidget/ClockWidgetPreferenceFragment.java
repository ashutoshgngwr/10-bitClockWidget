/*
 * Copyright 2017 Ashutosh Gangwar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.ashutoshgngwr.tenbitclockwidget;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ClockWidgetPreferenceFragment extends PreferenceFragment
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Preference.OnPreferenceClickListener extrasPreferenceClickListener
			= new Preference.OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			Intent activityIntent = new Intent(getActivity(), WebViewExtrasActivity.class);

			switch (preference.getKey()) {
				case "about":
					activityIntent.putExtra(WebViewExtrasActivity.EXTRA_ASSET_FILE, "about.html");
					break;
				case "help":
					activityIntent.putExtra(WebViewExtrasActivity.EXTRA_ASSET_FILE, "help.html");
					break;
			}

			startActivity(activityIntent);
			return true;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.widget_preference);

		ListPreference dotSize = (ListPreference) findPreference("dot_size");
		dotSize.setSummary(dotSize.getEntry());

		ListPreference updateFrequency = (ListPreference) findPreference("update_frequency");
		updateFrequency.setSummary(updateFrequency.getEntry());

		Preference about = findPreference("about");
		about.setOnPreferenceClickListener(extrasPreferenceClickListener);

		Preference help = findPreference("help");
		help.setOnPreferenceClickListener(extrasPreferenceClickListener);

		Preference checkUpdates = findPreference("check_updates");
		checkUpdates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new CheckUpdateTask(preference).execute();
				return true;
			}
		});
	}

	// Listening for changes in SharedPreferences to get updated values of ListPreference.
	// Adding OnPreferenceChangeListener gives old value for ListPreference#getEntry()
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference preference = findPreference(key);

		if (preference instanceof ListPreference)
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

	private class CheckUpdateTask extends AsyncTask<Void, Void, Integer> {

		private static final String CHECK_UPDATE_URL =
				"https://raw.githubusercontent.com/ashutoshgngwr/10-bitClockWidget/master/version.json";
		private static final String UPDATE_DOWNLOAD_URL =
				"https://github.com/ashutoshgngwr/10-bitClockWidget/releases/latest";

		private Preference checkUpdates;

		private CheckUpdateTask(Preference checkUpdates) {
			this.checkUpdates = checkUpdates;
		}

		@Override
		protected void onPreExecute() {
			this.checkUpdates.setEnabled(false);
			this.checkUpdates.setSummary(R.string.summary_checking_updates);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				URLConnection urlConnection = new URL(CHECK_UPDATE_URL).openConnection();
				InputStream is = urlConnection.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				byte[] buffer = new byte[16];
				int length;
				while((length = is.read(buffer)) != -1)
					baos.write(buffer, 0, length);

				JSONObject jsonObject = new JSONObject(baos.toString());
				if(jsonObject.has("version"))
					return jsonObject.getInt("version");
			} catch (IOException | JSONException e) {
				Log.w(getClass().getSimpleName(), e);
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			this.checkUpdates.setEnabled(true);
			if (result == 0) {
				this.checkUpdates.setSummary(R.string.summary_connection_error);
				return;
			}

			if(result.compareTo(ClockWidgetSettings.getAppVersionCode(getContext())) == 0) {
				this.checkUpdates.setSummary(R.string.summary_no_updates_available);
				return;
			}

			this.checkUpdates.setSummary(R.string.summary_updates_available);
			this.checkUpdates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse(UPDATE_DOWNLOAD_URL)));
					return true;
				}
			});
			new AlertDialog.Builder(getContext())
					.setMessage(R.string.msg_update_available)
					.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(Intent.ACTION_VIEW,
									Uri.parse(UPDATE_DOWNLOAD_URL)));
						}
					})
					.show();
		}
	}
}
