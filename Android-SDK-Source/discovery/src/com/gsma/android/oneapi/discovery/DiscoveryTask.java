package com.gsma.android.oneapi.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gsma.android.oneapi.utilsDiscovery.HttpUtils;
import com.gsma.android.oneapi.utilsDiscovery.JsonUtils;

/**
 * This is a background task which makes an initial connection to the discovery 
 * service - it will handle a variety of initial response types. Extends AsyncTask<Void, Void, JSONObject>
 */
@SuppressLint("SetJavaScriptEnabled")
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
	Activity activity;
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
			Activity activity,
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
		this.activity = activity;
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
//		String cookie = null;
		String phase1Uri=serviceUri;

		/*
		 * sets up the HTTP request with a redirect_uri parameter - in practice
		 * we're looking for mcc/mnc added to the redirect_uri if this step is
		 * necessary
		 */
		if (redirectUrl!=null && redirectUrl.trim().length() > 0){
			if (phase1Uri.indexOf("?")== -1) {
				phase1Uri = phase1Uri + "?Redirect_URL="+HttpUtils.encodeUriParameter(redirectUrl);
			} else {
				phase1Uri = phase1Uri + "&Redirect_URL="+HttpUtils.encodeUriParameter(redirectUrl);
			}
		}

		if (identifiedmcc!=null && identifiedmcc.trim().length()>0 && identifiedmnc!=null && identifiedmnc.trim().length()>0) {
			if (phase1Uri.indexOf("?")== -1) {
				phase1Uri = phase1Uri + "?Identified-MCC=" + HttpUtils.encodeUriParameter(identifiedmcc) + "&Identified-MNC=" + HttpUtils.encodeUriParameter(identifiedmnc);
			} else {
				phase1Uri = phase1Uri + "&Identified-MCC=" + HttpUtils.encodeUriParameter(identifiedmcc) + "&Identified-MNC=" + HttpUtils.encodeUriParameter(identifiedmnc);
			}
		}

		if (selectedmcc!=null && selectedmcc.trim().length()>0 && selectedmnc!=null && selectedmnc.trim().length()>0) {
			if (phase1Uri.indexOf("?")== -1) {
				phase1Uri = phase1Uri + "?Selected-MCC=" + HttpUtils.encodeUriParameter(selectedmcc) + "&Selected-MNC=" + HttpUtils.encodeUriParameter(selectedmnc);
			} else {
				phase1Uri = phase1Uri + "&Selected-MCC=" + HttpUtils.encodeUriParameter(selectedmcc) + "&Selected-MNC=" + HttpUtils.encodeUriParameter(selectedmnc);
			}
		}
		
		if (verboseTracing) Log.d(TAG, "Started discovery process via " + phase1Uri);
		
		HttpRequest httpRequest = null;
		if(msisdn!=null && msisdn.trim().length() > 0) {
			if (verboseTracing) Log.d(TAG, "Making POST request to " + phase1Uri);
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			BasicNameValuePair msisdnParams = new BasicNameValuePair("MSISDN", msisdn);
			postparams.add(msisdnParams);
			httpRequest = new HttpPost(phase1Uri);
			try {
				((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(postparams));
			} catch (UnsupportedEncodingException e) {
			}
			httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
		} else {
			if (verboseTracing) Log.d(TAG, "Making GET request to " + phase1Uri);
			httpRequest = new HttpGet(phase1Uri);
		}

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

			DefaultHttpClient httpClient = HttpUtils.getHttpAuthorizationClient(phase1Uri, consumerKey, consumerSecret, credentials, (HttpRequestBase) httpRequest);
			HttpParams httpParams = httpRequest.getParams();
			httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS,Boolean.FALSE);
			httpRequest.setParams(httpParams);
			HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.RFC_2109);

			if (verboseTracing) Log.d(TAG, "Add consumerKey and consumerSecret; " + consumerKey
					+ " - " + consumerSecret);

			HttpResponse httpResponse = httpClient.execute((HttpUriRequest) httpRequest);
			
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
				}

			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY && location != null) {
				if (verboseTracing) Log.d(TAG, "302 response " + location);
				
//				String mcc_mnc=HttpUtils.getQueryParameterFromUrl(location, "mcc_mnc");
//				String subscriber_id=HttpUtils.getQueryParameterFromUrl(location, "subscriber_id");
//
//				if (mcc_mnc!=null) {
//					if (verboseTracing) Log.d(TAG, "To try again with mcc_mnc = " + mcc_mnc);
//					response = ProcessDiscoveryToken.start(mcc_mnc, consumerKey, serviceUri, verboseTracing, subscriber_id);
//				} else {
					if (verboseTracing) Log.d(TAG, "Redirect requested to " + location);
					response = new JSONObject();
					if (followRedirect) {
						if (activity!=null) {
							Log.d(TAG, "Handling redirect to "+location+" in webview");
							response.put("USINGWEBVIEW", true);
							handleDiscoveryUIWebview(location, redirectUrl);
						} else {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(location));
							String title = "Choose a browser";
							Intent chooser = Intent.createChooser(intent, title);
							context.startActivity(chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
							response.put("Location", location);
						}
					} else {
						response.put("Location", location);
					}
//				}
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
				
//				String mcc_mnc=HttpUtils.getQueryParameterFromUrl(href, "mcc_mnc");
//				String subscriber_id=HttpUtils.getQueryParameterFromUrl(href, "subscriber_id");
//
//				if (mcc_mnc!=null) {
//					if (verboseTracing) Log.d(TAG, "To try again with mcc_mnc = " + mcc_mnc);
//					response = ProcessDiscoveryToken.start(mcc_mnc, consumerKey, serviceUri, verboseTracing, subscriber_id);
//				} else {
					if (verboseTracing) Log.d(TAG, "Redirect requested to " + href);
					response = new JSONObject();
					if (followRedirect) {
						if (activity!=null) {
							Log.d(TAG, "Handling redirect to "+href+" in webview");
							response.put("USINGWEBVIEW", true);
							handleDiscoveryUIWebview(href, redirectUrl);
						} else {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(href));
							String title = "Choose a browser";
							Intent chooser = Intent.createChooser(intent, title);
							context.startActivity(chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
							response.put("operatorSelection", href);
						}
					} else {
						response.put("operatorSelection", href);
					}
//				}

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
	
	@SuppressWarnings("unused")
	private void handleDiscoveryUIWebview(String requestUri, String redirectUri) {
		final String _requestUri=requestUri;
		final String _redirectUri=redirectUri;
		
		new Handler(
			Looper.getMainLooper()).post(
				new Runnable() {
					
			    @Override
			    public void run() {
					final WebView view = new WebView(context);

					LayoutParams fillParent=new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
					activity.addContentView(view, fillParent);
					
					view.setWebViewClient(new WebViewClient() {

						/*
						 * This is a stub - could be extended to handle error situations
						 * by returning to a relevant application screen
						 * 
						 * @see
						 * android.webkit.WebViewClient#onReceivedError(android.webkit
						 * .WebView, int, java.lang.String, java.lang.String)
						 */
						@Override
						public void onReceivedError(WebView view, int errorCode,
								String description, String failingUrl) {
							Log.d(TAG, "onReceivedError errorCode=" + errorCode
									+ " description=" + description + " failingUrl="
									+ failingUrl);
							
							view.setVisibility(View.INVISIBLE);
							view.destroy();
							
							JSONObject result = JsonUtils.simpleError("Content loading error",description);
							getJSONListener.receiveDiscoveryData(result);
						}
						
//						@Override
//						public void onLoadResource (WebView view, String url) {
//							Log.d(TAG, "loading resource "+url);
//						}

						/*
						 * The onPageStarted method is called whenever the WebView
						 * starts to load a new page - by examining the url for a
						 * discovery token we can extract this and move to the next
						 * stage of the process
						 * 
						 * @see
						 * android.webkit.WebViewClient#onPageStarted(android.webkit
						 * .WebView, java.lang.String, android.graphics.Bitmap)
						 */
						@Override
						public void onPageStarted(WebView view, String url,
								Bitmap favicon) {
							Log.d(TAG, "onPageStarted url=" + url);
							/*
							 * Check to see if the url contains the discovery token
							 * identifier - it could be a url parameter or a page
							 * fragment. The following checks and string manipulations
							 * retrieve the actual discovery token
							 */
							
							if (url != null && url.startsWith(_redirectUri)) {
								Log.d(TAG, "intercepted redirectUri "+_redirectUri);
								
								String mcc_mnc=HttpUtils.getQueryParameterFromUrl(url, "mcc_mnc");
								String subscriber_id=HttpUtils.getQueryParameterFromUrl(url, "subscriber_id");
								
								Log.d(TAG, "mcc_mnc = "+mcc_mnc);
								Log.d(TAG, "subscriber_id = "+subscriber_id);
								
								if ((mcc_mnc!=null && mcc_mnc.trim().length()>0)) {
									JSONObject result = ProcessDiscoveryToken.start(mcc_mnc, consumerKey, consumerSecret, 
																					serviceUri, verboseTracing, subscriber_id, _redirectUri);
									getJSONListener.receiveDiscoveryData(result);
								} else {
									JSONObject result = JsonUtils.simpleError("Discovery error","mcc_mnc not provided");
									getJSONListener.receiveDiscoveryData(result);
								}
								
								view.stopLoading();
								view.setVisibility(View.INVISIBLE);
								view.destroy();
							}
						}

					});
					
					/*
					 * enable JavaScript - the discovery web pages are enhanced with
					 * JavaScript
					 */
					WebSettings settings = view.getSettings();
					settings.setJavaScriptEnabled(true);
					settings.setSupportMultipleWindows(false);
					settings.setDomStorageEnabled(true);
					settings.setDatabaseEnabled(true);
					String databasePath = context.getDir("database", Context.MODE_PRIVATE).getPath(); 
				    settings.setDatabasePath(databasePath);

					/*
					 * load the specified URI along with the authorization header
					 */
					HashMap<String, String> extraheaders = new HashMap<String, String>();
					view.loadUrl(_requestUri, extraheaders);
					view.requestFocus(View.FOCUS_DOWN);
			    }
		
		});
		
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
//		try {
			if (response==null || !response.has("USINGWEBVIEW")) {
				if (verboseTracing) Log.d(TAG, "onPostExecute for " + response);
				getJSONListener.receiveDiscoveryData(response);
			}
//		} catch (JSONException e) {
//		}
	}

}
