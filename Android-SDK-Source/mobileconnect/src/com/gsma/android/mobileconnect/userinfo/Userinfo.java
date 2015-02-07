package com.gsma.android.mobileconnect.userinfo;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Main class with library methods. Implements OpenIDConnectCallbackUserinfoReciever
 */
public class Userinfo implements OpenIDConnectCallbackUserinfoReciever {

	RetrieveUserinfoTask initialRetrieveUserinfoTask;
	UserinfoListener listener;

	/**
	 * Constructor
	 */
	public Userinfo() {
	}

	/**
	 * Application can optionally use the access token to request access to
	 * stored user information via the OpenID Connect 'userinfo' service.
	 * 
	 * @param userinfoUri
	 *            userinfo endpoint.
	 * @param accessToken
	 * @param listener
	 *            It is necessary to implement userinfoResponse(JSONObject
	 *            response) and errorUserinfo(JSONObject error) to manage the
	 *            response.
	 */
	public void userinfo(String userinfoUri, String accessToken,
			UserinfoListener listener) {
		this.listener = listener;
		try {
			initialRetrieveUserinfoTask = new RetrieveUserinfoTask(userinfoUri,
					accessToken, this);
			initialRetrieveUserinfoTask.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Internal use. Method from the OpenIDConnectCallbackUserinfoReciever
	 * interface to wait the response.
	 * 
	 * @param response
	 * @throws JSONException
	 * @throws NullPointerException
	 */
	@Override
	public void processUserinfoResponse(JSONObject response) {
		if (initialRetrieveUserinfoTask != null) {
			initialRetrieveUserinfoTask.cancel(true);
			initialRetrieveUserinfoTask = null;
		}
		if (response != null) {
			try {
				UserinfoData userData = new UserinfoData(response);
				listener.userinfoResponse(userData);
			} catch (JSONException ex) {
				JSONObject json = new JSONObject();
				try {
					json.put("Exception", "JSONException");
					json.put("Message", ex.getMessage());
				} catch (JSONException exc) {
					exc.printStackTrace();
				}
				try{
					listener.errorUserinfo(json);
				}catch(NullPointerException excp){
					excp.printStackTrace();
				}
			} catch (NullPointerException e){
				e.printStackTrace();
			}
		} else {
			JSONObject json = new JSONObject();
			try {
				json.put("Exception", "JSONException");
				json.put("Message", "Null response");
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			try{
				listener.errorUserinfo(json);
			}catch(NullPointerException e){
				e.printStackTrace();
			}
		}
	}

}
