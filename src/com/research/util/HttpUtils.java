package com.research.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpUtils {
	/**
	 * 
	 * @param username
	 * @param password
	 * @return null---->error text--->success
	 */
	public static String loginByGet(String username, String password) {
		// 提交数据到服务器
		// 拼装路径

		try {
			String path = "http://10.10.5.31:8080/web/LoginServlet?username="
					+ URLEncoder.encode(username, "UTF-8") + "&password="
					+ password;
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			int code = conn.getResponseCode();
			if (code == 200) {
				// 请求成功
				InputStream is = conn.getInputStream();
				String text = readInputStream(is);
				return text;

			} else {
				return null;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return null---->error text--->success
	 */
	public static String loginByPost(String username, String password) {
		// 提交数据到服务器
		// 拼装路径

		try {
			String path = "http://10.10.5.31:8080/web/LoginServlet";
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			// 准备数据
			String data = "username=" + URLEncoder.encode(username, "UTF-8")
					+ "&password=" + password;
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", data.length() + "");

			// post实际上是浏览器把数据写给了服务器
			conn.setDoOutput(true);// UrlConnection允许向外传数据
			OutputStream os = conn.getOutputStream();
			os.write(data.getBytes());
			int code = conn.getResponseCode();
			if (code == 200) {
				// 请求成功
				InputStream is = conn.getInputStream();
				String text = readInputStream(is);
				return text;

			} else {
				return null;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String readInputStream(InputStream is) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int length = 0;
		byte[] buffer = new byte[1024];
		try {
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			is.close();
			baos.close();
			byte[] result = baos.toByteArray();
			// 试着解析result里的字符串
			String temp = new String(result);
			if (temp.contains("utf-8")) {
				return temp;
			} else if (temp.contains("gb2312")) {
				return new String(result, "gb2312");
			} else if (temp.contains("gbk")) {
				return new String(result, "gbk");
			} else {
				return temp;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "获取失败";
		}

	}
}
