/*
 * Copyright (c) 2000-2013 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package ch.fhnw.ds.compress;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CompressServer {

	public static void main(String[] args) throws IOException {
		int port = 55555;
		if (args.length > 0) { 
			port = Integer.parseInt(args[0]);
		}

		try(ServerSocket servSock = new ServerSocket(port)) {
			System.out.println("CompressServer started on port "+port);
	
			while (true) {
				Socket socket = servSock.accept();
				Thread thread = new Thread(new CompressHandler(socket));
				thread.start();
			}
		}
	}
}
