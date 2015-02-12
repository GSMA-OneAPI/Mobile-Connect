package com.gsma.android.xoperatorapidemo.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gsma.android.mobileconnect.token.Token;
import com.gsma.android.mobileconnect.token.TokenData;
import com.gsma.android.mobileconnect.token.TokenListener;
import com.gsma.android.mobileconnect.userinfo.Userinfo;
import com.gsma.android.mobileconnect.userinfo.UserinfoData;
import com.gsma.android.mobileconnect.userinfo.UserinfoListener;
import com.gsma.android.mobileconnectsdktest.R;
import com.gsma.android.oneapi.discovery.DiscoveryItem;
import com.gsma.android.oneapi.discovery.DiscoveryProvider;

/*
 * initiate the process of sign-in using the OperatorID API. 
 * the sign-in process is based on the user accessing the operator portal
 * through a browser. It is based on OpenID Connect
 * 
 * details on using an external browser are not finalised therefore at the moment
 * this uses a WebView
 */
public class AuthorizationCompleteActivity extends Activity implements TokenListener, UserinfoListener {
	private static final String TAG = "AuthorizationCompleteActivity";

	AuthorizationCompleteActivity authorizationCompleteActivityInstance; // saved copy of this instance -
	// needed when sending an intent
	
	String authUri = null;
	String tokenUri = null;
	String userinfoUri = null;
	String clientId = null;
	String clientSecret = null;
	String scopes = null;
	String returnUri = null;
	String state = null;
	String code = null;
	String error = null;

	TextView statusField = null;

	TextView authorizationCompleteEmailValue = null;
	TextView authorizationCompleteSubValue = null;
	TextView authorizationCompleteNameValue = null;
	TextView authorizationCompleteGenderValue = null;
	TextView authorizationCompleteLocaleValue = null;
	
	boolean setEmail=false;

	private static final String RETRIEVING ="retrieving ...";
	private static final String NA ="not available";
	
	/*
	 * method called when this activity is created - handles the receiving of
	 * endpoint parameters and setting up the WebView
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		authorizationCompleteActivityInstance = this;
		setContentView(R.layout.activity_identity_authorization_complete);
		
		statusField = (TextView) findViewById(R.id.authorizationCompleteStatus);

		authorizationCompleteEmailValue = (TextView) findViewById(R.id.authorizationCompleteEmailValue);
		authorizationCompleteSubValue = (TextView) findViewById(R.id.authorizationCompleteSubValue);
		authorizationCompleteNameValue = (TextView) findViewById(R.id.authorizationCompleteNameValue);
		authorizationCompleteGenderValue = (TextView) findViewById(R.id.authorizationCompleteGenderValue);
		authorizationCompleteLocaleValue = (TextView) findViewById(R.id.authorizationCompleteLocaleValue);

	}

	/*
	 * when this activity starts
	 * 
	 * @see android.app.Activity#onStart()
	 */
	public void onStart() {
		super.onStart();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			/*
			 * Extract the parameters from the bundle provided
			 */
			authUri = extras.getString("authUri");
			tokenUri = extras.getString("tokenUri");
			userinfoUri = extras.getString("userinfoUri");
//			clientId = extras.getString("clientId");
//			clientSecret = extras.getString("clientSecret");
			scopes = extras.getString("scopes");
			returnUri = extras.getString("returnUri");
			state = extras.getString("state");
			code = extras.getString("code");
			error = extras.getString("error");
			
			DiscoveryProvider discoveryProvider=new DiscoveryProvider();
			DiscoveryItem discoveryData=discoveryProvider.getCacheDiscoveryItem(this);
			String clientId=discoveryData.getResponse().getClient_id();
			String clientSecret=discoveryData.getResponse().getClient_secret();

			Log.d(TAG, "handling code="+code+" error="+error);
			
			String statusDescription="unknown";
			boolean authorized=false;
			if (code!=null && code.trim().length()>0) {
				statusDescription="authorized";
				authorized=true;
			} else if (error!=null && error.trim().length()>0) {
				statusDescription="not authorized";
			} 
				
			statusField.setText(statusDescription);
			
			if (authorized) {
				authorizationCompleteEmailValue.setText(RETRIEVING);
				authorizationCompleteSubValue.setText(RETRIEVING);
				authorizationCompleteNameValue.setText(RETRIEVING);
				authorizationCompleteGenderValue.setText(RETRIEVING);
				authorizationCompleteLocaleValue.setText(RETRIEVING);

				Log.d(TAG, "submitting request for token");
				Log.d(TAG, "clientId="+clientId);
				Log.d(TAG, "clientSecret="+clientSecret);
				Token tokenProcessor=new Token();
				tokenProcessor.tokenFromAuthorizationCode(tokenUri, code,
						clientId, clientSecret, returnUri, this);

			} else {
				authorizationCompleteEmailValue.setText("not available");
			}
			
 		}
	}

	/*
	 * go back to the main screen
	 */
	public void home(View view) {
		Intent intent = new Intent(authorizationCompleteActivityInstance, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void tokenResponse(TokenData response) {
		Log.d(TAG, "received token response");
		String access_token=response.getAccess_Token();
		boolean haveAccessToken=false;
		if (access_token!=null && access_token.trim().length()>0) {
			statusField.setText("retrieved access token");
			haveAccessToken=true;
		} else {
			statusField.setText("access token not received");
		}
		String id_token = response.getId_Token();
		if (id_token!=null && id_token.trim().length()>0) {
			String[] id_token_parts=id_token.split("\\.");
			if (id_token_parts!=null && id_token_parts.length>=2) {
				String idValue=id_token_parts[1];
				byte[] decoded=Base64.decode(idValue, Base64.DEFAULT);
				String dec=new String(decoded);
				Log.d(TAG, "decoded to "+dec);
				try {
					JSONObject json=new JSONObject(dec);
					String email=json.has("email")?json.getString("email"):null;
					if (email!=null && email.trim().length()>0) {
						authorizationCompleteEmailValue.setText(email);
						setEmail=true;
					}
					String sub=json.has("sub")?json.getString("sub"):null;
					if (sub!=null && sub.trim().length()>0) {
						authorizationCompleteSubValue.setText(sub);
					}
				} catch (JSONException e) {
				}
			}					
		}
		if (haveAccessToken && userinfoUri!=null) {
			Userinfo userinfo=new Userinfo();
			userinfo.userinfo(userinfoUri, access_token, this);
		}
	}

	@Override
	public void errorToken(JSONObject error) {
		Log.d(TAG, "token error");
		statusField.setText("error retrieving access token");
	}

	@Override
	public void userinfoResponse(UserinfoData response) {
		Log.d(TAG, "processs userinfo");
		if (response!=null) {
			statusField.setText("retrieved userinfo");
			String email=response.getEmail();
			String name=response.getName();
			String locale=response.getLocale();
			String gender=response.getGender();
			
			Log.d(TAG, "email = "+email);
			Log.d(TAG, "name = "+name);
			Log.d(TAG, "locale = "+locale);
			Log.d(TAG, "gender = "+gender);
			
			if (email!=null) {
				authorizationCompleteEmailValue.setText(email);
			} else if (!setEmail) {
				authorizationCompleteEmailValue.setText(NA);
			}
			if (name!=null) {
				authorizationCompleteNameValue.setText(name);	
			} else {
				authorizationCompleteNameValue.setText(NA);
			}
			if (locale!=null) {
				authorizationCompleteLocaleValue.setText(locale);	
			} else {
				authorizationCompleteLocaleValue.setText(NA);
			}
			if (gender!=null) {
				authorizationCompleteGenderValue.setText(gender);	
			} else {
				authorizationCompleteGenderValue.setText(NA);
			}
		}
	}

	@Override
	public void errorUserinfo(JSONObject error) {
		Log.d(TAG, "userinfo error");
		statusField.setText("error retrieving userinfo");
	}
	
}
