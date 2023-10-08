package restLayer;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.InsufficentAmountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionEntity;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.logic.impl.facade.TransactionLogicFacadeImpl;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializer;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerKafka;

import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/account-rest/account")
public class AccountController implements ErrorController {
    private final AccountLogicFacade accountFacade =
            new AccountLogicFacadeImpl(new AccountEntityFacadeDB());
    private final TransactionLogicFacade transactionFacade =
            new TransactionLogicFacadeImpl(new TransactionEntityFacadeDB());
    private static final AccountLoggerKafka accountLoggerKafka = new AccountLoggerKafka();
    private final AccountJsonSerializer jsonSerializer = new AccountJsonSerializerImpl();
    private final String kafkaTopic = "rest-requests";

    // http://localhost:8080/account-rest/create?accountType=check&person=person&bank=ICABANKEN
    @GetMapping(path="/create/")
    // ResponseEntity = HTTP response (headers and the whole shabang)
    public ResponseEntity<String> createAccount(@RequestParam(value = "accounttype", defaultValue = "nullAccount") String accountType,
                                                @RequestParam(value = "person", defaultValue = "nullPerson") String person,
                                                @RequestParam(value = "bank", defaultValue = "nullBank") String bank) {
        try {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO,
                    "Account creation request with: ",
                    "{" + accountType + ", " + person + ", " + bank + "} received and started.");
            String responseString = accountFacade.createAccount(accountType, person, bank);
            return ResponseEntity.ok(responseString);
        } catch (UnknownArgumentException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(),"");
            return new ResponseEntity<>("Account creation of {" + accountType + ", " + person + ", " + bank + "} is a Bad Request",
                    HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(),"");
            return new ResponseEntity<>("Any of accounttype, person or bank may not be used as input.",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (DataLayerException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>("Service Unavailable",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping(path="/find/person")
    public ResponseEntity<List<AccountEntity>> findAllAccountsOfPerson(@RequestParam(value = "person") String personKey) {
        List<AccountEntity> accounts;
        List<AccountEntity> emptyList = Collections.emptyList();
        accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, "Started findAllAccountsOfPerson", personKey);
        try {
            accounts = accountFacade.findAllAccountsOfPerson(personKey);

            accountLoggerKafka.sendKafka(kafkaTopic,
                    AccountLogger.TodoLoggerLevel.INFO, "findAllAccountOfPerson(" + personKey + ") successful.",
                    accounts.toString());
            return ResponseEntity.ok(accounts);
        } catch (IllegalArgumentException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>(emptyList, HttpStatus.BAD_REQUEST); // Fler statuskoder! 6 eller fler each.
        } catch (UnknownAccountException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>(emptyList, HttpStatus.NOT_FOUND);
        } catch (DataLayerException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>(emptyList, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


    @GetMapping(path="/debit")
    public ResponseEntity<String> debitAccount(@RequestParam(value = "id") long id,
                                               @RequestParam(value = "amount") int amount) {
        try
        {
            accountLoggerKafka.sendKafka(kafkaTopic,
                    AccountLogger.TodoLoggerLevel.INFO, "Account debit request with" + amount + " of account " + id,
                    "received and started.");
            String result = transactionFacade.debitAccount(id, amount);
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, "Debit of account" + id, "successful.");
            return ResponseEntity.ok(result);
        } catch (InsufficentAmountException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(),"" );
            return new ResponseEntity<>("Debit failed not enough money.", HttpStatus.METHOD_NOT_ALLOWED);
        } catch (UnknownAccountException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(),"" );
            return new ResponseEntity<>("Debit failed account not found", HttpStatus.NOT_FOUND);
        } catch (DataLayerException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RollbackException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>("Debit Rollback", HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @GetMapping(path="/credit")
    public ResponseEntity<String> creditAccount(@RequestParam(value = "id") long id,
                                                @RequestParam(value = "amount") int amount) {
        try {
            accountLoggerKafka.sendKafka(kafkaTopic,
                    AccountLogger.TodoLoggerLevel.INFO, "Account credit request with" + amount + " of account " + id,
                    "received and started.");
            String response = transactionFacade.creditAccount(id, amount);
            return ResponseEntity.ok(response);
        } catch (InsufficentAmountException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(),"" );
            return new ResponseEntity<>("Credit failed not enough money.", HttpStatus.METHOD_NOT_ALLOWED);
        } catch (UnknownAccountException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(),"" );
            return new ResponseEntity<>("Credit failed account not found", HttpStatus.NOT_FOUND);
        } catch (DataLayerException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (QueryTimeoutException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>("Query timed out", HttpStatus.REQUEST_TIMEOUT);
        } catch (RollbackException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>("Credit Rollback", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping(path="/transactions")
    public ResponseEntity<List<TransactionEntity>> getTransactions(@RequestParam(value = "id") long id) {
        List<TransactionEntity> allTransactions;
        List<TransactionEntity> emptyList = Collections.emptyList();
        try {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, "List all transactions of account "
                    + id, "received and started");
            allTransactions = transactionFacade.getTransactions(id);
            accountLoggerKafka.sendKafka(kafkaTopic,
                    AccountLogger.TodoLoggerLevel.INFO, "Recieved all transactions of " + id, "successfully.");
            return ResponseEntity.ok(allTransactions);
        } catch (UnknownAccountException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(), "");
            return new ResponseEntity<>(emptyList, HttpStatus.NOT_FOUND);
        } catch (QueryTimeoutException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.INFO, e.getMessage(), "");
            return new ResponseEntity<>(emptyList, HttpStatus.REQUEST_TIMEOUT);
        } catch (DataLayerException e) {
            accountLoggerKafka.sendKafka(kafkaTopic, AccountLogger.TodoLoggerLevel.ERROR, e.getMessage(), "");
            return new ResponseEntity<>(emptyList, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
