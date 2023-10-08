package se.liu.ida.tdp024.account.logic.api.facade;

import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownAccountException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;

import javax.persistence.EntityManager;
import javax.persistence.QueryTimeoutException;
import java.util.List;
import java.util.NoSuchElementException;

public interface AccountLogicFacade {

    String createAccount(String accountType, String person, String Bank)
            throws UnknownArgumentException, IllegalArgumentException,
            DataLayerException;

    List<AccountEntity> findAllAccountsOfPerson(String personID)
            throws IllegalArgumentException, UnknownAccountException, DataLayerException;

}

