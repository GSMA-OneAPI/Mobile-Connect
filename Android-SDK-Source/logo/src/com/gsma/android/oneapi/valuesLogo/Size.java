package com.gsma.android.oneapi.valuesLogo;

/**
 * Enum about Size options.
 */
public enum Size {

	SMALL("small"), 
	MEDIUM("medium"), 
	LARGE("large");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private Size(String v) {
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
	 * Gets the Size value from the String value.
	 * 
	 * @param value
	 * @return Size
	 * @throws IllegalArgumentException
	 */
	public static Size fromValue(String v) {
		for (Size c : Size.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
