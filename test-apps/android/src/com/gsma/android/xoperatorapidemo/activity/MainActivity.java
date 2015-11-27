package com.gsma.android.xoperatorapidemo.activity;

import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gsma.android.mobileconnect.authorization.Authorization;
import com.gsma.android.mobileconnect.authorization.AuthorizationListener;
import com.gsma.android.mobileconnect.authorization.AuthorizationOptions;
import com.gsma.android.mobileconnect.values.Prompt;
import com.gsma.android.mobileconnect.values.ResponseType;
import com.gsma.android.mobileconnectsdktest.R;
import com.gsma.android.oneapi.discovery.Api;
import com.gsma.android.oneapi.discovery.DiscoveryItem;
import com.gsma.android.oneapi.discovery.DiscoveryListener;
import com.gsma.android.oneapi.discovery.DiscoveryProvider;
import com.gsma.android.oneapi.discovery.DiscoveryResponse;
//import com.gsma.android.oneapi.logo.LogoCache;
//import com.gsma.android.oneapi.logo.LogoItem;
//import com.gsma.android.oneapi.logo.LogoItemArray;
//import com.gsma.android.oneapi.logo.LogoListener;
//import com.gsma.android.oneapi.logo.LogoProvider;
import com.gsma.android.oneapi.valuesDiscovery.DiscoveryCredentials;
import com.gsma.android.oneapi.valuesLogo.AspectRatio;
import com.gsma.android.oneapi.valuesLogo.BgColor;
import com.gsma.android.oneapi.valuesLogo.Size;
import com.gsma.android.xoperatorapidemo.discovery.DiscoveryStartupSettings;
import com.gsma.android.xoperatorapidemo.utils.PhoneState;
import com.gsma.android.xoperatorapidemo.utils.PhoneUtils;
import com.gsma.android.xoperatorapidemo.utils.PreferencesUtils;

public class MainActivity extends Activity implements DiscoveryListener /*, LogoListener*/, AuthorizationListener {

	private static final String TAG = "MainActivity";

	public static MainActivity mainActivityInstance = null;
		
	private static DiscoveryItem discoveryData=null;	
	
	private boolean useWebviewForDiscovery=true;
	
	public static final int DISCOVERY_COMPLETE=1;
	public static final int SETTINGS_COMPLETE=2;
//	public static final int LOGOS_UPDATED=100;

	/*
	 * has discovery been started - used to avoid making a duplicate request
	 */
	boolean started = false;
	boolean discovered = false;
	static boolean justDiscovered = false;

	Button discoveryButton = null;
	TextView vMCC = null;
	TextView vMNC = null;
	TextView vStatus = null;
	TextView vDiscoveryStatus = null;
	
	Button startOperatorId = null;
	
	static Handler discoveryHandler = null;
//	static Handler logoUpdateHandler = null;
	
	/*
	 * method called when the application first starts.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Starting the app...");
		
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);

        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d(TAG, "onCreate called");
		
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		vMCC = (TextView) findViewById(R.id.valueMCC);
		vMNC = (TextView) findViewById(R.id.valueMNC);
		vStatus = (TextView) findViewById(R.id.valueStatus);
		vDiscoveryStatus = (TextView) findViewById(R.id.valueDiscoveryStatus);
		
		discoveryButton = (Button) findViewById(R.id.discoveryButton);
		startOperatorId = (Button) findViewById(R.id.startOperatorId);

		
		/*
		 * load defaults from preferences file
		 */
		PreferencesUtils.loadPreferences(this);
		
		/*
		 * load settings from private local storage
		 */
		SettingsActivity.loadSettings(this);

		/*
		 * load logo cache
		 */
//		LogoCache.loadCache(this);
//		setLogos("exchange");
		
		mainActivityInstance = this;

		CookieSyncManager.createInstance(this.getApplicationContext());
		CookieManager.getInstance().setAcceptCookie(true);

		discoveryHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.d(TAG, "Discovery result processing. "+msg.what);
				
				vDiscoveryStatus.setText(getString(msg.what));
				setButtonStates((DiscoveryItem) msg.obj);
				discoveryButton.setEnabled(true);
			}
		};

		final Handler phoneStatusHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				vStatus.setText(getString(msg.what));
			}
		};
		
//		logoUpdateHandler = new Handler() {
//			public void handleMessage(Message msg) {
//				handleLogoUpdate();
//			}
//		};
//	    
		new Thread(new Runnable() { 
            public void run(){
            	boolean running=true;
            	while (running) {
					TelephonyManager telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					PhoneState state = PhoneUtils.getPhoneState(telephonyManager, connectivityManager);

					boolean connected = state.isConnected(); // Is the device connected to
					// the Internet
					boolean usingMobileData = state.isUsingMobileData(); // Is the device
					// connected using cellular/mobile data
					boolean roaming = state.isRoaming(); // Is the device roaming
					
					int status = R.string.statusDisconnected;
					if (roaming) {
						status = R.string.statusRoaming;
					} else if (usingMobileData) {
						status = R.string.statusOnNet;
					} else if (connected) {
						status = R.string.statusOffNet;
					}
					phoneStatusHandler.sendEmptyMessage(status);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						running=false;
					}
            	}
            }
		}).start();
				
//		Log.d(TAG, "Starting fetch for default logos");
//		LogoProvider logoProvider=new LogoProvider();
//		logoProvider.getLogo(SettingsActivity.getDeveloperOperator().getLogoEndpoint(), 
//				SettingsActivity.getDeveloperOperator().getAppKey(), 
//				SettingsActivity.getDeveloperOperator().getAppSecret(), 
//				null, /* IP address */ 
//				null, /* MCC */
//				null, /* MNC */
//				Size.SMALL, /* size */ 
//				BgColor.NORMAL, /* colour scheme */
//				AspectRatio.LANDSCAPE, /* aspect ratio */
//				this, /* listener */ 
//				this /* context */);
				
	}
	
//	public void handleLogoUpdate() {
//		Log.d(TAG, "called handleLogoUpdate");
//		boolean set=false;
//		if (discoveryData!=null && discoveryData.getResponse()!=null) {
//			String operator=discoveryData.getResponse().getSubscriber_operator();
//			set=setLogos(operator);
//		}
//		if (!set) {
//			set=setLogos("exchange");
//		}
//		if (!set) {
//			startOperatorId.setBackgroundDrawable(null);
//			startOperatorId.setText(R.string.startOperatorId);
//			set=true;
//		}
//	}
//	
//	private boolean setLogos(String operator) {
//		boolean set=false;
//		Log.d(TAG, "Trying logos for operator = "+operator);
//		Bitmap operatorIdImage=LogoCache.getBitmap(operator, "operatorid", "small", "normal", "landscape");
//		
//		if (operatorIdImage!=null) {
//			Drawable d = new BitmapDrawable(operatorIdImage);
//			startOperatorId.setBackgroundDrawable(d);
//			startOperatorId.setWidth(operatorIdImage.getWidth());
//			startOperatorId.setHeight(operatorIdImage.getHeight());
//			startOperatorId.setText("");
//			set=true;
//		}
//		return set;
//	}
	
	/*
	 * on start or return to the main screen reset the screen so that discovery
	 * can be started
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		Log.d(TAG, "onStart called");
		
		boolean cacheGood=false;
		
		/* Reset the flag that stops a duplicate discovery request to be made */
		started = false;
		
		if (!justDiscovered) {
			startOperatorId.setVisibility(View.INVISIBLE);
			
			DiscoveryProvider discoveryProvider=new DiscoveryProvider();
			discoveryProvider.setVerboseTracing(true);
			Log.d(TAG, "Checking for cached discovery response");
			discoveryData=discoveryProvider.getCacheDiscoveryItem(this);
			if (discoveryData!=null) {
				Log.d(TAG, "Cache is good");
				discovered=true;
				vDiscoveryStatus.setText(getString(R.string.discoveryStatusCached));
				cacheGood=true;
				discoveryButton.setEnabled(false);
				setButtonStates(discoveryData);
				discoveryButton.setEnabled(true);
			}

			String mcc=SettingsActivity.getMcc();
			String mnc=SettingsActivity.getMnc();
			
			vMCC.setText(mcc!=null?mcc:getText(R.string.valueUnknown));
			vMNC.setText(mnc!=null?mnc:getText(R.string.valueUnknown));
			
//			Log.d(TAG, "starting logo API request for current operator logos");
//			LogoProvider logoProvider=new LogoProvider();
//			logoProvider.getLogo(SettingsActivity.getDeveloperOperator().getLogoEndpoint(), 
//					SettingsActivity.getDeveloperOperator().getAppKey(), 
//					SettingsActivity.getDeveloperOperator().getAppSecret(), 
//					null, /* IP address */ 
//					mcc,
//					mnc,
//					Size.SMALL, /* size */ 
//					BgColor.NORMAL, /* colour scheme */
//					AspectRatio.LANDSCAPE, /* aspect ratio */
//					this, /* listener */ 
//					this /* context */);
			
			DiscoveryStartupSettings startupOption=SettingsActivity.getDiscoveryStartupSettings();
			if (startupOption==DiscoveryStartupSettings.STARTUP_OPTION_PASSIVE) {
				if (!cacheGood) {
					discoveryButton.setEnabled(false);
					vDiscoveryStatus.setText(getString(R.string.discoveryStatusStarted));
					
					Log.d(TAG, "Initiating passive discovery");
					
					discoveryProvider.clearCacheDiscoveryItem(this);
					
					if (SettingsActivity.getServingOperator().isAutomatic()) {
						discoveryProvider.getDiscoveryPassiveAutomaticMCCMNC(SettingsActivity.getDeveloperOperator().getEndpoint(), 
								SettingsActivity.getDeveloperOperator().getAppKey(), 
								SettingsActivity.getDeveloperOperator().getAppSecret(), 
								SettingsActivity.getServingOperator().getIpaddress(), 
								null, /* MSISDN */
								this, /* listener */
								this, /* context */
								DiscoveryCredentials.PLAIN, 
								"http://gsma.com/oneapi");
					} else {
						discoveryProvider.getDiscoveryPassive(SettingsActivity.getDeveloperOperator().getEndpoint(), 
								SettingsActivity.getDeveloperOperator().getAppKey(), 
								SettingsActivity.getDeveloperOperator().getAppSecret(), 
								SettingsActivity.getServingOperator().getIpaddress(),
								SettingsActivity.getServingOperator().getMcc(),
								SettingsActivity.getServingOperator().getMnc(),
								null, /* MSISDN */
								this, /* listener */
								this, /* context */
								DiscoveryCredentials.PLAIN, 
								"http://gsma.com/oneapi");
					}
					
				}
			} else if (startupOption==DiscoveryStartupSettings.STARTUP_OPTION_PREEMPTIVE) {
				if (!cacheGood) {
					discoveryButton.setEnabled(false);
					vDiscoveryStatus.setText(getString(R.string.discoveryStatusStarted));
					
					Log.d(TAG, "Initiating active discovery");
					
					discoveryProvider.clearCacheDiscoveryItem(this);
					
					if (SettingsActivity.getServingOperator().isAutomatic()) {
						discoveryProvider.getDiscoveryActiveAutomaticMCCMNCUsingWebview(SettingsActivity.getDeveloperOperator().getEndpoint(), 
								SettingsActivity.getDeveloperOperator().getAppKey(), 
								SettingsActivity.getDeveloperOperator().getAppSecret(), 
								SettingsActivity.getServingOperator().getIpaddress(), 
								null, /* MSISDN */
								this, /* listener */
								this, /* context */
								useWebviewForDiscovery?mainActivityInstance:null, /* activity */
								DiscoveryCredentials.PLAIN, 
								"http://gsma.com/oneapi");
					} else {
						discoveryProvider.getDiscoveryActiveUsingWebview(SettingsActivity.getDeveloperOperator().getEndpoint(), 
								SettingsActivity.getDeveloperOperator().getAppKey(), 
								SettingsActivity.getDeveloperOperator().getAppSecret(), 
								SettingsActivity.getServingOperator().getIpaddress(), 
								SettingsActivity.getServingOperator().getMcc(),
								SettingsActivity.getServingOperator().getMnc(),
								null, /* MSISDN */
								this, /* listener */
								this, /* context */
								useWebviewForDiscovery?mainActivityInstance:null, /* activity */
								DiscoveryCredentials.PLAIN, 
								"http://gsma.com/oneapi");						
					}

				}
			}

		} else {

			String mcc=SettingsActivity.getMcc();
			String mnc=SettingsActivity.getMnc();

//			Log.d(TAG, "starting logo API request for current operator logos");
//			LogoProvider logoProvider=new LogoProvider();
//			logoProvider.getLogo(SettingsActivity.getDeveloperOperator().getLogoEndpoint(), 
//					SettingsActivity.getDeveloperOperator().getAppKey(), 
//					SettingsActivity.getDeveloperOperator().getAppSecret(), 
//					null, /* IP address */
//					mcc,
//					mnc,
//					Size.SMALL, /* size */ 
//					BgColor.NORMAL, /* colour scheme */
//					AspectRatio.LANDSCAPE, /* aspect ratio */
//					this, /* listener */ 
//					this /* context */);
			
		}
	}

	/*
	 * default method to add a menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void setButtonStates(DiscoveryItem discoveryData) {
		Log.d(TAG, "Setting button states");
		boolean operatorIdEnabled=false;
		
		Log.d(TAG, "discoveryData="+discoveryData);
		if (discoveryData!=null && discoveryData.getResponse()!=null) {
			DiscoveryResponse resp=discoveryData.getResponse();
			Api operatorId=resp.getApi("operatorid");
			Log.d(TAG, "operatorid="+operatorId);
			if (operatorId!=null) {
				operatorIdEnabled=operatorId.getHref("authorization")!=null;
			}
		}
		
		Log.d(TAG, "OperatorID enabled="+operatorIdEnabled);
		startOperatorId.setVisibility(operatorIdEnabled?View.VISIBLE:View.INVISIBLE);

	}

	/*
	 * handles a restart/ refresh of the discovery process
	 */
	public void restart(View view) {
		/* Reset text on start button */
		discoveryButton.setText(getString(R.string.start));

		/* Reset the discovery process lock */
		started = false;
	}

	
	public static void clearDiscoveryData() {
		Log.d(TAG, "Clearing discovery data");
		discoveryData=null;
		DiscoveryProvider discoveryProvider=new DiscoveryProvider();
		discoveryProvider.clearCacheDiscoveryItem(mainActivityInstance);
		
		Message msg=new Message();
		msg.what=R.string.discoveryStatusPending;
		msg.obj=null;
		discoveryHandler.sendMessage(msg);
	}

	public static DiscoveryItem getDiscoveryData() {
		return MainActivity.discoveryData;
	}

	/*
	 * if there is an error any time during discovery it will be displayed via
	 * the displayError function
	 */
	public void displayError(String error, String errorDescription) {
		Toast toast = Toast.makeText(getBaseContext(), errorDescription,
				Toast.LENGTH_LONG);
		toast.show();
	}
	
	public String getServingOperatorName() {
		return SettingsActivity.getServingOperator().getName();
	}
	
	public void startSettings(View view) {
		cancelOutstandingDiscoveryTasks();
		Intent intent = new Intent(
				this,
				SettingsActivity.class);		
		startActivity(intent);
	}

	public void handleDiscovery(View view) {
		if (discoveryButton.isEnabled()) {
			String mcc=null;
			String mnc=null;
			
			if (SettingsActivity.getServingOperator().isAutomatic()) {
				TelephonyManager telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				PhoneState state = PhoneUtils.getPhoneState(telephonyManager, connectivityManager);
				mcc=state.getMcc();
				mnc=state.getMnc();
			} else {
				mcc=SettingsActivity.getServingOperator().getMcc();
				mnc=SettingsActivity.getServingOperator().getMnc();
			}

			discoveryButton.setEnabled(false);
			vDiscoveryStatus.setText(getString(R.string.discoveryStatusStarted));
			
			DiscoveryProvider discoveryProvider=new DiscoveryProvider();
			
			if (SettingsActivity.getServingOperator().isAutomatic()) {
				discoveryProvider.getDiscoveryActiveAutomaticMCCMNCUsingWebview(SettingsActivity.getDeveloperOperator().getEndpoint(), 
						SettingsActivity.getDeveloperOperator().getAppKey(), 
						SettingsActivity.getDeveloperOperator().getAppSecret(), 
						SettingsActivity.getServingOperator().getIpaddress(), 
						null, /* MSISDN */
						this, /* listener */
						this, /* context */
						useWebviewForDiscovery?mainActivityInstance:null, /* activity */
						DiscoveryCredentials.PLAIN, 
						"http://gsma.com/oneapi");
			} else {
				discoveryProvider.getDiscoveryActiveUsingWebview(SettingsActivity.getDeveloperOperator().getEndpoint(), 
						SettingsActivity.getDeveloperOperator().getAppKey(), 
						SettingsActivity.getDeveloperOperator().getAppSecret(), 
						SettingsActivity.getServingOperator().getIpaddress(), 
						mcc,
						mnc,
						null, /* MSISDN */
						this, /* listener */
						this, /* context */
						useWebviewForDiscovery?mainActivityInstance:null, /* activity */
						DiscoveryCredentials.PLAIN, 
						"http://gsma.com/oneapi");						
			}

		}
	}

	public void startOperatorId(View view) {
		cancelOutstandingDiscoveryTasks();
		Api operatoridEndpoint=discoveryData.getResponse()!=null?discoveryData.getResponse().getApi("operatorid"):null;
		
		String openIDConnectScopes=PreferencesUtils.getPreference("OpenIDConnectScopes");
		
		String returnUri=PreferencesUtils.getPreference("OpenIDConnectReturnUri");
		
		String authUri=operatoridEndpoint.getHref("authorization");
		String clientId=discoveryData.getResponse().getClient_id();
		String clientSecret=discoveryData.getResponse().getClient_secret();
		Log.d(TAG, "clientId="+discoveryData.getResponse().getClient_id());
		Log.d(TAG, "clientSecret="+discoveryData.getResponse().getClient_secret());

		String state=UUID.randomUUID().toString();	
		String nonce=UUID.randomUUID().toString();	
		int maxAge=3600;
		String acrValues="2";
		
		Authorization authorization=new Authorization();
		
		AuthorizationOptions authorizationOptions=new AuthorizationOptions();
		authorizationOptions.setClaimsLocales("en");
		authorizationOptions.setUILocales("en");
		authorizationOptions.setLoginHint("+44");

		Prompt prompt=Prompt.LOGIN;

		maxAge=0;
		prompt=Prompt.NONE;
		authorizationOptions.setUILocales("");
		
		Log.d(TAG, "Starting OpenIDConnect authorization");
		authorization.authorize(authUri, ResponseType.CODE, clientId, clientSecret, openIDConnectScopes, returnUri, state, nonce, prompt, 
								maxAge, acrValues, authorizationOptions, this /* listener */, this /* activity */);
		
	}

//	public static void processLogoUpdates() {
//		logoUpdateHandler.sendEmptyMessage(LOGOS_UPDATED);
//	}
	
	private void cancelOutstandingDiscoveryTasks() {
	}
	
	@Override
	public void discoveryInfo(DiscoveryItem di) {
		Log.d(TAG, "received discoveryInfo");
		if (di.getResponse()!=null) {
			Log.d(TAG, "clientId="+di.getResponse().getClient_id());
			Log.d(TAG, "clientSecret="+di.getResponse().getClient_secret());
			Log.d(TAG, "updating discovery data");
			discoveryData=di;
			justDiscovered=true;
			Message msg=new Message();
			msg.what=R.string.discoveryStatusCompleted;
			msg.obj=discoveryData;
			discoveryHandler.sendMessage(msg);
		}
		if (di.getError()!=null) {
			Log.d(TAG, "have error");
			displayError(di.getError(), di.getError_description());
		}
	}

	@Override
	public void errorDiscoveryInfo(JSONObject error) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "received discovery error"+error);
	}

//	@Override
//	public void errorLogoInfo(JSONObject error) {
//		// TODO Auto-generated method stub
//		Log.d(TAG, "received logo error "+error);
//	}
//
//	@Override
//	public void logoInfo(LogoItemArray logoItems) {
//		// TODO Auto-generated method stub
//		Log.d(TAG, "received logo data "+logoItems);
//		if (logoItems!=null) {
//			for (LogoItem logoItem:logoItems.getLogos()) {
//				Log.d(TAG, "Logo "+logoItem.getUrl());
//			}
//		}
//		processLogoUpdates();
//	}
//
	@Override
	public void authorizationCodeResponse(String state, String authorizationCode, String error, String clientId, String clientSecret, String scopes, String returnUri) {
		Log.d(TAG, "Have auth code "+authorizationCode);
		
		Api operatoridEndpoint=discoveryData.getResponse()!=null?discoveryData.getResponse().getApi("operatorid"):null;
		String authUri=operatoridEndpoint.getHref("authorization");
		String tokenUri=operatoridEndpoint.getHref("token");
		String userinfoUri=operatoridEndpoint.getHref("userinfo");
		
		Intent intent = new Intent(
				mainActivityInstance,
				AuthorizationCompleteActivity.class);
		intent.putExtra("state", state);
		intent.putExtra("code", authorizationCode);
		intent.putExtra("error", error);
		intent.putExtra("authUri", authUri);
		intent.putExtra("tokenUri", tokenUri);
		intent.putExtra("userinfoUri", userinfoUri);
		intent.putExtra("clientId", clientId);
		intent.putExtra("clientSecret", clientSecret);
		intent.putExtra("scopes", scopes);
		intent.putExtra("returnUri", returnUri);
		
		startActivity(intent);
	}

	@Override
	public void authorizationError(String reason) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Have error "+reason);
		displayError(reason, reason);
	}


}
