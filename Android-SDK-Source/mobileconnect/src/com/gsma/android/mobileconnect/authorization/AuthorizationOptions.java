package com.gsma.android.mobileconnect.authorization;

import java.util.HashMap;

import com.gsma.android.mobileconnect.values.Display;

/**
 * Helper functions to parse AuthorizationOptions parameter.
 */
public class AuthorizationOptions{
	
	HashMap<String, Object> hm = null; 
	
	/**
	 * Constructor
	 */
	public AuthorizationOptions(){
		hm = new HashMap<String, Object>(); 
	}
	
	/**
     * Get method extended by default object to be returned when key
     * is not found.
     *
     * @param key key to look up
     * @param _default default value to return if key is not found
     * @return value that is associated with key
     */
    public Object get(String key, Object _default) {
        if (hm.containsKey(key)) {
            return hm.get(key);
        }
        return _default;
    }

    
	/**
	 * @param display
	 *           display to set
	 */
	public void setDisplay(Display display) {
		hm.put("display", display);
	}

	/**
	 * @param idTokenHint
	 *           idTokenHint to set
	 */
	public void setIDTokenHint(String idTokenHint) {
		hm.put("id_token_hint", idTokenHint);
	}
	
	/**
	 * @param uiLocales
	 *           uiLocales to set
	 */
	public void setUILocales(String uiLocales) {
		hm.put("ui_locales", uiLocales);
	}

	/**
	 * @param claimsLocales
	 *           claimsLocales to set
	 */
	public void setClaimsLocales(String claimsLocales) {
		hm.put("claims_locales", claimsLocales);
	}
	
	/**
	 * @param loginHint
	 *           loginHint to set
	 */
	/*
	 * The login_hint can contain the MSISDN or the Encrypted MSISDN and SHOULD
	 * be tagged as MSISDN:<Value> and ENCR_MSISDN:<Value> respectively
	 */
	public void setLoginHint(String loginHint) {
		hm.put("login_hint", loginHint);
	}
	
	/**
	 * @param dtbs
	 *           dtbs to set
	 */
	public void setDtbs(String dtbs) {
		hm.put("dtbs", dtbs);
	}
	
	/**
	 * Removes a mapping with the specified key from this Map.
	 * @param key the key of the mapping to remove.
	 * @return Object
	 */
	public Object remove(String key) {
		return hm.remove(key);
	}
	
	/**
	 * Removes all mappings from this hash map, leaving it empty.
	 */
	public void clearAll() {
		hm.clear();
	}
	
	/**
	 * Returns the HashMap<String, Object> object that handles AuthorizationOptions.
	 * @return HashMap<String, Object>
	 */
	public HashMap<String, Object> getAuthorizationOptions(){
		return hm;
	}
	
}
