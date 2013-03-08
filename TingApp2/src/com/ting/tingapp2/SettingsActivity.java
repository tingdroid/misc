package com.ting.tingapp2;

import java.util.ArrayList;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	public static final String KEY_PREF_NOTIFY_SUBJECT = "pref_notifySubject";
	public static final String KEY_PREF_NOTIFY_METHOD = "pref_notifyMethod";
	public static final String KEY_PREF_NOTIFY_BEFORE = "pref_notifyBefore";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Deprecated. Suggested using a modern fragment-based
		// PreferenceActivity
		addPreferencesFromResource(R.xml.preferences);

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		onSharedPreferenceChanged(sharedPreferences, KEY_PREF_NOTIFY_SUBJECT);
		onSharedPreferenceChanged(sharedPreferences, KEY_PREF_NOTIFY_METHOD);
		onSharedPreferenceChanged(sharedPreferences, KEY_PREF_NOTIFY_BEFORE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_PREF_NOTIFY_SUBJECT)) {
			setStringSummary(sharedPreferences, key, R.string.pref_notifySubject_summ);
			return;
		}
		if (key.equals(KEY_PREF_NOTIFY_METHOD)) {
			setStringSummary(sharedPreferences, key, R.string.pref_notifyMethod_summ);
			return;
		}
		if (key.equals(KEY_PREF_NOTIFY_BEFORE)) {
			setListSummary(sharedPreferences, key, 0);
			return;
		}
	}

	// Summary Helpers
	
	void setStringSummary(SharedPreferences sharedPreferences, String key,
			int summaryResId) {
		Preference pref = this.findPreference(key);
		
		String val = sharedPreferences.getString(key, "");

		Resources res = getResources();
		String summ = summaryResId != 0 ? res.getString(summaryResId, val) : val;
		pref.setSummary(summ);
	}

	void setListSummary(SharedPreferences sharedPreferences, String key,
			int summaryResId) {
		MultiSelectListPreference pref = (MultiSelectListPreference) this
				.findPreference(key);

		Set<String> vals = sharedPreferences.getStringSet(key, null);
		if (vals == null || vals.size() == 0) {
			pref.setSummary(null);
			return;
		}

		ArrayList<CharSequence> selectedEntries = new ArrayList<CharSequence>();
		int i = 0;
		CharSequence[] entries = pref.getEntries();
		for (CharSequence val : pref.getEntryValues()) {
			if (vals.contains(val.toString())) {
				selectedEntries.add(entries[i]);
			}
			i++;
		}
		String seq = TextUtils.join(", ", selectedEntries);

		Resources res = getResources();
		String summ = summaryResId != 0 ? res.getString(summaryResId, seq) : seq;

		pref.setSummary(summ);
	}
}
