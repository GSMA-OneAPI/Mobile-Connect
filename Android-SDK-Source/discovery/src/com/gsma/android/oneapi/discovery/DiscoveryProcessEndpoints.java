package com.gsma.android.oneapi.discovery;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.gsma.android.oneapi.utilsDiscovery.HttpUtils;
import com.gsma.android.oneapi.utilsDiscovery.JsonUtils;

import android.util.Log;

/**
 * Makes a connection to the discovery service. 
 */
public class DiscoveryProcessEndpoints {

	private static final String TAG = "DiscoveryProcessEndpoints";

	/**
	 * Constructor 
	 * @param contentType
	 * @param httpResponse
	 * @param inputStream
	 * @param subscriber_id
	 * @return JSONObject
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject start(String contentType, HttpResponse httpResponse, InputStream inputStream, boolean verboseTracing, String subscriber_id) {

		JSONObject endpointResponse = new JSONObject();

		try {
			String contents = HttpUtils.getContentsFromInputStream(inputStream);

			if (verboseTracing) Log.d(TAG, "Read " + contents);

			if (contentType != null && contentType.toLowerCase().startsWith("application/json")) {
				if (verboseTracing) Log.d(TAG, "Read JSON content");

				Object rawJSON = JsonUtils.convertContent(contents, contentType);
				if (rawJSON != null) {
					if (verboseTracing) Log.d(TAG, "Have read the json data");

					if (rawJSON instanceof JSONObject) {
						JSONObject json = (JSONObject) rawJSON;
						endpointResponse = json;
					}
				}
			}

		} catch (IOException e) {
			endpointResponse = JsonUtils.simpleError("IOException",
					"IOException - " + e.getMessage());
		} catch (JSONException e) {
			endpointResponse = JsonUtils.simpleError("JSONException",
					"JSONException - " + e.getMessage());
		}
		
		/*
		 * In case subscriber_id is available
		 */
		if (subscriber_id!=null && endpointResponse!=null && !endpointResponse.has("subscriber_id")) {
			try {
				endpointResponse.put("subscriber_id", subscriber_id);
			} catch (JSONException e) {
			}
		}

		return endpointResponse;
	}

}
