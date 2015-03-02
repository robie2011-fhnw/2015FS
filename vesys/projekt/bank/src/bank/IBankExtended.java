package bank;

import java.io.IOException;

public interface IBankExtended extends Bank {
	
	public default void transfer(String accountNumberA, String accountNumberB, double amount)
			throws IOException, IllegalArgumentException, OverdrawException,
			InactiveException{
		
		transfer(getAccount(accountNumberA),
				getAccount(accountNumberB), 
				amount);
	}
	
}
