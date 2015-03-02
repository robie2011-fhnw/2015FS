/*
 * Copyright (c) 2000-2015 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package bank.local;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Driver implements bank.BankDriver {
	private bank.Bank bank = null;
	private Repository repository;
	Socket socket;

	@Override
	public void connect(String[] args) {
		try {
			String server = "localhost";
			int port = 9999;
			if(args.length>0){
				server = args[0];
			}
			
			if(args.length>1){
				port = Integer.parseInt(args[1]);
			}
			
			socket = new Socket(server, port);
			repository = new Repository(socket);
			System.out.println("connected...");
		} catch (IOException e) {
		}
		
		bank = repository.getBank();
		
	}

	@Override
	public void disconnect() {
		bank = null;
		try {
			new ObjectOutputStream(socket.getOutputStream()).writeObject(null);
			socket.shutdownOutput();
			socket.shutdownInput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		System.out.println("disconnected...");
	}

	@Override
	public bank.Bank getBank() {
		return bank;
	}

}


