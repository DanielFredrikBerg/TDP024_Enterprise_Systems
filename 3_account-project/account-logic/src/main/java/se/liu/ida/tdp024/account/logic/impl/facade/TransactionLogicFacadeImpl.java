package se.liu.ida.tdp024.account.logic.impl.facade;

import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.InsufficentAmountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionEntity;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerKafka;

import java.util.List;

public class TransactionLogicFacadeImpl implements TransactionLogicFacade
{
    private final TransactionEntityFacade transactionEntityFacade;
    private static final AccountLoggerKafka accountLoggerKafka = new AccountLoggerKafka();

    public TransactionLogicFacadeImpl(TransactionEntityFacade transactionEntityFacade) {
        this.transactionEntityFacade = transactionEntityFacade;
    }

    @Override
    public String debitAccount(long accountId, int amount)
            throws InsufficentAmountException,
                   UnknownAccountException, DataLayerException {
        String transactionResult = transactionEntityFacade.debitAccount(accountId, amount);
        accountLoggerKafka.log(AccountLogger.TodoLoggerLevel.INFO,
                String.format("Debit of amount %d for account %d", amount, accountId), transactionResult);
        return transactionResult;
    }

    @Override
    public String creditAccount(long accountId, int amount)
            throws InsufficentAmountException,
                   UnknownAccountException, DataLayerException{
        String transactionResult = transactionEntityFacade.creditAccount(accountId, amount);
        accountLoggerKafka.log(AccountLogger.TodoLoggerLevel.INFO,
                String.format("Credit of amount %d for account %d", amount, accountId), transactionResult);
        return transactionResult;
    }

    @Override
    public List<TransactionEntity> getTransactions(long accountId)
            throws UnknownAccountException ,DataLayerException {
        List<TransactionEntity> listResult = transactionEntityFacade.getTransactions(accountId);
        accountLoggerKafka.log(AccountLogger.TodoLoggerLevel.INFO,
                String.format("All transactions for account %d", accountId), listResult.toString());
        return listResult;
    }
}
