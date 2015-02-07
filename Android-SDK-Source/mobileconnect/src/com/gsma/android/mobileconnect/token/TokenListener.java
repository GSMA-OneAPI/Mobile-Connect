package com.gsma.android.mobileconnect.token;

import org.json.JSONObject;


/**
 * Interface TokenListener
 */
public interface TokenListener {

	/**
	 * Method to wait successful response.
	 * @param response
	 */
	public void tokenResponse(TokenData response);
	
	/**
	 * Method to wait error response.
	 * @param error
	 */
	public void errorToken(JSONObject error);

}
