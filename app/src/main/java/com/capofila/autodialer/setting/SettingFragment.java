package com.capofila.autodialer.setting;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.capofila.autodialer.R;

public class SettingFragment extends PreferenceFragmentCompat  {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);
    }
}
