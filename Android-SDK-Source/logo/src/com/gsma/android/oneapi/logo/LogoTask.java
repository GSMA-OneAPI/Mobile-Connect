package com.gsma.android.oneapi.logo;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.gsma.android.oneapi.utilsLogo.CookieManagement;
import com.gsma.android.oneapi.utilsLogo.HttpUtils;
import com.gsma.android.oneapi.utilsLogo.JsonUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This is a background task which makes an initial connection to the logo 
 * service - it will handle a variety of initial response types. Extends AsyncTask<Void, Void, Object>
 */
public class LogoTask extends AsyncTask<Void, Void, Object> {
	private static final String TAG = "LogoLoaderTask";

	String serviceUri; 
	String consumerKey; 
	String consumerSecret; 
	String sourceIP;
	String mcc; 
	String mnc;
	String logosize;
	String color;
	String ratio;
	LogoCallbackReceiver getJSONListener;
	Context context;

	/**
	 * Standard constructor.
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param mcc
	 * @param mnc
	 * @param logosize (small, medium, large)
	 * @param color (normal, black, reversed)
	 * @param ratio (landscape, square)
	 * @param getJSONListener
	 * @param context
	 */
	public LogoTask(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String mcc, String mnc,
			String logosize, String color, String ratio, LogoCallbackReceiver getJSONListener,
			Context context) {
		this.serviceUri = serviceUri;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.sourceIP = sourceIP;
		this.mcc = mcc;
		this.mnc = mnc;
		this.logosize = logosize;
		this.color = color;
		this.ratio = ratio;
		this.getJSONListener = getJSONListener;
		this.context = context;
	}

	/**
	 * The doInBackground function does the actual background processing.
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * @param params
	 * @return Object
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws NullPointerException
	 */
	@Override
	protected Object doInBackground(Void... params) {
		
		Object response = null;
		String cookie = null;

		/*
		 * sets up the HTTP request with a redirect_uri parameter - in practice
		 * we're looking for mcc/mnc added to the redirect_uri if this step is
		 * necessary
		 */
		String requestUri = serviceUri;
		if(logosize != null && logosize.trim().length() > 0)
			requestUri = HttpUtils.addUriParameter(requestUri, "logosize", logosize);
		if(color != null && color.trim().length() > 0)
			requestUri = HttpUtils.addUriParameter(requestUri, "bg_color", color);
		if(ratio != null && ratio.trim().length() > 0)
			requestUri = HttpUtils.addUriParameter(requestUri, "aspect_ratio", ratio);

		if (mcc != null && mcc.trim().length() > 0 && mnc != null && mnc.trim().length() > 0) {
			requestUri = requestUri + "&mcc_mnc=" + mcc + "_" + mnc;
		} else if ((cookie = CookieManagement.getCookie("mcc_mnc")) != null) {
			requestUri = requestUri + "&mcc_mnc=" + cookie;
		} else {
			requestUri = requestUri + "&mcc_mnc=_";
		}
		
		Log.d(TAG, "Started logo request via " + requestUri);

		HttpGet httpRequest = new HttpGet(requestUri);

		httpRequest.addHeader("Accept", "application/json");
		
		Log.d(TAG, "Add application/json");

		if (sourceIP != null) {
			httpRequest.addHeader("x-source-ip", sourceIP);
			Log.d(TAG, "Add x-source-ip " + sourceIP);
		}

		try {
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			SchemeRegistry registry = new SchemeRegistry();
			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			registry.register(new Scheme("https", socketFactory, 443));
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			HttpClient httpClient = HttpUtils.getHttpClient();
			HttpParams httpParams = httpRequest.getParams();
			httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
			httpRequest.setParams(httpParams);

			Log.d(TAG, "Making " + httpRequest.getMethod() + " request to " + httpRequest.getURI());
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 1);
			if (mcc != null && mcc.trim().length() > 0 && mnc != null && mnc.trim().length() > 0)
				CookieManagement.addCookieExpireOneDay("mcc_mnc", mcc + "_" + mnc);
			CookieManagement.updateCookieStore(httpClient);
			HttpResponse httpResponse = httpClient.execute(httpRequest);

			Log.d(TAG, "Request completed with status=" + httpResponse.getStatusLine().getStatusCode());

			HashMap<String, String> headerMap = HttpUtils.getHeaders(httpResponse);

			Log.d(TAG, "headerMap =" + headerMap);

			String contentType = headerMap.get("content-type");
			String location = headerMap.get("location");

			int statusCode = httpResponse.getStatusLine().getStatusCode();

			Log.d(TAG, "status=" + statusCode + " CT=" + contentType + " Loc="
					+ location + " JSON?" + HttpUtils.isJSON(contentType)
					+ " HTML?" + HttpUtils.isHTML(contentType));

			if (statusCode == HttpStatus.SC_OK) {
				if (HttpUtils.isJSON(contentType)) {

					String logoResponse = HttpUtils.getContentsFromHttpResponse(httpResponse);
					Log.d(TAG, "Converting JSON logo data " + logoResponse);
					response = JsonUtils.convertContent(logoResponse,contentType);
					LogoItemArray li = new LogoItemArray(response);
					if (li != null && li.getLogos() != null) {
						for (int i = 0; i < li.getLogos().length; i++) {
							Log.d(TAG,"URL["+i+"] = "+li.getLogos()[i].getUrl());
							LogoCache.addLogoResponse(li.getLogos()[i], context);
						}
					}

				}
			} else if (statusCode >= HttpStatus.SC_BAD_REQUEST) {

				Log.d(TAG, "Error " + statusCode);
				
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream is = httpEntity.getContent();
				String contents = HttpUtils.getContentsFromInputStream(is);

				if (HttpUtils.isJSON(contentType)) {
					Object rawJSON = JsonUtils.convertContent(contents,contentType);
					if (rawJSON != null && rawJSON instanceof JSONObject) {
						response = (JSONObject) rawJSON;
					}
				} else {
					response = JsonUtils.simpleError("HTTP " + statusCode,"HTTP " + statusCode);
				}

			}

		} catch (UnsupportedEncodingException e) {
			Log.d(TAG, "UnsupportedEncodingException=" + e.getMessage());
			response = JsonUtils.simpleError("UnsupportedEncodingException",
					"UnsupportedEncodingException - " + e.getMessage());
		} catch (ClientProtocolException e) {
			Log.d(TAG, "ClientProtocolException=" + e.getMessage());
			response = JsonUtils.simpleError("ClientProtocolException",
					"ClientProtocolException - " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "IOException=" + e.getMessage());
			response = JsonUtils.simpleError("IOException", "IOException - "
					+ e.getMessage());
		} catch (JSONException e) {
			Log.d(TAG, "JSONException=" + e.getMessage());
			response = JsonUtils.simpleError("JSONException",
					"JSONException - " + e.getMessage());
		} catch (NullPointerException e) {
			Log.d(TAG, "NullPointerException=" + e.getMessage());
			response = JsonUtils.simpleError("NullPointerException",
					"NullPointerException - " + e.getMessage());
		}

		return response;
	}

	/**
	 * On completion of this background task either this task has started the
	 * next part of the process or an error has occurred.
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 * 
	 * @param response
	 */
	@Override
	protected void onPostExecute(Object response) {
		Log.d(TAG, "onPostExecute for " + response);
		getJSONListener.receiveLogoData(response);
	}
}
