package com.gsma.android.oneapi.discovery;

import org.json.JSONObject;

/**
 * Interface DiscoveryCallbackReciever
 */
public interface DiscoveryCallbackReceiver {

	/**
	 * Method to wait the response.
	 * @param result
	 */
	public void receiveDiscoveryData(JSONObject result);

}
