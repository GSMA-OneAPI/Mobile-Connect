package com.gsma.android.oneapi.logo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.gsma.android.oneapi.utilsLogo.PhoneState;
import com.gsma.android.oneapi.utilsLogo.PhoneUtils;
import com.gsma.android.oneapi.valuesLogo.Apis;
import com.gsma.android.oneapi.valuesLogo.AspectRatio;
import com.gsma.android.oneapi.valuesLogo.BgColor;
import com.gsma.android.oneapi.valuesLogo.Size;

/**
 * Main class with library methods. Implements LogoCallbackReciever
 */
public class LogoProvider implements LogoCallbackReceiver {

	LogoTask initialLogoTask;
	LogoListener listener;

	/**
	 * Constructor
	 */
	public LogoProvider() {
	}

	/**
	 * Gets the logo information. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param logosize (SMALL, MEDIUM, LARGE)
	 * @param color (NORMAL, BLACK, REVERSED)
	 * @param ratio (LANDSCAPE, SQUARE)
	 * @param listener It is necessary to implement logoInfo(LogoItemArray li) 
	 * and errorLogoInfo(JSONObject o) to wait the response.
	 * @param context
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getLogoAutomaticMCCMNC(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, Size logosize,
			BgColor color, AspectRatio ratio, LogoListener listener, Context context) {
		try {
			this.listener = listener;
	
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			PhoneState phoneS = PhoneUtils.getPhoneState(tm, cm);
	
			String mcc = phoneS.getMcc();
			String mnc = phoneS.getMnc();
			
			String sz="";
			String cl="";
			String rt="";
			if(logosize!=null)
				sz = logosize.value();
			if(color!=null)
				cl = color.value();
			if(ratio!=null)
				rt = ratio.value();
	
			initialLogoTask = new LogoTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, mcc, mnc, sz, cl, rt, this,
						context);
			initialLogoTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the logo information. 
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param sourceIP
	 * @param mcc
	 * @param mnc
	 * @param logosize (SMALL, MEDIUM, LARGE)
	 * @param color (NORMAL, BLACK, REVERSED)
	 * @param ratio (LANDSCAPE, SQUARE)
	 * @param listener It is necessary to implement logoInfo(LogoItemArray li) 
	 * and errorLogoInfo(JSONObject o) to wait the response.
	 * @param context
	 * @throws Exception
	 * @throws NullPointerException
	 */
	public void getLogo(String serviceUri, String consumerKey,
			String consumerSecret, String sourceIP, String mcc, String mnc, Size logosize,
			BgColor color, AspectRatio ratio, LogoListener listener, Context context) {
		try {
			this.listener = listener;
			
			String sz="";
			String cl="";
			String rt="";
			if(logosize!=null)
				sz = logosize.value();
			if(color!=null)
				cl = color.value();
			if(ratio!=null)
				rt = ratio.value();
	
			initialLogoTask = new LogoTask(serviceUri, consumerKey,
						consumerSecret, sourceIP, mcc, mnc, sz, cl, rt, this,
						context);
			initialLogoTask.execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Internal use. Method from the LogoCallbackReciever interface to wait the response. 
	 * 
	 * @param result
	 * @throws JSONException
	 * @throws NullPointerException
	 */
	@Override
	public void receiveLogoData(Object result) {
		if (initialLogoTask != null) {
			initialLogoTask.cancel(true);
			initialLogoTask = null;
		}
		if (result instanceof JSONObject) {
			try{
				listener.errorLogoInfo((JSONObject) result);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			try {
				LogoItemArray li = new LogoItemArray(result);
				listener.logoInfo(li);
			} catch (JSONException e) {
				listener.errorLogoInfo(null);
			}catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to clear the saved logo information.
	 */
	public void clearCacheLogo() {
		LogoCache.clearCache();
	}

	/**
	 * Gets saved Bitmap object with the specific logo.
	 * 
	 * @param operatorName
	 * @param api (PAYMENT, OPERATORID)
	 * @param logosize (SMALL, MEDIUM, LARGE)
	 * @param color (NORMAL, BLACK, REVERSED)
	 * @param ratio (LANDSCAPE, SQUARE)
	 * @param context
	 * @return Bitmap
	 * @throws NullPointerException
	 */
	public Bitmap getCacheApiLogo(String operatorName, Apis api, Size logosize, BgColor color, AspectRatio ratio, Context context) {
		try{
			LogoCache.loadCache(context);
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		Bitmap logo;
		if(operatorName!=null && api!=null && logosize!=null && color!=null && ratio!=null)
			logo = LogoCache.getBitmap(operatorName, api.value(), logosize.value(), color.value(), ratio.value());
		else
			logo=null;
		return logo;
	}

	/**
	 * Gets an array with the complete saved logo information.
	 * 
	 * @param context
	 * @return ArrayList<LogoCacheItem>
	 * @throws NullPointerException
	 */
	public ArrayList<LogoCacheItem> getCacheLogo(Context context) {
		try{
			LogoCache.loadCache(context);
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		return LogoCache.logoCache;
	}

}
