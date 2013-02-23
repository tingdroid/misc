package com.ting.app;

import android.os.*;
import android.preference.*;

public class SettingsActivity extends PreferenceActivity
 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

