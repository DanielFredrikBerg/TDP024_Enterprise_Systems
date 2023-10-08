package se.liu.ida.tdp024.account.logic.impl.dto;

public class BankDataTransferObject {
    private String key;
    private String name;

/*    @Override
    public String toString() {
        return "BankDataTransferObject{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }*/

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

/*
    public String getName() {
        return name;
    }
*/

    public void setName(String name) {
        this.name = name;
    }
}
