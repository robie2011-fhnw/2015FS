package bank.server.datainterchange;

import java.io.Serializable;

public class QueryResult<T> implements Serializable{
	T data;
	Exception exception;
	public QueryResult(T data, Exception e){
		this.data = data;
		this.exception = e;
	}
	
	public T getResult(){ return data; }
	public Exception getException() { return exception; }
}