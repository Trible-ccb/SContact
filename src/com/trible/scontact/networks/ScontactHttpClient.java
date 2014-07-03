package com.trible.scontact.networks;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.trible.scontact.BuildConfig;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.utils.Bog;

public class ScontactHttpClient {

	public static DefaultHttpClient client = null;
//	public static DefaultNetWorkExceptionDispatcher ds = new DefaultNetWorkExceptionDispatcher();

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
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SSLSocketFactory sslf = null;
		try {
			sslf = new MySSLSocketFactory(trustStore);
			sslf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		schemeRegistry.register(new Scheme("https", sslf, 443));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schemeRegistry);
		client = new DefaultHttpClient(manager, params);
		CookieStore cookieStore = new BasicCookieStore();

		client.setCookieStore(cookieStore);

	}

	public static void asyncpost(final String url, List<NameValuePair> params,
			final AsyncHttpResponseHandler asynchandler){
		
		final StopWatch sw = new StopWatch();
		final HttpPost post = new HttpPost(url);
		if (params == null) {
			params = new ArrayList<NameValuePair>();
		}
//		post.setEntity(getFormFromParams(params));
		if (SContactApplication.ENV_DEBUG) {
			sw.reset();
			sw.start();
		}
		SimpleAsynTask task = new SimpleAsynTask();
		task.doTask(new AsynTaskListner() {
			int code;
			byte[] content;
			Header[] headers;
			@Override
			public void onTaskDone(NetWorkEvent event) {
				if ( asynchandler == null ) return;
				try {
					if ( code/100 == 2 ){
						asynchandler.onSuccess(code, headers, content);
					} else {
						asynchandler.onFailure(code, headers, content, new Throwable());
					}
				} catch (Exception e) {
					e.printStackTrace();
					if ( asynchandler != null )
						asynchandler.onFailure(-1, null, e.getMessage() == null ? "null".getBytes():e.getMessage().getBytes(), new Throwable());
				}
				asynchandler.onFinish();
			}
			
			@Override
			public void doInBackground() {
				try {
					ResponseHandler<HttpResponse> h = new ResponseHandler<HttpResponse>() {
						@Override
						public HttpResponse handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							code = response.getStatusLine().getStatusCode();
							if ( response.getEntity() != null ){
								InputStream input = response.getEntity().getContent();
								content = IOUtils.toByteArray(input);
							} else {
								content = "".getBytes();
							}
							headers = response.getAllHeaders();
							return response;
						}
					};
					ScontactHttpClient.client.execute(post, h);
					if (SContactApplication.ENV_DEBUG) {
						sw.stop();
						Bog.v("       ---http..." + StringUtils.right(url, 15)
								+ " consume: " + sw.getTime() + " ms");
					}
				} catch (ConnectTimeoutException cte) {
					cte.printStackTrace();
				}catch (Exception e3) {
					e3.printStackTrace();
					
				}
			}
		});
		
	}
	

	public static void post(final String url, List<NameValuePair> params,
			final AsyncHttpResponseHandler asynchandler){

		final StopWatch sw = new StopWatch();
		final HttpPost post = new HttpPost(url);
		if (params == null) {
			params = new ArrayList<NameValuePair>();
		}
//		post.setEntity(getFormFromParams(params));
		if (SContactApplication.ENV_DEBUG) {
			sw.reset();
			sw.start();
		}
		try {
			ResponseHandler<HttpResponse> h = new ResponseHandler<HttpResponse>() {
				public int code = 0;
				byte[] content = null;
				Header[] headers = null;
				@Override
				public HttpResponse handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					code = response.getStatusLine().getStatusCode();
					if ( response.getEntity() != null ){
						InputStream input = response.getEntity().getContent();
						content = IOUtils.toByteArray(input);
					} else {
						content = "".getBytes();
					}
					headers = response.getAllHeaders();
					
					if ( asynchandler == null ) return response;
					try {
						if ( code/100 == 2 ){
							asynchandler.onSuccess(code, headers, content);
						} else {
							asynchandler.onFailure(code, headers, content, new Throwable());
						}
					} catch (Exception e) {
						e.printStackTrace();
						if ( asynchandler != null )
							asynchandler.onFailure(-1, null, e.getMessage() == null ? "null".getBytes():e.getMessage().getBytes(), new Throwable());
					}
					asynchandler.onFinish();
					return response;
				}
			};
			ScontactHttpClient.client.execute(post, h);
			if (BuildConfig.DEBUG) {
				sw.stop();
				Bog.v("       ---http..." + StringUtils.right(url, 15)
						+ " consume: " + sw.getTime() + " ms");
			}

		} catch (ConnectTimeoutException cte) {
			cte.printStackTrace();
		}catch (Exception e3) {
			e3.printStackTrace();
		}
		
	}
	
//	public static HttpResponse post(String url, List<NameValuePair> params)
//			throws ClientProtocolException, IOException {
//		StopWatch sw = new StopWatch();
//		if (params == null) {
//
//			params = new ArrayList<NameValuePair>();
//		}
//		params.add(new BasicNameValuePair("cv", Constant.ApiParmas.CV));
//		HttpPost post = new HttpPost(url);
//		post.setEntity(getFormFromParams(params));
//		HttpResponse ret = null;
//		if (DiigoApp.DEBUG_MODE) {
//			sw.reset();
//			sw.start();
//
//		}
//		try {
//			ret = client.execute(post);
//		} catch (ConnectTimeoutException cte) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setEventType(NetWorkEvent.TIME_OUT_TYPE);
//			ne.setExceptionApi(url);
//			ne.setE(cte);
//			ds.fireListener(ne);
//			cte.printStackTrace();
//			throw cte;
//		} catch (SocketTimeoutException e2) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setEventType(NetWorkEvent.TIME_OUT_TYPE);
//			ne.setExceptionApi(url);
//			ne.setE(e2);
//			ds.fireListener(ne);
//			e2.printStackTrace();
//			throw e2;
//		} catch (ClientProtocolException e) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setExceptionApi(url);
//			ne.setE(e);
//			ds.fireListener(ne);
//			e.printStackTrace();
//			throw e;
//		} catch (IOException e1) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setExceptionApi(url);
//			ne.setE(e1);
//			ds.fireListener(ne);
//			e1.printStackTrace();
//			throw e1;
//		}
//		if (DiigoApp.DEBUG_MODE) {
//			sw.stop();
//			Dog.w("       ---http..." + StringUtils.right(url, 15)
//					+ " consume: " + sw.getTime() + " ms");
//
//		}
//		return ret;
//
//	}

//	public static <T> T post(String url, MultipartEntity entity,
//			ResponseHandler<T> handler) throws ClientProtocolException,
//			IOException {
//		StopWatch sw = new StopWatch();
//		HttpPost post = new HttpPost(url);
//		entity.addPart("cv", new StringBody(Constant.ApiParmas.CV));
//		post.setEntity(entity);
//		T t = null;
//		if (DiigoApp.DEBUG_MODE) {
//			sw.reset();
//			sw.start();
//
//		}
//
//		try {
//			t = ScontactHttpClient.client.execute(post, handler);
//		} catch (ConnectTimeoutException cte) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setEventType(NetWorkEvent.TIME_OUT_TYPE);
//			ne.setExceptionApi(url);
//			ne.setE(cte);
//			ds.fireListener(ne);
//			cte.printStackTrace();
//			throw cte;
//		} catch (SocketTimeoutException e2) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setEventType(NetWorkEvent.TIME_OUT_TYPE);
//			ne.setExceptionApi(url);
//			ne.setE(e2);
//			ds.fireListener(ne);
//			e2.printStackTrace();
//			throw e2;
//		} catch (ClientProtocolException e) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setExceptionApi(url);
//			ne.setE(e);
//			ds.fireListener(ne);
//			e.printStackTrace();
//			throw e;
//		} catch (IOException e1) {
//			NetWorkEvent ne = new NetWorkEvent();
//			ne.setExceptionApi(url);
//			ne.setE(e1);
//			ds.fireListener(ne);
//			e1.printStackTrace();
//			throw e1;
//		}
//
//		if (DiigoApp.DEBUG_MODE) {
//			sw.stop();
//			Dog.w("       ---http..." + StringUtils.right(url, 15)
//					+ " consume: " + sw.getTime() + " ms");
//
//		}
//		return t;
//
//	}

	public static void setCookies() {
		client.getCookieStore().clear();

//		BasicClientCookie cookie1 = new BasicClientCookie(
//				"diigoandlogincookie", "android-.-"
//						+ User.getInstance().getUserName() + "-.-android");
//		cookie1.setDomain(Constant.ApiParmas.DOMAIN);
//		cookie1.setVersion(1);
//		cookie1.setPath("/");
//		client.getCookieStore().addCookie(cookie1);
//
//		BasicClientCookie cookie2 = new BasicClientCookie("CHKIO", User
//				.getInstance().getsKey());
//		cookie2.setDomain(Constant.ApiParmas.DOMAIN);
//		cookie2.setVersion(1);
//		cookie2.setPath("/");
//		client.getCookieStore().addCookie(cookie2);
//
//		DiigoAsyncHttpClient.setCookies();
//		WebViewHttpClient.setCookies();
//		CookieManager cm = CookieManager.getInstance();
//		cm.acceptCookie();
//		cm.removeAllCookie();
//		cm.removeSessionCookie();
//		cm.removeExpiredCookie();
//
//		cm.setCookie(cookie2.getDomain(), cookie2.getValue());

	}

	private static UrlEncodedFormEntity getFormFromParams(
			List<NameValuePair> params) {

		UrlEncodedFormEntity ret = null;
		try {
			ret = new UrlEncodedFormEntity(params, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// no exception utf-8 it support
		}
		return ret;

	}

}
