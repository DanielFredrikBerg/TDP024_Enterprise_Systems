package se.liu.ida.tdp024.account.data.impl.db.util;

import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.util.InputValidator;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerKafka;

public class InputValidatorImpl implements InputValidator {

    private final AccountLoggerKafka accountLoggerKafka = new AccountLoggerKafka();
    private AccountLogger accountLogger = new AccountLoggerImpl();
    private String restRequests = "rest-requests";
    @Override
    public void checkInputNotNull(String accountType, String person, String bank) {
        boolean is_null = accountType == null || person == null || bank == null;

        if(is_null)
        {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR, "UnknownArgumentException",
                    "Any of accountType: " + accountType + " personKey: " + person + " bankKey: " + bank + "is null");
            throw new UnknownArgumentException("accountType, personKey and bankKey may not be null.");
        }
    }

    @Override
    public void checkSingleInputNotNullOrEmpty(String value) throws IllegalArgumentException {
        boolean valid_value = (value != null && !value.equals("") && !value.equals("null") );
        if(!valid_value)
        {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR, "IllegalArgumentException",
                    "Value is null");
            throw new IllegalArgumentException("Value may not be null.");
        }
    }

    @Override
    public void checkInputNotDefault(String accountType, String person, String bank) throws UnknownArgumentException {
        boolean is_default = accountType.equals("nullAccount") || person.equals("nullPerson") || bank.equals("nullBank");

        if(is_default)
        {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR, "UnknownArgumentException",
                    "Any of accountType: " + accountType + " personKey: " + person + " bankKey: " + bank + "is null");
            throw new UnknownArgumentException("Please provide values for accountType, person and bank.");
        }
    }

    @Override
    public void checkAccountType(String accountType) {
        boolean is_valid_account = (accountType != null && !accountType.equals("")) && (accountType.equals("CHECK") || accountType.equals("SAVINGS"));

        if(!is_valid_account) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR, "RuntimeException",
                    "accountType: " + accountType + ", but must be CHECK or SAVINGS.");
            throw new IllegalArgumentException("accountType must be CHECK or SAVINGS");
        }
    }

    @Override
    public void checkPersonType(String person) {
        boolean is_valid_account = isNumeric(person);

        if(!is_valid_account) {
            accountLoggerKafka.sendKafka(restRequests, AccountLogger.TodoLoggerLevel.ERROR, "RuntimeException",
                    "person = " + person + ", but must be a numerical string.");
            throw new IllegalArgumentException("person must be a numerical string");
        }
    }


    @Override
    public void runAllChecks(String accountType, String person, String bank) {
        checkInputNotNull(accountType, person, bank);
        checkInputNotDefault(accountType, person, bank);
        checkAccountType(accountType);
        checkPersonType(person);
    }

    // From https://www.baeldung.com/java-check-string-number
    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Long l = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
