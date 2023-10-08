package se.liu.ida.tdp024.account.data.impl.db.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.InputValidator;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;
import se.liu.ida.tdp024.account.data.impl.db.util.InputValidatorImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerKafka;

import javax.persistence.EntityManager;
import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public class AccountEntityFacadeDB implements AccountEntityFacade {

    private final AccountLoggerKafka accountLoggerKafka = new AccountLoggerKafka();
    private final String restRequests = "rest-requests";
    private final InputValidator inputValidator= new InputValidatorImpl();

    @Override
    public String create(String accountType, String personKey, String bankKey)
            throws RollbackException, DataLayerException {

        EntityManager em = EMF.getEntityManager();
        try {
            inputValidator.runAllChecks(accountType, personKey, bankKey);
            em.getTransaction().begin();
            AccountEntity account = new AccountEntity(accountType, personKey, bankKey);
            em.persist(account);
            em.getTransaction().commit();
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.INFO,
                    "Account creation of ", account.toString() + " successful.");

            return "OK";
        } catch (IllegalArgumentException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(),"");
            throw e;
        } catch (UnknownArgumentException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(), "");
            throw e;
        } catch (RollbackException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(), "");
            throw e;
        } catch (Throwable e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(), "");
            throw new DataLayerException(e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public List<AccountEntity> findAllAccountsOfPerson(String person)
        throws IllegalArgumentException, UnknownAccountException, DataLayerException {
        EntityManager em = EMF.getEntityManager();
        try {
            inputValidator.checkPersonType(person);
            List<AccountEntity> query =
                    em.createQuery("SELECT a FROM AccountEntity a WHERE a.personKey = :personKey",
                                    AccountEntity.class)
                            .setParameter("personKey", person)
                            .getResultList();
            if (query.size() == 0)
                throw new UnknownAccountException("Person with personKey " + person + " does not exist.");

            return query;
        } catch (IllegalArgumentException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(), "");
            throw e;
        } catch (UnknownAccountException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(), "");
            throw e;
        } catch (QueryTimeoutException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(), "");
            throw e;
        } catch (Throwable e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    e.getMessage(), "");
            throw new DataLayerException(e.getMessage());
        } finally {
            em.close();
        }
    }


}
