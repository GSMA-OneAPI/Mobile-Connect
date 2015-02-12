package com.gsma.android.oneapi.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import com.gsma.android.oneapi.utilsDiscovery.HttpUtils;
import com.gsma.android.oneapi.utilsDiscovery.JsonUtils;

import android.util.Log;

/**
 * Makes a petition to the discovery service. 
 */
public class ProcessDiscoveryToken {

	private static final String TAG = "ProcessDiscoveryToken";

	/**
	 * Constructor 
	 * @param mccmnc
	 * @param consumerKey
	 * @param serviceUri
	 * @return JSONObject
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static JSONObject start(String mccmnc, String consumerKey, String consumerSecret, String serviceUri, boolean verboseTracing, String subscriber_id, String redirectUrl) {

		JSONObject errorResponse = null;

		try {

			if (mccmnc!=null && mccmnc.indexOf("_") > -1) {
				String[] parts=mccmnc.split("_", 2);
				if (parts!=null && parts.length==2 && parts[0].trim().length()>0 && parts[1].trim().length()>0) {
					String phase2Uri = serviceUri + "?Selected-MCC=" + parts[0].trim()+"&Selected-MNC="+parts[1].trim()+"&Redirect_URL="+HttpUtils.encodeUriParameter(redirectUrl);
					if (verboseTracing) Log.d(TAG, "mccmnc = " + mccmnc);
					if (verboseTracing) Log.d(TAG, "phase2Uri = " + phase2Uri);
		
					HttpClient httpClient = HttpUtils.getHttpClient(phase2Uri, consumerKey, consumerSecret);
		
					HttpGet httpRequest = new HttpGet(phase2Uri);
					httpRequest.addHeader("Accept", "application/json");
					HttpResponse httpResponse = httpClient.execute(httpRequest);
		
					HashMap<String, String> headerMap = HttpUtils.getHeaders(httpResponse);
		
					String contentType = headerMap.get("content-type");
		
					HttpEntity httpEntity = httpResponse.getEntity();
					InputStream is = httpEntity.getContent();
		
					errorResponse = DiscoveryProcessEndpoints.start(contentType, httpResponse, is, verboseTracing, subscriber_id);
				} else {
					errorResponse = JsonUtils.simpleError("Response Error",
							"Invalid mccmnc response" + mccmnc);
				}
			} else {
				errorResponse = JsonUtils.simpleError("Response Error",
						"Missing mccmnc response");
			}

		} catch (ClientProtocolException e) {
			errorResponse = JsonUtils.simpleError("ClientProtocolException",
					"ClientProtocolException - " + e.getMessage());
		} catch (IOException e) {
			errorResponse = JsonUtils.simpleError("IOException",
					"IOException - " + e.getMessage());
		}
		return errorResponse;
	}

}
