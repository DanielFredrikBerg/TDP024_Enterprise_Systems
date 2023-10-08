package se.liu.ida.tdp024.account.data.test.facade;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.InsufficentAmountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionEntity;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import java.util.Date;
import java.util.List;

public class TransactionEntityFacadeTest {

    private final TransactionEntityFacade transactionEntityFacade = new TransactionEntityFacadeDB();
    private final AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB();
    private static final StorageFacade storageFacade = new StorageFacadeDB();
    @AfterClass
    public static void cleanUp() {
        storageFacade.emptyStorage();
    }

    @Test
    public void setterAndGetterTests() {
        AccountEntity account = new AccountEntity();
        TransactionEntity transaction = new TransactionEntity("CREDIT", 200, "OK", account);
        String date = new Date().toString();
        Assert.assertEquals(transaction.getStatus(), "OK");
        Assert.assertEquals(transaction.getAmount(), 200);
        Assert.assertEquals(transaction.getType(), "CREDIT");
        Assert.assertEquals(transaction.getAccount(), account);
        Assert.assertEquals(transaction.getDate(), date);
    }

    @Test
    @DisplayName("Credit one account")
    public void creditOneAccountTest() throws UnknownArgumentException {
        accountEntityFacade.create("CHECK", "33", "ASSHOLEBANK");
        List<AccountEntity> dimitri_accounts = accountEntityFacade.findAllAccountsOfPerson("33");
        String result = transactionEntityFacade.creditAccount(dimitri_accounts.get(0).getId(), 100);
        List<AccountEntity> dimitri_accounts_refreshed = accountEntityFacade.findAllAccountsOfPerson("33");
        Assert.assertEquals( 100, dimitri_accounts_refreshed.get(0).getHoldings());
        Assert.assertEquals(result, "OK");
    }

    @Test
    @DisplayName("Credit then debit same account")
    public void creditThenDebitSameAccountTest() throws UnknownArgumentException {
        accountEntityFacade.create("CHECK", "22", "ABANK");
        List<AccountEntity> dimitriAccounts = accountEntityFacade.findAllAccountsOfPerson("22");
        String creditResult = transactionEntityFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        String debitResult = transactionEntityFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        List<AccountEntity> dimitriAccountsRefreshed = accountEntityFacade.findAllAccountsOfPerson("22");
        Assert.assertEquals(dimitriAccountsRefreshed.get(0).getHoldings(), 500);
        Assert.assertEquals(creditResult, "OK");
        Assert.assertEquals(debitResult, "OK");
    }
    @Test
    @DisplayName("List transactions of account")
    public void listAllTransactionsOfAccountTest() throws UnknownAccountException {
        accountEntityFacade.create("CHECK", "65", "ABANK");
        List<AccountEntity> dimitriAccounts = accountEntityFacade.findAllAccountsOfPerson("65");
        transactionEntityFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionEntityFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        transactionEntityFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionEntityFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        transactionEntityFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionEntityFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        transactionEntityFacade.creditAccount(dimitriAccounts.get(0).getId(), 1000);
        transactionEntityFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
        List<AccountEntity> dimitriAccountsRefreshed = accountEntityFacade.findAllAccountsOfPerson("65");
        List<TransactionEntity> transactionList = transactionEntityFacade.getTransactions(dimitriAccounts.get(0).getId());
        Assert.assertEquals(dimitriAccountsRefreshed.get(0).getHoldings(), 2000);
        Assert.assertEquals(transactionList.size(), 8);
    }

    @Test(expected = InsufficentAmountException.class)
    @DisplayName("List transactions of account not existing.")
    public void debitEmptyAccount(){
        accountEntityFacade.create("CHECK", "89", "ABANK");
        List<AccountEntity> dimitriAccounts = accountEntityFacade.findAllAccountsOfPerson("89");
        transactionEntityFacade.debitAccount(dimitriAccounts.get(0).getId(), 500);
    }

    @Test(expected = UnknownAccountException.class)
    @DisplayName("List transactions of account not existing.")
    public void listAllTransactionsOfNonAccountTest() throws UnknownAccountException {
        List<TransactionEntity> transactionList = transactionEntityFacade.getTransactions(666);
    }
    @Test(expected = UnknownAccountException.class)
    @DisplayName("Credit account not existing.")
    public void creditOfNonAccountTest(){
        transactionEntityFacade.creditAccount(303, 1000);
    }
    @Test(expected = UnknownAccountException.class)
    @DisplayName("Debit account not existing.")
    public void debitOfNonAccountTest(){
        transactionEntityFacade.debitAccount(1337, 1000);
    }
    @Test(expected = InsufficentAmountException.class)
    @DisplayName("Debit account insufficient funds.")
    public void debitAccountInsufficientFundsTest(){
        accountEntityFacade.create("CHECK", "22", "ABANK");
        List<AccountEntity> dimitriAccounts = accountEntityFacade.findAllAccountsOfPerson("22");
        transactionEntityFacade.debitAccount(dimitriAccounts.get(0).getId(), 1000);
    }
    @Test(expected = InsufficentAmountException.class)
    @DisplayName("Debit account insufficient funds.")
    public void creditAccountInsufficientFundsTest(){
        accountEntityFacade.create("CHECK", "29", "ABANK");
        List<AccountEntity> dimitriAccounts = accountEntityFacade.findAllAccountsOfPerson("22");
        transactionEntityFacade.creditAccount(dimitriAccounts.get(0).getId(), -1000);
    }
}
