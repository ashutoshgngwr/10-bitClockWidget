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
