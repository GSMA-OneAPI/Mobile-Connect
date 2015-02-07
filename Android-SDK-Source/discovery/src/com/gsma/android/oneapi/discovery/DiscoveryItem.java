package com.gsma.android.oneapi.discovery;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse discovery response. Implements Serializable
 */
public class DiscoveryItem implements Serializable {

	private static final long serialVersionUID = 2147233270126180800L;

//	{
//	   "response":{
//	      "apis":{
//	         "payment":{
//	            "link":[
//	               {
//	                  "href":"https://etelco-prod.apigee.net/atel/v1/payment/acr:Authorization/transactions/amountReservation",
//	                  "rel":"reserve"
//	               },
//	               {
//	                  "href":"https://etelco-prod.apigee.net/atel/v1/payment/acr:Authorization/transactions/amountReservation/{transactionId}",
//	                  "rel":"chargeAgainstReservation"
//	               },
//	               {
//	                  "href":"https://etelco-prod.apigee.net/atel/v1/payment/acr:Authorization/transactions/amountReservation/{transactionId}",
//	                  "rel":"transactionstatus"
//	               },
//	               {
//	                  "href":"GET,POST-/payment/acr:Authorization/transactions/amount",
//	                  "rel":"scope"
//	               }
//	            ]
//	         },
//	         "operatorid":{
//	            "link":[
//	               {
//	                  "href":"https://etelco-prod.apigee.net/v1/oauth2/authorize",
//	                  "rel":"authorize"
//	               },
//	               {
//	                  "href":"https://etelco-prod.apigee.net/v1/oauth2/token",
//	                  "rel":"token"
//	               },
//	               {
//	                  "href":"https://etelco-prod.apigee.net/v1/oauth2/userinfo",
//	                  "rel":"userinfo"
//	               }
//	            ]
//	         }
//	      },
//	      "client_id":"RG1hUElYRmlocUpoSGhWUXdwazlOSGQ3QnpJelF4T2U=",
//	      "client_secret":"",
//	      "country":"US",
//	      "currency":"USD",
//	      "subscriber_operator":"Atel"
//	   },
//	   "ttl":1392554192821
//	}

	/**
	 * Constructor
	 */
	public DiscoveryItem() {
	}

	/**
	 * Constructor
	 * @param json
	 * @throws JSONException
	 */
	public DiscoveryItem(JSONObject json) throws JSONException {
		if (json != null) {
			this.ttl = json.has("ttl") ? json.getString("ttl") : null;
			this.response = new DiscoveryResponse(json.getJSONObject("response"));
			this.error = json.has("error") ? json.getString("error") : null;
			this.error_description = json.has("error_description") ? json.getString("error_description") : null;
		}
	}

	String ttl = null;

	/**
	 * @return String
	 */
	public String getTtl() {
		return ttl;
	}

	/**
	 * @param ttl
	 *           ttl to set
	 */
	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	DiscoveryResponse response = null;

	/**
	 * @return DiscoveryResponse
	 */
	public DiscoveryResponse getResponse() {
		return this.response;
	}

	/**
	 * @param response
	 *           response to set
	 */
	public void setResponse(DiscoveryResponse response) {
		this.response = response;
	}
	
	String error=null;
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	String error_description=null;

	public String getError_description() {
		return error_description;
	}

	public void setError_description(String error_description) {
		this.error_description = error_description;
	}

	/**
	 * Gets discovery information in JSONObject.
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("ttl", ttl);
		if (response != null) obj.put("response", response);
		if (error!=null) obj.put("error", error);
		if (error_description!=null) obj.put("error_description", error_description);
		return obj;
	}

}
