package com.gsma.android.oneapi.logo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

/**
 * Helper functions to save logo information.
 */
public class LogoCacheItem {

	/**
	 * @return LogoItem
	 */
	public LogoItem getLogo() {
		return logo;
	}

	/**
	 * @param logo 
	 *            logo to set
	 */
	public void setLogo(LogoItem logo) {
		this.logo = logo;
	}

	/**
	 * @return String 
	 */
	public String getLocalFile() {
		return localFile;
	}

	/**
	 * @param localFile
	 *           localFile to set
	 */
	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	/**
	 * @return String
	 */
	public String getETag() {
		return eTag;
	}

	/**
	 * @param eTag
	 *            eTag to set
	 */
	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	/**
	 * @return String 
	 */
	public String getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            lastModified to set
	 */
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	LogoItem logo;
	String localFile;
	String eTag;
	String lastModified;
	long lastModifiedTimestamp;

	/**
	 * @return long 
	 */
	public long getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	/**
	 * @param lastModifiedTimestamp
	 *            lastModifiedTimestamp to set
	 */
	public void setLastModifiedTimestamp(long lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}

	Bitmap imageFile;

	/**
	 * @return Bitmap
	 */
	public Bitmap getImageFile() {
		return imageFile;
	}
	
	/**
	 * @param imageFile
	 *            imageFile to set
	 */
	public void setImageFile(Bitmap imageFile) {
		this.imageFile = imageFile;
	}

	/**
	 * Constructor
	 * @param jsonObject
	 * @throws JSONException
	 */
	public LogoCacheItem(JSONObject jsonObject) throws JSONException {
		if (jsonObject != null) {
			this.localFile = jsonObject.has("localFile") ? jsonObject.getString("localFile") : null;
			this.eTag = jsonObject.has("eTag") ? jsonObject.getString("eTag") : null;
			this.lastModified = jsonObject.has("lastModified") ? jsonObject.getString("lastModified") : null;
			this.lastModifiedTimestamp = jsonObject.has("lastModifiedTimestamp") ? jsonObject.getLong("lastModifiedTimestamp") : 0;
			if(jsonObject.has("logo")){
				Object logo = jsonObject.get("logo");
				if (logo != null) {
					this.logo = new LogoItem(logo);
				}
			}
		}
	}

	/**
	 * Constructor
	 */
	public LogoCacheItem() {
	}

	/**
	 * Gets array of logo information from String.
	 * @param logoCacheSerialised
	 * @return LogoCacheItem[]
	 * @throws JSONException
	 */
	public static LogoCacheItem[] fromSerialisedArrayString(String logoCacheSerialised) throws JSONException {
		LogoCacheItem[] logoArray = null;
		JSONArray sa = new JSONArray(logoCacheSerialised);
		if (sa != null) {
			logoArray = new LogoCacheItem[sa.length()];
			for (int i = 0; i < sa.length(); i++) {
				JSONObject cur = sa.getJSONObject(i);
				logoArray[i] = new LogoCacheItem(cur);
			}
		}
		return logoArray;
	}

	/**
	 * Gets logo information saved in JSONObject.
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();

		if (eTag != null)
			obj.put("eTag", eTag);
		if (localFile != null)
			obj.put("localFile", localFile);
		if (lastModified != null)
			obj.put("lastModified", lastModified);
		if (logo != null)
			obj.put("logo", logo.toObject());
		obj.put("lastModifiedTimestamp", lastModifiedTimestamp);
		return obj;
	}

}
