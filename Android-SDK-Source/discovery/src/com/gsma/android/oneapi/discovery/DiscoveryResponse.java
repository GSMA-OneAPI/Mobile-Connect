package com.gsma.android.oneapi.discovery;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse discovery information. Implements Serializable
 */
public class DiscoveryResponse implements Serializable {

	private static final long serialVersionUID = 6597100283176242379L;

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

	/**
	 * Constructor
	 */
	public DiscoveryResponse() {

	}

	/**
	 * Constructor
	 * @param jsonObject
	 * @throws JSONException
	 */
	public DiscoveryResponse(JSONObject jsonObject) throws JSONException {
		if (jsonObject != null) {
			this.client_id = jsonObject.has("client_id") ? jsonObject.getString("client_id") : null;
			this.client_secret = jsonObject.has("client_secret") ? jsonObject.getString("client_secret") : null;
			this.subscriber_operator = jsonObject.has("subscriber_operator") ? jsonObject.getString("subscriber_operator") : null;
			this.country = jsonObject.has("country") ? jsonObject.getString("country") : null;
			this.currency = jsonObject.has("currency") ? jsonObject.getString("currency") : null;
			if(jsonObject.has("subscriber_id"))
				this.subscriber_id = jsonObject.getString("subscriber_id");

			if(jsonObject.has("apis")){
				JSONObject apilist = jsonObject.getJSONObject("apis");
				@SuppressWarnings("rawtypes")
				Iterator iter = apilist.keys();
				apis = new HashMap<String, Api>();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					Api api = new Api(apilist.getJSONObject(key));
					apis.put(key, api);
				}
			}
		}
	}

	String client_id = null;

	/**
	 * @return String 
	 */
	public String getClient_id() {
		return this.client_id;
	}

	/**
	 * @param client_id
	 *           client_id to set
	 */
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	String client_secret = null;

	/**
	 * @return String 
	 */
	public String getClient_secret() {
		return this.client_secret;
	}

	/**
	 * @param client_secret
	 *           client_secret to set
	 */
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	String subscriber_operator = null;

	/**
	 * @return String 
	 */
	public String getSubscriber_operator() {
		return this.subscriber_operator;
	}

	/**
	 * @param subscriber_operator
	 *           subscriber_operator to set
	 */
	public void setSubscriber_operator(String subscriber_operator) {
		this.subscriber_operator = subscriber_operator;
	}

	String country = null;

	/**
	 * @return String 
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * @param country
	 *           country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	String currency = null;

	/**
	 * @return String 
	 */
	public String getCurrency() {
		return this.currency;
	}

	/**
	 * @param currency
	 *           currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	String subscriber_id = null;

	/**
	 * @return String 
	 */
	public String getSubscriber_id() {
		return this.subscriber_id;
	}

	/**
	 * @param subscriber_id
	 *           subscriber_id to set
	 */
	public void setSubscriber_id(String subscriber_id) {
		this.subscriber_id = subscriber_id;
	}

	HashMap<String, Api> apis = null;

	/**
	 * @return HashMap<String, Api>
	 */
	public HashMap<String, Api> getApis() {
		return this.apis;
	}

	/**
	 * @param name
	 * @return Api 
	 */
	public Api getApi(String name) {
		return (name != null && apis != null) ? apis.get(name) : null;
	}

	/**
	 * Gets discovery response in JSONObject.
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("client_id", client_id);
		obj.put("client_secret", client_secret);
		obj.put("subscriber_operator", subscriber_operator);
		obj.put("country", country);
		obj.put("currency", currency);
		if(subscriber_id!=null)
			obj.put("subscriber_id", subscriber_id);
		if (apis != null)
			obj.put("apis", apis);
		return obj;
	}

	/**
	 * Gets discovery response in String.
	 * @return String
	 * @throws JSONException
	 */
	public String toString() {
		String rv = null;
		try {
			JSONObject obj = toObject();
			rv = obj.toString();
		} catch (JSONException e) {
		}
		return rv;
	}

}
