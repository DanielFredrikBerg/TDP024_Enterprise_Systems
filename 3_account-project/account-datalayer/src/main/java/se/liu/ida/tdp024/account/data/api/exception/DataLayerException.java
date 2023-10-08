package se.liu.ida.tdp024.account.data.api.exception;

import javax.persistence.RollbackException;

public class DataLayerException extends RollbackException {
    public DataLayerException(String message)
    {
        super(message);
    }
}
