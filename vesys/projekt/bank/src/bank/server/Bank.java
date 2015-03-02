package bank.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bank.InactiveException;
import bank.OverdrawException;


public class Bank implements bank.IBankExtended {
	private int lastAccountId = 0;
	
	private final Map<String, Account> accounts = new HashMap<>();

	private synchronized int getNewAccountNumber(){
		return lastAccountId++;
	}
	
	public Bank() {
		//createDummyAccounts(); // TODO: Remove
	}
	
	@Override
	public Set<String> getAccountNumbers() {			
		HashSet<String> numbers = new HashSet<String>();
		
		for(Account account : accounts.values()){
			if(account.isActive()){
				numbers.add(account.getNumber());
			}
		}
								
		return numbers;
	}

	@Override
	public String createAccount(String owner) {
		String newAccountNumber = Integer.toString(getNewAccountNumber());
		Account newAccount = new Account(owner, newAccountNumber); 
		
		accounts.put(newAccountNumber , newAccount);
		return newAccountNumber;
	}

	@Override
	public boolean closeAccount(String number) {
		Account account = accounts.get(number);
		
		if(account == null 
			|| !account.isActive() 
			|| account.getBalance() != 0) {				
			
			return false;
		}
		
		account.makeInactive();
		return true;
	}

	@Override
	public bank.Account getAccount(String number) {
		return accounts.get(number);
	}
	
	@Override
	public void transfer(bank.Account from, bank.Account to, double amount)
			throws IOException, InactiveException, OverdrawException {
		
		if(!from.isActive() || !to.isActive()) throw new InactiveException();
		if(from.getBalance() < amount) throw new OverdrawException();
		if(amount<0) throw new OverdrawException("amount can not be negativ");
		
		from.withdraw(amount);
		to.deposit(amount);
	}

}

