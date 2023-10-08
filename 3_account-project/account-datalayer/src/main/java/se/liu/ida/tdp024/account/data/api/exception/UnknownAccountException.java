package se.liu.ida.tdp024.account.data.api.exception;

public class UnknownAccountException extends RuntimeException{
    public UnknownAccountException(String message)
    {
        super(message);
    }

}
