package com.gsma.android.oneapi.utilsDiscovery;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Functions that get the phone state.
 */
public class PhoneUtils {

	/**
	 * Convert information which can be obtained from the Android OS into
	 * PhoneState information necessary for discovery
	 * 
	 * @param telephonyMgr
	 * @param connectivityMgr
	 * @return PhoneState 
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

		/*
		 * check if the device is currently connected
		 */
		boolean connected = activeNetwork != null ? activeNetwork.isConnected()
				: false;

		/*
		 * check if the device is currently roaming
		 */
		boolean roaming = activeNetwork != null ? activeNetwork.isRoaming()
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
		 * subscriberId is normally the IMSI - not directly useful to discovery
		 */
		String subscriberId = telephonyMgr.getSubscriberId();

		/*
		 * the simOperator indicates the registered network MCC/MNC the
		 * networkOperator indicates the current network MCC/MNC
		 */
		String simOperator = telephonyMgr.getSimOperator();
		String networkOperator = telephonyMgr.getNetworkOperator();

		/*
		 * deviceId is the unique device ID, for example, the IMEI for GSM and
		 * the MEID or ESN for CDMA phones. Null if the device ID is not
		 * available.
		 */
		String deviceId = telephonyMgr.getDeviceId();

		/*
		 * simOperatorName is the Service Provider Name (SPN).
		 */
		String simOperatorName = telephonyMgr.getSimOperatorName();

		/*
		 * simCountryIso is the ISO country code equivalent for the SIM
		 * provider's country code
		 */
		String simCountryIso = telephonyMgr.getSimCountryIso();

		/*
		 * networkType is the network name
		 */
		int networkType = telephonyMgr.getNetworkType();

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
		 * simState is a constant indicating the state of the device SIM card
		 * SIM_STATE_UNKNOWN SIM_STATE_ABSENT SIM_STATE_PIN_REQUIRED
		 * SIM_STATE_PUK_REQUIRED SIM_STATE_NETWORK_LOCKED SIM_STATE_READY
		 */
		int simState = telephonyMgr.getSimState();

		/*
		 * return a new PhoneState object from the parameters used in discovery
		 */
		return new PhoneState(msisdn, simOperator, networkOperator, mcc, mnc,
				connected, usingMobileData, roaming, simSerialNumber,
				subscriberId, deviceId, simOperatorName, simCountryIso,
				networkType, simState);

	}

}
