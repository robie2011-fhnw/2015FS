package bank.local;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bank.server.ServerApplication;

public class TestAccount {
	Thread t;
	
	
	@Before
	public void setUp(){
		
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ServerApplication.main(null);
				
			}
		});
		t.setDaemon(true);
		t.start();
		
	}
	
	@After
	public void tearDown(){
		//t.stop();
	}
	
	/*
	@Test
	public void account() throws UnknownHostException, IOException{
		
		Socket socket = new Socket("localhost", 9999);
		Repository repository = new Repository(socket);
		
		
		Driver d = new Driver();
		d.connect(null);
		LocalBank bank = d.getBank();
		
		//AccountGetBalance a = new AccountGetBalance("1");
		
		System.out.println("client result: ");
		System.out.println(a.getResult());
		
		
		assertEquals(1,1);
	}
	*/
	
	@Test
	public void getAccounts() throws UnknownHostException, IOException{
		Socket socket = new Socket("localhost", 9999);
		Repository repository = new Repository(socket);
		
		
		Driver d = new Driver();
		d.connect(null);
		bank.Bank bank = repository.getBank();
		
		Set<String> list = bank.getAccountNumbers();
		for(String s : list){
			System.out.println(s);
		}
	}
	
	@Test
	public void remoteBank() throws UnknownHostException, IOException{
		Socket socket = new Socket("localhost", 9999);
		Repository repository = new Repository(socket);
		Set<String> list = repository.getBank().getAccountNumbers();
		for(String s : list){
			System.out.println(s);
		}
	}
}
