package com.gsma.android.mobileconnect.authorization;

/**
 * Interface AuthorizationListener
 */
public interface AuthorizationListener {

	/**
	 * Method to wait successful response.
	 * @param state
	 * @param authorizationCode
	 * @param error
	 * @param clientId
	 * @param clientSecret
	 * @param scope
	 * @param redirectUri
	 */
	public void authorizationCodeResponse(String state, String authorizationCode, String error, String clientId, String clientSecret, String scopes, String redirectUri);
	
	/**
	 * Method to wait error response.
	 * @param reason
	 */
	public void authorizationError(String reason);

}
