/*
 * Copyright (c) 2000-2015 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package bank.local;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import bank.server.datainterchange.Repository;

public class Driver implements bank.BankDriver {
	private bank.Bank bank = null;
	private Repository repository;
	Socket socket;

	@Override
	public void connect(String[] args) {
		try {
			socket = new Socket("localhost", 9999);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.out.println("disconnected...");
	}

	@Override
	public bank.Bank getBank() {
		return bank;
	}

	/*
	
	static class LocalBank implements bank.Bank {
		private int lastAccountId = 0;
		private Repository repository;
		
		private final Map<String, Account> accounts = new HashMap<>();

		private synchronized int getNewAccountNumber(){
			return lastAccountId++;
		}
		
		public LocalBank(Repository repository) {
			this.repository = repository;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public Set<String> getAccountNumbers() {

			return null;
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
				|| !account.active 
				|| account.getBalance() != 0) {				
				
				return false;
			}
			
			account.active = false;
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

	static class Account implements bank.Account {
		private String number;
		private String owner;
		private double balance;
		private boolean active = true;

		Account(String owner, String number) {
			this.owner = owner;
			this.number = number;
		}

		@Override
		public double getBalance() {
			return balance;
		}

		@Override
		public String getOwner() {
			return owner;
		}

		@Override
		public String getNumber() {
			return number;
		}

		@Override
		public boolean isActive() {
			return active;
		}

		@Override
		public void deposit(double amount) throws InactiveException {
			// exception for negative amount?
			if(!isActive()) throw new InactiveException();
			if(amount<0) throw new InactiveException("amount can not be negativ");
			
			balance += amount;
		}

		@Override
		public void withdraw(double amount) throws InactiveException, OverdrawException {
			if(!isActive()) throw new InactiveException();
			if(getBalance()<amount) throw new OverdrawException();
			if(amount<0) throw new OverdrawException("amount can not be negativ");
			
			balance -= amount;
		}

	}
	*/
	
}





/*

class AccountGetBalance extends AbstractQueryCommand<Double, bank.server.Account>{
	public AccountGetBalance(String accountNumber) {
		setExecutionTarget(new AccountTarget(accountNumber));
	}

	@Override
	public void command(bank.server.Account targetObject) throws Exception {
		System.out.println("Balance is: " + targetObject.getBalance());
		setResult(targetObject.getBalance());		
	}
	
}
*/