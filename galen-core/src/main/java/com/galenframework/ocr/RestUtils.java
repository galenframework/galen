package com.galenframework.ocr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestUtils {
	public static String executePost(String urlTxt, String body) throws Exception{
		URL url = new URL(urlTxt);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");

		String input = body;

		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		StringBuffer buf = new StringBuffer();
		String output;
		while ((output = br.readLine()) != null) {
			buf.append(output); buf.append('\n');
		}
		conn.disconnect();
		return buf.toString();
	}
}
