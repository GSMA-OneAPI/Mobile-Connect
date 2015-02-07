package com.gsma.android.mobileconnect.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * Helper functions for HTTP networking.
 */
public class HttpUtils {

	// connection timeout period (mS)
	public static final int REGISTRATION_TIMEOUT = 15 * 1000; 
	// socket timeout period (mS)
	public static final int WAIT_TIMEOUT = 30 * 1000; 

	/**
	 * 
	 * This function adds support for pre-emptive HTTP Authentication for an HttpClient.
	 * 
	 * @param httpClient
	 */
	public static void makeAuthenticationPreemptive(HttpClient httpClient) {
		HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
			public void process(final HttpRequest request,final HttpContext context) throws HttpException,IOException{
				AuthState authState = (AuthState) context
						.getAttribute(ClientContext.TARGET_AUTH_STATE);
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context
						.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

				if (authState.getAuthScheme() == null) {
					AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
					Credentials creds = credsProvider.getCredentials(authScope);
					if (creds != null) {
						authState.setAuthScheme(new BasicScheme());
						authState.setCredentials(creds);
					}
				}
			}
		};

		((AbstractHttpClient) httpClient).addRequestInterceptor(preemptiveAuth,0);
	}

	/**
	 * Extract the domain and if specified the port number to create an
	 * authorization scope to use with HTTP Request authorization.
	 * 
	 * @param serviceUri
	 * @return AuthScope
	 * @throws NumberFormatException
	 */
	public static AuthScope getAuthscopeFor(String serviceUri) {
		int defaultPort = 80;
		if (serviceUri.startsWith("https://")) {
			defaultPort = 443;
		}
		String[] phase1 = serviceUri.split("://", 2);
		String[] phase2 = phase1[1].split("/", 2);
		String[] phase3 = phase2[0].split(":", 2);
		String domain = phase3[0];
		if (phase3.length == 2) {
			try {
				int port = Integer.parseInt(phase3[1]);
				if (port > 0)
					defaultPort = port;
			} catch (NumberFormatException nfe) {
			}
		}
		return new AuthScope(domain, defaultPort);
	}

	/**
	 * Create an instance of an HttpClient with default settings
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @return HttpClient
	 */
	public static HttpClient getHttpClient(String serviceUri, String consumerKey) {
		return getHttpAuthorizationClient(serviceUri, consumerKey, null, "plain", null);
	}
	
	/**
	 * Create an instance of an HttpClient with default settings.
	 * 
	 * @param serviceUri
	 * @param consumerKey
	 * @param consumerSecret
	 * @param credentials (none, plain, sha256)
	 * @param httpRequest
	 * @return HttpClient
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static HttpClient getHttpAuthorizationClient(String serviceUri,String consumerKey, String consumerSecret, String credentials, HttpRequestBase httpRequest) {
		HttpClient httpClient = new DefaultHttpClient();

		HttpParams httpParams = httpClient.getParams();

		HttpConnectionParams.setConnectionTimeout(httpParams,REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, WAIT_TIMEOUT);
		ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);

		httpParams.setBooleanParameter("http.protocol.handle-redirects", false);

		if (credentials.equals("plain")) {
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
					consumerKey, consumerSecret != null ? consumerSecret : "");

			((AbstractHttpClient) httpClient).getCredentialsProvider()
					.setCredentials(HttpUtils.getAuthscopeFor(serviceUri),
							creds);
			HttpUtils.makeAuthenticationPreemptive(httpClient);
		} else {
			try {
				String auth = consumerKey + ":" + consumerSecret;
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				digest.reset();
				String authCode = bin2hex(digest.digest(auth.getBytes("UTF-8")));
				String authHeader = "Basic " + authCode;
				httpRequest.addHeader("Authorization", authHeader);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return httpClient;
	}
	
	/**
	 * Create an instance of an HttpClient with default settings.
	 * 
	 * @param serviceUri
	 * @return HttpClient
	 */
	@Deprecated
	public static HttpClient getHttpClient(String serviceUri) {
		return getHttpClient();
	}
	
	/**
	 * Create an instance of an HttpClient with default settings.
	 * 
	 * @return HttpClient
	 */
	public static HttpClient getHttpClient() {
		HttpClient httpClient = new DefaultHttpClient();

		HttpParams httpParams = httpClient.getParams();

		HttpConnectionParams.setConnectionTimeout(httpParams,
				REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, WAIT_TIMEOUT);
		ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);

		httpParams.setBooleanParameter("http.protocol.handle-redirects", false);

		return httpClient;
	}


	/**
	 * Read the text contents from an InputStream.
	 * 
	 * @param inputstream
	 * @return String 
	 * @throws IOException
	 */
	public static String getContentsFromInputStream(InputStream is) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		if (is != null) {
			byte[] readbuf = new byte[1024];

			int n;

			while ((n = is.read(readbuf)) >= 0) {
				baos.write(readbuf, 0, n);
			}
		}

		return baos.toString();
	}

	/**
	 * Check if the contentType header indicates JSON content.
	 * 
	 * @param contentType
	 * @return boolean
	 */
	public static boolean isJSON(String contentType) {
		return (contentType != null && contentType.toLowerCase().startsWith("application/json"));
	}

	/**
	 * Check if the contentType header indicates HTML content.
	 * 
	 * @param contentType
	 * @return boolean
	 */
	public static boolean isHTML(String contentType) {
		return (contentType != null && contentType.toLowerCase().startsWith("text/html"));
	}

	/**
	 * Read the headers of an HTTP response and return. Note that header names
	 * are forced to lower case.
	 * 
	 * @param httpResponse
	 * @return HashMap<String, String>
	 */
	public static HashMap<String, String> getHeaders(HttpResponse httpResponse) {
		HashMap<String, String> headerMap = new HashMap<String, String>();

		Header[] responseHeaders = httpResponse.getAllHeaders();

		if (responseHeaders != null) {
			for (Header headerName : responseHeaders) {
				String name = headerName.getName();
				String value = headerName.getValue();

				headerMap.put(name.toLowerCase(), value);
			}
		}

		return headerMap;
	}

	/**
	 * Encode UTF-8 method.
	 * 
	 * @param parameter
	 * @return String 
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeUriParameter(String parameter) {
		String encoded;
		try {
			encoded = URLEncoder.encode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			encoded = "";
		}
		return encoded;
	}

	/**
	 * This method allows to add parameters to the Uri indicated.
	 * 
	 * @param baseUrl
	 * @param paramName
	 * @param paramValue
	 * @return String
	 */
	public static String addUriParameter(String baseUrl, String paramName, String paramValue) {
		String url = baseUrl;
		if (paramName != null && paramValue != null) {
			if (url.indexOf("?") > -1) {
				url = url + "&" + encodeUriParameter(paramName) + "="
						+ encodeUriParameter(paramValue);
			} else {
				url = url + "?" + encodeUriParameter(paramName) + "="
						+ encodeUriParameter(paramValue);
			}
		}
		return url;
	}

	/**
	 * Read the text contents from HttpResponse.
	 * 
	 * @param httpResponse
	 * @return String 
	 * @throws IOException
	 */
	public static String getContentsFromHttpResponse(HttpResponse httpResponse) throws IOException {
		InputStream inputStream = httpResponse.getEntity().getContent();
		StringBuffer jsonResponse = new StringBuffer();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			jsonResponse.append(line);
		}
		inputStream.close();
		return jsonResponse.toString();
	}
	
	/**
	 * Conversion method.
	 * @param data
	 * @return String 
	 */
	public static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,
				data));
	}

}

