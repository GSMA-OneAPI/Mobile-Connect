package com.gsma.android.mobileconnect.values;

/**
 * Enum about Prompt options.
 */
public enum Prompt {

	NONE("none"), 
	LOGIN("login"), 
	CONSENT("consent"),
	SELECT_ACCOUNT("select_account");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private Prompt(String v) {
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
	 * Gets the Prompt value from the String value.
	 * 
	 * @param value
	 * @return Prompt
	 * @throws IllegalArgumentException
	 */
	public static Prompt fromValue(String v) {
		for (Prompt c : Prompt.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}

