package com.gsma.android.oneapi.valuesLogo;

/**
 * Enum about AspectRatio options.
 */
public enum AspectRatio {

	LANDSCAPE("landscape"), 
	SQUARE("square");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private AspectRatio(String v) {
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
	 * Gets the AspectRatio value from the String value.
	 * 
	 * @param value
	 * @return AspectRatio
	 * @throws IllegalArgumentException
	 */
	public static AspectRatio fromValue(String v) {
		for (AspectRatio c : AspectRatio.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

	
}
