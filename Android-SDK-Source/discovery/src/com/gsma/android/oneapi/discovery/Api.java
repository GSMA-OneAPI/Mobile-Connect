package com.gsma.android.oneapi.discovery;

import java.io.Serializable;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse discovery information. Implements Serializable
 */
public class Api implements Serializable {

	private static final long serialVersionUID = -7245260322532564148L;

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


	/**
	 * Constructor
	 */
	public Api() {

	}

	/**
	 * Constructor
	 * @param jsonObject
	 * @throws JSONException
	 */
	public Api(JSONObject jsonObject) throws JSONException {
		if (jsonObject != null) {
			if(jsonObject.has("link")){
				JSONArray linkArray = jsonObject.getJSONArray("link");
				if (linkArray != null) {
					link = new Link[linkArray.length()];
					linkMap = new HashMap<String, String>();
					for (int i = 0; i < linkArray.length(); i++) {
						link[i] = new Link(linkArray.getJSONObject(i));
						linkMap.put(link[i].getRel(), link[i].getHref());
					}
				}
			}
		}
	}

	Link[] link = null;

	/**
	 * @return Link[] 
	 */
	public Link[] getLink() {
		return this.link;
	}

	/**
	 * @param link
	 *           link to set
	 */
	public void setLink(Link[] link) {
		this.link = link;
		linkMap = new HashMap<String, String>();
		if (link != null) {
			for (Link l : link) {
				linkMap.put(l.getRel(), l.getHref());
			}
		}
	}

	HashMap<String, String> linkMap = null;

	/**
	 * @param rel
	 * @return String 
	 */ 
	public String getHref(String rel) {
		return (linkMap != null && rel != null) ? linkMap.get(rel) : null;
	}

	/**
	 * Gets Api discovery response in JSONObject.
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();
		if (link != null)
			obj.put("link", link);
		return obj;
	}

	/**
	 * Gets Api discovery response in String.
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
