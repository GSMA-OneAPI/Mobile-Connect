package com.gsma.android.oneapi.valuesLogo;

/**
 * Enum about Credentials options.
 */
public enum LogoCredentials {

	NONE("none"), 
	PLAIN("plain"), 
	SHA256("sha256");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private LogoCredentials(String v) {
		value = v;
	}

	/**
	 * Gets the String value.
	 * 
	 * @return String value
	 */
	public String value() {
		return value;
	}
	
	/**
	 * Gets the Credentials value from the String value.
	 * 
	 * @param value
	 * @return Credentials
	 * @throws IllegalArgumentException
	 */
	public static LogoCredentials fromValue(String v) {
		for (LogoCredentials c : LogoCredentials.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
