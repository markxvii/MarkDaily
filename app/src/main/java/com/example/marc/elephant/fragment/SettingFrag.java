package com.example.marc.elephant.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.example.marc.elephant.R;
import com.example.marc.elephant.homepage.Content;

/**
 * Created by marc on 17-4-23.
 */

public class SettingFrag extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("clear").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Content.webView.clearCache(true);
                Toast.makeText(getActivity(), "成功", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

}
