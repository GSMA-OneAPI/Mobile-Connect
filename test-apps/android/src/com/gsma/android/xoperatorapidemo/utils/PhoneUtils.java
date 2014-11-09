package com.gsma.android.xoperatorapidemo.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * extract useful phone state information and return in the form of a PhoneState
 * object
 */
public class PhoneUtils {
	private static final String TAG = "PhoneUtils";

	/**
	 * convert information which can be obtained from the Android OS into
	 * PhoneState information necessary for discovery
	 * 
	 * @param telephonyMgr
	 * @param connectivityMgr
	 * @return
	 */
	public static PhoneState getPhoneState(TelephonyManager telephonyMgr,
			ConnectivityManager connectivityMgr) {

		/*
		 * the users' phone number is obtained - this is not always available/
		 * valid
		 */
		String msisdn = telephonyMgr.getLine1Number();

		/*
		 * get the active network
		 */
		NetworkInfo activeNetwork = connectivityMgr.getActiveNetworkInfo();

//		if (activeNetwork != null) {
//			Log.d(TAG, "activeNetwork = " + activeNetwork.toString());
//		}

		/*
		 * subscriberId is normally the IMSI - not directly useful to discovery
		 */
		String subscriberId = telephonyMgr.getSubscriberId();

		/*
		 * check if the device is currently connected
		 */
		boolean connected = activeNetwork != null
				? activeNetwork.isConnected()
				: false;

		/*
		 * check if the device is currently roaming
		 */
		boolean roaming = activeNetwork != null
				? activeNetwork.isRoaming()
				: false;

		/*
		 * check if the device is using mobile/cellular data
		 */
		boolean usingMobileData = activeNetwork != null ? activeNetwork
				.getType() == ConnectivityManager.TYPE_MOBILE : false;
		
		/*
		 * get the SIM serial number
		 */
		String simSerialNumber = telephonyMgr.getSimSerialNumber();

		/*
		 * the simOperator indicates the registered network MCC/MNC the
		 * networkOperator indicates the current network MCC/MNC
		 */
		String simOperator = telephonyMgr.getSimOperator();
		String networkOperator = telephonyMgr.getNetworkOperator();

//		Log.d(TAG, "Connected to Internet? " + connected);
//		Log.d(TAG, "Connected to mobile data? " + usingMobileData);
//		Log.d(TAG, "Roaming? " + roaming);
//		Log.d(TAG, "Detected MSISDN? " + msisdn);
//		Log.d(TAG, "subscriberId? " + subscriberId);
//		Log.d(TAG, "deviceId? " + telephonyMgr.getDeviceId());
//		Log.d(TAG, "simOperator? " + simOperator);
//		Log.d(TAG, "simOperatorName? " + telephonyMgr.getSimOperatorName());
//		Log.d(TAG, "simCountryIso? " + telephonyMgr.getSimCountryIso());
//		Log.d(TAG, "simSerialNumber = " + simSerialNumber);
//		Log.d(TAG, "networkOperator = " + networkOperator);

		/*
		 * Mobile Country Code is obtained from the first three digits of
		 * simOperator, Mobile Network Code is any remaining digits
		 */
		String mcc = null;
		String mnc = null;
		if (simOperator != null && simOperator.length() > 3) {
			mcc = simOperator.substring(0, 3);
			mnc = simOperator.substring(3);
		}

		/*
		 * return a new PhoneState object from the parameters used in discovery
		 */
		return new PhoneState(msisdn, simOperator, mcc, mnc, connected,
				usingMobileData, roaming, simSerialNumber);

	}
}
