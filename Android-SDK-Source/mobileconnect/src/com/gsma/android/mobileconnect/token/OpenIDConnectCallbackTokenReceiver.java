package com.gsma.android.mobileconnect.token;

import org.json.JSONObject;

/**
 * Interface OpenIDConnectCallbackTokenReciever
 */
public interface OpenIDConnectCallbackTokenReceiver{
	/**
	 * Method to wait the response.
	 * @param response
	 */
	public void processTokenResponse(JSONObject response);
}
