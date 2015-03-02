package bank.local;

import java.net.Socket;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import bank.server.ServerApplication;

public class TestServer {

	@Before
	public void setUp(){
		

	}
	
	@Test
	public void test1(){
		ServerApplication.main(null);
		
		
		Thread t = new Thread( () -> {
			Socket socket;
			try {
				socket = new Socket("localhost", 9999);
				Repository repository = new Repository(socket);
				
				
				Driver d = new Driver();
				d.connect(null);
				bank.Bank bank = repository.getBank();
				
				Set<String> list = bank.getAccountNumbers();
				for(String s : list){
					System.out.println(s);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		} );		
		t.setDaemon(true);
		t.start();
		
		
	}
}
