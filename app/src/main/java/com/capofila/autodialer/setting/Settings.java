package com.capofila.autodialer.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.capofila.autodialer.R;

public class Settings extends AppCompatActivity {
    public static final String KEY_PREF_CALL_START_TIME = "button_timeout";
    public static final String KEY_PREF_CALL_COMMENT_DIALOG_SWITCH = "comment_dialog";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,new SettingFragment())
                .commit();

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

    }
}
