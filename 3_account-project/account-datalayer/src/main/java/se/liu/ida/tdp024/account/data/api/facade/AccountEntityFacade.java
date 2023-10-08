package se.liu.ida.tdp024.account.data.api.facade;

import se.liu.ida.tdp024.account.data.api.exception.DataLayerException;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import java.util.List;

public interface AccountEntityFacade {

    String create(String accountType, String person, String bank) throws
            UnknownArgumentException, IllegalArgumentException,
            DataLayerException;

    List<AccountEntity> findAllAccountsOfPerson(String personID) throws
            UnknownArgumentException, IllegalArgumentException,
            DataLayerException;
}
