package com.gsma.android.mobileconnect.userinfo;

import org.json.JSONObject;

/**
 * Interface OpenIDConnectCallbackUserinfoReciever
 */
public interface OpenIDConnectCallbackUserinfoReciever{
	/**
	 * Method to wait the response.
	 * @param response
	 */
	public void processUserinfoResponse(JSONObject response);
}
