package com.asliced.slidingpuzzle;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PreferenceActivity extends android.preference.PreferenceActivity 
						implements OnSharedPreferenceChangeListener {
	public static String INCLUDED_PICS = "includedpic";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
    protected void onResume() {
		super.onResume();
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);    
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);    
    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		if(key.equals(INCLUDED_PICS)){
			String img = pref.getString(key, "");
			PlayerView.changePic(img);
		}
		
		finish();
	}
}
