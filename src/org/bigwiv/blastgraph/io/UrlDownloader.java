package org.bigwiv.blastgraph.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 * @author yeyanbo
 */
public class UrlDownloader {

	public static boolean saveUrlAs(String urlString, File fileName)
			throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		DataInputStream in = new DataInputStream(connection.getInputStream());
		DataOutputStream out = new DataOutputStream(new FileOutputStream(
				fileName));
		byte[] buffer = new byte[4096];
		int count = 0;
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
		}
		out.close();
		in.close();
		return true;
	}

	public static String getDocumentAt(String urlString) throws IOException {
		StringBuffer document = new StringBuffer();
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			document.append(line + "\n");
		}
		reader.close();
		return document.toString();
	}
}
