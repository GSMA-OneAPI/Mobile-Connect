package com.gsma.android.mobileconnect.userinfo;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse Userinfo information. Implements Serializable
 */
public class UserinfoData implements Serializable {
	private static final long serialVersionUID = -2553278213090322382L;

	String sub = null;
	String name = null;
	String given_name = null;
	String family_name = null;
	String middle_name = null;
	String nickname = null;
	String preferred_username = null;
	String profile = null;
	String picture = null;
	String website = null;
	String email = null;
	boolean email_verified = false;
	String gender = null;
	String birthdate = null;
	String zoneinfo = null;
	String locale = null;
	String phone_number = null;
	boolean phone_number_verfied = false;
	UserinfoAddress address = null;
	Number updated_at = null;

	/**
	 * Constructor
	 */
	public UserinfoData() {

	}

	/**
	 * Constructor
	 * 
	 * @param jsonObject
	 * @throws JSONException
	 */
	public UserinfoData(JSONObject jsonObject) throws JSONException {
		if (jsonObject != null) {
			this.sub = jsonObject.has("sub") ? jsonObject.getString("sub") : null;
			this.name = jsonObject.has("name") ? jsonObject.getString("name") : null;
			this.given_name = jsonObject.has("given_name") ? jsonObject.getString("given_name") : null;
			this.family_name = jsonObject.has("family_name") ? jsonObject.getString("family_name") : null;
			this.middle_name = jsonObject.has("middle_name") ? jsonObject.getString("middle_name") : null;
			this.nickname = jsonObject.has("nickname") ? jsonObject.getString("nickname") : null;
			this.preferred_username = jsonObject.has("preferred_username") ? jsonObject.getString("preferred_username") : null;
			this.profile = jsonObject.has("profile") ? jsonObject.getString("profile") : null;
			this.picture = jsonObject.has("picture") ? jsonObject.getString("picture") : null;
			this.website = jsonObject.has("website") ? jsonObject.getString("website") : null;
			this.email = jsonObject.has("email") ? jsonObject.getString("email") : null;
			this.email_verified = jsonObject.has("email_verified") ? jsonObject.getBoolean("email_verified") : false;
			this.gender = jsonObject.has("gender") ? jsonObject.getString("gender") : null;
			this.birthdate = jsonObject.has("birthdate") ? jsonObject.getString("birthdate") : null;
			this.zoneinfo = jsonObject.has("zoneinfo") ? jsonObject.getString("zoneinfo") : null;
			this.locale = jsonObject.has("locale") ? jsonObject.getString("locale") : null;
			this.phone_number = jsonObject.has("phone_number") ? jsonObject.getString("phone_number") : null;
			this.phone_number_verfied = jsonObject.has("phone_number_verfied") ? jsonObject.getBoolean("phone_number_verfied") : false;
			if(jsonObject.has("address")){
				JSONObject jsonAddress = jsonObject.getJSONObject("address");
				this.address = new UserinfoAddress(jsonAddress);
			}else this.address = null;
			this.updated_at = jsonObject.has("updated_at") ? (Number)jsonObject.get("updated_at") : null;
		}
	}

	/**
	 * Gets Userinfo response in JSONObject.
	 * 
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("sub", sub);
		obj.put("name", name);
		obj.put("given_name", given_name);
		obj.put("family_name", family_name);
		obj.put("middle_name", middle_name);
		obj.put("nickname", nickname);
		obj.put("preferred_username", preferred_username);
		obj.put("profile", profile);
		obj.put("picture", picture);
		obj.put("website", website);
		obj.put("email", email);
		obj.put("email_verified", email_verified);
		obj.put("gender", gender);
		obj.put("birthdate", birthdate);
		obj.put("zoneinfo", zoneinfo);
		obj.put("locale", locale);
		obj.put("phone_number", phone_number);
		obj.put("phone_number_verfied", phone_number_verfied);
		obj.put("address", address);
		obj.put("updated_at", updated_at);
		return obj;
	}

	/**
	 * Gets Userinfo response in String.
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
	 * @return String
	 */
	public String getSub() {
		return this.sub;
	}

	/**
	 * @param sub
	 *            sub to set
	 */
	public void setSub(String sub) {
		this.sub = sub;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return String
	 */
	public String getGiven_Name() {
		return this.given_name;
	}

	/**
	 * @param given_name
	 *            given_name to set
	 */
	public void setGiven_Name(String given_name) {
		this.given_name = given_name;
	}

	/**
	 * @return String
	 */
	public String getMiddle_Name() {
		return this.middle_name;
	}

	/**
	 * @param middle_name
	 *            middle_name to set
	 */
	public void setMiddle_Name(String middle_name) {
		this.middle_name = middle_name;
	}

	/**
	 * @return String
	 */
	public String getFamily_Name() {
		return this.family_name;
	}

	/**
	 * @param family_name
	 *            family_name to set
	 */
	public void setFamily_Name(String family_name) {
		this.family_name = family_name;
	}

	/**
	 * @return String
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * @param nickname
	 *            nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return String
	 */
	public String getPreferred_Username() {
		return this.preferred_username;
	}

	/**
	 * @param preferred_username
	 *            preferred_username to set
	 */
	public void setPreferred_Username(String preferred_username) {
		this.preferred_username = preferred_username;
	}

	/**
	 * @return String
	 */
	public String getProfile() {
		return this.profile;
	}

	/**
	 * @param profile
	 *            profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * @return String
	 */
	public String getPicture() {
		return this.picture;
	}

	/**
	 * @param picture
	 *            picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}

	/**
	 * @return String
	 */
	public String getWebsite() {
		return this.website;
	}

	/**
	 * @param website
	 *            website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * @return String
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email
	 *            email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return boolean
	 */
	public boolean getEmail_Verified() {
		return this.email_verified;
	}

	/**
	 * @param email_verified
	 *            email_verified to set
	 */
	public void setEmail_Verified(boolean email_verified) {
		this.email_verified = email_verified;
	}

	/**
	 * @return String
	 */
	public String getGender() {
		return this.gender;
	}

	/**
	 * @param gender
	 *            gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return String
	 */
	public String getBirthdate() {
		return this.birthdate;
	}

	/**
	 * @param birthdate
	 *            birthdate to set
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return String
	 */
	public String getZoneinfo() {
		return this.zoneinfo;
	}

	/**
	 * @param zoneinfo
	 *            zoneinfo to set
	 */
	public void setZoneinfo(String zoneinfo) {
		this.zoneinfo = zoneinfo;
	}

	/**
	 * @return String
	 */
	public String getLocale() {
		return this.locale;
	}

	/**
	 * @param locale
	 *            locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return boolean
	 */
	public boolean getPhone_Number_Verfied() {
		return this.phone_number_verfied;
	}

	/**
	 * @param phone_number_verfied
	 *            phone_number_verfied to set
	 */
	public void setPhone_Number_Verfied(boolean phone_number_verfied) {
		this.phone_number_verfied = phone_number_verfied;
	}

	/**
	 * @return String
	 */
	public String getPhone_Number() {
		return this.phone_number;
	}

	/**
	 * @param phone_number
	 *            phone_number to set
	 */
	public void setPhone_Number(String phone_number) {
		this.phone_number = phone_number;
	}

	/**
	 * @return UserinfoAddress
	 */
	public UserinfoAddress getAddress() {
		return this.address;
	}

	/**
	 * @param address
	 *            address to set
	 */
	public void setAddress(UserinfoAddress address) {
		this.address = address;
	}

	/**
	 * @return Number
	 */
	public Number getUpdated_At() {
		return this.updated_at;
	}

	/**
	 * @param updated_at
	 *            updated_at to set
	 */
	public void setUpdated_At(Number updated_at) {
		this.updated_at = updated_at;
	}

}
