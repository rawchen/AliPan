package com.rawchen.alipan.utils;

import com.rawchen.alipan.config.Const;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 *  调用 OAuth2 获取 token 及后续业务请求工具
 */
public class HttpClientUtil {

	public static String doPost(String url, String paramMap) {
		return doPost(url, paramMap, null);
	}

	/**
	 * 获取访问令牌access_token
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
		requestConfig = RequestConfig.custom().setConnectTimeout(Const.CONNECT_TIMEOUT)// 设置连接主机服务超时时间
				.setConnectionRequestTimeout(Const.CONNECTION_REQUEST_TIMEOUT)// 设置连接请求超时时间
				.setSocketTimeout(Const.SOCKET_TIMEOUT)// 设置读取数据连接超时时间
				.build();

		// 为httpPost实例设置配置
		httpPost.setConfig(requestConfig);
		// 设置请求头
		if (headerMap == null){
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
	 * 后续业务接口请求
	 * @param httpUrl
	 * @param jsonStr
	 * @param headerMap
	 * @return
	 */
	public static String requestPayload(String httpUrl, String jsonStr, Map<String, String> headerMap) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse closeableHttpResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(httpUrl);

			// 设置请求头
			if (headerMap != null){
				for (String key : headerMap.keySet()) {
					httpPost.addHeader(key, headerMap.get(key));
				}
			}

			StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			closeableHttpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = closeableHttpResponse.getEntity();
			return EntityUtils.toString(httpEntity);// 响应内容
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (closeableHttpResponse != null) {
				try {
					closeableHttpResponse.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}