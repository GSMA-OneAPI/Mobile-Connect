package com.gsma.android.mobileconnect.userinfo;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.gsma.android.mobileconnect.utils.HttpUtils;
import com.gsma.android.mobileconnect.utils.JsonUtils;

import android.os.AsyncTask;
import android.util.Log;

/**
 * this is a background task which initiates the identity process using OpenID Connect
 */
public class RetrieveUserinfoTask extends AsyncTask<Void, Void, JSONObject> {
	private static final String TAG = "RetrieveUserinfoTask";

	String userinfoUri; 
	String accessToken; 
//	String redirectUri; 
	
	OpenIDConnectCallbackUserinfoReciever getJSONListener;
	
	/*
	 * standard constructor - receives information from Userinfo.java
	 */
	public RetrieveUserinfoTask(String userinfoUri, String accessToken, OpenIDConnectCallbackUserinfoReciever getJSONListener) {
		this.userinfoUri = userinfoUri;
		this.accessToken = accessToken;
		this.getJSONListener = getJSONListener;
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		JSONObject json=null;
				
		Log.d(TAG, "Pausing a short while");

		Log.d(TAG, "requestUri="+userinfoUri);
		
		HttpGet httpRequest = new HttpGet(userinfoUri);
				
		httpRequest.addHeader("Accept", "application/json");
		httpRequest.addHeader("Authorization", "Bearer "+accessToken);

		HttpClient httpClient = HttpUtils.getHttpClient();
		HttpParams httpParams = httpRequest.getParams();
		httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS,Boolean.TRUE);
		httpRequest.setParams(httpParams);

		try {
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			
			Log.d(TAG, "Request completed with status="+httpResponse.getStatusLine().getStatusCode());
	
			/*
			 * obtain the headers from the httpResponse. Content-Type and
			 * Location are particularly required
			 */
			HashMap<String, String> headerMap = HttpUtils
					.getHeaders(httpResponse);
			String contentType = headerMap.get("content-type");
			String location = headerMap.get("location");
	
			/*
			 * the status code from the HTTP response is also needed in
			 * processing
			 */
			int statusCode = httpResponse.getStatusLine().getStatusCode();
	
			Log.d(TAG, "status=" + statusCode + " CT=" + contentType + " Loc="
					+ location + " JSON?" + HttpUtils.isJSON(contentType)
					+ " HTML?" + HttpUtils.isHTML(contentType));
			
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			
			json=JsonUtils.readJSON(is);
			
		} catch (java.io.IOException ioe) {
			Log.e(TAG, "IOException "+ioe.getMessage());
			json=new JSONObject();
			try {
				json.put("Exception", "IOException");
				json.put("Message", ioe.getMessage());
			} catch (JSONException e) {
			}
		} catch (JSONException je) {
			Log.e(TAG, "JSONException "+je.getMessage());
			json=new JSONObject();
			try {
				json.put("Exception", "JSONException");
				json.put("Message", je.getMessage());
			} catch (JSONException e) {
			}
		}
		
		return json;
	}
	
	@Override
	protected void onPostExecute(JSONObject response) {
		Log.d(TAG, "onPostExecute for " + response);
		getJSONListener.processUserinfoResponse(response);
	}

}
