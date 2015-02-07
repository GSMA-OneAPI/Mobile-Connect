package com.gsma.android.mobileconnect.token;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse Token information. Implements Serializable
 */
public class TokenData implements Serializable {
	private static final long serialVersionUID = -8174316022099823984L;

	String access_token = null;
	String token_type = null;
	String id_token = null;
	String expires_in = null;
	String refresh_token = null;

	/**
	 * Constructor
	 */
	public TokenData() {

	}

	/**
	 * Constructor
	 * 
	 * @param jsonObject
	 * @throws JSONException
	 */
	public TokenData(JSONObject jsonObject) throws JSONException {
		if (jsonObject != null) {
			this.access_token = jsonObject.has("access_token") ? jsonObject.getString("access_token") : null;
			this.token_type = jsonObject.has("token_type") ? jsonObject.getString("token_type") : null;
			this.id_token = jsonObject.has("id_token") ? jsonObject.getString("id_token") : null;
			this.expires_in = jsonObject.has("expires_in") ? jsonObject.getString("expires_in") : null;
			this.refresh_token = jsonObject.has("refresh_token") ? jsonObject.getString("refresh_token") : null;

		}
	}

	/**
	 * Gets Token response in JSONObject.
	 * 
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("access_token", access_token);
		obj.put("token_type", token_type);
		obj.put("id_token", id_token);
		obj.put("expires_in", expires_in);
		obj.put("refresh_token", refresh_token);
		return obj;
	}

	/**
	 * Gets Token response in String.
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
	public String getAccess_Token() {
		return this.access_token;
	}

	/**
	 * @param access_token
	 *            access_token to set
	 */
	public void setAccess_Token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * @return String
	 */
	public String getToken_Type() {
		return this.token_type;
	}

	/**
	 * @param token_type
	 *            token_type to set
	 */
	public void setToken_Type(String token_type) {
		this.token_type = token_type;
	}

	/**
	 * @return String
	 */
	public String getId_Token() {
		return this.id_token;
	}

	/**
	 * @param id_token
	 *            id_token to set
	 */
	public void setId_Token(String id_token) {
		this.id_token = id_token;
	}

	/**
	 * @return String
	 */
	public String getExpires_In() {
		return this.expires_in;
	}

	/**
	 * @param expires_in
	 *            expires_in to set
	 */
	public void setExpires_In(String expires_in) {
		this.expires_in = expires_in;
	}

	/**
	 * @return String
	 */
	public String getRefresh_Token() {
		return this.refresh_token;
	}

	/**
	 * @param refresh_token
	 *            refresh_token to set
	 */
	public void setRefresh_Token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

}
