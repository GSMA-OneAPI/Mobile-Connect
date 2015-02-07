package com.gsma.android.oneapi.valuesLogo;

/**
 * Enum about BgColor options.
 */
public enum BgColor {

	NORMAL("normal"), 
	BLACK("black"), 
	REVERSED("reversed");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private BgColor(String v) {
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
	 * Gets the BgColor value from the String value.
	 * 
	 * @param value
	 * @return BgColor
	 * @throws IllegalArgumentException
	 */
	public static BgColor fromValue(String v) {
		for (BgColor c : BgColor.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

	
}
