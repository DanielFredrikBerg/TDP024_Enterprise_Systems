package se.liu.ida.tdp024.account.data.impl.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import javax.persistence.*;


@Entity
public class AccountEntity implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "account_type", nullable = false)
    private String accountType;
    @Column(name = "personKey")
    private String personKey;
    @Column(name = "bankKey")
    private String bankKey;
    @Column(name = "holdings")
    private int holdings;

    public AccountEntity() {}

    public AccountEntity(String accountType, String person, String bank) {
        this.accountType = accountType;
        this.personKey = person;
        this.bankKey = bank;
        this.holdings = 0;
    }

    public long getId() { return id; }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", holdings='" + holdings + '\'' +
                ", accountType='" + accountType + '\'' +
                ", personKey='" + personKey + '\'' +
                ", bankKey=" + bankKey +
                '}';
    }

    public void setId(long id) { this.id = id; }

    @Override
    @JsonProperty("accountType")
    public String getAccountType() {
        return accountType;
    }
    @Override
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    @JsonProperty("personKey")
    public String getPerson() {
        return personKey;
    }
    @Override
    public void setPerson(String person) {
        this.personKey = person;
    }

    @Override
    @JsonProperty("bankKey")
    public String getBank() {
        return bankKey;
    }
    @Override
    public void setBank(String bank) {
        this.bankKey = bank;
    }

    @Override
    public int getHoldings() {
        return holdings;
    }
    @Override
    public void setHoldings(int holdings) {
        this.holdings = holdings;
    };
}
