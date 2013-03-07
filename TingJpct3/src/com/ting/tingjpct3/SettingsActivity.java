package com.ting.tingjpct3;

import android.os.*;
import android.preference.*;

public class SettingsActivity extends PreferenceActivity
 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Deprecated. Suggested using a modern fragment-based PreferenceActivity
        addPreferencesFromResource(R.xml.preferences);
    }
}

