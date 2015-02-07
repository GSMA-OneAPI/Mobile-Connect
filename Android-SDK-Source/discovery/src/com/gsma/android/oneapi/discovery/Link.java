package com.gsma.android.oneapi.discovery;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper functions to parse discovery information. Implements Serializable
 */
public class Link implements Serializable {

	private static final long serialVersionUID = 5293311103983139347L;

	String href = null;
	String rel = null;

	/**
	 * Constructor
	 */
	public Link() {

	}

	/**
	 * Constructor
	 * @param json
	 * @throws JSONException
	 */
	public Link(JSONObject json) throws JSONException {
		if (json != null) {
			this.rel = json.getString("rel");
			this.href = json.getString("href");
		}
	}

	/**
	 * @return String
	 */
	public String getHref() {
		return this.href;
	}

	/**
	 * @param href
	 *           href to set
	 */
	public void setHRef(String href) {
		this.href = href;
	}

	/**
	 * @return String
	 */
	public String getRel() {
		return this.rel;
	}

	/**
	 * @param rel
	 *           rel to set
	 */
	public void setRel(String rel) {
		this.rel = rel;
	}

}
