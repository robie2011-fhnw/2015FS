/*
 * Copyright (c) 2000-2013 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package ch.fhnw.ds.close;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CloseTestServer {
	
	public static void main(String args[]) throws IOException {
		int port = 55555;
		try (ServerSocket server = new ServerSocket(port)) {
			System.out.println("Startet Server on port " + port);
			while (true) {
				Socket s = server.accept();
				Thread t = new Thread(new MessageHandler(s));
				t.start();
			}
		}
	}

	static class MessageHandler implements Runnable {
		private Socket s;
		MessageHandler(Socket s) { this.s = s; }

		public void run() {
			System.out.println("connection from " + s);
			try {
				InputStream in = s.getInputStream();
				
				byte[] buf = new byte[100];
				int read = in.read(buf);
				while (read >= 0) {
					System.out.print(new String(buf, 0, read));
					read = in.read(buf);
				}
				System.out.println("\ndone serving " + s);
			} catch (IOException e) {
				System.err.println(e);
				throw new RuntimeException(e);
			} finally {
				try {
					s.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
	}
}