package com.tavant.droid.security.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.tavant.droid.security.utils.WSConstants;

/**
 * @author tavant
 *
 * This class is used to set up the HttpConnection with required perameters.
 */
public class HttpManager {

	public static DefaultHttpClient sClient;

	static {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 40 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpClientParams.setRedirecting(params, false);

		HttpProtocolParams.setUserAgent(params, "gladio");

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		//if(WSConstants.PROTOCOL.equals("http://")){
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		//}else{
		SSLSocketFactory sslfactory=getSSLSocketFactory(); 
		if(sslfactory!=null){
			schemeRegistry.register(new Scheme("https", sslfactory, 443));
		}else{
			schemeRegistry.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
		}
		//}
		
		ClientConnectionManager manager= new ThreadSafeClientConnManager(
				params, schemeRegistry);
		sClient = new DefaultHttpClient(manager, params);
	}

	private static SSLSocketFactory getSSLSocketFactory(){


		KeyStore trustStore=null;
		SSLSocketFactory sf=null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sf= new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return sf;	
	}

	private HttpManager() {
	}

	public static HttpResponse execute(HttpHead head) throws IOException {
		if (sClient != null)
			return sClient.execute(head);
		else
			return null;
	}

	public static HttpResponse execute(HttpHost host, HttpGet get)
			throws IOException {
		if (sClient != null)
			return sClient.execute(host, get);
		else
			return null;
	}

	public static HttpResponse execute(HttpRequestBase request)
			throws IOException {
		if (sClient != null)
		{
			return sClient.execute(request);
		}
		else
			return null;
	}






	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}













	public static void clear() {
		if (sClient != null)
			sClient = null;
	}
}