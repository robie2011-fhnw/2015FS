package bank.server.datainterchange;

import java.io.Serializable;


@FunctionalInterface
public interface ICommand<TExecutionObject, TResult> extends Serializable{
	TResult run(TExecutionObject target) throws Exception;
}