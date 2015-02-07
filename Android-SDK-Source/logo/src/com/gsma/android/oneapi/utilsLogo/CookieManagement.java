package com.gsma.android.oneapi.utilsLogo;

import java.util.Calendar;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * Helper functions for Cookies
 */
public class CookieManagement {

	private final static CookieStore cookieStore = new BasicCookieStore();

	/**
	 * Gets current cookie store.
	 * 
	 * @return CookieStore
	 */
	public static CookieStore getCookieStore() {
		return CookieManagement.cookieStore;
	}

	/**
	 * Adds the cookie store to the HttpClient.
	 * 
	 * @param client
	 */
	public static void updateCookieStore(HttpClient client) {
		CookieStore cookieStore = getCookieStore();
		synchronized (cookieStore) {
			((DefaultHttpClient) client).setCookieStore(cookieStore);
		}
	}

	/**
	 * Adds a cookie to the cookies store.
	 * 
	 * @param name
	 * @param value
	 */
	public static void addCookieExpireOneDay(String name, String value) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		if (name != null && value != null) {
			CookieStore cookieStore = getCookieStore();
			BasicClientCookie cookie = new BasicClientCookie(name, value);
			cookie.setExpiryDate(calendar.getTime());
			synchronized (cookieStore) {
				cookieStore.addCookie(cookie);
			}
		}
	}

	/**
	 * Gets the cookie associated with the specific name.
	 * 
	 * @param name
	 * @return String
	 */
	public static String getCookie(String name) {
		String value = null;
		List<Cookie> cookies = getCookieStore().getCookies();
		for (int i = 0; name != null && cookies != null && i < cookies.size(); i++) {
			if (cookies.get(i).getName().equalsIgnoreCase(name)) {
				value = cookies.get(i).getValue();
				break;
			}
		}
		return value;
	}

}
