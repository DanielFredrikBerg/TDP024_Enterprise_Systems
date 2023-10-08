package se.liu.ida.tdp024.account.logic.test.facade;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;

import java.util.List;
import java.util.NoSuchElementException;

public class AccountLogicFacadeTest {


    //--- Unit under test ---//
    public AccountLogicFacade accountLogicFacade = new AccountLogicFacadeImpl(new AccountEntityFacadeDB());
    private static StorageFacade storageFacade = new StorageFacadeDB();
    @AfterClass
    public static void cleanUp() {
        storageFacade.emptyStorage();
    }


    @Test
    @DisplayName("Create one account with correct inputs")
    public void testCreateGoodAccount() throws Exception {
       String result = accountLogicFacade.createAccount("SAVINGS", "3", "SWEDBANK");
        Assert.assertEquals(result, "OK");
        List<AccountEntity> all_accounts = accountLogicFacade.findAllAccountsOfPerson("3");
        Assert.assertEquals(all_accounts.size(), 1);
    }


    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Create one account with a person id that does not exists")
    public void testCreateBadPersonId() throws NoSuchElementException {
        String result = accountLogicFacade.createAccount("SAVINGS", "100", "SWEDBANK");
    }


    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Create one account with a person id that does not exists")
    public void testCreateBadBankId() throws NoSuchElementException {
        String result = accountLogicFacade.createAccount("SAVINGS", "3", "/=");
    }


    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Create one account with a person id that does not exists")
    public void testCreateBadAccountType() throws IllegalArgumentException {
        String result = accountLogicFacade.createAccount("BAD", "3", "SWEDBANK");
    }


    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Create one account with a person id that does not exists")
    public void testCreateAllFieldsEmpty() throws NoSuchElementException {
        String result = accountLogicFacade.createAccount("", "", "");
    }


    @Test
    @DisplayName("List all accounts of person")
    public void testListAllAccountsTest() throws UnknownArgumentException, DataLayerException {
        accountLogicFacade.createAccount("SAVINGS", "1", "NORDEA");
        accountLogicFacade.createAccount("CHECK", "2", "NORDEA");
        accountLogicFacade.createAccount("CHECK", "2", "SWEDBANK");
        accountLogicFacade.createAccount("SAVINGS", "2", "SWEDBANK");
        List<AccountEntity> all_accounts = accountLogicFacade.findAllAccountsOfPerson("2");
        Assert.assertEquals(3, all_accounts.size());
        Assert.assertEquals( all_accounts.get(0).getBank(), "4");
        Assert.assertEquals( all_accounts.get(1).getBank(), "1");
        Assert.assertEquals( all_accounts.get(2).getBank(), "1");
    }
}
