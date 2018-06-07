package com.yks.urc.fw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtility {

	private static Logger LOG = LoggerFactory.getLogger(HttpUtility.class);

	public static String httpGet(String url) {
		try {
			OkHttpClient httpClient = new OkHttpClient();
			Request request = new Request.Builder().url(url).build();
			Response response = httpClient.newCall(request).execute();
			return response.body().string(); // 返回的是string 类型，json的mapper可以直接处理
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String sendPost(String url, String param) throws Exception {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LOG.error("发送post请求出错,message={}", e.getMessage());
			throw new Exception(e.getMessage());
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				LOG.error("关闭流出错,message={}", ex.getMessage());
				throw new Exception(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String sendGet(String url, String param) throws Exception {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = url;
			if (param != null && !"".equals(param)) {
				urlName += "?" + param;
			}
			URL realUrl = new URL(urlName);
			URLConnection conn = realUrl.openConnection();// 打开和URL之间的连接
			conn.setRequestProperty("accept", "*/*");// 设置通用的请求属性
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setConnectTimeout(4000);
			conn.connect();// 建立实际的连接
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));// 定义BufferedReader输入流来读取URL的响应
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LOG.error("发送get请求出错,message={}", e.getMessage());
			throw new Exception(e.getMessage());
		} finally {// 使用finally块来关闭输入流
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				LOG.error("关闭流出错,message={}", ex.getMessage());
				throw new Exception(ex.getMessage());
			}
		}
		return result;
	}
//
//	public static void main(String[] args) {
//		String strUrl = "https://oapi.dingtalk.com/gettoken";
//		String ddd = "corpid=dinge8d7141acdb006a135c2f4657eb6378f&corpsecret=1Tf9YqLFKPNF0xJumHQWmZYGt9HdpPjlWT68P1NJu3yWYM1r9hJAajlFbXaZeuis";
//		try {
//			String strResp=sendGet(strUrl, ddd);
//			DingApiRespVO resp = StringUtility.parseObject(strResp, DingApiRespVO.class);
//			System.out.println(resp.access_token+","+resp.sub_dept_id_list+","+resp.expires_in);
//		} catch (Exception e) {
//
//		}
//	}

}
