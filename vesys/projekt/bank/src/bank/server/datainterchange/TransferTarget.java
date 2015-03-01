package bank.server.datainterchange;

import java.io.Serializable;

public class TransferTarget implements IExecutionTarget, Serializable {
	String a,b;
	public TransferTarget(String accountA, String accountB){
		a = accountA;
		b = accountB;
	}
	
	public String getAccountA(){
		return a;
	}
	
	public String getAccountB(){
		return b;
	}
	
}
