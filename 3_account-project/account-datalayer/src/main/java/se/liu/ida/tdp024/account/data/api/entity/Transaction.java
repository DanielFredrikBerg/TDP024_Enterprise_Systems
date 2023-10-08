package se.liu.ida.tdp024.account.data.api.entity;

import se.liu.ida.tdp024.account.data.impl.db.entity.AccountEntity;
import java.io.Serializable;

public interface Transaction extends Serializable {
    String getType();
    int getAmount();
    String getDate();

    String getStatus();

    AccountEntity getAccount();
}
