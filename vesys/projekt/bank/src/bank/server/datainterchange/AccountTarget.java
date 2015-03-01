package bank.server.datainterchange;

public class AccountTarget implements IExecutionTarget{
	String number;
	public AccountTarget(String number){ this.number = number; }	
	public String getNumber() { return number;	}
}
