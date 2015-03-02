package bank.local;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bank.Bank;
import bank.InactiveException;

public class TestClient {
	Socket socket;
	Bank bank;
	Driver driver;
	
	@Before
	public void setUp() throws UnknownHostException, IOException{
		socket = new Socket("localhost", 9999);
		Repository repository = new Repository(socket);
		driver = new Driver();
		driver.connect(null);
		bank = repository.getBank();
	}
	
	@Test
	public void test1() throws UnknownHostException, IOException{	
		
		Set<String> list = bank.getAccountNumbers();
		for(String s : list){
			System.out.println(s);
		}
	}
	
	@Test
	public void test2() throws IOException, IllegalArgumentException, InactiveException{
		String num = bank.createAccount("bla");
		//bank.getAccount(num).deposit(2321);
	}
	
	
	@After
	public void tearDown() throws IOException{
		driver.disconnect();
	}
}
