package com.gsma.android.oneapi.discovery;

import java.io.IOException;
import java.util.Calendar;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.gsma.android.oneapi.utilsDiscovery.PhoneState;
import com.gsma.android.oneapi.utilsDiscovery.PhoneUtils;
import com.gsma.android.oneapi.valuesDiscovery.DiscoveryCredentials;

/**
 * Main class with library methods. Implements DiscoveryCallbackReciever
 */
public class DiscoveryProvider implements DiscoveryCallbackReceiver {

	DiscoveryTask initialDiscoveryTask;
	DiscoveryListener listener;
	private static SharedPreferences mPrefs = null;
	private static final String PREFS_DISCOVERY = "discoveryPrefs";
	
	boolean verboseTracing=false;
	boolean allowAllSSL=false;

	/**
	 * Constructor
	 */
	public DiscoveryProvider() {
	}

	/**
	 * Gets the discovery information in passive mode. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param msisdn
	 * @param listener It is necessary to implement discoveryInfo(DiscoveryItem di) 
	 * and errorDiscoveryInfo(JSONObject jo) to manage the response
	 * @param context
	 * @param credentials (NONE, PLAIN, SHA256)
	 * @param redirectUri will point to a developer specified location which continues the discovery process
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getDiscoveryPassiveAutomaticMCCMNC(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String msisdn, DiscoveryListener listener,
			Context context, DiscoveryCredentials credentials, String redirectUri) {
		try {
			this.listener = listener;
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
	
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			boolean usingMobileData = phoneS.isUsingMobileData();
	
			String mcc = phoneS.getMcc();
			String mnc = phoneS.getMnc();
			
			initialDiscoveryTask = new DiscoveryTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, usingMobileData, msisdn, mcc,
						mnc, null /* selected mcc */, null /* selected mnc */, 
						this, credentials.value(), redirectUri, context, 
						null /* activity */,
						false /* follow redirect */, true /* json */, 
						allowAllSSL, verboseTracing);
			initialDiscoveryTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the discovery information in passive mode. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param mcc
	 * @param mnc
	 * @param msisdn
	 * @param listener It is necessary to implement discoveryInfo(DiscoveryItem di) 
	 * and errorDiscoveryInfo(JSONObject jo) to manage the response
	 * @param context
	 * @param credentials (NONE, PLAIN, SHA256)
	 * @param redirectUri will point to a developer specified location which continues the discovery process
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getDiscoveryPassive(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String mcc, String mnc, String msisdn, DiscoveryListener listener,
			Context context, DiscoveryCredentials credentials, String redirectUri) {
		try {
			this.listener = listener;
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
	
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			boolean usingMobileData = phoneS.isUsingMobileData();
			
			initialDiscoveryTask = new DiscoveryTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, usingMobileData, msisdn, mcc,
						mnc, null /* Selected MCC */, null /* Selected MNC */, 
						this, credentials.value(), redirectUri, context, 
						null /* activity */,
						false /* follow redirect */, true /* json */, 
						allowAllSSL, verboseTracing);
			initialDiscoveryTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the discovery information in passive mode. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param mcc
	 * @param mnc
	 * @param msisdn
	 * @param listener It is necessary to implement discoveryInfo(DiscoveryItem di) 
	 * and errorDiscoveryInfo(JSONObject jo) to manage the response
	 * @param context
	 * @param credentials (NONE, PLAIN, SHA256)
	 * @param redirectUri will point to a developer specified location which continues the discovery process
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getDiscoveryPassiveSelectedMCCMNC(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String mcc, String mnc, String msisdn, DiscoveryListener listener,
			Context context, DiscoveryCredentials credentials, String redirectUri) {
		try {
			this.listener = listener;
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
	
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			boolean usingMobileData = phoneS.isUsingMobileData();
			
			initialDiscoveryTask = new DiscoveryTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, usingMobileData, msisdn, 
						null /* Identified MCC */, null /* Identified MNC */,
						mcc, mnc,  
						this, credentials.value(), redirectUri, context, 
						null /* activity */,
						false /* follow redirect */, true /* json */, 
						allowAllSSL, verboseTracing);
			initialDiscoveryTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Gets the discovery information in active mode. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param msisdn
	 * @param listener. It is necessary to implement discoveryInfo(DiscoveryItem di) 
	 * and errorDiscoveryInfo(JSONObject jo) to manage the response
	 * @param context
	 * @param credentials (NONE, PLAIN, SHA256)
	 * @param redirectUri will point to a developer specified location which continues the discovery process
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getDiscoveryActiveAutomaticMCCMNC(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String msisdn, DiscoveryListener listener,
			Context context, DiscoveryCredentials credentials, String redirectUri) {
		try {
			this.listener = listener;
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
	
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			boolean usingMobileData = phoneS.isUsingMobileData();
	
			String mcc = phoneS.getMcc();
			String mnc = phoneS.getMnc();
	
			initialDiscoveryTask = new DiscoveryTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, usingMobileData, msisdn, mcc,
						mnc, null /* Selected MCC */, null /* Selected MNC */,
						this, credentials.value(),redirectUri, context, 
						null /* activity */,
						true /* follow redirect */, true /* json */, 
						allowAllSSL, verboseTracing);
			initialDiscoveryTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the discovery information in active mode. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param msisdn
	 * @param listener. It is necessary to implement discoveryInfo(DiscoveryItem di) 
	 * and errorDiscoveryInfo(JSONObject jo) to manage the response
	 * @param context
	 * @param credentials (NONE, PLAIN, SHA256)
	 * @param redirectUri will point to a developer specified location which continues the discovery process
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getDiscoveryActiveAutomaticMCCMNCUsingWebview(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String msisdn, DiscoveryListener listener,
			Context context, Activity activity, DiscoveryCredentials credentials, String redirectUri) {
		try {
			this.listener = listener;
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
	
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			boolean usingMobileData = phoneS.isUsingMobileData();
	
			String mcc = phoneS.getMcc();
			String mnc = phoneS.getMnc();
	
			initialDiscoveryTask = new DiscoveryTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, usingMobileData, msisdn, mcc,
						mnc, null /* Selected MCC */, null /* Selected MNC */,
						this, credentials.value(),redirectUri, context, 
						activity /* activity */,
						true /* follow redirect */, true /* json */, 
						allowAllSSL, verboseTracing);
			initialDiscoveryTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the discovery information in active mode. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param mcc
	 * @param mnc
	 * @param msisdn
	 * @param listener. It is necessary to implement discoveryInfo(DiscoveryItem di) 
	 * and errorDiscoveryInfo(JSONObject jo) to manage the response
	 * @param context
	 * @param credentials (NONE, PLAIN, SHA256)
	 * @param redirectUri will point to a developer specified location which continues the discovery process
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getDiscoveryActive(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String mcc, String mnc, String msisdn, DiscoveryListener listener,
			Context context, DiscoveryCredentials credentials, String redirectUri) {
		try {
			this.listener = listener;
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
	
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			boolean usingMobileData = phoneS.isUsingMobileData();
	
			initialDiscoveryTask = new DiscoveryTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, usingMobileData, msisdn, mcc,
						mnc, null /* Selected MCC */, null /* Selected MNC */,
						this, credentials.value(),redirectUri, context, 
						null /* activity */,
						true /* follow redirect */, true /* json */, 
						allowAllSSL, verboseTracing);
			initialDiscoveryTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the discovery information in active mode. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param mcc
	 * @param mnc
	 * @param msisdn
	 * @param listener. It is necessary to implement discoveryInfo(DiscoveryItem di) 
	 * and errorDiscoveryInfo(JSONObject jo) to manage the response
	 * @param context
	 * @param credentials (NONE, PLAIN, SHA256)
	 * @param redirectUri will point to a developer specified location which continues the discovery process
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getDiscoveryActiveUsingWebview(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String mcc, String mnc, String msisdn, DiscoveryListener listener,
			Context context, Activity activity,
			DiscoveryCredentials credentials, String redirectUri) {
		try {
			this.listener = listener;
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
	
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			boolean usingMobileData = phoneS.isUsingMobileData();
	
			initialDiscoveryTask = new DiscoveryTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, usingMobileData, msisdn, mcc,
						mnc, null /* Selected MCC */, null /* Selected MNC */,
						this, credentials.value(),redirectUri, context, 
						activity,
						true /* follow redirect */, true /* json */, 
						allowAllSSL, verboseTracing);
			initialDiscoveryTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Internal use. Method from the DiscoveryCallbackReciever interface to wait the response. 
	 * 
	 * @param result
	 * @throws JSONException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	@Override
	public void receiveDiscoveryData(JSONObject result) {
		if (initialDiscoveryTask != null) {
			initialDiscoveryTask.cancel(true);
			initialDiscoveryTask = null;
		}
		try {
			DiscoveryItem di = new DiscoveryItem(result);
			Editor editor = mPrefs.edit();
			ObjectMapper mapper = new ObjectMapper();
			String serialised = di != null ? mapper.writeValueAsString(di) : null;
			editor.putString("DiscoveryItem", serialised);
			editor.commit();
			listener.discoveryInfo(di);
		} catch (JSONException e) {
			try{
				listener.errorDiscoveryInfo(result);
			} catch (NullPointerException exc) {
				exc.printStackTrace();
			}
		} catch (JsonGenerationException e) {
			try{
				listener.errorDiscoveryInfo(null);
			} catch (NullPointerException exc) {
				exc.printStackTrace();
			}
		} catch (JsonMappingException e) {
			try{
				listener.errorDiscoveryInfo(null);
			} catch (NullPointerException exc) {
				exc.printStackTrace();
			}
		} catch (IOException e) {
			try{
				listener.errorDiscoveryInfo(null);
			} catch (NullPointerException exc) {
				exc.printStackTrace();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper function developed to parse the redirect sent by the discovery active
	 * server to the application and extract mcc_mnc value.
	 * 
	 * @param returnUri
	 *            which is the return point after the user has selected.
	 * @return String
	 */
	public String extractRedirectParameter(Uri returnUri) {
		return returnUri.getQueryParameter("mcc_mnc");  
	}

	/**
	 * Method to clear the saved discovery information.
	 * 
	 * @param context
	 * @throws NullPointerException
	 */
	public void clearCacheDiscoveryItem(Context context) {
		try{
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
			Editor editor = mPrefs.edit();
			editor.putString("DiscoveryItem", null);
			editor.commit();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the saved DiscoveryItem object with the complete discovery information.
	 * 
	 * @param context
	 * @return DiscoveryItem
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public DiscoveryItem getCacheDiscoveryItem(Context context) {
		try{
			mPrefs = context.getSharedPreferences(PREFS_DISCOVERY, 0);
			String discoveryItemSerialised = mPrefs.getString("DiscoveryItem", null);
			DiscoveryItem cachedData;
			if (discoveryItemSerialised != null) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					cachedData = mapper.readValue(discoveryItemSerialised, DiscoveryItem.class);
					Calendar calToday = Calendar.getInstance();
					Calendar calSaved = Calendar.getInstance();
					calSaved.setTimeInMillis(Long.parseLong(cachedData.getTtl()));
					if (calToday.getTimeInMillis() > calSaved.getTimeInMillis()) {
						clearCacheDiscoveryItem(context);
						cachedData = null;
					}
				} catch (JsonParseException e) {
					cachedData = null;
				} catch (JsonMappingException e) {
					cachedData = null;
				} catch (IOException e) {
					cachedData = null;
				}
			} else
				cachedData = null;
			return cachedData;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Useful for applications development phase - traces the discovery activity 
	 */
	public void setVerboseTracing(boolean verboseTracing) {
		this.verboseTracing=verboseTracing;
	}

	/**
	 * Useful for SDK/discovery service development 
	 */
	public void enableAllSSLCertificates() {
		allowAllSSL=true;
	}

}
