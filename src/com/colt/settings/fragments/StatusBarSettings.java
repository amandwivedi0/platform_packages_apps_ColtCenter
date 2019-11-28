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
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.UserHandle;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import android.provider.Settings;

import com.colt.settings.preference.CustomSeekBarPreference;
import com.colt.settings.preference.SystemSettingSwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.colt.ColtUtils;

import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.R;

import java.util.ArrayList;
import java.util.List;

import com.colt.settings.preference.SystemSettingSeekBarPreference;
import com.colt.settings.preference.SystemSettingSwitchPreference;

public class StatusBarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private CustomSeekBarPreference mThreshold;
    private SystemSettingSwitchPreference mNetMonitor;

    private static final String KEY_NETWORK_TRAFFIC = "network_traffic_location";
    private static final String KEY_NETWORK_TRAFFIC_ARROW = "network_traffic_arrow";
    private static final String KEY_NETWORK_TRAFFIC_AUTOHIDE = "network_traffic_autohide_threshold";

    private ListPreference mNetworkTraffic;
    private SystemSettingSwitchPreference mNetworkTrafficArrow;
    private SystemSettingSeekBarPreference mNetworkTrafficAutohide;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.statusbar_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
	final ContentResolver resolver = getActivity().getContentResolver();

        boolean isNetMonitorEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT) == 1;
        mNetMonitor = (SystemSettingSwitchPreference) findPreference("network_traffic_state");
        mNetMonitor.setChecked(isNetMonitorEnabled);
        mNetMonitor.setOnPreferenceChangeListener(this);

        int value = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold = (CustomSeekBarPreference) findPreference("network_traffic_autohide_threshold");
        mThreshold.setValue(value);
        mThreshold.setOnPreferenceChangeListener(this);
        mThreshold.setEnabled(isNetMonitorEnabled);

	mNetworkTraffic = (ListPreference) findPreference(KEY_NETWORK_TRAFFIC);
        int networkTraffic = Settings.System.getInt(resolver,
        Settings.System.NETWORK_TRAFFIC_LOCATION, 0);
        CharSequence[] NonNotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_statusbar),
                getResources().getString(R.string.network_traffic_qs_header) };
        CharSequence[] NotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_qs_header) };
        CharSequence[] NonNotchValues = {"0", "1" , "2"};
        CharSequence[] NotchValues = {"0", "2"};
        mNetworkTraffic.setEntries(Utils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mNetworkTraffic.setEntryValues(Utils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mNetworkTraffic.setValue(String.valueOf(networkTraffic));
        mNetworkTraffic.setSummary(mNetworkTraffic.getEntry());
        mNetworkTraffic.setOnPreferenceChangeListener(this);

        mNetworkTrafficArrow = (SystemSettingSwitchPreference) findPreference(KEY_NETWORK_TRAFFIC_ARROW);
        mNetworkTrafficAutohide = (SystemSettingSeekBarPreference) findPreference(KEY_NETWORK_TRAFFIC_AUTOHIDE);
        updateNetworkTrafficPrefs(networkTraffic);

   }

 @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
	if (preference == mNetMonitor) {
            boolean value = (Boolean) objValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mNetMonitor.setChecked(value);
            mThreshold.setEnabled(value);
            return true;
        } else if (preference == mThreshold) {
            int val = (Integer) objValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
            return true;
	} else if (preference == mNetworkTraffic) {
            int networkTraffic = Integer.valueOf((String) newValue);
            int index = mNetworkTraffic.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_LOCATION, networkTraffic);
            mNetworkTraffic.setSummary(mNetworkTraffic.getEntries()[index]);
            updateNetworkTrafficPrefs(networkTraffic);
            return true;
        }
        return false;
    }

    private void updateNetworkTrafficPrefs(int networkTraffic) {
        if (mNetworkTraffic != null) {
            if (networkTraffic == 0) {
                mNetworkTrafficArrow.setEnabled(false);
                mNetworkTrafficAutohide.setEnabled(false);
            } else {
                mNetworkTrafficArrow.setEnabled(true);
                mNetworkTrafficAutohide.setEnabled(true);
            }
        }
    }

@Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.COLT;
    }
 }
