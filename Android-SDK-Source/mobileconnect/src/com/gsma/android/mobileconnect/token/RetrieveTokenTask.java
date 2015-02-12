package com.gsma.android.mobileconnect.token;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.message.BasicNameValuePair;
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
public class RetrieveTokenTask extends AsyncTask<Void, Void, JSONObject> {
	private static final String TAG = "RetrieveTokenTask";

	String tokenUri;
	String clientId;
	String clientSecret;
	String infoType;
	String redirectUri;
	String scope;
	String type;

	OpenIDConnectCallbackTokenReceiver getJSONListener;

	/*
	 * standard constructor - receives information from Token.java
	 */
	public RetrieveTokenTask(String tokenUri, String infoType, String clientId,
			String clientSecret, String redirectUri, String scope, String type, OpenIDConnectCallbackTokenReceiver getJSONListener) {
		this.tokenUri = tokenUri;
		this.infoType = infoType;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		this.scope=scope;
		this.type=type;
		this.getJSONListener = getJSONListener;
	}

	@Override
	protected JSONObject doInBackground(Void... params) {
		JSONObject json = null;

		Log.d(TAG, "requestUri=" + tokenUri);

		HttpPost httpRequest = new HttpPost(tokenUri);

		List<NameValuePair> postparams = new ArrayList<NameValuePair>();
		BasicNameValuePair infoParams;
		if(type.equals("token"))
			infoParams = new BasicNameValuePair("code", infoType);
		else if(type.equals("refresh"))
			infoParams = new BasicNameValuePair("refresh_token", infoType);
		else
			infoParams = new BasicNameValuePair("access_token", infoType);
//		BasicNameValuePair codeParams = new BasicNameValuePair("code", code);
//		BasicNameValuePair clientIdParams = new BasicNameValuePair("client_id",
//				clientId);
		BasicNameValuePair grantTypeParams;
		if(type.equals("token"))
			grantTypeParams = new BasicNameValuePair("grant_type", "authorization_code");
		else if(type.equals("refresh"))
			grantTypeParams = new BasicNameValuePair("grant_type", "refresh_token");
		else
			grantTypeParams = new BasicNameValuePair("grant_type", "access_token");
		BasicNameValuePair redirectUriParams=null;
		if(type.equals("token"))
			redirectUriParams = new BasicNameValuePair("redirect_uri", redirectUri);
		BasicNameValuePair scopeParams=null;
		if(type.equals("refresh"))
			scopeParams = new BasicNameValuePair("scope", scope);
		postparams.add(infoParams);
		postparams.add(grantTypeParams);
		if(redirectUriParams!=null)
			postparams.add(redirectUriParams);
		if(scopeParams!=null)
			postparams.add(scopeParams);

//		for (NameValuePair nvp:postparams) {
//			Log.d(TAG, "Request param "+nvp.getName()+" = "+nvp.getValue());
//		}
		
		/* add the post parameters as the request body */
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(postparams));
//			Log.d(TAG, "submitted entity "+httpRequest.getEntity().toString());
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
		}

		httpRequest.addHeader("Accept", "application/json");
		httpRequest.addHeader("Content-Type",
				"application/x-www-form-urlencoded");
		
		HttpClient httpClient = HttpUtils.getHttpAuthorizationClient(tokenUri, clientId, clientSecret, "plain", httpRequest);
		HttpParams httpParams = httpRequest.getParams();
		httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
		httpRequest.setParams(httpParams);

		try {
			HttpResponse httpResponse = httpClient.execute(httpRequest);

			Log.d(TAG, "Request completed with status="
					+ httpResponse.getStatusLine().getStatusCode());

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

			json = JsonUtils.readJSON(is);
		} catch (java.io.IOException ioe) {
			Log.e(TAG, "IOException " + ioe.getMessage());
			json = new JSONObject();
			try {
				json.put("Exception", "IOException");
				json.put("Message", ioe.getMessage());
			} catch (JSONException e) {
			}
		} catch (JSONException je) {
			Log.e(TAG, "JSONException " + je.getMessage());
			json = new JSONObject();
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
		getJSONListener.processTokenResponse(response);
	}

}
