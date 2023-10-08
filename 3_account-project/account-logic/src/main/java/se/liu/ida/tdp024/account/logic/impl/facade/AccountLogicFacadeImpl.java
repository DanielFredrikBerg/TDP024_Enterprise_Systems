package se.liu.ida.tdp024.account.logic.impl.facade;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jackson.JsonParseException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import se.liu.ida.tdp024.account.data.impl.db.util.InputValidatorImpl;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.dto.BankDataTransferObject;
import se.liu.ida.tdp024.account.logic.impl.dto.PersonDataTransferObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializer;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerKafka;

import javax.persistence.EntityManager;
import javax.persistence.QueryTimeoutException;

@Service
public class AccountLogicFacadeImpl implements AccountLogicFacade {
    private final AccountEntityFacade accountEntityFacade;
    private final InputValidatorImpl inputValidator = new InputValidatorImpl();
    private final AccountLoggerKafka accountLoggerKafka = new AccountLoggerKafka();
    private final String restRequests = "rest-requests";
    private final HTTPHelperImpl httpHelper = new HTTPHelperImpl();
    private final AccountJsonSerializer serializeToJson = new AccountJsonSerializerImpl();

    public AccountLogicFacadeImpl(AccountEntityFacade accountEntityFacade) {
        this.accountEntityFacade = accountEntityFacade;
    }

    @Override
    public String createAccount(String accountType, String person, String bank)
            throws UnknownArgumentException, IllegalArgumentException,
            DataLayerException {
        inputValidator.runAllChecks(accountType, person, bank);
        String result = "";
        try {
            PersonDataTransferObject customer = getPersonDTO(person);
            BankDataTransferObject accountAtBank = getBankDTO(bank);
            String bankKey = accountAtBank.getKey();
            String personKey = customer.getKey();
            result = accountEntityFacade.create(accountType, personKey, bankKey);
            return result;
        } catch (Throwable e) {
            throw e;
        }

        /*catch(NullPointerException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    "NullPointerException", "bankKey or personKey null.");
        } catch (IllegalArgumentException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    "IllegalArgumentException", "bankKey or personKey not found during account creation.");
            throw e;
        } catch (DataLayerException e) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR,
                    "DataLayerException", "Unknown Error in createAccount.");
            throw e;
        }*/
    }


    @Override
    public List<AccountEntity> findAllAccountsOfPerson(String personKey)
        throws IllegalArgumentException, UnknownArgumentException, DataLayerException {
        inputValidator.checkPersonType(personKey);
        return accountEntityFacade.findAllAccountsOfPerson(personKey);
    }

    private PersonDataTransferObject getPersonDTO(String personKey)
        throws NoSuchElementException, IllegalArgumentException {
        inputValidator.checkPersonType(personKey); // Data från extern API ska valideras också!
        String scalaPersonResponse = httpHelper.get("http://localhost:8060/persons/find.key", "key", personKey);
        inputValidator.checkSingleInputNotNullOrEmpty(scalaPersonResponse);
        return serializeToJson.fromJson(scalaPersonResponse, PersonDataTransferObject.class);
    }

    private BankDataTransferObject getBankDTO(String bank)
            throws NoSuchElementException, IllegalArgumentException {
        inputValidator.checkSingleInputNotNullOrEmpty(bank);
        String flaskBankResponse = httpHelper.get("http://localhost:8070/find", "bank", bank);
        inputValidator.checkSingleInputNotNullOrEmpty(flaskBankResponse);
        BankDataTransferObject bankDataTransferObject = serializeToJson.fromJson(flaskBankResponse, BankDataTransferObject.class);
        return bankDataTransferObject;
    }
}


