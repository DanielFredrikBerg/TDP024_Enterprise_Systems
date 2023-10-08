package se.liu.ida.tdp024.account.xfinal.test.util;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    @JsonIgnore
    private long id;
    @JsonIgnore
    private String person;
    @JsonIgnore
    private String bank;
    @JsonIgnore
    private long holdings;
    @JsonIgnore
    private String accountType;
    @JsonIgnore
    private List<Object> transactions;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 71 * hash + (this.person != null ? this.person.hashCode() : 0);
        hash = 71 * hash + (this.bank != null ? this.bank.hashCode() : 0);
        hash = 71 * hash + (int) (this.holdings ^ (this.holdings >>> 32));
        hash = 71 * hash + (this.accountType != null ? this.accountType.hashCode() : 0);
        hash = 71 * hash + (this.transactions != null ? this.transactions.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccountDTO other = (AccountDTO) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.person == null) ? (other.person != null) : !this.person.equals(other.person)) {
            return false;
        }
        if ((this.bank == null) ? (other.bank != null) : !this.bank.equals(other.bank)) {
            return false;
        }
        if (this.holdings != other.holdings) {
            return false;
        }
        if ((this.accountType == null) ? (other.accountType != null) : !this.accountType.equals(other.accountType)) {
            return false;
        }
        if (this.transactions != other.transactions && (this.transactions == null || !this.transactions.equals(other.transactions))) {
            return false;
        }
        return true;
    }
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPersonKey() {
        return person;
    }

    public void setPersonKey(String personKey) {
        this.person = personKey;
    }

    public String getBankKey() {
        return bank;
    }

    public void setBankKey(String bankKey) {
        this.bank = bankKey;
    }

    public long getHoldings() {
        return holdings;
    }

    public void setHoldings(long holdings) {
        this.holdings = holdings;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<Object> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Object> transactions) {
        this.transactions = transactions;
    }
}
