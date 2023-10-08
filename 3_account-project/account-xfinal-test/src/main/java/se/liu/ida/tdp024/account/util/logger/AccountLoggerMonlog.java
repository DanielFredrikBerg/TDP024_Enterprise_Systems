package se.liu.ida.tdp024.account.util.logger;

import java.util.Date;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;
import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;

public class AccountLoggerMonlog implements AccountLogger {

    private static final HTTPHelper httpHelper = new HTTPHelperImpl();
    
    //-- Set your API key here ----------//
    private static final String apikey = "";
    //-----------------------------------//
    private static final String endpoint = "http://www.ida.liu.se/~TDP024/monlog/api/log/";

    @Override
    public void log(Throwable throwable) {

        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement ste : throwable.getStackTrace()) {
            stackTrace.append(ste.toString()).append("\n");
        }

        httpHelper.postJSON(
                endpoint,
                new String[]{"api_key", apikey, "format", "json"},
                new String[]{
            "severity", "5",
            "short_desc", throwable.getMessage(),
            "long_desc", stackTrace.toString(),
            "timestamp", new Date().getTime() + ""});

    }

    @Override
    public void log(TodoLoggerLevel todoLoggerLevel, String shortMessage, String longMessage) {

        httpHelper.postJSON(
                endpoint,
                new String[]{"api_key", apikey, "format", "json"},
                new String[]{
            "severity", todoLoggerLevel.ordinal() + "",
            "short_desc", shortMessage,
            "long_desc", longMessage,
            "timestamp", new Date().getTime() + ""});

    }

    @Override
    public void sendKafka(String kafkaTopic, TodoLoggerLevel todoLoggerLevel, String context, String info) {

    }
}
