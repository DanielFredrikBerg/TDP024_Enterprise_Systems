package se.liu.ida.tdp024.account.data.impl.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import javax.persistence.*;
import java.util.Date;

@Entity
public class TransactionEntity implements Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "amount")
    private Integer amount;

    // persistance.xml complains about "temporal type" if data is Date type
    @Column(name = "created")
    private String date;

    @Column(name = "status")
    private String status;

    @JoinColumn(name = "account")
    private AccountEntity account;

    public TransactionEntity() {}

    public TransactionEntity(String type, Integer amount, String status,
                             AccountEntity account)
    {
        this.type = type;
        this.amount = amount;
        this.date = new Date().toString();
        this.status = status;
        this.account = account;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", account=" + account +
                '}';
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    @JsonProperty("created")
    public String getDate() {
        return date;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public AccountEntity getAccount() {
        return account;
    }
}
