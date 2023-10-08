package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Account extends Serializable {

    void setId(long id);

    String getAccountType();
    void setAccountType(String accountType);

    String getPerson();
    void setPerson(String person);

    String getBank();
    void setBank(String bank);

    int getHoldings();
    void setHoldings(int holdings);
}
