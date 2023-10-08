package se.liu.ida.tdp024.account.rest.test;

import org.eclipse.persistence.sessions.serializers.JSONSerializer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import restLayer.AccountController;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;
import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializer;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerKafka;
import util.FinalConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.*;


public class FindPersonTest {
    private static final HTTPHelper httpHelper = new HTTPHelperImpl();
    private static StorageFacade storageFacade = new StorageFacadeDB();
    private AccountLogger accountLogger = new AccountLoggerImpl();
    private static AccountController accountController = new AccountController();
    private AccountJsonSerializerImpl jsonSerializer = new AccountJsonSerializerImpl();

    @BeforeClass
    public static void setup() {
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
                    accountController.createAccount(accountType, personId, bankName);
                }
            }
        }
    }

    @AfterClass
    public static void cleanUp() {
        storageFacade.emptyStorage();
    }
    @Test
    public void findPersonSuccess() {
        String person = "4";
        ResponseEntity<List<AccountEntity>> accountListResponse = accountController.findAllAccountsOfPerson(person);
        Assert.assertEquals(accountListResponse.getStatusCode(), OK);
        Assert.assertEquals(accountListResponse.getBody().size(), 18);
        Assert.assertEquals(accountListResponse.getStatusCode(), OK);
    }

    @Test()
    public void findPersonNullFailure() {
        String person = null;
        ResponseEntity<List<AccountEntity>> accountListResponse = accountController.findAllAccountsOfPerson(person);
        List<AccountEntity> emptyList = Collections.emptyList();
        Assert.assertEquals(accountListResponse.getBody(), emptyList);
        Assert.assertEquals(BAD_REQUEST, accountListResponse.getStatusCode());
    }

    @Test()
    public void findPersonEmptyStringFailure() {
        String person = "";
        ResponseEntity<List<AccountEntity>> accountListResponse = accountController.findAllAccountsOfPerson(person);
        List<AccountEntity> emptyList = Collections.emptyList();
        Assert.assertEquals(accountListResponse.getBody(), emptyList);
        Assert.assertEquals(BAD_REQUEST, accountListResponse.getStatusCode());
    }

    @Test
    public void findPersonNonExistingStringFailure() {
        String person = "99998888190";
        ResponseEntity<List<AccountEntity>> accountListResponse = accountController.findAllAccountsOfPerson(person);
        List<AccountEntity> emptyList = Collections.emptyList();
        Assert.assertEquals(accountListResponse.getBody(), emptyList);
        Assert.assertEquals(NOT_FOUND, accountListResponse.getStatusCode());
    }
}

