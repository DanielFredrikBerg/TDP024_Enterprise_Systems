package se.liu.ida.tdp024.account.util.json;

public interface AccountJsonSerializer {

    public <T> T fromJson(String json, Class<T> clazz);
    
    public String toJson(Object object);
}
