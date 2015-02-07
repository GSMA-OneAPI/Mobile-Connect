package com.gsma.android.mobileconnect.userinfo;

import org.json.JSONObject;


/**
 * Interface UserinfoListener
 */
public interface UserinfoListener {

	/**
	 * Method to wait successful response.
	 * @param response
	 */
	public void userinfoResponse(UserinfoData response);
	
	/**
	 * Method to wait error response.
	 * @param error
	 */
	public void errorUserinfo(JSONObject error);

}
