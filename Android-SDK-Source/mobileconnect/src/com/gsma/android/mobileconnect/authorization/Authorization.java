package com.gsma.android.mobileconnect.authorization;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gsma.android.mobileconnect.utils.HttpUtils;
import com.gsma.android.mobileconnect.utils.ParameterList;
import com.gsma.android.mobileconnect.values.Prompt;
import com.gsma.android.mobileconnect.values.ResponseType;

/**
 * Main class with library methods.
 */
public class Authorization {
	private static final String TAG = "Authorization";
	
	/**
	 * Constructor
	 */
	public Authorization() {
	}

	/**
	 * Handles the process between the MNO and the end user for the end user to
	 * sign in/ authorize the application. The application hands over to the
	 * browser during the authorization step. On completion the MNO redirects to
	 * the application sending the completion information as URL parameters.
	 * 
	 * @param authUri
	 *            authorization endpoint.
	 * @param responseType
	 *            (CODE).
	 * @param clientId
	 *            which is an ID for the application.
	 * @param clientSecret
	 *            which is an secret for the application.
	 * @param scopes
	 *            which is an application specified string value.
	 * @param redirectUri
	 *            which is the return point after the user has
	 *            authenticated/consented.
	 * @param state
	 *            which is application specified.
	 * @param nonce
	 *            which is application specified.
	 * @param prompt
	 *            (NONE, LOGIN, CONSENT, SELECT_ACCOUNT).
	 * @param maxAge
	 *            which is an integer value.
	 * @param acrValues
	 *            which is an application specified.
	 * @param authorizationOptions
	 *            structure in which you can provide values for any of the
	 *            optional parameters in the authorization request: Display,
	 *            Ui_locales, Claims_locales, Id_token_hint, Login_hint and
	 *            Dtbs.
	 * @param listener It is necessary to implement authorizationCodeResponse(String state, String authorizationCode, String error, String clientId, String clientSecret, String scopes, String redirectUri)
	 * and authorizationError(String reason) to manage the response
	 * @param activity
	 * @throws NullPointerException
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void authorize(String authUri, ResponseType responseType, String clientId, String clientSecret, String scopes,
			String redirectUri, String state, String nonce, Prompt prompt,
			int maxAge, String acrValues,
			AuthorizationOptions authorizationOptions, AuthorizationListener listener, Activity activity) {
		try{
			HashMap<String, Object> hmapExtraOptions = null;
			if(authorizationOptions!=null)
				hmapExtraOptions = authorizationOptions.getAuthorizationOptions();
	
			Log.d(TAG, "authUri = " + authUri);
			Log.d(TAG, "responseType = " + responseType.value());
			Log.d(TAG, "clientId = " + clientId);
			Log.d(TAG, "scopes = " + scopes);
			Log.d(TAG, "returnUri = " + redirectUri);
			Log.d(TAG, "state = " + state);
			Log.d(TAG, "nonce = " + nonce);
			Log.d(TAG, "prompt = " + prompt.value());
			Log.d(TAG, "maxAge = " + maxAge);
			Log.d(TAG, "acrValues = " + acrValues);
			if (hmapExtraOptions != null && hmapExtraOptions.size() > 0) {
				Log.d(TAG, "authorizationOptions keys: "
						+ authorizationOptions.getAuthorizationOptions().keySet());
				Log.d(TAG, "authorizationOptions values: "
						+ authorizationOptions.getAuthorizationOptions().values());
			} else
				Log.d(TAG, "authorizationOptions empty");
	
			if(authUri==null)
				authUri = "";
			String requestUri = authUri;
			if (authUri.indexOf("?") == -1) {
				requestUri += "?";
			} else if (authUri.indexOf("&") == -1) {
				requestUri += "&";
			}
	
			requestUri += "response_type=" + HttpUtils.encodeUriParameter(responseType.value());
			requestUri += "&client_id=" + HttpUtils.encodeUriParameter(clientId);
			requestUri += "&scope=" + HttpUtils.encodeUriParameter(scopes);
			requestUri += "&redirect_uri="
					+ HttpUtils.encodeUriParameter(redirectUri);
			requestUri += "&state=" + HttpUtils.encodeUriParameter(state);
			requestUri += "&nonce=" + HttpUtils.encodeUriParameter(nonce);
			requestUri += "&prompt=" + HttpUtils.encodeUriParameter(prompt.value());
			requestUri += "&max_age="
					+ HttpUtils.encodeUriParameter(Integer.toString(maxAge));
			requestUri += "&acr_values=" + HttpUtils.encodeUriParameter(acrValues);
			if (hmapExtraOptions != null && hmapExtraOptions.size() > 0) {
				for (String key : hmapExtraOptions.keySet()) {
					requestUri += "&"
							+ key
							+ "="
							+ HttpUtils.encodeUriParameter(hmapExtraOptions
									.get(key).toString());
				}
			}
			
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setData(Uri.parse(requestUri));
//			String title = "Choose a browser";
//			Intent chooser = Intent.createChooser(intent, title);
//			context.startActivity(chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			
			final AuthorizationListener _listener=listener;
			final String _redirectUri=redirectUri;
			final String _state=state;
			final String _clientId=clientId;
			final String _clientSecret=clientSecret;
			final String _scopes=scopes;
			
			final WebView view = new WebView(activity);

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
					_listener.authorizationError("Content loading error : "+description);
				}

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
						Log.d(TAG, "intercepted return");
						
						ParameterList parameters=ParameterList.getKeyValuesFromUrl(url, 0);
						
						String state=parameters.getValue("state");
						String code=parameters.getValue("code");
						String error=parameters.getValue("error");

						Log.d(TAG, "state = "+state);
						Log.d(TAG, "code = "+code);
						Log.d(TAG, "error = "+error);
						
						if ((code!=null && code.trim().length()>0) && (error==null || error.trim().length()==0) && _state.equalsIgnoreCase(state)) {

							view.stopLoading();
							
							/*String state, String authorizationCode, String error, String clientId, String clientSecret, String scopes, String returnUri*/
							_listener.authorizationCodeResponse(state, code, error, _clientId, _clientSecret, _scopes, _redirectUri);

//							Intent intent = new Intent(
//									initiator,
//									AuthorizationCompleteActivity.class);
//							intent.putExtra("state", state);
//							intent.putExtra("code", code);
//							intent.putExtra("error", error);
//							intent.putExtra("authUri", authUri);
//							intent.putExtra("tokenUri", tokenUri);
//							intent.putExtra("userinfoUri", userinfoUri);
//							intent.putExtra("clientId", clientId);
//							intent.putExtra("clientSecret", clientSecret);
//							intent.putExtra("scopes", scopes);
//							intent.putExtra("returnUri", returnUri);
//							
//							startActivity(intent);
						} else {
							_listener.authorizationError("Invalid authorization code response");
						}
						
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
			String databasePath = activity.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath(); 
		    settings.setDatabasePath(databasePath);

			/*
			 * load the specified URI along with the authorization header
			 */
			HashMap<String, String> extraheaders = new HashMap<String, String>();
			view.loadUrl(requestUri, extraheaders);
			view.requestFocus(View.FOCUS_DOWN);
			
		} catch (NullPointerException e){
			Log.d(TAG, "NullPointerException=" + e.getMessage());
		}
	}
	
	/**
	 * Helper function developed to parse the redirect sent by the authorization
	 * server to the application and extract code, state and error values.
	 * 
	 * @param returnUri
	 *            which is the return point after the user has
	 *            authenticated/consented.
	 * @return ParameterList
	 */
	public ParameterList extractRedirectParameter(String returnUri) {
		ParameterList parameters = null;
		
		if (returnUri != null && returnUri.trim().length() > 0) {
			Log.d(TAG, "intercepted return");
			
			parameters=ParameterList.getKeyValuesFromUrl(returnUri, 0);
		}
		
		return parameters;
	}

}
