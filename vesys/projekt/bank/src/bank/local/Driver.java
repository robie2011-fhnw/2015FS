/*
 * Copyright (c) 2000-2015 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package bank.local;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import bank.InactiveException;
import bank.OverdrawException;

public class Driver implements bank.BankDriver {
	private Bank bank = null;

	@Override
	public void connect(String[] args) {
		bank = new Bank();
		System.out.println("connected...");
	}

	@Override
	public void disconnect() {
		bank = null;
		System.out.println("disconnected...");
	}

	@Override
	public Bank getBank() {
		return bank;
	}

	static class Bank implements bank.Bank {
		private int lastAccountId = 0;
		
		private final Map<String, Account> accounts = new HashMap<>();

		private synchronized int getNewAccountNumber(){
			return lastAccountId++;
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

}