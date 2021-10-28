package com.rawchen.alipan.utils;

import cn.hutool.core.util.StrUtil;
import com.rawchen.alipan.config.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * HttpClient网络请求工具
 */
public class HttpClientUtil {

	/**
	 * GET
	 *
	 * @param url
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doGet(String url, String path, String method,
			Map<String, String> headers, Map<String, String> querys)
			throws Exception {
		HttpClient httpClient = wrapClient(url);

		HttpGet request = new HttpGet(buildUrl(url, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		return httpClient.execute(request);
	}

	/**
	 * POST
	 *
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return
	 */
	public static String doPost(String url, String paramMap, Map<String, String> headerMap) {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		String result = "";
		// 创建httpClient实例
		httpClient = HttpClients.createDefault();

		// 创建httpPost远程连接实例
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = null;
		// 配置请求参数实例
		requestConfig = RequestConfig.custom().setConnectTimeout(Constants.CONNECT_TIMEOUT)// 设置连接主机服务超时时间
				.setConnectionRequestTimeout(Constants.CONNECTION_REQUEST_TIMEOUT)// 设置连接请求超时时间
				.setSocketTimeout(Constants.SOCKET_TIMEOUT)// 设置读取数据连接超时时间
				.build();

		// 为httpPost实例设置配置
		httpPost.setConfig(requestConfig);
		// 设置请求头
		if (headerMap == null) {
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Access-Control-Allow-Origin", "*");
			httpPost.addHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length,Authorization,Origin,Accept,X-Requested-With");
			httpPost.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS.PUT,PATCH,DELETE");
			httpPost.addHeader("X-Powered-By", "3.2.1");
		} else {
			for (String key : headerMap.keySet()) {
				httpPost.addHeader(key, headerMap.get(key));
			}
		}

		// 封装post请求参数
		if (null != paramMap) {
			// 为httpPost设置封装好的请求参数
			try {
				httpPost.setEntity(new StringEntity(paramMap, "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			// httpClient对象执行post请求,并返回响应参数对象
			httpResponse = httpClient.execute(httpPost);
			// 从响应对象中获取响应内容
			HttpEntity entity = httpResponse.getEntity();
			result = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			if (null != httpResponse) {
				try {
					httpResponse.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != httpClient) {
				try {
					httpClient.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * POST无请求头
	 *
	 * @param url
	 * @param paramMap
	 * @return
	 */
	public static String doPost(String url, String paramMap) {
		return doPost(url, paramMap, null);
	}

	private static String buildUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(host);
		if (!StrUtil.isBlank(path)) {
			sbUrl.append(path);
		}
		if (null != querys) {
			StringBuilder sbQuery = new StringBuilder();
			for (Map.Entry<String, String> query : querys.entrySet()) {
				if (0 < sbQuery.length()) {
					sbQuery.append("&");
				}
				if (StrUtil.isBlank(query.getKey()) && !StrUtil.isBlank(query.getValue())) {
					sbQuery.append(query.getValue());
				}
				if (!StrUtil.isBlank(query.getKey())) {
					sbQuery.append(query.getKey());
					if (!StrUtil.isBlank(query.getValue())) {
						sbQuery.append("=");
						sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
					}
				}
			}
			if (0 < sbQuery.length()) {
				sbUrl.append("?").append(sbQuery);
			}
		}

		return sbUrl.toString();
	}

	private static HttpClient wrapClient(String host) {
		HttpClient httpClient = new DefaultHttpClient();
		if (host.startsWith("https://")) {
			sslClient(httpClient);
		}

		return httpClient;
	}

	private static void sslClient(HttpClient httpClient) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] xcs, String str) {

				}

				public void checkServerTrusted(X509Certificate[] xcs, String str) {

				}
			};
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = httpClient.getConnectionManager();
			SchemeRegistry registry = ccm.getSchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
		} catch (KeyManagementException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}
}