package com.gsma.android.oneapi.logo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLHandshakeException;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gsma.android.oneapi.utilsLogo.HttpUtils;

/**
 * Helper class to save logo object.
 */
public class LogoCache {

	private static final String TAG = "LogoCache";

	private static SharedPreferences mPrefs = null;
	private static final String PREFS_LOGO = "logoPrefs";
	private static final String preferenceName = "LogoCache";
	public static ArrayList<LogoCacheItem> logoCache = null;
	private static HashMap<String, LogoCacheItem> logoQuickCache = null;
	private static ContextWrapper contextWrapper = null;
	private static final String filepath = "LogoStorage";

	/**
	 * Load logo information from preferences storage.
	 * @param context
	 * @throws JSONException
	 * @throws NullPointerException
	 */
	public static void loadCache(Context context) {
		try {
			if (contextWrapper == null)
				contextWrapper = new ContextWrapper(context);
			if (logoCache == null)
				logoCache = new ArrayList<LogoCacheItem>();
			if (logoQuickCache == null)
				logoQuickCache = new HashMap<String, LogoCacheItem>();
			if (mPrefs == null)
				mPrefs = context.getSharedPreferences(PREFS_LOGO, 0);
	
			Log.d(TAG, "Recovering logo cache");
			String logoCacheSerialised = mPrefs.getString(preferenceName, null);
			if (logoCacheSerialised != null) {
				Log.d(TAG, "Logo cache data=" + logoCacheSerialised);
				LogoCacheItem[] storedLogos;
				Log.d(TAG, "Converting from string to object");
				storedLogos = LogoCacheItem.fromSerialisedArrayString(logoCacheSerialised);
				Log.d(TAG, "Object = " + storedLogos);
				if (storedLogos != null) {
					Log.d(TAG, "Cache contains " + storedLogos.length + " entries");
					for (int i = 0; i < storedLogos.length; i++) {
						Log.d(TAG, "[" + i + "] = " + storedLogos[i].getLogo().getUrl() + " " + storedLogos[i].getLocalFile());
						Bitmap logoBitmap = BitmapFactory.decodeFile(storedLogos[i].getLocalFile());
						if (logoBitmap != null) {
							Log.d(TAG, "Read bitmap");
							storedLogos[i].setImageFile(logoBitmap);
							logoCache.add(storedLogos[i]);
							logoQuickCache.put(storedLogos[i].getLogo().getUrl(), storedLogos[i]);
						}
					}
				}
			}
		} catch (JSONException e) {
			Log.d(TAG, "JSONException=" + e.getMessage());
		} catch (NullPointerException e) {
			Log.d(TAG, "NullPointerException=" + e.getMessage());
		}
	}

	/**
	 * Save logo information in preferences storage.
	 */
	private static void save() {
		String logoCacheSerialised = toSerialisedString();
		Log.d(TAG, "Serialised data=" + logoCacheSerialised);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(preferenceName, logoCacheSerialised);
		editor.commit();
	}

	/**
	 * Clear the logo information.
	 */
	public static void clearCache() {
		if(logoCache!=null)
			logoCache.clear();
		if(logoQuickCache!=null)
			logoQuickCache.clear();
		if(mPrefs!=null)
			save();
	}
	
	/**
	 * Gets Bitmap according to operator, API and other parameters
	 * @param operatorName the name of the operator as returned by the discovery API
	 * @param apiName the name of the API to display the logo for
	 * @param size required logo size 
	 * @param color required logo color scheme
	 * @param ratio required logo aspect ratio
	 * @return Bitmap
	 */
	public static Bitmap getBitmap(String operatorName, String apiName, String size, String color, String ratio) {
		Bitmap bitmap = null;
		for (int i = 0; i < logoCache.size() && bitmap == null; i++) {
			LogoCacheItem lci = logoCache.get(i);
			if (lci!=null) {
				LogoItem lr = lci.getLogo();
				if (lr!=null) {
					boolean matched=true;
					if (operatorName!=null && !operatorName.equalsIgnoreCase(lr.getOperatorId())) matched=false;
					if (apiName!=null && !apiName.equalsIgnoreCase(lr.getApiName())) matched=false;
					if (size!=null && !size.equalsIgnoreCase(lr.getSize())) matched=false;
					if (color!=null && !color.equalsIgnoreCase(lr.getBgColor())) matched=false;
					if (ratio!=null && !ratio.equalsIgnoreCase(lr.getAspectRatio())) matched=false;
					if (matched) {
						bitmap = lci.getImageFile();
					}
				}
			}
		}
		return bitmap;
	}

	/**
	 * Gets String with each logo information saved.
	 * @return String
	 * @throws JSONException
	 */
	public static String toSerialisedString() {
		String rv = null;
		try {
			JSONArray obj = toArray();
			rv = obj.toString();
		} catch (JSONException e) {
		}
		return rv;
	}

	/**
	 * Gets array with each logo information saved.
	 * @return JSONArray
	 * @throws JSONException
	 */
	public static JSONArray toArray() throws JSONException {
		JSONArray obj = new JSONArray();
		if (logoCache != null) {
			for (int i = 0; i < logoCache.size(); i++) {
				obj.put(i, logoCache.get(i).toObject());
			}
		}
		return obj;
	}

	/**
	 * Add logo information to preferences storage.
	 * @param logoResponse
	 * @param context
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NullPointerException
	 */
	public static void addLogoResponse(LogoItem logoResponse, Context context) {
		try{
			if (contextWrapper == null)
				contextWrapper = new ContextWrapper(context);
			if (mPrefs == null)
				mPrefs = context.getSharedPreferences(PREFS_LOGO, 0);
		} catch (NullPointerException e) {
			Log.d(TAG, "NullPointerException=" + e.getMessage());
		}
		if (logoCache == null)
			logoCache = new ArrayList<LogoCacheItem>();
		if (logoQuickCache == null)
			logoQuickCache = new HashMap<String, LogoCacheItem>();
		if (logoResponse != null) {
			if (logoQuickCache.containsKey(logoResponse.getUrl())) {
				LogoCacheItem lci = logoQuickCache.get(logoResponse.getUrl());
				Log.d(TAG, "Already in cache - checking resource");
				try {
					URL url = new URL(logoResponse.getUrl());
					URLConnection urlC = url.openConnection();
					if (lci.getETag() != null) {
						urlC.addRequestProperty("If-None-Match", lci.getETag());
					} else if (lci.getLastModifiedTimestamp() > 0) {
						urlC.setIfModifiedSince(lci.getLastModifiedTimestamp());
					}

					String eTag = urlC.getHeaderField("ETag");
					String lastModified = urlC.getHeaderField("Last-Modified");
					long lastModifiedTimestamp = urlC.getLastModified();

					boolean updated = false;
					Log.d(TAG, "eTag=" + eTag);
					Log.d(TAG, "lastModified=" + lastModified);
					if (eTag != null && !eTag.equals(lci.getETag())) {
						updated = true;
					} else if (lastModifiedTimestamp > 0
							&& lastModifiedTimestamp > lci
									.getLastModifiedTimestamp()) {
						updated = true;
					}

					Log.d(TAG, "Update? = " + updated);
					if (updated) {
						InputStream in = urlC.getInputStream();
						Bitmap logoBitmap = BitmapFactory.decodeStream(in);
						File localImageFile = new File(lci.getLocalFile());
						FileOutputStream fos = new FileOutputStream(localImageFile);
						logoBitmap.compress(CompressFormat.PNG, 100, fos);
						fos.close();
						lci.setETag(eTag);
						lci.setLastModified(lastModified);
						lci.setImageFile(logoBitmap);
						lci.setLastModifiedTimestamp(lastModifiedTimestamp);
						save();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				LogoCacheItem lci = new LogoCacheItem();
				lci.setLogo(logoResponse);

				try {
					Log.d(TAG, "Reading from " + logoResponse.getUrl());
					URL url = new URL(logoResponse.getUrl());
					URLConnection urlC = url.openConnection();
					String eTag = urlC.getHeaderField("ETag");
					String lastModified = urlC.getHeaderField("Last-Modified");
					Log.d(TAG, "eTag = " + eTag);
					Log.d(TAG, "lastModified = " + lastModified);
					InputStream in = urlC.getInputStream();
					Bitmap logoBitmap = BitmapFactory.decodeStream(in);

					if (logoBitmap!=null) {
						File directory = contextWrapper.getDir(filepath,Context.MODE_PRIVATE);
						MessageDigest digest = null;
						digest = MessageDigest.getInstance("SHA-256");
						digest.reset();
						String localFilename = HttpUtils.bin2hex(digest.digest(logoResponse.getUrl().getBytes("UTF-8"))) + ".png";
						Log.d(TAG, "Local file name=" + localFilename);
	
						File localImageFile = new File(directory, localFilename);
						FileOutputStream fos = new FileOutputStream(localImageFile);
						logoBitmap.compress(CompressFormat.PNG, 100, fos);
						fos.close();
						Log.d(TAG, "Written to=" + localImageFile.getAbsolutePath());
	
						lci.setETag(eTag);
						lci.setLastModified(lastModified);
						lci.setLocalFile(localImageFile.getAbsolutePath());
						lci.setImageFile(logoBitmap);
						lci.setLastModifiedTimestamp(urlC.getLastModified());
	
						Log.d(TAG, "Storing to cache");
						logoCache.add(lci);
						logoQuickCache.put(logoResponse.getUrl(), lci);
	
						save();
					} else {
						Log.w(TAG, "Logo not found at "+logoResponse.getUrl());
					}
				} catch (SSLHandshakeException e) {
					Log.e(TAG, "SSLHandshakeException "+e.getMessage());
				} catch (MalformedURLException e) {
					Log.e(TAG, "MalformedURLException "+e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, "IOException "+e.getMessage());
				} catch (NoSuchAlgorithmException e) {
					Log.e(TAG, "NoSuchAlgorithmException "+e.getMessage());
				}  catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException "+e.getMessage());
				}
				
			}
		}
	}

}
