package com.gsma.android.mobileconnect.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.util.Log;

/**
 * Helper function developed to extract code, state and error values.
 */
public class ParameterList {
	ArrayList<KeyValuePair> parameterList=new ArrayList<KeyValuePair>();
	private static final String TAG = "ParameterList";
	
	public void put(String key, String value) {
		KeyValuePair kv=new KeyValuePair(key, value);
		parameterList.add(kv);
	}
	
	public String getValue(String key) {
		boolean found=false;
		String value=null;
		if (key!=null) {
			for (int i=0; i<parameterList.size() && !found; i++) {
				if (key.equals(parameterList.get(i).getKey())) {
					found=true;
					value=parameterList.get(i).getValue();
				}
			}
		}
		Log.d(TAG, "Parameter "+key+" = "+value);
		return value;
	}
	
	public String[] getAttribute(String type) {
		String[] attributes=null;
		
		Log.d(TAG, "Get attribute value for "+type);
		// Part 1 - find key/value pair for the attribute exchange namespace
		String key=null;
		for (int i=0; i<parameterList.size() && key==null; i++) {
			if ("http://openid.net/srv/ax/1.0".equals(parameterList.get(i).getValue())) {
				key=parameterList.get(i).getKey();
			}
		}
		
		Log.d(TAG, "Namespace key = "+key);
		// Check that the namespace is known
		if (key!=null && key.startsWith("openid.ns")) {
			String[] parts=key.split("\\.");
			
			// Extract the namespace element 
			String ns=null;
			if (parts.length>=3) {
				ns=parts[parts.length-1];
			}
			
			// Everything else will be retrieved via the namespace
			String prefix="openid."+ns;
			String prefixType=prefix+".type";

			Log.d(TAG, "prefix = "+prefix);
			
			// Locate the relevant type alias based on the type name
			String typeAlias=null;
			for (int i=0; i<parameterList.size() && typeAlias==null; i++) {
				String parameterKey=parameterList.get(i).getKey();
				if (parameterKey!=null && parameterKey.startsWith(prefixType)) {
					if (parameterList.get(i).getValue().equals(type)) {
						typeAlias=parameterList.get(i).getKey();
					}
				}
			}
			
			Log.d(TAG, "typeAlias = "+typeAlias);
			
			if (typeAlias!=null) {
				String[] tParts=typeAlias.split("\\.");
				String alias=tParts[tParts.length-1];
				
				// Next try and fetch simple single value e.g.
				// openid.ext1.type.email = http://openid.net/schema/contact/internet/email
				// openid.ext1.value.email = gsma.developer@yahoo.co.uk
				String simpleValue=prefix+".value."+alias;
				String value=getValue(simpleValue);
				if (value!=null) {
					attributes=new String[]{value};
					Log.d(TAG, "simpleValue "+simpleValue+" = "+value);
				} else {
					// Try and remove complex value e.g.
					// openid.ax.type.ext1 = http://axschema.org/contact/email
					// openid.ax.count.ext1 = 1
					// openid.ax.value.ext1.1 = gsma.developer@yahoo.co.uk
					
					String countKey=prefix+".count."+alias;
					String countValue=getValue(countKey);
					
					Log.d(TAG, "countKey = "+countKey+" value = "+countValue);
					if (countValue!=null && countValue.trim().length()>0) {
						try {
							int count=Integer.parseInt(countValue.trim());
							if (count>0) {
								attributes=new String[count];
								for (int i=0; i<count; i++) {
									String attributeKey=prefix+".value."+alias+"."+(i+1);
									attributes[i]=getValue(attributeKey);
									Log.d(TAG, "attributeKey = "+attributeKey+ " value = "+attributes[i]);
								}
							}
						} catch (NumberFormatException nfe) {}
					}
				}
			}
			
		}
		return attributes;
		
	}
	
	public String encodeUriParameters(String baseUri) {
		StringBuffer buffer = baseUri!=null?new StringBuffer(baseUri):new StringBuffer();

		boolean first=true;
		boolean needQueryStart=baseUri!=null && baseUri.indexOf('?')==-1;
		
		for (KeyValuePair entry : parameterList) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			if (key!=null) {
				if (first) {
					first=false;
					if (needQueryStart) {
						buffer.append("?");
					}
				} else {
					buffer.append("&");
				}
				try {
					buffer.append(URLEncoder.encode(key, "UTF-8"));
					buffer.append("=");
					if (value != null)
						buffer.append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
				}

			}

		}
		Log.d(TAG, "Generated = "+buffer.toString());
		return buffer.toString();
	}

	public static ParameterList getKeyValuesFromUrl(String url, int start) {
		ParameterList parameterList=new ParameterList();
		parameterList.loadKeyValuesFromUrl(url, start);
		return parameterList;
	}

	public static ParameterList getKeyValuesFromUrl(String url) {
		ParameterList parameterList=new ParameterList();
		parameterList.loadKeyValuesFromUrl(url, 1);
		return parameterList;
	}
	
	public void loadKeyValuesFromUrl(String url,  int start) {
		String[] urlParts = url.split("[\\?\\&]");
		for (int i = start; i < urlParts.length; i++) {
			String part = urlParts[i];
			String[] kv = part.split("=", 2);
			if (kv.length == 2) {
				String key = kv[0];
				String value = kv[1];
				try {
					key = URLDecoder.decode(key, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
				try {
					value = URLDecoder.decode(value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}

				Log.d(TAG, "Returned " + key + " = " + value);
				put(key, value);
			} else if (kv.length == 1) {
				String key = kv[0];
				try {
					key = URLDecoder.decode(key, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
				put(key, null);
			}
		}

		
	}
	
	/**
	 * simple parser to extract key value pair from a text line
	 * 
	 * @param currentLine
	 * @return
	 */
	public void getKeyValuePairFromPlainTextLine(String currentLine) {
		if (!currentLine.equals("")) {
			String a[] = currentLine.split(":", 2);
			try {
				put(URLDecoder.decode(a[0], "UTF-8"), URLDecoder.decode(a[1], "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	
}
