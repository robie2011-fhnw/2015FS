package playground;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

import bank.Account;
import bank.server.datainterchange.QueryCommandBase;
import bank.server.datainterchange.AccountTarget;


public class TestClient {

	public static void main(String[] args) {
		Socket socket;
		try {
			socket = new Socket("127.0.0.1", 9999);			
			useBankObject(socket);			
			shutdownSocket(socket);
			socket.close();
			
		} catch (Exception e) {e.printStackTrace();} 
	}

	void useString(Socket socket) throws IOException{
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		
		out.println("Hallo Welt\n");
		out.print("");
		out.flush();
	}
	
	static void useObject(Socket socket) throws IOException{		
		ObjectOutputStream oostream = new ObjectOutputStream(socket.getOutputStream());
		TransportClass t = new TransportClass();
		t.msg = "news for you!";
		t.value = 123456789;
		
		oostream.writeObject(t);
		oostream.writeObject(null);
		oostream.flush();
	}
	
	static void useBankObject(Socket socket) throws IOException, ClassNotFoundException{
	/*
		AbstractQueryCommand<Double, Account> cmd = new AbstractQueryCommand<Double, Account>() {

			@Override
			public void command(Account targetObject) throws Exception {
				setResult(targetObject.getBalance());				
			}
		};
		
		cmd.setExecutionTarget(new AccountTarget("1"));
		
		ObjectOutputStream oostream = new ObjectOutputStream(socket.getOutputStream());
		
		oostream.writeObject(cmd);
		
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		
		AbstractQueryCommand msg = (AbstractQueryCommand) in.readObject();
		
		System.out.println("Message was: " +msg.getResult());
	*/
	}
	
	
	static void shutdownSocket(Socket socket) throws IOException, InterruptedException{
		socket.shutdownOutput();
		socket.shutdownInput();
		Thread.sleep(2000);
	}
}


class SenderThread extends Thread{
	Queue<Object> outbox;
	ObjectOutputStream oostream;
	Thread mainThread;
	

	public SenderThread(Socket socket, Queue<Object> outbox, Thread mainThread){
		this.outbox = outbox;
		this.mainThread = mainThread;
		try{
			oostream = new ObjectOutputStream(socket.getOutputStream());
		}catch(Exception e){}
	}
	
	public void tryToSend(){
		while(outbox.size()>0){
			Object o = outbox.poll();
			try{			
				System.out.println("writing Object to stream");
				oostream.writeObject(o);
				oostream.flush();
			}catch(Exception e){}
		}

	}
	
	@Override
	public void run() {
		while(true){			
			tryToSend();		
			System.out.println("Going to sleep");
			synchronized (mainThread) {
				try {
					mainThread.wait();
					System.out.println("waked up");
				} catch (InterruptedException e) {}
			}
		
		}

	}
}
