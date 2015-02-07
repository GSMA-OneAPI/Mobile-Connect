package com.gsma.android.oneapi.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gsma.android.oneapi.utilsDiscovery.HttpUtils;
import com.gsma.android.oneapi.utilsDiscovery.JsonUtils;

/**
 * This is a background task which makes an initial connection to the discovery 
 * service - it will handle a variety of initial response types. Extends AsyncTask<Void, Void, JSONObject>
 */
public class DiscoveryTask extends AsyncTask<Void, Void, JSONObject> {
	private static final String TAG = "DiscoveryTask";

	String serviceUri; 
	String consumerKey;
	String consumerSecret; 
	String sourceIP;
	boolean usingMobileData;
	String msisdn;
	String identifiedmcc;
	String identifiedmnc;
	String selectedmcc;
	String selectedmnc;
	DiscoveryCallbackReceiver getJSONListener;
	String credentials;
	String redirectUrl;
	Context context;
	boolean followRedirect;
	boolean json;
	boolean allowAllSSL;
	boolean verboseTracing;

	/**
	 * Standard constructor
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param usingMobileData
	 * @param msisdn
	 * @param mcc
	 * @param mnc
	 * @param getJSONListener
	 * @param credentials (none, plain, sha256)
	 * @param redirectUri 
	 * @param context
	 * @param followRedirect
	 */
	public DiscoveryTask(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, boolean usingMobileData,
			String msisdn, String identifiedmcc, String identifiedmnc, String selectedmcc, String selectedmnc,
			DiscoveryCallbackReceiver getJSONListener, String credentials, String redirectUrl, Context context, 
			boolean followRedirect, boolean json, boolean allowAllSSL, boolean verboseTracing) {
		this.serviceUri = serviceUri;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.sourceIP = sourceIP;
		this.usingMobileData = usingMobileData;
		this.msisdn = msisdn;
		this.identifiedmcc = identifiedmcc;
		this.identifiedmnc = identifiedmnc;
		this.selectedmcc = selectedmcc;
		this.selectedmnc = selectedmnc;
		this.getJSONListener = getJSONListener;
		this.credentials = credentials;
		this.redirectUrl = redirectUrl;
		this.context = context;
		this.followRedirect = followRedirect;
		this.json = json;
		this.allowAllSSL = allowAllSSL;
		this.verboseTracing = verboseTracing;
	}

	/**
	 * The doInBackground function does the actual background processing.
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * @param params
	 * @return JSONObject
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	@Override
	protected JSONObject doInBackground(Void... params) {

		JSONObject response = null;
		String cookie = null;
		String phase1Uri=serviceUri;

		/*
		 * sets up the HTTP request with a redirect_uri parameter - in practice
		 * we're looking for mcc/mnc added to the redirect_uri if this step is
		 * necessary
		 */
		if (redirectUrl!=null && redirectUrl.trim().length() > 0){
			if (phase1Uri.indexOf("?")== -1) {
				phase1Uri = phase1Uri + "?Redirect_URL="+redirectUrl;
			} else {
				phase1Uri = phase1Uri + "&Redirect_URL="+redirectUrl;
			}
		}

		if (identifiedmcc!=null && identifiedmcc.trim().length()>0 && identifiedmnc!=null && identifiedmnc.trim().length()>0) {
			if (phase1Uri.indexOf("?")== -1) {
				phase1Uri = phase1Uri + "?Identified-MCC=" + identifiedmcc + "&Identified-MNC=" + identifiedmnc;
			} else {
				phase1Uri = phase1Uri + "&Identified-MCC=" + identifiedmcc + "&Identified-MNC=" + identifiedmnc;
			}
		}

		if (selectedmcc!=null && selectedmcc.trim().length()>0 && selectedmnc!=null && selectedmnc.trim().length()>0) {
			if (phase1Uri.indexOf("?")== -1) {
				phase1Uri = phase1Uri + "?Selected-MCC=" + selectedmcc + "&Selected-MNC=" + selectedmnc;
			} else {
				phase1Uri = phase1Uri + "&Selected-MCC=" + selectedmcc + "&Selected-MNC=" + selectedmnc;
			}
		}
		
		if(msisdn!=null && msisdn.trim().length() > 0) {
			if (phase1Uri.indexOf("?")== -1) {
				phase1Uri = phase1Uri + "?MSISDN=" + msisdn;
			} else {
				phase1Uri = phase1Uri + "&MSISDN=" + msisdn;
			}
		}
		
		if (verboseTracing) Log.d(TAG, "Started discovery process via " + phase1Uri);
		
		HttpGet httpRequest = new HttpGet(phase1Uri);

		if (json) {
			httpRequest.addHeader("Accept", "application/json");
			if (verboseTracing) Log.d(TAG, "Request application/json");
		} else {
			httpRequest.addHeader("Accept", "text/html");// msisdn and redirect cases
			if (verboseTracing) Log.d(TAG, "Request text/html");
		}

		if (sourceIP != null) {
			httpRequest.addHeader("X-Source-IP", sourceIP);
			if (verboseTracing)  Log.d(TAG, "Set X-Source-IP header to "+sourceIP);
		}

		try {
			
			if (allowAllSSL) {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("https", socketFactory, 443));
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			}

			DefaultHttpClient httpClient = HttpUtils.getHttpAuthorizationClient(phase1Uri, consumerKey, consumerSecret, credentials, httpRequest);
			HttpParams httpParams = httpRequest.getParams();
			httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS,Boolean.FALSE);
			httpRequest.setParams(httpParams);
			HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.RFC_2109);

			if (verboseTracing) Log.d(TAG, "Add consumerKey and consumerSecret; " + consumerKey
					+ " - " + consumerSecret);

			if (verboseTracing) Log.d(TAG, "Making " + httpRequest.getMethod() + " request to " + httpRequest.getURI());
			
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			
			if (verboseTracing) Log.d(TAG, "Request executed and completed with status=" + httpResponse.getStatusLine().getStatusCode());

			HashMap<String, String> headerMap = HttpUtils.getHeaders(httpResponse);
			
			if (verboseTracing) Log.d(TAG, "headerMap =" + headerMap);
			
			String contentType = headerMap.get("content-type");
			String location = headerMap.get("location");

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			
			if (verboseTracing) Log.d(TAG, "status=" + statusCode + " CT=" + contentType + " Loc="
					+ location + " JSON?" + HttpUtils.isJSON(contentType)
					+ " HTML?" + HttpUtils.isHTML(contentType));

			if (statusCode == HttpStatus.SC_OK) {

				if (HttpUtils.isJSON(contentType)) {

					HttpEntity httpEntity = httpResponse.getEntity();
					InputStream is = httpEntity.getContent();
					if (verboseTracing) Log.d(TAG, "Converting discovery data OK (JSON)");
					response = JsonUtils.readJSON(is);

				} else if (HttpUtils.isHTML(contentType)) {
					if (verboseTracing) Log.d(TAG,"Have OK HTML content - needs to be handled through the browser");
					// msisdn
				}

			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY && location != null) {
				if (verboseTracing) Log.d(TAG, "302 response " + location);
				
				String mcc_mnc=HttpUtils.getQueryParameterFromUrl(location, "mcc_mnc");
				String subscriber_id=HttpUtils.getQueryParameterFromUrl(location, "subscriber_id");

				if (mcc_mnc!=null) {
					if (verboseTracing) Log.d(TAG, "To try again with mcc_mnc = " + mcc_mnc);
					response = ProcessDiscoveryToken.start(mcc_mnc, consumerKey, serviceUri, verboseTracing, subscriber_id);
				} else {
					if (verboseTracing) Log.d(TAG, "Redirect requested to " + location);
					if(followRedirect){
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(location));
						String title = "Choose a browser";
						Intent chooser = Intent.createChooser(intent, title);
						context.startActivity(chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					}
					response = new JSONObject();
					response.put("Location", location);
				}
			} else if (statusCode == HttpStatus.SC_ACCEPTED) {

				if (verboseTracing) Log.d(TAG, "202 response ");
				
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream is = httpEntity.getContent();
				JSONObject jo = JsonUtils.readJSON(is);
				Link[] link = null;
				String href = "";
				if (jo != null) {
					JSONArray linkArray = jo.getJSONArray("links");
					if (linkArray != null) {
						link = new Link[linkArray.length()];
						for (int i = 0; i < linkArray.length(); i++) {
							link[i] = new Link(linkArray.getJSONObject(i));
						}
					}
				}
				if (link[0] != null)
					href = link[0].getHref();
				
				String mcc_mnc=HttpUtils.getQueryParameterFromUrl(href, "mcc_mnc");
				String subscriber_id=HttpUtils.getQueryParameterFromUrl(href, "subscriber_id");

				if (mcc_mnc!=null) {
					if (verboseTracing) Log.d(TAG, "To try again with mcc_mnc = " + mcc_mnc);
					response = ProcessDiscoveryToken.start(mcc_mnc, consumerKey, serviceUri, verboseTracing, subscriber_id);
				} else {
					if (verboseTracing) Log.d(TAG, "Redirect requested to " + href);
					if(followRedirect){
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(href));
						String title = "Choose a browser";
						Intent chooser = Intent.createChooser(intent, title);
						context.startActivity(chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					}
					response = new JSONObject();
					response.put("operatorSelection", href);
				}

			} else if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
				if (verboseTracing) Log.d(TAG, "Bad request response " + statusCode);

				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream is = httpEntity.getContent();
				String contents = HttpUtils.getContentsFromInputStream(is);

				if (HttpUtils.isJSON(contentType)) {
					if (verboseTracing) Log.d(TAG, "Bad request response " + contentType);
					Object rawJSON = JsonUtils.convertContent(contents, contentType);
					if (rawJSON != null && rawJSON instanceof JSONObject) {
						response = (JSONObject) rawJSON;
					}
				} else {
					if (verboseTracing) Log.d(TAG, "Bad request response " + contentType);
					response = JsonUtils.simpleError("HTTP " + statusCode, "HTTP " + statusCode);
				}

			}

		} catch (UnsupportedEncodingException e) {
			if (verboseTracing) Log.d(TAG, "UnsupportedEncodingException=" + e.getMessage());
			response = JsonUtils.simpleError("UnsupportedEncodingException",
					"UnsupportedEncodingException - " + e.getMessage());
		} catch (ClientProtocolException e) {
			if (verboseTracing) Log.d(TAG, "ClientProtocolException=" + e.getMessage());
			response = JsonUtils.simpleError("ClientProtocolException",
					"ClientProtocolException - " + e.getMessage());
		} catch (IOException e) {
			if (verboseTracing) Log.d(TAG, "IOException=" + e.getMessage());
			response = JsonUtils.simpleError("IOException", "IOException - "
					+ e.getMessage());
		} catch (JSONException e) {
			if (verboseTracing) Log.d(TAG, "JSONException=" + e.getMessage());
			response = JsonUtils.simpleError("JSONException",
					"JSONException - " + e.getMessage());
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
	protected void onPostExecute(JSONObject response) {
		if (verboseTracing) Log.d(TAG, "onPostExecute for " + response);
		getJSONListener.receiveDiscoveryData(response);
	}

}
