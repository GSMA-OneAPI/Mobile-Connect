package com.gsma.android.oneapi.valuesLogo;

/**
 * Enum about Apis options.
 */
public enum Apis {

	PAYMENT("payment"), 
	OPERATORID("operatorid");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private Apis(String v) {
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
	 * Gets the Apis value from the String value.
	 * 
	 * @param value
	 * @return Apis
	 * @throws IllegalArgumentException
	 */
	public static Apis fromValue(String v) {
		for (Apis c : Apis.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
