package com.gsma.android.mobileconnect.utils;

/**
 * simple utilities which help with ParameterList data
 */
public class KeyValuePair {

	String key=null;
	String value=null;

	public KeyValuePair(String key, String value) {
		this.key=key;
		this.value=value;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	
}
