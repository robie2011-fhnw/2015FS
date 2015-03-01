package bank.server.datainterchange;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import bank.Account;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;

public class Repository {
	Socket socket;
	public Repository(Socket socket){
		this.socket = socket;
	}
	
	<TResult, TTarget>
	QueryCommandBase<TResult, TTarget> 
	runCommandAndReturnQuery(ICommand<TTarget, TResult> command, IExecutionTarget target){
		QueryCommandBase<TResult, TTarget> query = new QueryCommandBase<TResult, TTarget>();
		query.setCommand(command);
		query.setExecutionTarget(target);
		this.query(query);
		return query;
	}
	
	<TResult, TTarget>
	QueryResult<TResult> 
	runCommand(ICommand<TTarget, TResult> command, IExecutionTarget target){
		return runCommandAndReturnQuery(command,target).getResult();
	}
	
	<TResult, TTarget>
	QueryResult<TResult> 
	runCommandOnBank(ICommand<TTarget, TResult> command){
		return runCommand(command, new BankTarget());
	}

	<TResult, TTarget>
	QueryResult<TResult> 
	runCommandOnAccount(ICommand<TTarget, TResult> command, String accountNumber){
		return runCommand(command, new AccountTarget(accountNumber));
	}
	
	
		
	public synchronized void query(QueryCommandBase cmd){
		try {
			
			System.out.println("Client write object into socket");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(cmd);
			out.flush();
			
			System.out.println("Client read object from socket");
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			QueryCommandBase msg = (QueryCommandBase) in.readObject();
			cmd.overrideQueryResult(msg.getResult().getResult());
						
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return null;
	}
	
	public Bank getBank(){
		return new RemoteBank();
	}
	
	
	class RemoteBank implements bank.Bank{

		@Override
		public String createAccount(String owner) throws IOException {
			System.out.println("remote call createAccount");
			ICommand<Bank, String> command = (bank) -> {return bank.createAccount(owner); };
			return runCommandOnBank(command).getResult();
		}

		@Override
		public boolean closeAccount(String number) throws IOException {
			throw new NotImplementedException();
		}

		@Override
		public Set<String> getAccountNumbers() throws IOException {
			System.out.println("remote call getAccountNumbers");
			ICommand<Bank, Set<String>> command = bank -> bank.getAccountNumbers();			
			return runCommandOnBank(command).getResult();
		}

		@Override
		public Account getAccount(String number) throws IOException {
			System.out.println("remote call getAccount");
			ICommand<Bank, Account> command = bank -> bank.getAccount(number);
			
			Account account = runCommandOnBank(command).getResult();
			return new RemoteAccount(account);
		}

		@Override
		public void transfer(Account a, Account b, double amount)
									throws IOException, IllegalArgumentException,
									OverdrawException, InactiveException {
			System.out.println("remote call transfer");

			// TODO: This could lead to sync. issues
			ICommand<Bank, Object> command = bank -> { 
														bank.transfer(getOriginalAccount(a), getOriginalAccount(b), amount); 
														return null;};
			QueryCommandBase<Object, Bank>  query = runCommandAndReturnQuery(command, new BankTarget());
			
			Exception exception = query.getException();
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
		
		private Account getOriginalAccount(Account account){
			return ((RemoteAccount) account).account;
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
			
			return runCommandOnAccount(command, this.getNumber())
					.getResult();
		}

		@Override
		public void deposit(double amount) throws IOException,
				IllegalArgumentException, InactiveException {
			System.out.println("client/account: remote call deposite()");

			ICommand<Account, Object> command = account -> {account.deposit(amount); return null;};
			QueryCommandBase<Object, Account>  query = runCommandAndReturnQuery(command, new AccountTarget(this.getNumber()));
			
			Exception exception = query.getException();
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
			QueryCommandBase<Object, Account>  query = runCommandAndReturnQuery(command, new AccountTarget(this.getNumber()));
			
			Exception exception = query.getException();			
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
			
			return runCommandOnAccount(command, this.getNumber())
					.getResult();
		}
		
	}

}