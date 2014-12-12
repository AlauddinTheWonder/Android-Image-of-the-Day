package com.alauddin.imageoftheday;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

public class SettingsPreference extends PreferenceActivity
{
	static Context context;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		
	    super.onCreate(savedInstanceState);
	    
	    context = getApplicationContext();
	    
	    getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }
	
	
	public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            
            ListPreference storageListPref = (ListPreference) findPreference("defaultStorage");
            
            CharSequence[] storageNames;
            CharSequence[] storageValues;
            
            if(Common.externalStorageExists())
            {
            	storageNames = new CharSequence[2];
            	storageValues = new CharSequence[2];
            	
            	storageNames[0] = "Internal Storage";
            	storageValues[0] = Common.getInternalStorage();
            	
            	storageNames[1] = "External SdCard";
            	storageValues[1] = Common.getExternalStorage();
            }
            else
            {
            	storageNames = new CharSequence[1];
            	storageValues = new CharSequence[1];
            	
            	storageNames[0] = "Internal Storage";
            	storageValues[0] = Common.getInternalStorage();
            }
            storageListPref.setEntries(storageNames);
            storageListPref.setEntryValues(storageValues);
            storageListPref.setPersistent(true);
            
            if(storageListPref.getValue() == null)
            	storageListPref.setValueIndex(0); // default value index
        }
        
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
        {
        	Log.d("SP", key);
            
        	if(key.equals("enableAutoDownload"))
        	{
        		Boolean isEnabled = sharedPreferences.getBoolean(key, true);
        		
        		ComponentName receiver = new ComponentName(context, NetworkReceiver.class);
    			PackageManager pm = context.getPackageManager();
    			
    			if(isEnabled)
    			{
    				pm.setComponentEnabledSetting(receiver,
    				        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
    				        PackageManager.DONT_KILL_APP);
    				
    				Toast.makeText(context, "Enabled auto-downloader", Toast.LENGTH_SHORT).show();
    			}
    			else
    			{
    				pm.setComponentEnabledSetting(receiver,
    				        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
    				        PackageManager.DONT_KILL_APP);
    				
    				Toast.makeText(context, "Disabled auto-downloader", Toast.LENGTH_SHORT).show();
    			}
        	}
        	else if(key.equals("defaultStorage"))
        	{
        		Preference editPref = findPreference(key);
        		editPref.setSummary("Currently pointed to: " + sharedPreferences.getString(key, ""));
        	}
        }
    }
}
