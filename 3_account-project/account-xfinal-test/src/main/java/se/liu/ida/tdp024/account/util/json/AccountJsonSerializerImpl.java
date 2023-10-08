package se.liu.ida.tdp024.account.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;

public class AccountJsonSerializerImpl implements AccountJsonSerializer {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //
    private AccountLogger todoLogger = new AccountLoggerImpl();
    private ObjectMapper mapper;
    
    public AccountJsonSerializerImpl() {
        mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonMappingException e) {
            todoLogger.log(e);
            return null;
        } catch (IOException e) {
            todoLogger.log(e);
            return null;
        }

    }

    @Override
    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            todoLogger.log(e);
            return null;
        }
    }
}
