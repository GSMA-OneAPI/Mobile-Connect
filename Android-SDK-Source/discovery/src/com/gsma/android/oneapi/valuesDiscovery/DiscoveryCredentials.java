package com.gsma.android.oneapi.valuesDiscovery;

/**
 * Enum about Credentials options.
 */
public enum DiscoveryCredentials {

	NONE("none"), 
	PLAIN("plain"), 
	SHA256("sha256");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private DiscoveryCredentials(String v) {
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
	public static DiscoveryCredentials fromValue(String v) {
		for (DiscoveryCredentials c : DiscoveryCredentials.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
