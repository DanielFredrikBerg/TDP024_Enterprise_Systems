package se.liu.ida.tdp024.account.data.impl.db.facade;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.InsufficentAmountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionEntity;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerKafka;
import javax.persistence.*;
import java.util.List;

public class TransactionEntityFacadeDB implements TransactionEntityFacade {

    private final AccountLoggerKafka accountLoggerKafka = new AccountLoggerKafka();
    private final String transactions = "transactions";

    private String recordTransaction(Long accountId,
                                     String type, Integer amount, String status, EntityManager em)
    throws DataLayerException {
        try {
            AccountEntity account = em.find(AccountEntity.class, accountId);
            Transaction transaction = new TransactionEntity(type, amount, status, account);
            em.persist(transaction);
            em.getTransaction().commit();
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.INFO, "Transaction " + transaction + " recorded", "OK");
            return "OK";
        } catch (RollbackException | QueryTimeoutException e) {
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            throw e;
        } catch (Exception e) {
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            throw new DataLayerException("Transaction for " + accountId + " could not be recorded.");
        }

    }


    private String modifyAccountBalance(Long accountId, Integer amount, EntityManager em)
            throws InsufficentAmountException, UnknownAccountException, RollbackException,
            UnknownArgumentException, QueryTimeoutException {

        String status = "FAILED";
        try {
            em.getTransaction().begin();
            AccountEntity account = em.find(AccountEntity.class, accountId, LockModeType.PESSIMISTIC_WRITE);
            if (account == null) {
                throw new UnknownAccountException("Account with accountId " + accountId + " does not exist.");
            }
            int new_holdings = account.getHoldings() + amount;
            if (new_holdings >= 0) {
                account.setHoldings(new_holdings);
                status = "OK";
            } else {
                throw new InsufficentAmountException("Insufficient Account Funds for account: " + accountId);
            }
            em.merge(account);

            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.INFO, "Transaction " + status,
                    "Account " + accountId + " now has " + new_holdings + ".");
            return status;

        } catch (UnknownAccountException | InsufficentAmountException | RollbackException e) {
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(), "");
            throw e;
        } catch (Throwable e) {
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(), "");
            throw new DataLayerException("Credit / Debit failed due to an Unexpected Error, " +
                            "please try again later.");
        }
    }


    @Override
    public String debitAccount(long accountId, int amount)
            throws InsufficentAmountException, QueryTimeoutException,
                   UnknownAccountException, DataLayerException {
        EntityManager entityManager = EMF.getEntityManager();
        String modOpStatus = "FAILED";
        try {
            modOpStatus = modifyAccountBalance(accountId, -1 * amount, entityManager);
            recordTransaction(accountId, "DEBIT", amount,
                    modOpStatus, entityManager);
            return modOpStatus;
        } catch (DataLayerException | InsufficentAmountException | QueryTimeoutException | UnknownAccountException e) {
            throw e;
        } finally {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }


    @Override
    public String creditAccount(long accountId, int amount)
            throws InsufficentAmountException, QueryTimeoutException,
                   UnknownAccountException, DataLayerException {
        EntityManager entityManager = EMF.getEntityManager();
        String modOpStatus = "FAILED";
        try {
            modOpStatus = modifyAccountBalance(accountId, amount, entityManager);
            recordTransaction(accountId, "CREDIT", amount,
                    modOpStatus, entityManager);
            return modOpStatus;
        } catch (DataLayerException | UnknownAccountException | QueryTimeoutException | InsufficentAmountException e) {
            throw e;
        } finally {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }

    }


    private AccountEntity getAccountFromId(long accountId, EntityManager em)
            throws UnknownAccountException {
        TypedQuery<AccountEntity> query = em.createQuery("SELECT a FROM AccountEntity a WHERE a.id = :accountID",
                        AccountEntity.class)
                .setParameter("accountID", accountId);
        List<AccountEntity> resultList = query.getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        } else {
            throw new UnknownAccountException("Account with accountId " + accountId + " does not exist.");
        }
    }


    @Override
    public List<TransactionEntity> getTransactions(long accountId)
            throws UnknownAccountException, DataLayerException {
        accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.INFO,
                "Attempting to get transactions for account", Long.toString(accountId));
        EntityManager em = EMF.getEntityManager();
        try {
            getAccountFromId(accountId, em);
            List<TransactionEntity> query = em.createQuery("SELECT a FROM TransactionEntity a WHERE a.account.id = :accountID",
                            TransactionEntity.class)
                    .setParameter("accountID", accountId)
                    .getResultList();
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.INFO,
                    "Returned list of all account for", Long.toString(accountId));
            return query;
        } catch (UnknownAccountException | QueryTimeoutException | RollbackException e) {
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(), "");
            throw e;
        }catch (Throwable e) {
            accountLoggerKafka.sendKafka(transactions, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(), "");
            throw new DataLayerException("Credit / Debit failed due to an Unexpected Error, " +
                    "please try again later.");
        } finally {
            em.close();
        }
    }
}
