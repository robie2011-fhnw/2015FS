package playground;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import bank.server.datainterchange.AccountTarget;
import bank.server.datainterchange.QueryCommandBase;

public class TestServerApplication {

	public static void main(String[] args) {
		
		try {
			Socket socket = new ServerSocket(9999).accept();
			System.out.println("socket created");		
			
			communicateUsingBank2(socket);
		
			//socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		

	}
	
	static void communicateUsingString(Socket socket) throws IOException {
		
		BufferedReader in 
			= new BufferedReader(new InputStreamReader(socket.getInputStream()));
		System.out.println("buffered reader created");
		
		String input = in.readLine();
		System.out.println("firstline received");

		while(input != null && !"".equals(input)){
			System.out.println(input);
			input = in.readLine();
		}				
	}
	
	static void communicateUsingObject(Socket socket) throws Exception{
		ObjectInputStream oostream 
			= new ObjectInputStream(socket.getInputStream());
		
		TransportClass o = (TransportClass) oostream.readObject();
		while(o != null && !socket.isClosed()){
			System.out.println("Object received");
			System.out.println(o.value);
			System.out.println(o.msg);
			
			o = null;
			if(!socket.isInputShutdown() && !socket.isOutputShutdown())
				o = (TransportClass) oostream.readObject();
		}
		
	}
	
	static void communicateUsingBank(Socket socket) throws Exception{
		ObjectInputStream oostream 
		= new ObjectInputStream(socket.getInputStream());
		
		while(oostream.available()<1){
			System.out.println("sleeping: " + oostream.available());
			Thread.sleep(1000);
		}
		
		
		Object o = oostream.readObject();
		while(o != null && !socket.isClosed() && socket.isConnected()){
			System.out.println("Object received");
			if(o instanceof QueryCommandBase){
				System.out.println( (((QueryCommandBase) o).getExecutionTarget()));				
			}
			
			
			o = null;
			while(oostream.available()<1){
				System.out.println("sleeping");
				Thread.sleep(1000);
			}
			if(!socket.isInputShutdown() && !socket.isOutputShutdown())
				o = (QueryCommandBase) oostream.readObject();
		}
		
	}
	
	static void communicateUsingBank2(Socket socket) throws Exception{		
		
		ObjectInputStream oostream 
		= new ObjectInputStream(socket.getInputStream());
		
		ObjectOutputStream out 
			= new ObjectOutputStream(socket.getOutputStream());
		
		
		System.out.println("read");
		Object o = oostream.readObject();
		System.out.println("Object received");			
		QueryCommandBase cmd = (QueryCommandBase) o;
			
		if(cmd.getExecutionTarget() instanceof AccountTarget){
			String accountNumber = ((AccountTarget) cmd.getExecutionTarget() ).getNumber();
			
			String message = "Your number was: " +accountNumber;
			out.writeObject(message);
		}
		
			
		
		
	}

}

