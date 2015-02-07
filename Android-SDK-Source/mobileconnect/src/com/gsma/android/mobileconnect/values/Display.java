package com.gsma.android.mobileconnect.values;

/**
 * Enum about Display options.
 */
public enum Display {

	PAGE("page"), 
	POPUP("popup"), 
	TOUCH("touch"),
	WAP("wap");

	private final String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	private Display(String v) {
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
	 * Gets the Display value from the String value.
	 * 
	 * @param value
	 * @return Display
	 * @throws IllegalArgumentException
	 */
	public static Display fromValue(String v) {
		for (Display c : Display.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

	
}
