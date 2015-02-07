package com.gsma.android.oneapi.discovery;

import org.json.JSONObject;

/**
 * Interface DiscoveryListener
 */
public interface DiscoveryListener {

	/**
	 * Method to wait successful response.
	 * @param discoveryitem
	 */
	public void discoveryInfo(DiscoveryItem di);
	
	/**
	 * Method to wait error response.
	 * @param jsonobject
	 */
	public void errorDiscoveryInfo(JSONObject jo);

}
