package com.gsma.android.oneapi.logo;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Helper functions to parse logo response.
 */
public class LogoItemArray {

	LogoItem[] logos;

//	[
//	    {
//	        "operatorId": "exchange",
//	        "apiName": "operatorid",
//	        "language": "en",
//	        "size": "small",
//	        "height": "58px",
//	        "width": "128px",
//	        "action": "default",
//	        "url": "https://integration-api.apiexchange.org:443/v1/logostorage/images/english/mobcon_pms5275_rgb_128.png",
//	        "bgColourRange": "#ffffff,#000000",
//	        "bgColor": "normal",
//	        "aspectRatio": "landscape"
//	    },
//	    {
//	        "operatorId": "exchange",
//	        "apiName": "payment",
//	        "language": "en",
//	        "size": "small",
//	        "height": "64px",
//	        "width": "260px",
//	        "action": "default",
//	        "url": "https://integration-api.apiexchange.org:443/v1/logostorage/images/english/pay_with_operator_260x64.png",
//	        "bgColourRange": "#ffffff,#000000",
//	        "bgColor": "normal",
//	        "aspectRatio": "landscape"
//	    }
//	]
	

	/**
	 * @return LogoItem[]
	 */
	public LogoItem[] getLogos() {
		return logos;
	}

	/**
	 * @param logos
	 *            an array to set
	 */
	public void setLogos(LogoItem[] logos) {
		this.logos = logos;
	}

	/**
	 * Constructor
	 */
	public LogoItemArray() {

	}

	/**
	 * Constructor
	 * @param source
	 * @throws JSONException
	 */
	public LogoItemArray(Object source) throws JSONException {
		if (source != null) {
			if (source instanceof JSONArray) {
				JSONArray sa = (JSONArray) source;
				logos = new LogoItem[sa.length()];
				for (int i = 0; i < sa.length(); i++) {
					logos[i] = new LogoItem(sa.get(i));
				}
			}
		}
	}

}
