
package se.liu.ida.tdp024.account.rest.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import restLayer.AccountController;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionEntity;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;
import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializer;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializerImpl;
import javax.persistence.RollbackException;
import static org.springframework.http.HttpStatus.OK;

public class RestTransactionTest {
    private static final HTTPHelper httpHelper = new HTTPHelperImpl();
    private final AccountController accountController = new AccountController();
    private static StorageFacade storageFacade = new StorageFacadeDB();

    @AfterClass
    public static void cleanUp() {
        storageFacade.emptyStorage();
    }

    @Test
    public void testCreditUnknownAccount()
    {
        ResponseEntity<String> response = accountController.creditAccount(49, 200);
        Assert.assertEquals( HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals("Credit failed account not found", response.getBody());
    }
    @Test
    public void testDebitUnknownAccount()
    {
        ResponseEntity<String> response = accountController.debitAccount(49, 200);
        Assert.assertEquals( HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals("Debit failed account not found", response.getBody());
    }

    @Test
    public void testCreditInsufficientAmount()
    {
        accountController.createAccount("CHECK", "1", "SBAB");
        ResponseEntity<String> response = accountController.creditAccount(1, -200);
        Assert.assertEquals( HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        Assert.assertEquals("Credit failed not enough money.", response.getBody());
    }

    @Test
    public void getTransactionFromNonExistentAccountId()
    {
        ResponseEntity<List<TransactionEntity>> response = accountController.getTransactions(65559);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        List<TransactionEntity> anEmptyList = Collections.emptyList();
        Assert.assertEquals(anEmptyList, response.getBody());
    }

    @Test
    public void testFind() {
        {
            String person = "3";
            String bank = "SWEDBANK";
            String type = "SAVINGS";
            ResponseEntity<String> responseEntity = accountController.createAccount(type, person, bank);
            Assert.assertEquals(responseEntity.getStatusCode(), OK);
            Assert.assertEquals("OK", responseEntity.getBody());
        }

        ResponseEntity<List<AccountEntity>> response = accountController.findAllAccountsOfPerson("3");
        int accountID = (int)response.getBody().get(0).getId();

        accountController.creditAccount(accountID, 200);
        accountController.debitAccount(accountID, 50);
        accountController.creditAccount(accountID, 25);
        accountController.debitAccount(accountID, 100);
        accountController.debitAccount(accountID, 75);
        try {
            accountController.debitAccount(accountID, 10);
        } catch (RollbackException e) {
            // Nothing
        }

        ResponseEntity<List<TransactionEntity>> transactions = accountController.getTransactions(accountID);
        List<TransactionEntity> transactionList = transactions.getBody();


        Assert.assertEquals("OK", transactionList.get(0).getStatus());
        Assert.assertEquals("OK", transactionList.get(1).getStatus());
        Assert.assertEquals("OK", transactionList.get(2).getStatus());
        Assert.assertEquals("OK", transactionList.get(3).getStatus());
        Assert.assertEquals("OK", transactionList.get(4).getStatus());
        //Assert.assertEquals("FAILED", transactionList.get(5).get("status"));
        Assert.assertEquals(200, transactionList.get(0).getAmount());
        Assert.assertEquals(50, transactionList.get(1).getAmount());
        Assert.assertEquals(25, transactionList.get(2).getAmount());
        Assert.assertEquals(100, transactionList.get(3).getAmount());
        Assert.assertEquals(75, transactionList.get(4).getAmount());
        //Assert.assertEquals(10, transactionList.get(5).getAmount());

        Assert.assertEquals("CREDIT", transactionList.get(0).getType());
        Assert.assertEquals("DEBIT", transactionList.get(1).getType());
        Assert.assertEquals("CREDIT", transactionList.get(2).getType());
        Assert.assertEquals("DEBIT", transactionList.get(3).getType());
        Assert.assertEquals("DEBIT", transactionList.get(4).getType());
        //Assert.assertEquals("DEBIT", transactionList.get(5).getType());

        Assert.assertNotNull(transactionList.get(0).getDate());
        Assert.assertNotNull(transactionList.get(1).getDate());
        Assert.assertNotNull(transactionList.get(2).getDate());
        Assert.assertNotNull(transactionList.get(3).getDate());
        Assert.assertNotNull(transactionList.get(4).getDate());
        //Assert.assertNotNull(transactionList.get(5).getDate());
    }

    @Test
    public void testDebitConcurrency() {

        {
            String person = "1";
            String bank = "SWEDBANK";
            String type = "SAVINGS";
            ResponseEntity<String> responseEntity = accountController.createAccount(type, person, bank);
            Assert.assertEquals(responseEntity.getStatusCode(), OK);
            Assert.assertEquals("OK", responseEntity.getBody());
        }


        ResponseEntity<List<AccountEntity>> response = accountController.findAllAccountsOfPerson("1");
        List<AccountEntity> accountList = response.getBody();

        int accountID = (int)accountList.get(0).getId();

        //Initialize the account with a random amount
        int initialAmount = (int) (Math.random() * 100);
        accountController.creditAccount(accountID, initialAmount);

        //Create lots of small removals
        int size = 1000;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            threads.add(new Thread(() -> {
                try {
                    Thread.sleep((long)(Math.random() * 100));
                    int amount = (int) (Math.random() * 10);
                    accountController.debitAccount(accountID, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }

        //Run the removals
        for (Thread thread : threads) {
            thread.start();
        }

        //Assume that it take 20 seconds to complete
        try {
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Now check the balance of the account
        {
            ResponseEntity<List<AccountEntity>> accountList2 = accountController.findAllAccountsOfPerson("1");
            Assert.assertEquals(0, accountList2.getBody().get(0).getHoldings());
        }
    }

    @Test
    public void testCreditConcurrency() {

        {
            String person = "4";
            String bank = "SWEDBANK";
            String type = "SAVINGS";
            ResponseEntity<String> responseEntity = accountController.createAccount(type, person, bank);
            Assert.assertEquals(responseEntity.getStatusCode(), OK);
            Assert.assertEquals("OK", responseEntity.getBody());
        }

        ResponseEntity<List<AccountEntity>> accountList = accountController.findAllAccountsOfPerson("4");
        int accountID = (int)accountList.getBody().get(0).getId();

        //Create lots of small credits
        final int size = 1000;
        final int amount = 10;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            threads.add(new Thread(() -> {
                try {
                    Thread.sleep((long)(Math.random() * 100));
                    accountController.creditAccount(accountID, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }

        //Run the removals
        for (Thread thread : threads) {
            thread.start();
        }

        //Assume that it take 20 seconds to complete
        try {
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Now check the balance of the account
        {
            ResponseEntity<List<AccountEntity>> accountList2 = accountController.findAllAccountsOfPerson("4");
            Assert.assertEquals(size*amount, accountList2.getBody().get(0).getHoldings());
        }

    }
}