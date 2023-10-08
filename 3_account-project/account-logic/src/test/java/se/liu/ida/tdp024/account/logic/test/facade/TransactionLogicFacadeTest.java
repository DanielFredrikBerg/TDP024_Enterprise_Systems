package se.liu.ida.tdp024.account.logic.test.facade;

import junit.framework.JUnit4TestCaseFacade;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import se.liu.ida.tdp024.account.data.api.exception.InsufficentAmountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionEntity;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.logic.impl.facade.TransactionLogicFacadeImpl;

import javax.persistence.RollbackException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;


public class TransactionLogicFacadeTest {
    private final TransactionLogicFacade transactionLogicFacade =
            new TransactionLogicFacadeImpl(new TransactionEntityFacadeDB());
    public AccountLogicFacade accountLogicFacade =
            new AccountLogicFacadeImpl(new AccountEntityFacadeDB());
    private static StorageFacade storageFacade = new StorageFacadeDB();

    @After
    public void cleanUp() {
        storageFacade.emptyStorage();
    }

    @Test
    @DisplayName("Credit one account")
    public void creditOneAccountTest() throws UnknownArgumentException {
        accountLogicFacade.createAccount("CHECK", "1", "NORDEA");
        List<AccountEntity> dimitri_accounts = accountLogicFacade.findAllAccountsOfPerson("1");
        String result = transactionLogicFacade.creditAccount(dimitri_accounts.get(0).getId(), 100);
        List<AccountEntity> dimitri_accounts_refreshed = accountLogicFacade.findAllAccountsOfPerson("1");
        Assert.assertEquals(dimitri_accounts_refreshed.get(0).getHoldings(), 100);
        Assert.assertEquals(result, "OK");
    }

    @Test
    @DisplayName("Credit then debit same account")
    public void creditThenDebitSameAccountTest() throws UnknownArgumentException {
        accountLogicFacade.createAccount("CHECK", "1", "SWEDBANK");
        List<AccountEntity> dimitriAccounts = accountLogicFacade.findAllAccountsOfPerson("1");
        String creditResult = transactionLogicFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        String debitResult = transactionLogicFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        List<AccountEntity> dimitriAccountsRefreshed = accountLogicFacade.findAllAccountsOfPerson("1");
        Assert.assertEquals(dimitriAccountsRefreshed.get(0).getHoldings(), 500);
        Assert.assertEquals(creditResult, "OK");
        Assert.assertEquals(debitResult, "OK");
    }
    @Test
    @DisplayName("List transactions of account")
    public void listAllTransactionsOfAccountTest() throws AccountNotFoundException {
        accountLogicFacade.createAccount("CHECK", "1", "SWEDBANK");
        List<AccountEntity> dimitriAccounts = accountLogicFacade.findAllAccountsOfPerson("1");
        transactionLogicFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionLogicFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        transactionLogicFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionLogicFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        transactionLogicFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionLogicFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        transactionLogicFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionLogicFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        List<TransactionEntity> transactionList = transactionLogicFacade.getTransactions(dimitriAccounts.get(0).getId());
        List<AccountEntity> dimitriAccountsRefreshed = accountLogicFacade.findAllAccountsOfPerson("1");
        Assert.assertEquals(dimitriAccountsRefreshed.get(0).getHoldings(), 2000);
        Assert.assertEquals(transactionList.size(), 8);
    }
    @Test(expected = InsufficentAmountException.class)
    @DisplayName("List transactions of account not existing.")
    public void debitEmptyAccount(){
        accountLogicFacade.createAccount("CHECK", "1", "SWEDBANK");
        List<AccountEntity> dimitriAccounts = accountLogicFacade.findAllAccountsOfPerson("1");
        transactionLogicFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
    }
    @Test(expected = UnknownAccountException.class)
    @DisplayName("List transactions of account not existing.")
    public void listAllTransactionsOfNonAccountTest() throws UnknownAccountException {
        List<TransactionEntity> transactionList = transactionLogicFacade.getTransactions(666);
    }
    @Test(expected = UnknownAccountException.class)
    @DisplayName("Credit account not existing.")
    public void creditOfNonAccountTest(){
        transactionLogicFacade.creditAccount(303, 1000);
    }
    @Test(expected = UnknownAccountException.class)
    @DisplayName("Debit account not existing.")
    public void debitOfNonAccountTest(){
        transactionLogicFacade.debitAccount(1337, 1000);
    }
    @Test(expected = InsufficentAmountException.class)
    @DisplayName("Debit account insufficient funds.")
    public void debitAccountInsufficientFundsTest(){
        accountLogicFacade.createAccount("CHECK", "1", "SWEDBANK");
        List<AccountEntity> dimitriAccounts = accountLogicFacade.findAllAccountsOfPerson("1");
        transactionLogicFacade.debitAccount(dimitriAccounts.get(0).getId(), 1000);
    }
}
