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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewExtrasActivity extends AppCompatActivity {

	protected static final String EXTRA_URL_STRING_ID = "url_string_id";

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_extras);

		final WebView webView = findViewById(R.id.wv_main);
		webView.getSettings().setDisplayZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setJavaScriptEnabled(true);

		int resID = getIntent().getIntExtra(EXTRA_URL_STRING_ID, 0);
		if (resID != 0) {
			webView.loadUrl(getString(resID));
		}

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			webView.setWebViewClient(new WebViewClient() {

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url.startsWith("http")) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
						return true;
					}

					return false;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					actionBar.setTitle(view.getTitle());

					try {
						PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
						view.loadUrl("javascript:update_app_info('" +
							packageInfo.versionName + "', '" +
							packageInfo.versionCode + "');");
					} catch (PackageManager.NameNotFoundException e) {
						Log.w(getClass().getName(), e);
					}
				}
			});
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}
}
