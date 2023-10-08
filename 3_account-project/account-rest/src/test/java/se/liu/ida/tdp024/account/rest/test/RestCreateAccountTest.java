package se.liu.ida.tdp024.account.rest.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import restLayer.AccountController;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;
import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;
import util.FinalConstants;


import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

public class RestCreateAccountTest {

    //---- Unit under test ----//
    private static final HTTPHelper httpHelper = new HTTPHelperImpl();
    private static StorageFacade storageFacade = new StorageFacadeDB();
    private AccountLogger accountLogger = new AccountLoggerImpl();
    private AccountController accountController = new AccountController();


    @Test
    public void createSuccess() {
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "1";
            String bank = "NORDEA";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "1";
            String bank = "NORDEA";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "1";
            String bank = "SWEDBANK";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "4";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "4";
            String bank = "JPMORGAN";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "4";
            String bank = "NORDNET";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
    }
    @Test
    public void createSuccessAllCombos() {

        List<String> personIds = new ArrayList<String>();
        List<String> bankNames = new ArrayList<String>();
        List<String> accountTypes = new ArrayList<String>();

        personIds.add("1");
        personIds.add("2");
        personIds.add("3");
        personIds.add("4");
        personIds.add("5");

        bankNames.add("SWEDBANK");
        bankNames.add("IKANOBANKEN");
        bankNames.add("JPMORGAN");
        bankNames.add("NORDEA");
        bankNames.add("CITIBANK");
        bankNames.add("HANDELSBANKEN");
        bankNames.add("SBAB");
        bankNames.add("HSBC");
        bankNames.add("NORDNET");

        accountTypes.add("CHECK");
        accountTypes.add("SAVINGS");

        for (String personId : personIds) {
            for (String bankName : bankNames) {
                for (String accountType : accountTypes) {
                    String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", personId, "bank", bankName, "accounttype", accountType);
                    Assert.assertEquals("OK", response);
                }
            }
        }
    }

    @Test
    public void createAccountTypeFailure() {
        String person = "3";
        String bank = "SWEDBANK";
        String accountType = "CREDITCARD";
        ResponseEntity responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals("Any of accounttype, person or bank may not be used as input.", responseEntity.getBody());
    }


    @Test
    public void createAccountNullPerson() {
        String person = "nullPerson";
        String bank = "SWEDBANK";
        String accountType = "SAVINGS";
        ResponseEntity responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), BAD_REQUEST);
        Assert.assertEquals("Account creation of {SAVINGS, nullPerson, SWEDBANK} is a Bad Request", responseEntity.getBody());
    }

    @Test
    public void createPersonFailure() {
        String person = "01219210";
        String bank = "SWEDBANK";
        String accountType = "CHECK";
        ResponseEntity responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals("Any of accounttype, person or bank may not be used as input.", responseEntity.getBody());
    }

    @Test
    public void createBankFailure() {
        String person = "3";
        String bank = "LEHMAN";
        String accountType = "CHECK";
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals("Any of accounttype, person or bank may not be used as input.", responseEntity.getBody());
    }

    @Test
    public void createEmptyBank() {
        String person = "3";
        String bank = "";
        String accountType = "CHECK";
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals("Any of accounttype, person or bank may not be used as input.", responseEntity.getBody());
    }

    @Test
    public void createEmptyAccountType() {
        String person = "3";
        String bank = "SWEDBANK";
        String accountType = "";
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals(responseEntity.getBody(), "Any of accounttype, person or bank may not be used as input.");
    }


    @Test
    public void createEmptyAllFields() {
        String person = "";
        String bank = "";
        String accountType = "";
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals(responseEntity.getBody(), "Any of accounttype, person or bank may not be used as input.");
    }

    @Test
    public void createEmpty2() {
        String person = "";
        String bank = "";
        String accountType = "CHECK";
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals("Any of accounttype, person or bank may not be used as input.", responseEntity.getBody());
    }


    @Test
    public void createEmpty3() {
        String person = "";
        String bank = "";
        String accountType = "CREDITCARD";
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals(responseEntity.getBody(), "Any of accounttype, person or bank may not be used as input.");
    }


    @Test
    public void createEmpty4() {
        String person = "3";
        String bank = "SWEDBANK";
        String accountType = "SAVING"; //It should be SAVINGS
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), UNPROCESSABLE_ENTITY);
        Assert.assertEquals(responseEntity.getBody(), "Any of accounttype, person or bank may not be used as input.");
    }


    @Test
    public void createGood() {
        String person = "3";
        String bank = "SWEDBANK";
        String accountType = "SAVINGS";
        //String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "bank", bank, "accounttype", accountType);
        ResponseEntity<String> responseEntity = accountController.createAccount(accountType, person, bank);
        Assert.assertEquals(responseEntity.getStatusCode(), OK);
        Assert.assertEquals(responseEntity.getBody(), "OK");
    }


        /*{
        {
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "accounttype", accountType);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "bank", bank);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "accounttype", accountType);
        }

        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/");
        }



        *//* Wrong endpoint (i.e. incorrect request) *//*

        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "create/", "person", person, "bank", bank, "accounttype", accountType);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "/", "person", person, "bank", bank, "accounttype", accountType);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/account/create/", "person", person, "bank", bank, "accounttype", accountType);
        }
    }*/

    @After
    public void tearDown() {
        storageFacade.emptyStorage();
    }
}
