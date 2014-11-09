package com.gsma.android.xoperatorapidemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.gsma.android.mobileconnectsdktest.R;
import com.gsma.android.oneapi.logo.LogoCache;
import com.gsma.android.xoperatorapidemo.discovery.DeveloperOperatorSetting;
import com.gsma.android.xoperatorapidemo.discovery.DiscoveryDeveloperOperatorSettings;
import com.gsma.android.xoperatorapidemo.discovery.DiscoveryServingOperatorSettings;
import com.gsma.android.xoperatorapidemo.discovery.DiscoveryStartupSettings;
import com.gsma.android.xoperatorapidemo.discovery.ServingOperatorSetting;
import com.gsma.android.xoperatorapidemo.utils.PhoneState;
import com.gsma.android.xoperatorapidemo.utils.PhoneUtils;

public class SettingsActivity extends Activity {

	private static final String TAG = "SettingsActivity";

	public static SettingsActivity settingsActivityInstance = null;
	
	/*
	 * Currently selected developer operator/ serving operator
	 */
	private static int developerOperatorIndex=0;
	private static int servingOperatorIndex=0;
	private static DeveloperOperatorSetting developerOperator=DiscoveryDeveloperOperatorSettings.getOperator(developerOperatorIndex);
	private static ServingOperatorSetting servingOperator=DiscoveryServingOperatorSettings.getOperator(servingOperatorIndex);
	
	public static DeveloperOperatorSetting getDeveloperOperator() {
		return developerOperator;
	}

	public static ServingOperatorSetting getServingOperator() {
		return servingOperator;
	}

	private static int startupOptionIndex=0;
	private static DiscoveryStartupSettings startupOption=null;

	private static boolean mccMncSelected=true;
	private static boolean cookiesSelected=true;
	
	public static boolean isCookiesSelected() {
		return cookiesSelected;
	}

	CheckBox mccMncPrompt = null;
	CheckBox promptCookies = null;
	Spinner developerOperatorSpinner = null;
	Spinner servingOperatorSpinner = null;
	Spinner startupOptionSpinner = null;
	
	private static String mcc=null;
	private static String mnc=null;
	
	private static SharedPreferences  mPrefs=null;
	
	public static DiscoveryStartupSettings getDiscoveryStartupSettings() {
		return startupOption;
	}
	
	public static void loadSettings(Activity activity) {
		mPrefs = activity.getPreferences(MODE_PRIVATE);
		
		mccMncSelected=mPrefs.getBoolean("MCCMNC", true);
		cookiesSelected=mPrefs.getBoolean("Cookies", true);
		developerOperatorIndex=mPrefs.getInt("DeveloperOperator", 0);
		servingOperatorIndex=mPrefs.getInt("ServingOperator", 0);
		developerOperator=DiscoveryDeveloperOperatorSettings.getOperator(developerOperatorIndex);
    	servingOperator=DiscoveryServingOperatorSettings.getOperator(servingOperatorIndex);

		int startupOptionValue=mPrefs.getInt("StartupOptionValue", DiscoveryStartupSettings.DEFAULT);
		
		startupOption=DiscoveryStartupSettings.getByValue(startupOptionValue);
		startupOptionIndex=DiscoveryStartupSettings.getIndexByValue(startupOptionValue);
		
		Log.d(TAG, "Loaded setting MCCMNC="+mccMncSelected);
		Log.d(TAG, "Loaded setting Cookies="+cookiesSelected);
		Log.d(TAG, "Loaded setting DeveloperOperator="+developerOperatorIndex);
		Log.d(TAG, "Loaded setting ServingOperator="+servingOperatorIndex);
		Log.d(TAG, "Loaded setting StartupOption="+startupOptionIndex);
		
		updateMccMnc(activity);
	}


	/*
	 * method called when the application first starts.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mccMncPrompt = (CheckBox) findViewById(R.id.promptMCCMNC);
		promptCookies = (CheckBox) findViewById(R.id.promptCookies);
		developerOperatorSpinner = (Spinner) findViewById(R.id.developerOperatorSpinner);
		servingOperatorSpinner = (Spinner) findViewById(R.id.servingOperator);
		startupOptionSpinner = (Spinner) findViewById(R.id.startupOptionSpinner);

		/*
		 * save a copy of the current instance - will be needed later
		 */
		settingsActivityInstance = this;

		ArrayAdapter<String> developerOperatorAdapter = new ArrayAdapter<String>(this,   
				android.R.layout.simple_spinner_item, DiscoveryDeveloperOperatorSettings.getOperatorNames());
		developerOperatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		developerOperatorSpinner.setAdapter(developerOperatorAdapter);
		
		developerOperatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
				updateDeveloperOperator(pos);
		    } 
		    public void onNothingSelected(AdapterView<?> adapterView) {
		        return;
		    } 
		}); 
		
		ArrayAdapter<String> servingOperatorAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, DiscoveryServingOperatorSettings.getOperatorNames());
		servingOperatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		servingOperatorSpinner.setAdapter(servingOperatorAdapter);

		servingOperatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
				updateServingOperator(pos);
		    } 
		    public void onNothingSelected(AdapterView<?> adapterView) {
		        return;
		    } 
		}); 

		ArrayAdapter<String> startupOptionAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, DiscoveryStartupSettings.getLabels());
		startupOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		startupOptionSpinner.setAdapter(startupOptionAdapter);

		startupOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
				updateStartupOption(pos);
		    } 
		    public void onNothingSelected(AdapterView<?> adapterView) {
		        return;
		    } 
		}); 

		
	}

	@Override
	public void onStart() {
		super.onStart();
		mccMncPrompt.setChecked(mccMncSelected);
		promptCookies.setChecked(cookiesSelected);
		developerOperatorSpinner.setSelection(developerOperatorIndex);
		servingOperatorSpinner.setSelection(servingOperatorIndex);
		startupOptionSpinner.setSelection(startupOptionIndex);
	}
	
	public void clickedMCCMNC(View view) {
		mccMncSelected=mccMncPrompt.isChecked();
		SharedPreferences.Editor editor = mPrefs.edit();
	    editor.putBoolean("MCCMNC", mccMncSelected);
	    editor.commit();
	}

	public void clickedCookies(View view) {
		cookiesSelected=promptCookies.isChecked();
		SharedPreferences.Editor editor = mPrefs.edit();
	    editor.putBoolean("Cookies", cookiesSelected);
	    editor.commit();
	}

	public void updateDeveloperOperator(int index) {
    	developerOperatorIndex=index;
    	developerOperator=DiscoveryDeveloperOperatorSettings.getOperator(index);
		Log.d(TAG, "Selected developer operator "+index+" "+developerOperator.getName());
		SharedPreferences.Editor editor = mPrefs.edit();
	    editor.putInt("DeveloperOperator", index);
	    editor.commit();
	}
	
	public void updateServingOperator(int index) {
    	servingOperatorIndex=index;
    	servingOperator=DiscoveryServingOperatorSettings.getOperator(index);
		Log.d(TAG, "Selected serving operator "+index+" "+servingOperator.getName()+
				" auto="+servingOperator.isAutomatic()+" mcc="+servingOperator.getMcc()+" mnc="+servingOperator.getMnc());
		SharedPreferences.Editor editor = mPrefs.edit();
	    editor.putInt("ServingOperator", index);
	    editor.commit();
	    updateMccMnc(this);
	}
	
	public static void updateMccMnc(Activity activity) {
		mcc = null; // Mobile country code
		mnc = null; // Mobile network code

		if (servingOperator.isAutomatic()) {
			PhoneState state = PhoneUtils
					.getPhoneState(
							(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE),
							(ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE));
	
			mcc = state.getMcc(); // Mobile country code
			mnc = state.getMnc(); // Mobile network code
		} else {
			mcc=servingOperator.getMcc();
			mnc=servingOperator.getMnc();
		}
		
	}

	public void updateStartupOption(int index) {
    	startupOptionIndex=index;
    	startupOption=DiscoveryStartupSettings.get(index);
		Log.d(TAG, "Selected startup option "+index+" "+startupOption.getLabel());
		SharedPreferences.Editor editor = mPrefs.edit();
	    editor.putInt("StartupOptionValue", startupOption.getValue());
	    editor.commit();
	}

	public static String getServingOperatorName() {
		return servingOperator.getName();
	}
	
	public static String getMcc() {
		return mcc;
	}
	
	public static String getMnc() {
		return mnc;
	}
	
	public static String getMccMnc() {
		String mcc_mnc=null;
		if (mcc!=null && mnc!=null) mcc_mnc=mcc+"_"+mnc;
		return mcc_mnc;
	}
	
	public void clearCache(View view) {
		MainActivity.clearDiscoveryData();
		LogoCache.clearCache();
		MainActivity.processLogoUpdates();
	}
	
}
