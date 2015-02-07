package com.gsma.android.oneapi.logo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse logo information.
 */
public class LogoItem {

	String operatorId;
	String apiName;
	String language;
	String size;
	String height;
	String width;
	String action;
	String url;
	String bgColourRange;
	String bgColor;
    String aspectRatio;

//	[
//	    {
//	        "operatorId": "exchange",
//	        "apiName": "operatorid",
//	        "language": "en",
//	        "size": "large",
//	        "height": "346px",
//	        "width": "768px",
//	        "action": "default",
//	        "url": "https://integration-api.apiexchange.org:443/v1/logostorage/images/english/mobcon_mono_rgb_768.png",
//	        "bgColourRange": "#ffffff,#000000",
//	        "bgColor": "black",
//	        "aspectRatio": "landscape"
//	    }
//	]

	/**
	 * Constructor
	 */
	public LogoItem() {
	}

	/**
	 * Constructor
	 * @param source
	 * @throws JSONException
	 */
	public LogoItem(Object source) throws JSONException {
		if (source != null && source instanceof JSONObject) {
			JSONObject so = (JSONObject) source;
			operatorId = so.has("operatorId") ? so.getString("operatorId") : null;
			apiName = so.has("apiName") ? so.getString("apiName") : null;
			language = so.has("language") ? so.getString("language") : null;
			size = so.has("size") ? so.getString("size") : null;
			height = so.has("height") ? so.getString("height") : null;
			width = so.has("width") ? so.getString("width") : null;
			action = so.has("action") ? so.getString("action") : null;
			url = so.has("url") ? so.getString("url") : null;
			bgColourRange = so.has("bgColourRange") ? so.getString("bgColourRange") : null;
			bgColor = so.has("bgColor") ? so.getString("bgColor") : null;
			aspectRatio = so.has("aspectRatio") ? so.getString("aspectRatio") : null;
		}
	}

	/**
	 * Gets logo information in JSONObject.
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject toObject() throws JSONException {
		JSONObject obj = new JSONObject();

		if (operatorId != null)
			obj.put("operatorId", operatorId);
		if (apiName != null)
			obj.put("apiName", apiName);
		if (language != null)
			obj.put("language", language);
		if (size != null)
			obj.put("size", size);
		if (height != null)
			obj.put("height", height);
		if (width != null)
			obj.put("width", width);
		if (action != null)
			obj.put("action", action);
		if (url != null)
			obj.put("url", url);
		if (bgColourRange != null)
			obj.put("bgColourRange", bgColourRange);
		if (bgColor != null)
			obj.put("bgColor", bgColor);
		if (aspectRatio != null)
			obj.put("aspectRatio", aspectRatio);
		return obj;
	}

	/**
	 * @return String 
	 */
	public String getOperatorId() {
		return operatorId;
	}

	/**
	 * @param operatorId
	 *           operatorId to set
	 */
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	/**
	 * @return String 
	 */
	public String getApiName() {
		return apiName;
	}

	/**
	 * @param apiName
	 *            apiName to set
	 */
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	/**
	 * @return String
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *             language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return String 
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param size
	 *            size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return String
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return String
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return String 
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return String 
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return String 
	 */
	public String getBgColourRange() {
		return bgColourRange;
	}

	/**
	 * @param bgColourRange
	 *            bgColourRange to set
	 */
	public void setBgColourRange(String bgColourRange) {
		this.bgColourRange = bgColourRange;
	}
	
	/**
	 * @return String 
	 */
	public String getBgColor() {
		return bgColor;
	}

	/**
	 * @param bgColor
	 *            bgColor to set
	 */
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}
	
	/**
	 * @return String 
	 */
	public String getAspectRatio() {
		return aspectRatio;
	}

	/**
	 * @param aspectRatio
	 *            aspectRatio to set
	 */
	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

}
