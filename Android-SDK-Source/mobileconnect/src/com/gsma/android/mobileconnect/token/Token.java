package com.gsma.android.mobileconnect.token;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

/**
 * Main class with library methods. Implements OpenIDConnectCallbackTokenReciever
 */
public class Token implements OpenIDConnectCallbackTokenReceiver {
	private static final String TAG = "Token";

	RetrieveTokenTask initialRetrieveTokenTask;
	TokenListener listener;

	/**
	 * Constructor
	 */
	public Token() {
	}

	/**
	 * Exchanges an authorization code for an access token which can be used to
	 * authenticate access to services.
	 * 
	 * @param tokenUri
	 *            token endpoint.
	 * @param code
	 *            as returned from the authorization call.
	 * @param clientId
	 *            used for HTTP Basic Authorization of the request.
	 * @param clientSecret
	 *            used for HTTP Basic Authorization of the request.
	 * @param returnUri
	 *            which is the return point after the user has
	 *            authenticated/consented.
	 * @param listener
	 *            It is necessary to implement tokenResponse(JSONObject
	 *            response) and errorToken(JSONObject error) to manage the
	 *            response.
	 */
	public void tokenFromAuthorizationCode(String tokenUri, String code,
			String clientId, String clientSecret, String returnUri, TokenListener listener) {
		this.listener = listener;
		try {
			initialRetrieveTokenTask = new RetrieveTokenTask(tokenUri, code,
					clientId, clientSecret, returnUri, null, "token", this);
			initialRetrieveTokenTask.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Internal use. Method from the OpenIDConnectCallbackTokenReciever interface to
	 * wait the response.
	 * 
	 * @param response
	 * @throws JSONException
	 * @throws NullPointerException
	 */
	@Override
	public void processTokenResponse(JSONObject response) {
		if (initialRetrieveTokenTask != null) {
			initialRetrieveTokenTask.cancel(true);
			initialRetrieveTokenTask = null;
		}
		try {
			TokenData tokenData = new TokenData(response);
			listener.tokenResponse(tokenData);
		} catch (JSONException e) {
			JSONObject json = new JSONObject();
			try {
				json.put("Exception", "JSONException");
				json.put("Message", e.getMessage());
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			try{
				listener.errorToken(json);
			}catch(NullPointerException exc) {
				exc.printStackTrace();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the refresh process and will return a new access token if
	 * successful as well as marking the old access token and refresh token as
	 * revoked.
	 * 
	 * @param tokenUri
	 * @param refreshToken
	 * @param scope
	 * @param clientId
	 * @param clientSecret
	 * @param listener
	 */
	public void refreshingToken(String tokenUri, String refreshToken, String scope,
			String clientId, String clientSecret, TokenListener listener) {
		this.listener = listener;
		try {
			initialRetrieveTokenTask = new RetrieveTokenTask(tokenUri, refreshToken,
					clientId, clientSecret, null, scope, "refresh", this);
			initialRetrieveTokenTask.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the revocation process and if successful marking the old access
	 * token and any associated refresh token as revoked.
	 * 
	 * @param tokenUri
	 * @param accessToken
	 * @param clientId
	 * @param clientSecret
	 * @param listener
	 */
	public void revokingAccessToken(String tokenUri, String accessToken, String clientId,
			String clientSecret, TokenListener listener) {
		this.listener = listener;
		try {
			initialRetrieveTokenTask = new RetrieveTokenTask(tokenUri, accessToken,
					clientId, clientSecret, null, null, "revoke", this);
			initialRetrieveTokenTask.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decodes the contents of JSON Web Token.
	 * 
	 * @param idToken
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject decodeJWTIDToken(String idToken) {
		JSONObject json = null;
		if (idToken != null && idToken.trim().length() > 0) {
			String[] id_token_parts = idToken.split("\\.");
			if (id_token_parts != null && id_token_parts.length >= 2) {
				String idValue = id_token_parts[1];
				byte[] decoded = Base64.decode(idValue, Base64.DEFAULT);
				String dec = new String(decoded);
				try {
					json = new JSONObject(dec);
					String iss = json.has("iss") ? json.getString("iss") : null;
					String sub = json.has("sub") ? json.getString("sub") : null;
					String aud = json.has("aud") ? json.getString("aud") : null;
					String exp = json.has("exp") ? json.getString("exp") : null;
					String iat = json.has("iat") ? json.getString("iat") : null;
					String auth_time = json.has("auth_time") ? json
							.getString("auth_time") : null;
					String nonce = json.has("nonce") ? json.getString("nonce")
							: null;
					String at_hash = json.has("at_hash") ? json
							.getString("at_hash") : null;
					String acr = json.has("acr") ? json.getString("acr") : null;
					String amr = json.has("amr") ? json.getString("amr") : null;
					String azp = json.has("azp") ? json.getString("azp") : null;
					String dts = json.has("dts") ? json.getString("dts") : null;
					String upk = json.has("upk") ? json.getString("upk") : null;
					String dts_time = json.has("dts_time") ? json
							.getString("dts_time") : null;
					String email = json.has("email") ? json.getString("email")
							: null;
					Log.d(TAG, "iss: "+iss);
					Log.d(TAG, "sub: "+sub);
					Log.d(TAG, "aud: "+aud);
					Log.d(TAG, "exp: "+exp);
					Log.d(TAG, "iat: "+iat);
					Log.d(TAG, "auth_time: "+auth_time);
					Log.d(TAG, "nonce: "+nonce);
					Log.d(TAG, "at_hash: "+at_hash);
					Log.d(TAG, "acr: "+acr);
					Log.d(TAG, "amr: "+amr);
					Log.d(TAG, "azp: "+azp);
					Log.d(TAG, "dts: "+dts);
					Log.d(TAG, "upk: "+upk);
					Log.d(TAG, "dts_time: "+dts_time);
					Log.d(TAG, "email: "+email);
				} catch (JSONException e) {
					json = new JSONObject();
					try {
						json.put("Exception", "JSONException");
						json.put("Message", e.getMessage());
					} catch (JSONException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return json;
	}

	/**
	 * Checks if the access token is still valid based on current time and
	 * expires_in value.
	 * 
	 * @param expiresIn
	 * @return boolean
	 * @throws NumberFormatException
	 */
	public boolean isAccessTokenValid(String expiresIn) {
		boolean valid = false;
		Calendar calToday = Calendar.getInstance();
		Calendar calSaved = Calendar.getInstance();
		try{
			calSaved.setTimeInMillis(Long.parseLong(expiresIn));
			if (calToday.getTimeInMillis() <= calSaved.getTimeInMillis())
				valid = true;
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return valid;
	}

}
