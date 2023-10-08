package se.liu.ida.tdp024.account.data.api.util;

import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;

import java.util.NoSuchElementException;

public interface InputValidator {
    void checkInputNotNull(String accountType, String person, String bank)
            throws UnknownArgumentException;

    void checkSingleInputNotNullOrEmpty(String value) throws IllegalArgumentException;

    void checkInputNotDefault(String accountType, String person, String bank)
            throws UnknownArgumentException;
    void checkAccountType(String accountType)
            throws IllegalArgumentException;
    void runAllChecks(String accountType, String person, String bank)
            throws UnknownArgumentException, IllegalArgumentException;
    void checkPersonType(String person)
        throws IllegalArgumentException;
}