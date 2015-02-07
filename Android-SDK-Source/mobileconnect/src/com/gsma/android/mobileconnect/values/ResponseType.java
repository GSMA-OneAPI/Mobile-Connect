package com.gsma.android.mobileconnect.values;

/**
 * Enum about ResponseType options.
 */
public enum ResponseType {

		CODE("code");

		private final String value;

		/**
		 * Constructor
		 * 
		 * @param value
		 */
		private ResponseType(String v) {
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
		 * Gets the ResponseType value from the String value.
		 * 
		 * @param value
		 * @return ResponseType
		 * @throws IllegalArgumentException
		 */
		public static ResponseType fromValue(String v) {
			for (ResponseType c : ResponseType.values()) {
				if (c.value.equalsIgnoreCase(v)) {
					return c;
				}
			}
			throw new IllegalArgumentException(v);
		}

}
