package com.ting.tingapp2;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Deprecated. Suggested using a modern fragment-based PreferenceActivity
        addPreferencesFromResource(R.xml.preferences);
    }
}
