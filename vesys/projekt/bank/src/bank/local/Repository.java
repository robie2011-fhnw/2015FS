package bank.local;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

import bank.Account;
import bank.Bank;
import bank.IBankExtended;
import bank.InactiveException;
import bank.OverdrawException;
import bank.server.datainterchange.AccountTarget;
import bank.server.datainterchange.BankTarget;
import bank.server.datainterchange.ICommand;
import bank.server.datainterchange.IExecutionTarget;
import bank.server.datainterchange.QueryCommandBase;
import bank.server.datainterchange.QueryResult;



public class Repository {
	Socket socket;
	public Repository(Socket socket){
		this.socket = socket;
	}
	
	<TResult, TTarget>
	QueryResult<TResult> 
	runCommandAndReturnQuery(ICommand<TTarget, TResult> command, IExecutionTarget target){
		QueryCommandBase<TResult, TTarget> query = new QueryCommandBase<TResult, TTarget>();
		query.setCommand(command);
		query.setExecutionTarget(target);
		QueryResult<TResult> result = this.query(query);
		return result;
	}
	
	<TResult, TTarget>
	TResult 
	runCommand(ICommand<TTarget, TResult> command, IExecutionTarget target){
			return runCommandAndReturnQuery(command,target).getResult();
	}
	
	<TResult, TTarget>
	TResult 
	runCommandOnBank(ICommand<TTarget, TResult> command){
		return runCommand(command, new BankTarget());
	}

	<TResult, TTarget>
	TResult 
	runCommandOnAccount(ICommand<TTarget, TResult> command, String accountNumber){
		return runCommand(command, new AccountTarget(accountNumber));
	}
	
	public synchronized <TResult, TTarget> 
	QueryResult<TResult> query(QueryCommandBase<TResult, TTarget> cmd){
		try {
			
			System.out.println("Client write object into socket");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(cmd);
			out.flush();
			
			System.out.println("Client read object from socket");
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			QueryResult<TResult> result = (QueryResult<TResult>) in.readObject();
			return result;
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Bank getBank(){
		return new RemoteBank();
	}

	
	
	class RemoteBank implements bank.Bank{

		@Override
		public String createAccount(String owner) throws IOException {
			System.out.println("remote call createAccount");
			ICommand<Bank, String> command = (bank) -> {return bank.createAccount(owner); };
			
			return runCommandOnBank(command);
		}

		@Override
		public boolean closeAccount(String number) throws IOException {
			System.out.println("remote call closeAccount");
			ICommand<Bank, Boolean> command = (bank) -> {return bank.closeAccount(number); };
			return runCommandOnBank(command);
		}

		@Override
		public Set<String> getAccountNumbers() throws IOException {
			System.out.println("remote call getAccountNumbers");
			ICommand<Bank, Set<String>> command = bank -> bank.getAccountNumbers();			
			return runCommandOnBank(command);
		}

		@Override
		public Account getAccount(String number) throws IOException {
			System.out.println("remote call getAccount");
			ICommand<Bank, Account> command = bank -> bank.getAccount(number);
			
			Account account = runCommandOnBank(command);
			if(account == null) return null;
			return new RemoteAccount(account);
		}

		@Override
		public void transfer(Account a, Account b, double amount)
									throws IOException, IllegalArgumentException,
									OverdrawException, InactiveException {
			System.out.println("remote call transfer");

			// TODO: This could lead to sync. issues
			String numberA = a.getNumber();
			String numberB = b.getNumber();
			ICommand<IBankExtended, Object> command = bank -> { 
														bank.transfer(numberA, numberB, amount); 
														return null;};
			QueryResult<Object> result = runCommandAndReturnQuery(command, new BankTarget());
			
			Exception exception = result.getException();
			if(exception == null){
				return;
			}else{
				if(exception instanceof InactiveException){
					throw (InactiveException) exception;
				}else if(exception instanceof IllegalArgumentException){
					throw (IllegalArgumentException) exception;
				}else if(exception instanceof IOException){
					throw (IOException) exception;
				}else if(exception instanceof OverdrawException){
					throw (OverdrawException) exception;
				}
			}
		}
									
	}
	
	class RemoteAccount implements bank.Account{
		bank.Account account;		
		
		public RemoteAccount(bank.Account account) {
			this.account = account;
		}

		@Override
		public String getNumber() throws IOException {
			return account.getNumber();
		}

		@Override
		public String getOwner() throws IOException {
			return account.getOwner();
		}

		@Override
		public boolean isActive() throws IOException {
			System.out.println("client/account: remote call isActive()");
			ICommand<Account, Boolean> command = account -> account.isActive();
			
			return runCommandOnAccount(command, this.getNumber());
		}

		@Override
		public void deposit(double amount) throws IOException,
				IllegalArgumentException, InactiveException {
			System.out.println("client/account: remote call deposite()");

			ICommand<Account, Object> command = account -> {account.deposit(amount); return null;};
			QueryResult<Object> result = runCommandAndReturnQuery(command, new AccountTarget(this.getNumber()));
			
			Exception exception = result.getException();
			if(exception == null){
				return;
			}else{
				if(exception instanceof InactiveException){
					throw (InactiveException) exception;
				}else if(exception instanceof IllegalArgumentException){
					throw (IllegalArgumentException) exception;
				}else if(exception instanceof IOException){
					throw (IOException) exception;
				}
			}
		}

		@Override
		public void withdraw(double amount) throws IOException,
				IllegalArgumentException, OverdrawException, InactiveException {
			ICommand<Account, Object> command = account -> {account.withdraw(amount); return null;};			
			QueryResult<Object> result = runCommandAndReturnQuery(command, new AccountTarget(this.getNumber()));
			
			Exception exception = result.getException();			
			if(exception == null){
				return;
			}else{
				if(exception instanceof InactiveException){
					throw (InactiveException) exception;
				}else if(exception instanceof IllegalArgumentException){
					throw (IllegalArgumentException) exception;
				}else if(exception instanceof IOException){
					throw (IOException) exception;
				}else if(exception instanceof OverdrawException){
					throw (OverdrawException) exception;
				}
			}
		}

		@Override
		public double getBalance() throws IOException {
			System.out.println("client/account: remote call getBalance()");
			ICommand<Account, Double> command = account -> account.getBalance();
			
			return runCommandOnAccount(command, this.getNumber());
		}
		
	}

	
}
