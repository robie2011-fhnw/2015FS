/*
 * Copyright (c) 2000-2013 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package ch.fhnw.ds.compress;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

public class CompressHandler implements Runnable {

	public static final int BUFSIZE = 1024; // Size of receive buffer
	private Socket socket;

	public CompressHandler(Socket socket) {
		this.socket = socket;
	}

	public static void handleCompressClient(Socket socket) {
		try {
			InputStream in = socket.getInputStream();
			GZIPOutputStream out = new GZIPOutputStream(socket.getOutputStream());

			byte[] buffer = new byte[BUFSIZE];
			int bytesRead;
			// Receive until client closes connection, indicated by -1 return
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.finish(); // Flush bytes from GZIPOutputStream
		} catch (IOException ex) {
			System.err.println(ex);
		}

		try { // Close socket
			socket.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void run() {
		handleCompressClient(this.socket);
	}
}
