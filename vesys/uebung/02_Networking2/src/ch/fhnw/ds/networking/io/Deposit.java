package ch.fhnw.ds.networking.io;




class Deposit implements java.io.Serializable {
	static final long serialVersionUID = 7863022113333675133L;
	
	private int account;
	private double amount;

//	int  x = 100;
//	Date date = new GregorianCalendar().getTime();

	Deposit(int account, double amount) {
		this.account = account;
		this.amount = amount;
	}

	public int getAccount() { return account; }
	public double getAmount() { return amount; }


//	private void readObject(ObjectInputStream stream)
//				throws IOException, ClassNotFoundException {
//		System.err.println("Deposit.readObject called");
//		ObjectInputStream.GetField fields = stream.readFields();
////		x     = fields.get("x", 0);
//		account = fields.get("account", 0);
//		amount  = fields.get("amount", 0.0);
//	}

}

// cd D:\Documents\Kurse\DistributedSystems\Teaching\02_Networking2\02_Networking2\bin
// serialver ch.fhnw.ds.networking.io.Deposit
