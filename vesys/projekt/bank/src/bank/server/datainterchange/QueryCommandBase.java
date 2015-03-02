package bank.server.datainterchange;

import java.io.Serializable;

public class QueryCommandBase<TResult, TTarget> 
	implements Serializable{
	
	boolean hasResponse = false;
	boolean successfull = false;
	Exception exception = null;
	TResult result;
	IExecutionTarget executionTarget;
	ICommand<TTarget, TResult> command;
	
	protected void setResult(TResult result){
		this.result = result;
	}
	
	public void setException(Exception exception){
		this.exception = exception;
	}
	
	public Exception getException(){
		return exception;
	}
	
	public boolean hasResponse(){
		return hasResponse;
	}		
	
	public QueryResult<TResult> getResult(){
		return new QueryResult<TResult>(result, getException());
	}
	
	public boolean successful(){
		return successfull;
	}
	
	public void setExecutionTarget(IExecutionTarget target){
		this.executionTarget = target;
	}
	
	public IExecutionTarget getExecutionTarget(){
		return executionTarget;
	}
	
	public QueryResult<TResult> execute(TTarget target){
		TResult result = null;
		Exception exception = null;
		try{
			result = this.command.run(target);
		}catch(Exception e){
			exception = e;
		}
		return new QueryResult<TResult>(result, exception);
	}		
	
	public void setCommand(ICommand<TTarget, TResult> cmd){
		this.command = cmd;
	}
	
	public ICommand<TTarget, TResult> getCommand(){
		return this.command;
	}
	
	public void overrideQueryResult(TResult result){
		this.result = result;  
	}
}

