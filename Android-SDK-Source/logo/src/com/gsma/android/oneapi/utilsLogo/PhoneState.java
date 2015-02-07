package com.gsma.android.oneapi.utilsLogo;

import android.telephony.TelephonyManager;

/**
 * Helper functions for phone state management.
 */
public class PhoneState {

	String msisdn = null; // mobile telephone number of the user
	String simOperator = null; // field comprising Mobile Country Code and Mobile Network Code
	String networkOperator = null; // indicates the current network MCC/MNC
	String mcc = null; // Mobile Country Code
	String mnc = null; // Mobile Network Code
	boolean connected = false; // is the device connected to the Internet
	boolean usingMobileData = false; // is the device connecting using mobile/cellular data
	boolean roaming = false; // is the device roaming (international)
	String simSerialNumber = null; // the SIM serial number
	String subscriberId = null; // normally the IMSI
	String deviceId = null; // normally the IMEI
	String simOperatorName = null; // the Service Provider Name (SPN)
	String simCountryIso = null; // the ISO country code equivalent for the SIM provider's country code
	int networkType = 0;// the network name
	int simState = 0;// the SIM state

	/**
	 * Convert information which can be obtained from the Android OS into
	 * PhoneState information necessary for discovery.
	 * 
	 * @param msisdn
	 * @param simOperator
	 * @param networkOperator
	 * @param mcc
	 * @param mnc
	 * @param connected
	 * @param usingMobileData
	 * @param roaming
	 * @param simSerialNumber
	 * @param subscriberId
	 * @param deviceId
	 * @param simOperatorName
	 * @param simCountryIso
	 * @param networkType
	 * @param simState
	 */
	public PhoneState(String msisdn, String simOperator,
			String networkOperator, String mcc, String mnc, boolean connected,
			boolean usingMobileData, boolean roaming, String simSerialNumber,
			String subscriberId, String deviceId, String simOperatorName,
			String simCountryIso, int networkType, int simState) {
		this.msisdn = msisdn;
		this.simOperator = simOperator;
		this.networkOperator = networkOperator;
		this.mcc = mcc;
		this.mnc = mnc;
		this.connected = connected;
		this.usingMobileData = usingMobileData;
		this.roaming = roaming;
		this.simSerialNumber = simSerialNumber;
		this.subscriberId = subscriberId;
		this.deviceId = deviceId;
		this.simOperatorName = simOperatorName;
		this.simCountryIso = simCountryIso;
		this.networkType = networkType;
		this.simState = simState;
	}

	/**
	 * @return String
	 */
	public String getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn
	 *            the msisdn to set
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	/**
	 * @return String
	 */
	public String getSimOperator() {
		String simOP;
		if (simState == TelephonyManager.SIM_STATE_READY)
			simOP = simOperator;
		else
			simOP = null;
		return simOP;
	}

	/**
	 * @param simOperator
	 *            the simOperator to set
	 */
	public void setSimOperator(String simOperator) {
		this.simOperator = simOperator;
	}

	/**
	 * @return String
	 */
	public String getNetworkOperator() {
		return networkOperator;
	}

	/**
	 * @param networkOperator
	 *            the networkOperator to set
	 */
	public void setNetworkOperator(String networkOperator) {
		this.networkOperator = networkOperator;
	}

	/**
	 * @return String
	 */
	public String getMcc() {
		return mcc;
	}

	/**
	 * @param mcc
	 *            the mcc to set
	 */
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	/**
	 * @return String
	 */
	public String getMnc() {
		return mnc;
	}

	/**
	 * @param mnc
	 *            the mnc to set
	 */
	public void setMnc(String mnc) {
		this.mnc = mnc;
	}

	/**
	 * @return boolean
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @param connected
	 *            the connected to set
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * @return boolean
	 */
	public boolean isUsingMobileData() {
		return usingMobileData;
	}

	/**
	 * @param usingMobileData
	 *            the usingMobileData to set
	 */
	public void setUsingMobileData(boolean usingMobileData) {
		this.usingMobileData = usingMobileData;
	}

	/**
	 * @return boolean
	 */
	public boolean isRoaming() {
		return roaming;
	}

	/**
	 * @param roaming
	 *            the roaming to set
	 */
	public void setRoaming(boolean roaming) {
		this.roaming = roaming;
	}

	/**
	 * @return String
	 */
	public String getSimSerialNumber() {
		return simSerialNumber;
	}

	/**
	 * @param simSerialNumber
	 *            the simSerialNumber to set
	 */
	public void setSimSerialNumber(String simSerialNumber) {
		this.simSerialNumber = simSerialNumber;
	}

	/**
	 * @return String
	 */
	public String getSubscriberId() {
		return subscriberId;
	}

	/**
	 * @param subscriberId
	 *            the subscriberId to set
	 */
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
	 * @return String
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return String
	 */
	public String getSimOperatorName() {
		String operatorId;
		if (simState == TelephonyManager.SIM_STATE_READY)
			operatorId = simOperatorName;
		else
			operatorId = null;
		return operatorId;
	}

	/**
	 * @param simOperatorName
	 *            the simOperatorName to set
	 */
	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}

	/**
	 * @return String
	 */
	public String getSimCountryIso() {
		return simCountryIso;
	}

	/**
	 * @param simCountryIso
	 *            the simCountryIso to set
	 */
	public void setSimCountryIso(String simCountryIso) {
		this.simCountryIso = simCountryIso;
	}

	/**
	 * @return int
	 */
	public int getNetworkType() {
		return networkType;
	}

	/**
	 * @return String
	 */
	public String getNetworkTypeValue() {
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return "GPRS";
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return "EDGE";
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return "UMTS";
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return "HSDPA";
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return "HSUPA";
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return "HSPA";
		default:
			return "unknown";
		}
	}

	/**
	 * @param networkType
	 *            the networkType to set
	 */
	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	/**
	 * @return int
	 */
	public int getSimState() {
		return simState;
	}

	/**
	 * @param simState
	 *            the simState to set
	 */
	public void setSimState(int simState) {
		this.simState = simState;
	}

}
