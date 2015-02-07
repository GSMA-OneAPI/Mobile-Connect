package com.gsma.android.mobileconnect.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;



/**
 * Helper functions for JSON management.
 */
public class JsonUtils {
	
	/**
	 * Convert JSON data to a Java object representing the JSON, uses the
	 * in-built Android libraries
	 * 
	 * @param content
	 * @param contentType
	 * @return Object
	 * @throws JSONException
	 */
	public static Object convertContent(String content, String contentType)
			throws JSONException {
		String trimmed = content != null ? content.trim() : null;
		Object result = null;

		if (contentType != null
				&& contentType.toLowerCase().startsWith("application/json")
				&& trimmed != null && trimmed.length() > 0) {
			JSONTokener tokener = new JSONTokener(trimmed);
			result = tokener.nextValue();
		}
		return result;
	}

	/**
	 * Creates a simple error object from an error and error_description field
	 * 
	 * @param error
	 * @param error_description
	 * @return JSONObject
	 * @throws JSONException
	 */
	public static JSONObject simpleError(String error, String error_description) {
		JSONObject container = new JSONObject();
		try {
			container.put("error", error);
		} catch (JSONException e) {}
		try {
			container.put("error_description", error_description);
		} catch (JSONException e) {}
		return container;
	}

	/**
	 * Creates a JSONObject from an InputStream.
	 * 
	 * @param inputstream
	 * @return JSONObject
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject readJSON(InputStream is) throws IOException, JSONException { 
		JSONObject response = null;
		StringBuffer buf = new StringBuffer();

		if (is != null) {
			final Reader in = new InputStreamReader(is, "UTF-8");
			
			char[] b=new char[1024];
			int n;
			while ((n=in.read(b)) != -1) {
				buf.append(b, 0, n);
			}
			
			in.close();
			
			response = new JSONObject(buf.toString());
		}
		
		return response;
	}

}
