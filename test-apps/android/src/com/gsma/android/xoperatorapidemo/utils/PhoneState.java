package com.gsma.android.xoperatorapidemo.utils;

/**
 * object holding the phone state that is useful to discovery
 */
public class PhoneState {
	String msisdn = null; // mobile telephone number of the user
	String simOperator = null; // field comprising Mobile Country Code and
								// Mobile Network Code
	String mcc = null; // Mobile Country Code
	String mnc = null; // Mobile Network Code
	boolean connected = false; // is the device connected to the Internet
	boolean usingMobileData = false; // is the device connecting using
										// mobile/cellular data
	boolean roaming = false; // is the device roaming (international)
	String simSerialNumber = null; // the SIM serial number 

	public PhoneState(String msisdn, String simOperator, String mcc,
			String mnc, boolean connected, boolean usingMobileData,
			boolean roaming, String simSerialNumber) {
		this.msisdn = msisdn;
		this.simOperator = simOperator;
		this.mcc = mcc;
		this.mnc = mnc;
		this.connected = connected;
		this.usingMobileData = usingMobileData;
		this.roaming = roaming;
		this.simSerialNumber = simSerialNumber;
	}

	/**
	 * @return the msisdn
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
	 * @return the simOperator
	 */
	public String getSimOperator() {
		return simOperator;
	}

	/**
	 * @param simOperator
	 *            the simOperator to set
	 */
	public void setSimOperator(String simOperator) {
		this.simOperator = simOperator;
	}

	/**
	 * @return the mcc
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
	 * @return the mnc
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
	 * @return the connected
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
	 * @return the usingMobileData
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
	 * @return the roaming
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
	 * @return the simSerialNumber
	 */
	public String getSimSerialNumber() {
		return simSerialNumber;
	}

	/**
	 * @param simSerialNumber the simSerialNumber to set
	 */
	public void setSimSerialNumber(String simSerialNumber) {
		this.simSerialNumber = simSerialNumber;
	}

}
