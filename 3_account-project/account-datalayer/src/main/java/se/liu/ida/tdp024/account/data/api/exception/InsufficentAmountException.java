package se.liu.ida.tdp024.account.data.api.exception;

public class InsufficentAmountException extends RuntimeException{
    public InsufficentAmountException(String message)
    {
        super(message);
    }
}
