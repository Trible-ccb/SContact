package com.trible.scontact.networks;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.AccountInfo;

/**
 * 
 * 
 *         provide some static method Make asynchronous HTTP requests, handle
 *         responses in anonymous callbacks
 * 
 *         callback method excute in ui thread
 * 
 */
public class SContactAsyncHttpClient {

	private static AsyncHttpClient client = new AsyncHttpClient();
	static {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, true);
		HttpClientParams.setCookiePolicy(params,
				CookiePolicy.BROWSER_COMPATIBILITY);
		DefaultHttpClient dhc = (DefaultHttpClient) client.getHttpClient();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schemeRegistry);
		dhc.setParams(params);

		client.getHttpClient().getParams()
				.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		client.addHeader("User-Agent", "Mozilla/5.0");
		client.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
//		client.get(url, params, responseHandler);
		ScontactHttpClient.asyncpost(url, null, responseHandler);

	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		//because the cache bug in loop that request does not send
//		client.post(url, params, responseHandler);
		//we use DefaultHttpClient instead. wrapped it in asynchronized way
		ScontactHttpClient.asyncpost(url, null, responseHandler);
	}

	public static void cancel(Context c,boolean flg){
		client.cancelRequests(c, flg);
	}
	public static void refreshCookie(){
		DefaultHttpClient dhc = ScontactHttpClient.client;
		DefaultHttpClient dhc2 = (DefaultHttpClient) SContactAsyncHttpClient.client.getHttpClient();
		setCookies(dhc2);
		setCookies(dhc);
	}
	public static void setCookies(DefaultHttpClient dhc) {
//		DefaultHttpClient dhc = ScontactHttpClient.client;
		dhc.getCookieStore().clear();
		String uid = "";
		String cookie = "";
		if ( AccountInfo.getInstance().getId() != null ){
			uid = AccountInfo.getInstance().getId() + "";
		}
		if ( AccountInfo.getInstance().getCookie() != null ){
			cookie = AccountInfo.getInstance().getCookie();
		}
		
		BasicClientCookie cookie1 = new BasicClientCookie(
				"requestcookie",
						""
//						+ AccountInfo.getInstance().getDisplayName() + "_"
						+ uid + "_._"
						+ cookie
						);
		cookie1.setDomain(SContactApplication.DEV_IP);
		cookie1.setVersion(1);
		cookie1.setPath("/");
		dhc.getCookieStore().addCookie(cookie1);

//		BasicClientCookie cookie2 = new BasicClientCookie("CHKIO", User
//				.getInstance().getsKey());
//		cookie2.setDomain(SContactApplication.IP);
//		cookie2.setVersion(1);
//		cookie2.setPath("/");
//		dhc.getCookieStore().addCookie(cookie2);

	}
}
