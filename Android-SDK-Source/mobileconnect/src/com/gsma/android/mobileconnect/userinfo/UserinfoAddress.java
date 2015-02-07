package com.gsma.android.mobileconnect.userinfo;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse Userinfo address information. Implements
 * Serializable
 */
public class UserinfoAddress implements Serializable {
	private static final long serialVersionUID = 3095311166296409832L;

	String formatted;
	String street_address;
	String locality;
	String region;
	String postal_code;
	String country;

	/**
	 * Constructor
	 */
	public UserinfoAddress() {

	}

	/**
	 * Constructor
	 * 
	 * @param jsonObject
	 * @throws JSONException
	 */
	public UserinfoAddress(JSONObject jsonObject) throws JSONException {
		if (jsonObject != null) {
			this.formatted = jsonObject.has("formatted") ? jsonObject.getString("formatted") : null;
			this.street_address = jsonObject.has("street_address") ? jsonObject.getString("street_address") : null;
			this.locality = jsonObject.has("locality") ? jsonObject.getString("locality") : null;
			this.region = jsonObject.has("region") ? jsonObject.getString("region") : null;
			this.postal_code = jsonObject.has("postal_code") ? jsonObject.getString("postal_code") : null;
			this.country = jsonObject.has("country") ? jsonObject.getString("country") : null;
		}
	}

	/**
	 * Gets Userinfo address response in JSONObject.
	 * 
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("formatted", formatted);
		obj.put("street_address", street_address);
		obj.put("locality", locality);
		obj.put("region", region);
		obj.put("postal_code", postal_code);
		obj.put("country", country);
		return obj;
	}

	/**
	 * Gets Userinfo address response in String.
	 * 
	 * @return String
	 * @throws JSONException
	 */
	public String toString() {
		String rv = null;
		try {
			JSONObject obj = toObject();
			rv = obj.toString();
		} catch (JSONException e) {
		}
		return rv;
	}

	/**
	 * @return the formatted
	 */
	public String getFormatted() {
		return formatted;
	}

	/**
	 * @param formatted
	 *            the formatted to set
	 */
	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}

	/**
	 * @return the street_address
	 */
	public String getStreet_address() {
		return street_address;
	}

	/**
	 * @param street_address
	 *            the street_address to set
	 */
	public void setStreet_address(String street_address) {
		this.street_address = street_address;
	}

	/**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * @param locality
	 *            the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the postal_code
	 */
	public String getPostal_code() {
		return postal_code;
	}

	/**
	 * @param postal_code
	 *            the postal_code to set
	 */
	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

}
