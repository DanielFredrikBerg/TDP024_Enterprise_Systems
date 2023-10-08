package se.liu.ida.tdp024.account.data.api.facade;

import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.InsufficentAmountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionEntity;

import javax.persistence.EntityManager;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface TransactionEntityFacade {

    String debitAccount(long accountId, int amount)
            throws InsufficentAmountException, UnknownAccountException, DataLayerException;

    String creditAccount(long accountId, int amount)
            throws InsufficentAmountException, UnknownAccountException, DataLayerException;

    List<TransactionEntity> getTransactions(long accountId)
            throws UnknownAccountException, DataLayerException;
}
