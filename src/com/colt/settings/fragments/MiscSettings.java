/*
 * Copyright (C) 2017 ColtOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.colt.settings.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.provider.Settings;
import android.os.UserHandle;
import android.view.View;
import android.widget.EditText;
import com.colt.settings.utils.Utils;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class MiscSettings extends SettingsPreferenceFragment implements
         Preference.OnPreferenceChangeListener {

	private static final String QS_HEADER_STYLE = "qs_header_style";

	private ListPreference mQsHeaderStyle;


        @Override
	public void onCreate(Bundle icicle) {
         super.onCreate(icicle);

        addPreferencesFromResource(R.xml.misc_settings);

	ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();
        Resources res = getResources();

	mFooterPreferenceMixin.createFooterPreference().setTitle(R.string.gesture_settings_info);

	mQsHeaderStyle = (ListPreference) findPreference(QS_HEADER_STYLE);
        int qsHeaderStyle = Settings.System.getInt(resolver,
                Settings.System.QS_HEADER_STYLE, 0);
        int newIndex = mQsHeaderStyle.findIndexOfValue(String.valueOf(qsHeaderStyle));
        mQsHeaderStyle.setValueIndex(newIndex >= 0 ? newIndex : 0);
        mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
        mQsHeaderStyle.setOnPreferenceChangeListener(this);
}

     @Override
     public boolean onPreferenceChange(Preference preference, Object objValue) {
         ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mQsHeaderStyle) {
             String value = (String) objValue;
             Settings.System.putInt(resolver, Settings.System.QS_HEADER_STYLE,
                    Integer.valueOf(value));
             int newIndex = mQsHeaderStyle.findIndexOfValue(value);
             mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntries()[newIndex]);
         }
         return true;
     }

     @Override
     public void onResume() {
         super.onResume();
     }

     @Override
     public void onPause() {
         super.onPause();
     }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.COLT;
    }
}