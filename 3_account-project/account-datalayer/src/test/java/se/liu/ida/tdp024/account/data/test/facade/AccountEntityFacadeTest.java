package se.liu.ida.tdp024.account.data.test.facade;

import org.apache.catalina.connector.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.annotation.Order;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;

import java.util.List;

import static org.junit.Assert.fail;

public class AccountEntityFacadeTest {
    
    //---- Unit under test ----//
    private AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB();
    private static StorageFacade storageFacade = new StorageFacadeDB();
    @After
    public void cleanUp() {
        storageFacade.emptyStorage();
    }
    private AccountEntity account = new AccountEntity();

    @Test
    @Order(1)
    public void testSettersAndGetters() {
        account.setId(4206969);
        Assert.assertEquals(4206969, account.getId());

        account.setAccountType("FOX");
        Assert.assertEquals(account.getAccountType(), "FOX");

        account.setPerson("52");
        Assert.assertEquals(account.getPerson(), "52");

        account.setBank("SWEATBANK");
        Assert.assertEquals(account.getBank(), "SWEATBANK");

        account.setHoldings(420);
        Assert.assertEquals(420, account.getHoldings());

        Assert.assertEquals(account.toString(), "{id=4206969, holdings='420', accountType='FOX', personKey='52', bankKey=SWEATBANK}");

    }
    @Test
    @DisplayName("Should create one CHECK accounts")
    public void testCreateOneCheckAccountTest() throws UnknownArgumentException, DataLayerException {
        String result = accountEntityFacade.create("CHECK", "52", "ONEBANK");
        Assert.assertEquals( result, "OK");
    }
    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Should create one CHECK accounts")
    public void testCreateBadAccount() throws UnknownArgumentException, DataLayerException {
        String result = accountEntityFacade.create("CHECK", "bad", "ONEBANK");
        Assert.assertEquals( result, "OK");
    }
    @Test(expected = UnknownArgumentException.class)
    @DisplayName("Should create one CHECK accounts")
    public void testCreateNullBank() throws UnknownArgumentException, DataLayerException {
        String result = accountEntityFacade.create("CHECK", "22", "nullBank");
        Assert.assertEquals( result, "OK");
    }
    @Test
    @DisplayName("Should create one DEBIT accounts")
    public void testCreateOneDebitAccountTest() throws UnknownArgumentException, DataLayerException {
        String result = accountEntityFacade.create("SAVINGS", "52", "ONEBANK");
        Assert.assertEquals( result, "OK");
    }
    @Test(expected = UnknownArgumentException.class)
    @DisplayName("Should fail to create account with any null")
    public void testCreateOneArgNullAccountTest() throws UnknownArgumentException, DataLayerException {
        String result = accountEntityFacade.create("CHECK", null, "ONEBANK");
    }
    @Test(expected = UnknownArgumentException.class)
    @DisplayName("Should fail to create account with all null")
    public void testCreateAllArgsNullAccountTest() throws UnknownArgumentException, DataLayerException {
        String result = accountEntityFacade.create(null, null, null);
    }
    @Test(expected = RuntimeException.class)
    @DisplayName("Should fail to create account with one wrong parameter input.")
    public void testCreateOneArgsIllegalAccountTest() throws UnknownArgumentException, DataLayerException {
        String result = accountEntityFacade.create("SOMETHINGELSEs", "Dude", "ONEBANK");
    }
    @Test
    @DisplayName("List all accounts of person")
    public void testListAllAccountsTest() throws UnknownArgumentException, DataLayerException {
        accountEntityFacade.create("SAVINGS", "33", "UNBANKYOURSELF");
        accountEntityFacade.create("CHECK", "52", "ASSHOLEBANK");
        accountEntityFacade.create("CHECK", "52", "SWEATBANK");
        accountEntityFacade.create("SAVINGS", "52", "SWEATBANK");
        List<AccountEntity> all_accounts = accountEntityFacade.findAllAccountsOfPerson("52");
        Assert.assertTrue(all_accounts.size() == 3);
        Assert.assertEquals( all_accounts.get(0).getBank(), "ASSHOLEBANK");
        Assert.assertEquals( all_accounts.get(1).getBank(), "SWEATBANK");
        Assert.assertEquals( all_accounts.get(2).getBank(), "SWEATBANK");
    }
    @Test(expected = UnknownAccountException.class)
    @DisplayName("Try to List all accounts of non existing person")
    public void testListAllAccountsOfNonPersonTest() throws UnknownArgumentException, DataLayerException {
        List<AccountEntity> all_accounts = accountEntityFacade.findAllAccountsOfPerson("52782");
    }
    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Throw exception when listing all accounts of person null.")
    public void testListAllAccountsWhenPersonDoesNotExist() throws IllegalArgumentException {
        List<AccountEntity> all_accounts = accountEntityFacade.findAllAccountsOfPerson("");
    }

    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Throw exception when listing all accounts of person null.")
    public void testListAllAccountsWhenPersonDoesNotExist2() throws IllegalArgumentException {
        List<AccountEntity> all_accounts = accountEntityFacade.findAllAccountsOfPerson("ddd");
    }
    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Throw exception when listing all accounts of person null.")
    public void testListAllAccountsNullArgTest() throws UnknownArgumentException {
        List<AccountEntity> all_accounts = accountEntityFacade.findAllAccountsOfPerson(null);
    }



   /* @Test
    @DisplayName("")
    public void testCreateManyAccountsTest() throws UnknownArgumentException {
        String result = accountEntityFacade.create("CHECK", "52", "ASSHOLEBANK");
        accountEntityFacade.create("CHECK", "52", "SWEATBANK");

        List<AccountEntity> all_accounts = accountEntityFacade.findAllAccountsOfPerson("52");

        Assert.assertEquals( all_accounts.size(), 2);
        Assert.assertEquals( all_accounts.get(0).getBank(), "ASSHOLEBANK");
        Assert.assertEquals( all_accounts.get(1).getBank(), "SWEATBANK");
    }*/

}