package com.gsma.android.xoperatorapidemo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * simple utilities to help with retrieving application preferences
 */
public class PreferencesUtils {
	private static final String TAG = "PreferencesUtils";

	/*
	 * the loaded properties file
	 */
	private static Properties properties = null;

	/**
	 * load the preferences for the application - needs to have a reference to
	 * the activity initiating the load
	 * 
	 * @param activity
	 */
	public static void loadPreferences(Activity activity) {

		/*
		 * use the AssetManager to load in the properties
		 */
		AssetManager assetManager = activity.getResources().getAssets();

		// Read from the /assets directory
		try {
			/*
			 * load the application preferences from file named
			 * crossoperatorapidemo.properties
			 */
			InputStream inputStream = assetManager
					.open("mobileconnecttest.properties");
			properties = new Properties();
			properties.load(inputStream);
			Log.d(TAG, "The properties are now loaded");
			Log.d(TAG, "properties: " + properties);
		} catch (IOException e) {
			Log.e(TAG, "Failed to open mobileconnecttest property file");
			e.printStackTrace();
		}
	}

	/**
	 * retrieve the value of the named preference
	 * 
	 * @param name
	 * @return
	 */
	public static String getPreference(String name) {
		return properties != null ? properties.getProperty(name) : null;
	}
}
