package bank.server.datainterchange;

public class QueryResult<T>{
	T result;
	Exception exception;
	
	public QueryResult(T result, Exception e){
		this.result = result;
	}
	
	public QueryResult(T result){
		this(result, null);
	}
	
	public T getResult(){
		return result;
	}
}