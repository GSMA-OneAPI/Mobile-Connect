package com.gsma.android.oneapi.logo;

import org.json.JSONObject;

/**
 * Interface LogoListener
 */
public interface LogoListener {

	/**
	 * Method to wait successful response.
	 * @param logoitem
	 */
	public void logoInfo(LogoItemArray li);
	
	/**
	 * Method to wait error response.
	 * @param jsonobject
	 */
	public void errorLogoInfo(JSONObject o);

}
