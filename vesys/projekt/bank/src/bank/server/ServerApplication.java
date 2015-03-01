package bank.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import bank.server.datainterchange.ICommand;
import bank.server.datainterchange.QueryCommandBase;
import bank.server.datainterchange.AccountTarget;
import bank.server.datainterchange.BankTarget;
import bank.server.datainterchange.IExecutionTarget;


public class ServerApplication {
	
	

	public static void main(String[] args) {
		
		Bank bank = new Bank();
		
		try {
			ServerSocket serverSocket = new ServerSocket(9999);
			while(true){
				
				Socket socket = serverSocket.accept();
				handlingConnection(socket,bank);
			
			}
			//socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
	}
	
	static void handlingConnection(Socket socket, Bank bank) throws Exception{
		System.out.println("");
		System.out.println("socket created");
		System.out.println("===================================");
		
		
		try{
				while(!socket.isClosed()
						&& !socket.isInputShutdown()
						&& socket.isConnected()){
						
						ObjectInputStream oostream 
							= new ObjectInputStream(socket.getInputStream());
								
						
						QueryCommandBase query = (QueryCommandBase) oostream.readObject();
						System.out.println("Object received");
						
						if(query == null) {
							System.out.println("server: nullobject received. exit loop");
							break;
						}
						
						IExecutionTarget target = query.getExecutionTarget();
						if(target instanceof AccountTarget){				
							String number = ((AccountTarget) target).getNumber();
							System.out.println("Request for Accountnr: " +number);
							query.execute(bank.getAccount(number));
							//System.out.println( (Double) query.getResult());
							
						}else if(target instanceof BankTarget){
							System.out.println("bank request");
							query.execute(bank);
							
						}
						
						System.out.println("Writing response");
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject(query);
						out.flush();
				}
			}catch(Exception ex){				
				System.out.println("closing server socket");
				socket.close();
			}
		
	}

}
